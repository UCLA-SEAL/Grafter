package edu.ucla.cs.grafter.port;

import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.core.dom.CompilationUnit;

import edu.ucla.cs.grafter.file.FileUtils;
import edu.ucla.cs.grafter.graft.analysis.CloneParser;
import edu.ucla.cs.grafter.graft.analysis.Constructor;
import edu.ucla.cs.grafter.graft.analysis.FieldVisitor;
import edu.ucla.cs.grafter.graft.analysis.Getter;
import edu.ucla.cs.grafter.graft.analysis.MethodVisitor;
import edu.ucla.cs.grafter.graft.analysis.Setter;
import edu.ucla.cs.grafter.graft.analysis.TypeChecker;
import edu.ucla.cs.grafter.graft.analysis.TypeVisitor;
import edu.ucla.cs.grafter.graft.model.Pair;
import edu.ucla.cs.grafter.graft.model.Token;
import edu.ucla.cs.grafter.graft.model.Clone;

public class TranSynthesizer {
	/**
	 * this method synthesizes the transformation function that populates the value of t1 to t2.
	 * Token t1 and t2 must be explicitly used by code of code clones 
	 * 
	 * @param t1
	 * @param t2
	 * @return
	 * @throws IOException 
	 */
	public static String transform(Token t1, Token t2, Clone orign, Clone clone) throws IOException{
		String type1 = t1.getType();
		String name1 = t1.getName();
		String type2 = t2.getType();
		String name2 = t2.getName();
		
		if(type1.equals(type2) && name1.equals(name2)){
			// do nothing
			return "";
		}else if(type1.equals(type2) || isConvertible(type1, type2)){
			// if types are same but names are different, just do assignment
			return (t2.isField() ? (t2.isStatic ? clone.getOwner() + "." : "this.") : "") 
					+ t2.getName() + " = " + (t1.isField() ? (t1.isStatic ? orign.getOwner() + "." : "this.") : "") + t1.getName() + ";";
		}else if(isBoxed(type1, type2)){
			// if type2 is boxed from type1, call the boxing constructor
			return (t2.isField() ? (t2.isStatic ? clone.getOwner() + "." : "this.") : "") 
					+ t2.getName() + " = new " + t2.getType() + "(" + (t1.isField() ? (t1.isStatic ? orign.getOwner() + "." : "this.") : "") + t1.getName() + ");";
		}else if(isUnboxed(type1, type2)){
			// if type2 is unboxed from type1, call the XXXvalue method
			return (t2.isField() ? (t2.isStatic ? clone.getOwner() + "." : "this.") : "") 
					+ t2.getName() + " = " + (t1.isField() ? (t1.isStatic ? orign.getOwner() + "." : "this.") : "") + t1.getName() + "." + t1.getType() + "Value();"; 
		}else if(isSiblingType(type1, type2) || isStructurallyEquivalent(type1, type2)){
			return transformObject(t1, t2);
		}else{
			// Now I don't think we should mock the value. Just leave it as it is.
			System.out.println("Grafter cannot port the variable " + t1.getName() + " of the type " + t1.getType() + " to " + t2.getName() + " of the type " + t2.getType());
			return "";
			// The matcher is not supposed to match them, mock up the value of t2
			// return (t2.isField() ? (t2.isStatic ? clone.getOwner() + "." : "this.") : "") + t2.getName() + " = " + Mocker.mock(type2) + ";\n";
		}
	}
	
	/**
	 * t1 and t2 are convertible if Java allows to cast t1 to t2 
	 * @param t1
	 * @param t2
	 * @return
	 * @throws IOException 
	 */
	private static boolean isConvertible(String t1, String t2) throws IOException {
		if(t1.equals("int")){
			return t2.equals("short") || t2.equals("long") || t2.equals("float") || t2.equals("double") || t2.equals("char") || t2.equals("byte");
		}else if(t1.equals("short")){
			return t2.equals("int") || t2.equals("long") || t2.equals("float") || t2.equals("double") || t2.equals("char") || t2.equals("byte");
		}else if(t1.equals("byte")){
			return t2.equals("int") || t2.equals("long") || t2.equals("float") || t2.equals("double") || t2.equals("char") || t2.equals("short");
		}else if(t1.equals("long")){
			return t2.equals("int") || t2.equals("byte") || t2.equals("float") || t2.equals("double") || t2.equals("char") || t2.equals("short");
		}else if(t1.equals("float")){
			return t2.equals("int") || t2.equals("byte") || t2.equals("long") || t2.equals("double") || t2.equals("char") || t2.equals("short");
		}else if(t1.equals("double")){
			return t2.equals("int") || t2.equals("byte") || t2.equals("long") || t2.equals("float") || t2.equals("char") || t2.equals("short");
		}else if(t1.equals("char")){
			return t2.equals("int") || t2.equals("byte") || t2.equals("long") || t2.equals("double") || t2.equals("float") || t2.equals("short");
		}else if(isSubType(t1, t2)){
			return true;
		}
		return false;
	}

	private static MethodVisitor prepare(String type) throws IOException{
		File f = FileUtils.findFile(type + ".java");
		if(f == null){
			// couldn't find the source code for this type
			return null;
		}
		CloneParser p = new CloneParser();
		CompilationUnit cu = p.parse(f.getAbsolutePath());
		MethodVisitor mv = new MethodVisitor(type);
		cu.accept(mv);
		
		return mv;
	}
	
	/**
	 * This method is used to transform structurally equivalent objects or objects that are hierarchical siblings
	 * 
	 * @param t1
	 * @param t2
	 * @return
	 * @throws IOException
	 */
	private static String transformObject(Token t1, Token t2) throws IOException{
		String type1 = t1.getType();
		String type2 = t2.getType();
		
		MethodVisitor mv1 = prepare(type1);
		MethodVisitor mv2 = prepare(type2);
		
		if(mv1 == null || mv2 == null){
			// couldn't find the source code of the type
			return t2.getName() + " = " + Mocker.mock(t2.getType()) + ";\n";
		}
		
		HashSet<Token> s1 = getFields(type1);
		HashSet<Token> s2 = getFields(type2);
	
		ArrayList<Pair> pairs = new ArrayList<Pair>();
		ArrayList<Token> unmatches = new ArrayList<Token>();
		
		// match two fields that are exactly the same or convertible
		HashSet<Token> tmp = new HashSet<Token>();
		for(Token t : s1){
			for(Token tt : s2){
				if(tt.equals(t) || (tt.getName().equals(t.getName()) && isConvertible(t.getType(), tt.getType()))){
					Pair p = new Pair(t, tt);
				    pairs.add(p);
				    tmp.add(tt);
				    break;
				}
			}
		}
		
		// match the rest based on textual similarity
		HashSet<Token> rest = new HashSet<Token>(s2);
		rest.removeAll(tmp);
		for(Token tt2 : rest){
			String name2 = tt2.getName();
			
			double max = Double.MIN_VALUE;
			Token match = null;
			
			for(Token tt1 : s1){
				String name1 = tt1.getName();
				int distance = StringUtils.getLevenshteinDistance(name1, name2);
				int length = Math.max(name1.length(), name2.length());
				double score = 1 - (double)distance/length;
				
				if(score > max && !isMatched(tt1, pairs)){
					match = tt1;
					max = score;
				}
			}
			
			if(match != null){
				pairs.add(new Pair(match, tt2));
			}else{
				// this situation happens when t2 has extra fields compared with t1
				unmatches.add(tt2);
			}
		}
		
		// remove fields that are final and have already been initialized
		ArrayList<Pair> finals = new ArrayList<Pair>();
		for(Pair p : pairs){
			Token orgn = p.orign();
			Token clone = p.clone();
			
			if((orgn.isFinal() && orgn.isInitialized()) || (clone.isFinal() && clone.isInitialized())){
				finals.add(p);
			}
		}
		pairs.removeAll(finals);
		
		// redefine private fields as public fields
		for(Pair p : pairs){
			Token orgn = p.orign();
			redefineField(orgn);
			Token clone = p.clone();
			redefineField(clone);
		}
		
		// synthesize transformation function for each field and 
		// concatenate them as the transformation function between two types
		String code = "";
		
		// construct the target object with corresponding field values if any, otherwise use default values
		if(mv2.constructors.isEmpty()){
			code += t2.getName() + " = new " + t2.getType() + "();";
		}else{
			Constructor constructorWithLeastParameters = null; 
			for(Constructor c : mv2.constructors){
				if(constructorWithLeastParameters == null){
					constructorWithLeastParameters = c;
				}else if(c.args.size() < constructorWithLeastParameters.args.size()){
					constructorWithLeastParameters = c;
				}
			}
			
			code += t2.getName() + " = new " + t2.getType() + "(";
			for(Token arg : constructorWithLeastParameters.args){
				Token matchingField = null;
				for(Pair p : pairs){
					Token orgn = p.orign();
					if(orgn.getType().equals(arg.getType())){
						matchingField = orgn;
					}
				}
				
				if(matchingField != null){
					code += t1.getName() + "." + matchingField.getName();
				}else{
					code += Mocker.mock(arg.getType());
				}
				
				if(constructorWithLeastParameters.args.indexOf(arg) != constructorWithLeastParameters.args.size() - 1){
					code += ",";
				}
			}
			code += ");" + System.lineSeparator();
		}
		
		// propagate data between corresponding fields
		for(Pair p : pairs){
			Token orgn = p.orign();
			Token clone = p.clone();
			code += t2.getName() + "." + clone.getName() + " = " 
					+ t1.getName() + "." + orgn.getName() + ";" + System.lineSeparator();
		}
		
		return code;
	}
	
	private static void redefineField(Token t) throws IOException{
		if(t.isPrivate() || t.isProtected()){
			String path = t.getPath();
			String[] src = FileUtils.readFileToArray(path);
			for(int i = 0; i < src.length; i++){
				String line = src[i];
				if(line.contains(t.getName()) && line.contains("private")){
					src[i] = line.replace("private", "public");
				}else if(line.contains(t.getName()) && line.contains("protected")){
					src[i] = line.replace("protected", "public");
				}
			}
			
			File bak = new File(path + ".bak");
			if(bak.exists()){
				FileUtils.writeStringArraytoFile(src, path);
			}else{
			    File old_file = new File(path);
			    old_file.renameTo(new File(path + ".bak"));
			    File new_file = new File(path);
			    new_file.createNewFile();
			    FileUtils.writeStringArraytoFile(src, path);
			}
		}
	}
	
	private static Constructor findBest(ArrayList<Constructor> list, ArrayList<Pair> pairs) throws IOException{
		HashMap<Constructor, ArrayList<Token>> counter = new HashMap<Constructor, ArrayList<Token>>();
		for(Constructor c : list){
			ArrayList<Token> ts = new ArrayList<Token>();
			for(Token arg : c.args){
				for(Pair p : pairs){
					Token clone = p.clone();
					// we don't care if it's private or not if we can find a constructor that initializes it
					TypeChecker tc = new TypeChecker();
//					if(arg.getType().equals(clone.getType())){
					if(tc.check(arg.getType(), clone.getType())){
						ts.add(clone);
					}
				}
			}
			
			counter.put(c, ts);
		}
		
		int max = 0;
		Constructor best = null;
		// set the best constructor as the default constructor as basement, if there is a default constructor
		for(Constructor c : counter.keySet()){
			if(c.args.isEmpty()){
				best = c;
			}
		}
		
		for(Constructor c : counter.keySet()){
			if(counter.get(c).size() > max){
				max = counter.get(c).size();
				best = c;
			}
		}
		
		return best;
	}
	
	private static boolean isMatched(Token t, ArrayList<Pair> pairs) {
		for(Pair p : pairs){
			Token orgn = p.orign();
			if(t.equals(orgn)){
				return true;
			}
		}
		
		return false;
	}

	private static boolean isStructurallyEquivalent(String t1, String t2) throws IOException {
		HashSet<Token> s1 = getFields(t1);
		HashSet<Token> s2 = getFields(t2);
		
		if(s1 == null && s2 == null){ 
			return false;
		}else if(s1 == null || s2 == null){
			return false;
		}else{
			HashMap<String, Integer> tmp1 = new HashMap<String, Integer>();
			HashMap<String, Integer> tmp2 = new HashMap<String, Integer>();
			
			for(Token t : s1){
				String type = t.getType();
				if(tmp1.containsKey(type)){
					tmp1.put(type, tmp1.get(type)+ 1);
				}else{
					tmp1.put(type, 1);
				}
			}
			
			for(Token t : s2){
				String type = t.getType();
				if(tmp2.containsKey(type)){
					tmp2.put(type, tmp2.get(type)+ 1);
				}else{
					tmp2.put(type, 1);
				}
			}
			
			if(tmp1.keySet().equals(tmp2.keySet())){
				for(String s : tmp1.keySet()){
					if(!tmp1.get(s).equals(tmp2.get(s))){
						return false;
					}
				}
				
				return true;
			}else{
				return false;
			}
		}
	}
	
	private static HashSet<Token> getFields(String type) throws IOException{
		File f = FileUtils.findFile(type + ".java");
		if(f == null){
			// third party or built-in types
			// another possibility is that it is a private type, will fix later
			return null;
		}

		CloneParser p = new CloneParser();
		CompilationUnit cu = p.parse(f.getAbsolutePath());
		FieldVisitor visitor = new FieldVisitor(type, f.getAbsolutePath()); 
		cu.accept(visitor);
		return visitor.fields;
	}

	/**
	 * Check if type1 and type2 have the same supertype
	 * 
	 * @param t1
	 * @param t2
	 * @return
	 * @throws IOException 
	 */
	private static boolean isSiblingType(String t1, String t2) throws IOException {
		// first build type hierarchy for each type
		ArrayList<String> th1 = getTypeHierarchy(t1);
		ArrayList<String> th2 = getTypeHierarchy(t2);
		
		for(String tt1 : th1){
			for(String tt2 : th2){
				if(tt1.equals(tt2)){
					return true;
				}
			}
		}
		
		return false;
	}
	
	private static boolean isSubType(String t1, String t2) throws IOException{
		ArrayList<String> th1 = getTypeHierarchy(t1);
		
		for(String tt1 : th1){
			if(tt1.equals(t2)){
				return true;
			}
		}
		
		return false;
	}
	
	private static ArrayList<String> getTypeHierarchy(String t) throws IOException{
		File f = FileUtils.findFile(t + ".java");
		if(f == null){
			return new ArrayList<String>();
		}else{
			CloneParser p = new CloneParser();
			CompilationUnit cu = p.parse(f.getAbsolutePath());
			TypeVisitor tv = new TypeVisitor(t);
			cu.accept(tv);
			
			ArrayList<String> a = new ArrayList<String>();
			a.addAll(tv.interfaces);
			if(tv.superType == null){
				a.add(t);
				return a;
			}else{
				ArrayList<String> b = getTypeHierarchy(tv.superType);
				a.addAll(b);
				return a;
			}
		}
	}

	/**
	 * Check if type t2 is boxed from t1 
	 * @param t1
	 * @param t2
	 * @return
	 */
	private static boolean isBoxed(String t1, String t2) {
		if(t1.equals("byte") && t2.equals("Byte")){
			return true;
		}else if(t1.equals("boolean") && t2.equals("Boolean")){
			return true;
		}else if(t1.equals("char") && t2.equals("Character")){
			return true;
		}else if(t1.equals("float") && t2.equals("Float")){
			return true;
		}else if(t1.equals("int") && t2.equals("Integer")){
			return true;
		}else if(t1.equals("long") && t2.equals("Long")){
			return true;
		}else if(t1.equals("short") && t2.equals("Short")){
			return true;
		}else if(t1.equals("double") && t2.equals("Double")){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * Check if type t2 is unboxed from t1 
	 * @param t1
	 * @param t2
	 * @return
	 */
	private static boolean isUnboxed(String t1, String t2) {
		if(t2.equals("byte") && t1.equals("Byte")){
			return true;
		}else if(t2.equals("boolean") && t1.equals("Boolean")){
			return true;
		}else if(t2.equals("char") && t1.equals("Character")){
			return true;
		}else if(t2.equals("float") && t1.equals("Float")){
			return true;
		}else if(t2.equals("int") && t1.equals("Integer")){
			return true;
		}else if(t2.equals("long") && t1.equals("Long")){
			return true;
		}else if(t2.equals("short") && t1.equals("Short")){
			return true;
		}else if(t2.equals("double") && t1.equals("Double")){
			return true;
		}else{
			return false;
		}
	}
}

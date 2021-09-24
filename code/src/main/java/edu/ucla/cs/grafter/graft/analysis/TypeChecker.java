package edu.ucla.cs.grafter.graft.analysis;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.core.dom.CompilationUnit;

import edu.ucla.cs.grafter.file.FileUtils;
import edu.ucla.cs.grafter.graft.model.Clone;
import edu.ucla.cs.grafter.graft.model.Pair;
import edu.ucla.cs.grafter.graft.model.Token;

public class TypeChecker {
	
	public boolean check(Set<Pair> matches) throws IOException{
		boolean flag = true;
		for(Pair p : matches){
			if(!check(p.orign().getType(), p.clone().getType())){
				System.out.println(p.toString() + " is not type safe.");
				flag = false;
			}
		}
		return flag;
	}
	
	public boolean check(String t1, String t2) throws IOException{
		if(t1.equals(t2) || isConvertible(t1, t2)){
			return true;
		}else if(isSiblingType(t1, t2)){
			return true;
		}else{
			if(t1.contains("[") && t2.contains("[")){
				// array type
				return isStructuralEqual(t1.substring(0, t1.indexOf("[")), t2.substring(0, t2.indexOf("[")));
			}else if(t1.contains("<") && t2.contains("<")){
				String t11 = t1.substring(0, t1.indexOf("<"));
				String t12 = t1.substring(t1.indexOf("<") + 1, t1.indexOf(">"));
				String t21 = t2.substring(0, t2.indexOf("<"));
				String t22 = t2.substring(t2.indexOf("<") + 1, t2.indexOf(">"));
				return check(t11, t21) && check(t12, t22);
			}else{
				return isStructuralEqual(t1, t2);
			}
		}
	}
	
//	private boolean isSubtype(String t1, String t2) throws IOException {
//		if(t1.equals(t2)){
//			return true;
//		}else{
//			File f = FileUtilities.findFile(t1 + ".java");
//			if(f == null){
//				return false;
//			}else{
//				CloneParser p = new CloneParser();
//				CompilationUnit cu = p.parse(f.getAbsolutePath());
//				TypeVisitor tv = new TypeVisitor(t1);
//				cu.accept(tv);
//				if(tv.superType == null){
//					return false;
//				}else{
//					return isSubtype(tv.superType, t2);
//				}
//			}
//		}
//	}
	
//	private boolean isSibling(String t1, String t2) throws IOException{
//		if(t1.equals(t2)){
//			return true;
//		}else{
//			File f1 = FileUtilities.findFile(t1 + ".java");
//			File f2 = FileUtilities.findFile(t2 + ".java");
//			if(f1 == null || f2 == null){
//				return false;
//			}else{
//				CloneParser p1 = new CloneParser();
//				CompilationUnit cu1 = p1.parse(f1.getAbsolutePath());
//				TypeVisitor tv1 = new TypeVisitor(t1);
//				cu1.accept(tv1);
//				
//				CloneParser p2 = new CloneParser();
//				CompilationUnit cu2 = p2.parse(f2.getAbsolutePath());
//				TypeVisitor tv2 = new TypeVisitor(t2);
//				cu2.accept(tv2);
//				if((tv1.superType != null && tv2.superType != null && tv1.superType.equals(tv2.superType)) || tv1.interfaces.equals(tv2.interfaces)){
//					return true;
//				}else{
//					return false;
//				}
//			}
//		}
//	}

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
	
	private boolean isStructuralEqual(String t1, String t2) throws IOException{
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
	
	private HashSet<Token> getFields(String type) throws IOException{
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
	
	private boolean isConvertible(String t1, String t2) throws IOException {
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
	
	private static boolean isSubType(String t1, String t2) throws IOException{
		ArrayList<String> th1 = getTypeHierarchy(t1);
		
		for(String tt1 : th1){
			if(tt1.equals(t2)){
				return true;
			}
		}
		
		return false;
	}
	
	public static void main(String[] args) throws IOException{
		CloneParser cp = new CloneParser();
		CompilationUnit cu1 = cp.parse("/home/troy/SysAssure/code/SysAssure/example/group1.java");
		CompilationUnit cu2 = cp.parse("/home/troy/SysAssure/code/SysAssure/example/group1.java");
		Clone clone1 = new Clone(6408, 6726, "setExcludes", "group1");
		Clone clone2 = new Clone(5831, 6149, "setIncludes", "group1");
		CloneVisitor cv1 = new CloneVisitor(clone1, cu1, "/home/troy/SysAssure/code/SysAssure/example/group1.java");
		CloneVisitor cv2 = new CloneVisitor(clone2, cu2, "/home/troy/SysAssure/code/SysAssure/example/group1.java");
		cu1.accept(cv1);
		cu2.accept(cv2);
		HashSet<Token> s1 = cv1.getWildVars();
		HashSet<Token> s2 = cv2.getWildVars();
		CloneMatcher cm = new CloneMatcher(s1, s2);
		if(cm.match()){
			ArrayList<Pair> matches =  new ArrayList<Pair>(cm.matches);
			TypeChecker tc = new TypeChecker();
			for(Pair p : matches){
				if(!tc.check(p.orign().getType(), p.clone().getType())){
					System.out.println(p.toString() + " is not type safe.");
				}
			}
		}
	}
}

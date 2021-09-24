package edu.ucla.cs.grafter.port;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.hamcrest.core.IsSame;

import edu.ucla.cs.grafter.config.GrafterConfig;
import edu.ucla.cs.grafter.file.FileUtils;
import edu.ucla.cs.grafter.graft.analysis.CloneCalibrator;
import edu.ucla.cs.grafter.graft.analysis.CloneMatcher;
import edu.ucla.cs.grafter.graft.analysis.CloneVisitor;
import edu.ucla.cs.grafter.graft.analysis.DUAnalyzer;
import edu.ucla.cs.grafter.graft.model.Clone;
import edu.ucla.cs.grafter.graft.model.Matches;
import edu.ucla.cs.grafter.graft.model.Method;
import edu.ucla.cs.grafter.graft.model.Pair;
import edu.ucla.cs.grafter.graft.model.Token;
import edu.ucla.cs.grafter.instrument.JavaParser;

public class Grafter {
	public Clone orign; // the clone to be replaced
	private String src;  // the source code of the file that contains the clone to be replaced
	private HashSet<Token> prestate1;
	private HashSet<Token> poststate1;
	private HashSet<Token> allstate1;
	private HashMap<String, HashSet<Token>> all1;
	private HashMap<String, HashSet<Token>> pre1;
	private HashMap<String, HashSet<Token>> post1;
	private int orign_start;
	private int orign_end;
	private CloneVisitor cv1;

	public Clone clone; // the clone to be grafted
	private String tgt; // the source code of the file that contains the clone to be grafted
	private HashSet<Token> prestate2;
	private HashSet<Token> poststate2;
	private HashSet<Token> allstate2;
	private HashMap<String, HashSet<Token>> all2;
	private HashMap<String, HashSet<Token>> pre2;
	private HashMap<String, HashSet<Token>> post2;
	private int clone_start;
	private int clone_end;
	private CloneVisitor cv2;
	
	private CloneMatcher pre_cm;
	private Matches prematch;
	private HashSet<Token> pre_unmatch_clone;
	private CloneMatcher post_cm;
	public Matches postmatch;
	private HashSet<Token> post_unmatch_clone;
	
	private int methodStart;
	public boolean isMethodEnd;
	String test;
	
	@Deprecated
	public Grafter(String path1, String path2, String m1, String m2, int x1, int y1, int x2, int y2, String test) throws IOException{
		// preserve the source code first
		src = FileUtils.readFileToString(path1);
		tgt = FileUtils.readFileToString(path2);
		
		this.test = test;
		orign = new Clone(path1, m1, x1, y1);
		clone = new Clone(path2, m2, x2, y2);
		init();
		
		pre_cm = match(pre1, pre2, path1.equals(path2));
		post_cm = match(post1, post2, path1.equals(path2));
		
		prematch = new Matches(pre_cm.matches);
		postmatch = new Matches(post_cm.matches);
		pre_unmatch_clone = pre_cm.unmatches2;
		post_unmatch_clone = post_cm.unmatches2;
	}
	
	public Grafter(String path1, String m1, int start1, int end1, String path2, String m2, int start2, int end2, String test) throws IOException{
		// preserve the source code first
		src = FileUtils.readFileToString(path1);
		tgt = FileUtils.readFileToString(path2);
				
		this.test = test;
		int x1 = FileUtils.getStartIndex(src, start1);
		int y1 = FileUtils.getEndIndex(src, end1);
		orign = new Clone(path1, m1, x1, y1);
		
		int x2 = FileUtils.getStartIndex(tgt, start2);
		int y2 = FileUtils.getEndIndex(tgt, end2);
		clone = new Clone(path2, m2, x2, y2);

		init();
		
		pre_cm = match(pre1, pre2, path1.equals(path2));
		post_cm = match(post1, post2, path1.equals(path2));
		
		prematch = new Matches(pre_cm.matches);
		postmatch = new Matches(post_cm.matches);
		pre_unmatch_clone = pre_cm.unmatches2;
		post_unmatch_clone = post_cm.unmatches2;
	}
	
	public Grafter(String path1, int start1, int end1, String path2, int start2, int end2) throws IOException{
		// preserve the source code first
		src = FileUtils.readFileToString(path1);
		tgt = FileUtils.readFileToString(path2);
		
		// find the range of graftable code in each clone
		this.orign_start = start1;
		this.orign_end = end1;
		this.clone_start = start2;
		this.clone_end = end2;
		CompilationUnit cu1 = JavaParser.parse(path1);
		CloneCalibrator calib1 = new CloneCalibrator(cu1, start1, end1);
		cu1.accept(calib1);
		CompilationUnit cu2 = JavaParser.parse(path2);
		CloneCalibrator calib2 = new CloneCalibrator(JavaParser.parse(path2), start2, end2);
		cu2.accept(calib2);
		
		// retrieve the method name containing each clone
		CloneMethodFinder finder1 = new CloneMethodFinder(cu1, start1, end1);
		cu1.accept(finder1);
		String m1 = finder1.method;
		
		CloneMethodFinder finder2 = new CloneMethodFinder(cu2, start2, end2);
		cu2.accept(finder2);
		String m2 = finder2.method;
		
		// create two refined clones with only graftable code
		int x1 = FileUtils.getStartIndex(src, calib1.first);
		int y1 = FileUtils.getEndIndex(src, calib1.last);
		orign = new Clone(path1, m1, x1, y1);
		
		int x2 = FileUtils.getStartIndex(tgt, calib2.first);
		int y2 = FileUtils.getEndIndex(tgt, calib2.last);
		clone = new Clone(path2, m2, x2, y2);

		init();
		
		pre_cm = match(pre1, pre2, path1.equals(path2));
		post_cm = match(post1, post2, path1.equals(path2));
		
		prematch = new Matches(pre_cm.matches);
		postmatch = new Matches(post_cm.matches);
		pre_unmatch_clone = pre_cm.unmatches2;
		post_unmatch_clone = post_cm.unmatches2;
	}
	
	private void init() throws IOException{
		DUAnalyzer a1 = new DUAnalyzer(orign);
		DUAnalyzer a2 = new DUAnalyzer(clone);
		a1.analyze();
		a2.analyze();
		
		methodStart = a1.methodStart;
		isMethodEnd = a1.isMethodEnd;
		prestate1 = a1.prestate;
		prestate2 = a2.prestate;
		poststate1 = a1.poststate;
		poststate2 = a2.poststate;
		allstate1 = a1.allstate;
		allstate2 = a2.allstate;
		all1 = a1.all;
		all2 = a2.all;
		pre1 = a1.pre;
		post1 = a1.post;
		pre2 = a2.pre;
		post2 = a2.post;
		
		cv1 = a1.cv;
		cv2 = a2.cv;
	}
	
	
	/**
	 * The state-level match is slightly different from the test-level match.
	 * Because the test-level match only focuses on the living variables on the entry and exit(s) of a clone, but does not care about the internal ones.
	 * Though there is a return statement in the middle of the clone, we do not need to transport the values of internal local variables because the method returns anyway.
	 * At such point, we only need to transport the values of affected fields. 
	 * However, in the state level, the match should consider all variables used by the clone. 
	 * Because if there is a return statement within the clone, we need to capture all the variables that are alive at the program point of the return statement
	 * instead of just at the end of the clone. 
	 * 
	 * @return
	 */
	public Matches state_level_match(){
		CloneMatcher cm = match(all1, all2, orign.getPath().equals(clone.getPath()));
		cm.match();
		return new Matches(cm.matches);
	}
	
	private CloneMatcher match(HashSet<Token> s1, HashSet<Token> s2){
		CloneMatcher cm = new CloneMatcher(s1, s2);
		if(cm.match()){
			for(Pair match : cm.matches){
				if(GrafterConfig.verbose){
					System.out.println(match.orign().toString() + " <-> " + match.clone().toString());
				}
			}
		}else{
			for(Token unmatch : cm.unmatches2){
				if(GrafterConfig.verbose){
					System.out.println(unmatch.getName() + " doesn't have a match.");
				}
			}
		}
		
		for(Pair p : cm.matches){
			// redefine matched private fields if they are defined in the super types
			Token t = p.orign();
			Token c = p.clone();
			if(t.isField() && t.isPrivate() && !t.getPath().equals(orign.getPath())){
				try {
					redefineField(t);
				} catch (IOException e) {
					e.printStackTrace();
			    }
			}
			
			if(c.isField() && c.isPrivate() && !c.getPath().equals(clone.getPath())){
				try {
					redefineField(c);
				} catch (IOException e) {
					e.printStackTrace();
			    }
			}
		}
		
		return cm;
	}
	
	private HashMap<String, HashSet<Token>> deepCloneHashmap(HashMap<String, HashSet<Token>> vars){
		HashMap<String, HashSet<Token>> set = new HashMap<String, HashSet<Token>>();
		for(String m: vars.keySet()){
			HashSet<Token> tset = new HashSet<Token>(vars.get(m));
			set.put(m, tset);
		}
		return set;
	}
	
	private CloneMatcher match(HashMap<String, HashSet<Token>> vars1, HashMap<String, HashSet<Token>> vars2, boolean isInSameFile){
		CloneMatcher cm = new CloneMatcher();
		
		// deep cloning the given two hashmaps before matching, because Grafter will remove certain variables to avoid repetitive matching
		HashMap<String, HashSet<Token>> s1 = deepCloneHashmap(vars1); 
		HashMap<String, HashSet<Token>> s2 = deepCloneHashmap(vars2);
		
		Set<String> mset1 = s1.keySet();
		Set<String> mset2 = s2.keySet();

		HashMap<String, String> m_map = new HashMap<String, String>();
		// try our best to match methods related to the clone at the target location
		for(String m1 : mset1){
			// fuzzy matching
			double max = -1;
			String match = null;
			for(String m2: mset2){
				int distance = StringUtils.getLevenshteinDistance(m1, m2);
				int length = Math.max(m1.length(), m2.length());
				double score = 1 - (double)distance/length;
				if(score > max){
					match = m2;
					max = score;
				}
			}
			
			m_map.put(m1, match);
		}
		
		// start with the method containing the code clone
		HashSet<Token> tsc1 = s1.get("clone");
		HashSet<Token> tsc2 = s2.get("clone");
		cm.tokens1.addAll(tsc1);
		cm.tokens2.addAll(tsc2);
		CloneMatcher mc = match(tsc1, tsc2);
		cm.matches.addAll(mc.matches);
		cm.unmatches1.addAll(mc.unmatches1);
		cm.unmatches2.addAll(mc.unmatches2);
		
		// process the callees
		for(String m1 : m_map.keySet()){
			if(m1.equals("clone")) continue;
			
			String m2 = m_map.get(m1);
			if(isInSameFile && m1.equals(m2)){
				// do not need to match because they are the same method in the same file
				// this is necessary because we have seen cases in Apache Ant where two clones call the same method in the same file
				// and that method uses two variables corresponded between two clones, let's say, excludes and includes,
				// then when matching the same method with itself, includes is matched with itself instead of excludes, which causes trouble.
				continue;
			}
			
			HashSet<Token> ts1 = s1.get(m1);
			// avoid repetitive matching
			ts1.removeAll(cm.tokens1);
			HashSet<Token> ts2 = s2.get(m_map.get(m1));
			ts2.removeAll(cm.tokens2);
			cm.tokens1.addAll(ts1);
			cm.tokens2.addAll(ts2);
			CloneMatcher m = match(ts1, ts2);
			cm.matches.addAll(m.matches);
			cm.unmatches1.addAll(m.unmatches1);
			cm.unmatches2.addAll(m.unmatches2);
		}
		
		// put all fields in the unmatched methods in the set of unmatched variables
		HashSet<String> unmatched_methods = new HashSet<String>(mset2);
		unmatched_methods.removeAll(m_map.values());
		for(String m : unmatched_methods){
			HashSet<Token> ts2 = new HashSet<Token>(s2.get(m));
			// remove fields that have already been matched
			ts2.removeAll(cm.tokens2);
			cm.unmatches2.addAll(ts2);
		}
		
		// Bug fix: in Apache Ant clone pair#18, one clone uses the variable performGc in its containing method 
		// but the other clone uses a variable with the same name in a called method. These two variables should be matched. 
		// So we need to do another round of matching on the unmatched variables.
		HashSet<Token> tmp1 = new HashSet<Token>();
		HashSet<Token> tmp2 = new HashSet<Token>();
		for(Token t1 : cm.unmatches1){
			for(Token t2 : cm.unmatches2){
				String name1 = t1.getName().replace("_", "");
				String name2 = t2.getName().replace("_", "");
				if(name1.equalsIgnoreCase(name2)){
					cm.matches.add(new Pair(t1, t2));
					tmp1.add(t1);
					tmp2.add(t2);
				}
			}
		}
		cm.unmatches1.removeAll(tmp1);
		cm.unmatches2.removeAll(tmp2);
		
		return cm;
	}
	
	public Clone graft(boolean isFirstGraft) throws IOException{		
		int x1 = orign.getX();
		int y1 = orign.getY();
		
		String code_clone = clone.getCode(); 
				
		String before = "";
		
		int count_port_decls = 0;
		
		if(!orign.getPath().equals(clone.getPath())){
			// we need to port the declarations of imports, fields, and methods used by the grafted clone to make sure there is no syntactic error when we copy code clones over
			String[] lines = src.substring(0, x1).split(System.lineSeparator());
			for(int i = 0; i < lines.length; i++){
				
				// port undefined methods and fields to the location right before the method containing the counterpart clone
				if(i == this.methodStart - 1){
					HashSet<Token> temp = new HashSet<Token>();
					temp.addAll(prestate2);
					temp.addAll(poststate2);
					for(Token t : temp){
						if(t.isField()){
							Token orign = null;
						
							if(prematch.getOrigin(t) != null){
								orign = prematch.getOrigin(t);
							}else{
								orign = postmatch.getOrigin(t);
							}
						
							if(orign != null && cv1.field_decls.containsKey(t.getName())){
								// avoid redefine
								continue;
							}else{
								// unmatched field or matched but undeclared
								String fd = cv2.field_decls.get(t.getName()).toString();
								
							    // handle references to the containing class of the grafted clone, if any
								if(fd.contains(clone.getOwner())){
									fd = fd.replaceAll(clone.getOwner(), this.orign.getOwner());
								}
								
								before += fd;
								count_port_decls += fd.split(System.lineSeparator()).length;
							}
						}
					}
					
					HashSet<Method> undef = new HashSet<Method>(cv2.method_decls.keySet());
					undef.removeAll(cv1.method_decls.keySet());
					HashSet<String> calls = new HashSet<String>(pre2.keySet());
					for(String m : calls){
						if(m.equals("clone")) continue;
						for(Method method : undef){
							if(method.name.equals(m)){
								// port the undefined method
								MethodDeclaration md = cv2.method_decls.get(method);
								String s = md.toString();
								before += s;
								count_port_decls += s.split(System.lineSeparator()).length;
							}
						}
					}
				}
				
				before += lines[i] + System.lineSeparator();
				
				// port undefined imports right after the package declaration
				if(lines[i].contains("package ")){
					HashSet<String> is1 = new HashSet<String>(cv1.import_decls);
					HashSet<String> is2 = new HashSet<String>(cv2.import_decls);
					// porting the imports that do not appear in the other file may introduce unused imports
					// this is not a good programming habit, but we choose to implement this for the simplicity
					is2.removeAll(is1);
					for(String s : is2){
						before += s;
						count_port_decls += s.split(System.lineSeparator()).length;
					}
				}
			}
		}else{
			before += src.substring(0, x1);
		}
		
		// update the living range for the tokens after porting import, field, and method declarations
		if(isFirstGraft){
			updateLiveRange(0, count_port_decls);
		}
		
		String after = src.substring(y1);
		
		String prestate_transform = "";
		if(orign.getPath().equals(clone.getPath()) && orign.getMethod().equals(clone.getMethod()) && orign.getParameters().equals(clone.getParameters())){
			// put the synthesized code in a block to avoid syntactic conflicts, e.g., redefined variables
			prestate_transform += "{" + System.lineSeparator();
		}
		
		// save field values affected by the code clone
		if(orign.getPath().equals(clone.getPath())){
			// if original location and cloned location are in the same file, need to preserve all fields that are used or affected by the cloned location to eliminate side effects
			for(Token t : postmatch.getAllClones()){
				Token orign = postmatch.getOrigin(t);
				if(t.isField() && !t.isFinal() && !orign.getName().equals(t.getName())){
					// need to preserve the fields that are used at the cloned location but not at the original location
					prestate_transform += t.getType() + " saved_" + t.getName() + " = " + (t.isStatic ? this.clone.getOwner() + "." : "this.") + t.getName() + ";" + System.lineSeparator();
				}
			} 
		}else{
			// If there are duplicated fields between two clones and if such fields are modified, we also need to restore their values to eliminate side effects
			for(Token t : postmatch.getAllClones()){
				Token orign = postmatch.getOrigin(t);
				if(t.isField() && !t.isFinal() && orign != null && !orign.getName().equals(t.getName()) && cv1.field_decls.containsKey(t.getName())){
					// need to preserve the fields that are used at the cloned location but not at the original location
					prestate_transform += t.getType() + " saved_" + t.getName() + " = " + (t.isStatic ? this.clone.getOwner() + "." : "this.") + t.getName() + ";" + System.lineSeparator();
				}
			}
		}
		
		for(Token t : prematch.getAllClones()){
			Token orign = prematch.getOrigin(t);
			
			// TODO: have seen a case where clones use variables with the same name but different type
//			if(t.getName().equals(orign.getName()) && !t.getType().equals(orign.getType())){
//				// rename variables in the code clone to avoid naming conflict
//				code_clone.replace(t.getName(), "_" + t.getName());
//				t.setName("_" + t.getName());
//			}
			if(!t.isFinal()){
				prestate_transform += prestate_transform(orign, t);
			}
		}
		
		for(Token t : pre_unmatch_clone){
			if(!(t.isField() && t.isInitialized())){
				// mock the values of unmatched clones, if they are not initialized
				prestate_transform += (t.isField() ? (t.isStatic ? this.clone.getOwner() + "." : "this.") : t.getType() + " ") + t.getName() + " = " + Mocker.mock(t.getType()) + ";" + System.lineSeparator();
			}
		}
		
		// update living range for tokens after prestate transformation
		if(isFirstGraft && prestate_transform.length() > 0){
			updateLiveRange(this.clone.getStart(), prestate_transform.split(System.lineSeparator()).length);
		}
		
		// rewrite code clone to do post transformation for all used fields before any program exits
		String poststate_transform_fields = "";
		for(Token t : postmatch.getAllClones()){
			Token orign = postmatch.getOrigin(t);
			
			if(t.isField() && !t.isFinal()){
				poststate_transform_fields += poststate_transform(t, orign);

				// eliminate side effects 
				if(!orign.getName().equals(t.getName()) && ((this.orign.getPath().equals(this.clone.getPath()))
						|| (orign != null && cv1.field_decls.containsKey(t.getName())))){
					poststate_transform_fields += (t.isStatic ? this.clone.getOwner() + "." : "this.") + t.getName() + " = saved_" + t.getName() + ";" + System.lineSeparator();
				}
			}
		}
		
		String[] clone_lines = code_clone.split(System.lineSeparator());
		code_clone = ""; // reset clone's code
		for(int i = 0; i < clone_lines.length; i++){
			String s = clone_lines[i];
			
			if (s.contains("return this;") && poststate_transform_fields.length() > 0) {
				// do not rewrite the return statement
				code_clone += poststate_transform_fields;
				code_clone += "return this;";
				
				// update living range of tokens after poststate transform
				if(isFirstGraft){
					updateLiveRange(this.clone.getStart() + i, poststate_transform_fields.split(System.lineSeparator()).length + 1);
				}
					
				continue;
			}else if (s.contains("return ") && !s.substring(0, s.indexOf("return ")).contains("//") && poststate_transform_fields.length() > 0){
				// At program exit point, return XXX except "return this" should be rewritten as Type result = XXX; restore program states; return result;
				String return_expression = s.substring(s.indexOf("return ") + 7);
				code_clone += clone.getType() + " tmp_return_value = " +  return_expression + System.lineSeparator();
				code_clone += poststate_transform_fields;
				if(i == clone_lines.length - 1){
					code_clone += "return tmp_return_value;";
				}else{
					code_clone += "return tmp_return_value;" + System.lineSeparator();
				}
				
				// update living range of tokens after poststate transform
				if(isFirstGraft){
					updateLiveRange(this.clone.getStart() + i, poststate_transform_fields.split(System.lineSeparator()).length
						+ return_expression.split(System.lineSeparator()).length + 1);
				}
					
				continue;
			}
			
			if(s.contains("throw ") && poststate_transform_fields.length() > 0){
				code_clone += poststate_transform_fields;
				// update living range of tokens after poststate transform
				if(isFirstGraft){
					updateLiveRange(this.clone.getStart() + i, poststate_transform_fields.split(System.lineSeparator()).length);
				}
			}
			
			if(i == clone_lines.length - 1){
				code_clone += s; 
			}else{
				code_clone += s + System.lineSeparator();
			}
		}
		
		// handle recursions in the code clone, if any
		if(clone.getMethod() != null && orign.getMethod() != null && code_clone.contains(clone.getMethod())){
			code_clone = code_clone.replace(clone.getMethod() + "(", orign.getMethod() + "(");
		}
		
		// handle references to the containing class of the grafted clone, if any
		if(code_clone.contains(clone.getOwner())){
			code_clone = code_clone.replaceAll(clone.getOwner(), orign.getOwner());
		}
		
		String poststate_transform = "";
		int i = 0;
		for(Token t : postmatch.getAllClones()){
			Token orign = postmatch.getOrigin(t);
			
			// If it is a local variable but is declared only within the target clone not the source one then we have to re-declare it before the transformation
			if(t.isLocalVariable() && !orign.getName().equals(t.getName()) && !prematch.getAllClones().contains(t)){
				poststate_transform = orign.getType() + " " + orign.getName() + ";" + System.lineSeparator();
			}
			
			if(!orign.isFinal()){
				poststate_transform += poststate_transform(t, orign);
			}
			
			if(t.isField() && !t.isFinal() && !orign.getName().equals(t.getName()) && 
					(
							(this.orign.getPath().equals(this.clone.getPath())) 
							|| 
							(orign != null && cv1.field_decls.containsKey(t.getName()))
					)
			){
				// eliminate the side effect (affected fields that are not used at the original location but only at the cloned location)
				if(i == postmatch.getAllClones().size() - 1){
					poststate_transform += (t.isStatic ? this.clone.getOwner() + "." : "this.") + t.getName() + " = saved_" + t.getName() + ";";
				}else{
					poststate_transform += (t.isStatic ? this.clone.getOwner() + "." : "this.") + t.getName() + " = saved_" + t.getName() + ";" + System.lineSeparator();
				}
			}
			
			i++;
		}
		
		// check the reachability of code clone
		String s = "";
		if(isMethodEnd && code_clone.contains("return ")){
			// no need to do post transformation after executing the code clone
			if(orign.getPath().equals(clone.getPath()) && orign.getMethod().equals(clone.getMethod()) && orign.getParameters().equals(clone.getParameters())){
				// put the synthesized code in a block to avoid syntactic conflicts, e.g., redefined variables
				code_clone += "}\n";
			}
			
			s = before + prestate_transform + code_clone + after;
			
			// empty the poststate transform code to correctly calculate the end line number of the grafted clone in the following code
			poststate_transform = "";
		}else{
			if(orign.getPath().equals(clone.getPath()) && orign.getMethod().equals(clone.getMethod()) && orign.getParameters().equals(clone.getParameters())){
				// put the synthesized code in a block to avoid syntactic conflicts, e.g., redefined variables
				poststate_transform += "}\n";
			}
			
			s = before + prestate_transform + code_clone + System.lineSeparator() + poststate_transform + after;
			
			// update the living range of tokens after poststate transform
			if(isFirstGraft && poststate_transform.length() > 0){
				updateLiveRange(this.clone.getEnd(), poststate_transform.split(System.lineSeparator()).length);
			}
		}
		
		// preserve the old file
		File bak = new File(orign.getPath() + ".bak");
		File file = new File(orign.getPath());
		if(!bak.exists()){
			file.renameTo(bak);
		}
		
		// write to the file
		File new_file = new File(orign.getPath());
		new_file.createNewFile();
		FileUtils.writeStringtoFile(s, orign.getPath());
		
		int new_start = this.orign_start + count_port_decls; 
		int new_X = FileUtils.getStartIndex(s, new_start);
		int new_end = this.orign_end + count_port_decls 
				+ (prestate_transform.isEmpty() ? 0 : prestate_transform.split(System.lineSeparator()).length) 
				+ code_clone.split(System.lineSeparator()).length 
				+ (poststate_transform.isEmpty() ? 0 :poststate_transform.split(System.lineSeparator()).length) 
				- (orign.getEnd() - orign.getStart() + 1); 
		int new_Y = FileUtils.getEndIndex(s, new_end);
		
		// update the living range of tokens in the grafted clone to the corresponding range after porting it to the target location
		if(isFirstGraft){
			for(Token t : allstate2){
				if(!t.isField()){
					t.graft_start = t.graft_start - this.clone.getStart() /* offset */ + this.orign.getStart();
					t.graft_end = t.graft_end - this.clone.getStart() /* offset */ + this.orign.getStart();
				}
			}	
		}
		
		return new Clone(orign.getPath(), orign.getMethod(), new_X, new_Y);
	}
	
	/**
	 * It is copied from TranSynthesizer. Therefore it should be updated consistently with the original method.
	 * 
	 * @param t
	 * @throws IOException
	 */
	private void redefineField(Token t) throws IOException{
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
	
	/**
	 * @param line the line number in the clone before grafting
	 * @param count the number of lines of the stub code we just inserted in the last action
	 */
	private void updateLiveRange(int line, int count){
		for(Token t : allstate2){
			if(!t.isField()){
				// initialize if haven't
				if(t.graft_start == Integer.MAX_VALUE) t.graft_start = t.start;
				if(t.graft_end == Integer.MIN_VALUE) t.graft_end = t.end;
				
				if(t.start >= line){
					// the code is inserted before the token is declared
					t.graft_start += count;
					t.graft_end += count;
				}else if(t.start < line && t.end >= line){
					// the code is inserted within the range where the token is alive
					t.graft_end += count;
				}
			}
		}
	}
	
	/**
	 * Restore the instrumented file
	 * 
	 * @throws IOException
	 */
	public void restore() throws IOException{
		FileUtils.writeStringtoFile(src, orign.getPath());
	}
	
	private String prestate_transform(Token t1, Token t2) throws IOException{
		if(t1.getType().equals(t2.getType()) && t1.getName().equals(t2.getName())){
			// no need to transform, return an empty string
			return "";
		}
		
		String code = "";
		if(!t2.isField()){
			// if the token is a local variable, declare it first
			code += t2.getType() + " " + t2.getName() + ";" + System.lineSeparator();
		}
		
		code += TranSynthesizer.transform(t1, t2, orign, clone) + System.lineSeparator();
		return code;
	}
	
	private String poststate_transform(Token t1, Token t2) throws IOException{
		if(t1.getType().equals(t2.getType()) && t1.getName().equals(t2.getName())){
			// no need to transform, return an empty string
			return "";
		}
		
		String code = "";
		code += TranSynthesizer.transform(t1, t2, orign, clone) + System.lineSeparator();
				
		return code;
	}
}

class CloneMethodFinder extends ASTVisitor{
	CompilationUnit cu;
	int start;
	int end;
	String method;
	
	public CloneMethodFinder(CompilationUnit cu, int start, int end){
		this.cu = cu;
		this.start = start;
		this.end = end;
	}
	public boolean visit(MethodDeclaration node){
		int start = cu.getLineNumber(node.getStartPosition());
		int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
		if(start <= this.start && end >= this.end){
			method = node.getName().toString();
		}
		return false;
	}
}

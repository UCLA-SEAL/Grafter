package edu.ucla.cs.grafter.graft.analysis;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import edu.ucla.cs.grafter.graft.model.Clone;
import edu.ucla.cs.grafter.graft.model.Method;
import edu.ucla.cs.grafter.graft.model.Token;


public class DUAnalyzer {
	Clone clone;
	String path;
	public HashSet<Token> prestate; // program state for the test level, including alive variables at the beginning of the clone
	public HashSet<Token> poststate; // program state for the test level, including alive variables at the end of the clone
	public HashSet<Token> allstate;// program state for the state level, including variables that are alive at any program point in the clone
	public HashMap<String, HashSet<Token>> all;  
	public HashMap<String, HashSet<Token>> pre;
	public HashMap<String, HashSet<Token>> post;
	
	public int methodStart = 0; // the starting line number of the method containing code clones
	public boolean isMethodEnd = false;
	
	public CloneVisitor cv;
	
	public DUAnalyzer(Clone c, String path){
		this.clone = c;
		this.path = path;
		this.prestate = new HashSet<Token>();
		this.poststate = new HashSet<Token>();
		this.allstate = new HashSet<Token>();
		this.all = new HashMap<String, HashSet<Token>>();
		this.pre = new HashMap<String, HashSet<Token>>();
		this.post = new HashMap<String, HashSet<Token>>();
	}
	
	public DUAnalyzer(Clone c){
		this.clone = c;
		this.path = c.getPath();
		this.prestate = new HashSet<Token>();
		this.poststate = new HashSet<Token>();
		this.allstate = new HashSet<Token>();
		this.all = new HashMap<String, HashSet<Token>>();
		this.pre = new HashMap<String, HashSet<Token>>();
		this.post = new HashMap<String, HashSet<Token>>();
	}

	public void analyze() throws IOException{
		HashSet<Token> uses = new HashSet<Token>();
		HashSet<Token> alive = new HashSet<Token>();
		
		CloneParser cp = new CloneParser();
		CompilationUnit cu = cp.parse(path);
		
		cv = new CloneVisitor(clone, cu, path);
		cu.accept(cv);
		this.methodStart = cv.methodStart;
		this.isMethodEnd = cv.isMethodEnd;
		uses.addAll(cv.wilds);
		alive.addAll(cv.uses);
		alive.addAll(cv.defs);
		alive.removeAll(cv.vars2);
		
		this.pre.put("clone", new HashSet<Token>(uses));
		this.post.put("clone", new HashSet<Token>(alive));
		HashSet<Token> all = new HashSet<Token>(alive);
		all.addAll(cv.vars2);
		this.all.put("clone", new HashSet<Token>(all));
		
		LinkedList<String> queue = new LinkedList<>(cv.callstack);
		HashSet<String> visited = new HashSet<String>();
		while(!queue.isEmpty()){
			String m = queue.pop();
			CalleeVisitor cv2 = new CalleeVisitor(m, cv.fields);
			HashSet<MethodDeclaration> ms = new HashSet<MethodDeclaration>();
			for(Method method : cv.method_decls.keySet()){
				if(method.name.equals(m)){
					ms.add(cv.method_decls.get(method));
				}
			}
			
			HashSet<Token> fs = new HashSet<Token>();
			for(MethodDeclaration md : ms){
				md.accept(cv2);
				
				fs.addAll(cv2.effects);
				fs.addAll(cv2.uses);
				
				uses.addAll(fs);
				alive.addAll(fs);
				
				this.pre.put(m, fs);
				this.post.put(m, fs);
				this.all.put(m, fs);
				
				visited.add(m);
				
				for(String c : cv2.callstack){
					if(!visited.contains(c)){
						queue.push(c);
					}
				}
			}
		}
		
		this.prestate.addAll(uses);
		this.poststate.addAll(alive);
		this.allstate.addAll(alive);
		this.allstate.addAll(cv.vars2);
	}
}

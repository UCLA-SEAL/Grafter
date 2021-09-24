package edu.ucla.cs.grafter.graft.model;

import java.util.ArrayList;

/**
 * This class is an abstraction of java method because we find it too expensive
 * to store the MethodDeclaration ASTNode and just the method name is not
 * sufficient to handle overloaded methods.
 * 
 * @author troy
 *
 */
public class Method {
	public String name;
	public ArrayList<String> args;
	
	public Method(String name, ArrayList<String> args){
		this.name = name;
		this.args = new ArrayList<String>();
		for(String arg : args){
			this.args.add(arg);
		}
	}
	
	@Override
	public boolean equals(Object obj){
		if(obj instanceof Method){
			Method other = (Method)obj;
			return other.name.equals(this.name) && other.args.equals(this.args);
		}
		
		return false;
	}
	
	@Override
	public int hashCode(){
		return name.hashCode() + args.hashCode();
	}
}

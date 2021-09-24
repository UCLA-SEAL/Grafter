package edu.ucla.cs.grafter.graft.analysis;

import edu.ucla.cs.grafter.graft.model.Token;

public class Getter {
	public String name;
	public Token token;
	public String returnType;
	
	public Getter(String name, Token t, String returnType){
		this.name = name;
		this.token = t;
		this.returnType = returnType;
	}
}

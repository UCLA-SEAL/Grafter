package edu.ucla.cs.grafter.graft.analysis;

import java.util.ArrayList;

import edu.ucla.cs.grafter.graft.model.Token;

public class Setter {
	public String name;
	public Token token;
	public ArrayList<Token> args;
	
	public Setter(String name, Token t, ArrayList<Token> args){
		this.name = name;
		this.token = t;
		this.args = new ArrayList<Token>(args);
	}
}

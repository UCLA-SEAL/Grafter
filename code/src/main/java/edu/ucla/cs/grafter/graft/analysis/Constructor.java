package edu.ucla.cs.grafter.graft.analysis;

import java.util.ArrayList;

import edu.ucla.cs.grafter.graft.model.Token;


public class Constructor {
	public String name;
	public ArrayList<Token> args;
	
	public Constructor(String name, ArrayList<Token> args){
		this.name = name;
		this.args = new ArrayList<Token>(args);
	}
}

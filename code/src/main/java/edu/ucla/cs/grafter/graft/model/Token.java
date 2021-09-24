package edu.ucla.cs.grafter.graft.model;

import java.util.ArrayList;
import java.util.HashSet;

public class Token {
	String type; // method return type, variable type and field type
	Label label; // method, local variable, field
	String name; // token name
	Object value; // token value, if any
	String init; // store the initialization express, if any
	HashSet<ArrayList<String>> usage; // all paths to its usage 
	boolean isFinal = false;
	boolean isInitialized = false;
	boolean isPrivate = false;
	boolean isProtected = false;
	public boolean isStatic = false;
	public int start = Integer.MAX_VALUE;
	public int end = Integer.MIN_VALUE;
	public int graft_start = Integer.MAX_VALUE;
	public int graft_end = Integer.MIN_VALUE;
	String path;
	
	
	/**
	 * Clone this token to a new token with the given name
	 * This API is only used for the instrumentation&serialization purpose in the state-compare process, please do not use elsewhere.
	 * @param name
	 * @return
	 */
	public Token clone(String name){
		Token t = new Token(this.type, this.label, name);
		t.value = value;
		t.init = init;
		t.usage = usage;
		t.isFinal = isFinal;
		t.isInitialized = isInitialized;
		t.isPrivate = isPrivate;
		t.isProtected = isProtected;
		t.isStatic = isStatic;
		t.start = start;
		t.end = end;
		return t;
	}
	
	public Token(String type, Label label, String name, Object value){
		this.type = type;
		this.label = label;
		this.name = name;
		this.value = value;
		this.usage = new HashSet<ArrayList<String>>();
	}
	
	public Token(String type, Label label, String name){
		this.type = type;
		this.label = label;
		this.name = name;
		this.usage = new HashSet<ArrayList<String>>();
	}
	
	public Token(String type, Label label, String name, boolean isFinal){
		this.type = type;
		this.label = label;
		this.name = name;
		this.isFinal = isFinal;
		this.usage = new HashSet<ArrayList<String>>();
	}
	
	public Token(String type, Label label, String name, boolean isFinal, boolean isPrivate){
		this.type = type;
		this.label = label;
		this.name = name;
		this.isFinal = isFinal;
		this.isPrivate = isPrivate;
		this.usage = new HashSet<ArrayList<String>>();
	}
	
	public Token(String type, Label label, String name, boolean isFinal, boolean isPrivate, boolean isInitialized){
		this.type = type;
		this.label = label;
		this.name = name;
		this.isFinal = isFinal;
		this.isPrivate = isPrivate;
		this.isInitialized = isInitialized;
		this.usage = new HashSet<ArrayList<String>>();
	}
	
	public Token(String type, Label label, String name, boolean isFinal, boolean isPrivate, boolean isProtected, boolean isInitialized){
		this.type = type;
		this.label = label;
		this.name = name;
		this.isFinal = isFinal;
		this.isPrivate = isPrivate;
		this.isProtected = isProtected;
		this.isInitialized = isInitialized;
		this.usage = new HashSet<ArrayList<String>>();
	}
	
	public void addUsage(ArrayList<String> path){
		// remove irrelevant nodes and  abstract control nodes
		ArrayList<String> tmp = new ArrayList<String>();
		for(String s: path){
			switch(s){
			case "ForStatement": tmp.add("LOOP");break;
			case "EnhancedForStatement": tmp.add("LOOP"); break;
			case "WhileStatement": tmp.add("LOOP"); break;
			case "IfStatement": tmp.add("CONDITION"); break;
			case "SwitchStatement": tmp.add("CONDITION"); break;
			case "SwitchCase": break;
			case "Block": break;
			case "InfixExpression": break;
			case "ParenthesizedExpression": break;
			default: tmp.add(s); break;
			}
		}
		
		// remove repeated if-else-if statements, since the location doesn't matter
		ArrayList<String> tmp2 = new ArrayList<String>();
		for(String s : tmp){
			if(s.equals("CONDITION") && tmp2.size() != 0 && tmp2.get(tmp2.size() - 1).equals("CONDITION")){
				continue;
			}else{
				tmp2.add(s);
			}
		}
		
		this.usage.add(tmp2);
	}
	
	public HashSet<ArrayList<String>> getAllUsage(){
		return this.usage;
	}
	
	public void setValue(Object o){
		this.value = o;
	}
	
	public Object getValue(){
		return this.value;
	}
	
	public String getType() {
		return type;
	}
	
	public String getInit(){
		return this.init;
	}
	
	public void setInit(String init){
		this.init = init;
	}
	
	public AtomicType getAtomicType() throws Exception {
		if(type.equals("int") || type.equals("Integer")){
			return AtomicType.INTEGER;
		}else if(type.equals("double") || type.equals("Double")){
			return AtomicType.DOUBLE;
		}else if(type.equals("float") || type.equals("Float")){
			return AtomicType.FLOAT;
		}else if(type.equals("String")){
			return AtomicType.STRING;
		}else if(type.equals("boolean") || type.equals("Boolean")){
			return AtomicType.BOOLEAN;
		}else if(type.equals("char")){
			return AtomicType.CHAR;
		}else{
			throw new Exception("Unknown type for transformation");
		}
	}

	public void setType(String type) {
		this.type = type;
	}

	public Label getLabel() {
		return label;
	}

	public void setLabel(Label label) {
		this.label = label;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getPath() {
		return path;
	}
	
	public void setPath(String path){
		this.path = path;
	}

	public boolean isField(){
		return label == Label.FIELD;
	}
	
	public boolean isLocalVariable(){
		return label == Label.LOCAL;
	}
	
	public boolean isMethod(){
		return label == Label.METHOD;
	}
	
	public boolean isFinal(){
		return this.isFinal;
	}
	
	public boolean isPrivate(){
		return this.isPrivate;
	}

	public boolean isProtected(){
		return this.isProtected;
	}
	
	public boolean isInitialized(){
		return this.isInitialized;
	}
	
	@Override
	public boolean equals(Object o){
		if(o instanceof Token){
			Token t = (Token)o;
			return this.label == t.label && this.name.equals(t.name) && this.type.equals(t.type);
		}else{
			return false;
		}
	}
	
	@Override
	public int hashCode(){
		return this.label.hashCode() + this.name.hashCode() + this.type.hashCode();
	}
	
	@Override
	public String toString(){
		return label.toString() + " - " + type + " " + name;
	}
}

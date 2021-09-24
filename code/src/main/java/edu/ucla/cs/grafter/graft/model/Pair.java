package edu.ucla.cs.grafter.graft.model;

public class Pair {
	Token t1;
	Token t2;
	
	public Pair(Token orign, Token clone){
		this.t1 = orign;
		this.t2 = clone;
	}
	
	public Token orign(){
		return t1;
	}
	
	public Token clone(){
		return t2;
	}
	
	@Override
	public boolean equals(Object obj){
		if(obj instanceof Pair){
			Pair pair = (Pair)obj;
			return this.t1.equals(pair.t1) && this.t2.equals(pair.t2);
		}else{
			return false;
		}
	}
	
	@Override
	public int hashCode(){
		return this.orign().hashCode() + this.clone().hashCode();
	}
	
	@Override
	public String toString(){
		return t1.name + " <-> " + t2.name;
	}
}

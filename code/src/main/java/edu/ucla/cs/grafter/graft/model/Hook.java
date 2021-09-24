package edu.ucla.cs.grafter.graft.model;

public class Hook {
	public int hook;
	public int constructor;
	public int wrapper;
	public int imprt;
	
	public Hook(int imprt, int constructor, int hook, int wrapper){
		this.imprt = imprt;
		this.hook = hook;
		this.constructor = constructor;
		this.wrapper = wrapper;
	}
}

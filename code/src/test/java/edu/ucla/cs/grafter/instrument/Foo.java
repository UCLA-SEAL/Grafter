package edu.ucla.cs.grafter.instrument;

public class Foo {
	String content;
	
	public Foo(String text){
		this.content = text;
	}
	
	public void print(){
		System.out.println("[Grafter]" + TestTracker.getTestName());
		System.out.println(content);
	}
}

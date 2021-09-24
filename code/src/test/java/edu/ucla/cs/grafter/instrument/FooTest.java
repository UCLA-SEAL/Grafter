package edu.ucla.cs.grafter.instrument;

import static org.junit.Assert.assertEquals;

import org.junit.Ignore;
import org.junit.Test;

public class FooTest {
	
	@Test
	@Ignore
	public void test1(){
		Foo foo = new Foo("hello");
		assertEquals(foo.content, "hello");
	}
	
	@Test
	public void test2(){
		Foo foo = new Foo("hello");
		foo.print();
	}
	
}

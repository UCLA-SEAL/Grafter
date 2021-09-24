package edu.ucla.cs.grafter.data;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;

public class CloneFilterTest {
	static final String junit4Test = "/home/troy/SysAssure/dataset/java-apns-1.0.0.Beta3/src/test/java/com/notnoop/apns/PayloadBuilderTest.java";
	static final String junit4TestWithExtaConstraints = 
			"/home/troy/SysAssure/dataset/java-apns-1.0.0.Beta3/src/test/java/com/notnoop/apns/integration/ApnsConnectionCacheTest.java";
	static final String junit3Test = "/home/troy/SysAssure/dataset/TimeAndMoney/tests/src/com/domainlanguage/money/MoneyTest.java";
	static final String nonTest = "/home/troy/SysAssure/code/Grafter/Grafter/src/main/java/edu/ucla/cs/grafter/ui/CloneGroupPanel.java";
	
	@Test
	@Ignore
	public void testFilterJUnit4(){
		TestFileChecker filter = new TestFileChecker();
		try {
			assertTrue(filter.filter(junit4Test));
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	@Ignore
	public void testFilterJUnit3(){
		TestFileChecker filter = new TestFileChecker();
		try {
			assertTrue(filter.filter(junit3Test));
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test 
	@Ignore
	public void testFilterNonTestFile(){
		TestFileChecker filter = new TestFileChecker();
		try {
			assertFalse(filter.filter(nonTest));
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	@Ignore
	public void testFilterJUnit4WithExtraContraint(){
		TestFileChecker filter = new TestFileChecker();
		try {
			assertTrue(filter.filter(junit4TestWithExtaConstraints));
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
	}
}

package edu.ucla.cs.grafter.process;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;

import edu.ucla.cs.grafter.config.GrafterConfig;

public class TestRunnerTest {
	
	@Test
	public void testMVNSurefire(){
		GrafterConfig.config("/home/troy/SysAssure/code/Grafter/Grafter/Grafter(Java-Apns).config");
		boolean result;
		try {
			result = TestRunner.runTestUsingMVNSurefire("com.notnoop.apns.PayloadBuilderTest", "abitComplicatedEnglishLength");
			assertTrue(result);
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
	}
}

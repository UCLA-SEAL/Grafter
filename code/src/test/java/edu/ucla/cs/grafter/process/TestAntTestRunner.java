package edu.ucla.cs.grafter.process;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;

import edu.ucla.cs.grafter.config.GrafterConfig;

public class TestAntTestRunner {
	
	@Test
	@Ignore
	public void testRewriteBuildXMLMethod(){
		GrafterConfig.config();
		try {
			AntTestRunner.rewriteBuildXML("org.apache.tools.ant.types.FileSetTest");
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	@Ignore
	public void testRunAntTestMethod(){
		GrafterConfig.config();
		assertTrue(AntTestRunner.runAntTestMethod("org.apache.tools.ant.types.PatternSetTest", "testCircularReferenceCheck"));
	}
}

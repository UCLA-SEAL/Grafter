package edu.ucla.cs.grafter.compare;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.JOptionPane;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.sonatype.aether.resolution.DependencyResolutionException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import edu.ucla.cs.grafter.config.BuildTool;
import edu.ucla.cs.grafter.config.GrafterConfig;
import edu.ucla.cs.grafter.file.FileUtils;
import edu.ucla.cs.grafter.process.AntTestRunner;
import edu.ucla.cs.grafter.process.TestRunner;
import edu.ucla.cs.grafter.ui.CloneTreeNode;
import edu.ucla.cs.grafter.ui.GroupTreeNode;
import edu.ucla.cs.grafter.ui.TestTreeNode;

public class TestCompare extends BehaviorCompare {
	// all test cases
	HashSet<ImmutablePair<String, String>> leftTests = new HashSet<ImmutablePair<String, String>>();
	HashSet<ImmutablePair<String, String>> rightTests = new HashSet<ImmutablePair<String, String>>();


	public TestCompare(GroupTreeNode gtn) {
		super(gtn);

		// collect test cases for the pair of clones
		CloneTreeNode ctn1 = (CloneTreeNode) gtn.getChildAt(0);
		CloneTreeNode ctn2 = (CloneTreeNode) gtn.getChildAt(1);

		Enumeration enumerator = ctn1.children();
		while (enumerator.hasMoreElements()) {
			TestTreeNode ttn = (TestTreeNode) enumerator.nextElement();
			String testPath = ttn.getTestPath();
			String testClass = testPath.substring(
					GrafterConfig.test_dir.length(), testPath.length() - 5)
					.replace(File.separatorChar, '.');
			leftTests.add(new ImmutablePair<String, String>(testClass, ttn
					.getTestName()));
		}
		enumerator = ctn2.children();
		while (enumerator.hasMoreElements()) {
			TestTreeNode ttn = (TestTreeNode) enumerator.nextElement();
			String testPath = ttn.getTestPath();
			String testClass = testPath.substring(
					GrafterConfig.test_dir.length(), testPath.length() - 5)
					.replace(File.separatorChar, '.');
			rightTests.add(new ImmutablePair<String, String>(testClass, ttn
					.getTestName()));
		}
	}

	public boolean compare() {
		// test the left clone with its own tests
		HashMap<String, Boolean> result_left_left = test(true);
		if(result_left_left == null) return false;
		
		// graft the right clone to the left
		graftRight();

		// compile before run the tests
		compile();

		// test the right clone with the tests of the left clone 
		HashMap<String, Boolean> result_right_left = test(true);
		
		// restore the modified left file
		restore(file1);
		if(result_right_left == null) return false;
		
		// test the right clone with its own tests
		HashMap<String, Boolean> result_right_right = test(false);
		if(result_right_right == null) return false;
		
		// graft the left clone to the right
		graftLeft();

		// compile before run the tests
		compile();
		
		// test the left clone with the tests of the right clone
		HashMap<String, Boolean> result_left_right = test(false);

		// restore the modified right file
		restore(file2);
		if(result_left_right == null) return false;
		
		// check for the rest of bak files
		HashSet<File> bakFiles = FileUtils.findFilesWithExtension(".bak", new File(GrafterConfig.src_dir));
		for(File file : bakFiles){
			String pathToModifiedFile = file.getAbsolutePath().replace(".bak", ""); 
			new File(pathToModifiedFile).delete();
			file.renameTo(new File(pathToModifiedFile));
		}
		
		// combine the comparison results of the left tests and right tests
		HashMap<String, Boolean> result_left = new HashMap<String, Boolean>();
		for(String test : result_left_left.keySet()){
			result_left.put(test, result_left_left.get(test));
		}
		
		for(String test : result_left_right.keySet()){
			if(result_left_left.containsKey(test)){
				// both clones are exercised by the same test, keep the test result that leads to behavioral divergence
				if(!result_right_right.get(test).equals(result_left_right.get(test))){
					result_left.put(test, result_left_right.get(test));
				}else{
					continue;
				}
			}else{
				result_left.put(test, result_left_right.get(test));
			}
		}
		
		HashMap<String, Boolean> result_right = new HashMap<String, Boolean>();
		for(String test : result_right_left.keySet()){
			result_right.put(test, result_right_left.get(test));
		}
		
		for(String test : result_right_right.keySet()){
			if(result_right_left.containsKey(test)){
				// both clones are exercised by the same test, keep the test result that leads to behavioral divergence
				if(!result_right_right.get(test).equals(result_left_right.get(test))){
					result_right.put(test, result_right_right.get(test));
				}else{
					continue;
				}
			}else{
				result_right.put(test, result_right_right.get(test));
			}
		}
		
		final HashMap<String, Boolean> result1 = result_left;
		final HashMap<String, Boolean> result2 = result_right;
		if(GrafterConfig.batch){
			for(String test : result1.keySet()){
				Boolean r1 = result1.get(test);
				Boolean r2 = result2.get(test);
				if(r1 != r2){
					System.out.println(test + " kills the mutant");
				}
			}
		}else{
			javax.swing.SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					TestCompareView tcv = new TestCompareView(className1 + "("
							+ startLine1 + "，" + endLine1 + ")", className2 + "("
							+ startLine2 + "，" + endLine2 + ")", result1, result2);
					tcv.createAndShowGUI();
				}
			});
		}

		return result1.equals(result2);
	}

	public HashMap<String, Boolean> test(boolean isLeft) {
		HashMap<String, Boolean> results;

		if (GrafterConfig.build == BuildTool.Ant) {
			results = antTest(isLeft);
		} else {
			results = mvnTest(isLeft);
		}

		return results;
	}

	public HashMap<String, Boolean> antTest(boolean isLeft) {
		HashMap<String, Boolean> results = new HashMap<String, Boolean>();
		HashSet<ImmutablePair<String, String>> tests = isLeft ? leftTests : rightTests;
		
		// group the test cases based on the test classes they belong to
		HashMap<String, HashSet<String>> tmap = new HashMap<String, HashSet<String>>();
		for (ImmutablePair<String, String> test : tests) {
			String tClass = test.getLeft();
			String tMethod = test.getRight();
			HashSet<String> tset;
			if (tmap.containsKey(tClass)) {
				tset = tmap.get(tClass);
			} else {
				tset = new HashSet<String>();
			}

			tset.add(tMethod);
			tmap.put(tClass, tset);
		}

		for (String tClass : tmap.keySet()) {
			HashSet<String> tset = tmap.get(tClass);
			HashMap<String, Boolean> trs = AntTestRunner.runAntTestClass(tClass);
			for(String m : trs.keySet()){
				if(tset.contains(m) && trs.containsKey(m)){
					results.put(tClass + "." + m, trs.get(m));
					if(GrafterConfig.verbose){
						if (trs.get(m)) {
							System.out.println(m + "：" + tClass
									+ " succeeds.");
						} else {
							System.out
								.println(m + "：" + tClass + " fails.");
						}
					}
				}
			}
		}
		
		return results;
	}
	
	public HashMap<String, Boolean> mvnTest(boolean isLeft) {
		HashMap<String, Boolean> results = new HashMap<String, Boolean>();
		HashSet<ImmutablePair<String, String>> tests = isLeft ? leftTests : rightTests;

		for (ImmutablePair<String, String> test : tests) {
			String testClass = test.getLeft();
			String testMethod = test.getRight();
			try {
				boolean result = TestRunner.runTestUsingMVNSurefire(testClass, testMethod);

				results.put(testClass + "." + testMethod, result);
				if(GrafterConfig.verbose){
					if (result) {
						System.out.println(testMethod + "：" + testClass
								+ " succeeds.");
					} else {
						System.out
								.println(testMethod + "：" + testClass + " fails.");
					}
				}
			} catch (IOException e1) {
				e1.printStackTrace();
				if(GrafterConfig.batch){
					System.out.println("Grafter fails when running tests on " + className1);
				}else{
					JOptionPane.showMessageDialog(null,
							"Grafter fails when running tests on " + className1);
				}
				
				return null;
			}
		}

		return results;
	}
}

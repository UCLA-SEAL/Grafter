package edu.ucla.cs.grafter.process;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import edu.ucla.cs.grafter.config.GrafterConfig;
import edu.ucla.cs.grafter.file.FileUtils;

/**
 * This test runner runs a single test in a project built by ant. It is designed
 * as a replacement for the general, stand-alone test runner. Because I observed
 * that some tests in the Apache-Ant project requires to set up the testing
 * environment, like creating some dummy files etc etc etc, which is specified
 * in the build file. Using the stand-alone test runner will ignore the set-up
 * process and cause test failure. To use this runner, you have to manually
 * modify the build file and add an ant target to run a single test using
 * following template
 * 
 * <target name="grafter-run-single-test" depends="XXXX"> <test-junit>
 * <formatter type="plain" usefile="false"/> <test name="XXX"/> </test-junit>
 * </target>
 * 
 * Please do not change the target name. Grafter will automatically replace the
 * test name at run time so no worries about that.
 * 
 * @author troy
 *
 */
public class AntTestRunner {

	/**
	 * Run the single test by executing 'ant grafter-run-single-test' It does
	 * not directly retrieve the correctness of the specific test but use the
	 * number of test failures/errors as an agent measurement.
	 * 
	 * @param testClass
	 * @param testMethod
	 * @return
	 */
	public static boolean runAntTestMethod(String testClass, String testMethod) {
		try {
			rewriteBuildXML(testClass);
		} catch (IOException e) {
			e.printStackTrace();
			if(GrafterConfig.batch){
				System.out.println("Rewrite Build XML failure.");
			}else{
				JOptionPane.showMessageDialog(null, "Rewrite Build XML failure.");
			}
			return false;
		}

		boolean result = false;
		try {
			File workingDir = new File(GrafterConfig.root_dir);
			Process test = Runtime.getRuntime().exec(
					"ant grafter-run-single-test", null, workingDir);
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(
					test.getInputStream()));
			String s = null;
			boolean trigger = false;
			while ((s = stdInput.readLine()) != null) {
				if(trigger){
					if(s.contains("ERROR") || s.contains("FAILED")){
						result = false;
					}
					else{
						result = true;
					}

					break;
				}
				if (s.trim()
						.matches(
								"^\\[junit\\] Testcase: " + testMethod + " took .*$")) {
					trigger = true;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			if(GrafterConfig.batch){
				System.out.println("Fails to run the ant test.");
			}else{
				JOptionPane.showMessageDialog(null, "Fails to run the ant test.");
			}
			
			result = false;
		} finally{
			// clean the test report files in the root folder
			File root = new File(GrafterConfig.root_dir);
			File[] files = root.listFiles();
			for(File f : files){
				String fName = f.getName();
				if(fName.startsWith("TEST-")){
					f.delete();
				}
			}
		}
		
		return result;
	}
	
	public static HashMap<String, Boolean> runAntTestClass(String tClass) {
		try {
			rewriteBuildXML(tClass);
		} catch (IOException e) {
			e.printStackTrace();
			if(GrafterConfig.batch){
				System.out.println("Rewrite Build XML failure.");
			}else{
				JOptionPane.showMessageDialog(null, "Rewrite Build XML failure.");
			}
			
			return new HashMap<String, Boolean>();
		}
		
		HashMap<String, Boolean> results = new HashMap<String, Boolean>();
		ArrayList<String> output = new ArrayList<String>();
		try {
			File workingDir = new File(GrafterConfig.root_dir);
			Process test = Runtime.getRuntime().exec(
					"ant grafter-run-single-test", null, workingDir);
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(
					test.getInputStream()));
			String s = null;
			while ((s = stdInput.readLine()) != null) {
				output.add(s);
			}
		} catch (IOException e) {
			e.printStackTrace();
			if(GrafterConfig.batch){
				System.out.println("Fails to run the ant test");
			}else{
				JOptionPane.showMessageDialog(null, "Fails to run the ant test.");
			}
		} finally{
			// clean the test report files in the root folder
			File root = new File(GrafterConfig.root_dir);
			File[] files = root.listFiles();
			for(File f : files){
				String fName = f.getName();
				if(fName.startsWith("TEST-")){
					f.delete();
				}
			}
		}
		
		// analyze the test output
		for(int i = 0; i < output.size(); i++){
			String s = output.get(i);
			if(s.trim()
						.matches(
								"^\\[junit\\] Testcase: \\w+ took .*$")){
				int start = s.indexOf("[junit] Testcase: ") + 18;
				int end = s.indexOf(" took ");
				String m = s.substring(start, end);
				String s2 = output.get(i+1);
				if(s2.contains("ERROR") || s2.contains("FAILED")){
					results.put(m, false);
				}
				else{
					results.put(m, true);
				}
			}
		}
		
		return results;
	}

	
	/**
	 * Copied from LogParser
	 * 
	 * @param pat
	 * @param s
	 * @return
	 */
	static String match(String pat, String s){
		Pattern p = Pattern.compile(pat);
		Matcher m = p.matcher(s);
		if(m.find()){
			return m.group();
		}else{
			return "";
		}
	}
	
	static void rewriteBuildXML(String test) throws IOException {
		String bXML = GrafterConfig.root_dir + "build.xml";
		String[] ss = FileUtils.readFileToArray(bXML);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < ss.length; i++) {
			String s = ss[i];
			if (s.contains("target name=\"grafter-run-single-test\"")) {
				ss[i + 3] = "<test name=\"" + test + "\" />";
			}
			sb.append(s + System.lineSeparator());
		}
		FileUtils.writeStringtoFile(sb.toString(), bXML);
	}
}

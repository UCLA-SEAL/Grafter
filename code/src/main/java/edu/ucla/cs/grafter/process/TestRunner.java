package edu.ucla.cs.grafter.process;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.swing.JOptionPane;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.sonatype.aether.resolution.DependencyResolutionException;

import edu.ucla.cs.grafter.config.GrafterConfig;

public class TestRunner {

	/**
	 * run the entire test suite
	 * 
	 * @return
	 * @throws IOException
	 */
	public static boolean run() throws IOException {
		File workingDir = new File(GrafterConfig.root_dir);
		Process test = Runtime.getRuntime().exec(GrafterConfig.test_cmd, null,
				workingDir);
//		BufferedReader stdInput = new BufferedReader(new InputStreamReader(
//				test.getInputStream()));

		try {
			// wait till the process terminates
			test.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		boolean result = true;
		String s = null;
		String log_file = GrafterConfig.root_dir + "test_log.txt";
//		PrintWriter writer = new PrintWriter(log_file, "UTF-8");
//		while ((s = stdInput.readLine()) != null) {
//			if (StringUtils.containsIgnoreCase(s, "build failure")) {
//				result = false;
//			}
			// log test output to test_log.txt
//			writer.println(s);
//		}
//		writer.close();
		return result;
	}

	/**
	 * 
	 * Run a test method (i.e., a test case) in a new JVM using the stand-alone
	 * test runner
	 * 
	 * @param testClass
	 * @param testMethod
	 * @return
	 * @throws IOException
	 * @throws DependencyResolutionException
	 * @throws XmlPullParserException
	 */
	public static boolean runTestUsingStandAloneTestRunner(String testClass,
			String testMethod) throws IOException {
		Process test = Runtime
				.getRuntime()
				.exec("java -jar ./lib/testrunner/testrunner-standalone-jar-with-dependencies.jar ./Grafter.config "
						+ testClass + " " + testMethod);
		try {
			// wait till the process terminates
			test.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		BufferedReader stdInput = new BufferedReader(new InputStreamReader(
				test.getInputStream()));
		BufferedReader stdError = new BufferedReader(new InputStreamReader(
				test.getErrorStream()));

		String s = null;
		while ((s = stdInput.readLine()) != null) {
			if (s.trim().equalsIgnoreCase("true")) {
				return true;
			} else if (s.trim().equalsIgnoreCase("false")) {
				return false;
			} else {
				System.out.println(s);
			}
		}

		if ((s = stdError.readLine()) != null) {
			System.err.println("[Grafter]The test " + testClass + "."
					+ testMethod + "throws exception.");
			if (GrafterConfig.batch) {
				System.out.println("[Grafter]The test " + testClass + "."
						+ testMethod + "throws exception.");
			} else {
				JOptionPane.showMessageDialog(null, "[Grafter]The test "
						+ testClass + "." + testMethod + "throws exception.");
			}

			return false;
		}

		return false;
	}

	public static boolean runTestUsingMVNSurefire(String testClass,
			String testMethod) throws IOException {
		Process test = Runtime.getRuntime().exec(
				"mvn -Dtest=" + testClass + "#" + testMethod + " test", null,
				new File(GrafterConfig.root_dir));
		try {
			int exitValue = test.waitFor();
			if(!GrafterConfig.batch){
				String exitResult = exitValue == 0 ? "normally" : "abnormally";
				System.out.println(testClass + "." + testMethod + " exits "
						+ exitResult);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		BufferedReader stdInput = new BufferedReader(new InputStreamReader(
				test.getInputStream()));
		BufferedReader stdError = new BufferedReader(new InputStreamReader(
				test.getErrorStream()));

		String s = null;
		while ((s = stdInput.readLine()) != null) {
			if (s.contains("BUILD SUCCESS")) {
				return true;
			} else if (s.contains("BUILD FAILURE")) {
				return false;
			} 
//			else {
//				System.out.println(s);
//			}
		}

		if ((s = stdError.readLine()) != null) {
			System.err.println("[Grafter]The test " + testClass + "."
					+ testMethod + "throws exception.");
			if (GrafterConfig.batch) {
				System.out.println("[Grafter]The test " + testClass + "."
						+ testMethod + "throws exception.");
			} else {
				JOptionPane.showMessageDialog(null, "[Grafter]The test "
						+ testClass + "." + testMethod + "throws exception.");
			}

			return false;
		}

		return false;
	}

	public static void main(String[] args) throws Exception {
		GrafterConfig.config();
		System.out.println(TestRunner.runTestUsingStandAloneTestRunner(
				"com.notnoop.apns.PayloadBuilderTest",
				"abitComplicatedEnglishLength"));
	}
}

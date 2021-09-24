package edu.ucla.cs.grafter.coverage;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.HashSet;

import org.junit.Ignore;
import org.junit.Test;

public class LogParserTest {
	final static String log = "/home/troy/SysAssure/dataset/java-apns-1.0.0.Beta3/test_log.txt";
	
	@Test
	@Ignore
	public void testFindCloneWithTests(){
		HashSet<String> expects = new HashSet<String>();
		expects.add("com.notnoop.apns.integration.ApnsConnectionTest.sendOneQueued");
		expects.add("com.notnoop.apns.integration.ApnsConnectionTest.sendOneSimpleWithoutTimeout");
		expects.add("com.notnoop.apns.integration.ApnsConnectionTest.sendOneSimpleWithTimeout");
		expects.add("com.notnoop.apns.integration.ApnsConnectionTest.sendOneSimple");
		expects.add("com.notnoop.apns.integration.ApnsConnectionCacheTest.handleReTransmissionError5Good1Bad7Good");
		expects.add("com.notnoop.apns.integration.ApnsConnectionCacheTest.handleReTransmissionError1Good1Bad2Good");
		expects.add("com.notnoop.apns.integration.ApnsConnectionCacheTest.handleReTransmissionError1Bad");
		expects.add("com.notnoop.apns.integration.ApnsConnectionCacheTest.cacheLengthNotification");
		expects.add("com.notnoop.apns.integration.FeedbackTest.simpleQueuedFeedback");
		expects.add("com.notnoop.apns.integration.FeedbackTest.simpleFeedback");
		expects.add("com.notnoop.apns.integration.FeedbackTest.threeQueuedFeedback");
		expects.add("com.notnoop.apns.integration.FeedbackTest.simpleFeedbackWithTimeout");
		expects.add("com.notnoop.apns.integration.FeedbackTest.threeFeedback");
		expects.add("com.notnoop.apns.integration.FeedbackTest.simpleFeedbackWithoutTimeout");
		LogParser lp = new LogParser();
		try {
			lp.parse(log);
			HashSet<String> tests = lp.findTests(22, "ApnsServiceBuilder", 227, 230);
			assertEquals(expects, tests);
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	@Ignore
	public void testFindCloneWithOutTests(){
		HashSet<String> expects = new HashSet<String>();
		LogParser lp = new LogParser();
		try {
			lp.parse(log);
			HashSet<String> tests = lp.findTests(4, "ApnsServiceBuilder", 179, 202);
			assertEquals(expects, tests);
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
	}
}

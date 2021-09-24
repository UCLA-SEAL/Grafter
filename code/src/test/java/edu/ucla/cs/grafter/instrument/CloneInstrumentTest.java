package edu.ucla.cs.grafter.instrument;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;

import edu.ucla.cs.grafter.file.FileUtils;

public class CloneInstrumentTest {

	@Test
	@Ignore
	public void testGetPackageName(){
		String path = "/home/troy/SysAssure/dataset/java-apns-1.0.0.Beta3/src/main/java/com/notnoop/apns/SimpleApnsNotification.java";
		CloneInstrument ci = new CloneInstrument(0, path, 108, 112);
		
		assertEquals(ci.getPackageName(), "com.notnoop.apns");
	}
	
	@Test
	@Ignore
	public void testTrackerExists(){
		String path = "/home/troy/SysAssure/code/Grafter/Grafter/src/main/java/edu/ucla/cs/grafter/instrument/JavaParser.java";
		CloneInstrument ci = new CloneInstrument(0, path, 136, 148);
		try{
			ci.addTestTracker();
		}catch(Exception ex){
			ex.printStackTrace();
			fail();
		}
	}
	
	@Test
	@Ignore
	public void testTrackerNotExists(){
		String path = "/home/troy/SysAssure/dataset/java-apns-1.0.0.Beta3/src/main/java/com/notnoop/apns/SimpleApnsNotification.java";
		CloneInstrument ci = new CloneInstrument(0, path, 108, 112);
		try {
			ci.addTestTracker();
			// check if new TestTracker.java exists
			File file = new File("/home/troy/SysAssure/dataset/java-apns-1.0.0.Beta3/src/main/java/com/notnoop/apns/TestTracker.java");
			assertTrue(file.exists());
			file.delete();
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	@Ignore
	public void testInsertPrintStatement(){
		String path = "/home/troy/SysAssure/dataset/java-apns-1.0.0.Beta3/src/main/java/com/notnoop/apns/SimpleApnsNotification.java";
		CloneInstrument ci = new CloneInstrument(0, path, 108, 112);
		try {
			ci.insertPrintStatement();
			// check if the print statement is inserted in the right position
			String code = FileUtils.readFileToString(path);
		    String lineSeparator = System.getProperty("line.separator");
			String[] ss = code.split(lineSeparator);
			assertEquals(ss[108], "System.out.println(\"[Grafter][Clone Group 0][Class SimpleApnsNotification][Range(108,112)]\"+ TestTracker.getTestName());");
			restore(path);
		}catch(IOException e){
			e.printStackTrace();
			fail();
		} catch (InstrumentException e) {
			fail();
		}
	}
	
	private void restore(String file){
		File new_file = new File(file);
		File old_file = new File(file + ".bak");
		new_file.delete();
		old_file.renameTo(new File(file));
	}
}

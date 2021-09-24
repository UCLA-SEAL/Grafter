package edu.ucla.cs.grafter.data;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.xml.sax.SAXException;

public class XMLSerializerTest {
	
	@Test
	@Ignore
	public void testXMLSeralization(){
		XMLReader xr = new XMLReader("/home/troy/SysAssure/code/Grafter/Grafter/src/test/resources/data.xml");
		try {
			DefaultMutableTreeNode root = xr.loadData();
			XMLSerializer.serialize(root, "/home/troy/SysAssure/code/Grafter/Grafter/src/test/resources/serialization.xml");
			File file1 = new File("/home/troy/SysAssure/code/Grafter/Grafter/src/test/resources/serialization.xml");
			File file2 = new File("/home/troy/SysAssure/code/Grafter/Grafter/src/test/resources/data.xml");
			assertTrue(FileUtils.contentEquals(file1, file2));
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	@Ignore
	public void checkSerializationValidty(){
		XMLReader xr = new XMLReader("/home/troy/SysAssure/code/Grafter/Grafter/src/test/resources/serialization.xml");
		try{
			DefaultMutableTreeNode root = xr.loadData();
		} catch(Exception ex){
			ex.printStackTrace();
			fail();
		}
	}
}

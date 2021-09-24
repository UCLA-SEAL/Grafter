package edu.ucla.cs.grafter.data;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import edu.ucla.cs.grafter.config.GrafterConfig;
import edu.ucla.cs.grafter.ui.CloneTreeNode;
import edu.ucla.cs.grafter.ui.GroupTreeNode;
import edu.ucla.cs.grafter.ui.TestTreeNode;

public class XMLReader {
	private String path;
	
	public XMLReader(String s){
		this.path = s;
	}
	
	public DefaultMutableTreeNode loadData() throws ParserConfigurationException, SAXException, IOException{
		File file = new File(path);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(file);
		
		NodeList groups = doc.getElementsByTagName("group");
		
		// Create the root node
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
        
        for(int i = 0; i < groups.getLength(); i++){
        	// Add each clone group
        	Node group = groups.item(i);
        	int id = Integer.parseInt(((Element)group).getAttribute("id"));
        	boolean isExcluded = Boolean.parseBoolean(((Element)group).getAttribute("excluded"));
        	GroupTreeNode tGroupNode = new GroupTreeNode(id, isExcluded);
        	NodeList clones = ((Element)group).getElementsByTagName("clone");
        	for(int j = 0; j < clones.getLength(); j++){
        		// Add each clone
        		Node clone = clones.item(j);
        		String method = ((Element)clone).getAttribute("method");
        		String filePath = GrafterConfig.root_dir + ((Element)clone).getAttribute("file");
        		int start = Integer.parseInt(((Element)clone).getAttribute("start"));
        		int end = Integer.parseInt(((Element)clone).getAttribute("end"));
        		boolean isTest = Boolean.parseBoolean(((Element)clone).getAttribute("isTest"));
        		CloneTreeNode tCloneNode = new CloneTreeNode(method, filePath, start, end, isTest);
        		
        		NodeList tests = ((Element)clone).getElementsByTagName("test");
        		for(int k = 0; k < tests.getLength(); k++){
        			Node test = tests.item(k);
        			String testName = test.getTextContent();
        			String testPath = GrafterConfig.test_dir + ((Element)test).getAttribute("path");
        			TestTreeNode tTestNode = new TestTreeNode(testPath, testName);
        			tCloneNode.add(tTestNode);
        		}
        		
        		tGroupNode.add(tCloneNode);
        	}
        	root.add(tGroupNode);
        }
        
        return root;
	}
	
	public static void main(String[] args){
		XMLReader reader = new XMLReader("/home/troy/SysAssure/code/Grafter/Grafter/src/test/resources/data.xml");
		try {
			DefaultMutableTreeNode root = reader.loadData();
			Enumeration enumerator = root.children();
				
			while(enumerator.hasMoreElements()){
				DefaultMutableTreeNode child = (DefaultMutableTreeNode) enumerator.nextElement();
				System.out.println(child.getUserObject());
			}
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
	}
}

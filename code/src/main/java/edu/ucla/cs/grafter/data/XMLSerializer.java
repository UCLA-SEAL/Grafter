package edu.ucla.cs.grafter.data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;

import javax.swing.tree.DefaultMutableTreeNode;

import edu.ucla.cs.grafter.config.GrafterConfig;
import edu.ucla.cs.grafter.ui.CloneTreeNode;
import edu.ucla.cs.grafter.ui.GroupTreeNode;
import edu.ucla.cs.grafter.ui.TestTreeNode;

public class XMLSerializer {
	static final String lineSeparator = System.lineSeparator();
	
	public static void serialize(DefaultMutableTreeNode root, String file){
		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + lineSeparator);
		sb.append("<data>" + lineSeparator);
		Enumeration groups = root.children();
		while(groups.hasMoreElements()){
			GroupTreeNode group = (GroupTreeNode)groups.nextElement();
			int id = group.getId();
			boolean isExcluded = group.isExcluded();
			sb.append("\t<group id=\""+ id + "\" excluded=\"" + isExcluded + "\">" + lineSeparator);
			Enumeration clones = group.children();
			while(clones.hasMoreElements()){
				CloneTreeNode clone = (CloneTreeNode)clones.nextElement();
				String method = clone.getMethod();
				// encode clone path relatively to the root folder
				String path = clone.getFile().replace(GrafterConfig.root_dir, "");
				int start = clone.start();
				int end = clone.end();
				boolean isTest = clone.isTrivial();
				
				sb.append("\t\t<clone method=\"" + method + "\" file=\"" + path + "\" start=\"" + start + "\" end=\"" + end 
						+ "\" isTest=\"" + isTest + "\">" + lineSeparator);
				Enumeration tests = clone.children();
				while(tests.hasMoreElements()){
					TestTreeNode test = (TestTreeNode)tests.nextElement();
					// encode test path relatively to the test folder 
					String testPath = test.getTestPath().replace(GrafterConfig.test_dir, "");
					String testName = test.getTestName();
					
					sb.append("\t\t\t<test path=\"" + testPath + "\">" + testName + "</test>" + lineSeparator);
				}
				sb.append("\t\t</clone>" + lineSeparator);
			}
			sb.append("\t</group>" + lineSeparator);
		}
		sb.append("</data>" + lineSeparator);
		
		String content = sb.toString();
		File dataFile = new File(file);
		
		if(!dataFile.exists()){
			try {
				dataFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		BufferedWriter bw = null;
		try{
			bw = new BufferedWriter(new FileWriter(dataFile));
			bw.write(content);
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			if(bw != null){
				try {
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}

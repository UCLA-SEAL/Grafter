package edu.ucla.cs.grafter.batch;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import edu.ucla.cs.grafter.compare.StateCompare;
import edu.ucla.cs.grafter.compare.TestCompare;
import edu.ucla.cs.grafter.config.GrafterConfig;
import edu.ucla.cs.grafter.data.XMLReader;
import edu.ucla.cs.grafter.file.FileUtils;
import edu.ucla.cs.grafter.ui.CloneTreeNode;
import edu.ucla.cs.grafter.ui.GroupTreeNode;

public class BatchRunner {
	
	public void run(){
		XMLReader reader = new XMLReader("/home/troy/SysAssure/dataset/xmlsec-2.0.5/clone-report/clones_with_selected_tests.xml");
		try {
			DefaultMutableTreeNode root = reader.loadData();
			Enumeration pairs = root.children();
				
			while(pairs.hasMoreElements()){
				GroupTreeNode pair = (GroupTreeNode) pairs.nextElement();
				System.out.println(pair.getUserObject());
				if(pair.getUserObject().equals("Clone Group 11")){
					mutate(pair);
				}
			}
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
	}
	
	public void mutate(GroupTreeNode gtn){
		int id = gtn.getId();
		File dir = new File(GrafterConfig.root_dir + "mutants/Clone" + id);
		
		Enumeration pair = gtn.children();
		CloneTreeNode left = (CloneTreeNode)pair.nextElement();
		File f1 = new File(dir.getAbsolutePath() + File.separator + "left");
		if(f1.exists()){
			mutate(f1, left, gtn);
		}
		
		CloneTreeNode right = (CloneTreeNode)pair.nextElement();
		File f2 = new File(dir.getAbsolutePath() + File.separator + "right");
		if(f2.exists()){
			mutate(f2, right, gtn);
		}
	}
	
	private void mutate(File dir, CloneTreeNode ctn, GroupTreeNode gtn){
		String file = ctn.getFile();
		File orgn = new File(file);
		
		// preserve the original file that contains the clone
		File bak = new File(file + ".unmutated");
		orgn.renameTo(bak);
		
		File[] mutants = dir.listFiles();
		Arrays.sort(mutants);
		for(File mutant : dir.listFiles()){
			String[] ss = mutant.getAbsolutePath().split(File.separator);
			if(ss[ss.length - 1].contains("mutant") || ss[ss.length-1].contains("inf")){
				continue;
			}
			int id = Integer.parseInt(ss[ss.length -1]);
			System.out.println("Testing mutant " + id + " in " + ctn.getClassName() + "." + ctn.getMethod());
			// copy the mutant to replace the original one
			File f = FileUtils.findFileHelper(orgn.getName(), mutant);
			try {
				String content = FileUtils.readFileToString(f.getAbsolutePath());
				orgn.createNewFile();
				FileUtils.writeStringtoFile(content, orgn.getAbsolutePath());
				
				// Test comparison
				TestCompare testCompare = new TestCompare(gtn);
				testCompare.compare();
				
				// State comparison
				StateCompare stateCompare = new StateCompare(gtn);
				stateCompare.compare();
				
				orgn.delete();
			} catch (IOException e) {
				e.printStackTrace();
			}			
		}
		
		// restore the mutated file
		bak.renameTo(orgn);
	}
	
	public static void main(String[] args){
		GrafterConfig.config();
		GrafterConfig.batch = true;
		GrafterConfig.verbose = false;
		
		BatchRunner br = new BatchRunner();
		br.run();
	}
}

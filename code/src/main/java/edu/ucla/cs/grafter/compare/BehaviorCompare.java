package edu.ucla.cs.grafter.compare;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashSet;

import javax.swing.JOptionPane;

import org.apache.commons.lang3.tuple.ImmutablePair;

import edu.ucla.cs.grafter.config.GrafterConfig;
import edu.ucla.cs.grafter.file.FileUtils;
import edu.ucla.cs.grafter.port.Grafter;
import edu.ucla.cs.grafter.process.CompileRunner;
import edu.ucla.cs.grafter.ui.CloneTreeNode;
import edu.ucla.cs.grafter.ui.GroupTreeNode;
import edu.ucla.cs.grafter.ui.TestTreeNode;

public abstract class BehaviorCompare {
	// clone1 (left clone) properties
	String file1;
	int startLine1;
	int endLine1;
	String className1;
	
	// clone2 (right clone) properties
	String file2;
	int startLine2;
	int endLine2;
	String className2;
	
	
	/**
	 * For the test purpose, do not call.
	 */
	public BehaviorCompare() {}
	
	public BehaviorCompare(GroupTreeNode gtn){
		CloneTreeNode ctn1 = (CloneTreeNode)gtn.getChildAt(0);
		CloneTreeNode ctn2 = (CloneTreeNode)gtn.getChildAt(1);
		file1 = ctn1.getFile();
		file2 = ctn2.getFile();
		startLine1 = ctn1.start();
		startLine2 = ctn2.start();
		endLine1 = ctn1.end();
		endLine2 = ctn2.end();
		className1 = ctn1.getClassName();
		className2 = ctn2.getClassName();
	}
	
	public void graftLeft(){
		Grafter graft;
		try {
			graft = new Grafter(file2, startLine2, endLine2, file1, startLine1, endLine1);
			graft.graft(true);
		} catch (IOException e1) {
			e1.printStackTrace();
			if(GrafterConfig.batch){
				System.out.println("Grafter fails when grafting " + className1 + " to " + className2);
			}else{
				JOptionPane.showMessageDialog(null, "Grafter fails when grafting " + className1 + " to " + className2);
			}
			
			return;
		}
	}
	
	public void graftRight(){
		Grafter graft;
		try {
			graft = new Grafter(file1, startLine1, endLine1, file2, startLine2, endLine2);
			graft.graft(true);
		} catch (IOException e1) {
			e1.printStackTrace();
			if(GrafterConfig.batch){
				System.out.println("Grafter fails when grafting " + className2 + " to " + className1);
			}else{
				JOptionPane.showMessageDialog(null, "Grafter fails when grafting " + className2 + " to " + className1);
			}
			
			return;
		}
	}
	
	public void compile(){
		try {
			boolean result = CompileRunner.run();
			if(!result){
				// compilation error
				if(GrafterConfig.batch){
					System.err.println("[Grafter]Fails to build the grafted project.");
				}else{
					JOptionPane.showMessageDialog(null, "[Grafter]Fails to build the grafted project.");
				}
				return;
			}
		} catch (IOException excp) {
			excp.printStackTrace();
			return;
		}
	}
	
	public void restore(String path){
		File bakFile = new File(path + ".bak");
		if(bakFile.exists()){
			File file = new File(path);
			file.delete();
			bakFile.renameTo(new File(path));
		}					
	}
	
	public abstract boolean compare();
	
}

package edu.ucla.cs.grafter.ui;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.plaf.metal.MetalIconFactory;
import javax.swing.tree.DefaultMutableTreeNode;

public class TestTreeNode extends DefaultMutableTreeNode{
	private static final long serialVersionUID = 1L;
	static Icon icon = new ImageIcon("src/main/resources/images/testicon.jpg");
	String path;
	String name;
	TestResult result;
	
	public TestTreeNode(String path, String name){
		super(name);
		this.path = path;
		this.name = name;
		this.result = TestResult.NONE;
	}
	
	public Icon getIcon(){
		return icon;
	}
	
	public String getTestPath(){
		return path;
	}
	
	public String getTestName(){
		return name;
	}
	
	public TestResult getTestResult(){
		return result;
	}
}

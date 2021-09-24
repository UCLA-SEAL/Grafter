package edu.ucla.cs.grafter.ui;

import java.io.File;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.tree.DefaultMutableTreeNode;

public class CloneTreeNode extends DefaultMutableTreeNode{
	private static final long serialVersionUID = 1L;
	static Icon icon = new ImageIcon("src/main/resources/images/cloneicon.jpg");
	String method;
	String file;
	private String clazz; // no need to serialize
	int start;
	int end;
	boolean isTrvial;
	
	public CloneTreeNode(String method, String path, int start, int end, boolean isTrivial){
		super(method + "(" + start + "," + end + ")");
		this.method = method;
		this.file = path;
		this.clazz = path.substring(path.lastIndexOf(File.separator) + 1, path.lastIndexOf('.'));
		this.start = start;
		this.end = end;
		this.isTrvial = isTrivial;
	}
	
	public Icon getIcon(){
		return icon;
	}
	
	public String getMethod(){
		return method;
	}
	
	public String getFile(){
		return file;
	}
	
	public int start(){
		return start;
	}
	
	public int end(){
		return end;
	}
	
	public void setMethod(String method){
		this.method = method;
	}
	
	public void setStart(int start){
		this.start = start;
	}
	
	public void setEnd(int end){
		this.end = end;
	}
	
	public boolean isTrivial(){
		return this.isTrvial;
	}
	
	public void markAsTest(){
		this.isTrvial = true;
	}

	public String getClassName() {
		return this.clazz;
	}
}

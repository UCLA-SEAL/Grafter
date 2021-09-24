package edu.ucla.cs.grafter.ui;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.tree.DefaultMutableTreeNode;

public class GroupTreeNode extends DefaultMutableTreeNode{
	private static final long serialVersionUID = 1L;
	static Icon icon = new ImageIcon("src/main/resources/images/groupicon.jpg");
	int id;
	boolean excluded;
	
	public GroupTreeNode(int id, boolean isExcluded) {
		super("Clone Group " + id);
		this.id = id;
		this.excluded = isExcluded;
	}

	public Icon getIcon(){
		return icon;
	}
	
	public int getId(){
		return id;
	}
	
	public void setExcluded(){
		this.excluded = true;
	}
	
	public void setIncluded(){
		this.excluded = false;
	}
	
	public boolean isExcluded(){
		return this.excluded == true;
	}
}

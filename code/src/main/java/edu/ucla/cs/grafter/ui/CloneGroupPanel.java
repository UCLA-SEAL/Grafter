package edu.ucla.cs.grafter.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.EventObject;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.jmeld.ui.CloneComparison;
import org.jmeld.ui.JMeldPanel;
import org.sonatype.aether.resolution.DependencyResolutionException;

import edu.ucla.cs.grafter.compare.StateCompare;
import edu.ucla.cs.grafter.compare.TestCompare;
import edu.ucla.cs.grafter.config.BuildTool;
import edu.ucla.cs.grafter.config.GrafterConfig;
import edu.ucla.cs.grafter.data.XMLSerializer;
import edu.ucla.cs.grafter.process.AntTestRunner;
import edu.ucla.cs.grafter.process.CompileRunner;
import edu.ucla.cs.grafter.process.TestRunner;

public class CloneGroupPanel extends JPanel{
	private static final long serialVersionUID = 1L;
	
	static JTree tree;
	static JPopupMenu groupMenu;
	static JMenuItem compare;
	static JMenuItem exclude;
	static JMenuItem include;
	static JMenuItem test;
	static JMenuItem testBehavior;
	static JMenuItem stateBehavior;
	static JPopupMenu cloneMenu;
	static JMenuItem rename;
	static DefaultMutableTreeNode currSel = null;
	static JMeldPanel panel;
	static TreeNodeRenderer renderer;
	static TreeNodeEditor editor;
	
	public CloneGroupPanel(final JMeldPanel panel){
		this.panel = panel;
		
		setLayout(new BorderLayout());
		
		// Make a tree list with all the nodes, and make it a JTree
		try {
			DefaultMutableTreeNode root = new DefaultMutableTreeNode();
		    tree = new JTree(root);
		    
		    tree.setEditable(true);
			// Add a selection listener
		    tree.addTreeSelectionListener(new TreeSelectionListener() {
		        public void valueChanged(TreeSelectionEvent e) {
		          DefaultMutableTreeNode node = (DefaultMutableTreeNode) e
		              .getPath().getLastPathComponent();
		          currSel = node;
		        }
		    });
		    
		    // Instantiate the popup menu for each clone group
		    groupMenu = new JPopupMenu();
		    
		    compare = new JMenuItem("Compare");
		    compare.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					if(currSel!= null && currSel instanceof GroupTreeNode){
						// differentiate clones in this group
						GroupTreeNode gtn = (GroupTreeNode)currSel;
//						if(gtn.getChildCount() == 2){
							// TODO: currently we only support two way comparison
							CloneTreeNode ctn1 = (CloneTreeNode)gtn.getChildAt(0);
							CloneTreeNode ctn2 = (CloneTreeNode)gtn.getChildAt(1);
							String file1 = ctn1.getFile();
							String file2 = ctn2.getFile();
							
							CloneComparison comparison = new CloneComparison(panel, new File(file1), new File(file2), ctn1.start, ctn1.end, ctn2.start, ctn2.end);
							comparison.setOpenInBackground(false);
							comparison.execute();
//						}
					}
				}
			});
		    groupMenu.add(compare);
		    
		    exclude = new JMenuItem("Exclude");
		    exclude.addActionListener(new ActionListener(){
		    	@Override
				public void actionPerformed(ActionEvent e) {
		    		if(currSel!=null && currSel instanceof GroupTreeNode){
		    			GroupTreeNode gtn = (GroupTreeNode)currSel;
		    			gtn.setExcluded();
		    			DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
		    			model.nodeChanged(gtn);
		    			tree.collapsePath(new TreePath(gtn.getPath()));
		    			tree.repaint();
		    		}
		    	}
		    });
		    groupMenu.add(exclude);
		    
		    include = new JMenuItem("Include");
		    include.addActionListener(new ActionListener(){
		    	@Override
				public void actionPerformed(ActionEvent e) {
		    		if(currSel!=null && currSel instanceof GroupTreeNode){
		    			GroupTreeNode gtn = (GroupTreeNode)currSel;
		    			gtn.setIncluded();
		    			DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
		    			model.nodeChanged(gtn);
		    			tree.expandPath(new TreePath(gtn.getPath()));
		    			tree.repaint();
		    		}
		    	}
		    });
		    groupMenu.add(include);
		    
		    test = new JMenuItem("Test");
		    test.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if(currSel!=null && currSel instanceof GroupTreeNode){
		    			GroupTreeNode gtn = (GroupTreeNode)currSel;
		    			
		    			if(!gtn.isExcluded()){
			    			// recompile first
			    			try {
								boolean result = CompileRunner.run();
								if(!result){
									// compilation error
									System.err.println("[Grafter]Fails to build the grafted project.");
									JOptionPane.showMessageDialog(panel, "[Grafter]Fails to build the grafted project.");
									return;
								}
							} catch (IOException excp) {
								excp.printStackTrace();
								return;
							}
			    			
			    			// run the test(s)
			    			Enumeration clones = gtn.children();
			    			while(clones.hasMoreElements()){
			    				CloneTreeNode ctn = (CloneTreeNode)clones.nextElement();
			    				Enumeration tests = ctn.children();
			    				while(tests.hasMoreElements()){
			    					TestTreeNode ttn = (TestTreeNode)tests.nextElement();
			    					String testPath = ttn.path;
			    					// convert file path to qualified class name
			    					String testClass = testPath.substring(GrafterConfig.test_dir.length(), testPath.length() - 5).replace(File.separatorChar, '.');
			    					String testMethod = ttn.name;
									try {
										boolean testResult;
										if(GrafterConfig.build == BuildTool.Ant){
											testResult = AntTestRunner.runAntTestMethod(testClass, testMethod);
										}else{
											testResult = TestRunner.runTestUsingMVNSurefire(testClass, testMethod);
										}
				    					if(testResult){
				    						ttn.result = TestResult.SUCCESS;
				    						System.out.println(testMethod + "：" + testClass + " succeeds.");
				    					}else{
				    						ttn.result = TestResult.FAILURE;
				    						System.out.println(testMethod + "：" + testClass + " fails.");
				    					}
				    					
				    					DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
						    			model.nodeChanged(ttn);
						    			tree.repaint();
									} catch (IOException e1) {
										e1.printStackTrace();
										return;
									}
			    				}
			    			}
		    			}
					}
				}
			});
		    groupMenu.add(test);
		    
		    testBehavior = new JMenuItem("Compare Test Behavior");
		    testBehavior.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if(currSel!=null && currSel instanceof GroupTreeNode){
		    			GroupTreeNode gtn = (GroupTreeNode)currSel;
		    			
		    			if(!gtn.isExcluded()){
		    				TestCompare testCompare = new TestCompare(gtn);
		    				testCompare.compare();
		    			}
		    		}
				}
			});
		    groupMenu.add(testBehavior);
		    
		    stateBehavior = new JMenuItem("Compare State Behavior");
		    stateBehavior.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if(currSel!=null && currSel instanceof GroupTreeNode){
		    			GroupTreeNode gtn = (GroupTreeNode)currSel;
		    			
		    			if(!gtn.isExcluded()){
		    				StateCompare stateCompare = new StateCompare(gtn);
		    				stateCompare.compare();
		    			}
		    		}
				}
			});
		    groupMenu.add(stateBehavior);
		    
		    // Add a mouse listener
		    tree.addMouseListener(new MouseAdapter() {
		    	@Override
		    	public void mousePressed(MouseEvent e){
		    		int selRow = tree.getRowForLocation(e.getX(), e.getY());
		            TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
		            if(selRow != -1 && e.getClickCount() == 2) {
		            	doDoubleClick(selRow, selPath);
		            }
		    	}
		    	
		    	public void mouseClicked(MouseEvent e){
		    		if(SwingUtilities.isRightMouseButton(e)){
			    		int selRow = tree.getRowForLocation(e.getX(), e.getY());
			            TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
			            if(selRow != -1){
			            	Object obj = selPath.getLastPathComponent();
			            	currSel = (DefaultMutableTreeNode)obj;
			            	if(obj instanceof GroupTreeNode){	
				            	GroupTreeNode gtn = (GroupTreeNode)obj;
				            	groupMenu.getSubElements();
				            	if(gtn.isExcluded()){
				            		// disable the exclude menu item
				            		exclude.setEnabled(false);
				            		// enable the include menu item
				            		include.setEnabled(true);
				            	}else{
				            		// disable the include menu item 
				            		include.setEnabled(false);
				            		// enable the exclude menu items
				            		exclude.setEnabled(true);
				            	}
				            	
				            	groupMenu.show(e.getComponent(), e.getX(), e.getY());
			            	}else if(obj instanceof CloneTreeNode){
			            		CloneTreeNode ctn = (CloneTreeNode)obj;
			            		// TODO: add the field of isExcluded in CloneTreeNode
			            	}
			            }
		    		}
		    	}
		    	
		    	public void doDoubleClick(int row, TreePath path){
		    		Object obj = path.getLastPathComponent();
		    		if(obj instanceof GroupTreeNode){
		    			// TODO open the comparison view
		    			System.out.println("Open the comparison view.");
		    		}else if(obj instanceof CloneTreeNode){
		    			tree.startEditingAtPath(path);
		    		}
		    	}
			});
		    
		    // Set the root node invisible
		    tree.setRootVisible(false);
		    
		    // Set the customized tree node render for different nodes
		    renderer = new TreeNodeRenderer();
		    tree.setCellRenderer(renderer);
		    
		    // Set the customized tree node editor for different nodes
		    editor = new TreeNodeEditor(tree, renderer);
		    editor.addCellEditorListener(new CellEditorListener() {

	            @Override
	            public void editingStopped(ChangeEvent e) {
	            	String value = editor.getCellEditorValue().toString();
	            	// TODO currently we only allow rename a clone to the format of XXX.YYY(AA, BB)
	            	String pattern = "(\\w+\\.)?\\w+\\(\\d+,(\\s+)?\\d+\\)";
	            	if(value.matches(pattern)){
	            		String method = value.substring(0, value.indexOf('('));
	            		int start = Integer.parseInt(value.substring(value.indexOf('(') + 1, value.indexOf(',')));
	            		int end = Integer.parseInt(value.substring(value.indexOf(',') + 1, value.indexOf(')')));
	            		
	            		// update the clone model
	            		if(currSel instanceof CloneTreeNode){
	            			CloneTreeNode ctn = (CloneTreeNode)currSel;
	            			ctn.setMethod(method);
	            			ctn.setStart(start);
	            			ctn.setEnd(end);
	            		}else{
	            			// This is a bug
	            			System.err.println("The pointer to current selection is not set correctly");
	            			JOptionPane.showMessageDialog(panel, "[Grafter]The pointer to current selection is not set correctly.");
	            			System.exit(-1);
	            		}
	            	}
	            }

	            @Override
	            public void editingCanceled(ChangeEvent e) {
	            }
	        });
		    tree.setCellEditor(editor);

		    // Lastly, put the JTree into a JScrollPane.
		    JScrollPane scrollpane = new JScrollPane();
		    scrollpane.getViewport().add(tree);
		    add(BorderLayout.CENTER, scrollpane);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
	public void expandAllIncluded(){
		// Expand the tree view by default 
		DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
		DefaultMutableTreeNode root = (DefaultMutableTreeNode)model.getRoot();
		Enumeration groups = root.children();
		while(groups.hasMoreElements()){
			GroupTreeNode gtn = (GroupTreeNode)groups.nextElement();
			if(!gtn.isExcluded()) tree.expandPath(new TreePath(gtn.getPath()));
			
			Enumeration clones = gtn.children();
			while(clones.hasMoreElements()){
				CloneTreeNode ctn = (CloneTreeNode)clones.nextElement();
				if(ctn.getChildCount() > 0) tree.expandPath(new TreePath(ctn.getPath()));
			}
		}
	}
	
	public DefaultMutableTreeNode getRootNode(){
		Object obj = this.tree.getModel().getRoot();
		if(obj instanceof DefaultMutableTreeNode){
			return (DefaultMutableTreeNode)obj;
		}else{
			return null;
		}
	}
	
	public void reload(DefaultMutableTreeNode root){
		this.tree.setModel(new DefaultTreeModel(root));
	}

	public void saveToXML(String path) {
		DefaultMutableTreeNode root = (DefaultMutableTreeNode)this.tree.getModel().getRoot();
		XMLSerializer.serialize(root, path);
	}

	public void update() {
		tree.repaint();
		expandAllIncluded();
	}
}

class TreeNodeRenderer extends DefaultTreeCellRenderer{
	
	private static final long serialVersionUID = 1L;

	public Component getTreeCellRendererComponent(JTree tree, Object value,
		      boolean sel, boolean expanded, boolean leaf, int row,
		      boolean hasFocus) {
		JComponent c = (JComponent)super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf,
		        row, hasFocus);
		
		if(value instanceof GroupTreeNode){
			GroupTreeNode gtn = (GroupTreeNode)value;
			Icon icon = gtn.getIcon();
			setIcon(icon);
		}else if(value instanceof CloneTreeNode){
			CloneTreeNode ctn = (CloneTreeNode)value;
			Icon icon = ctn.getIcon();
			setIcon(icon);
		}else if(value instanceof TestTreeNode){
			TestTreeNode ttn = (TestTreeNode)value;
			Icon icon = ttn.getIcon();
			setIcon(icon);
		}
		
		// change background color to red if the clone group is excluded 
		if(sel){
		    c.setForeground(getTextSelectionColor());
		    if ((value instanceof GroupTreeNode && ((GroupTreeNode)value).isExcluded())
		    		|| (value instanceof CloneTreeNode && ((CloneTreeNode)value).isTrvial)) {
		        c.setOpaque(true);
		        c.setBackground(Color.RED);
		    } else if (value instanceof TestTreeNode && ((TestTreeNode)value).getTestResult() == TestResult.SUCCESS) {
		    	// change the background color to bright green if the test succeeded
				c.setOpaque(true);
				c.setBackground(Color.GREEN.brighter());
		    } else if(value instanceof TestTreeNode && ((TestTreeNode)value).getTestResult() == TestResult.FAILURE){
		    	// change the background color to dark red if the test failed
		    	c.setOpaque(true);
				c.setBackground(Color.RED.darker());
		    } else { 
		        c.setOpaque(false);
		        c.setBackground(getBackgroundSelectionColor());
		    }
		} else {
			if (value instanceof GroupTreeNode && ((GroupTreeNode)value).isExcluded()
		    		|| (value instanceof CloneTreeNode && ((CloneTreeNode)value).isTrvial)) {
		        c.setOpaque(true);
		        c.setBackground(Color.RED);
		    } else if (value instanceof TestTreeNode && ((TestTreeNode)value).getTestResult() == TestResult.SUCCESS) {
		    	// change the background color to bright green if the test succeeded
				c.setOpaque(true);
				c.setBackground(Color.GREEN.brighter());
		    } else if(value instanceof TestTreeNode && ((TestTreeNode)value).getTestResult() == TestResult.FAILURE){
		    	// change the background color to dark red if the test failed
		    	c.setOpaque(true);
				c.setBackground(Color.RED.darker());
		    } else {
		    	c.setOpaque(false);
		    	c.setForeground(getTextNonSelectionColor());
		    	c.setBackground(getBackgroundNonSelectionColor());
		    }
		}
		
		return c;
	}
}

class TreeNodeEditor extends DefaultTreeCellEditor{
	public TreeNodeEditor(JTree tree, DefaultTreeCellRenderer renderer) {
        super(tree, renderer);
    }
	
	@Override
    public Object getCellEditorValue() {
        return super.getCellEditorValue();
    }

	@Override
    public boolean isCellEditable(EventObject e) {
        return super.isCellEditable(e)
            && lastPath.getLastPathComponent() instanceof CloneTreeNode;
    }

}

package edu.ucla.cs.grafter.compare;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.HashMap;
 
public class TestCompareView extends JPanel {
	private static final long serialVersionUID = 1L;

	public TestCompareView(String c1, String c2, HashMap<String, Boolean> r1, HashMap<String, Boolean> r2) {
        super(new GridLayout(1,1));
        TestCompareTableModel model = new TestCompareTableModel(c1, c2, r1, r2);
        
        final JTable table = new JTable(model);
        table.setPreferredScrollableViewportSize(new Dimension(500, 70));
        table.setFillsViewportHeight(true);
 
        //Create the scroll pane and add the table to it.
        JScrollPane scrollPane = new JScrollPane(table);
        
        //Set up column sizes.
        initColumnAndPanelSizes(table);
        
        //Set up the render for Color class
        table.setDefaultRenderer(Color.class, new ColorRenderer(true));
        
        //Align the content
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        
        //Add the scroll pane to this panel.
        add(scrollPane);
    }
	
	private void initColumnAndPanelSizes(JTable table) {
        TestCompareTableModel model = (TestCompareTableModel)table.getModel();
        TableColumn column = null;
        Component comp = null;
        int width = 0;
        int headerWidth = 0;
        int cellWidth = 0;
        TableCellRenderer headerRenderer =
            table.getTableHeader().getDefaultRenderer();
        
        // set the width for the test column
        for (int i = 0; i < 3; i++) {
            column = table.getColumnModel().getColumn(i);
            
            String longestValue = "";
            for(int j = 0; j < model.getRowCount(); j++){
            	String value = (String)model.getValueAt(j, i);
            	longestValue = value.length() > longestValue.length() ? value : longestValue;
            }
            
            comp = headerRenderer.getTableCellRendererComponent(
                                 null, column.getHeaderValue(),
                                 false, false, 0, 0);
            headerWidth = comp.getPreferredSize().width;
 
            comp = table.getDefaultRenderer(model.getColumnClass(i)).
                             getTableCellRendererComponent(
                                 table, longestValue,
                                 false, false, 0, i);
            cellWidth = comp.getPreferredSize().width;
            column.setPreferredWidth(Math.max(headerWidth, cellWidth));
            width += Math.max(headerWidth, cellWidth);
        }
        
        //Set the size of the panel
        this.setPreferredSize(new Dimension(width + 100, 350));
    }
	
	class TestCompareTableModel extends AbstractTableModel {
		private static final long serialVersionUID = 1L;
		
		private String[] columnNames;
        private Object[][] data;
        
        public TestCompareTableModel(String c1, String c2, HashMap<String, Boolean> r1, HashMap<String, Boolean> r2){
        	String[] columnNames = {"Test", c1, c2, "Comparison"};
        	this.columnNames = columnNames;
        	
        	Object[][] data = new Object[r1.size()][4];
            int count = 0;
            for(String test : r1.keySet()){
            	data[count][0] = test;
            	data[count][1] = r1.get(test).toString();
            	data[count][2] = r2.get(test).toString();
            	boolean isSame = r1.get(test).equals(r2.get(test));
            	data[count][3] = isSame ? Color.GREEN.darker() : Color.RED.darker();
            	if(isSame){
            		System.out.println(test + " does not expose a behavioral divergence (" + data[count][1] + "," +  data[count][2] + ").");
            	}else{
            		System.out.println(test + " exposes a behavioral divergence(" + data[count][1] + "," +  data[count][2] + ").");
            	}
            	count ++;
            }
            this.data = data;
        }
 
        public int getColumnCount() {
            return columnNames.length;
        }
 
        public int getRowCount() {
            return data.length;
        }
 
        public String getColumnName(int col) {
            return columnNames[col];
        }
 
        public Object getValueAt(int row, int col) {
            return data[row][col];
        }
 
        /*
         * JTable uses this method to determine the default renderer/
         * editor for each cell.  If we didn't implement this method,
         * then the last column would contain text ("true"/"false"),
         * rather than a check box.
         */
        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }
    }
 
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    public void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("Test-level Comparison");
 
        //Create and set up the content pane.
        this.setOpaque(true); //content panes must be opaque
        frame.setContentPane(this);
        
        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
 
    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	HashMap<String, Boolean> result1 = new HashMap<String, Boolean>();
            	result1.put("org.apache.tools.ant.types.AbstractFileSetTest.testEmptyElementIfIsReference", false);
            	result1.put("org.apache.tools.ant.types.PatternSetTest.testCircularReferenceCheck", true);
            	result1.put("org.apache.tools.ant.types.PathTest.testFileSet", true);
            	result1.put("org.apache.tools.ant.types.PatternSetTest.testNestedPatternset", true);
            	result1.put("org.apache.tools.ant.types.PatternSetTest.testEmptyElementIfIsReference", true);

            	HashMap<String, Boolean> result2 = new HashMap<String, Boolean>();
            	result2.put("org.apache.tools.ant.types.AbstractFileSetTest.testEmptyElementIfIsReference", false);
            	result2.put("org.apache.tools.ant.types.PatternSetTest.testCircularReferenceCheck", true);
            	result2.put("org.apache.tools.ant.types.PathTest.testFileSet", true);
            	result2.put("org.apache.tools.ant.types.PatternSetTest.testNestedPatternset", true);
            	result2.put("org.apache.tools.ant.types.PatternSetTest.testEmptyElementIfIsReference", false);
            	
            	TestCompareView tcv = new TestCompareView("org.apache.tools.ant.types.PatternSet(289, 299)", 
                 		"org.apache.tools.ant.types.PatternSet(307, 317)", result1, result2);
                tcv.createAndShowGUI();
            }
        });
    }
}


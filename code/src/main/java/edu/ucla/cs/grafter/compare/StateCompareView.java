package edu.ucla.cs.grafter.compare;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.lang3.StringEscapeUtils;
import org.xml.sax.SAXParseException;

public class StateCompareView extends JPanel{
	private static final long serialVersionUID = 1L;
	HashMap<CloneState, CloneState> map = new HashMap<CloneState, CloneState>();
	
	public StateCompareView(String c1, String c2, HashMap<CloneState, CloneState> result) {
        super(new GridLayout(1,1));
        map = result;
        
        StateCompareTableModel model = new StateCompareTableModel(c1, c2, result);
        
        final JTable table = new JTable(model);
        table.setPreferredScrollableViewportSize(new Dimension(500, 70));
        table.setFillsViewportHeight(true);
 
        //Create the scroll pane and add the table to it.
        JScrollPane scrollPane = new JScrollPane(table);
        
        //Set up column sizes.
        initUIPreferences(table);
        
        //Set up the render for Color class
        table.setDefaultRenderer(Color.class, new ColorRenderer(true));
        
        //Align the content
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        
        //Add the scroll pane to this panel.
        add(scrollPane);
    }
	
	private void initUIPreferences(JTable table) {
        StateCompareTableModel model = (StateCompareTableModel)table.getModel();
        TableColumn column = null;
        Component comp = null;
        int width = 0;
        int headerWidth = 0;
        int cellWidth = 0;
        TableCellRenderer headerRenderer =
            table.getTableHeader().getDefaultRenderer();
        
        // set the width for the test column
        for (int i = 0; i < 4; i++) {
            column = table.getColumnModel().getColumn(i);
            
            String longestValue = "";
            for(int j = 0; j < model.getRowCount(); j++){
            	String value = (String)model.getValueAt(j, i);
            	if(value == null){
            		System.out.println("Warning! It shouldn't be null.");
            		value = "null";
            	}
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
            cellWidth = Math.max(headerWidth, cellWidth);
            if(cellWidth > 400) cellWidth = 400;
            column.setPreferredWidth(cellWidth);
            width += Math.max(headerWidth, cellWidth);
        }
        
        //Set tooltips for the state columns
        table.getColumnModel().getColumn(1).setCellRenderer(new StateCellRenderer());
        table.getColumnModel().getColumn(3).setCellRenderer(new StateCellRenderer());
        
        //Set tooltips for the comparison columns
        table.getColumnModel().getColumn(4).setCellRenderer(new ComparisonCellRenderer());
        
        //Set the size of the panel
        this.setPreferredSize(new Dimension(width + 100, 350));
    }
	
	class StateCompareTableModel extends AbstractTableModel {
		private static final long serialVersionUID = 1L;
		
		private String[] columnNames;
        private Object[][] data;
        
        public StateCompareTableModel(String c1, String c2, HashMap<CloneState, CloneState> result){
        	String[] columnNames = {"State - " + c1, "Value", "State - " + c2, "Value", "Comparison"};
        	this.columnNames = columnNames;
        	
        	Object[][] data = new Object[result.size()][5];
            int count = 0;
            for(CloneState cs1 : result.keySet()){
            	data[count][0] = cs1.name;
            	data[count][1] = cs1.value;
            	data[count][2] = result.get(cs1).name;
            	data[count][3] = result.get(cs1).value;
            	data[count][4] = cs1.value.equalsIgnoreCase(result.get(cs1).value) ? Color.GREEN.darker() : Color.RED.darker();
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
	
	class StateCellRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 1L;

		@Override
	    public Component getTableCellRendererComponent(
	                        JTable table, Object value,
	                        boolean isSelected, boolean hasFocus,
	                        int row, int column) {
	        JLabel c = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	        
	        System.out.println(value.toString());
			if(value.toString().equals("null") || value.toString().equals("no value") || value.toString().contains("&#")){
				// all these cases will induce SAXParseException
				String s = value.toString();
				String[] ss = s.split(System.lineSeparator()); 
				if(ss.length > 100){
					// truncate the content if it has over 100 lines
					StringBuilder sb = new StringBuilder();
					for(String si : ss){
						sb.append(si + System.lineSeparator());
					}
					s = sb.toString();
				}

				c.setToolTipText(s);
				return c;
			}
	        
			try {
		        TransformerFactory transformerFactory = TransformerFactory.newInstance();
		        transformerFactory.setAttribute("indent-number", 4);
		        Transformer transformer = transformerFactory.newTransformer();
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		        //initialize StreamResult with File object to save to file
		        StreamResult result = new StreamResult(new StringWriter());
		        Source src = new StreamSource(new StringReader(value.toString()));
		        transformer.transform(src, result);
		        String st = result.getWriter().toString();
		        st = st.substring(38);
		        st = "<html>" + StringEscapeUtils.escapeHtml4(st).replace(System.lineSeparator(), "<br>").replace(" ", "&nbsp;") + "</html>";
		        c.setToolTipText(st);
//		        c.setToolTipText(c.getText());
			} catch (TransformerFactoryConfigurationError | TransformerException e) {
				e.printStackTrace();
			}
	        
	        return c;
	    }
	}
	
	class ComparisonCellRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 1L;

		@Override
	    public Component getTableCellRendererComponent(
	                        JTable table, Object value,
	                        boolean isSelected, boolean hasFocus,
	                        int row, int column) {
	        JLabel c = (JLabel)super.getTableCellRendererComponent(table, "", isSelected, hasFocus, row, column);
	        
	        Color color = (Color)value;
	        c.setBackground(color);
	        
	        String tip = "";
	        int i = 0;
	        for(CloneState cs1 : map.keySet()){
	        	if(i == row){
	        		tip = cs1.test + "_" + cs1.index;
	        		break;
	        	}
	        	
	        	i++;
	        }
	        
			c.setToolTipText(tip);
	        
	        return c;
	    }
	}

	
	/**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    public void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("State-level Comparison");
 
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
            	HashMap<CloneState, CloneState> result = new HashMap<CloneState, CloneState>();
            	result.put(new CloneState("includeList", 
//            			"<html>Following word <br> is <font color=\"red\">RED</font>.</html>",
            			"<root><child>aaa</child><child/></root>",
            			"", 0),  new CloneState("excludeList", "<root>bbb</root>", "", 0));
            	
            	StateCompareView scv = new StateCompareView("PatternSet(289, 299)", "PatternSet(307, 317)", result);
                scv.createAndShowGUI();
            }
        });
    }
}

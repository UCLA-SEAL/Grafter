package edu.ucla.cs.grafter.instrument;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JOptionPane;

import org.eclipse.jdt.core.dom.CompilationUnit;

import edu.ucla.cs.grafter.config.GrafterConfig;
import edu.ucla.cs.grafter.file.FileUtils;
import edu.ucla.cs.grafter.graft.analysis.CloneCalibrator;

public class CloneInstrument {
	static final String template = "/home/troy/SysAssure/code/Grafter/Grafter/src/main/resources/template/TestTracker.template";
	int id;
	String path;
	int start;
	int end;
	private int start_new;
	private int end_new;
	
	public CloneInstrument(int id, String path, int start, int end){
		this.id = id;
		this.path = path;
		this.start = start;
		this.end = end;
		this.start_new = start;
		this.end_new = end;
	}
	
	public void instrument() throws InstrumentException{
		try {
			addTestTracker();
			updateRange();
			insertPrintStatement();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	void addTestTracker() throws IOException{
		// Check if TestTracker.java exists in the current package
		String dir = path.substring(0, path.lastIndexOf(File.separator));
		String testTracker = dir + File.separator + "TestTracker.java";
		File file = new File(testTracker);

		if(!file.exists()){
			// Get the package name
			String packageName = getPackageName();
			// Customize the template
			String code = "package " + packageName + ";" + System.lineSeparator() +FileUtils.readFileToString(template);
			
			// Create TestTracker.java in the package of the code clone
			file.createNewFile();
			BufferedWriter bw = null;
			try{
				bw = new BufferedWriter(new FileWriter(file));
				bw.write(code);
			}catch(IOException e){
				e.printStackTrace();
			}finally{
				if(bw != null) bw.close();
			}
		}
	}
	
	/**
	 * Update the start and end line numbers, since Grafter may have inserted print statements into the same file for other clone before
	 * @throws IOException 
	 * @throws InstrumentException 
	 */
	void updateRange() throws IOException, InstrumentException{
		String code = FileUtils.readFileToString(this.path);
	    String lineSeparator = System.getProperty("line.separator");
	    
	    String[] cc = code.split(lineSeparator);
	    int counter = 0; // count the inserted print statements before the clone
	    int counter2 = 0; // count the inserted print statements in the clone
	    for(int i = 0; i < cc.length; i++){
	    	if(i < this.start && cc[i].contains("TestTracker.getTestName")){
	    		counter ++;
	    	}else if(i >= this.start && i <= this.end && cc[i].contains("TestTracker.getTestName")){
	    		// a clone may appear in other clone groups and therefore can be instrumented before
				counter2 ++;
	    	}
	    }
	    
	    this.start_new += counter;
	    this.end_new += counter + counter2;
	}
	
	void insertPrintStatement() throws IOException, InstrumentException{		
		// Get the line number of the first statement in the clone
		CompilationUnit cu = JavaParser.parse(this.path); 
		CloneCalibrator calibrator = new CloneCalibrator(cu, this.start_new, this.end_new);
		cu.accept(calibrator);
				
		if(calibrator.first == Integer.MAX_VALUE){
			// we cannot find the first statment in the clone, please double check the validity of the clone
			if(GrafterConfig.batch){
				System.out.println("[Grafter]Cannot find the first statement in the clone in file -- " + this.path + " -- in group " + this.id);
			}else{
				JOptionPane.showMessageDialog(null, "[Grafter]Cannot find the first statement in the clone in file -- " + this.path + " -- in group " + this.id);
			}
			
			throw new InstrumentException();
		}
				
		// insert the print statement in the clone
		int ln = calibrator.first;
		String clazz = this.path.substring(this.path.lastIndexOf(File.separator) + 1, this.path.lastIndexOf('.'));
		String instr = "System.out.println(\"[Grafter][Clone Group " + this.id + "][Class " + clazz + "][Range(" 
				+ this.start + "," + this.end + ")]\"+ TestTracker.getTestName());";
		String code = FileUtils.readFileToString(this.path);
	    String lineSeparator = System.getProperty("line.separator");

		String[] cc = code.split(lineSeparator);
		String before = "";
		String after = "";
		for(int i = 0; i < cc.length; i++){
			if(i + 1 < ln){
				before += cc[i] + lineSeparator;
			}else{
				after += cc[i] + lineSeparator;
			}
		}
		
		// rename the old file
		File backup = new File(this.path + ".bak");
		if(!backup.exists()){
			// no need to back up multiple times
			File old_file = new File(this.path);
			backup.createNewFile();
			old_file.renameTo(new File(this.path + ".bak"));
		}
		
		// create a new file with the same name
		File new_file = new File(this.path);
		new_file.createNewFile();
		code = before + instr + lineSeparator + after;
		BufferedWriter bw = null;
		try{
			bw = new BufferedWriter(new FileWriter(new_file));
			bw.write(code);
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			if(bw != null) bw.close();
		}
	}
	
	String getPackageName(){
		try {
			CompilationUnit cu = JavaParser.parse(path);
			return cu.getPackage().getName().getFullyQualifiedName();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return "";
	}
}
package edu.ucla.cs.grafter.file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

import edu.ucla.cs.grafter.config.GrafterConfig;
import edu.ucla.cs.grafter.instrument.JavaParser;

public class FileUtils {
	
	public static String readFileToString(String filePath) throws IOException {
		File file = new File(filePath);
	    StringBuilder fileContents = new StringBuilder((int)file.length());
	    Scanner scanner = new Scanner(file);
	    String lineSeparator = System.getProperty("line.separator");

	    try {
	        while(scanner.hasNextLine()) {        
	            fileContents.append(scanner.nextLine() + lineSeparator);
	        }
	        return fileContents.toString();
	    } finally {
	        scanner.close();
	    }
	}
	
	public static String[] readFileToArray(String filePath) throws IOException{
		String content = readFileToString(filePath);
		return content.split(System.lineSeparator());
	}
	
	public static ArrayList<File> match(String pattern, File dir){
		ArrayList<File> matches = new ArrayList<File>();
		File[] files = dir.listFiles();
		if(files != null){
			for(File f : files){
				if(f.isDirectory()){
					matches.addAll(match(pattern, f));
				}else if(f.getName().matches(pattern)){
					matches.add(f);
				}
			}
		}
		
		return matches;
	}
	
	/**
	 * Fine the file in the src directory based on the given file name
	 * @param name
	 * @return
	 */
	public static File findFile(String name){
		File dir = new File(GrafterConfig.src_dir);
		return findFileHelper(name, dir);
	}
	
	public static File findFileHelper(String name, File dir){
		File result = null;
		File[] files = dir.listFiles();
		if(files != null){
			for(File f : files){
				if(f.getName().equalsIgnoreCase(name)){
					return f;
				}else if(f.isDirectory()){
					result = findFileHelper(name, f);
					if(result != null){
						return result;
					}
				}
			}
		}
		
		return result;
	}
	
	public static void writeStringtoFile(String content, String path) throws IOException{
		File log = new File(path);
		FileWriter w = new FileWriter(log, false);
		BufferedWriter writer = new BufferedWriter(w);
		writer.write(content);
		writer.flush();
		writer.close();
	}
	
	public static void writeStringArraytoFile(String[] lines, String path) throws IOException {
		String s = "";
		for(int i = 0; i < lines.length; i++){
			String line = lines[i];
			if(i == lines.length - 1) {
				s += line;
			}else{
				s += line + System.lineSeparator();
			}
		}
		writeStringtoFile(s, path);
	}
	
	/**
	 * insert the content to a specific line
	 * @param content
	 * @param line
	 * @param path
	 * @throws IOException 
	 */
	public static void writeStringtoFile(String content, int line, String path) throws IOException{
		String[] ss = readFileToArray(path);
        ArrayList<String> contents = new ArrayList<String>();
        for(String s : ss){
        	contents.add(s);
        }
        
        contents.add(line - 1, content);
        
        String text = "";
        for(int i = 0; i < contents.size() - 1; i ++){
        	text += contents.get(i) + System.lineSeparator();
        }
        text += contents.get(contents.size() - 1);
        
        writeStringtoFile(text, path);
	}
	
	public static void rewriteStringToFile(String content, int line, String path) throws IOException{
		String[] ss = readFileToArray(path);
		ArrayList<String> contents = new ArrayList<String>();
        for(String s : ss){
        	contents.add(s);
        }
        
        contents.set(line - 1, content);
        
        String text = "";
        for(int i = 0; i < contents.size() - 1; i ++){
        	text += contents.get(i) + System.lineSeparator();
        }
        text += contents.get(contents.size() - 1);
        
        writeStringtoFile(text, path);
	}
	
	public static int getStartIndex(String s, int line){
		String lineSeparator = System.getProperty("line.separator");
		String[] lines = s.split(lineSeparator);
		
		int index = 0;
		// line number starts from 1
		for(int i = 1; i < line; i ++){
			index += lines[i-1].length() + lineSeparator.length();
		}
		
		return index;
	}
	
	public static int getEndIndex(String s, int line){
		String[] lines = s.split(System.lineSeparator());
		
		int index = 0;
		// line number starts from 1
		for(int i = 1; i < line + 1; i ++){
			index += lines[i-1].length() + System.lineSeparator().length();
		}
		
		return index - System.lineSeparator().length();
	}
	
	public static int grepLineNumber(String file, String text) throws IOException {
		File f = new File(file);
	    LineNumberReader rdr = new LineNumberReader(new FileReader(f));
	 
	    try {
	        String line = rdr.readLine();
	        while(line != null){
	        	if (line.indexOf(text) >= 0) {
		            return rdr.getLineNumber();
		        }else{
		        	line = rdr.readLine();
		        }
	        }
	        
	    } finally {
	        rdr.close();
	    }
	    
	    return -1;
	}
	
	public static String grepLines(String file, String text) throws IOException {
		File f = new File(file);
	    LineNumberReader rdr = new LineNumberReader(new FileReader(f));
	    String results = "";
	    
	    try {
	        String line = rdr.readLine();
	        while(line != null){
	        	if (line.indexOf(text) >= 0 && !line.contains("ucla.cs.")) {
		            results += line + "\n";
		        }
	        	
		        line = rdr.readLine();
	        }
	        
	    } finally {
	        rdr.close();
	    }
	    
	    return results;
	}
	
	public static String grepLine(String file, int n) throws IOException{
		File f = new File(file);
	    LineNumberReader rdr = new LineNumberReader(new FileReader(f));
	    
	    try {
	    	int i = 1;
	        String line = rdr.readLine();
	        while(line != null){
	        	if (i == n) {
		            return line;
		        }else{
		        	line = rdr.readLine();
		        	i ++;
		        }
	        }
	        
	    } finally {
	        rdr.close();
	    }
	    
	    return "";
	}
	
	public static void match_delete(String pattern){
		ArrayList<File> matches = match(pattern, new File(GrafterConfig.src_dir));
		for(File f : matches){
			f.delete();
		}
	}
	
	public static boolean delete(String path){
		File f = new File(path);
		if(!f.exists()) return true;
		
		if(f.isDirectory()){
			File[] files = f.listFiles();
			if(files != null){
				for(File file : files){
					delete(file.getAbsolutePath());
				}
			}
		}
		
		return f.delete();
	}
	
	
	public static boolean emptyDir(String path){
		File dir = new File(path);
		if(!dir.exists()) return false;
		
		if(dir.isDirectory()){
			File[] files = dir.listFiles();
			if(files != null){
				for(File file : files){
					delete(file.getAbsolutePath());
				}
			}
		}
		
		return true;
	}
	
	public static HashSet<File> findFilesWithExtension(String extension, File dir){
		HashSet<File> bakFiles = new HashSet<File>();
		if(dir.isDirectory()){
			File[] files = dir.listFiles();
			for(File file : files){
				if(file.isFile() && file.getName().endsWith(extension)){
					bakFiles.add(file);
				}else if(file.isDirectory()){
					HashSet<File> bakFiles2 = findFilesWithExtension(extension, file);
					bakFiles.addAll(bakFiles2);
				}
			}
		}
		
		return bakFiles;
	}
}

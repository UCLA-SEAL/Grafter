package edu.ucla.cs.grafter.graft.analysis;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

public class CloneParser {
	
	//read file content into a string
	public String readFileToString(String filePath) throws IOException {
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
			
	public CompilationUnit parse(String file) throws IOException {
		// read file
		String str = readFileToString(file);
				
		// call ASPParser to generate the AST
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setSource(str.toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		Map options = JavaCore.getOptions();
		JavaCore.setComplianceOptions(JavaCore.VERSION_1_5, options);
		parser.setCompilerOptions(options);
		final CompilationUnit cu = (CompilationUnit) parser.createAST(null);
		
		return cu;
	}
	
	public static void main(String[] args) throws IOException{
		CloneParser cp = new CloneParser();
		String str = cp.readFileToString("/home/troy/SysAssure/dataset/Ant0.0/src/main/org/apache/tools/ant/types/PatternSet.java");
		int x = str.indexOf("public boolean equals(MinuteOfHour another) {");
		System.out.println(x+54);
		System.out.println(x+84);
		System.out.print(str.substring(6401, 6737));
	}
}

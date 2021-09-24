package edu.ucla.cs.grafter.instrument;

import java.io.IOException;
import java.util.Map;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

import edu.ucla.cs.grafter.file.FileUtils;

public class JavaParser {				
	public static CompilationUnit parse(String file) throws IOException {
		// read file
		String str = FileUtils.readFileToString(file);
					
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
}

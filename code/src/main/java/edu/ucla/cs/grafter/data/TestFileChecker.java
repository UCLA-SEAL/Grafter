package edu.ucla.cs.grafter.data;

import java.io.IOException;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import edu.ucla.cs.grafter.instrument.JavaParser;

public class TestFileChecker extends ASTVisitor{
	boolean isTest = false;
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.MarkerAnnotation)
	 * MarkerAnnotation is like @Type
	 */
	@Override
	public boolean visit(MarkerAnnotation node){
		// check for JUnit 4 test cases
		Name name = node.getTypeName();
		
		if(name.toString().equals("org.junit.Test") || name.toString().equals("Test") || name.toString().equals("Before") || name.toString().equals("After")
				|| name.toString().equals("Theory")){	
			isTest = true;
		}		
		return false;
	}
		
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.NormalAnnotation)
	 * NormalAnnotation is like @Type(X=Y)
	 */
	@Override
	public boolean visit(NormalAnnotation node){
		// Check for JUnit 4 test cases with annotation like @Test(timeout=1500)
		Name name = node.getTypeName();
		String pattern = "(org\\.junit\\.)?Test(\\(.*\\))?";
		if(name.toString().matches(pattern)){	
			isTest = true;
		}		
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.NormalAnnotation)
	 * NormalAnnotation is like @Type(X)
	 */
	@Override
	public boolean visit(SingleMemberAnnotation node){
		Name name = node.getTypeName();
		String pattern = "(org\\.junit\\.)?Test(\\(.*\\))?";
		if(name.toString().matches(pattern)){	
			isTest = true;
		}		
		return false;
	}
	
	@Override
	public boolean visit(TypeDeclaration node){
		// Check for JUnit 3 test cases
		Type superType = node.getSuperclassType();
		if(superType != null && (superType.toString().equals("TestCase") || superType.toString().equals("TestSuite"))){
			isTest = true;
			return false;
		}
		return true;
	}
	
	public boolean filter(String file) throws IOException{
		CompilationUnit cu = JavaParser.parse(file);
		cu.accept(this);
		return isTest;
	}
}

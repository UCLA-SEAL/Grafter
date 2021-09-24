package edu.ucla.cs.grafter.graft.analysis;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;

public class ReturnTypeFinder extends ASTVisitor{
	int line;
	CompilationUnit cu;
	public String type;
	
	public ReturnTypeFinder(int n, CompilationUnit cu){
		line = n;
		this.cu = cu;
	}
	
	@Override
	public boolean visit(MethodDeclaration node){
		int start = cu.getLineNumber(node.getStartPosition());
		int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
		
		if(start <= line && end >= line){
			type = node.getReturnType2() == null ? "" : node.getReturnType2().toString();
		}
		
		return false;
	}
}

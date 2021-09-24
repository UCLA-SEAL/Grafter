package edu.ucla.cs.grafter.graft.analysis;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AssertStatement;
import org.eclipse.jdt.core.dom.BreakStatement;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.ContinueStatement;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

public class StatementFinder extends ASTVisitor{
	int line;
	CompilationUnit cu;
	public ASTNode node;
	
	public StatementFinder(CompilationUnit cu, int line){
		this.line = line;
		this.cu = cu;
		this.node = null;
	}
	
	private int getStartLineNumber(ASTNode node){
		return cu.getLineNumber(node.getStartPosition());
	}
	
	private int getEndLineNumber(ASTNode node){
		return cu.getLineNumber(node.getStartPosition() + node.getLength());
	}
	
	@Override
	public boolean visit(AssertStatement node){
		int start = getStartLineNumber(node);
		int end = getEndLineNumber(node);
			
		if(start <= line && line <= end){
			this.node = node;
		}
		
		return false;
	}
	
	@Override
	public boolean visit(BreakStatement node){
		int start = getStartLineNumber(node);
		int end = getEndLineNumber(node);
			
		if(start <= line && line <= end){
			this.node = node;
		}
		
		return false;
	}
	
	@Override
	public boolean visit(ConstructorInvocation node){
		int start = getStartLineNumber(node);
		int end = getEndLineNumber(node);
			
		if(start <= line && line <= end){
			this.node = node;
		}
		
		return false;
	}
	
	@Override
	public boolean visit(ContinueStatement node){
		int start = getStartLineNumber(node);
		int end = getEndLineNumber(node);
			
		if(start <= line && line <= end){
			this.node = node;
		}
		
		return false;
	}
	
	@Override
	public boolean visit(ExpressionStatement node){
		int start = getStartLineNumber(node);
		int end = getEndLineNumber(node);
			
		if(start <= line && line <= end){
			this.node = node;
		}
		
		return false;
	}
	
	@Override
	public boolean visit(ReturnStatement node){
		int start = getStartLineNumber(node);
		int end = getEndLineNumber(node);
			
		if(start <= line && line <= end){
			this.node = node;
		}
		
		return false;
	}
	
	@Override
	public boolean visit(VariableDeclarationStatement node){
		int start = getStartLineNumber(node);
		int end = getEndLineNumber(node);
			
		if(start <= line && line <= end){
			this.node = node;
		}
		
		return false;
	}
	
	@Override
	public boolean visit(SwitchCase node){
		int start = getStartLineNumber(node);
		int end = getEndLineNumber(node);
			
		if(start <= line && line <= end){
			this.node = node;
		}
		
		return false;
	}
	
	@Override
	public boolean visit(SuperConstructorInvocation node){
		int start = getStartLineNumber(node);
		int end = getEndLineNumber(node);
			
		if(start <= line && line <= end){
			this.node = node;
		}
		
		return false;
	}
	
	@Override
	public boolean visit(ThrowStatement node){
		int start = getStartLineNumber(node);
		int end = getEndLineNumber(node);
			
		if(start <= line && line <= end){
			this.node = node;
		}
		
		return false;
	}
}

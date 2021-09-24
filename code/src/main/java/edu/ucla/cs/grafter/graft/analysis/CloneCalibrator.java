package edu.ucla.cs.grafter.graft.analysis;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AssertStatement;
import org.eclipse.jdt.core.dom.BreakStatement;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.ContinueStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.SynchronizedStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;

public class CloneCalibrator extends ASTVisitor{
	public int first = Integer.MAX_VALUE; // the line number of the first statement in the clone
	public int last = Integer.MIN_VALUE; // the line number of the last statement in the clone
	int start = -1;
	int end = -1;
	CompilationUnit cu;
	
	public CloneCalibrator(CompilationUnit cu, int start, int end){
		this.cu = cu;
		this.start = start;
		this.end = end;
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
			
		if(this.start <= start && end <= this.end){
			if(start < this.first)	this.first = start;
			if(end > this.last) this.last = end;
		}
		
		return false;
	}
	
	@Override
	public boolean visit(BreakStatement node){
		int start = getStartLineNumber(node);
		int end = getEndLineNumber(node);
			
		if(this.start <= start && end <= this.end){
			if(start < this.first)	this.first = start;
			if(end > this.last) this.last = end;
		}
		
		return false;
	}
	
	@Override
	public boolean visit(ConstructorInvocation node){
		int start = getStartLineNumber(node);
		int end = getEndLineNumber(node);
			
		if(this.start <= start && end <= this.end){
			if(start < this.first)	this.first = start;
			if(end > this.last) this.last = end;
		}
		
		return false;
	}
	
	@Override
	public boolean visit(ContinueStatement node){
		int start = getStartLineNumber(node);
		int end = getEndLineNumber(node);
			
		if(this.start <= start && end <= this.end){
			if(start < this.first)	this.first = start;
			if(end > this.last) this.last = end;
		}
		
		return false;
	}
	
	@Override
	public boolean visit(EnhancedForStatement node){
		int start = getStartLineNumber(node);
		int end = getEndLineNumber(node);
			
		if(this.start <= start && end <= this.end){
			if(start < this.first)	this.first = start;
			if(end > this.last) this.last = end;
		}else if (start < this.start  && this.end < end){
			// the clone is within this node
			return true;
		}
		
		return false;
	}
	
	@Override
	public boolean visit(ExpressionStatement node){
		int start = getStartLineNumber(node);
		int end = getEndLineNumber(node);
			
		if(this.start <= start && end <= this.end){
			if(start < this.first)	this.first = start;
			if(end > this.last) this.last = end;
		}
		
		return false;
	}
	
	@Override
	public boolean visit(ForStatement node){
		int start = getStartLineNumber(node);
		int end = getEndLineNumber(node);
			
		if(this.start <= start && end <= this.end){
			if(start < this.first)	this.first = start;
			if(end > this.last) this.last = end;
		}else if (start < this.start  && this.end < end){
			// the clone is within this node
			return true;
		}
		
		return false;
	}
	
	@Override
	public boolean visit(IfStatement node){
		int start = getStartLineNumber(node);
		int end = getEndLineNumber(node);
			
		if(this.start <= start && end <= this.end){
			if(start < this.first)	this.first = start;
			if(end > this.last) this.last = end;
		}else if (start < this.start  && this.end < end){
			// the clone is within this node
			return true;
		}
		
		return false;
	}
	
	@Override
	public boolean visit(ReturnStatement node){
		int start = getStartLineNumber(node);
		int end = getEndLineNumber(node);
			
		if(this.start <= start && end <= this.end){
			if(start < this.first)	this.first = start;
			if(end > this.last) this.last = end;
		}
		
		return false;
	}
	
	@Override
	public boolean visit(SwitchStatement node){
		int start = getStartLineNumber(node);
		int end = getEndLineNumber(node);
			
		if(this.start <= start && end <= this.end){
			if(start < this.first)	this.first = start;
			if(end > this.last) this.last = end;
		}else if (start < this.start  && this.end < end){
			// the clone is within this node
			return true;
		}
		
		return false;
	}
	
//	@Override
//	public boolean visit(SuperConstructorInvocation node){
//		int start = getStartLineNumber(node);
//		int end = getEndLineNumber(node);
//			
//		if(this.start <= start && end <= this.end){
//			if(start < this.first)	this.first = start;
//			if(end > this.last) this.last = end;
//		}
//		
//		return false;
//	}
	
	@Override
	public boolean visit(SynchronizedStatement node){
		int start = getStartLineNumber(node);
		int end = getEndLineNumber(node);
			
		if(this.start <= start && end <= this.end){
			if(start < this.first)	this.first = start;
			if(end > this.last) this.last = end;
		}else if (start < this.start  && this.end < end){
			// the clone is within this node
			return true;
		}
		
		return false;
	}
	
	@Override
	public boolean visit(TryStatement node){
		int start = getStartLineNumber(node);
		int end = getEndLineNumber(node);
			
		if(this.start <= start && end <= this.end){
			if(start < this.first)	this.first = start;
			if(end > this.last) this.last = end;
		}else if (start < this.start  && this.end < end){
			// the clone is within this node
			return true;
		}
		
		return false;
	}
	
	@Override
	public boolean visit(VariableDeclarationStatement node){
		int start = getStartLineNumber(node);
		int end = getEndLineNumber(node);
			
		if(this.start <= start && end <= this.end){
			if(start < this.first)	this.first = start;
			if(end > this.last) this.last = end;
		}
		
		return false;
	}
	
	@Override
	public boolean visit(WhileStatement node){
		int start = getStartLineNumber(node);
		int end = getEndLineNumber(node);
			
		if(this.start <= start && end <= this.end){
			if(start < this.first)	this.first = start;
			if(end > this.last) this.last = end;
		}else if (this.start < start && end > this.end){
			// the clone is within this node
			return true;
		}
		
		return false;
	}
}

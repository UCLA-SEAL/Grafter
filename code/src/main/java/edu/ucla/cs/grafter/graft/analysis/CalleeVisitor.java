package edu.ucla.cs.grafter.graft.analysis;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SimpleName;
import edu.ucla.cs.grafter.graft.model.Token;

/**
 * @author troy
 * This visitor is supposed to collect class fields that are used in the callee method
 */
public class CalleeVisitor extends ASTVisitor{
	HashSet<Token> fields = new HashSet<Token>();
	HashSet<Token> uses = new HashSet<Token>();
	HashSet<Token> effects = new HashSet<Token>();
	HashSet<String> callstack = new HashSet<String>();
	String cur;
	boolean isAssignmentLeftHand = false;
	boolean isInvoked = false;
	
	public CalleeVisitor(String method, ArrayList<Token> fields){
		this.cur = method;
		this.fields.addAll(fields);
	}
	
	@Override
	public boolean visit(MethodInvocation node){
		Expression expr = node.getExpression();
		if(expr != null){
			isInvoked = true;
			expr.accept(this);
		}else{
			String name = node.getName().toString();
			if(!name.equals(cur)){
				// skip recursive calls
				callstack.add(name);
			}
		}
		
		isInvoked = false;
		List args = node.arguments();
		if(args != null){
			for(Object arg : args){
				if(arg instanceof ASTNode){
					((ASTNode) arg).accept(this);
				}
			}
		}
		
		return false;
	}
	
	@Override
	public boolean visit(Assignment node){
		Expression left = node.getLeftHandSide();
		Expression right = node.getRightHandSide();
		
		isAssignmentLeftHand = true;
		left.accept(this);
		isAssignmentLeftHand = false;
		right.accept(this);
		
		return false;
	}
	
	@Override
	public boolean visit(FieldAccess node){
		if(node.getExpression().toString().equals("this")){
			String name = node.getName().getIdentifier();
			for(Token f : fields){
				if(f.getName().equals(name)){
					if(isInvoked){
						// a method is called on this field, so it is used and may be affected
						uses.add(f);
						effects.add(f);
					}else if(!isAssignmentLeftHand){
						// the field is used at the right side of an assignment expression 
						uses.add(f);
					}else{
						// it is at the left side of an assignment expression and it is not used to call method, so it is assigned a new value(affected)
						effects.add(f);
					}
					
					break;
				}
			}
		}else{
			Expression expr = node.getExpression();
			expr.accept(this);
		}
		
		return false;
	}
	
	@Override
	public boolean visit(SimpleName node){
		String name = node.getIdentifier();

		if(isInvoked){
			for(Token f : fields){
				if(f.getName().equals(name)){
					uses.add(f);
					effects.add(f);
					ArrayList<String> path = findPath(node, new ArrayList<String>());
					f.addUsage(path);
					break;
				}
			}
		}else if(!isAssignmentLeftHand){
			for(Token f : fields){
				if(f.getName().equals(name)){
					uses.add(f);
					ArrayList<String> path = findPath(node, new ArrayList<String>());
					f.addUsage(path);
					break;
				}
			}
		}else{
			for(Token f : fields){
				if(f.getName().equals(name)){
					effects.add(f);
					ArrayList<String> path = findPath(node, new ArrayList<String>());
					f.addUsage(path);
					break;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * copy-paste from CloneVisitor private method, findPath. Modified the if condition.
	 * @param node
	 * @param p
	 * @return
	 */
	private ArrayList<String> findPath(ASTNode node, ArrayList<String> p){
		ArrayList<String> path = new ArrayList<String>(p);
		
		if(node == null || node instanceof MethodDeclaration){
			return path;
		}else{
			String qName = node.nodeClassForType(node.getNodeType()).getName();
			String[] ss = qName.split("\\.");
			String sName = ss[ss.length - 1];
			path.add(sName);
			
			ASTNode parent = node.getParent();
			return findPath(parent, path);
		}
	}
}

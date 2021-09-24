package edu.ucla.cs.grafter.graft.analysis;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;

import edu.ucla.cs.grafter.file.FileUtils;
import edu.ucla.cs.grafter.graft.model.Clone;
import edu.ucla.cs.grafter.graft.model.Label;
import edu.ucla.cs.grafter.graft.model.Method;
import edu.ucla.cs.grafter.graft.model.Token;

/**
 * CloneVisitor collects a list of identifiers, including variables, fields and parameters that
 * are used by the code clone but are not defined in the code clone
 * 
 * @author troy
 * @date Mar 08， 2015
 */
public class CloneVisitor extends ASTVisitor{
	static Logger log = Logger.getLogger(CloneVisitor.class.getName());
	
	CompilationUnit cu;
	Clone clone;
	HashSet<Token> defs;
	HashSet<Token> wilds; // subset of uses, including variables used by the clone but not defined within the code clone
	HashSet<Token> uses; // all variables used within the code clone
	HashSet<Token> effects; // all variables that are potentially affected by the code clone 
	ArrayList<Token> vars; // all local variables in the code clone
	HashSet<Token> vars2; // local variables declared in the for, while, try, catch, if blocks.
	ArrayList<Token> fields; 
	boolean isAssignmentLeftHand = false;
	boolean isInvoked = false;
	boolean inBlock = false;
	Stack<Pair<Integer, Integer>> blockStacks = new Stack<Pair<Integer, Integer>>();
	boolean isArgument = false;
	public HashSet<String> import_decls = new HashSet<String>();
	public HashMap<String, FieldDeclaration> field_decls = new HashMap<String, FieldDeclaration>();
	public HashMap<Method, MethodDeclaration> method_decls = new HashMap<Method, MethodDeclaration>();
	HashSet<String> callstack = new HashSet<String>();
	public String path;
	
	public int methodStart = 0; // the line number of the last field declaration
	public boolean isMethodEnd = false;
	
	@Deprecated
	public CloneVisitor(Clone clone){
		this.clone = clone;
	}
	
	public CloneVisitor(Clone clone, CompilationUnit cu, String path){
		this.clone = clone;
		this.cu = cu;
		this.path = path;
		defs = new HashSet<Token>();
		wilds = new HashSet<Token>();
		vars = new ArrayList<Token>();
		vars2 = new HashSet<Token>();
		fields = new ArrayList<Token>();
		uses = new HashSet<Token>();
		effects = new HashSet<Token>();
	}
	
	private int getLineNumber(ASTNode node){
		return cu.getLineNumber(node.getStartPosition());
	}
	
	@Override
	public boolean visit(ImportDeclaration node){
		this.import_decls.add(node.toString());
		return false;
	}
	
	@Override
	public boolean visit(TypeDeclaration node){
		if(!node.getName().toString().equals(this.clone.getOwner())){
			// TODO: currently we do not handle inner classes
			return false;
		}
		
		Type superType = node.getSuperclassType();
		if(superType != null){
			if(superType.isSimpleType()){
				SimpleType st = (SimpleType)superType;
				String fn = st.getName().getFullyQualifiedName();
				String[] ss = fn.split("\\.");
				String name = ss[ss.length - 1];
				File file = FileUtils.findFile(name + ".java");
				if(file != null){
					CloneParser cp = new CloneParser();
					try {
						CompilationUnit cu = cp.parse(file.getAbsolutePath());
						CloneVisitor cv = new CloneVisitor(new Clone(0, 0, "", name), cu, file.getAbsolutePath());
						cu.accept(cv);
						
						// add all fields in the superclass if not overriden 
						for(Token f : cv.fields){
							if(!this.fields.contains(f)){
								this.fields.add(f);
								this.field_decls.put(f.getName(), cv.field_decls.get(f.getName()));
							}
						}
						
						// add all inherited methods, if not overridden
						for(Method m : cv.method_decls.keySet()){
							if(!this.method_decls.containsKey(m)){
								this.method_decls.put(m, cv.method_decls.get(m));
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
						System.exit(-1);
					}
				}
			}
		}
		
		return true;
	}
	
	@Override
	public boolean visit(FieldDeclaration node){
		String type = node.getType().toString();
		
		boolean isFinal = false;
		boolean isPrivate = false;
		boolean isStatic = false;
		boolean isProtected = false;
		
		// check if it is final and private
		List mods = node.modifiers();
		for(Object obj : mods){
			if(obj instanceof Modifier){
				String mod = ((Modifier)obj).toString();
				if(mod.equals("final")){
					isFinal = true;
				}else if(mod.equals("private")){
					isPrivate = true;
				}else if(mod.equals("protected")){
					isProtected = true;
				}else if(mod.equals("static")){
					isStatic = true;
				}
			}
		}
		
		Iterator<?> iter = node.fragments().iterator();
		while(iter.hasNext()){
			VariableDeclarationFragment f = (VariableDeclarationFragment)iter.next();
			SimpleName var = f.getName();
			String name = var.getIdentifier();
			
			// check if the field is initialized
			// check if the field is initialized
			boolean isInit = false;
			String init = null;
			if(f.getInitializer() != null){
				isInit = true;
				init = f.getInitializer().toString();
			}
			
			Token t = new Token(type, Label.FIELD, name, isFinal, isPrivate, isProtected, isInit);
			fields.add(t);
			t.setInit(init);
			t.isStatic = isStatic;
			t.setPath(this.path);
			this.field_decls.put(t.getName(), node);
		}
		
		return false;
	}
	
	@Override
	public boolean visit(MethodDeclaration node){
		String name = node.getName().getIdentifier();
		
		ArrayList<String> args = new ArrayList<String>();
		List l = node.parameters();
		for(Object o : l){
			if(o instanceof SingleVariableDeclaration){
				SingleVariableDeclaration svd = (SingleVariableDeclaration)o;
				args.add(svd.getType().toString());
			}
		}
		Method m = new Method(name, args);
		method_decls.put(m, node);
		
		// only analyze the method that contains code clones, also need to check the location of method since there might be overwritten methods
		if(name.equals(clone.getMethod()) && node.getStartPosition() <= clone.getX() && node.getStartPosition() + node.getLength() >= clone.getY()){
			int start = cu.getLineNumber(node.getStartPosition());
			int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
			
			// the method containing the clone is the biggest block and should be pushed to the blockStack first
			blockStacks.push(new ImmutablePair<Integer, Integer>(start, end));

			// set method start
			this.methodStart = start;
			
			// check if clone is at the end of the method
			if(end == clone.getEnd() + 1){
				this.isMethodEnd = true;
			}
			
			// check if it is static
			boolean isStatic = false;
			List mods = node.modifiers();
			for(Object obj : mods){
				if(obj instanceof Modifier){
					String mod = ((Modifier)obj).toString();
					if(mod.equals("static")){
						isStatic = true;
						this.clone.setStatic(isStatic);;
					}
				}
			}
			if(node.getReturnType2() == null){
				// this is an constructor
				clone.setType("");
			}else{
				clone.setType(node.getReturnType2().toString());
			}
			
			// get thrown exceptions
			ArrayList<String> excps = new ArrayList<String>();
			for(Object obj: node.thrownExceptions()){
				if(obj instanceof Name){
					excps.add(((Name)obj).toString());
				}
			}
			clone.setExceptions(excps);
			
			ArrayList<Token> params = new ArrayList<Token>();
			for(Object obj : node.parameters()){
				SingleVariableDeclaration decl = (SingleVariableDeclaration)obj;
				String arg = decl.getName().getIdentifier();
				String type = decl.getType().toString();
				Token t = new Token(type, Label.LOCAL, arg);
				int line = getLineNumber(decl);
				t.start = line; // the parameter is alive starting from the line it is declared
				t.end = end - 1; // the parameter is dead at the end of the method
				vars.add(t);
				params.add(t);
			}
			clone.setParameters(params);
			
			node.getBody().accept(this);
			
			blockStacks.pop();
		}
		
		return false;
	}
	
	@Override
	public boolean visit(CatchClause node){
		int start = node.getStartPosition();
		int end = node.getLength() + start;
		
		if(start < clone.getX() || end > clone.getY()){
			// not in the clone
			return true;
		}else{
			// push this block to the stack
			int startLine = cu.getLineNumber(start);
			int endLine = cu.getLineNumber(end);
			blockStacks.push(new ImmutablePair<Integer, Integer>(startLine, endLine));
			
			if(inBlock){
				// nested block, do nothing
				node.getException().accept(this);
				node.getBody().accept(this);
			}else{
				// the outer-most block
				inBlock = true;
				SingleVariableDeclaration decl = node.getException();
				Block block = node.getBody();
				if(decl != null){
					decl.accept(this);
				}
				
				if(block != null){
					block.accept(this);
				}
				inBlock = false;
			}
			
			blockStacks.pop();
		}
		
		return false;
	}
	
	@Override
	public boolean visit(IfStatement node){
		int start = node.getStartPosition();
		int end = node.getLength() + start;
		
		if(start < clone.getX() || end > clone.getY()){
			// not in the clone
			return true;
		}else{
			// push this block to the stack
			int startLine = cu.getLineNumber(start);
			int endLine = cu.getLineNumber(end);
			blockStacks.push(new ImmutablePair<Integer, Integer>(startLine, endLine));
						
			if(inBlock){
				// nested block, do nothing
				Expression expr = node.getExpression();
				Statement thenS = node.getThenStatement();
				Statement elseS = node.getElseStatement();
				expr.accept(this);
				if(thenS != null){
					thenS.accept(this);
				}
				if(elseS != null){
					elseS.accept(this);
				}
			}else{
				// the outer-most block
				inBlock = true;
				Expression expr = node.getExpression();
				Statement thenS = node.getThenStatement();
				Statement elseS = node.getElseStatement();
				expr.accept(this);
				if(thenS != null){
					thenS.accept(this);
				}
				if(elseS != null){
					elseS.accept(this);
				}
				inBlock = false;
			}
			
			blockStacks.pop();
			
			return false;
		}
	}
	
	@Override
	public boolean visit(WhileStatement node){
		int start = node.getStartPosition();
		int end = node.getLength() + start;
		
		if(start < clone.getX() || end > clone.getY()){
			// not in the clone
			return true;
		}else{
			// push this block to the stack
			int startLine = cu.getLineNumber(start);
			int endLine = cu.getLineNumber(end);
			blockStacks.push(new ImmutablePair<Integer, Integer>(startLine, endLine));
			
			if(inBlock){
				// nested block, do nothing
				Expression expr = node.getExpression();
				Statement body = node.getBody();
				expr.accept(this);
				body.accept(this);
			}else{
				// the outer-most block
				inBlock = true;
				Expression expr = node.getExpression();
				Statement body = node.getBody();
				expr.accept(this);
				body.accept(this);
				inBlock = false;
			}
			
			blockStacks.pop();
			
			return false;
		}
	}
	
	@Override
	public boolean visit(DoStatement node){
		int start = node.getStartPosition();
		int end = node.getLength() + start;
		
		if(start < clone.getX() || end > clone.getY()){
			// not in the clone
			return true;
		}else{
			// push this block to the stack
			int startLine = cu.getLineNumber(start);
			int endLine = cu.getLineNumber(end);
			blockStacks.push(new ImmutablePair<Integer, Integer>(startLine, endLine));
						
			if(inBlock){
				// nested block, do nothing
				node.getExpression().accept(this);
				node.getBody().accept(this);
			}else{
				// the outer-most block
				inBlock = true;
				Expression expr = node.getExpression();
				Statement body = node.getBody();
				expr.accept(this);
				body.accept(this);
				inBlock = false;
			}
			
			blockStacks.pop();
		}
		
		return false;
	}
	
	@Override
	public boolean visit(EnhancedForStatement node){
		int start = node.getStartPosition();
		int end = node.getLength() + start;
		
		if(start < clone.getX() || end > clone.getY()){
			// not in the clone
			return true;
		}else{
			// push this block to the stack
			int startLine = cu.getLineNumber(start);
			int endLine = cu.getLineNumber(end);
			blockStacks.push(new ImmutablePair<Integer, Integer>(startLine, endLine));
						
			if(inBlock){
				// nested block, do nothing
				node.getParameter().accept(this);
				node.getExpression().accept(this);
				node.getBody().accept(this);
			}else{
				// the outer-most block
				inBlock = true;
				node.getParameter().accept(this);
				node.getExpression().accept(this);
				node.getBody().accept(this);
				inBlock = false;
			}
			
			blockStacks.pop();
		}

		return false;
	}
	
	@Override
	public boolean visit(ForStatement node){
		int start = node.getStartPosition();
		int end = node.getLength() + start;
		
		if(start < clone.getX() || end > clone.getY()){
			// not in the clone
			return true;
		}else{
			// push this block to the stack
			int startLine = cu.getLineNumber(start);
			int endLine = cu.getLineNumber(end);
			blockStacks.push(new ImmutablePair<Integer, Integer>(startLine, endLine));
						
			if(inBlock){
				// nested block, do nothing
				List<Expression> inits = node.initializers();
				Expression expr = node.getExpression();
				Statement body = node.getBody();
				if(inits != null){
					for(Expression e : inits){
						e.accept(this);
					}
				}
				expr.accept(this);
				body.accept(this);
			}else{
				// the outer-most block
				inBlock = true;
				List<Expression> inits = node.initializers();
				Expression expr = node.getExpression();
				Statement body = node.getBody();
				if(inits != null){
					for(Expression e : inits){
						e.accept(this);
					}
				}
				expr.accept(this);
				body.accept(this);
				inBlock = false;
			}
			
			blockStacks.pop();
		}

		return false;
	}
	
	public boolean visit(TryStatement node){
		int start = node.getStartPosition();
		int end = node.getLength() + start;
		
		if(start < clone.getX() || end > clone.getY()){
			// not in the clone
			return true;
		}else{
			// push this block to the stack
			int startLine = cu.getLineNumber(start);
			int endLine = cu.getLineNumber(end);
			blockStacks.push(new ImmutablePair<Integer, Integer>(startLine, endLine));
			
			if(inBlock){
				// nested block, do nothing
				Block block = node.getBody();
				List<CatchClause> catches = node.catchClauses();
				Block fnl = node.getFinally();
				block.accept(this);
				for(CatchClause cc : catches){
					cc.accept(this);
				}
				
				if(fnl != null){
					fnl.accept(this);
				}
			}else{
				// the outer-most block
				inBlock = true;
				Block block = node.getBody();
				List<CatchClause> catches = node.catchClauses();
				Block fnl = node.getFinally();
				block.accept(this);
				for(CatchClause cc : catches){
					cc.accept(this);
				}
				
				if(fnl != null){
					fnl.accept(this);
				}
				inBlock = false;
			}
			
			blockStacks.pop();
		}
	
		return false;
	}
	
	@Override
	public boolean visit(SingleVariableDeclaration node){
		String name = node.getName().getIdentifier();
		String type = node.getType().toString();
		Token t = new Token(type, Label.LOCAL, name);
		int line = getLineNumber(node);
		t.start = line;
		t.end = blockStacks.peek().getRight() - 1;
		defs.add(t);
		vars.add(t);
		
		if(inBlock){
			vars2.add(t);
		}
		
		Expression expr = node.getInitializer();
		if(expr != null){
			expr.accept(this);
		}
		
		return false;
	}
	
	@Override
	public boolean visit(VariableDeclarationStatement node){
		int start = node.getStartPosition();
		int end = node.getLength() + start;
		String type = node.getType().toString();
		
		boolean isFinal = false;
		boolean isPrivate = false;
		// check if it is final and private
		List mods = node.modifiers();
		for(Object obj : mods){
			if(obj instanceof Modifier){
				String mod = ((Modifier)obj).toString();
				if(mod.equals("final")){
					isFinal = true;
				}
			}
		}
		
		Iterator<?> iter = node.fragments().iterator();
		while(iter.hasNext()){
			VariableDeclarationFragment f = (VariableDeclarationFragment)iter.next();
			
			boolean isInit = false;
			Expression expr = f.getInitializer();
			if(expr != null){
				// still need to visit initializer first before adding this variable to vars
				expr.accept(this);
				isInit = true;
			}
			
			SimpleName var = f.getName();
			String name = var.getIdentifier();
			Token t = new Token(type, Label.LOCAL, name, isFinal, isPrivate, isInit);
			int line = getLineNumber(node);
			t.start = line;
			t.end = blockStacks.peek().getRight() - 1;
			vars.add(t);
			
			if((clone.getX() <= start ) && (end <= clone.getY())){
				// this local variable is defined in the clone 
				defs.add(t);
				effects.add(t);
				
				if(inBlock){
					vars2.add(t);
				}
			}
		}
	
		return false;
	}
	
	@Override
	public boolean visit(VariableDeclarationExpression node){
		int start = node.getStartPosition();
		int end = node.getLength() + start;
		String type = node.getType().toString();
		
		boolean isFinal = false;
		boolean isPrivate = false;
		// check if it is final
		List mods = node.modifiers();
		for(Object obj : mods){
			if(obj instanceof Modifier){
				String mod = ((Modifier)obj).toString();
				if(mod.equals("final")){
					isFinal = true;
				}
			}
		}
		
		Iterator<?> iter = node.fragments().iterator();
		while(iter.hasNext()){
			VariableDeclarationFragment f = (VariableDeclarationFragment)iter.next();
			
			boolean isInit = false;
			Expression expr = f.getInitializer();
			if(expr != null){
				// still need to visit initializer first before adding this variable to vars
				expr.accept(this);
				isInit = true;
			}
			
			SimpleName var = f.getName();
			String name = var.getIdentifier();
			Token t = new Token(type, Label.LOCAL, name, isFinal, isPrivate, isInit);
			int line = getLineNumber(node);
			t.start = line;
			t.end = blockStacks.peek().getRight() - 1;
			vars.add(t);
			
			if((clone.getX() <= start ) && (end <= clone.getY())){
				// this local variable is defined in the clone 
				defs.add(t);
				effects.add(t);
				
				if(inBlock){
					vars2.add(t);
				}
			}
		}
	
		return false;
	}
	
	@Override
	public boolean visit(MethodInvocation node){
		int start = node.getStartPosition();
		int end = node.getLength() + start;
		
		if((clone.getX() <= start) && (end <= clone.getY())){
			Expression expr = node.getExpression();
			if(expr != null){
				isInvoked = true;
				expr.accept(this);
			}else{
				String name = node.getName().toString();
				if(!name.equals(clone.getMethod())){
					// skip recursive calls
					callstack.add(name);
				}
			}
			
			isInvoked = false;
			List args = node.arguments();
			if(args != null){
				for(Object arg : args){
					if(arg instanceof ASTNode){
						isArgument = true;
						((ASTNode) arg).accept(this);
						isArgument = false;
					}
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
		int start = node.getStartPosition();
		int end = node.getLength() + start;
		int line = getLineNumber(node);
		
		if((clone.getX() <= start ) && (end <= clone.getY())){
			if(node.getExpression().toString().equals("this")){
				String name = node.getName().getIdentifier();
				for(Token f : fields){
					if(f.getName().equals(name)){
						if(isAssignmentLeftHand){
							f.start = line < f.start ? line : f.start;
							f.end = line > f.end ? line : f.end ;
							defs.add(f);
							effects.add(f);
						}else{
							f.start = line < f.start ? line : f.start;
							f.end = line > f.end ? line : f.end ;
							uses.add(f);
							if(!defs.contains(f) && !(f.isFinal() && f.isInitialized())){
								wilds.add(f);
							}
						}
						
						break;
					}
				}
			}else{
				Expression expr = node.getExpression();
				expr.accept(this);
			}
		}
		
		return false;
	}
	
	@Override
	public boolean visit(SimpleName node){
		int start = node.getStartPosition();
		int end = node.getLength() + start;
		String name = node.getIdentifier();
		int line = getLineNumber(node);
		
		if((clone.getX() <= start ) && (end <= clone.getY())){
			Token t = overshadow(name);
			if(t != null){
				if(isAssignmentLeftHand){
					// it is redefined
					t.start = line < t.start ? line : t.start;
					t.end = line > t.end ? line : t.end ;
					//defs.add(t);
					effects.add(t);
				}else{
					t.start = line < t.start ? line : t.start;
					t.end = line > t.end ? line : t.end ;
					uses.add(t);
					if(isInvoked){
						effects.add(t);
					}else if (isArgument && t.isField()){
						// if a field is passed as an argument to a method call, the field is likely to be modified.
						effects.add(t);
					}
					
					// find path to this usage
					ArrayList<String> path = findPath(node, new ArrayList<String>());
					t.addUsage(path);
					
					if(!defs.contains(t) && !(t.isFinal() && t.isInitialized())){
						wilds.add(t);
					}
				}
			}
		}
		
		return false;
	}
	
	private ArrayList<String> findPath(ASTNode node, ArrayList<String> p){
		ArrayList<String> path = new ArrayList<String>(p);
		
		if(node == null || node instanceof MethodDeclaration || 
				node.getStartPosition() < clone.getX() || 
				node.getStartPosition() + node.getLength() - 1 > clone.getY()){
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
	
	public Token overshadow(String name){
		Token t = null;
		// check local variables from back to front
		for(int i = vars.size() - 1; i >= 0; i --){
			t = vars.get(i);
			if(t.getName().equals(name)){
				return t;
			}
		}
		
		// check fields from back to front
		for(int i = fields.size() - 1; i >= 0; i --){
			t = fields.get(i);
			if(t.getName().equals(name)){
				return t;
			}
		}
		
		return null;
	}
	
	public HashSet<Token> getWildVars(){
		return this.wilds;
	}
	
	public HashSet<Token> getUses(){
		return this.uses;
	}
	
	public static void main(String[] args) throws IOException{
		CloneParser cp = new CloneParser();
		CompilationUnit cu = cp.parse("/home/troy/SysAssure/dataset/Ant0.0/src/main/org/apache/tools/ant/types/PatternSet.java");
		Clone clone = new Clone(5831, 6159, "setIncludes", "PatternSet");
		CloneVisitor cv = new CloneVisitor(clone, cu, "/home/troy/SysAssure/dataset/Ant0.0/src/main/org/apache/tools/ant/types/PatternSet.java");
		cu.accept(cv);
		for(Token t : cv.wilds){
			System.out.println(t.getLabel() + " - " + t.getType() + " " + t.getName());
		}
		
		System.out.println();
		for(Token t : cv.effects){
			System.out.println(t.getLabel() + " - " + t.getType() + " " + t.getName());
		}
	}
}

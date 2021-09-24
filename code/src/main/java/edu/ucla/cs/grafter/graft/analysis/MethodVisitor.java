package edu.ucla.cs.grafter.graft.analysis;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import edu.ucla.cs.grafter.graft.model.Label;
import edu.ucla.cs.grafter.graft.model.Token;

public class MethodVisitor extends ASTVisitor{
	public ArrayList<Constructor> constructors;
	public ArrayList<Constructor> getInstances;
	public ArrayList<Getter> getters;
	public ArrayList<Setter> setters;
	public String owner;
	
	public MethodVisitor(String clazz){
		this.constructors = new ArrayList<Constructor>();
		this.getInstances = new ArrayList<Constructor>();
		this.getters = new ArrayList<Getter>();
		this.setters = new ArrayList<Setter>();
		this.owner = clazz;
	}
	
	@Override
	public boolean visit(MethodDeclaration node){
		String name = node.getName().getIdentifier();
		 
		boolean isStatic = false;
		List mods = node.modifiers();
		for(Object obj : mods){
			if(obj instanceof Modifier){
				String mod = ((Modifier)obj).toString();
				if(mod.equals("private")){
					// it's pointless to consider a private method in the synthesis process, 
					// since we cannot even call this method outside
					return false;
				}else if(mod.equals("static")){
					isStatic = true;
				}
			}
		}
		
		// check the return type
		Type rt = node.getReturnType2();
		String return_type = rt != null? rt.toString(): "";
		
		ArrayList<Token> args = new ArrayList<Token>();
		for(Object obj : node.parameters()){
			SingleVariableDeclaration decl = (SingleVariableDeclaration)obj;
			String arg = decl.getName().getIdentifier();
			String type = decl.getType().toString();
			Token t = new Token(type, Label.LOCAL, arg);
			args.add(t);
		}
		
		if(node.isConstructor()){
			constructors.add(new Constructor(name, args));
		}else if(name.startsWith("get")){
			String type = node.getReturnType2().toString();
			getters.add(new Getter(name, new Token(type, Label.FIELD, name.replace("get", "")), return_type));
		}else if(name.startsWith("set")){
			// we don't actually care about its type
			setters.add(new Setter(name, new Token("", Label.FIELD, name.replace("set", "")), args));
		}else if(isStatic && return_type.equals(owner)){
			getInstances.add(new Constructor(name, args));
		}
		
		return false;
	}
}

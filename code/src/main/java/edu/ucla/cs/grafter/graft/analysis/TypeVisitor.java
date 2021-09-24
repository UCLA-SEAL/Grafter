package edu.ucla.cs.grafter.graft.analysis;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class TypeVisitor extends ASTVisitor{
	String name;
	public String superType;
	public ArrayList<String> interfaces = new ArrayList<String>();
	
	public TypeVisitor(String name){
		this.name = name;
		this.superType = null;
	}
	
	@Override
	public boolean visit(TypeDeclaration node){
		Type t = node.getSuperclassType();
		if(t != null && t.isSimpleType()){
			SimpleType st = (SimpleType)t;
			String fn = st.getName().getFullyQualifiedName();
			String[] ss = fn.split("\\.");
			String name = ss[ss.length - 1];
			this.superType = name;
		}
		
		List<Type> interfaces = node.superInterfaceTypes();
		if(interfaces != null){
			for(Type itf : interfaces){
				if(itf.isSimpleType()){
					SimpleType st = (SimpleType)itf;
					String fn = st.getName().getFullyQualifiedName();
					String[] ss = fn.split("\\.");
					String name = ss[ss.length - 1];
					this.interfaces.add(name);
				}
			}
		}
		
		return false;
	}
}

package edu.ucla.cs.grafter.graft.analysis;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import edu.ucla.cs.grafter.file.FileUtils;
import edu.ucla.cs.grafter.graft.model.Label;
import edu.ucla.cs.grafter.graft.model.Token;


public class FieldVisitor extends ASTVisitor{
	public HashSet<Token> fields;
	public HashMap<Token, String> decls;
	String name;
	String path;
	
	public FieldVisitor(String name, String path){
		fields = new HashSet<Token>();
		decls = new HashMap<Token, String>();
		this.name = name;
		this.path = path;
	}
	
	@Override
	public boolean visit(TypeDeclaration node){
		
		if(!node.getName().getIdentifier().equals(name)){
			return false;
		}
		
		// add fields in supertype first
		Type superType = node.getSuperclassType();
		if(superType != null){
			if(superType.isSimpleType()){
				SimpleType st = (SimpleType)superType;
				String fn = st.getName().getFullyQualifiedName();
				String[] ss = fn.split("\\.");
				String name = ss[ss.length - 1];
				File f = FileUtils.findFile(name + ".java");
				if(f != null){
					CloneParser cp = new CloneParser();
					try {
						CompilationUnit cu = cp.parse(f.getAbsolutePath());
						FieldVisitor cv = new FieldVisitor(name, f.getAbsolutePath());
						cu.accept(cv);
						// add all fields in supertype
						fields.addAll(cv.fields);
					} catch (IOException e) {
						e.printStackTrace();
						System.exit(-1);
					}
				}
			}
		}
		
		FieldDeclaration[] fds = node.getFields();
		if(fds != null){
			for(FieldDeclaration fd : fds){
				String type = fd.getType().toString();
				
				boolean isFinal = false;
				boolean isPrivate = false;
				boolean isStatic = false;
				boolean isProtected = false;
				// check if it is final and private
				List mods = fd.modifiers();
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
				
				Iterator<?> iter = fd.fragments().iterator();
				while(iter.hasNext()){
					VariableDeclarationFragment f = (VariableDeclarationFragment)iter.next();
					SimpleName var = f.getName();
					String name = var.getIdentifier();
					
					// check if the field is initialized
					boolean isInit = false;
					String init = null;
					if(f.getInitializer() != null){
						isInit = true;
						init = f.getInitializer().toString();
					}
					
					Token t = new Token(type, Label.FIELD, name, isFinal, isPrivate, isProtected, isInit);
					t.setInit(init);
					t.isStatic = isStatic;
					t.setPath(path);
					// overshadow fields in supertype, if any
					overshadow(t);
				}
			}
		}
		
		return true;
	}
	
	private void overshadow(Token t){
		for(Token f : fields){
			if(f.getName().equals(t.getName())){
				f.setType(t.getType());
				return;
			}
		}
		
		fields.add(t);
	}
}

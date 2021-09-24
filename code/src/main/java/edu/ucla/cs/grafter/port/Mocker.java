package edu.ucla.cs.grafter.port;

public class Mocker {
	
	public static String mock(String type){
		if(type.equals("int") || type.equals("Integer")){
			return "1";
		}else if(type.equals("byte") || type.equals("Byte")){
			return "1";
		}else if(type.equals("double") || type.equals("Double")){
			return "1.0";
		}else if(type.equals("float") || type.equals("Float")){
			return "1.0";
		}else if(type.equals("string") || type.equals("String")){
			return "\"abc\"";
		}else if(type.equals("char")){
			return "'a'";
		}else if(type.equals("boolean") || type.equals("Boolean")){
			return "false";
		}else{
			return "null";
		}
	}
}

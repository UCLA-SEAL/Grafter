package edu.ucla.cs.grafter.graft.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jdt.core.dom.CompilationUnit;

import edu.ucla.cs.grafter.graft.analysis.CloneParser;

public class Clone {
	String owner;
	String method;
	String type; // return type of the method
	ArrayList<Token> params; // the parameters of the method
	ArrayList<String> exceptions; // the exceptions thrown by the method
	int start; // line number of starting character
	int end; // line number of ending character
	int x; // index of starting character
	int y; // index of ending character
	List<Token> tokens;
	String code;
	Hook hook;
	String path;
	String test;
	boolean isStatic = false;
	
	public Clone(String clazz, String method, String code){
		this.owner = clazz;
		this.method = method;
		this.code = code;
	}
	
	public Clone(int x, int y, String method, String clazz){
		this.x = x;
		this.y = y;
		this.method = method;
		this.owner = clazz;
	}
	
	public Clone(String clazz, String method, String code, int x, int y, String path){
		this.owner = clazz;
		this.method = method;
		this.code = code;
		this.x = x;
		this.y = y;
		this.path = path;
	}
	
	public Clone(String clazz, String method, String code, int x, int y, String path, int start, int end){
		this.owner = clazz;
		this.method = method;
		this.code = code;
		this.x = x;
		this.y = y;
		this.path = path;
		this.start = start;
		this.end = end;
	}
	
	public Clone(String path, String m, int x, int y) throws IOException{
		this.path = path;
		this.method = m;
		this.x = x;
		this.y = y;
		this.owner = path.substring(path.lastIndexOf('/') + 1).replace(".java", "");
		this.code = read(path, x, y);
		this.start = getLineNumber(path, x);
		this.end = getLineNumber(path, y);
	}
	
	private String read(String path, int x, int y) throws IOException{
		CloneParser cp = new CloneParser();
		String code = cp.readFileToString(path);
		return code.substring(x,y);
	}
	
	private int getLineNumber(String path, int x) throws IOException{
		CloneParser cp = new CloneParser();
		CompilationUnit cu = cp.parse(path);
		return cu.getLineNumber(x);
	}
	
	public void setStatic(boolean b){
		this.isStatic = b;
	}
	
	public boolean isStatic(){
		return this.isStatic;
	}
	
	public String getCode(){
		return code;
	}
	
	public void setCode(String code){
		this.code = code;
	}
	
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner){
		this.owner = owner;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	public int getEnd() {
		return end;
	}
	public void setEnd(int end) {
		this.end = end;
	}
	public List<Token> getTokens() {
		return tokens;
	}
	public void setTokens(List<Token> tokens) {
		this.tokens = tokens;
	}
	public Hook getHook() {
		return hook;
	}
	public void setHook(Hook hook) {
		this.hook = hook;
	}
	
	public int getX(){
		return x;
	}
	
	public int getY(){
		return y;
	}
	
	public String getPath(){
		return path;
	}
	
	public void setTestPath(String s){
		this.test = s;
	}
	
	public String getTestPath(){
		return this.test;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public String getType(){
		return this.type;
	}
	
	public ArrayList<String> getExceptions(){
		return exceptions;
	}
	
	public void setExceptions(ArrayList<String> expts){
		this.exceptions = expts;
	}
	
	public ArrayList<Token> getParameters(){
		return this.params;
	}
	
	public void setParameters(ArrayList<Token> params){
		this.params = params;
	}
}

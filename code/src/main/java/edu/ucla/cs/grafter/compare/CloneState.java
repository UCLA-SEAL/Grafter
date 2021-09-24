package edu.ucla.cs.grafter.compare;

public class CloneState {
	public String name;
	public String value;
	public String test;
	public int index;
	
	public CloneState(String name, String value, String test, int index){
		this.name = name;
		this.value = value;
		this.test = test;
		this.index = index;
	}
	
	@Override
	public boolean equals(Object obj){
		if(!(obj instanceof CloneState)) return false;
		CloneState state = (CloneState)obj;
		return this.name.equals(state.name);
	}
	
	@Override
	public int hashCode(){
		int code = 37;
		code += 7 * name.hashCode();
		return code;
	}
}

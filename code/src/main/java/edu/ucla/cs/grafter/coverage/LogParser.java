package edu.ucla.cs.grafter.coverage;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.ucla.cs.grafter.file.FileUtils;

public class LogParser {
	final static String pattern = "^.*\\[Grafter\\]\\[Clone\\sGroup\\s\\d+\\]\\[Class\\s\\S+\\]\\[Range\\(\\d+,\\d+\\)\\]((\\S+)\\.)*\\S+$";
	final static String groupPattern = "\\[Clone\\sGroup\\s\\d+\\]";
	final static String clonePattern = "\\[Class\\s\\S+\\]\\[";
	final static String rangePattern = "\\[Range\\(\\d+,\\d+\\)\\]";
	final static String testPattern = "\\)\\]((\\S+)\\.)*\\S+$";
	
	HashMap<CloneTuple, HashSet<String>> map = new HashMap<CloneTuple, HashSet<String>>();
	
	public void parse(String file) throws IOException{
		String[] ss = FileUtils.readFileToArray(file);
		for(String s : ss){
			if(s.matches(pattern)){
				String group = match(groupPattern, s);
				int id = Integer.parseInt(group.substring(13, group.length() - 1));
				String clone = match(clonePattern, s);
				String clazz = clone.substring(7, clone.length() - 2);
				String range = match(rangePattern, s);
				int start = Integer.parseInt(range.substring(7, range.indexOf(',')));
				int end = Integer.parseInt(range.substring(range.indexOf(',') + 1, range.length() - 2));
				CloneTuple ct = new CloneTuple(id, clazz, start, end);
				String test = match(testPattern, s).substring(2);
				HashSet<String> tests;
				if(map.containsKey(ct)){
					tests = map.get(ct);
				}else{
					tests = new HashSet<String>();
				}
				tests.add(test);
				map.put(ct, tests);
			}
		}
	}
	
	private String match(String pat, String s){
		Pattern p = Pattern.compile(pat);
		Matcher m = p.matcher(s);
		if(m.find()){
			return m.group();
		}else{
			return "";
		}
	}
	
	public HashSet<String> findTests(int id, String clazz, int start, int end){
		CloneTuple ct = new CloneTuple(id, clazz, start, end);
		if(map.containsKey(ct)){
			return map.get(ct);
		}else{
			return new HashSet<String>();
		}
	}
	
	public static void main(String[] args){
		String file = "/home/troy/SysAssure/dataset/apache-ant-1.9.6/test_log.txt";
		LogParser lp = new LogParser();
		try {
			lp.parse(file);
			for(CloneTuple ct : lp.map.keySet()){
				System.out.println("Clone" + ct.id + ": " + ct.clazz + "(" + ct.start + "," + ct.end + ")");
				for(String test : lp.map.get(ct)){
					System.out.println(test);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

class CloneTuple{
	int id;
	String clazz;
	int start;
	int end;
	
	public CloneTuple(int id, String clazz, int start, int end){
		this.id = id;
		this.clazz = clazz;
		this.start = start;
		this.end = end;
	}
	
	@Override
	public boolean equals(Object o){
		if(o instanceof CloneTuple){
			CloneTuple other = (CloneTuple)o;
			return other.id == this.id && other.start == this.start && other.end == this.end && other.clazz.equals(this.clazz);
		}else{
			return false;
		}
	}
	
	@Override
	public int hashCode(){
		int result = 17;
		result = 31 * result + id;
		result = 31 * result + start;
		result = 31 * result + end;
		result = 31 * result + clazz.hashCode();
		
		return result;
	}
}

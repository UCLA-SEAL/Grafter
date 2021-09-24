package edu.ucla.cs.grafter.compare;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

import org.junit.Ignore;
import org.junit.Test;

import edu.ucla.cs.grafter.config.GrafterConfig;
import edu.ucla.cs.grafter.graft.model.Clone;
import edu.ucla.cs.grafter.graft.model.Label;
import edu.ucla.cs.grafter.graft.model.Token;
import edu.ucla.cs.grafter.port.Grafter;

public class TestStateInstrument {
	@Test
	@Ignore
	public void testInstrument(){
		GrafterConfig.config();
		
		String clone = "/home/troy/SysAssure/dataset/apache-ant-1.9.6/src/main/org/apache/tools/ant/types/PatternSet.java";
		int start = 289;
		int end = 299;
		HashSet<Token> vars = new HashSet<Token>();
		Token t1 = new Token("List<NameEntry>", Label.FIELD, "includeList", false, false, false);
		Token t2 = new Token("String", Label.LOCAL, "includes", false, false, false);
		vars.add(t1);
		vars.add(t2);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("includeList", "/home/troy/SysAssure/dataset/apache-ant-1.9.6/build/testcases/0_PatternSetTest/PatternSet_289_299/includeList");
		map.put("includes", "/home/troy/SysAssure/dataset/apache-ant-1.9.6/build/testcases/0_PatternSetTest/PatternSet_289_299/includes");
		
		StateCompare sc = new StateCompare();
		try {
			sc.instrument(clone, start, end, vars, map, false, true, false);
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	@Ignore
	public void testInstrumentAfterGrafting() throws IOException{
		GrafterConfig.config();
		
		String clone1 = "/home/troy/SysAssure/dataset/apache-ant-1.9.6/src/main/org/apache/tools/ant/types/PatternSet.java";
		int start1 = 289;
		int end1 = 299;
		String clone2 = "/home/troy/SysAssure/dataset/apache-ant-1.9.6/src/main/org/apache/tools/ant/types/PatternSet.java";
		int start2 = 307;
		int end2 = 317;
		
		Grafter grafter = new Grafter(clone1, start1, end1, clone2, start2,
				end2);

		// graft the clone2 to the clone1, please notice that the range of
		// grafted clone2 is changed as we insert stub code
		Clone clone_new = grafter.graft(true);

		// update the range of the grafted clone2
		int start1_new = clone_new.getStart();
		int end1_new = clone_new.getEnd();
		
		StateCompare sc = new StateCompare();
		HashSet<Token> vars = new HashSet<Token>();
		Token t = new Token("List<NameEntry>", Label.FIELD, "includeList", false, false, false);
		vars.add(t);
		
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("includeList", "/home/troy/SysAssure/dataset/apache-ant-1.9.6/build/testcases/1_PatternSetTest/PatternSet_289_299/excludeList");
		
		try {
			sc.instrument(clone1, start1_new, end1_new, vars, map, true, true, false);
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	@Ignore
	public void testInstrument2(){
		GrafterConfig.config();
		
		String clone = "/home/troy/SysAssure/dataset/apache-ant-1.9.6/src/main/org/apache/tools/ant/types/PatternSet.java";
		int start = 243;
		int end = 248;
		HashSet<Token> vars = new HashSet<Token>();
		Token t1 = new Token("List<NameEntry>", Label.FIELD, "includeList", false, false, false);
		Token t2 = new Token("Reference", Label.FIELD, "ref", false, false, false);
		vars.add(t1);
		vars.add(t2);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("includeList", "/home/troy/SysAssure/dataset/apache-ant-1.9.6/build/testcases/0_PatternSetTest/PatternSet_289_299/includeList");
		map.put("ref", "/home/troy/SysAssure/dataset/apache-ant-1.9.6/build/testcases/0_PatternSetTest/PatternSet_289_299/ref");
		
		StateCompare sc = new StateCompare();
		try {
			sc.instrument(clone, start, end, vars, map, false, true, false);
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	@Ignore
	public void testInstrumentAfterGrafting2() throws IOException{
		GrafterConfig.config();
		
		String clone1 = "/home/troy/SysAssure/dataset/apache-ant-1.9.6/src/main/org/apache/tools/ant/types/PatternSet.java";
		int start1 = 413;
		int end1 = 420;
		String clone2 = "/home/troy/SysAssure/dataset/apache-ant-1.9.6/src/main/org/apache/tools/ant/types/PatternSet.java";
		int start2 = 427;
		int end2 = 434;
		
		Grafter grafter = new Grafter(clone1, start1, end1, clone2, start2,
				end2);

		// graft the clone2 to the clone1, please notice that the range of
		// grafted clone2 is changed as we insert stub code
		Clone clone_new = grafter.graft(true);

		// update the range of the grafted clone2
		int start1_new = clone_new.getStart();
		int end1_new = clone_new.getEnd();
		
		StateCompare sc = new StateCompare();
		HashSet<Token> vars = new HashSet<Token>();
		Token t1 = new Token("List<NameEntry>", Label.FIELD, "includeList", false, false, false);
		vars.add(t1);
		Token t2 = new Token("Reference", Label.FIELD, "ref", false, false, false);
		vars.add(t2);
		
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("includeList", "/home/troy/SysAssure/dataset/apache-ant-1.9.6/build/testcases/0_PatternSetTest/PatternSet_265_270/excludeList");
		map.put("ref", "/home/troy/SysAssure/dataset/apache-ant-1.9.6/build/testcases/0_PatternSetTest/PatternSet_265_270/ref");
		
		try {
			sc.instrument(clone1, start1_new, end1_new, vars, map, true, true, false);
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
	}
}

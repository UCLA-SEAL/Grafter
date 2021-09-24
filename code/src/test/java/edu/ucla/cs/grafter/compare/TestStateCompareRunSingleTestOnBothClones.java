package edu.ucla.cs.grafter.compare;

import java.util.HashSet;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Ignore;
import org.junit.Test;

import edu.ucla.cs.grafter.config.GrafterConfig;

public class TestStateCompareRunSingleTestOnBothClones {

	@Test
	@Ignore
	public void test1(){
		GrafterConfig.config();
		
		HashSet<ImmutablePair<String, String>> tset = new HashSet<ImmutablePair<String, String>>();
		tset.add(new ImmutablePair<String, String>(
						"org.apache.tools.ant.types.PatternSetTest",
						"testEmptyElementIfIsReference"));
		
		String clone1 = "/home/troy/SysAssure/dataset/apache-ant-1.9.6/src/main/org/apache/tools/ant/types/PatternSet.java";
		int start1 = 289;
		int end1 = 299;
		String clone2 = "/home/troy/SysAssure/dataset/apache-ant-1.9.6/src/main/org/apache/tools/ant/types/PatternSet.java";
		int start2 = 307;
		int end2 = 317;
		
		StateCompare sc = new StateCompare();
		sc.runMVN(tset, clone1, "PatternSet", start1, end1, clone2, "PatternSet", start2, end2, true);
	}
	
	@Test
	@Ignore
	public void test2(){
		GrafterConfig.config();
		
		HashSet<ImmutablePair<String, String>> tset = new HashSet<ImmutablePair<String, String>>();
		tset.add(new ImmutablePair<String, String>(
						"org.apache.tools.ant.types.PatternSetTest",
						"testCircularReferenceCheck"));
		
		String clone1 = "/home/troy/SysAssure/dataset/apache-ant-1.9.6/src/main/org/apache/tools/ant/types/PatternSet.java";
		int start1 = 289;
		int end1 = 299;
		String clone2 = "/home/troy/SysAssure/dataset/apache-ant-1.9.6/src/main/org/apache/tools/ant/types/PatternSet.java";
		int start2 = 307;
		int end2 = 317;
		
		StateCompare sc = new StateCompare();
		sc.runMVN(tset, clone1, "PatternSet", start1, end1, clone2, "PatternSet", start2, end2, true);
	}
	
	@Test
	@Ignore
	public void test3(){
		GrafterConfig.config();
		
		HashSet<ImmutablePair<String, String>> tset = new HashSet<ImmutablePair<String, String>>();
		tset.add(new ImmutablePair<String, String>(
						"org.apache.tools.ant.types.PatternSetTest",
						"testNestedPatternset"));
		
		String clone1 = "/home/troy/SysAssure/dataset/apache-ant-1.9.6/src/main/org/apache/tools/ant/types/PatternSet.java";
		int start1 = 289;
		int end1 = 299;
		String clone2 = "/home/troy/SysAssure/dataset/apache-ant-1.9.6/src/main/org/apache/tools/ant/types/PatternSet.java";
		int start2 = 307;
		int end2 = 317;
		
		StateCompare sc = new StateCompare();
		sc.runMVN(tset, clone1, "PatternSet", start1, end1, clone2, "PatternSet", start2, end2, true);
	}
	
	@Test
	@Ignore
	public void test4(){
		GrafterConfig.config();
		
		HashSet<ImmutablePair<String, String>> tset = new HashSet<ImmutablePair<String, String>>();
		tset.add(new ImmutablePair<String, String>(
						"org.apache.tools.ant.taskdefs.AntLikeTasksAtTopLevelTest",
						"testSubant"));
		
		String clone1 = "/home/troy/SysAssure/dataset/apache-ant-1.9.6/src/main/org/apache/tools/ant/types/PatternSet.java";
		int start1 = 243;
		int end1 = 248;
		String clone2 = "/home/troy/SysAssure/dataset/apache-ant-1.9.6/src/main/org/apache/tools/ant/types/PatternSet.java";
		int start2 = 265;
		int end2 = 270;
		
		StateCompare sc = new StateCompare();
		sc.runMVN(tset, clone1, "PatternSet", start1, end1, clone2, "PatternSet", start2, end2, true);
	}
}

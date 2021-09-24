package edu.ucla.cs.grafter.compare;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Ignore;
import org.junit.Test;

import edu.ucla.cs.grafter.config.GrafterConfig;
import edu.ucla.cs.grafter.graft.model.Clone;
import edu.ucla.cs.grafter.graft.model.Label;
import edu.ucla.cs.grafter.graft.model.Token;
import edu.ucla.cs.grafter.port.Grafter;

public class TestStateCompareRunSingleTestOnSingleClone {
	@Test
	@Ignore
	public void testRunOnLeftUnGraft() {
		GrafterConfig.config();

		String clone1 = "/home/troy/SysAssure/dataset/apache-ant-1.9.6/src/main/org/apache/tools/ant/types/PatternSet.java";
		int start1 = 289;
		int end1 = 299;
		HashSet<Token> vars = new HashSet<Token>();
		Token t = new Token("List<NameEntry>", Label.FIELD, "includeList",
				false, false, false);
		vars.add(t);
		HashMap<String, String> matches = new HashMap<String, String>();
		matches.put("includeList", "excludeList");

		StateCompare sc = new StateCompare();
		sc.runMVN(clone1, "PatternSet", start1, end1, vars,
				new ImmutablePair<String, String>(
						"org.apache.tools.ant.types.PatternSetTest",
						"testEmptyElementIfIsReference"), matches, false, true, true, false, start1, end1);
	}

	@Test
	@Ignore
	public void testRunOnRightGraft() throws IOException {
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
		Token t = new Token("List<NameEntry>", Label.FIELD, "includeList",
				false, false, false);
		vars.add(t);
		HashMap<String, String> matches = new HashMap<String, String>();
		matches.put("includeList", "excludeList");

		sc.runMVN(clone1, "PatternSet", start1_new, end1_new, vars,
				new ImmutablePair<String, String>(
						"org.apache.tools.ant.types.PatternSetTest",
						"testEmptyElementIfIsReference"), matches, true, false, true, false, start2, end2);
	}
}

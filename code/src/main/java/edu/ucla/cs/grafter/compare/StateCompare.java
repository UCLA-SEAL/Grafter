package edu.ucla.cs.grafter.compare;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.JOptionPane;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.ThrowStatement;
import edu.ucla.cs.grafter.config.BuildTool;
import edu.ucla.cs.grafter.config.GrafterConfig;
import edu.ucla.cs.grafter.file.FileUtils;
import edu.ucla.cs.grafter.graft.analysis.CloneCalibrator;
import edu.ucla.cs.grafter.graft.analysis.ReturnTypeFinder;
import edu.ucla.cs.grafter.graft.analysis.StatementFinder;
import edu.ucla.cs.grafter.graft.model.Clone;
import edu.ucla.cs.grafter.graft.model.Matches;
import edu.ucla.cs.grafter.graft.model.Token;
import edu.ucla.cs.grafter.instrument.JavaParser;
import edu.ucla.cs.grafter.port.Grafter;
import edu.ucla.cs.grafter.process.AntTestRunner;
import edu.ucla.cs.grafter.process.TestRunner;
import edu.ucla.cs.grafter.ui.CloneTreeNode;
import edu.ucla.cs.grafter.ui.GroupTreeNode;
import edu.ucla.cs.grafter.ui.TestTreeNode;

public class StateCompare extends BehaviorCompare {
	static final String template = "/home/troy/SysAssure/code/Grafter/Grafter/src/main/resources/template/GrafterSerializer.template";
	boolean debug = true;

	// test set for clone1
	HashSet<ImmutablePair<String, String>> tset1 = new HashSet<ImmutablePair<String, String>>();
	// test set for clone2
	HashSet<ImmutablePair<String, String>> tset2 = new HashSet<ImmutablePair<String, String>>();
	

	HashMap<CloneState, CloneState> result = new HashMap<CloneState, CloneState>();
	HashMap<String, String> diffs = new HashMap<String, String>(); // keep track
																	// of the
																	// diverged
																	// state

	public StateCompare() {
	}

	public StateCompare(GroupTreeNode gtn) {
		super(gtn);

		// collect tests separately for the pair of clones
		CloneTreeNode ctn1 = (CloneTreeNode) gtn.getChildAt(0);
		CloneTreeNode ctn2 = (CloneTreeNode) gtn.getChildAt(1);

		Enumeration enumerator = ctn1.children();
		while (enumerator.hasMoreElements()) {
			TestTreeNode ttn = (TestTreeNode) enumerator.nextElement();
			String testPath = ttn.getTestPath();
			String testClass = testPath.substring(
					GrafterConfig.test_dir.length(), testPath.length() - 5)
					.replace(File.separatorChar, '.');
			tset1.add(new ImmutablePair<String, String>(testClass, ttn
					.getTestName()));
		}
		enumerator = ctn2.children();
		while (enumerator.hasMoreElements()) {
			TestTreeNode ttn = (TestTreeNode) enumerator.nextElement();
			String testPath = ttn.getTestPath();
			String testClass = testPath.substring(
					GrafterConfig.test_dir.length(), testPath.length() - 5)
					.replace(File.separatorChar, '.');
			tset2.add(new ImmutablePair<String, String>(testClass, ttn
					.getTestName()));
		}
	}

	public boolean compare() {
		// add the grafter folder in the target project for the serialization
		File gdir = new File(GrafterConfig.root_dir + "grafter");
		if (!gdir.exists()) {
			gdir.mkdir();
		}

		// run the tests on the left clone and serialize the intermediate
		// results of both clones to the disk
		boolean result;
		if (GrafterConfig.build == BuildTool.Ant) {
			result = runANT(tset1, file1, className1, startLine1, endLine1,
					file2, className2, startLine2, endLine2, true);
		} else {
			result = runMVN(tset1, file1, className1, startLine1, endLine1,
					file2, className2, startLine2, endLine2, true);
		}
		if (!result) {
			// clean the temporary folder
			FileUtils.delete(gdir.getAbsolutePath());

			return false;
		}

		// run the tests on the right clone and serialize the intermediate
		// results of both clones to the disk
		if (GrafterConfig.build == BuildTool.Ant) {
			result = runANT(tset2, file2, className2, startLine2, endLine2,
					file1, className1, startLine1, endLine1, false);
		} else {
			result = runMVN(tset2, file2, className2, startLine2, endLine2,
					file1, className1, startLine1, endLine1, false);
		}

		if (!result) {
			// clean the temporary folder
			FileUtils.delete(gdir.getAbsolutePath());

			return false;
		}

		// read the serialized states and compare
		boolean comparison = gatherAndCompare();

		// clean the temporary folder
		FileUtils.delete(gdir.getAbsolutePath());
		
		// check for the rest of bak files
		HashSet<File> bakFiles = FileUtils.findFilesWithExtension(".bak", new File(GrafterConfig.src_dir));
		for(File file : bakFiles){
			String pathToModifiedFile = file.getAbsolutePath().replace(".bak", ""); 
			new File(pathToModifiedFile).delete();
			file.renameTo(new File(pathToModifiedFile));
		}

		if (comparison) {
			if(GrafterConfig.batch){
				for(CloneState cs1 : this.result.keySet()){
					CloneState cs2 = this.result.get(cs1);
					if(cs1.value == null) cs1.value = "null";
					if(cs2.value == null) cs2.value = "null";
					if(!cs1.value.equalsIgnoreCase(cs2.value)){
						System.out.println("The program state in [" + cs1.name + "," + cs2.name + "] kills the mutant.");
					}
	            }
			}else{
				showComparisonTable();
			}
			
			return true;
		} else {
			return false;
		}
	}
	
	boolean gatherAndCompare() {
		HashMap<String, String> matches = new HashMap<String, String>();
		HashMap<String, ArrayList<String>> state1 = new HashMap<String, ArrayList<String>>();
		HashMap<String, ArrayList<String>> state2 = new HashMap<String, ArrayList<String>>();

		// get the match between clone1 and clone2
		Grafter grafter;
		try {
			grafter = new Grafter(file1, startLine1, endLine1, file2,
					startLine2, endLine2);
			Matches ms = grafter.state_level_match();
			for (Token t : ms.getAllOrigin()) {
				if ((t.isField() && t.isFinal()) || (grafter.orign.isStatic() && t.isField() && !t.isStatic)) {
					// There are two cases we don't need to compare the state
					// 1. the variable/field is final, since grafter cannot update it anyway
					// 2. the field is non-static but the containing method of the clone is static, since non-static variable cannot be accessed in a static context
					continue; 
				}
				
				if (t.isField() && !t.isStatic) {
					matches.put("this." + t.getName(), "this."
							+ ms.getClone(t).getName());
				} else {
					matches.put(t.getName(), ms.getClone(t).getName());
				}
			}
			
			// compare the return value if any
			if(grafter.orign.getCode().contains("return ") && grafter.clone.getCode().contains("return ")){
				matches.put("tmp_return_value", "tmp_return_value");
			}
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane
					.showMessageDialog(null,
							"Exception happens when preparing the grafter, e.g., matching two clones.");
			return false;
		}

		File root = new File(GrafterConfig.root_dir + "grafter");

		if (!root.exists())
			return false;

		File[] dirs = root.listFiles();
		Arrays.sort(dirs);
		for (File testDir : dirs) {
			if (testDir.isDirectory()) {
				String[] s = testDir.getAbsolutePath().split(File.separator);
				String test = s[s.length - 1];
				File[] cDirs = testDir.listFiles();
				if (cDirs.length == 2) {
					for (File cloneDir : cDirs) {
						if (cloneDir.isDirectory()) {
							String[] ss = cloneDir.getAbsolutePath().split(
									File.separator);
							if (ss[ss.length - 1].contains(className1 + "_"
									+ startLine1 + "_" + endLine1)) {
								state1 = gatherStates(cloneDir
										.getAbsolutePath());
							} else {
								state2 = gatherStates(cloneDir
										.getAbsolutePath());
							}
						}
					}

					compare(state1, state2, matches, test);
				} else {
					if(GrafterConfig.batch){
						System.out.println(cDirs.length + " dirs in the test directory "
								+ testDir.getAbsolutePath()
								+ ", should be only two.");
					}else{
						JOptionPane.showMessageDialog(null,
								cDirs.length + " dirs in the test directory "
										+ testDir.getAbsolutePath()
										+ ", should be only two.");
					}
					return false;
				}
			}
		}

		return true;
	}

	private void compare(HashMap<String, ArrayList<String>> state1,
			HashMap<String, ArrayList<String>> state2,
			HashMap<String, String> matches, String test) {
		// do the comparison till find a state divergence
		for (String var : matches.keySet()) {
			if (diffs.keySet().contains(var))
				continue; // no need to compare as we have already detected
							// divergence on this variable

			String var2 = matches.get(var);
			if (state1.containsKey(var) && state2.containsKey(var2)) {
				ArrayList<String> l1 = state1.get(var);
				ArrayList<String> l2 = state2.get(var2);
				for (int i = 0; i < Math.min(l1.size(), l2.size()); i++) {
					String s1 = l1.get(i);
					String s2 = l2.get(i);
					if (s1 != null && s2 != null) {
						CloneState cs1 = new CloneState(var, s1, test, i);
						CloneState cs2 = new CloneState(var2, s2, test, i);
						result.remove(cs1);
						result.put(cs1, cs2);
						if (!s1.equalsIgnoreCase(s2)) {
							diffs.put(var, test + "_" + i);
							break; // divergence, no need to compare again
						}
					} else if (s1 == null && s2 != null) {
						CloneState cs1 = new CloneState(var, "null", test, i);
						CloneState cs2 = new CloneState(var2, s2, test, i);
						result.remove(cs1);
						result.put(cs1, cs2);
						diffs.put(var, test + "_" + i);
						break; // divergence, no need to compare again
					} else if (s1 != null && s2 == null) {
						CloneState cs1 = new CloneState(var, s1, test, i);
						CloneState cs2 = new CloneState(var2, "null", test, i);
						result.remove(cs1);
						result.put(cs1, cs2);
						diffs.put(var, test + "_" + i);
						break; // divergence, no need to compare again
					}
				}
			} else if (!state1.containsKey(var) && state2.containsKey(var2)) {
				CloneState cs1 = new CloneState(var, "no value", test, -1);
				CloneState cs2 = new CloneState(var2, state2.get(var2).get(0),
						test, -1);
				result.remove(cs1);
				result.put(cs1, cs2);
				diffs.put(var, test + "_0"); // divergence, no need to compare
												// again
			} else if (state1.containsKey(var) && !state2.containsKey(var2)) {
				CloneState cs1 = new CloneState(var, state1.get(var).get(0),
						test, -1);
				CloneState cs2 = new CloneState(var2, "no value", test, -1);
				result.remove(cs1);
				result.put(cs1, cs2);
				diffs.put(var, test + "_0"); // divergence, no need to compare
												// again
			} else {
				if (!result.containsKey(new CloneState(var, "", "", 0))) {
					result.put(new CloneState(var, "no value", test, -1),
							new CloneState(var2, "no value", test, -1));
				}
			}

			if (diffs.size() == matches.size()) {
				// find divergences on all variables, no need to do more
				// compares
				break;
			}
		}
	}

	public void showComparisonTable() {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				StateCompareView tcv = new StateCompareView(className1 + "("
						+ startLine1 + "，" + endLine1 + ")", className2 + "("
						+ startLine2 + "，" + endLine2 + ")", result);
				tcv.createAndShowGUI();
			}
		});
	}

	private Matches cur_ms = null;

	public boolean runANT(HashSet<ImmutablePair<String, String>> tset,
			String clone1, String cName1, int start1, int end1, String clone2,
			String cName2, int start2, int end2, boolean isLeft) {
		Grafter grafter;
		try {
			grafter = new Grafter(clone1, start1, end1, clone2, start2, end2);
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane
					.showMessageDialog(null,
							"Exception happens when preparing the grafter, e.g., matching two clones.");
			return false;
		}

		cur_ms = grafter.state_level_match();
		HashMap<String, String> matches = new HashMap<String, String>();
		for (Token t : cur_ms.getAllOrigin()) {
			// use ''this'' to access non-static fields
			if (t.isField() && !t.isStatic) {
				matches.put("this." + t.getName(), "this."
						+ cur_ms.getClone(t).getName());
			} else {
				matches.put(t.getName(), cur_ms.getClone(t).getName());
			}
		}
		
		if(grafter.orign.getCode().contains("return ") && grafter.clone.getCode().contains("return ")){
			matches.put("tmp_return_value", "tmp_return_value");
		}

		// group the test cases based on the test classes they belong to
		HashMap<String, HashSet<String>> tmap = new HashMap<String, HashSet<String>>();
		for (ImmutablePair<String, String> test : tset) {
			String tClass = test.getLeft();
			String tMethod = test.getRight();
			HashSet<String> set;
			if (tmap.containsKey(tClass)) {
				set = tmap.get(tClass);
			} else {
				set = new HashSet<String>();
			}

			set.add(tMethod);
			tmap.put(tClass, set);
		}

		boolean isFirstGraft = true;
		for (String tClass : tmap.keySet()) {
			boolean result = runANT(clone1, cName1, start1, end1,
					cur_ms.getAllOrigin(), tClass, tmap.get(tClass), matches,
					false, isLeft, grafter.isMethodEnd, grafter.orign.isStatic(), start1, end1);

			if (!result)
				return false;

			// graft the clone2 to the clone1, please notice that the range of
			// grafted clone2 is changed as we insert stub code
			Clone clone_new;
			try {
				clone_new = grafter.graft(isFirstGraft);
				isFirstGraft = false;
			} catch (IOException e) {
				e.printStackTrace();
				if(GrafterConfig.batch){
					System.out.println("Exception happens when grafting the clone");
				}else{
					JOptionPane.showMessageDialog(null,
							"Exception happens when grafting the clone");
				}
				
				return false;
			}

			deleteOutDatedClassFile(clone1);

			// compile the grafted file
			compile();

			// update the range of the grafted clone2
			int start1_new = clone_new.getStart();
			int end1_new = clone_new.getEnd();

			// run the test on clone1 and capture the intermediate result for
			// each execution of clone1
			result = runANT(clone1, cName2, start1_new, end1_new,
					cur_ms.getAllOrigin(), tClass, tmap.get(tClass), matches,
					true, isLeft, grafter.isMethodEnd, grafter.orign.isStatic(), start2, end2);

			if (!result)
				return false;
		}

		return true;
	}

	boolean runANT(String clone, String cName, int start, int end,
			HashSet<Token> vars, String tClass, HashSet<String> tset,
			HashMap<String, String> matches, boolean isGraft, boolean isLeft,
			boolean isMethodEnd, boolean isStatic, int os, int oe) {
		// create a folder in the root directory to store the serialized objects
		File grafterDir;
		if (isLeft) {
			grafterDir = new File(GrafterConfig.root_dir + "grafter"
					+ File.separator + "0_" + tClass);
		} else {
			grafterDir = new File(GrafterConfig.root_dir + "grafter"
					+ File.separator + "1_" + tClass);
		}

		grafterDir.mkdirs();

		// create the clone directory in the test directory
		File cloneDir = new File(grafterDir.getAbsolutePath() + File.separator
				+ cName + "_" + os + "_" + oe);
		cloneDir.mkdirs();

		// prepare the file path for each serialized object
		HashSet<Token> new_vars = new HashSet<Token>();
		HashMap<String, String> paths = new HashMap<String, String>();
		for (Token t : vars) {
			String var;
			if (t.isField() && !t.isStatic) {
				var = "this." + t.getName();
			} else {
				var = t.getName();
			}
			String path;
			if (isGraft) {
				path = cloneDir.getAbsolutePath() + File.separator
						+ matches.get(var);

				if (t.isLocalVariable()) {
					var = matches.get(var);
				}
			} else {
				path = cloneDir.getAbsolutePath() + File.separator + var;
			}

			Token new_t = t.clone(var);
			if (isGraft && t.isLocalVariable()) {
				new_t.graft_start = cur_ms.getClone(t).graft_start;
				new_t.graft_end = cur_ms.getClone(t).graft_end;
			}
			new_vars.add(new_t);

			paths.put(var, path);
		}

		// instrument the clone1
		try {
			instrument(clone, start, end, new_vars, paths, isGraft, isMethodEnd, isStatic);
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane
					.showMessageDialog(
							null,
							"Exception happens when instrumenting the clone to capture intermediate output for the state-level comparison");
			return false;
		}

		// compile the instrumented file
		compile();

		// run the test
		HashMap<String, Boolean> results = AntTestRunner
				.runAntTestClass(tClass);
		for (String m : tset) {
			if(results.containsKey(m)) {
				if(GrafterConfig.verbose){
					if (results.get(m)) {
						System.out.println(m + ":" + tClass + " succeeds.");
					} else {
						System.out.println(m + ":" + tClass + " fails.");
					}
				}
			}
		}

		// restore the instrumented file
		restore(clone);

		return true;
	}

	/**
	 * tset is the set of tests covers clone1 and we need to graft clone2 to
	 * clone1 to exercise clone2 with the same test
	 * 
	 * @param tset
	 * @param clone1
	 * @param start1
	 * @param end1
	 * @param clone2
	 * @param start2
	 * @param end2
	 * @return
	 * @throws IOException
	 */
	public boolean runMVN(HashSet<ImmutablePair<String, String>> tset,
			String clone1, String cName1, int start1, int end1, String clone2,
			String cName2, int start2, int end2, boolean isLeft) {
		Grafter grafter;
		try {
			grafter = new Grafter(clone1, start1, end1, clone2, start2, end2);
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane
					.showMessageDialog(null,
							"Exception happens when preparing the grafter, e.g., matching two clones.");
			return false;
		}

		cur_ms = grafter.state_level_match();
		HashMap<String, String> matches = new HashMap<String, String>();
		for (Token t : cur_ms.getAllOrigin()) {
			// use ''this.'' to access non-static fields
			if (t.isField() && !t.isStatic) {
				matches.put("this." + t.getName(), "this."
						+ cur_ms.getClone(t).getName());
			} else {
				matches.put(t.getName(), cur_ms.getClone(t).getName());
			}
		}
		
		if(grafter.orign.getCode().contains("return ") && grafter.clone.getCode().contains("return ")){
			matches.put("tmp_return_value", "tmp_return_value");
		}

		boolean isFirstGraft = true;
		for (ImmutablePair<String, String> test : tset) {
			// run the test on clone1 and capture the intermediate result for
			// each execution of clone1
			boolean result = runMVN(clone1, cName1, start1, end1,
					cur_ms.getAllOrigin(), test, matches, false, isLeft,
					grafter.isMethodEnd, grafter.orign.isStatic(), start1, end1);

			if (!result)
				return false;

			// graft the clone2 to the clone1, please notice that the range of
			// grafted clone2 is changed as we insert stub code
			Clone clone_new;
			try {
				clone_new = grafter.graft(isFirstGraft);
				isFirstGraft = false;
			} catch (IOException e) {
				e.printStackTrace();
				if(GrafterConfig.batch){
					System.out.println("Exception happens when grafting the clone");
				}else{
					JOptionPane.showMessageDialog(null,
							"Exception happens when grafting the clone");
				}
				
				return false;
			}

			deleteOutDatedClassFile(clone1);

			// compile the grafted file
			compile();

			// update the range of the grafted clone2
			int start1_new = clone_new.getStart();
			int end1_new = clone_new.getEnd();

			// run the test on clone1 and capture the intermediate result for
			// each execution of clone1
			result = runMVN(clone1, cName2, start1_new, end1_new,
					cur_ms.getAllOrigin(), test, matches, true, isLeft,
					grafter.isMethodEnd, grafter.orign.isStatic(), start2, end2);

			if (!result)
				return false;
		}

		return true;
	}
	
	/**
	 * Run a test on the clone and capture the intermediate results after
	 * exercising the clone
	 * 
	 * @param clone
	 * @param start
	 * @param end
	 * @param vars
	 * @return
	 */
	boolean runMVN(String clone, String cName, int start, int end,
			HashSet<Token> vars, ImmutablePair<String, String> test,
			HashMap<String, String> matches, boolean isGraft, boolean isLeft,
			boolean isMethodEnd, boolean isStatic, int os, int oe) {
		// create a folder in the root directory to store the serialized objects
		File grafterDir;
		if (isLeft) {
			grafterDir = new File(GrafterConfig.root_dir + "grafter"
					+ File.separator + "0_" + test.getLeft() + "_"
					+ test.getRight());
		} else {
			grafterDir = new File(GrafterConfig.root_dir + "grafter"
					+ File.separator + "1_" + test.getLeft() + "_"
					+ test.getRight());
		}

		grafterDir.mkdirs();

		// create the clone directory in the test directory
		File cloneDir = new File(grafterDir.getAbsolutePath() + File.separator
				+ cName + "_" + os + "_" + oe);
		cloneDir.mkdirs();

		// prepare the file path for each serialized object
		HashSet<Token> new_vars = new HashSet<Token>();
		HashMap<String, String> paths = new HashMap<String, String>();
		for (Token t : vars) {
			String var;
			if (t.isField() && !t.isStatic) {
				var = "this." + t.getName();
			} else {
				var = t.getName();
			}
			String path;
			if (isGraft) {
				path = cloneDir.getAbsolutePath() + File.separator
						+ matches.get(var);

				if (t.isLocalVariable()) {
					var = matches.get(var);
				}
			} else {
				path = cloneDir.getAbsolutePath() + File.separator + var;
			}

			Token new_t = t.clone(var);
			if (isGraft && t.isLocalVariable()) {
				new_t.graft_start = cur_ms.getClone(t).graft_start;
				new_t.graft_end = cur_ms.getClone(t).graft_end;
			}
			new_vars.add(new_t);

			paths.put(var, path);
		}

		// instrument the clone1
		try {
			instrument(clone, start, end, new_vars, paths, isGraft, isMethodEnd, isStatic);
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane
					.showMessageDialog(
							null,
							"Exception happens when instrumenting the clone to capture intermediate output for the state-level comparison");
			return false;
		}

		// compile the instrumented file
		compile();

		// run the test
		try {
			boolean result = TestRunner.runTestUsingMVNSurefire(test.getLeft(),
					test.getRight());
			if(GrafterConfig.verbose){
				if (result) {
					System.out.println(test.getRight() + "：" + test.getLeft()
							+ " succeeds.");
				} else {
					System.out.println(test.getRight() + "：" + test.getLeft()
							+ " fails.");
				}
			}
		} catch (IOException e1) {
			e1.printStackTrace();
			if(GrafterConfig.batch){
				System.out.println("Grafter fails when running tests on " + className1);
			}else{
				JOptionPane.showMessageDialog(null,
						"Grafter fails when running tests on " + className1);
			}
			
			return false;
		}

		// restore the instrumented file
		restore(clone);

		return true;
	}

	void instrument(String clone, int start, int end, HashSet<Token> vars,
			HashMap<String, String> paths, boolean isGraft, boolean isMethodEnd, boolean isStatic)
			throws IOException {
		if (vars.isEmpty())
			return;
		
		HashSet<Token> no_final_vars = new HashSet<Token>();
		for(Token t : vars){
			if((t.isField() && t.isFinal()) || (isStatic && t.isField() && !t.isStatic)){
				// we won't serialize a variable/field for two cases:
				// 1. the variable/field is final, since final field/variable is never updated anyway
				// 2. the variable/field is non-static but the containing method of the clone is static, since non-static variable cannot be referenced in static context 
				continue;
			}
			no_final_vars.add(t);
		}

		// backup the original file
		File file = new File(clone + ".uninstrument");
		if (!file.exists()) {
			// only backup it once
			file.createNewFile();
			String content = FileUtils.readFileToString(clone);
			FileUtils.writeStringtoFile(content, file.getAbsolutePath());
		}

		// add the serializer java file as needed
		addSerializer(clone);

		// get the line number of the last statement in the clone
		CompilationUnit cu = JavaParser.parse(clone);
		CloneCalibrator calibrator = new CloneCalibrator(cu, start, end);
		cu.accept(calibrator);

		if (calibrator.last == Integer.MIN_VALUE) {
			// we cannot find the first statment in the clone, please double
			// check the validity of the clone
			if(GrafterConfig.batch){
				System.out
				.println("[Grafter]Cannot find the last statement in the clone in file -- "
						+ clone);
			}else{
				JOptionPane.showMessageDialog(null,
						"[Grafter]Cannot find the last statement in the clone in file -- "
								+ clone);
			}
			
			return;
		} else {
			end = calibrator.last;
		}

		// check if there is any return statement in the code clone
		String[] ss = FileUtils.readFileToArray(clone);
		int count = 0;
		for (int i = start - 1; i < end; i++) {
			String s = ss[i];
			if ((s.contains("return ") && !s.substring(0, s.indexOf("return ")).contains("//"))|| s.contains("throw ")) {
				// check which variables are alive before this statement
				HashSet<Token> pruned_alive = checkLiveness(no_final_vars, i, isGraft);
				if (pruned_alive.isEmpty())
					continue;
				String ret_ins = "";
				int count_alive_before_exit = 0;
				for (Token t : pruned_alive) {
					// Still needs to serialize the local variables before throw statements for state comparison
//					if (s.contains("throw ") && t.isLocalVariable()) {
//						continue;
//					}
					String name = t.getName();
					ret_ins += "GrafterSerializer.serialize(" + name + ",\""
							+ paths.get(name) + "\");" + System.lineSeparator();
					count_alive_before_exit++;
				}
				if(s.contains("return ")){
					String p = null;
					for(Token t : vars){
						p = paths.get(t.getName());
						p = p.substring(0, p.lastIndexOf(File.separator));
					}
					p += File.separator + "tmp_return_value";
					ret_ins += "GrafterSerializer.serialize(tmp_return_value,\"" + p + "\");" + System.lineSeparator();
					count_alive_before_exit++;
				}
				ret_ins += "GrafterSerializer.counter++;";

				if (s.contains("return tmp_return_value")) {
					// this is a grafted clone and has already decomposed the
					// return statement, no need to decompose again
					// instrument before the return statement and throw
					// statement
					FileUtils.writeStringtoFile(ret_ins, i + count + 1, clone);
					count += count_alive_before_exit + 1;
				} else if (s.contains("return ")
						&& !s.contains("return tmp_return_value")) {
					// decompose the return statement first
					String return_expression = s
							.substring(s.indexOf("return ") + 7);

					// find the return type
					ReturnTypeFinder rtf = new ReturnTypeFinder(i, cu);
					cu.accept(rtf);
					String ret_1 = rtf.type + " tmp_return_value = "
							+ return_expression + System.lineSeparator();
					String ret_2 = "return tmp_return_value;";

					// rewrite the return statement
					ret_ins = ret_1 + ret_ins + System.lineSeparator() + ret_2;
					FileUtils
							.rewriteStringToFile(ret_ins, i + count + 1, clone);
					count += count_alive_before_exit + 2;
				} else {
					// this is a throw statement
					FileUtils.writeStringtoFile(ret_ins, i + count + 1, clone);
					count += count_alive_before_exit + 1;
				}
			}
		}

		// check if the last statement in the clone is return
		boolean isReturn = false;

		for (int i = end; i >= start; i--) {
			StatementFinder sf = new StatementFinder(cu, i);
			cu.accept(sf);
			if (sf.node != null) {
				if (sf.node instanceof ReturnStatement
						|| sf.node instanceof ThrowStatement) {
					isReturn = true;
				}
				break;
			}
		}

		if (!(isReturn && isMethodEnd)) {
			// do not insert the code to serialize the given variables at the end of the clone
			// only when the last statement is return/throw statement and it is the end of the method 
			String code = "";
			HashSet<Token> pruned_alive = checkLiveness(no_final_vars, end, isGraft);
			for (Token t : pruned_alive) {
				String name = t.getName();
				code += "GrafterSerializer.serialize(" + name + ",\""
						+ paths.get(name) + "\");\n";
			}
			code += "GrafterSerializer.counter++;";
			FileUtils.writeStringtoFile(code, end + count + 1, clone);
			count += pruned_alive.size() + 1;
		}

		deleteOutDatedClassFile(clone);
	}

	private void deleteOutDatedClassFile(String path) {
		// delete the corresponding out-of-date object file
		String objPath = GrafterConfig.bin_src_dir
				+ path.replace(GrafterConfig.src_dir, "");
		objPath = objPath.replace(".java", ".class");
		File objFile = new File(objPath);
		objFile.delete();
	}

	private void addSerializer(String file) throws IOException {
		// Check if TestTracker.java exists in the current package of the given
		// file
		String dir = file.substring(0, file.lastIndexOf(File.separator));
		String serializer = dir + File.separator + "GrafterSerializer.java";
		File f = new File(serializer);

		if (!f.exists()) {
			// Get the package name
			String packageName = getPackageName(file);
			// Customize the template
			String code = "package " + packageName + ";"
					+ System.lineSeparator()
					+ FileUtils.readFileToString(template);

			// Create GrafterSerializer.java in the package of the code clone
			f.createNewFile();
			BufferedWriter bw = null;
			try {
				bw = new BufferedWriter(new FileWriter(serializer));
				bw.write(code);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (bw != null)
					bw.close();
			}
		}
	}

	private String getPackageName(String file) {
		try {
			CompilationUnit cu = JavaParser.parse(file);
			return cu.getPackage().getName().getFullyQualifiedName();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return "";
	}

	private HashSet<Token> checkLiveness(HashSet<Token> s, int line,
			boolean isGraft) {
		HashSet<Token> tmp = new HashSet<Token>();
		for (Token t : s) {
			if (t.isField()) {
				tmp.add(t);
			} else if (t.isLocalVariable()) {
				int start = t.start;
				int end = t.end;

				if (isGraft) {
					start = t.graft_start;
					end = t.graft_end;
				}

				if (start > line || end < line) {
					// if a token is a local variable and it is either not
					// defined yet or has already died, then we will not
					// serializer it.
					continue;
				} else {
					tmp.add(t);
				}
			}
		}

		return tmp;
	}

	HashMap<String, ArrayList<String>> gatherStates(String path) {
		HashMap<String, ArrayList<String>> states = new HashMap<String, ArrayList<String>>();
		File dir = new File(path);

		File[] files = dir.listFiles();
		for (File f : files) {
			String fname = f.getName();
			if (fname.endsWith(".xml")) {
				String var = fname.substring(0, fname.lastIndexOf('_'));
				String tmp = fname.substring(fname.lastIndexOf('_') + 1,
						fname.lastIndexOf('.'));
				int index = Integer.parseInt(tmp);
				ArrayList<String> list;
				if (states.containsKey(var)) {
					list = states.get(var);
				} else {
					list = new ArrayList<String>();
				}

				String value;
				try {
					value = FileUtils.readFileToString(f.getAbsolutePath());
					if (index >= list.size()) {
						for (int i = list.size(); i < index; i++) {
							list.add(null);
						}
						list.add(value);
					} else {
						list.set(index, value);
					}
					states.put(var, list);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return states;
	}

	@Override
	public void restore(String path) {
		File bakFile = new File(path + ".uninstrument");
		if (bakFile.exists()) {
			// first restore the instrumented file if such a file exists
			File file = new File(path);
			file.delete();
			bakFile.renameTo(new File(path));
		}
		
		// then restore the grafted file if such a file exists
		super.restore(path);
		
		// remove the serializer file
		String dir = path.substring(0, path.lastIndexOf(File.separator));
		String serializer = dir + File.separator + "GrafterSerializer.java";
		File sf = new File(serializer);
		sf.delete();
	}
}

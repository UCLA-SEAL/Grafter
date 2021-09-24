package edu.ucla.cs.grafter.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;

import javax.swing.tree.DefaultMutableTreeNode;

import edu.ucla.cs.grafter.ui.CloneTreeNode;
import edu.ucla.cs.grafter.ui.GroupTreeNode;

public class DeckardReader {
	String file;
	int id = 0;

	public DeckardReader(String file) {
		this.file = file;
	}

	public DefaultMutableTreeNode loadData() throws IOException {
		FileInputStream fis = new FileInputStream(new File(file));
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));

		DefaultMutableTreeNode root = new DefaultMutableTreeNode();

		String line = null;
		GroupTreeNode gtn = new GroupTreeNode(0, false);
		int pre_clone_length = 0;
		while ((line = br.readLine()) != null) {
			if (line.isEmpty()) {
				// check how many clones are from test files in the group
				int testCount = 0;
				Enumeration clones = gtn.children();
				while (clones.hasMoreElements()) {
					CloneTreeNode clone = (CloneTreeNode) clones.nextElement();
					if (clone.isTrivial())
						testCount++;
				}
				if (gtn.getChildCount() - testCount < 2
						|| gtn.getChildCount() > 4) {
					// filter 1: mark it as excluded if we have only at most one clone
					// that is not in a test
					// filter 2: mark it as excluded if there are more than 4 individual
					// clones in the group, because based on our observation a
					// big clone group is rarely meaningful clones.
					gtn.setExcluded();
				} else {
					gtn.setIncluded();
				}

				root.add(gtn);
				id++;
				gtn = new GroupTreeNode(id, false);
				pre_clone_length = 0;
			} else {
				boolean isTrivial = false;

				String[] ss = line.split("	");
				String fl = ss[2];
				String[] ss2 = fl.split(" ");
				String path = ss2[1];
				String lines = ss2[2].substring(5);
				int start = Integer.parseInt(lines.split(":")[0]);
				int length = Integer.parseInt(lines.split(":")[1]);
				int end = start + length - 1;

				// filter 3 : if a clone has less than 2 lines of code, we consider it to
				// be trivial.
				if (length < 2)
					isTrivial = true;

				// filter 4: if two clones from the same group have lines with a deviation
				// up to 10, we consider it to be trivial.
				if (pre_clone_length == 0) {
					// this is the first clone in the group
					pre_clone_length = length;
				} else {
					isTrivial = Math.abs(pre_clone_length - length) > 10 ? true
							: false;
				}

				// filter 5: Check if the clone is in the test. If a clone is from a test
				// file, we consider it to be trivial.
				if (!isTrivial) {
					TestFileChecker filter = new TestFileChecker();
					isTrivial = filter.filter(path);
				}

				CloneTreeNode ctn = new CloneTreeNode("", path, start, end,
						isTrivial);
				gtn.add(ctn);
			}
		}

		return root;
	}

	public static void main(String[] args) {
		DeckardReader dr = new DeckardReader(
				"/home/troy/SysAssure/dataset/java-apns-1.0.0.Beta3/clusters/post_cluster_vdb_50_0_allg_0.95_30");
		try {
			dr.loadData();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

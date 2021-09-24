package edu.ucla.cs.grafter.graft.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.core.dom.CompilationUnit;

import edu.ucla.cs.grafter.config.GrafterConfig;
import edu.ucla.cs.grafter.graft.model.Clone;
import edu.ucla.cs.grafter.graft.model.Pair;
import edu.ucla.cs.grafter.graft.model.Token;

public class CloneMatcher {
	public HashSet<Token> tokens1;
	public HashSet<Token> tokens2;
	public HashSet<Pair> matches;
	public HashSet<Token> unmatches1;
	public HashSet<Token> unmatches2;
	
	public CloneMatcher(HashSet<Token> s1, HashSet<Token> s2){
		matches = new HashSet<Pair>();
		tokens1 = s1;
		tokens2 = s2;
		unmatches1 = new HashSet<Token>();
		unmatches2 = new HashSet<Token>();
	}
	
	public CloneMatcher(){
		matches = new HashSet<Pair>();
		tokens1 = new HashSet<Token>();
		tokens2 = new HashSet<Token>();
		unmatches1 = new HashSet<Token>();
		unmatches2 = new HashSet<Token>();
	}

	public boolean match(){
		// match tokens with exact same name and type
		exactMacth(new Candidate(tokens1, tokens2));
		
		// match tokens based on the path and similarity between names
		nominativeMatch(new Candidate(unmatches1, unmatches2));
		
		// If there is still unmatched tokens, throws exception to ensure type safety
		if(GrafterConfig.verbose && !unmatches2.isEmpty()){
			System.out.println("Our matcher cannot find matches for all tokens, exit. "
					+ "Please inspect and refine the matcher.");
			return false;
		}
		
		return true;
	}
	
	private void exactMacth(Candidate c) {
		// we consider primitive types and corresponding autoboxed types equivalent
		HashSet<Token> s1 = new HashSet<Token>(c.s1);
		HashSet<Token> s2 = new HashSet<Token>(c.s2);
				
		for(Token t : s1){
			unboxing(t);
		}
				
		for(Token t : s2){
			unboxing(t);
		}
		
		s1.retainAll(s2);
		
		for(Token t : s1){
			for(Token t2 : s2){
				if(t.equals(t2)){
					matches.add(new Pair(t, t2));
				}
			}
		}
		
		HashSet<Token> tmp1 = new HashSet<Token>(c.s1);
		tmp1.removeAll(s1);
		HashSet<Token> tmp2 = new HashSet<Token>(c.s2);
		tmp2.removeAll(s1);
		unmatches1.addAll(tmp1);
		unmatches2.addAll(tmp2);
	}

	/**
	 * Only need to find a match from candidate.s2 to candidate s1
	 * because we only need to propagate values of clone1 variables to clone2 variables
	 * 
	 * @param candidate
	 */
	private void nominativeMatch(Candidate candidate){
		HashSet<Token> s1 = candidate.s1;
		HashSet<Token> s2 = candidate.s2;
		
		HashMap<Token, ArrayList<Token>> map = new HashMap<Token, ArrayList<Token>>();
		
		// path match first
		for(Token t2 : s2){
			for(Token t1 : s1){
				if(isSamePath(t1, t2)){
					if(map.containsKey(t2)){
						ArrayList<Token> matches = map.get(t2);
						matches.add(t1);
					}else{
						ArrayList<Token> matches = new ArrayList<Token>();
						matches.add(t1);
						map.put(t2, matches);
					}
				}
			}
		}
		
		if(map.keySet().size() != s2.size()){
			if(GrafterConfig.verbose){
				System.out.println("Failed to match all clone2 tokens to clone1 tokens with the same path.");
			}
		}
		
		// name match
		for(Token t : map.keySet()){
			ArrayList<Token> matches = map.get(t);
			// fuzzy matching
			String name1 = t.getName();
			double max = -1;
			Token match = null;
			for(Token c: matches){
				String name2 = c.getName();
				int distance = StringUtils.getLevenshteinDistance(name1, name2);
				int length = Math.max(name1.length(), name2.length());
				double score = 1 - (double)distance/length;
				if(score > max){
					match = c;
					max = score;
				}
			}
			
			this.matches.add(new Pair(match, t));
		}
		
		// remove matched tokens from unmatched lists
		prune();
	}
	
	private boolean isSamePath(Token t1, Token t2){
		//if half of the usage are the same, we consider them similar
		HashSet<ArrayList<String>> tmp1 = new HashSet<ArrayList<String>>(t1.getAllUsage());
		HashSet<ArrayList<String>> tmp2 = new HashSet<ArrayList<String>>(t2.getAllUsage());
		
		if(tmp1.size() == 0 && tmp2.size() == 0){
			// both have no usage
			return true;
		}
		
		int max = Math.max(tmp1.size(), tmp2.size());
		tmp1.retainAll(tmp2);
		int score = tmp1.size();
		
		return (double)score/max >= 0.5;
	}
	
	private void unboxing(Token t){
		switch (t.getType()) {
		case "Boolean": t.setType("boolean"); break;
		case "Integer": t.setType("int"); break;
		case "Double": t.setType("double"); break;
		case "Float": t.setType("float"); break;
		case "Byte": t.setType("byte"); break;
		case "Character": t.setType("char"); break;
		case "Short": t.setType("short"); break;
		case "Long": t.setType("long"); break;
		default: break;
		}
	}
	
	private void prune(){
		// remove matched tokens from unmatched lists
		HashSet<Token> tmp1 = new HashSet<Token>();
		HashSet<Token> tmp2 = new HashSet<Token>();
		for(Pair c : matches){
			tmp1.add(c.orign());
			tmp2.add(c.clone());
		}
				
		unmatches1.removeAll(tmp1);
		unmatches2.removeAll(tmp2);
	}
	
//	private Set<Pair> nameMatch(Candidate c){
//		HashMap<Pair, Integer> candidates = new HashMap<Pair, Integer>();
//		
//		HashSet<Token> s1 = c.s1;
//		HashSet<Token> s2 = c.s2;
//		
//		// initialize all potential candidates first
//		for(Token t1 : s1){
//			for(Token t2: s2){
//				String name1 = t1.getName();
//				String name2 = t2.getName();
//				// (a, ab) and (ab, a) should have same distance
//				int score1 = StringUtils.getLevenshteinDistance(name1, name2);
//				int score2 = StringUtils.getLevenshteinDistance(name2, name1);
//				candidates.put(new Pair(t1, t2), score1 + score2);
//			}
//		}
//		
//		// rank by ascending order
//		List<Pair> sorted = rank(candidates);
//		
//		List<Pair> result = new ArrayList<Pair>();
//		
//		// match all identifiers in location 2 (uncovered by test cases) to identifiers in location 1 (covered)
//		HashSet<Token> tmpLeft = new HashSet<Token>();
//		HashSet<Token> tmpRight = new HashSet<Token>();
//        for(Pair p : sorted){
//            if(!tmpLeft.contains(p.orign()) || !tmpRight.contains(p.clone())){
//                result.add(p);
//                tmpLeft.add(p.orign());
//                tmpRight.add(p.clone());
//            }
//        }
//
//        return new HashSet(result);
//    }
//	
//	private List<Pair> rank(Map<Pair, Integer> unsortMap)
//    {
//        List<Entry<Pair, Integer>> list = new LinkedList<Entry<Pair, Integer>>(unsortMap.entrySet());
//
//        // Sorting the list based on values
//        Collections.sort(list, new Comparator<Entry<Pair, Integer>>()
//        {
//            public int compare(Entry<Pair, Integer> o1,
//                    Entry<Pair, Integer> o2)
//            {
//            	return o1.getValue().compareTo(o2.getValue());
//            }
//        });
//
//        // Maintaining insertion order with the help of LinkedList
//        ArrayList<Pair> result = new ArrayList<Pair>();
//        for (Entry<Pair, Integer> entry : list)
//        {
//        	result.add(entry.getKey());
//        }
//
//        return result;
//    }
//	
//	private ArrayList<Candidate> typeMatch(Candidate c) {
//		ArrayList<Candidate> matches = new ArrayList<Candidate>();
//		
//		HashSet<Token> s1 = c.s1;
//		HashSet<Token> s2 = c.s2;
//		
//		HashMap<String, HashSet<Token>> map1 = new HashMap<String, HashSet<Token>>();
//		HashMap<String, HashSet<Token>> map2 = new HashMap<String, HashSet<Token>>();
//		
//		for(Token t : s1){
//			if(map1.containsKey(t.getType())){
//				HashSet<Token> tmp = map1.get(t.getType());
//				tmp.add(t);
//				map1.put(t.getType(), tmp);
//			}else{
//				HashSet<Token> tmp = new HashSet<Token>();
//				tmp.add(t);
//				map1.put(t.getType(), tmp);
//			}
//		}
//		
//		for(Token t : s2){
//			if(map2.containsKey(t.getType())){
//				HashSet<Token> tmp = map2.get(t.getType());
//				tmp.add(t);
//				map2.put(t.getType(), tmp);
//			}else{
//				HashSet<Token> tmp = new HashSet<Token>();
//				tmp.add(t);
//				map2.put(t.getType(), tmp);
//			}
//		}
//		
//		for(String type : map1.keySet()){
//			if(map2.containsKey(type)){
//				// add as candidate
//				matches.add(new Candidate(map1.get(type), map2.get(type)));
//			}else{
//				HashSet<Token> tmp = map1.get(type);
//				for(Token t : tmp){
//					unmatches1.add(t);
//				}
//			}
//		}
//		
//		for(String type : map2.keySet()){
//			if(map1.containsKey(type)){
//				// do nothing, has already been added
//			}else{
//				HashSet<Token> tmp = map2.get(type);
//				for(Token t : tmp){
//					unmatches2.add(t);
//				}
//			}
//		}
//		
//		return matches;
//	}
//
//	private ArrayList<Candidate> labelMatch(Candidate c) {
//		ArrayList<Candidate> matches = new ArrayList<Candidate>();
//		
//		HashSet<Token> s1 = c.s1;
//		HashSet<Token> s2 = c.s2;
//		
//		HashSet<Token> fields1 = map(s1, Label.FIELD);
//		HashSet<Token> fields2 = map(s2, Label.FIELD);
//		HashSet<Token> vars1 = map(s1, Label.LOCAL);
//		HashSet<Token> vars2 = map(s2, Label.LOCAL);
//		
//		matches.add(new Candidate(fields1, fields2));
//		matches.add(new Candidate(vars1, vars2));
//		return matches;
//	}
//
//	private HashSet<Token> map(HashSet<Token> s, Label l){
//		HashSet<Token> result = new HashSet<Token>();
//		for(Token t : s){
//			if(t.getLabel() == l){
//				result.add(t);
//			}
//		}
//		
//		return result;
//	}
	
	private class Candidate{
		HashSet<Token> s1;
		HashSet<Token> s2;
		
		public Candidate(HashSet<Token> s1, HashSet<Token> s2){
			this.s1 = s1;
			this.s2 = s2;
		}
	}
	
	public static void main(String[] args) throws Exception{
		CloneParser cp = new CloneParser();
		CompilationUnit cu1 = cp.parse("/home/troy/SysAssure/code/SysAssure/test/ucla/cs/synthesizer/foo.java");
		CompilationUnit cu2 = cp.parse("/home/troy/SysAssure/code/SysAssure/test/ucla/cs/synthesizer/bar.java");
		Clone clone1 = new Clone(168, 249, "incr", "foo");
		Clone clone2 = new Clone(111, 183, "mult", "bar");
		CloneVisitor cv1 = new CloneVisitor(clone1, cu1, "/home/troy/SysAssure/code/SysAssure/test/ucla/cs/synthesizer/foo.java");
		CloneVisitor cv2 = new CloneVisitor(clone2, cu2, "/home/troy/SysAssure/code/SysAssure/test/ucla/cs/synthesizer/bar.java");
		cu1.accept(cv1);
		cu2.accept(cv2);
		HashSet<Token> s1 = cv1.wilds;
		HashSet<Token> s2 = cv2.wilds;
		CloneMatcher cm = new CloneMatcher(s1, s2);
		if(cm.match()){
			for(Pair match : cm.matches){
				System.out.println(match.orign().toString() + " <-> " + match.clone().toString());
			}
		}else{
			for(Token unmatch : cm.unmatches2){
				System.out.println(unmatch.getName() + " doesn't have a match.");
			}
		}
	}
}

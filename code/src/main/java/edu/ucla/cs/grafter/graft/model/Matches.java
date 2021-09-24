package edu.ucla.cs.grafter.graft.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Matches {
	ArrayList<Pair> pairs; // only need pairs of identifiers that are defined outside of the clone
	HashMap<Token, Token> map1; // one-to-one mapping from identifiers in origin to identifiers in clone
	HashMap<Token, Token> map2; // one-to-one mapping from identifiers in clone to identifiers in origin
	HashSet<Token> origins;
	HashSet<Token> clones;
	
	public Matches(ArrayList<Pair> list){
		pairs = list;
		origins = new HashSet<Token>();
		clones = new HashSet<Token>();
		map1 = new HashMap<Token, Token>();
		map2 = new HashMap<Token, Token>();
		
		for(Pair pair : list){
			map1.put(pair.orign(), pair.clone());
			map2.put(pair.clone(), pair.orign());
			origins.add(pair.orign());
			clones.add(pair.clone());
		}
	}
	
	public Matches(Set<Pair> list){
		pairs = new ArrayList<Pair>();
		for(Pair p : list){
			pairs.add(p);
		}
		origins = new HashSet<Token>();
		clones = new HashSet<Token>();
		map1 = new HashMap<Token, Token>();
		map2 = new HashMap<Token, Token>();
		
		for(Pair pair : list){
			map1.put(pair.orign(), pair.clone());
			map2.put(pair.clone(), pair.orign());
			origins.add(pair.orign());
			clones.add(pair.clone());
		}
	}
	
	public Token getOrigin(Token clone){
		return map2.get(clone);
	}
	
	public Token getClone(Token origin){
		return map1.get(origin);
	}
	
	public HashSet<Token> getAllOrigin(){
		return origins;
	}
	
	public HashSet<Token> getAllClones(){
		return clones;
	}
}

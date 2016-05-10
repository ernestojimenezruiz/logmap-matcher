package uk.ac.ox.krr.logmap2.indexing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LightTarjan implements Serializable {

	private static final long serialVersionUID = -3317618668787472814L;
	private int index = 0;
	private ArrayList<Integer> stack = new ArrayList<>();
	private Map<Integer,Set<Integer>> SCCs = new HashMap<>();
	private int [] idx;
	private int [] lowlink;

	private Map<Integer,Set<Integer>> tarjan(Integer v, 
			HashMap<Integer, Set<Integer>> graph){
		
		idx[v] = index;
		lowlink[v] = index;

		++index;
		stack.add(0, v);

		if(graph.containsKey(v)){
			for(Integer n : graph.get(v)){
				if(idx[n] == -1){
					tarjan(n, graph);
					lowlink[v] = Math.min(lowlink[v], lowlink[n]);
				}
				else if(stack.contains(n))
					lowlink[v] = Math.min(lowlink[v], idx[n]);
			}
		}
//		else
//			throw new RuntimeException("Unknown node " + v);
		
		if(lowlink[v] == idx[v]){
			Integer n;
			Set<Integer> component = new HashSet<>();
			do{
				n = stack.remove(0);
				component.add(n);
			} while(n != v);
			for (Integer i : component)
				SCCs.put(i,component);
		}
		return SCCs;
	}

	public Map<Integer,Set<Integer>> executeTarjan(HashMap<Integer, 
			Set<Integer>> graph) {
		SCCs.clear();
		index = 0;
		stack.clear();
		assert graph != null && !graph.isEmpty();

		Set<Integer> nodeList = new HashSet<>(graph.keySet());
		for (Set<Integer> children : graph.values())
			nodeList.addAll(children);
		assert !nodeList.isEmpty();
		int maxId = nodeList.isEmpty() ? 0 : Collections.max(nodeList) + 1;
		
		idx = new int[maxId];
		Arrays.fill(idx, -1);
		lowlink = new int[maxId];
		Arrays.fill(lowlink, -1);

		for (Integer node : nodeList)
			if(idx[node] == -1)
				tarjan(node, graph);

		return SCCs;
	}
}

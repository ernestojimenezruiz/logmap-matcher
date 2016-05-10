/*******************************************************************************
 * Copyright 2012 by the Department of Computer Science (University of Oxford)
 * 
 *    This file is part of LogMap.
 * 
 *    LogMap is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU Lesser General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 * 
 *    LogMap is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU Lesser General Public License for more details.
 * 
 *    You should have received a copy of the GNU Lesser General Public License
 *    along with LogMap.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package uk.ac.ox.krr.logmap2.indexing.labelling_schema;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * 
 * 
 * @author Anton Morant
 */
public class Test {

	private IntervalLabelledHierarchy preHierarchy;
	private IntervalLabelledHierarchy postHierarchy;
	private int[] classes = {1,2,3,4,5,6,7,8};

	public static void main(String[] args) {
		new Test().run();
	}
	
	public Test() {
		HashMap<Integer, Set<Integer>> map = new HashMap<Integer, Set<Integer>>();
		HashSet<Integer> bc = new HashSet<Integer>();	bc.add(2);	bc.add(3);
		HashSet<Integer> de = new HashSet<Integer>();	de.add(4);	de.add(5);
		HashSet<Integer> eg = new HashSet<Integer>();	eg.add(5);	eg.add(7);
		HashSet<Integer> f = new HashSet<Integer>();	f.add(6);
		HashSet<Integer> h = new HashSet<Integer>();	h.add(8);
				
		map.put(1, bc);
		map.put(2, de);
		map.put(3, eg);
		map.put(4, f);
		map.put(5, f);
		map.put(3, eg);
		map.put(7, h);
		
		preHierarchy = new PreIntervalLabelledHierarchy(map, false);
		postHierarchy = new PostIntervalLabelledHierarchy(map, false);
	}
	
	public void run() {
		testHierarchy(preHierarchy);
		//testHierarchy(postHierarchy);
		checkPrePostConsistency(preHierarchy, postHierarchy);
	}

	public void printHierarchy(IntervalLabelledHierarchy hierarchy) {
		System.out.println();
		Node root = hierarchy.root;
		printHierarchyNode(root, 0, new HashSet<Node>());
	}
	
	private void testHierarchy(IntervalLabelledHierarchy hierarchy) {
		printHierarchy(hierarchy);
		System.out.println(new TopologicalSorting().sort(hierarchy.root));
		printTree(hierarchy);
		printChildrenAnswers(hierarchy);		
		checkDescAncConsistency(hierarchy);
	}
	
	private void printHierarchyNode(Node node, int indent, Set<Node> visited) {
		for(int i=0; i<indent; i++)
			System.out.print("\t");
		if(visited.contains(node)) {
			System.out.println("[" + node.getClassId() + "]");			
		} else {
			visited.add(node);
			System.out.println(getNodeDescription(node));
			for(Node child : node.getChildren())
				printHierarchyNode(child, indent + 1, visited);
		}
	}
	
	public void printTree(IntervalLabelledHierarchy hierarchy) {
		System.out.println();
		Node root = hierarchy.root;
		printTreeNode(root, 0, new HashSet<Node>());
	}
	
	private void printTreeNode(Node node, int indent, Set<Node> visited) {
		for(int i=0; i<indent; i++)
			System.out.print("\t");
		if(visited.contains(node)) {
			System.out.println("(" + node.getClassId() + ")");			
		} else {
			visited.add(node);
			System.out.println(getNodeDescription(node));
			for(Node child : node.getDescTreeChildren())
				printTreeNode(child, indent + 1, visited);
		}
	}

	private String getNodeDescription(Node node) {
		return node.getClassId()
			+ "(" + node.getDescOrder() + "|" + node.getAscOrder() + ")"
			+ ": d=" + node.descIntervals + " a=" + node.ascIntervals;
	}
	
	private void printChildrenAnswers(IntervalLabelledHierarchy hierarchy) {
		System.out.println();
		for(int parentId : classes) {
			System.out.print(parentId + " : ");
			for(int childId : classes) {
				if(hierarchy.hasDescendant(parentId, childId))
					System.out.print(childId + " ");
			}
			System.out.println();
		}
	}
	
	private void checkDescAncConsistency(IntervalLabelledHierarchy hierarchy) {
		System.out.println();
		int errors = 0;
		for(int idA : classes) {
			for(int idB : classes) {
				boolean isDesc = hierarchy.hasDescendant(idA, idB);
				boolean isAnc = hierarchy.hasAncestor(idB, idA); 
				if(isDesc != isAnc) {
					System.out.println("Descendant/Ancestor inconsistency for classes "
							+ idA + ", " + idB +". hasDescendant = " + isDesc
						+ ", hasAncestor = " + isAnc);
					errors++;
				}
			}
		}
		if(errors == 0)
			System.out.println("Ancestor/Descendant consistency checked with no errors.");
		else
			System.out.println("Ancestor/Descendant consistency checked with " + errors + " errors.");
	}

	
	private void checkPrePostConsistency(IntervalLabelledHierarchy preHierarc, IntervalLabelledHierarchy postHierarc) {
		System.out.println();
		int errors = 0;
		for(int idA : classes) {
			for(int idB : classes) {
				boolean isDescPre = preHierarc.hasDescendant(idA, idB);
				boolean isAncPre = preHierarc.hasAncestor(idB, idA); 
				boolean isDescPost = postHierarc.hasDescendant(idA, idB);
				boolean isAncPost = postHierarc.hasAncestor(idB, idA);
				if(isDescPre != isDescPost) {
					System.out.println("Pre/Postorder inconsistency for classes "
							+ idA + ", " + idB +". hasDescendant (pre) = " + isDescPre
						+ ", hasDescendant (post) = " + isDescPost);
					errors++;
				}
				if(isAncPre != isAncPost) {
					System.out.println("Pre/Postorder inconsistency for classes "
							+ idA + ", " + idB +". hasAncestor (pre) = " + isAncPre
						+ ", hasAncestor (post) = " + isAncPost);
					errors++;
				}
			}
		}
		if(errors == 0)
			System.out.println("Pre/Post consistency checked with no errors.");
		else
			System.out.println("Pre/Post consistency checked with " + errors + " errors.");
		
	}
}

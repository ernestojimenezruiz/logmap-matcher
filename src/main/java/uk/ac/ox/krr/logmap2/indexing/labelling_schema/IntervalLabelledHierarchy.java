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
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import uk.ac.ox.krr.logmap2.io.LogOutput;


/**
 * 
 * @author Anton Morant
 */
public abstract class IntervalLabelledHierarchy {

	public static final int ROOT_LABEL = -100;
	public static final int LEAF_LABEL = -99;
	
	protected HashMap<Integer, Set<Integer>> ontoHierarchy;
	protected HashMap<Integer, Node> classesToNodesMap;
	protected Node root;
	protected Node leaf; //Inverted root for ancestors
	protected List<Node> topoSortedNodes;
	
	public IntervalLabelledHierarchy(HashMap<Integer, Set<Integer>> ontoHierarchy, boolean clearStructures) {
		this.ontoHierarchy = ontoHierarchy;
		createClassesToNodesMap(); //Create nodes and map classId:Node
		createHierarchy(); //Link parents to children (and converse)
		// Check DAG?
		obtainRoot(); //Add artificial root if needed
		obtainLeaf(); //Add artificial inverse root if needed (so there is only one leaf)

		if(leaf.getClassId().equals(LEAF_LABEL))
			leaf.unattach(); //Saves time and memory
		
		obtainDescSpanningTree();
		walkDescendant(); //Perform preorder/postorder walk, assigning one "tree interval" to each node
		computeDescIntervals(); //Compute each node's inherited intervals
		
		if(root.getClassId().equals(ROOT_LABEL))
			root.unattach(); //Saves time and memory
		if(leaf.getClassId().equals(LEAF_LABEL))
			leaf.reattach();
		
		obtainAscSpanningTree();
		walkAscendant(); //Perform preorder/postorder walk, assigning one "tree interval" to each node
		computeAscIntervals(); //Compute each node's inherited intervals

		if(leaf.getClassId().equals(LEAF_LABEL))
			leaf.unattach();
		
		if(clearStructures)
			clearNodeStructures(); //Clear all data structures except node intervals.
	}
	
	public IntervalLabelledHierarchy(HashMap<Integer, Set<Integer>> ontoHierarchy) {
		this(ontoHierarchy, true);
	}
	

	// ABSTRACT METHODS
	// To be implemented depending on whether a preorder or postorder policy is being used.


	/**
	 * Create PreNode or PostNode for the given class.
	 */
	protected abstract Node createNode(int classId);
	
	/**
	 * Perform preorder or postorder walk.
	 */
	protected abstract void walkDescendant();
	
	/**
	 * Perform preorder or postorder walk.
	 */
	protected abstract void walkAscendant();
	
	
	//PUBLIC METHODS
	
	public Map<Integer, Node> getClassesToNodesMap() {
		return classesToNodesMap;
	}
	
	public boolean hasDescendant(int parentClassId, int childClassId) {
		Node parent = classesToNodesMap.get(parentClassId);
		Node child = classesToNodesMap.get(childClassId);
		if(parent == null)
			throw new IllegalArgumentException("Parent class not found");
		if(child == null)
			throw new IllegalArgumentException("Child class not found");
		
		int index = child.getDescOrder();
		
		for(Interval interval : parent.getDescIntervals())
			if(interval.containsIndex(index))
				return true;
		
		return false;
	}
	
	public boolean hasAncestor(int childClassId, int parentClassId) {
		Node child = classesToNodesMap.get(childClassId);
		Node parent = classesToNodesMap.get(parentClassId);
		if(parent == null)
			throw new IllegalArgumentException("Parent class not found");
		if(child == null)
			throw new IllegalArgumentException("Child class not found");
		
		int index = parent.getAscOrder();
		
		for(Interval interval : child.getAscIntervals())
			if(interval.containsIndex(index))
				return true;
		
		return false;
	}
	
	
	//PRIVATE METHODS - BUILDING HIERARCHY

	private void createClassesToNodesMap() {
		classesToNodesMap = new HashMap<Integer, Node>();
		assert ontoHierarchy != null;
		//Map nodes with children
		for(int classId : ontoHierarchy.keySet())
			classesToNodesMap.put(classId, createNode(classId));
		//Map leaf nodes
		for(Set<Integer> children : ontoHierarchy.values())
			for(int childId : children)
				if(!classesToNodesMap.containsKey(childId))
					classesToNodesMap.put(childId, createNode(childId));
	}
	
	private void createHierarchy() {
		for(Entry<Integer, Set<Integer>> entry : ontoHierarchy.entrySet()) {
			int classId = entry.getKey();
			Set<Integer> childrenIds = entry.getValue();
			Node node = classesToNodesMap.get(classId);
			for(int childId : childrenIds) {
				Node child = classesToNodesMap.get(childId);
				assert child != null;
				node.addChild(child);
			}
		}
	}
	
	private void obtainRoot() {
		Set<Node> roots = new HashSet<Node>();
		for(Node node : classesToNodesMap.values()) {
			if(node.isRoot())
				roots.add(node);
		}
		if(roots.size()>1) {
			//We assume DAG, so roots.size() > 0
			root = createNode(ROOT_LABEL); //TOP
			for(Node node : roots)
				root.addChild(node);
		} else {
			root = roots.iterator().next();
		}
	}
	
	private void obtainLeaf() {
		Set<Node> leaves = new HashSet<Node>();
		for(Node node : classesToNodesMap.values()) {
			if(node.isLeaf())
				leaves.add(node);
		}
		if(leaves.size()>1) {
			//We assume DAG, so leaves.size() > 0
			leaf = createNode(LEAF_LABEL); //BOTTOM
			for(Node node : leaves)
				node.addChild(leaf);
		} else {
			leaf = leaves.iterator().next();
		}
	}
	
	private void obtainDescSpanningTree() {
		doTopologicalSort();
		for(Node node : topoSortedNodes) {
			Node maxPredsParent = null;
			int maxPreds = -1;
			for(Node parent : node.getParents()) {
				if(parent.getParents().size() > maxPreds) {
					maxPredsParent = parent;
					maxPreds = parent.getParents().size();
				}
			}
			node.setDescTreeParent(maxPredsParent);
		}
	}
	
	private void obtainAscSpanningTree() {
		doTopologicalSort();
		
		ListIterator<Node> iter = topoSortedNodes.listIterator(topoSortedNodes.size());
		
		while(iter.hasPrevious()) {
			Node node = iter.previous();
			Node maxPredsParent = null;
			int maxPreds = -1;
			for(Node parent : node.getChildren()) { //Inverting edges!
				if(parent.getChildren().size() > maxPreds) {
					maxPredsParent = parent;
					maxPreds = parent.getChildren().size();
				}
			}
			node.setAscTreeParent(maxPredsParent);
		}
	}
	
	private void computeDescIntervals() {
		doTopologicalSort();
		ListIterator<Node> iter = topoSortedNodes.listIterator(topoSortedNodes.size());
		while(iter.hasPrevious()) {
			Node node = iter.previous();
			for(Node child : node.getChildren()) {
				//TODO Revise> Concurrent modification otherwise
				if (node.equals(child)){
					LogOutput.print("Same node as children: computeDescIntervals");
					continue;
				}
				for(Interval interval : child.getDescIntervals()) {
					node.addDescInterval(interval);
				}
			}
		}		
	}
	
	private void computeAscIntervals() {
		doTopologicalSort();
		for(Node node : topoSortedNodes) {
			for(Node child : node.getParents()) { //Inverting edges!
				if (node.equals(child)){
					LogOutput.print("Same node as children: computeAscIntervals");
					continue;
				}
				for(Interval interval : child.getAscIntervals()) {
					node.addAscInterval(interval);
				}
			}
		}		
	}

	private void clearNodeStructures() {
		for(Node node : classesToNodesMap.values())
			node.clearAuxiliarStructures();
		ontoHierarchy.clear();
		ontoHierarchy = null;
	}
	
	
	//PRIVATE METHODS - USEFUL
	
	protected void doTopologicalSort() {
		//Obtain topologically ordered list of nodes (for several uses)
		if(topoSortedNodes == null)
			topoSortedNodes = new TopologicalSorting().sort(root);
		
	}
	
}

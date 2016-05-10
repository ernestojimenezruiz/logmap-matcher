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

import java.util.HashSet;

import java.util.Set;


/**
 * 
 * @author Anton Morant
 */
public abstract class Node {
	//Contained ontology class
	protected int classId;
	//Ontology hierarchy subsumption structure
	protected Set<Node> parents;
	protected Set<Node> children;
	//Minimum spanning tree structure for descendants
	protected Node descTreeParent;
	protected Set<Node> descTreeChildren;
	//Minimum spanning tree structure for ascendants
	protected Node ascTreeParent;
	protected Set<Node> ascTreeChildren;
	//Computed intervals
	protected Set<Interval> descIntervals;
	protected Set<Interval> ascIntervals;

	public abstract void setDescOrder(int orderIndex);
	public abstract int getDescOrder();
	public abstract void setAscOrder(int orderIndex);
	public abstract int getAscOrder();
	public abstract void setDescChildOrder(int childOrderIndex);
	public abstract int getDescChildOrder();
	public abstract void setAscChildOrder(int childOrderIndex);
	public abstract int getAscChildOrder();
	public abstract Interval getDescOrderInterval();
	public abstract Interval getAscOrderInterval();
	
	public Node(Integer classId) {
		this.classId = classId;
		parents = new HashSet<Node>();
		children = new HashSet<Node>();
		descTreeChildren = new HashSet<Node>();
		ascTreeChildren = new HashSet<Node>();
		descIntervals = new HashSet<Interval>();
		ascIntervals = new HashSet<Interval>();
	}
	
	/**
	 * This method should be called after finishing building the index
	 * in order to free all reserved memory except for the intervals.
	 */
	public void clearAuxiliarStructures() {
		parents.clear();
		parents = null;
		children.clear();
		children = null;
		descTreeParent = null;
		descTreeChildren.clear();
		descTreeChildren = null;
		ascTreeParent = null;
		ascTreeChildren.clear();
		ascTreeChildren = null;
	}
	
	
	/**
	 * Removes interval structures only
	 */
	public void clearIntervalStructures(){
		
		descIntervals.clear();
		//descIntervals=null;
		ascIntervals.clear();
		//ascIntervals=null;
		
		
	}
	
	//PUBLIC GETTERS
	
	public Integer getClassId() {
		return classId;
	}
	
	public Set<Interval> getDescIntervals() {
		return descIntervals;
	}
	
	public Set<Interval> getAscIntervals() {
		return ascIntervals;
	}
	
	
	//Setters (ernesto): need when intervals are loaded from files
	public Set<Interval> setDescIntervals(Set<Interval> intervals) {
		return descIntervals=intervals;
	}
	
	public Set<Interval> setAncIntervals(Set<Interval> intervals) {
		return ascIntervals=intervals;
	}
	
	//PACKAGE GETTERS
	
	/*
	 * This structures are to be used only during the construction of the interval-labelled schema,
	 * and are the structures that can be freed with clearStructures().
	 * The getters' visibility has therefore been reduced to package, since they are not to be
	 * used by external classes.
	 */

	
	Set<Node> getChildren() {
		return children;
	}
	
	Set<Node> getParents() {
		return parents;
	}
		
	Node getDescTreeParent() {
		return descTreeParent;
	}
	
	Set<Node> getDescTreeChildren() {
		return descTreeChildren;
	}
	
	Node getAscTreeParent() {
		return ascTreeParent;
	}
	
	Set<Node> getAscTreeChildren() {
		return ascTreeChildren;
	}
	
	
	
	
	//Edge manipulation
	
	public void addChild(Node child) {
		children.add(child);
		//To be removed later if needed by setDescTreeParent:
		descTreeChildren.add(child);
		child.parents.add(this);
		//To be removed later if needed by setAscTreeParent:
		child.ascTreeChildren.add(this);  //Inverting edges!
	}
	
	public void setDescTreeParent(Node node) {
		assert parents.contains(node);
		descTreeParent = node;
		for(Node parent : parents)
			if(parent!=descTreeParent)
				parent.getDescTreeChildren().remove(this);
	}
	
	public void setAscTreeParent(Node node) {
		assert children.contains(node); //Inverting edges!
		ascTreeParent = node;
		for(Node parent : children) //Inverting edges!
			if(parent != ascTreeParent)
				parent.getAscTreeChildren().remove(this);
	}
	
	//Interval manipulation

	public void addDescInterval(Interval interval) {
		for(Interval ownInterval : descIntervals) {
			if(ownInterval.isSuperIntervalOf(interval))
				return; //No need to add
			if(interval.isSuperIntervalOf(ownInterval)) {
				//Replace smaller interval with new one
				descIntervals.remove(ownInterval);
				descIntervals.add(interval);
				return;
			}
		}
		//Do not add negative intervals <-1,-1> or <-id,-id>
		if (interval.leftbound>=0 && interval.rightbound>=0)
			descIntervals.add(interval); //Need to add
	}

	public void addAscInterval(Interval interval) {
		for(Interval ownInterval : ascIntervals) {
			if(ownInterval.isSuperIntervalOf(interval))
				return; //No need to add
			if(interval.isSuperIntervalOf(ownInterval)) {
				//Replace smaller interval with new one
				ascIntervals.remove(ownInterval);
				ascIntervals.add(interval);
				return;
			} else if(ownInterval.isAdjacentTo(interval)) {
				//Replace interval for its union with adjacent one
				Interval unionInterval = new Interval(
						Math.min(ownInterval.getLeftBound(), interval.getLeftBound()),
						Math.max(ownInterval.getRightBound(), interval.getRightBound()));
				ascIntervals.remove(ownInterval);
				ascIntervals.add(unionInterval);
				return;
			}
		}
		ascIntervals.add(interval); //Need to add
	}
	
	//Others
	
	public boolean isRoot() {
		return parents.size() == 0;
	}
	
	public boolean isLeaf() {
		return children.size() == 0;
	}
	
	/**
	 * Remove links from other nodes to this one (keep the other direction for reattachment).
	 */
	public void unattach() {
		for(Node parent : parents) {
			parent.children.remove(this);
			parent.descTreeChildren.remove(this); //In case treeParent does not belong to parents
		}
		for(Node child : children) {
			child.parents.remove(this);
			if(child.descTreeParent.equals(this))
				child.descTreeParent = null; //In case treeChildren is not contained in children
		}
		if(descTreeParent != null)
			descTreeParent.descTreeChildren.remove(this);
		for(Node descTreeChild : descTreeChildren)
			descTreeChild.descTreeParent = null;
		if(ascTreeParent != null)			
			ascTreeParent.ascTreeChildren.remove(this);
		for(Node ascTreeChild : ascTreeChildren)
			ascTreeChild.ascTreeParent = null;
	}

	/**
	 * Restore links from other nodes to this one (from kept links at unattachment).
	 */
	public void reattach() {
		for(Node parent : parents)
			parent.children.add(this);
		for(Node child : children)
			child.parents.add(this);
		if(descTreeParent != null)
			descTreeParent.descTreeChildren.add(this);
		for(Node descTreeChild : descTreeChildren)
			descTreeChild.descTreeParent = this;
		if(ascTreeParent != null)
			ascTreeParent.ascTreeChildren.add(this);
		for(Node ascTreeChild : ascTreeChildren)
			ascTreeChild.ascTreeParent = this;
	}
	
	@Override
	public String toString() {
		return String.valueOf(getClassId());
	}
}

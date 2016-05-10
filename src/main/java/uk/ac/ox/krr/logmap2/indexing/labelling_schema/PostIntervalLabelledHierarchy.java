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
import java.util.Set;

import uk.ac.ox.krr.logmap2.io.LogOutput;

/**
 * 
 * @author Anton Morant
 */
public class PostIntervalLabelledHierarchy extends IntervalLabelledHierarchy {

	public final static int BASE_INDEX = 1; //First leaf node gets postorder 1
	public final static int INDEX_INCREMENT = 1;

	public PostIntervalLabelledHierarchy(HashMap<Integer, Set<Integer>> ontoHierarchy, boolean clearStructures) {
		super(ontoHierarchy, clearStructures);
	}

	public PostIntervalLabelledHierarchy(HashMap<Integer, Set<Integer>> ontoHierarchy) {
		super(ontoHierarchy);
	}

	/* (non-Javadoc)
	 * @see intervallabelling.IntervalLabelledHierarchy#createNode(java.lang.String)
	 */
	@Override
	protected Node createNode(int classId) {
		return new PostNode(classId);
	}

	/* (non-Javadoc)
	 * @see intervallabelling.IntervalLabelledHierarchy#walkOrder()
	 */
	@Override
	protected void walkDescendant() {
		walkDescNode(root, BASE_INDEX);
	}
	
	private int walkDescNode(Node node, int orderIndex) {
		node.setDescChildOrder(orderIndex);		
		for(Node child : node.getDescTreeChildren()) {
			if (node.equals(child)){
				LogOutput.print("Same node as children: walkDescNode");
				continue;
			}
			orderIndex = walkDescNode(child, orderIndex);
		}
		node.setDescOrder(orderIndex);
		orderIndex++;		
		return orderIndex;
	}

	@Override
	protected void walkAscendant() {
		walkAscNode(leaf, BASE_INDEX);
	}
	
	private int walkAscNode(Node node, int orderIndex) {		
		node.setAscChildOrder(orderIndex);		
		for(Node child : node.getAscTreeChildren()) {
			if (node.equals(child)){
				LogOutput.print("Same node as children: walkAscNode");
				continue;
			}
			orderIndex = walkAscNode(child, orderIndex);
		}
		node.setAscOrder(orderIndex);
		orderIndex++;		
		return orderIndex;
	}
}

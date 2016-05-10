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
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * 
 * 
 * @author Anton Morant
 */
public class TopologicalSorting {
	private LinkedList<Node> nodes;
	private Set<Node> visited;
	
	public TopologicalSorting() {
		reset();
	}
	
	public void reset() {
		nodes = new LinkedList<Node>();
		visited = new HashSet<Node>();		
	}
	
	public List<Node> sort(Node root) {		
		visit(root);		
		return nodes;
	}
	
	private void visit(Node node) {
		if(!visited.contains(node)) {
			visited.add(node);
			
			for(Node child : node.getChildren())
				visit(child);
			
			nodes.addFirst(node);
		}
	}	
}

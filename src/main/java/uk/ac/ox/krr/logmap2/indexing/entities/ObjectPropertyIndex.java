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
package uk.ac.ox.krr.logmap2.indexing.entities;

import java.util.Collections;
import java.util.Set;

import java.util.HashSet;

public class ObjectPropertyIndex extends PropertyIndex {
	
	
	
	
	Set<Integer> range = new HashSet<Integer>();
	
	
	
	public ObjectPropertyIndex(int index){
		super(index, PropertyIndex.OBJECTPROPERTY);
	}
	
	
	public Set<Integer> getRangeClassIndexes(){
		return range;
	}
	
	public void addRangeClassIndex(int icls){
		range.add(icls);
	}

	
	
	
	
	
	
}

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


/**
 * 
 * @author Anton Morant
 */
public class PreNode extends Node {
	
	private Interval descOrderInterval;
	private Interval ascOrderInterval;
	
	public PreNode(int classId) {
		super(classId);
		//descOrderInterval = new Interval(-1,-1);
		descOrderInterval = new Interval(-classId,-classId); //We avoid wrong preorders in case not given
		descIntervals.add(descOrderInterval);
		ascOrderInterval = new Interval(-1,-1);
		ascIntervals.add(ascOrderInterval);
	}

	@Override
	public void setDescOrder(int preorder) {
		descOrderInterval.setLeftBound(preorder);
	}

	@Override
	public void setDescChildOrder(int maxPreorder) {
		descOrderInterval.setRightBound(maxPreorder);
	}

	@Override
	public int getDescOrder() {
		return descOrderInterval.getLeftBound();
	}

	@Override
	public int getDescChildOrder() {
		return descOrderInterval.getRightBound();
	}

	@Override
	public Interval getDescOrderInterval() {
		return descOrderInterval;
	}

	@Override
	public void setAscOrder(int preorder) {
		ascOrderInterval.setLeftBound(preorder);
	}

	@Override
	public void setAscChildOrder(int maxPreorder) {
		ascOrderInterval.setRightBound(maxPreorder);
	}

	@Override
	public int getAscOrder() {
		return ascOrderInterval.getLeftBound();
	}

	@Override
	public int getAscChildOrder() {
		return ascOrderInterval.getRightBound();
	}

	@Override
	public Interval getAscOrderInterval() {
		return ascOrderInterval;
	}	
}

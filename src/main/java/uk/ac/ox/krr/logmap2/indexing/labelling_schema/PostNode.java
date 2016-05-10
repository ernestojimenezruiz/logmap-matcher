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
public class PostNode extends Node {
	
	private Interval descOrderInterval;
	private Interval ascOrderInterval;
	
	public PostNode(int classId) {
		super(classId);
		descOrderInterval = new Interval(-1,-1);
		descIntervals.add(descOrderInterval);
		ascOrderInterval = new Interval(-1,-1);
		ascIntervals.add(ascOrderInterval);
	}

	@Override
	public void setDescOrder(int postorder) {
		descOrderInterval.setRightBound(postorder);
	}

	@Override
	public void setDescChildOrder(int minPostorder) {
		descOrderInterval.setLeftBound(minPostorder);
	}

	@Override
	public int getDescOrder() {
		return descOrderInterval.getRightBound();
	}

	@Override
	public int getDescChildOrder() {
		return descOrderInterval.getLeftBound();
	}

	@Override
	public Interval getDescOrderInterval() {
		return descOrderInterval;
	}

	@Override
	public void setAscOrder(int postorder) {
		ascOrderInterval.setRightBound(postorder);
	}

	@Override
	public void setAscChildOrder(int minPostorder) {
		ascOrderInterval.setLeftBound(minPostorder);
	}

	@Override
	public int getAscOrder() {
		return ascOrderInterval.getRightBound();
	}

	@Override
	public int getAscChildOrder() {
		return ascOrderInterval.getLeftBound();
	}

	@Override
	public Interval getAscOrderInterval() {
		return ascOrderInterval;
	}	
}

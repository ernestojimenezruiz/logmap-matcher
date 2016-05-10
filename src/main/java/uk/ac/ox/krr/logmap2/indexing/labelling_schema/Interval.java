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

import java.util.List;
import java.util.ArrayList;

public class Interval{

	int leftbound;
	int rightbound;
	
	
	public Interval (int leftb, int rightb){
		leftbound=leftb;
		rightbound=rightb;
		
	}
	
	
	public Interval (String serialized_interval){
		
		if (serialized_interval.indexOf(",")>0){
			leftbound=Integer.valueOf(serialized_interval.split(",")[0]);
			rightbound=Integer.valueOf(serialized_interval.split(",")[1]);
		}
		else{
			leftbound=-1;
			rightbound=-1;
		}
	}
	
	
	
	public int getLeftBound(){
		return leftbound;
	}
	
	public int getRightBound(){
		return rightbound;
	}
	
	public void setLeftBound(int leftbound) {
		this.leftbound = leftbound;
	}

	public void setRightBound(int rightbound) {
		this.rightbound = rightbound;
	}


	public boolean equals(Object o){
		
		if  (o == null)
			return false;
		if (o == this)
			return true;
		if (!(o instanceof Interval))
			return false;
		
		Interval i =  (Interval)o;
		
		return equals(i);
		
	}
	
	public boolean equals(Interval i){
	
		if (leftbound!=i.getLeftBound())
			return false;
		
		if (rightbound!=i.getRightBound())
			return false;
		
		return true;

	}
	
	/**
	 * True if given i is included in the interval   
	 * @param i
	 * @return
	 */
	public boolean isSuperIntervalOf(Interval i){
		
		if(i.getLeftBound()<leftbound)
			return false;
		if(i.getRightBound()>rightbound)
			return false;
		
		return true;
		
	}
	
	
	/**
	 * True if interval is included in given i   
	 * @param i
	 * @return
	 */
	public boolean isSubIntervalOf(Interval i){
		
		if(i.getLeftBound()>leftbound)
			return false;
		if(i.getRightBound()<rightbound)
			return false;
		
		return true;
		
	}
	
	
	
	/**
	 * 
	 * @return
	 */
	public boolean containsIndex(int index){
		
		if (index>rightbound || index<leftbound)
			return false;
		
		return true;
		
	}
	
	/**
	 * True if given i intersects in the interval
	 * Intervals like that has a non-empty intersection [1,3][3,5]-> [3,3]    
	 * @param i
	 * @return
	 */
	public boolean hasNonEmptyIntersectionWith(Interval i){
		
		if (rightbound < i.leftbound)
			return false;
		
		if (i.rightbound < leftbound)
			return false;
		
		return true;
		
	}
	
	
	private int min(int a, int b){
		if (a<b)
			return a;
		return b;
		
	}
	
	private int max(int a, int b){
		if (a>b)
			return a;
		return b;
		
	}
	
	
	public Interval getIntersectionWith(Interval i){
		
		if (hasNonEmptyIntersectionWith(i)){
			return new Interval(
						max(leftbound, i.getLeftBound()),
						min(rightbound, i.getRightBound())
					);
		}
		return new Interval(-1,-1);
		
	}
	
	
	public boolean isAdjacentTo(Interval i){
		if (hasNonEmptyIntersectionWith(i))
			return true;
		
		if (i.rightbound==leftbound-1)
			return true;
		
		if (rightbound==i.leftbound-1)
			return true;
		
		return false;
		
	}
	
	
	/**
	 * Assumes that are adjacent a returns an Interval
	 * @param i
	 * @return
	 */
	public Interval getUnionWith(Interval i){
		
		//Assumes that are adjacent
		return new Interval(
					min(leftbound, i.getLeftBound()),
					max(rightbound, i.getRightBound())
				);
		
		
	}
	
	/**
	 * Returns a list with one interval if the were adjacent or with the two intervals if they were not
	 * @param i
	 * @return
	 */
	public List<Interval> getUnionWithList(Interval i){
		
		List<Interval> unionList = new ArrayList<Interval>();
		
		if (isAdjacentTo(i)){
			unionList.add(new Interval(
						min(leftbound, i.getLeftBound()),
						max(rightbound, i.getRightBound())
					));
		}
		else{
			unionList.add(this);
			unionList.add(i);
		}
			
		return unionList;
		
	}
	
	
	
	public boolean hasLowerLeftBoundThan(Interval i){
		if (leftbound < i.getLeftBound())
			return true;
		return false;
	}
	
	
	public boolean hasGreaterLeftBoundThan(Interval i){
		if (leftbound > i.getLeftBound())
			return true;
		return false;
	}
	
	
	/**
	 * What we consider empty interval
	 * @return
	 */
	public boolean isEmptyInterval(){
		if (leftbound<0)
			return false;
		
		return true;
	}
	
	
	public String toString(){
		return "<" + leftbound + ", " +rightbound + ">";
	}
	
	public String serialize(){
		return leftbound + "," +rightbound;
	}
	
	
	public  int hashCode() {
		  int code = 10;
		  code = 40 * code + leftbound;
		  code = 40 * code + rightbound;
		  return code;
	}
	
	
	
	
	
}

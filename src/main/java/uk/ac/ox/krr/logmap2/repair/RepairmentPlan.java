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
package uk.ac.ox.krr.logmap2.repair;

import java.util.Set;


import uk.ac.ox.krr.logmap2.repair.hornSAT.HornClause;

/**
 * 
 * This class will store extracted plans and its confidence
 *
 * @author Anton Morant
 * Created: August, 2011
 *
 */
public class RepairmentPlan {

	private Set<HornClause> mappings;
	private double confidence;
	private int conflictiveness;
	
	public RepairmentPlan(Set<HornClause> mappings, double confidence, int conflictiveness) {
		this.mappings = mappings;
		this.confidence = confidence;
		this.conflictiveness = conflictiveness;
	}
	
	
	//Getters & setters

	public Set<HornClause> getMappings() {
		return mappings;
	}
	
	public double getConfidence() {
		return confidence;
	}
	
	public int getconflictiveness() {
		return conflictiveness;
	}
	
	public void setConfidence(double confidence) {
		this.confidence = confidence;
	}
	
	@Override
	public boolean equals(Object plan) {
		if(!(plan instanceof Object))
			return false;
		return mappings.equals(((RepairmentPlan)plan).getMappings());
	}
	
	@Override
	public int hashCode() {
		return mappings.hashCode();
	}
	
	public String toString(){
		return mappings + " (Conf: " + confidence +")";
	}

}

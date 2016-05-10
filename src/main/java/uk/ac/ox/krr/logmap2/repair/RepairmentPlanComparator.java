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

import java.util.Comparator;

/**
 * 
 * This class compares plans
 *
 * @author Anton Morant
 * Created: August, 2011
 *
 */
public class RepairmentPlanComparator implements Comparator<RepairmentPlan> {

	@Override
	public int compare(RepairmentPlan plan1, RepairmentPlan plan2) {
		
		//We first order by conflictiveness
		if (plan1.getconflictiveness() > plan2.getconflictiveness())
			return -1;
		else if (plan1.getconflictiveness() < plan2.getconflictiveness())
			return 1;
		else {
			//If equal conflictiveness we order by confidence
			if(plan1.getConfidence() > plan2.getConfidence())
				return 1;
			else if (plan1.getConfidence() < plan2.getConfidence())
				return -1;
			else
				return 0;
		}
	}

}

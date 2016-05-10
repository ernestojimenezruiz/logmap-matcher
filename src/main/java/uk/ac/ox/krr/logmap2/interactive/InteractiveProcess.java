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
package uk.ac.ox.krr.logmap2.interactive;

import java.util.TreeSet;

import uk.ac.ox.krr.logmap2.interactive.objects.MappingObjectInteractivity;

public abstract class InteractiveProcess {
	
	
	protected TreeSet<MappingObjectInteractivity> orderedMappings2Ask;  
	
	protected double precision;
	protected double recall;
	protected double Fmeasure;
	
	
	
	public abstract void startInteractiveProcess();
	
	
	
	public abstract void endInteractiveProcess(boolean filter);
	
	


	/**
	 * @return the orderedMappings2Ask
	 */
	public TreeSet<MappingObjectInteractivity> getOrderedMappings2Ask() {
		return orderedMappings2Ask;
	}



	public abstract void  endInteractiveProcess();
		// TODO Auto-generated method stub


	
	
	
	
	//protected abstract void getPrecisionAndRecall();

	
	
	
	

	

}

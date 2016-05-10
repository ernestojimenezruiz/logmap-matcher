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
package uk.ac.ox.krr.logmap2.SIAssessment;

import java.util.Set;

import uk.ac.ox.krr.logmap2.indexing.IndexManager;
import uk.ac.ox.krr.logmap2.mappings.MappingManager;

/**
 * Manages the compatibility of properties
 * 
 * @author Ernesto
 *
 */
public abstract class PropertyMappingAssessment<T> {
	
	protected IndexManager index;
	protected MappingManager mapping_manager;
	
	protected final int EMPTY_RANGE_OR_DOMAIN=0;
	protected final int SAME_RANGE_AND_DOMAIN=1;
	protected final int ONLY_SAME_RANGE_OR_DOMAIN=2;
	protected final int COMPATIBLE_RANGE_DOMAIN=3;
	protected final int INCOMPATIBLE_RANGE_OR_DOMAIN=4;
	protected final int PROBABLY_INCOMPATIBLE_RANGE_OR_DOMAIN=5;
	
	protected abstract int arePropertiesCompatible(int ident1, int ident2);
	
	protected abstract int arePropertiesCompatibleLight(int ident1, int ident2);
	
	
	/**
	 * This defines a minimum confidence to be a mapping accepted
	 * @param ident1
	 * @param ident2
	 * @return
	 */
	public double getConfidence4Compatibility(int ident1, int ident2){
		
		//int compatibility = arePropertiesCompatible(ident1, ident2);
		//TODO used in LogMap for RODI
		int compatibility = arePropertiesCompatibleLight(ident1, ident2);
		
		switch (compatibility) {
	    	case EMPTY_RANGE_OR_DOMAIN: //Both empty
	    		return 0.90; //0.95
	    	case SAME_RANGE_AND_DOMAIN:
	    		return 0.75;
	    	case ONLY_SAME_RANGE_OR_DOMAIN: //And the other non empty and compatible
	    		return 0.85; //0.90
	    	case COMPATIBLE_RANGE_DOMAIN:
	    		return 0.90; //0.93
	    	case PROBABLY_INCOMPATIBLE_RANGE_OR_DOMAIN:
	    		return 1.5;
	    	case INCOMPATIBLE_RANGE_OR_DOMAIN: //(Also indludes the cases where the domain1/range1 is empty and the other domain2/range2 no)
	    		return 2.0; //Max isub score is 1.0
	    	default:
	    		return 2.0; //Max isub score is 1.0
		}
	}
	
	
	protected boolean haveSameRange(Set<T> range1, Set<T> range2){
		
		if (range1.size()>0 && range2.size()>0){			
			return range1.equals(range2);
		}
		
		return false;	
	}
	
	
	protected boolean haveSameDomain(Set<Integer> dom1, Set<Integer> dom2){
		
		if (dom1.size()>0 || dom2.size()>0){			
			return dom1.equals(dom2);
		}
		
		return false;				
		//return intersect.size()>0 && dom1.size()==intersect.size() && dom2.size()==intersect.size();
	}
	
	

	
	
	

}

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

import java.util.HashSet;
import java.util.Set;

import uk.ac.ox.krr.logmap2.indexing.IndexManager;
import uk.ac.ox.krr.logmap2.mappings.MappingManager;

/**
 * Manages the compatibility of data properties
 * 
 * @author Ernesto
 *
 */
public class DataPropertyMappingAssessment extends PropertyMappingAssessment<String> {

	
	
	public DataPropertyMappingAssessment(IndexManager index, MappingManager mapping_manager){
		
		this.index = index;
		this.mapping_manager = mapping_manager;
		
		
		
	}
	
	
	
	protected int arePropertiesCompatible(int ident1, int ident2) {
		
		
		Set<Integer> domain1=index.getDomainDataProp4Identifier(ident1);
		Set<Integer> mapped_domain1=new HashSet<Integer>();
		Set<Integer> domain2=index.getDomainDataProp4Identifier(ident2);
				
		Set<String> range1=index.getRangeDataProp4Identifier(ident1);
		Set<String> range2=index.getRangeDataProp4Identifier(ident2);
				
		
		boolean same_domain=false;
		boolean same_range=false;
		
		
		if (domain1.isEmpty() && domain2.isEmpty() && range1.isEmpty() && range2.isEmpty())
			return EMPTY_RANGE_OR_DOMAIN;
		
		
		//look for incompatibilities
		if (
				(domain1.isEmpty() && !domain2.isEmpty()) || 
				(range1.isEmpty() && !range2.isEmpty()) ||
				(!domain1.isEmpty() && domain2.isEmpty()) || 
				(!range1.isEmpty() && range2.isEmpty())				
		){
			return INCOMPATIBLE_RANGE_OR_DOMAIN; //we do not risk (proved: source of errors!)
		}
		
		
		for (int ide1 : domain1){
			if (index.getDangerousClasses().contains(ide1))//If domain or ranges are equivalent to Top 
				return INCOMPATIBLE_RANGE_OR_DOMAIN;
		}
		
		for (int ide2 : domain2){
			if (index.getDangerousClasses().contains(ide2))//If domain or ranges are equivalent to Top 
				return INCOMPATIBLE_RANGE_OR_DOMAIN;
		}
		
		
		
		//One side may still be empty	
		if (!domain1.isEmpty() && !domain2.isEmpty()){
			//Find mappings		
			for (int cls1 : domain1){
				if (mapping_manager.getLogMapMappings().containsKey(cls1)){
					mapped_domain1.addAll(mapping_manager.getLogMapMappings().get(cls1));
				}
			}
		}
		
	
		
		same_domain=haveSameDomain(mapped_domain1, domain2);
		same_range=haveSameRange(range1, range2);
		
		
		//Same sets in one of teh sides at least
		if (same_domain && same_range){
			return SAME_RANGE_AND_DOMAIN;
		}
		//If different range --> incompatible
		else if (same_range){ //same range
			
			//All possible mappings must be compatible (i.e. same hierarchy)
			for (int cls1 : domain1){
				for (int cls2 : domain2){
					if (mapping_manager.isMappingInConflictWithFixedMappings(cls1, cls2)){
						return INCOMPATIBLE_RANGE_OR_DOMAIN;
					}
						
				}
			}
			
			//Compatible ranges or domains?? Dangerous?
			return COMPATIBLE_RANGE_DOMAIN;
			//return INCOMPATIBLE_RANGE_OR_DOMAIN;
			
		}
		
		//Different ranges!
		return INCOMPATIBLE_RANGE_OR_DOMAIN;
		
		
		


		
		
	}
	
	
	
	/**
	 * Less aggresive method
	 */
	protected int arePropertiesCompatibleLight(int ident1, int ident2) {
		
		
		Set<Integer> domain1=index.getDomainDataProp4Identifier(ident1);
		Set<Integer> mapped_domain1=new HashSet<Integer>();
		Set<Integer> domain2=index.getDomainDataProp4Identifier(ident2);
				
		Set<String> range1=index.getRangeDataProp4Identifier(ident1);
		Set<String> range2=index.getRangeDataProp4Identifier(ident2);
				
		
		boolean same_domain=false;
		boolean same_range=false;
		
		
		if (domain1.isEmpty() && domain2.isEmpty() && range1.isEmpty() && range2.isEmpty())
			return EMPTY_RANGE_OR_DOMAIN;
		
		
		//look for incompatibilities -> too aggressive
		/*if (
				(domain1.isEmpty() && !domain2.isEmpty()) || 
				(range1.isEmpty() && !range2.isEmpty()) ||
				(!domain1.isEmpty() && domain2.isEmpty()) || 
				(!range1.isEmpty() && range2.isEmpty())				
		){
			return INCOMPATIBLE_RANGE_OR_DOMAIN; //we do not risk (proved: source of errors!)
		}*/
		
		
		for (int ide1 : domain1){
			if (index.getDangerousClasses().contains(ide1))//If domain or ranges are equivalent to Top 
				return INCOMPATIBLE_RANGE_OR_DOMAIN;
		}
		
		for (int ide2 : domain2){
			if (index.getDangerousClasses().contains(ide2))//If domain or ranges are equivalent to Top 
				return INCOMPATIBLE_RANGE_OR_DOMAIN;
		}
		
		
		
		//One side may still be empty	
		if (!domain1.isEmpty() && !domain2.isEmpty()){
			//Find mappings		
			for (int cls1 : domain1){
				if (mapping_manager.getLogMapMappings().containsKey(cls1)){
					mapped_domain1.addAll(mapping_manager.getLogMapMappings().get(cls1));
				}
			}
		}
		
	
		
		same_domain=haveSameDomain(mapped_domain1, domain2);
		same_range=haveSameRange(range1, range2);
		
		
		//TODO Identify if rdf:Literal
		if (range1.size()==1 && range1.contains("Literal")){
			same_range=true;
		}		
		else if (range2.size()==1 && range2.contains("Literal")){
			same_range=true;
		}
		
		
		//Same sets in one of teh sides at least
		if (same_domain && same_range){
			return SAME_RANGE_AND_DOMAIN;
		}
		//If different range --> incompatible
		else if (same_range){ //same range
			
			//All possible mappings must be compatible (i.e. same hierarchy)
			for (int cls1 : domain1){
				for (int cls2 : domain2){
					if (mapping_manager.isMappingInConflictWithFixedMappings(cls1, cls2)){
						return INCOMPATIBLE_RANGE_OR_DOMAIN;
					}						
				}
			}
			
			//Compatible ranges or domains?? Dangerous?
			return COMPATIBLE_RANGE_DOMAIN;
			//return INCOMPATIBLE_RANGE_OR_DOMAIN;
			
		}
		
		
		//Different ranges!
		return INCOMPATIBLE_RANGE_OR_DOMAIN;
		
		
		


		
		
	}
	
	
	
	
	
	
	
	/*protected boolean haveSameRange(Set<String> range1, Set<String> range2){
		
		if (range1.size()>0 && range2.size()>0){			
			return range1.equals(range2);
		}
		
		return false;				
	}*/

}

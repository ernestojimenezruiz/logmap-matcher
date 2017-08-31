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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IndividualIndex  extends EntityIndex{
	
	protected Set<Integer> class_types = new HashSet<Integer>();
	
	protected Set<String> categories = new HashSet<String>();
	
	private Set<String> alternativeLabels;
	
	private boolean showInOutput=true;
	
	//THis list will identify characteristics of an individual, e.g.: num plublications, num awards, ... 
	protected List<Integer> characteristics = new ArrayList<Integer>();
	
	
	private Set<Integer> referencedIndividuals  = new HashSet<Integer>();
	
	
	public IndividualIndex(int i){		
		index=i;				
	}
	
	
	public Set<Integer> getClassTypes(){
		return class_types;
	}
	
	public void addClassTypeIndex(int icls){
		class_types.add(icls);
	}
	
	
	public Set<String> getCategories(){
		return categories;
	}
	
	public void addCategory(String cat){
		categories.add(cat);
	}
	
	
	/**
	@deprectaed
	**/
	public boolean showInOutput(){
		return showInOutput;
	}
	
	public void setShowInOutput(boolean showInOutput){
		this.showInOutput=showInOutput;
	}
	
	
	public boolean hasDirectClassTypes(){
		return !class_types.isEmpty();		
	}
	
	
	
	public void addAlternativeLabel(String altLabel){
		
		if (alternativeLabels==null)
			alternativeLabels=new HashSet<String>();
		
		alternativeLabels.add(altLabel);
		
	}
	
	public void setAlternativeLabels(Set<String> altLabels){
		
		alternativeLabels=new HashSet<String>(altLabels);
		
	}
	
	
	public void setEmptyAlternativeLabels(){		
		alternativeLabels=new HashSet<String>();		
	}
	
	
	public List<Integer> getCharacteristics(){
		return characteristics;
	}
	
	
	
	
	/**
	 * Set of alternative labels or synonyms
	 * @return
	 */
	public Set<String> getAlternativeLabels(){
		if (alternativeLabels==null)
			return Collections.emptySet();
		
		return alternativeLabels;
	}
	
	public boolean hasAlternativeLabels(){
		if (alternativeLabels==null)
			return false;
		return true;		
	}


	public Set<Integer> getReferencedIndividuals() {
		return referencedIndividuals;
	}


	public void setReferencedIndividuals(Set<Integer> referencedIndividuals) {
		this.referencedIndividuals = referencedIndividuals;
	}
	
	
	public void addReferencedIndividuals(int index) {
		this.referencedIndividuals.add(index);
	}


	public void addCharacteristic(int value) {
		this.characteristics.add(value);
		
	}
	
	
	
	
	
	
	

}

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
package uk.ac.ox.krr.logmap2.interactive.objects;

import uk.ac.ox.krr.logmap2.mappings.MappingManager;
import uk.ac.ox.krr.logmap2.mappings.objects.MappingObject;
import uk.ac.ox.krr.logmap2.utilities.Utilities;

import java.util.*;

public class MappingObjectInteractivity extends MappingObject{
	
	
	private boolean removedFlag=false; //used in heuristics and interactivity
	private boolean addedFlag=false; //used in interactivity only
	//Important to assess weakened mappings by DandG
	//Moreover the user may also decide to split the mapping	
	private int dirMapping; 
	
	private boolean assessedDirectlyFlag=false; 
	
	//private boolean disjointFlag=false; //used in automatic-interactivity only
	//private boolean inGSFlag=false; //used in automatic-interactivity only
	
	protected List<Double> similarityList=new ArrayList<Double>();
	
	private double m_score;

	private double scope = 0.0;
	private double lexSim = 0.0;
	
	
	//They will point to the "list" of mappings
	//For precomputed conflicts
	private Set<Integer> mappingsInConflict = new HashSet<Integer>();
	private Set<Integer> ambiguousMappings = new HashSet<Integer>();
	
	
	
	public MappingObjectInteractivity(int ide1, int ide2){
		
		this(ide1, ide2, Utilities.EQ);
		
	}
	
	public MappingObjectInteractivity(int ide1, int ide2, int dirMapping){
		
		ident_onto1 = ide1;
		ident_onto2 = ide2;
		this.setDirMapping(dirMapping);
		
	}
	
	public MappingObjectInteractivity(int ide1, int ide2, int dirMapping, double scope, double lexSim){
		
		ident_onto1 = ide1;
		ident_onto2 = ide2;
		this.setDirMapping(dirMapping);		
		this.scope = scope;
		this.lexSim = lexSim;
		
	}

	
	
	public double getScope(){
		return scope;
	}
	
	
	public double getLexSim(){
		return lexSim;
	}
	
	
	

	public boolean isRemovedFlagActive() {
		return removedFlag;
	}


	public void setRemovedFlag(boolean removedFlag) {
		this.removedFlag = removedFlag;
		if (removedFlag)
			this.addedFlag = !removedFlag;
	}


	public boolean isAddedFlagActive() {
		return addedFlag;
	}


	public void setAddedFlag(boolean addedFlag) {
		this.addedFlag = addedFlag;
		if (addedFlag)
			this.removedFlag = !addedFlag;
	}
	
	
	public boolean hasDecision() {
		return addedFlag || removedFlag;
	}
	

	public boolean isAssessedFlagActive() {
		return assessedDirectlyFlag;
	}
	
	public void setAssessedFlag(boolean assessedFlag) {
		this.assessedDirectlyFlag = assessedFlag;	
	}


	/**
	 * @return the similarity
	 */
	public List<Double> getSimilarityList() {
		return similarityList;
	}

	
	/**
	 * @return the similarity
	 */
	public void addSimilarityValue2List(double value) {
		similarityList.add(value);
	}

	
	public void setScore(MappingManager manager)
	{
		m_score = manager.getSimWeak4Mapping(ident_onto1, ident_onto2);
		
	}
	
	
	
	
	
	
	public double getScore()
	{
		return m_score;
	}

	private boolean inGSMappings = false;
	
	public void setInGSMappings()
	{
		inGSMappings = true;
	}
	
	public boolean isInGSMappings()
	{
		return inGSMappings;
	}

	/**
	 * @return the dirMapping
	 */
	public int getDirMapping() {
		return dirMapping;
	}

	/**
	 * @param dirMapping the dirMapping to set
	 */
	public void setDirMapping(int dirMapping) {
		this.dirMapping = dirMapping;
	}
	
	
	
	public Set<Integer> getMappingsInconflict(){
		return mappingsInConflict;
	}
	
	public Set<Integer> getAmbiguousMappings(){
		return ambiguousMappings;
	}

	
	
	public void addConflictiveMapping(int mapping_id){
		mappingsInConflict.add(mapping_id);
	}
	
	public void addAmbiguousMapping(int mapping_id){
		ambiguousMappings.add(mapping_id);
	}
	
	
}

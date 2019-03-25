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
package uk.ac.ox.krr.logmap2.mappings;

import java.util.ArrayList;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.ac.ox.krr.logmap2.Parameters;
import uk.ac.ox.krr.logmap2.indexing.IndexManager;
import uk.ac.ox.krr.logmap2.io.LogOutput;
import uk.ac.ox.krr.logmap2.io.OutPutFilesManager;
import uk.ac.ox.krr.logmap2.interactive.objects.MappingObjectInteractivity;
import uk.ac.ox.krr.logmap2.mappings.objects.MappingObjectStr;
import uk.ac.ox.krr.logmap2.utilities.Utilities;


/**
 * This class defines the basic methods to provide the anchors between two ontologies
 * Mappings have been used to index mappings and their direction then the entry ide_a:{id_b, id_bb} will 
 * represent the mappings ide_a->ide_b and ide_a->ide_bb (with the exception of structures representing equivalence mappings.) 
 * 
 * 
 * @author ernesto
 *
 */
public abstract class MappingManager {
	
	long init, fin;

	protected IndexManager index;
	
	/**Necessary for statistic purposes*/
	protected Map<Integer, Set<Integer>> GSMappings1N = new HashMap<Integer, Set<Integer>>();
	
	/**Anchor mappings. They represent "safe" mappings, high lexical similarity and with scope.
	 * They will be used as reference to asses/clean other candidates. We store mappings and implicitly the dir.*/
	protected Map<Integer, Set<Integer>> anchorMappings1N = new HashMap<Integer, Set<Integer>>();
	
	
	/** LogMap Mappings: they grow from anchors to the final set of output mappings **/
	protected Map<Integer, Set<Integer>> logmapMappings1N = new HashMap<Integer, Set<Integer>>();
	
	/** Mappings from IF exact which must be assessed; or "repechaged" from 2ask (logmap or user)*/
	protected Map<Integer, Set<Integer>> mappings2Review = new HashMap<Integer, Set<Integer>>();
	
	
	
	/**We store mappings and implicitely the dir. MAPPINGS TO BE ASKED TO LOGMAP AMBIGUITY HEURISTICS*/
	protected Map<Integer, Set<Integer>> candidateMappings2askLogMap1N = new HashMap<Integer, Set<Integer>>();
	
	
	/**We store mappings equiv mappings*/
	protected Map<Integer, Set<Integer>> candidateMappings2askUser1N = new HashMap<Integer, Set<Integer>>();
	
	
	/**List of mappings objects. Mappings to ask to the user*/	
	//protected List<MappingObjectInteractivity> listMappings2askUser1N = new ArrayList<MappingObjectInteractivity>();
	protected Set<MappingObjectInteractivity> listMappings2askUser1N = new HashSet<MappingObjectInteractivity>();
	

	/**Split mappings with Dawling and Gallier**/
	protected Map<Integer, Set<Integer>> weakenedDandG_Mappings1N = new HashMap<Integer, Set<Integer>>();
	
	/** Discarded mappings by LogMap*/
	protected Map<Integer, Set<Integer>> discardedMappings1N = new HashMap<Integer, Set<Integer>>();
	
	/** Hard Discarded mappings by LogMap, should not be retrieved*/
	protected Map<Integer, Set<Integer>> hardDiscardedMappings1N = new HashMap<Integer, Set<Integer>>();
	
	/** Mappings selected by user (only interactive process)*/
	protected Map<Integer, Set<Integer>> interactiveProcessMappings1N = new HashMap<Integer, Set<Integer>>();
	
	/**Mappings that cause an error with D&G or are in conflict with exact mappings wrt interval labelling schema*/
	protected Map<Integer, Set<Integer>> conflictiveMappings1N = new HashMap<Integer, Set<Integer>>();
	
	/**Only mappings that cause an error with D&G**/
	protected Map<Integer, Set<Integer>> conflictiveMappings1N_D_G = new HashMap<Integer, Set<Integer>>();
	
	
	/**Subset of weak mappings to be considered as candidates**/
	protected Map<Integer, Set<Integer>> weakCandidateMappings1N = new HashMap<Integer, Set<Integer>>();
	
	
	/**All possible mappings. Will be used for scope purposes*/ 
	protected Map<Integer, Set<Integer>> allWeakMappings1N = new HashMap<Integer, Set<Integer>>();
	
	
	protected Map<Integer, Integer> dataPropertyMappings = new HashMap<Integer, Integer>();
	protected Map<Integer, Integer> objPropertyMappings = new HashMap<Integer, Integer>();
	
	protected Map<Integer, Double> dataPropertyMappings2confidence = new HashMap<Integer, Double>();
	protected Map<Integer, Double> objPropertyMappings2confidence = new HashMap<Integer, Double>();
	
	private Map<Integer, Map<Integer,Double>> anchor2scopeAll = new HashMap<Integer, Map<Integer,Double>>();
	private Map<Integer, Map<Integer,Double>> anchor2scopeAnchors = new HashMap<Integer, Map<Integer,Double>>();
	private Map<Integer, Map<Integer,Double>> anchor2isub = new HashMap<Integer, Map<Integer,Double>>();
	private Map<Integer, Map<Integer,Double>> anchor2isub_avg = new HashMap<Integer, Map<Integer,Double>>();
	private Map<Integer, Map<Integer,Double>> anchor2sim_weak = new HashMap<Integer, Map<Integer,Double>>();
	private Map<Integer, Set<Integer>> anchor2addedByUser = new HashMap<Integer, Set<Integer>>();
	

	protected Map<Integer, Set<Integer>> instanceMappings1N = new HashMap<Integer, Set<Integer>>();
	protected Map<Integer, Set<Integer>> instanceMappings1N_inverted = new HashMap<Integer, Set<Integer>>(); //only for queries and statistic purposes
	protected Map<Integer, Map<Integer,Double>> instanceMappings2isub = new HashMap<Integer, Map<Integer,Double>>();
	protected Map<Integer, Map<Integer,Double>> instanceMappings2compfactor = new HashMap<Integer, Map<Integer,Double>>();
	protected Map<Integer, Map<Integer,Double>> instanceMappings2scope = new HashMap<Integer, Map<Integer,Double>>();
	
	protected Map<Integer, Set<Integer>> instanceMappings1N_not_allowed_output = new HashMap<Integer, Set<Integer>>();
	
	protected Map<Integer, Set<Integer>> instanceMappings1N_ambiguity  = new HashMap<Integer, Set<Integer>>();
	
	
	//0: ok, 1: disc1, 2: disc2, 3: incompatible
	protected Map<Integer, Map<Integer,Integer>> instanceMappings2outputtype = new HashMap<Integer, Map<Integer,Integer>>();
	
	
	/**For statictics purposes*/
	protected Set<MappingObjectStr> discarded_candidates = new HashSet<MappingObjectStr>();
	
	/** For P&R purposes*/
	protected Set<MappingObjectStr> mappings_candidates_Str = new HashSet<MappingObjectStr>();
	protected Set<MappingObjectStr> mappings_GS_Str = new HashSet<MappingObjectStr>();
	
	
	/** Category mappings (relevant for instance matching) **/
	protected Map<String, Set<String>> category_mappings = new HashMap<String, Set<String>>();
	
	
	double weightIsub=0.5;
	double weightIsub_avg=0.0;//not used
	double weightScope=0.3;
	double weightScopeAnchors=0.2;
	
	I_Sub isub = new I_Sub();
	
	
	private boolean areExactFixed=false;
	
	private boolean filterWithHeuristicsInteractivity = true;
	
	
	//Statistical purposes (DISCARDED)
	//Type 0
	protected int goodAmb4=0;
	protected int badAmb4=0;
	//Type 1
	protected int goodDangEquiv=0;
	protected int badDangEquiv=0;
	//Type 2
	protected int goodAmbigExact=0;
	protected int badAmbigExact=0;
	//Type 3
	protected int goodConfidence=0;
	protected int badConfidence=0;
	
	
	
	
	
	
	
	protected void isHardMappingInGS(int ide1, int ide2, int type){
		if (isMappingInGoldStandard(ide1, ide2)){
			switch(type){
				case 0: 
					goodAmb4++;
					break;
				case 1:
					goodDangEquiv++;
					break;
				case 2:
					goodAmbigExact++;
					break;
				case 3:
					goodConfidence++;
					break;
			}
		}
		else {
			switch(type){
				case 0: 
					badAmb4++;
					break;
				case 1:
					badDangEquiv++;
					break;
				case 2:
					badAmbigExact++;
					break;
				case 3:
					badConfidence++;
					break;
			}
		}
	}
	
	
	public void printHarDiscardedStatistics(){
		
		LogOutput.print("Good Amb 4: " + goodAmb4);
		LogOutput.print("Bad Amb 4: " + badAmb4);
		
		LogOutput.print("Good DangEqui: " + goodDangEquiv);
		LogOutput.print("Bad DangEqui: " + badDangEquiv);
		
		LogOutput.print("Good AmbigExact: " + goodAmbigExact);
		LogOutput.print("Bad AmbigExact: " + badAmbigExact);
		
		LogOutput.print("Good Confidence: " + goodConfidence);
		LogOutput.print("Bad Confidence: " + badConfidence);
		
	}
	
	
	/**
	 * If no interactivity, mappings are filtered using automatic heuristics, 
	 * if this flag is set to true. Otherwise, former mappings 2 ask user are 
	 * added automatically
	 * @return the filterWithHeuristicsInteractivity
	 */
	public boolean isFilterWithHeuristicsSecondLevelMappings() {
		return filterWithHeuristicsInteractivity;
	}


	/**
	 * If no interactivity, mappings are filtered using automatic heuristics, 
	 * if this flag is set to true. Otherwise, former mappings 2 ask user are 
	 * added automatically
	 * @param filterWithHeuristicsInteractivity the filterWithHeuristicsInteractivity to set
	 */
	public void setFilterWithHeuristicsSecondLevelMappings(
			boolean filterWithHeuristicsInteractivity) {
		this.filterWithHeuristicsInteractivity = filterWithHeuristicsInteractivity;
	}


	//Not necessary for every extractor
	public void intersectInvertedFiles(){
		
	}
	
	
	public abstract void processInputMappings(Set<MappingObjectStr> mappings);
	
	/*public void clearIntersectedInvertedFilesExact(){
	}
	
	public void clearIntersectedInvertedFilesStemmingAndWeak(){
	}
	
	public void clearIntersectedInvertedFiles4Properties(){
		
	}*/
	
	public void assesWeakenedMappingsDandG2(boolean removeAfterwards, boolean add2Anchors){
		//weakenedDandG_Mappings1N
	}
	
	public void assessMappings2AskUser(){
		
	}
	
	
	
	public abstract void createAnchors();
	
	public abstract void createCandidates();	
	
	
	public abstract void createDataPropertyAnchors();
	
	public abstract void createObjectPropertyAnchors();
	
	public abstract void createInstanceAnchors();
	
	public abstract void extractAllWeakMappings();
	
	public abstract void extractCandidatesSubsetFromWeakMappings();
	
	
	
	public int getEntityAmbiguity_LogMapMappings(int ide){
		// Side 
		int amb=0;
		
		if (getLogMapMappings().containsKey(ide)){
			amb += getLogMapMappings().get(ide).size();
		}
		
		if (getToAskLogMapMappings().containsKey(ide)){
			amb += getToAskLogMapMappings().get(ide).size();
		}
		
		return amb;
			
	}
	
	public int getEntityAmbiguity_UserMappings(int ide){
		// Side 
		int amb=0;
		
		if (getLogMapMappings().containsKey(ide)){
			amb += getLogMapMappings().get(ide).size();
		}
		
		if (getToAskUserMappings().containsKey(ide)){
			amb += getToAskUserMappings().get(ide).size();
		}
		
		return amb;
			
	}
	
	
	
	/*public Set<Integer> getMappedEntitiesOnto1(){
		return mapped_entities_onto1;
	}
	
	public Set<Integer> getMappedEntitiesOnto2(){
		return mapped_entities_onto2;
	}*/
	
	
	public boolean isEntityAlreadyMapped(int index){
		return logmapMappings1N.containsKey(index); //If the entry was deleted if should be then in discarded entries!!!
	}
	
	
	
	public boolean isEntityInCandidates2AskUser(int index){
		return candidateMappings2askUser1N.containsKey(index); 
	}
	
	public boolean isEntityInCandidates2AskLogMap(int index){
		return candidateMappings2askLogMap1N.containsKey(index); 
	}
	
	/**
	 * This method must only be used when only exact mappings are considered since treats the maps as 1 to 1  
	 * @param index
	 * @return
	 */
	public int getTargetEntity4Index(int index){
		if (logmapMappings1N.containsKey(index)){
			for (int ide2 : logmapMappings1N.get(index))
				return ide2;
		}
			
		return -1;//in case there is no entity
	}
	
	
	public Set<Integer> getTargetEntities4Index(int index){
		if (logmapMappings1N.containsKey(index)){
			return logmapMappings1N.get(index);
		}
			
		return Collections.emptySet();
	}
	

	public void removeSubMappingFromStructure(int ide1, int ide2){ //only one direction
		if (logmapMappings1N.containsKey(ide1)){
			logmapMappings1N.get(ide1).remove(ide2);
			if (logmapMappings1N.get(ide1).size()==0){
				logmapMappings1N.remove(ide1);
			}
		}
	}
	
	
	public void removeSubMappingFromMappings2Review(int ide1, int ide2){ //only one direction
		if (mappings2Review.containsKey(ide1)){
			mappings2Review.get(ide1).remove(ide2);
			if (mappings2Review.get(ide1).size()==0){
				mappings2Review.remove(ide1);
			}
		}
		
	}
	
	
	
	public void removeSubMappingFromConflictive_D_G(int ide1, int ide2){ //only one direction
		if (conflictiveMappings1N_D_G.containsKey(ide1)){
			conflictiveMappings1N_D_G.get(ide1).remove(ide2);
			if (conflictiveMappings1N_D_G.get(ide1).size()==0){
				conflictiveMappings1N_D_G.remove(ide1);
			}
		}
		
	}
	
	
	
	
	
	
	
	public void removeSubMappingFromWeakenedDandGMappings(int ide1, int ide2){ //only one direction
		if (weakenedDandG_Mappings1N.containsKey(ide1)){
			weakenedDandG_Mappings1N.get(ide1).remove(ide2);
			if (weakenedDandG_Mappings1N.get(ide1).size()==0){
				weakenedDandG_Mappings1N.remove(ide1);
			}
		}
	}
	
	
	public void removeSubMappingFromDiscarded(int ide1, int ide2){ //only one direction
		if (discardedMappings1N.containsKey(ide1)){
			discardedMappings1N.get(ide1).remove(ide2);
			if (discardedMappings1N.get(ide1).size()==0){
				discardedMappings1N.remove(ide1);
			}
		}
	}
	
	/**
	 * Removes mappings and adds it to ambiguosmappimgs
	 * @param ide1
	 * @param ide2
	 */
	protected void removeAmbiguousFullMappingFromStructure(int ide1, int ide2, double min_average_isub){
		
		removeSubMappingFromStructure(ide1, ide2);
		removeSubMappingFromStructure(ide2, ide1);
		
		if (extractISUBAverage4Mapping(ide1, ide2) < min_average_isub){
			addEquivMapping2DiscardedAnchors(ide1, ide2);
		}
		else{
			addMapping2ListOfAnchors2AskLogMap(ide1, ide2);
		}
	}
	
	
	/**
	 * Removes mappings and adds it to mappings2review
	 * @param ide1
	 * @param ide2
	 */
	protected void moveMapping2ReviewList(int ide1, int ide2){
		
		removeSubMappingFromStructure(ide1, ide2);
		removeSubMappingFromStructure(ide2, ide1);
		
		//OJO!!
		//addEquivMapping2Mappings2Review(ide2, ide1);
		addSubMapping2Mappings2Review(ide1, ide2);//only one side
	
	}
	
	
	/*
	public boolean isEntityAlreadyMappedOnto2(int index){
		return mapped_entities_onto2.contains(index);
	}*/
	
	
	
	public void saveAnchors() {
		anchorMappings1N.putAll(logmapMappings1N);
	}
	
	
	
	public void setExactAsFixed(boolean set){
		areExactFixed=set;
	}
	
	public Map<Integer, Set<Integer>> getFixedMappings(){
		if (areExactFixed)
			return logmapMappings1N;
		else
			return Collections.EMPTY_MAP;
	}
	
	
	/**
	 * Highly precise mappings
	 * @return
	 */
	public Map<Integer, Set<Integer>> getAnchors(){
		return anchorMappings1N;
	}
	
	
	public Map<Integer, Set<Integer>> getLogMapMappings(){
		return logmapMappings1N;
	}
	
	
	public int getDirMapping(int ide1, int ide2){
		
		if (isId1SmallerThanId2(ide1, ide2)){
			
			if (isMappingAlreadyInList(ide2, ide1)){
				return Utilities.EQ;
			}
			else {
				return Utilities.L2R;
			}
		}
		else {
			
			if (isMappingAlreadyInList(ide2, ide1)){
				return Utilities.NoMap; //Already considered
			}
			else {
				return Utilities.R2L;
			}
		}
	}
	
	
	public Map<Integer, Set<Integer>> getMappings2Review(){
		return mappings2Review;
	}
	
	
	public Map<Integer, Set<Integer>> getToAskUserMappings(){
		return candidateMappings2askUser1N;
	}
	
	
	
	//public List<MappingObjectInteractivity> getListOfMappingsToAskUser(){
	public Set<MappingObjectInteractivity> getListOfMappingsToAskUser(){
		return listMappings2askUser1N;
	}
	
	
	public void addMappingObject2AskUserList(int index1, int index2){
		//listMappings2askUser1N.add(new MappingObjectInteractivity(index1, index2, ambiguity));
		listMappings2askUser1N.add(new MappingObjectInteractivity(
				index1, index2, Utilities.EQ, extractScopeAll4Mapping(index1, index2), extractISUB4Mapping(index1, index2)));
		
		
	}
	
	
	public void addMappingObject2AskUserList(int index1, int index2, int diMapping){
		//listMappings2askUser1N.add(new MappingObjectInteractivity(index1, index2, ambiguity));
		listMappings2askUser1N.add(new MappingObjectInteractivity(
				index1, index2, diMapping, extractScopeAll4Mapping(index1, index2), extractISUB4Mapping(index1, index2)));
	}
	
	
	
	public Map<Integer, Set<Integer>> getToAskLogMapMappings(){
		return candidateMappings2askLogMap1N;
	}
	
	
	
	
	
	public Map<Integer, Set<Integer>> getWeakAnchors(){
		return allWeakMappings1N;
	}
	
	public Map<Integer, Set<Integer>> getWeakCandidateAnchors(){
		return weakCandidateMappings1N;
	}
	
	
	public void clearWeakCandidateAnchors(){
		weakCandidateMappings1N.clear();
	}
	
	
	
	public Map<Integer, Integer> getDataPropertyAnchors(){
		return dataPropertyMappings;
	}
	
	public Map<Integer, Integer> getObjectPropertyAnchors(){
		return objPropertyMappings;
	}
	
	
	public Map<Integer, Set<Integer>> getInstanceMappings(){
		return instanceMappings1N;
	}
	
	/**
	 * Only for statistics
	 */
	public Map<Integer, Map<Integer,Integer>> getInstanceMappings4OutputType(){
		return instanceMappings2outputtype;
	}
	
	
	
	public void addDataPropertyAnchor(int ide1, int ide2){
		dataPropertyMappings.put(ide1, ide2);
	}
	
	
	public void addObjectPropertyAnchor(int ide1, int ide2){
		objPropertyMappings.put(ide1, ide2);
	}
	
	public void addDataPropertyAnchorConfidence(int ide1, double conf){
		dataPropertyMappings2confidence.put(ide1, conf);
	}
	
	
	public void addObjectPropertyAnchorConfidence(int ide1, double conf){
		objPropertyMappings2confidence.put(ide1, conf);
	}
	
	
	public void addInstanceAnchorConfidence(int ide1, int ide2, double conf){
		addIsub4Indivual(ide1, ide2, conf);
		addIsub4Indivual(ide2, ide1, conf);
	}
	
	
	
	
	
	
	
	
	public Map<Integer, Set<Integer>> getInstanceMappings_inverted(){
		return instanceMappings1N_inverted;
	}
	
	
	public int getSizeOfInstanceMappings(){
		int num=0;
		for (int ide1 : instanceMappings1N.keySet()){
			//for (int ide2 : instanceMappings1N.get(ide1)){	
			//	num++;
			//}
			num+=instanceMappings1N.get(ide1).size();
			
			/*if (instanceMappings1N.get(ide1).size()>1){
				//Ambiguity
				LogOutput.print("AMBIGUITY: " + index.getAlternativeLabels4IndividualIndex(ide1).toString());
				for (int ide2 : instanceMappings1N.get(ide1)){
					LogOutput.print("\t" +index.getAlternativeLabels4IndividualIndex(ide2).toString());
				}
				
			}*/
		}
		return num;
	}
	
	
	public boolean isIndividualAlreadyMapped(int ide){
		
		return instanceMappings1N.containsKey(ide) || instanceMappings1N_inverted.containsKey(ide) || instanceMappings1N_not_allowed_output.containsKey(ide) || instanceMappings1N_ambiguity.containsKey(ide);
		
	}
	
	
	
	public double getConfidence4DataPropertyAnchor(int ident1, int ident2){
		//return dataPropertyMappings2confidence.get(ident1); //currently mappings are only one 2 one
		if (dataPropertyMappings2confidence.containsKey(ident1)){
			return dataPropertyMappings2confidence.get(ident1); //currently mappings are only one 2 one
		}
		return 0.80;
	}
	
	public double getConfidence4ObjectPropertyAnchor(int ident1, int ident2){
		//return objPropertyMappings2confidence.get(ident1); //currently mappings are only one 2 one
		if (objPropertyMappings2confidence.containsKey(ident1)){
			return objPropertyMappings2confidence.get(ident1); //currently mappings are only one 2 one
		}
		return 0.80;
	}
	
	/**
	 * Returns ISUb confidence
	 * @param ident1
	 * @param ident2
	 * @return
	 */
	public double getConfidence4InstanceMapping(int ident1, int ident2){
		if (instanceMappings2isub.containsKey(ident1) && instanceMappings2isub.get(ident1).containsKey(ident2)){
			return instanceMappings2isub.get(ident1).get(ident2);
		}
		return 0.70; //we return something just in case
	}
	
	public double getISUB4InstanceMapping(int ident1, int ident2){
		if (instanceMappings2isub.containsKey(ident1) && instanceMappings2isub.get(ident1).containsKey(ident2)){
			return instanceMappings2isub.get(ident1).get(ident2);
		}
		return 0.70;
	}
	
	public double getCompFactor4InstanceMapping(int ident1, int ident2){
		if (instanceMappings2compfactor.containsKey(ident1) && instanceMappings2compfactor.get(ident1).containsKey(ident2)){
			return instanceMappings2compfactor.get(ident1).get(ident2);
		}
		return 0.50;
	}
	
	public double getScope4InstanceMapping(int ident1, int ident2){
		if (instanceMappings2scope.containsKey(ident1) && instanceMappings2scope.get(ident1).containsKey(ident2)){
			return instanceMappings2scope.get(ident1).get(ident2);
		}
		return 0.50;
	}
	
	
	public double getOutputType4InstanceMapping(int ident1, int ident2){
		if (instanceMappings2outputtype.containsKey(ident1) && instanceMappings2outputtype.get(ident1).containsKey(ident2)){
			return instanceMappings2outputtype.get(ident1).get(ident2);
		}
		return 0;
	}
	
	
	/*public List<MappingObjectIdentifiers> getAnchorObjects(){
		return identifier2anchor;
	}*/
	
	
	public Set<MappingObjectStr> getStringLogMapMappings(){
		return mappings_candidates_Str;
	}
	
	public Set<MappingObjectStr> getStringGoldStandardAnchors(){
		return mappings_GS_Str;
	}
	
	
	public Map<Integer, Set<Integer>> getDiscardedMappings(){
		return discardedMappings1N;
	}
	
	public Map<Integer, Set<Integer>> getHardDiscardedMappings(){
		return hardDiscardedMappings1N;
	}
	
	public Map<Integer, Set<Integer>> getMappingsInteractiveProcess(){
		return interactiveProcessMappings1N;
	}
	
	
	
	public Map<Integer, Set<Integer>> getConflictiveMappings(){
		return conflictiveMappings1N;
	}
	
	public Map<Integer, Set<Integer>> getWeakenedDandGMappings(){
		return weakenedDandG_Mappings1N;
	}
	
	public Set<MappingObjectStr> getDiscardedMappingsStr(){
		return discarded_candidates;
	}
	
	
	/**
	 * We add each direcction individually
	 * @param index1
	 * @param index2
	 */
	public void addSubMapping2ListOfAnchors(int index1, int index2){
		
		//mapped_entities_onto1.add(index1);
		//mapped_entities_onto2.add(index2);
		
		//Deprecated: We can implicitely store the dir in map!!
		//We need this structure to store the direction (type) of the mappings
		//MappingObjectIdentifiers mapping = new MappingObjectIdentifiers(index1, index2);
		//if (!identifier2anchor.contains(mapping)){
		//	identifier2anchor.add(mapping);
		//}
		
		if (!logmapMappings1N.containsKey(index1)){
			logmapMappings1N.put(index1, new HashSet<Integer>());
		}
		logmapMappings1N.get(index1).add(index2);
		
	}
	
	
	
	public void addInstanceMapping(int index1, int index2){
		addInstanceMapping(index1, index2, false);
	}
	
	
	
	/**
	 * Check if the instance is an allowed type
	 * @param types
	 * @param allowed_types
	 * @return
	 */
	private boolean hasAllowedType(Set<Integer> types){
		
		//No restrictions or empty set of allowed types
		if (!Parameters.isRestrictInstanceTypesActive() || index.getAllowedInstanceTypes().isEmpty())
			return true;
		
		
		for (int itype : types){
			//is class type listed as allowed
			if (index.getAllowedInstanceTypes().contains(itype))
				return true;
			
			for (int atype : index.getAllowedInstanceTypes()){
				//Is class type as subclass of allowed types 
				if (index.isSubClassOf(itype, atype))
						return true;
			}
		}
		
		return false;
		
	}
	
	/**
	 * We deal instance mappings as one side mappings. We only consider SameAs. 
	 * If during cleaning they are deleted then we do not consider mapping
	 * @param index1
	 * @param index2
	 */
	public void addInstanceMapping(int index1, int index2, boolean ambiguity){
			
		
		//If type is not allowed in the output
		//Old code
		//if (!index.getIdentifier2IndividualIndexMap().get(index1).showInOutput() ||
		//	!index.getIdentifier2IndividualIndexMap().get(index2).showInOutput()){
		//New code: August 28, 2017
		if (!hasAllowedType(index.getIdentifier2IndividualIndexMap().get(index1).getClassTypes()) ||
			!hasAllowedType(index.getIdentifier2IndividualIndexMap().get(index2).getClassTypes())){			
			
			if (!instanceMappings1N_not_allowed_output.containsKey(index1)){
				instanceMappings1N_not_allowed_output.put(index1, new HashSet<Integer>());
			}
			instanceMappings1N_not_allowed_output.get(index1).add(index2);
			
			
			return;
			
		}
		
		
		//We add to different structure for future revision
		if (ambiguity){
			
			if (!instanceMappings1N_ambiguity.containsKey(index1)){
				instanceMappings1N_ambiguity.put(index1, new HashSet<Integer>());
			}
			instanceMappings1N_ambiguity.get(index1).add(index2);
			
			
			return;
			
		}
		
		
		
		if (!instanceMappings1N.containsKey(index1)){
			instanceMappings1N.put(index1, new HashSet<Integer>());
		}
		instanceMappings1N.get(index1).add(index2);
		
		
		
		//Only for queries
		if (!instanceMappings1N_inverted.containsKey(index2)){
			instanceMappings1N_inverted.put(index2, new HashSet<Integer>());
		}
		instanceMappings1N_inverted.get(index2).add(index1);
		
	}
	
	
	
	/**
	 * By user in interactivity
	 * @param index1
	 * @param index2
	 * @param add2conflictive
	 */
	public void removeInstanceMappings(int index1, int index2){
		
		if (instanceMappings1N.containsKey(index1)){
			instanceMappings1N.get(index1).remove(index2);
			if (instanceMappings1N.get(index1).size()==0){
				instanceMappings1N.remove(index1);
			}
		}
	}
	
	
	
	public void addSubMapping2Mappings2Review(int index1, int index2){
		
		if (!mappings2Review.containsKey(index1)){
			mappings2Review.put(index1, new HashSet<Integer>());
		}
		mappings2Review.get(index1).add(index2);
	}
	
	
	public void moveMappingsToReview2AnchorList(){
		
		for (int ide1 : mappings2Review.keySet()){
			for (int ide2 : mappings2Review.get(ide1)){
				
				addSubMapping2ListOfAnchors(ide1, ide2);
				
			}
			
		}
		
	}
	
	
	
	
	
	
	
	
	/**
	 * We add equivalence mappings
	 * @param index1
	 * @param index2
	 */
	public void addMapping2ListOfAnchors2AskLogMap(int index1, int index2){
		
		if (!candidateMappings2askLogMap1N.containsKey(index1)){
			candidateMappings2askLogMap1N.put(index1, new HashSet<Integer>());
		}
		candidateMappings2askLogMap1N.get(index1).add(index2);
		
		
		//Add both sides for statistical purposes
		if (!candidateMappings2askLogMap1N.containsKey(index2)){
			candidateMappings2askLogMap1N.put(index2, new HashSet<Integer>());
		}
		candidateMappings2askLogMap1N.get(index2).add(index1);
		
	}
	
	
	
	/**
	 * We add equivalence mappings
	 * @param index1
	 * @param index2
	 */
	public void addEquivMapping2ListOfAnchors2AskUser(int index1, int index2){
		
		if (!candidateMappings2askUser1N.containsKey(index1)){
			candidateMappings2askUser1N.put(index1, new HashSet<Integer>());
		}
		candidateMappings2askUser1N.get(index1).add(index2);
		
		//Add both sides for statistical purposes
		if (!candidateMappings2askUser1N.containsKey(index2)){
			candidateMappings2askUser1N.put(index2, new HashSet<Integer>());
		}
		candidateMappings2askUser1N.get(index2).add(index1);
			
	}
	
	

	
	
	/**
	 * By user in interactivity
	 * @param index1
	 * @param index2
	 * @param add2conflictive
	 */
	public void removeEquivMappingFromListOfAnchors2AskUser(int index1, int index2){
		
		if (candidateMappings2askUser1N.containsKey(index1)){
			candidateMappings2askUser1N.get(index1).remove(index2);
			if (candidateMappings2askUser1N.get(index1).size()==0){
				candidateMappings2askUser1N.remove(index1);
			}
		}
	}
	
	
	
	/**
	 * By user in interactivity
	 * @param index1
	 * @param index2
	 * @param add2conflictive
	 */
	public void removeEquivMappingFromListOfAnchors2AskUser(int index1, int index2, boolean add2conflictive){
		
		if (candidateMappings2askUser1N.containsKey(index1)){
			candidateMappings2askUser1N.get(index1).remove(index2);
			if (candidateMappings2askUser1N.get(index1).size()==0){
				candidateMappings2askUser1N.remove(index1);
			}
		}
		
		if (add2conflictive){
			addSubMapping2ConflictiveAnchors(index1, index2);
			addSubMapping2ConflictiveAnchors(index2, index1);
		}
		else{
			addEquivMapping2DiscardedAnchors(index1, index2);
			addEquivMapping2DiscardedAnchors(index2, index1);
		}
		
	}
	
	
	
	
	/**
	 * We add equiv mappings
	 * @param index1
	 * @param index2
	 */
	
	int num_discarded=0;
	public void addEquivMapping2DiscardedAnchors(int index1, int index2){
		
		if (!discardedMappings1N.containsKey(index1)){
			discardedMappings1N.put(index1, new HashSet<Integer>());
		}
		if (!discardedMappings1N.get(index1).contains(index2)){
			num_discarded++;
			discardedMappings1N.get(index1).add(index2);
		}
		
		//Temporary: Add both sides
		/*if (!discardedMappings1N.containsKey(index2)){
			discardedMappings1N.put(index2, new HashSet<Integer>());
		}
		discardedMappings1N.get(index2).add(index1);
		*/
		
	}
	
	public int getNumDiscardedMappings(){
		return num_discarded;
	}
	
	
	
	/**
	 * We add equiv mappings
	 * @param index1
	 * @param index2
	 */
	public void addEquivMapping2HardDiscardedAnchors(int index1, int index2){
		
		//To save space and time
		if (!hardDiscardedMappings1N.containsKey(index1)){
			hardDiscardedMappings1N.put(index1, new HashSet<Integer>());
		}
		hardDiscardedMappings1N.get(index1).add(index2);
	}
	
	
	
	public void addEquivMapping2InteractiveMappings(int index1, int index2){
		
		if (!interactiveProcessMappings1N.containsKey(index1)){
			interactiveProcessMappings1N.put(index1, new HashSet<Integer>());
		}
		interactiveProcessMappings1N.get(index1).add(index2);
		
		
	}
	
	
	
	
	
	/**
	 * We add each mapping direcction individually
	 * Direction are discarded in pre-conflict detection and in D&G
	 * @param index1
	 * @param index2
	 */
	public void addSubMapping2ConflictiveAnchors(int index1, int index2){
		
		addSubMapping2ConflictiveAnchors(index1, index2, false);
	
	}
	
	public void addSubMapping2ConflictiveAnchors(int index1, int index2, boolean fromDG){
		
		if (!conflictiveMappings1N.containsKey(index1)){
			conflictiveMappings1N.put(index1, new HashSet<Integer>());
		}
		conflictiveMappings1N.get(index1).add(index2);
		
		
		if (fromDG){
			//We add mapping only conflictive in D&G
			//This may help us to retrieve them back in some cases
			if (!conflictiveMappings1N_D_G.containsKey(index1)){
				conflictiveMappings1N_D_G.put(index1, new HashSet<Integer>());
			}
			conflictiveMappings1N_D_G.get(index1).add(index2);
		}
	}
	
	public Map<Integer, Set<Integer>> getConflictiveMappings_D_G(){
		return conflictiveMappings1N_D_G;
	}
		
	
	
	/**
	 * We add each mapping direcction individually
	 * @param index1
	 * @param index2
	 */
	public void addSubMapping2WeakenedDandGAnchors(int index1, int index2){
		
		if (!weakenedDandG_Mappings1N.containsKey(index1)){
			weakenedDandG_Mappings1N.put(index1, new HashSet<Integer>());
		}
		weakenedDandG_Mappings1N.get(index1).add(index2);
		
		
	}
	
	
	
	public void addMapping2GoldStandardAnchors(int index1, int index2){
		
		if (!GSMappings1N.containsKey(index1)){
			GSMappings1N.put(index1, new HashSet<Integer>());
		}
		GSMappings1N.get(index1).add(index2);
		
		
	}
	
	public Map<Integer, Set<Integer>> getGoldStandardMappings()
	{
		return GSMappings1N;
	}
	
	
	public boolean isMappingInGoldStandard(int index1, int index2){  //it is important the order of the question!
		if (GSMappings1N.containsKey(index1)){
			if (GSMappings1N.get(index1).contains(index2)){
				return true;
			}
		}
		return false;
	}
	
	
	public boolean isMappingAlreadyInList(int index1, int index2){
		
		if (logmapMappings1N.containsKey(index1)){
			if (logmapMappings1N.get(index1).contains(index2)){
				return true;
			}
		}
		return false;
		
	}
	
	
	
	
	public boolean isMappingInList2Review(int index1, int index2){
		
		if (mappings2Review.containsKey(index1)){
			if (mappings2Review.get(index1).contains(index2)){
				return true;
			}
		}
		return false;
		
	}
	
	public boolean isMappingInAnchors2AskLogMap(int index1, int index2){
		
		if (candidateMappings2askLogMap1N.containsKey(index1)){
			if (candidateMappings2askLogMap1N.get(index1).contains(index2)){
				return true;
			}
		}
		return false;
		
	}
	
	

	public boolean isMappingInAnchors2AskUser(int index1, int index2){
		
		if (candidateMappings2askUser1N.containsKey(index1)){
			if (candidateMappings2askUser1N.get(index1).contains(index2)){
				return true;
			}
		}
		return false;
		
	}
	
	
	
	
	public boolean isMappingInDiscardedSet(int index1, int index2){
		
		if (discardedMappings1N.containsKey(index1)){
			if (discardedMappings1N.get(index1).contains(index2)){
				return true;
			}
		}
		return false;
		
	}
	
	public boolean isMappingInHardDiscardedSet(int index1, int index2){
		
		if (hardDiscardedMappings1N.containsKey(index1)){
			if (hardDiscardedMappings1N.get(index1).contains(index2)){
				return true;
			}
		}
		return false;
		
	}
	
	
	public boolean isMappingInConflictiveSet(int index1, int index2){
		
		if (conflictiveMappings1N.containsKey(index1)){
			if (conflictiveMappings1N.get(index1).contains(index2)){
				return true;
			}
		}
		return false;
		
	}

	
	public boolean isMappingWeakenedDandG(int index1, int index2){
		
		if (weakenedDandG_Mappings1N.containsKey(index1)){
			if (weakenedDandG_Mappings1N.get(index1).contains(index2)){
				return true;
			}
		}
		return false;
		
	}
	
	

	public boolean isMappingInAnchors(int index1, int index2){
		
		//Both sides
		if (isMappingAlreadyInList(index1, index2) || isMappingAlreadyInList(index2, index1))
			return true;
		
		return false;
		
	}
	
	
	
	/**
	 * To avoid considering again mapping in different steps (stemming, weak...)
	 * @param index1
	 * @param index2
	 * @return
	 */
	public boolean isMappingAlreadyConsidered(int index1, int index2){
		
		//Both sides
		if (isMappingAlreadyInList(index1, index2) || isMappingAlreadyInList(index2, index1))
			return true;
		
		if (isMappingInList2Review(index1, index2) || isMappingInList2Review(index2, index1))
			return true;
		
		if (isMappingInDiscardedSet(index1, index2) || isMappingInDiscardedSet(index2, index1))
			return true;
		
		if (isMappingInHardDiscardedSet(index1, index2) || isMappingInHardDiscardedSet(index2, index1))
			return true;
		
		if (isMappingInConflictiveSet(index1, index2) || isMappingInDiscardedSet(index2, index1))
			return true;
		
		if (isMappingWeakenedDandG(index1, index2) || isMappingWeakenedDandG(index2, index1)) //In case was plit the other side
			return true;
		
		
		if (isMappingInAnchors2AskLogMap(index1, index2) || isMappingInAnchors2AskLogMap(index2, index1))
			return true;
	
		
		return false;
		
	}
	
	/**
	 * 
	 * @param index1
	 * @param index2
	 */
	public void addEquivMapping2ListOfWeakAnchors(int index1, int index2){
		
		//TODO in case we need to store which entities have at least a weak anchor
		//weak_mapped_entities_onto1.add(index1);
		//weak_mapped_entities_onto2.add(index2);
		
		
		if (!allWeakMappings1N.containsKey(index1)){
			allWeakMappings1N.put(index1, new HashSet<Integer>());
		}
		allWeakMappings1N.get(index1).add(index2);
		
	}

	
	/**
	 * 
	 * @param index1
	 * @param index2
	 */
	public void addEquivMapping2ListOfWeakCandidateAnchors(int index1, int index2){
		
		
		
		
		if (!weakCandidateMappings1N.containsKey(index1)){
			weakCandidateMappings1N.put(index1, new HashSet<Integer>());
		}
		weakCandidateMappings1N.get(index1).add(index2);
		
		
	}
	
	public void setStringAnchors(){
		setStringAnchors(true, true, true);
	}
	
	/**
	 * For Precision and recall purposes
	 */
	public void setStringAnchors(
			boolean out_class_map,
			boolean out_prop_map,
			boolean out_inst_map){
	
		mappings_candidates_Str.clear();
		
		
		if (out_class_map){
		
			for (int ide1 : logmapMappings1N.keySet()){
				for (int ide2 : logmapMappings1N.get(ide1)){
					addStringAnchor(ide1, ide2);
				}
			}
		}
		
		
		if (out_prop_map){
			for (int ident1 : getDataPropertyAnchors().keySet()){
				addStringAnchorDataProp(ident1, getDataPropertyAnchors().get(ident1));
			}
			
			for (int ident1 : getObjectPropertyAnchors().keySet()){
				addStringAnchorObjProp(ident1, getObjectPropertyAnchors().get(ident1));
			}
		}
		
		if (out_inst_map){
			//One one side from O1 to O2
			//TODO Revise this (Changed method Sept 2014)
			
			if (Parameters.output_instance_mapping_files){
				//We extend the output with discarded instance mappings (level 1)
				int type;
				for (int ide1 : getInstanceMappings4OutputType().keySet()) {
					
					for (int ide2 : getInstanceMappings4OutputType().get(ide1).keySet()){
					
						
						type = getInstanceMappings4OutputType().get(ide1).get(ide2);
						
						
						if (type<=1){
							addStringMappingIndividual(ide1, ide2);
						}
					}
				}
			}
			else{
				
				//Normal behaviour
				for (int ide1 : getInstanceMappings().keySet()){
					for (int ide2 : getInstanceMappings().get(ide1)){
						addStringMappingIndividual(ide1, ide2);
					}
				}
			}
			
			
			
			
		}
		
		
		
		//LogOutput.print(mappings_candidates_Str.size());
		
		
		/*for (int ide1 : interactiveProcessMappings1N.keySet()){
			for (int ide2 : interactiveProcessMappings1N.get(ide1)){
				addStringAnchor(ide1, ide2);
			}
		}*/
		
		//LogOutput.print(mappings_candidates_Str.size());
		
		
		//temporal
		/*for (int ide1 : mappings2Review.keySet()){
			for (int ide2 : mappings2Review.get(ide1)){
				addStringAnchor(ide1, ide2);
			}
		}*/
		
		
		
		
		/*for (int ide1 : candidateMappings2askUser1N.keySet()){
			for (int ide2 : candidateMappings2askUser1N.get(ide1)){
				addStringAnchor(ide1, ide2);
			}
		}
		
		/*for (int ide1 : discardedMappings1N.keySet()){
			for (int ide2 : discardedMappings1N.get(ide1)){
				addStringAnchor(ide1, ide2);
			}
		}*/
		
		
		/*for (MappingObjectIdentifiers int_anchor : getAnchorObjects()){
			addStringAnchor(
					int_anchor.getIdentifierOnto1(), 
					int_anchor.getIdentifierOnto2());		
		}*/
				
		
		
		
		
	}
	
	
	
	/**
	 * For Statistics
	 */
	public void setDiscardedStringAnchors(){
	
		discarded_candidates.clear();
		
		
		for (int ide1 : getDiscardedMappings().keySet()){
				for (int ide2 : getDiscardedMappings().get(ide1)){
					addDiscardedStringMappingsAnchor(ide1, ide2);
				}
		}
		
		
		
		
	}
	
	public void addStringAnchor(int ide1, int ide2){
		
		String iri1;
		String iri2;
		//To keep an order on the mappings
		
		if (isId1SmallerThanId2(ide1, ide2)){
			iri1 = index.getIRIStr4ConceptIndex(ide1);
			iri2 = index.getIRIStr4ConceptIndex(ide2);
		}
		else {
			iri2 = index.getIRIStr4ConceptIndex(ide1);
			iri1 = index.getIRIStr4ConceptIndex(ide2);
		}
		
		
		//We might be adding several times the same....
		//TODO: we might be interested in storing the direction for the mappings...
		//TODO: also for gold standard
		//MappingObjectStr obj;
		mappings_candidates_Str.add(new MappingObjectStr(iri1, iri2, getConfidence4Mapping(ide1, ide2)));
		
		//getConfidence4Mapping(ide1, ide2);
	}
	
	
	public void addDiscardedStringMappingsAnchor(int ide1, int ide2){
		
		String iri1;
		String iri2;
		//To keep an order on the mappings
		
		if (isId1SmallerThanId2(ide1, ide2)){
			iri1 = index.getIRIStr4ConceptIndex(ide1);
			iri2 = index.getIRIStr4ConceptIndex(ide2);
		}
		else {
			iri2 = index.getIRIStr4ConceptIndex(ide1);
			iri1 = index.getIRIStr4ConceptIndex(ide2);
		}
		
		
		//We might be adding several times the same....
		//TODO: we might be interested in storing the direction for the mappings...
		//TODO: also for gold standard
		//MappingObjectStr obj;
		discarded_candidates.add(new MappingObjectStr(iri1, iri2, getConfidence4Mapping(ide1, ide2)));
		
		//getConfidence4Mapping(ide1, ide2);
	}
	
	
	public void addStringAnchorDataProp(int ide1, int ide2){
		mappings_candidates_Str.add(
				new MappingObjectStr(
						index.getIRIStr4DataPropIndex(ide1),
						index.getIRIStr4DataPropIndex(ide2),
						getConfidence4DataPropertyAnchor(ide1, ide2)));		
	}
	
	public void addStringAnchorObjProp(int ide1, int ide2){
		mappings_candidates_Str.add(
				new MappingObjectStr(
						index.getIRIStr4ObjPropIndex(ide1),
						index.getIRIStr4ObjPropIndex(ide2),
						getConfidence4ObjectPropertyAnchor(ide1, ide2)));		
	}
	
	
	public void  addStringMappingIndividual(int ide1, int ide2){
		mappings_candidates_Str.add(
				new MappingObjectStr(
						index.getIRIStr4IndividualIndex(ide1),
						index.getIRIStr4IndividualIndex(ide2),
						getConfidence4InstanceMapping(ide1, ide2)));		
	}
	
	
	
	
	protected void addScopeAll2Structure(int ide1, int ide2, double conf){
		
		if (!anchor2scopeAll.containsKey(ide1))
			anchor2scopeAll.put(ide1, new HashMap<Integer, Double>());
		
		anchor2scopeAll.get(ide1).put(ide2, conf);
		
	}
	
	
	private void addScopeAnchors2Structure(int ide1, int ide2, double conf){
		
		if (!anchor2scopeAnchors.containsKey(ide1))
			anchor2scopeAnchors.put(ide1, new HashMap<Integer, Double>());
		
		anchor2scopeAnchors.get(ide1).put(ide2, conf);
		
	}
	
	
	public void addIsub2Structure(int ide1, int ide2, double conf){
		
		if (!anchor2isub.containsKey(ide1))
			anchor2isub.put(ide1, new HashMap<Integer, Double>());
		
		anchor2isub.get(ide1).put(ide2, conf);		
	}
	
	
	public void addIsub4Indivual(int ide1, int ide2, double conf){
		
		if (!instanceMappings2isub.containsKey(ide1))
			instanceMappings2isub.put(ide1, new HashMap<Integer, Double>());
		
		instanceMappings2isub.get(ide1).put(ide2, conf);		
	}
	
	
	public void addOutputType4Indivual(int ide1, int ide2, int conf){
		
		if (!instanceMappings2outputtype.containsKey(ide1))
			instanceMappings2outputtype.put(ide1, new HashMap<Integer, Integer>());
		
		instanceMappings2outputtype.get(ide1).put(ide2, conf);		
	}
	
	
	public void addCompFactor4Indivual(int ide1, int ide2, double conf){
		
		if (!instanceMappings2compfactor.containsKey(ide1))
			instanceMappings2compfactor.put(ide1, new HashMap<Integer, Double>());
		
		instanceMappings2compfactor.get(ide1).put(ide2, conf);
		
	}
	
	
	public void addScope4Indivual(int ide1, int ide2, double conf){
		
		if (!instanceMappings2scope.containsKey(ide1))
			instanceMappings2scope.put(ide1, new HashMap<Integer, Double>());
		
		instanceMappings2scope.get(ide1).put(ide2, conf);
		
	}
	
	
	protected void addIsubAverage2Structure(int ide1, int ide2, double conf){
		
		if (!anchor2isub_avg.containsKey(ide1))
			anchor2isub_avg.put(ide1, new HashMap<Integer, Double>());
		
		anchor2isub_avg.get(ide1).put(ide2, conf);
		
	}
	
	
	
	
	
	/**
	 * Similarity based ob combined occurrence frequency for weak mappings
	 * @param ide1
	 * @param ide2
	 * @param conf
	 */
	protected void addSimWeak2Structure(int ide1, int ide2, double conf){
		
		if (!anchor2sim_weak.containsKey(ide1))
			anchor2sim_weak.put(ide1, new HashMap<Integer, Double>());
		
		anchor2sim_weak.get(ide1).put(ide2, conf);
		
	}
	
	
	
	
	public void addMappingAddedByUser2Structure(int ide1, int ide2){
		
		if (!anchor2addedByUser.containsKey(ide1))
			anchor2addedByUser.put(ide1, new HashSet<Integer>());
		
		anchor2addedByUser.get(ide1).add(ide2);
		
	}
	
	
	public boolean isMappingAddedByUser(int ide1, int ide2){
		
		if (!anchor2addedByUser.containsKey(ide1)){
			return false;
		}
		
		if (!anchor2addedByUser.get(ide1).contains(ide2)){
			return false;
		}
		
		return true;
		
		
	}

	
	/**
	 * 
	 * @param ide1
	 * @param ide2
	 * @return
	 */
	private double getISUB4Mapping(int ide1, int ide2){
		
		//In current version this should not happen
		if (!anchor2isub.containsKey(ide1)){
			return 0.70; //in case does not have a value...
		}
		else if (!anchor2isub.get(ide1).containsKey(ide2)){
			return 0.70;
		}
		else {
			return anchor2isub.get(ide1).get(ide2);
		}
		
	}
	
	
	private double getISUBAverage4Mapping(int ide1, int ide2){
		
		//In current version this should not happen
		if (!anchor2isub_avg.containsKey(ide1)){
			return 0.70; //in case does not have a value...
		}
		else if (!anchor2isub_avg.get(ide1).containsKey(ide2)){
			return 0.70;
		}
		else {
			return anchor2isub_avg.get(ide1).get(ide2);
		}
		
	}
	
	
	public boolean hasWeakMappingSim(int ide1, int ide2){
		
		//In current version this should not happen
		if (!anchor2sim_weak.containsKey(ide1)){
			return false;
		}
		
		if (!anchor2sim_weak.get(ide1).containsKey(ide2)){
			return false;
		}
		
		return true;
		
		
	}
	
	
	public double getSimWeak4Mapping2(int ide1, int ide2){
		if (anchor2sim_weak.containsKey(ide1) && anchor2sim_weak.get(ide1).containsKey(ide2))
			return anchor2sim_weak.get(ide1).get(ide2);
		
		return -1.0;
	}
	
	public double getSimWeak4Mapping(int ide1, int ide2){
		if (anchor2sim_weak.containsKey(ide1) && anchor2sim_weak.get(ide1).containsKey(ide2))
			return anchor2sim_weak.get(ide1).get(ide2);
		
		return getISUB4Mapping(ide1, ide2) / 9.;
	}
	
	
	private double getScopeAll4Mapping(int ide1, int ide2){
		if (!anchor2scopeAll.containsKey(ide1)){
			return getISUB4Mapping(ide1, ide2);//0.70; ////TODO
		}
		else if (!anchor2scopeAll.get(ide1).containsKey(ide2)){
			return getISUB4Mapping(ide1, ide2); //return 0.70;
		}
		else {
			return anchor2scopeAll.get(ide1).get(ide2);
		}
		
	}
	

	private double getScopeAnchors4Mapping(int ide1, int ide2){
		if (!anchor2scopeAnchors.containsKey(ide1)){
			return getISUB4Mapping(ide1, ide2); //0.70; //TODO We add isub?
		}
		else if (!anchor2scopeAnchors.get(ide1).containsKey(ide2)){
			return getISUB4Mapping(ide1, ide2); //return 0.70;
		}
		else {
			return anchor2scopeAnchors.get(ide1).get(ide2);
		}
		
	}

	
	public double getLexicalScore(int ide1, int ide2) {
		return getISUB4Mapping(ide1, ide2);
	}
	
	
	public double getStructuralScore(int ide1, int ide2) {
		return getScopeAnchors4Mapping(ide1, ide2);
	}
	

	public double getConfidence4Mapping(int ide1, int ide2){
		
		//Max value
		if (isMappingAddedByUser(ide1, ide2)){
			return 1.0;
		}
		
		double conf = (weightIsub*getISUB4Mapping(ide1, ide2) + 
				weightIsub_avg*getISUBAverage4Mapping(ide1, ide2) + 
				weightScope*getScopeAll4Mapping(ide1, ide2) +
				weightScopeAnchors*getScopeAnchors4Mapping(ide1, ide2));
		
		
		
		if (hasWeakMappingSim(ide1, ide2)){
			//We just add as an additional value
			//return conf + getSimWeak4Mapping2(ide1, ide2);
			return conf + 0.1; //fixed value...?
		}
		
		return conf;
		
		
	}
	
	
	
	public double extractISUB4Mapping(int ide1, int ide2){
		
		if (anchor2isub.containsKey(ide1) && anchor2isub.get(ide1).containsKey(ide2)){
			return anchor2isub.get(ide1).get(ide2);
		}
		//else extract
		double isub_score = getIsubScore4ConceptsLabels(ide1, ide2);
		
		//store
		addIsub2Structure(ide1, ide2, isub_score);
		addIsub2Structure(ide2, ide1, isub_score);//we add both sides
		
		//return
		return isub_score;
		
	}
	
	
	public double extractISUB4InstanceMapping(int ide1, int ide2){
	
		if (instanceMappings2isub.containsKey(ide1) && instanceMappings2isub.get(ide1).containsKey(ide2)){
			return instanceMappings2isub.get(ide1).get(ide2);
		}
		//else extract
		double isub_score = getIsubScore4IndividualLabels(ide1, ide2);
		
		//store
		addIsub4Indivual(ide1, ide2, isub_score);
		addIsub4Indivual(ide2, ide1, isub_score);//we add both sides
		
		//return
		return isub_score;
		
	}
	
	
	public double extractScope4InstanceMapping(int ide1, int ide2){
		
		if (instanceMappings2scope.containsKey(ide1) && instanceMappings2scope.get(ide1).containsKey(ide2)){
			return instanceMappings2scope.get(ide1).get(ide2);
		}
		
		
		double scope_score = getScopeScore4Individuals(ide1, ide2); 
		
		//store
		addScope4Indivual(ide1, ide2, scope_score);
		addScope4Indivual(ide2, ide1, scope_score);//we add both sides
		
		//return
		return scope_score;
		
		
		
	}
	
	
	
	public double extractISUBAverage4Mapping(int ide1, int ide2){
		
		if (anchor2isub_avg.containsKey(ide1) && anchor2isub_avg.get(ide1).containsKey(ide2)){
			return anchor2isub_avg.get(ide1).get(ide2);
		}
		//else extract
		double isub_score = getIsubAverageScore4ConceptsLabels(ide1, ide2);
		
		//store
		addIsubAverage2Structure(ide1, ide2, isub_score);
		addIsubAverage2Structure(ide2, ide1, isub_score);//we add both sides
		
		//return
		return isub_score;
		
	}
	
	
	public double extractScopeAll4Mapping(int ide1, int ide2){
		if (anchor2scopeAll.containsKey(ide1) && anchor2scopeAll.get(ide1).containsKey(ide2)){
			return anchor2scopeAll.get(ide1).get(ide2);
		}
		//else extract
		double scope_score = getScopeScoreAll4Neighbourhood(ide1, ide2);
			
		//store
		addScopeAll2Structure(ide1, ide2, scope_score);
		addScopeAll2Structure(ide2, ide1, scope_score); //we add both sides
		
		//return
		return scope_score;
		
		
		
	}
	
	
	public double extractScopeAnchors4Mapping(int ide1, int ide2){
		if (anchor2scopeAnchors.containsKey(ide1) && anchor2scopeAnchors.get(ide1).containsKey(ide2)){
			return anchor2scopeAnchors.get(ide1).get(ide2);
		}
		//else extract
		double scope_score = getScopeScoreAnchors4Neighbourhood(ide1, ide2);
			
		//store
		addScopeAnchors2Structure(ide1, ide2, scope_score);
		addScopeAnchors2Structure(ide2, ide1, scope_score); //we add both sides
		
		//return
		return scope_score;
		
		
		
	}
	
	
	public double getIsubScore4DataPropertyLabels(int ide1, int ide2){
		return getIsubScore4DataPropertyLabels(ide1, ide2, true);
	}
	
	public double getIsubScore4DataPropertyLabels(int ide1, int ide2, boolean useAlternatives){
		
		if (useAlternatives){
			
			double max_score=-10.0;
			double isub_score;
			
			for (String str1 : index.getAlternativeLabels4DataPropertyIndex(ide1)){
				for (String str2 : index.getAlternativeLabels4DataPropertyIndex(ide2)){
					isub_score = isub.score(str1, str2);
					
					if (isub_score<0){ //Do not return negative isubs...
						isub_score=0.0;
					}
					
					if (isub_score>max_score){
						max_score=isub_score;
					}
				}
			}
			
			
			return max_score;
			
		}
		else {
			return isub.score(
					index.getLabel4DataPropIndex(ide1),
					index.getLabel4DataPropIndex(ide2)
					);
	
		}
	}
	
	
	public double getIsubScore4ObjectPropertyLabels(int ide1, int ide2){
		return getIsubScore4ObjectPropertyLabels(ide1, ide2, true);
	}
	
	public double getIsubScore4ObjectPropertyLabels(int ide1, int ide2, boolean useAlternativeLabels){
		
		if (useAlternativeLabels){
			
			double max_score=-10.0;
			double isub_score;
			
			for (String str1 : index.getAlternativeLabels4ObjectPropertyIndex(ide1)){
				for (String str2 : index.getAlternativeLabels4ObjectPropertyIndex(ide2)){
					isub_score = isub.score(str1, str2);
					
					if (isub_score<0){ //Do not return negative isubs...
						isub_score=0.0;
					}
					
					if (isub_score>max_score){
						max_score=isub_score;
					}
				}
			}
			
			
			return max_score;
			
		}
		else {
			return isub.score(
					index.getLabel4ObjPropIndex(ide1),
					index.getLabel4ObjPropIndex(ide2)
					);
		}
	}

	
	
	public double getIsubScore4ConceptsLabels(int ide1, int ide2){
		return getIsubScore4ConceptsLabels(ide1, ide2, 0.99);
	}
	
	/**
	 * It takes into account synonyms, variations without stopwords
	 * @param ide1
	 * @param ide2
	 * @return
	 */
	protected double getIsubScore4ConceptsLabels(int ide1, int ide2, double enough_score){
				
		double max_score=-10.0;
		double isub_score;
		
		//LogOuput.print(index_onto1.getAlternativeLabels4ConceptIndex(ide1));
		
		for (String str1 : index.getAlternativeLabels4ConceptIndex(ide1)){
			for (String str2 : index.getAlternativeLabels4ConceptIndex(ide2)){
				isub_score = isub.score(str1, str2);
				
				if (isub_score<0){ //Do not return negative isubs...
					isub_score=0.0;
				}
				
				if (isub_score>max_score){
					max_score=isub_score;
				}
				if (max_score>enough_score)
					return max_score;
			}
		}
		
		
		return max_score;
		
	}
	
	
	public double getIsubScore4IndividualLabels(int ide1, int ide2){
		return getIsubScore4IndividualLabels(ide1, ide2, 0.99);
	}
	
	/**
	 * It takes into account synonyms, variations without stopwords
	 * @param ide1
	 * @param ide2
	 * @return
	 */
	protected double getIsubScore4IndividualLabels(int ide1, int ide2, double enough_score){
				
		double max_score=-10.0;
		double isub_score;
		
		//LogOuput.print(index_onto1.getAlternativeLabels4ConceptIndex(ide1));
		
		for (String str1 : index.getAlternativeLabels4IndividualIndex(ide1)){
			for (String str2 : index.getAlternativeLabels4IndividualIndex(ide2)){
				isub_score = isub.score(str1, str2);
				
				if (isub_score<0){ //Do not return negative isubs...
					isub_score=0.0;
				}
				
				if (isub_score>max_score){
					max_score=isub_score;
				}
				if (max_score>enough_score)
					return max_score;
			}
		}
		
		//For cases without labels matched through IF of role assertions. It will always be 0.
		if (max_score==0.0)
			max_score=0.5;
		
		return max_score;
		
	}
	
	
	private double getIsubAverageScore4ConceptsLabels(int ide1, int ide2){
		
		int num_matchings=0;
		double isub_avg=0.0;
		double isub_score;
		
		//LogOuput.print(index_onto1.getAlternativeLabels4ConceptIndex(ide1));
		
		for (String str1 : index.getAlternativeLabels4ConceptIndex(ide1)){
			for (String str2 : index.getAlternativeLabels4ConceptIndex(ide2)){
				
				num_matchings++;
				isub_score = isub.score(str1, str2);
				if (isub_score<0){
					isub_score=0.0;
				}
				isub_avg += isub_score;
				
			}
		}
		
		if (num_matchings==0)
			return 0.0;
		
		return (isub_avg)/((double)num_matchings);
		
	}
	
	
	
	
	
	/**
	 * 
	 * We extract scope score considering weak and exact mappings
	 * If no mappings then 0.0. If there is no scope then 0.01.
	 * 
	 * @param ide1
	 * @param ide2
	 * @param mappings
	 * @return
	 */
	private double getScopeScoreAll4Neighbourhood(int ide1, int ide2){
			
		//Mappings: do not consider mapping itself for its scope!!
		
		
		
		Set<Integer> module1 = index.getScope4Identifier_Condifence(ide1); 
		Set<Integer> module2 = index.getScope4Identifier_Condifence(ide2);
		
		//No scope in one side
		if ((module1.size()==0 && module2.size()>0) || (module2.size()==0 && module1.size()>0)){
			//LogOutput.print(index.getName4ConceptIndex(ide1) + " " + module1.size());
			//LogOutput.print(index.getName4ConceptIndex(ide2) + " " + module2.size());
			
			return 0.01; //we cannot say much
		}
		
		
		
		
		//No scope in both sides
		//We give an small value: we cannot say more.
		if (module1.size()==0 || module2.size()==0 ){
			return 0.01; //we cannot say much
		}
				
	
		int unionSize = module1.size() + module2.size();

		double weightedIntersection=0.0;
	
		
		
		
		//WEAK MAPPINGS
		double score;
		double max_score=-1;
		
		boolean mapped;
		//We look for mappings from module/scope 1 to module/scope 2
		
		
		for (int ent1 : module1){
			
			mapped=false;
			
			
			//In exact mappings (they are not complete yet...)
			//------------------------------------------
			if (logmapMappings1N.containsKey(ent1)){
				
				for (int ent2 : logmapMappings1N.get(ent1)){ //Candidate mappings
					
					if (module2.contains(ent2)){
						
						weightedIntersection++;
						mapped=true;
						break;
						
					}
					
				}
				
			}
			
			if (mapped) //Do not look for weak mappings
				continue;
			
			
			//In weak mappings: includes exact
			//------------------------------------------
			if (allWeakMappings1N.containsKey(ent1)){ //Only if involved in mappings	
				
				max_score=-1;
				
				for (int ent2 : allWeakMappings1N.get(ent1)){
					
					if (module2.contains(ent2)){
						
						score = getIsubScore4ConceptsLabels(ent1, ent2);//we do not store isub
						
						if (score>max_score){
							max_score=score;
						}
						
					}
					
				}

			}
			
			// TODO Yujiao - move out by me
			if (max_score>0){
				//We only add Max score in case there were several
				weightedIntersection+=max_score;
				mapped=true;
			}
			
			/*if (mapped) //Do not look for weak mappings
				continue;
			
			
			max_score=-1;
			
			*/
		}
		
		if (weightedIntersection<=0.0){
			
			if (areMappedEntitiesSharingScope(ide1, ide2)){
				return 0.10; //At least they are sharing in the big scopes
			}
			
			//NO intersection of big scopes
			
			//Look for ISUB if no exact of weak mappings
			//------------------------------------------
			//REFINE THIS
			for (int ent1 : module1){
				for (int ent2: module2){
				
					//We want to exclude those mappings that do not have scope at all
					score = getIsubScore4ConceptsLabels(ent1, ent2, 0.65);//We just look for similar neighbours
					
					if (score>0.65){//If we found one we are done!
						weightedIntersection+=score;
						break;
					}
				}
			}
		}
		
		
		// Yujiao - check this condition!?
		//No mappings or only one with confidence less than 0.25
		if (weightedIntersection<0.5) { //No mappings (we do not need to do any further calculation)
			return 0.0;
		}
		
		
		//Scope: adaptaed dices coefficient
		double scopeScore = (double)Math.round((2.0*(double)(weightedIntersection)/(double)unionSize)*100.0)/100.0;
		
		
		return scopeScore;
		
		
	}
	
	
	
	/**
	 * 
	 * We extract scope score considering only anchor mappings
	 * 
	 * @param ide1
	 * @param ide2
	 * @param mappings
	 * @return
	 */
	private double getScopeScoreAnchors4Neighbourhood(int ide1, int ide2){
			
		//Mappings: do not consider mapping itself for its scope!!
		
		
		
		Set<Integer> module1 = index.getScope4Identifier_Condifence(ide1); 
		Set<Integer> module2 = index.getScope4Identifier_Condifence(ide2);
		
		//No scope in one side
		if ((module1.size()==0 && module2.size()>0) || (module2.size()==0 && module1.size()>0)){			
			return 0.01; //we cannot say much
		}
		
		
		
		
		//No scope in both sides
		//We give an small value: we cannot say more.
		if (module1.size()==0 || module2.size()==0 ){
			return 0.01; //we cannot say much
		}
				
	
		int unionSize = module1.size() + module2.size();

		int num_mappings=0;
	
	
		
		
		for (int ent1 : module1){
			
			
			//In exact mappings (they are not complete yet...)
			//------------------------------------------
			if (logmapMappings1N.containsKey(ent1)){
				
				for (int ent2 : logmapMappings1N.get(ent1)){ //Candidate mappings
					
					if (module2.contains(ent2)){
						
						num_mappings++;
						break;
						
					}
					
				}
				
			}
		
			
		}
		
		
		if (num_mappings<1) { //No mappings (we do not need to do any futher calculation)
			return 0.0;
		}
		
		
		//Scope: adapted dices coefficient
		double scopeScore = (double)Math.round((2.0*(double)(num_mappings)/(double)unionSize)*100.0)/100.0;
		
		
		return scopeScore;
		
		
	}
	
	
	
	
	/**
	 * 
	 * We extract isub of scope. No mappings (anchors or weak) are considered
	 * 
	 * @param ide1
	 * @param ide2
	 * @param mappings
	 * @return
	 */
	public double getScopeISUB4Neighbourhood(int ide1, int ide2){
		
		Set<Integer> module1 = index.getScope4Identifier_Condifence(ide1); 
		Set<Integer> module2 = index.getScope4Identifier_Condifence(ide2);
		
		//No scope in one side
		if ((module1.size()==0 && module2.size()>0) || (module2.size()==0 && module1.size()>0)){
			//LogOutput.print(index.getName4ConceptIndex(ide1) + " " + module1.size());
			//LogOutput.print(index.getName4ConceptIndex(ide2) + " " + module2.size());
			
			return 0.01; //we cannot say much
		}
		
		
		//No scope in both sides
		//We give an small value: we cannot say more.
		if (module1.size()==0 || module2.size()==0 ){
			return 0.01; //we cannot say much
		}
				
	
		int unionSize = module1.size() + module2.size();

		double weightedIntersection=0.0;
	
		double score;
		
		for (int ent1 : module1){
			for (int ent2: module2){
				
				//We want to exclude those mappings that do not have scope at all
				score = getIsubScore4ConceptsLabels(ent1, ent2, 0.75);//We just look for similar neighbours
					
				if (score>0.75){//If we found one we are done!
					weightedIntersection+=score;
					break;
				}
			}
		}
		
		//Scope: adapted dices coefficient
		double scopeScore = (double)Math.round((2.0*(double)(weightedIntersection)/(double)unionSize)*100.0)/100.0;
		
		return scopeScore;
		
		
	}
	
	
	
	public double getScopeScore4Individuals(int ide1, int ide2){
		
		Set<Integer> types1 = index.getIndividualClassTypes4Identifier(ide1);
		Set<Integer> types2 = index.getIndividualClassTypes4Identifier(ide2);
		
		if (types1.isEmpty() || types2.isEmpty()){
			return 0.0;
		}
		
		
		
		double scope;
		double max_scope=-1; 
		
		for (int cls1 : types1){
			for (int cls2 : types2){
				scope = getScopeAll4Mapping(cls1, cls2);
						//getScopeISUB4Neighbourhood(cls1, cls2);
				
				if (scope>0.9)
					return scope;
				
				if (scope>max_scope)
					max_scope=scope;
					
			}	
		}
		
		
		return max_scope;
	}
	
	
	
	
	
	
	/**
	 * We seek for weak mappings within the big scopes of the entities
	 * If not weak mappings found then the mapping it is likely to be wrong 
	 * @param ide1
	 * @param ide2
	 * @return
	 */
	private boolean areMappedEntitiesSharingScope(int ide1, int ide2){
			

		Set<Integer> module1 = index.getScope4Identifier_Big(ide1); 
		Set<Integer> module2 = index.getScope4Identifier_Big(ide2);
		
		//No scope in one side
		if ((module1.size()==0 && module2.size()>0) || (module2.size()==0 && module1.size()>0)){
			//LogOutput.print(index.getName4ConceptIndex(ide1) + " " + module1.size());
			//LogOutput.print(index.getName4ConceptIndex(ide2) + " " + module2.size());
			
			return true; //we cannot say much
		}
		
		
		
		
		//No scope in both sides
		//We give an small value: we cannot say more.
		if (module1.size()==0 || module2.size()==0 ){
			return true; //we cannot say much
		}
				
	
		
		
		for (int ent1 : module1){
		
			//In weak mappings
			//------------------------------------------
			if (allWeakMappings1N.containsKey(ent1)){ //Only if involved in mappings	
				
				for (int ent2 : allWeakMappings1N.get(ent1)){
					
					if (module2.contains(ent2)){
						
						return true;
					}
					
				}
				
			}
		}
			

		return false;
					
		
	}
	
	
	
	public boolean isMappingInConflictWithFixedMappings(int ident1, int ident2){
		
		//return isMappingInConflictWithFixedMappings_direct_disjointness(ident1, ident2);
		return isMappingInConflictWithFixedMappings_includes_descendants(ident1, ident2);
		
	}
	
	
	
	//TODO only direct disjoint cases 
	public boolean isMappingInConflictWithFixedMappings_direct_disjointness(int ident1, int ident2){
		
		//CONFLICTIVE MAPPINGS WRT EXACT ONES
		if (index.areDisjoint(ident1, ident2) || index.areDisjoint(ident2, ident1)) { //Not sure about the symmetry
			//System.out.println("Conflict: " + index.getName4ConceptIndex(ident1) + " -  " + index.getName4ConceptIndex(ident2));
			return true;
		}
		
		return false;
		//addSubMapping2ConflictiveAnchors(ident1, ident2);
		//addSubMapping2ConflictiveAnchors(ident2, ident1);
		
	}
	
	
	
	
	
	//TODO it includes descendants
	public boolean isMappingInConflictWithFixedMappings_includes_descendants(int ident1, int ident2){
		
		
		//CONFLICTIVE MAPPINGS WRT EXACT ONES
		if (index.areDisjoint(ident1, ident2) || index.areDisjoint(ident2, ident1)) { //Not sure about the symmetry
			//System.out.println("Conflict: " + index.getName4ConceptIndex(ident1) + " -  " + index.getName4ConceptIndex(ident2));
			return true;
		}
		
		
		//This method also checks for subclasses
		//if A is not in conflict with B but it is with B1 which is subclass of B
		
		//Example: 
		//O1:Paper is mapped to O2:Paper
		//O2:Paper is disjoint with O2:Poster
		//O2:Paper and O2:Poster subclassof O2:Contribution
		//Can be mapped O1:Paper to O2:Contribution
		
		//We check if descendants are disjoint
		//This method is too aggressive
		//Descendants may be disjoint and the mapping still be valid and not leading to unsat!!
		//if (index.arePartiallyDisjoint(ident1, ident2)){
		//	//System.out.println("Conflict: " + index.getName4ConceptIndex(ident1) + " -  " + index.getName4ConceptIndex(ident2));
		//	return true;
		//}
		
		//Not symmetric method
		if (index.isDisjointWithDescendants(ident1, ident2) || index.isDisjointWithDescendants(ident2, ident1)){
			//System.out.println("Conflict: " + index.getName4ConceptIndex(ident1) + " -  " + index.getName4ConceptIndex(ident2));
			return true;
		}
		
		
		
		
		return false;
		//addSubMapping2ConflictiveAnchors(ident1, ident2);
		//addSubMapping2ConflictiveAnchors(ident2, ident1);
		
	}
	
	
	/**
	 * Check if two given mappings are in conflict
	 * @param ideA
	 * @param ideB
	 * @param ideAA
	 * @param ideBB
	 * @return
	 */
	public boolean areMappingsInConflict(int ideA, int ideB, int ideAA, int ideBB){
		
		boolean AequivAA=false;
		boolean BequivBB=false;
		
		boolean AcontAA=false;
		boolean AAcontA=false;
		boolean BcontBB=false;
		boolean BBcontB=false;
		
		boolean AdisjAA=false;
		boolean BdisjBB=false;
		
		boolean conflict=false;

		
		AequivAA = index.areEquivalentClasses(ideA, ideAA);
		
		BequivBB = index.areEquivalentClasses(ideB, ideBB);
		
		AcontAA = AequivAA || index.isSubClassOf(ideA, ideAA);
				
		AAcontA = AequivAA || index.isSubClassOf(ideAA, ideA);
		
		BcontBB = BequivBB || index.isSubClassOf(ideB, ideBB);
		
		BBcontB = BequivBB || index.isSubClassOf(ideBB, ideB);
	
		//over same ontology
		AdisjAA = !AcontAA && !AAcontA && (index.areDisjoint(ideA, ideAA));// || index.isDisjointWithDescendants(ideA, ideAA) || index.isDisjointWithDescendants(ideAA, ideA));
		
		BdisjBB = !BcontBB && !BBcontB && (index.areDisjoint(ideB, ideBB));// || index.isDisjointWithDescendants(ideB, ideBB) || index.isDisjointWithDescendants(ideBB, ideB));
				
				
		conflict = ((AcontAA || AAcontA) && BdisjBB) || ((BcontBB || BBcontB) && AdisjAA);
		
		return conflict;
		
	}
	
	
	public boolean isMappingDangerousEquiv(int ident1, int ident2){
		
		//Repair 
		
		//boolean tmp = index.isSubClassOf(ident1, ident2); 
		//tmp = index.isSubClassOf(ident2, ident1);
		
		//System.out.println("hello2");

		
		
		//There is already a parent-child relationship and no ambiguity
		if (index.isSubClassOf(ident1, ident2) || index.isSubClassOf(ident2, ident1)){
			//No ambiguity in mapping
			if (!isEntityAlreadyMapped(ident1) && !isEntityAlreadyMapped(ident2)){
				return true;
			}
		}
		
		return false;
		
	}
	
	
	public boolean isMappingInferred(int ident1, int ident2){
		
		//CONFLICTIVE MAPPINGS WRT EXACT ONES
		if (index.isSubClassOf(ident1, ident2))
			return true;
		
		
		return false;
	}
	
	
	
	
	public Map<String, Set<String>> getCategoryMappings(){
		return category_mappings;
	}

	public void setCategoryMappings(Map<String, Set<String>> mappings){
		category_mappings=mappings;
	}
	
	public boolean hasCategoryMappings(String uri){
		return category_mappings.containsKey(uri);
	}
	
	public Set<String> getMappings4Category(String uri){		
		return category_mappings.get(uri);		
	}
	
	
	public Set<String> getMappings4CategoryWithCheck(String uri){
		if (category_mappings.containsKey(uri))
			return category_mappings.get(uri);
		else
			return new HashSet<String>();
	}
	
	
	
	
	
	public void printStatisticsMappingEvaluation(){
		//LogOutput.print("Anchors IF exact: " + exactMappings1N.size());		
		
		LogOutput.print("");
		
		int mappings=0;
		int good=0;
		for (int ide1 : logmapMappings1N.keySet()){
			for (int ide2 : logmapMappings1N.get(ide1)){
				
				if (isId1SmallerThanId2(ide1, ide2)){
				
					mappings++;
					
					
					if (isMappingInGoldStandard(ide1, ide2)){
						good++;
						
						
					}
					else {
						/*LogOutput.print("ANCH" + " - " +
								index.getLabel4ConceptIndex(ide1) + " - " + //.getName4ConceptIndex(ide1)  + " - " +
								index.getLabel4ConceptIndex(ide2) + " - " + //index.getName4ConceptIndex(ide2)  + " - " +
								extractISUB4Mapping(ide1, ide2) + " - " +
								extractISUBAverage4Mapping(ide1, ide2)  + " - " +
								extractScope4Mapping(ide1, ide2));*/
					}
				}
			}
		}
		
		LogOutput.print("Anchors IF exact: " + mappings);
		LogOutput.print("Good Anchors in GS: " + good);
	
		
		
		
		
		mappings=0;
		good=0;
		for (int ide1 : mappings2Review.keySet()){
			for (int ide2 : mappings2Review.get(ide1)){
				
				//check only one side
				if (isId1SmallerThanId2(ide1, ide2)){
				
					mappings++;
					
					
					if (isMappingInGoldStandard(ide1, ide2)){
						good++;
					}
				}
			}
		}
		
		LogOutput.print("Mappings 2 review: " + mappings);
		LogOutput.print("Good mappings 2 review in GS: " + good);
		
		
		
		mappings=0;
		good=0;
		for (int ide1 : candidateMappings2askLogMap1N.keySet()){
			for (int ide2 : candidateMappings2askLogMap1N.get(ide1)){
				
				//check only one side
				if (isId1SmallerThanId2(ide1, ide2)){
				
					mappings++;
					
					
					if (isMappingInGoldStandard(ide1, ide2)){
						good++;
						//addSubMapping2ListOfAnchors(ide1, ide2);
						//addSubMapping2ListOfAnchors(ide2, ide1);
					}
					else{/*
						LogOutput.print("ASK" + " - " +
								index.getName4ConceptIndex(ide1)  + " - " +
								index.getName4ConceptIndex(ide2)  + " - " +
								extractISUB4Mapping(ide1, ide2) + " - " +
								extractISUBAverage4Mapping(ide1, ide2)  + " - " +
								extractScope4Mapping(ide1, ide2));
					*/}
				}
			}
		}
		
		LogOutput.print("Mappings 2 ask logmap: " + mappings);
		LogOutput.print("Good mappings 2 ask logmap in GS: " + good);
		
		
		
		mappings=0;
		good=0;
		for (int ide1 : candidateMappings2askUser1N.keySet()){
			for (int ide2 : candidateMappings2askUser1N.get(ide1)){
				
				//check only one side
				if (isId1SmallerThanId2(ide1, ide2)){
				
					mappings++;
					
					
					if (isMappingInGoldStandard(ide1, ide2)){
						good++;
						//addSubMapping2ListOfAnchors(ide1, ide2);
						//addSubMapping2ListOfAnchors(ide2, ide1);
					}
					else{/*
						LogOutput.print("ASK" + " - " +
								index.getName4ConceptIndex(ide1)  + " - " +
								index.getName4ConceptIndex(ide2)  + " - " +
								extractISUB4Mapping(ide1, ide2) + " - " +
								extractISUBAverage4Mapping(ide1, ide2)  + " - " +
								extractScope4Mapping(ide1, ide2));
					*/}
				}
			}
		}
		
		LogOutput.print("Mappings 2 ask user: " + mappings);
		LogOutput.print("Good mappings 2 ask user in GS: " + good);
		
		
		mappings=0;
		good=0;
		for (MappingObjectInteractivity mapping : listMappings2askUser1N){

			mappings++;
					
					
			if (isMappingInGoldStandard(mapping.getIdentifierOnto1(), mapping.getIdentifierOnto2())){
				good++;
			}
			
		}
		
		LogOutput.print("Mappings 2 ask user (filtered list): " + mappings);
		LogOutput.print("Good mappings 2 ask user in GS (filtered list): " + good);
		
		
		mappings=0;
		good=0;
		for (int ide1 : weakenedDandG_Mappings1N.keySet()){
			for (int ide2 : weakenedDandG_Mappings1N.get(ide1)){
				
				//if (index.getClassIndex(ide1).getOntologyId()<index.getClassIndex(ide2).getOntologyId()){
				
					mappings++;
					
					if (isMappingInGoldStandard(ide1, ide2) || isMappingInGoldStandard(ide2, ide1)){
						good++;
				//	}
				}
			}
		}
		
		LogOutput.print("Weakened DG anchors: " + mappings);
		LogOutput.print("Good weakened DG anchors in GS: " + good);
		
		
		
		mappings=0;
		good=0;
		for (int ide1 : discardedMappings1N.keySet()){
			for (int ide2 : discardedMappings1N.get(ide1)){
				
				//check only one side
				if (isId1SmallerThanId2(ide1, ide2)){
				
					mappings++;
					
					
					if (isMappingInGoldStandard(ide1, ide2)){
						good++;
						/*LogOutput.print("DISC-GOOD" + " - " +
								index.getLabel4ConceptIndex(ide1) + " - " + //.getName4ConceptIndex(ide1)  + " - " +
								index.getLabel4ConceptIndex(ide2) + " - " + //index.getName4ConceptIndex(ide2)  + " - " +
								extractISUB4Mapping(ide1, ide2) + " - " +
								extractISUBAverage4Mapping(ide1, ide2)  + " - " +
								extractScope4Mapping(ide1, ide2));*/
						//addSubMapping2ListOfAnchors(ide1, ide2);
						//addSubMapping2ListOfAnchors(ide2, ide1);
						
					}
					else{
						/*LogOutput.print("DISC-BAD" + " - " +
								index.getName4ConceptIndex(ide1)  + " - " +
								index.getName4ConceptIndex(ide2)  + " - " +
								extractISUB4Mapping(ide1, ide2) + " - " +
								extractISUBAverage4Mapping(ide1, ide2)  + " - " +
								extractScope4Mapping(ide1, ide2));*/
					}
				}
			}
		}
		
		LogOutput.print("Mappings Discarded: " + mappings);
		LogOutput.print("Good mappings Discarded: " + good);
		
		

		mappings=0;
		good=0;
		for (int ide1 : hardDiscardedMappings1N.keySet()){
			for (int ide2 : hardDiscardedMappings1N.get(ide1)){
				
				//check only one side
				if (isId1SmallerThanId2(ide1, ide2)){
				
					mappings++;
					
					
					if (isMappingInGoldStandard(ide1, ide2)){
						good++;
						/*LogOutput.print("DISC-GOOD" + " - " +
								index.getLabel4ConceptIndex(ide1) + " - " + //.getName4ConceptIndex(ide1)  + " - " +
								index.getLabel4ConceptIndex(ide2) + " - " + //index.getName4ConceptIndex(ide2)  + " - " +
								extractISUB4Mapping(ide1, ide2) + " - " +
								extractISUBAverage4Mapping(ide1, ide2)  + " - " +
								extractScope4Mapping(ide1, ide2));*/
						//addSubMapping2ListOfAnchors(ide1, ide2);
						//addSubMapping2ListOfAnchors(ide2, ide1);
						
					}
					else{
						/*LogOutput.print("DISC-BAD" + " - " +
								index.getName4ConceptIndex(ide1)  + " - " +
								index.getName4ConceptIndex(ide2)  + " - " +
								extractISUB4Mapping(ide1, ide2) + " - " +
								extractISUBAverage4Mapping(ide1, ide2)  + " - " +
								extractScope4Mapping(ide1, ide2));*/
					}
				}
			}
		}
		
		LogOutput.print("Mappings Hard Discarded: " + mappings);
		LogOutput.print("Good mappings Hard Discarded: " + good);
		
		
		
		mappings=0;
		good=0;
		for (int ide1 : conflictiveMappings1N.keySet()){
			for (int ide2 : conflictiveMappings1N.get(ide1)){
				
				//check only one side
				//if (isId1SmallerThanId2(ide1, ide2)){
					
					
					if (!isId1SmallerThanId2(ide1, ide2)){
						if (conflictiveMappings1N.containsKey(ide2) && conflictiveMappings1N.get(ide2).contains(ide1)){
							continue; //Already visited
						}
					}
					mappings++;
					
					if (isMappingInGoldStandard(ide1, ide2)){
						good++;
					}
					else{/*
						LogOutput.print("CONF" + " - " +
								index.getName4ConceptIndex(ide1)  + " - " +
								index.getName4ConceptIndex(ide2)  + " - " +
								extractISUB4Mapping(ide1, ide2) + " - " +
								extractISUBAverage4Mapping(ide1, ide2)  + " - " +
								extractScope4Mapping(ide1, ide2));
					*/}
				//}
			}
		}
		
		LogOutput.print("Mappings Conflictive: " + mappings);
		LogOutput.print("Good mappings Conflictive: " + good);
		
		
		
		mappings=0;
		good=0;
		for (int ide1 : weakCandidateMappings1N.keySet()){
			for (int ide2 : weakCandidateMappings1N.get(ide1)){
				
				//check only one side
				//if (isId1SmallerThanId2(ide1, ide2)){
					mappings++;
					
					
					if (isMappingInGoldStandard(ide1, ide2)){
						good++;
					}
					else{/*
						LogOutput.print("CONF" + " - " +
								index.getName4ConceptIndex(ide1)  + " - " +
								index.getName4ConceptIndex(ide2)  + " - " +
								extractISUB4Mapping(ide1, ide2) + " - " +
								extractISUBAverage4Mapping(ide1, ide2)  + " - " +
								extractScope4Mapping(ide1, ide2));
					*/}
				//}
			}
		}
		
		LogOutput.print("Mappings Weak: " + mappings);
		LogOutput.print("Good mappings Weak: " + good);
		
		
	}
	
	
	/**
	 * The method may change in the future
	 * @param id1
	 * @param ide2
	 */
	public boolean isId1SmallerThanId2(int ide1, int ide2){
		
		//if (index.getClassIndex(ide1).getOntologyId()<index.getClassIndex(ide2).getOntologyId()){
		if (ide1<ide2){
			return true;
		}
		return false;
		
	}


	public void createAnchors(boolean are_input_mapping_validated) {
		// TODO Auto-generated method stub
		
	}

		
}

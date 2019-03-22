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

import java.util.Comparator;


import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.Stack;
import java.util.Random;

import uk.ac.ox.krr.logmap2.Parameters;
import uk.ac.ox.krr.logmap2.indexing.IndexManager;
import uk.ac.ox.krr.logmap2.interactive.objects.MappingObjectInteractivity;
import uk.ac.ox.krr.logmap2.io.LogOutput;
import uk.ac.ox.krr.logmap2.io.WriteFile;
import uk.ac.ox.krr.logmap2.mappings.MappingManager;
import uk.ac.ox.krr.logmap2.mappings.objects.MappingObjectStr;
import uk.ac.ox.krr.logmap2.repair.AnchorAssessment;
import uk.ac.ox.krr.logmap2.statistics.StatisticsManager;
import uk.ac.ox.krr.logmap2.utilities.PrecomputeIndexCombination;
import uk.ac.ox.krr.logmap2.utilities.Utilities;

/**
 * 
 * This class will perform the ordering for the interactive process based on the ambiguity and the confidence
 * 
 * @author Ernesto
 *
 */
public class InteractiveProcessAmbiguity extends InteractiveProcess {
	

	private IndexManager index;
	
	private MappingManager mapping_manager;
	
	private AnchorAssessment mapping_assessment;
	
	private boolean useHeuristics;
	
	private int error_user;
	
	private boolean record_process;
	
	/**New mappings (to process) added in interactivity (by user or heuristic)*/ 
	private Stack<MappingObjectInteractivity> new2Add;
	/**New mappings (to process) deleted in interactivity (by user or heuristic)*/
	private Stack<MappingObjectInteractivity> new2Del;
	
	String interactivityFile;
	
	
	private Random generator;
	
	
	public InteractiveProcessAmbiguity(
			IndexManager index, 
			MappingManager mapping_manager, 
			boolean useHeuristics, 
			boolean orderQuestions,
			int error_user, //user error
			boolean ask_everything,
			boolean record_process,
			String interactivityFile){
		
		this.interactivityFile=interactivityFile;
		
		this.index = index;
		this.mapping_manager = mapping_manager;
		mapping_assessment = new AnchorAssessment(index, mapping_manager);
		this.useHeuristics = useHeuristics;
		this.error_user = error_user;
		
		this.record_process=record_process;
		
		generator = new Random(Calendar.getInstance().getTimeInMillis());
		
		new2Add = new Stack<MappingObjectInteractivity>();
		new2Del = new Stack<MappingObjectInteractivity>();
		
		//We use specific comparator
		if (orderQuestions)
			orderedMappings2Ask = 
				new TreeSet<MappingObjectInteractivity>(new MappingInteractivityComparator());
		else //No comparator
			orderedMappings2Ask = 
				new TreeSet<MappingObjectInteractivity>(new NoComparator());
		
		
		int ambiguity;
		
		
		//TODO: we ask everything. This will be used in experiments
		//We ask even anchors!!!
		if (ask_everything){
			for (int ide1 : mapping_manager.getLogMapMappings().keySet()){
				for (int ide2 : mapping_manager.getLogMapMappings().get(ide1)){
					if (mapping_manager.isId1SmallerThanId2(ide1, ide2)){
						mapping_manager.addMappingObject2AskUserList(ide1, ide2); //we add equivalence
					}
				}
			}
			
			mapping_manager.getLogMapMappings().clear();
			
			//mapping_manager.setExactAsFixed(false);		
			
		}
		
		
		//input: getListOfMappingsToAskUser
		for (MappingObjectInteractivity mapping : mapping_manager.getListOfMappingsToAskUser()){
			
						
			ambiguity = mapping_manager.getEntityAmbiguity_UserMappings(mapping.getIdentifierOnto1()) +
					mapping_manager.getEntityAmbiguity_UserMappings(mapping.getIdentifierOnto2());
						
			
			//First similarity value is ambiguity
			mapping.addSimilarityValue2List((double)ambiguity);//2, 3, 4
			
			//Second is confidence (it is already in mapping manager)
			//mapping.addSimilarityValue2List(getConfidence4Mapping(mapping));
			
			
			//Add to ordered list
			orderedMappings2Ask.add(mapping);
			
						
		}
		
		if (record_process)
			printOrdering();
			
		
		
	}
	
	
	private void printOrdering(){
		
		WriteFile writer = new WriteFile("/usr/local/data/DataUMLS/InteractiveProcess/ambiguityOrdering.txt"); 
		
		Iterator<MappingObjectInteractivity> it = orderedMappings2Ask.descendingIterator();
		
		MappingObjectInteractivity mapping;
		
		int num=0;
		
		while (it.hasNext()){
			
			mapping = it.next();
			
			writer.writeLine(mapping + " -> " + 
					getAmbiguityScore(mapping) + " " +
					getConfidence4Mapping(mapping) + " " +
					mapping_manager.isMappingInGoldStandard(mapping.getIdentifierOnto1(), mapping.getIdentifierOnto2()) 
					+ ", isub: " + mapping_manager.extractISUB4Mapping(mapping.getIdentifierOnto1(), mapping.getIdentifierOnto2())
					+ ", scope all: " + mapping_manager.extractScopeAll4Mapping(mapping.getIdentifierOnto1(), mapping.getIdentifierOnto2())
					+ ", scope anc: " + mapping_manager.extractScopeAnchors4Mapping(mapping.getIdentifierOnto1(), mapping.getIdentifierOnto2())
					+ ", Sim weak: " + mapping_manager.getSimWeak4Mapping2(mapping.getIdentifierOnto1(), mapping.getIdentifierOnto2())
					//+ ", removed flag: " + mapping.isRemovedFlagActive()
					);
		
			num++;
			
		}
		
		writer.closeBuffer();
		LogOutput.print("Num entries: " + num);
		
		
	}
	
	
	private double getConfidence4Mapping(MappingObjectInteractivity mapping){
		return mapping_manager.getConfidence4Mapping(
				mapping.getIdentifierOnto1(), 
				mapping.getIdentifierOnto2());
	}
	
	
	
	

	/**
	 * Start simulation interactive process
	 */
	public void startInteractiveProcess() {
		
		try {
		
			WriteFile writer = null;
			List<Integer> recordResultsPoints = new ArrayList<Integer>();
			recordResultsPoints.add(3000);
			int num_user_int=0;
			int point=0;
			
			
			int max_user_int=4000;
			
			long init, fin;
			double time=0.0;
				
			MappingObjectInteractivity selected_mapping;
			
			good_marked=0;
			bad_marked=0;
			
			
			if (record_process){
				//Should be an input parameter
				//WriteFile writer = new WriteFile("/usr/local/data/DataUMLS/InteractiveProcess/" + interactivityFile);
				writer = new WriteFile(interactivityFile);
				writer.writeLine("User Actions\tGood\tBad\tStill to ask\tPrecision\tRecall\tFMeasure\tTime Uuser");
				
				writer.writeLine(num_user_int + "\t" + good_marked + "\t" + bad_marked + "\t" + orderedMappings2Ask.size() 
						+ "\t" +  precision + "\t" +  recall + "\t" +  Fmeasure + "\t" + 0.0);
				
				recordResultsPoints.clear();
				recordResultsPoints.add(5);
				recordResultsPoints.add(10);
				recordResultsPoints.add(20);
				recordResultsPoints.add(30);
				recordResultsPoints.add(40);
				recordResultsPoints.add(50);
				recordResultsPoints.add(75);
				recordResultsPoints.add(100);
				recordResultsPoints.add(125);
				recordResultsPoints.add(150);
				recordResultsPoints.add(200);
				recordResultsPoints.add(250);
				recordResultsPoints.add(300);
				recordResultsPoints.add(350);
				recordResultsPoints.add(400);
				recordResultsPoints.add(450);
				recordResultsPoints.add(500);
				recordResultsPoints.add(600);
				recordResultsPoints.add(700);
				recordResultsPoints.add(800);
				recordResultsPoints.add(900);
				recordResultsPoints.add(1000);
				recordResultsPoints.add(1250);
				recordResultsPoints.add(1500);
				recordResultsPoints.add(1750);
				recordResultsPoints.add(2000);
				recordResultsPoints.add(2500);
				recordResultsPoints.add(3000);
			}			
			
			
			
			
			//precision and recall with no user
			//getPrecisionAndRecall();
			cleanMappingsAndRecordPrecisionAndRecall();
			
			
			
			
			
			
			if (useHeuristics){
			
				while (orderedMappings2Ask.size()>0 && num_user_int<max_user_int){ //Until no more to answer or limit questions 
					
					//Select first mapping in list			
					selected_mapping = orderedMappings2Ask.first();
					num_user_int++;
					
					
					//Mark as good or bad (depending on GS)
					markMappingAndAdd2List(selected_mapping, isMappingGood(selected_mapping));
		
					
					//Analyse added and deleted list (impact of user decision)
					init = Calendar.getInstance().getTimeInMillis();
					//if (useHeuristics)
					evaluateImpactOfMapping();
					fin = Calendar.getInstance().getTimeInMillis();
					time = (double)(((double)fin-(double)init)/1000.0);
								
					
					//Reorder list
					reOrderMappings2ask();
					
					
					//Get precision and recall
					//getPrecisionAndRecall();
					
					//Print results each 10 answers...
					if (num_user_int==recordResultsPoints.get(point) && record_process){
						LogOutput.print("ITERATION: " + num_user_int);
						cleanMappingsAndRecordPrecisionAndRecall();
						
						writer.writeLine(num_user_int + "\t" + good_marked + "\t" + bad_marked + "\t" + orderedMappings2Ask.size()
							+ "\t" +  precision + "\t" +  recall + "\t" +  Fmeasure + "\t" + time);
						
						if (point < recordResultsPoints.size()-1)
							point++;
						else
							point=0;
							
					}
					
				}
				
				StatisticsManager.Mask_heur = num_user_int;
				LogOutput.print("ITERATION: " + num_user_int);
								
				if (record_process){
					cleanMappingsAndRecordPrecisionAndRecall();
					
					writer.writeLine(num_user_int + "\t" + good_marked + "\t" + bad_marked + "\t" + orderedMappings2Ask.size()
							+ "\t" +  precision + "\t" +  recall + "\t" +  Fmeasure + "\t" + time);
				}
				
				
			}
			
			else { //No heuristics. only user with or without errors
				
				//User with error
				for (MappingObjectInteractivity mapping : mapping_manager.getListOfMappingsToAskUser()){
					if (isMappingGood(mapping)){
						if (isUserFailing()){
							mapping.setRemovedFlag(true); //Marking bad a good mappings
						}
						else{
							mapping.setAddedFlag(true);
						}
					}
					else{
						
						if (isUserFailing()){
							mapping.setAddedFlag(true); //Marking good a bad mappings
						}
						else {
							mapping.setRemovedFlag(true);
						}
					}
				}
			}
			
			//Print results after test
			//writer.writeLine(num_user_int + "\t" + good_marked + "\t" + bad_marked + "\t" + orderedMappings2Ask.size()
			//		+ "\t" +  precision + "\t" +  recall + "\t" +  Fmeasure + "\t" + time);
			
			//TODO Check how many mappings are wrongly flagged as bad or good 
			
			if (record_process){
				writer.closeBuffer();
			}
			
			//If user ended then apply heuristics 
		}
		catch (Exception e){
			System.err.println("Error in interactive precess: " + e.getLocalizedMessage());
			e.printStackTrace();
		}
		
		
	}
	
	
	
	//It depends on error percentage
	private boolean isUserFailing(){
		
		int random_num = generator.nextInt(100);
		
		if (random_num < error_user){
			return true;
		}
		return false;
		
	}
	
	
	
	
	int good_marked=0;
	int bad_marked=0;
	
	/**
	 * Marks a given mapping to be added or deleted and adds it to the correspondent list to be reviewed later 
	 * @param m
	 * @param toadd
	 */
	private void markMappingAndAdd2List(MappingObjectInteractivity m, boolean toadd){
		
		if (toadd){
			m.setAddedFlag(true);
			new2Add.add(m);
			
			good_marked++;
			
			//User additions
			mapping_manager.addMappingAddedByUser2Structure(m.getIdentifierOnto1(), m.getIdentifierOnto2());
			mapping_manager.addMappingAddedByUser2Structure(m.getIdentifierOnto2(), m.getIdentifierOnto1());
			
		}
		else {
			m.setRemovedFlag(true);
			new2Del.add(m);
			bad_marked++;
		}
		
	}
	
	
	

	
	
	
	/**
	 * Ambiguous mappings with added mapping are susceptible to be deleted. 
	 * Moreover mappings in conflict will be deleted
	 * @param added_mapping
	 */
	private void removeMappingsInConflictWithAddition(MappingObjectInteractivity added_mapping){
		
		//Check mappings in conflict
		for (MappingObjectInteractivity mapping2check : mapping_manager.getListOfMappingsToAskUser()){
					
			if (mapping2check.equals(added_mapping))
				continue;
					
			//Already removed or added
			if (mapping2check.isRemovedFlagActive() || mapping2check.isAddedFlagActive())
				continue;
					
			
			if (areMappingsAmbiguous(
					added_mapping.getIdentifierOnto1(),
					added_mapping.getIdentifierOnto2(),
					mapping2check.getIdentifierOnto1(),
					mapping2check.getIdentifierOnto2())){
				
				mapping2check.setRemovedFlag(true);
				new2Del.push(mapping2check);
				
				
				//System.out.println(index.getName4ConceptIndex(added_mapping.getIdentifierOnto1()) + "  " +
				//		index.getName4ConceptIndex(added_mapping.getIdentifierOnto2()));
				
				//System.out.println("\t"+ index.getName4ConceptIndex(mapping2check.getIdentifierOnto1()) + "  " +
				//		index.getName4ConceptIndex(mapping2check.getIdentifierOnto2()));
				
				
				
			}			
					
			else if (areMappingsInConflict(
					added_mapping.getIdentifierOnto1(),
					added_mapping.getIdentifierOnto2(),
					mapping2check.getIdentifierOnto1(),
					mapping2check.getIdentifierOnto2())){
						
			
				mapping2check.setRemovedFlag(true);
				new2Del.push(mapping2check);
			}
			
		}
		
	}
	
	
	/**
	 * Ambiguous mappings with deleted mapping are susceptible to be added since they do not have high ambiguity any more 
	 * @param de_mapping
	 */
	private void addMappingsAmbiguousWithDeletion(MappingObjectInteractivity del_mapping){
			
		//Check mappings in conflict
		for (MappingObjectInteractivity mapping2check : mapping_manager.getListOfMappingsToAskUser()){
					
			if (mapping2check.equals(del_mapping))
				continue;
					
			//Already removed or added
			if (mapping2check.isRemovedFlagActive() || mapping2check.isAddedFlagActive())
				continue;
			
			//Only if scope and good confidence
			if (!hasScopeAll(mapping2check) || !hasGoodConfidence(mapping2check))
				continue;
			

			if (areMappingsAmbiguous(
					del_mapping.getIdentifierOnto1(),
					del_mapping.getIdentifierOnto2(),
					mapping2check.getIdentifierOnto1(),
					mapping2check.getIdentifierOnto2())){
				
				
				mapping2check.setAddedFlag(true);
				new2Add.push(mapping2check);
				
			}
			//This is too dangerous
			/*else if (areMappingsInConflict(
					del_mapping.getIdentifierOnto1(),
					del_mapping.getIdentifierOnto2(),
					mapping2check.getIdentifierOnto1(),
					mapping2check.getIdentifierOnto2())){
						
			
				mapping2check.setAddedFlag(true);
				new2Add.push(mapping2check);
			}*/
		}
		
	}
	
	
	
	/**
	 * We evaluate the impact of user decision
	 */
	private void evaluateImpactOfMapping(){
		
		
		//TODO Algorithm
		
		MappingObjectInteractivity mapping;
		
		while (new2Add.size()>0 || new2Del.size()>0){
			
			if (new2Add.size()>0){
				
				mapping = new2Add.pop();
				
				//Marks them as deleted and add them to new2Del stack
				removeMappingsInConflictWithAddition(mapping);
			
			}
			
			
			
			if (new2Del.size()>0){
				
				mapping = new2Del.pop();
				
				//Marks them as added and adds them to new2Add stack
				addMappingsAmbiguousWithDeletion(mapping);
				
			}
			
			
		}
		
		
		
	}
	
	
	private void reOrderMappings2ask(){
		
		//we may not need this method currently
		//However for the user interface we will only show mappings in orderedMappings2Ask
		
		orderedMappings2Ask.clear();		
		
		//We add mappings to order
		for (MappingObjectInteractivity mapping : mapping_manager.getListOfMappingsToAskUser()){
		
			//For the GUI we should only order by impact			
			if (mapping.isRemovedFlagActive() || mapping.isAddedFlagActive())				
				continue;
				
				
			orderedMappings2Ask.add(mapping);				
								
		}
		
	}
	
	
	public void endInteractiveProcess() {
		endInteractiveProcess(false);
	}
	

	@Override
	public void endInteractiveProcess(boolean filter) {

		//Add mappings with flag "toadd" and apply heuristics
		//Set added mappings or (with scope and confidence>0.5)
		for (MappingObjectInteractivity mapping : mapping_manager.getListOfMappingsToAskUser()){
						
			if (!filter || //In case we do not want to apply further filtering
				mapping.isAddedFlagActive() //User 
				|| (hasScopeAll(mapping) && hasGoodConfidence(mapping) && !mapping.isRemovedFlagActive()) //LogMap heuristics for not remove mappings
				){
				
				//SOME MAPPINGS MAY REPRESENT ONLY ONE SIDE
				//EITHER THE USER DECIDED TO SPLIT IT or IT WAS D&G
				if (mapping.getDirMapping()==Utilities.EQ || mapping.getDirMapping()==Utilities.L2R)
					mapping_manager.addSubMapping2Mappings2Review(mapping.getIdentifierOnto1(), mapping.getIdentifierOnto2());
				
				if (mapping.getDirMapping()==Utilities.EQ || mapping.getDirMapping()==Utilities.R2L)
					mapping_manager.addSubMapping2Mappings2Review(mapping.getIdentifierOnto2(), mapping.getIdentifierOnto1());
			
			}
			else{
				
				mapping_manager.addEquivMapping2DiscardedAnchors(mapping.getIdentifierOnto1(), mapping.getIdentifierOnto2());
				
			}
		}
		
		
		//TODO Clear structures
		//mapping_manager.getListOfMappingsToAskUser().clear();
		orderedMappings2Ask.clear();
		mapping_manager.setStringAnchors();
		
	}

	
	private void setStringMappings(){
		
		//Sets anchors (it also clears structure)
		mapping_manager.setStringAnchors();
		
		
		//Set mappings to review
		for (int ide1 : mapping_manager.getMappings2Review().keySet()){
		
			for (int ide2 : mapping_manager.getMappings2Review().get(ide1)){
				
				mapping_manager.addStringAnchor(ide1, ide2);
				
			}
			
		}
		
		//Do not add them
		//TODO Add Weakened mappings (only for P&R)
		//Note that removed ones are not added
		/*for (int ide1 : mapping_manager.getWeakenedDandGAnchors().keySet()){
			
			for (int ide2 : mapping_manager.getWeakenedDandGAnchors().get(ide1)){
						
				if (mapping_manager.isMappingInConflictWithFixedMappings(ide1, ide2) || 
						mapping_manager.isMappingInferred(ide1, ide2) ||
						mapping_manager.isMappingInConflictiveSet(ide1, ide2)){
					continue;
				}
				
				mapping_manager.addStringAnchor(ide1, ide2);
				
			}
		}*/
		
		
		
		
	}
	

	
	
	
	private void getPrecisionAndRecall() {
		
		Set <MappingObjectStr> intersection;
		
		//We set current mappings to get provisional P&R
		setStringMappings();
			
		
		intersection=new HashSet<MappingObjectStr>(mapping_manager.getStringLogMapMappings());
		intersection.retainAll(mapping_manager.getStringGoldStandardAnchors());
		
		precision=((double)intersection.size())/((double)mapping_manager.getStringLogMapMappings().size());
		recall=((double)intersection.size())/((double)mapping_manager.getStringGoldStandardAnchors().size());

		Fmeasure = (2*recall*precision)/(precision+recall);
		
		//Sets anchors (it also clears structure)
		mapping_manager.setStringAnchors();
		
		
	}

	
	
	private void cleanMappingsAndRecordPrecisionAndRecall() throws Exception{
		
		
		//We add mappings from current interactivity status
		for (MappingObjectInteractivity mapping : mapping_manager.getListOfMappingsToAskUser()){
			
			if (
				mapping.isAddedFlagActive() //User 
				|| (hasScopeAll(mapping) && hasGoodConfidence(mapping) && !mapping.isRemovedFlagActive()) //LogMap heuristics for not remove mappings
				){
				
				mapping_manager.addSubMapping2Mappings2Review(mapping.getIdentifierOnto1(), mapping.getIdentifierOnto2());
				mapping_manager.addSubMapping2Mappings2Review(mapping.getIdentifierOnto2(), mapping.getIdentifierOnto1());
			
			}
		}
		
		//TODO We also add weakened mappings in initial list "assesMappings2AskUser"
		//mapping_manager.assesWeakenedMappingsDandG(false, false);
		
		
		//Clean "Mappings2Review" mappings D&G
		mapping_assessment.CheckSatisfiabilityOfIntegration_DandG(mapping_manager.getMappings2Review());  //With Fixed mappings!
		
		
		
		//With Anchors and clean mappinsg 2 review
		getPrecisionAndRecall();
		
		///We clear them
		mapping_manager.getMappings2Review().clear();
		
		
	}
	
	
	
	
	
	
	private boolean areMappingsAmbiguous(int ideA, int ideB, int ideAA, int ideBB){
		
		
		//We have already checked they are not equivalent
		if (ideA==ideAA || ideB==ideBB){
			
			return true;
			
		}		
		
		return false;
		
	}
	
	
	
	//private double getConfidence(MappingObjectInteractivity m){
		//return m.getSimilarityList().get(1);
	//}
	
	
	
	private boolean areMappingsInConflict(int ideA, int ideB, int ideAA, int ideBB){
		
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
	
		AdisjAA = !AcontAA && !AAcontA && index.areDisjoint(ideA, ideAA);
		
		BdisjBB = !BcontBB && !BBcontB && index.areDisjoint(ideB, ideBB);
				
				
		conflict = ((AcontAA || AAcontA) && BdisjBB) || ((BcontBB || BBcontB) && AdisjAA);
		
		return conflict;
		
	}
	
	
	private boolean isMappingGood(MappingObjectInteractivity m){
		
		//TODO we just check link occurrence not dir of mapping		
		if (mapping_manager.isMappingInGoldStandard(
				m.getIdentifierOnto1(), 
				m.getIdentifierOnto2()))
			return true;
		
		return false;
		
	}
	
	
	private double getAmbiguityScore(MappingObjectInteractivity m){
		
		return m.getSimilarityList().get(0);
		
	}
	
	
	private void setAmbiguityScore(MappingObjectInteractivity m, double ambiguity){
		
		m.getSimilarityList().set(0, ambiguity);
		
	}
	
	
	private boolean hasGoodConfidence(MappingObjectInteractivity m){
		
		return (mapping_manager.getConfidence4Mapping(m.getIdentifierOnto1(), m.getIdentifierOnto2())>Parameters.good_confidence);
		
	}
	
	
	private boolean hasScopeAll(MappingObjectInteractivity m){
		
		return (mapping_manager.extractScopeAll4Mapping(m.getIdentifierOnto1(), m.getIdentifierOnto2())>Parameters.bad_score_scope);
		
	}
	
	
	private boolean hasScopeAnchors(MappingObjectInteractivity m){
		
		return (mapping_manager.extractScopeAnchors4Mapping(m.getIdentifierOnto1(), m.getIdentifierOnto2())>Parameters.bad_score_scope);
		
	}
	
	
	private double getScopeAll(MappingObjectInteractivity m){
		
		return (mapping_manager.extractScopeAll4Mapping(m.getIdentifierOnto1(), m.getIdentifierOnto2()));
		
	}
	
	
	private double getScopeAnchors(MappingObjectInteractivity m){
		
		return (mapping_manager.extractScopeAnchors4Mapping(m.getIdentifierOnto1(), m.getIdentifierOnto2()));
		
	}
	
	
	
	

	
	
	
	
	
	
	
	
	
	
	/*
	 * @deprecated
	private void removeAmbiguousMappingsWith(MappingObjectInteractivity added_mapping){
		
		
		//TODO Check that conf<0.50 and no scope?
		
		
		MappingObjectInteractivity mapping_amb = new MappingObjectInteractivity(-1, -1);
		int index_list;
		
		
		
		for (int ide2 : mapping_manager.getToAskUserMappings().get(added_mapping.getIdentifierOnto1())){
			
			mapping_amb.setIdentifierOnto1(added_mapping.getIdentifierOnto1());
			mapping_amb.setIdentifierOnto2(ide2);
			
			index_list = mapping_manager.getListOfMappingsToAskUser().indexOf(mapping_amb);
			
			//Alredy added or removed
			if (mapping_manager.getListOfMappingsToAskUser().get(index_list).isAddedFlagActive() || mapping_manager.getListOfMappingsToAskUser().get(index_list).isRemovedFlagActive())
				continue;
			
			mapping_manager.getListOfMappingsToAskUser().get(index_list).setRemovedFlag(true);
			new2Del.push(mapping_manager.getListOfMappingsToAskUser().get(index_list));
			
			
		}
		
		for (int ide1 : mapping_manager.getToAskUserMappings().get(added_mapping.getIdentifierOnto2())){
			
			mapping_amb.setIdentifierOnto1(ide1);
			mapping_amb.setIdentifierOnto2(added_mapping.getIdentifierOnto2());
			
			index_list = mapping_manager.getListOfMappingsToAskUser().indexOf(mapping_amb);
			
			//Alredy added or removed
			if (mapping_manager.getListOfMappingsToAskUser().get(index_list).isAddedFlagActive() || mapping_manager.getListOfMappingsToAskUser().get(index_list).isRemovedFlagActive())
				continue;
			
			mapping_manager.getListOfMappingsToAskUser().get(index_list).setRemovedFlag(true);
			new2Del.push(mapping_manager.getListOfMappingsToAskUser().get(index_list));
			
			
		}
		
		
		
		
		
	}*/
	
	
	/**
	 * @deprecated
	 */
	private void removeConflictiveMappingsWith(MappingObjectInteractivity added_mapping){
		
		
		//Check mappings in conflict
		for (MappingObjectInteractivity mapping_conf : mapping_manager.getListOfMappingsToAskUser()){
			
			if (mapping_conf.equals(added_mapping))
				continue;
			
			//Already removed
			if (mapping_conf.isRemovedFlagActive())
				continue;
			
			
			if (areMappingsInConflict(
					added_mapping.getIdentifierOnto1(),
					added_mapping.getIdentifierOnto2(),
					mapping_conf.getIdentifierOnto1(),
					mapping_conf.getIdentifierOnto2())){
				
				//Alredy removed
				if (mapping_conf.isAddedFlagActive()){
					System.err.println("Conflict between added mappings. Shoudl be solved later");
				}
				else {
					mapping_conf.setRemovedFlag(true);
					new2Del.push(mapping_conf);
				}
				
				
			}
			
		}
		
		
		
	}
	
	
	/*
	 * @deprecated
	private void addAmbiguousMappingsWith(MappingObjectInteractivity del_mapping){
		
		
		
		MappingObjectInteractivity mapping_amb = new MappingObjectInteractivity(-1, -1);
		int index_list;
		
		//We remove ambiguous mappings
		
		
		for (int ide2 : mapping_manager.getToAskUserMappings().get(del_mapping.getIdentifierOnto1())){
			
			mapping_amb.setIdentifierOnto1(del_mapping.getIdentifierOnto1());
			mapping_amb.setIdentifierOnto2(ide2);
			
			index_list = mapping_manager.getListOfMappingsToAskUser().indexOf(mapping_amb);
			
			//Alredy added or removed
			if (mapping_manager.getListOfMappingsToAskUser().get(index_list).isAddedFlagActive() || mapping_manager.getListOfMappingsToAskUser().get(index_list).isRemovedFlagActive())
				continue;
			
			//We remove ambiguity index
			setAmbiguityScore(
					mapping_manager.getListOfMappingsToAskUser().get(index_list),
					getAmbiguityScore(mapping_manager.getListOfMappingsToAskUser().get(index_list))-1);
			
			//No ambiguity any more, scope and good confidence
			if (getAmbiguityScore(mapping_manager.getListOfMappingsToAskUser().get(index_list))<=2 &&
				hasScopeAll(mapping_manager.getListOfMappingsToAskUser().get(index_list)) && 
				hasGoodConfidence(mapping_manager.getListOfMappingsToAskUser().get(index_list))){
				
					mapping_manager.getListOfMappingsToAskUser().get(index_list).setAddedFlag(true);
					new2Add.push(mapping_manager.getListOfMappingsToAskUser().get(index_list));
			}
			
			
		}
		
		for (int ide1 : mapping_manager.getToAskUserMappings().get(del_mapping.getIdentifierOnto2())){
			
			mapping_amb.setIdentifierOnto1(ide1);
			mapping_amb.setIdentifierOnto2(del_mapping.getIdentifierOnto2());
			
			index_list = mapping_manager.getListOfMappingsToAskUser().indexOf(mapping_amb);
			
			
			//Alredy added or removed
			if (mapping_manager.getListOfMappingsToAskUser().get(index_list).isAddedFlagActive() || mapping_manager.getListOfMappingsToAskUser().get(index_list).isRemovedFlagActive())
				continue;
			
			
			//We remove ambiguity index
			setAmbiguityScore(
					mapping_manager.getListOfMappingsToAskUser().get(index_list),
					getAmbiguityScore(mapping_manager.getListOfMappingsToAskUser().get(index_list))-1);
			
			
			
			//No ambiguity any more, scope and good confidence
			if (getAmbiguityScore(mapping_manager.getListOfMappingsToAskUser().get(index_list))<=2 &&
				hasScopeAll(mapping_manager.getListOfMappingsToAskUser().get(index_list)) && 
				hasGoodConfidence(mapping_manager.getListOfMappingsToAskUser().get(index_list))){
				
					mapping_manager.getListOfMappingsToAskUser().get(index_list).setAddedFlag(true);
					new2Add.push(mapping_manager.getListOfMappingsToAskUser().get(index_list));
			}
			
			
		}
		
		
	}*/
	
	
	
	
	
	
	
	
	/**
	 * No comparator. for no order of mappings to ask
	 * @author Ernesto
	 *
	 */
	private class NoComparator implements Comparator<MappingObjectInteractivity> {
		
		public int compare(MappingObjectInteractivity m1, MappingObjectInteractivity m2) {
			
			return -1;
			
		}
		
	}
	
	
	
	/**
	 * Comparator
	 * @author Ernesto
	 *
	 */
	private class MappingInteractivityComparator implements Comparator<MappingObjectInteractivity> {//extends MappingInteractivityComparator {
		
		
		/**
		 * @deprecated
		 * @param m1
		 * @param m2
		 * @return
		 */
		public int compare2(MappingObjectInteractivity m1, MappingObjectInteractivity m2) {

			//Ambiguity first
			//if (getAmbiguityScore(m1)<getAmbiguityScore(m2)){
			//	return 1; //better les ambiguity
			//}
			//else if (getAmbiguityScore(m1)==getAmbiguityScore(m2)){
				
				//Second ask for scope anchors
				if (getScopeAnchors(m1)<getScopeAnchors(m2)){
					return -1;
				}
				else if (getScopeAnchors(m1)==getScopeAnchors(m2)){
					
					//Third ask for scope
					if (getScopeAll(m1)<getScopeAll(m2)){
						return -1;
					}
					else if (getScopeAll(m1)==getScopeAll(m2)){
						
						//Fourth compare confidence
						if (getConfidence4Mapping(m1)<getConfidence4Mapping(m2)){
							return -1;					
						}
						else{
							return 1;
						}
					}
					else{
						return 1;
					}
					
				}
				else{
					return 1;
				}
				
			//}
			//else {
			//	return -1;
			//}
		}
		
		
		public int orderbyConfidence(MappingObjectInteractivity m1, MappingObjectInteractivity m2){
			//Fourth compare confidence
			if (getConfidence4Mapping(m1)<getConfidence4Mapping(m2)){
				return -1;					
			}
			else{
				return 1;
			}
		}
		
		
		public int orderByScope(MappingObjectInteractivity m1, MappingObjectInteractivity m2){
			if (getScopeAll(m1)<getScopeAll(m2)){
				return -1;						
			}					
			else{
				return 1;
			}
		}
		
		
		/**
		 * We order by scope with only anchors and confidence
		 */
		public int compare(MappingObjectInteractivity m1, MappingObjectInteractivity m2) {

		
			//First ask for scope anchors
			if (getScopeAnchors(m1)<getScopeAnchors(m2)){
				return -1;
			}
			
			else if (getScopeAnchors(m1)==getScopeAnchors(m2)){ //If no scope anchor or equal the for confidence
				
				//We only differentiate if it has scope or not (not the value itself)
				if ((hasScopeAll(m1) && hasScopeAll(m2)) || (!hasScopeAll(m1) && !hasScopeAll(m2))){					
					return orderbyConfidence(m1, m2);										
				}
				else{				
					//One of them has no confidence then give less preference
					return orderByScope(m1, m2);
				}				
			}
			else{
				return 1;
			}
				
			
		}
		
	}

}

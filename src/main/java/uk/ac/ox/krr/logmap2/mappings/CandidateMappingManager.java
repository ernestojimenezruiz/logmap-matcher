package uk.ac.ox.krr.logmap2.mappings;

import java.util.LinkedList;

import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.ac.ox.krr.logmap2.oaei.oracle.OracleManager;

import uk.ac.ox.krr.logmap2.Parameters;
import uk.ac.ox.krr.logmap2.SIAssessment.DataPropertyMappingAssessment;
import uk.ac.ox.krr.logmap2.SIAssessment.ObjectPropertyMappingAssessment;
import uk.ac.ox.krr.logmap2.SIAssessment.InstanceMatchingAssessment;
//import uk.ac.ox.krr.logmap2.backknowlede.ManageBackgroundKnowledge;
import uk.ac.ox.krr.logmap2.indexing.IndexManager;
import uk.ac.ox.krr.logmap2.indexing.OntologyProcessing;
import uk.ac.ox.krr.logmap2.indexing.entities.ClassIndex;
import uk.ac.ox.krr.logmap2.io.LogOutput;
import uk.ac.ox.krr.logmap2.mappings.objects.MappingObjectIdentifiers;
import uk.ac.ox.krr.logmap2.mappings.objects.MappingObjectStr;
import uk.ac.ox.krr.logmap2.utilities.Lib;
import uk.ac.ox.krr.logmap2.statistics.*;
import uk.ac.ox.krr.logmap2.utilities.Utilities;

/**
 * 
 * This class extracts the anchors by intersecting the lexical inverted files
 * associated with the respective ontologies. It will also manage the discovery of
 * further mappings
 *
 * @author Ernesto Jimenez-Ruiz
 * Created: Dec 13, 2011
 *
 */
public class CandidateMappingManager extends MappingManager {

	
	private OntologyProcessing onto_process1;
	private OntologyProcessing onto_process2;
	
	private Set<Set<String>> if_exact_intersection;
	private Set<Set<String>> if_stemming_intersection;
	private Set<Set<String>> if_weak_intersection;

	private Set<Set<String>> if_exact_intersection4data_prop;
	
	private Set<Set<String>> if_exact_intersection4obj_prop;
	
	private Set<Set<String>> if_exact_intersection4individuals;	
	private Set<String> if_weak_intersection4individuals;
	private Set<String> if_roleassertions_intersection4individuals;
	
	private Set<MappingObjectIdentifiers> input_mappings = new HashSet<MappingObjectIdentifiers>();
	
	private int IF_VALIDATED=0;
	private int IF_EXACT=1;
	private int IF_STEMMING=2;
	private int IF_WEAK=3;
	
	
	//Statistics: which mappings should be required to ask to the user
	/*private int include=0;
	private int exclude=0;
	private int ask=0;
	
	private int include_good=0;
	private int exclude_good=0;
	private int ask_good=0;*/
	
	/**
	 * 
	 * @param index
	 * @param onto_process1
	 * @param onto_process2
	 */
	public CandidateMappingManager(
			IndexManager index, 
			OntologyProcessing onto_process1, 
			OntologyProcessing onto_process2){
		
		this.index=index;
		this.onto_process1=onto_process1;
		this.onto_process2=onto_process2;
		
	}
	
	
	
	public void clearIntersectedInvertedFilesExact(){
		if_exact_intersection.clear();
		
	}

	
	public void clearIntersectedInvertedFilesStemmingAndWeak(){
		if_stemming_intersection.clear();
		if_weak_intersection.clear();
	}
	
	
	public void clearIntersectedInvertedFiles4Properties(){
		if_exact_intersection4data_prop.clear();
		if_exact_intersection4obj_prop.clear();
		
	}
	
	
	
	/**
	 * Intersects exact and stemming IFs, extract IF weak and intersects them
	 */
	public void intersectInvertedFiles(){
		
		//INTERSECTION
		
		//DATA PROP IF
		if_exact_intersection4data_prop = onto_process1.getInvertedFileExactMatching4DataProp().keySet();
		if_exact_intersection4data_prop.retainAll(onto_process2.getInvertedFileExactMatching4DataProp().keySet());
		onto_process2.getInvertedFileExactMatching4DataProp().keySet().retainAll(if_exact_intersection4data_prop);
		
		//OBJ PROP IF
		if_exact_intersection4obj_prop = onto_process1.getInvertedFileExactMatching4ObjProp().keySet();
		if_exact_intersection4obj_prop.retainAll(onto_process2.getInvertedFileExactMatching4ObjProp().keySet());
		onto_process2.getInvertedFileExactMatching4ObjProp().keySet().retainAll(if_exact_intersection4obj_prop);
		
		
		//INDIVIDUALS IF exact
		if_exact_intersection4individuals = onto_process1.getInvertedFileMatching4Individuals().keySet();
		if_exact_intersection4individuals.retainAll(onto_process2.getInvertedFileMatching4Individuals().keySet());
		onto_process2.getInvertedFileMatching4Individuals().keySet().retainAll(if_exact_intersection4individuals);
		
		
		//INDIVIDUALS IF weak
		if_weak_intersection4individuals = onto_process1.getInvertedFileWeakMatching4Individuals().keySet();
		if_weak_intersection4individuals.retainAll(onto_process2.getInvertedFileWeakMatching4Individuals().keySet());
		onto_process2.getInvertedFileWeakMatching4Individuals().keySet().retainAll(if_weak_intersection4individuals);
		
		
		//INDIVIDUALS IF role assertions
		if_roleassertions_intersection4individuals = onto_process1.getInvertedFileRoleAssertions().keySet();
		if_roleassertions_intersection4individuals.retainAll(onto_process2.getInvertedFileRoleAssertions().keySet());
		onto_process2.getInvertedFileRoleAssertions().keySet().retainAll(if_roleassertions_intersection4individuals);
				
		
		
		
		
		
		
		
		
		
		Map<Set<String>,Set<Integer>> if_difference1;
		Map<Set<String>,Set<Integer>> if_difference2;
		
		
		//INT EXACT
		
		LogOutput.print("IF Exact 1: " + onto_process1.getInvertedFileExactMatching().size());
		LogOutput.print("IF Exact 2: " + onto_process2.getInvertedFileExactMatching().size());
		//We perform intersection an we only keep in inverted file the intersected elements
		if_exact_intersection = onto_process1.getInvertedFileExactMatching().keySet();
		if_exact_intersection.retainAll(onto_process2.getInvertedFileExactMatching().keySet());
		onto_process2.getInvertedFileExactMatching().keySet().retainAll(if_exact_intersection);
		
		LogOutput.print("Intersection IF Exact: " + if_exact_intersection.size());
		
		
		
		//WEAK STEMMING
		LogOutput.print("IF Stemming 1: " + onto_process1.getInvertedFileWeakLabelsStemming().size());
		LogOutput.print("IF Stemming 2: " + onto_process2.getInvertedFileWeakLabelsStemming().size());
		
		if_stemming_intersection = new HashSet<Set<String>>(onto_process1.getInvertedFileWeakLabelsStemming().keySet());
		if_stemming_intersection.retainAll(onto_process2.getInvertedFileWeakLabelsStemming().keySet());
		
		
		LogOutput.print("Intersection IF Stemming: " + if_stemming_intersection.size());
		
		
		//NOT MATCHED IN IF STEMMING -> TO WEAK ENTRIES
		if_difference1  = new HashMap<Set<String>,Set<Integer>>(onto_process1.getInvertedFileWeakLabelsStemming());
		//Reduce IF
		onto_process1.getInvertedFileWeakLabelsStemming().keySet().retainAll(if_stemming_intersection);
		if_difference1.keySet().removeAll(if_stemming_intersection);
		onto_process1.addEntries2InvertedFileWeakLabels(if_difference1);
		if_difference1.clear();
		LogOutput.print("Entries in Weak IF from Setemming: " + onto_process1.getInvertedFileWeakLabels().size());
		
		
		if_difference2  = new HashMap<Set<String>,Set<Integer>>(onto_process2.getInvertedFileWeakLabelsStemming());
		//Reduce IF
		onto_process2.getInvertedFileWeakLabelsStemming().keySet().retainAll(if_stemming_intersection);
		if_difference2.keySet().removeAll(if_stemming_intersection);
		onto_process2.addEntries2InvertedFileWeakLabels(if_difference2);
		if_difference2.clear();
		LogOutput.print("Entries in Weak IF from Setemming: " + onto_process2.getInvertedFileWeakLabels().size());
		
		
		//Extract weak mappings
		//onto_process1.setFullInvertedFileWeakLabels();
		//onto_process2.setFullInvertedFileWeakLabels();
		onto_process1.setInvertedFileWeakLabels();
		onto_process2.setInvertedFileWeakLabels();
		
		LogOutput.print("IF Weak 1: " + onto_process1.getInvertedFileWeakLabels().size());
		LogOutput.print("IF Weak 2: " + onto_process2.getInvertedFileWeakLabels().size());
		
		
		//We perform intersection an we only keep in inverted file the intersected elements
		if_weak_intersection = onto_process1.getInvertedFileWeakLabels().keySet();
		if_weak_intersection.retainAll(onto_process2.getInvertedFileWeakLabels().keySet());
		onto_process2.getInvertedFileWeakLabels().keySet().retainAll(if_weak_intersection);
		
		LogOutput.print("Intersection IF Weak: " + if_weak_intersection.size());
		
		
		
		
		
	}
	
	
	
	
	/**
	 * This method extract all (weak) mappings. This set will only be used for scope purposes  
	 */
	public void extractAllWeakMappings() {
		
		//if (true)
		//return;
		
		for (Set<String> set_str : if_exact_intersection){			
			for (int ide1 : onto_process1.getInvertedFileExactMatching().get(set_str)){
				for (int ide2 : onto_process2.getInvertedFileExactMatching().get(set_str)){
					addEquivMapping2ListOfWeakAnchors(ide1, ide2);
				}
			}
		}
		
		if (Parameters.use_stemming) {
			for (Set<String> set_str : if_stemming_intersection){
				for (int ide1 : onto_process1.getInvertedFileWeakLabelsStemming().get(set_str)){
					for (int ide2 : onto_process2.getInvertedFileWeakLabelsStemming().get(set_str)){
						addEquivMapping2ListOfWeakAnchors(ide1, ide2);
					}
				}
			}
		}
		
		for (Set<String> set_str : if_weak_intersection)
			for (int ide1 : onto_process1.getInvertedFileWeakLabels().get(set_str)) 
				for (int ide2 : onto_process2.getInvertedFileWeakLabels().get(set_str))
					addEquivMapping2ListOfWeakAnchors(ide1, ide2);
		
		
		
		
		
		//Count all weak anchors
		int weak_anchors=0;
		for (int ide1 : allWeakMappings1N.keySet()){
			weak_anchors+=allWeakMappings1N.get(ide1).size();
		}
		LogOutput.print("WEAK ANCHORS: " + weak_anchors);
		
		
		
		
	}
	
	
	
	
	public void extractCandidatesSubsetFromWeakMappings(){
		
		extractSubsetFromWeakMapping();
		extractSubsetFromWeakMappingFrequency();
		
		//Clear structures
		//Clear weak
		onto_process1.clearInvertedFileWeak();
		onto_process2.clearInvertedFileWeak();
		if_weak_intersection.clear();
		
		
		
	}
	
	

	public void createAnchors() {
		createAnchors(false);
	}
	
	
	
	@Override
	public void createAnchors(boolean are_input_mapping_validated) {
		
		try {
			
			//From input mappings (e.g. composed mappings given as input)
			createCandidatesFromInputMappings(are_input_mapping_validated);
			
			//Inverted file intersection
			createCandidatesFromExactIF();
			
			
			//Dataprop and ObjectProp. Currently is at the end of the process
			
			
			
			//Clear
			//Clear Exact IF
			onto_process1.clearInvertedFilesExact();
			onto_process2.clearInvertedFilesExact();
			
			clearIntersectedInvertedFilesExact();
			//clearIntersectedInvertedFiles4Properties();
			
			
		}
		catch (Exception e){
			System.err.println("Error extracting anchors: " + e.getMessage());
			e.printStackTrace();
		}
		
	}
	
	
	
	/*public void createCandidatesFromBackgroundKnowledge() throws Exception{
		
		
		Map<String, Set<String>> map = ManageBackgroundKnowledge.createComposedMappingsMouse2Uberon2NCI();
		
		int mouse_id = -1;
		int nci_id = -1;
		
		for (String mouse_iri : map.keySet()){
			
			mouse_id = onto_process1.getIdentifier4ConceptName(Utilities.getEntityLabelFromURI(mouse_iri));
			
			for (String nci_iri : map.get(mouse_iri)){
			
				nci_id = onto_process2.getIdentifier4ConceptName(Utilities.getEntityLabelFromURI(nci_iri));
				
				if (mouse_id>=0 && nci_id>=0){
					
					//We will see
					//evaluateCandidateMapping(mouse_id, nci_id, IF_EXACT);
					
					//We do not need to filter by isub in exact cases
					//With scope, no ambiguous or best confidence
					addSubMapping2ListOfAnchors(mouse_id, nci_id);
					addSubMapping2ListOfAnchors(nci_id, mouse_id);
					
					//we add confidences
					addIsub2Structure(mouse_id, nci_id, 1.0);
					addIsub2Structure(nci_id, mouse_id, 1.0);
					
					addIsubAverage2Structure(mouse_id, nci_id, 1.0);
					addIsubAverage2Structure(nci_id, mouse_id, 1.0);
					
					addScopeAll2Structure(mouse_id, nci_id, 1.0);
					addScopeAll2Structure(nci_id, mouse_id, 1.0); //we add both sides
					
				}
				
			}
			
		}
		
		
		
		
		map.clear();
		ManageBackgroundKnowledge.clearStructures();
		
	}*/
	
	
	
	/**
	 * We will increase the number of anchors and create the set of mappings 2 ask
	 */
	public void createCandidates() {
		
		try {
			//Inverted file intersection
			LogOutput.print("assessAnchors2Review()");
			assessAnchors2Review(); //Mappings from IF exact to assess
			
			//Stemmming extraction
			LogOutput.print("createCandidates4StemmingLikeAnchors()");
			createCandidates4StemmingLikeAnchors();
			//clear
			onto_process1.clearInvertedFileStemming();
			onto_process2.clearInvertedFileStemming();
			if_stemming_intersection.clear();
			
			
			
			//Deprecated EXPANSION from LogMap 1
			//createAnchorsExpansion(); //Not good results (does not improves what extracted by weak mappings)
			
			//Extracts/assess weak mappings
			LogOutput.print("createCandidates4WeakAnchors()");
			createCandidates4WeakAnchors();
			
			//We remove the set of week candidate mappings: already in correspondent sets
			//clearWeakCandidateAnchors();
			weakCandidateMappings1N.clear();
			
			
			//Clear intersected IF
			//clearIntersectedInvertedFilesStemmingAndWeak();
			
			//Characterise/reduce mappings to ask. Some of them will automatically be included within anchors and others will be discarded
			LogOutput.print("createMappings2AskUser()");
			createMappings2AskUser();
			
			
			
			
			
		}
		catch (Exception e){
			System.err.println("Error extracting anchors: " + e.getMessage());
			e.printStackTrace();
		}
		
	}

	
	
	public void processInputMappings(Set<MappingObjectStr> mappings) {
		
		for (MappingObjectStr mapping : mappings){
			
			if (mapping.isClassMapping()){
				
				int ide1 = onto_process1.getIdentifier4ConceptIRI(mapping.getIRIStrEnt1());
				int ide2 = onto_process2.getIdentifier4ConceptIRI(mapping.getIRIStrEnt2());
				
				if (ide1>=0 && ide2>=0){
					input_mappings.add(
							new MappingObjectIdentifiers(
									onto_process1.getIdentifier4ConceptIRI(mapping.getIRIStrEnt1()),
									onto_process2.getIdentifier4ConceptIRI(mapping.getIRIStrEnt2()))
							);
				}
				else
					System.err.println("Mapping with no direct correspondence to ids: " + mapping);
			}
		}	
	}

	
	
	
	/**
	 * Create candidates from input mappings (e.g. )
	 */
	private void createCandidatesFromInputMappings(boolean are_input_mapping_validated) throws Exception {
	
		//int candidates=0;
		
		for (MappingObjectIdentifiers mapping : input_mappings){

			if (onto_process1.getDangerousClasses().contains(mapping.getIdentifierOnto1()) ||
				onto_process2.getDangerousClasses().contains(mapping.getIdentifierOnto2()))
				continue;
		
			if (are_input_mapping_validated)
				evaluateCandidateMapping(mapping.getIdentifierOnto1(), mapping.getIdentifierOnto2(), IF_VALIDATED);
			else
				evaluateCandidateMapping(mapping.getIdentifierOnto1(), mapping.getIdentifierOnto2(), IF_EXACT);
			
		}
		
		
		
		
		
	}
	
	

	
	
	
	
	/**
	 * Processes the intersected 'exact' inverted files and extract anchors.
	 * When ambiguity filter by isub and scope
	 */
	private void createCandidatesFromExactIF() throws Exception {
	
		int candidates=0;
		
		
		/*We need to remove ambiguity*/		
		for (Set<String> set_str : if_exact_intersection){
			
			if (set_str.isEmpty()){
				LogOutput.print("EMPTY SET IN EXACT IF: " + onto_process1.getInvertedFileExactMatching().get(set_str).size() + " - " +  + onto_process2.getInvertedFileExactMatching().get(set_str).size());
				continue;
			}
			
			
			for (int ide1 : onto_process1.getInvertedFileExactMatching().get(set_str)){
				
				if (onto_process1.getDangerousClasses().contains(ide1))
					continue;
			
				for (int ide2 : onto_process2.getInvertedFileExactMatching().get(set_str)){
					
					if (onto_process2.getDangerousClasses().contains(ide2))
						continue;
					
					//Decides if mappings should be considered
					//Checks ambiguity and scope
					evaluateCandidateMapping(ide1, ide2, IF_EXACT);
					candidates++;
					
				}
			}
		}
		
		
		//printStatisticsMappingEvaluation();
		//LogOutput.print("Candidates EXACT: " + candidates);
		
		
		
		
	}
	
	
	/**
	 * Evaluates mapping confidence and decides if mappings should be included or must be treated later (e.g.
	 * ambiguity cases and mappings without scope)
	 * @param ide1
	 * @param ide2
	 * @param origin
	 */
	private void evaluateCandidateMapping(int ide1, int ide2, int origin){
		
		
		if (origin==IF_VALIDATED){
			evaluateCandidateMappingIFValidated(ide1, ide2);
		}		
		else if (origin==IF_EXACT){
			evaluateCandidateMappingIFExact(ide1, ide2);
		}
		else if (origin==IF_STEMMING){
			evaluateCandidateMappingIFStemming(ide1, ide2);
		}
		else if (origin==IF_WEAK){
			evaluateCandidateMappingIFWeak(ide1, ide2);
		}
		
	}
	
	
	/**
	 * Evaluation of mappings coming from a validated input
	 * @param ide1
	 * @param ide2
	 */
	private void evaluateCandidateMappingIFValidated(int ide1, int ide2){
		
		
		//Already considered
		if (isMappingAlreadyConsidered(ide1, ide2))
			return;
		
		//Statistics All
		StatisticsManager.addStatisticsMappingsAll(ide1, ide2);
		
		//We extract and store
		extractISUB4Mapping(ide1, ide2);
		extractISUBAverage4Mapping(ide1, ide2);
						
		extractScopeAll4Mapping(ide1, ide2);
		
		getConfidence4Mapping(ide1, ide2); //isub+scope

		
		//We do not need to filter by isub in exact cases
		//With scope, no ambiguous or best confidence
		addSubMapping2ListOfAnchors(ide1, ide2);
		addSubMapping2ListOfAnchors(ide2, ide1);
		
		
	}
	
	
	/**
	 * Evaluation of mappings coming from exact IF
	 * @param ide1
	 * @param ide2
	 */
	private void evaluateCandidateMappingIFExact(int ide1, int ide2){
		
		
		double scoreScope;
		double scoreISUB;
		double scoreISUB_avg;
		
		double conf;
		
		
		//Already considered
		if (isMappingAlreadyConsidered(ide1, ide2))
			return;
		
		//Statistics Mall
		StatisticsManager.addStatisticsMappingsAll(ide1, ide2);
		
		
		
		//We extract and store
		scoreISUB = extractISUB4Mapping(ide1, ide2);
		scoreISUB_avg = extractISUBAverage4Mapping(ide1, ide2);
						
		scoreScope = extractScopeAll4Mapping(ide1, ide2);
		
		conf = getConfidence4Mapping(ide1, ide2); //isub+scope
		
		
		if (scoreISUB<Parameters.good_isub_anchors || scoreISUB_avg<0.35 || scoreScope<=Parameters.bad_score_scope){
			addSubMapping2Mappings2Review(ide1, ide2);//only one side, at this stage we consider in this set equivalent mappings
			return;
		}
		
		
		int ide1a;
		int ide2a;
		
		double conf1;
		double conf2;
		
		
		boolean ambiguous=false;
		
		
		//SCOPE and ISUB>=0.95
		//Ambiguity: cases with scope that are already in exact mappings
		//At this stage we will only allow mappings 1:1
		if (isEntityAlreadyMapped(ide1) && isEntityAlreadyMapped(ide2)){
			//compare cases (3 cases)
			///ide1-ide2a
			//ide1a-ide2
			//ide1=ide2
			
			ide2a = getTargetEntity4Index(ide1);
			ide1a = getTargetEntity4Index(ide2);
			conf1 = getConfidence4Mapping(ide1, ide2a);
			conf2 = getConfidence4Mapping(ide1a, ide2);

			//substitute only if better than both two
			if (conf > conf1 && conf > conf2){

				moveMapping2ReviewList(ide1, ide2a);
				moveMapping2ReviewList(ide1a, ide2);
				
				//we add mapping to list in the end			
			}			
			else{ //Ambiguous
				ambiguous=true;
			}
			
		}
		else if (isEntityAlreadyMapped(ide1)){
		
			//If better confidence then substitute (add loser to ambiguity)
			ide2a = getTargetEntity4Index(ide1);
			conf1 = getConfidence4Mapping(ide1, ide2a);
			
			if (conf>conf1){				
				moveMapping2ReviewList(ide1, ide2a);									
			}
			else { //Ambiguous
				ambiguous=true;	
			}
			
			
		}
		else if (isEntityAlreadyMapped(ide2)){
			
			//If better confidence then substitute (add loser to ambiguity)
			ide1a = getTargetEntity4Index(ide2);
			conf1 = getConfidence4Mapping(ide1a, ide2);
			
			if (conf>conf1){				
				moveMapping2ReviewList(ide1a, ide2);				
			}
			else { //Ambiguous
				ambiguous=true;				
			}
		}
		
		
		if (ambiguous){
			addSubMapping2Mappings2Review(ide1, ide2);
			return;
		}
		
		
		
		//We do not need to filter by isub in exact cases
		//With scope, no ambiguous or best confidence
		addSubMapping2ListOfAnchors(ide1, ide2);
		addSubMapping2ListOfAnchors(ide2, ide1);
		
	}
	
	
	

	
	
	
	

	
	
	
	/**
	 * Mappings coming from exact IF. We evaluate wrt fixed mappings
	 */
	private void assessAnchors2Review(){
		
		
		//Mappings to review and 
		/*for (int ide1 : weakenedDandG_Mappings1N.keySet()){
			
			for (int ide2 : weakenedDandG_Mappings1N.get(ide1)){
				
				if (!isMappingInferred(ide1, ide2)){
					
					//Add in exact
					addSubMapping2ListOfAnchors(ide1, ide2);
					
				}
			}
		}
		
		//We remove those mappings that are already inferred
		weakenedDandG_Mappings1N.clear();*/
		
		
		//Deleted with D&G
		//We try to recover some of them (we give the another chance)
		//Preliminary tests: it seem there is not impact on that
		//Note that we may check them again as a weak mapping
		/*for (int ide1 : getDircardedAnchors().keySet()){
			for (int ide2 : getDircardedAnchors().get(ide1)){
				
				if (isId1SmallerThanId2(ide1, ide2)){
					addSubMapping2Mappings2Review(ide1, ide2);
				}
				else{
					addSubMapping2Mappings2Review(ide2, ide1);
				}		
			}
		}*/
		
		
		
		for (int ide1 : mappings2Review.keySet()){
			for (int ide2 : mappings2Review.get(ide1)){
				
				if (!isId1SmallerThanId2(ide1, ide2))
					continue; //JUST IN CASE
				
				evaluateAnchor2Review(ide1, ide2);
				
			}
		}
		
		mappings2Review.clear();
		
		
		
	}
	
	
	
	private void evaluateAnchor2Review(int ide1, int ide2){
		
		
		//if (isMappingInAnchors(ide1, ide2))
		//	return;
		
		if (isMappingInConflictWithFixedMappings(ide1, ide2)){
			addSubMapping2ConflictiveAnchors(ide1, ide2);
			addSubMapping2ConflictiveAnchors(ide2, ide1);
			return;
		}
		
		if (isMappingDangerousEquiv(ide1, ide2)){
			addEquivMapping2HardDiscardedAnchors(ide1, ide2);
			isHardMappingInGS(ide1, ide2, 1);// tmp method
			return;
		}
		
		//Already extracted
		//double scoreISUB = extractISUB4Mapping(ide1, ide2);
		//double scoreISUB_avg = extractISUBAverage4Mapping(ide1, ide2);
		
		//Scope with only anchors: for future check
		extractScopeAnchors4Mapping(ide1, ide2);
		
		double scoreScope = extractScopeAll4Mapping(ide1, ide2);
		
		
		//Discard if no scope and ambiguity
		if (scoreScope<=Parameters.bad_score_scope && (isEntityAlreadyMapped(ide1) || isEntityAlreadyMapped(ide2))){// && scoreISUB_avg<0.9
			
			addEquivMapping2HardDiscardedAnchors(ide1, ide2); //No scope and ambiguous
			isHardMappingInGS(ide1, ide2, 2);//TODO tmp method
			return;
		}
		
		//Minimum ISUB... as before....
		//Only ambiguity -> ask
		//No scope ->ask
		
		
		//Always ask otherwise
		addMapping2ListOfAnchors2AskLogMap(ide1, ide2);
		
		
		
	}
	
	
	
	
	

	
	
	
	/**
	 * Created candidates for anchors using a stemmed lexicon.
	 */
	private void createCandidates4StemmingLikeAnchors() throws Exception{
		
		int candidates=0;
		int ambiguity, size1, size2;
		
		//System.out.println(if_stemming_intersection.size());
		//System.out.println(onto_process1.getInvertedFileWeakLabelsStemming().values().size());
		//System.out.println(onto_process2.getInvertedFileWeakLabelsStemming().values().size());
		
		
		for (Set<String> set_str : if_stemming_intersection){
			
			
			if (set_str.isEmpty()){
				LogOutput.print("EMPTY SET IN STEMMING IF: " + onto_process1.getInvertedFileWeakLabelsStemming().get(set_str).size() + " - " +  + onto_process2.getInvertedFileWeakLabelsStemming().get(set_str).size());
				continue;
			}
			
			size1=onto_process1.getInvertedFileWeakLabelsStemming().get(set_str).size();
			size2=onto_process2.getInvertedFileWeakLabelsStemming().get(set_str).size();
			
			
			
			//Only Low amabiguity
			ambiguity=size1*size2;
			if (ambiguity>=7 || size1>=4 || size2>=4){//Max cases 3*2  (for FMA-NCI-SNOMED)
			//if (ambiguity>=13 && size1>=5 && size2>=5){//Max cases 3*2 (Mosue?)
				
				if (ambiguity>20)
					LogOutput.print("High ambiguity stemming: "+ ambiguity);
				
				continue;
			}
			
			
			//System.out.println(onto_process1.getInvertedFileWeakLabelsStemming().get(set_str).size());
			//System.out.println(onto_process2.getInvertedFileWeakLabelsStemming().get(set_str).size());
			//System.out.println();
			
			for (int ide1 : onto_process1.getInvertedFileWeakLabelsStemming().get(set_str)){							
				
				if (onto_process1.getDangerousClasses().contains(ide1))
					continue;
				
				for (int ide2 : onto_process2.getInvertedFileWeakLabelsStemming().get(set_str)){
					
					if (onto_process2.getDangerousClasses().contains(ide2))
						continue;
					
					//Decides if mappings should be considered
					//Checks ambiguity and scope
					evaluateCandidateMapping(ide1, ide2, IF_STEMMING);
					candidates++;
				}
			}
			
		}
		
		
		//printStatisticsMappingEvaluation();
		LogOutput.print("Candidates STEMING: " + candidates);
		
	
		
	}
	
	
	
	
	/**
	 * Evaluation of mappings coming from stemming IF
	 * 
	 * @param ide1
	 * @param ide2
	 */
	private void evaluateCandidateMappingIFStemming(int ide1, int ide2){
		
		double scoreScope;
		double scoreISUB;
		double confidence;
		
		//Already in list or discarded
		if (isMappingAlreadyConsidered(ide1, ide2))
			return;
		
		//Statistics Mall
		StatisticsManager.addStatisticsMappingsAll(ide1, ide2);
		
		
		if (isMappingInConflictWithFixedMappings(ide1, ide2)){
			addSubMapping2ConflictiveAnchors(ide1, ide2);
			addSubMapping2ConflictiveAnchors(ide2, ide1);
			return;
		}
		
		
		
		
		if (isMappingDangerousEquiv(ide1, ide2)){
			addEquivMapping2HardDiscardedAnchors(ide1, ide2);
			isHardMappingInGS(ide1, ide2, 1);// tmp method
			return;
		}
		
		//Ambiguity->discard! with current anchors
		if (isEntityAlreadyMapped(ide1) || isEntityAlreadyMapped(ide2)){
			addEquivMapping2HardDiscardedAnchors(ide1, ide2);
			isHardMappingInGS(ide1, ide2, 2);// tmp method
			return;
		}
		
		//Scope with only anchors: for future check
		extractScopeAnchors4Mapping(ide1, ide2);
		
		scoreScope = extractScopeAll4Mapping(ide1, ide2);
		//We extract and store
		scoreISUB = extractISUB4Mapping(ide1, ide2);
		//if (scoreISUB<0.75)
		//			return; //do not consider...
		
		confidence = getConfidence4Mapping(ide1, ide2);
		
		if (scoreScope<=Parameters.bad_score_scope || scoreISUB<Parameters.good_isub_candidates) {
			
			//For mouse anatomy
			if (scoreISUB>0.70 && confidence>0.45){
				addEquivMapping2DiscardedAnchors(ide1, ide2);
			}
			else{
				addEquivMapping2HardDiscardedAnchors(ide1, ide2);
				isHardMappingInGS(ide1, ide2, 3);// tmp method
				//if (.isMappingInGoldStandard(ide1, ide2))
				//	System.out.println();
			}
			return;
		}
		
		
	
		addMapping2ListOfAnchors2AskLogMap(ide1, ide2);
		
		
		
		
	}
	
	
	/**
	 * Evaluation of mappings coming from expansion
	 * 
	 * @param ide1
	 * @param ide2
	 * @deprecated Not used
	 */
	private boolean evaluateCandidateMappingExpansion(int ide1, int ide2){
		
		double scoreISUB;

		
		//Already in list or discarded
		if (isMappingAlreadyConsidered(ide1, ide2))
			return false;
		
		
		scoreISUB = extractISUB4Mapping(ide1, ide2);
		if (scoreISUB<0.95)//do not consider
			return false;
		
		
		//Cases Isub > 0.95
		
		if (isMappingInConflictWithFixedMappings(ide1, ide2)){
			addSubMapping2ConflictiveAnchors(ide1, ide2);
			addSubMapping2ConflictiveAnchors(ide2, ide1);
			return false;
		}
		
		if (isMappingDangerousEquiv(ide1, ide2)){
			addEquivMapping2HardDiscardedAnchors(ide1, ide2);
			return false;
		}
		
		//Ambiguity->discard!
		if (isEntityAlreadyMapped(ide1) || isEntityAlreadyMapped(ide2) || scoreISUB<0.98){
			//addEquivMapping2HardDiscardedAnchors(ide1, ide2); //to avoid many discards
			return false;
		}
		
		//>0.98
		addMapping2ListOfAnchors2AskLogMap(ide1, ide2);
		return true;
		
		
		
		
	}
	
	

	
	/**
	 * Creation of candidate mappings from weak anchors. called from main LogMap method
	 * 
	 */
	private void createCandidates4WeakAnchors(){
	
		
		//Now the set weakCandidateMappings1N is pre-extracted
		//extractSubsetFromWeakMapping();
		//extractSubsetFromWeakMappingFrequency();
		
		int candidates=0;
		
		//for (int ide1 : weakMappings1N.keySet()){
		for (int ide1 : weakCandidateMappings1N.keySet()){
			
			if (onto_process1.getDangerousClasses().contains(ide1))
				continue;
			
			//for (int ide2 : weakMappings1N.get(ide1)){
			for (int ide2 : weakCandidateMappings1N.get(ide1)){
				
				if (onto_process2.getDangerousClasses().contains(ide2))
					continue;
				
				if (!isId1SmallerThanId2(ide1, ide2))
					continue;
				
				
				
				evaluateCandidateMappingIFWeak(ide1, ide2);
				
				
				
				candidates++;
				
			}
			
		}
		
		//printStatisticsMappingEvaluation();
	//	LogOutput.print("Candidates WEAK: " + candidates);
		
		
		
	}
	
	
	
	private void evaluateCandidateMappingIFWeak(int ide1, int ide2){

		
		
		
		double scoreScopeAnc;
		double scoreScope;
		double scoreISUB;
		double confidence;
		
		//Only if the mapping is already in list of anchors or in list to ask logmap
		if (isMappingAlreadyInList(ide1, ide2) || isMappingInAnchors2AskLogMap(ide1, ide2) || isMappingInConflictiveSet(ide1, ide2)){
			return;
		}
		
		//Already in list or discarded (but not weak yujiao, we give another chance)
		//Probably not necessary anymore
		if (isMappingAlreadyConsidered(ide1, ide2) && !hasWeakMappingSim(ide1, ide2))
			return;
		
		if (!isMappingAlreadyConsidered(ide1, ide2)){
			
			//Statistics Mall
			StatisticsManager.addStatisticsMappingsAll(ide1, ide2);
			
		}
		
		
		
		
		if (isMappingInConflictWithFixedMappings(ide1, ide2)){
			addSubMapping2ConflictiveAnchors(ide1, ide2);
			addSubMapping2ConflictiveAnchors(ide2, ide1);
			return;
		}
		
		
		
		//Do not consider?
		if (isMappingDangerousEquiv(ide1, ide2)){
			addEquivMapping2HardDiscardedAnchors(ide1, ide2);
			isHardMappingInGS(ide1, ide2, 1);// tmp method
			return;
		}
		
		//Ambiguity->discard! with anchors then no consider
		if (isEntityAlreadyMapped(ide1) || isEntityAlreadyMapped(ide2)){ //no anchor
			addEquivMapping2HardDiscardedAnchors(ide1, ide2);
			isHardMappingInGS(ide1, ide2, 2);// tmp method
			return;
		}
		
		
		
		
		
		
		scoreScope = extractScopeAll4Mapping(ide1, ide2);
		//We extract and store
		scoreISUB = extractISUB4Mapping(ide1, ide2);
		
		confidence = getConfidence4Mapping(ide1, ide2);
		
		//Scope with only anchors: for future check
		scoreScopeAnc = extractScopeAnchors4Mapping(ide1, ide2);
		
		
		if (!hasWeakMappingSim(ide1, ide2)){
		
			if (scoreISUB<Parameters.good_isub_candidates && scoreScope<=Parameters.bad_score_scope){
				//Must be hard discarded always
				addEquivMapping2HardDiscardedAnchors(ide1, ide2);
				//isHardMappingInGS(ide1, ide2, 3);//TODO tmp method
				return;
			}
			
			
			if (scoreISUB<Parameters.good_isub_candidates) {
				//if (scoreISUB<0.70 && scoreScope<=0.01)
				//	return;
				
				//For mouse anatomy
				if (scoreISUB>0.83 && confidence>0.45){
					addEquivMapping2DiscardedAnchors(ide1, ide2);
				}
				else{
					//Must be hard discarded always
					addEquivMapping2HardDiscardedAnchors(ide1, ide2);
					//isHardMappingInGS(ide1, ide2, 3);// tmp method
				}
				
				return;
			}
			
			addMapping2ListOfAnchors2AskLogMap(ide1, ide2);
		}
		else{
			
						
			if (scoreISUB>=Parameters.good_isub_candidates && scoreScope>Parameters.bad_score_scope){

				//System.out.println("1");
				
				addMapping2ListOfAnchors2AskLogMap(ide1, ide2);
				return;
			}
			
			if (getSimWeak4Mapping2(ide1, ide2)>=Parameters.good_sim_coocurrence && scoreScope>Parameters.bad_score_scope){
				addMapping2ListOfAnchors2AskLogMap(ide1, ide2);
				//LogOutput.print("Mapping " + ide1 + "=" + ide2 + ", sim: " + getSimWeak4Mapping2(ide1, ide2) + ", scope: " + scoreScope);
				//LogOutput.print("\tASK");
				
				//System.out.println("2");
				
				
				return;
			}
			
			
			if (scoreISUB<0.65){
				//Always
				addEquivMapping2HardDiscardedAnchors(ide1, ide2);
				
				/*if (index.getIRIStr4ConceptIndex(ide1).equals("http://bioontology.org/projects/ontologies/fma/fmaOwlDlComponent_2_0#Stromal_cell")
						|| index.getIRIStr4ConceptIndex(ide2).equals("http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl#Stromal_Neoplasm")){
						System.out.println("3");
						System.out.println(index.getIRIStr4ConceptIndex(ide1) + " " + index.getIRIStr4ConceptIndex(ide2) + "  WEAK");
				}*/
				
				
				return;
			}
			
			
			//if (confidence>0.40 && getSimWeak4Mapping2(ide1, ide2)>=0.05 && scoreScope>0.0){
			//if (scoreISUB>0.83 && confidence>0.45 && scoreScope>0.0){ //Best results 112/167
			if (scoreISUB>0.89 || (scoreISUB > 0.80 && confidence>Parameters.good_confidence && scoreScope>Parameters.bad_score_scope) || (confidence>0.80)){
			//if (getSimWeak4Mapping(ide1, ide2)>=0.05){
				
				//System.out.println("4");
				
				addEquivMapping2DiscardedAnchors(ide1, ide2);
				//LogOutput.print("Mapping " + ide1 + "=" + ide2 + ", sim: " + getSimWeak4Mapping2(ide1, ide2) + ", scope: " + scoreScope);
				//LogOutput.print("\tDISCARDED");
				
				
				
			}
			else{
				
				//System.out.println("5");
				
				addEquivMapping2HardDiscardedAnchors(ide1, ide2);
				isHardMappingInGS(ide1, ide2, 3);// tmp method
				
				//if (isMappingInGoldStandard(ide1, ide2))
				//	LogOutput.print("YES   Mapping " + ide1 + "=" + ide2 + ", sim: " + getSimWeak4Mapping2(ide1, ide2) + ", scope: " + scoreScope
				//			+ ", scopeAnc: " + scoreScopeAnc + ", isub: " + scoreISUB);
				//else
				//	LogOutput.print("NO   Mapping " + ide1 + "=" + ide2 + ", sim: " + getSimWeak4Mapping2(ide1, ide2) + ", scope: " + scoreScope
				//			+ ", scopeAnc: " + scoreScopeAnc + ", isub: " + scoreISUB);
				
			}
			return;
				
			//String GS="BAD"; 
			//if (isMappingInGoldStandard(ide1, ide2)){
			//	GS="GOOD";
			//}
				
				
			/*LogOutput.print(GS + " - " +
						index.getLabel4ConceptIndex(ide1) + " - " + //.getName4ConceptIndex(ide1)  + " - " +
						index.getLabel4ConceptIndex(ide2) + " - " + //index.getName4ConceptIndex(ide2)  + " - " +
						extractISUB4Mapping(ide1, ide2) + " - " +
						extractISUBAverage4Mapping(ide1, ide2)  + " - " +
						extractScope4Mapping(ide1, ide2) + " - " +
						getSimWeak4Mapping(ide1, ide2));
			*/
				
			
		}		
			
		
		

	}
	
	
	
	private void extractSubsetFromWeakMapping(){
		int size1;
		int size2;
		int ambiguity;
		
		
		//Look in allweakmappings 
		
		for (Set<String> set_str : if_weak_intersection){
			
			
			if (set_str.isEmpty()){
				LogOutput.print("EMPTY SET IN WEAK IF: " + onto_process1.getInvertedFileWeakLabels().get(set_str).size() + " - " +  + onto_process2.getInvertedFileWeakLabels().get(set_str).size());
				continue;
			}
			
			
			size1=onto_process1.getInvertedFileWeakLabels().get(set_str).size();
			size2=onto_process2.getInvertedFileWeakLabels().get(set_str).size();
			
			//Only Low amabiguity
			ambiguity=size1*size2;
			//if (ambiguity>=7 || size1>=4 || size2>=4){//Max cases 3*2  (for FMA-NCI-SNOMED)
			if (ambiguity>=13 && size1>=5 && size2>=5){//Max cases 3*2 (Mosue?)
				continue;
			}
			
			
			
			for (int ide1 : onto_process1.getInvertedFileWeakLabels().get(set_str)){
				
				if (onto_process1.getDangerousClasses().contains(ide1))
					continue;
				
				
				for (int ide2 : onto_process2.getInvertedFileWeakLabels().get(set_str)){

					if (onto_process2.getDangerousClasses().contains(ide2))
						continue;
					
					
					addEquivMapping2ListOfWeakCandidateAnchors(ide1, ide2);
					
				}
			}
		}
		//count candidateweak anchors
		int weak_anchors=0;
		for (int ide1 : weakCandidateMappings1N.keySet()){
			weak_anchors+=weakCandidateMappings1N.get(ide1).size();
		}		
		//LogOutput.print("CANDIDATE WEAK ANCHORS: " + weak_anchors);
	}
	
	
	
	
	
	
	
	/*
	 * Good-bad discarded-weakened mappings
	public void printStatisticsAssessment(){
		
		int weakened_mappings=0;
		int weakened_good=0;
		for (int ide1 : weakenedMappings1N.keySet()){
			for (int ide2 : weakenedMappings1N.get(ide1)){
				
				//if (index.getClassIndex(ide1).getOntologyId()<index.getClassIndex(ide2).getOntologyId()){
				
					weakened_mappings++;
					
					if (isMappingInGoldStandard(ide1, ide2) || isMappingInGoldStandard(ide2, ide1)){
						weakened_good++;
				//	}
				}
			}
		}
		
		LogOutput.print("\nWeakened anchors: " + weakened_mappings);
		LogOutput.print("Good weakened anchors in GS: " + weakened_good);
		
		
		
		int conflictive_mappings=0;
		int conflictive_good=0;
		for (int ide1 : conflictiveMappings1N.keySet()){
			for (int ide2 : conflictiveMappings1N.get(ide1)){
				
				if (isMappingWeakened(ide2, ide1)) //It was not completely discarded
					continue;
				
				if (index.getClassIndex(ide1).getOntologyId()<index.getClassIndex(ide2).getOntologyId()){
				
					conflictive_mappings++;
					
					if (isMappingInGoldStandard(ide1, ide2) || isMappingInGoldStandard(ide2, ide1)){
						conflictive_good++;
					}
				}
			}
		}
		
		LogOutput.print("Conflictive anchors: " + conflictive_mappings);
		LogOutput.print("Good conflictive anchors in GS: " + conflictive_good);
		
		
		int discarded_mappings=0;
		int discarded_good=0;
		for (int ide1 : discardedMappings1N.keySet()){
			for (int ide2 : discardedMappings1N.get(ide1)){
				
				if (index.getClassIndex(ide1).getOntologyId()<index.getClassIndex(ide2).getOntologyId()){
				
					discarded_mappings++;
					
					if (isMappingInGoldStandard(ide1, ide2) || isMappingInGoldStandard(ide2, ide1)){
						discarded_good++;
					}
				}
			}
		}
		
		LogOutput.print("Discarded anchors: " + discarded_mappings);
		LogOutput.print("Good discarded anchors in GS: " + discarded_good);
		
		
		
		int ask_mappings=0;
		int ask_good=0;
		for (int ide1 : candidateMappings2ask1N.keySet()){
			for (int ide2 : candidateMappings2ask1N.get(ide1)){
				
				if (index.getClassIndex(ide1).getOntologyId()<index.getClassIndex(ide2).getOntologyId()){
				
					ask_mappings++;
					
					if (isMappingInGoldStandard(ide1, ide2) || isMappingInGoldStandard(ide2, ide1)){
						ask_good++;
					}
				}
			}
		}
		
		LogOutput.print("Mappings 2 ASK: " + ask_mappings);
		LogOutput.print("Good Mappings 2 ASK: " + ask_good);
		
		
		
		int candidate_mappings=0;
		int candidate_good=0;
		int candidate_mappings_Amb=0;
		int candidate_good_Amb=0;
		boolean ambiguity=false;
		
		for (int ide1 : newCandidatetMappings1N.keySet()){
			
			ambiguity=false;
			
			if (newCandidatetMappings1N.get(ide1).size()>1){
				ambiguity=true;
			}
			
			for (int ide2 : newCandidatetMappings1N.get(ide1)){
				
				if (index.getClassIndex(ide1).getOntologyId()<index.getClassIndex(ide2).getOntologyId()){
				
					if (ambiguity)
						candidate_mappings_Amb++;
					else
						candidate_mappings++;
					
					if (isMappingInGoldStandard(ide1, ide2) || isMappingInGoldStandard(ide2, ide1)){
						if (ambiguity)
							candidate_good_Amb++;
						else
							candidate_good++;
					}
				}
			}
		}
		
		LogOutput.print("New Candidate anchors: " + candidate_mappings);
		LogOutput.print("Good New candidate anchors in GS: " + candidate_good);
		LogOutput.print("New Candidate anchors with hight amb: " + candidate_mappings_Amb);
		LogOutput.print("Good New candidate anchors with high amb in GS: " + candidate_good_Amb);
		LogOutput.print("");
		
		
	
	}*/
	
	
	
	
	
	
	/**
	 * 
	 * frequency formula
	 */
	private void extractSubsetFromWeakMappingFrequency() {
		
		int size1, size2;
		String label1, label2;
		Set<Integer> set1, set2;
		Set<String> words1 = new HashSet<String>(), words2 = new HashSet<String>();		
//		ArrayList<WeakCandidate> newCandidates = new ArrayList<WeakCandidate>();
		WeakCandidate [] picked = new WeakCandidate[3];
		WeakCandidate cand; 
		ClassIndex class1 = null, class2 = null;
//		boolean mark = false;
		double score;
		int f_key;
//		Set<Integer> list_key;
		double iSub, sim;
				
		for (Set<String> set_str : if_weak_intersection)
		{
//			set_str = new HashSet<String>();
//			set_str.add("scalp");
//			mark = set_str.toString().contains("tricep");
			
			set1 = onto_process1.getInvertedFileWeakLabels().get(set_str);
			size1=set1.size();
			set2 = onto_process2.getInvertedFileWeakLabels().get(set_str);
			size2=set2.size();
			
			//For scalability??
			if (size1 > 5 & size2 > 5 && size1 * size2 > 100) //too ambiguous
			//if (size1 > 6 & size2 > 6 && size1 * size2 > 100) //too ambiguous
				continue;
			
			try {
				f_key = index.getCooccurrenceOfWords(set_str).size();
			}
			catch (NullPointerException e)
			{
				LogOutput.print("NullPointerException   " + set_str.toString());
				continue;
			}
			
			for (int ide1 : set1)
			{
//				label1 = (class1 = index.getClassIndex(ide1)).getEntityName();
				label1 = (class1 = index.getClassIndex(ide1)).findStemmedAltLabel(set_str);
				if (label1 == null)
					continue;
				//TODO Yujiao - why is there a class named "F" that doesn't have alternativeLabels & other attributes?

//				if (class1.getEntityName().equals("Pseudolobe"))
//					mark = true;
				
				for (String word : label1.split("_"))
					words1.add(word);
				
				for (int ide2 : set2)
				{
//					label2 = (class2 = index.getClassIndex(ide2)).getEntityName();
					label2 = (class2 = index.getClassIndex(ide2)).findStemmedAltLabel(set_str);
					if (label2 == null)
						continue;

					if (!words2.isEmpty())
						words2.clear(); 
					
					for (String word : label2.split("_"))
						words2.add(word);
					
//					if (class1.getEntityName().equals("Scalp") && class2.getEntityName().equals("Scalp_structure"))
//						Lib.debuginfo("here");
//					if (mark)
//						System.out.print(label1 + " | " + label2 + "\n");
											
					//ensure that common(label1, label2) == set_str
					if (!hasCommonWords(words2, words1, set_str))
						continue;
					
					cand = new WeakCandidate(set_str, ide1, ide2, label1, label2, index, f_key);
//					newCandidates.add(cand);
					insertCandidate(picked, cand);

				}
				
				for (int i = 0, id1, id2; i < picked.length; ++i)
				{
					if (picked[i] == null) break;
					addEquivMapping2ListOfWeakCandidateAnchors((id1 = picked[i].getKey()), (id2 = picked[i].getValue()));

					//iSub = extractISUB4Mapping(id1, id2);
					sim = picked[i].getScore(); 
					//if (sim > 0.5)
					//	LogOutput.print(label1 + "  " + index.getClassIndex(id2).findStemmedAltLabel(set_str) + "  " + sim + "\n");
					
					addSimWeak2Structure(id1, id2, sim);
					addSimWeak2Structure(id2, id1, sim);
					//score = iSub > sim ? iSub : 0.5 * (iSub + Math.sqrt(Math.max(0, sim)));
					//addIsub2Structure(id1, id2, score);
					//addIsub2Structure(id2, id1, score);
					picked[i] = null;
				}

				words1.clear();
			}
			
/*
			if (newCandidates.size() == 0)
				continue;

			Collections.sort(newCandidates, newCandidates.get(0));
			
			for (int i = 0; i < newCandidates.size(); ++i)
			{	
				if (i > 2)// && Lib.dcmp(1. - score) > 0)
					break;
				score = newCandidates.get(i).getScore();
				if (Lib.dcmp(score - WeakCandidate.MINSCORE) <= 0)
					break;
				addEquivMapping2ListOfWeakCandidateAnchors(newCandidates.get(i).getKey(), newCandidates.get(i).getValue());
			}
*/
		
//			newCandidates.clear();
		}
		
	}
	
	
	private boolean hasCommonWords(Set<String> a, Set<String> b, Set<String> c)
	{
		for (String word : a)
			if (b.contains(word) && !c.contains(word))
				return false;
		return true;
	}
	
	private void insertCandidate(WeakCandidate[] picked, WeakCandidate cand) {
		if (Lib.dcmp(cand.getScore() - WeakCandidate.MINSCORE) <= 0)
			return ;
		
		for (int i = 0; i < picked.length; ++i)
			if (picked[i] == null || cand.compareTo(picked[i]) > 0)
			{
				for (int j = picked.length - 1; j > i; --j)
					picked[j] = picked[j - 1];
				picked[i] = cand;
				break;
			}
	}
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * This method filters mappings 2 ask wrt ambiguity.
	 * Too ambiguous mappings are likely to be wrong. 
	 * Note that for weak and stemming-like candidates we forced mappings to be non ambiguous wrt anchors.
	 */
	private void createMappings2AskUser(){
		
		int side1_amb_logmap=0;
		int side2_amb_logmap=0;
		int both_amb_logmap=0;
		
		mappings2Review.clear();
		
		
		
		//We add everything ins discarded if it is a small set
		//Useful in cases like  mouse anatomy and fma2nci small 
		//where discarded mappinsg are small wrt the number of anchors
		if (getNumDiscardedMappings()<(getLogMapMappings().size()/Parameters.ratio_second_chance_discarded)){
		//if (getNumDiscardedMappings()<(getAnchors().size()/2.5)){
			
			LogOutput.print("Second chance to discarded mappings.");
			
			//No further filtering with heuristics
			//If no interactivity, mappings are filtered using automatic heuristics, 
			//if this flag is set to true. Otherwise, former mappings 2 ask user are 
			//added automatically
			//If second chance and flag is true, the the mappings are filtered anyway (and there is 
			//no effect for 2nd chance mappings)
			setFilterWithHeuristicsSecondLevelMappings(false);
			
			for (int ide1 : getDiscardedMappings().keySet()){
				for (int ide2 : getDiscardedMappings().get(ide1)){
					//addEquivMapping2ListOfAnchors2AskUser(ide1, ide2);
					addMapping2ListOfAnchors2AskLogMap(ide1, ide2);
					
				}
			}
			
			getDiscardedMappings().clear();
		}
		
		
		//We remove high ambiguity mappings
		//-------------------------------
		for (int ide1 : getToAskLogMapMappings().keySet()){
			
			// Side 1
			/*side1_amb=0;
			
			if (getAnchors().containsKey(ide1)){
				side1_amb += getAnchors().get(ide1).size();
			}
			
			if (getToAskLogMapMappings().containsKey(ide1)){
				side1_amb += getToAskLogMapMappings().get(ide1).size();
			}*/
			side1_amb_logmap = getEntityAmbiguity_LogMapMappings(ide1);
			
			
			for (int ide2 : getToAskLogMapMappings().get(ide1)){
				
				if (isId1SmallerThanId2(ide1, ide2)){
				
					// Side 2
					/*side2_amb=0;
					if (getAnchors().containsKey(ide2)){
						side2_amb += getAnchors().get(ide2).size();
					}
					if (getToAskLogMapMappings().containsKey(ide2)){
						side2_amb += getToAskLogMapMappings().get(ide2).size();
					}*/
					side2_amb_logmap = getEntityAmbiguity_LogMapMappings(ide2);
					
					
					both_amb_logmap = side1_amb_logmap + side2_amb_logmap;
					
					if (both_amb_logmap>Parameters.max_ambiguity){ //High ambiguity
						isHardMappingInGS(ide1, ide2, 0);// tmp method
						//addEquivMapping2HardDiscardedAnchors(ide1, ide2);
						addEquivMapping2DiscardedAnchors(ide1, ide2);
					}
					//min ambiguity (entities are only involved within one mapping)
					
					else if (both_amb_logmap<=Parameters.good_ambiguity && 
							//extractScopeAll4Mapping(ide1, ide2)>0.0 
							//&&
							//extractScopeAll4Mapping(ide1, ide2)>0.0 && getConfidence4Mapping(ide1, ide2)>0.50 Too restrictive for non ambiguous
							//(extractISUB4Mapping(ide1, ide2)>0.99 ||
							(extractISUB4Mapping(ide1, ide2)>Parameters.good_isub_anchors ||
							(hasWeakMappingSim(ide1, ide2) && getSimWeak4Mapping2(ide1, ide2)>=Parameters.good_sim_coocurrence))
							){ 
						
						
						
						//Otherwise we were asking almost nothing in interactive conference track
						if (OracleManager.isActive()){
							addEquivMapping2ListOfAnchors2AskUser(ide1, ide2);
							
							LogOutput.printAlways("New Added 2 ask: " +
									index.getIRIStr4ConceptIndex(ide1) + "  " +
									index.getIRIStr4ConceptIndex(ide2));
						
						}
						
						else{
							addSubMapping2Mappings2Review(ide1, ide2);
							addSubMapping2Mappings2Review(ide2, ide1);
						}
						
						
						//Manchors++;
						//if (isMappingInGoldStandard(ide1, ide2)){
						//	Manchors_ok++;						
						//}
						
					
					}
					else{
						//Mappings that are likely to be incorrect
						//We give a chance to be revsiewed by the user
						addEquivMapping2ListOfAnchors2AskUser(ide1, ide2);
					}
				}
				
				
			}
			
		}
		
		
		
		int side1_amb_user=0;
		int side2_amb_user=0;
		int both_amb_user=0;
		
		
		//TODO: move this interactivity user=0
		//APPLY HEURISTICS
		/*
		for (int ide1 : getToAskLogMapMappings().keySet()){
		
			for (int ide2 : getToAskLogMapMappings().get(ide1)){
				
				side1_amb_logmap=getEntityAmbiguity_LogMapMappings(ide1);
			
				side2_amb_logmap=getEntityAmbiguity_LogMapMappings(ide2);
				
				both_amb_logmap = side1_amb_logmap + side2_amb_logmap;
				
				if (both_amb_logmap<=3 && extractISUB4Mapping(ide1, ide2)>=0.98){
				
					addSubMapping2Mappings2Review(ide1, ide2);
					addSubMapping2Mappings2Review(ide2, ide1);
					
					removeEquivMappingFromListOfAnchors2AskUser(ide1, ide2);
					
				}
				
			}
		}*/
		
		
		//Heuristics tests: move to interactivity user=0
		/*for (int ide1 : getToAskUserMappings().keySet()){
			
			for (int ide2 : getToAskUserMappings().get(ide1)){
				
				if (isId1SmallerThanId2(ide1, ide2) && extractScopeAll4Mapping(ide1, ide2)>0.0 && getConfidence4Mapping(ide1, ide2)>0.50){
					
					addSubMapping2Mappings2Review(ide1, ide2);
					addSubMapping2Mappings2Review(ide2, ide1);
					
				}
				
			}
		}*/
		
		
		//Not giving good results... (only a few mappings are not ambiguous any more)
		//It seems ambiguous mapping groupos are amb between them. Make sense somehow...
		//TODO 2ask user are equiv mappings only one side is added!!!!!! 
		//TODO: Repeage mappings that are not ambiguous any more when high ambiguos mappings are discarded
		//We explore "ToAskLogMapMappings" in order to be able to remove mappings from "ToAskUserMappings"
		///----------------------------
		/*for (int ide1 : getToAskLogMapMappings().keySet()){
			
			for (int ide2 : getToAskLogMapMappings().get(ide1)){
				
				//Check only those in mappings to ask
				if (isMappingInAnchors2AskUser(ide1, ide2)){
					
					// Side 1
					side1_amb_logmap=getEntityAmbiguity_LogMapMappings(ide1);
					side1_amb_user=getEntityAmbiguity_UserMappings(ide1);
					
					// Side 2
					side2_amb_logmap=getEntityAmbiguity_LogMapMappings(ide2);
					side2_amb_user=getEntityAmbiguity_UserMappings(ide2);
					
					both_amb_logmap = side1_amb_logmap + side2_amb_logmap;
					both_amb_user = side1_amb_user + side2_amb_user;
					
					////Not GOOD Heuristic (apply only for amb 3)!!
					
					//We add to new candidates and remove from 2ask list (only those with amb 3)
					if (both_amb_user<=2 && both_amb_logmap<=3 && 
							(extractISUB4Mapping(ide1, ide2)>=0.99 || 
							(hasWeakMappingSim(ide1, ide2) && getSimWeak4Mapping2(ide1, ide2)>=0.09))){ 
						
						addSubMapping2Mappings2Review(ide1, ide2);
						addSubMapping2Mappings2Review(ide2, ide1);
						
						removeEquivMappingFromListOfAnchors2AskUser(ide1, ide2);
					
					}
					
					
				}
				
			}
			
		}*/
		
		
		//TODO only for mosue anatomy???
		//If discarded is not too big (wrt anchors) then add to questions for user
		//Case for small ontologies (mouse anatomy)
		//--------------------------------
		/*if (getNumDiscardedMappings()<(getAnchors().size()/5)){
			
			setFilterWithHeuristicsInteractivity(false);
			
			for (int ide1 : getDircardedAnchors().keySet()){
				for (int ide2 : getDircardedAnchors().get(ide1)){
					addEquivMapping2ListOfAnchors2AskUser(ide1, ide2);
					
					
				}
			}
			
			//getDircardedAnchors().clear();
			
		}*/
		
		
		//TODO we remove this set, mappings are already in other sets
		//We need this set for ambiguity statistics purposes
		//Probably with to ask user is enough
		//getToAskLogMapMappings().clear();
		
		
		
	}
	
	
	
	
	
	/**
	 * We will ask to the user those mappings not in conflict with current fixed mappings
	 */
	public void assessMappings2AskUser(){
		//Mappings to review and
		
		//int side1_amb=0;
		//int side2_amb=0;
		
		LogOutput.print("Assessing mappings to ask user.");
		
		for (int ide1 : candidateMappings2askUser1N.keySet()){
					
			for (int ide2 : candidateMappings2askUser1N.get(ide1)){
				
				if (isId1SmallerThanId2(ide1, ide2)){//
					
					if (isMappingInConflictWithFixedMappings(ide1, ide2)){
						
						addSubMapping2ConflictiveAnchors(ide1, ide2);
						addSubMapping2ConflictiveAnchors(ide2, ide1);
						
					}
					else if (isMappingDangerousEquiv(ide1, ide2)){
						
						addEquivMapping2DiscardedAnchors(ide1, ide2);
						
					}
					else { //ask user (store ambiguity)
					
						// Side 1
						/*side1_amb=0;
						
						if (getAnchors().containsKey(ide1)){
							side1_amb += getAnchors().get(ide1).size();
						}
						
						if (getToAskLogMapMappings().containsKey(ide1)){
							side1_amb += getToAskLogMapMappings().get(ide1).size();
						}
						
						// Side 2
						side2_amb=0;
						if (getAnchors().containsKey(ide2)){
							side2_amb += getAnchors().get(ide2).size();
						}
						if (getToAskLogMapMappings().containsKey(ide2)){
							side2_amb += getToAskLogMapMappings().get(ide2).size();
						}*/
						
						//Include this in interactivity ordering
						//addMappingObject2AskUserList(ide1, ide2, (side1_amb+side2_amb));
						addMappingObject2AskUserList(ide1, ide2);
					}
				}
			}
		}
		
		
		
		
		
		//we need them for interactivity
		//candidateMappings2askUser1N.clear();
		
		for (int ide1 : weakenedDandG_Mappings1N.keySet()){
			
			for (int ide2 : weakenedDandG_Mappings1N.get(ide1)){
						
				if (isMappingInConflictWithFixedMappings(ide1, ide2)){
					addSubMapping2ConflictiveAnchors(ide1, ide2);
				}
				else if (!isMappingInferred(ide1, ide2)){ //If inferred do not add
					
					//Statistics
					StatisticsManager.addStatisticsSplitMapping(ide1, ide2);
					
					//Add 2 list 2 ask
					if (isId1SmallerThanId2(ide1, ide2))
						addMappingObject2AskUserList(ide1, ide2, Utilities.L2R);
					else
						addMappingObject2AskUserList(ide2, ide1, Utilities.R2L);
				}
			}
		}
		weakenedDandG_Mappings1N.clear();
		
	
		//Statistics mappings to ask
		StatisticsManager.extractStatisticsMappings2Ask();
		
		//STATISTCS
		StatisticsManager.extracStatisticsDiscardedMappings();
		StatisticsManager.extracStatisticsHardDiscardedMappings();
		StatisticsManager.extracStatisticsConflictiveMappings();
		StatisticsManager.extracStatisticsConflictiveMappings_D_G();
	
		
		
	}
	
	
	/**
	 * This method checks if some of the conflictive could be retrieved back.
	 * That is, not in conflict with index.
	 */
	public void assessConflictiveMappings(){

		
		for (int ident1 : getConflictiveMappings().keySet()){
			
			for (int ident2 : getConflictiveMappings().get(ident1)){
				
				if (isMappingInConflictWithFixedMappings(ident1, ident2)){
					
					//if ()
					
				}
				
				
			}
			
		}

	}
	
	
	
	/**
	 * We add them iff they are not already inferred and are not in conflict
	 * Last Step of LogMap
	 * @deprecated
	 */
	public void assesWeakenedMappingsDandG2(boolean removeAfterwards, boolean add2Anchors){
		//Mappings to review and 
		LogOutput.print("Assessing weakened mappings by Dowling and Gallier.");
		
		for (int ide1 : weakenedDandG_Mappings1N.keySet()){
					
			for (int ide2 : weakenedDandG_Mappings1N.get(ide1)){
						
				if (isMappingInConflictWithFixedMappings(ide1, ide2)){
					addSubMapping2ConflictiveAnchors(ide1, ide2);
				}
				else if (!isMappingInferred(ide1, ide2)){ //If inferred do not add
					//Add in exact
					//
					if (add2Anchors) //Final addition
						addSubMapping2ListOfAnchors(ide1, ide2);
					else
						addSubMapping2Mappings2Review(ide1, ide2); //check with new ones
				}
			}
		}
		
		if (removeAfterwards)
			weakenedDandG_Mappings1N.clear();
	}
	
	
	
	
	
	
	/**
	 * Instance ANCHORS
	 */
	public void createInstanceAnchors(){
		
		InstanceMatchingAssessment instanceMappingAssessment = new InstanceMatchingAssessment(index, this);
		
		double required_confidence;
		double compatibility_factor;
		double confidence;
		//0: ok, 1: disc1, 2: disc2, 3: incompatible
		int type_output;
		//int num=0;
		
		int num_incompatible_instances =0;
		
		//EXACT IF
		LogOutput.print("Size IF intersection exact: " + if_exact_intersection4individuals.size());
		//System.out.println("Size IF intersection exact: " + if_exact_intersection4individuals.size());
		
		boolean ambiguity;
		for (Set<String> if_entry : if_exact_intersection4individuals){
			
			ambiguity=false;
			
			//Only those individuals uniquely identified or almost!!
			 if (onto_process1.getInvertedFileMatching4Individuals().get(if_entry).size()>1 ||
			     onto_process2.getInvertedFileMatching4Individuals().get(if_entry).size()>1){
				ambiguity=true; 
			 }
				
			for (int ident1 : onto_process1.getInvertedFileMatching4Individuals().get(if_entry)){
				
				for (int ident2 : onto_process2.getInvertedFileMatching4Individuals().get(if_entry)){
					
					//required confidence and comp factor are the same...
					required_confidence = instanceMappingAssessment.getConfidence4Compatibility(ident1, ident2);
					compatibility_factor = instanceMappingAssessment.getCompatibilityFactor(ident1, ident2);
					
					
					//TODO Categories, only for ambiguouss mappings
					if (!instanceMappingAssessment.haveInstancesCompatibleCategories(ident1, ident2) && ambiguity){
						required_confidence=3.0;
					}
					
					
					if (required_confidence>1.0){
						//LogOutput.print("Incompatible individuals: " + if_entry + " " + ident1 + " " + ident2);
						//LogOutput.print("\t" +index.getName4IndividualIndex(ident1) + " " + index.getAlternativeLabels4IndividualIndex(ident1));
						//LogOutput.print("\t" +index.getName4IndividualIndex(ident2) + " " + index.getAlternativeLabels4IndividualIndex(ident2));
						num_incompatible_instances++;
						
						type_output=3;//incomp
						
					}
					else{
						
						//we extract isub
						confidence = extractISUB4InstanceMapping(ident1, ident2);
						
						
						
						if (confidence>=required_confidence){
																		
							addInstanceMapping(ident1, ident2, ambiguity);
							//addSubInstanceMapping(ident2, ident1); we only add one side since we do not split instance mappings
							
							type_output=0;
							
													
			    		}
						else{
							type_output=1;//disc 1
							//LogOutput.print("Not good individuals: " + if_entry + " " + ident1 + " " + ident2 + "  " + confidence + " " + required_confidence);
							//LogOutput.print("\t" +index.getName4IndividualIndex(ident1) + " " + index.getAlternativeLabels4IndividualIndex(ident1));
							//LogOutput.print("\t" +index.getName4IndividualIndex(ident2) + " " + index.getAlternativeLabels4IndividualIndex(ident2));
						}
					}
					
					if (Parameters.output_instance_mapping_files){
						
						addOutputType4Indivual(ident1, ident2, type_output);
						
						//EXTRACT ISB IF not yet
						extractISUB4InstanceMapping(ident1, ident2);
						
						//add comp factor
						addCompFactor4Indivual(ident1,  ident2, compatibility_factor);
						
						//Extract scope
						extractScope4InstanceMapping(ident1, ident2);
						
					}
					
					
				}
				
			}
			
		}//for if exact
		
		LogOutput.print("\nNUmber of Instance mappings exact IF: " + getInstanceMappings().keySet().size() + " - " + getSizeOfInstanceMappings());
		//System.out.println("\nNUmber of Instance mappings exact IF: " + getInstanceMappings().keySet().size() + " - " + getSizeOfInstanceMappings());
		
		LogOutput.print("NUM INCOMPATIBLE INDIV MAPPINGS: " + num_incompatible_instances + "\n");
		
		if_exact_intersection4individuals.clear();
		num_incompatible_instances=0;
		
		
		//WEAK IF
		LogOutput.print("Sife IF intersection weak: " + if_weak_intersection4individuals.size());
		
		for (String if_entry : if_weak_intersection4individuals){
			
			ambiguity=false;
			//max ambiguity for these cases
			int max_amb=1; ///TODO this also changed!
			
			//Only those individuals uniquely identified or almost!!
			 if (onto_process1.getInvertedFileWeakMatching4Individuals().get(if_entry).size()>max_amb ||
			     onto_process2.getInvertedFileWeakMatching4Individuals().get(if_entry).size()>max_amb){
				ambiguity=true; 
			 }
			
			
			for (int ident1 : onto_process1.getInvertedFileWeakMatching4Individuals().get(if_entry)){
				
				if (isIndividualAlreadyMapped(ident1)){
					continue;
				}
				
				for (int ident2 : onto_process2.getInvertedFileWeakMatching4Individuals().get(if_entry)){
				
					if (isIndividualAlreadyMapped(ident2)){
						continue;
					}
					
				
					
					//New candidate mapping
					required_confidence = instanceMappingAssessment.getConfidence4Compatibility(ident1, ident2);
					compatibility_factor = instanceMappingAssessment.getCompatibilityFactor(ident1, ident2);									
					
					if (ambiguity){
					
						required_confidence=3.0;
						
						//TODO Categories, only for ambiguouss mappings
						if (instanceMappingAssessment.haveInstancesCompatibleCategories(ident1, ident2)){
							
						}
					}
					
					
					if (required_confidence>1.0){
						//LogOutput.print("Incompatible individuals: " + if_entry + " " + ident1 + " " + ident2);
						//LogOutput.print("\t" +index.getName4IndividualIndex(ident1) + " " + index.getAlternativeLabels4IndividualIndex(ident1));
						//LogOutput.print("\t" +index.getName4IndividualIndex(ident2) + " " + index.getAlternativeLabels4IndividualIndex(ident2));
						num_incompatible_instances++;
						
						type_output=3;
						
					}
					
					else {
						//we extract isub
						confidence = extractISUB4InstanceMapping(ident1, ident2);									
						
						if (confidence>=required_confidence){
																		
							addInstanceMapping(ident1, ident2, ambiguity);
							//addSubInstanceMapping(ident2, ident1); we only add one side
							
							type_output=0;
													
			    		}
						else{							
							if (confidence>0.65){
								//LogOutput.print("Not good individuals: " + if_entry + " " + ident1 + " " + ident2 + "  " + confidence + " " + required_confidence);
								//LogOutput.print("\t" +index.getName4IndividualIndex(ident1) + " " + index.getAlternativeLabels4IndividualIndex(ident1));
								//LogOutput.print("\t" +index.getName4IndividualIndex(ident2) + " " + index.getAlternativeLabels4IndividualIndex(ident2));
								//LogOutput.print("\t Types 1: " +index.getIndividualClassTypes4Identifier(ident1));
								//for (int id : index.getIndividualClassTypes4Identifier(ident1)){
								//	LogOutput.printAlways("\t\t"+ index.getName4ConceptIndex(id));
								//}
								//LogOutput.print("\t Types 2: " +index.getIndividualClassTypes4Identifier(ident2));
								//for (int id : index.getIndividualClassTypes4Identifier(ident2)){
								//	LogOutput.printAlways("\t\t"+ index.getName4ConceptIndex(id));
								//}
								type_output=1;								
							}
							else{
								type_output=2;
							}
						}
					}
					
					if (Parameters.output_instance_mapping_files){
						
						addOutputType4Indivual(ident1, ident2, type_output);
						
						//EXTRACT ISB IF not yet
						extractISUB4InstanceMapping(ident1, ident2);
						
						//add comp factor
						addCompFactor4Indivual(ident1,  ident2, compatibility_factor);
						
						//Extract scope
						extractScope4InstanceMapping(ident1, ident2);
						
					}
					
					
					
				}
				
			}
			
		}
		
		LogOutput.print("\nNUmber of Instance mappings exact+weak IF: " + getInstanceMappings().keySet().size() + " - " + getSizeOfInstanceMappings());
		//System.out.println("\nNUmber of Instance mappings exact+weak IF: " + getInstanceMappings().keySet().size() + " - " + getSizeOfInstanceMappings());
		LogOutput.print("NUM INCOMPATIBLE INDIV MAPPINGS: " + num_incompatible_instances);
		
		
		if_weak_intersection4individuals.clear();
		
		
		
		//WEAK IF role assertions
		LogOutput.print("Size role assetions: " + if_roleassertions_intersection4individuals.size());
		//System.out.println("Size role assetions IF itersect: " + if_roleassertions_intersection4individuals.size());
		
		
		//max ambiguity for these cases
		int max_amb=1; ///TODO this also changed!
		
		for (String if_entry : if_roleassertions_intersection4individuals){
			
			
			//Only those individuals uniquely identified or almost!!
			 if (onto_process1.getInvertedFileRoleAssertions().get(if_entry).size()>max_amb ||
				onto_process2.getInvertedFileRoleAssertions().get(if_entry).size()>max_amb)
				continue;
			
			
			/*if ((onto_process1.getInvertedFileRoleAssertions().get(if_entry).size()+
				onto_process2.getInvertedFileRoleAssertions().get(if_entry).size()) > max_amb)
					continue;
			*/
			
			
			
			for (int ident1 : onto_process1.getInvertedFileRoleAssertions().get(if_entry)){
				
				if (isIndividualAlreadyMapped(ident1)){
					continue;
				}
				
				for (int ident2 : onto_process2.getInvertedFileRoleAssertions().get(if_entry)){
				
					if (isIndividualAlreadyMapped(ident2)){
						continue;
					}
					
					//New candidate mapping
					//System.out.println("NEW!! Size IFs: "+ onto_process1.getInvertedFileRoleAssertions().get(if_entry).size()+ " - "+ onto_process2.getInvertedFileRoleAssertions().get(if_entry).size());
					
					required_confidence = instanceMappingAssessment.getConfidence4Compatibility(ident1, ident2);
					compatibility_factor = instanceMappingAssessment.getCompatibilityFactor(ident1, ident2);
					
					//TODO Categories
					if (!instanceMappingAssessment.haveInstancesCompatibleCategories(ident1, ident2)){
						required_confidence=3.0;
					}
					
					
					if (required_confidence>1.0){
						//LogOutput.print("Incompatible individuals: " + if_entry + " " + ident1 + " " + ident2);
						//LogOutput.print("\t" +index.getName4IndividualIndex(ident1) + " " + index.getAlternativeLabels4IndividualIndex(ident1));
						//LogOutput.print("\t" +index.getName4IndividualIndex(ident2) + " " + index.getAlternativeLabels4IndividualIndex(ident2));
						num_incompatible_instances++;
						type_output = 3;
						
					}
					else {
						//we extract isub
						confidence = extractISUB4InstanceMapping(ident1, ident2);									
						
						//we do not discard by confidence since if is probably low
						addInstanceMapping(ident1, ident2, true); //check always
						
						type_output = 0;
					}
					
					
					
					if (Parameters.output_instance_mapping_files){
						
						addOutputType4Indivual(ident1, ident2, type_output);
						
						//EXTRACT ISB IF not yet
						extractISUB4InstanceMapping(ident1, ident2);
						
						//add comp factor
						addCompFactor4Indivual(ident1,  ident2, compatibility_factor);
						
						//Extract scope
						extractScope4InstanceMapping(ident1, ident2);
					}
					
					
				}
				
			}
			
		}
		
		LogOutput.print("\nNUmber of Instance mappings exact+weak+roleass IF: " + getInstanceMappings().keySet().size() + " - " + getSizeOfInstanceMappings());
		//System.out.println("\nNUmber of Instance mappings exact+weak+roleass IF: " + getInstanceMappings().keySet().size() + " - " + getSizeOfInstanceMappings());
		
		LogOutput.print("NUM INCOMPATIBLE INDIV MAPPINGS: " + num_incompatible_instances);
		
		if_roleassertions_intersection4individuals.clear();
						
		
		
		LogOutput.print("Instances: " + instanceMappings1N.size());
		LogOutput.print("Instances ambiguity: " + instanceMappings1N_ambiguity.size());
		LogOutput.print("Instances not in output: " + instanceMappings1N_not_allowed_output.size());
		
		//-----------------------------------------------------------------
		//REVISE instance mappings using referenced individuals
		//------------------------------------------------------------------
		double required_sim;
		for (int index1 : instanceMappings1N_ambiguity.keySet()){
			for (int index2 : instanceMappings1N_ambiguity.get(index1)){
				
				
				double sim = getSimilarityReferredInstances(index1, index2);
				//if (sim >0)
				//System.out.println("Similarity: " + sim);
				
				
				required_sim=0.20;
				if (getISUB4InstanceMapping(index1, index2)>0.9){
					required_sim=0.10;
				}
				
				
				if (sim>required_sim){
					//TODO probably not good for  precision
					//addInstanceMapping(index1, index2);
				}
								
				//We give another chance to the individuals using their characteristics (e.g. publication count, numb of brothers, num directed films etc.)
				//Charactersitics are a list of values
				//TODO probably not good for precision
				//if (haveIndividualsSameCharacteristics(index1, index2))
				//	addInstanceMapping(index1, index2);
			}
			
		}
		
		
		
	}
	
	/**
	 * Returns Jaccard-based similarity
	 * @param index1
	 * @param index2
	 * @return
	 */
	private double getSimilarityReferredInstances(int index1, int index2){
		
		//For mapped instances we use the target individuals
		Set<Integer> referred1_2 = new HashSet<Integer>();		
		Set<Integer> referred1 = index.getReferencedIndividuals4Individual(index1);
		for (int ref_indiv1 : referred1){
			if (instanceMappings1N.containsKey(ref_indiv1)){
				referred1_2.addAll(instanceMappings1N.get(ref_indiv1));
			}
			if (instanceMappings1N_ambiguity.containsKey(ref_indiv1)){
				referred1_2.addAll(instanceMappings1N_ambiguity.get(ref_indiv1));
			}
			if (instanceMappings1N_not_allowed_output.containsKey(ref_indiv1)){
				referred1_2.addAll(instanceMappings1N_not_allowed_output.get(ref_indiv1));
			}
			
		}
		
		//System.out.println(index.getLabel4IndividualIndex(index1));
		
		
		Set<Integer> referred2 = index.getReferencedIndividuals4Individual(index2);
		
		int size_union = referred2.size() + referred1_2.size();
		
		
		//System.out.println("\t: Referred 1: " + referred1.size());
		//System.out.println("\t: Referred 1->2: " + referred1_2.size());
		//System.out.println("\t: Referred 2: " + referred2.size());
		
		
		//intersection
		referred1_2.retainAll(referred2);		
		
		
		//System.out.println("\tIntesection: " + referred1_2.size());
		
		
		//Jaccard index
		return (double)((double)referred1_2.size()/(double) size_union);
		
	}
	
	
	private boolean haveIndividualsSameCharacteristics(int index1, int index2){
	
		List<Integer> charact1 = index.getCharactersitics4Individual(index1);
		List<Integer> charact2 = index.getCharactersitics4Individual(index2);
		
		if (charact1.size()!=charact2.size())
			return false;
		
		for (int i=0; i < charact1.size(); i++){
			//if (charact1.get(i)!=charact2.get(i)){
			if (!charact1.get(i).equals(charact2.get(i))){
				//if (i==2)
				//	LogOutput.printError(charact1.get(i) + "-" + charact2.get(i));
				return false;
			}
		}
		
			
		return true;
	}
	
	
	
	/**
	 * @deprecated
	 * @param index1
	 * @param index2
	 * @return
	 */
	private boolean areReferredInstancesInContainmentRelationship(int index1, int index2){
		
		//For mapped instances we use the target individuals
		Set<Integer> referred1_2 = new HashSet<Integer>();		
		Set<Integer> referred1 = index.getReferencedIndividuals4Individual(index1);
		for (int ref_indiv1 : referred1){
			if (instanceMappings1N.containsKey(ref_indiv1)){
				referred1_2.addAll(instanceMappings1N.get(ref_indiv1));
			}
			if (instanceMappings1N_ambiguity.containsKey(ref_indiv1)){
				referred1_2.addAll(instanceMappings1N_ambiguity.get(ref_indiv1));
			}
			if (instanceMappings1N_not_allowed_output.containsKey(ref_indiv1)){
				referred1_2.addAll(instanceMappings1N_not_allowed_output.get(ref_indiv1));
			}
			
		}
		
		
		Set<Integer> referred2 = index.getReferencedIndividuals4Individual(index2);
		
		if (referred2.containsAll(referred1_2) || referred1_2.containsAll(referred2)){
			System.out.println("Containment");
			return true;
		}
		
		return false;
		
		
	}
	
	
	
	
	
	
	
	
	/**
	 * Called from repair facility and assessment from web service. Only to delete incompatible mappings.
	 * Isub in principle has been already added
	 * 
	 * 
	 */
	public void evaluateCompatibilityInstanceMappings(){
		
		InstanceMatchingAssessment instanceMappingAssessment = new InstanceMatchingAssessment(index, this);
		
		Map<Integer, Set<Integer>> todelete = new HashMap<Integer, Set<Integer>>();
		double required_confidence;
		
		
		int num_incompatible_instances=0;
		
		//We evaluate the compatibility wrt classes
		for (int ident1 : getInstanceMappings().keySet()){
		
			for (int ident2 : getInstanceMappings().get(ident1)){
				
				required_confidence = instanceMappingAssessment.getConfidence4Compatibility(ident1, ident2);
				
				//TODO in this repair we do not remove according to categories
				
				if (required_confidence>1.0){
					LogOutput.print("Incompatible individuals: " + ident1 + " " + ident2);
					LogOutput.print("\t" +index.getName4IndividualIndex(ident1) + " " + index.getAlternativeLabels4IndividualIndex(ident1));
					LogOutput.print("\t" +index.getName4IndividualIndex(ident2) + " " + index.getAlternativeLabels4IndividualIndex(ident2));
					num_incompatible_instances++;
					
					if (!todelete.containsKey(ident1)){
						todelete.put(ident1, new HashSet<Integer>());
					}
					todelete.get(ident1).add(ident2);
														
				}
		
			}
		}
			
		
		deleteIncompatibleInstanceMappings(todelete);	
		LogOutput.print("NUM INCOMPATIBLE INDIV MAPPINGS: " + num_incompatible_instances);
		
		todelete.clear();
			
			
	}
	
	private void deleteIncompatibleInstanceMappings(Map<Integer, Set<Integer>> todelete){

		for (int ident1 : todelete.keySet()){
			
			for (int ident2 : todelete.get(ident1)){
				
				getInstanceMappings().get(ident1).remove(ident2);
				
				if (getInstanceMappings().get(ident1).size()==0){
					getInstanceMappings().remove(ident1);
				}
				
			}
		}
		
	}
	
	
	
	/**
	 * 
	 * DATA PROPERTY ANCHORS
	 * 
	 */
	public void createDataPropertyAnchors() {
		
		// Intersects IF and also uses ISUB
		
		
		
		
		//if_exact_intersection4data_prop.clear();
		
		
		//Entries are only 1 to 1
		for (Set<String> if_entry : if_exact_intersection4data_prop){
			
			dataPropertyMappings.put(
					onto_process1.getInvertedFileExactMatching4DataProp().get(if_entry),
					onto_process2.getInvertedFileExactMatching4DataProp().get(if_entry)
					);
			
			dataPropertyMappings2confidence.put(
					onto_process1.getInvertedFileExactMatching4DataProp().get(if_entry),
					1.0
					);			
		}
		
		if_exact_intersection4data_prop.clear();
		
		
		

		//ISUB
		double maxconf=-10;
		double score;
		int identmax=-1;
		
		//use isub to extract references
		for (int ident1 : index.getDataPropIdentifierSet()){
			maxconf=-10;
			identmax=-1;
			
			//if (index.getDataPropertyIndex(ident1).getOntologyId()>)
			//	continue;
			
			if (dataPropertyMappings.containsKey(ident1))
				continue;
			
			for (int ident2 : index.getDataPropIdentifierSet()){
				
				if (dataPropertyMappings.containsValue(ident2))
					continue;
				
				if (ident1>=ident2)
					continue;
				
				//They are from the same ontology
				if (index.getDataPropertyIndex(ident1).getOntologyId()==index.getDataPropertyIndex(ident2).getOntologyId()){
					continue;
				}
				
				score=getIsubScore4DataPropertyLabels(ident1, ident2);
				
				if (score>maxconf){
					maxconf=score;
					identmax=ident2;
				}
			}
			
			if (maxconf>0.75){ //Input parameter
				dataPropertyMappings.put(ident1, identmax);
				dataPropertyMappings2confidence.put(ident1, maxconf);
			}
		}
		
		LogOutput.printAlways("Dirty anchors Data Prop: "  + dataPropertyMappings.size());
		
		//COMPATIBILITY
		//Analize compatibility of domains and ranges
		//Filter: eg writtenBy (cmt confof)		
		evaluateCompatibilityDataPropertyMappings(); //and delete conflictive mappings
		
		
		
	}
	
	
	
	public void evaluateCompatibilityDataPropertyMappings(){
		
		//COMPATIBILITY
		//Analize compatibility of domains and ranges
		//Filter: eg writtenBy (cmt confof)		
		DataPropertyMappingAssessment propertyMappingAssessment = new DataPropertyMappingAssessment(index, this);
		Set<Integer> todelete = new HashSet<Integer>();
		double required_confidence;
		double confidence_mapping;
		for (int ident1 : dataPropertyMappings.keySet()){
			
			//required_confidence = propertyMappingAssessment.arePropertiesCompatible(ident1, dataPropertyMappings.get(ident1));
			required_confidence = propertyMappingAssessment.getConfidence4Compatibility(ident1, dataPropertyMappings.get(ident1));

			
			confidence_mapping = getConfidence4DataPropertyAnchor(ident1, dataPropertyMappings.get(ident1));
			
			if (confidence_mapping<required_confidence){
    			
				
				LogOutput.printAlways(required_confidence +  "   " +  dataPropertyMappings2confidence.get(ident1));
				
				if (required_confidence>1.5){
					LogOutput.printAlways("Incompatible data properties");
					LogOutput.printAlways("\t" +index.getName4DataPropIndex(ident1));
					LogOutput.printAlways("\t" +index.getName4DataPropIndex(dataPropertyMappings.get(ident1)));
				}
				//TODO 
    			//Ask if oracle active and properties are compatible
				else if (OracleManager.isActive()){
    				if (confidence_mapping>=Parameters.min_conf_pro_map){
    					if (OracleManager.isMappingValid(
    							index.getIRIStr4DataPropIndex(ident1),
    							index.getIRIStr4DataPropIndex(dataPropertyMappings.get(ident1)))){
    						
    						LogOutput.printAlways("Data property mapping in Oracle");
    						LogOutput.printAlways("\t" +index.getName4DataPropIndex(ident1));
    						LogOutput.printAlways("\t" +index.getName4DataPropIndex(dataPropertyMappings.get(ident1)));
    						
    						
    						//We also ask for domains
    						//Not any more
    						//askOraculoAboutDomains4DataPropertyMappings(ident1, dataPropertyMappings.get(ident1));
    						
    						
    						continue; //so we do not delete the mapping
    					}
    					else{
    						LogOutput.printAlways("Data property mapping NOT in Oracle");
    						LogOutput.printAlways("\t" +index.getName4DataPropIndex(ident1));
    						LogOutput.printAlways("\t" +index.getName4DataPropIndex(dataPropertyMappings.get(ident1)));
    					}
    				}
    			}
				
    			//deleteDataPropertyAnchor(ident1);
				
				todelete.add(ident1);
				
				//LogOutput.printAlways(index.getIRIStr4DataPropIndex(ident1));
				//LogOutput.printAlways(index.getIRIStr4DataPropIndex(dataPropertyMappings.get(ident1)));
    		}
			//ONLY IF oraculo active we double check those cases extended with alternative labels
			else if (OracleManager.isActive() && (index.getAlternativeLabels4DataPropertyIndex(ident1).size()>1 || index.getAlternativeLabels4DataPropertyIndex(dataPropertyMappings.get(ident1)).size()>1)){
				
				
				double isub_labels = getIsubScore4DataPropertyLabels(ident1, dataPropertyMappings.get(ident1), false);
				
				//Check isub without alternative labels
				if (isub_labels<Parameters.min_conf_pro_map){ //very bad confidence (also smaller than required_conf)
				
					LogOutput.printAlways(required_confidence +  "   " +  isub_labels);
					
					if (!OracleManager.isMappingValid(
							index.getIRIStr4DataPropIndex(ident1),
							index.getIRIStr4DataPropIndex(dataPropertyMappings.get(ident1)))){
						
						LogOutput.printAlways("Good Confidence Data property mapping NOT in Oracle");
						LogOutput.printAlways("\t" +index.getName4DataPropIndex(ident1));
						LogOutput.printAlways("\t" +index.getName4DataPropIndex(dataPropertyMappings.get(ident1)));
						
						todelete.add(ident1);
					
					}
					else {
						LogOutput.printAlways("Good Confidence Data property mapping In Oracle");
						LogOutput.printAlways("\t" +index.getName4DataPropIndex(ident1));
						LogOutput.printAlways("\t" +index.getName4DataPropIndex(dataPropertyMappings.get(ident1)));
						
						//We also ask for domains
						//NOt anymore
						//askOraculoAboutDomains4DataPropertyMappings(ident1, dataPropertyMappings.get(ident1));
						
					}
				}
			}
			
		}//end for
		
		
		
		deleteDataPropertyAnchors(todelete);
		
		LogOutput.printAlways("\tAssessing Data Property mappings: "  + dataPropertyMappings.size() + ", to delete: " + todelete.size());
		
		todelete.clear();
		
	}
	

	
	/**
	 * 
	 * OBJECT PROPERTY ANCHORS
	 * 
	 */
	public void createObjectPropertyAnchors() {
		
		
		
		//if_exact_intersection4obj_prop.clear();
		
		for (Set<String> set_str : if_exact_intersection4obj_prop){
			
			objPropertyMappings.put(
					onto_process1.getInvertedFileExactMatching4ObjProp().get(set_str),
					onto_process2.getInvertedFileExactMatching4ObjProp().get(set_str)
					);
			
			objPropertyMappings2confidence.put(
					onto_process1.getInvertedFileExactMatching4ObjProp().get(set_str),
					1.0);
		}
		
		
		if_exact_intersection4obj_prop.clear();
		
		
		
		///ISUB
		//use isub to extract references
		double maxconf=-10;
		double score;
		int identmax=-1;
		
		//use isub to extract references
		for (int ident1 : index.getObjectPropIdentifierSet()){
			maxconf=-10;
			identmax=-1;
			
			if (objPropertyMappings.containsKey(ident1))
				continue;
			
			for (int ident2 : index.getObjectPropIdentifierSet()){
				
				if (objPropertyMappings.containsValue(ident2))
					continue;
				
				
				if (ident1>=ident2)
					continue;
				
				//They are from the same ontology
				if (index.getObjectPropertyIndex(ident1).getOntologyId()==index.getObjectPropertyIndex(ident2).getOntologyId()){
					continue;
				}
				
				
				score=getIsubScore4ObjectPropertyLabels(ident1, ident2);
				
				if (score>maxconf){
					maxconf=score;
					identmax=ident2;
				}
			}
			
			if (maxconf>0.75){
				objPropertyMappings.put(ident1, identmax);
				objPropertyMappings2confidence.put(ident1, maxconf);
			}
		}
		
		
		LogOutput.printAlways("Dirty anchors Object Prop: " + objPropertyMappings.size());
		
		
		//COMPATIBILITY
		//Analize compatibility of domains and ranges
		//Filter: eg writtenBy (cmt confof)
		evaluateCompatibilityObjectPropertyMappings();
		
		
		
		
	}
	
	
	public void evaluateCompatibilityObjectPropertyMappings(){
		
		//COMPATIBILITY
		//Analize compatibility of domains and ranges
		//Filter: eg writtenBy (cmt confof)
		
		
		ObjectPropertyMappingAssessment propertyMappingAssessment = new ObjectPropertyMappingAssessment(index, this);
		
		Set<Integer> todelete = new HashSet<Integer>();
		double required_confidence;
		double confidence_mapping;
		for (int ident1 : objPropertyMappings.keySet()){
			
			
			//required_confidence = propertyMappingAssessment.arePropertiesCompatible(ident1, objPropertyMappings.get(ident1));
			required_confidence = propertyMappingAssessment.getConfidence4Compatibility(ident1, objPropertyMappings.get(ident1));					
			
			
			
			
			
			//LogOuput.print(ident1);
			//LogOuput.print(objPropertyMappings2confidence);
			//LogOuput.print(objPropertyMappings.get(ident1));
			//LogOuput.print(objPropertyMappings2confidence.get(ident1));
			confidence_mapping = getConfidence4ObjectPropertyAnchor(ident1, objPropertyMappings.get(ident1));
			if (confidence_mapping<required_confidence){
				
				//LogOutput.print(index.getName4ObjPropIndex(ident1));
				//LogOutput.print(index.getName4ObjPropIndex(objPropertyMappings.get(ident1)));
				LogOutput.printAlways(required_confidence +  "   " +  objPropertyMappings2confidence.get(ident1));
				
				if (required_confidence>1.5){
					LogOutput.printAlways("Incompatible object properties");
					LogOutput.printAlways(index.getIRIStr4ObjPropIndex(ident1));
					LogOutput.printAlways(index.getIRIStr4ObjPropIndex(objPropertyMappings.get(ident1)));
				}
    			//TODO 
    			//Ask if oracle active and compatible props (we also include PROBABLY_INCOMPATIBLE_RANGE_OR_DOMAIN)
				else if (OracleManager.isActive()){
    				if (confidence_mapping>=Parameters.min_conf_pro_map){
    					if (OracleManager.isMappingValid(
    							index.getIRIStr4ObjPropIndex(ident1),
    							index.getIRIStr4ObjPropIndex(objPropertyMappings.get(ident1)))){
    						
    						LogOutput.printAlways("Object property mapping in Oracle");
    						LogOutput.printAlways(index.getIRIStr4ObjPropIndex(ident1));
    						LogOutput.printAlways(index.getIRIStr4ObjPropIndex(objPropertyMappings.get(ident1)));
    						
    						//We also ask for domain and ranges
    						//NOt any more
    						//askOraculoAboutDomainsAndRange4ObjectPropertyMappings(ident1, objPropertyMappings.get(ident1));
    						
    						continue; //so we do not delete the mapping
    					}
    					else {
    						LogOutput.printAlways("Object property mapping NOT in Oracle");
    						LogOutput.printAlways(index.getIRIStr4ObjPropIndex(ident1));
    						LogOutput.printAlways(index.getIRIStr4ObjPropIndex(objPropertyMappings.get(ident1)));
    					}
    				}
    			}
    			
    			todelete.add(ident1);
				//deleteObjectPropertyAnchor(ident1);
    			
    		}
			//ONLY IF oraculo active we double check those cases extended with alternative labels
			else if (OracleManager.isActive() && (index.getAlternativeLabels4ObjectPropertyIndex(ident1).size()>1 || index.getAlternativeLabels4ObjectPropertyIndex(objPropertyMappings.get(ident1)).size()>1)){
				//Check isub without alternative labels
				
				double isub_labels = getIsubScore4ObjectPropertyLabels(ident1, objPropertyMappings.get(ident1), false);
				
				if (isub_labels<Parameters.min_conf_pro_map){ //bad confidence (also smaller than required_conf)
					
					LogOutput.printAlways(required_confidence +  "   " +  isub_labels);
					
					
					if (!OracleManager.isMappingValid(
							index.getIRIStr4ObjPropIndex(ident1),
							index.getIRIStr4ObjPropIndex(objPropertyMappings.get(ident1)))){
						
						todelete.add(ident1);
						
						LogOutput.printAlways("Good Confidence Object property mapping NOT in Oracle");
						LogOutput.printAlways(index.getIRIStr4ObjPropIndex(ident1));
						LogOutput.printAlways(index.getIRIStr4ObjPropIndex(objPropertyMappings.get(ident1)));
					
					}
					else {
						LogOutput.printAlways("Good Confidence Object property mapping In Oracle");
						LogOutput.printAlways(index.getIRIStr4ObjPropIndex(ident1));
						LogOutput.printAlways(index.getIRIStr4ObjPropIndex(objPropertyMappings.get(ident1)));
						
						//We also ask for domain and ranges
						//Not anu more
						//askOraculoAboutDomainsAndRange4ObjectPropertyMappings(ident1, objPropertyMappings.get(ident1));
						
					}
				}
			}
		}//end for
		
		deleteObjectPropertyAnchors(todelete);
		
		LogOutput.printAlways("\tAssessing Object Property mappings: "  + objPropertyMappings.size() + ", to delete: " + todelete.size());
		
		todelete.clear();
		
	}
	
	
	private void askOraculoAboutDomains4DataPropertyMappings(int ident1, int ident2){
		
		Set<Integer> domain1=index.getDomainDataProp4Identifier(ident1);
		Set<Integer> domain2=index.getDomainDataProp4Identifier(ident2);
		
		
		//askOraculoAboutPossibleMappings(domain1, domain2);
	
		
	}
	
	
	
	private void askOraculoAboutDomainsAndRange4ObjectPropertyMappings(int ident1, int ident2){
		
		Set<Integer> domain1=index.getDomainObjProp4Identifier(ident1);
		Set<Integer> domain2=index.getDomainObjProp4Identifier(ident2);
		
		
		Set<Integer> ranges1=index.getRangeObjProp4Identifier(ident1);
		Set<Integer> ranges2=index.getRangeObjProp4Identifier(ident2);
		
		
		//askOraculoAboutPossibleMappings(domain1, domain2);
		
		//askOraculoAboutPossibleMappings(ranges1, ranges2);
		
	}
	
	
	private void askOraculoAboutPossibleMappings(Set<Integer> classes1, Set<Integer> classes2){
		
		for (int cls1 : classes1){
			for (int cls2 : classes2){
				
				//Only mappings not considered already as conflictive or included in anchors
				if (!isMappingInConflictiveSet(cls1, cls2) && 
						!isMappingAlreadyInList(cls1, cls2) && 
						!isMappingInConflictiveSet(cls2, cls1) && 
						!isMappingAlreadyInList(cls2, cls1) &&
						!isMappingInConflictWithFixedMappings(cls1, cls2)){
					
					//We ask oraculo
					if (OracleManager.isMappingValid(
							index.getIRIStr4ConceptIndex(cls1), 
							index.getIRIStr4ConceptIndex(cls2))){
						
						
						//it may still be in conflict with other classes
						LogOutput.printAlways("Added new mapping: " +
								index.getIRIStr4ConceptIndex(cls1) + "  " +
								index.getIRIStr4ConceptIndex(cls2));
						
						
						addSubMapping2ListOfAnchors(cls1, cls2);
						addSubMapping2ListOfAnchors(cls2, cls1);

						
					}
					else{
						LogOutput.printAlways("NOT Added new mapping: " +
								index.getIRIStr4ConceptIndex(cls1) + "  " +
								index.getIRIStr4ConceptIndex(cls2));
					}
					
					
				}
				else if (isMappingInConflictWithFixedMappings(cls1, cls2)){
					LogOutput.printAlways("In Conflict new mapping: " + 
							index.getIRIStr4ConceptIndex(cls1) + "  " +
							index.getIRIStr4ConceptIndex(cls2));
				}
				
			}
			
		}
		
	}
	
	
	
	
	
	private void deleteDataPropertyAnchors(Set<Integer> todelete){
		dataPropertyMappings.keySet().removeAll(todelete);
		dataPropertyMappings2confidence.keySet().removeAll(todelete);
		
	}
	
	private void deleteObjectPropertyAnchors(Set<Integer> todelete){
		objPropertyMappings.keySet().removeAll(todelete);
		objPropertyMappings2confidence.keySet().removeAll(todelete);
		
	}
	
	
	
	
	
	private Map<Integer, Set<Integer>> visitedScopeMappings = new HashMap<Integer, Set<Integer>>();
	
	private boolean isaVisitedScopeMapping(int index1, int index2){
		
		if (visitedScopeMappings.containsKey(index1)){
			if (visitedScopeMappings.get(index1).contains(index2)){
				return true;
			}
		}
		return false;
		
	}
	
	private void addVisistedScopeMapping(int index1, int index2){
		if (!visitedScopeMappings.containsKey(index1)){
			visitedScopeMappings.put(index1, new HashSet<Integer>());
		}
		visitedScopeMappings.get(index1).add(index2);
	}
	
	
	/**
	 * We anaylize the scope of current mappings
	 * @deprecated
	 */
	protected void createAnchorsExpansion(){
		
		//We transform structure anchorMappings1N to list of objects 
		
		LinkedList<MappingObjectIdentifiers> list_mappings = new LinkedList<MappingObjectIdentifiers>();
		
		for (int ide1 : logmapMappings1N.keySet()){
			for (int ide2 : logmapMappings1N.get(ide1)){
				
				//we consider the equivalence only
				if (isId1SmallerThanId2(ide1, ide2)){
				
					list_mappings.add(new MappingObjectIdentifiers(ide1, ide2));
					
				}
			}	
		}
		
		
		/*for (int ide1 : candidateMappings2ask1N.keySet()){
			for (int ide2 : candidateMappings2ask1N.get(ide1)){
				
				//we consider the equivalence only
				if (isId1SmallerThanId2(ide1, ide2)){
				
					list_mappings.add(new MappingObjectIdentifiers(ide1, ide2));
					
				}
			}	
		}*/
		
		Set<Integer> module1;
		Set<Integer> module2;
		MappingObjectIdentifiers head_mapping;
		
		int newIn=0;
		int newCons=0;
		
		LogOutput.print("Mappings 2 EXPAND: " + list_mappings.size());
		
		while (!list_mappings.isEmpty()){
			
			head_mapping = list_mappings.poll(); //retrieves and removes elements
			
			module1 = index.getScope4Identifier_Expansion(head_mapping.getIdentifierOnto1()); 
			module2 = index.getScope4Identifier_Expansion(head_mapping.getIdentifierOnto2());
			
			//LogOutput.print("Module 1: " + module1.size());
			//LogOutput.print("Module 2: " + module2.size());
			
			for (int ide1 : module1){
				for (int ide2: module2){
					
					if (!isId1SmallerThanId2(ide1, ide2))
						continue;
				
					//Ignore
					if (isaVisitedScopeMapping(ide1, ide2))
						continue;
					
					addVisistedScopeMapping(ide1, ide2);
					newCons++;
					
					
					//if true the add to expansion list
					if (evaluateCandidateMappingExpansion(ide1, ide2)){
						list_mappings.add(new MappingObjectIdentifiers(ide1, ide2));
						newIn++;
					}
					
					
				}
			}
			
		}
		
		
		list_mappings.clear();
		//printStatisticsMappingEvaluation();
		LogOutput.print("Considered: " + newCons);
		LogOutput.print("New mappings expansion: " + newIn);
		
		
	}//en anchor expansion



}

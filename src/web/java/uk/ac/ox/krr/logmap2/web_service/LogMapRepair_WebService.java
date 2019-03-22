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
package uk.ac.ox.krr.logmap2.web_service;


import uk.ac.ox.krr.logmap2.Parameters;
import uk.ac.ox.krr.logmap2.indexing.JointIndexManager;
import uk.ac.ox.krr.logmap2.indexing.OntologyProcessing;
import uk.ac.ox.krr.logmap2.io.LogOutput;
import uk.ac.ox.krr.logmap2.io.OWLAlignmentFormat;
import uk.ac.ox.krr.logmap2.io.OutPutFilesManager;
import uk.ac.ox.krr.logmap2.mappings.CandidateMappingManager;
import uk.ac.ox.krr.logmap2.mappings.objects.MappingObjectStr;
import uk.ac.ox.krr.logmap2.reasoning.SatisfiabilityIntegration;
import uk.ac.ox.krr.logmap2.repair.AnchorAssessment;
import uk.ac.ox.krr.logmap2.utilities.Utilities;
import uk.ac.ox.krr.logmap2.lexicon.LexicalUtilities;

import org.semanticweb.owlapi.model.OWLOntology;


import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;


/**
 * This class will require two OWL ontologies and a set of mappings (see <code>MappingObjectStr<code>)
 * @author Ernesto
 *
 */
public class LogMapRepair_WebService {
	
	
	private long init_global, init, fin;
	
	private OntologyProcessing onto_process1;
	private OntologyProcessing onto_process2;
	
	private JointIndexManager index;
	
	private CandidateMappingManager mapping_manager;
	
	private AnchorAssessment mapping_assessment;
	
	private OWLOntology onto1;
	private OWLOntology onto2;
	private Set<MappingObjectStr> fixed_mappings;
	private Set<MappingObjectStr> candidate_mappings;
	
	
	private Set<MappingObjectStr> clean_mappings = new HashSet<MappingObjectStr>();
	
	
	protected Map<Integer, Set<Integer>> mappings2Review_step2 = new HashMap<Integer, Set<Integer>>();
	
	private HTMLResultsFileManager progress_manager;
	
	
	
	/**
	 * Method to be called from web service
	 * @param onto1
	 * @param onto2
	 * @param fixedmappings
	 * @param mappings2review
	 */
	public LogMapRepair_WebService(
			OWLOntology onto1,
			OWLOntology onto2, 
			Set<MappingObjectStr> fixedmappings,
			Set<MappingObjectStr> mappings2review,
			String outPutFileName,
			HTMLResultsFileManager progress_manager) throws Exception{
		
		
		this.onto1 = onto1;
		this.onto2 = onto2;
		this.fixed_mappings = fixedmappings;
		this.candidate_mappings = mappings2review;
		this.progress_manager = progress_manager;
		
		//Parameters.print_output=true;
		
		try{
			
			init_global = init = Calendar.getInstance().getTimeInMillis();
		
			
			setUpStructures();
			
			//TODO It also includes assessment for properties and instances!			
			assessMappings();
			updateHTMLProgress("Diagnosis of mappings...done");
			
			//Always... at least for testing
			//Clean mapping
			//keepRepairedMappings();
			
			//TODO
			saveRepairedMappings(outPutFileName);
			
			
			
			fin = Calendar.getInstance().getTimeInMillis();
			LogOutput.printAlways("TOTAL REPAIR TIME (s): " + (float)((double)fin-(double)init_global)/1000.0);
			
		
		}
		catch (Exception e){
			LogOutput.printAlways("Error repairing mappings using LogMap repair module: " + e.getMessage());
			throw new Exception();
		}
		
		
		
		
	}
	
	
	private void updateHTMLProgress(String text_progress){
		progress_manager.updateProgress(text_progress);
	}
	
	
	
	private void addSubMapping2Mappings2Review(int index1, int index2){
		
		if (!mappings2Review_step2.containsKey(index1)){
			mappings2Review_step2.put(index1, new HashSet<Integer>());
		}
		mappings2Review_step2.get(index1).add(index2);
	}
	
	

	
	
	/**
	 * We add mapping to structures in mapping_manager
	 */
	private void addMapping2Structures(){
		
		
		mappings2Review_step2.clear();
		
		
		//Fixed mappings can only be classes (so far)
		for (MappingObjectStr map : fixed_mappings){
				
			if (map.getTypeOfMapping()==Utilities.CLASSES) {
				
				addClassMapping(map, true);
				
			}
		}
		
		//Class mappings from user interaction and property/instance mappings
		for (MappingObjectStr map : candidate_mappings){
			
			if (map.getTypeOfMapping()==Utilities.CLASSES) {
				
				addClassMapping(map, false);
				
			}
			
			else if (map.getTypeOfMapping()==Utilities.OBJECTPROPERTIES) {
					
				addObjectPropertyMapping(map);
				
			}
			else if (map.getTypeOfMapping()==Utilities.DATAPROPERTIES) {
				
				addDataPropertyMapping(map);
				
			}
			
			else if (map.getTypeOfMapping()==Utilities.INSTANCES) {
					
				addInstanceMapping(map);			
					
			}
			else {
				//Do nothing			
			}
			
			
		}
		

		
		
		LogOutput.printAlways("Numb of reliable mappings: " + num_anchors);
		LogOutput.printAlways("Numb of other mappings: " + num_mappings2review);
		
		
		
		
	}
	
	
	
	
	private void setUpStructures() throws Exception{
		
		
		//try{
		
				
		LogOutput.showOutpuLog(false);
		
		//init = Calendar.getInstance().getTimeInMillis();
		//PrecomputeIndexCombination.preComputeIdentifierCombination();
		//fin = Calendar.getInstance().getTimeInMillis();
		//LogOutput.print("Time precomputing index combinations (s): " + (float)((double)fin-(double)init)/1000.0);
		
		
		//Create Index and new Ontology Index...
		index = new JointIndexManager();
		
		
		//No overlapping
		onto_process1 = new OntologyProcessing(onto1, index, new LexicalUtilities());
		onto_process2 = new OntologyProcessing(onto2, index, new LexicalUtilities());
		
		
		mapping_manager = new CandidateMappingManager(index, onto_process1, onto_process2);
		
		
		
		//Extracts lexicon
		init = Calendar.getInstance().getTimeInMillis();
		onto_process1.precessLexicon(false);
		onto_process2.precessLexicon(false);
		fin = Calendar.getInstance().getTimeInMillis();
		LogOutput.printAlways("Time indexing entities (s): " + (float)((double)fin-(double)init)/1000.0);

		
		//We add mappings to structures
		addMapping2Structures();
		
				
		
		//Extracts Taxonomy
		//Also extracts A^B->C
		init = Calendar.getInstance().getTimeInMillis();
		onto_process1.setTaxonomicData();
		onto_process2.setTaxonomicData();
		fin = Calendar.getInstance().getTimeInMillis();
		LogOutput.printAlways("Time extracting structural information (s): " + (float)((double)fin-(double)init)/1000.0);
				
			
	
		
		onto_process1.clearReasoner();
		onto_process1.getClass2Identifier().clear();

		onto_process2.clearReasoner();
		onto_process2.getClass2Identifier().clear();
		//}
		//catch (Exception e){
		//	e.printStackTrace();
		//}
			
		
	}
	
	
	
	
	private void assessMappings(){
		
		//CLASS MAPPING ASSESSMENT
		//We have reliable and rest of candidates
		assessClassMappings2steps(); //we split between reliable and other mappings
		
		
		
		//Clean property mappings and individual mappings
		//--------------------------------
		
		//Assess Property mappings: using index
		if (mapping_manager.getDataPropertyAnchors().size() >0 || mapping_manager.getObjectPropertyAnchors().size() > 0) {
			init = Calendar.getInstance().getTimeInMillis();
			mapping_manager.evaluateCompatibilityDataPropertyMappings();
			mapping_manager.evaluateCompatibilityObjectPropertyMappings();
			fin = Calendar.getInstance().getTimeInMillis();
			LogOutput.printAlways("\tTime assessing property mappings (s): " + (float)((double)fin-(double)init)/1000.0);		
		}
		
		
		
		//Asses Individuals index
		if (mapping_manager.getInstanceMappings().size()>0){
		
			init = Calendar.getInstance().getTimeInMillis();
			mapping_manager.evaluateCompatibilityInstanceMappings();
			
			
			//Asses Individuals D&G
			if (mapping_manager.getInstanceMappings().size()>0){
						
				init = Calendar.getInstance().getTimeInMillis();
				
				//We have an specific method since there is not a top-down search. And we first repair classes
				mapping_assessment.CheckSatisfiabilityOfIntegration_DandG_Individuals(
						mapping_manager.getInstanceMappings());
				
				fin = Calendar.getInstance().getTimeInMillis();
				LogOutput.printAlways("Time cleaning instance mappings D&G (s): " + (float)((double)fin-(double)init)/1000.0);
			}
			
			fin = Calendar.getInstance().getTimeInMillis();
			LogOutput.printAlways("\tTime assessing instance mappings (s): " + (float)((double)fin-(double)init)/1000.0);
		}
		
		
	}
	
	/**
	 * Clean first the identified anchors and thes clean the rest of candidates
	 */
	private void assessClassMappings2steps(){
		
		
		int discarded_with_index=0;
		
		mapping_assessment = new AnchorAssessment(index, mapping_manager);
		
		//TODO uncomments for general behaviour
		//For SNOMED-NCI cases we need an approximation and for other cases as well
		//init = Calendar.getInstance().getTimeInMillis();
		//mapping_assessment.CountSatisfiabilityOfIntegration_DandG(mapping_manager.getAnchors());
		//fin = Calendar.getInstance().getTimeInMillis();
		//LogOutput.print("\tTime counting unsat Dowling and Gallier (s): " + (float)((double)fin-(double)init)/1000.0);
		
		
		init = Calendar.getInstance().getTimeInMillis();
		mapping_assessment.CheckSatisfiabilityOfIntegration_DandG(mapping_manager.getLogMapMappings());
		fin = Calendar.getInstance().getTimeInMillis();
		LogOutput.printAlways("\tTime cleaning reliable class mappings Dowling and Gallier (s): " + (float)((double)fin-(double)init)/1000.0);
		LogOutput.printAlways("\tRepaired Root Unsat using Dowling and Gallier (aproximation): " + mapping_assessment.getNumRepairedUnsatClasses());
		
		//After repairing exact
		mapping_manager.setExactAsFixed(true);
		
		
		//Interval labelling schema and cleaning of mappings 2 review against anchors
		//------------------------------------------------------------------------------
		try {			
			
			init = Calendar.getInstance().getTimeInMillis();
			
			//Index already have the necessary taxonomical information apart from the equiv mappings
			index.setIntervalLabellingIndex(mapping_manager.getFixedMappings());
			index.clearAuxStructuresforLabellingSchema();
			
			fin = Calendar.getInstance().getTimeInMillis();
			LogOutput.printAlways("\tTime indexing hierarchy + anchors (ILS) (s): " + (float)((double)fin-(double)init)/1000.0);
			
			
			//Asses mappings to review
			for (int ide1 : mappings2Review_step2.keySet()){
				for (int ide2 : mappings2Review_step2.get(ide1)){
					
					if (index.areDisjoint(ide1, ide2)) {
					//if (mapping_manager.isMappingInConflictWithFixedMappings(ide1, ide2)){
						discarded_with_index++;
					}
					else {
						mapping_manager.addSubMapping2Mappings2Review(ide1, ide2);						
					}
				}				
			}
			
			LogOutput.printAlways("Discarded with index: " + discarded_with_index);
			
			
		}
		catch (Exception e){
			LogOutput.printAlways("Error creating Interval Labelling index 1: " + e.getMessage());
			e.printStackTrace();
		}
		
		if (mapping_manager.getMappings2Review().size()>0){
		
			//Clean D&G mappings 2 review
			init = Calendar.getInstance().getTimeInMillis();
			mapping_assessment.CheckSatisfiabilityOfIntegration_DandG(mapping_manager.getMappings2Review());  //With Fixed mappings!
			fin = Calendar.getInstance().getTimeInMillis();
			LogOutput.printAlways("Time cleaning rest of the mappings using D&G (s): " + (float)((double)fin-(double)init)/1000.0);
			LogOutput.printAlways("\tRepaired Root Unsat using Dowling and Gallier 2 (aproximation): " + mapping_assessment.getNumRepairedUnsatClasses());
			
			//Move clean to anchors
			mapping_manager.moveMappingsToReview2AnchorList();
			
			
			//Interval labelling index with try block with all clean mappings
			//------------------------------
			try{			
				init = Calendar.getInstance().getTimeInMillis();
				index.setIntervalLabellingIndex(mapping_manager.getLogMapMappings());//It also contains mappings 2 review
				index.clearAuxStructuresforLabellingSchema();
				fin = Calendar.getInstance().getTimeInMillis();
				LogOutput.printAlways("Time indexing hierarchy + anchors and candidates I (ILS) (s): " + (float)((double)fin-(double)init)/1000.0);
			}
			catch (Exception e){
				LogOutput.printAlways("Error creating Interval Labelling index 2: " + e.getMessage());
				e.printStackTrace();
			}
		}
		
		
		//Add weakened
		for (int ide1 : mapping_manager.getWeakenedDandGMappings().keySet()){
			
			for (int ide2 : mapping_manager.getWeakenedDandGMappings().get(ide1)){
				
				if (!mapping_manager.isMappingInConflictWithFixedMappings(ide1, ide2)){
					
					mapping_manager.addSubMapping2ListOfAnchors(ide1, ide2);
					
				}
			}
		}
		
		
		//Repair all just in case
		///--------------------------
		mapping_manager.setExactAsFixed(false);
		
		init = Calendar.getInstance().getTimeInMillis();
		mapping_assessment.CheckSatisfiabilityOfIntegration_DandG(mapping_manager.getLogMapMappings());
		fin = Calendar.getInstance().getTimeInMillis();
		LogOutput.printAlways("\tTime cleaning ALL class mappings Dowling and Gallier (s): " + (float)((double)fin-(double)init)/1000.0);
		LogOutput.printAlways("\tRepaired Root Unsat using Dowling and Gallier 3 (aproximation): " + mapping_assessment.getNumRepairedUnsatClasses());
		
		//New weakened ar not added
				
		
		
	}
	
	

	
	
	int num_anchors=0;
	int num_mappings2review=0;
		
	/**
	 * Adds mappings to structures
	 * @param map
	 */
	private void addClassMapping(MappingObjectStr map, boolean fixed){
		
		
		
		int ide1;
		int ide2;
		
		
				
		//Translate from mapping 2 index
		ide1=onto_process1.getIdentifier4ConceptName(Utilities.getEntityLabelFromURI(map.getIRIStrEnt1()));
		ide2=onto_process2.getIdentifier4ConceptName(Utilities.getEntityLabelFromURI(map.getIRIStrEnt2()));
		
		
		//We only consider classes
		if (ide1<0 || ide2<0){
			LogOutput.print("Classes not found in ontology.");
			LogOutput.print("\t" + ide1 + "  " + map.getIRIStrEnt1());
			LogOutput.print("\t" + ide2 + "  " + map.getIRIStrEnt2());
			return;
		}
		
		
		mapping_manager.addIsub2Structure(ide1, ide2, map.getConfidence());
		mapping_manager.addIsub2Structure(ide2, ide1, map.getConfidence());
		
		
	
		if (fixed){
			
			num_anchors++;				
			
			if (map.getMappingDirection()==Utilities.EQ){
				mapping_manager.addSubMapping2ListOfAnchors(ide1, ide2);
				mapping_manager.addSubMapping2ListOfAnchors(ide2, ide1);
				
			}
			else if (map.getMappingDirection()==Utilities.L2R){
				mapping_manager.addSubMapping2ListOfAnchors(ide1, ide2);
				
			}
			else{
				mapping_manager.addSubMapping2ListOfAnchors(ide2, ide1);
				
			}
			
		}
		else { //Candidate 2 review
			
			num_mappings2review++;
			
			if (map.getMappingDirection()==Utilities.EQ){
				addSubMapping2Mappings2Review(ide1, ide2); //local method
				addSubMapping2Mappings2Review(ide2, ide1);
				
			}
			else if (map.getMappingDirection()==Utilities.L2R){
				addSubMapping2Mappings2Review(ide1, ide2);				
			}
			else{				
				addSubMapping2Mappings2Review(ide2, ide1);
			}
			
		}
		
	}
	
	
	private void addObjectPropertyMapping(MappingObjectStr map){
		
		int ide1;
		int ide2;
		
		//Translate from mapping 2 index
		ide1=onto_process1.getIdentifier4ObjectPropName(Utilities.getEntityLabelFromURI(map.getIRIStrEnt1()));
		ide2=onto_process2.getIdentifier4ObjectPropName(Utilities.getEntityLabelFromURI(map.getIRIStrEnt2()));
		
		
		//We only consider classes
		if (ide1<0 || ide2<0){
			LogOutput.print("Object properties not found in ontology.");
			LogOutput.print("\t" + ide1 + "  " + map.getIRIStrEnt1());
			LogOutput.print("\t" + ide2 + "  " + map.getIRIStrEnt2());
			return;
		}
		
		//So far only equivalences are considered
		//if (map.getMappingDirection()==Utilities.EQ){
			mapping_manager.addObjectPropertyAnchor(ide1, ide2);
			mapping_manager.addObjectPropertyAnchorConfidence(ide1, map.getConfidence());
		//}		
	}
	
	
	
	
	
	
	
	private void addDataPropertyMapping(MappingObjectStr map){
		
		int ide1;
		int ide2;
		
		//Translate from mapping 2 index
		ide1=onto_process1.getIdentifier4DataPropName(Utilities.getEntityLabelFromURI(map.getIRIStrEnt1()));
		ide2=onto_process2.getIdentifier4DataPropName(Utilities.getEntityLabelFromURI(map.getIRIStrEnt2()));
		
		
		//We only consider classes
		if (ide1<0 || ide2<0){
			LogOutput.print("Data properties not found in ontology.");
			LogOutput.print("\t" + ide1 + "  " + map.getIRIStrEnt1());
			LogOutput.print("\t" + ide2 + "  " + map.getIRIStrEnt2());
			return;
		}
		
		//So far only equivalences are considered
		//if (map.getMappingDirection()==Utilities.EQ){
			mapping_manager.addDataPropertyAnchor(ide1, ide2);
			mapping_manager.addDataPropertyAnchorConfidence(ide1, map.getConfidence());
		//}		
		
		
	}
	
	
	private void addInstanceMapping(MappingObjectStr map){
		
		
		int ide1;
		int ide2;
		
		//Translate from mapping 2 index
		ide1=onto_process1.getIdentifier4InstanceName(Utilities.getEntityLabelFromURI(map.getIRIStrEnt1()));
		ide2=onto_process2.getIdentifier4InstanceName(Utilities.getEntityLabelFromURI(map.getIRIStrEnt2()));
		
		
		//We only consider classes
		if (ide1<0 || ide2<0){
			LogOutput.print("Individuals not found in ontology.");
			LogOutput.print("\t" + ide1 + "  " + map.getIRIStrEnt1());
			LogOutput.print("\t" + ide2 + "  " + map.getIRIStrEnt2());
			return;
		}
		
		//So far only equivalences are considered
		//if (map.getMappingDirection()==Utilities.EQ){
			mapping_manager.addInstanceMapping(ide1, ide2);			
			mapping_manager.addInstanceAnchorConfidence(ide1, ide2, map.getConfidence());
		//}		
		
		
	}
	
	
	
	
	
	
	
	private void saveRepairedMappings(String outPutFileName){
		
		int dirMapping;
		
		OutPutFilesManager outPutFilesManager = new OutPutFilesManager();
		
		int num_clean_mappings=0;
		int num_clean_class_mappings=0;
		int num_clean_dprop_mappings=0;
		int num_clean_prop_mappings=0;
		int num_clean_oprop_mappings=0;
		int num_clean_instance_mappings=0;
		
		try {
			outPutFilesManager.createOutFiles(
					//logmap_mappings_path + "Output/mappings",
					//path + "/" + file_name,
					//outPutFileName + "/" + "repaired_mappings",
					outPutFileName,
					OutPutFilesManager.AllFormats,
					onto_process1.getOntoIRI(),
					onto_process1.getOntoIRI());
			
			//if (Parameters.output_class_mappings){
			
			for (int idea : mapping_manager.getLogMapMappings().keySet()){
				for (int ideb : mapping_manager.getLogMapMappings().get(idea)){
						
						//This is important to keep compatibility with OAEI and Flat alignment formats
						//The order of mappings is important
						//For OWL output would be the same since mappings are axioms
						if (mapping_manager.isId1SmallerThanId2(idea, ideb)){
							
							if (mapping_manager.isMappingAlreadyInList(ideb, idea)){
								dirMapping=Utilities.EQ;
							}
							else {
								dirMapping=Utilities.L2R;
							}
							
							num_clean_mappings++;
							num_clean_class_mappings++;
							
							outPutFilesManager.addClassMapping2Files(
									index.getIRIStr4ConceptIndex(idea),
									index.getIRIStr4ConceptIndex(ideb),
									dirMapping, 
									mapping_manager.getConfidence4Mapping(idea, ideb));
						}
						else {
							if (mapping_manager.isMappingAlreadyInList(ideb, idea)){
								//Do nothing
							}
							else {
								
								num_clean_mappings++;
								num_clean_class_mappings++;
								
								outPutFilesManager.addClassMapping2Files(
										index.getIRIStr4ConceptIndex(ideb),
										index.getIRIStr4ConceptIndex(idea),
										Utilities.R2L, 
										mapping_manager.getConfidence4Mapping(idea, ideb));
							}
						}
					
						
				}
			}
			//}
			
			//if (Parameters.output_prop_mappings){
			
			for (int ide1 : mapping_manager.getDataPropertyAnchors().keySet()){		
				
				num_clean_mappings++;
				num_clean_dprop_mappings++;
				num_clean_prop_mappings++;
				
				outPutFilesManager.addDataPropMapping2Files(
							index.getIRIStr4DataPropIndex(ide1),
							index.getIRIStr4DataPropIndex(mapping_manager.getDataPropertyAnchors().get(ide1)),
							Utilities.EQ,  
							mapping_manager.getConfidence4DataPropertyAnchor(ide1, mapping_manager.getDataPropertyAnchors().get(ide1))//1.0
						);
			}
				
			for (int ide1 : mapping_manager.getObjectPropertyAnchors().keySet()){
				
				num_clean_mappings++;
				num_clean_oprop_mappings++;
				num_clean_prop_mappings++;
				
				outPutFilesManager.addObjPropMapping2Files(
							index.getIRIStr4ObjPropIndex(ide1),
							index.getIRIStr4ObjPropIndex(mapping_manager.getObjectPropertyAnchors().get(ide1)),
							Utilities.EQ, 
							mapping_manager.getConfidence4ObjectPropertyAnchor(ide1, mapping_manager.getObjectPropertyAnchors().get(ide1))//1.0
						);
			}
			//}
			
			

			//if (Parameters.perform_instance_matching && Parameters.output_instance_mappings){
				
			for (int ide1 : mapping_manager.getInstanceMappings().keySet()){
				for (int ide2 : mapping_manager.getInstanceMappings().get(ide1)){
				
					num_clean_mappings++;
					num_clean_instance_mappings++;
					
					outPutFilesManager.addInstanceMapping2Files(
								index.getIRIStr4IndividualIndex(ide1), 
								index.getIRIStr4IndividualIndex(ide2), 
								mapping_manager.getConfidence4InstanceMapping(ide1, ide2)
							);
						
				}
				
			}
			//}
			
			
			String iri_mappings_owl = progress_manager.getURIMappingsOWL();
			String iri_mappings_txt = progress_manager.getURIMappingsTXT();
			String iri_mappings_rdf = progress_manager.getURIMappingsRDF();
			
			updateHTMLProgress("Output mappings (class mappings = " + num_clean_class_mappings + 
					", property mappings = " + num_clean_prop_mappings + ", instance mappings = " + num_clean_instance_mappings + "): " + 
					"<a href=\"" + iri_mappings_owl +  "\">[OWL format]</a>, " + 
					"<a href=\"" + iri_mappings_txt + "\">[TXT format]</a>, " + 
					"<a href=\"" + iri_mappings_rdf + "\">[OAEI Alignment format]</a>.");
			
			
			
			//mapping_manager.setStringAnchors();
			LogOutput.printAlways("Num repaired mappings: " + num_clean_mappings);
			LogOutput.printAlways("\tNum repaired class mappings: " + num_clean_class_mappings);
			LogOutput.printAlways("\tNum repaired object property mappings: " + num_clean_oprop_mappings);
			LogOutput.printAlways("\tNum repaired data property mappings: " + num_clean_dprop_mappings);			
			LogOutput.printAlways("\tNum repaired instance mappings: " + num_clean_instance_mappings);
			
			outPutFilesManager.closeAndSaveFiles();
			
			
		}
		catch (Exception e){
			System.err.println("Error saving mappings...");
			e.printStackTrace();
		}
		
		
	}
	
	
	
	/**
	 * Returns the set of mappings that have been repaired using LogMap's repair facility
	 * @return
	 */
	public Set<MappingObjectStr> getCleanMappings(){
		return clean_mappings;
	}
	
	
	
	private void checkSatisfiabilityCleanMappings() throws Exception {
		checkSatisfiabilityMappings(clean_mappings);
	}
	
	
	private void checkSatisfiabilityMappings(Set<MappingObjectStr> mappings) throws Exception {
		
		OWLOntology mappins_owl_onto = getOWLOntology4CleanMappings(mappings);
		
		
		SatisfiabilityIntegration.setTimeoutClassSatisfiabilityCheck(60);
		
		
		SatisfiabilityIntegration sat_checker = new SatisfiabilityIntegration(
				onto1, 
				onto2,
				mappins_owl_onto,
				true,//class sat
				true,//Time_Out_Class
				false); //use factory
		
		
		LogOutput.print("Num unsat classes lead by repaired mappings using LogMap: " + sat_checker.getNumUnsatClasses());
		
	}
	
	
	/**
	 * Returns the clean mappings as an OWLOntology object.
	 * @return
	 * @throws Exception
	 */
	public OWLOntology getOWLOntology4CleanMappings(Set<MappingObjectStr> mappings) throws Exception {
		
		OWLAlignmentFormat owlformat = new OWLAlignmentFormat("");
		
		
		for (MappingObjectStr mapping : mappings){
			
			
			if (mapping.getTypeOfMapping() == Utilities.INSTANCE){
				
				owlformat.addInstanceMapping2Output(
						mapping.getIRIStrEnt1(),
						mapping.getIRIStrEnt2(),						
						mapping.getConfidence());				
			}
			
			
			else if (mapping.getTypeOfMapping() == Utilities.CLASSES){
				
				
				owlformat.addClassMapping2Output(
						mapping.getIRIStrEnt1(),
						mapping.getIRIStrEnt2(),
						mapping.getMappingDirection(),
						mapping.getConfidence());
			}
			
			else if (mapping.getTypeOfMapping() == Utilities.OBJECTPROPERTIES){
				
				owlformat.addObjPropMapping2Output(
						mapping.getIRIStrEnt1(),
						mapping.getIRIStrEnt2(),
						mapping.getMappingDirection(),
						mapping.getConfidence());
			}
			
			else if (mapping.getTypeOfMapping() == Utilities.DATAPROPERTIES){
				
				owlformat.addDataPropMapping2Output(
						mapping.getIRIStrEnt1(),
						mapping.getIRIStrEnt2(),
						mapping.getMappingDirection(),
						mapping.getConfidence());
				
			}
			
			
		}//end for mappings
		

		return owlformat.getOWLOntology();
		
		
	}
	
	
	
	
	
	
	
	private void keepRepairedMappings(){
		
		int dirMapping;
		
		int num_clean_mappings=0;
		int num_clean_class_mappings=0;
		int num_clean_dprop_mappings=0;
		int num_clean_oprop_mappings=0;
		int num_clean_instance_mappings=0;
		
		clean_mappings.clear();
		
		try {
			
			for (int idea : mapping_manager.getLogMapMappings().keySet()){
				for (int ideb : mapping_manager.getLogMapMappings().get(idea)){
						
						//This is important to keep compatibility with OAEI and Flat alignment formats
						//The order of mappings is important
						//For OWL output would be the same since mappings are axioms
						if (mapping_manager.isId1SmallerThanId2(idea, ideb)){
							
							if (mapping_manager.isMappingAlreadyInList(ideb, idea)){
								dirMapping=Utilities.EQ;
							}
							else {
								dirMapping=Utilities.L2R;
							}
							
							num_clean_mappings++;
							num_clean_class_mappings++;
							
							clean_mappings.add(
									new MappingObjectStr(
											index.getIRIStr4ConceptIndex(idea), 
											index.getIRIStr4ConceptIndex(ideb), 
											mapping_manager.getConfidence4Mapping(idea, ideb), 
											dirMapping,
											Utilities.CLASSES));
							
						}
						else {
							if (mapping_manager.isMappingAlreadyInList(ideb, idea)){
								//Do nothing
							}
							else {
								
								num_clean_mappings++;
								num_clean_class_mappings++;
								
								clean_mappings.add(
										new MappingObjectStr(
												index.getIRIStr4ConceptIndex(ideb),
												index.getIRIStr4ConceptIndex(idea), 
												mapping_manager.getConfidence4Mapping(idea, ideb), 
												Utilities.R2L,
												Utilities.CLASSES));
							}
						}
					
						
				}
			}
			//}
			
			//if (Parameters.output_prop_mappings){
			
			for (int ide1 : mapping_manager.getDataPropertyAnchors().keySet()){		
				
				num_clean_mappings++;
				num_clean_dprop_mappings++;
				
				
				
				clean_mappings.add(
						new MappingObjectStr(
								index.getIRIStr4ConceptIndex(ide1), 
								index.getIRIStr4DataPropIndex(mapping_manager.getDataPropertyAnchors().get(ide1)), 
								mapping_manager.getConfidence4DataPropertyAnchor(ide1, mapping_manager.getDataPropertyAnchors().get(ide1)), 
								Utilities.EQ,
								Utilities.DATAPROPERTIES));
				
				
			}
				
			for (int ide1 : mapping_manager.getObjectPropertyAnchors().keySet()){
				
				num_clean_mappings++;
				num_clean_oprop_mappings++;
				
				clean_mappings.add(
						new MappingObjectStr(
								index.getIRIStr4ObjPropIndex(ide1),
								index.getIRIStr4ObjPropIndex(mapping_manager.getObjectPropertyAnchors().get(ide1)),								 
								mapping_manager.getConfidence4ObjectPropertyAnchor(ide1, mapping_manager.getObjectPropertyAnchors().get(ide1)),
								Utilities.EQ,
								Utilities.OBJECTPROPERTIES));
			}
			//}
			
			

			//if (Parameters.perform_instance_matching && Parameters.output_instance_mappings){
				
			for (int ide1 : mapping_manager.getInstanceMappings().keySet()){
				for (int ide2 : mapping_manager.getInstanceMappings().get(ide1)){
				
					num_clean_mappings++;
					num_clean_instance_mappings++;
					
					clean_mappings.add(
							new MappingObjectStr(
									index.getIRIStr4IndividualIndex(ide1), 
									index.getIRIStr4IndividualIndex(ide2), 
									mapping_manager.getConfidence4InstanceMapping(ide1, ide2),
									Utilities.EQ,
									Utilities.INSTANCES));
						
				}
				
			}
			//}
			
			
			//mapping_manager.setStringAnchors();
			//TODO uncomment
			/*LogOutput.print("\tNum clean mappings: " + num_clean_mappings);
			LogOutput.print("\t\tNum clean class mappings: " + num_clean_class_mappings);
			LogOutput.print("\t\tNum clean object property mappings: " + num_clean_oprop_mappings);
			LogOutput.print("\t\tNum clean data property mappings: " + num_clean_dprop_mappings);			
			LogOutput.print("\t\tNum clean instance mappings: " + num_clean_instance_mappings);
			*/
			
			
			
		}
		catch (Exception e){
			System.err.println("Error keeping mappings...");
			e.printStackTrace();
		}
		
		
	}
	
	
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
			
		
		
		
		

	}
		
	
	

	
	
	
	
	

}





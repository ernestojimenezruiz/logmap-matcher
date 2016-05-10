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
package uk.ac.ox.krr.logmap2;

import java.net.URL;

import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import uk.ac.ox.krr.logmap2.mappings.objects.MappingObjectStr;
import uk.ac.ox.krr.logmap2.oaei.Oraculo;
import uk.ac.ox.krr.logmap2.oaei.reader.RDFAlignReader;
import uk.ac.ox.krr.logmap2.owlapi.SynchronizedOWLManager;
import uk.ac.ox.krr.logmap2.reasoning.ReasonerManager;
import uk.ac.ox.krr.logmap2.reasoning.SatisfiabilityIntegration;
import uk.ac.ox.krr.logmap2.statistics.StatisticsTimeMappings;
import uk.ac.ox.krr.logmap2.utilities.Utilities;
import uk.ac.ox.krr.logmap2.bioportal.MediatingOntologyExtractor;
import uk.ac.ox.krr.logmap2.io.LogOutput;
import uk.ac.ox.krr.logmap2.io.OAEIAlignmentOutput;
import uk.ac.ox.krr.logmap2.io.OWLAlignmentFormat;

/**
 * This classes manages the required wrapper of LogMap 2 in order to be accept and provided the required input and output data. 
 * @author root
 *
 */
public class LogMap2_OAEI_BioPortal {

	OAEIAlignmentOutput alignment_output;
	
	MediatingOntologyExtractor MO_extractor;
	
	OWLOntology O1;
	OWLOntology O2;
	OWLOntology MO;
	
	String str_uri_onto1;
	String str_uri_onto2;
	
	LogMap2_Matcher logmap2;
	
	Set<MappingObjectStr> mappings = new HashSet<MappingObjectStr>();
	
	//voted mappings in composition
	Map<MappingObjectStr, Integer> mappings2votes = new HashMap<MappingObjectStr, Integer>();
	
	
	
	private final int MIN_VOTES=2;
	
	
	
	public LogMap2_OAEI_BioPortal(){
		
		//LogOutput.showOutpuLog(false);
		//Oraculo.unsetStatusOraculo();
				
		//TODO call logmap matcher and use MO test in LogMap Bioportal project...
		//1. tests that the relevant/representative labels are ok (Done)
		//2. Check extraction of MO for mouse and largebio (Done)
		//3. Evaluate download times... (Done)
		//4. Since many MOs 1-5 clear structures if possible!! (Done)
		//5. Test P&R (Done)
		//6. Unsat (Done)
		
	}
	
	
	public void align(URL source, URL target) throws Exception{
		
		align(source.toURI().toString(), target.toURI().toString());
		
		
	}
	
	
	public void align(String source_iri, String target_iri) throws Exception{
		
		//It does not work here since it is also used inside LogMap calls
		//StatisticsTimeMappings.setInitGlobalTime();
		long init_global = StatisticsTimeMappings.getCurrentTimeInMillis();
		
		//TODO  No interactive mode!
		Parameters.allow_interactivity=false;
		Oraculo.allowOracle(Parameters.allow_interactivity);						
		//Read from parameters file done inside calls to LogMap
		
		
		//0. Load input ontologies
		//---------------------------------------
		O1 = loadOWLOntology(source_iri);
		O2 = loadOWLOntology(target_iri);
		
		
		//1 (composed). We map the input ontologies ontologies (only basic lexical matching to get representative labels)		
		//---------------------------------------
		/*StatisticsTimeMappings.setCurrentInitTime();
		//Structures are clean inside LogMapMatcher
		//No need to repair
		Parameters.cleanD_G=false;
		LogMap2_Matcher logmap2_exact = new LogMap2_Matcher(O1, O2, true);
		str_uri_onto1 = logmap2_exact.getIRIOntology1();
		str_uri_onto2 = logmap2_exact.getIRIOntology2();		
		print("Time getting exact matching with input ontologies O1 and O2 (s): " + StatisticsTimeMappings.getRunningTime());
		print("Representative elements: " + logmap2_exact.getRepresentativeLabelsForMappings().size());
		*/
		
		//1. Perform LogMap matching using (no composed mappings as input)
		//---------------------------------------
		//Mappings between O1 and O2 will be considered as the same level as composed mappings 
		StatisticsTimeMappings.setCurrentInitTime();
		//Parameters.ratio_second_chance_discarded=100;
		logmap2 = new LogMap2_Matcher(O1, O2);
		//for (MappingObjectStr mapping : logmap2.getLogmap2_Mappings()){
			//addVote2Mappings(mapping, 2);//we start with vote 1 to guarantee they are in the output	
		//}
		getPrecisionRecall(logmap2.getLogmap2_Mappings());
		//getPrecisionRecall(mappings2votes.keySet());
		print("Representative elements: " + logmap2.getRepresentativeLabelsForMappings().size());
		print("Time performing matching with input ontologies O1 and O2 (s): " + StatisticsTimeMappings.getRunningTime());
		
		
		Parameters.ratio_second_chance_discarded=5;
		
		
		//2. Extract mediating ontologies
		//---------------------------------------
		print("Extracting up to " + Parameters.max_mediating_ontologies + " mediating ontologies from BioPortal.");
		StatisticsTimeMappings.setCurrentInitTime();
		MO_extractor = 
				new MediatingOntologyExtractor(logmap2.getRepresentativeLabelsForMappings());
		
		print("Time extracting Mediating Ontologies (s): " + StatisticsTimeMappings.getRunningTime());
		//print("Necessary number of calls to BioPortal: " + MO_extractor.getNumCallsBioPortal());
		
		//We make sure we repair
		Parameters.cleanD_G=true;
		
		
		//3. Perform matching with mediating ontology and get composed mappings		
		//---------------------------------------
		int num_used_MO = 0;
		
		for (String ontoAcronym : MO_extractor.getSelectedMediatingOntologies()){
			
			StatisticsTimeMappings.setCurrentInitTime();
			//It also updates the mapping2votes inside
			Set<MappingObjectStr> composed_mappings = performComposedMatchingWithMediatingOntology(ontoAcronym);
			
			print("\nTime performing composed matching with MO '" + ontoAcronym +"' (s): " + StatisticsTimeMappings.getRunningTime());
			print("Composed mappings: " + composed_mappings.size());
			getPrecisionRecall(composed_mappings);
			getPrecisionRecall(mappings2votes.keySet());
			
			//We perform MO matching with a asubset of the selected ontologies and for those contribute to the composed mappings
			if (composed_mappings.size()>0)
				num_used_MO++;
			if (num_used_MO>=MO_extractor.MAX_MO_FOR_MATCHING)
				break;
		}
		
		
		
				
		
		print("\nResults Composed mappings: ");
		getComposedMappings(1);
		getComposedMappings(2);
		getComposedMappings(3);
		getComposedMappings(4);
		getComposedMappings(5);
		getComposedMappings();
		getComposedMappings2();
		getComposedMappings3();
		getComposedMappings4();
		print("\n");
		
		//4 (composed). Perform LogMap matching using composed mappings as input
		/*StatisticsTimeMappings.setCurrentInitTime();
		Parameters.ratio_second_chance_discarded=100;
		LogMap2_Matcher logmap2 = new LogMap2_Matcher(O1, O2, getComposedMappings2());
		mappings.addAll(logmap2.getLogmap2_Mappings());
		print("Time performing matching with input ontologies O1 and O2 and composed mappings (s): " + StatisticsTimeMappings.getRunningTime());
		*/
		
		
		//5. Perform mapping repair -> not necessary if 4
		
		StatisticsTimeMappings.setCurrentInitTime();		
		//LogMap2_RepairFacility logmap_repair = new LogMap2_RepairFacility(O1, O2, getComposedMappings2(), true);
		//LogMap2_RepairFacility logmap_repair = new LogMap2_RepairFacility(O1, O2, logmap2.getLogmap2_Mappings(), getComposedMappings2());
		LogMap3_RepairFacility logmap_repair = new LogMap3_RepairFacility(O1, O2, logmap2.getLogmap2_Mappings(), getComposedMappings4());
		
		mappings.addAll(logmap_repair.getCleanMappings());
		print("\nTime performing mapping repir (s): " + StatisticsTimeMappings.getRunningTime());
		

		//5. Check unsatisfiability
		//checkSatisfiabilityMappings();

		
		
		//print("Number of original computed mappings: " + logmap2_exact.getLogmap2_Mappings().size());
		print("Number of total computed mappings: " + mappings.size());
		getPrecisionRecall(mappings);
		print("LogMap 2 (with BioPortal) total matching time (s): " + StatisticsTimeMappings.getRunningTime(init_global));
		
		
		
	}
	
	
	public URL returnAlignmentFile() throws Exception{

		
		alignment_output = new OAEIAlignmentOutput("alignment", str_uri_onto1, str_uri_onto2);
		
		int dir_mapping;
		
		for (MappingObjectStr mapping : mappings){
			
			if (Parameters.output_class_mappings && mapping.isClassMapping()){

				dir_mapping = mapping.getMappingDirection();
				
				//GSs in OAIE only contains, in general, equivalence mappings
				//For test only
				if (Parameters.output_equivalences_only)
					dir_mapping=Utilities.EQ;
				
				alignment_output.addClassMapping2Output(
						mapping.getIRIStrEnt1(),
						mapping.getIRIStrEnt2(),
						dir_mapping,
						mapping.getConfidence()
						);
				
			}
			
			if (Parameters.output_prop_mappings){
				
				if (mapping.isDataPropertyMapping()){

					dir_mapping = mapping.getMappingDirection();
										
					alignment_output.addDataPropMapping2Output(
							mapping.getIRIStrEnt1(),
							mapping.getIRIStrEnt2(),
							dir_mapping,
							mapping.getConfidence()
							);
				}
					
				if (mapping.isObjectPropertyMapping()){

					dir_mapping = mapping.getMappingDirection();
											
					alignment_output.addObjPropMapping2Output(
							mapping.getIRIStrEnt1(),
							mapping.getIRIStrEnt2(),
							dir_mapping,
							mapping.getConfidence()
							);
				}				
				
			}
			
			
			
			if (Parameters.perform_instance_matching && Parameters.output_instance_mappings && mapping.isInstanceMapping()){

				dir_mapping = mapping.getMappingDirection();
				
				alignment_output.addInstanceMapping2Output(
						mapping.getIRIStrEnt1(),
						mapping.getIRIStrEnt2(),
						mapping.getConfidence()
						);
				
			}
			
		}
		
		
		
		
		
		alignment_output.saveOutputFile();
		
		//Called already in LgMap matcher
		///logmap2.clearIndexStructures();
		
		return alignment_output.returnAlignmentFile();
		
	}
	
	
	/**
	 * Downloads ontology from BioPortal, Performs matching with mediating ontology and returns composed mappings
	 * @param onto_acronym
	 * @return composed mappings
	 */
	private Set<MappingObjectStr> performComposedMatchingWithMediatingOntology(String ontoAcronym){
		
		//Download BioPortal ontology
		StatisticsTimeMappings.setCurrentInitTime();
		OWLOntology MO = MO_extractor.downloadBioPortalOntology(ontoAcronym, 1);	
		print("\nTime downloading MO '" + ontoAcronym +"' (s): " + StatisticsTimeMappings.getRunningTime());
		
		Set<MappingObjectStr> composed_mappings = new HashSet<MappingObjectStr>();
		
		if (MO!=null){
			
			//Matching O1 with MO
			StatisticsTimeMappings.setCurrentInitTime();
			LogMap2_Matcher logmap2_matcher_mo1 = new LogMap2_Matcher(O1, MO);
			print("Time matching O1 with MO '" + ontoAcronym +"' (s): " + StatisticsTimeMappings.getRunningTime());
			
			//Matching O2 with MO
			StatisticsTimeMappings.setCurrentInitTime();
			LogMap2_Matcher logmap2_matcher_mo2 = new LogMap2_Matcher(MO, O2);
			print("Time matching O2 with MO '" + ontoAcronym +"' (s): " + StatisticsTimeMappings.getRunningTime());
			
			//Compose mappings
			StatisticsTimeMappings.setCurrentInitTime();
			
			Set<MappingObjectStr> mappings_mo1 = logmap2_matcher_mo1.getLogmap2_Mappings();
			Set<MappingObjectStr> mappings_mo2 = logmap2_matcher_mo2.getLogmap2_Mappings();
			
			composed_mappings = new HashSet<MappingObjectStr>();
						
			for (MappingObjectStr map_mo1 : mappings_mo1){
				if (!map_mo1.isClassMapping())
					continue;
				for (MappingObjectStr map_mo2 : mappings_mo2){
					
					if (!map_mo2.isClassMapping())
						continue;
					
					if (map_mo1.getIRIStrEnt2().equals(map_mo2.getIRIStrEnt1())){
			
						//TODO Check Impact on Precision
						//if (map_mo1.getConfidence()>Parameters.confidence_composed_mappings && 
						//	map_mo2.getConfidence()>Parameters.confidence_composed_mappings)
						
						MappingObjectStr mapping = new MappingObjectStr(
								map_mo1.getIRIStrEnt1(), 
								map_mo2.getIRIStrEnt2(), 
								(map_mo1.getConfidence()+map_mo2.getConfidence())/2.0, MappingObjectStr.EQ, MappingObjectStr.CLASSES);
						
						composed_mappings.add(mapping);
						
						addVote2Mappings(mapping);
						

					}					
				}
				
			}
			print("Time creating composed mappings for'" + ontoAcronym +"' (s): " + StatisticsTimeMappings.getRunningTime());
			
		}
		
		return composed_mappings;
		
		
	}
	
	
	private void addVote2Mappings(MappingObjectStr mapping){
		addVote2Mappings(mapping, 0);
	}
	
	private void addVote2Mappings(MappingObjectStr mapping, int init_vote){
		if (!mappings2votes.containsKey(mapping)){
			mappings2votes.put(mapping, init_vote);
		}
		mappings2votes.put(mapping, mappings2votes.get(mapping).intValue()+1);
	}
	
	
	//if (map_mo1.getConfidence()>Parameters.confidence_composed_mappings && 
	//	map_mo2.getConfidence()>Parameters.confidence_composed_mappings)
	
	
	private Set<MappingObjectStr> getComposedMappings(){
		
		Set<MappingObjectStr> composed_mappings = new HashSet<MappingObjectStr>();
		
		for (MappingObjectStr mapping : mappings2votes.keySet()){
			
			if (logmap2.getLogmap2_Mappings().contains(mapping))
				continue;
			
			//Voted by at least "min_votes". 3 is good
			if (mappings2votes.get(mapping).intValue()>=MIN_VOTES){
				composed_mappings.add(mapping);
			}
			//By confidence
			else if (mapping.getConfidence()>Parameters.confidence_composed_mappings2){
				composed_mappings.add(mapping);
			}
			
		}
		
		getPrecisionRecall(composed_mappings);
		
		return composed_mappings;
	}
	
	private Set<MappingObjectStr> getComposedMappings2(){
		
		Set<MappingObjectStr> composed_mappings = new HashSet<MappingObjectStr>();
		
		for (MappingObjectStr mapping : mappings2votes.keySet()){
			
			if (logmap2.getLogmap2_Mappings().contains(mapping))
				continue;
			
			//Voted by at least "min_votes". 3 is good
			if (mappings2votes.get(mapping).intValue()>=MIN_VOTES){
				composed_mappings.add(mapping);
			}
			//By confidence
			else if (mappings2votes.get(mapping).intValue()>=2 && mapping.getConfidence()>Parameters.confidence_composed_mappings2){
				composed_mappings.add(mapping);
			}
			
		}
		
		getPrecisionRecall(composed_mappings);
		
		return composed_mappings;
	}
	
	
	private Set<MappingObjectStr> getComposedMappings3(){
		
		Set<MappingObjectStr> composed_mappings = new HashSet<MappingObjectStr>();
		
		for (MappingObjectStr mapping : mappings2votes.keySet()){
			
			if (logmap2.getLogmap2_Mappings().contains(mapping))
				continue;
			
			//Voted by at least "min_votes". 3 is good
			if (mappings2votes.get(mapping).intValue()>=MIN_VOTES){
				composed_mappings.add(mapping);
			}
			//By confidence
			else if (mappings2votes.get(mapping).intValue()>=2 && mapping.getConfidence()>Parameters.confidence_composed_mappings2){
				composed_mappings.add(mapping);
			}
			else if (mappings2votes.get(mapping).intValue()>=1 && mapping.getConfidence()>Parameters.confidence_composed_mappings1){
				composed_mappings.add(mapping);
			}
			
		}
		
		getPrecisionRecall(composed_mappings);
		
		return composed_mappings;
	}
	
	
	private Set<MappingObjectStr> getComposedMappings4(){
		
		Set<MappingObjectStr> composed_mappings = new HashSet<MappingObjectStr>();
		
		for (MappingObjectStr mapping : mappings2votes.keySet()){
			
			if (logmap2.getLogmap2_Mappings().contains(mapping))
				continue;
			
			
			if (mappings2votes.get(mapping).intValue()>=MIN_VOTES && mapping.getConfidence()>Parameters.confidence_composed_mappings2){
				composed_mappings.add(mapping);
			}
			else if (mappings2votes.get(mapping).intValue()>=1 && mapping.getConfidence()>Parameters.confidence_composed_mappings1){
				composed_mappings.add(mapping);
			}
			
		}
		
		getPrecisionRecall(composed_mappings);
		
		return composed_mappings;
	}
	
	
	private Set<MappingObjectStr> getComposedMappings(int min_votes){
		
		Set<MappingObjectStr> composed_mappings = new HashSet<MappingObjectStr>();
		
		for (MappingObjectStr mapping : mappings2votes.keySet()){
			
			if (logmap2.getLogmap2_Mappings().contains(mapping))
				continue;
			
			//Voted by at least "min_votes"
			if (mappings2votes.get(mapping).intValue()>=min_votes){
				composed_mappings.add(mapping);
			}
			
		}
		
		getPrecisionRecall(composed_mappings);
		
		return composed_mappings;
	}
	
	
	private OWLOntology loadOWLOntology(String iri_onto) throws OWLOntologyCreationException{		

		OWLOntologyManager managerOnto = SynchronizedOWLManager.createOWLOntologyManager();
		
		managerOnto.setSilentMissingImportsHandling(true);
									
		return managerOnto.loadOntology(IRI.create(iri_onto));
		
	}
	
	
	
	private void checkSatisfiabilityMappings() throws Exception {
		
		OWLOntology mappins_owl_onto = getOWLOntology4GivenMappings(mappings);
		
		
		SatisfiabilityIntegration.setTimeoutClassSatisfiabilityCheck(60);
		
		
		SatisfiabilityIntegration sat_checker = new SatisfiabilityIntegration(
				O1, 
				O2,
				mappins_owl_onto,
				true,//class sat
				true,//Time_Out_Class
				false); //use factory
		
		
		print("Num unsat classes lead by LogMap mappings: " + sat_checker.getNumUnsatClasses());
		
	}
	
	
	
	/**
	 * Returns the given mappings as an OWLOntology object.
	 * @return
	 * @throws Exception
	 */
	private OWLOntology getOWLOntology4GivenMappings(Set<MappingObjectStr> mappings) throws Exception {
		
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
	
	

	
	
	
	private void getPrecisionRecall(Set<MappingObjectStr> mappingsToTest){
		
		boolean printPR = false;
		
		try{
			
			if (printPR){
			
			double p;
			double r;
			double f;
			
			int size_mappings=mappingsToTest.size();
			
			Set<MappingObjectStr> mappings = new HashSet<MappingObjectStr>(mappingsToTest);
			
			
			mappings.retainAll(reference);
			
			int good_mappings = mappings.size();
			
			p = (double)good_mappings/(double) size_mappings;
			r = (double)good_mappings/(double) reference.size();
			f = 2*r*p/(r+p);
			
			
			print("P: "+ p + ", R: " + r +", F: "+ f +", Num: "+ mappingsToTest.size());
			}
		}
		catch(Exception e){
			//do nothing
		}
		
	}
	
	
	
	private static Set<MappingObjectStr> loadReference(String ref){
		
		try {
			
			RDFAlignReader readerref;
			
			readerref = new RDFAlignReader(ref);
			
			return readerref.getMappingObjects();
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			
		
		return null;
		
	}
	
	
	private void print(String text){
		//if (Parameters.print_output_always)
			System.out.println(text);
		
	}
	
	
	public static Set<MappingObjectStr> reference = new HashSet<MappingObjectStr>();
	
	public static void main(String[] args){
		
		String iri1, iri2, ref;
		
		
		iri1= "file:/home/ernesto/Documents/OAEI_datasets/mouse.owl";
		iri2= "file:/home/ernesto/Documents/OAEI_datasets/human.owl";
		ref = "/home/ernesto/Documents/OAEI_datasets/reference.rdf"; // Reference ontology
				
		/*iri1= "file:/home/ernesto/Documents/OAEI_datasets/oaei_2013/oaei2013_FMA_small_overlapping_nci.owl";
		iri2= "file:/home/ernesto/Documents/OAEI_datasets/oaei_2013/oaei2013_NCI_small_overlapping_fma.owl";
		
		iri1= "file:/home/ernesto/Documents/OAEI_datasets/oaei_2013/oaei2013_FMA_whole_ontology.owl";
		iri2= "file:/home/ernesto/Documents/OAEI_datasets/oaei_2013/oaei2013_NCI_whole_ontology.owl";
		ref = "/home/ernesto/Documents/OAEI_datasets/oaei_2013/oaei2013_FMA2NCI_repaired_UMLS_mappings.rdf";
		
		/*iri1= "file:/home/ernesto/Documents/OAEI_datasets/oaei_2013/oaei2013_FMA_small_overlapping_snomed.owl";
		iri2= "file:/home/ernesto/Documents/OAEI_datasets/oaei_2013/oaei2013_SNOMED_small_overlapping_fma.owl";
		
		iri1= "file:/home/ernesto/Documents/OAEI_datasets/oaei_2013/oaei2013_FMA_whole_ontology.owl";
		iri2= "file:/home/ernesto/Documents/OAEI_datasets/oaei_2013/oaei2013_SNOMED_extended_overlapping_fma_nci.owl";
		
		ref = "/home/ernesto/Documents/OAEI_datasets/oaei_2013/oaei2013_FMA2SNOMED_repaired_UMLS_mappings.rdf";
		
		/*iri1= "file:/home/ernesto/Documents/OAEI_datasets/oaei_2013/oaei2013_SNOMED_small_overlapping_nci.owl";
		iri2= "file:/home/ernesto/Documents/OAEI_datasets/oaei_2013/oaei2013_NCI_small_overlapping_snomed.owl";
		
		iri1= "file:/home/ernesto/Documents/OAEI_datasets/oaei_2013/oaei2013_SNOMED_extended_overlapping_fma_nci.owl";
		iri2= "file:/home/ernesto/Documents/OAEI_datasets/oaei_2013/oaei2013_NCI_whole_ontology.owl";
		ref = "/home/ernesto/Documents/OAEI_datasets/oaei_2013/oaei2013_SNOMED2NCI_repaired_UMLS_mappings.rdf";
		
		SatisfiabilityIntegration.setReasoner(ReasonerManager.ELK);
		*/
		

		reference = loadReference(ref);
		
		LogMap2_OAEI_BioPortal logmap_Bio = new LogMap2_OAEI_BioPortal();
		
		
		try {
			logmap_Bio.align(iri1, iri2);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}

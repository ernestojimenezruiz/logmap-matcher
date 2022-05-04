package uk.ac.ox.krr.logmap2;

import java.util.Calendar;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;

import uk.ac.ox.krr.logmap2.indexing.JointIndexManager;
import uk.ac.ox.krr.logmap2.indexing.OntologyProcessing;
import uk.ac.ox.krr.logmap2.io.LogOutput;
import uk.ac.ox.krr.logmap2.lexicon.LexicalUtilities;
import uk.ac.ox.krr.logmap2.mappings.CandidateMappingManager;
import uk.ac.ox.krr.logmap2.mappings.objects.MappingObjectStr;
import uk.ac.ox.krr.logmap2.overlapping.OverlappingExtractor4Mappings;
import uk.ac.ox.krr.logmap2.repair.AnchorAssessment;
import uk.ac.ox.krr.logmap2.utilities.Utilities;

public class LogMap2_MappingRanking {
	
	//Input ontologies (constructor) and a set of mappings to rank in a method.
	
	//Call logmap without emptying structures. Keep LogMap active
	
	//Check Confidence of mappings. If conflicting then latest in rank.
	
	//Return ranked list (sort autmatically) according to confidence or other factors
	
	private long init, fin;

	
	LogMap2Core logmap2;

	
	private Set<MappingObjectStr> input_mappings;	
	
	private OWLOntology onto1;
	
	private OWLOntology onto2;
	
	
	private OntologyProcessing onto_process1;
	private OntologyProcessing onto_process2;
	
	private JointIndexManager index;
	
	private CandidateMappingManager mapping_manager;
	
	private AnchorAssessment mapping_assessment;
	
	boolean overlapping = false;
	
	
	
	
	private TreeSet<MappingObjectStr> ordered_mappings = new TreeSet<MappingObjectStr>(new MappingComparator());
	
	
	
	
	
	public LogMap2_MappingRanking(OWLOntology onto1, OWLOntology onto2) throws Exception{
		
		//LogOutput.showOutpuLog(true);
		Parameters.readParameters();
		
		this.onto1 = onto1;
		this.onto2 = onto2;
		
		try {
			logmap2 = new LogMap2Core(onto1, onto2);
		} 
		catch (Exception e) {
			e.printStackTrace();
		}		
		
		System.out.println("LogMap mappings: " + logmap2.getLogMapMappings().size());
		
		//Recreate LogMap structures otherwise I think they are removed in LogMap
		setUpStructures();

	}
	
	
	private void setUpStructures() throws Exception{
		
		
		//Create Index and new Ontology Index...
		index = new JointIndexManager();
		
		
		//Extract overlapping if indicated
		if (overlapping){
			OverlappingExtractor4Mappings overlapping = new OverlappingExtractor4Mappings();
			
			overlapping.createOverlapping(onto1, onto2, input_mappings);
			
			onto_process1 = new OntologyProcessing(overlapping.getOverlappingOnto1(), index, new LexicalUtilities());
			onto_process2 = new OntologyProcessing(overlapping.getOverlappingOnto2(), index, new LexicalUtilities());
		}
		else{
			onto_process1 = new OntologyProcessing(onto1, index, new LexicalUtilities());
			onto_process2 = new OntologyProcessing(onto2, index, new LexicalUtilities());
		}
		
		
		
		
		mapping_manager = new CandidateMappingManager(index, onto_process1, onto_process2);
		
		
		
		//Extracts lexicon
		init = Calendar.getInstance().getTimeInMillis();
		//TODO This may affect scalability!!
		onto_process1.precessLexicon(true); //we process labels if "useLogMapConfidences"
		onto_process2.precessLexicon(true);
		fin = Calendar.getInstance().getTimeInMillis();
		LogOutput.print("Time indexing entities (s): " + (float)((double)fin-(double)init)/1000.0);

		
		//Extracts Taxonomy
		//Also extracts A^B->C
		init = Calendar.getInstance().getTimeInMillis();
		onto_process1.setTaxonomicData();
		onto_process2.setTaxonomicData();
		fin = Calendar.getInstance().getTimeInMillis();
		LogOutput.print("Time extracting structural information (s): " + (float)((double)fin-(double)init)/1000.0);
		
		
		
		init = Calendar.getInstance().getTimeInMillis();
		
		//Index already have the necessary taxonomical information apart from the equiv mappings
		index.setIntervalLabellingIndex(logmap2.getLogMapMappings1N()); //We get them from logmap not the input mappings!!
		index.clearAuxStructuresforLabellingSchema();
		
		fin = Calendar.getInstance().getTimeInMillis();
		LogOutput.print("\tTime indexing hierarchy + anchors (ILS) (s): " + (float)((double)fin-(double)init)/1000.0);
		
		
		
		
	}
	
	
	
	

	
	
	
	
	
	/**
	 * Ranks a set of candidate mappings
	 * @param input_mappings
	 */
	public TreeSet<MappingObjectStr> rankedMappings(Set<MappingObjectStr> input_mappings) {
		
		this.ordered_mappings.clear();
		this.input_mappings.clear();
		
		this.input_mappings = input_mappings;
		
		//Associate type in case type is missing + LogMap confidence
		associateType2Mappings();
		
		
		//Asses mappings
		assessClassMappings1step();
		
		
		return ordered_mappings;
		
		
	}
	
	
	
	/**
	 * We associate type to mappings in case the object does indicate this.
	 */
	private void associateType2Mappings(){

		
		//TODO, only using LogMap's confidence for class mappings!!!
		

		//TREAT GIVEN MAPPINGS

		int num_original_class_mappings=0;
		int num_original_dprop_mappings=0;
		int num_original_oprop_mappings=0;
		int num_original_instance_mappings=0;
		int num_mixed_mappings=0;
		
		
		for (MappingObjectStr map : input_mappings){
		
			//Check if it contains type? Better to double check
			
			
			//Detect the type of mapping: class, property or instance
			//In some cases it might be included
			if (onto1.containsClassInSignature(IRI.create(map.getIRIStrEnt1()), true)
				&& onto2.containsClassInSignature(IRI.create(map.getIRIStrEnt2()), true)) {
				
				map.setTypeOfMapping(Utilities.CLASSES);
				
				
				
				//Translate from mapping 2 index
				int ide1=onto_process1.getIdentifier4ConceptName(Utilities.getEntityLabelFromURI(map.getIRIStrEnt1()));
				int ide2=onto_process2.getIdentifier4ConceptName(Utilities.getEntityLabelFromURI(map.getIRIStrEnt2()));
						
				
				//Computes the scores
				double scoreISUB = mapping_manager.getIsubScore4ConceptsLabels(ide1, ide2);//extractISUB4Mapping(ide1, ide2);
				double scoreScope = mapping_manager.getScopeISUB4Neighbourhood(ide1, ide2);//.extractScopeAll4Mapping(ide1, ide2);
				
				//System.out.println(scoreISUB + " " + scoreScope);
				
				
				//Update with LogMap's confidence
				map.setConfidenceMapping((double)(scoreISUB+scoreScope)/2.0);
				
				
				
				//ordered_mappings.add(map);
				
				num_original_class_mappings++;
				
				
			}
			else if (onto1.containsObjectPropertyInSignature(IRI.create(map.getIRIStrEnt1()), true)
					&& onto2.containsObjectPropertyInSignature(IRI.create(map.getIRIStrEnt2()), true)) {
					
				map.setTypeOfMapping(Utilities.OBJECTPROPERTIES);
				
				//ordered_mappings.add(map);
				
				num_original_oprop_mappings++;
				
			
			}
			else if (onto1.containsDataPropertyInSignature(IRI.create(map.getIRIStrEnt1()), true)
				&& onto2.containsDataPropertyInSignature(IRI.create(map.getIRIStrEnt2()), true)) {
				
				map.setTypeOfMapping(Utilities.DATAPROPERTIES);
				
				//ordered_mappings.add(map);
				
				num_original_dprop_mappings++;
				
			}
			
			else if (onto1.containsIndividualInSignature(IRI.create(map.getIRIStrEnt1()), true)
					&& onto2.containsIndividualInSignature(IRI.create(map.getIRIStrEnt2()), true)) {
				
				map.setTypeOfMapping(Utilities.INSTANCES);
					
				//ordered_mappings.add(map);			
				
				num_original_instance_mappings++;
				
			}
			else {
				//System.out.println("Mixed Entities or entities not in signature of ontologies: ");
				//System.out.println("\t" + map.getIRIStrEnt1());
				//System.out.println("\t" + map.getIRIStrEnt2());
				
				num_mixed_mappings++;
				
			}
			
			
			if (num_mixed_mappings>0) {
				System.out.println("Input alignment contain mixed type mappings: " + num_mixed_mappings);
			}
			
			
		}
		
	}
	
	
	
	/**
	 * Clean the complete set of mappings at once
	 */
	private void assessClassMappings1step(){
		
				
		
		for (MappingObjectStr map : input_mappings){
			
			if (map.isClassMapping()) {
			
				int ide1=onto_process1.getIdentifier4ConceptName(Utilities.getEntityLabelFromURI(map.getIRIStrEnt1()));
				int ide2=onto_process2.getIdentifier4ConceptName(Utilities.getEntityLabelFromURI(map.getIRIStrEnt2()));
				
				if (mapping_manager.isMappingInConflictWithFixedMappings(ide1, ide2)){					
					map.setConfidenceMapping(-1.0);					
				}
				
				//System.out.println(mapping_manager.isMappingInferred(ide1, ide2));
				if (mapping_manager.isMappingInferred(ide1, ide2))
					map.setConfidenceMapping(1.0);
				
				
			}
			
			//Otherwise
			ordered_mappings.add(map);				
			
		}
	}
	
	
	
	 
	
	
	public void clearStructures() {
		logmap2.clearIndexStructures();
	}
	
	
	
	
	/**
	 * Comparator based on the mapping confidence
	 * @author Ernesto
	 *
	 */
	private class MappingComparator implements Comparator<MappingObjectStr> {
		
		/**
		 * We order by  confidence
		 */
		public int compare(MappingObjectStr m1, MappingObjectStr m2) {

		
			if (m1.getConfidence()<m2.getConfidence()){
				return -1;					
			}
			else{
				return 1;
			}
				
			
		}
		
	}

	
	

}

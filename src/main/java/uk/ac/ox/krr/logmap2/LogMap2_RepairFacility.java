package uk.ac.ox.krr.logmap2;


import uk.ac.ox.krr.logmap2.indexing.JointIndexManager;
import uk.ac.ox.krr.logmap2.oaei.reader.MappingsReaderManager;
import uk.ac.ox.krr.logmap2.indexing.OntologyProcessing;
import uk.ac.ox.krr.logmap2.io.LogOutput;
import uk.ac.ox.krr.logmap2.io.OWLAlignmentFormat;
import uk.ac.ox.krr.logmap2.io.OutPutFilesManager;
import uk.ac.ox.krr.logmap2.io.ReadFile;
import uk.ac.ox.krr.logmap2.mappings.CandidateMappingManager;
import uk.ac.ox.krr.logmap2.mappings.objects.MappingObjectStr;
import uk.ac.ox.krr.logmap2.reasoning.SatisfiabilityIntegration;
import uk.ac.ox.krr.logmap2.repair.AnchorAssessment;
import uk.ac.ox.krr.logmap2.utilities.Utilities;
import uk.ac.ox.krr.logmap2.lexicon.LexicalUtilities;
import uk.ac.ox.krr.logmap2.overlapping.OverlappingExtractor4Mappings;

import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.apibinding.OWLManager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.TreeSet;


/**
 * This class will require two OWL ontologies and a set of mappings (see <code>MappingObjectStr<code>)
 * @author Ernesto
 *
 */
public class LogMap2_RepairFacility {
	
	
	private long init_global, init, fin;
	
	private OntologyProcessing onto_process1;
	private OntologyProcessing onto_process2;
	
	private JointIndexManager index;
	
	private CandidateMappingManager mapping_manager;
	
	private AnchorAssessment mapping_assessment;
	
	private OWLOntology onto1;
	private OWLOntology onto2;
	private Set<MappingObjectStr> input_mappings;	
	private boolean overlapping;
	private boolean method_optimal;
	private boolean useLogMapConfidences;
	
	
	private TreeSet<MappingObjectStr> ordered_mappings = new TreeSet<MappingObjectStr>(new MappingComparator());
	private Set<MappingObjectStr> clean_mappings = new HashSet<MappingObjectStr>();
	
	
	protected Map<Integer, Set<Integer>> mappings2Review_step2 = new HashMap<Integer, Set<Integer>>();
	
	
	private double average_confidence=0;
	
	
	
	public LogMap2_RepairFacility(
			OWLOntology onto1, 
			OWLOntology onto2, 
			Set<MappingObjectStr> mappings,
			boolean sat_check){
		this(onto1, onto2, mappings, false, false, sat_check, "");
	}
	
	
	
	/**
	 * Constructor from Java application
	 * @param onto1
	 * @param onto2
	 * @param mappings
	 * @param overlapping If the intersection or overlapping of the ontologies are extracted before the repair
	 * @param optimal If the repair is performed in a two steps process (optimal) or in one cleaning step (more aggressive)
	 */
	public LogMap2_RepairFacility(
			OWLOntology onto1, 
			OWLOntology onto2, 
			Set<MappingObjectStr> mappings, 
			boolean overlapping, 
			boolean optimal){
		this(onto1, onto2, mappings, overlapping, optimal, false, "");
	}
	
	
	
	public LogMap2_RepairFacility(
			OWLOntology onto1,
			OWLOntology onto2, 
			Set<MappingObjectStr> mappings, 
			boolean overlapping, 
			boolean optimal,
			boolean chechSatisfiability,
			String outPutFileName){
		
		this(onto1, onto2, mappings, overlapping, optimal, false, chechSatisfiability, outPutFileName);
		
	}
	
	
	
	/**
	 * Constructor for command line
	 * @param onto1
	 * @param onto2
	 * @param mappings
	 * @param overlapping If the intersection or overlapping of the ontologies are extracted before the repair
	 * @param optimal If the repair is performed in a two steps process (optimal) or in one cleaning step (more aggressive)
	 * @param chechSatisfiability Uses HermiT to check if the repaired mappings lead to unsatisfiable classes
	 * @param outPutFileName
	 */
	public LogMap2_RepairFacility(
			OWLOntology onto1,
			OWLOntology onto2, 
			Set<MappingObjectStr> mappings, 
			boolean overlapping, 
			boolean optimal, 
			boolean useLogMapConfidences,
			boolean chechSatisfiability,
			String outPutFileName){
		
		this.onto1 = onto1;
		this.onto2 = onto2;
		this.input_mappings = mappings;
		this.overlapping=overlapping;
		this.method_optimal = optimal;
		this.useLogMapConfidences = useLogMapConfidences;
		
		
			
		
		try{
			
			init_global = init = Calendar.getInstance().getTimeInMillis();
			
			
			setUpStructures();
			
			//TODO It also includes assessment for properties and instances!			
			assessMappings();
			
			//Always... at least for testing
			keepRepairedMappings();
			
			
			if (!outPutFileName.equals("")){
				saveRepairedMappings(outPutFileName);
			}
			
			if (chechSatisfiability){
				checkSatisfiabilityMappings(clean_mappings);
			}
			
			
			fin = Calendar.getInstance().getTimeInMillis();
			System.out.println("TOTAL REPAIR TIME (s): " + (float)((double)fin-(double)init_global)/1000.0);
			
		
		}
		catch (Exception e){
			System.out.println("Error repairing mappings using LogMap repair module: " + e.getMessage());
		}
		
		
	}
	
	
	
	/**
	 * Method to be called from web service
	 * Create a new class??? //TODO
	 * @param onto1
	 * @param onto2
	 * @param fixedmappings
	 * @param mappings2review
	 * @deprecated Not completed method!
	 */
	public LogMap2_RepairFacility(
			OWLOntology onto1,
			OWLOntology onto2, 
			Set<MappingObjectStr> fixedmappings,
			Set<MappingObjectStr> mappings2review){
		
		
		this.onto1 = onto1;
		this.onto2 = onto2;
		this.input_mappings = fixedmappings;
		this.overlapping=false; //we already have the modules!
		this.method_optimal = true;
		

		try{
			
			init_global = init = Calendar.getInstance().getTimeInMillis();
		
			setUpStructures();
			
			//TODO It also includes assessment for properties and instances!			
			assessMappings();
			
			//Always... at least for testing
			//Clean mapping
			keepRepairedMappings();
			
			
			
			fin = Calendar.getInstance().getTimeInMillis();
			System.out.println("TOTAL REPAIR TIME (s): " + (float)((double)fin-(double)init_global)/1000.0);
			
		
		}
		catch (Exception e){
			System.out.println("Error repairing mappings using LogMap repair module: " + e.getMessage());
		}
		
		
		
		
	}
	
	
	
	private void addSubMapping2Mappings2Review(int index1, int index2){
		
		if (!mappings2Review_step2.containsKey(index1)){
			mappings2Review_step2.put(index1, new HashSet<Integer>());
		}
		mappings2Review_step2.get(index1).add(index2);
	}
	
	
	/**
	 * We associate type to mappings in case the object does indicate this.
	 */
	private void associateType2Mappings(){
		

		//TREAT GIVEN MAPPINGS

		int num_original_class_mappings=0;
		int num_original_dprop_mappings=0;
		int num_original_oprop_mappings=0;
		int num_original_instance_mappings=0;
		int num_mixed_mappings=0;
		
		double min_confidence = 2.0;
		double max_confidence = 0.0;
		average_confidence = 0.0;
		
		
		for (MappingObjectStr map : input_mappings){
		
			//Check if it contains type? Better to double check
			
			average_confidence = average_confidence + map.getConfidence();
			if (map.getConfidence()<min_confidence){
				min_confidence = map.getConfidence(); 
			}
			if (map.getConfidence()>max_confidence){
				max_confidence = map.getConfidence(); 
			}
			
			
			//Detect the type of mapping: class, property or instance
			//In some cases it might be included
			if (onto1.containsClassInSignature(IRI.create(map.getIRIStrEnt1()), true)
				&& onto2.containsClassInSignature(IRI.create(map.getIRIStrEnt2()), true)) {
				
				map.setTypeOfMapping(Utilities.CLASSES);
				
				ordered_mappings.add(map);
				
				num_original_class_mappings++;
				
				
			}
			else if (onto1.containsObjectPropertyInSignature(IRI.create(map.getIRIStrEnt1()), true)
					&& onto2.containsObjectPropertyInSignature(IRI.create(map.getIRIStrEnt2()), true)) {
					
				map.setTypeOfMapping(Utilities.OBJECTPROPERTIES);
				
				ordered_mappings.add(map);
				
				num_original_oprop_mappings++;
				
			
			}
			else if (onto1.containsDataPropertyInSignature(IRI.create(map.getIRIStrEnt1()), true)
				&& onto2.containsDataPropertyInSignature(IRI.create(map.getIRIStrEnt2()), true)) {
				
				map.setTypeOfMapping(Utilities.DATAPROPERTIES);
				
				ordered_mappings.add(map);
				
				num_original_dprop_mappings++;
				
			}
			
			else if (onto1.containsIndividualInSignature(IRI.create(map.getIRIStrEnt1()), true)
					&& onto2.containsIndividualInSignature(IRI.create(map.getIRIStrEnt2()), true)) {
				
				map.setTypeOfMapping(Utilities.INSTANCES);
					
				ordered_mappings.add(map);			
				
				num_original_instance_mappings++;
				
			}
			else {
				//System.out.println("Mixed Entities or entities not in signature of ontologies: ");
				//System.out.println("\t" + map.getIRIStrEnt1());
				//System.out.println("\t" + map.getIRIStrEnt2());
				
				num_mixed_mappings++;
				
			}
			
			
		}
		
		average_confidence = average_confidence/(double)input_mappings.size();
		
		LogOutput.printAlways("Num original mappings: " + input_mappings.size());
		LogOutput.print("\tNum original class mappings: " + num_original_class_mappings);
		LogOutput.print("\tNum original object property mappings: " + num_original_oprop_mappings);
		LogOutput.print("\tNum original data property mappings: " + num_original_dprop_mappings);			
		LogOutput.print("\tNum original instance mappings: " + num_original_instance_mappings);
		LogOutput.print("\tNum mixed mappings: " + num_mixed_mappings);
		LogOutput.print("\tMin confidence: " + min_confidence);
		LogOutput.print("\tMax confidence: " + max_confidence);
		LogOutput.print("\tAVERAGE confidence: " + average_confidence);
		
		//We do not differentiate between reliable mappings and other mappings
		if (!method_optimal)
			average_confidence=-1.0;
		
		
		
	}
	
	
	/**
	 * We add mapping to structures in mapping_manager
	 */
	private void addMapping2Structures(){
		
		
		mappings2Review_step2.clear();
		
		
		Iterator<MappingObjectStr> it = ordered_mappings.descendingIterator();
		
		MappingObjectStr map;
		
		
		//for (MappingObjectStr map : ordered_mappings){
		while (it.hasNext()){
			
			map = it.next();
			
			//System.out.println(map + "   " + map.getConfidence());
			
			if (map.getTypeOfMapping()==Utilities.CLASSES) {
				
				addClassMapping(map);
				
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
		
		//Not necessary any more
		ordered_mappings.clear();
		
		
		LogOutput.print("Numb of reliable mappings: " + num_anchors);
		LogOutput.print("Numb of other mappings: " + num_mappings2review);
		
		
		
		
	}
	
	
	
	
	private void setUpStructures() throws Exception{
		
		
		//try{
		
		//TODO showOutput!!		
		LogOutput.showOutpuLog(false);
		
		//init = Calendar.getInstance().getTimeInMillis();
		//PrecomputeIndexCombination.preComputeIdentifierCombination();
		//fin = Calendar.getInstance().getTimeInMillis();
		//LogOutput.print("Time precomputing index combinations (s): " + (float)((double)fin-(double)init)/1000.0);
		
		
		//When reading from RDF align there is no type
		associateType2Mappings();
		
		
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
		onto_process1.precessLexicon(useLogMapConfidences); //we process labels if "useLogMapConfidences"
		onto_process2.precessLexicon(useLogMapConfidences);
		fin = Calendar.getInstance().getTimeInMillis();
		LogOutput.print("Time indexing entities (s): " + (float)((double)fin-(double)init)/1000.0);

		
		//Extracts Taxonomy
		//Also extracts A^B->C
		init = Calendar.getInstance().getTimeInMillis();
		onto_process1.setTaxonomicData();
		onto_process2.setTaxonomicData();
		fin = Calendar.getInstance().getTimeInMillis();
		LogOutput.print("Time extracting structural information (s): " + (float)((double)fin-(double)init)/1000.0);
				
		
		//We add mappings to structures (important to be after setting tax data)
		addMapping2Structures();
		
		
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
		
		
		//CLASS MAPPINGS ASSESSESMENT
		if (method_optimal)
			assessClassMappings2steps(); //we split between reliable and other mappings
		else
			assessClassMappings1step();
		
		
		
		//Clean property mappings and individual mappings
		//--------------------------------
		
		//Assess Property mappings: using index
		if (mapping_manager.getDataPropertyAnchors().size() >0 || mapping_manager.getObjectPropertyAnchors().size() > 0) {
			init = Calendar.getInstance().getTimeInMillis();
			mapping_manager.evaluateCompatibilityDataPropertyMappings();
			mapping_manager.evaluateCompatibilityObjectPropertyMappings();
			fin = Calendar.getInstance().getTimeInMillis();
			LogOutput.print("\tTime assessing property mappings (s): " + (float)((double)fin-(double)init)/1000.0);		
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
				LogOutput.print("Time cleaning instance mappings D&G (s): " + (float)((double)fin-(double)init)/1000.0);
			}
			
			fin = Calendar.getInstance().getTimeInMillis();
			LogOutput.print("\tTime assessing instance mappings (s): " + (float)((double)fin-(double)init)/1000.0);
		}
		
		
	}
	
	/**
	 * Clean first the identified anchors and then clean the rest of candidates
	 */
	private void assessClassMappings2steps(){
		
		
		int discarded_with_index=0;
		
		mapping_assessment = new AnchorAssessment(index, mapping_manager);
		
		//TODO uncomments for general behaviour
		//For SNOMED-NCI cases we need an approximation and for other cases as well
		//init = Calendar.getInstance().getTimeInMillis();
		//mapping_assessment.CountSatisfiabilityOfIntegration_DandG(mapping_manager.getAnchors());
		//fin = Calendar.getInstance().getTimeInMillis();
		//System.out.println("\tTime counting unsat Dowling and Gallier (s): " + (float)((double)fin-(double)init)/1000.0);
		
		
		init = Calendar.getInstance().getTimeInMillis();
		mapping_assessment.CheckSatisfiabilityOfIntegration_DandG(mapping_manager.getLogMapMappings());
		fin = Calendar.getInstance().getTimeInMillis();
		LogOutput.print("\tTime cleaning reliable class mappings Dowling and Gallier (s): " + (float)((double)fin-(double)init)/1000.0);
		LogOutput.print("\tRepaired Root Unsat using Dowling and Gallier (aproximation): " + mapping_assessment.getNumRepairedUnsatClasses());
		
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
			LogOutput.print("\tTime indexing hierarchy + anchors (ILS) (s): " + (float)((double)fin-(double)init)/1000.0);
			
			
			//Asses mappings to review
			for (int ide1 : mappings2Review_step2.keySet()){
				for (int ide2 : mappings2Review_step2.get(ide1)){
					
					//if (index.areDisjoint(ide1, ide2)) { //less agressive?
					if (mapping_manager.isMappingInConflictWithFixedMappings(ide1, ide2)){
						discarded_with_index++;
					}
					else {
						mapping_manager.addSubMapping2Mappings2Review(ide1, ide2);						
					}
				}				
			}
			
			LogOutput.print("Discarded with index: " + discarded_with_index);
			
			
		}
		catch (Exception e){
			System.out.println("Error creating Interval Labelling index 1: " + e.getMessage());
			e.printStackTrace();
		}
		
		if (mapping_manager.getMappings2Review().size()>0){
		
			mapping_manager.setExactAsFixed(false);//repair all, just in case...
			
			//Clean D&G mappings 2 review
			init = Calendar.getInstance().getTimeInMillis();
			mapping_assessment.CheckSatisfiabilityOfIntegration_DandG(mapping_manager.getMappings2Review());  //With Fixed mappings!
			fin = Calendar.getInstance().getTimeInMillis();
			LogOutput.print("Time cleaning rest of the mappings using D&G (s): " + (float)((double)fin-(double)init)/1000.0);
			LogOutput.print("\tRepaired Root Unsat using Dowling and Gallier 2 (aproximation): " + mapping_assessment.getNumRepairedUnsatClasses());
			
			//Move clean to anchors
			mapping_manager.moveMappingsToReview2AnchorList();
			
			
			//Interval labelling index with try block with all clean mappings
			//------------------------------
			try{			
				init = Calendar.getInstance().getTimeInMillis();
				index.setIntervalLabellingIndex(mapping_manager.getLogMapMappings());//It also contains mappings 2 review
				index.clearAuxStructuresforLabellingSchema();
				fin = Calendar.getInstance().getTimeInMillis();
				LogOutput.print("Time indexing hierarchy + anchors and candidates I (ILS) (s): " + (float)((double)fin-(double)init)/1000.0);
			}
			catch (Exception e){
				System.out.println("Error creating Interval Labelling index 2: " + e.getMessage());
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
		
		
		//TODO necesary??
		//Repair all just in case
		///--------------------------
		//mapping_manager.setExactAsFixed(false);
		
		//init = Calendar.getInstance().getTimeInMillis();
		//mapping_assessment.CheckSatisfiabilityOfIntegration_DandG(mapping_manager.getAnchors());
		//fin = Calendar.getInstance().getTimeInMillis();
		//LogOutput.print("\tTime cleaning ALL class mappings Dowling and Gallier (s): " + (float)((double)fin-(double)init)/1000.0);
		//LogOutput.print("\tRepaired Root Unsat using Dowling and Gallier 3 (aproximation): " + mapping_assessment.getNumRepairedUnsatClasses());
		
		//New weakened ar not added
				
		
		
	}
	
	
	/**
	 * Clean the complete set of mappings at once
	 */
	private void assessClassMappings1step(){
		
		mapping_assessment = new AnchorAssessment(index, mapping_manager);
		
		//TODO comments for general behaviour
		//For SNOMED-NCI cases we need an approximation and for other cases as well
		//init = Calendar.getInstance().getTimeInMillis();
		//mapping_assessment.CountSatisfiabilityOfIntegration_DandG(mapping_manager.getAnchors());
		//fin = Calendar.getInstance().getTimeInMillis();
		//System.out.println("\tTime counting unsat Dowling and Gallier (s): " + (float)((double)fin-(double)init)/1000.0);
		//if (true)
		//	return;
		//End to comments
					
					
		init = Calendar.getInstance().getTimeInMillis();
		mapping_assessment.CheckSatisfiabilityOfIntegration_DandG(mapping_manager.getLogMapMappings());
		fin = Calendar.getInstance().getTimeInMillis();
		LogOutput.print("\tTime cleaning class mappings Dowling and Gallier (s): " + (float)((double)fin-(double)init)/1000.0);
		LogOutput.print("\tRepaired Root Unsat using Dowling and Gallier (aproximation): " + mapping_assessment.getNumRepairedUnsatClasses());
		
		
		
		try {
		
			//Interval labelling schema
			//--------------------------
			init = Calendar.getInstance().getTimeInMillis();
			
			//Index already have the necessary taxonomical information apart from the equiv mappings
			index.setIntervalLabellingIndex(mapping_manager.getLogMapMappings());
			index.clearAuxStructuresforLabellingSchema();
			
			fin = Calendar.getInstance().getTimeInMillis();
			LogOutput.print("\tTime indexing hierarchy + anchors (ILS) (s): " + (float)((double)fin-(double)init)/1000.0);
		}
		catch (Exception e){
			System.out.println("Error creating Interval Labelling index: " + e.getMessage());
			e.printStackTrace();
		}
		
					
		for (int ide1 : mapping_manager.getWeakenedDandGMappings().keySet()){
			
			for (int ide2 : mapping_manager.getWeakenedDandGMappings().get(ide1)){
				
				//TODO: This is necessary no?
				if (!mapping_manager.isMappingInConflictWithFixedMappings(ide1, ide2)){
					mapping_manager.addSubMapping2ListOfAnchors(ide1, ide2);
				}
				
			}
		}
	}
	
	
	int num_anchors=0;
	int num_mappings2review=0;
	
	
	/**
	 * Adds mappings to structures and extract isub and scope using LogMap methods
	 * It deprecates the original given confidence in order to identity "reliable" mappings as LogMap  does.
	 * @param map
	 */
	private void addClassMapping(MappingObjectStr map){
		
		int ide1;
		int ide2;
		
		double scoreISUB;
		double scoreScope;
		
				
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
		
		
		//New isub and scope calculated by LogMap
		if (useLogMapConfidences){
			scoreISUB = mapping_manager.extractISUB4Mapping(ide1, ide2);
			scoreScope = mapping_manager.extractScopeAll4Mapping(ide1, ide2);
		}
		else{
			//TODO Confidence given in the mapping (e.g. calculated by the OM systems)
			//In that case, method_optimal is not recommended
			mapping_manager.addIsub2Structure(ide1, ide2, map.getConfidence());
			mapping_manager.addIsub2Structure(ide2, ide1, map.getConfidence());
			
			scoreISUB = map.getConfidence();
			scoreScope = map.getConfidence();
		}

		
		if (method_optimal){
			//TODO
			//If good isub and scope then reliable! Otherwise clean...
		
			if (scoreISUB>=Parameters.good_isub_anchors && scoreScope>Parameters.bad_score_scope && map.getMappingDirection()==Utilities.EQ){
				//Reliable "equivalence" mappings
				mapping_manager.addSubMapping2ListOfAnchors(ide1, ide2);
				mapping_manager.addSubMapping2ListOfAnchors(ide2, ide1);
				num_anchors++;
			}
			else if (map.getMappingDirection()==Utilities.L2R){	
				addSubMapping2Mappings2Review(ide1, ide2);
				num_mappings2review++;
			}
			else{
				addSubMapping2Mappings2Review(ide2, ide1);
				num_mappings2review++;
			}
		}
		else{ //We do not split mappings: all are anchors
			
			num_anchors++;
			
			if (map.getMappingDirection()==Utilities.EQ){
				//Reliable "equivalence" mappings
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
	
	}
	
	
	
	
	/**
	 * Adds mappings to structures. Uses confidence given in mapping
	 * @param map
	 * @deprecated
	 */
	private void addClassMapping2(MappingObjectStr map){
		
		
		
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
		
		//Confidence given in the mapping (e.g. calculated by the OM systems)
		mapping_manager.addIsub2Structure(ide1, ide2, map.getConfidence());
		mapping_manager.addIsub2Structure(ide2, ide1, map.getConfidence());
		
		
		//TODO Split mappings in anchors and candidates 2 review
		//Less than half
		if (((2*num_anchors) < ordered_mappings.size()) || !method_optimal){
		//if (map.getConfidence()>=average_confidence){
			
			num_anchors++;				
			
			if (map.getMappingDirection()==Utilities.EQ){
				mapping_manager.addSubMapping2ListOfAnchors(ide1, ide2);
				mapping_manager.addSubMapping2ListOfAnchors(ide2, ide1);
				
			}
			else if (map.getMappingDirection()==Utilities.L2R){
				mapping_manager.addSubMapping2ListOfAnchors(ide1, ide2); //TODO Check this
				//mapping_manager.addIsub2Structure(ide1, ide2, map.getConfidence()); //confidence
				
				if (method_optimal){//we do not add them to reliable mappings for indexing issues
					addSubMapping2Mappings2Review(ide1, ide2);
				}
				
			}
			else{
				mapping_manager.addSubMapping2ListOfAnchors(ide2, ide1);
				
				if (method_optimal){ //we do not add them to reliable mappings for indexing issues
					addSubMapping2Mappings2Review(ide2, ide1);
				}
				//mapping_manager.addIsub2Structure(ide2, ide1, map.getConfidence());
			}
			
		}
		else {
			
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
	
	
	public void saveRepairedMappings(String outPutFileName){
		saveRepairedMappings(outPutFileName, OutPutFilesManager.AllFormats);
	}
	
	
	public void saveRepairedMappings(String outPutFileName, int format){
		
		int dirMapping;
		
		OutPutFilesManager outPutFilesManager = new OutPutFilesManager();
		
		int num_clean_mappings=0;
		int num_clean_class_mappings=0;
		int num_clean_dprop_mappings=0;
		int num_clean_oprop_mappings=0;
		int num_clean_instance_mappings=0;
		
		try {
			outPutFilesManager.createOutFiles(
					//logmap_mappings_path + "Output/mappings",
					//path + "/" + file_name,
					//outPutFileName + "/" + "repaired_mappings",
					outPutFileName,
					//OutPutFilesManager.AllFormats,
					//OutPutFilesManager.OAEIFormat,
					format,
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
			
			
			//mapping_manager.setStringAnchors();
			LogOutput.printAlways("Num repaired mappings: " + num_clean_mappings);
			LogOutput.print("\tNum repaired class mappings: " + num_clean_class_mappings);
			LogOutput.print("\tNum repaired object property mappings: " + num_clean_oprop_mappings);
			LogOutput.print("\tNum repaired data property mappings: " + num_clean_dprop_mappings);			
			LogOutput.print("\tNum repaired instance mappings: " + num_clean_instance_mappings);
			
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
	
	/**
	 * Returns the input set of mappings (NOT repaired)
	 * @return
	 */
	public Set<MappingObjectStr> getInputMappings(){
		return input_mappings;
	}
	
	/**
	 * Returns the real size of the repair: number of removed clauses
	 * @return
	 */
	public int getSizeOfRepair(){
		
		int clauses = 0;
		
		for (int ide1 : mapping_manager.getConflictiveMappings().keySet()){
			clauses += mapping_manager.getConflictiveMappings().get(ide1).size();
		}
		
		return clauses;
		
	}
	
	
	public void checkSatisfiabilityInputMappings() throws Exception {
		checkSatisfiabilityMappings(input_mappings);
	}
	
	public void checkSatisfiabilityCleanMappings() throws Exception {
		checkSatisfiabilityMappings(clean_mappings);
	}
	
	
	public void checkSatisfiabilityMappings(Set<MappingObjectStr> mappings) throws Exception {
		
		OWLOntology mappins_owl_onto = getOWLOntology4GivenMappings(mappings);
		
		
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
	
	
	
	public OWLOntology getOWLOntology4CleanMappings() throws Exception {
		return getOWLOntology4GivenMappings(clean_mappings);
	}
	
	
	
	/**
	 * Returns the clean mappings as an OWLOntology object.
	 * @return
	 * @throws Exception
	 */
	public OWLOntology getOWLOntology4GivenMappings(Set<MappingObjectStr> mappings) throws Exception {
		
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
			/*System.out.println("\tNum clean mappings: " + num_clean_mappings);
			System.out.println("\t\tNum clean class mappings: " + num_clean_class_mappings);
			System.out.println("\t\tNum clean object property mappings: " + num_clean_oprop_mappings);
			System.out.println("\t\tNum clean data property mappings: " + num_clean_dprop_mappings);			
			System.out.println("\t\tNum clean instance mappings: " + num_clean_instance_mappings);
			*/
			
			
			
		}
		catch (Exception e){
			System.err.println("Error keeping mappings...");
			e.printStackTrace();
		}
		
		
	}
	
	

	

	private static Set<MappingObjectStr> emptyMappings() throws Exception{
		
		Set<MappingObjectStr> mappings = new HashSet<MappingObjectStr>();
		
		
		mappings.add(new MappingObjectStr(
				"http://csu6325.cs.ox.ac.uk/ontologies/matching_21_05_2012/emptyOntology.owl#lala", 
				"http://csu6325.cs.ox.ac.uk/ontologies/matching_31_05_2012/ontology_31_05_2012__18_53_50_221#lala", 
				1.0,
				Utilities.EQ));
		
		return mappings;
		
		
	}
		
	
	
	/**
	 * 
	 * @deprecated Use instead mappings manager reader
	 * @param file
	 * @return
	 * @throws Exception
	 */
	private static Set<MappingObjectStr> readMappings(String file) throws Exception{
		
		
		Set<MappingObjectStr> mappings = new HashSet<MappingObjectStr>();
		
		ReadFile reader = new ReadFile(file);
		
		
		String line;
		String[] elements;
		
		line=reader.readLine();
		
		int dir;
		
		while (line!=null) {
			
			if (line.indexOf("|")<0){
				line=reader.readLine();
				continue;
			}
			
			elements=line.split("\\|");
			
			if (elements.length<4)
				continue;
			
			
			if (elements[2].equals(">")){
				dir = Utilities.R2L;
			}
			else if (elements[2].equals("<")){
				dir = Utilities.L2R;
			}
			else {
				dir = Utilities.EQ;
			}
			
			
			mappings.add(new MappingObjectStr(elements[0], elements[1], Double.valueOf(elements[3]), dir));
				
			line=reader.readLine();
		}		
		
		reader.closeBuffer();
		
		
		return mappings;
		
		
	}
	

	
	
	private static String getHelpMessage(){
		return "LogMap's repair facility requires 7 parameters:\n" +
				"\t1. IRI ontology 1. e.g.: http://myonto1.owl  or  file:/C://myonto1.owl  or  file:/usr/local/myonto1.owl\n" +
				"\t2. IRI ontology 2. e.g.: http://myonto2.owl  or  file:/C://myonto2.owl  or  file:/usr/local/myonto2.owl\n" +
				"\t3. Format mappings e.g.: OWL  or  RDF  or  TXT\n" +
				"\t4. Full IRI or full Path:\n" +
				"\t\ta. Full IRI of input mappings if OWL format. e.g.: file:/C://mymappings.owl  or  file:/usr/local/mymappings.owl  or http://mymappings.owl\n" +
				"\t\tb. Full path of input mappings if formats RDF or TXT. e.g.: C://mymappings.rdf  or  /usr/local/mymappings.txt\n" +
				"\t5. Full output path for the repaired mappings: e.g. /usr/local/output_path or C://output_path\n" +
				"\t6. Extract modules for repair?: true or false\n" +
				"\t7. Check satisfiability after repair using HermiT? true or false\n";		
				//"\t4. Classify the input ontologies together with the mappings. e.g. true or false";
	}
	

	
	
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
			
		
		
		try {
			
			String iri_onto1;
			String iri_onto2;
			String format_mappings;
			String input_mappings_path;
			String output_path;
			boolean overlapping;
			boolean satisfiability_check;
			
			MappingsReaderManager readermanager;
			OntologyLoader loader1;
			OntologyLoader loader2;
			
			/*
			if (args.length==1){
				
				if (args[0].toLowerCase().contains("help")){
					System.out.println("HELP:\n" + getHelpMessage());
					return;
				}
				
			}
			*/
			
			if (args.length!=7){
				System.out.println(getHelpMessage());
				return;
			}
			else{				
				iri_onto1=args[0];
				iri_onto2=args[1];
				format_mappings=args[2];
				input_mappings_path=args[3];
				output_path=args[4];
				overlapping=Boolean.valueOf(args[5]);
				satisfiability_check=Boolean.valueOf(args[6]);
				
			}
			
			
			
			LogOutput.printAlways("Loading ontologies...");
			loader1 = new OntologyLoader(iri_onto1);
			loader2 = new OntologyLoader(iri_onto2);
			LogOutput.printAlways("...Done");
			
			
			readermanager = new MappingsReaderManager(input_mappings_path, format_mappings);
			
			new LogMap2_RepairFacility(
					loader1.getOWLOntology(), 
					loader2.getOWLOntology(), 
					readermanager.getMappingObjects(),
					overlapping,
					false, //always optimal?
					satisfiability_check,
					output_path +  "/" + "mappings_repaired_with_LogMap");
			
			
			
			
		}
		catch (Exception e){
			e.printStackTrace();
		}
		

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

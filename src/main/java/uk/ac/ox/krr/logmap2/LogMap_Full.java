package uk.ac.ox.krr.logmap2;


import java.util.Calendar;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLOntology;

import uk.ac.ox.krr.logmap2.indexing.IndexManager;
import uk.ac.ox.krr.logmap2.indexing.ReasonerBasedIndexManager;
import uk.ac.ox.krr.logmap2.indexing.OntologyProcessing;
import uk.ac.ox.krr.logmap2.interactive.InteractiveProcess;
import uk.ac.ox.krr.logmap2.interactive.InteractiveProcessAmbiguity;
import uk.ac.ox.krr.logmap2.io.LogOutput;
import uk.ac.ox.krr.logmap2.io.OWLAlignmentFormat;
import uk.ac.ox.krr.logmap2.io.ReadFile;
import uk.ac.ox.krr.logmap2.lexicon.LexicalUtilities;
import uk.ac.ox.krr.logmap2.mappings.CandidateMappingManager;
import uk.ac.ox.krr.logmap2.mappings.MappingManager;
import uk.ac.ox.krr.logmap2.mappings.objects.MappingObjectStr;
import uk.ac.ox.krr.logmap2.overlapping.LexicalOverlappingExtractor;
import uk.ac.ox.krr.logmap2.overlapping.NoOverlappingExtractor;
import uk.ac.ox.krr.logmap2.overlapping.OverlappingExtractor;
import uk.ac.ox.krr.logmap2.reasoning.ReasonerManager;
import uk.ac.ox.krr.logmap2.reasoning.SatisfiabilityIntegration;
import uk.ac.ox.krr.logmap2.repair.AnchorsAssessmentFullReasoning;
import uk.ac.ox.krr.logmap2.statistics.StatisticsManager;
import uk.ac.ox.krr.logmap2.utilities.Utilities;



/**
 * This version of LogMap implements full reasoning capabilities
 * @author root
 *
 */
public class LogMap_Full {
	
	private OverlappingExtractor overlappingExtractor;
	
	private IndexManager index;
	
	private OntologyProcessing onto_process1;
	private OntologyProcessing onto_process2;
	
	private MappingManager mapping_extractor;
	
	private AnchorsAssessmentFullReasoning anchor_assessment;
	
	private InteractiveProcess interactiveProcessManager;
	
	//For precission and recall
	private Set<MappingObjectStr> mappings_gs = new HashSet<MappingObjectStr>();
	
	private LexicalUtilities lexicalUtilities = new LexicalUtilities();

	private String prefix4IRIs;

	private String gs_mappings;
	
	long init_global, init, fin;
	
	
	private int reasoner_id = ReasonerManager.HERMIT;
	
	
	
	
	/**
	 * 
	 */
	public LogMap_Full(
			String iri1_str, 
			String iri2_str, 
			String gs_mappings,
			int ReasonerID) throws Exception{
		
		this.reasoner_id = ReasonerID;
		this.gs_mappings = gs_mappings;
		//this.interactivityFile=interactivityFile;
		

		init_global = init = Calendar.getInstance().getTimeInMillis();
		
		//INIT LOGMAP: lex and precomp integer combinations
		InitLogMap();
		
		
		
		//Overlapping estimation
		OverlappingEstimation(iri1_str, iri2_str);
		

		//Indexes lexicon (IF creation) and structure
		IndexLexiconAndStructure();
		
		
		
		init = Calendar.getInstance().getTimeInMillis();
		//Only for statistical purposes (and interactivity)
		if (!gs_mappings.equals("")){
			loadMappingsGS();
		}
		//printStatisticsGoldStandard();
		
		
		
		//EXTRACT, CLEAN ANCHORS and INDEX INTLABELLING
		LogOutput.printAlways("\n\nCLEANING ANCHORS");
		LogOutput.printAlways("---------------------------");
		createAndCleanAnchors();
		
		
		//Extract new candidates, clean them and index labelling
		LogOutput.printAlways("\n\nCLEANING CANDIDATES 1");
		LogOutput.printAlways("---------------------------");
		createCandidateMappings();
		mapping_extractor.printStatisticsMappingEvaluation();
		
		
		//Anchors should be non ambiguous for interval labelling index 
		//LogOutput.print("AMBIGUOUS ANCHORS: " + areAnchorsdAmbiguous());
		
		
		
		StatisticsManager.setPrecisionAndRecallAnchors(mapping_extractor.getStringGoldStandardAnchors().size());				
		//Statistics before interactivity
		StatisticsManager.printStatisticsLogMap_mappings();
		
		
		
		//Before last cleaning!
		mapping_extractor.createObjectPropertyAnchors();
		mapping_extractor.createDataPropertyAnchors();
		
		
		//Delete inverted files for properties
		onto_process1.clearInvertedFiles4properties();
		onto_process2.clearInvertedFiles4properties();
		
		
		
		
		//INTERACTIVE PROCESS
		//Also weakened in previous iterations
		
		LogOutput.printAlways("\n\nCLEANING CANDIDATES 2");
		LogOutput.printAlways("---------------------------");
		createMappingsInteractiveProcess(false);
		
		
	
		
		//OUTPUT
		//P&R wrt GS
		if (!gs_mappings.equals("")){
			getPrecisionAndRecallMappings();
		}
		
		
		LogOutput.printAlways("Average time taxonomic queries: " + 
				index.getAvgTime4TaxCalls() + ". Total: " + index.getTime4TaxCalls()  + ". Num calls: " + index.getNumberOfTaxCalls());
		
		LogOutput.printAlways("Average time disjointness queries: " + 
				index.getAvgTime4DisjCalls() + ". Total: " + index.getTime4DisjCalls()  + ". Num calls: " + index.getNumberOfDisjCalls());
		
		
		
		fin = Calendar.getInstance().getTimeInMillis();
		LogOutput.printAlways("TOTAL TIME (s): " + (float)((double)fin-(double)init_global)/1000.0);
		
		
		//Clean index strcutires and others...
		//Only if no cleaning is necessary anymore
		//index.clearTaxonomicalStructures();
		
		impactIntegration();
		
		
	}
	
	private void impactIntegration() throws Exception {
		
		OWLOntology mappings_onto = getOWLOntology4Mappings();
		
		//We only have TBOX axioms
		
		System.out.println("ONTO 1 axioms: " + overlappingExtractor.getTBOXOverlappingOnto1().size());
		System.out.println("ONTO 2 axioms: " + overlappingExtractor.getTBOXOverlappingOnto2().size());
		System.out.println("Mappings: " + mappings_onto.getAxiomCount());
		
		
		
		SatisfiabilityIntegration sat_checker = new SatisfiabilityIntegration(
				overlappingExtractor.getTBOXOverlappingOnto1(), 
				overlappingExtractor.getTBOXOverlappingOnto2(),
				mappings_onto.getAxioms(),
				false,//Time_Out_Class
				false); //factory
		
		System.out.println("Num unsat classes: " + sat_checker.getNumUnsatClasses());
	}
	
	
	/**
	 * This method will be used to build an OWLOntology object to evaluate the impact
	 * 
	 * @return
	 * @throws Exception
	 */
	private OWLOntology getOWLOntology4Mappings() throws Exception{
	
		int ident2;
		
		OWLAlignmentFormat owlformat = new OWLAlignmentFormat("");
		
		int dir_mapping;
		
		for (int ide1 : getClassMappings().keySet()){
			for (int ide2 : getClassMappings().get(ide1)){
				
				dir_mapping = getDirClassMapping(ide1, ide2);
				
				if (dir_mapping!=Utilities.NoMap){
						
					//System.out.println(getIRI4ConceptIdentifier(ide1));
					//System.out.println(getIRI4ConceptIdentifier(ide2));
					//System.out.println(dir_mapping);
								
					if (dir_mapping!=Utilities.R2L){
						owlformat.addClassMapping2Output(
								getIRI4ConceptIdentifier(ide1),
								getIRI4ConceptIdentifier(ide2),
								dir_mapping,
								getConfidence4ConceptMapping(ide1, ide2)
								);
					}
					else{
						owlformat.addClassMapping2Output(
								getIRI4ConceptIdentifier(ide2),
								getIRI4ConceptIdentifier(ide1),								
								dir_mapping,
								getConfidence4ConceptMapping(ide1, ide2)
								);
					}
				}
			}
		}
		
		for (int ide1 : getDataPropMappings().keySet()){							
			owlformat.addDataPropMapping2Output(
					getIRI4DataPropIdentifier(ide1),
					getIRI4DataPropIdentifier(getDataPropMappings().get(ide1)),
						Utilities.EQ,  
						1.0
						);
		}
		
		for (int ide1 : getObjectPropMappings().keySet()){
				
			owlformat.addObjPropMapping2Output(
					getIRI4ObjectPropIdentifier(ide1),
					getIRI4ObjectPropIdentifier(getObjectPropMappings().get(ide1)),
						Utilities.EQ, 
						1.0
						);
		}
		
		return owlformat.getOWLOntology();
		
	}
	
	
	public Map<Integer, Set<Integer>> getClassMappings(){
		return mapping_extractor.getLogMapMappings();
		
	}
	
	public int getDirClassMapping(int ide1, int ide2){
		return mapping_extractor.getDirMapping(ide1, ide2);
	}
	
	
	public String getIRIOntology1(){
		return onto_process1.getOntoIRI();
	}
	
	public String getIRIOntology2(){
		return onto_process2.getOntoIRI();
	}

	public Map<Integer, Integer> getDataPropMappings(){
		return mapping_extractor.getDataPropertyAnchors();	
	}
	
	
	public Map<Integer, Integer> getObjectPropMappings(){
		return mapping_extractor.getObjectPropertyAnchors();	
	}
	
	
	public double getConfidence4ConceptMapping(int ide1, int ide2){
		return mapping_extractor.getConfidence4Mapping(ide1, ide2);
	}
	
	public double getConfidence4DataPropConceptMapping(int ide1, int ide2){
		return mapping_extractor.getConfidence4DataPropertyAnchor(ide1, ide2);
	}
	
	public double getConfidence4ObjectPropConceptMapping(int ide1, int ide2){
		return mapping_extractor.getConfidence4ObjectPropertyAnchor(ide1, ide2);
	}
	
	
	public String getIRI4ConceptIdentifier(int ide){
		return index.getIRIStr4ConceptIndex(ide);
	}
	
	public String getIRI4DataPropIdentifier(int ide){
		return index.getIRIStr4DataPropIndex(ide);
	}
	
	public String getIRI4ObjectPropIdentifier(int ide){
		return index.getIRIStr4ObjPropIndex(ide);
	}
	
	
	private void InitLogMap() throws Exception{
		
		//Show print outs
		LogOutput.showOutpuLog(false);
		
		
		lexicalUtilities.loadStopWords();
		
		if (Parameters.use_umls_lexicon)
			lexicalUtilities.loadUMLSLexiconResources();
		
		lexicalUtilities.setStemmer(); //creates stemmer object (Paice by default)
		
		fin = Calendar.getInstance().getTimeInMillis();
		LogOutput.print("Time initializing lexical utilities (s): " + (float)((double)fin-(double)init)/1000.0);
		
		
		//init = Calendar.getInstance().getTimeInMillis();
		//PrecomputeIndexCombination.preComputeIdentifierCombination();
		//fin = Calendar.getInstance().getTimeInMillis();
		//LogOutput.print("Time precomputing index combinations (s): " + (float)((double)fin-(double)init)/1000.0);
		
	}
	
	
	
	private void OverlappingEstimation(String iri1_str, String iri2_str) throws Exception{
		
		LogOutput.print("OVERLAPPING");
		init = Calendar.getInstance().getTimeInMillis();
				
		if (!Parameters.use_overlapping){
			overlappingExtractor = new NoOverlappingExtractor();
		}
		else{
			overlappingExtractor = new LexicalOverlappingExtractor(lexicalUtilities, true);
		}

		
		overlappingExtractor.createOverlapping(iri1_str, iri2_str);
		fin = Calendar.getInstance().getTimeInMillis();
		LogOutput.print("Time extracting overlapping (s): " + (float)((double)fin-(double)init)/1000.0);
	}
	
	
	
	private void IndexLexiconAndStructure() throws Exception{
		
		//Create Index and new Ontology Index...
		index = new ReasonerBasedIndexManager();
		
		
		//Process ontologies: lexicon and taxonomy (class) and IFs
		onto_process1 = new OntologyProcessing(overlappingExtractor.getOverlappingOnto1(), index, lexicalUtilities);
		onto_process2 = new OntologyProcessing(overlappingExtractor.getOverlappingOnto2(), index, lexicalUtilities);
		
		
		//Extracts lexicon
		init = Calendar.getInstance().getTimeInMillis();
		onto_process1.precessLexicon();
		onto_process2.precessLexicon();
		fin = Calendar.getInstance().getTimeInMillis();
		LogOutput.print("Time extracting lexicon and IF (s): " + (float)((double)fin-(double)init)/1000.0);
		
		//I guess can be deleted here
		lexicalUtilities.clearStructures();
		
		
		init = Calendar.getInstance().getTimeInMillis();
		//Init Mapping extractor: intersects IF and extract IF weak
		//mapping_extractor = new LexicalMappingExtractor(index, onto_process1, onto_process2);
		mapping_extractor = new CandidateMappingManager(index, onto_process1, onto_process2);
		
		//Statistics
		StatisticsManager.reInitValues();
		StatisticsManager.setMappingManager(mapping_extractor);
		
		
		mapping_extractor.intersectInvertedFiles();
		fin = Calendar.getInstance().getTimeInMillis();
		LogOutput.print("Time intersecting IF and extracting IF weak (s): " + (float)((double)fin-(double)init)/1000.0);
		
		//Clear ontology stemmed labels 
		onto_process1.clearStemmedLabels();
		onto_process2.clearStemmedLabels();
		
		//Extracts Taxonomy
		//Also extracts A^B->C
		init = Calendar.getInstance().getTimeInMillis();
		onto_process1.setTaxonomicData();
		onto_process2.setTaxonomicData();
		fin = Calendar.getInstance().getTimeInMillis();
		LogOutput.print("Time extracting structural information (s): " + (float)((double)fin-(double)init)/1000.0);
		
		
		//We only need the axioms to create merged ontology
		overlappingExtractor.keepOnlyTBOXOverlapping();
		
		
		//TODO Remove ide2class and class2ide??		
		//We keep ontology things
		//onto_process1.clearOntologyRelatedInfo();
		//onto_process2.clearOntologyRelatedInfo();

		
		
		
		//We first create weak anchors to be used for scopes
		init = Calendar.getInstance().getTimeInMillis();
		mapping_extractor.extractAllWeakMappings();
		fin = Calendar.getInstance().getTimeInMillis();
		LogOutput.print("Time creating all weak anchors (s): " + (float)((double)fin-(double)init)/1000.0);
		
		
		//Extract subsets from all weak mappings to evaluate later
		//----------------------------------------------------------
		init = Calendar.getInstance().getTimeInMillis();
		mapping_extractor.extractCandidatesSubsetFromWeakMappings();
		fin = Calendar.getInstance().getTimeInMillis();
		LogOutput.print("Time creating candidate subset of weak anchors (s): " + (float)((double)fin-(double)init)/1000.0);
		
		
		//Remove structures used for frequency extractor
		
		//Frequency structures
		onto_process1.clearFrequencyRelatedStructures();
		onto_process2.clearFrequencyRelatedStructures();
		index.clearSingleWordInvertedIndex();
		
		//Only used by frequency-like weak mappings
		index.clearStemmedAlternativeLabels4Classes();
		
		
	}
	
	
	
	
	private void createAndCleanAnchors() throws Exception{
		
		//CREATE ANCHORS
		//------------------------
		init = Calendar.getInstance().getTimeInMillis();
		mapping_extractor.createAnchors();
		
		//Create different groups: "exact", ambiguity and no_scope (different sets...). We will add them later (almost done)
		
		fin = Calendar.getInstance().getTimeInMillis();
		LogOutput.print("Time creating anchors (s): " + (float)((double)fin-(double)init)/1000.0);
		
		
		countAnchors();
	

		//ANCHORS ASSESSMENT
		//------------------------
		
		
		
		init = Calendar.getInstance().getTimeInMillis();
		anchor_assessment = new AnchorsAssessmentFullReasoning(
				reasoner_id, index, mapping_extractor, overlappingExtractor, true);
		
		//For statistics (Uncomment)
		//anchor_assessment.checkUnsatisfiability();
		//anchor_assessment.clearStructures();		
		
		anchor_assessment.classifyAndRepairUnsatisfiability();
	
		fin = Calendar.getInstance().getTimeInMillis();
		LogOutput.print("Time cleaning anchors with DL reasoner (s): " + (float)((double)fin-(double)init)/1000.0);
		
		//After repairing exact
		mapping_extractor.setExactAsFixed(true);
	
		
		
		//SET REASONER FOR INDEX
		//------------------------
		index.setJointReasoner(anchor_assessment.getReasoner());
		
		
		countAnchors();
		
	}
	
	
	private void countAnchors(){
		
		int numMappings = 0;
		
		for (int ide1: mapping_extractor.getLogMapMappings().keySet()){
			for (int ide2: mapping_extractor.getLogMapMappings().get(ide1)){
			
				if (ide1<ide2)
					numMappings++;
				
			}
			
			
			
		}
		
		LogOutput.print("\nNum Anchors: " + numMappings + "\n");
		
		
	}
	
	
	
	private void createCandidateMappings() throws Exception{
		
		//After this method we will have 3 sets: Mappings2Review with DandG, Mappings to ask user, and discarded mappoings
		mapping_extractor.createCandidates();
		
		
		//Delete Alt labels in class index
		//We won't extract more mappings
		index.clearAlternativeLabels4Classes();
		
		
		countAnchors();
		
		//CLEAN CANDIDATED
		//--------------------------
		init = Calendar.getInstance().getTimeInMillis();
	
		anchor_assessment.clearStructures();//From anchor assessment
		
		
		anchor_assessment = new AnchorsAssessmentFullReasoning(
				reasoner_id, index, mapping_extractor, overlappingExtractor, false);
		
		//For statistics (Uncomment)
		//anchor_assessment.checkUnsatisfiability();		
		//anchor_assessment.clearStructures();
				
		
		anchor_assessment.classifyAndRepairUnsatisfiability();
		
		fin = Calendar.getInstance().getTimeInMillis();
		LogOutput.print("Time cleaning new candidates DL Reasoner (s): " + (float)((double)fin-(double)init)/1000.0);
		
		//Merge mappings 2 review and anchors
		//Comment if no fixed anchors
		mapping_extractor.moveMappingsToReview2AnchorList();
		
		
		countAnchors();
		
		
		//Remove mappings to review
		mapping_extractor.getMappings2Review().clear();
		
		
		//SET REASONER FOR INDEX
		//------------------------
		index.setJointReasoner(anchor_assessment.getReasoner());
						
		
		
		//Assess mappings 2 ask user
		mapping_extractor.assessMappings2AskUser();
		
		
				
		//Get Anchor statistics (after 2 iterations and cleaning them)
		StatisticsManager.extractStatisticsAnchors();
	
		mapping_extractor.printHarDiscardedStatistics();
		
		
	}




	private void createMappingsInteractiveProcess(boolean useThreshold) throws Exception{
	
	
		//At this point we only have mappings to ask user
		init = Calendar.getInstance().getTimeInMillis();
		
		interactiveProcessManager = 
				new InteractiveProcessAmbiguity(
						index, mapping_extractor, false, false, 0, false,
						false,
						"");
		
		
		//No interactivity
		interactiveProcessManager.endInteractiveProcess(mapping_extractor.isFilterWithHeuristicsSecondLevelMappings()); //adds mappings selected by user and logmap heuristics

		fin = Calendar.getInstance().getTimeInMillis();
		LogOutput.print("Time interactive process (s): " + (float)((double)fin-(double)init)/1000.0);
		
		
		//Also index interval labelling?? Problem, no mappings 1-1 for index....
			
		
		
		//CLEAN INTERACTIVITY
		//--------------------------
		
		init = Calendar.getInstance().getTimeInMillis();
		
		anchor_assessment.clearStructures(); //from candidates
		
		
		anchor_assessment = new AnchorsAssessmentFullReasoning(
				reasoner_id, index, mapping_extractor, overlappingExtractor, false);
		
		// For statistics (Uncomment)
		//anchor_assessment.checkUnsatisfiability();
		//anchor_assessment.clearStructures();
		
		
		anchor_assessment.classifyAndRepairUnsatisfiability();
		
		fin = Calendar.getInstance().getTimeInMillis();
		LogOutput.print("Time cleaning interacrtive mappings DL reasoner (s): " + (float)((double)fin-(double)init)/1000.0);
		

		//TODO Merge mappings 2 review and anchors
		//Comment if no fixed anchors
		mapping_extractor.moveMappingsToReview2AnchorList();
		
		
		//Remove mappings to review
		mapping_extractor.getMappings2Review().clear();
	
	
	}
	
	
	
	
	/**
	 * Load Gold Standard Mappings
	 * @throws Exception
	 */
	private void loadMappingsGS() throws Exception{
	
		ReadFile reader = new ReadFile(gs_mappings);
		
		
		String line;
		String[] elements;
		
		line=reader.readLine();
		
		int index1;
		int index2;
		double confidence;
		
		while (line!=null) {
			
			if (line.indexOf("|")<0){
				line=reader.readLine();
				continue;
			}
			
			elements=line.split("\\|");
			
			
			//Necessary for precision and recall or only for GS cleaning
			index1=onto_process1.getIdentifier4ConceptName(Utilities.getEntityLabelFromURI(elements[0]));
			index2=onto_process2.getIdentifier4ConceptName(Utilities.getEntityLabelFromURI(elements[1]));
			
			
			if (index1>0 && index2>0){	//IN CASE IT DOES NOT EXISTS
				
				mapping_extractor.addMapping2GoldStandardAnchors(index1, index2);
				
			}
			
			mappings_gs.add(new MappingObjectStr(elements[0], elements[1]));
			mapping_extractor.getStringGoldStandardAnchors().add(new MappingObjectStr(elements[0], elements[1]));
			
				
			line=reader.readLine();
		}		
		
		reader.closeBuffer();

	}
	
	
	private void getPrecisionAndRecallMappings() throws Exception{

		
		Set <MappingObjectStr> intersection;
		
		
		double precision;
		double recall;
		
		mapping_extractor.setStringAnchors();
		
		
		StatisticsManager.setMFinal(mapping_extractor.getStringLogMapMappings().size());
		LogOutput.print("MAPPINGS: " + mapping_extractor.getStringLogMapMappings().size());
		
		
		//ALL UMLS MAPPINGS
		intersection=new HashSet<MappingObjectStr>(mapping_extractor.getStringLogMapMappings());
		intersection.retainAll(mappings_gs);
		
		StatisticsManager.setGoodMFinal(intersection.size());
		
		
		precision=((double)intersection.size())/((double)mapping_extractor.getStringLogMapMappings().size());
		recall=((double)intersection.size())/((double)mappings_gs.size());

		
		System.out.println("MAPPINGS: " + mapping_extractor.getStringLogMapMappings().size());
		System.out.println("WRT GS MAPPINGS");
		System.out.println("\tPrecision Mappings: " + precision);
		System.out.println("\tRecall Mapping: " + recall);
		System.out.println("\tF measure: " + (2*recall*precision)/(precision+recall));
		
		LogOutput.print("WRT GS MAPPINGS");
		LogOutput.print("\tPrecision Mappings: " + precision);
		LogOutput.print("\tRecall Mapping: " + recall);
		LogOutput.print("\tF measure: " + (2*recall*precision)/(precision+recall));
		
	
		
		Set <MappingObjectStr> difference;
        difference=new HashSet<MappingObjectStr>(mappings_gs);
        difference.removeAll(mapping_extractor.getStringLogMapMappings());
        //LogOutput.print("Difference in GS: " + difference.size());
        System.out.println("Difference in GS: " + difference.size());
        
        Set <MappingObjectStr> difference2;
        difference2=new HashSet<MappingObjectStr>(mapping_extractor.getStringLogMapMappings());
        difference2.removeAll(mappings_gs);
        //LogOutput.print("Difference in Candidates: " + difference2.size());
        System.out.println("Difference in Candidates: " + difference2.size());
        
        
        StatisticsManager.setMMissing(difference.size());
               
	}
	
	


}

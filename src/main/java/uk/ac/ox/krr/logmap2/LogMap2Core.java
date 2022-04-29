package uk.ac.ox.krr.logmap2;


import java.io.*;


import java.util.HashMap;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.Map;
import java.util.TreeSet;

import java.util.HashSet;


import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.semanticweb.owlapi.model.OWLDataFactory;

import uk.ac.manchester.syntactic_locality.ModuleExtractor;
import uk.ac.ox.krr.logmap2.background.CategoryMappingsLoader;
import uk.ac.ox.krr.logmap2.io.*;
import uk.ac.ox.krr.logmap2.reasoning.SatisfiabilityIntegration;
import uk.ac.ox.krr.logmap2.repair.*;
import uk.ac.ox.krr.logmap2.mappings.objects.*;
import uk.ac.ox.krr.logmap2.oaei.oracle.OracleManager;
import uk.ac.ox.krr.logmap2.overlapping.*;
import uk.ac.ox.krr.logmap2.owlapi.SynchronizedOWLManager;
import uk.ac.ox.krr.logmap2.utilities.Lib;
import uk.ac.ox.krr.logmap2.utilities.PrecomputeIndexCombination;

import uk.ac.ox.krr.logmap2.lexicon.LexicalUtilities;
import uk.ac.ox.krr.logmap2.utilities.Utilities;

import uk.ac.ox.krr.logmap2.indexing.*;
import uk.ac.ox.krr.logmap2.indexing.entities.ClassIndex;
import uk.ac.ox.krr.logmap2.interactive.*;

import uk.ac.ox.krr.logmap2.interactive.objects.MappingObjectInteractivity;
import uk.ac.ox.krr.logmap2.mappings.MappingManager;
import uk.ac.ox.krr.logmap2.mappings.CandidateMappingManager;

import uk.ac.ox.krr.logmap2.io.LogOutput;

import uk.ac.ox.krr.logmap2.statistics.*;


/**
 * This class implements LogMap 2 algorithm calling it main functionalities
 * 
 * @author ernesto
 *
 */
public class LogMap2Core {
	
	private OverlappingExtractor overlappingExtractor;
	
	private IndexManager index;
	
	private OntologyProcessing onto_process1;
	private OntologyProcessing onto_process2;
	
	//private AnchorExtraction mapping_extractor;
	private MappingManager mapping_extractor;
	
	private AnchorAssessment mappings_assessment;
	
	private InteractiveProcess interactiveProcessManager;
	
	//For precission and recall
	private Set<MappingObjectStr> mappings_gs = new HashSet<MappingObjectStr>();
	
	private LexicalUtilities lexicalUtilities = new LexicalUtilities();
	
	private CategoryMappingsLoader categoryMappingsLoader = new CategoryMappingsLoader();
	
	private OWLDataFactory dataFactory;

	private String prefix4IRIs;
	private String logmap_mappings_path="";
	private String gs_mappings="";
	
	long init;
	
	

	//boolean cleanD_G=true;

	
	boolean useInteractivity=true;
	boolean useHeuristics=true;
	boolean orderQuestions=true;
	int error_user = 0; //%error for user
	boolean record_interactivity=false;
	
	boolean ask_everything=false;
	
	boolean evaluate_impact=false;
	
	//boolean overlapping=true;
	//String interactivityFile;
	
	
	private static final Logger LOGGER = LoggerFactory.getLogger(LogMap2Core.class);

	
	/**
	 * OAEI constructor
	 * @param iri1_str
	 * @param iri2_str
	 * @throws Exception
	 */
	public LogMap2Core(
			String iri1_str, 
			String iri2_str) throws Exception{
		
		this(iri1_str, iri2_str, "", "", "", "", false, false, false, 0, false, false, false);
		
	}
	
	
	/**
	 * Constructor for tests: conference task with gold standard
	 * @param iri1_str
	 * @param iri2_str
	 * @param evaluate_impact
	 * @param file_gs Gold standard file
	 * @throws Exception
	 */
	public LogMap2Core(
			String iri1_str, 
			String iri2_str,
			boolean evaluate_impact,
			String file_gs) throws Exception{
		
		this(iri1_str, iri2_str, "", "", file_gs, "", false, false, false, 0, false, false, evaluate_impact);
		
	}
	
	
	/**
	 * Constructor for tests: conference/multifarm task
	 * @param iri1_str
	 * @param iri2_str
	 * @param evaluate_impact
	 */
	public LogMap2Core(
			String iri1_str, 
			String iri2_str,
			boolean evaluate_impact) throws Exception {
		
		this(iri1_str, iri2_str, "", "", "", "", false, false, false, 0, false, false, evaluate_impact); //eval impact
		
	}
	
	
	/**
	 * Constructor for tests: instance matching task
	 * @param iri1_str
	 * @param iri2_str
	 * @param output_path
	 * @param gs_file
	 * @param evaluate_impact
	 * @throws Exception
	 */
	public LogMap2Core(
			String iri1_str, 
			String iri2_str, 
			String output_path,
			String gs_file,
			boolean evaluate_impact) throws Exception {
		
		this(iri1_str, iri2_str, "", "", gs_file, output_path, false, false, false, 0, false, false, evaluate_impact); //eval impact
		
	}
	

	
	
	/**
	 * basic constructor: currently only from LogMap2_launch
	 * @param iri1_str
	 * @param iri2_str
	 * @param output_path
	 * @param eval_impact
	 * @throws Exception
	 */
	public LogMap2Core(
			String iri1_str, 
			String iri2_str, 
			String output_path,
			boolean eval_impact) throws Exception{
		
		this(iri1_str, iri2_str, "", "", "", output_path, false, false, false, 0, false, false, eval_impact);
		
	}
	
	
	
	/**
	 * Constructor from a java application
	 */
	public LogMap2Core(
			OWLOntology onto1,
			OWLOntology onto2,
			boolean only_achors) throws Exception{
		
		this(onto1, onto2, new HashSet<MappingObjectStr>(), only_achors);
		
	}
	
	
	/**
	 * Constructor from a java application
	 */
	public LogMap2Core(
			OWLOntology onto1,
			OWLOntology onto2) throws Exception{
		
		this(onto1, onto2, new HashSet<MappingObjectStr>(), false);
		
	}
	
	/**
	 * Constructor from a java application with input mapping (e.g. composed mappings)
	 */
	public LogMap2Core(
			OWLOntology onto1,
			OWLOntology onto2,
			Set<MappingObjectStr> input_mappings) throws Exception{
	
		this(onto1, onto2, input_mappings, false);
	}
	
	/**
	 * Constructor from a java application with input mapping (e.g. composed mappings). If validated_input_mappings = true, 
	 * then the input mappings are added to the anchor set as they are  
	 * @param onto1
	 * @param onto2
	 * @param input_mappings
	 * @param are_input_mappings_validated
	 * @throws Exception
	 */
	public LogMap2Core(
			OWLOntology onto1,
			OWLOntology onto2,
			Set<MappingObjectStr> input_mappings,
			boolean are_input_mappings_validated) throws Exception{
		
		
		boolean only_anchors=false;
		
		//we keep true in order to not to delete modules overlappings
		//See IndexLexiconAndStructure
		//Furthremore we may want to evaluate impact at some point
		//Although in this constructor it does NIT evaluate impact
		evaluate_impact=true;
		
		//dataFactory = OWLManager.getOWLDataFactory();
		dataFactory = SynchronizedOWLManager.createOWLDataFactory();
		
		
				
		StatisticsTimeMappings.setInitGlobalTime();
		init = StatisticsTimeMappings.getCurrentTimeInMillis();
		
		
		//INIT LOGMAP: lex and precomp integer combinations
		InitLogMap();
		
		
		//Overlapping estimation
		OverlappingEstimation(onto1, onto2);
		

		//Indexes lexicon (IF creation) and structure
		IndexLexiconAndStructure(input_mappings);
				
		
		LogOutput.printAlways("Time Parsing and Index Lexicon (s): " + StatisticsTimeMappings.getRunningTime(init));
							
		
		//EXTRACT, CLEAN ANCHORS and INDEX INTLABELLING
		createAndCleanAnchors(are_input_mappings_validated);
		//System.out.println("Anchors completed");
		
		if (!only_anchors){
			//Extract new candidates (Level 1), clean them and index labelling
			createCandidateMappings();
			//System.out.println("Candidate mappings completed");
			
			
			//Extract more candidates Level 2 (If interactivity is active then those are the mappings to ask)
			createCandidateMappingsLevel2();
			//System.out.println("Candidate mappings level 2 completed");
			
			//Last Cleaning
			//lastLogicalCleaning();
			
			
			
			if (Parameters.perform_property_matching){
				createAndAssessPropertyMappings();
			}			
			//System.out.println("Property mappings completed.");
			
			//Optional (see parameters file)
			if (Parameters.perform_instance_matching){
				createAndAssessInstanceMappings();
			}
			//System.out.println("Instance mappings completed.");
			
			
			//System.out.println("TOTAL MATCHING TIME (s): " + StatisticsTimeMappings.getTotalRunningTime());
			//System.out.println("LogMap 2 Total Matching Time (s): " + StatisticsTimeMappings.getTotalRunningTime());
			
			
			//Clean index structures and others...
			//Not here because OAEI and call from other application
			//index.clearTaxonomicalStructures();		
		}
		
		
	}
	
	
	/**
	 * Constructor for test (implementation process)
	 * @param iri1_str
	 * @param iri2_str
	 * @param iri1_str_out
	 * @param iri2_str_out
	 * @param gs_mappings
	 * @param logmap_mappings_path
	 * @param useInteractivity
	 * @param useHeuristics
	 * @param orderQuestions
	 * @throws Exception
	 */
	public LogMap2Core(
			String iri1_str, 
			String iri2_str, 
			String iri1_str_out, 
			String iri2_str_out, 
			String gs_mappings, 
			String logmap_mappings_path,
			boolean useInteractivity,
			boolean useHeuristics, //should be an input parameter
			boolean orderQuestions,
			int error_user,
			boolean ask_everything,
			boolean record_interactivity,
			boolean evaluate_impact) throws Exception{
		
		
		this.logmap_mappings_path = logmap_mappings_path;
		this.gs_mappings = gs_mappings;
		//this.interactivityFile=interactivityFile;
		
		this.useInteractivity=useInteractivity;
		this.useHeuristics=useHeuristics;
		this.orderQuestions=orderQuestions;
		
		this.error_user = error_user;
		
		this.record_interactivity=record_interactivity;
		
		this.ask_everything=ask_everything;
		
		
		this.evaluate_impact=evaluate_impact;
		
		
		//dataFactory = OWLManager.getOWLDataFactory();
		dataFactory = SynchronizedOWLManager.createOWLDataFactory();
		
		if (logmap_mappings_path.startsWith("/"))
			prefix4IRIs = "file:";
		else
			prefix4IRIs = "file:/";
		
		
		
		StatisticsTimeMappings.setInitGlobalTime();
		init = StatisticsTimeMappings.getCurrentTimeInMillis();
		
		
		//INIT LOGMAP: lex and precomp integer combinations
		InitLogMap();
		
		
		LOGGER.info("LogMap: ontology loading and overlapping extraction");
		
		//Overlapping estimation
		OverlappingEstimation(iri1_str, iri2_str);
		//OverlappingEstimation(iri1_str, iri2_str, iri1_str_out, iri2_str_out);//test
		//if (true)
		//	return;
		
		
		LOGGER.info("LogMap indexation");
		
		//Indexes lexicon (IF creation) and structure
		IndexLexiconAndStructure();
		
		
		LogOutput.printAlways("Time Parsing and Index Lexicon (s): " + StatisticsTimeMappings.getRunningTime(init));
		
		
		
		
		//Only for statistical purposes (and interactivity)
		if (!gs_mappings.equals("")){
			loadMappingsGS();
		}
		//printStatisticsGoldStandard();
		
		
		//EXTRACT, CLEAN ANCHORS and INDEX INTLABELLING
		LOGGER.info("LogMap anchor extraction");
		createAndCleanAnchors();
		
		
		//Extract new candidates (Level 1), clean them and index labelling
		LOGGER.info("LogMap class mapping extraction");
		createCandidateMappings();
		//mapping_extractor.printStatisticsMappingEvaluation();
		
		
		//Anchors should be non ambiguous for interval labelling index 
		//LogOutput.print("AMBIGUOUS ANCHORS: " + areAnchorsdAmbiguous());
		
		
		
		StatisticsManager.setPrecisionAndRecallAnchors(mapping_extractor.getStringGoldStandardAnchors().size());				
		//Statistics before interactivity
		StatisticsManager.printStatisticsLogMap_mappings();
		
		
		
		//Extract more candidates Level 2 (If interactivity is active then those are the mappings to ask)
		//Also weakened in previous iterations
		createCandidateMappingsLevel2();
		
		
		//mapping_extractor.printAllDicoveredMappingsStatistics();				
		//mapping_extractor.printHarDiscardedStatistics();	
		//mapping_extractor.printStatisticsMappingEvaluation();//|Everything
		
		
		StatisticsManager.printMappingsAskedHeur();
		
		
		//mapping_extractor.printStatisticsLogMap_mappings();
		
		//Tests
		//outputMappings(mapping_extractor.getAnchors(), logmap_mappings_path + "all_extracted.txt");
		//outputMappings(mapping_extractor.getDircardedAnchors(), logmap_mappings_path + "discarded.txt");
		//orderedOutputMappings(true);   //For LUCADA
		
		
		
		//Last cleaning just in case
		//lastLogicalCleaning();
		
		
		if (Parameters.perform_property_matching){
			LOGGER.info("LogMap property mapping extraction");
			createAndAssessPropertyMappings();
		}
		
		
		//Optional (see parameters file)
		if (Parameters.perform_instance_matching){
			
			LOGGER.info("LogMap instance mapping extraction");
			
			createAndAssessInstanceMappings();
			
			if (Parameters.output_instance_mapping_files && !logmap_mappings_path.equals("")){
				
				outputInstaceMappings4Evaluation();
				
			}
			
		}
		
		
				
		//TODO Delete OntologyProcessing if possible
			
	
		//OUTPUT
		//P&R wrt GS
		if (!gs_mappings.equals("")){
			getPrecisionAndRecallMappings();
		}
		
		
		File file_folder = new File(logmap_mappings_path);
		
		
		if (!logmap_mappings_path.equals("") && file_folder.exists() && file_folder.isAbsolute()){ //Some experiments do not provide path...
			
			//if (!file_folder.isAbsolute()){
			//	logmap_mappings_path = file_folder.getAbsolutePath();
			//}
			
			//FINAL OVERLAPPING
			//Extracts new overlapping for anchors and saves overlapping as OWL files
			LogOutput.print("Creating overlapping output");
			createOutput4Overlapping(true); //TODO UNCOMMENT (just removed for tests)
			
			//Mapping OUTPUTFILES
			LogOutput.print("Saving output mapping files");
			saveExtractedMappings("logmap2_mappings");
		}
		else{
			//System.err.println("The given output path is not absolute or it does not exist. The output mappings cannot be stored.");
			LogOutput.print("The given output path is not absolute or it does not exist. The output mappings cannot be stored.");
		}
		
		
		
		LogOutput.print("Average time taxonomic queries: " + 
				index.getAvgTime4TaxCalls() + ". Total: " + index.getTime4TaxCalls()  + ". Num calls: " + index.getNumberOfTaxCalls());
		
		LogOutput.print("Average time disjointness queries: " + 
				index.getAvgTime4DisjCalls() + ". Total: " + index.getTime4DisjCalls()  + ". Num calls: " + index.getNumberOfDisjCalls());
		
		
		
		
		//System.out.println("Number of computed mappings: " + getLogMapMappings());
		LogOutput.printAlways("Number of computed mappings: " + getLogMapMappings().size());
		
		//System.out.println("TOTAL MATCHING TIME (s): " + StatisticsTimeMappings.getTotalRunningTime());
		LogOutput.printAlways("LogMap 2 Total Matching Time (s): " + StatisticsTimeMappings.getTotalRunningTime());
		
		
		if (evaluate_impact){
			init = StatisticsTimeMappings.getCurrentTimeInMillis();
			//impactIntegration();
			impactIntegration(iri1_str, iri2_str);			
			LogOutput.printAlways("Time checking impact integration (s): " + StatisticsTimeMappings.getRunningTime(init));
		}
		
		
		
		
		
		//Clean index structures and others...
		//Not here because OAEI
		//index.clearTaxonomicalStructures();
		//Since we need to return the mappings! We cannot remove the index!
		
		
	}
	
	SatisfiabilityIntegration sat_checker;
	boolean hasUnSat = false;
	int numUnsat = 0;
	
	public boolean hasUnsatClasses(){
		
		return hasUnSat;
	}
	
	public int getNumUnsatClasses(){
		
		return numUnsat;
	}
	
	
	private void impactIntegration() throws Exception {
		
		OWLOntology mappins_owl_onto = getOWLOntology4Mappings();
		
		LogOutput.print(overlappingExtractor.getOverlappingOnto1().getAxiomCount());
		LogOutput.print(overlappingExtractor.getOverlappingOnto2().getAxiomCount());
		LogOutput.print(mappins_owl_onto.getAxiomCount());
		
		
		
		sat_checker = new SatisfiabilityIntegration(
				overlappingExtractor.getOverlappingOnto1(), 
				overlappingExtractor.getOverlappingOnto2(),
				mappins_owl_onto,
				true,//Time_Out_Class
				false); //use factory
		
		hasUnSat=sat_checker.hasUnsatClasses();
		numUnsat=sat_checker.getNumUnsatClasses();
		
		LogOutput.print("Num unsat classes: " + sat_checker.getNumUnsatClasses());
		
		
		/*ImpactAuditedIntegration impact = new ImpactAuditedIntegration();
		
		try{
			impact.reasonWithGivenOntologiesHermit(
				overlappingExtractor.getOverlappingOnto1(), 
				overlappingExtractor.getOverlappingOnto2(),
				getOWLOntology4Mappings());
		}
		catch (Exception e){
			System.err.println("HermiT could not deal with these ontologies: " + e.getMessage());
		}
		
		try{
			impact.reasonWithGivenOntologiesPellet(
				overlappingExtractor.getOverlappingOnto1(), 
				overlappingExtractor.getOverlappingOnto2(),
				getOWLOntology4Mappings());
		}
		catch (Exception e){
			System.err.println("Pellet could not deal with these ontologies: " + e.getMessage());
		}
		*/
		
		
	}
	
	private void impactIntegration(String iri1, String iri2) throws Exception {
		
		OWLOntology mappings_owl_onto = getOWLOntology4Mappings();
		
		OntologyLoader loader1 = new OntologyLoader(iri1);
		OntologyLoader loader2 = new OntologyLoader(iri2);
		
		LogOutput.printAlways("Evaluating impact...");
				
		LogOutput.print("Axioms onto1: " + loader1.getOWLOntology().getAxiomCount());
		LogOutput.print("Axioms onto2: " + loader2.getOWLOntology().getAxiomCount());
		LogOutput.print("Mapping Axioms: " + mappings_owl_onto.getAxiomCount());
		
		
		
		sat_checker = new SatisfiabilityIntegration(
				loader1.getOWLOntology(), 
				loader2.getOWLOntology(),
				mappings_owl_onto,
				true,//Time_Out_Class
				false); //use factory
		
		
		hasUnSat=sat_checker.hasUnsatClasses();
		numUnsat=sat_checker.getNumUnsatClasses();
		
		//OWLManager.createOWLOntologyManager().saveOntology(mappings_owl_onto, new RDFXMLOntologyFormat(),
				//IRI.create("file:/usr/local/data/MappingsConferenceBenchmark/ontologies/mappings-cmt-confof.owl"));
		//		IRI.create("file:/usr/local/data/MappingsConferenceBenchmark/ontologies/mappings-edas-ekaw.owl"));
		System.out.println("\nNum unsat classes after integration: " + sat_checker.getNumUnsatClasses());
		//LogOutput.printAlways
	}
	
	
	
	/**
	 * This method will be used to build an OWLOntology object to evaluate the logical impact
	 * The ontology will not be stored (see @saveExtractedMappings())
	 * 
	 * @return
	 * @throws Exception
	 */
	public OWLOntology getOWLOntology4Mappings() throws Exception{
	
		int ident2;
		
		OWLAlignmentFormat owlformat = new OWLAlignmentFormat("");
		
		int dir_mapping;
		
		for (int ide1 : getClassMappings().keySet()){
			for (int ide2 : getClassMappings().get(ide1)){
				
				//LogOutput.print(getIRI4ConceptIdentifier(ide1));
				//LogOutput.print(getIRI4ConceptIdentifier(ide2));
				//LogOutput.print(getConfidence4ConceptMapping(ide1, ide2));
				//LogOutput.print("");
				
				dir_mapping = getDirClassMapping(ide1, ide2);
				
				if (dir_mapping!=Utilities.NoMap){
						
					//System.out.println(getIRI4ConceptIdentifier(ide1));
					//System.out.println(getIRI4ConceptIdentifier(ide2));
					//System.out.println(dir_mapping);
								
					//TODO No need to reverse ids. Done in OWLAlignmentFormat
					//if (dir_mapping!=Utilities.R2L){
						owlformat.addClassMapping2Output(
								getIRI4ConceptIdentifier(ide1),
								getIRI4ConceptIdentifier(ide2),
								dir_mapping,
								getConfidence4ConceptMapping(ide1, ide2)
								);
					/*}
					else{
						owlformat.addClassMapping2Output(
								getIRI4ConceptIdentifier(ide2),
								getIRI4ConceptIdentifier(ide1),								
								dir_mapping,
								getConfidence4ConceptMapping(ide1, ide2)
							);
					}*/
				}
			}
		}
		
		for (int ide1 : getDataPropMappings().keySet()){
			
			
			
			//System.out.println(getIRI4DataPropIdentifier(ide1));
			//System.out.println(getIRI4DataPropIdentifier(getDataPropMappings().get(ide1)));
			//System.out.println(getConfidence4DataPropConceptMapping(ide1, getDataPropMappings().get(ide1)));
			
			
			owlformat.addDataPropMapping2Output(
					getIRI4DataPropIdentifier(ide1),
					getIRI4DataPropIdentifier(getDataPropMappings().get(ide1)),
					Utilities.EQ,  
					getConfidence4DataPropConceptMapping(ide1, getDataPropMappings().get(ide1))//1.0
				);
		}
		
		for (int ide1 : getObjectPropMappings().keySet()){
			
			//System.out.println(getIRI4ObjectPropIdentifier(ide1));
			//System.out.println(getIRI4ObjectPropIdentifier(getObjectPropMappings().get(ide1)));
			//System.out.println(getConfidence4ObjectPropConceptMapping(ide1, getObjectPropMappings().get(ide1)));
			//LogOutput.print("");
				
			owlformat.addObjPropMapping2Output(
					getIRI4ObjectPropIdentifier(ide1),
					getIRI4ObjectPropIdentifier(getObjectPropMappings().get(ide1)),
					Utilities.EQ, 
					getConfidence4ObjectPropConceptMapping(ide1, getObjectPropMappings().get(ide1))//1.0
				);
		}
		

		if (Parameters.perform_instance_matching){
			
			for (int ide1 : getInstanceMappings().keySet()){
				for (int ide2 : getInstanceMappings().get(ide1)){
				
					owlformat.addInstanceMapping2Output(
							getIRI4InstanceIdentifier(ide1), 
							getIRI4InstanceIdentifier(ide2), 
							getConfidence4InstanceMapping(ide1, ide2)
						);
					
				}
			}
		}
		
		return owlformat.getOWLOntology();
		
	}
	
	
	
	private boolean areAnchorsdAmbiguous(){
		
		for (int ide : mapping_extractor.getLogMapMappings().keySet()){
			
			if (mapping_extractor.getLogMapMappings().get(ide).size()>1){
				LogOutput.print(ide + "  " + mapping_extractor.getLogMapMappings().get(ide));
				return true;
			}
			
		}
		
		return false;
		
		
	}
	
	
	
	
	private void InitLogMap() throws Exception{
		
		//Show print outs
		LogOutput.showOutpuLog(Parameters.print_output); //False for OAEI?
		LogOutput.showOutpuLogAlways(Parameters.print_output || Parameters.print_output_always);
		
		
		
		//Only from OAEI-LogMap
		//Parameters.readParameters();
		
		//We load both sets
		lexicalUtilities.loadStopWords();
		lexicalUtilities.loadStopWordsExtended();
		
		if (Parameters.use_umls_lexicon)
			lexicalUtilities.loadUMLSLexiconResources();
		
		lexicalUtilities.setStemmer(); //creates stemmer object (Paice by default)
		
		//Lib.debuginfo(LexicalUtilities.getStemming4Word("Prolactin") + " " + LexicalUtilities.getStemming4Word("brachii") + "\n");
		
		
		
		LogOutput.print("Time initializing lexical utilities (s): " + StatisticsTimeMappings.getRunningTime(init));
		StatisticsTimeMappings.addUpperbound_mappings_time(StatisticsTimeMappings.getRunningTime(init));
		
		
		//init = StatisticsTimeMappings.getCurrentTimeInMillis();
		//PrecomputeIndexCombination.preComputeIdentifierCombination();
		//
		//LogOutput.print("Time precomputing index combinations (s): " + StatisticsTimeMappings.getRunningTime(init));
		
	}
	
	
	
	private void OverlappingEstimation(String iri1_str, String iri2_str) throws Exception{
		
		LogOutput.print("OVERLAPPING");
		init = StatisticsTimeMappings.getCurrentTimeInMillis();
		
		//Overlapping to store and compare 
		//overlappingExtractor = new LexicalOverlappingExtractor(gs_mappings, logmap_mappings, iri1_str_out, iri2_str_out);
		
		//Overlapping to be used in the normal behaviour
		//No overlapping if size onto <10,000 entities (see parameters file)
		if (!Parameters.use_overlapping){
			overlappingExtractor = new NoOverlappingExtractor();
		}
		else{
			overlappingExtractor = new LexicalOverlappingExtractor(lexicalUtilities, true);
		}

		
		overlappingExtractor.createOverlapping(iri1_str, iri2_str);
		
		LogOutput.printAlways("Entities ontology 1: " + overlappingExtractor.getBaseOverlappedEntities1().size());
		LogOutput.printAlways("Entities ontology 2: " + overlappingExtractor.getBaseOverlappedEntities2().size());
		
		LogOutput.print("Time extracting overlapping (s): " + StatisticsTimeMappings.getRunningTime(init));
	}
	
	private void OverlappingEstimation(OWLOntology onto1, OWLOntology onto2) throws Exception{
		
		LogOutput.print("OVERLAPPING");
		init = StatisticsTimeMappings.getCurrentTimeInMillis();
		
		//Overlapping to store and compare 
		//overlappingExtractor = new LexicalOverlappingExtractor(gs_mappings, logmap_mappings, iri1_str_out, iri2_str_out);
		
		//Overlapping to be used in the normal behaviour
		//No overlapping if size onto <10,000 entities (see parameters file)
		if (!Parameters.use_overlapping){
			overlappingExtractor = new NoOverlappingExtractor();
		}
		else{
			overlappingExtractor = new LexicalOverlappingExtractor(lexicalUtilities, true);
		}

		
		overlappingExtractor.createOverlapping(onto1, onto2);
		
		LogOutput.print("Time extracting overlapping (s): " + StatisticsTimeMappings.getRunningTime(init));
	}
	
	
	private void IndexLexiconAndStructure() throws Exception{
		IndexLexiconAndStructure(new HashSet<MappingObjectStr>());
	}
	
	private void IndexLexiconAndStructure(Set<MappingObjectStr> input_mappings) throws Exception{
		
		//Create Index and new Ontology Index...
		index = new JointIndexManager();
		
		
		//Process ontologies: lexicon and taxonomy (class) and IFs
		onto_process1 = new OntologyProcessing(overlappingExtractor.getOverlappingOnto1(), index, lexicalUtilities);
		onto_process2 = new OntologyProcessing(overlappingExtractor.getOverlappingOnto2(), index, lexicalUtilities);
		
		
		//Extracts lexicon
		init = StatisticsTimeMappings.getCurrentTimeInMillis();
		onto_process1.precessLexicon();
		onto_process2.precessLexicon();
		
		LogOutput.print("Time extracting lexicon and IF (s): " + StatisticsTimeMappings.getRunningTime(init));
		
		//I guess can be deleted here
		lexicalUtilities.clearStructures();
		
		
		init = StatisticsTimeMappings.getCurrentTimeInMillis();
		//Init Mapping extractor: intersects IF and extract IF weak
		//mapping_extractor = new LexicalMappingExtractor(index, onto_process1, onto_process2);
		mapping_extractor = new CandidateMappingManager(index, onto_process1, onto_process2);
		
		//Statistics
		StatisticsManager.reInitValues();
		StatisticsManager.setMappingManager(mapping_extractor);
		
		
		//TODO Category mappings
		categoryMappingsLoader.loadMappings(Parameters.path_mappings_categories);
		mapping_extractor.setCategoryMappings(categoryMappingsLoader.getCategoryMappings());
		
		
		//Input mappings (e.g. composed mappings using MO given as input)
		mapping_extractor.processInputMappings(input_mappings);
		
		
		mapping_extractor.intersectInvertedFiles();
		
		LogOutput.print("Time intersecting IF and extracting IF weak (s): " + StatisticsTimeMappings.getRunningTime(init));
		//System.out.println("Time intersecting IF and extracting IF weak (s): " + StatisticsTimeMappings.getRunningTime(init));
		
		//Clear ontology stemmed labels 
		onto_process1.clearStemmedLabels();
		onto_process2.clearStemmedLabels();
		
		//Extracts Taxonomy
		//Also extracts A^B->C
		init = StatisticsTimeMappings.getCurrentTimeInMillis();
		onto_process1.setTaxonomicData();
		onto_process2.setTaxonomicData();
		
		LogOutput.print("Time extracting structural information (s): " + StatisticsTimeMappings.getRunningTime(init));
		
		
		//TODO Remove!!
		//if (!gs_mappings.equals("")){
		//	loadMappingsGS();
		//}
		
		
		
		//Keep only TBOX axioms to extract final module
		if (logmap_mappings_path.equals("")){ // no output files for overlappings
			if (!evaluate_impact){
				overlappingExtractor.clearModulesOverlapping();//Not necessary if we do no provide output
			}
		}
		else {
			overlappingExtractor.keepOnlyTBOXOverlapping(!evaluate_impact);
		}
		
		
		
		
		//We do not need the references to OWLEntities anymore
		onto_process1.clearOntologyRelatedInfo();
		onto_process2.clearOntologyRelatedInfo();

		/*
		loadMappingsGS(gs_mappings);
		printStatisticsGoldStandard();
		if (true)
		{
			LogOutput.print("finished!");
			return ;
		}
		*/
		
		
		//We first create weak anchors to be used for scopes
		init = StatisticsTimeMappings.getCurrentTimeInMillis();
		mapping_extractor.extractAllWeakMappings();
		
		LogOutput.print("Time creating all weak anchors (s): " + StatisticsTimeMappings.getRunningTime(init));
		
		
		//Extract subsets from all weak mappings to evaluate later
		//----------------------------------------------------------
		init = StatisticsTimeMappings.getCurrentTimeInMillis();
		mapping_extractor.extractCandidatesSubsetFromWeakMappings();
		
		LogOutput.print("Time creating candidate subset of weak anchors (s): " + StatisticsTimeMappings.getRunningTime(init));
		
		
		//Remove structures used for frequency extractor
		
		//Frequency structures
		onto_process1.clearFrequencyRelatedStructures();
		onto_process2.clearFrequencyRelatedStructures();
		index.clearSingleWordInvertedIndex();
		
		//Only used by frequency-like weak mappings
		index.clearStemmedAlternativeLabels4Classes();
		
		
	}
	
	
	private void createAndCleanAnchors() throws Exception{
		createAndCleanAnchors(false);
	}
	
	
	
	private void createAndCleanAnchors(boolean are_input_mapping_validated) throws Exception{
		
		LogOutput.printAlways("\nANCHOR DIAGNOSIS ");
		
		
		//TODO test for mouse 2 anatomy
		//Background knowledge: mappings composition
		//((CandidateMappingManager)mapping_extractor).createCandidatesFromBackgroundKnowledge();
		//getPrecisionAndRecallMappings();
		
		
		init = StatisticsTimeMappings.getCurrentTimeInMillis();
		mapping_extractor.createAnchors(are_input_mapping_validated);
		
		//Create different groups: "exact", ambiguity and no_scope (different sets...). We will add them later (almost done)
		
		
		LogOutput.printAlways("Time creating anchors (s): " + StatisticsTimeMappings.getRunningTime(init));
		
		
		
		
		//if (true){
		//	return ;
		//}
		
		//Tests
		//outputMappings(mapping_extractor.getAnchors(), logmap_mappings_path + "good.txt");
		
		
		
		
		countAnchors();
		mappings_assessment = new AnchorAssessment(index, mapping_extractor);
		
				
		if (Parameters.cleanD_G){
		
			//Only for statistical purposes or to extract global infor			
			if (Parameters.extractGlobal_D_G_Info){
				init = StatisticsTimeMappings.getCurrentTimeInMillis();
				mappings_assessment.CountSatisfiabilityOfIntegration_DandG(mapping_extractor.getLogMapMappings());
				
				LogOutput.printAlways("Time checking satisfiability D&G (s): " + StatisticsTimeMappings.getRunningTime(init));
			}
			
			
			init = StatisticsTimeMappings.getCurrentTimeInMillis();
			mappings_assessment.CheckSatisfiabilityOfIntegration_DandG(mapping_extractor.getLogMapMappings());
			
			LogOutput.printAlways("Time cleaning anchors D&G (s): " + StatisticsTimeMappings.getRunningTime(init));
		}
		
		//After repairing exact
		mapping_extractor.setExactAsFixed(true);
		mapping_extractor.saveAnchors(); //we save anchors in new structure so that we can retrieve them at the end of the process
		
		countAnchors();
		
		
		//TODO extract further disjointness based on weak anchors and mappings
		
		
		
		//INTERVAL LABELLING SCHEMA
		//--------------------------
		init = StatisticsTimeMappings.getCurrentTimeInMillis();
		
		//Index already have the necessary taxonomical information apart from the equiv mappings

		index.setIntervalLabellingIndex(mapping_extractor.getFixedMappings());
		
		index.clearAuxStructuresforLabellingSchema();
		
		
		LogOutput.printAlways("Time indexing hierarchy + anchors (ILS) (s): " + StatisticsTimeMappings.getRunningTime(init));
		
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
		
		LogOutput.printAlways("\nCANDIDATE DIAGNOSIS 1");
		
		//After this method we will have 3 sets: Mappings2Review with DandG, Mappings to ask user, and discarded mappoings
		
		init = StatisticsTimeMappings.getCurrentTimeInMillis();
		mapping_extractor.createCandidates();
		
		
		//TODO
		//Delete Alt labels in class index
		//We won't extract more mappings
		//TODO Remove later on to extract lexical scores
		//index.clearAlternativeLabels4Classes();
		
		
		LogOutput.printAlways("Time creating candidates (s): " + StatisticsTimeMappings.getRunningTime(init));
		
		
		//Uncomment if no fixed anchors
		//We add candidates to anchors, nothing noew is fixed
		//mapping_extractor.setExactAsFixed(false);
		//mapping_extractor.moveMappingsToReview2AnchorList();
		
		
		//Clean with Dawling and Gallier mappings 2 review
		//-- D&G
		//countAnchors();
		//mappings_assessment = new AnchorAssessment(index, mapping_extractor);
		init = StatisticsTimeMappings.getCurrentTimeInMillis();
		if (Parameters.cleanD_G){
			
			
			//Only for statistical purposes or to extract global infor			
			if (Parameters.extractGlobal_D_G_Info){
				init = StatisticsTimeMappings.getCurrentTimeInMillis();
				mappings_assessment.CountSatisfiabilityOfIntegration_DandG(mapping_extractor.getMappings2Review());
				
				LogOutput.printAlways("Time checking satisfiability D&G (s): " + StatisticsTimeMappings.getRunningTime(init));
			}
			
			//Adds clean mappings to anchors. Conflictive and split mappings to respective sets
			init = StatisticsTimeMappings.getCurrentTimeInMillis();
			mappings_assessment.CheckSatisfiabilityOfIntegration_DandG(mapping_extractor.getMappings2Review());  //With Fixed mappings!
			//mappings_assessment.CheckSatisfiabilityOfIntegration_DandG(mapping_extractor.getAnchors());  //No fixed mappings
			
			LogOutput.printAlways("Time cleaning new candidates D&G (s): " + StatisticsTimeMappings.getRunningTime(init));
		}
		
		//Merge mappings 2 review and anchors
		//Comment if no fixed anchors
		mapping_extractor.moveMappingsToReview2AnchorList();
		
		
		
	
		
		
		countAnchors();
		
		
		//Remove mappings to review
		mapping_extractor.getMappings2Review().clear();
		
		
		//INTERVAL LABELLING SCHEMA
		//--------------------------
		init = StatisticsTimeMappings.getCurrentTimeInMillis();
		index.setIntervalLabellingIndex(mapping_extractor.getLogMapMappings());//It also contains mappings 2 review
		index.clearAuxStructuresforLabellingSchema();
		
		LogOutput.printAlways("Time indexing hierarchy + anchors and candidates I (ILS) (s): " + StatisticsTimeMappings.getRunningTime(init));
		
		//LogOutput.print("\n\nNEW UNSAT:");
		//To check not solved cases of unsatisfiability
		//mappings_assessment.CheckSatisfiabilityOfConcreteClasses_DandG(mapping_extractor.getAnchors(), index.getUnsatisfiableClassesILS());
		
		//Assess mappings 2 ask user
		mapping_extractor.assessMappings2AskUser();
		
				
		//Get Anchor statistics (after 2 iterations and cleaning them)
		StatisticsManager.extractStatisticsAnchors();
		
		
		
		
		
		
		
	}
		
	
	/**
	 * Only for interactivity testing
	 */
	private void createCandidateMappingsInteractiveProcess2(){
		
		
		boolean useThreshold=false;
		
		//At this point we only have mappings to ask user
		init = StatisticsTimeMappings.getCurrentTimeInMillis();
		
		
		if (!useThreshold)
			interactiveProcessManager = 
				new InteractiveProcessAmbiguity(
						index, mapping_extractor, useHeuristics, orderQuestions, error_user, ask_everything,
						record_interactivity,
						logmap_mappings_path + "SimulationInteractivity_" + useInteractivity + "_" + useHeuristics + "_" + orderQuestions + ".txt");
		else 
			interactiveProcessManager = new InteractiveProcessThreshold(index, mapping_extractor);
		
		if (useInteractivity){
			interactiveProcessManager.startInteractiveProcess(); //Starts "automatic" user interaction			
		}
		//TODO!!!!
		interactiveProcessManager.endInteractiveProcess(mapping_extractor.isFilterWithHeuristicsSecondLevelMappings()); //adds mappings selected by user and logmap heuristics
		/*else {
			for (MappingObjectInteractivity mapping : mapping_extractor.getListOfMappingsToAskUser()){
				
				mapping_extractor.addSubMapping2Mappings2Review(mapping.getIdentifierOnto1(), mapping.getIdentifierOnto2());
				mapping_extractor.addSubMapping2Mappings2Review(mapping.getIdentifierOnto2(), mapping.getIdentifierOnto1());
				
			}
		}*/
		
		
		
		
		LogOutput.print("Time interactive process (s): " + StatisticsTimeMappings.getRunningTime(init));

		
		
	}
	
	
	/**
	 * Uses an oracle for the interactivity (i.e GS)
	 */
	private void createCandidateMappingsInteractiveProcess(){
				
		InteractiveProcessOAEI interactivityOAEI = 
				new InteractiveProcessOAEI(index, mapping_extractor, true, false); //we ask everything to OAEI tests
		
		//We strat asking to oracle and we also try to apply automatic decisions 
		interactivityOAEI.startInteractiveProcess();
		
		//We add to list of accepted mappings the validated mappings
		interactivityOAEI.endInteractiveProcess();
		
		
		//Adhoc method
		//askAll2AOracle()
		
		
	}
	
	
	/**
	 * @deprecated
	 */
	private void askAll2AOracle(){
		//Adhoc method ask everything
		//------------------------------------
		for (MappingObjectInteractivity mapping : mapping_extractor.getListOfMappingsToAskUser()){
					
			if (OracleManager.isMappingValid(
					index.getIRIStr4ConceptIndex(mapping.getIdentifierOnto1()),
					index.getIRIStr4ConceptIndex(mapping.getIdentifierOnto2()))){
						
				//SOME MAPPINGS MAY REPRESENT ONLY ONE SIDE
				//EITHER THE USER DECIDED TO SPLIT IT or IT WAS SPLIT BY D&G
				if (mapping.getDirMapping()==Utilities.EQ || mapping.getDirMapping()==Utilities.L2R)
					mapping_extractor.addSubMapping2Mappings2Review(mapping.getIdentifierOnto1(), mapping.getIdentifierOnto2());
						
				if (mapping.getDirMapping()==Utilities.EQ || mapping.getDirMapping()==Utilities.R2L)
					mapping_extractor.addSubMapping2Mappings2Review(mapping.getIdentifierOnto2(), mapping.getIdentifierOnto1());
					
			}
		}
	}
	
	
	
	
	private boolean hasScopeAll(MappingObjectInteractivity m){
		
		return (mapping_extractor.extractScopeAll4Mapping(m.getIdentifierOnto1(), m.getIdentifierOnto2())>Parameters.bad_score_scope);
		
	}
	
	private boolean hasGoodConfidence(MappingObjectInteractivity m){
		
		return (mapping_extractor.getConfidence4Mapping(m.getIdentifierOnto1(), m.getIdentifierOnto2())>Parameters.good_confidence);
		
	}
	
	/**
	 * Automatic decisions to mappings to ask if interactivity is not active
	 */
	private void performAutomaticDecisions(){

		for (MappingObjectInteractivity mapping : mapping_extractor.getListOfMappingsToAskUser()){
			
			
			//See createMappings2AskUser in mapping_extractor for more information about the use of this filter
			if (!mapping_extractor.isFilterWithHeuristicsSecondLevelMappings() 
				|| hasScopeAll(mapping) && hasGoodConfidence(mapping)){
				
				//SOME MAPPINGS MAY REPRESENT ONLY ONE SIDE
				//EITHER THE USER DECIDED TO SPLIT IT or IT WAS SPLIT BY D&G
				if (mapping.getDirMapping()==Utilities.EQ || mapping.getDirMapping()==Utilities.L2R)
					mapping_extractor.addSubMapping2Mappings2Review(mapping.getIdentifierOnto1(), mapping.getIdentifierOnto2());
				
				if (mapping.getDirMapping()==Utilities.EQ || mapping.getDirMapping()==Utilities.R2L)
					mapping_extractor.addSubMapping2Mappings2Review(mapping.getIdentifierOnto2(), mapping.getIdentifierOnto1());
			
			}
		}
				
		//mapping_extractor.setStringAnchors(); ???
			
	}
	
	
	
	private void createCandidateMappingsLevel2() throws Exception{
		
		
		LogOutput.printAlways("\nCANDIDATE DIAGNOSIS 2");
					
		if (OracleManager.isActive()){
			LogOutput.printAlways("Oracle is active for interactivity.");
			createCandidateMappingsInteractiveProcess();
		}
		else{
			LogOutput.printAlways("Oracle is not active. Performing automatic decisions.");
			performAutomaticDecisions();
		}
		
		
		//Also index interval labelling?? Problem, no mappings 1-1 for index....
		//We keep previous index
		
		
		
		
		//Add weakened mappings by D and G iff no conflictive or already inferred
		//Add no conflictive to anchors and the clean together with interactivity
		//We should add a low confidence so that if they are involved in an error then remove
		//isMappingWeakenedDandG
		//!!!Already Included in assesMappings2AskUser!!!
		//mapping_extractor.assesWeakenedMappingsDandG(true, false); //we add them to mappings to review
		
		
		//Retrieve mappings from conflictive mappings if not in conflict
		//Then add to mapping to review
		if (Parameters.second_chance_conflicts)
			secondChanceConflictiveMappingsD_G();
		
		
		
		
		
		
		//Clean interactive-like mappings + weakened with DandG		
		
		init = StatisticsTimeMappings.getCurrentTimeInMillis();
		if (Parameters.cleanD_G){
			
			
			//Only for statistical purposes or to extract global infor			
			if (Parameters.extractGlobal_D_G_Info){
				init = StatisticsTimeMappings.getCurrentTimeInMillis();
				mappings_assessment.CountSatisfiabilityOfIntegration_DandG(mapping_extractor.getMappings2Review());
				
				LogOutput.printAlways("Time checking satisfiability D&G (s): " + StatisticsTimeMappings.getRunningTime(init));
			}
			
			//Adds clean mappings to anchors. Conflictive and split mappings to respective sets
			init = StatisticsTimeMappings.getCurrentTimeInMillis();
			mappings_assessment.CheckSatisfiabilityOfIntegration_DandG(mapping_extractor.getMappings2Review());  //With Fixed mappings!
			//mappings_assessment.CheckSatisfiabilityOfIntegration_DandG(mapping_extractor.getAnchors());  //No fixed mappings
			
			LogOutput.printAlways("Time cleaning interactive mappings D&G (s): " + StatisticsTimeMappings.getRunningTime(init));
		}
		
		//Merge mappings 2 review and anchors
		//Comment if no fixed anchors
		mapping_extractor.moveMappingsToReview2AnchorList();
		
		
		//Add new weakened mappings
		//Do not add them? Since they are suspicious>?
		//mapping_extractor.assesWeakenedMappingsDandG(false, true); //we add them to anchors
		
		
		//Remove mappings to review
		mapping_extractor.getMappings2Review().clear();
		
		
		
	}
	
	/**
	 * This method aims at giving a second chance to those mappings involved in a conflict
	 * and removed using the D&G method. Some of them may be good and not cause an error
	 */
	private void secondChanceConflictiveMappingsD_G(){
		
		LogOutput.print("Second chance to mappings discarded by Dowling and Gallier.");
		
		int second_chance = 0;
		int good_second_chance = 0;
		
		Map<Integer, Set<Integer>> toDelete = new HashMap<Integer, Set<Integer>>();
		
		
		//Check conflicts with fixed mappings
		//--------------------------------------
		for (int ide1 : mapping_extractor.getConflictiveMappings_D_G().keySet()){
		
			for (int ide2 : mapping_extractor.getConflictiveMappings_D_G().get(ide1)){
				
				if (mapping_extractor.isMappingInConflictWithFixedMappings(ide1, ide2)){
					
					if (!toDelete.containsKey(ide1)){
						toDelete.put(ide1, new HashSet<Integer>());
					}
					toDelete.get(ide1).add(ide2);
					

				}
				
			}
		}
		
		
		for (int ide1 : toDelete.keySet()){
			for (int ide2 : toDelete.get(ide1)){
				
				mapping_extractor.addSubMapping2ConflictiveAnchors(ide1, ide2);
				
				mapping_extractor.removeSubMappingFromConflictive_D_G(ide1, ide2);
			}
		}
		
		toDelete.clear();
				
				
		
		//Check if they are in conflict between them
		//If in conflict then check confidences and remove the one with less value
		//-----------------------------------------------------------------------------
		for (int ide1 : mapping_extractor.getConflictiveMappings_D_G().keySet()){
			
			for (int ide2 : mapping_extractor.getConflictiveMappings_D_G().get(ide1)){
				
				for (int ideA : mapping_extractor.getConflictiveMappings_D_G().keySet()){
					
					for (int ideB : mapping_extractor.getConflictiveMappings_D_G().get(ideA)){
						
						if (ide1==ideA && ide2==ideB)
							continue;
						
						if (mapping_extractor.areMappingsInConflict(ide1, ide2, ideA, ideB)){
						
							//Check confidences		
							if (mapping_extractor.getConfidence4Mapping(ide1, ide2) >=
									mapping_extractor.getConfidence4Mapping(ideA, ideB)){
							
								if (!toDelete.containsKey(ideA)){
									toDelete.put(ideA, new HashSet<Integer>());
								}
								toDelete.get(ideA).add(ideB);
							}
							else{
								
								if (!toDelete.containsKey(ide1)){
									toDelete.put(ide1, new HashSet<Integer>());
								}
								toDelete.get(ide1).add(ide2);
							}
						}
						
						
					}
				}
			}	
		}
		
		
		for (int ide1 : toDelete.keySet()){
			for (int ide2 : toDelete.get(ide1)){
				
				mapping_extractor.addSubMapping2ConflictiveAnchors(ide1, ide2);
				
				mapping_extractor.removeSubMappingFromConflictive_D_G(ide1, ide2);
			}
		}
		toDelete.clear();
		
		
		
		//Check conflicts with mappings 2 review
		//--------------------------------------
		for (int ide1 : mapping_extractor.getConflictiveMappings_D_G().keySet()){
					
			for (int ide2 : mapping_extractor.getConflictiveMappings_D_G().get(ide1)){
				
				//Already filtered
				//if (mapping_extractor.isMappingInConflictWithFixedMappings(ide1, ide2)){
				//	mapping_extractor.addSubMapping2ConflictiveAnchors(ide1, ide2);
				//}
				//else { //If no conflict add to mappings to review
					
					//Check if there are mappings in conflict in mappings2review
					for (int ideA : mapping_extractor.getMappings2Review().keySet()){
						
						for (int ideB : mapping_extractor.getMappings2Review().get(ideA)){
							
							if (mapping_extractor.areMappingsInConflict(ide1, ide2, ideA, ideB)){
								
								//Remove mapping 2 review. We give priority to the anchor deleted by D&G
								if (!toDelete.containsKey(ideA)){
									toDelete.put(ideA, new HashSet<Integer>());
								}
								toDelete.get(ideA).add(ideB);		
										
							}
							
						}
					}
					//We remove them to facilitate D&G processs
					for (int ideA : toDelete.keySet()){
						for (int ideB : toDelete.get(ideA)){
							mapping_extractor.removeSubMappingFromMappings2Review(ideA, ideB);
						}
					}
					
					
					mapping_extractor.addSubMapping2Mappings2Review(ide1, ide2); //check with new ones
					
					if (mapping_extractor.isMappingInGoldStandard(ide1, ide2))
						good_second_chance++;
					
					second_chance++;
					
					 
					
					
				}
			//}
		}
		LogOutput.print("Mappings with second chance: " + second_chance + " in GS: " + good_second_chance);
		
		
	}
	
	
	/**
	 * We perform a last check using D&G
	 */
	private void lastLogicalCleaning(){
		
		if (Parameters.cleanD_G){
		
			//Adds clean mappings to anchors. Conflictive and split mappings to respective sets
			init = StatisticsTimeMappings.getCurrentTimeInMillis();
		
			mapping_extractor.setExactAsFixed(false);
			mappings_assessment.CheckSatisfiabilityOfIntegration_DandG(mapping_extractor.getLogMapMappings());  ///No fixed mappings: we clean everything just in case
			mapping_extractor.setExactAsFixed(true);
			
			
			LogOutput.printAlways("LAST CLEANING MAPPINGS D&G (s): " + StatisticsTimeMappings.getRunningTime(init));
			
		}
		
		
	}
	
	
	
	
	/**
	 * Discovery and assessment of DATA and OBJECT property mappings
	 */
	private void createAndAssessPropertyMappings(){
		mapping_extractor.createObjectPropertyAnchors();
		mapping_extractor.createDataPropertyAnchors();
		
		
		//Delete inverted files for properties
		onto_process1.clearInvertedFiles4properties();
		onto_process2.clearInvertedFiles4properties();
	}
	
	
	/**
	 * Discovery and assessment of Instance mappings
	 */
	private void createAndAssessInstanceMappings(){
				
		
		//Check allowed class for instances
		int type_id;
		for (String uri: Parameters.allowed_instance_types){
			type_id = onto_process1.getIdentifier4ConceptIRI(uri);
			if (type_id>=0)				
				index.addAllowedInstanceType(type_id);
			
			type_id = onto_process2.getIdentifier4ConceptIRI(uri);
			if (type_id>=0)				
				index.addAllowedInstanceType(type_id);
		}
		//System.out.println("Checcking allowed types completed.");
		
		
		mapping_extractor.createInstanceAnchors();
		
		//Clean D&G
		if (mapping_extractor.getInstanceMappings().size()>0 && Parameters.cleanD_G){
			
			init = StatisticsTimeMappings.getCurrentTimeInMillis();
			
			//We have an specific method since there is not a top-down search. And we first repair classes
			mappings_assessment.CheckSatisfiabilityOfIntegration_DandG_Individuals(
					mapping_extractor.getInstanceMappings());
			
			
			LogOutput.printAlways("Time cleaning instance mappings D&G (s): " + StatisticsTimeMappings.getRunningTime(init));
		}
		
		
		onto_process1.clearInvertedFiles4Individuals();
		onto_process2.clearInvertedFiles4Individuals();
	
	
	}
	
	
	
	Set<OWLEntity> signature_onto1 = new HashSet<OWLEntity>();
	Set<OWLEntity> signature_onto2 = new HashSet<OWLEntity>();
	
	private void createSignatureFromMappings(Map<Integer, Set<Integer>> mappings){
		
		for (int idea : mappings.keySet()){
			for (int ideb : mappings.get(idea)){
				
				if (mapping_extractor.isId1SmallerThanId2(idea, ideb)){
					
					signature_onto1.add(
							dataFactory.getOWLClass(index.getIRI4ConceptIndex(idea)));
					
					//System.out.println(index.getIRI4ConceptIndex(idea));
					
					
					signature_onto2.add(
							dataFactory.getOWLClass(index.getIRI4ConceptIndex(ideb)));
					
				}
				else{ 
					
					//Not alresdy included
					if (!mapping_extractor.isMappingAlreadyInList(ideb, idea)){
					
						signature_onto1.add(
								dataFactory.getOWLClass(index.getIRI4ConceptIndex(ideb)));

						//System.out.println(index.getIRI4ConceptIndex(ideb));
						
						signature_onto2.add(
								dataFactory.getOWLClass(index.getIRI4ConceptIndex(idea)));
						
					}
					
					
				}
			}
		}
		
	}
	
	/**
	 * This method creates the OWL files correspondent to the overlapping/module files.
	 * Uses Old extractor
	 */
	private void createOutput4Overlapping(boolean use_discarded){
		
		
		createSignatureFromMappings(mapping_extractor.getLogMapMappings());
		if (use_discarded)
			createSignatureFromMappings(mapping_extractor.getDiscardedMappings());
		
		
		
		//EXTRACT MODULE 1
		ModuleExtractor module_extractor1 = new ModuleExtractor(
				overlappingExtractor.getTBOXOverlappingOnto1(), false, false, true, true, false);
		
		module_extractor1.getLocalityModuleForSignatureGroup(signature_onto1, onto_process1.getOntoIRI());		
		
		module_extractor1.saveExtractedModule(prefix4IRIs + logmap_mappings_path + "/module1_overlapping_logmap2.owl"); //"Output/module1.owl"
		
		module_extractor1.clearStrutures();
		overlappingExtractor.getTBOXOverlappingOnto1().clear();
		signature_onto1.clear();
		
		
		//EXTRACT MODULE 2
		ModuleExtractor module_extractor2 = new ModuleExtractor(
				overlappingExtractor.getTBOXOverlappingOnto2(), false, false, true, true, false);
		
		module_extractor2.getLocalityModuleForSignatureGroup(signature_onto2, onto_process2.getOntoIRI());		
		
		module_extractor2.saveExtractedModule(prefix4IRIs + logmap_mappings_path + "/module2_overlapping_logmap2.owl"); //Output/module2.owl
		
		module_extractor2.clearStrutures();
		overlappingExtractor.getTBOXOverlappingOnto2().clear();	
		signature_onto2.clear();
		
		
		
		
	}
	
	
	
	private void saveExtractedMappings(String file_name){
		
		int dirMapping;
		
		OutPutFilesManager outPutFilesManager = new OutPutFilesManager();
		
		try {
			outPutFilesManager.createOutFiles(
					//logmap_mappings_path + "Output/mappings",
					logmap_mappings_path + "/" + file_name,
					OutPutFilesManager.AllFormats,
					onto_process1.getOntoIRI(),
					onto_process1.getOntoIRI());
			
			if (Parameters.output_class_mappings){
			
				for (int idea : mapping_extractor.getLogMapMappings().keySet()){
					for (int ideb : mapping_extractor.getLogMapMappings().get(idea)){
						
						//This is important to keep compatibility with OAEI and Flat alignment formats
						//The order of mappings is important
						//For OWL output would be the same since mappings are axioms
						if (mapping_extractor.isId1SmallerThanId2(idea, ideb)){
							
							if (mapping_extractor.isMappingAlreadyInList(ideb, idea)){
								dirMapping=Utilities.EQ;
							}
							else {
								dirMapping=Utilities.L2R;
							}
							
							outPutFilesManager.addClassMapping2Files(
									index.getIRIStr4ConceptIndex(idea),
									index.getIRIStr4ConceptIndex(ideb),
									dirMapping, 
									mapping_extractor.getConfidence4Mapping(idea, ideb));
						}
						else {
							if (mapping_extractor.isMappingAlreadyInList(ideb, idea)){
								//Do nothing
							}
							else {
								outPutFilesManager.addClassMapping2Files(
										index.getIRIStr4ConceptIndex(ideb),
										index.getIRIStr4ConceptIndex(idea),
										Utilities.R2L, 
										mapping_extractor.getConfidence4Mapping(idea, ideb));
							}
						}
					
						
					}
				}
			}
			
			
			if (Parameters.output_prop_mappings){
			
				for (int ide1 : getDataPropMappings().keySet()){							
					outPutFilesManager.addDataPropMapping2Files(
							getIRI4DataPropIdentifier(ide1),
							getIRI4DataPropIdentifier(getDataPropMappings().get(ide1)),
							Utilities.EQ,  
							getConfidence4DataPropConceptMapping(ide1, getDataPropMappings().get(ide1))//1.0
						);
				}
				
				for (int ide1 : getObjectPropMappings().keySet()){
						
					outPutFilesManager.addObjPropMapping2Files(
							getIRI4ObjectPropIdentifier(ide1),
							getIRI4ObjectPropIdentifier(getObjectPropMappings().get(ide1)),
							Utilities.EQ, 
							getConfidence4ObjectPropConceptMapping(ide1, getObjectPropMappings().get(ide1))//1.0
						);
				}
			}
			
			

			if (Parameters.perform_instance_matching && Parameters.output_instance_mappings){
				
				for (int ide1 : getInstanceMappings().keySet()){
					for (int ide2 : getInstanceMappings().get(ide1)){
					
						outPutFilesManager.addInstanceMapping2Files(
								getIRI4InstanceIdentifier(ide1), 
								getIRI4InstanceIdentifier(ide2), 
								getConfidence4InstanceMapping(ide1, ide2)
							);
						
					}
				
				}
			}
			
			
			
			outPutFilesManager.closeAndSaveFiles();
			
		}
		catch (Exception e){
			System.err.println("Error saving mappings...");
			e.printStackTrace();
		}
		
		
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
		
		int wrong=0;
		
		while (line!=null) {
			
			if (line.indexOf("|")<0 && line.indexOf("\t")<0){
				line=reader.readLine();
				continue;
			}
			
			if (line.indexOf("|")>=0)
				elements=line.split("\\|");
			else { // if (line.indexOf("\t")>=0){
				elements=line.split("\\t");
			}
			
			//TODO temporal, only for im assessment
			/*if (!overlappingExtractor.getOverlappingOnto1().containsEntityInSignature(IRI.create(elements[0]), true) ||
					!overlappingExtractor.getOverlappingOnto2().containsEntityInSignature(IRI.create(elements[1]), true)){
				//LogOutput.printAlways("Wrong mapping: " + elements[0] + "  " + elements[1]);			
				wrong++;
				line=reader.readLine();
				continue;
			}*/
			
			
			
			//Necessary for preccsion and recall or only for GS cleaning
			index1=onto_process1.getIdentifier4ConceptName(Utilities.getEntityLabelFromURI(elements[0]));
			index2=onto_process2.getIdentifier4ConceptName(Utilities.getEntityLabelFromURI(elements[1]));
			
			if (index1>0 && index2>0){	//IN CASE IT DOES NOT EXISTS
				
				mapping_extractor.addMapping2GoldStandardAnchors(index1, index2);
				
				//if (umls_assessment)
				//	identifier2exactMapping.add(new MappingObjectIdentifiers(index1, index2));
				
			}
			
			//System.out.println(elements[0] + "  " + elements[1]);
			
			mappings_gs.add(new MappingObjectStr(elements[0], elements[1]));
			mapping_extractor.getStringGoldStandardAnchors().add(new MappingObjectStr(elements[0], elements[1]));
			
			
				
			line=reader.readLine();
		}		
		
		reader.closeBuffer();
		
		LogOutput.printAlways("Wrong mappings: " + wrong);
		LogOutput.printAlways("Gold Standard:" + mappings_gs.size());

	}
	
	
	
	
	
	
	
	
	
	
	
	double precision=0.0;
	double recall=0.0;
	double fmeasure=0.0;
	
	public double getPrecision(){
		return precision;
	}
	public double getRecall(){
		return recall;
	}
	public double getFmeasure(){
		return fmeasure;
	}
	
	
	
	public Set<MappingObjectStr> getLogMapMappings(){
		
		mapping_extractor.setStringAnchors(
				Parameters.output_class_mappings,
				Parameters.output_prop_mappings,
				Parameters.output_instance_mappings);
		
		return mapping_extractor.getStringLogMapMappings();
	}
	
	
	/*public Set<MappingObjectStr> getDiscardedLogMapMappings(){
		
		mapping_extractor.setDiscardedStringAnchors();
		
		return mapping_extractor.getDiscardedMappingsStr();
	}*/
	
	
	
	private void getPrecisionAndRecallMappings() throws Exception{

		
		Set <MappingObjectStr> intersection;
		
		
		//double precision;
		//double recall;
		
		mapping_extractor.setStringAnchors(
				Parameters.output_class_mappings,
				Parameters.output_prop_mappings,
				Parameters.output_instance_mappings);
		
		
		StatisticsManager.setMFinal(mapping_extractor.getStringLogMapMappings().size());
		LogOutput.printAlways("MAPPINGS: " + mapping_extractor.getStringLogMapMappings().size());
		
		
		//ALL UMLS MAPPINGS
		intersection=new HashSet<MappingObjectStr>(mapping_extractor.getStringLogMapMappings());
		intersection.retainAll(mappings_gs);
		
		StatisticsManager.setGoodMFinal(intersection.size());
		
		
		precision=((double)intersection.size())/((double)mapping_extractor.getStringLogMapMappings().size());
		recall=((double)intersection.size())/((double)mappings_gs.size());

		//String dir = "/auto/users/yzhou/LogMapStuff/Test/FMA2NCI/";
		/*if (!logmap_mappings_path.equals("")){
			String dir = logmap_mappings_path;
			BufferedWriter writer = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(dir + "_itersection.txt")));
			for (MappingObjectStr obj : intersection)
				writer.write(obj.getIRIStrEnt1() + "|" + obj.getIRIStrEnt2() + " " + obj.getConfidence() + "\n");
			writer.close();
	
			writer = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(dir + "_add_bad.txt")));
			for (MappingObjectStr obj : mapping_extractor.getStringAnchors())
				if (!intersection.contains(obj))
					writer.write(obj.getIRIStrEnt1() + "|" + obj.getIRIStrEnt2() + " " + obj.getConfidence() + "\n");
			writer.close();
	
			writer = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(dir + "_lose_good.txt")));
			for (MappingObjectStr obj : mappings_gs)
				if (!intersection.contains(obj))
					writer.write(obj.getIRIStrEnt1() + "|" + obj.getIRIStrEnt2() + "\n");
			writer.close();
		}*/
		
		fmeasure=(2*recall*precision)/(precision+recall);
		
		System.out.println("WRT GS MAPPINGS");
		System.out.println("\tPrecision Mappings: " + precision);
		System.out.println("\tRecall Mapping: " + recall);
		System.out.println("\tF measure: " + (2*recall*precision)/(precision+recall));
		
		LogOutput.print("WRT GS MAPPINGS");
		LogOutput.print("\tPrecision Mappings: " + precision);
		LogOutput.print("\tRecall Mapping: " + recall);
		LogOutput.print("\tF measure: " + (2*recall*precision)/(precision+recall));
		
		
		
		//
		/*getPrecisionandRecallMappings4Subset(0.95);
		getPrecisionandRecallMappings4Subset(0.80);
		getPrecisionandRecallMappings4Subset(0.50);
		getPrecisionandRecallMappings4Subset(0.20);
		*/
		
		
		int max_print;
		int prints;
		
		Set <MappingObjectStr> difference;
        difference=new HashSet<MappingObjectStr>(mappings_gs);
        difference.removeAll(mapping_extractor.getStringLogMapMappings());
        //LogOutput.print("Difference in GS: " + difference.size());
        LogOutput.printAlways("Difference in GS: " + difference.size());
        LogOutput.printAlways("\tPrinting only the first 250");
        max_print=250;
        prints=0;
        //if (difference.size()<250){
	        for (MappingObjectStr mapping : difference){
	        	//LogOutput.print("\t" + mapping.getIRIStrEnt1() + "--" + mapping.getIRIStrEnt2());
	        	prints++;
	        	if (prints>=max_print)
	        		break;
	        }
	    //}
        Set <MappingObjectStr> difference2;
        difference2=new HashSet<MappingObjectStr>(mapping_extractor.getStringLogMapMappings());
        difference2.removeAll(mappings_gs);
        //LogOutput.print("Difference in Candidates: " + difference2.size());
        LogOutput.printAlways("Difference in Candidates: " + difference2.size());
        LogOutput.printAlways("\tPrinting only the first 100");
        max_print=100;
        prints=0;
       // if (difference2.size()<100){
	        for (MappingObjectStr mapping : difference2){
	        	//LogOutput.print("\t" + mapping.getIRIStrEnt1() + "--" + mapping.getIRIStrEnt2());
	        	prints++;
	        	if (prints>=max_print)
	        		break;
	        }
        //}
        
        
        StatisticsManager.setMMissing(difference.size());
               
        
       
      
	}
	
	
	
	private void clearStructures(){
		
		//TODO
		
		
		
	}
	
	
	
	
	
	
	private void outputInstaceMappings4Evaluation() throws Exception{
		
		//Only exact mappings (this method is only for statistics purposes)
		
		FlatAlignmentFormat good_file = new FlatAlignmentFormat(logmap_mappings_path + "/logmap_instance_mappings.txt");
		FlatAlignmentFormat disc1_file = new FlatAlignmentFormat(logmap_mappings_path + "/discarded1_instance_mappings.txt");
		FlatAlignmentFormat disc2_file = new FlatAlignmentFormat(logmap_mappings_path + "/discarded2_instance_mappings.txt");
		FlatAlignmentFormat incomp_file = new FlatAlignmentFormat(logmap_mappings_path + "/incompatible_instance_mappings.txt");
		
		int type;
		
		for (int ide1 : mapping_extractor.getInstanceMappings4OutputType().keySet()) {
		
			for (int ide2 : mapping_extractor.getInstanceMappings4OutputType().get(ide1).keySet()){
			
				
				type = mapping_extractor.getInstanceMappings4OutputType().get(ide1).get(ide2);
				
				
				if (type==0){
				
					good_file.addInstanceMapping2Output(
							index.getIRIStr4IndividualIndex(ide1), 
							index.getIRIStr4IndividualIndex(ide2),
							"=", 
							mapping_extractor.getISUB4InstanceMapping(ide1, ide2),
							mapping_extractor.getCompFactor4InstanceMapping(ide1, ide2),
							mapping_extractor.getScope4InstanceMapping(ide1, ide2));
					
				}
				else if (type==1){
				
					disc1_file.addInstanceMapping2Output(
							index.getIRIStr4IndividualIndex(ide1), 
							index.getIRIStr4IndividualIndex(ide2),
							"=", 
							mapping_extractor.getISUB4InstanceMapping(ide1, ide2),
							mapping_extractor.getCompFactor4InstanceMapping(ide1, ide2),
							mapping_extractor.getScope4InstanceMapping(ide1, ide2));
					
				}
				else if (type==2){
				
					disc2_file.addInstanceMapping2Output(
							index.getIRIStr4IndividualIndex(ide1), 
							index.getIRIStr4IndividualIndex(ide2),
							"=", 
							mapping_extractor.getISUB4InstanceMapping(ide1, ide2),
							mapping_extractor.getCompFactor4InstanceMapping(ide1, ide2),
							mapping_extractor.getScope4InstanceMapping(ide1, ide2));
					
				}
				else if (type==3){
				
					incomp_file.addInstanceMapping2Output(
							index.getIRIStr4IndividualIndex(ide1), 
							index.getIRIStr4IndividualIndex(ide2),
							"=", 
							mapping_extractor.getISUB4InstanceMapping(ide1, ide2),
							mapping_extractor.getCompFactor4InstanceMapping(ide1, ide2),
							mapping_extractor.getScope4InstanceMapping(ide1, ide2));
					
				}
				
				
				
				
			}
			
		}
		
		good_file.saveOutputFile();
		disc1_file.saveOutputFile();
		disc2_file.saveOutputFile();
		incomp_file.saveOutputFile();
		
		
	}
		
		
	
	
	
	
	
	private void outputMappings(Map<Integer, Set<Integer>> mappings, String fileName) throws Exception{
		
		//Only exact mappings (this method is only for statistics purposes)
		
		FlatAlignmentFormat outputFile = new FlatAlignmentFormat(fileName);
		
		LogOutput.print(fileName);
		
		String inGS;
		
		for (int idea : mappings.keySet()){
			
			for (int ideb : mappings.get(idea)){
				
				if (idea<ideb){
					
					
					if (mapping_extractor.isMappingInGoldStandard(idea, ideb)){
						inGS="YES";
					}
					else{
						inGS="NO";
					}
					
					//Onto 1 to onto2
					//if (mapping_extractor.hasWeakMappingSim(idea, ideb)){//Only weak mappings
						outputFile.addClassMapping2Output(
								index.getName4ConceptIndex(idea), 
								index.getName4ConceptIndex(ideb),
								Utilities.EQ, 
								mapping_extractor.getSimWeak4Mapping2(idea, ideb),
								mapping_extractor.extractScopeAll4Mapping(idea, ideb) +"|"+ 
								mapping_extractor.extractISUB4Mapping(idea, ideb) +"|"+ inGS);
					//}					
				}
				
			}
			
		}
		

		
		outputFile.saveOutputFile();
		
		
		
	}
	
	
	
	
	private void orderedOutputMappings(boolean include_discarded) throws Exception{
		
		TreeSet<MappingObjectInteractivity> ordered_output_mappings = 
				new TreeSet<MappingObjectInteractivity>(new ComparatorConfidence());
		
		
		MappingObjectInteractivity mapping;
		
		Iterator<MappingObjectInteractivity> it;
		
		WriteFile writer;
		
		String inGS;
		
		
		//ORDER ANCHORS
		for (int idea : mapping_extractor.getLogMapMappings().keySet()){
			
			for (int ideb : mapping_extractor.getLogMapMappings().get(idea)){
				
				if (idea<ideb){
					
					ordered_output_mappings.add(new MappingObjectInteractivity(idea, ideb));
					
				}
			}
		}
		
		//SAVE ANCHORS
		writer = new WriteFile("/usr/local/data/DataUMLS/UMLS_Onto_Versions/LUCADA/ordered_output_mappings_NCI2LUCADA.txt");
		
		
		writer.writeLine(
				"Label 1" + "|" +
				"Label 2" + "|" +
				"IRI 1" + "|" +
				"IRI 2" + "|" +
				"Confidence" + "|" +
				"ISUB (Lex. Sim.)"  + "|" +
				"Scope" + "|" +
				"IN GS"
				);
		
		it = ordered_output_mappings.descendingIterator();
						
		while (it.hasNext()){
			
			mapping = it.next();
			
			if (mapping_extractor.isMappingInGoldStandard(mapping.getIdentifierOnto1(), mapping.getIdentifierOnto2())){
				inGS="YES";
			}
			else{
				inGS="NO";
			}
			
			writer.writeLine(
					index.getLabel4ConceptIndex(mapping.getIdentifierOnto1()) + "|" +
					index.getLabel4ConceptIndex(mapping.getIdentifierOnto2()) + "|" +
					index.getIRIStr4ConceptIndex(mapping.getIdentifierOnto1()) + "|" +
					index.getIRIStr4ConceptIndex(mapping.getIdentifierOnto2()) + "|" +
					getConfidence(mapping) + "|" +
					mapping_extractor.extractISUB4Mapping(mapping.getIdentifierOnto1(), mapping.getIdentifierOnto2())  + "|" +
					mapping_extractor.extractScopeAnchors4Mapping(mapping.getIdentifierOnto1(), mapping.getIdentifierOnto2()) + "|" +
					inGS
					);
		
			
			
		}
		
		writer.closeBuffer();
		
		
		ordered_output_mappings.clear();
		
		if (include_discarded){
		
			//ORDER DISCARDED
			/*for (int idea : mapping_extractor.getDircardedAnchors().keySet()){
				
				for (int ideb : mapping_extractor.getDircardedAnchors().get(idea)){
					
					if (idea<ideb){
						
						ordered_output_mappings.add(new MappingObjectInteractivity(idea, ideb));
						
					}
				}
			}*/
			
			for (int idea : mapping_extractor.getHardDiscardedMappings().keySet()){
				
				for (int ideb : mapping_extractor.getHardDiscardedMappings().get(idea)){
					
					if (idea<ideb){
						
						ordered_output_mappings.add(new MappingObjectInteractivity(idea, ideb));
						
					}
				}
			}
			
			
			//SAVE DISCARDED
			writer = new WriteFile("/usr/local/data/DataUMLS/UMLS_Onto_Versions/LUCADA/ordered_harddiscarded_mappings_NCI2LUCADA.txt");
			
			it = ordered_output_mappings.descendingIterator();
			
			writer.writeLine(
					"Label 1" + "|" +
					"Label 2" + "|" +
					"IRI 1" + "|" +
					"IRI 2" + "|" +
					"Confidence" + "|" +
					"ISUB (Lex. Sim.)"  + "|" +
					"Scope"  + "|" +
					"IN GS"
					);
			
			while (it.hasNext()){
				
				mapping = it.next();
				
				if (mapping_extractor.isMappingInGoldStandard(mapping.getIdentifierOnto1(), mapping.getIdentifierOnto2())){
					inGS="YES";
				}
				else{
					inGS="NO";
				}
				
				writer.writeLine(
						index.getLabel4ConceptIndex(mapping.getIdentifierOnto1()) + "|" +
						index.getLabel4ConceptIndex(mapping.getIdentifierOnto2()) + "|" +
						index.getIRIStr4ConceptIndex(mapping.getIdentifierOnto1()) + "|" +
						index.getIRIStr4ConceptIndex(mapping.getIdentifierOnto2()) + "|" +
						getConfidence(mapping) + "|" +
						mapping_extractor.extractISUB4Mapping(mapping.getIdentifierOnto1(), mapping.getIdentifierOnto2())  + "|" +
						mapping_extractor.extractScopeAnchors4Mapping(mapping.getIdentifierOnto1(), mapping.getIdentifierOnto2())+ "|" +
						inGS
						);
			
				
			}
			
			writer.closeBuffer();
			
			
			ordered_output_mappings.clear();
			
		}		
		
		
		
		
	}
	
	
	


	
	
	
	public Map<Integer, Set<Integer>> getClassMappings(){
		return mapping_extractor.getLogMapMappings();
		
	}
	
	
	public Map<Integer, Set<Integer>> getDiscardedClassMappings(){
		return mapping_extractor.getDiscardedMappings();
		
	}
	
	public Map<Integer, Set<Integer>> getHardDiscardedClassMappings(){
		return mapping_extractor.getHardDiscardedMappings();
		
	}
	
	public Map<Integer, Set<Integer>> getConflictiveAnchors(){
		return mapping_extractor.getConflictiveMappings();
		
	}
	
	
	public Map<Integer, Set<Integer>> getAnchors(){
		return mapping_extractor.getAnchors();
		
	}
	
	
	public Map<Integer, Set<Integer>> getLogMapMappings1N(){
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
	
	public Map<Integer, Set<Integer>> getInstanceMappings(){
		return mapping_extractor.getInstanceMappings();	
	}
	
	public Map<Integer, Map<Integer, Integer>> getInstanceMappings4OutputType(){
		return mapping_extractor.getInstanceMappings4OutputType();
	}
	
	
	
	/*public double getConfidence4ClassMappingStr(MappingObjectStr map) {
		
		int ide1 = onto_process1.getIdentifier4ConceptIRI(map.getIRIStrEnt1());
		int ide2 = onto_process2.getIdentifier4ConceptIRI(map.getIRIStrEnt2());
		
				
		return getConfidence4ConceptMapping(ide1, ide2);
		
		
	}
	
	
	public int getId4ClassURIOnto1(String uri1) {		
		return onto_process1.getIdentifier4ConceptIRI(uri1);
	}
	public int getId4ClassURIOnto2(String uri2) {		
		return onto_process2.getIdentifier4ConceptIRI(uri2);
	}
	
	
	
	
	public AnchorAssessment getMappingAssessmentObject() {
		return mappings_assessment;
	}
	
	public MappingManager getMappingExtractorObject() {
		return mapping_extractor;
	}
*/
	
	
	//TODO
	public double getLexicalScore4ConceptMapping(int ide1, int ide2){
		//return mapping_extractor.getLexicalScore(ide1, ide2);
		//In case it was not extracted
		return mapping_extractor.extractISUB4Mapping(ide1, ide2);
		
	}
	
	
	//TODO
	public double getStrutcuralScore4ConceptMapping(int ide1, int ide2){
		//return mapping_extractor.getStructuralScore(ide1, ide2);
		//In case it was not extracted
		return mapping_extractor.extractScopeAnchors4Mapping(ide1, ide2);
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
	
	public double getConfidence4InstanceMapping(int ide1, int ide2){
		return mapping_extractor.getConfidence4InstanceMapping(ide1, ide2);
	}
	
	
	public double getLexicalScore4InstanceMapping(int ide1, int ide2){
		//return mapping_extractor.getISUB4InstanceMapping(ide1, ide2);
		//In case it was not extracted
		return mapping_extractor.extractISUB4InstanceMapping(ide1, ide2);
	}
	
	public double getStructuralScore4InstanceMapping(int ide1, int ide2){
		//return mapping_extractor.getScope4InstanceMapping(ide1, ide2);
		//In case it was not extracted
		return mapping_extractor.extractScope4InstanceMapping(ide1, ide2);
	}
	
	
	public String getIRI4ConceptIdentifier(int ide){
		return index.getIRIStr4ConceptIndex(ide);
	}
	
	
	public String getLabel4ConceptIdentifier(int ide){
		return index.getLabel4ConceptIndex(ide);
	}
	
	
	public String getIRI4DataPropIdentifier(int ide){
		return index.getIRIStr4DataPropIndex(ide);
	}
	
	public String getIRI4ObjectPropIdentifier(int ide){
		return index.getIRIStr4ObjPropIndex(ide);
	}
	
	public String getIRI4InstanceIdentifier(int ide){
		return index.getIRIStr4IndividualIndex(ide);
	}
	
	
	public void clearIndexStructures(){
		index.clearAlternativeLabels4Classes();
		index.clearTaxonomicalStructures();
	}
	
	
	
	
	
	
	

	
	
	
	private double getConfidence(MappingObjectInteractivity m){
		
		return (mapping_extractor.getConfidence4Mapping(m.getIdentifierOnto1(), m.getIdentifierOnto2()));
		
	}
	
	
	
	/**
	 * @author Ernesto
	 *
	 */
	private class ComparatorConfidence implements Comparator<MappingObjectInteractivity> {
		
		public int compare(MappingObjectInteractivity m1, MappingObjectInteractivity m2) {
			
			if (getConfidence(m1) < getConfidence(m2))
				return -1;
			else
				return 1;
		}
		
	}
	
	

}

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

import java.util.Calendar;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Map;

import org.semanticweb.owlapi.io.RDFXMLOntologyFormat;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLDataFactory;
//import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddImport;

import uk.ac.ox.krr.logmap2.Parameters;
import uk.ac.ox.krr.logmap2.io.OutPutFilesManager;
import uk.ac.ox.krr.logmap2.io.WriteFile;
import uk.ac.ox.krr.logmap2.io.LogOutput;
import uk.ac.ox.krr.logmap2.reasoning.ReasonerManager;
import uk.ac.ox.krr.logmap2.repair.*;
import uk.ac.ox.krr.logmap2.statistics.StatisticsManager;
import uk.ac.ox.krr.logmap2.overlapping.*;
import uk.ac.ox.krr.logmap2.owlapi.SynchronizedOWLManager;
import uk.ac.ox.krr.logmap2.utilities.PrecomputeIndexCombination;
import uk.ac.ox.krr.logmap2.lexicon.LexicalUtilities;
import uk.ac.ox.krr.logmap2.utilities.Utilities;

import uk.ac.ox.krr.logmap2.indexing.*;
import uk.ac.ox.krr.logmap2.interactive.*;
import uk.ac.ox.krr.logmap2.interactive.objects.MappingObjectInteractivity;

import uk.ac.ox.krr.logmap2.mappings.MappingManager;
import uk.ac.ox.krr.logmap2.mappings.CandidateMappingManager;

//import uk.ac.ox.krr.logmap2.io.LogOutput;

/**
 * This class will be invoked from LogMap's web facility
 * 
 * @author ernesto
 * 
 */
public class LogMapInteractivity_WebService {

	private OverlappingExtractor overlappingExtractor;

	private IndexManager index;

	private OntologyProcessing onto_process1;
	private OntologyProcessing onto_process2;

	// private AnchorExtraction mapping_extractor;
	private MappingManager mapping_extractor;

	private AnchorAssessment mappings_assessment;

	//private InteractiveProcess interactiveProcessManager;

	private long init_global, init, fin;

	private boolean cleanD_G = true;

	private HTMLResultsFileManager progress_manager;

	private String path_output; // for mappings and overlapping

	private String iri_output;

	// Input URIS
	private String iri1_str;
	private String iri2_str;

	private LexicalUtilities lexicalUtilities = new LexicalUtilities();
	
	private String email;
	private String name;
	private String id_task;
	private int reasoner;
	
	private boolean endProcess = false;

	/**
	 * 
	 * @param iri1_str
	 * @param iri2_str
	 * @param diagnosis
	 * @param path_output
	 * @param progress_manager
	 * @throws Exception
	 */
	public LogMapInteractivity_WebService(String iri1_str, String iri2_str,
			boolean diagnosis, String path_output, String iri_output,
			HTMLResultsFileManager progress_manager,
			String email,
			String name,
			String id_task,
			int reasoner)
			throws OWLOntologyCreationException, Exception {

		// try {
		init_global = init = Calendar.getInstance().getTimeInMillis();

		this.iri1_str = iri1_str;
		this.iri2_str = iri2_str;

		this.cleanD_G = diagnosis;

		this.progress_manager = progress_manager;

		this.path_output = path_output;
		this.iri_output = iri_output;
		
		this.email=email;
		this.name=name;
		this.id_task=id_task;
		this.reasoner=reasoner;
		
		

		// INIT LOGMAP: lex and precomp integer combinations
		InitLogMap();

		// Overlapping estimation
		OverlappingEstimation(iri1_str, iri2_str);
		updateHTMLProgress("Loading and overlapping...done");

		// Indexes lexicon (IF creation) and structure
		IndexLexiconAndStructure();

		fin = Calendar.getInstance().getTimeInMillis();
		writeInternalLog("Global Time Parsing and Index Lexicon (s): "
				+ (float) ((double) fin - (double) init_global) / 1000.0);
		updateHTMLProgress("Lexical indexing...done");

		// EXTRACT, CLEAN ANCHORS and INDEX INTLABELLING
		createAndCleanAnchors();
		updateHTMLProgress("Extraction and diagnosis of reliable mappings...done");

		// Extract new candidates, clean them and index labelling
		createCandidateMappings();

		// ------------------------------------------------
		// We perform matching of properties and instances
		// We clean wrt reliable mappings and candidates
		// ------------------------------------------------
		// PROPERTIES
		init = Calendar.getInstance().getTimeInMillis();
		mapping_extractor.createObjectPropertyAnchors();
		mapping_extractor.createDataPropertyAnchors();

		// updateHTMLProgress("Extraction of property mappings...done");

		// Delete inverted files for properties
		onto_process1.clearInvertedFiles4properties();
		onto_process2.clearInvertedFiles4properties();

		fin = Calendar.getInstance().getTimeInMillis();
		writeInternalLog("Time creating property mappings (s): "
				+ (float) ((double) fin - (double) init) / 1000.0);

		init = Calendar.getInstance().getTimeInMillis();
		// INDIVIDUALS
		if (Parameters.perform_instance_matching) {
			createAndAssessInstanceMappings();
		}
		// updateHTMLProgress("Extraction of instance mappings...done");
		fin = Calendar.getInstance().getTimeInMillis();
		writeInternalLog("Time creating instance mappings (s): "
				+ (float) ((double) fin - (double) init) / 1000.0);
		
		

		updateHTMLProgress("Reliable mappings = "
				+ getCurrentReliableMappings() + ", Mappings requiring user feedback = "
				+ mapping_extractor.getListOfMappingsToAskUser().size());

		
		// 
		//THERE ARE MAPPINGS TO ASSESS
		if (mapping_extractor.getListOfMappingsToAskUser().size()>0){
			endProcess = false;
			doInteractivity();
			
		}
		else{ //The process ends here!
			endProcess = true;
			endProcess();
		}
		


		

		index.clearTaxonomicalStructures();

		fin = Calendar.getInstance().getTimeInMillis();
		writeInternalLog("TOTAL TIME (without interactivity) (s): "
				+ (float) ((double) fin - (double) init_global) / 1000.0);

		// }
		// catch (Exception e){
		// writeInternalLog("ERROR: " + e.getMessage());
		// writeInternalLog("ERROR: " + e.getLocalizedMessage());
		// }

	}
	
	
	/**
	 * 
	 * @return
	 */
	public boolean isProcessFinished(){
		return endProcess;
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	private void doInteractivity() throws Exception{
		//Mapping and module OUTPUT FILES
		try{
			saveReliableMappings();
			saveMappings2review(); //we save them only in textual format! (reliable + properties + instances)
			saveOverlappingModules(false); //an integrated ontology
			
			//Store info paths in html
			
		} 
		catch (Exception e){
			System.out.println(e.getMessage());
			e.printStackTrace();
			throw new Exception();
		}
		
		
		
		
		// INTERACTIVE PROCESS
		// ---------------------------
		init = Calendar.getInstance().getTimeInMillis();
		InteractiveProcess_WebService mappings4interactivity = 
				new InteractiveProcess_WebService(
						progress_manager,
						mapping_extractor.getListOfMappingsToAskUser(),
						index,
						path_output, 
						//iri_output,
						email,
						name,
						id_task,
						reasoner);
		
		
		// Delete Alt labels in class index
		//We do not clear structure before because we need synonyms for mappings to be assessed by the user
		index.clearAlternativeLabels4Classes();
		
		//TODO
		mappings4interactivity.clearStructures();

		fin = Calendar.getInstance().getTimeInMillis();
		writeInternalLog("Time creating information for mappings2ask (s): "
				+ (float) ((double) fin - (double) init) / 1000.0);
		
		LogOutput.print("Time creating information for mappings2ask (s): "
				+ (float) ((double) fin - (double) init) / 1000.0);
				
	}
	
	
	
	private void endProcess() throws Exception{
		
		updateHTMLProgress("<b>No mappings requiring user feedback.</b>");
		updateHTMLProgress("Matching task finished. Storing output files...");
		
		
		//Mapping and module OUTPUT FILES
		try{
			saveExtractedMappings();
			saveOverlappingModules(true);
		} 
		catch (Exception e){
			System.out.println(e.getMessage());
			e.printStackTrace();
			throw new Exception();
		}
	}
	
	
	

	private int getCurrentReliableMappings() {

		int dir_mapping;

		int num_class_mappings = 0;

		for (int ide1 : getClassMappings().keySet()) {
			for (int ide2 : getClassMappings().get(ide1)) {

				dir_mapping = getDirClassMapping(ide1, ide2);

				if (dir_mapping != Utilities.NoMap) {

					num_class_mappings++;

				}
			}
		}

		return num_class_mappings;

	}

	private void updateHTMLProgress(String text_progress) {
		progress_manager.updateProgress(text_progress);
	}

	private WriteFile internal_log;

	/**
	 * To keep logmap progress... internal use
	 */
	private void writeInternalLog(String text_log) {

		// System.out.println(text_log);

		// We open/close it each time
		internal_log = new WriteFile(path_output
				+ "/logmap_internal_progress.log", true);

		internal_log.writeLine(text_log);

		internal_log.closeBuffer();
	}

	private void InitLogMap() throws Exception {

		// Show print outs (not used for the web facility)
		// We store instead a log
		LogOutput.showOutpuLog(true);

		lexicalUtilities.loadStopWords();
		// LexicalUtilities.loadStopWordsExtended();

		if (Parameters.use_umls_lexicon)
			lexicalUtilities.loadUMLSLexiconResources();

		lexicalUtilities.setStemmer(); // creates stemmer object (Paice by
										// default)

		// Lib.debuginfo(LexicalUtilities.getStemming4Word("Prolactin") + " " +
		// LexicalUtilities.getStemming4Word("brachii") + "\n");

		fin = Calendar.getInstance().getTimeInMillis();
		writeInternalLog("Time initializing lexical utilities (s): "
				+ (float) ((double) fin - (double) init) / 1000.0);

		// init = Calendar.getInstance().getTimeInMillis();
		// PrecomputeIndexCombination.preComputeIdentifierCombination();
		// fin = Calendar.getInstance().getTimeInMillis();
		// writeInternalLog("Time precomputing index combinations (s): " +
		// (float)((double)fin-(double)init)/1000.0);

	}

	private void OverlappingEstimation(String iri1_str, String iri2_str)
			throws OWLOntologyCreationException, Exception {

		writeInternalLog("OVERLAPPING");
		init = Calendar.getInstance().getTimeInMillis();

		Parameters.setMinSize4Overlapping(5000);

		if (!Parameters.use_overlapping) {
			overlappingExtractor = new NoOverlappingExtractor();
		} else {
			overlappingExtractor = new LexicalOverlappingExtractor(
					lexicalUtilities);
		}

		overlappingExtractor.createOverlapping(iri1_str, iri2_str);
		fin = Calendar.getInstance().getTimeInMillis();
		writeInternalLog("Time extracting overlapping (s): "
				+ (float) ((double) fin - (double) init) / 1000.0);
	}

	private void IndexLexiconAndStructure() throws Exception {

		// Create Index and new Ontology Index...
		index = new JointIndexManager();

		updateHTMLProgress("Overlapping ontology 1: "
				+ overlappingExtractor.getOverlappingOnto1()
						.getClassesInSignature(true).size()
				+ " classes, "
				+ (overlappingExtractor.getOverlappingOnto1()
						.getDataPropertiesInSignature(true).size() + overlappingExtractor
						.getOverlappingOnto1()
						.getObjectPropertiesInSignature(true).size())
				+ " properties, "
				+ overlappingExtractor.getOverlappingOnto1()
						.getIndividualsInSignature(true).size()
				+ " individuals.");

		updateHTMLProgress("Overlapping ontology 2: "
				+ overlappingExtractor.getOverlappingOnto2()
						.getClassesInSignature(true).size()
				+ " classes, "
				+ (overlappingExtractor.getOverlappingOnto2()
						.getDataPropertiesInSignature(true).size() + overlappingExtractor
						.getOverlappingOnto2()
						.getObjectPropertiesInSignature(true).size())
				+ " properties, "
				+ overlappingExtractor.getOverlappingOnto2()
						.getIndividualsInSignature(true).size()
				+ " individuals.");

		// Process ontologies: lexicon and taxonomy (class) and IFs
		onto_process1 = new OntologyProcessing(
				overlappingExtractor.getOverlappingOnto1(), index,
				lexicalUtilities);
		onto_process2 = new OntologyProcessing(
				overlappingExtractor.getOverlappingOnto2(), index,
				lexicalUtilities);

		// Extracts lexicon
		init = Calendar.getInstance().getTimeInMillis();
		onto_process1.precessLexicon();
		onto_process2.precessLexicon();
		fin = Calendar.getInstance().getTimeInMillis();
		writeInternalLog("Time extracting lexicon and IF (s): "
				+ (float) ((double) fin - (double) init) / 1000.0);

		// I guess can be deleted here
		lexicalUtilities.clearStructures();

		init = Calendar.getInstance().getTimeInMillis();
		// Init Mapping extractor: intersects IF and extract IF weak
		// mapping_extractor = new LexicalMappingExtractor(index, onto_process1,
		// onto_process2);
		mapping_extractor = new CandidateMappingManager(index, onto_process1,
				onto_process2);

		// Statistics (necessray to avoid error...)
		StatisticsManager.reInitValues();
		StatisticsManager.setMappingManager(mapping_extractor);

		mapping_extractor.intersectInvertedFiles();
		fin = Calendar.getInstance().getTimeInMillis();
		writeInternalLog("Time intersecting IF and extracting IF weak (s): "
				+ (float) ((double) fin - (double) init) / 1000.0);

		// Clear ontology stemmed labels
		onto_process1.clearStemmedLabels();
		onto_process2.clearStemmedLabels();

		// Extracts Taxonomy
		// Also extracts A^B->C
		init = Calendar.getInstance().getTimeInMillis();
		onto_process1.setTaxonomicData();
		onto_process2.setTaxonomicData();
		fin = Calendar.getInstance().getTimeInMillis();
		writeInternalLog("Time extracting structural information (s): "
				+ (float) ((double) fin - (double) init) / 1000.0);

		// We do not need the references to OWLEntities anymore
		onto_process1.clearOntologyRelatedInfo();
		onto_process2.clearOntologyRelatedInfo();

		// We first create weak anchors to be used for scopes
		init = Calendar.getInstance().getTimeInMillis();
		mapping_extractor.extractAllWeakMappings();
		fin = Calendar.getInstance().getTimeInMillis();
		writeInternalLog("Time creating all weak anchors (s): "
				+ (float) ((double) fin - (double) init) / 1000.0);

		// Extract subsets from all weak mappings to evaluate later
		// ----------------------------------------------------------
		init = Calendar.getInstance().getTimeInMillis();
		mapping_extractor.extractCandidatesSubsetFromWeakMappings();
		fin = Calendar.getInstance().getTimeInMillis();
		writeInternalLog("Time creating candidate subset of weak anchors (s): "
				+ (float) ((double) fin - (double) init) / 1000.0);

		// Remove structures used for frequency extractor

		// Frequency structures
		onto_process1.clearFrequencyRelatedStructures();
		onto_process2.clearFrequencyRelatedStructures();
		index.clearSingleWordInvertedIndex();

		// Only used by frequency-like weak mappings
		index.clearStemmedAlternativeLabels4Classes();

	}

	private void createAndCleanAnchors() throws Exception {

		writeInternalLog("\nANCHOR DIAGNOSIS ");

		init = Calendar.getInstance().getTimeInMillis();
		mapping_extractor.createAnchors();

		// Create different groups: "exact", ambiguity and no_scope (different
		// sets...). We will add them later (almost done)

		fin = Calendar.getInstance().getTimeInMillis();
		writeInternalLog("Time creating anchors (s): "
				+ (float) ((double) fin - (double) init) / 1000.0);

		mappings_assessment = new AnchorAssessment(index, mapping_extractor);

		if (cleanD_G) {
			// TODO comment
			/*
			 * init = Calendar.getInstance().getTimeInMillis();
			 * mappings_assessment
			 * .CountSatisfiabilityOfIntegration_DandG(mapping_extractor
			 * .getAnchors()); fin = Calendar.getInstance().getTimeInMillis();
			 * LogOutput.printAlways("Time checking satisfiability D&G (s): " +
			 * (float)((double)fin-(double)init)/1000.0);
			 */

			init = Calendar.getInstance().getTimeInMillis();
			mappings_assessment
					.CheckSatisfiabilityOfIntegration_DandG(mapping_extractor
							.getAnchors());
			fin = Calendar.getInstance().getTimeInMillis();
			writeInternalLog("Time cleaning anchors D&G (s): "
					+ (float) ((double) fin - (double) init) / 1000.0);
		}

		// After repairing exact
		mapping_extractor.setExactAsFixed(true);

		// TODO extract further disjointness based on weak anchors and mappings
		// TODO LATER....

		// INTERVAL LABELLING SCHEMA
		// --------------------------
		init = Calendar.getInstance().getTimeInMillis();

		// Index already have the necessary taxonomical information apart from
		// the equiv mappings
		// TODO Extract ident2directkids with clean anchors
		index.setIntervalLabellingIndex(mapping_extractor.getFixedAnchors());

		index.clearAuxStructuresforLabellingSchema();

		fin = Calendar.getInstance().getTimeInMillis();
		writeInternalLog("Time indexing hierarchy + anchors (ILS) (s): "
				+ (float) ((double) fin - (double) init) / 1000.0);

	}

	private void createCandidateMappings() throws Exception {

		writeInternalLog("\nCANDIDATE DIAGNOSIS 1");

		// After this method we will have 3 sets: Mappings2Review with DandG,
		// Mappings to ask user, and discarded mappoings

		init = Calendar.getInstance().getTimeInMillis();
		mapping_extractor.createCandidates();

		

		fin = Calendar.getInstance().getTimeInMillis();
		writeInternalLog("Time creating candidates (s): "
				+ (float) ((double) fin - (double) init) / 1000.0);

		// TODO Uncomment if no fixed anchors
		// We add candidates to anchors, nothing noew is fixed
		// mapping_extractor.setExactAsFixed(false);
		// mapping_extractor.moveMappingsToReview2AnchorList();

		// Clean with Dawling and Gallier mappings 2 review
		// -- D&G
		// countAnchors();
		// mappings_assessment = new AnchorAssessment(index, mapping_extractor);
		init = Calendar.getInstance().getTimeInMillis();
		if (cleanD_G) {
			// Adds clean mappings to anchors. Conflictive and split mappings to
			// respective sets
			init = Calendar.getInstance().getTimeInMillis();
			mappings_assessment
					.CheckSatisfiabilityOfIntegration_DandG(mapping_extractor
							.getMappings2Review()); // With Fixed mappings!
			// mappings_assessment.CheckSatisfiabilityOfIntegration_DandG(mapping_extractor.getAnchors());
			// //No fixed mappings
			fin = Calendar.getInstance().getTimeInMillis();
			writeInternalLog("Time cleaning new candidates D&G (s): "
					+ (float) ((double) fin - (double) init) / 1000.0);
		}

		// TODO Merge mappings 2 review and anchors
		// Comment if no fixed anchors
		mapping_extractor.moveMappingsToReview2AnchorList();

		// Remove mappings to review
		mapping_extractor.getMappings2Review().clear();

		// INTERVAL LABELLING SCHEMA
		// --------------------------
		init = Calendar.getInstance().getTimeInMillis();
		index.setIntervalLabellingIndex(mapping_extractor.getAnchors());// It
																		// also
																		// contains
																		// mappings
																		// 2
																		// review
		index.clearAuxStructuresforLabellingSchema();
		fin = Calendar.getInstance().getTimeInMillis();
		writeInternalLog("Time indexing hierarchy + anchors and candidates I (ILS) (s): "
				+ (float) ((double) fin - (double) init) / 1000.0);

		// LogOutput.print("\n\nNEW UNSAT:");
		// To check not solved cases of unsatisfiability
		// mappings_assessment.CheckSatisfiabilityOfConcreteClasses_DandG(mapping_extractor.getAnchors(),
		// index.getUnsatisfiableClassesILS());

		// Assess mappings 2 ask user
		mapping_extractor.assessMappings2AskUser();

	}
	
	
	
	/**
	 * This method creates the OWL files correspondent to the overlapping/module files.
	 * We show progress if end of matching task (no interactivity needed)
	 */
	private void saveOverlappingModules(boolean showProgress) throws Exception{
		
		
		//OWLDataFactory dataFactory = OWLManager.getOWLDataFactory();
		OWLDataFactory dataFactory = SynchronizedOWLManager.createOWLDataFactory();
		
		String iri_module1 = progress_manager.getURIModule1();//iri_output + "/module1.owl";
		String iri_module2 = progress_manager.getURIModule2();//iri_output + "/module2.owl";
		String iri_mappings = progress_manager.getURIMappings();//iri_output + "/mappings.owl";
		String iri_integrated_onto = progress_manager.getIntegratedOntologyIRIStr();//iri_output + "/integratedOntology.owl";
		String iri_integrated_onto_modules = progress_manager.getIntegratedOntologyModulesIRIStr(); //iri_output + "/integratedOntologyWithModules.owl";
		
		//We need the IRIs to use the reasoner (optional)
		//progress_manager.setIntegratedOntologyIRIStr(iri_integrated_onto);
		//progress_manager.setIntegratedOntologyModulesIRIStr(iri_integrated_onto_modules);
		
		
		List<AddImport> listImports2Add= new ArrayList<AddImport>();
		
		init = Calendar.getInstance().getTimeInMillis();
		
		
		///INTEGRATED ONTOLOGY		
		listImports2Add.clear();
		
		//OWLOntologyManager managerIntegratedOnto = OWLManager.createOWLOntologyManager();
		OWLOntologyManager managerIntegratedOnto = SynchronizedOWLManager.createOWLOntologyManager();
		
		OWLOntology integratedOntology = 
				managerIntegratedOnto.createOntology(IRI.create(iri_integrated_onto));
		
		listImports2Add.add(
				new AddImport(
						integratedOntology,
						dataFactory.getOWLImportsDeclaration(IRI.create(iri1_str))
						));
		
		listImports2Add.add(
				new AddImport(
						integratedOntology,
						dataFactory.getOWLImportsDeclaration(IRI.create(iri2_str))
						));
		
		listImports2Add.add(
				new AddImport(
						integratedOntology,
						dataFactory.getOWLImportsDeclaration(IRI.create(iri_mappings))
						));
		
		managerIntegratedOnto.applyChanges(listImports2Add);
		
		//managerIntegratedOnto.saveOntology(
		//		integratedOntology, new RDFXMLOntologyFormat(), IRI.create("file:" + path_output + "/integratedOntology.owl"));
		SynchronizedOWLManager.saveOntology(managerIntegratedOnto, integratedOntology, "file:" + path_output + "/integratedOntology.owl");

		
		if (showProgress){
		updateHTMLProgress("Full integrated ontology: " + 
				"<a href=\"" + iri_integrated_onto + "\">[Original ontologies + mappings]</a>, " + 
				//"<a href=\"" + iri_integrated_onto_modules + "\">[Overlapping ontologies + mappings]</a> " + 
				"<i>(note that this ontology only imports the respective OWL files)</i>");
		}
		
		
		
		
		//----------------------------------------
		///INTEGRATED ONTOLOGY WITH MODULES
		//-----------------------------------------
		
		//OWLOntologyManager moduleManager1 = OWLManager.createOWLOntologyManager();
		//OWLOntologyManager moduleManager1 = SynchronizedOWLManager.createOWLOntologyManager();
		//OWLOntologyManager moduleManager2 = OWLManager.createOWLOntologyManager();
		//OWLOntologyManager moduleManager2 = SynchronizedOWLManager.createOWLOntologyManager();
		
		//moduleManager1.saveOntology(
		//		overlappingExtractor.getOverlappingOnto1(), new RDFXMLOntologyFormat(), IRI.create("file:" + path_output + "/module1.owl"));
		
		//moduleManager2.saveOntology(
		//		overlappingExtractor.getOverlappingOnto2(), new RDFXMLOntologyFormat(), IRI.create("file:" + path_output + "/module2.owl"));
		
		//Module 1
		SynchronizedOWLManager.saveOntology(overlappingExtractor.getOverlappingOnto1(), "file:" + path_output + "/module1.owl");
		
		//Module 2
		SynchronizedOWLManager.saveOntology(overlappingExtractor.getOverlappingOnto2(), "file:" + path_output + "/module2.owl");
		
		
		
		if (showProgress){
			updateHTMLProgress("Overlapping ontology modules: " + 
					"<a href=\"" + iri_module1 + "\">[Module 1]</a>, " + 
					"<a href=\"" + iri_module2 + "\">[Module 2]</a>.");
		}
		
		
		//Create ontologies importing integrated ontologies		
		
		
		
		
		
		//OWLOntologyManager managerIntegratedOntoWithModules = OWLManager.createOWLOntologyManager();
		OWLOntologyManager managerIntegratedOntoWithModules = SynchronizedOWLManager.createOWLOntologyManager();
		
		OWLOntology integratedOntologyWithModules = 
				managerIntegratedOntoWithModules.createOntology(IRI.create(iri_integrated_onto_modules));
		
		listImports2Add.add(
				new AddImport(
						integratedOntologyWithModules,
						dataFactory.getOWLImportsDeclaration(IRI.create(iri_module1))
						));
		
		listImports2Add.add(
				new AddImport(
						integratedOntologyWithModules,
						dataFactory.getOWLImportsDeclaration(IRI.create(iri_module2))
						));
		
		listImports2Add.add(
				new AddImport(
						integratedOntologyWithModules,
						dataFactory.getOWLImportsDeclaration(IRI.create(iri_mappings))
						));
		
		
		managerIntegratedOntoWithModules.applyChanges(listImports2Add);
		
		//managerIntegratedOntoWithModules.saveOntology(
		//		integratedOntologyWithModules, new RDFXMLOntologyFormat(), IRI.create("file:" + path_output + "/integratedOntologyWithModules.owl"));
		SynchronizedOWLManager.saveOntology(managerIntegratedOntoWithModules, integratedOntologyWithModules, "file:" + path_output + "/integratedOntologyWithModules.owl");
		
		
				
		
		
		if (showProgress){
		updateHTMLProgress("Integrated ontology using overlappings/modules: " + 
		 
				//"<a href=\"" + iri_integrated_onto + "\">[Original ontologies + mappings]</a>, " + 
				"<a href=\"" + iri_integrated_onto_modules + "\">[Overlapping ontologies + mappings]</a> " + 
				"<i>(note that this ontology only imports the respective OWL files)</i>");
		}
		
		
		
		fin = Calendar.getInstance().getTimeInMillis();			
		writeInternalLog("Time Saving overlapping and integrated ontologies (s): " + (float)((double)fin-(double)init)/1000.0);
		
		
		
		
	}
	
	
	/**
	 * We store current reliable mappings
	 * @throws Exception
	 */
	private void saveReliableMappings() throws Exception{
		
		init = Calendar.getInstance().getTimeInMillis();
		
		
		int dir_mapping;
		
		/*int num_class_mappings=0;
		int num_prop_mappings=0;
		int num_instance_mappings=0;*/
		
		OutPutFilesManager outPutFilesManager = new OutPutFilesManager();
		
		
		outPutFilesManager.createOutFiles(
					path_output + "/reliable_mappings", 
					OutPutFilesManager.FlatFormat,
					onto_process1.getOntoIRI(),
					onto_process1.getOntoIRI());
			
						
			for (int ide1 : getClassMappings().keySet()){
				for (int ide2 : getClassMappings().get(ide1)){
					
					dir_mapping = getDirClassMapping(ide1, ide2);
					
					if (dir_mapping!=Utilities.NoMap){
						
						//num_class_mappings++;
						
						if (dir_mapping!=Utilities.R2L){
									
							outPutFilesManager.addClassMapping2Files(
									getIRI4ConceptIdentifier(ide1),
									getIRI4ConceptIdentifier(ide2),
									dir_mapping,
									getConfidence4ConceptMapping(ide1, ide2)
									);
						}
						else{
							outPutFilesManager.addClassMapping2Files(								
									getIRI4ConceptIdentifier(ide2),
									getIRI4ConceptIdentifier(ide1),
									dir_mapping,
									getConfidence4ConceptMapping(ide2, ide1)
									);
						}
					}
				}
			}
			
						
			outPutFilesManager.closeAndSaveFiles();
			
			/*String iri_mappings_owl = iri_output + "/mappings.owl";
			String iri_mappings_txt = iri_output + "/mappings.txt";
			String iri_mappings_rdf = iri_output + "/mappings.rdf";
			
			updateHTMLProgress("Output mappings (class mappings = " + num_class_mappings + 
						", property mappings = " + num_prop_mappings + ", instance mappings = " + num_instance_mappings + "): " + 
						"<a href=\"" + iri_mappings_owl +  "\">[OWL format]</a>, " + 
						"<a href=\"" + iri_mappings_txt + "\">[TXT format]</a>, " + 
						"<a href=\"" + iri_mappings_rdf + "\">[OAEI Alignment format]</a>.");
			*/
			
			
			fin = Calendar.getInstance().getTimeInMillis();			
			writeInternalLog("Time Saving reliable mappings (s): " + (float)((double)fin-(double)init)/1000.0);
		
	}
	
	
	/**
	 * We store property and instance mappings to be assessed in last step
	 * @throws Exception
	 */
	private void saveMappings2review() throws Exception{
		
		init = Calendar.getInstance().getTimeInMillis();
		
		
		int dir_mapping;
		
		//int num_class_mappings=0;
		int num_prop_mappings=0;
		int num_instance_mappings=0;
		
		OutPutFilesManager outPutFilesManager = new OutPutFilesManager();
		
		
		outPutFilesManager.createOutFiles(
					path_output + "/mappings2review", 
					OutPutFilesManager.FlatFormat,
					onto_process1.getOntoIRI(),
					onto_process1.getOntoIRI());
			
						
			
			
			for (int ide1 : getDataPropMappings().keySet()){
				
				num_prop_mappings++;
				
				outPutFilesManager.addDataPropMapping2Files(
							getIRI4DataPropIdentifier(ide1),
							getIRI4DataPropIdentifier(getDataPropMappings().get(ide1)),
							Utilities.EQ,  
							getConfidence4DataPropConceptMapping(ide1, getDataPropMappings().get(ide1))							
							);
			}
			
			for (int ide1 : getObjectPropMappings().keySet()){		
				
				num_prop_mappings++;
				
				outPutFilesManager.addObjPropMapping2Files(
							getIRI4ObjectPropIdentifier(ide1),
							getIRI4ObjectPropIdentifier(getObjectPropMappings().get(ide1)),
							Utilities.EQ, 
							getConfidence4ObjectPropConceptMapping(ide1, getObjectPropMappings().get(ide1))
							);
			}
			
			
			//TODO: update!!
			if (Parameters.perform_instance_matching && Parameters.output_instance_mappings){
				
				for (int ide1 : getInstanceMappings().keySet()){
					for (int ide2 : getInstanceMappings().get(ide1)){
						
						num_instance_mappings++;
					
						outPutFilesManager.addInstanceMapping2Files(
								getIRI4InstanceIdentifier(ide1), 
								getIRI4InstanceIdentifier(ide2), 
								getConfidence4InstanceMapping(ide1, ide2)
							);
						
					}
				
				}
			}
			
			
						
			outPutFilesManager.closeAndSaveFiles();
			
			/*String iri_mappings_owl = iri_output + "/mappings.owl";
			String iri_mappings_txt = iri_output + "/mappings.txt";
			String iri_mappings_rdf = iri_output + "/mappings.rdf";
			
			updateHTMLProgress("Output mappings (class mappings = " + num_class_mappings + 
						", property mappings = " + num_prop_mappings + ", instance mappings = " + num_instance_mappings + "): " + 
						"<a href=\"" + iri_mappings_owl +  "\">[OWL format]</a>, " + 
						"<a href=\"" + iri_mappings_txt + "\">[TXT format]</a>, " + 
						"<a href=\"" + iri_mappings_rdf + "\">[OAEI Alignment format]</a>.");
			*/
			
			
			fin = Calendar.getInstance().getTimeInMillis();			
			writeInternalLog("Time Saving mapping 2 review (s): " + (float)((double)fin-(double)init)/1000.0);
		
	}
	
	
	
	/**
	 * If no mappings requiring user feedback we store them.
	 * @throws Exception
	 */
	private void saveExtractedMappings() throws Exception{
		
		init = Calendar.getInstance().getTimeInMillis();
		
		
		int dir_mapping;
		
		int num_class_mappings=0;
		int num_prop_mappings=0;
		int num_instance_mappings=0;
		
		OutPutFilesManager outPutFilesManager = new OutPutFilesManager();
		
		
		outPutFilesManager.createOutFiles(
					path_output + "/mappings", 
					OutPutFilesManager.AllFormats,
					onto_process1.getOntoIRI(),
					onto_process1.getOntoIRI());
			
						
			for (int ide1 : getClassMappings().keySet()){
				for (int ide2 : getClassMappings().get(ide1)){
					
					dir_mapping = getDirClassMapping(ide1, ide2);
					
					if (dir_mapping!=Utilities.NoMap){
						
						num_class_mappings++;
						
						if (dir_mapping!=Utilities.R2L){
									
							outPutFilesManager.addClassMapping2Files(
									getIRI4ConceptIdentifier(ide1),
									getIRI4ConceptIdentifier(ide2),
									dir_mapping,
									getConfidence4ConceptMapping(ide1, ide2)
									);
						}
						else{
							outPutFilesManager.addClassMapping2Files(								
									getIRI4ConceptIdentifier(ide2),
									getIRI4ConceptIdentifier(ide1),
									dir_mapping,
									getConfidence4ConceptMapping(ide2, ide1)
									);
						}
					}
				}
			}
			
			for (int ide1 : getDataPropMappings().keySet()){
				
				num_prop_mappings++;
				
				outPutFilesManager.addDataPropMapping2Files(
							getIRI4DataPropIdentifier(ide1),
							getIRI4DataPropIdentifier(getDataPropMappings().get(ide1)),
							Utilities.EQ,  
							getConfidence4DataPropConceptMapping(ide1, getDataPropMappings().get(ide1))							
							);
			}
			
			for (int ide1 : getObjectPropMappings().keySet()){		
				
				num_prop_mappings++;
				
				outPutFilesManager.addObjPropMapping2Files(
							getIRI4ObjectPropIdentifier(ide1),
							getIRI4ObjectPropIdentifier(getObjectPropMappings().get(ide1)),
							Utilities.EQ, 
							getConfidence4ObjectPropConceptMapping(ide1, getObjectPropMappings().get(ide1))
							);
			}
			
			
			//TODO: update!!
			if (Parameters.perform_instance_matching && Parameters.output_instance_mappings){
				
				for (int ide1 : getInstanceMappings().keySet()){
					for (int ide2 : getInstanceMappings().get(ide1)){
						
						num_instance_mappings++;
					
						outPutFilesManager.addInstanceMapping2Files(
								getIRI4InstanceIdentifier(ide1), 
								getIRI4InstanceIdentifier(ide2), 
								getConfidence4InstanceMapping(ide1, ide2)
							);
						
					}
				
				}
			}
			
			
						
			outPutFilesManager.closeAndSaveFiles();
			
			String iri_mappings_owl = iri_output + "/mappings.owl";
			String iri_mappings_txt = iri_output + "/mappings.txt";
			String iri_mappings_rdf = iri_output + "/mappings.rdf";
			
			updateHTMLProgress("Output mappings (class mappings = " + num_class_mappings + 
						", property mappings = " + num_prop_mappings + ", instance mappings = " + num_instance_mappings + "): " + 
						"<a href=\"" + iri_mappings_owl +  "\">[OWL format]</a>, " + 
						"<a href=\"" + iri_mappings_txt + "\">[TXT format]</a>, " + 
						"<a href=\"" + iri_mappings_rdf + "\">[OAEI Alignment format]</a>.");
			
			
			
			fin = Calendar.getInstance().getTimeInMillis();			
			writeInternalLog("Time Saving output mapping files (s): " + (float)((double)fin-(double)init)/1000.0);
			
		
		
		
	}

	
	
	

	/**
	 * Discovery and assessment of Instance mappings
	 */
	private void createAndAssessInstanceMappings() {

		mapping_extractor.createInstanceAnchors();

		// Clean D&G
		if (mapping_extractor.getInstanceMappings().size() > 0 && cleanD_G) {

			init = Calendar.getInstance().getTimeInMillis();

			// We have an specific method since there is not a top-down search.
			// And we first repair classes
			mappings_assessment
					.CheckSatisfiabilityOfIntegration_DandG_Individuals(mapping_extractor
							.getInstanceMappings());

			fin = Calendar.getInstance().getTimeInMillis();
			LogOutput.printAlways("Time cleaning instance mappings D&G (s): "
					+ (float) ((double) fin - (double) init) / 1000.0);
		}

		onto_process1.clearInvertedFiles4Individuals();
		onto_process2.clearInvertedFiles4Individuals();

	}

	public Map<Integer, Set<Integer>> getClassMappings() {
		return mapping_extractor.getAnchors();

	}

	public int getDirClassMapping(int ide1, int ide2) {
		return mapping_extractor.getDirMapping(ide1, ide2);
	}

	public String getIRIOntology1() {
		return onto_process1.getOntoIRI();
	}

	public String getIRIOntology2() {
		return onto_process2.getOntoIRI();
	}

	public Map<Integer, Integer> getDataPropMappings() {
		return mapping_extractor.getDataPropertyAnchors();
	}

	public Map<Integer, Integer> getObjectPropMappings() {
		return mapping_extractor.getObjectPropertyAnchors();
	}

	public Map<Integer, Set<Integer>> getInstanceMappings() {
		return mapping_extractor.getInstanceMappings();
	}

	public double getConfidence4ConceptMapping(int ide1, int ide2) {
		return mapping_extractor.getConfidence4Mapping(ide1, ide2);
	}

	public double getConfidence4DataPropConceptMapping(int ide1, int ide2) {
		return mapping_extractor.getConfidence4DataPropertyAnchor(ide1, ide2);
	}

	public double getConfidence4ObjectPropConceptMapping(int ide1, int ide2) {
		return mapping_extractor.getConfidence4ObjectPropertyAnchor(ide1, ide2);
	}

	public double getConfidence4InstanceMapping(int ide1, int ide2) {
		return mapping_extractor.getConfidence4InstanceMapping(ide1, ide2);
	}

	public String getIRI4ConceptIdentifier(int ide) {
		return index.getIRIStr4ConceptIndex(ide);
	}

	public String getIRI4DataPropIdentifier(int ide) {
		return index.getIRIStr4DataPropIndex(ide);
	}

	public String getIRI4ObjectPropIdentifier(int ide) {
		return index.getIRIStr4ObjPropIndex(ide);
	}

	public String getIRI4InstanceIdentifier(int ide) {
		return index.getIRIStr4IndividualIndex(ide);
	}

	public void clearIndexStructures() {
		index.clearTaxonomicalStructures();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String base_path = "/usr/local/apache-tomcat-7.0.27/webapps/ROOT/output";

		String base_uri = "http://192.168.18.200:8080";

		// String
		// uri1="ftp://ftp1.nci.nih.gov/pub/cacore/EVS/NCI_Thesaurus/Thesaurus_12.03d.OWL.zip";
		// String uri1 =
		// "http://csu6325.cs.ox.ac.uk/ontologies/matching_09_05_2012/HERO_ONTOLOGY_V17.03.2012.owl";
		// String
		// uri2="http://protege.cim3.net/file/pub/ontologies/not.galen/not-galen.owl";
		// String
		// uri2="http://csu6325.cs.ox.ac.uk/ontologies/matching_09_05_2012/univ-bench.owl";

		// String uri1 = "http://dbpedia.org/ontology/";
		// String uri2 = "http://dbpedia.org/ontology/";

		// String uri1 =
		// "http://csu6325.cs.ox.ac.uk/ontologies/matching_31_05_2012/ontology_31_05_2012__18_53_50_221";
		// String uri1 =
		// "http://csu6325.cs.ox.ac.uk/ontologies/matching_31_05_2012/ontology_31_05_2012__18_53_50_221";
		// String uri1 =
		// "http://csu6325.cs.ox.ac.uk/ontologies/matching_31_05_2012/ontology_31_05_2012__18_54_00_263";
		// String uri2 =
		// "http://csu6325.cs.ox.ac.uk/ontologies/matching_21_05_2012/emptyOntology.owl";

		//String uri1 = "http://csu6325.cs.ox.ac.uk/ontologies/matching_15_06_2012/sigEDAM.owl";
		//String uri2 = "http://csu6325.cs.ox.ac.uk/ontologies/matching_15_06_2012/1316088466.owl";

		//String uri1 = "http://protege.cim3.net/file/pub/ontologies/tambis/tambis-full.owl";
		//String uri2 = "http://protege.cim3.net/file/pub/ontologies/not.galen/not-galen.owl";
		
		String uri1 = "http://web.informatik.uni-mannheim.de/oaei/anatomy10/data/mouse_anatomy_2010.owl";
		String uri2 = "http://web.informatik.uni-mannheim.de/oaei/anatomy10/data/nci_anatomy_2010.owl";
		
		// Create Output file
		HTMLResultsFileManager output_file_manager = new HTMLResultsFileManager(
				base_path, base_uri, "Ernesto", "LogMap with interactivity", uri1,
				uri2);

		String output_path = base_path
				+ output_file_manager.getRelativeOutputPath();
		String output_uri = base_uri + "/output"
				+ output_file_manager.getRelativeOutputPath();

		try {
			new LogMapInteractivity_WebService(uri1, uri2,
			// "http://csu6325.cs.ox.ac.uk:80/ontologies/matching_08_05_2012/HERO_ONTOLOGY_V 17.03.2012.owl",
			// "http://csu6325.cs.ox.ac.uk:80/ontologies/matching_08_05_2012/univ-bench.owl",
					true, output_path, output_uri, output_file_manager, "ermesto@cs.ox.ac.uk", "Ernesto", "1", ReasonerManager.NONE);
		} catch (Exception e) {
			System.err.println(e.getMessage() + "   " + e.getCause());
			e.printStackTrace();
		}

	}

}

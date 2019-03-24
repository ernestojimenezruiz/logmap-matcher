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


import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;

import org.semanticweb.owlapi.model.AddImport;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLDataFactory;

import uk.ac.manchester.syntactic_locality.OntologyModuleExtractor;

import uk.ac.ox.krr.logmap2.io.LogOutput;
import uk.ac.ox.krr.logmap2.io.OutPutFilesManager;
import uk.ac.ox.krr.logmap2.io.WriteFile;
import uk.ac.ox.krr.logmap2.owlapi.SynchronizedOWLManager;
import uk.ac.ox.krr.logmap_lite.LogMap_Lite;
import uk.ac.ox.krr.logmap_lite.MappingObjectStr;
import uk.ac.ox.krr.logmap_lite.OntologyLoader;
import uk.ac.ox.krr.logmap_lite.OntologyProcessing;


/**
 * 
 * This class includes the basic lexical capabilities of LogMap:
 * overlapping extraction and intersection of inverted files
 * 
 * @author Ernesto Jimenez Ruiz
 *
 */
public class LogMapLite_WebService {

	

	//DIR IMPLICATION
	public static final int L2R=0; //P->Q
	public static final int R2L=-1; //P<-Q
	public static final int EQ=-2; //P<->Q
	
	
	private long init_tot, init, fin;
	private double time_loading=0.0;
	private double total_time=0.0;
	
	private OntologyLoader onto_loader1;
	private OntologyLoader onto_loader2;
	
	private OntologyProcessing onto_proc1;
	private OntologyProcessing onto_proc2;
		
	private OWLOntology module1_overlapping;
	private OWLOntology module2_overlapping;
	private OWLOntology mappings_owl;
	
	private boolean extract_overlapping = true;
	
	private Map<Integer, Set<Integer>> mappings;
	private Map<Integer, Set<Integer>> mappingsOProp;
	private Map<Integer, Set<Integer>> mappingsDProp;
	private Map<Integer, Set<Integer>> mappingsIndiv;
	
	
	private HTMLResultsFileManager progress_manager;
	
	private String path_output; //for mappings and overlapping
	
	private String iri_output;

	//Input URIS
	private String iri1_str;
	private String iri2_str;
	
	
	
	/**
	 * 
	 * @param iri1_str
	 * @param iri2_str
	 * @param path_output
	 * @param iri_output
	 * @param progress_manager
	 */
	public LogMapLite_WebService(
			String iri1_str, 
			String iri2_str,			
			String path_output,
			String iri_output,
			HTMLResultsFileManager progress_manager) throws OWLOntologyCreationException, Exception{
	
		
		this.iri1_str=iri1_str;
		this.iri2_str=iri2_str;
		
		this.progress_manager = progress_manager;
		
		this.path_output = path_output;
		this.iri_output=iri_output;
		
		
		//try {
			
			init_tot = Calendar.getInstance().getTimeInMillis();
			loadingAndProcessingOntologies(iri1_str, iri2_str);
			
			
			
			
			computeMappings();			
			
			
			updateHTMLProgress("Matching task finished. Storing output files...");
			
			
			//Create mappings
			createOWLOntology4Mappings();
				
			//Create overlapping modules
			computeOveralappingAndSaveModules();
			
			//Save integrated ontos
			saveIntegratedOntologies();
			
			
			fin = Calendar.getInstance().getTimeInMillis();
			//System.out.println("Matching Time (s): " + (float)((double)fin-(double)init_tot)/1000.0);
			total_time = (float)((double)fin-(double)init_tot)/1000.0;
			total_time = total_time - time_loading;
			writeInternalLog("Time loading ontos (s): " + time_loading);
			writeInternalLog("LogMap Lite Matching Time (s): " + total_time);
			
		//}
		//catch (Exception e){
		//	writeInternalLog("ERROR: " + e.getMessage());
		//}
			
		
		
		
	}
	
	
	
	private void updateHTMLProgress(String text_progress){
		progress_manager.updateProgress(text_progress);
	}
	
	
	
	private WriteFile internal_log;
	/**
	 * To keep logmap progress... internal use
	 */
	private void writeInternalLog(String text_log){
		
		//System.out.println(text_log);
		
		//We open/close it each time		
		internal_log = new WriteFile(path_output + "/logmap_internal_progress.log", true);
		
		internal_log.writeLine(text_log);
		
		internal_log.closeBuffer();
	}
	
	
	
	
	private void loadingAndProcessingOntologies(String iri1_str, String iri2_str) throws OWLOntologyCreationException, Exception{
		//Loading Ontology 1 and Light processing
		init = Calendar.getInstance().getTimeInMillis();
		onto_loader1 = new OntologyLoader(iri1_str);		
		fin = Calendar.getInstance().getTimeInMillis();
		//System.out.println("Time loading ontology 1 (s): " + (float)((double)fin-(double)init)/1000.0);
		time_loading += (float)((double)fin-(double)init)/1000.0;
		
		updateHTMLProgress("Loading ontology 1...done");
		updateHTMLProgress("Ontology 1: " + 
				onto_loader1.getOWLOntology().getClassesInSignature(true).size() + " classes, " +
				(onto_loader1.getOWLOntology().getDataPropertiesInSignature(true).size() +
						onto_loader1.getOWLOntology().getObjectPropertiesInSignature(true).size()) + " properties, " +
						onto_loader1.getOWLOntology().getIndividualsInSignature(true).size() + " individuals.");
		
		
		init = Calendar.getInstance().getTimeInMillis();
		onto_proc1 = new OntologyProcessing(onto_loader1.getOWLOntology(), extract_overlapping);
				
		onto_loader1.createAxiomSet(); //we only keep set of axioms
		onto_loader1.clearOntology();
		onto_proc1.clearOntoloy();
				
		fin = Calendar.getInstance().getTimeInMillis();
		//System.out.println("Time processing ontology 1 (s): " + (float)((double)fin-(double)init)/1000.0);
		
		writeInternalLog("Time processing ontogy 1 (s): " + (float)((double)fin-(double)init)/1000.0);			
		updateHTMLProgress("Lexical indexing ontology 1...done");
				
			
		//Loading Ontology 2 and Light processing
		init = Calendar.getInstance().getTimeInMillis();
		onto_loader2 = new OntologyLoader(iri2_str);		
		fin = Calendar.getInstance().getTimeInMillis();
		//System.out.println("Time loading ontology 2 (s): " + (float)((double)fin-(double)init)/1000.0);
		time_loading += (float)((double)fin-(double)init)/1000.0;	
		updateHTMLProgress("Loading ontology 2...done");
		
		updateHTMLProgress("Ontology 2: " + 
				onto_loader2.getOWLOntology().getClassesInSignature(true).size() + " classes, " +
				(onto_loader2.getOWLOntology().getDataPropertiesInSignature(true).size() +
						onto_loader2.getOWLOntology().getObjectPropertiesInSignature(true).size()) + " properties, " +
						onto_loader2.getOWLOntology().getIndividualsInSignature(true).size() + " individuals.");
		
		
		
		init = Calendar.getInstance().getTimeInMillis();
		onto_proc2 = new OntologyProcessing(onto_loader2.getOWLOntology(), extract_overlapping);
			
		onto_loader2.createAxiomSet();
		onto_loader2.clearOntology();
		onto_proc2.clearOntoloy();
				
		fin = Calendar.getInstance().getTimeInMillis();
		//System.out.println("Time processing ontology 2 (s): " + (float)((double)fin-(double)init)/1000.0);
		
		writeInternalLog("Time processing ontogy 2 (s): " + (float)((double)fin-(double)init)/1000.0);			
		updateHTMLProgress("Lexical indexing ontology 2...done");
		
	}
	
	
	

	
	
	private void computeMappings(){
		
		
		computeClassMappings();
		
		computeDataPropMappings();
		
		computeObjPropMappings();
		
		compuetIndividualMappings();
		
		//System.out.println("Number of mappings: "+ mappings_ll.size());
		
	}
	
	
	private void computeClassMappings(){
		
		Set<Set<String>> if_exact_intersect;
		
		init = Calendar.getInstance().getTimeInMillis();
		//We perform intersection an we only keep in inverted file the intersected elements
		if_exact_intersect = onto_proc1.getInvertedFileExact().keySet();
		if_exact_intersect.retainAll(onto_proc2.getInvertedFileExact().keySet());
		onto_proc2.getInvertedFileExact().keySet().retainAll(if_exact_intersect);

		//fin = Calendar.getInstance().getTimeInMillis();
		//System.out.println("Time intersecting IF exact classes (s): " + (float)((double)fin-(double)init)/1000.0);
		
		mappings = new HashMap<Integer, Set<Integer>>();
		
		for (Set<String> entry : if_exact_intersect){
			for (int ide1 : onto_proc1.getInvertedFileExact().get(entry)){
				
				if (!mappings.containsKey(ide1))
					mappings.put(ide1, new HashSet<Integer>());
				
				for (int ide2 : onto_proc2.getInvertedFileExact().get(entry)){
					mappings.get(ide1).add(ide2);
					
				}
					
			}
		}
		
		fin = Calendar.getInstance().getTimeInMillis();
		writeInternalLog("Time computing class mappings (s): " + (float)((double)fin-(double)init)/1000.0);			
		updateHTMLProgress("Computing class mappings...done");
		
	}
	
	
	private void computeDataPropMappings(){
		
		Set<Set<String>> if_exact_intersect;
		
		init = Calendar.getInstance().getTimeInMillis();
		//We perform intersection an we only keep in inverted file the intersected elements
		if_exact_intersect = onto_proc1.getInvertedFileExactDataProp().keySet();
		if_exact_intersect.retainAll(onto_proc2.getInvertedFileExactDataProp().keySet());
		onto_proc2.getInvertedFileExactDataProp().keySet().retainAll(if_exact_intersect);

		//fin = Calendar.getInstance().getTimeInMillis();
		//System.out.println("Time intersecting IF exact dProp (s): " + (float)((double)fin-(double)init)/1000.0);
		
		mappingsDProp = new HashMap<Integer, Set<Integer>>();
		
		for (Set<String> entry : if_exact_intersect){
			for (int ide1 : onto_proc1.getInvertedFileExactDataProp().get(entry)){
				
				if (!mappingsDProp.containsKey(ide1))
					mappingsDProp.put(ide1, new HashSet<Integer>());
				
				for (int ide2 : onto_proc2.getInvertedFileExactDataProp().get(entry)){
					mappingsDProp.get(ide1).add(ide2);
					
				}
					
			}
		}
		
		fin = Calendar.getInstance().getTimeInMillis();
		writeInternalLog("Time computing data property mappings (s): " + (float)((double)fin-(double)init)/1000.0);			
		updateHTMLProgress("Computing data property mappings...done");
		
		
	}

	private void computeObjPropMappings(){
		
		Set<Set<String>> if_exact_intersect;
		
		init = Calendar.getInstance().getTimeInMillis();
		//We perform intersection an we only keep in inverted file the intersected elements
		if_exact_intersect = onto_proc1.getInvertedFileExactObjectProp().keySet();
		if_exact_intersect.retainAll(onto_proc2.getInvertedFileExactObjectProp().keySet());
		onto_proc2.getInvertedFileExactObjectProp().keySet().retainAll(if_exact_intersect);

		//fin = Calendar.getInstance().getTimeInMillis();
		//System.out.println("Time intersecting IF exact oProp (s): " + (float)((double)fin-(double)init)/1000.0);
		
		mappingsOProp = new HashMap<Integer, Set<Integer>>();
		
		for (Set<String> entry : if_exact_intersect){
			for (int ide1 : onto_proc1.getInvertedFileExactObjectProp().get(entry)){
				
				if (!mappingsOProp.containsKey(ide1))
					mappingsOProp.put(ide1, new HashSet<Integer>());
				
				for (int ide2 : onto_proc2.getInvertedFileExactObjectProp().get(entry)){
					mappingsOProp.get(ide1).add(ide2);
					
				}
					
			}
		}
		
		fin = Calendar.getInstance().getTimeInMillis();
		writeInternalLog("Time computing object property mappings (s): " + (float)((double)fin-(double)init)/1000.0);			
		updateHTMLProgress("Computing object property mappings...done");
		
		
	}
	
	
	
	private void compuetIndividualMappings(){
		
		
		Set<Set<String>> if_exact_intersect_indiv;
		
		init = Calendar.getInstance().getTimeInMillis();
		//We perform intersection an we only keep in inverted file the intersected elements
		if_exact_intersect_indiv = onto_proc1.getInvertedFileExactIndividuals().keySet();
		if_exact_intersect_indiv.retainAll(onto_proc2.getInvertedFileExactIndividuals().keySet());
		onto_proc2.getInvertedFileExactIndividuals().keySet().retainAll(if_exact_intersect_indiv);

		//fin = Calendar.getInstance().getTimeInMillis();
		//System.out.println("Time intersecting IF exact classes (s): " + (float)((double)fin-(double)init)/1000.0);
		
		mappingsIndiv = new HashMap<Integer, Set<Integer>>();
		
		for (Set<String> entry : if_exact_intersect_indiv){
			
			/*if (entry.isEmpty()){
				System.out.println("EMPTY SET IN IF CLASSES");
				continue;
			}*/
			
			for (int ide1 : onto_proc1.getInvertedFileExactIndividuals().get(entry)){
				
				if (!mappingsIndiv.containsKey(ide1))
					mappingsIndiv.put(ide1, new HashSet<Integer>());
				
				for (int ide2 : onto_proc2.getInvertedFileExactIndividuals().get(entry)){
					mappingsIndiv.get(ide1).add(ide2);
					
				}
					
			}
		}
		
		fin = Calendar.getInstance().getTimeInMillis();
		writeInternalLog("Time computing instance mappings (s): " + (float)((double)fin-(double)init)/1000.0);			
		updateHTMLProgress("Computing instance mappings...done");
		
	}
	
	
	
	
	private void computeOveralappingAndSaveModules() throws Exception{
		init = Calendar.getInstance().getTimeInMillis();
		
		Set<Set<String>> if_weak_intersect;
		
		//We perform intersection an we only keep in inverted file the intersected elements
		if_weak_intersect = onto_proc1.getWeakInvertedFile().keySet();
		if_weak_intersect.retainAll(onto_proc2.getWeakInvertedFile().keySet());
		onto_proc2.getWeakInvertedFile().keySet().retainAll(if_weak_intersect);

		
		//fin = Calendar.getInstance().getTimeInMillis();
		//System.out.println("\tTime intersecting IF weak (s): " + (float)((double)fin-(double)init)/1000.0);
		
		
		Set<OWLEntity> entities1 = new HashSet<OWLEntity>();
		Set<OWLEntity> entities2 = new HashSet<OWLEntity>();
		
		for (Set<String> str_set: if_weak_intersect){
			for (int ide1 : onto_proc1.getWeakInvertedFile().get(str_set)){
				entities1.add(onto_proc1.getOWLClass4identifier(ide1));
			}
			for (int ide2 : onto_proc2.getWeakInvertedFile().get(str_set)){
				entities2.add(onto_proc2.getOWLClass4identifier(ide2));
			}
			
		}
		
		//LogOutput.print("\tSize entities 1: " + entities1.size());
		//LogOutput.print("\tSize entities 2: " + entities2.size());
		
		//We empty overlapping structures
		onto_proc1.clearStructures();
		onto_proc2.clearStructures();
		
		//fin = Calendar.getInstance().getTimeInMillis();
		//System.out.println("\tTime extracting entities4modules (s): " + (float)((double)fin-(double)init)/1000.0);
		
		
		//Extract module (create new constructor)
		//Give onto IRI to module
		init = Calendar.getInstance().getTimeInMillis();
		
		//ModuleExtractor module_extractor1 = new ModuleExtractor(
		//		onto_loader1.getAxiomSet(), SynchronizedOWLManager.createOWLOntologyManager(), false, false, true, true, false);
		//module1_overlapping = module_extractor1.getLocalityModuleForSignatureGroup(entities1, onto_loader1.getOntologyIRIStr(), false);		
		OntologyModuleExtractor module_extractor1 =
				new OntologyModuleExtractor(
						SynchronizedOWLManager.createOWLOntologyManager(),
						onto_loader1.getAxiomSet(),
						true,
						false,
						true);
		
		module1_overlapping = module_extractor1.extractAsOntology(
				entities1, 
				IRI.create(onto_loader1.getOntologyIRIStr()));
		
		
		
		//Save module 1
		//module_extractor1.saveExtractedModule("file:" + path_output + "/module1.owl");
		SynchronizedOWLManager.saveOntology(module1_overlapping, "file:" + path_output + "/module1.owl");
		
		
		module_extractor1.clearStrutures();
		
		
		onto_loader1.clearAxiomSet();
		entities1.clear(); 
		
		//ModuleExtractor module_extractor2 = new ModuleExtractor(
		//		onto_loader2.getAxiomSet(), SynchronizedOWLManager.createOWLOntologyManager(), false, false, true, true, false);
		//module2_overlapping = module_extractor2.getLocalityModuleForSignatureGroup(entities2, onto_loader2.getOntologyIRIStr(), false);
		OntologyModuleExtractor module_extractor2 =
				new OntologyModuleExtractor(
						SynchronizedOWLManager.createOWLOntologyManager(),
						onto_loader2.getAxiomSet(),
						true,
						false,
						true);
		module2_overlapping = module_extractor2.extractAsOntology(
				entities2, 
				IRI.create(onto_loader2.getOntologyIRIStr()));
		
		
		
		//Save module 2
		//module_extractor2.saveExtractedModule("file:" + path_output + "/module2.owl");
		SynchronizedOWLManager.saveOntology(module2_overlapping, "file:" + path_output + "/module2.owl");
		
		module_extractor2.clearStrutures();
		
		onto_loader2.clearAxiomSet();
		entities2.clear(); 
		
		

		fin = Calendar.getInstance().getTimeInMillis();			
		writeInternalLog("Time saving overlapping (s): " + (float)((double)fin-(double)init)/1000.0);
		
		
		//fin = Calendar.getInstance().getTimeInMillis();
		//System.out.println("\tSize module 1: " + module1_overlapping.getSignature().size() + "  " + onto_loader1.getSignatureSize());
		//System.out.println("\tSize module 2: " + module2_overlapping.getSignature().size() + "  " + onto_loader2.getSignatureSize());
		//System.out.println("\tSize classes module 1: " + module1_overlapping.getClassesInSignature().size() + "  " + onto_loader1.getClassesInSignatureSize());
		//System.out.println("\tSize classes module 2: " + module2_overlapping.getClassesInSignature().size() + "  " + onto_loader2.getClassesInSignatureSize());
		//System.out.println("\tTime extracting modules (s): " + (float)((double)fin-(double)init)/1000.0);
		
	}
	
	
	public OWLOntology getModule1(){
		return module1_overlapping;
	}
	public OWLOntology getModule2(){
		return module2_overlapping;
	}
	
	public OWLOntology getOWLMappingsOntology(){
		return mappings_owl;
	}
	
	
	private void createOWLOntology4Mappings() throws Exception{
		
		init = Calendar.getInstance().getTimeInMillis();
		
		OutPutFilesManager outPutFilesManager = new OutPutFilesManager();
		
		int num_class_mappings=0;
		int num_prop_mappings=0;
		int num_instance_mappings=0;
		
		outPutFilesManager.createOutFiles(
					path_output + "/mappings", 
					OutPutFilesManager.AllFormats,
					onto_loader1.getOntologyIRIStr(), 
					onto_loader2.getOntologyIRIStr());
		
		
		for (int ide1 : mappings.keySet()){
			for (int ide2 : mappings.get(ide1)){
				
				num_class_mappings++;
				
				outPutFilesManager.addClassMapping2Files(
						onto_proc1.getIRI4identifier(ide1),
						onto_proc2.getIRI4identifier(ide2),
						LogMap_Lite.EQ, 
						1.0
						);					
			}
		}
		
		for (int ide1 : mappingsDProp.keySet()){
			for (int ide2 : mappingsDProp.get(ide1)){
				
				num_prop_mappings++;
				
				outPutFilesManager.addDataPropMapping2Files(
						onto_proc1.getIRI4DPropIdentifier(ide1),
						onto_proc2.getIRI4DPropIdentifier(ide2),
						LogMap_Lite.EQ, 
						1.0
						);					
			}
		}
		
		for (int ide1 : mappingsOProp.keySet()){
			for (int ide2 : mappingsOProp.get(ide1)){
				
				num_prop_mappings++;
				
				outPutFilesManager.addObjPropMapping2Files(
						onto_proc1.getIRI4OPropIdentifier(ide1),
						onto_proc2.getIRI4OPropIdentifier(ide2),
						LogMap_Lite.EQ, 
						1.0
						);					
			}
		}
		
		
		for (int ide1 : mappingsIndiv.keySet()){
			for (int ide2 : mappingsIndiv.get(ide1)){
				
				num_instance_mappings++;
			
				outPutFilesManager.addInstanceMapping2Files(
						onto_proc1.getIRI4Individual(ide1),
						onto_proc2.getIRI4Individual(ide2),  
						1.0
					);
				
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
	 * This method creates and saves the correspondent integrated ontologies
	 */
	private void saveIntegratedOntologies() throws Exception{
		
		
		//OWLDataFactory dataFactory = OWLManager.getOWLDataFactory();
		OWLDataFactory dataFactory = SynchronizedOWLManager.createOWLDataFactory();
		
		init = Calendar.getInstance().getTimeInMillis();
		
		
		//Create ontologies importing integrated ontologies			
		
		String iri_module1 = iri_output + "/module1.owl";
		String iri_module2 = iri_output + "/module2.owl";
		String iri_mappings = iri_output + "/mappings.owl";
		String iri_integrated_onto = iri_output + "/integratedOntology.owl";
		String iri_integrated_onto_modules = iri_output + "/integratedOntologyWithModules.owl";
		
		//We need the IRIs to use the reasoner (optional)
		//progress_manager.setIntegratedOntologyIRIStr(iri_integrated_onto);
		//progress_manager.setIntegratedOntologyModulesIRIStr(iri_integrated_onto_modules);
		
		
		List<AddImport> listImports2Add= new ArrayList<AddImport>();
		
		
		
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
		
		
		updateHTMLProgress("Full integrated ontology: " + 
				"<a href=\"" + iri_integrated_onto + "\">[Original ontologies + mappings]</a>, " + 
				//"<a href=\"" + iri_integrated_onto_modules + "\">[Overlapping ontologies + mappings]</a> " + 
				"<i>(note that this ontology only imports the respective OWL files)</i>");
		
		
		
		
		///INTEGRATED ONTOLOGY WITH MODULES		
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
		
				
		
		
		
		
		
		
		updateHTMLProgress("Overlapping ontology modules: " + 
				"<a href=\"" + iri_module1 + "\">[Module 1]</a>, " + 
				"<a href=\"" + iri_module2 + "\">[Module 2]</a>.");
		
		updateHTMLProgress("Integrated ontology using overlappings/modules: " +  
				//"<a href=\"" + iri_integrated_onto + "\">[Original ontologies + mappings]</a>, " + 
				"<a href=\"" + iri_integrated_onto_modules + "\">[Overlapping ontologies + mappings]</a> " + 
				"<i>(note that this ontology only imports the respective OWL files)</i>");
		
		
		
		
		fin = Calendar.getInstance().getTimeInMillis();			
		writeInternalLog("Time Saving overlapping and integrated ontologies (s): " + (float)((double)fin-(double)init)/1000.0);
		
		
	}
	
	

	
	
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		
		String base_path = "/usr/local/apache-tomcat-7.0.27/webapps/ROOT/output";
		
		String base_uri = "http://192.168.18.200:8080";
	       
		String uri1="http://192.168.18.200:8080/output/ontologies/mouse_anatomy2011.owl"; 
		String uri2="http://192.168.18.200:8080/output/ontologies/nci_anatomy2011.owl";
	       
	    //Create Output file
	    HTMLResultsFileManager output_file_manager = new HTMLResultsFileManager(base_path, base_uri, "Ernesto", "LogMap Lite", uri1, uri2);
		
	    
	    String output_path =  base_path + output_file_manager.getRelativeOutputPath();
	    String output_uri = base_uri + "/output" + output_file_manager.getRelativeOutputPath();
	    
	    try {
			new LogMapLite_WebService(
					uri1,
					uri2,
					output_path,
					output_uri,
					output_file_manager
					);
	    }
		catch (Exception e){
			e.printStackTrace();
		}
		
		
	}
	
	
	
}

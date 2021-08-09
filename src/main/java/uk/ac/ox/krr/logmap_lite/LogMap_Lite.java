package uk.ac.ox.krr.logmap_lite;


import java.net.URL;




import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

//import uk.ac.manchester.syntactic_locality.ModuleExtractor;
import uk.ac.manchester.syntactic_locality.OntologyModuleExtractor;

import uk.ac.ox.krr.logmap2.io.LogOutput;
import uk.ac.ox.krr.logmap2.io.OutPutFilesManager;
import uk.ac.ox.krr.logmap2.owlapi.SynchronizedOWLManager;
import uk.ac.ox.krr.logmap2.reasoning.SatisfiabilityIntegration;
import uk.ac.ox.krr.logmap_lite.io.ReadFile;
import uk.ac.ox.krr.logmap_lite.io.OAEIAlignmentOutput;
import uk.ac.ox.krr.logmap_lite.io.OWLAlignmentFormat;
import uk.ac.ox.krr.logmap_lite.io.RDFAlignmentFormat;




/**
 * 
 * This class includes the basic lexical capabilities of LogMap:
 * overlapping extraction and intersection of inverted files
 * 
 * @author Ernesto Jimenez Ruiz
 *
 */
public class LogMap_Lite {

	

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
	
	private boolean extract_overlapping=false;
	private boolean check_with_gold_standard=false;
	
	private OWLOntology module1_overlapping;
	private OWLOntology module2_overlapping;
	private OWLOntology mappings_owl;
	
	private OAEIAlignmentOutput alignment_output;
	
	private Map<Integer, Set<Integer>> mappings;
	private Map<Integer, Set<Integer>> mappingsOProp;
	private Map<Integer, Set<Integer>> mappingsDProp;
	private Map<Integer, Set<Integer>> mappingsIndiv;
	
	
	private boolean outPutOnlyIndiv;
	private String output_path;
	
	
	/**
	 * Constructor OAEI
	 */
	public LogMap_Lite(){
		
		extract_overlapping=false;
		check_with_gold_standard=false;
		
	}
	
	
	/**
	 * General constructor
	 * @param iri1_str
	 * @param iri2_str
	 * @param extract_overlapping
	 */
	public LogMap_Lite(
			String iri1_str, 
			String iri2_str, 
			String gs_mappings_file, 
			boolean create_owl_mappings_onto, 
			boolean extract_overlapping,
			boolean eval_impact){
	
		
		this.extract_overlapping=extract_overlapping;
		
		try {
			
			outPutOnlyIndiv=false;
			
		
			init_tot = Calendar.getInstance().getTimeInMillis();
			loadingAndProcessingOntologies(iri1_str, iri2_str);
			
			if (!gs_mappings_file.equals(""))	
				check_with_gold_standard=true;
			
			
			computeMappings();			
			
			
			if (check_with_gold_standard){
	
				loadMappingsGS(gs_mappings_file);
				
				getPrecisionAndRecallMappings();
			
			}
			
			
			
			fin = Calendar.getInstance().getTimeInMillis();
			//System.out.println("Matching Time (s): " + (float)((double)fin-(double)init_tot)/1000.0);
			total_time = (float)((double)fin-(double)init_tot)/1000.0;
			total_time = total_time - time_loading;
			System.out.println("Time loading ontos (s): " + time_loading);
			System.out.println("LogMap Lite Matching Time (s): " + total_time);
			
			
			if (create_owl_mappings_onto)
				createOWLOntology4Mappings();
				
				
			if (extract_overlapping){
				computeOveralapping();  //Module1 and Module2
			}
			
			if (eval_impact){
				init = Calendar.getInstance().getTimeInMillis();
				impactIntegration();
				fin = Calendar.getInstance().getTimeInMillis();
				total_time = (float)((double)fin-(double)init)/1000.0;
				System.out.println("Time checking impact (s): " + total_time);
			}
			
			
		}
		catch (Exception e){
			e.printStackTrace();
		}
			
		
		
		
	}
	
	
	/**
	 * Constructor for Individual matching
	 * @param iri1_str
	 * @param iri2_str
	 * @param gs_mappings_file
	 * @param output_path
	 */
	public LogMap_Lite(
			String iri1_str, 
			String iri2_str, 
			String gs_mappings_file, 
			String output_path){
	
		
		try {
			
			outPutOnlyIndiv=true;
			
			this.output_path = output_path;
			
		
			init_tot = Calendar.getInstance().getTimeInMillis();
			loadingAndProcessingOntologies(iri1_str, iri2_str);
			
			if (!gs_mappings_file.equals(""))	
				check_with_gold_standard=true;
			
			
			computeMappings();			
			
			
			if (check_with_gold_standard){
	
				loadMappingsGS(gs_mappings_file);
				
				getPrecisionAndRecallMappings();
			
			}
			
			createOWLOntology4Mappings();
			
			
			saveExtractedIndividualMappings("logmap_lite");
			
			
			fin = Calendar.getInstance().getTimeInMillis();
			//System.out.println("Matching Time (s): " + (float)((double)fin-(double)init_tot)/1000.0);
			total_time = (float)((double)fin-(double)init_tot)/1000.0;
			total_time = total_time - time_loading;
			System.out.println("Time loading ontos (s): " + time_loading);
			System.out.println("LogMap Lite Matching Time (s): " + total_time + "\n");
			
			
			
			
			
			
			
		}
		catch (Exception e){
			e.printStackTrace();
		}
			
		
	}
	
	
	public LogMap_Lite(
			String iri1_str, 
			String iri2_str,
			String output_path) {
		
		try {
			
			align(iri1_str, iri2_str);
			saveMapping(output_path);
			
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
		
	}
	
		
	
	
	
	
	
	/**
	 * Method OAEI
	 * @param source
	 * @param target
	 * @throws Exception
	 */
	public void align(URL source, URL target) throws Exception{
		
		
		init_tot = Calendar.getInstance().getTimeInMillis();
		
		loadingAndProcessingOntologies(source.toURI().toString(), target.toURI().toString());
		
		computeMappings();
		
		fin = Calendar.getInstance().getTimeInMillis();
		//System.out.println("Matching Time (s): " + (float)((double)fin-(double)init_tot)/1000.0);
		total_time = (float)((double)fin-(double)init_tot)/1000.0;
		total_time = total_time - time_loading;
		System.out.println("Time loading ontos (s): " + time_loading);
		System.out.println("LogMap Lite Matching Time (s): " + total_time);
		
		
	}
	
	
	/**
	 * Basic method 
	 * @param source
	 * @param target
	 * @throws Exception
	 */
	public void align(String source, String target) throws Exception{
		
		
		init_tot = Calendar.getInstance().getTimeInMillis();
		
		loadingAndProcessingOntologies(source, target);
		
		computeMappings();
		
		fin = Calendar.getInstance().getTimeInMillis();
		//System.out.println("Matching Time (s): " + (float)((double)fin-(double)init_tot)/1000.0);
		total_time = (float)((double)fin-(double)init_tot)/1000.0;
		total_time = total_time - time_loading;
		System.out.println("Size LogMap Lite mappings: "+ mappings.size());
		System.out.println("Time loading ontos (s): " + time_loading);
		System.out.println("LogMap Lite Matching Time (s): " + total_time);
		
		
	}
	
	
	/**
	 * Method OAEI
	 * @return
	 * @throws Exception
	 */
	public URL returnAlignmentFile() throws Exception{

		
		alignment_output = new OAEIAlignmentOutput("alignment", onto_loader1.getOntologyIRIStr(), onto_loader2.getOntologyIRIStr());
		
		
		for (int ide1 : mappings.keySet()){
			for (int ide2 : mappings.get(ide1)){
				
				alignment_output.addClassMapping2Output(
						onto_proc1.getIRI4identifier(ide1),
						onto_proc2.getIRI4identifier(ide2),
						LogMap_Lite.EQ, 
						1.0
						);					
			}
		}
		
		for (int ide1 : mappingsDProp.keySet()){
			for (int ide2 : mappingsDProp.get(ide1)){
				
				alignment_output.addDataPropMapping2Output(
						onto_proc1.getIRI4DPropIdentifier(ide1),
						onto_proc2.getIRI4DPropIdentifier(ide2),
						LogMap_Lite.EQ, 
						1.0
						);					
			}
		}
		
		for (int ide1 : mappingsOProp.keySet()){
			for (int ide2 : mappingsOProp.get(ide1)){
				
				alignment_output.addObjPropMapping2Output(
						onto_proc1.getIRI4OPropIdentifier(ide1),
						onto_proc2.getIRI4OPropIdentifier(ide2),
						LogMap_Lite.EQ, 
						1.0
						);					
			}
		}
		
		for (int ide1 : mappingsIndiv.keySet()){
			for (int ide2 : mappingsIndiv.get(ide1)){
				alignment_output.addInstanceMapping2Output(
						onto_proc1.getIRI4Individual(ide1),
						onto_proc2.getIRI4Individual(ide2),
						1.0
						);					
			}
		}
		
		
	
		
		
		alignment_output.saveOutputFile();
		
		return alignment_output.returnAlignmentFile();
		
	}
	
	
	
	public void saveMapping(String output_folder) throws Exception{

		OutPutFilesManager ouput_manager =  new OutPutFilesManager();
		
		ouput_manager.createOutFiles(output_folder+"logmap-lite-mappings", OutPutFilesManager.AllFormats, onto_loader1.getOntologyIRIStr(), onto_loader2.getOntologyIRIStr());
		
		
		for (int ide1 : mappings.keySet()){
			for (int ide2 : mappings.get(ide1)){
				
				ouput_manager.addClassMapping2Files(
						onto_proc1.getIRI4identifier(ide1),
						onto_proc2.getIRI4identifier(ide2),
						LogMap_Lite.EQ, 
						1.0
						);					
			}
		}
		
		for (int ide1 : mappingsDProp.keySet()){
			for (int ide2 : mappingsDProp.get(ide1)){
				
				ouput_manager.addDataPropMapping2Files(
						onto_proc1.getIRI4DPropIdentifier(ide1),
						onto_proc2.getIRI4DPropIdentifier(ide2),
						LogMap_Lite.EQ, 
						1.0
						);					
			}
		}
		
		for (int ide1 : mappingsOProp.keySet()){
			for (int ide2 : mappingsOProp.get(ide1)){
				
				ouput_manager.addObjPropMapping2Files(
						onto_proc1.getIRI4OPropIdentifier(ide1),
						onto_proc2.getIRI4OPropIdentifier(ide2),
						LogMap_Lite.EQ, 
						1.0
						);					
			}
		}
		
		for (int ide1 : mappingsIndiv.keySet()){
			for (int ide2 : mappingsIndiv.get(ide1)){
				ouput_manager.addInstanceMapping2Files(
						onto_proc1.getIRI4Individual(ide1),
						onto_proc2.getIRI4Individual(ide2),
						1.0
						);					
			}
		}
		
		
	
		
		
		ouput_manager.closeAndSaveFiles();
		
		
		
	}
	
	
	/**
	 * So far only instance
	 * @param file_name
	 * @throws Exception
	 */
	private void saveExtractedIndividualMappings(String file_name) throws Exception{
		
		RDFAlignmentFormat rdf_output = new RDFAlignmentFormat(
				output_path + "/" + file_name + ".rdf", onto_loader1.getOntologyIRIStr(), onto_loader2.getOntologyIRIStr());
		
		//rdf_output.
		
		for (int ide1 : mappingsIndiv.keySet()){
			for (int ide2 : mappingsIndiv.get(ide1)){
				rdf_output.addInstanceMapping2Output(
						onto_proc1.getIRI4Individual(ide1),
						onto_proc2.getIRI4Individual(ide2),
						1.0
						);					
			}
		}
		
		
		rdf_output.saveOutputFile();
		
		
		
		
	}
	
	
	
	
	private void loadingAndProcessingOntologies(String iri1_str, String iri2_str) throws Exception{
		//Loading Ontology 1 and Light processing
		init = Calendar.getInstance().getTimeInMillis();
		onto_loader1 = new OntologyLoader(iri1_str);		
		fin = Calendar.getInstance().getTimeInMillis();
		//System.out.println("Time loading ontology 1 (s): " + (float)((double)fin-(double)init)/1000.0);
		time_loading += (float)((double)fin-(double)init)/1000.0;
		
		init = Calendar.getInstance().getTimeInMillis();
		onto_proc1 = new OntologyProcessing(onto_loader1.getOWLOntology(), extract_overlapping);
				
		onto_loader1.createAxiomSet(); //we only keep set of axioms
		onto_loader1.clearOntology();
		onto_proc1.clearOntoloy();
				
		fin = Calendar.getInstance().getTimeInMillis();
		//System.out.println("Time processing ontology 1 (s): " + (float)((double)fin-(double)init)/1000.0);
				
			
		//Loading Ontology 2 and Light processing
		init = Calendar.getInstance().getTimeInMillis();
		onto_loader2 = new OntologyLoader(iri2_str);
		fin = Calendar.getInstance().getTimeInMillis();
		//System.out.println("Time loading ontology 2 (s): " + (float)((double)fin-(double)init)/1000.0);
		time_loading += (float)((double)fin-(double)init)/1000.0;	
				
		init = Calendar.getInstance().getTimeInMillis();
		onto_proc2 = new OntologyProcessing(onto_loader2.getOWLOntology(), extract_overlapping);
			
		onto_loader2.createAxiomSet();
		onto_loader2.clearOntology();
		onto_proc2.clearOntoloy();
				
		fin = Calendar.getInstance().getTimeInMillis();
		//System.out.println("Time processing ontology 2 (s): " + (float)((double)fin-(double)init)/1000.0);
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

		fin = Calendar.getInstance().getTimeInMillis();
		//System.out.println("Time intersecting IF exact classes (s): " + (float)((double)fin-(double)init)/1000.0);
		
		mappings = new HashMap<Integer, Set<Integer>>();
		
		for (Set<String> entry : if_exact_intersect){
			
			if (entry.isEmpty()){
				System.out.println("EMPTY SET IN IF CLASSES");
				continue;
			}
			
			for (int ide1 : onto_proc1.getInvertedFileExact().get(entry)){
				
				if (!mappings.containsKey(ide1))
					mappings.put(ide1, new HashSet<Integer>());
				
				for (int ide2 : onto_proc2.getInvertedFileExact().get(entry)){
					mappings.get(ide1).add(ide2);
					
				}
					
			}
		}
		
		//For local comparison
		if (check_with_gold_standard && !outPutOnlyIndiv){
			
			for (int ide1 : mappings.keySet()){
				for (int ide2 : mappings.get(ide1)){
						
					mappings_ll.add(new MappingObjectStr(
							onto_proc1.getIRI4identifier(ide1),
							onto_proc2.getIRI4identifier(ide2)));
					
				}
			}
			
		}
	}
	
	
	
	
	
	private void compuetIndividualMappings(){
		
		
		Set<Set<String>> if_exact_intersect_indiv;
		
		init = Calendar.getInstance().getTimeInMillis();
		//We perform intersection an we only keep in inverted file the intersected elements
		if_exact_intersect_indiv = onto_proc1.getInvertedFileExactIndividuals().keySet();
		if_exact_intersect_indiv.retainAll(onto_proc2.getInvertedFileExactIndividuals().keySet());
		onto_proc2.getInvertedFileExactIndividuals().keySet().retainAll(if_exact_intersect_indiv);

		fin = Calendar.getInstance().getTimeInMillis();
		//System.out.println("Time intersecting IF exact classes (s): " + (float)((double)fin-(double)init)/1000.0);
		
		mappingsIndiv = new HashMap<Integer, Set<Integer>>();
		
		for (Set<String> entry : if_exact_intersect_indiv){
			
			if (entry.isEmpty()){
				System.out.println("EMPTY SET IN IF CLASSES");
				continue;
			}
			
			for (int ide1 : onto_proc1.getInvertedFileExactIndividuals().get(entry)){
				
				if (!mappingsIndiv.containsKey(ide1))
					mappingsIndiv.put(ide1, new HashSet<Integer>());
				
				for (int ide2 : onto_proc2.getInvertedFileExactIndividuals().get(entry)){
					mappingsIndiv.get(ide1).add(ide2);
					
				}
					
			}
		}
		
		//For local comparison
		if (check_with_gold_standard){
			for (int ide1 : mappingsIndiv.keySet()){
				for (int ide2 : mappingsIndiv.get(ide1)){
						
					mappings_ll.add(new MappingObjectStr(
							onto_proc1.getIRI4Individual(ide1),
							onto_proc2.getIRI4Individual(ide2)));
					
				}
			}
		}
		
		
	}
	
	
	private void computeDataPropMappings(){
		
		Set<Set<String>> if_exact_intersect;
		
		init = Calendar.getInstance().getTimeInMillis();
		//We perform intersection an we only keep in inverted file the intersected elements
		if_exact_intersect = onto_proc1.getInvertedFileExactDataProp().keySet();
		if_exact_intersect.retainAll(onto_proc2.getInvertedFileExactDataProp().keySet());
		onto_proc2.getInvertedFileExactDataProp().keySet().retainAll(if_exact_intersect);

		fin = Calendar.getInstance().getTimeInMillis();
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
		
		//For local comparison
		if (check_with_gold_standard && !outPutOnlyIndiv){
			
			for (int ide1 : mappingsDProp.keySet()){
				for (int ide2 : mappingsDProp.get(ide1)){
						
					mappings_ll.add(new MappingObjectStr(
							onto_proc1.getIRI4DPropIdentifier(ide1),
							onto_proc2.getIRI4DPropIdentifier(ide2)));
					
				}
			}
			
		}
	}

	private void computeObjPropMappings(){
		
		Set<Set<String>> if_exact_intersect;
		
		init = Calendar.getInstance().getTimeInMillis();
		//We perform intersection an we only keep in inverted file the intersected elements
		if_exact_intersect = onto_proc1.getInvertedFileExactObjectProp().keySet();
		if_exact_intersect.retainAll(onto_proc2.getInvertedFileExactObjectProp().keySet());
		onto_proc2.getInvertedFileExactObjectProp().keySet().retainAll(if_exact_intersect);

		fin = Calendar.getInstance().getTimeInMillis();
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
		
		
		//For local comparison
		if (check_with_gold_standard && !outPutOnlyIndiv){
			
			for (int ide1 : mappingsOProp.keySet()){
				for (int ide2 : mappingsOProp.get(ide1)){
						
					mappings_ll.add(new MappingObjectStr(
							onto_proc1.getIRI4OPropIdentifier(ide1),
							onto_proc2.getIRI4OPropIdentifier(ide2)));
					
				}
			}
			
		}
		
		
	}
	
	
	private void computeOveralapping(){
		init = Calendar.getInstance().getTimeInMillis();
		
		Set<Set<String>> if_weak_intersect;
		
		//We perform intersection an we only keep in inverted file the intersected elements
		if_weak_intersect = onto_proc1.getWeakInvertedFile().keySet();
		if_weak_intersect.retainAll(onto_proc2.getWeakInvertedFile().keySet());
		onto_proc2.getWeakInvertedFile().keySet().retainAll(if_weak_intersect);

		
		fin = Calendar.getInstance().getTimeInMillis();
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
		
		fin = Calendar.getInstance().getTimeInMillis();
		//System.out.println("\tTime extracting entities4modules (s): " + (float)((double)fin-(double)init)/1000.0);
		
		
		//Extract module (create new constructor)
		//Give onto IRI to module
		init = Calendar.getInstance().getTimeInMillis();
		
		//Old module extractor
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
		try {
			module1_overlapping = module_extractor1.extractAsOntology(
					entities1, 
					IRI.create(onto_loader1.getOntologyIRIStr()));
		} catch (OWLOntologyCreationException e) {
			LogOutput.print("Error when creating module ontology 1");
			//e.printStackTrace();
		}
		
		
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
		try {
			module2_overlapping = module_extractor2.extractAsOntology(
					entities2, 
					IRI.create(onto_loader2.getOntologyIRIStr()));
		} catch (OWLOntologyCreationException e) {
			LogOutput.print("Error when creating module ontology 2");
			//e.printStackTrace();
		}
		
		
		module_extractor2.clearStrutures();
		
		onto_loader2.clearAxiomSet();
		entities2.clear(); 
		
		fin = Calendar.getInstance().getTimeInMillis();
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
		
		
		OWLAlignmentFormat owl_alignment_output = new OWLAlignmentFormat("alignment.owl");
		
		
		for (int ide1 : mappings.keySet()){
			for (int ide2 : mappings.get(ide1)){
				
				owl_alignment_output.addClassMapping2Output(
						onto_proc1.getIRI4identifier(ide1),
						onto_proc2.getIRI4identifier(ide2),
						LogMap_Lite.EQ, 
						1.0
						);					
			}
		}
		
		for (int ide1 : mappingsDProp.keySet()){
			for (int ide2 : mappingsDProp.get(ide1)){
				
				owl_alignment_output.addDataPropMapping2Output(
						onto_proc1.getIRI4DPropIdentifier(ide1),
						onto_proc2.getIRI4DPropIdentifier(ide2),
						LogMap_Lite.EQ, 
						1.0
						);					
			}
		}
		
		for (int ide1 : mappingsOProp.keySet()){
			for (int ide2 : mappingsOProp.get(ide1)){
				
				owl_alignment_output.addObjPropMapping2Output(
						onto_proc1.getIRI4OPropIdentifier(ide1),
						onto_proc2.getIRI4OPropIdentifier(ide2),
						LogMap_Lite.EQ, 
						1.0
						);					
			}
		}
		
		
		for (int ide1 : mappingsIndiv.keySet()){
			for (int ide2 : mappingsIndiv.get(ide1)){
				owl_alignment_output.addInstanceMapping2Output(
						onto_proc1.getIRI4Individual(ide1),
						onto_proc2.getIRI4Individual(ide2),
						1.0
						);					
			}
		}
		
		
		mappings_owl = owl_alignment_output.getOWLOntology();
		
		
	}
	
	
	//For precission and recall 
	private Set<MappingObjectStr> mappings_gs = new HashSet<MappingObjectStr>();
	private Set<MappingObjectStr> mappings_ll = new HashSet<MappingObjectStr>();
	
	/**
	 * Load Gold Standard Mappings
	 * @throws Exception
	 */
	private void loadMappingsGS(String gs_mappings) throws Exception{
	
		ReadFile reader = new ReadFile(gs_mappings);
		
		
		String line;
		String[] elements;
		
		line=reader.readLine();
		
		while (line!=null) {
			
			//if (line.indexOf("|")<0){
			if (line.indexOf("|")<0 && line.indexOf("\t")<0){
				line=reader.readLine();
				continue;
			}
			
			//elements=line.split("\\|");
			if (line.indexOf("|")>=0)
				elements=line.split("\\|");
			else { // if (line.indexOf("\t")>=0){
				elements=line.split("\\t");
			}
			
			mappings_gs.add(new MappingObjectStr(elements[0], elements[1]));
			
				
			line=reader.readLine();
		}		
		
		reader.closeBuffer();

	}
	
	
	
	public Map<Integer, Set<Integer>> getClassMappings(){
		return mappings;
	}
	

	public Map<Integer, Set<Integer>> getDataPropMappings(){
		return mappingsDProp;
	}
	
	
	public Map<Integer, Set<Integer>> getObjectPropMappings(){
		return mappingsOProp;	
	}
	
	public Map<Integer, Set<Integer>> getInstanceMappings(){
		return mappingsIndiv;	
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
	
	private void getPrecisionAndRecallMappings() throws Exception{

		
		Set <MappingObjectStr> intersection;
		
		
		
		//ALL UMLS MAPPINGS
		intersection=new HashSet<MappingObjectStr>(mappings_gs);
		intersection.retainAll(mappings_ll);
		
		precision=((double)intersection.size())/((double)mappings_ll.size());
		recall=((double)intersection.size())/((double)mappings_gs.size());
	
		fmeasure=(2*recall*precision)/(precision+recall);
		
		
		System.out.println("WRT GS MAPPINGS");
		System.out.println("\tPrecision Mappings: " + precision);
		System.out.println("\tRecall Mapping: " + recall);
		System.out.println("\tF measure: " + (2*recall*precision)/(precision+recall));
		
		
		
		Set <MappingObjectStr> difference;
        difference=new HashSet<MappingObjectStr>(mappings_gs);
        difference.removeAll(mappings_ll);
        System.out.println("Difference in GS: " + difference.size());
        
        Set <MappingObjectStr> difference2;
        difference2=new HashSet<MappingObjectStr>(mappings_ll);
        difference2.removeAll(mappings_gs);
        System.out.println("Difference in Candidates: " + difference2.size());
        
       
      
	}
	
	
	public Set<MappingObjectStr> getLogMapLiteMappings(){
		return mappings_ll;
	}
	
	
	private int num_unsat = 0;
	
	private void impactIntegration() throws Exception {
		
		
		System.out.println(module1_overlapping.getAxiomCount());
		System.out.println(module2_overlapping.getAxiomCount());
		System.out.println(mappings_owl.getAxiomCount());
		
		
		
		SatisfiabilityIntegration sat_checker = new SatisfiabilityIntegration(
			module1_overlapping, 
			module2_overlapping,
			mappings_owl,
			false,//Time_Out_Class
			false); //factory
		
		num_unsat = sat_checker.getNumUnsatClasses();
		
		System.out.println("Num unsat classes: " + num_unsat);
	}
	
	
	public boolean hasUnsatClasses(){
		return num_unsat>0;
	}
	
	public int getNumUnsatClasses(){
		return num_unsat;
	}
	
	
	
	
	
	
	
}

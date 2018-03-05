package uk.ac.ox.krr.logmap2.partitioning;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.io.IRIDocumentSource;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.MissingImportHandlingStrategy;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyLoaderConfiguration;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import uk.ac.manchester.syntactic_locality.OntologyModuleExtractor;
import uk.ac.ox.krr.logmap2.io.LogOutput;
import uk.ac.ox.krr.logmap2.lexicon.LexicalUtilities;
import uk.ac.ox.krr.logmap2.overlapping.OntologyProcessing4Overlapping;
import uk.ac.ox.krr.logmap2.owlapi.SynchronizedOWLManager;
import uk.ac.ox.krr.logmap2.statistics.StatisticsTimeMappings;


/**
 * This class aims at implementing an efficient algorithm to produce multiple partitions for ontology alignment.
 * The methods rely on efficient lexical indexes, locality based module extraction and simple clustering algorithms. 
 * @author ernesto
 *
 */
public class BasicMultiplePartitioning extends OntologyAlignmentPartitioning{
	
	OntologyProcessing4Overlapping source_processing;
	OntologyProcessing4Overlapping target_processing;
	
	boolean use_full_overlapping = true;
	
	Set<Set<String>> if_intersection;
	
			
	Set<OWLAxiom> overlapping_source;
	Set<OWLAxiom> overlapping_target;
	
	Set<OWLEntity> entities_source = new HashSet<OWLEntity>();
	Set<OWLEntity> entities_target = new HashSet<OWLEntity>();
	
	
	OntologyModuleExtractor module_extractor_source;
	
	OntologyModuleExtractor module_extractor_target;
	
	
	double total_time=0.0;
	
	
	long size_source_ontology;
	long size_target_ontology;
	
	public double getComputationTime(){
		return total_time;
	}
	
	
	public long getSizeSourceOntology(){
		return size_source_ontology;
	}
	
	
	public long getSizeTargetOntology(){
		return size_target_ontology;
	}
	
	
	
	public void clear(){
		
		try{
			source_processing.clearStructures();
			target_processing.clearStructures();
			
			source_processing.clearClass2Identifier();
			target_processing.clearClass2Identifier();
			
			if_intersection.clear();
			
			entities_source.clear();
			entities_target.clear();
			
			
			module_extractor_source.clearStrutures();
			
			module_extractor_target.clearStrutures();
			
			overlapping_source.clear();
			overlapping_target.clear();
		}
		catch (Exception e){
			//In case of error
		}
	}
	
	
	@Override
	public List<MatchingTask> createPartitionedMatchingTasks(
			String sourceIRIStr, String targetIRIStr, int num_tasks)
			throws OWLOntologyCreationException, Exception {		
		
		return createPartitionedMatchingTasks(loadOWLOntology(sourceIRIStr), loadOWLOntology(targetIRIStr), num_tasks);
	}
	

	

	@Override
	public List<MatchingTask> createPartitionedMatchingTasks(OWLOntology source,
			OWLOntology target, int num_tasks) throws OWLOntologyCreationException, Exception {
		
		
		
		size_source_ontology = source.getSignature(true).size();
		size_target_ontology = target.getSignature(true).size();
		
		
		
		boolean compute_overlapping_stimation=true;
		if (num_tasks==1)
			compute_overlapping_stimation=false;
		
		StatisticsTimeMappings.setInitGlobalTime();
		StatisticsTimeMappings.setCurrentInitTime();
		
		List<MatchingTask> tasks = new ArrayList<MatchingTask>();
		
		
		entities_source.clear();
		entities_target.clear();
		
		
		//1. Create IF inverted Indexes: ontologyProcessing for overlapping
		
		//1.1 Necessary for stemming and stopwords
		LexicalUtilities lexicalUtilities = new LexicalUtilities();		
		
		//1.2 Source
		source_processing = createInvertedFile(source, lexicalUtilities, use_full_overlapping, 0);
		//1.3 Target
		target_processing = createInvertedFile(target, lexicalUtilities, use_full_overlapping, source_processing.getLastidentifier()+10);
		
							
		//2. Intersect inverted indexes				
		//We perform intersection an we only keep in inverted file the intersected elements		
		if_intersection = source_processing.getWeakInvertedFile().keySet();
		if_intersection.retainAll(target_processing.getWeakInvertedFile().keySet());
		target_processing.getWeakInvertedFile().keySet().retainAll(if_intersection);
		
		
		double if_file_time = StatisticsTimeMappings.getRunningTime();

		LogOutput.print("Time computing inverted file for overlapping (s): " + if_file_time);		
		LogOutput.print("Number of entries IF: " + if_intersection.size());
		
		
		//It has a good impact specially if number of tasks > 10
		//3. Create overlapping estimation and "discard original ontologies"
		//3.1. Create Entity sets in Overlapping
		if (compute_overlapping_stimation){
			StatisticsTimeMappings.setCurrentInitTime();
			for (Set<String> str_set: if_intersection){
				
				for (int ide1 : source_processing.getWeakInvertedFile().get(str_set)){
					entities_source.add(source_processing.getClass4identifier(ide1));
				}
				for (int ide2 : target_processing.getWeakInvertedFile().get(str_set)){
					entities_target.add(target_processing.getClass4identifier(ide2));
				}
			}
			
			
			//3.2 Overlapping Source
			overlapping_source = createOverlappingEstimation(source, entities_source);
		
			//3.3 Overlapping Target
			overlapping_target = createOverlappingEstimation(target, entities_target);
								
			
			
			double overlapping_time = StatisticsTimeMappings.getRunningTime();
			LogOutput.print("Time computing overlapping modules (overstimation) (s): " + overlapping_time);	
		}	
		
		
		//3. Shuffle intersection of inverted file
		//Required to perform several experiments and see if order in IF has an important impact as the split is random!
		StatisticsTimeMappings.setCurrentInitTime();
		
		
		List<Set<String>> list_if_entries = new ArrayList<Set<String>>();
		list_if_entries.addAll(if_intersection)	;
		Collections.shuffle(list_if_entries);
		
		
		double shufling_time = StatisticsTimeMappings.getRunningTime();
		LogOutput.print("Time shuffling IF (s): " + shufling_time);		
		
		
		//4. Split shuffled into X groups, being X the number of desired matching tasks
		//4.1 Extract entities for each of the groups,
		//4.2. then corresponding modules in source and target
		//4.3. and finally create task
		
		StatisticsTimeMappings.setCurrentInitTime();
		
		String uri_onto1 = source.getOntologyID().getOntologyIRI().toString();
		String uri_onto2 = target.getOntologyID().getOntologyIRI().toString();
		
		if (compute_overlapping_stimation)
			setUpModuleExtractors(overlapping_source, overlapping_target);
		else
			setUpModuleExtractors(source.getAxioms(), target.getAxioms());
		
		
		Long size_groups = Math.round((double)list_if_entries.size() / (double)num_tasks);
		
		//System.out.println(size_groups);
		
		for (int n_task = 0; n_task<num_tasks; n_task++){
		
			//To avoid empty tasks
			if (n_task*size_groups.intValue()<list_if_entries.size())
				tasks.add(createMatchingTask(uri_onto1, uri_onto2, list_if_entries, n_task, size_groups.intValue()));

		
		}
		
		double modules_time = StatisticsTimeMappings.getRunningTime();
		LogOutput.print("Time computing modules for tasks (s): " + modules_time);		

		
		
		
		//TODO Store matching tasks!
		
		
		total_time = StatisticsTimeMappings.getTotalRunningTime();
		LogOutput.print("Total time (s): " + total_time);		
		
		

		return tasks;
		
		
	}

	
	
	/**
	 * @param source
	 * @param target
	 */
	private void setUpModuleExtractors(Set<OWLAxiom> source_ax, Set<OWLAxiom> target_ax) {
		
		module_extractor_source =
				new OntologyModuleExtractor(
						SynchronizedOWLManager.createOWLOntologyManager(),
						source_ax,
						true,
						false,
						true);
		
		module_extractor_target =
				new OntologyModuleExtractor(
						SynchronizedOWLManager.createOWLOntologyManager(),
						target_ax,
						true,
						false,
						true);
		
		
		
		
	}




	/**
	 * @param source
	 * @param target
	 * @param list_if_entries
	 * @param n_task
	 * @param size_groups
	 * @return
	 * @throws OWLOntologyCreationException 
	 */
	private MatchingTask createMatchingTask(String uri_source, String uri_target, List<Set<String>> list_if_entries, int n_task, int size_groups) throws OWLOntologyCreationException {
		
		int lower_bound = n_task*size_groups;
		int upper_bound = (n_task+1)*size_groups;
		
		if (upper_bound>list_if_entries.size())
			upper_bound=list_if_entries.size();
		
		
		entities_source.clear();
		entities_target.clear();
		
		
		//Extract entities from IFs and convert id to OWLEntity		
		for (int i=lower_bound; i<upper_bound; i++){
			
			Set<String> set_words = list_if_entries.get(i);
			
			
			for (int ide1 : source_processing.getWeakInvertedFile().get(set_words)){
				entities_source.add(source_processing.getClass4identifier(ide1));
			}
			for (int ide2 : target_processing.getWeakInvertedFile().get(set_words)){
				entities_target.add(target_processing.getClass4identifier(ide2));
			}
			
		}
		
		
		return new MatchingTask(
				module_extractor_source.extractAsOntology(entities_source, IRI.create(uri_source + "-Task-" + n_task)),
				module_extractor_target.extractAsOntology(entities_target, IRI.create(uri_target + "-Task-" + n_task))
				);
		
	}
	
	
	
	
	private OWLOntology loadOWLOntology(String phy_iri_onto) throws OWLOntologyCreationException{		

		try {
			
			OWLOntologyManager managerOnto;
			managerOnto = SynchronizedOWLManager.createOWLOntologyManager();			
			//managerOnto.setSilentMissingImportsHandling(true);	
			OWLOntologyLoaderConfiguration conf = new OWLOntologyLoaderConfiguration();
			conf.setMissingImportHandlingStrategy(MissingImportHandlingStrategy.SILENT);			
			return managerOnto.loadOntologyFromOntologyDocument(
					new IRIDocumentSource(IRI.create(phy_iri_onto)), conf);
			
						
		}
		catch(Exception e){
			System.err.println("Error loading OWL ontology: " + e.getMessage());
			//e.printStackTrace();
			throw new OWLOntologyCreationException();
		}
	}
	
	
	private OntologyProcessing4Overlapping createInvertedFile( 
			OWLOntology ontology, 
			LexicalUtilities lexicalUtilities,
			boolean use_full_overlapping, 
			int init_index){
			
		OntologyProcessing4Overlapping ontology_processing = new OntologyProcessing4Overlapping(ontology, lexicalUtilities, use_full_overlapping, true, init_index);
		ontology_processing.processOntologyClassLabels();
		ontology_processing.setInvertedFile4Overlapping();
		
		return ontology_processing;
		
	}
	
	
		
	private Set<OWLAxiom> createOverlappingEstimation(OWLOntology ontology, Set<OWLEntity> entities){
		
		//Module: overlapping overestimation
		OntologyModuleExtractor module_extractor =
				new OntologyModuleExtractor(
						SynchronizedOWLManager.createOWLOntologyManager(),
						ontology.getAxioms(),
						true,
						false,
						true);
		//OWLOntology module_source = module_extractor_source.extractAsOntology(
		//		entities_source, 
		//		IRI.create(source.getOntologyID().getOntologyIRI().toString()));
		Set<OWLAxiom> overlapping = new HashSet<OWLAxiom>();
		
		overlapping.addAll(module_extractor.extract(entities));
					
		module_extractor.clearStrutures();
						
		//Remove original ontology
		//ontology.getOWLOntologyManager().removeOntology(ontology);
		//ontology=null;
		
		entities.clear();
		
		return overlapping;
		
	}
	
	
	
}

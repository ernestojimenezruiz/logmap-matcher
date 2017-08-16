package uk.ac.ox.krr.logmap2.partitioning;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import uk.ac.manchester.syntactic_locality.OntologyModuleExtractor;
import uk.ac.ox.krr.logmap2.lexicon.LexicalUtilities;
import uk.ac.ox.krr.logmap2.overlapping.OntologyProcessing4Overlapping;
import uk.ac.ox.krr.logmap2.owlapi.SynchronizedOWLManager;


/**
 * This class aims at implementing an efficient algorithm to produce multiple partitions for ontology alignment.
 * The methods arely on efficient lexical indexes, locality based module extraction and simple clustering algorithms. 
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
	
	
	List<PreMatchingTask> preMatchingTasks = new ArrayList<PreMatchingTask>();
	
	

	@Override
	public Set<MatchingTask> createPartitionedMatchingTasks(OWLOntology source,
			OWLOntology target) throws OWLOntologyCreationException, Exception {
		
		
		//1. Create IF inverted Indexes: ontologyProcessing for overlapping
		
		//1.1 Necessary for stemming and stopwords
		LexicalUtilities lexicalUtilities = new LexicalUtilities();		
		
		//1.2 Source
		createInvertedFile(source_processing, source, lexicalUtilities, use_full_overlapping);
		//1.3 Target
		createInvertedFile(target_processing, target, lexicalUtilities, use_full_overlapping);
				
						
		//2. Intersect inverted indexes				
		//We perform intersection an we only keep in inverted file the intersected elements
		if_intersection = source_processing.getWeakInvertedFile().keySet();
		if_intersection.retainAll(target_processing.getWeakInvertedFile().keySet());
		target_processing.getWeakInvertedFile().keySet().retainAll(if_intersection);

		
		//3. Create overlapping estimation and discard original ontologies
					
		//3.1. Create Entity sets in Overlapping		
		for (Set<String> str_set: if_intersection){					
			for (int ide1 : source_processing.getWeakInvertedFile().get(str_set)){
				entities_source.add(source_processing.getClass4identifier(ide1));
			}
			for (int ide2 : target_processing.getWeakInvertedFile().get(str_set)){
				entities_target.add(target_processing.getClass4identifier(ide2));
			}
		}
		
		//3.2 Overlapping Source
		createOverlappingEstimation(source, entities_source, overlapping_source);
	
		//3.3 Overlapping Target
		createOverlappingEstimation(target, entities_target, overlapping_target);
							
		
		//4. Create logical modules for each entry: we should use a combined module including entities from O1 and O2
		//We create a list of pre-matching (sub)tasks
		createModulesPreMatchingTasks();
		
		
		//TODO
		//Evaluate recall now and get some statistics about the tasks!
		//In principle recall should be as large as the overlapping overstimation
		//Keep some URI to id to compare with Gold standards!
		
		
		
		//Check if extracted modules already include entities. Index, entity to entities
		
		//4. Merge modules (max size: 1000-2000 classes)
		
		//Check how many modules are the same!
		
		
		
		//Strategies: (merging should take into account both sides)
		//a. Start merging till a fix point is reached
		//b. Get the top X largest modules which will serve as centroids. Do some clean up
		
		
		return null;
	}

	@Override
	public Set<MatchingTask> createPartitionedMatchingTasks(
			String sourceIRIStr, String targetIRIStr)
			throws OWLOntologyCreationException, Exception {		
		
		return createPartitionedMatchingTasks(loadOWLOntology(sourceIRIStr), loadOWLOntology(targetIRIStr));
	}
	
	
	
	public OWLOntology loadOWLOntology(String phy_iri_onto) throws OWLOntologyCreationException{		

		try {
			
			OWLOntologyManager managerOnto;
			managerOnto = SynchronizedOWLManager.createOWLOntologyManager();			
			managerOnto.setSilentMissingImportsHandling(true);									
			return managerOnto.loadOntology(IRI.create(phy_iri_onto));
			
						
		}
		catch(Exception e){
			System.err.println("Error loading OWL ontology: " + e.getMessage());
			//e.printStackTrace();
			throw new OWLOntologyCreationException();
		}
	}
	
	
	private void createInvertedFile(
			OntologyProcessing4Overlapping ontology_processing, OWLOntology ontology, LexicalUtilities lexicalUtilities, boolean use_full_overlapping){
			
		ontology_processing = new OntologyProcessing4Overlapping(ontology, lexicalUtilities, use_full_overlapping, true);
		ontology_processing.processOntologyClassLabels();
		ontology_processing.setInvertedFile4Overlapping();
				
	}
	
	
		
	private void createOverlappingEstimation(OWLOntology ontology, Set<OWLEntity> entities, Set<OWLAxiom> overlapping){
		
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
		overlapping = module_extractor.extract(entities);
					
		module_extractor.clearStrutures();
						
		//Remove original ontology
		ontology.getOWLOntologyManager().removeOntology(ontology);
		ontology=null;
		
		entities.clear();
		
	}
	
	
	
	
	private void createModulesPreMatchingTasks(){
		
		OntologyModuleExtractor module_extractor_source =
				new OntologyModuleExtractor(
						SynchronizedOWLManager.createOWLOntologyManager(),
						overlapping_source,
						true,
						false,
						true);
		
		OntologyModuleExtractor module_extractor_target =
				new OntologyModuleExtractor(
						SynchronizedOWLManager.createOWLOntologyManager(),
						overlapping_target,
						true,
						false,
						true);
		
		for (Set<String> str_set: if_intersection){
			entities_source.clear();
			entities_target.clear();			
			
			//MODULES for entry: create PreMatchingTask
			PreMatchingTask pmt = new PreMatchingTask();
			
			for (int ide1 : source_processing.getWeakInvertedFile().get(str_set)){				
				entities_source.add(source_processing.getClass4identifier(ide1));
			}
			
			//Source module
			module_extractor_source.extract(entities_source);
			
			//Source identifiers for task
			createTaskClassIdentifiers(source_processing, module_extractor_source.getModuleEntities(), pmt.getIdsSource());
			
			
			for (int ide2 : target_processing.getWeakInvertedFile().get(str_set)){
				entities_target.add(target_processing.getClass4identifier(ide2));
			}			
			
			//Target module
			module_extractor_target.extract(entities_target);
				
			//Target identifiers for task
			createTaskClassIdentifiers(target_processing, module_extractor_target.getModuleEntities(), pmt.getIdsTarget());				
			
			
			preMatchingTasks.add(pmt);
			
		}
		
		//clear structures modules
		module_extractor_source.clearStrutures();
		module_extractor_target.clearStrutures();
		
		//clear IF files
		source_processing.getWeakInvertedFile().clear();
		target_processing.getWeakInvertedFile().clear();
		if_intersection.clear();
		
		//additional indexes not necessary
		source_processing.clearClass2Identifier();
		target_processing.clearClass2Identifier();
		
		
		
		
	}
	
	private void createTaskClassIdentifiers(OntologyProcessing4Overlapping ontology_processing, Set<OWLEntity> entities, Set<Integer> identifiers){
		
		int ident;
		
		for (OWLEntity ent: entities){
			
			if (ent.isOWLClass()){ //only for classes
				
				ident = ontology_processing.getIdentifier4Class(ent.asOWLClass());
				if (ident>=0)
					identifiers.add(ident);
			}
		}
		
	}
	
	
	
	
	/**
	 * 
	 * This class stores pre-matching (sub)tasks in thq form or set of class identifiers in the source and target ontologies. 
	 * @author ernesto
	 *
	 */
	private class PreMatchingTask{
		
		private Set<Integer> ids_source = new HashSet<Integer>();
		private Set<Integer> ids_target = new HashSet<Integer>();
		
		public Set<Integer> getIdsSource() {
			return ids_source;
		}
		public void setIdsSource(Set<Integer> ids_source) {
			this.ids_source = ids_source;
		}
		public Set<Integer> getIdsTarget() {
			return ids_target;
		}
		public void setIdsTarget(Set<Integer> ids_target) {
			this.ids_target = ids_target;
		}
		
		
			
		
		
		
	}
	
	
	
	

}

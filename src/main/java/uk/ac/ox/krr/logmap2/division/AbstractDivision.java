package uk.ac.ox.krr.logmap2.division;

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
import uk.ac.ox.krr.logmap2.lexicon.LexicalUtilities;
import uk.ac.ox.krr.logmap2.overlapping.OntologyProcessing4Overlapping;
import uk.ac.ox.krr.logmap2.owlapi.SynchronizedOWLManager;

/**
 *
 * @author ernesto
 * Created on 6 Mar 2018
 *
 */
public abstract class AbstractDivision {
	
	protected OntologyProcessing4Overlapping source_processing;
	protected OntologyProcessing4Overlapping target_processing;
	
	protected boolean use_full_overlapping = true;
	
	protected Set<Set<String>> if_intersection;
	
			
	protected Set<OWLAxiom> overlapping_source;
	protected Set<OWLAxiom> overlapping_target;
	
	protected Set<OWLEntity> entities_source = new HashSet<OWLEntity>();
	protected Set<OWLEntity> entities_target = new HashSet<OWLEntity>();
	
	
	protected OntologyModuleExtractor module_extractor_source;
	
	protected OntologyModuleExtractor module_extractor_target;
	
	
	protected double total_time=0.0;
	
	
	protected long size_source_ontology;
	protected long size_target_ontology;
	
	
	
	protected OWLOntology loadOWLOntology(String phy_iri_onto) throws OWLOntologyCreationException{		

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
	
	
	
	/**
	 * @param source
	 * @param target
	 */
	protected void setUpModuleExtractors(Set<OWLAxiom> source_ax, Set<OWLAxiom> target_ax) {
		
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
		//Optimization very important!
		
		
		
	}




	/**
	 * Method from naive algorithm
	 * @param source
	 * @param target
	 * @param list_if_entries
	 * @param n_task
	 * @param size_groups
	 * @return
	 * @throws OWLOntologyCreationException 
	 */
	protected MatchingTask createMatchingTask(String uri_source, String uri_target, List<Set<String>> list_if_entries, int n_task, int size_groups) throws OWLOntologyCreationException {
		
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
	
	
	
	/**
	 * Method from advanced algorithm (word embeddings)
	 * @param source
	 * @param target
	 * @param list_if_entries
	 * @param n_task
	 * @return
	 * @throws OWLOntologyCreationException 
	 */
	protected MatchingTask createMatchingTask(String uri_source, String uri_target, Set<Set<String>> set_if_entries, int n_task, int max_ambiguity, boolean change_ontology_uri) throws OWLOntologyCreationException {
		
		entities_source.clear();
		entities_target.clear();
		
		
		//Extract entities from IFs and convert id to OWLEntity		
		for (Set<String> set_words : set_if_entries){
			
			//Safety check
			if (!source_processing.getWeakInvertedFile().containsKey(set_words) ||
				!target_processing.getWeakInvertedFile().containsKey(set_words))
				continue;
			
			int num_ids1 = source_processing.getWeakInvertedFile().get(set_words).size();
			int num_ids2 = target_processing.getWeakInvertedFile().get(set_words).size();
			
			//To avoid highly ambiguos entries in IF 
			if ((num_ids1 + num_ids2) > max_ambiguity)
				continue;
			
			
			try{
				for (int ide1 : source_processing.getWeakInvertedFile().get(set_words)){
					entities_source.add(source_processing.getClass4identifier(ide1));
				}
				for (int ide2 : target_processing.getWeakInvertedFile().get(set_words)){
					entities_target.add(target_processing.getClass4identifier(ide2));
				}
			}
			catch (Exception e){
				//System.out.println(set_words);
				//for (String s : set_words)
				//	System.out.println(s);
				//e.printStackTrace();
				continue;
			}
			
		}
		
		if (change_ontology_uri) {
		
			return new MatchingTask(
					module_extractor_source.extractAsOntology(entities_source, IRI.create(uri_source + "-Task-" + n_task)),
					module_extractor_target.extractAsOntology(entities_target, IRI.create(uri_target + "-Task-" + n_task))
					//Pomap case
					//module_extractor_source.extractAsOntology(entities_source, IRI.create("http://source.owl")),
					//module_extractor_target.extractAsOntology(entities_target, IRI.create("http://target.owl"))
					);
		}
		else {
			//Alod2vec does not like the change of uri. ALthough it seem to be a bug in the system
			return new MatchingTask(
					module_extractor_source.extractAsOntology(entities_source, IRI.create(uri_source)),
					module_extractor_target.extractAsOntology(entities_target, IRI.create(uri_target))
					);
		}
	}
	
	
	
	/**
	 * Method from advanced algorithm (concept embeddings)
	 * @param source
	 * @param target
	 * @param list_if_entries
	 * @param n_task
	 * @return
	 * @throws OWLOntologyCreationException 
	 */
	protected MatchingTask createMatchingTask(String uri_source, String uri_target, int n_task, Set<Integer> concept_ids) throws OWLOntologyCreationException {
		
		entities_source.clear();
		entities_target.clear();
		
		
		//System.out.println("Last identifier s: " + source_processing.getLastidentifier());
		//System.out.println("Last identifier t: " + target_processing.getLastidentifier());
		
		//Extract entities from IFs and convert id to OWLEntity		
		for (int concept_id : concept_ids){
			
			try{
				//Belongs to ontology 1
				if (concept_id <= source_processing.getLastidentifier()){
					entities_source.add(source_processing.getClass4identifier(concept_id));
				}
				else {
					
					entities_target.add(target_processing.getClass4identifier(concept_id));
				}
			}
			catch (Exception e){
				//System.out.println(set_words);
				e.printStackTrace();
			}
			
		}
		
		
		//System.out.println("Entities source: " + entities_source.size());
		//System.out.println("Entities target: " + entities_target.size());
		
		
		return new MatchingTask(
				module_extractor_source.extractAsOntology(entities_source, IRI.create(uri_source + "-Task-" + n_task)),
				module_extractor_target.extractAsOntology(entities_target, IRI.create(uri_target + "-Task-" + n_task))
				);
		
	}
	
	
	
	
	
	
	
	protected OntologyProcessing4Overlapping createInvertedFile( 
			OWLOntology ontology, 
			LexicalUtilities lexicalUtilities,
			boolean use_full_overlapping, 
			int init_index){
			
		OntologyProcessing4Overlapping ontology_processing = new OntologyProcessing4Overlapping(ontology, lexicalUtilities, use_full_overlapping, true, init_index);
		ontology_processing.processOntologyClassLabels();
		ontology_processing.setInvertedFile4Overlapping();
		
		return ontology_processing;
		
	}
	
	
		
	protected Set<OWLAxiom> createOverlappingEstimation(OWLOntology ontology, Set<OWLEntity> entities){
		
		//Module: overlapping overestimation
		OntologyModuleExtractor module_extractor =
				new OntologyModuleExtractor(
						SynchronizedOWLManager.createOWLOntologyManager(),
						ontology.getAxioms(),
						true,
						false,
						true);
		
		Set<OWLAxiom> overlapping = new HashSet<OWLAxiom>();
		
		overlapping.addAll(module_extractor.extract(entities));
		
		module_extractor.clearStrutures();
						
		//Remove original ontology
		//ontology.getOWLOntologyManager().removeOntology(ontology);
		//ontology=null;
		
		entities.clear();
		
		return overlapping;
		
	}
	
	protected OWLOntology createOverlappingEstimationAsOntology(OWLOntology ontology, Set<OWLEntity> entities, String uri) throws OWLOntologyCreationException{
		
		//Module: overlapping overestimation
		OntologyModuleExtractor module_extractor =
				new OntologyModuleExtractor(
						SynchronizedOWLManager.createOWLOntologyManager(),
						ontology.getAxioms(),
						true,
						false,
						true);
		
		OWLOntology overlapping = module_extractor.extractAsOntology(entities, IRI.create(uri));
		
		module_extractor.clearStrutures();
						
		//Remove original ontology
		//ontology.getOWLOntologyManager().removeOntology(ontology);
		//ontology=null;
		
		entities.clear();
		
		return overlapping;
		
	}
	
	
	
}

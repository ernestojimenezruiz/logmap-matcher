package uk.ac.ox.krr.logmap2.partitioning;

import java.util.ArrayList;
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
import uk.ac.ox.krr.logmap2.io.WriteFile;
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
	
	
	List<PreMatchingTask> preMatchingTasks = new ArrayList<PreMatchingTask>();
	
	

	@Override
	public Set<MatchingTask> createPartitionedMatchingTasks(OWLOntology source,
			OWLOntology target) throws OWLOntologyCreationException, Exception {
		
		
		StatisticsTimeMappings.setInitGlobalTime();
		StatisticsTimeMappings.setCurrentInitTime();
		
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

		LogOutput.printAlways("Time computing inverted file for overlapping (s): " + StatisticsTimeMappings.getRunningTime());		
		System.out.println("Number of entries IF: " + if_intersection.size());
		
		
		StatisticsTimeMappings.setCurrentInitTime();
		
		
		//3. Create overlapping estimation and discard original ontologies
		
		
		String label1;
		String label2;
		
		label1 = "mouse";
		label2 = "ncia";
		
		
		label1 = "fma";
		label2 = "nci";
		
		
		label1 = "fma";
		label2 = "snomed";
		
		
		label1 = "snomed";
		label2 = "nci";
		
		
		WriteFile writerO1 =  new WriteFile("/home/ernesto/Documents/OAEI_2017.5/overlapping/if_" + label1);
		WriteFile writerO2 =  new WriteFile("/home/ernesto/Documents/OAEI_2017.5/overlapping/if_" + label2);
		WriteFile writerComb =  new WriteFile("/home/ernesto/Documents/OAEI_2017.5/overlapping/if_" + label1 + "_"+label2);
		
		String lineO1;
		String lineO2;
		String lineComb;

		
		//3.1. Create Entity sets in Overlapping		
		for (Set<String> str_set: if_intersection){

			lineO1="";
			lineO2="";
			lineComb="";
					
			
			for (String str : str_set){
				lineO1+= str+";";
			}
			
			//lineO1 = lineO1.substring(0, lineO1.length()-1);
			lineO1=lineComb=lineO2=lineO1.substring(0, lineO1.length()-1) + "|";
			
			
			for (int ide1 : source_processing.getWeakInvertedFile().get(str_set)){
				
				lineO1+=ide1 + ";";
				lineComb+=ide1 + ";";
				
				entities_source.add(source_processing.getClass4identifier(ide1));
			}
			for (int ide2 : target_processing.getWeakInvertedFile().get(str_set)){
				
				lineO2+=ide2 + ";";
				lineComb+=ide2 + ";";
				
				entities_target.add(target_processing.getClass4identifier(ide2));
			}
			
			
			lineO1=lineO1.substring(0, lineO1.length()-1);
			lineO2=lineO2.substring(0, lineO2.length()-1);
			lineComb=lineComb.substring(0, lineComb.length()-1);
			
			
			writerO1.writeLine(lineO1);
			writerO2.writeLine(lineO2);
			writerComb.writeLine(lineComb);
			
		}
		
		writerO1.closeBuffer();
		writerO2.closeBuffer();
		writerComb.closeBuffer();
		
		
		//3.2 Overlapping Source
		overlapping_source = createOverlappingEstimation(source, entities_source);
	
		//3.3 Overlapping Target
		overlapping_target = createOverlappingEstimation(target, entities_target);
							
		
		LogOutput.printAlways("Time computing overlapping modules (overstimation) (s): " + StatisticsTimeMappings.getRunningTime());	
		StatisticsTimeMappings.setCurrentInitTime();
		
		//4. Create logical modules for each entry: we should use a combined module including entities from O1 and O2
		//We create a list of pre-matching (sub)tasks
		//createModulesPreMatchingTasks();
		
		
		LogOutput.printAlways("Time computing small modules (1 per IF entry) (s): " + StatisticsTimeMappings.getRunningTime());
		
		
		
		//TODO
		
		//Problem: complexity of extracting many modules....
		//Instead of extracting modules: use labelling index to check similarity among IF values:
		//For example e1 not= e2 but e1 sub e2
		
		
		
		//Clustering at the inverted index level: keys pointing to similar values (taking into account o1 and o2)
		//How to define this similarity value?
		//Modules would definitiely complement this, but it may be very expensive.
		
		
		
		//Evaluate recall now and get some statistics about the tasks!
		//In principle recall should be as large as the overlapping overstimation
		//Keep some URI to id to compare with Gold standards!

		
		//Check if extracted modules already include entities. Index, entity to entities
		//I guess to group entries
		
		
		
		//4. Merge modules (max size: 1000-2000 classes)
		//USe a TreeSet and each entry module characterised by a boolean vector (occurrence or not of a class)
		//Check how many modules/tasks are the same: what happens in this case, are the tasks merged? Since it is a set 
		//I guess so as long the the hashCode and equal methods are properly defined
		
		
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
		ontology.getOWLOntologyManager().removeOntology(ontology);
		ontology=null;
		
		entities.clear();
		
		return overlapping;
		
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
	 * This class stores pre-matching (sub)tasks in the form or set of class identifiers in the source and target ontologies. 
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

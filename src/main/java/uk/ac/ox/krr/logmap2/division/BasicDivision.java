package uk.ac.ox.krr.logmap2.division;

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
import org.semanticweb.owlapi.model.parameters.Imports;

import uk.ac.manchester.syntactic_locality.OntologyModuleExtractor;
import uk.ac.ox.krr.logmap2.Parameters;
import uk.ac.ox.krr.logmap2.io.LogOutput;
import uk.ac.ox.krr.logmap2.lexicon.LexicalUtilities;
import uk.ac.ox.krr.logmap2.overlapping.OntologyProcessing4Overlapping;
import uk.ac.ox.krr.logmap2.owlapi.SynchronizedOWLManager;
import uk.ac.ox.krr.logmap2.statistics.StatisticsTimeMappings;


/**
 * This class aims at implementing an efficient algorithm to produce multiple 
 * partitions for ontology alignment. 
 * The methods rely on efficient lexical indexes and locality based module extraction. 
 * Number of partitions or matching tasks is required as input.
 * @author ernesto
 *
 */
public class BasicDivision extends AbstractDivision implements OntologyAlignmentDivision {
	
	
	int num_tasks;
	int num_tasks_ouput;
	
	/**
	 * 
	 * @param num_tasks The number of required matching tasks
	 */
	public BasicDivision(int num_tasks){
		this.num_tasks=num_tasks;
		this.num_tasks_ouput=num_tasks;
	}
	
	
	/**
	 * For tests o prediction we are interested in only one task as ouput 
	 * @param tasks_ouput
	 */
	public void setNumTask2Outout(int tasks_ouput){
		this.num_tasks_ouput=tasks_ouput;
	}
	
	
	@Override
	public List<MatchingTask> createPartitionedMatchingTasks(
			String sourceIRIStr, String targetIRIStr)
			throws OWLOntologyCreationException, Exception {		
		
		return createPartitionedMatchingTasks(loadOWLOntology(sourceIRIStr), loadOWLOntology(targetIRIStr));
	}
	

	

	@Override
	public List<MatchingTask> createPartitionedMatchingTasks(OWLOntology source,
			OWLOntology target) throws OWLOntologyCreationException, Exception {
		
		
		
		size_source_ontology = source.getSignature(Imports.INCLUDED).size();
		size_target_ontology = target.getSignature(Imports.INCLUDED).size();
		
		
		
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
		lexicalUtilities.loadStopWords();
		//lexicalUtilities.loadStopWordsExtended();
		//if (Parameters.use_umls_lexicon)
		//	lexicalUtilities.loadUMLSLexiconResources();		
		lexicalUtilities.setStemmer(); //creates stemmer object (Paice by default)
		
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
		
		
		//4. Shuffle intersection of inverted file
		//Required to perform several experiments and see if order in IF has an important impact as the split is random!
		StatisticsTimeMappings.setCurrentInitTime();
		
		
		List<Set<String>> list_if_entries = new ArrayList<Set<String>>();
		list_if_entries.addAll(if_intersection)	;
		Collections.shuffle(list_if_entries);
		
		
		double shufling_time = StatisticsTimeMappings.getRunningTime();
		LogOutput.print("Time shuffling IF (s): " + shufling_time);		
		
		
		//5. Split shuffled into X groups, being X the number of desired matching tasks
		//5.1 Extract entities for each of the groups,
		//5.2. then corresponding modules in source and target
		//5.3. and finally create task
		
		StatisticsTimeMappings.setCurrentInitTime();
		
		
		//SOme ontologies does not include an (explicit) URI....
		String uri_onto1;		
		try	{
			uri_onto1 = "http://logmap-partitioning/ontology1";
			if (source.getOntologyID().getOntologyIRI().isPresent())
				uri_onto1 = source.getOntologyID().getOntologyIRI().get().toString();
		}
		catch (Exception e){
			uri_onto1 = "http://logmap-partitioning/ontology1";
		}
		String uri_onto2;
		
		try{
			uri_onto2 = "http://logmap-partitioning/ontology2";
			if (target.getOntologyID().getOntologyIRI().isPresent())
				uri_onto2 = target.getOntologyID().getOntologyIRI().get().toString();
		}
		catch (Exception e){
			uri_onto2 = "http://logmap-partitioning/ontology2";
		}
		
		if (compute_overlapping_stimation)
			setUpModuleExtractors(overlapping_source, overlapping_target);
		else
			setUpModuleExtractors(source.getAxioms(), target.getAxioms());
		
		
		double modules_setUp__time = StatisticsTimeMappings.getRunningTime();
		LogOutput.print("Time setting up module extractors (s): " + modules_setUp__time);		

		
		
		
		StatisticsTimeMappings.setCurrentInitTime();
		
		Long size_groups = Math.round((double)list_if_entries.size() / (double)num_tasks);
		
		//System.out.println(size_groups);
		
		for (int n_task = 0; n_task<num_tasks_ouput; n_task++){
		
			long init_task = StatisticsTimeMappings.getCurrentTimeInMillis();
			
			
			
			//To avoid empty tasks
			if (n_task*size_groups.intValue()<list_if_entries.size())
				tasks.add(createMatchingTask(uri_onto1, uri_onto2, list_if_entries, n_task, size_groups.intValue()));

		
			
			double modules_time_task = StatisticsTimeMappings.getRunningTime(init_task);
			LogOutput.print("Time computing modules for task "+ n_task + "  (s): " + modules_time_task);	
			
		}
		
		double modules_time = StatisticsTimeMappings.getRunningTime();
		LogOutput.print("Time computing modules for tasks (s): " + modules_time);		

		
		
		
		
		total_time = StatisticsTimeMappings.getTotalRunningTime();
		LogOutput.print("Total time (s): " + total_time);		
		
		

		return tasks;
		
		
	}

	
	

	
	
	
}

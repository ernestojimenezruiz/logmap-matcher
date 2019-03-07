/*******************************************************************************
 * Copyright 2017 by the Department of Informatics (University of Oslo)
 * 
 *    This file is part of the Ontology Services Toolkit 
 *
 *******************************************************************************/
package uk.ac.ox.krr.logmap2.test.overlapping;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.io.IRIDocumentSource;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.MissingImportHandlingStrategy;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyLoaderConfiguration;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import uk.ac.ox.krr.logmap2.LogMap2_Matcher;
import uk.ac.ox.krr.logmap2.Parameters;
import uk.ac.ox.krr.logmap2.division.WordEmbeddingDivision;
import uk.ac.ox.krr.logmap2.division.BasicDivision;
import uk.ac.ox.krr.logmap2.division.MatchingTask;
import uk.ac.ox.krr.logmap2.division.QualityMeasures;
import uk.ac.ox.krr.logmap2.io.LogOutput;
import uk.ac.ox.krr.logmap2.io.WriteFile;
import uk.ac.ox.krr.logmap2.mappings.objects.MappingObjectStr;
import uk.ac.ox.krr.logmap2.oaei.reader.RDFAlignReader;
import uk.ac.ox.krr.logmap2.owlapi.SynchronizedOWLManager;
import uk.ac.ox.krr.logmap2.statistics.StatisticsTimeMappings;
import uk.ac.ox.krr.logmap2.utilities.Utilities;

/**
 *
 * @author ernesto
 * Created on 26 Feb 2018
 *
 */
public class TestWordEmbeddingDivisionAlignmentTask extends AbstractTestDivisionAlignmentTask {


	

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		
			
		int ontopair = 0;
				
		Parameters.readParameters();
		
		Parameters.print_output = false;
		Parameters.print_output_always = true;
		
		LogOutput.showOutpuLog(Parameters.print_output);
		LogOutput.showOutpuLogAlways(Parameters.print_output_always);
		
		Parameters.min_size_overlapping=0;

		
		ontopair=Utilities.MOUSE2HUMAN;
		//ontopair=Utilities.FMA2NCI;		
		//ontopair=Utilities.FMA2SNOMED;
		ontopair=Utilities.SNOMED2NCI;
		
		//ontopair=HP2MP2016;
		//ontopair=DOID2ORDO2016;
		//ontopair=HP2MP2017;
		//ontopair=DOID2ORDO2017;
			
		
		
		
		//SET MATCHING CASE
		setMatchingCase(ontopair);
		//----------------------------
		
	
		
		
		

		try {
			//overlapping.createPartitionedMatchingTasks(uri1, uri2);
			
		
			
			
			String output_path;
			String path_sizes;
			String path_file_clusters;
			
			
			//------------------------------------
			//Three variants:
			//1. Only aggregated vector word embeddings for the IF entries
			//2. Cncatenated vector for IF entries:
			//aggregated vector word embeddings + aggregated vector concept embeddings
			//3. Only aggregated vector concept embeddings for the IF entries
			int type_embedding;
			int WORD_EMBEDDING=0;
			int WORD_CONCEPT_EMBEDDING=1;
			int CONCEPT_EMBEDDING=2; //We still cluster word entries but according to concept embeddings only
			
			//type_embedding=WORD_EMBEDDING;
			type_embedding=WORD_CONCEPT_EMBEDDING;
			//type_embedding=CONCEPT_EMBEDDING;
			
			
			if (type_embedding==WORD_EMBEDDING) {
				output_path = base_output_path + "tasks/embeddings_words/";
				path_sizes = base_output_path + "task_sizes/embeddings_words/";
				path_file_clusters = "/home/ejimenez-ruiz/Documents/ATI_AIDA/DivisionMatchingTask/experiments-ijcai/clusters-words/";
			}
			else if (type_embedding==WORD_CONCEPT_EMBEDDING) {
				output_path = base_output_path + "tasks/embeddings_if/";
				//output_path = base_output_path + "tasks_alod2vec/embeddings_if/";   //for alod2vec and sanom
				//output_path = base_output_path + "tasks_pomap/embeddings_if/";   //special case for pomap for which anatomy uris must be different
				path_sizes = base_output_path + "task_sizes/embeddings_if/";
				path_file_clusters = "/home/ejimenez-ruiz/Documents/ATI_AIDA/DivisionMatchingTask/experiments-ijcai/clusters-if/";
			}
			else {//if (type_embedding==WORD_CONCEPT_EMBEDDING) {
				output_path = base_output_path + "tasks/embeddings_if_concepts/";
				path_sizes = base_output_path + "task_sizes/embeddings_if_concepts/";
				path_file_clusters = "/home/ejimenez-ruiz/Documents/ATI_AIDA/DivisionMatchingTask/experiments-ijcai/clusters-if-concepts/";
			}
			//output_path = base_output_path + "tasks/embeddings_iswc/";
			//path_sizes = base_output_path + "task_sizes/embeddings_iswc/";
			//path_file_clusters = "/home/ejimenez-ruiz/Documents/ATI_AIDA/DivisionMatchingTask/experiments-ijcai/clusters-iswc/";
			
			
			
			//number of tasks
			//int[] num_tasks={2,5,10,20, 50, 75,100, 125, 150, 175,200};
			
			
			//int[] num_tasks={1, 2,5,10, 20,50,100,200};
			//int[] num_tasks={2,5, 10, 20};
			int[] num_tasks={200};
			//int[] num_tasks={20,50,100};
			//int repetitions = 5;
			int repetitions = 1;
			
			
			//TODO Important parameter to filter entries in IF pointing to a large number of concepts!!
			int max_ambiguity = 60;  //Anatomy=60, fma-nci=60
			
			
			
			//Loads ontologies and get sizes
			loadOntologies(ontopair);//uri1 and uri2);
			
			
			//Change from http://uri1 to http://ur1_task_10
			//Alod2vec seems to have a problem when concepts have a different namespace from the ontology URI
			boolean change_original_uris = true; //false for alod2vec and sanom  //true for pomap
			
			boolean store_tasks=true;
			boolean run_system=false;
			boolean store_size_files=true;
			
			new File(output_path + folder).mkdir();
			new File(path_sizes + folder).mkdir();
			
			//Header				
			System.out.println(QualityMeasures.toStringHeader());
			
			
			for (int j=0; j<num_tasks.length; j++){
			
				//String file_clusters = "/home/ernesto/Documents/OAEI_2017.5/overlapping/clusters_advanced/"+ folder + "cluster-" +  num_tasks[j];
				String file_clusters = path_file_clusters + folder + "cluster-" +  num_tasks[j];
				
				new File(output_path + folder + num_tasks[j] + "/").mkdir();
				
				
				
				
				//Repetitions
				for (int i=0; i<repetitions; i++){
					
					Parameters.use_overlapping=true;
					Parameters.min_size_overlapping=0;
					
					//TODO
					WordEmbeddingDivision segmenter = new WordEmbeddingDivision(file_clusters, num_tasks[j], max_ambiguity, change_original_uris); 
					
					List<MatchingTask> tasks = segmenter.createPartitionedMatchingTasks(onto1, onto2);
					
					//if (true)
					//	return;
					
					
					//Load ground truth and consensus
					Set<MappingObjectStr> alignment_gt = loadMappingsRDF(file_gs_rdf);
					Set<MappingObjectStr> alignment_consensus = loadMappingsRDF(file_consensus_rdf);
					
					
					QualityMeasures quality = new QualityMeasures(
							tasks, alignment_gt, alignment_consensus, segmenter.getComputationTime(), size_onto1, size_onto2); 
					
					
					
					
					System.out.println(quality.toString());
					
					
					StatisticsTimeMappings.setCurrentInitTime();
					
					
					//Parameters.min_size_overlapping=0;
					Parameters.use_overlapping=false;
					
					WriteFile writer=null;
					if (store_size_files)
						writer = new WriteFile(path_sizes + folder + num_tasks[j] + ".txt");
					
					//for (MatchingTask mtask : tasks){
					for (int id_task = 0; id_task<tasks.size(); id_task++){
						//mtask.saveMatchingTask(irirootpath);
						//mtask.clear();
						
						if (store_tasks){
							new File(output_path + folder + num_tasks[j] + "/" + id_task + "/").mkdir();
							//System.out.println(output_path + folder + num_tasks[j] + "/" + id_task + "/");
							tasks.get(id_task).saveMatchingTask(output_path + folder + num_tasks[j] + "/" + id_task + "/");
						}
						
						//Call systems
						if (run_system){
							LogMap2_Matcher logmap = new LogMap2_Matcher(tasks.get(id_task).getSourceOntology(), tasks.get(id_task).getTargetOntology());
						}
						
						
						//Store files with sizes of the mathcing tasks
						if (store_size_files)
							writer.writeLine(tasks.get(id_task).getSignatureSourceOntology().size()+ "\t" + tasks.get(id_task).getSignatureTargetOntology().size());
						
						
						tasks.get(id_task).clear();
					}
					
					if (store_size_files)
						writer.closeBuffer();
					
					
					double system_time = StatisticsTimeMappings.getRunningTime();
					//System.out.println("System or storage or cleaning time for "+ num_tasks[j] + " partitions: " + system_time);
					
					
					tasks.clear();
					alignment_gt.clear();
					alignment_consensus.clear();
					quality.clear();
					
					segmenter.clear();
					
				}
			}
			
			
			
		} catch (OWLOntologyCreationException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	
	}

}

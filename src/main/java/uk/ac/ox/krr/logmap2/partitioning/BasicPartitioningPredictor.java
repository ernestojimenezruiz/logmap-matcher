/*******************************************************************************
 * Copyright 2017 by the Department of Informatics (University of Oslo)
 * 
 *    This file is part of the Ontology Services Toolkit 
 *
 *******************************************************************************/
package uk.ac.ox.krr.logmap2.partitioning;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import uk.ac.ox.krr.logmap2.io.LogOutput;
import uk.ac.ox.krr.logmap2.lexicon.LexicalUtilities;
import uk.ac.ox.krr.logmap2.statistics.StatisticsTimeMappings;

/**
 *
 * This class aims at predicting the number of partitions (i.e. mathcing tasks) given a desired module size.
 * It may be the case the desired module size is not possible or very expensive to get 
 * (large number or partitions) 
 *
 * @author ernesto
 * Created on 6 Mar 2018
 *
 */
public class BasicPartitioningPredictor extends AbstractBasicPartitioning implements OntologyAlignmentPartitioning{
	
	
	int required_module_size;
	
	int max_number_tasks=500;
	
	int predicted_number_tasks;
	
	
	public  BasicPartitioningPredictor(int required_module_size){
		this.required_module_size=required_module_size;
	}
	
	
	/**
	 * Default is 1000
	 * @param max_number_tasks
	 */
	public void setMaxNumbertasks(int max_number_tasks){
		this.max_number_tasks=max_number_tasks;
	}
	

	@Override
	public List<MatchingTask> createPartitionedMatchingTasks(OWLOntology source, OWLOntology target)
			throws OWLOntologyCreationException, Exception {
		
		
		List<MatchingTask> tasks;
		
		createTaskSizesList();
		
		
		
		for (int n_tasks : number_tasks_list){
			BasicMultiplePartitioning partitioning = new BasicMultiplePartitioning(n_tasks);
			
			//We only require one
			partitioning.setNumTask2Outout(1);
			
			tasks = partitioning.createPartitionedMatchingTasks(source, target);
			
			if (tasks.get(0).getSignatureSourceOntology().size()<=required_module_size && tasks.get(0).getSignatureTargetOntology().size()<=required_module_size){
				
				predicted_number_tasks = n_tasks;
				
				System.out.println("\tPREDICTED NUMBER TASKS: " + predicted_number_tasks);
				
				break;
			}
			
			
		}
		
		
		//Return partitioning for prediction
		BasicMultiplePartitioning partitioning = new BasicMultiplePartitioning(predicted_number_tasks);
			
		return partitioning.createPartitionedMatchingTasks(source, target);
	}

	
	
	List<Integer> number_tasks_list=new ArrayList<Integer>();
	
	/**
	 * @return
	 */
	private void createTaskSizesList() {
		number_tasks_list.add(1);
		number_tasks_list.add(2);
		number_tasks_list.add(5);
		number_tasks_list.add(10);
		number_tasks_list.add(20);
		number_tasks_list.add(30);
		int size = 50;		
		int i=1;
		
		while ((i*size)<max_number_tasks){
			number_tasks_list.add(i*size);
			i++;
		}
		
		
		
	}
		
	
	

	@Override
	public List<MatchingTask> createPartitionedMatchingTasks(String sourceIRIStr, String targetIRIStr)
			throws OWLOntologyCreationException, Exception {
		return createPartitionedMatchingTasks(loadOWLOntology(sourceIRIStr), loadOWLOntology(targetIRIStr));
	}
	
	
	
	

}

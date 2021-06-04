package uk.ac.ox.krr.logmap2.division;

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
 * This class aims at predicting the number of partitions (i.e. matching tasks) given a desired module size.
 * It may be the case the desired module size is not possible or very expensive to get 
 * (large number or partitions) 
 *
 * @author ernesto
 * Created on 6 Mar 2018
 *
 */
public class BasicDivisionPredictor extends AbstractDivision implements OntologyAlignmentDivision{
	
	
	int required_module_size;
	
	int max_number_tasks=500;
	
	int predicted_number_tasks=-1;
	
	boolean strict_requirement=false;
	
	
	/**
	 * Predicts number of matching tasks to (approximately) obtain modules of the given size. 
	 * If "strict_requirement"=true then both source and target ontology modules must be smaller than required_module_size
	 * If "strict_requirement"=false then one of the  module (source or target) is required to be smaller than required_module_size
	 * @param required_module_size
	 * @param strict_requirement
	 */
	public BasicDivisionPredictor(int required_module_size, boolean strict_requirement){
		this.required_module_size=required_module_size;
		this.strict_requirement = strict_requirement;
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
		
		StatisticsTimeMappings.setInitGlobalTime();
		
		
		List<MatchingTask> tasks=null;
		
		predicted_number_tasks=-1;
		
		createTaskSizesList();
		
		for (int n_tasks : number_tasks_list){
			BasicDivision partitioning = new BasicDivision(n_tasks);
			
			//We only require one
			partitioning.setNumTask2Outout(1);
			
			tasks = partitioning.createPartitionedMatchingTasks(source, target);
			
			if ((tasks.get(0).getSignatureSourceOntology().size()<=required_module_size && tasks.get(0).getSignatureTargetOntology().size()<=required_module_size) ||
				(!strict_requirement && (tasks.get(0).getSignatureSourceOntology().size()<=required_module_size || tasks.get(0).getSignatureTargetOntology().size()<=required_module_size))){
				
				predicted_number_tasks = n_tasks;
				
				LogOutput.print("\tPREDICTED NUMBER TASKS: " + predicted_number_tasks);
				
				break;
			}
			
			
		}
		
		if (predicted_number_tasks < 0){
			System.err.println("The required module size is too small for the maximun number of tasks set to '" + max_number_tasks + "'. The current maximum number of tasks leads to modules of size (approximately): " + 
						tasks.get(0).getSignatureSourceOntology().size() + " in the source ontoology, and "+ tasks.get(0).getSignatureTargetOntology().size() + " in the target ontology.");
			tasks =  new ArrayList<MatchingTask>();
			
		}
		else{
			//Return partitioning for prediction
			BasicDivision partitioning = new BasicDivision(predicted_number_tasks);				
			
			//Return tasks
			tasks= partitioning.createPartitionedMatchingTasks(source, target);
			
		}
		
		total_time = StatisticsTimeMappings.getTotalRunningTime();
		LogOutput.print("Total time (s): " + total_time);		
		
		return tasks;
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

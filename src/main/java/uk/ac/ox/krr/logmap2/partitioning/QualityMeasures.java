/*******************************************************************************
 * Copyright 2017 by the Department of Informatics (University of Oslo)
 * 
 *    This file is part of the Ontology Services Toolkit 
 *
 *******************************************************************************/
package uk.ac.ox.krr.logmap2.partitioning;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.lang.Math.*;

import uk.ac.ox.krr.logmap2.mappings.objects.MappingObjectStr;

/**
 *
 * @author ernesto
 * Created on 27 Feb 2018
 *
 */
public class QualityMeasures {
	
	//Coverage: per matching task and total
	//Number of matching tasks
	//Size modules: average, max and min, balanced modules
	//Overlapping among modules: bad and good overlapping
	//Overlapping degree: matrix... average
	
	//general statistics
	int number_tasks;
	int min_size_modules;
	int max_size_modules;
	double average_size_modules;
	int min_size_task; //m X n
	int max_size_task;
	double avg_size_task;
	
	//balanced/ratio modules: 01/02 -> compare with ratio of original task
	double min_ratio_modules;
	double max_ratio_modules;
	double average_ratio_modules;
	
	
	
	//overlapping: 
	//aggregation of module sizes wrt to ontology size
	//aggregation of (sub)task sizes wrt size original task (Sig(O1) x Sig(O2)).
	int aggregation_modules_ontology1;
	int aggregation_modules_ontology2;
	int aggregation_task_sizes;
	
	
	
	//Coverage
	//Local
	double min_coverage_task;
	double max_coverage_task;
	double average_coverage_task;
	//Global
	double global_coverage;
	
	//Coverage + overlapping
	//Cases where e1 from RA appears in MT but not e2 and viceversa: related to coverage?
	//Cases where e1 and e2 appear in more than one MT
	int positive_redundancy;
	int negative_redundancy;
	
	
	
	List<MatchingTask> tasks;
	Set<MappingObjectStr> alignment;
	
	
	
	
	public QualityMeasures(){
		
	}
	
	
	public QualityMeasures(List<MatchingTask> tasks, Set<MappingObjectStr> alignment){
		this.tasks=tasks;
		this.alignment=alignment;
		
	}
	
	
	
	public void computeMetricsModuleTasks(List<MatchingTask> tasks){
	
		int aux;
		double ratio;
		
		number_tasks = tasks.size();
		aggregation_modules_ontology1=0;
		aggregation_modules_ontology2=0;		
		
	
		min_size_modules = min(tasks.get(0).getSignatureSourceOntology().size(), tasks.get(0).getSignatureTargetOntology().size());
		max_size_modules = max(tasks.get(0).getSignatureSourceOntology().size(), tasks.get(0).getSignatureTargetOntology().size());
		average_size_modules=0;
		
		min_ratio_modules = (double)tasks.get(0).getSignatureSourceOntology().size()/(double)tasks.get(0).getSignatureTargetOntology().size();
		max_ratio_modules = (double)tasks.get(0).getSignatureSourceOntology().size()/(double)tasks.get(0).getSignatureTargetOntology().size();
		average_ratio_modules = 0;
		
		for (int i=0; i<tasks.size(); i++){			
			
			aggregation_modules_ontology1+=tasks.get(i).getSignatureSourceOntology().size();
			aggregation_modules_ontology2+=tasks.get(i).getSignatureTargetOntology().size();
			
			
			aux = min(tasks.get(i).getSignatureSourceOntology().size(), tasks.get(i).getSignatureTargetOntology().size());
			if (aux<min_size_modules)
				min_size_modules = aux;
			
			aux = max(tasks.get(i).getSignatureSourceOntology().size(), tasks.get(i).getSignatureTargetOntology().size());
			if (aux>max_size_modules)
				max_size_modules = aux;
			
			average_size_modules += tasks.get(i).getSignatureSourceOntology().size() + tasks.get(i).getSignatureTargetOntology().size();
			
			
			ratio = (double)tasks.get(i).getSignatureSourceOntology().size()/(double)tasks.get(i).getSignatureTargetOntology().size();
			if (ratio<min_ratio_modules)
				min_ratio_modules=ratio;
			else if (ratio>max_ratio_modules)
				max_ratio_modules=ratio;
			
			average_ratio_modules += ratio;
			
		}
		
		average_size_modules = (double)average_size_modules/(double)tasks.size()*2;
		
		average_ratio_modules = (double)average_ratio_modules/(double)tasks.size();
		
	}
	
	
	
	
	public void computeSizeTasks(List<MatchingTask> tasks){
		
		avg_size_task=0;
		
		aggregation_task_sizes=0;
		
		int size_task  = tasks.get(0).getSignatureSourceOntology().size() * tasks.get(0).getSignatureTargetOntology().size(); //m X n;
		
		min_size_task = size_task;
		max_size_task = size_task;
		
		for (int i=0; i<tasks.size(); i++){	
		
			size_task = tasks.get(i).getSignatureSourceOntology().size() * tasks.get(i).getSignatureTargetOntology().size(); //m X n;
			
			if (size_task<min_size_task)
				min_size_task=size_task;
			else if (size_task>max_size_task)
				max_size_task=size_task;
			
			aggregation_task_sizes+=size_task;
			
		}
		
		avg_size_task = (double)aggregation_task_sizes/(double)tasks.size();
		
		
	}
	
	
	
	public double getLocalCoverage(MatchingTask task, Set<MappingObjectStr> alignment){
		
		int positive_hits=0;
		
		for (MappingObjectStr mapping : alignment){
			//if (task.getSignatureSourceOntology().contains(mapping.getIRIStrEnt1()) && task.getSignatureTargetOntology().contains(mapping.getIRIStrEnt2())){
			if (isMappingAvailableInTask(task, mapping)){
				positive_hits++;
			}			
		}
		
		return (double)positive_hits/(double)alignment.size();
		
	}
	
	
	/**
	 * Computes global coverage and also local values
	 * @param tasks
	 * @param alignment
	 * @return global coverage
	 */
	public double computeCoverageTasks(List<MatchingTask> tasks, Set<MappingObjectStr> alignment){
		
		int positive_hits=0;
		
		negative_redundancy=0;
		
		//Each entry is associated to one mapping
		List<Integer> positive_hits_list = new ArrayList<Integer>();
		
		//Each entry associated to a task
		List<Integer> positive_hits_task = new ArrayList<Integer>();
		for (int i = 0; i < tasks.size(); i++) {
			positive_hits_task.add(0);
		}
		
		
		for (MappingObjectStr mapping : alignment){
			
			positive_hits=0;
			
			for (int i=0; i<tasks.size(); i++){
				
				MatchingTask task = tasks.get(i);
				
				if (isMappingAvailableInTask(task, mapping)){
					positive_hits_task.set(i, positive_hits_task.get(i) + 1); //local coverage
					positive_hits++; //global coverage
				}
				else if (isPartialMappingAvailableInTask(task, mapping)){
					negative_redundancy++;
				}
				
			}
			
			positive_hits_list.add(positive_hits);
			
		}
		
		
		
		
		//Local coverage
		min_coverage_task=positive_hits_task.get(0);
		max_coverage_task=positive_hits_task.get(0);
		average_coverage_task=0;
		for (int i=0; i<positive_hits_task.size(); i++){
			
			if (positive_hits_task.get(i)>max_coverage_task)
				max_coverage_task=positive_hits_task.get(i);
			else if (positive_hits_task.get(i) < min_coverage_task)
				min_coverage_task=positive_hits_task.get(i);
			
			average_coverage_task+=positive_hits_task.get(i);
			
		}
		
		
		average_coverage_task = (double)average_coverage_task/(double)tasks.size();
		average_coverage_task = (double)average_coverage_task/(double)alignment.size();
		
		max_coverage_task = (double)max_coverage_task/(double)alignment.size();
		min_coverage_task = (double)min_coverage_task/(double)alignment.size();	
		
		
		
		
		//Global coverage
		positive_hits=0;
		for (int hits : positive_hits_list){
			
			if (hits>0)
				positive_hits++;
			
			if (hits>1)
				positive_redundancy++;
			
		}
		
		global_coverage = (double)positive_hits/(double)alignment.size();
		
		return global_coverage;
		
	}
	
	
	public boolean isMappingAvailableInTask(MatchingTask task, MappingObjectStr mapping){
		
		if (task.getSignatureSourceOntology().contains(mapping.getIRIStrEnt1()) && task.getSignatureTargetOntology().contains(mapping.getIRIStrEnt2()))
			return true;
		
		return false;
	}
	
	
	//Negative redundancy
	public boolean isPartialMappingAvailableInTask(MatchingTask task, MappingObjectStr mapping){
		
		if (task.getSignatureSourceOntology().contains(mapping.getIRIStrEnt1()) || task.getSignatureTargetOntology().contains(mapping.getIRIStrEnt2()))
			return true;
		
		return false;
	}
	
	
	
	
	public String toString(){
		return "";
	}
	
	
	
	
	
	

}

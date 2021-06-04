package uk.ac.ox.krr.logmap2.division;

import java.util.ArrayList;
import java.util.Formatter;
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
	
	
	int min_size_modules_ontology1;
	int max_size_modules_ontology1;
	double average_size_modules_ontology1;
	int min_size_modules_ontology2;
	int max_size_modules_ontology2;
	double average_size_modules_ontology2;
	
	long min_size_task; //m X n
	long max_size_task;
	String min_size_task_str; //m X n
	String max_size_task_str;
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
	long aggregation_task_sizes;
	
	double ratio_task_size;
	
	long size_ontology1;
	long size_ontology2;
	
	
	
	//Coverage
	//Local
	double min_coverage_task;
	double max_coverage_task;
	double average_coverage_task;
	//Global
	double global_coverage_1;
	double global_coverage_2;
	
	//Coverage + overlapping
	//Cases where e1 from RA appears in MT but not e2 and viceversa: related to coverage?
	//Cases where e1 and e2 appear in more than one MT
	int positive_redundancy;
	int negative_redundancy;
	
	
	double computation_time=0.0;
	
	
	
	
	List<MatchingTask> tasks;
	//Set<MappingObjectStr> alignment;
	
	
	
	
	
	
	public QualityMeasures(){
		
	}
	
	

	
	
	public QualityMeasures(List<MatchingTask> tasks, Set<MappingObjectStr> alignment_1, Set<MappingObjectStr> alignment_2, double time, long size_ontology1, long size_ontology2){
		this(tasks, alignment_1, alignment_2, size_ontology1, size_ontology2);
		
		computation_time=time;
		
	}
	
	
	public void clear(){
		tasks.clear();
		//alignment.clear();
	}
	
	
	public QualityMeasures(List<MatchingTask> tasks, Set<MappingObjectStr> alignment_1, Set<MappingObjectStr> alignment_2, long size_ontology1, long size_ontology2){
		this.tasks=tasks;
		//this.alignment=alignment;
		
		
		/*for (MatchingTask task : tasks){
			System.out.println(
					task.getSignatureSourceOntology().size() + "\t" + 
					task.getSignatureTargetOntology().size() + "\t" + getLocalCoverage(task, alignment));
		}*/
		
		
		
		computeMetricsModuleTasks(tasks);
		
		computeSizeTasks(tasks, size_ontology1, size_ontology2);
		
		global_coverage_1 = computeCoverageTasks(tasks, alignment_1);
		global_coverage_2 = computeCoverageTasks(tasks, alignment_2);
		
		
	}
	
	
	
	public void computeMetricsModuleTasks(List<MatchingTask> tasks){
	
		int aux;
		double ratio;
		
		number_tasks = tasks.size();
		aggregation_modules_ontology1=0;
		aggregation_modules_ontology2=0;		
		
	
		min_size_modules_ontology1 = tasks.get(0).getSignatureSourceOntology().size();
		max_size_modules_ontology1 = tasks.get(0).getSignatureSourceOntology().size();
		
		min_size_modules_ontology2 = tasks.get(0).getSignatureTargetOntology().size();
		max_size_modules_ontology2 = tasks.get(0).getSignatureTargetOntology().size();
		
		average_size_modules_ontology1=0;
		average_size_modules_ontology2=0;
		
		min_ratio_modules = (double)tasks.get(0).getSignatureSourceOntology().size()/(double)tasks.get(0).getSignatureTargetOntology().size();
		max_ratio_modules = (double)tasks.get(0).getSignatureSourceOntology().size()/(double)tasks.get(0).getSignatureTargetOntology().size();
		average_ratio_modules = 0;
		
		for (int i=0; i<tasks.size(); i++){			
			
			aggregation_modules_ontology1+=tasks.get(i).getSignatureSourceOntology().size();
			aggregation_modules_ontology2+=tasks.get(i).getSignatureTargetOntology().size();
			
			
			aux = tasks.get(i).getSignatureSourceOntology().size();
			if (aux<min_size_modules_ontology1)
				min_size_modules_ontology1 = aux;
			else if (aux>max_size_modules_ontology1)
				max_size_modules_ontology1 = aux;
			
			
			aux = tasks.get(i).getSignatureTargetOntology().size();
			if (aux<min_size_modules_ontology2)
				min_size_modules_ontology2 = aux;
			else if (aux>max_size_modules_ontology2)
				max_size_modules_ontology2 = aux;
			
			
			average_size_modules_ontology1 += tasks.get(i).getSignatureSourceOntology().size();
			average_size_modules_ontology2 += tasks.get(i).getSignatureTargetOntology().size();
			
			
			ratio = (double)tasks.get(i).getSignatureSourceOntology().size()/(double)tasks.get(i).getSignatureTargetOntology().size();
			if (ratio<min_ratio_modules)
				min_ratio_modules=ratio;
			else if (ratio>max_ratio_modules)
				max_ratio_modules=ratio;
			
			average_ratio_modules += ratio;
			
		}
		
		average_size_modules_ontology1 = (double)average_size_modules_ontology1/(double)tasks.size();
		average_size_modules_ontology2 = (double)average_size_modules_ontology2/(double)tasks.size();
		
		average_ratio_modules = (double)average_ratio_modules/(double)tasks.size();
		
	}
	
	
	
	
	public void computeSizeTasks(List<MatchingTask> tasks, long size_ontology1, long size_ontology2){
		
		avg_size_task=0;
		
		aggregation_task_sizes=0;
		
		
		//long is important for the ontology sizes otherwise the task size does not fit within "int"
		long original_task_size = size_ontology1 * size_ontology2;
		
		
		long size_task  = (long)tasks.get(0).getSignatureSourceOntology().size() * (long)tasks.get(0).getSignatureTargetOntology().size(); //m X n;
		String size_task_str = (long)tasks.get(0).getSignatureSourceOntology().size() + " x " + (long)tasks.get(0).getSignatureTargetOntology().size();
		
		min_size_task = size_task;
		min_size_task_str = size_task_str;
		max_size_task = size_task;
		max_size_task_str = size_task_str;
		
		for (int i=0; i<tasks.size(); i++){	
		
			size_task = (long)tasks.get(i).getSignatureSourceOntology().size() * (long)tasks.get(i).getSignatureTargetOntology().size(); //m X n;
			size_task_str = (long)tasks.get(i).getSignatureSourceOntology().size() + " x " + (long)tasks.get(i).getSignatureTargetOntology().size();
			
			
			if (size_task<min_size_task) {
				min_size_task=size_task;
				min_size_task_str=size_task_str;
			}
			else if (size_task>max_size_task) {
				max_size_task=size_task;
				max_size_task_str=size_task_str;
			}
			
			aggregation_task_sizes+=size_task;
			
			//System.out.println(size_task);
			
		}
		
		avg_size_task = (double)aggregation_task_sizes/(double)tasks.size();
		
		
		//System.out.println(tasks.get(0).getSignatureSourceOntology().size()  + " "+ tasks.get(0).getSignatureTargetOntology().size());
		//System.out.println(size_ontology1 + " " + size_ontology2);
		//
		//System.out.println(aggregation_task_sizes);
		//System.out.println(original_task_size);
		ratio_task_size = (double)aggregation_task_sizes / (double)(original_task_size); 
		
		
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
		
		//global_coverage = (double)positive_hits/(double)alignment.size();
		
		//return global_coverage;
		
		return (double)positive_hits/(double)alignment.size();
		
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
	
	
	public static String toStringHeader(){
		
		
		StringBuilder sb = new StringBuilder();
		
		//sb.append("\n");
		
		sb.append("number_tasks").append("\t")
		.append("min_modules_O1").append("\t")
		.append("max_modules_O1").append("\t")
		.append("avg_modules_O1").append("\t")
		.append("min_modules_O2").append("\t")
		.append("max_modules_O2").append("\t")
		.append("avg_modules_O2").append("\t")
		.append("min_task").append("\t")
		.append("max_task").append("\t")
		//.append("min_size_task").append("\t")
		//.append("max_size_task").append("\t")
		//.append("avg_size_task").append("\t")
		//.append("min_ratio_modules").append("\t")
		//.append("max_ratio_modules").append("\t")
		//.append("average_ratio_modules").append("\t")
		//.append("aggregation_modules_ontology1").append("\t")
		//.append("aggregation_modules_ontology2").append("\t")
		//.append("task_size").append("\t")
		.append("ratio_tasks_size").append("\t")
		//.append("positive_redundancy").append("\t")
		//.append("negative_redundancy").append("\t")
		//.append("min_coverage_task").append("\t")
		//.append("max_coverage_task").append("\t")
		//.append("average_coverage_task").append("\t")
		.append("coverage1").append("\t")
		.append("coverage2").append("\t")
		.append("time");
		
		return sb.toString();
	}
	
	
	
	
	public String toString(){
		
		
		StringBuilder sb = new StringBuilder();
		//StringBuilder sb_formattter = new StringBuilder();
		//=Formatter formatter = new Formatter(sb_formattter);
		//formatter.format("%,d", 
		
		
		sb.append(number_tasks).append("\t")
			.append(min_size_modules_ontology1).append("\t")
			.append(max_size_modules_ontology1).append("\t")
			.append(average_size_modules_ontology1).append("\t")
			.append(min_size_modules_ontology2).append("\t")
			.append(max_size_modules_ontology2).append("\t")
			.append(average_size_modules_ontology2).append("\t")
			.append(min_size_task_str).append("\t")
			.append(max_size_task_str).append("\t")
			//.append(min_size_task).append("\t")
			//.append(max_size_task).append("\t")
			//.append(avg_size_task).append("\t")
			//.append(min_ratio_modules).append("\t")
			//.append(max_ratio_modules).append("\t")
			//.append(average_ratio_modules).append("\t")
			//.append(aggregation_modules_ontology1).append("\t")
			//.append(aggregation_modules_ontology2).append("\t")
			//.append(aggregation_task_sizes).append("\t")
			.append(ratio_task_size).append("\t")
			//.append(positive_redundancy).append("\t")
			//.append(negative_redundancy).append("\t")
			//.append(min_coverage_task).append("\t")
			//.append(max_coverage_task).append("\t")
			//.append(average_coverage_task).append("\t")
			.append(global_coverage_1).append("\t")
			.append(global_coverage_2).append("\t")
			.append(computation_time);
		
		
		
		
		return sb.toString();
	}
	
	
	
	
	
	

}

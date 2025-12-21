/*******************************************************************************
 * Copyright 2025 by City St George's, University of London
 * 
 *******************************************************************************/
package uk.ac.ox.krr.logmap2.test.oaei;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import uk.ac.ox.krr.logmap2.LogMapLLM_Interface;
import uk.ac.ox.krr.logmap2.mappings.objects.MappingObjectStr;
import uk.ac.ox.krr.logmap2.oaei.reader.RDFAlignReader;
import uk.ac.ox.krr.logmap2.utilities.Timer;


/**
 *
 * @author ernesto
 * Created on 20 Dec 2025
 *
 */
public abstract class TestOAEITrackEnhanced {
	
	
	List<OAEITask> tasks = new ArrayList<OAEITask>();
	
	Timer t;
	
	//Default parameters
	boolean extractExtendedQuestions4LLM=false;
	String path_to_paramaters="";
	String base_path_to_output_mappings="";
	
	//TODO To set these
	Set<MappingObjectStr> mappings_local_oracle = new HashSet<MappingObjectStr>();
	String path_to_local_oracle="";
	
	
	
	
	public TestOAEITrackEnhanced(){
		
		//Parameters.print_output_always=true;
		//Parameters.use_overlapping=false;
		
		setUp();
		
	}
	
	
	protected void setExtendedQuestions4LLM(boolean extended_questions) {
		extractExtendedQuestions4LLM = extended_questions;
	}
	
	
	/**
	 * Otherwise default path will be used
	 * @param path
	 */
	protected void setPathToLogMapParameters(String path) {
		path_to_paramaters = path;
	}
	
	
	/**
	 * If empty/default. Mappings will not be stored.
	 * @param path
	 */
	protected void setBasePathForOutputMappings(String path) {
		base_path_to_output_mappings = path;
	}
	
	
	protected void setMappingsLocalOracle(Set<MappingObjectStr> mappings) {
		mappings_local_oracle.addAll(mappings);
	}
	
	
	protected void setMappingsLocalOracle(String pathfile) {
		path_to_local_oracle = pathfile;
	}
	
	
	protected abstract void setUp(); 
	
	
	
	public void evaluateTasks() throws MalformedURLException, Exception{
	
		//Includes questions to ask use/oracle/llm
		System.out.println("Task\tTime\tMappings\tQuestions\tPrecision\tRecall\tF-Score");
		for (OAEITask task : tasks){
			//System.out.println(task.getTaskName());
			evaluateTask(task);
		}
		
	}
	
	
	
	private void printMissedCasses(Set<MappingObjectStr> prediction, Set<MappingObjectStr> reference){
		Set<MappingObjectStr> difference = new HashSet<MappingObjectStr>();
		difference.addAll(reference);
		difference.removeAll(prediction);
		
		for (MappingObjectStr mapping : difference){
			System.out.println(mapping.getIRIStrEnt1() + "  " + mapping.getIRIStrEnt2() + "  " + mapping.getConfidence());
		}
	}
	
	
	
	private void printIntersection(Set<MappingObjectStr> prediction, Set<MappingObjectStr> reference){
		Set<MappingObjectStr> intersection = new HashSet<MappingObjectStr>();
		intersection.addAll(prediction);
		intersection.retainAll(reference);
		
		for (MappingObjectStr mapping : intersection){
			System.out.println(mapping.getIRIStrEnt1() + "  " + mapping.getIRIStrEnt2() + "  " + mapping.getConfidence());
		}
	}
	
	
	public void evaluateTask(OAEITask task) throws MalformedURLException, Exception{
		
		
		double matching_time;
		
		t = new Timer();
		
		LogMapLLM_Interface logmapllm = new LogMapLLM_Interface(task.getSource(), task.getTarget(), task.getTaskName());
		
		String full_path_to_output_mappings = base_path_to_output_mappings + task.getTaskName() + "/";
				
		logmapllm.setExtendedQuestions4LLM(extractExtendedQuestions4LLM);
		logmapllm.setPathForOutputMappings(full_path_to_output_mappings);
		logmapllm.setPathToLogMapParameters(path_to_paramaters); 
		
		
		if (!mappings_local_oracle.isEmpty())
			logmapllm.performAlignmentWithLocalOracle(mappings_local_oracle);
		else if (!path_to_local_oracle.equals(""))
			logmapllm.performAlignmentWithLocalOracle(path_to_local_oracle);
		else
			logmapllm.performAlignment();
		
		
		
		matching_time = t.durationMilisecons();
		t.pause();
		
		
		if (task.getReference()!=null && !task.getReference().equals("")) {
			
			RDFAlignReader readerReference = 
					new RDFAlignReader(new URL(task.getReference()));
			
			
			//printMissedCasses(logmapllm.getLogMapMappings(), readerReference.getMappingObjects());
			//printIntersection(logmapllm.getLogMapMappings(), readerReference.getMappingObjects());
			
			StandardMeasures.computeStandardMeasures(logmapllm.getLogMapMappings(), readerReference.getMappingObjects());
			//System.out.println(readerReference.getMappingObjects().size());
			
			
			System.out.println(task.getTaskName() + "\t" + matching_time + "\t" + logmapllm.getLogMapMappings().size() + "\t"  + logmapllm.getLogMapMappingsForLLM().size() + "\t" + StandardMeasures.getPrecision()  + "\t" + StandardMeasures.getRecall()  + "\t" + StandardMeasures.getFscore());
		}
		else {
			System.out.println("No reference alignment given.");
		}
		
			
		
	}



	
	
	
	

}

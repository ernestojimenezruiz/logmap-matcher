package uk.ac.ox.krr.logmap2;

import java.io.File;
import java.util.Set;

import uk.ac.ox.krr.logmap2.mappings.objects.MappingObjectStr;
import uk.ac.ox.krr.logmap2.oaei.oracle.LocalOracle;
import uk.ac.ox.krr.logmap2.oaei.oracle.OracleManager;

/**
 * 
 */
public class LogMapLLM_Interface {
	
	String onto_uri1; 
	String onto_uri2;
	String task_name;
	
	//Default parameters
	boolean extractExtendedQuestions4LLM=false;
	String path_to_paramaters="";
	String path_to_output_mappings="";
	
	boolean save_mappings = false;
	
	LogMap2_Matcher logmap;
	
	
	/**
	 * Constructor
	 * @param onto_uri1
	 * @param onto_uri2
	 * @param task_name The name of the matching task (e.g., onto1-onto2, anatomy, bioml-omim-ordo, conference-ekaw-sigkdd)
	 */
	public LogMapLLM_Interface(String onto_uri1, String onto_uri2, String task_name) {
		this.onto_uri1 = onto_uri1;
		this.onto_uri2 = onto_uri2;
		this.task_name = task_name;
	}
	
	
	
	public void setExtendedQuestions4LLM(boolean extended_questions) {
		extractExtendedQuestions4LLM = extended_questions;
	}
	
	
	/**
	 * Otherwise default path will be used
	 * @param path
	 */
	public void setPathToLogMapParameters(String path) {
		path_to_paramaters = path;
	}
	
	
	/**
	 * If empty/default. Mappings will not be stored.
	 * @param path
	 */
	public void setPathForOutputMappings(String path) {
		path_to_output_mappings = path;
	}
	
	
	
	private void setUpParameters() {
		
		//Default 
		if (path_to_paramaters==null || path_to_paramaters.equals(""))
			Parameters.readParameters();
		else
			Parameters.readParameters(path_to_paramaters);
		
		
			
		if (path_to_output_mappings!=null && !path_to_output_mappings.equals("")) {
			File directory = new File(path_to_output_mappings);
			if (!directory.exists())
				if (directory.mkdir())
					save_mappings=true;
				else
					System.err.println("The directory '"+ path_to_output_mappings + "' could not be created. Output mappinsg will not be saved.");
			//else {
			//	System.out.println("Directory exists " + path_to_output_mappings);
			//}
		}
		//else {
		//	System.out.println("Empty path  " + path_to_output_mappings);
		//}
		
		
		OracleManager.setExtendedQuestions(extractExtendedQuestions4LLM); 
		
		
		
	}
	
	
	
	
	/**
	 * Performs ontology alignment with LogMap
	 */
	public void performAlignment() {
			
		//Sets things up in terms of paths and parameters
		setUpParameters();
		
		
		if (save_mappings){
			String output_file_template = path_to_output_mappings + task_name + "-";			
			//System.out.println("Saving mappings to " + path_to_output_mappings);			
			//Save all formats and also discarded: done now internally
			logmap = new LogMap2_Matcher(onto_uri1, onto_uri2, output_file_template, false);
		}
		else {
			logmap = new LogMap2_Matcher(onto_uri1, onto_uri2);
		}
				
		
	}
	
	
	/**
	 * Perform alignment using a local oracle. For example, a set of mapping annotated/validated by an LLM given as objects.
	 * @param mappings_local_oracle
	 */
	public void performAlignmentWithLocalOracle(Set<MappingObjectStr> mappings_local_oracle) {
		
		//Setting up oracle
		OracleManager.setLocalOracle(true);
				
		//Load from objects
		LocalOracle.loadLocalOraculo(mappings_local_oracle);
				
		//Error rate
		LocalOracle.setErrorRate(0);
		
		
		performAlignment();
		
		
	}
	
	
	
	/**
	 * Perform alignment using a local oracle. For example, a set of mapping annotated/validated by an LLM given as a File.
	 * @param mappings_local_oracle
	 */
	public void performAlignmentWithLocalOracle(String path_to_local_oracle) {
		
		//Setting up oracle
		OracleManager.setLocalOracle(true);
				
		//Load from objects
		LocalOracle.loadLocalOraculoLLM(path_to_local_oracle);
				
		//Error rate
		LocalOracle.setErrorRate(0);
		
		
		performAlignment();
		
		
	}
	
	
	/**
	 * Those mapping where LogMap is uncertain
	 * @return
	 */
	public Set<MappingObjectStr> getLogMapMappingsForLLM(){
		return logmap.getLogmap2_mappings4user();
	}
	
	
	/**
	 * Those selected by LogMap as output
	 * @return
	 */
	public Set<MappingObjectStr> getLogMapMappings(){
		return logmap.getLogmap2_Mappings();
	}
	
	

}

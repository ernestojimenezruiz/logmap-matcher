package uk.ac.ox.krr.logmap2.test.oaei.interactivity;

import java.util.Set;

import uk.ac.ox.krr.logmap2.mappings.objects.MappingObjectStr;
import uk.ac.ox.krr.logmap2.oaei.reader.MappingsReaderManager;

/**
 * THis class checks teh performance of how LogMap performs on the Mappings that it would ask to an oracle (e.g., domain expert, large language models, simulated user).
 * Created in April 2025
 *  
 */
public class PerformanceLogMapAutomaticDecisions {
	
	Set<MappingObjectStr> logmap_mappings;
	Set<MappingObjectStr> reference_mappings;
	Set<MappingObjectStr> mappings4oracle;
	
	public PerformanceLogMapAutomaticDecisions() {
		
	}

	
	private Set<MappingObjectStr> loadMappings(String file_mappings){
		MappingsReaderManager manager = new MappingsReaderManager(file_mappings, MappingsReaderManager.FlatFormat);
		
		return manager.getMappingObjects();
	}
	
	
	
	public void TestAnatomy() {
		
		String path = "C:/Users/Ernes/OneDrive/Documents/OAEI/oracle/anatomy/";
		String logmap_mappings_file = path + "anatomy-logmap_mappings.txt";
		String reference_mappings_file = path + "reference.rdf.txt";
		String mappings4oracle_file = path + "anatomy-logmap_mappings_to_ask_oracle_user_llm.txt";
		
		logmap_mappings = loadMappings(logmap_mappings_file);
		reference_mappings = loadMappings(reference_mappings_file);
		mappings4oracle = loadMappings(mappings4oracle_file);
		
		CalculateMetrics();		
		
	}
	
	
	private void CalculateMetrics() {
		
		int tp = 0;
		int fp = 0;
		int fn = 0;
		
		for (MappingObjectStr mapping : mappings4oracle) {
			
			if (logmap_mappings.contains(mapping)) {
				if (reference_mappings.contains(mapping))
					tp++;
				else
					fp++;
			}			
			else {
				if (reference_mappings.contains(mapping))
					fn++;
			}						
			
		}
		
		System.out.println(tp + " " + fp + "  " + fn);

		
		double precision = (double)tp/(double)(tp+fp);
		double recall = (double)tp/(double)(tp+fn);
		double f_score = (2*precision*recall)/(precision + recall);
		
		System.out.println(precision + " " + recall + "  " + f_score);
	}
	
	
	
	
	public static void main(String[] args){
		
		PerformanceLogMapAutomaticDecisions preformance_logmap_automatic = new PerformanceLogMapAutomaticDecisions();
		
		
		preformance_logmap_automatic.TestAnatomy();
		
		
	}
	

}

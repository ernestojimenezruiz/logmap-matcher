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
	
	boolean reduced_set = false;
	
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
		String mappings4oracle_file;
		if (reduced_set)
			mappings4oracle_file = path + "anatomy-logmap_mappings_to_ask_oracle_user_llm_reduced.txt";
		else
			mappings4oracle_file = path + "anatomy-logmap_mappings_to_ask_oracle_user_llm.txt";
		
		logmap_mappings = loadMappings(logmap_mappings_file);
		reference_mappings = loadMappings(reference_mappings_file);
		mappings4oracle = loadMappings(mappings4oracle_file);
		
		CalculateMetrics();		
		
	}
	
	
	public void TestNCIT2DOID() {
		
		String path = "C:/Users/Ernes/OneDrive/Documents/OAEI/oracle/bioml-ncit-doid/";
		String logmap_mappings_file = path + "bioml-ncit-doid-logmap_mappings.txt";
		String reference_mappings_file = path + "ncit-doid-ref.txt";
		
		String mappings4oracle_file;
		if (reduced_set)
			mappings4oracle_file = path + "bioml-ncit-doid-logmap_mappings_to_ask_oracle_user_llm_reduced.txt";
		else
			mappings4oracle_file = path + "bioml-ncit-doid-logmap_mappings_to_ask_oracle_user_llm.txt";
		
		logmap_mappings = loadMappings(logmap_mappings_file);
		reference_mappings = loadMappings(reference_mappings_file);
		mappings4oracle = loadMappings(mappings4oracle_file);
		
		CalculateMetrics();		
		
	}
	
	public void TestOMIM2ORDO() {
		
		String path = "C:/Users/Ernes/OneDrive/Documents/OAEI/oracle/bioml-omim-ordo/";
		String logmap_mappings_file = path + "bioml-omim-ordo-logmap_mappings.txt";
		String reference_mappings_file = path + "omim-ordo-ref.txt";
		
		String mappings4oracle_file;
		if (reduced_set)		
			mappings4oracle_file = path + "bioml-omim-ordo-logmap_mappings_to_ask_oracle_user_llm_reduced.txt";
		else
			mappings4oracle_file = path + "bioml-omim-ordo-logmap_mappings_to_ask_oracle_user_llm.txt";
		
		logmap_mappings = loadMappings(logmap_mappings_file);
		reference_mappings = loadMappings(reference_mappings_file);
		mappings4oracle = loadMappings(mappings4oracle_file);
		
		CalculateMetrics();		
		
	}
	
	
	
	public void TestSNOMED2FMA_BODY() {
		
		String path = "C:/Users/Ernes/OneDrive/Documents/OAEI/oracle/bioml-snomed-fma.body/";
		String logmap_mappings_file = path + "bioml-snomed-fma.body-logmap_mappings.txt";
		String reference_mappings_file = path + "snomed-fma.body-ref.txt";
		
		String mappings4oracle_file;
		if (reduced_set)
			mappings4oracle_file = path + "bioml-snomed-fma.body-logmap_mappings_to_ask_oracle_user_llm_reduced.txt";
		else
			mappings4oracle_file = path + "bioml-snomed-fma.body-logmap_mappings_to_ask_oracle_user_llm.txt";
		
		logmap_mappings = loadMappings(logmap_mappings_file);
		reference_mappings = loadMappings(reference_mappings_file);
		mappings4oracle = loadMappings(mappings4oracle_file);
		
		CalculateMetrics();		
		
	}
	
	
	public void TestSNOMED2NCIT_NEO() {
		
		String path = "C:/Users/Ernes/OneDrive/Documents/OAEI/oracle/bioml-snomed-ncit.neoplas/";
		String logmap_mappings_file = path + "bioml-snomed-ncit-neoplas-logmap_mappings.txt";
		String reference_mappings_file = path + "snomed-ncit.neoplas-ref.txt";
		
		String mappings4oracle_file;
		if (reduced_set)
			mappings4oracle_file = path + "bioml-snomed-ncit-neoplas-logmap_mappings_to_ask_oracle_user_llm_reduced.txt";
		else
			mappings4oracle_file = path + "bioml-snomed-ncit-neoplas-logmap_mappings_to_ask_oracle_user_llm.txt";
		
		logmap_mappings = loadMappings(logmap_mappings_file);
		reference_mappings = loadMappings(reference_mappings_file);
		mappings4oracle = loadMappings(mappings4oracle_file);
		
		CalculateMetrics();		
		
	}
	
	public void TestSNOMED2NCIT_PHARM() {
		
		String path = "C:/Users/Ernes/OneDrive/Documents/OAEI/oracle/bioml-snomed-ncit.pharm/";
		String logmap_mappings_file = path + "bioml-snomed-ncit-pharm-logmap_mappings.txt";
		String reference_mappings_file = path + "snomed-ncit.pharm-ref.txt";
		
		String mappings4oracle_file;
		if (reduced_set)
			mappings4oracle_file = path + "bioml-snomed-ncit-pharm-logmap_mappings_to_ask_oracle_user_llm_reduced.txt";
		else
			mappings4oracle_file = path + "bioml-snomed-ncit-pharm-logmap_mappings_to_ask_oracle_user_llm.txt";
		
		logmap_mappings = loadMappings(logmap_mappings_file);
		reference_mappings = loadMappings(reference_mappings_file);
		mappings4oracle = loadMappings(mappings4oracle_file);
		
		CalculateMetrics();		
		
	}
	
	
	
	
	
	public void TestFMA2NCI() {
		
		String path = "C:/Users/Ernes/OneDrive/Documents/OAEI/oracle/largebio-fma-nci/";
		String logmap_mappings_file = path + "largebio-fma-nci-logmap_mappings.txt";		
		String reference_mappings_file = path + "oaei_FMA2NCI_UMLS_mappings_with_flagged_repairs.rdf.txt";
		
		String mappings4oracle_file;
		if (reduced_set)
			mappings4oracle_file = path + "largebio-fma-nci-logmap_mappings_to_ask_oracle_user_llm_reduced.txt";
		else
			mappings4oracle_file = path + "largebio-fma-nci-logmap_mappings_to_ask_oracle_user_llm.txt";
			
		logmap_mappings = loadMappings(logmap_mappings_file);
		reference_mappings = loadMappings(reference_mappings_file);
		mappings4oracle = loadMappings(mappings4oracle_file);
		
		CalculateMetrics();		
		
	}
	
	
	public void TestFMA2SNOMED() {
		
		String path = "C:/Users/Ernes/OneDrive/Documents/OAEI/oracle/largebio-fma-snomed/";
		String logmap_mappings_file = path + "largebio-fma-snomed-logmap_mappings.txt";
		String reference_mappings_file = path + "oaei_FMA2SNOMED_UMLS_mappings_with_flagged_repairs.rdf.txt";
		
		String mappings4oracle_file;
		if (reduced_set)
			mappings4oracle_file = path + "largebio-fma-snomed-logmap_mappings_to_ask_oracle_user_llm_reduced.txt";
		else
			mappings4oracle_file = path + "largebio-fma-snomed-logmap_mappings_to_ask_oracle_user_llm.txt";
		
		logmap_mappings = loadMappings(logmap_mappings_file);
		reference_mappings = loadMappings(reference_mappings_file);
		mappings4oracle = loadMappings(mappings4oracle_file);
		
		CalculateMetrics();		
		
	}
	
	
	public void TestSNOMED2NCI() {
		
		String path = "C:/Users/Ernes/OneDrive/Documents/OAEI/oracle/largebio-snomed-nci/";
		String logmap_mappings_file = path + "largebio-snomed-nci-logmap_mappings.txt";		
		String reference_mappings_file = path + "oaei_SNOMED2NCI_UMLS_mappings_with_flagged_repairs.rdf.txt";
		
		String mappings4oracle_file;
		if (reduced_set)
			mappings4oracle_file = path + "largebio-snomed-nci-logmap_mappings_to_ask_oracle_user_llm_reduced.txt";
		else
			mappings4oracle_file = path + "largebio-snomed-nci-logmap_mappings_to_ask_oracle_user_llm.txt";
			
		logmap_mappings = loadMappings(logmap_mappings_file);
		reference_mappings = loadMappings(reference_mappings_file);
		mappings4oracle = loadMappings(mappings4oracle_file);
		
		CalculateMetrics();		
		
	}
	
	
	
	
	
	private void CalculateMetrics() {
		
		int tp = 0;
		int fp = 0;
		int fn = 0;
		int tn = 0;
		
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
				else 
					tn++;
			}						
			
		}
		
		System.out.println(tp + "\t" + fp + "\t" + fn + "\t" + tn);

		
		double precision = (double)tp/(double)(tp+fp);
		double recall = (double)tp/(double)(tp+fn);  //sensitivity
		double f_score = (2*precision*recall)/(precision + recall);
		
		
		
		double specificity = (double)tn/(double)(tn+fp);
		
		
		System.out.println("precision" + "\t" + "recall" + "\t" + "f_score" + "\t" + "specificity");
		System.out.println(precision + "\t" + recall + "\t" + f_score + "\t" + specificity);
	}
	
	
	
	
	public static void main(String[] args){
		
		PerformanceLogMapAutomaticDecisions preformance_logmap_automatic = new PerformanceLogMapAutomaticDecisions();
		
		
		//preformance_logmap_automatic.TestAnatomy();
		
		//preformance_logmap_automatic.TestNCIT2DOID();
		//preformance_logmap_automatic.TestOMIM2ORDO();
		
		//preformance_logmap_automatic.TestSNOMED2FMA_BODY(); //TODO
		
		//preformance_logmap_automatic.TestSNOMED2NCIT_NEO();
		//preformance_logmap_automatic.TestSNOMED2NCIT_PHARM();
		
		
		//TODO
		//preformance_logmap_automatic.TestFMA2NCI();
		//preformance_logmap_automatic.TestFMA2SNOMED();
		preformance_logmap_automatic.TestSNOMED2NCI();
		
		
	}
	

}

package uk.ac.ox.krr.logmap2.test.oaei.interactivity;

import java.util.Set;

import uk.ac.ox.krr.logmap2.mappings.objects.MappingObjectStr;
import uk.ac.ox.krr.logmap2.oaei.reader.MappingsReaderManager;

/**
 * This class checks tthe number of correcnt (P) and incorrect (N) mappings in mappings to ask.
 * Created in May 2025
 *  
 */
public class CorrectnessMappingsToAskOracle {	
	Set<MappingObjectStr> logmap_mappings;
	Set<MappingObjectStr> reference_mappings;
	Set<MappingObjectStr> mappings4oracle;
	
	boolean reduced_set = true;
	
	
	String base_path = "/home/ernesto/Documents/OAEI/oracle/";
	String task;
	
	public CorrectnessMappingsToAskOracle() {
		
	}

	
	private Set<MappingObjectStr> loadMappings(String file_mappings){
		MappingsReaderManager manager = new MappingsReaderManager(file_mappings, MappingsReaderManager.FlatFormat);
		
		return manager.getMappingObjects();
	}
	
	
	
	public void TestAnatomy() {
		
		task = "anatomy";
		String path = base_path + task + "/";
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
		
		task = "bioml-ncit-doid";
		String path = base_path + task + "/";
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
		
		task = "bioml-omim-ordo";
		String path = base_path + task + "/";
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
		
		task = "bioml-snomed-fma.body";
		String path = base_path + task + "/";
		String logmap_mappings_file = path + "bioml-snomed-fma-body-logmap_mappings.txt";
		String reference_mappings_file = path + "snomed-fma.body-ref.txt";
		
		String mappings4oracle_file;
		if (reduced_set)
			mappings4oracle_file = path + "bioml-snomed-fma-body-logmap_mappings_to_ask_oracle_user_llm_reduced.txt";
		else
			mappings4oracle_file = path + "bioml-snomed-fma-body-logmap_mappings_to_ask_oracle_user_llm.txt";
		
		logmap_mappings = loadMappings(logmap_mappings_file);
		reference_mappings = loadMappings(reference_mappings_file);
		mappings4oracle = loadMappings(mappings4oracle_file);
		
		CalculateMetrics();		
		
	}
	
	
	public void TestSNOMED2NCIT_NEO() {
		
		task = "bioml-snomed-ncit.neoplas";
		String path = base_path + task + "/";
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
		
		task = "bioml-snomed-ncit.pharm";
		String path = base_path + task + "/";
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
		
		task = "largebio-fma-nci";
		String path = base_path + task + "/";
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
		
		task = "largebio-fma-snomed";
		String path = base_path + task + "/";
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
		
		task = "largebio-snomed-nci";
		String path = base_path + task + "/";
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
		
		//Real positves, real negatives
		int p = 0;
		int n = 0;
		
		for (MappingObjectStr mapping : mappings4oracle) {
			
			if (reference_mappings.contains(mapping))
				p++;
			else 
				n++;
		}						
		
		
		System.out.println(task + "\t" + p + "\t" + n);

	}
	
	
	
	
	public static void main(String[] args){
		
		CorrectnessMappingsToAskOracle correctness_check_mappings_to_ask = new CorrectnessMappingsToAskOracle();
		
		
		correctness_check_mappings_to_ask.TestAnatomy();
		
		correctness_check_mappings_to_ask.TestNCIT2DOID();
		correctness_check_mappings_to_ask.TestOMIM2ORDO();
		
		correctness_check_mappings_to_ask.TestSNOMED2FMA_BODY(); //TODO
		
		correctness_check_mappings_to_ask.TestSNOMED2NCIT_NEO();
		correctness_check_mappings_to_ask.TestSNOMED2NCIT_PHARM();
		
		
		correctness_check_mappings_to_ask.TestFMA2NCI();
		correctness_check_mappings_to_ask.TestFMA2SNOMED();
		correctness_check_mappings_to_ask.TestSNOMED2NCI();
		
		
	}
	

}

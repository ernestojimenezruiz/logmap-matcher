package uk.ac.ox.krr.logmap2.test.oaei.interactivity;

import uk.ac.ox.krr.logmap2.test.oaei.OAEITask;


public class TestLargeBio_SNOMED_NCI_WithLocalOracle extends TestOAEITrackWithOracle{

	protected void setUp(boolean extended_questions, boolean llm_oracle, int error_rate) {
		
		

		setIputOutputFiles("largebio/snomed-nci", "largebio-snomed-nci",  extended_questions, llm_oracle, error_rate);
		SAVE_MAPPINGS = false; //overrides if saving mappings	 

		
		tasks.add(
				new OAEITask(
						//URI_PATH + "oaei_SNOMED_small_overlapping_nci.owl",  //source
						URI_PATH + "oaei_SNOMED_extended_overlapping_fma_nci.owl",
						//URI_PATH + "oaei_NCI_small_overlapping_snomed.owl",  //target
						URI_PATH + "oaei_NCI_whole_ontology.owl",
						URI_PATH + "oaei_SNOMED2NCI_UMLS_mappings_with_flagged_repairs.rdf",   //reference mappings if any
						//"", //no reference
						"largebio-snomed-nci"
				));
		
	}
	
	
	public static void main(String[] args){
		
		boolean extended_questions = false;
		boolean llm_oracle = false;
		int min_err = 0;
		int max_err = 30;
		
		
		for (int error_rate=min_err; error_rate<=max_err; error_rate+=10) {
			
			System.out.println("Error rate: "+ error_rate);
		
		
			TestLargeBio_SNOMED_NCI_WithLocalOracle test = new TestLargeBio_SNOMED_NCI_WithLocalOracle();
			
			
			try {
				test.setUp(extended_questions, llm_oracle, error_rate);
				test.evaluateTasks();
				
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			System.out.println("");
		}
		
		
	}


}

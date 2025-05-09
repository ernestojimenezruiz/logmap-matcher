package uk.ac.ox.krr.logmap2.test.oaei.interactivity;

import uk.ac.ox.krr.logmap2.test.oaei.OAEITask;

public class TestLargeBio_FMA_SNOMED_WithLocalOracle extends TestOAEITrackWithOracle{

	protected void setUp(boolean extended_questions, boolean llm_oracle, int error_rate) {
				

		setIputOutputFiles("largebio/fma-snomed", "largebio-fma-snomed",  extended_questions, llm_oracle, error_rate);
		
		//SAVE_MAPPINGS = false; //overrides if saving mappings	 

		
		tasks.add(
				new OAEITask(
						//URI_PATH + "oaei_FMA_small_overlapping_snomed.owl",  //source
						URI_PATH + "oaei_FMA_whole_ontology.owl",
						//URI_PATH + "oaei_SNOMED_small_overlapping_fma.owl",  //target
						URI_PATH + "oaei_SNOMED_extended_overlapping_fma_nci.owl",
						URI_PATH + "oaei_FMA2SNOMED_UMLS_mappings_with_flagged_repairs.rdf",   //reference mappings if any
						//"", //no reference
						"largebio-fma-snomed"
				));
		
	}
	
	
	public static void main(String[] args){
		
		
		boolean extended_questions = false;
		boolean llm_oracle = true;
		int min_err = 0;
		int max_err = 0;
		
		
		for (int error_rate=min_err; error_rate<=max_err; error_rate+=10) {
			
			System.out.println("Error rate: "+ error_rate);
		
			TestLargeBio_FMA_SNOMED_WithLocalOracle test = new TestLargeBio_FMA_SNOMED_WithLocalOracle();
			
			
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

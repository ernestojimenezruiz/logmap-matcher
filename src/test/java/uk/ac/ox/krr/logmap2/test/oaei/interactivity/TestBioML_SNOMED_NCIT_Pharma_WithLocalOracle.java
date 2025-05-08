package uk.ac.ox.krr.logmap2.test.oaei.interactivity;

import uk.ac.ox.krr.logmap2.test.oaei.OAEITask;

public class TestBioML_SNOMED_NCIT_Pharma_WithLocalOracle extends TestOAEITrackWithOracle{

	protected void setUp(boolean extended_questions, boolean llm_oracle, int error_rate) {

		setIputOutputFiles("bio-ml/snomed-ncit.pharm", "bioml-snomed-ncit.pharm",  extended_questions, llm_oracle, error_rate);
		
		SAVE_MAPPINGS = false; //overrides if saving mappings

		
		
		tasks.add(
				new OAEITask(
						URI_PATH + "snomed.pharm.owl",  //source
						URI_PATH + "ncit.pharm.owl",  //target
						//URI_PATH + "reference.rdf",   //reference mappings if any
						URI_PATH + "refs_equiv/snomed-ncit.pharm-ref.rdf", //no reference
						"bioml-snomed-ncit-pharm"
				));
		
	}
	
	
	public static void main(String[] args){
		
		boolean extended_questions = false;
		boolean llm_oracle = false;
		int min_err = 0;
		int max_err = 30;
		
		
		for (int error_rate=min_err; error_rate<=max_err; error_rate+=10) {
			
			System.out.println("Error rate: "+ error_rate);
		
			TestBioML_SNOMED_NCIT_Pharma_WithLocalOracle test = new TestBioML_SNOMED_NCIT_Pharma_WithLocalOracle();
			
			
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

package uk.ac.ox.krr.logmap2.test.oaei.interactivity;

import uk.ac.ox.krr.logmap2.test.oaei.OAEITask;

public class TestBioML_NCIT_DOID_WithLocalOracle extends TestOAEITrackWithOracle{

	
	protected void setUp(boolean extended_questions, boolean llm_oracle, int error_rate) {
		
		
		setIputOutputFiles("bio-ml/ncit-doid", "bioml-ncit-doid",  extended_questions, llm_oracle, error_rate);
		//SAVE_MAPPINGS = false; //overrides if saving mappings

		
		
		tasks.add(
				new OAEITask(
						URI_PATH + "ncit.owl",  //source
						URI_PATH + "doid.owl",  //target
						//uri_path + "reference.rdf",   //reference mappings if any
						URI_PATH + "refs_equiv/ncit-doid-ref.rdf", //no reference
						"bioml-ncit-doid"
				));
		
	}
	
	
	public static void main(String[] args){
		
		
		boolean extended_questions = true;
		boolean llm_oracle = true;
		int min_err = 0;
		int max_err = 0;
		
		
		for (int error_rate=min_err; error_rate<=max_err; error_rate+=10) {
			
			System.out.println("Error rate: "+ error_rate);
			
			TestBioML_NCIT_DOID_WithLocalOracle test = new TestBioML_NCIT_DOID_WithLocalOracle();
			
			
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

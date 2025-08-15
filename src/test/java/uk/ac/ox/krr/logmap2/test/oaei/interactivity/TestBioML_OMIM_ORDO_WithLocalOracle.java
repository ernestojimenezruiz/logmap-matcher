package uk.ac.ox.krr.logmap2.test.oaei.interactivity;


import uk.ac.ox.krr.logmap2.test.oaei.OAEITask;

public class TestBioML_OMIM_ORDO_WithLocalOracle extends TestOAEITrackWithOracle{
	
	

	protected void setUp(boolean extended_questions, boolean llm_oracle, int error_rate) {
		
		
		
		setIputOutputFiles("bio-ml/omim-ordo", "bioml-omim-ordo",  extended_questions, llm_oracle, error_rate);		
		//SAVE_MAPPINGS = false; //overrides if saving mappings	 
				
		tasks.add(
				new OAEITask(
						URI_PATH + "omim.owl",  //source
						URI_PATH + "ordo.owl",  //target
						//uri_path + "reference.rdf",   //reference mappings if any
						URI_PATH + "refs_equiv/omim-ordo-ref.rdf", //no reference
						"bioml-omim-ordo"
				));
		/*tasks.add(
				new OAEITask(
						uri_path + "flat_ontology_food_terms_based_on_piyathilake_dataset.ttl",  //source
						uri_path + "foodon-merged.owl",  //target
						//uri_path + "AML-food.rdf",   //reference mappings if any
						"", //no reference
						"food-terms"
				));
		*/
	}
	
	
	public static void main(String[] args){
		
		boolean extended_questions = false;
		boolean llm_oracle = true;
		int min_err = 0;
		int max_err = 0;
		
		
		for (int error_rate=min_err; error_rate<=max_err; error_rate+=10) {
			
			System.out.println("Error rate: "+ error_rate);
		
			TestBioML_OMIM_ORDO_WithLocalOracle test = new TestBioML_OMIM_ORDO_WithLocalOracle();
			
			
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

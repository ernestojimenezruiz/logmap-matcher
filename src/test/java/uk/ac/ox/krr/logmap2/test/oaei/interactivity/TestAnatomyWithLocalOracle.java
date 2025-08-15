package uk.ac.ox.krr.logmap2.test.oaei.interactivity;

import uk.ac.ox.krr.logmap2.test.oaei.OAEITask;


public class TestAnatomyWithLocalOracle extends TestOAEITrackWithOracle{
	
	public TestAnatomyWithLocalOracle(){
		super();
	}

	
	protected void setUp(boolean extended_questions, boolean llm_oracle, int error_rate) {
		

		setIputOutputFiles("anatomy", "anatomy", extended_questions, llm_oracle, error_rate);
		
		//SAVE_MAPPINGS = false; //overrides if saving mappings
		
		
		tasks.add(
				new OAEITask(
						URI_PATH + "mouse.owl", //source
						URI_PATH + "human.owl",   //target
						URI_PATH + "reference.rdf",   //reference mappings if any						
						"anatomy"
				));
		
	}
	
	
	public static void main(String[] args){
		
		boolean extended_questions = false;		
		boolean llm_oracle = true;
		int min_err = 0;
		int max_err = 0;
		
		
		for (int err=min_err; err<=max_err; err+=10) {
			
			System.out.println("Error rate: "+ err);
		
			TestAnatomyWithLocalOracle test = new TestAnatomyWithLocalOracle();
			
			try {
				test.setUp(extended_questions, llm_oracle, err);
				test.evaluateTasks();
				
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			System.out.println("");
		}
		
		
		
		
	}



}

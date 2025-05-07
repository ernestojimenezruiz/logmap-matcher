package uk.ac.ox.krr.logmap2.test.oaei.interactivity;

import uk.ac.ox.krr.logmap2.test.oaei.OAEITask;


public class TestAnatomyWithLocalOracle extends TestOAEITrackWithOracle{
	
	public TestAnatomyWithLocalOracle(){
		super();
	}

	@Override
	protected void setUp() {
		
		boolean extended_questions = false;		
		boolean llm_oracle = false;
		int error_rate = 30;

		setIputOutputFiles("anatomy", "anatomy", extended_questions, llm_oracle, error_rate);
		
		SAVE_MAPPINGS = false; //overrides if saving mappings
		
		
		tasks.add(
				new OAEITask(
						URI_PATH + "mouse.owl", //source
						URI_PATH + "human.owl",   //target
						URI_PATH + "reference.rdf",   //reference mappings if any						
						"anatomy"
				));
		
	}
	
	
	public static void main(String[] args){
		
		TestAnatomyWithLocalOracle test = new TestAnatomyWithLocalOracle();
		
		
		try {
			test.evaluateTasks();
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}



}

package uk.ac.ox.krr.logmap2.test.oaei.interactivity;

import uk.ac.ox.krr.logmap2.test.oaei.OAEITask;


public class TestAnatomyWithLocalOracle extends TestOAEITrackWithOracle{
	
	public TestAnatomyWithLocalOracle(){
		super();
	}

	@Override
	protected void setUp() {
		
		boolean extended_questions = true;		

		setIputOutputFiles("anatomy", "anatomy", extended_questions);		
		
		
		
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

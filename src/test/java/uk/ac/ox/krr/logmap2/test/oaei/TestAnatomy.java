package uk.ac.ox.krr.logmap2.test.oaei;

import uk.ac.ox.krr.logmap2.Parameters;

public class TestAnatomy extends TestOAEITrack{
	
	public TestAnatomy(){
		super();
	}

	@Override
	protected void setUp() {
		
		Parameters.readParameters();
		
		SAVE_MAPPINGS = true; 
		//OUTPUT_FILE_TEMPLATE: will generate files logmap-alignment-food.owl, logmap-alignment-food.txt
		//PATH = "/home/ernesto/Documents/Datasets/Food/logmap-alignment"; +  + "-"+task.getTaskName();
		PATH = "C:/Users/Ernes/OneDrive/Documents/OAEI/anatomy-dataset/";
		
		String uri_path = "file:" + PATH;
		 
		
		tasks.add(
				new OAEITask(
						uri_path + "mouse.owl", //source
						uri_path + "human.owl",   //target
						uri_path + "reference.rdf",   //reference mappings if any
						//"", //no reference
						"anatomy"
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
		
		TestAnatomy test = new TestAnatomy();
		
		
		try {
			test.evaluateTasks();
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}



}

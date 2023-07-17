package uk.ac.ox.krr.logmap2.test.oaei;

public class TestFoodOntologies extends TestOAEITrack{

	@Override
	protected void setUp() {
		
		SAVE_MAPPINGS = true; 
		//OUTPUT_FILE_TEMPLATE: will generate files logmap-alignment-food.owl, logmap-alignment-food.txt
		//PATH = "/home/ernesto/Documents/Datasets/Food/logmap-alignment"; +  + "-"+task.getTaskName();
		PATH = "/home/ernesto/Documents/Datasets/Food/";
		
		String uri_path = "file:/home/ernesto/Documents/Datasets/Food/";
		
		
		tasks.add(
				new OAEITask(
						uri_path + "helis_v1.00.owl",  //source
						uri_path + "foodon-merged.owl",  //target
						uri_path + "AML-food.rdf",   //reference mappings if any
						//"", //no reference
						"food-ontos"
				));
		tasks.add(
				new OAEITask(
						uri_path + "flat_ontology_food_terms_based_on_piyathilake_dataset.ttl",  //source
						uri_path + "foodon-merged.owl",  //target
						//uri_path + "AML-food.rdf",   //reference mappings if any
						"", //no reference
						"food-terms"
				));
		
	}
	
	
	public static void main(String[] args){
		
		TestFoodOntologies test = new TestFoodOntologies();
		
		try {
			test.evaluateTasks();
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}



}

package uk.ac.ox.krr.logmap2.test.oaei;

public class TestBioML_OMIM_ORDO extends TestOAEITrack{

	@Override
	protected void setUp() {
		
		SAVE_MAPPINGS = true; 
		//OUTPUT_FILE_TEMPLATE: will generate files logmap-alignment-food.owl, logmap-alignment-food.txt
		//PATH = "/home/ernesto/Documents/Datasets/Food/logmap-alignment"; +  + "-"+task.getTaskName();
		PATH = "C:/Users/Ernes/OneDrive/Documents/OAEI/bio-ml/omim-ordo/logmap/";  //path must exist
		
			 
		String path_task = "C:/Users/Ernes/OneDrive/Documents/OAEI/bio-ml/omim-ordo/";
		String uri_path = "file:/" + path_task;
		
		tasks.add(
				new OAEITask(
						uri_path + "omim.owl",  //source
						uri_path + "ordo.owl",  //target
						//uri_path + "reference.rdf",   //reference mappings if any
						"", //no reference
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
		
		TestBioML_OMIM_ORDO test = new TestBioML_OMIM_ORDO();
		
		
		try {
			test.evaluateTasks();
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}


}

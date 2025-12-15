package uk.ac.ox.krr.logmap2.test.oaei;

public class TestBioML_NCIT_DOID extends TestOAEITrack{

	@Override
	protected void setUp() {
		
		SAVE_MAPPINGS = true; 
		//OUTPUT_FILE_TEMPLATE: will generate files logmap-alignment-food.owl, logmap-alignment-food.txt
		//PATH = "/home/ernesto/Documents/Datasets/Food/logmap-alignment"; +  + "-"+task.getTaskName();
		//PATH = "C:/Users/Ernes/OneDrive/Documents/OAEI/bio-ml/ncit-doid/logmap/";
		
		//String base_path = "C:/Users/Ernes/OneDrive/Documents/OAEI/";
		String base_path = "C:/Users/sbrn854/Documents/OAEI/";
		
		
		String dataset = "bio-ml/ncit-doid/";

		String path_task = base_path + dataset; 
		String uri_path = "file:" + path_task;
		
		
		PATH = path_task + "/logmap/";  //for output mappings (folder must exist)
		
				
		tasks.add(
				new OAEITask(
						uri_path + "ncit.owl",  //source
						uri_path + "doid.owl",  //target
						//uri_path + "reference.rdf",   //reference mappings if any
						uri_path + "refs_equiv/ncit-doid-ref.rdf", //no reference
						"bioml-ncit-doid"
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
		
		TestBioML_NCIT_DOID test = new TestBioML_NCIT_DOID();
		
		
		try {
			test.evaluateTasks();
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}


}

package uk.ac.ox.krr.logmap2.test.oaei;

public class TestPizza  extends TestOAEITrack{

	@Override
	protected void setUp() {
		
		
	
		SAVE_MAPPINGS = true;
		//OUTPUT_FILE_TEMPLATE: will generate files logmap-alignment-food.owl, logmap-alignment-food.txt
		//PATH = "/home/ernesto/Documents/Datasets/Food/logmap-alignment"; +  + "-"+task.getTaskName();
		PATH = "/home/ernesto/Documents/City_Teaching/2022-2023/";
		
		String uri_path = "file:" + PATH;
		
		tasks.add(
				new OAEITask(
						uri_path + "pizza-restaurants-ontology.owl",  //source
						uri_path + "pizza.owl",  //target
						//uri_path + "ref.rdf",   //reference mappings if any
						"", //no reference
						"pizza"
				));
		
	}
	
	
	public static void main(String[] args){
		
		TestPizza test = new TestPizza();
		
		try {
			test.evaluateTasks();
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}



}
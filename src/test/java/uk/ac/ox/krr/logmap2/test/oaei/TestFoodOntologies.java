package uk.ac.ox.krr.logmap2.test.oaei;

public class TestFoodOntologies extends TestOAEITrack{

	@Override
	protected void setUp() {
		SAVE_MAPPINGS = true;
		//OUTPUT_FILE_TEMPLATE
		PATH = "/home/ernesto/Documents/Datasets/Food/logmap-alignment";
		
		String uri_path = "file:/home/ernesto/Documents/Datasets/Food/";
		
		tasks.add(
				new OAEITask(
						uri_path + "foodon-helis.owl",
						uri_path + "foodon-merged.owl",
						uri_path + "empty.rdf",
						"food"
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

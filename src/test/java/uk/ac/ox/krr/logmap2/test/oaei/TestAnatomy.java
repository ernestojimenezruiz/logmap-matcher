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
		
		//String base_path = "/home/ernesto/Documents/OAEI/";
		//String base_path = "C:/Users/Ernes/OneDrive/Documents/OAEI/";
		String base_path = "C:/Users/sbrn854/Documents/OAEI/";
		
							
		String dataset = "anatomy-dataset/";

		String path_task = base_path + dataset; 
		String uri_path = "file:" + path_task;
		
		
		PATH = path_task + "/logmap/";  //for output mappings (folder must exist)
		
		tasks.add(
				new OAEITask(
						uri_path + "mouse.owl", //source
						uri_path + "human.owl",   //target
						uri_path + "reference.rdf",   //reference mappings if any
						//"", //no reference
						"anatomy"
				));
		
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

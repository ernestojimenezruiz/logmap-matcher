package uk.ac.ox.krr.logmap2.test.oaei;

public class TestBioML_SNOMED_NCIT_Neoplas extends TestOAEITrack{

	@Override
	protected void setUp() {
		
		SAVE_MAPPINGS = true; 
		//OUTPUT_FILE_TEMPLATE: will generate files logmap-alignment-food.owl, logmap-alignment-food.txt
		//PATH = "/home/ernesto/Documents/Datasets/Food/logmap-alignment"; +  + "-"+task.getTaskName();
		//PATH = "C:/Users/Ernes/OneDrive/Documents/OAEI/bio-ml/snomed-ncit.neoplas/logmap/";  //path must exist
		
		
		//String base_path = "C:/Users/Ernes/OneDrive/Documents/OAEI/";
		String base_path = "C:/Users/sbrn854/Documents/OAEI/";
						
		
		String dataset = "bio-ml/snomed-ncit.neoplas/";

		String path_task = base_path + dataset; 
		String uri_path = "file:" + path_task;
						
		
		PATH = path_task + "/logmap/";  //for output mappings (folder must exist)
							

		
			 
		//String path_task = "C:/Users/Ernes/OneDrive/Documents/OAEI/bio-ml/snomed-ncit.neoplas/";
		//String uri_path = "file:/" + path_task;
		
		tasks.add(
				new OAEITask(
						uri_path + "snomed.neoplas.owl",  //source
						uri_path + "ncit.neoplas.owl",  //target
						//uri_path + "reference.rdf",   //reference mappings if any
						uri_path + "refs_equiv/snomed-ncit.neoplas-ref.rdf", //no reference
						"bioml-snomed-ncit-neoplas"
				));
		
	}
	
	
	public static void main(String[] args){
		
		TestBioML_SNOMED_NCIT_Neoplas test = new TestBioML_SNOMED_NCIT_Neoplas();
		
		
		try {
			test.evaluateTasks();
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}


}

package uk.ac.ox.krr.logmap2.test.oaei;

public class TestBioML_SNOMED_NCIT_Pharma extends TestOAEITrack{

	@Override
	protected void setUp() {
		
		SAVE_MAPPINGS = true; 
		//OUTPUT_FILE_TEMPLATE: will generate files logmap-alignment-food.owl, logmap-alignment-food.txt
		//PATH = "/home/ernesto/Documents/Datasets/Food/logmap-alignment"; +  + "-"+task.getTaskName();
		PATH = "C:/Users/Ernes/OneDrive/Documents/OAEI/bio-ml/snomed-ncit.pharm/logmap/";  //path must exist
		
			 
		String path_task = "C:/Users/Ernes/OneDrive/Documents/OAEI/bio-ml/snomed-ncit.pharm/";
		String uri_path = "file:/" + path_task;
		
		tasks.add(
				new OAEITask(
						uri_path + "snomed.pharm.owl",  //source
						uri_path + "ncit.pharm.owl",  //target
						//uri_path + "reference.rdf",   //reference mappings if any
						uri_path + "refs_equiv/snomed-ncit.pharm-ref.rdf", //no reference
						"bioml-snomed-ncit-pharm"
				));
		
	}
	
	
	public static void main(String[] args){
		
		TestBioML_SNOMED_NCIT_Pharma test = new TestBioML_SNOMED_NCIT_Pharma();
		
		
		try {
			test.evaluateTasks();
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}


}

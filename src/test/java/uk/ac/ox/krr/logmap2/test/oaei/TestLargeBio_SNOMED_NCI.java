package uk.ac.ox.krr.logmap2.test.oaei;


public class TestLargeBio_SNOMED_NCI extends TestOAEITrack{

	@Override
	protected void setUp() {
		
		SAVE_MAPPINGS = true;
		
		//PATH = "C:/Users/Ernes/OneDrive/Documents/OAEI/largebio/snomed-nci/logmap/";  //path must exist
		
		
		//String base_path = "C:/Users/Ernes/OneDrive/Documents/OAEI/";
		String base_path = "C:/Users/sbrn854/Documents/OAEI/";
						
		
		String dataset = "largebio/snomed-nci/";

		String path_task = base_path + dataset; 
		String uri_path = "file:" + path_task;
						
		
		PATH = path_task + "/logmap/";  //for output mappings (folder must exist)	

		
			 
		//String path_task = "C:/Users/Ernes/OneDrive/Documents/OAEI/largebio/snomed-nci/";
		//String uri_path = "file:/" + path_task;
		
		
		
		//TODO: modify back the interactive bit? allow_interactivity|true or the oracle set up to true?
		tasks.add(
				new OAEITask(
						//uri_path + "oaei_SNOMED_small_overlapping_nci.owl",  //source
						uri_path + "oaei_SNOMED_extended_overlapping_fma_nci.owl",
						//uri_path + "oaei_NCI_small_overlapping_snomed.owl",  //target
						uri_path + "oaei_NCI_whole_ontology.owl",
						uri_path + "oaei_SNOMED2NCI_UMLS_mappings_with_flagged_repairs.rdf",   //reference mappings if any
						//"", //no reference
						"largebio-snomed-nci"
				));
		
	}
	
	
	public static void main(String[] args){
		
		TestLargeBio_SNOMED_NCI test = new TestLargeBio_SNOMED_NCI();
		
		
		try {
			test.evaluateTasks();
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}


}

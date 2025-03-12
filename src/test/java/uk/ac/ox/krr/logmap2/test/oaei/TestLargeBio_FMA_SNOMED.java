package uk.ac.ox.krr.logmap2.test.oaei;


public class TestLargeBio_FMA_SNOMED extends TestOAEITrack{

	@Override
	protected void setUp() {
		
		SAVE_MAPPINGS = true;
		
		PATH = "C:/Users/Ernes/OneDrive/Documents/OAEI/largebio/fma-snomed/logmap/";  //path must exist
		
			 
		String path_task = "C:/Users/Ernes/OneDrive/Documents/OAEI/largebio/fma-snomed/";
		String uri_path = "file:/" + path_task;
		
		
		
		//TODO: modify back the interactive bit? allow_interactivity|true or the oracle set up to true?
		tasks.add(
				new OAEITask(
						uri_path + "oaei_FMA_small_overlapping_snomed.owl",  //source
						uri_path + "oaei_SNOMED_small_overlapping_fma.owl",  //target
						uri_path + "oaei_FMA2SNOMED_UMLS_mappings_with_flagged_repairs.rdf",   //reference mappings if any
						//"", //no reference
						"largebio-fma-snomed"
				));
		
	}
	
	
	public static void main(String[] args){
		
		TestLargeBio_FMA_SNOMED test = new TestLargeBio_FMA_SNOMED();
		
		
		try {
			test.evaluateTasks();
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}


}

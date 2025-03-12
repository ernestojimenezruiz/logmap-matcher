package uk.ac.ox.krr.logmap2.test.oaei;


public class TestBioML_SNOMED_FMA_Body extends TestOAEITrack{

	@Override
	protected void setUp() {
		
		SAVE_MAPPINGS = true;
		
		PATH = "C:/Users/Ernes/OneDrive/Documents/OAEI/bio-ml/snomed-fma.body/logmap/";  //path must exist
		
			 
		String path_task = "C:/Users/Ernes/OneDrive/Documents/OAEI/bio-ml/snomed-fma.body/";
		String uri_path = "file:/" + path_task;
		
		tasks.add(
				new OAEITask(
						uri_path + "snomed.body.owl",  //source
						uri_path + "fma.body.owl",  //target
						//uri_path + "reference.rdf",   //reference mappings if any
						"", //no reference
						"bioml-snomed-fma-body"
				));
		
	}
	
	
	public static void main(String[] args){
		
		TestBioML_SNOMED_FMA_Body test = new TestBioML_SNOMED_FMA_Body();
		
		
		try {
			test.evaluateTasks();
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}


}

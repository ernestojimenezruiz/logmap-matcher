package uk.ac.ox.krr.logmap2.test.oaei;

import java.io.File;



public class TestConferenceTrack extends TestOAEITrack{
	
		
	@Override
	protected void setUp() {
		
		String base_path = "C:/Users/Ernes/OneDrive/Documents/OAEI/conference/";
		String uri_base_path = "file:/" + base_path;
		String iri_path_ontologies = uri_base_path + "ontologies/";		
		String pattern=".rdf";
		
				
		File directory = new File(base_path);
		String filenames[] = directory.list();
		
		String[] elements;
		
		
		SAVE_MAPPINGS = true; 
		PATH = base_path + "logmap-conference-oaei/";  //for output mappings (folder must exist)
		
		
		for(int i=0; i<filenames.length; i++){
			
			if (!filenames[i].contains(pattern)) 
				continue;
			
			//System.out.println(filenames[i]);
			
			elements = filenames[i].split("-|\\.");
			
			String onto1 = iri_path_ontologies + elements[0] + ".owl";
			String onto2 = iri_path_ontologies + elements[1] + ".owl";
			
			tasks.add(
					new OAEITask(
							onto1,  //source
							onto2,  //target
							uri_base_path + filenames[i],
							"conference-oaei-" + elements[0] + "-" + elements[1]
					));
			

		}
		
	}
		
	

	

	
	
	public static void main(String[] args) {
		
		try {
			TestConferenceTrack test = new TestConferenceTrack();
			test.evaluateTasks();
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
			
	
	
}

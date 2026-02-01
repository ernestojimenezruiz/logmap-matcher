package uk.ac.ox.krr.logmap2.test.oaei;

import java.io.File;

public class Tes_DISO_guard  extends TestOAEITrackEnhanced{

	
	@Override
	protected void setUp() {
		//Task specific details
		String base_path = "C:/Users/Ernes/OneDrive/Documents/OAEI/diso/";
		String uri_base_path = "file:/" + base_path;
				
		String base_path_output_mappings = base_path + "logmap-diso-oaei/";  //for output mappings (if folder does not exist if will be created)
		//end task specific
				
						
		File directory = new File(base_path);
		String filenames[] = directory.list();
				
						
				 
					
		setExtendedQuestions4LLM(false);
		//setExtendedQuestions4LLM(true);
		setBasePathForOutputMappings(base_path_output_mappings); //partial path, the task name will be added as an extra folder.
		setPathToLogMapParameters("");  //default location
		
		//TODO if oracle available
		//setMappingsLocalOracle(mappings_local_oracle);
		//or
		//setMappingsLocalOracle(base_path_output_mappings);
			
		
		for(int i=0; i<filenames.length; i++){ //all against all
		//for(int i=0; i<filenames.length-1; i++){ //different ontos
		//for(int i=filenames.length-1; i>0; i--){ //different ontos reversed
			
			if (!filenames[i].contains(".owl") && !filenames[i].contains(".rdf") && !filenames[i].contains(".ttl")) 
				continue;
			
			for(int j=0; j<filenames.length; j++){ //all against all
			//for(int j=i+1; j<filenames.length; j++){ //different ontos
			//for(int j=i-1; j>=0; j--){ //different ontos reversed
				
				if (!filenames[j].contains(".owl") && !filenames[j].contains(".rdf") && !filenames[j].contains(".ttl")) 
					continue;
				
				
				
				System.out.println(filenames[i] + " - "  + filenames[j]);
				
				String onto1 = uri_base_path + filenames[i]; 
				String onto2  = uri_base_path + filenames[j];
				
				//System.out.println(onto1 + " - " + onto2);
				
				tasks.add(
						new OAEITask(
								onto1,  //source
								onto2,  //target
								"",
								filenames[i] + "-" + filenames[j]
						));
				
				
			}
			
					
		}
			
	}
	
	
	public static void main(String[] args) {
		
		try {
			Tes_DISO_guard test = new Tes_DISO_guard();
			test.evaluateTasks();
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
			
	
	

}

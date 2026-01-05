package uk.ac.ox.krr.logmap2.test.oaei;

import java.io.File;

public class TestOAEIKGTrack extends TestOAEITrackEnhanced{

	public TestOAEIKGTrack(){
		super();
	}

	@Override
	protected void setUp() {
		

		//Task specific details		
		String base_path = "C:/Users/Ernes/OneDrive/Documents/OAEI/knowledge-graph/";
		//String base_path = "C:/Users/sbrn854/Documents/OAEI/anatomy-dataset/";
		String uri_base_path = "file:/" + base_path;		
		
		String iri_path_ontologies = uri_base_path + "ontologies/";		
		String pattern=".rdf";		
		String base_path_output_mappings = base_path + "logmap-mappings-kg/";  //for output mappings (if folder does not exist if will be created)
		//end task specific
				
						
		
		//setExtendedQuestions4LLM(false);
		setExtendedQuestions4LLM(true);
		setBasePathForOutputMappings(base_path_output_mappings); //partial path, the task name will be added as an extra folder.
		setPathToLogMapParameters("");  //default location
		
		
		
		//TODO if oracle available
		//setMappingsLocalOracle(mappings_local_oracle);
		//or
		//setMappingsLocalOracle(base_path_output_mappings); //Reads any .csv file with the right format in the given folder
		
		
		
		File directory = new File(base_path);
		String filenames[] = directory.list();
		
		String[] elements;
		
	
		//Tasks:
		//starwars-swg
		//starwars-swtor
		//marvelcinematicuniverse-marvel
		//memoryalpha-memorybeta
		//memoryalpha-stexpanded
		
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
							elements[0] + "-" + elements[1]
					));
			

		}
		
	}
	
	
	public static void main(String[] args){
		
		TestOAEIKGTrack test = new TestOAEIKGTrack();
		
		
		try {
			test.evaluateTasks();
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}



}

package uk.ac.ox.krr.logmap2.test.oaei;

public class TestAnatomyEnhanced extends TestOAEITrackEnhanced{
	
	public TestAnatomyEnhanced(){
		super();
	}

	@Override
	protected void setUp() {
		

		//Task specific details		
		String base_path = "C:/Users/Ernes/OneDrive/Documents/OAEI/anatomy/";
		//String base_path = "C:/Users/sbrn854/Documents/OAEI/anatomy-dataset/";
		String uri_base_path = "file:/" + base_path;		
		String base_path_output_mappings = base_path + "logmap-anatomy-oaei/";  //for output mappings (if folder does not exist if will be created)
		//end task specific
				
						
		
		setExtendedQuestions4LLM(false);
		//setExtendedQuestions4LLM(true);
		setBasePathForOutputMappings(base_path_output_mappings); //partial path, the task name will be added as an extra folder.
		setPathToLogMapParameters("");  //default location

		
		//Reads any .csv file with the right format in the given folder
		String PATH_TO_ORACLE = "C:/Users/Ernes/OneDrive/Documents/OAEI/oracle/anatomy/"; //Will look for an available csv file
		setMappingsLocalOracle(PATH_TO_ORACLE);
		
		
		tasks.add(
				new OAEITask(
						uri_base_path + "mouse.owl", //source
						uri_base_path + "human.owl",   //target
						uri_base_path + "reference.rdf",   //reference mappings if any
						//"", //no reference
						"anatomy"
				));
		
	}
	
	
	public static void main(String[] args){
		
		TestAnatomyEnhanced test = new TestAnatomyEnhanced();
		
		
		try {
			test.evaluateTasks();
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}



}

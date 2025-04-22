package uk.ac.ox.krr.logmap2.test.oaei.interactivity;

import uk.ac.ox.krr.logmap2.Parameters;
import uk.ac.ox.krr.logmap2.oaei.oracle.OracleManager;
import uk.ac.ox.krr.logmap2.test.oaei.OAEITask;
import uk.ac.ox.krr.logmap2.test.oaei.TestOAEITrack;


public class TestAnatomyWithLocalOracle extends TestOAEITrackWithOracle{
	
	public TestAnatomyWithLocalOracle(){
		super();
	}

	@Override
	protected void setUp() {
		
		Parameters.readParameters();
		
		SAVE_MAPPINGS = false;
		//SAVE_MAPPINGS = true;	
		//PATH = "C:/Users/Ernes/OneDrive/Documents/OAEI/anatomy-dataset/logmap_with_oracle/";  //for output mappings (the folder must exist)
		
		//Reads any .csv file with the right format in the given folder
		PATH_TO_ORACLE = "C:/Users/Ernes/OneDrive/Documents/OAEI/oracle/anatomy/";
		//OracleManager.setExtendedQuestions(true); //Extended set of questions to Oracle
		OracleManager.setExtendedQuestions(false);  //Reduced set of questions to Oracle
		
		
		
		String path_task = "C:/Users/Ernes/OneDrive/Documents/OAEI/anatomy-dataset/";
		String uri_path = "file:" + path_task;
		 
		
		tasks.add(
				new OAEITask(
						uri_path + "mouse.owl", //source
						uri_path + "human.owl",   //target
						uri_path + "reference.rdf",   //reference mappings if any						
						"anatomy"
				));
		
	}
	
	
	public static void main(String[] args){
		
		TestAnatomyWithLocalOracle test = new TestAnatomyWithLocalOracle();
		
		
		try {
			test.evaluateTasks();
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}



}

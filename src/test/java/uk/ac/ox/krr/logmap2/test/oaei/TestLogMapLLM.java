package uk.ac.ox.krr.logmap2.test.oaei;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Set;

import uk.ac.ox.krr.logmap2.LogMapLLM_Interface;
import uk.ac.ox.krr.logmap2.io.ReadFile;
import uk.ac.ox.krr.logmap2.mappings.objects.MappingObjectStr;

public class TestLogMapLLM {
	
	
	public TestLogMapLLM() {
		

		//Task specific details		
		String base_path = "C:/Users/Ernes/OneDrive/Documents/OAEI/anatomy/";
		//String base_path = "C:/Users/sbrn854/Documents/OAEI/anatomy-dataset/";
		String uri_base_path = "file:/" + base_path;		
		String base_path_output_mappings = base_path + "logmap-anatomy-oaei/";  //for output mappings (if folder does not exist if will be created)
		//end task specific
		
		
		LogMapLLM_Interface logmapllm = new LogMapLLM_Interface(
				uri_base_path + "mouse.owl",
				uri_base_path + "human.owl",
				"anatomy");

		
		logmapllm.setExtendedQuestions4LLM(true);
		//logmapllm.setExtendedQuestions4LLM(true);
		logmapllm.setPathForOutputMappings(base_path_output_mappings); //partial path, the task name will be added as an extra folder.
		logmapllm.setPathToLogMapParameters("");  //default location
		
		
		logmapllm.performAlignment();
		
		System.out.println("Mappings to ask: " + logmapllm.getLogMapMappingsForLLM().size());
		System.out.println("Output Mappings LogMap: " + logmapllm.getLogMapMappings().size());
		

		//		
		//Reads any .csv file with the right format in the given folder
		String PATH_TO_ORACLE = "C:/Users/Ernes/OneDrive/Documents/OAEI/oracle/anatomy/"; //Will look for an available csv file
		
		
		System.out.println("WITH LLM ORACLE");
		
		
		logmapllm.performAlignmentWithLocalOracle(
				loadLocalOraculoLLM(PATH_TO_ORACLE));
		
		
		System.out.println("Mappings asked: " + logmapllm.getLogMapMappingsForLLM().size());
		System.out.println("Output Mappings LogMapLLM: " + logmapllm.getLogMapMappings().size());
		
		
	}
	
	
	
	
	/**
	 * Load Oracle from LLM input
	 * Format: 
	 * Source,Target,Prediction,Confidence
	 * http://human.owl#NCI_C49191,http://mouse.owl#MA_0000702,False,0.9399113610439951
	 * @param base_path
	 */
	private Set<MappingObjectStr> loadLocalOraculoLLM(String base_path) {
		
		Set<MappingObjectStr> mappings_oracle = new HashSet<MappingObjectStr>();

		try {
			int countTrue=0;
			int countFalse=0;
			String pattern = ".csv";
			
			File directory = new File(base_path);
			String filenames[] = directory.list();
			
			
			for(int i=0; i<filenames.length; i++){
				
				if (!filenames[i].contains(pattern)) 
					continue;
				
				
			
				ReadFile reader = new ReadFile(base_path + filenames[i]);
				
				String line;
				String[] elements;
				
				line=reader.readLine();
				
				while (line!=null) {
					
					if (line.startsWith("#")){ //comments
						line=reader.readLine();
						continue;
					}
					
					if (line.indexOf(",")<0){
						line=reader.readLine();
						continue;
					}
					
					elements=line.split(",");
					
					//System.out.println(elements[0] + "  " + elements[1]  + "  " + elements[2]);
					
					if (Boolean.parseBoolean(elements[2].toLowerCase())) {
						
						
						mappings_oracle.add(new MappingObjectStr(elements[0], elements[1], 1.0));
					
						//addMapping2LocalOracle(elements[0], elements[1]);
						//addMapping2LocalOracle(elements[1], elements[0]);
												
						countTrue++;
					}
					else {
						countFalse++;
					}
					
					line=reader.readLine();
				}
				
				reader.closeBuffer();
				System.out.println("Num mapping in oracle (positives): " + countTrue +  "  " + mappings_oracle.size());
				System.out.println("Num mapping NOT in oracle (negatives): " + countFalse);
			}
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		
		//System.out.println("LLM-based oracle size: " + mappings_oracle.size());
		
		return mappings_oracle;
		
	}
	
	
	
	public static void main(String[] args){
		
		try {
			
			TestLogMapLLM test = new TestLogMapLLM();
		
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	

}



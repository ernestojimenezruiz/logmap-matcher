package uk.ac.ox.krr.logmap2.oaei.oracle;

import java.io.File;
import java.io.FileNotFoundException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import uk.ac.ox.krr.logmap2.io.ReadFile;
import uk.ac.ox.krr.logmap2.mappings.objects.MappingObjectStr;

/**
 * with the collaboration of Diego Jimenez Carapella
 */
public class LocalOracle extends Oraculo{
	
	
		
	private static Map<String, Set<String>> oracle_mappings = new HashMap<String, Set<String>>();
	private static int error_rate = 0;
	
	
	/**
	 * For local simulations: 0 to 100
	 */
	public static void setErrorRate(int error) {
		error_rate=error;
	}
	
	
	/**
	 * Random number between 1 and 100
	 * @return
	 */
	private static int generateRandomNumber() {
	        SecureRandom secureRandom = new SecureRandom();
	       return secureRandom.nextInt(100) + 1; // Generates a number between 1 and 100
	        //System.out.println("Random number: " + randomNumber);
	}
	
	
	
	
	@Override
	public boolean isMappingValid(String uri1, String uri2) {
		
		int random = generateRandomNumber();
		
		boolean isInOracle = isMappingInLocalOracle(uri1, uri2);
		
		if (random>error_rate) 
			return isInOracle;
		else
			return !isInOracle;  //negation
				
			
	}
	
	
	
	/**
	 * Adds mapping to local oracle
	 * @param uri1
	 * @param uri2
	 */
	private static void addMapping2LocalOracle(String uri1, String uri2){
		if (!oracle_mappings.containsKey(uri1)){
			oracle_mappings.put(uri1, new HashSet<String>());
		}
		oracle_mappings.get(uri1).add(uri2);
	}
	
	
	/**
	 * Checks if mapping is in local oracle
	 * @param uri1
	 * @param uri2
	 * @return
	 */
	private static boolean isMappingInLocalOracle(String uri1, String uri2){
		if (oracle_mappings.containsKey(uri1)){
			if (oracle_mappings.get(uri1).contains(uri2)){
				return true;
			}
		}
		return false;
	}
	
	
	
	/**
	 * Classic TXT format entiti1|entitity2|other_fields
	 * @param base_path
	 */
	public static void loadLocalOraculo(String base_path) {
		
		try {
			int count=0;			
			String pattern = "ref.txt";
			
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
					
					if (line.indexOf("|")<0){
						line=reader.readLine();
						continue;
					}
					
					elements=line.split("\\|");
					
					addMapping2LocalOracle(elements[0], elements[1]);
					addMapping2LocalOracle(elements[1], elements[0]);

					
					
					//System.out.println(elements[0] + "  " + elements[1]);
					count++;
					
					line=reader.readLine();
				}
				
				reader.closeBuffer();
				//System.out.println("Num mapping in oracle: " + count +  "  " + oracle_mappings.size());
				//System.out.println("Num mapping in oracle: " + count +  "  " + oracle_mappings.size());
				
			}
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	
	
	/**
	 * Load Oracle from LLM input CSV files
	 * The method expects a path where there may be one or many CSV files with local Oracle mappings
	 * Format of the file(s): 
	 * Source,Target,Prediction,Confidence
	 * http://human.owl#NCI_C49191,http://mouse.owl#MA_0000702,False,0.9399113610439951
	 * @param base_path
	 */
	public static void loadLocalOraculoLLM(String base_path) {
		
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
					
						addMapping2LocalOracle(elements[0], elements[1]);
						addMapping2LocalOracle(elements[1], elements[0]);
												
						countTrue++;
					}
					else {
						countFalse++;
					}
					
					line=reader.readLine();
				}
				
				reader.closeBuffer();
				//System.out.println("Num mapping in oracle: " + countTrue +  "  " + oracle_mappings.size());
				//System.out.println("Num mapping NOT in oracle: " + countFalse);
			}
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	
	public static void loadLocalOraculo(Set<MappingObjectStr> mappings_local_oracle) {
	
		//TODO in the future we may need to consider type and direction of the mapping, specially direction.
		//TODO For now we assume equivalence among URIs
		for (MappingObjectStr mapping : mappings_local_oracle) {		
			addMapping2LocalOracle(mapping.getIRIStrEnt1(), mapping.getIRIStrEnt2());
			addMapping2LocalOracle(mapping.getIRIStrEnt2(), mapping.getIRIStrEnt1());
		}
		
	}
	
	

	@Override
	public boolean isActive() {
		return true;
	}
	
	
	
	/**
	 * Resets local oracle in case it is used several times in multiple runs with different mappings
	 */
	public static void resetLocalOracle() {
		oracle_mappings.clear();
		error_rate = 0;
	}

	
	
	public static void main(String[] args) {
		String base_path = "C:/Users/Ernes/OneDrive/Documents/OAEI/oracle/anatomy/";
		loadLocalOraculoLLM(base_path);
	}

	
	
}

package uk.ac.ox.krr.logmap2.oaei.oracle;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import uk.ac.ox.krr.logmap2.io.ReadFile;

public class LocalOracle extends Oraculo{
	
	
		
	private static Map<String, Set<String>> oracle_mappings = new HashMap<String, Set<String>>();  
	
	
	@Override
	public boolean isMappingValid(String uri1, String uri2) {
		
		return isMappingInLocalOracle(uri1, uri2);
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
	
	
	
	
	public static void loadLocalOraculo(String base_path) {
		
		try {
			int count=0;			
			String pattern = ".txt";
			
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
					
					//System.out.println(elements[0] + "  " + elements[1]);
					count++;
					
					line=reader.readLine();
				}
				
				reader.closeBuffer();
				//System.out.println("Num mapping in oracle: " + count +  "  " + oracle_mappings.size());
			}
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	
	public static void main(String[] args) {
		String base_path = "/usr/local/data/MappingsConferenceBenchmark/reference-alignment-subset2012/";
		loadLocalOraculo(base_path);
	}



	@Override
	public boolean isActive() {
		return true;
	}
}

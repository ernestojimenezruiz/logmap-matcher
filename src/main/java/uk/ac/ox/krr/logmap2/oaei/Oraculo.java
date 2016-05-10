package uk.ac.ox.krr.logmap2.oaei;

//TODO
//Uncomment for OAEI
//import eu.sealsproject.omt.client.interactive.Oracle;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;

import uk.ac.ox.krr.logmap2.io.LogOutput;
import uk.ac.ox.krr.logmap2.io.ReadFile;



/**
 * Interface to query an Oracle
 * @author Ernesto
 *
 */
public class Oraculo {
	
	private static Map<String, Set<String>> oracle_mappings = new HashMap<String, Set<String>>();  
	
	private static int numQuestions = 0;
	
	//0 we do not know, 1 not, 2 yes, 3 local active
	private static final int NOTSET = 0;
	private static final int NO = 1;
	private static final int YES = 2;
	private static final int LOCAL = 3;
	private static int status_oraculo = NOTSET;
	
	
	
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
	
	
	
	
	public static void loadOraculoConference() {
		
		try {
			int count=0;
			String base_path = "/usr/local/data/MappingsConferenceBenchmark/reference-alignment-subset2012/";
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
	
	public static void allowOracle(boolean allow){
		if (allow)
			unsetStatusOraculo();
		else
			status_oraculo=NO;
	}
	
	public static void setLocalOracle(boolean active){
		if (active)
			status_oraculo=LOCAL;
		else
			status_oraculo=NO;
	}

	public static boolean isActive(){
		
		if (status_oraculo==NOTSET){ //We do not know
		
			try{
				
				return false; //comment for OAEI
				
				//TODO
				///Uncomment for OAEI
				/*if (Oracle.isInteractive()){
					status_oraculo=YES;
					return true;
				}
				else{
					status_oraculo=NO;
					return false;
				}*/				
				//Uncomment for OAEI
				
			}
			catch (Exception e){
				//e.printStackTrace();
				LogOutput.printAlways("Error asking Oracle");
				status_oraculo=NO;
				return false;
			}
			//return isActive;
		}
		//We know already
		else if (status_oraculo==NO){
			return false;
		}
		else if (status_oraculo==YES){
			return true;
		}
		else { //local oraculo
			return true;
		}
		
	}
	
	public static boolean isMappingValid(String uri1, String uri2){
		/*try {
		    Class.forName("eu.sealsproject.omt.client.interactive.Oracle");
		    // It is available
		}
		catch (ClassNotFoundException exception) {
		    // It is not available
		}*/
		
		//Local oraculo (comment for OAEI?)
		if (status_oraculo==LOCAL){
			numQuestions++;
			return isMappingInLocalOracle(uri1, uri2);
		}
		
		//TODO
		///Uncomment for OAEI
		/*try{
			
			//if (Oracle.check(uri1, uri2, Oracle.Relation.EQUIVALENCE)){
			if (Oracle.check(uri1, uri2, "=")){
				numQuestions++;
				LogOutput.printAlways("Oracle.check(uri1, uri2, =): " + numQuestions);
				return true;
			}
			//else if (Oracle.check(uri1, uri2, Oracle.Relation.SUBSUMED_BY)){
			else if (Oracle.check(uri1, uri2, "<")){				
				numQuestions++;
				LogOutput.printAlways("Oracle.check(uri1, uri2, <): " + numQuestions);
				return true;
			}
			//else if (Oracle.check(uri1, uri2, Oracle.Relation.SUBSUMES)){
			else if (Oracle.check(uri1, uri2, ">")){
				numQuestions++;
				LogOutput.printAlways("Oracle.check(uri1, uri2, =): " + numQuestions);
				return true;
			}
			else {
				LogOutput.printAlways("Not in oracle: " + numQuestions);
				numQuestions++;
				return false;
			}
			//return Oracle.check(uri1, uri2, Oracle.Relation.EQUIVALENCE) || 
			//		   Oracle.check(uri1, uri2, Oracle.Relation.SUBSUMED_BY) || 
			//		   Oracle.check(uri1, uri2, Oracle.Relation.SUBSUMES);
		}
		catch (Exception e){
			LogOutput.printAlways("Error asking Oracle");
			return false;
		}*/
		///Uncomment for OAEI
		
		return false; //comment for OAEI
		
	}
	
	public static int getNumberOfQuestions(){
		return numQuestions;
	}
	
	public static int getStatusOraculo(){
		return status_oraculo;
	}
	public static void unsetStatusOraculo(){
		numQuestions=0;
		status_oraculo=NOTSET;
	}
	
	
	public static void main(String[] args) {
		loadOraculoConference();
	}

}

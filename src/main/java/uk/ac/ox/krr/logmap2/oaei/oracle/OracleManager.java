package uk.ac.ox.krr.logmap2.oaei.oracle;


import uk.ac.ox.krr.logmap2.io.LogOutput;

/**
 * Interface to query an Oracle
 * @author Ernesto
 *
 */
public class OracleManager {
	
	
	private static int numQuestions = 0;
	
	//0 we do not know, 1 not, 2 yes, 3 local active
	private static final int NOTSET = 0;
	private static final int NO = 1;
	private static final int YES = 2;
	private static final int LOCAL = 3;
	private static int status_oraculo = NOTSET;
	
	private static LocalOracle localOracle = new LocalOracle();
	
	private static OAEIOracle oaeiOracle = new OAEIOracle();
	
	
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
				
				
				if (oaeiOracle.isActive()){
					status_oraculo=YES;
					return true;
				}
				else{
					status_oraculo=NO;
					return false; //comment for OAEI
				}
				
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
		else { //local oraculo: always true
			return localOracle.isActive();
		}
		
	}
	
	public static boolean isMappingValid(String uri1, String uri2){
		
		if (status_oraculo==LOCAL){
			numQuestions++;
			return localOracle.isMappingValid(uri1, uri2);
		}
		else{
			numQuestions++;
			return oaeiOracle.isMappingValid(uri1, uri2);
		}
		
		
				
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
	
	
	

}

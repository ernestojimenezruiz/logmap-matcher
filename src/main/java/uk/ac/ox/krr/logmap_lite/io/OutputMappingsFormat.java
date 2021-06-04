package uk.ac.ox.krr.logmap_lite.io;


public abstract class OutputMappingsFormat {

	protected String output_file;
	
	
	protected OutputMappingsFormat(String output_file_str){
		
		output_file=output_file_str;
		
	}
	
	
	protected abstract void setOutput() throws Exception;
	
		
	protected abstract void addClassMapping2Output(String iri_str1, String iri_str2, int dir_mapping, double conf) throws Exception;
	
	protected abstract void addDataPropMapping2Output(String iri_str1, String iri_str2, int dir_mapping, double conf) throws Exception;
	
	protected abstract void addObjPropMapping2Output(String iri_str1, String iri_str2, int dir_mapping, double conf) throws Exception;
	
	protected abstract void addInstanceMapping2Output(String iri_str1, String iri_str2, double conf) throws Exception;
	
	protected abstract void saveOutputFile() throws Exception;
	
	
	/**
	 * Rounded and shifted to [0-1] interval 
	 * @param conf
	 * @return
	 */
	protected double getRoundConfidence(double conf){
		//return (double)Math.round(conf*10.0)/1000.0;
		return (double)Math.round(conf*100.0)/100.0; //two decimals
	}
	

}

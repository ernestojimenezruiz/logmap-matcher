package uk.ac.ox.krr.logmap2.test.oaei;

import uk.ac.ox.krr.logmap2.io.OutPutFilesManager;
import uk.ac.ox.krr.logmap2.oaei.reader.MappingsReaderManager;

public class testRDF2TXTMappings {
	
	
	public testRDF2TXTMappings(String input_file) throws Exception{
		
		//1. Load mappings
		MappingsReaderManager reader = new MappingsReaderManager(input_file, MappingsReaderManager.OAEIFormat);
	
		//2. Output mappings in several formats
		OutPutFilesManager writer = new OutPutFilesManager();
		
		writer.createOutFiles(input_file, OutPutFilesManager.AllFlatFormats, "", "");
		
		writer.addMappings(reader.getMappingObjects());
		writer.closeAndSaveFiles();
		
		
	}
	
	
	
	public static void main(String[] args){
		
		String path;
		
		//path = "C:/Users/Ernes/OneDrive/Documents/OAEI/anatomy-dataset/reference.rdf";
		
		path = "C:/Users/Ernes/OneDrive/Documents/OAEI/largebio/fma-nci/oaei_FMA2NCI_UMLS_mappings_with_flagged_repairs.rdf";
		//path = "C:/Users/Ernes/OneDrive/Documents/OAEI/largebio/fma-snomed/oaei_FMA2SNOMED_UMLS_mappings_with_flagged_repairs.rdf";
		//path = "C:/Users/Ernes/OneDrive/Documents/OAEI/largebio/snomed-nci/oaei_SNOMED2NCI_UMLS_mappings_with_flagged_repairs.rdf";
		
		
		try {
			new testRDF2TXTMappings(path);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	
	
	
	
	

}

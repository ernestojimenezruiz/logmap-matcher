package uk.ac.ox.krr.logmap2.test.oaei;

import uk.ac.ox.krr.logmap2.oaei.reader.MappingsReaderManager;

public class TestLoadMappings {
	
	public static void main (String[] args) {
		
		MappingsReaderManager manager =				
				new MappingsReaderManager("/home/ernesto/Downloads/export_bao_ncit.rdf", MappingsReaderManager.OAEIFormat);
		
		//new MappingsReaderManager("/home/ernesto/Downloads/export_bao_ncit.rdf", MappingsReaderManager.OAEIFormat);
		
		System.out.println(manager.getMappingObjectsSize());
		
		
	}

}

package uk.ac.ox.krr.logmap2.test.oaei;

import uk.ac.ox.krr.logmap2.io.OutPutFilesManager;
import uk.ac.ox.krr.logmap2.oaei.reader.MappingsReaderManager;

public class TestTSVBioML2RDFMappings {
	
	
	public TestTSVBioML2RDFMappings(String input_file, String output_file, String iri1, String iri2) throws Exception{
		
		//1. Load mappings
		MappingsReaderManager reader = new MappingsReaderManager(input_file, MappingsReaderManager.FlatFormat);
	
		//2. Output mappings in several formats
		OutPutFilesManager writer = new OutPutFilesManager();
		
		writer.createOutFiles(output_file, OutPutFilesManager.AllFormats, iri1, iri2);
		
		writer.addMappings(reader.getMappingObjects());
		writer.closeAndSaveFiles();
				
	}
	
	
	public static void main(String[] args){
		
		String path_in;
		String path_out;
		String iri1;
		String iri2;
		
		path_in = "C:/Users/Ernes/OneDrive/Documents/OAEI/bio-ml/ncit-doid/refs_equiv/full.tsv";
		path_out = "C:/Users/Ernes/OneDrive/Documents/OAEI/bio-ml/ncit-doid/refs_equiv/ncit-doid-ref";
		iri1 = "http://logmap-tests/oaei/source.owl";
		iri2 = "http://logmap-tests/oaei/target.owl";
		
		//path_in = "C:/Users/Ernes/OneDrive/Documents/OAEI/bio-ml/omim-ordo/refs_equiv/full.tsv";
		//path_out = "C:/Users/Ernes/OneDrive/Documents/OAEI/bio-ml/omim-ordo/refs_equiv/omim-ordo-ref";
		
		//path_in = "C:/Users/Ernes/OneDrive/Documents/OAEI/bio-ml/snomed-fma.body/refs_equiv/full.tsv";
		//path_out = "C:/Users/Ernes/OneDrive/Documents/OAEI/bio-ml/snomed-fma.body/refs_equiv/snomed-fma.body-ref";
				
		//path_in = "C:/Users/Ernes/OneDrive/Documents/OAEI/bio-ml/snomed-ncit.neoplas/refs_equiv/full.tsv";
		//path_out = "C:/Users/Ernes/OneDrive/Documents/OAEI/bio-ml/snomed-ncit.neoplas/refs_equiv/snomed-ncit.neoplas-ref";
		
		//path_in = "C:/Users/Ernes/OneDrive/Documents/OAEI/bio-ml/snomed-ncit.pharm/refs_equiv/full.tsv";
		//path_out = "C:/Users/Ernes/OneDrive/Documents/OAEI/bio-ml/snomed-ncit.pharm/refs_equiv/snomed-ncit.pharm-ref";
		
		
		try {
			new TestTSVBioML2RDFMappings(path_in, path_out, iri1, iri2);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	

}

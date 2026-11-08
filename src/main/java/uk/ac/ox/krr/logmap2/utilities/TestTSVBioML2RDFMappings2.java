package uk.ac.ox.krr.logmap2.utilities;

import java.util.HashSet;
import java.util.Set;

import uk.ac.ox.krr.logmap2.io.OutPutFilesManager;
import uk.ac.ox.krr.logmap2.mappings.objects.MappingObjectStr;
import uk.ac.ox.krr.logmap2.oaei.reader.MappingsReaderManager;

public class TestTSVBioML2RDFMappings2 {
	
	
	


	public TestTSVBioML2RDFMappings2(String input_file, String output_file, String iri1, String iri2) throws Exception{
		
		
		//1. Load mappings
		MappingsReaderManager reader = new MappingsReaderManager(input_file, MappingsReaderManager.FlatFormat);
	
		//2. Output mappings in several formats
		OutPutFilesManager writer = new OutPutFilesManager();
		
		writer.createOutFiles(output_file, OutPutFilesManager.AllFormats, iri1, iri2);
		
		writer.addMappings(reader.getMappingObjects());
		writer.closeAndSaveFiles();
				
	}
	
	
	public TestTSVBioML2RDFMappings2(String test_file, String train_file, String output_file, String iri1, String iri2) throws Exception{
		
		
		
		//0. Create extra constructor.
		//1 Load test
		//2 Load training as flagged = true (to be implemented)
		//3 Merge
		//4 Store: check that stored properly
		//5 Use them for precision and recall
		
		
		//1. Load mappings
		MappingsReaderManager reader_test = new MappingsReaderManager(test_file, MappingsReaderManager.FlatFormat);
		
		MappingsReaderManager reader_train = new MappingsReaderManager(train_file, MappingsReaderManager.FlatFormat);
		
		
		Set<MappingObjectStr> mappings = new HashSet<MappingObjectStr>();
		mappings.addAll(reader_test.getMappingObjects());
		
		
		//Only for supervised setting
		for (MappingObjectStr m : reader_train.getMappingObjects()) {
			m.setDirMapping(MappingObjectStr.Flagged);
			mappings.add(m);
		}
		
		
		
	
		//2. Output mappings in several formats
		OutPutFilesManager writer = new OutPutFilesManager();
		
		writer.createOutFiles(output_file, OutPutFilesManager.AllFormats, iri1, iri2);
		
		writer.addMappings(mappings);
		writer.closeAndSaveFiles();
				
	}
	
	
	public static void main(String[] args){
		
		String path_test;
		String path_train;
		String path_out;
		String iri1;
		String iri2;
		
		iri1 = "http://logmap-tests/oaei/source.owl";
		iri2 = "http://logmap-tests/oaei/target.owl";
		
		final int NCITDOID = 1;
		final int OMIMORDO = 2;
		final int SNOMEDFMABody = 3;
		final int SNOMEDNCITNeoplas = 4;
		final int SNOMEDNCITPharm = 5;
		int test=5;
		
		
		//NCI-DOID
		if (test==NCITDOID) {
			path_test = "C:/Users/Ernes/OneDrive/Documents/OAEI/bio-ml/ncit-doid/refs_equiv/test.tsv";
			path_train = "C:/Users/Ernes/OneDrive/Documents/OAEI/bio-ml/ncit-doid/refs_equiv/train.tsv";
			path_out = "C:/Users/Ernes/OneDrive/Documents/OAEI/bio-ml/ncit-doid/refs_equiv/ncit-doid-ref-flagged";
		}
		else if (test==OMIMORDO) {
			//OMIM-ORDO
			path_test = "C:/Users/Ernes/OneDrive/Documents/OAEI/bio-ml/omim-ordo/refs_equiv/test.tsv";
			path_train = "C:/Users/Ernes/OneDrive/Documents/OAEI/bio-ml/omim-ordo/refs_equiv/train.tsv";
			path_out = "C:/Users/Ernes/OneDrive/Documents/OAEI/bio-ml/omim-ordo/refs_equiv/omim-ordo-ref-flagged";
		}
		else if (test==SNOMEDFMABody) {	
			//SNOMED-FMA body
			path_test = "C:/Users/Ernes/OneDrive/Documents/OAEI/bio-ml/snomed-fma.body/refs_equiv/test.tsv";
			path_train = "C:/Users/Ernes/OneDrive/Documents/OAEI/bio-ml/snomed-fma.body/refs_equiv/train.tsv";
			path_out = "C:/Users/Ernes/OneDrive/Documents/OAEI/bio-ml/snomed-fma.body/refs_equiv/snomed-fma.body-ref-flagged";
		}
		else if (test==SNOMEDNCITNeoplas) {
			//SNOMED-NCIT Neoplas
			path_test = "C:/Users/Ernes/OneDrive/Documents/OAEI/bio-ml/snomed-ncit.neoplas/refs_equiv/test.tsv";
			path_train = "C:/Users/Ernes/OneDrive/Documents/OAEI/bio-ml/snomed-ncit.neoplas/refs_equiv/train.tsv";
			path_out = "C:/Users/Ernes/OneDrive/Documents/OAEI/bio-ml/snomed-ncit.neoplas/refs_equiv/snomed-ncit.neoplas-ref-flagged";
		}
		else { //test==SNOMEDNCITNPharm
			//SNOMED-NCIT Pharm
			path_test = "C:/Users/Ernes/OneDrive/Documents/OAEI/bio-ml/snomed-ncit.pharm/refs_equiv/test.tsv";
			path_train = "C:/Users/Ernes/OneDrive/Documents/OAEI/bio-ml/snomed-ncit.pharm/refs_equiv/train.tsv";
			path_out = "C:/Users/Ernes/OneDrive/Documents/OAEI/bio-ml/snomed-ncit.pharm/refs_equiv/snomed-ncit.pharm-ref-flagged";
		}
		
		
		
		
		
		try {
			new TestTSVBioML2RDFMappings2(path_test, path_train, path_out, iri1, iri2);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	

}

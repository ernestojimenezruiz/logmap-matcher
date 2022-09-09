package uk.ac.ox.krr.logmap2.test.oaei;

import java.util.HashSet;
import java.util.Set;

import uk.ac.ox.krr.logmap2.LogMap2_OAEI_BioPortal;
import uk.ac.ox.krr.logmap2.mappings.objects.MappingObjectStr;
import uk.ac.ox.krr.logmap2.oaei.reader.RDFAlignReader;

public class TestLogMapBio {
	
	
private void getPrecisionRecall(Set<MappingObjectStr> mappingsToTest){
		
		boolean printPR = false;
		
		try{
			
			if (printPR){
			
			double p;
			double r;
			double f;
			
			int size_mappings=mappingsToTest.size();
			
			Set<MappingObjectStr> mappings = new HashSet<MappingObjectStr>(mappingsToTest);
			
			
			mappings.retainAll(reference);
			
			int good_mappings = mappings.size();
			
			p = (double)good_mappings/(double) size_mappings;
			r = (double)good_mappings/(double) reference.size();
			f = 2*r*p/(r+p);
			
			
			System.out.println("P: "+ p + ", R: " + r +", F: "+ f +", Num: "+ mappingsToTest.size());
			}
		}
		catch(Exception e){
			//do nothing
		}
		
	}
	
	
	
	private static Set<MappingObjectStr> loadReference(String ref){
		
		try {
			
			RDFAlignReader readerref;
			
			readerref = new RDFAlignReader(ref);
			
			return readerref.getMappingObjects();
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			
		
		return null;
		
	}
	
	
	
	
	
	public static Set<MappingObjectStr> reference = new HashSet<MappingObjectStr>();
	
	public static void main(String[] args){
		
		String iri1, iri2, ref, file_out;
		
		//if (true)
		//	return;
		
		
		iri1= "file:/home/ernesto/Documents/OAEI_Datasets-revise/mouse/mouse2012.owl";
		iri2= "file:/home/ernesto/Documents/OAEI_Datasets-revise/mouse/human2012.owl";
		ref = "/home/ernesto/Documents/OAEI_Datasets-revise/mouse/reference2012.rdf"; // Reference ontology
		file_out= "/home/ernesto/Documents/OAEI_Datasets-revise/mouse/logmapbio-test";
		
		/*iri1= "file:/home/ernesto/Documents/OAEI_datasets/oaei_2013/oaei2013_FMA_small_overlapping_nci.owl";
		iri2= "file:/home/ernesto/Documents/OAEI_datasets/oaei_2013/oaei2013_NCI_small_overlapping_fma.owl";
		
		iri1= "file:/home/ernesto/Documents/OAEI_datasets/oaei_2013/oaei2013_FMA_whole_ontology.owl";
		iri2= "file:/home/ernesto/Documents/OAEI_datasets/oaei_2013/oaei2013_NCI_whole_ontology.owl";
		ref = "/home/ernesto/Documents/OAEI_datasets/oaei_2013/oaei2013_FMA2NCI_repaired_UMLS_mappings.rdf";
		
		/*iri1= "file:/home/ernesto/Documents/OAEI_datasets/oaei_2013/oaei2013_FMA_small_overlapping_snomed.owl";
		iri2= "file:/home/ernesto/Documents/OAEI_datasets/oaei_2013/oaei2013_SNOMED_small_overlapping_fma.owl";
		
		iri1= "file:/home/ernesto/Documents/OAEI_datasets/oaei_2013/oaei2013_FMA_whole_ontology.owl";
		iri2= "file:/home/ernesto/Documents/OAEI_datasets/oaei_2013/oaei2013_SNOMED_extended_overlapping_fma_nci.owl";
		
		ref = "/home/ernesto/Documents/OAEI_datasets/oaei_2013/oaei2013_FMA2SNOMED_repaired_UMLS_mappings.rdf";
		
		/*iri1= "file:/home/ernesto/Documents/OAEI_datasets/oaei_2013/oaei2013_SNOMED_small_overlapping_nci.owl";
		iri2= "file:/home/ernesto/Documents/OAEI_datasets/oaei_2013/oaei2013_NCI_small_overlapping_snomed.owl";
		
		iri1= "file:/home/ernesto/Documents/OAEI_datasets/oaei_2013/oaei2013_SNOMED_extended_overlapping_fma_nci.owl";
		iri2= "file:/home/ernesto/Documents/OAEI_datasets/oaei_2013/oaei2013_NCI_whole_ontology.owl";
		ref = "/home/ernesto/Documents/OAEI_datasets/oaei_2013/oaei2013_SNOMED2NCI_repaired_UMLS_mappings.rdf";
		
		SatisfiabilityIntegration.setReasoner(ReasonerManager.ELK);
		*/

		
		
		
		//iri1= "";
		//iri2= "";
		//file_out = "";  //no extension
		
		
		
		
		

		reference = loadReference(ref);
		
		LogMap2_OAEI_BioPortal logmap_Bio = new LogMap2_OAEI_BioPortal();
		
		
		try {
			logmap_Bio.align(iri1, iri2);
			logmap_Bio.returnAlignmentFile(file_out);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	
	

}

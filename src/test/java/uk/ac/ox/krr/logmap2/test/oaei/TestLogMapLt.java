package uk.ac.ox.krr.logmap2.test.oaei;

import uk.ac.ox.krr.logmap_lite.LogMap_Lite;

public class TestLogMapLt {
	
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		String uri1;
		String uri2;
		String gs_mappings;
		int ontos = 1;
		
		if (ontos==1){
			uri1= "file:/usr/local/data/DataUMLS/UMLS_Onto_Versions/OAEI_datasets/fma2nci_dataset/oaei2012_FMA_whole_ontology.owl";		
			uri2= "file:/usr/local/data/DataUMLS/UMLS_Onto_Versions/OAEI_datasets/fma2nci_dataset/oaei2012_NCI_whole_ontology.owl";
			gs_mappings = "/usr/local/data/DataUMLS/UMLS_source_data/onto_mappings_FMA_NCI_cleantDG.txt";
		}
		else if (ontos==2){
			uri1= "file:/usr/local/data/DataUMLS/UMLS_Onto_Versions/OAEI_datasets/fma2nci_dataset/oaei2012_FMA_whole_ontology.owl";
			uri2= "file:/usr/local/data/DataUMLS/UMLS_Onto_Versions/OAEI_datasets/snomed20090131_replab.owl";
			gs_mappings = "/usr/local/data/DataUMLS/UMLS_source_data/onto_mappings_FMA_SNOMED_cleantDG.txt";
		}
		else if (ontos==3){
			uri1= "file:/usr/local/data/DataUMLS/UMLS_Onto_Versions/OAEI_datasets/snomed20090131_replab.owl";		
			uri2= "file:/usr/local/data/DataUMLS/UMLS_Onto_Versions/OAEI_datasets/fma2nci_dataset/oaei2012_NCI_whole_ontology.owl";
			gs_mappings = "/usr/local/data/DataUMLS/UMLS_source_data/onto_mappings_SNOMED_NCI_cleantDG.txt";
		}
		else{
			//uri1= "file:/usr/local/data/DataUMLS/UMLS_Onto_Versions/Anatomy/mouse_anatomy_2010_norm.owl";
			//uri2= "file:/usr/local/data/DataUMLS/UMLS_Onto_Versions/Anatomy/nci_anatomy_2010_norm.owl";
			uri1= "file:/usr/local/data/DataUMLS/UMLS_Onto_Versions/Anatomy/mouse_anatomy_2010.owl";
			uri2= "file:/usr/local/data/DataUMLS/UMLS_Onto_Versions/Anatomy/nci_anatomy_2010.owl";
			gs_mappings = "/usr/local/data/DataUMLS/UMLS_Onto_Versions/Anatomy/GSAll_Anatomy_2010.txt";
		}
		boolean overlapping = false;
		boolean create_owl_mappings_onto = false;
		boolean eval_impact = false;
		
		
		
		uri1 = "file:/usr/local/data/Benchmark_Track/onto101.owl";
		uri2 = "file:/usr/local/data/Benchmark_Track/onto201.owl";
		
		uri1 = "http://seals-test.sti2.at/tdrs-web/testdata/persistent/biblio-dataset/biblio-dataset-r1/suite/101/component/source";
		uri2 = "http://seals-test.sti2.at/tdrs-web/testdata/persistent/biblio-dataset/biblio-dataset-r1/suite/202/component/target";
		
		gs_mappings = "";
		
		LogMap_Lite logmap = new LogMap_Lite(uri1, uri2, gs_mappings, create_owl_mappings_onto, overlapping, eval_impact);
		//logmap.getModule1();
		//logmap.getModule2();
		//logmap.getOWLMappingsOntology();
		
	}

}

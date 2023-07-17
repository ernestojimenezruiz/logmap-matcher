package uk.ac.ox.krr.logmap2.test.oaei;

import uk.ac.ox.krr.logmap2.LogMap_Full;
import uk.ac.ox.krr.logmap2.io.LogOutput;
import uk.ac.ox.krr.logmap2.reasoning.ReasonerManager;
import uk.ac.ox.krr.logmap2.utilities.Utilities;

public class TestLogMapFull {
	
	
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		//long init, fin;
		
		String uri1="";
		String uri2="";
		String gs_mappings="";

		int ontopair;
		
		int reasoner_id;
		
		/*public static int FMA2NCI=0;
		public static int FMA2SNOMED=1;
		public static int SNOMED2NCI=2;
		public static int MOUSE2HUMAN=5;*/
		
		if (args.length==2){
			ontopair=Integer.valueOf(args[0]);
			reasoner_id=Integer.valueOf(args[1]);
	
			
			LogOutput.print("Ontology pair: " + args[0] + "  "  + args[1]);
			
		}
		
		else if (args.length==4){
			
			uri1 = args[0];
			uri2 = args[1];
			
			gs_mappings = args[2];
			reasoner_id=Integer.valueOf(args[3]);
	
			ontopair=-1;
			
			
			LogOutput.print("Ontology pair: " + uri1 + "  "  + uri2 + "  " + gs_mappings + "  " + reasoner_id);
			
			
			
			
		}
		
		else{
		//if (args.length!=2){
			
			reasoner_id = ReasonerManager.HERMIT;
			//reasoner_id = ReasonerManager.PELLET;
			//reasoner_id = ReasonerManager.FACTpp;
			reasoner_id = ReasonerManager.ELK;
			//reasoner_id = ReasonerManager.TrOWL;
			
			ontopair=Utilities.MOUSE2HUMAN;
			
			//ontopair=Utilities.FMA2NCI;
			//ontopair=Utilities.NCIpeque2FMA;
			//ontopair=Utilities.NCI2FMApeque;
			//ontopair=Utilities.SNOMED2LUCADA;
			//ontopair=Utilities.NCI2LUCADA;
			//ontopair=Utilities.FMA2LUCADA;

			//ontopair=Utilities.FMA2SNOMED;
			//ontopair=Utilities.SNOMED2NCI;
	
			//ontopair=8;
			
		}
		
		
		
		if (ontopair==Utilities.FMA2NCI){
			
				
				uri1= "file:/usr/local/data/DataUMLS/UMLS_Onto_Versions/OAEI_datasets/fma2nci_dataset/oaei2012_FMA_whole_ontology.owl";		
				uri2= "file:/usr/local/data/DataUMLS/UMLS_Onto_Versions/OAEI_datasets/fma2nci_dataset/oaei2012_NCI_whole_ontology.owl";
				
				//uri1= "file:/usr/local/data/DataUMLS/UMLS_Onto_Versions/OAEI_datasets/fma2nci_dataset/oaei2012_FMA_big_overlapping_nci.owl";		
				//uri2= "file:/usr/local/data/DataUMLS/UMLS_Onto_Versions/OAEI_datasets/fma2nci_dataset/oaei2012_NCI_big_overlapping_fma.owl";
				
				//uri1= "file:/usr/local/data/DataUMLS/UMLS_Onto_Versions/OAEI_datasets/fma2nci_dataset/FMA_bigoverlapping_nci.owl";		
				//uri2= "file:/usr/local/data/DataUMLS/UMLS_Onto_Versions/OAEI_datasets/fma2nci_dataset/NCI_bigoverlapping_fma.owl";
				
				
				//uri1= "file:/usr/local/data/DataUMLS/UMLS_Onto_Versions/FMA_overlapping_nci.owl";
				//uri2= "file:/usr/local/data/DataUMLS/UMLS_Onto_Versions/NCI_overlapping_fma.owl";
				
				
				//old
				//uri1=  "http://seals-test.sti2.at/tdrs-web/testdata/persistent/cf0378d9-da30-4b58-b937-192028ed4961/1dc20ac1-400c-4c01-afc1-1e0e80f16ace/suite/original-fma-c-nci-c/component/source";
				//uri2=  "http://seals-test.sti2.at/tdrs-web/testdata/persistent/cf0378d9-da30-4b58-b937-192028ed4961/1dc20ac1-400c-4c01-afc1-1e0e80f16ace/suite/original-fma-c-nci-c/component/target";
				
				//uri1=  "http://seals-test.sti2.at/tdrs-web/testdata/persistent/cf0378d9-da30-4b58-b937-192028ed4961/a9fe4a9b-54c0-4ecf-bdfe-f405cf72193d/suite/original-fma-c-nci-c/component/source";
				//uri2=  "http://seals-test.sti2.at/tdrs-web/testdata/persistent/cf0378d9-da30-4b58-b937-192028ed4961/a9fe4a9b-54c0-4ecf-bdfe-f405cf72193d/suite/original-fma-c-nci-c/component/target";
				
				//uri1 = "http://seals-test.sti2.at/tdrs-web/testdata/persistent/cf0378d9-da30-4b58-b937-192028ed4961/a9fe4a9b-54c0-4ecf-bdfe-f405cf72193d/suite/original-fma-a-nci-a/component/source";
				//uri2 = "http://seals-test.sti2.at/tdrs-web/testdata/persistent/cf0378d9-da30-4b58-b937-192028ed4961/a9fe4a9b-54c0-4ecf-bdfe-f405cf72193d/suite/original-fma-a-nci-a/component/target";
				
				
				
				//uri1_out= "file:/usr/local/data/DataUMLS/UMLS_Onto_Versions/FMA_bigoverlapping_nci.owl";
				//uri2_out= "file:/usr/local/data/DataUMLS/UMLS_Onto_Versions/NCI_bigoverlapping_fma.owl";
				
				gs_mappings = "/usr/local/data/DataUMLS/UMLS_source_data/onto_mappings_FMA_NCI_cleantDG.txt";
				//gs_mappings = "/usr/local/data/DataUMLS/UMLS_source_data/onto_mappings_FMA_NCI_dirty.txt";
			
																				
		}
		else if (ontopair==Utilities.FMA2SNOMED){
			
				//uri1= "file:/usr/local/data/DataUMLS/UMLS_Onto_Versions/FMADL_2_0_with_synonyms.owl";
				uri1= "file:/usr/local/data/DataUMLS/UMLS_Onto_Versions/OAEI_datasets/fma2nci_dataset/oaei2012_FMA_whole_ontology.owl";
				uri2= "file:/usr/local/data/DataUMLS/UMLS_Onto_Versions/OAEI_datasets/snomed20090131_replab.owl";
								
				//uri1= "file:/usr/local/data/DataUMLS/UMLS_Onto_Versions/FMA_bigoverlapping_snmd.owl";
				//uri2= "file:/usr/local/data/DataUMLS/UMLS_Onto_Versions/SNMD_bigoverlapping_fma.owl";
				
				//uri1= "file:/usr/local/data/DataUMLS/UMLS_Onto_Versions/OAEI_datasets/fma2snmd_dataset/oaei2012_FMA_big_overlapping_snomed.owl";		
				//uri2= "file:/usr/local/data/DataUMLS/UMLS_Onto_Versions/OAEI_datasets/fma2snmd_dataset/oaei2012_SNOMED_big_overlapping_fma.owl";
				
				
				//uri1_out= "file:/usr/local/data/DataUMLS/UMLS_Onto_Versions/FMA_bigoverlapping_snmd.owl";
				//uri2_out= "file:/usr/local/data/DataUMLS/UMLS_Onto_Versions/SNMD_bigoverlapping_fma.owl";
				
			
				
				gs_mappings = "/usr/local/data/DataUMLS/UMLS_source_data/onto_mappings_FMA_SNOMED_cleantDG.txt";
				//gs_mappings = "/usr/local/data/DataUMLS/UMLS_source_data/onto_mappings_FMA_SNOMED_dirty.txt";
			
				
					
		}
		else if (ontopair==Utilities.SNOMED2NCI){
			
				uri1= "file:/usr/local/data/DataUMLS/UMLS_Onto_Versions/OAEI_datasets/snomed20090131_replab.owl";		
				//uri2= "file:/usr/local/data/DataUMLS/UMLS_Onto_Versions/NCI_Thesaurus_08.05d_with_synonyms.owl";//
				uri2= "file:/usr/local/data/DataUMLS/UMLS_Onto_Versions/OAEI_datasets/fma2nci_dataset/oaei2012_NCI_whole_ontology.owl";
				
				gs_mappings = "/usr/local/data/DataUMLS/UMLS_source_data/onto_mappings_SNOMED_NCI_cleantDG.txt";
				//gs_mappings = "/usr/local/data/DataUMLS/UMLS_source_data/onto_mappings_SNOMED_NCI_dirty.txt";
				
			
		}
		else if (ontopair==Utilities.MOUSE2HUMAN){
			
			//interactivityFile = "simulationInteractivityMouse.txt";
			
			System.out.println("LogMap full");
			
			String path = "/home/ernesto/Documents/Datasets/anatomy/";
			
			uri1= "file:" + path + "mouse.owl";
			uri2= "file:" + path + "human.owl";
			
			gs_mappings = "";
			
		}
		
		
		else if (ontopair==Utilities.SNOMED2LUCADA){
			
			//interactivityFile = "simulationInteractivitySNMD2LUCADA.txt";
			
			//uri1= "file:/usr/local/data/DataUMLS/UMLS_Onto_Versions/snomed20090131_replab.owl";
			//uri1= "file:/usr/local/data/DataUMLS/UMLS_Onto_Versions/LUCADA/snomed20110131_replab.owl";
			uri1= "file:/usr/local/data/DataUMLS/UMLS_Onto_Versions/LUCADA/snomed20110131_replab_with_ids.owl";
			uri2= "file:/usr/local/data/DataUMLS/UMLS_Onto_Versions/LUCADA/LUCADAOntology15September2011.owl";
					
			
			gs_mappings = "/usr/local/data/DataUMLS/UMLS_Onto_Versions/LUCADA/references_to_snomed.txt";
			
		
		}
		
		else if (ontopair==Utilities.NCI2LUCADA){
			
			//interactivityFile = "simulationInteractivityNCI2LUCADA.txt";
			
			uri1= "file:/usr/local/data/DataUMLS/UMLS_Onto_Versions/OAEI_datasets/fma2nci_dataset/oaei2012_NCI_whole_ontology.owl";
			uri2= "file:/usr/local/data/DataUMLS/UMLS_Onto_Versions/LUCADA/LUCADAOntology15September2011.owl";
					
										
			gs_mappings = "/usr/local/data/DataUMLS/UMLS_Onto_Versions/LUCADA/references_to_nci.txt";
			
			
		}
		
		else {
			//Do nothing
		}
		
		
		
		
		
		try{
			//init = Calendar.getInstance().getTimeInMillis();
		
			new LogMap_Full(
					uri1, 
					uri2, 
					gs_mappings,
					reasoner_id
				);
		
			//fin = Calendar.getInstance().getTimeInMillis();
			//LogOutput.print("TOTAL TIME (s): " + (float)((double)fin-(double)init)/1000.0);
		}
		catch (Exception e){
			e.printStackTrace();
		}
		
			
	}	
	
	

}

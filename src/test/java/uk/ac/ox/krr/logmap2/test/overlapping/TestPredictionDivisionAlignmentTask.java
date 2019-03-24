/*******************************************************************************
 * Copyright 2017 by the Department of Informatics (University of Oslo)
 * 
 *    This file is part of the Ontology Services Toolkit 
 *
 *******************************************************************************/
package uk.ac.ox.krr.logmap2.test.overlapping;

import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.io.IRIDocumentSource;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.MissingImportHandlingStrategy;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyLoaderConfiguration;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.parameters.Imports;

import uk.ac.ox.krr.logmap2.Parameters;
import uk.ac.ox.krr.logmap2.division.BasicDivision;
import uk.ac.ox.krr.logmap2.division.BasicDivisionPredictor;
import uk.ac.ox.krr.logmap2.division.MatchingTask;
import uk.ac.ox.krr.logmap2.division.QualityMeasures;
import uk.ac.ox.krr.logmap2.io.LogOutput;
import uk.ac.ox.krr.logmap2.mappings.objects.MappingObjectStr;
import uk.ac.ox.krr.logmap2.oaei.reader.RDFAlignReader;
import uk.ac.ox.krr.logmap2.owlapi.SynchronizedOWLManager;
import uk.ac.ox.krr.logmap2.utilities.Utilities;

/**
 *
 * @author ernesto
 * Created on 26 Feb 2018
 *
 */
public class TestPredictionDivisionAlignmentTask {


	
	public static final int HP2MP2016=15;
	public static final int DOID2ORDO2016=16;
	
	public static final int HP2MP2017=17;
	public static final int DOID2ORDO2017=18;
	
	
	
	/**
	 * UMLS mappings will be our gold standard.
	 * @throws Exception
	 */
	private static Set<MappingObjectStr> loadMappingsRDF(String file_mappings) throws Exception{
	
		RDFAlignReader reader = new RDFAlignReader(file_mappings);
		
		return reader.getMappingObjects();
		
	}
	
	
	
	private static OWLOntology loadOWLOntology(String phy_iri_onto) throws OWLOntologyCreationException{		

		try {
			
			OWLOntologyManager managerOnto;
			managerOnto = SynchronizedOWLManager.createOWLOntologyManager();			
			//managerOnto.setSilentMissingImportsHandling(true);	
			OWLOntologyLoaderConfiguration conf = new OWLOntologyLoaderConfiguration();
			conf.setMissingImportHandlingStrategy(MissingImportHandlingStrategy.SILENT);			
			return managerOnto.loadOntologyFromOntologyDocument(
					new IRIDocumentSource(IRI.create(phy_iri_onto)), conf);
			
						
		}
		catch(Exception e){
			System.err.println("Error loading OWL ontology: " + e.getMessage());
			//e.printStackTrace();
			throw new OWLOntologyCreationException();
		}
	}
	
	

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String uri1;
		String uri2;
		
		//String file_gs_mappings; 
		
		String file_gs_rdf; 
		
		//String file_logmapbio_mappings;
		//String file_logmap2_mappings;
		
						
				
		
		
		int ontopair = 0;
				
		Parameters.readParameters();
		
		Parameters.print_output = false;
		Parameters.print_output_always = true;
		
		LogOutput.showOutpuLog(Parameters.print_output);
		LogOutput.showOutpuLogAlways(Parameters.print_output_always);
		
		Parameters.min_size_overlapping=0;

		
		ontopair=Utilities.MOUSE2HUMAN;
		//ontopair=Utilities.FMA2NCI;		
		//ontopair=Utilities.FMA2SNOMED;
		//ontopair=Utilities.SNOMED2NCI;
		
		//ontopair=HP2MP2016;
		//ontopair=DOID2ORDO2016;
		//ontopair=HP2MP2017;
		//ontopair=DOID2ORDO2017;
					
		String path = "/home/ernesto/Documents/BackUp_Mar_20_2014/data/DataUMLS/UMLS_Onto_Versions/OAEI_datasets/oaei_2013/";
		String irirootpath = "file:" + path;
		
		
		if (ontopair==Utilities.FMA2NCI){
			
			uri1 = irirootpath + "oaei2013_FMA_whole_ontology.owl";
			uri2 = irirootpath + "oaei2013_NCI_whole_ontology.owl";

			//task="FMA-NCI";
			
			
			//file_gs_mappings = path + "oaei2013_FMA2NCI_repaired_UMLS_mappings.txt";				
			file_gs_rdf = path + "reference_alignment/oaei2013_FMA2NCI_original_UMLS_mappings_with_confidence.rdf";
			
			
			
			//file_logmap_mappings = "file:/home/ernesto/Documents/BackUp_Mar_20_2014/data/DataUMLS/UMLS_Onto_Versions/ISWC_LogMap0.9_Mappings/FMA2NCI_logmap_mappings.owl";
			//file_logmapbio_mappings="/home/ernesto/Documents/OAEI_2016/EVAL_2016/MAPPINGS/LargeBio/LogMapBio-largebio-fma_nci_whole_2016.rdf";
			//file_logmap2_mappings="/home/ernesto/Documents/OAEI_2016/EVAL_2016/MAPPINGS/LargeBio/LogMap-largebio-fma_nci_whole_2016.rdf";
				
																				
		}
		else if (ontopair==Utilities.FMA2SNOMED){
			
			uri1 = irirootpath + "oaei2013_FMA_whole_ontology.owl";
			uri2 = irirootpath + "oaei2013_SNOMED_extended_overlapping_fma_nci.owl";			
			//uri2 = "file:/home/ernesto/Documents/BackUp_Mar_20_2014/data/DataUMLS/UMLS_Onto_Versions/OAEI_datasets/snomed20090131_replab.owl";
			
			
			//task="FMA-SNOMED";
			
			
			//file_gs_mappings = path + "oaei2013_FMA2SNOMED_repaired_UMLS_mappings.txt";
			
			file_gs_rdf = path + "reference_alignment/oaei2013_FMA2SNOMED_original_UMLS_mappings_with_confidence.rdf";
			
			
			//file_logmap_mappings = "file:/home/ernesto/Documents/BackUp_Mar_20_2014/data/DataUMLS/UMLS_Onto_Versions/ISWC_LogMap0.9_Mappings/FMA2SNMD_logmap_mappings.owl";
			//file_logmapbio_mappings="/home/ernesto/Documents/OAEI_2016/EVAL_2016/MAPPINGS/LargeBio/LogMapBio-largebio-fma_snomed_whole_2016.rdf";
			//file_logmap2_mappings="/home/ernesto/Documents/OAEI_2016/EVAL_2016/MAPPINGS/LargeBio/LogMap-largebio-fma_snomed_whole_2016.rdf";
				
				
					
		}
		else if (ontopair==Utilities.SNOMED2NCI){
			
			//uri1 = "file:/home/ernesto/Documents/BackUp_Mar_20_2014/data/DataUMLS/UMLS_Onto_Versions/OAEI_datasets/snomed20090131_replab.owl";
			uri1 = irirootpath + "oaei2013_SNOMED_extended_overlapping_fma_nci.owl";
			uri2 = irirootpath + "oaei2013_NCI_whole_ontology.owl";

			//task="SNOMED-NCI";
			
			//file_gs_mappings = path + "oaei2013_SNOMED2NCI_repaired_UMLS_mappings.txt";
			file_gs_rdf = path + "reference_alignment/oaei2013_SNOMED2NCI_original_UMLS_mappings_with_confidence.rdf";
			
			
			//file_logmap_mappings = "file:/home/ernesto/Documents/BackUp_Mar_20_2014/data/DataUMLS/UMLS_Onto_Versions/ISWC_LogMap0.9_Mappings/SNMD2NCI_logmap_mappings.owl";
			//file_logmapbio_mappings="/home/ernesto/Documents/OAEI_2016/EVAL_2016/MAPPINGS/LargeBio/LogMapBio-largebio-snomed_nci_whole_2016.rdf";
			//file_logmap2_mappings="/home/ernesto/Documents/OAEI_2016/EVAL_2016/MAPPINGS/LargeBio/LogMap-largebio-snomed_nci_whole_2016.rdf";
			
			
		}
		else if (ontopair==Utilities.MOUSE2HUMAN){
			
			//task="MOUSE";
			
			uri1= "file:/home/ernesto/Documents/BackUp_Mar_20_2014/data/DataUMLS/UMLS_Onto_Versions/Anatomy/2012/mouse2012.owl";
			uri2= "file:/home/ernesto/Documents/BackUp_Mar_20_2014/data/DataUMLS/UMLS_Onto_Versions/Anatomy/2012/human2012.owl";
								
			//file_gs_mappings = "/home/ernesto/Documents/BackUp_Mar_20_2014/data/DataUMLS/UMLS_Onto_Versions/Anatomy/2012/reference2012.txt";
			file_gs_rdf = "/home/ernesto/Documents/BackUp_Mar_20_2014/data/DataUMLS/UMLS_Onto_Versions/Anatomy/2012/reference2012.rdf";
			
			//file_logmapbio_mappings="/home/ernesto/Documents/OAEI_2016/EVAL_2016/MAPPINGS/LargeBio/LogMapBio-Anatomy.rdf";
		 	//file_logmap2_mappings="/home/ernesto/Documents/OAEI_2016/EVAL_2016/MAPPINGS/LargeBio/LogMap-Anatomy.rdf";
			
			//file_logmap_mappings = "/home/ernesto/Documents/OAEI_OM_2015/EVAL_2015/MAPPINGS/Mouse_logmap2_Output";
			
				
		}
		
		else if (ontopair==HP2MP2016){
			
			String path2 = "/home/ernesto/Documents/OAEI_2016/Pistoia/OAEI_datasets/";
			String iri_path2 = "file:"+ path2;
			
			uri1 = iri_path2 + "HP.rdf";
			uri2 = iri_path2 + "MP.rdf";
			
			//silver 2
			file_gs_rdf = path2 + "Silver-hp-mp-2.rdf";
			
		}
		
		else  if (ontopair==DOID2ORDO2016) {
			
			String path2 = "/home/ernesto/Documents/OAEI_2016/Pistoia/OAEI_datasets/";
			String iri_path2 = "file:"+ path2;
			
			uri1 = iri_path2 + "DOID.rdf";
			uri2 = iri_path2 + "ORDO.rdf";
			
			//silver 2
			file_gs_rdf = path2 + "Silver-doid-ordo-2.rdf";
			
		}
		else if (ontopair==HP2MP2017){
			
			String path2 = "/home/ernesto/Documents/OAEI_2017/Pistoia/OAEI_datasets/";
			String iri_path2 = "file:"+ path2;
			
			uri1 = iri_path2 + "hp_noimports.owl";
			uri2 = iri_path2 + "mp_noimports.owl";
			
			//silver 2
			file_gs_rdf = path2 + "selected/Silver-hp-mp-2.rdf";
			
		}
		
		else { //if (ontopair==DOID2ORDO2017) {
			
			String path2 = "/home/ernesto/Documents/OAEI_2017/Pistoia/OAEI_datasets/";
			String iri_path2 = "file:"+ path2;
			
			uri1 = iri_path2 + "doid_noimports.owl";
			uri2 = iri_path2 + "ordo.owl";
			
			//silver 2
			file_gs_rdf = path2 + "selected/Silver-doid-ordo-2.rdf";
			
		}
		
		
		
		
		

		try {
						
			//number of tasks
			//int[] size_module={10000, 9000, 8000, 7000, 6000, 5000, 4000, 3000, 2000, 1000, 500, 200, 100};
			int[] size_module={100};

			int repetitions = 10;
			//int repetitions = 1;
			
			
			OWLOntology onto1 = loadOWLOntology(uri1);
			OWLOntology onto2 = loadOWLOntology(uri2);
			
			
			for (int j=0; j<size_module.length; j++){
				
				System.out.println("\nRequired module size: "+size_module[j]);
				
				//Header				
				System.out.println(QualityMeasures.toStringHeader());
				
			
				
				//Repetitions
				for (int i=0; i<repetitions; i++){ 
					
					BasicDivisionPredictor partitioner = new BasicDivisionPredictor(size_module[j], false);
					
					List<MatchingTask> tasks = partitioner.createPartitionedMatchingTasks(onto1, onto2);
					
					Set<MappingObjectStr> alignment = loadMappingsRDF(file_gs_rdf);
					
					
					if (tasks.size()>0){
						
						//TODO Add different alignment
						QualityMeasures quality = new QualityMeasures(tasks, 
								alignment,
								alignment,
								partitioner.getComputationTime(), 
								onto1.getSignature(Imports.INCLUDED).size(), 
								onto2.getSignature(Imports.INCLUDED).size()); 
						
						
						
						System.out.println(quality.toString());
					}
					
					for (MatchingTask mtask : tasks){
						mtask.clear();
					}
					
					tasks.clear();
					alignment.clear();
					//quality.clear();
					
					partitioner.clear();
					
				}
			}
			
			
			
		} catch (OWLOntologyCreationException e) {
			e.printStackTrace();
		} catch (Exception e) {
			System.err.println(e.getLocalizedMessage());
			e.printStackTrace();
		}
		
		
		/*
		
		
		List<String> list = new ArrayList<String>();
		
		list.add("a");
		list.add("b");
		list.add("c");
		list.add("d");
		list.add("e");
		
		System.out.println(list);
		
		Collections.shuffle(list);
		
		System.out.println(list);
		
		Collections.shuffle(list);
		
		System.out.println(list);
		
		Collections.shuffle(list);
		
		System.out.println(list);
		
		
		*/
	}

}

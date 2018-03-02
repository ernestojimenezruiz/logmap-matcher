/*******************************************************************************
 * Copyright 2017 by the Department of Informatics (University of Oslo)
 * 
 *    This file is part of the Ontology Services Toolkit 
 *
 *******************************************************************************/
package uk.ac.ox.krr.logmap2.test.overlapping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import uk.ac.ox.krr.logmap2.Parameters;
import uk.ac.ox.krr.logmap2.io.LogOutput;
import uk.ac.ox.krr.logmap2.io.ReadFile;
import uk.ac.ox.krr.logmap2.mappings.objects.MappingObjectStr;
import uk.ac.ox.krr.logmap2.oaei.reader.RDFAlignReader;
import uk.ac.ox.krr.logmap2.partitioning.BasicMultiplePartitioning;
import uk.ac.ox.krr.logmap2.partitioning.MatchingTask;
import uk.ac.ox.krr.logmap2.partitioning.OntologyAlignmentPartitioning;
import uk.ac.ox.krr.logmap2.partitioning.OverlappingEstimation;
import uk.ac.ox.krr.logmap2.partitioning.QualityMeasures;
import uk.ac.ox.krr.logmap2.utilities.Utilities;

/**
 *
 * @author ernesto
 * Created on 26 Feb 2018
 *
 */
public class TestPartitioning {


	
	/**
	 * UMLS mappings will be our gold standard.
	 * @throws Exception
	 */
	private static Set<MappingObjectStr> loadMappingsRDF(String file_mappings) throws Exception{
	
		RDFAlignReader reader = new RDFAlignReader(file_mappings);
		
		return reader.getMappingObjects();
		
	}
	

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String uri1;
		String uri2;
		
		String file_gs_mappings; 
		
		String file_gs_rdf; 
		
		String file_logmapbio_mappings;
		String file_logmap2_mappings;
		
		
		Set<String> entities1_gs = new HashSet<String>();
		Set<String> entities2_gs = new HashSet<String>();
		
		Set<String> entities1_logmap = new HashSet<String>();
		Set<String> entities2_logmap = new HashSet<String>();
		
		Set<String> entities1_logmapbio = new HashSet<String>();
		Set<String> entities2_logmapbio = new HashSet<String>();
						
				
		
		
		int ontopair = 0;
				
		Parameters.readParameters();
		
		Parameters.print_output = false;
		Parameters.print_output_always = true;
		
		LogOutput.showOutpuLog(Parameters.print_output);
		LogOutput.showOutpuLogAlways(Parameters.print_output_always);
		
		Parameters.min_size_overlapping=0;
		boolean useExtendedLabels=true;
		
		String task;
		
		double recall1;
		double recall2;
		
		double recall1_logmapbio;
		double recall2_logmapbio;
		
		double recall1_logmap;
		double recall2_logmap;
		
		double overlappingratio1;
		double overlappingratio2;
		
		ontopair=Utilities.MOUSE2HUMAN;
		//ontopair=Utilities.FMA2NCI;		
		//ontopair=Utilities.FMA2SNOMED;
		//ontopair=Utilities.SNOMED2NCI;
		
					
		String path = "/home/ernesto/Documents/BackUp_Mar_20_2014/data/DataUMLS/UMLS_Onto_Versions/OAEI_datasets/oaei_2013/";
		String irirootpath = "file:" + path;
		
		
		if (ontopair==Utilities.FMA2NCI){
			
			uri1 = irirootpath + "oaei2013_FMA_whole_ontology.owl";
			uri2 = irirootpath + "oaei2013_NCI_whole_ontology.owl";

			task="FMA-NCI";
			
			
			file_gs_mappings = path + "oaei2013_FMA2NCI_repaired_UMLS_mappings.txt";				
			file_gs_rdf = path + "reference_alignment/oaei2013_FMA2NCI_original_UMLS_mappings_with_confidence.rdf";
			
			
			
			//file_logmap_mappings = "file:/home/ernesto/Documents/BackUp_Mar_20_2014/data/DataUMLS/UMLS_Onto_Versions/ISWC_LogMap0.9_Mappings/FMA2NCI_logmap_mappings.owl";
			file_logmapbio_mappings="/home/ernesto/Documents/OAEI_2016/EVAL_2016/MAPPINGS/LargeBio/LogMapBio-largebio-fma_nci_whole_2016.rdf";
			file_logmap2_mappings="/home/ernesto/Documents/OAEI_2016/EVAL_2016/MAPPINGS/LargeBio/LogMap-largebio-fma_nci_whole_2016.rdf";
				
																				
		}
		else if (ontopair==Utilities.FMA2SNOMED){
			
			uri1 = irirootpath + "oaei2013_FMA_whole_ontology.owl";
			//uri2 = irirootpath + "oaei2013_SNOMED_extended_overlapping_fma_nci.owl";			
			uri2 = "file:/home/ernesto/Documents/BackUp_Mar_20_2014/data/DataUMLS/UMLS_Onto_Versions/OAEI_datasets/snomed20090131_replab.owl";
			
			
			task="FMA-SNOMED";
			
			
			file_gs_mappings = path + "oaei2013_FMA2SNOMED_repaired_UMLS_mappings.txt";
			
			file_gs_rdf = path + "reference_alignment/oaei2013_FMA2SNOMED_original_UMLS_mappings_with_confidence.rdf";
			
			
			//file_logmap_mappings = "file:/home/ernesto/Documents/BackUp_Mar_20_2014/data/DataUMLS/UMLS_Onto_Versions/ISWC_LogMap0.9_Mappings/FMA2SNMD_logmap_mappings.owl";
			file_logmapbio_mappings="/home/ernesto/Documents/OAEI_2016/EVAL_2016/MAPPINGS/LargeBio/LogMapBio-largebio-fma_snomed_whole_2016.rdf";
			file_logmap2_mappings="/home/ernesto/Documents/OAEI_2016/EVAL_2016/MAPPINGS/LargeBio/LogMap-largebio-fma_snomed_whole_2016.rdf";
				
				
					
		}
		else if (ontopair==Utilities.SNOMED2NCI){
			
			uri1 = "file:/home/ernesto/Documents/BackUp_Mar_20_2014/data/DataUMLS/UMLS_Onto_Versions/OAEI_datasets/snomed20090131_replab.owl";
			//uri1 = irirootpath + "oaei2013_SNOMED_extended_overlapping_fma_nci.owl";
			uri2 = irirootpath + "oaei2013_NCI_whole_ontology.owl";

			task="SNOMED-NCI";
			
			file_gs_mappings = path + "oaei2013_SNOMED2NCI_repaired_UMLS_mappings.txt";
			file_gs_rdf = path + "reference_alignment/oaei2013_SNOMED2NCI_original_UMLS_mappings_with_confidence.rdf";
			
			
			//file_logmap_mappings = "file:/home/ernesto/Documents/BackUp_Mar_20_2014/data/DataUMLS/UMLS_Onto_Versions/ISWC_LogMap0.9_Mappings/SNMD2NCI_logmap_mappings.owl";
			file_logmapbio_mappings="/home/ernesto/Documents/OAEI_2016/EVAL_2016/MAPPINGS/LargeBio/LogMapBio-largebio-snomed_nci_whole_2016.rdf";
			file_logmap2_mappings="/home/ernesto/Documents/OAEI_2016/EVAL_2016/MAPPINGS/LargeBio/LogMap-largebio-snomed_nci_whole_2016.rdf";
			
			
		}
		else {// if (ontopair==Utilities.MOUSE2HUMAN){
			
			task="MOUSE";
			
			uri1= "file:/home/ernesto/Documents/BackUp_Mar_20_2014/data/DataUMLS/UMLS_Onto_Versions/Anatomy/2012/mouse2012.owl";
			uri2= "file:/home/ernesto/Documents/BackUp_Mar_20_2014/data/DataUMLS/UMLS_Onto_Versions/Anatomy/2012/human2012.owl";
								
			file_gs_mappings = "/home/ernesto/Documents/BackUp_Mar_20_2014/data/DataUMLS/UMLS_Onto_Versions/Anatomy/2012/reference2012.txt";
			file_gs_rdf = "/home/ernesto/Documents/BackUp_Mar_20_2014/data/DataUMLS/UMLS_Onto_Versions/Anatomy/2012/reference2012.rdf";
			
			file_logmapbio_mappings="/home/ernesto/Documents/OAEI_2016/EVAL_2016/MAPPINGS/LargeBio/LogMapBio-Anatomy.rdf";
			file_logmap2_mappings="/home/ernesto/Documents/OAEI_2016/EVAL_2016/MAPPINGS/LargeBio/LogMap-Anatomy.rdf";
			
			//file_logmap_mappings = "/home/ernesto/Documents/OAEI_OM_2015/EVAL_2015/MAPPINGS/Mouse_logmap2_Output";
			
			
			
		}
		
		
		BasicMultiplePartitioning partitioner;
		

		try {
			//overlapping.createPartitionedMatchingTasks(uri1, uri2);
			
			partitioner = new BasicMultiplePartitioning();
			
			
			//number of tasks
			//int[] num_tasks={1,5,10,20,50,100,200};
			int[] num_tasks={200};
			//int repetitions = 10;
			int repetitions = 1;
			
			
			for (int j=0; j<num_tasks.length; j++){
				
				//Header				
				System.out.println(QualityMeasures.toStringHeader());
				
				//Repetitions
				for (int i=0; i<repetitions; i++){ 
										
					List<MatchingTask> tasks = partitioner.createPartitionedMatchingTasks(uri1, uri2, num_tasks[j]);
					
					Set<MappingObjectStr> alignment = loadMappingsRDF(file_gs_rdf);
					
					
					QualityMeasures quality = new QualityMeasures(tasks, alignment, partitioner.getComputationTime()); //TODO read alignment ass Set of mappingObjectStr
					
					
					System.out.println(quality.toString());
				}
			}
			
			
			
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
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

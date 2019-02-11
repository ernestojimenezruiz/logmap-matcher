/*******************************************************************************
 * Copyright 2017 by the Department of Informatics (University of Oslo)
 * 
 *    This file is part of the Ontology Services Toolkit 
 *
 *******************************************************************************/
package uk.ac.ox.krr.logmap2.test.overlapping;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.io.IRIDocumentSource;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.MissingImportHandlingStrategy;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyLoaderConfiguration;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import uk.ac.ox.krr.logmap2.LogMap2_Matcher;
import uk.ac.ox.krr.logmap2.Parameters;
import uk.ac.ox.krr.logmap2.division.BasicDivision;
import uk.ac.ox.krr.logmap2.division.MatchingTask;
import uk.ac.ox.krr.logmap2.division.QualityMeasures;
import uk.ac.ox.krr.logmap2.io.LogOutput;
import uk.ac.ox.krr.logmap2.mappings.objects.MappingObjectStr;
import uk.ac.ox.krr.logmap2.oaei.reader.RDFAlignReader;
import uk.ac.ox.krr.logmap2.owlapi.SynchronizedOWLManager;
import uk.ac.ox.krr.logmap2.statistics.StatisticsTimeMappings;
import uk.ac.ox.krr.logmap2.utilities.Utilities;

/**
 *
 * @author ernesto
 * Created on 26 Feb 2018
 *
 */
public class TestLoading {


	
	public static final int HP2MP2016=15;
	public static final int DOID2ORDO2016=16;
	
	public static final int HP2MP2017=17;
	public static final int DOID2ORDO2017=18;
	
	
	
	private static void loadAndUnloadOWLOntology(String phy_iri_onto) throws OWLOntologyCreationException{		

		try {
			
			OWLOntologyManager managerOnto;
			managerOnto = SynchronizedOWLManager.createOWLOntologyManager();			
			//managerOnto.setSilentMissingImportsHandling(true);	
			OWLOntologyLoaderConfiguration conf = new OWLOntologyLoaderConfiguration();
			conf.setMissingImportHandlingStrategy(MissingImportHandlingStrategy.SILENT);			
			
			OWLOntology onto = managerOnto.loadOntologyFromOntologyDocument(
					new IRIDocumentSource(IRI.create(phy_iri_onto)), conf);
			
			managerOnto.removeOntology(onto);
			
						
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
		
		
		int ontopair = 0;
				
		Parameters.readParameters();
		
		Parameters.print_output = false;
		Parameters.print_output_always = false;
		
		LogOutput.showOutpuLog(Parameters.print_output);
		LogOutput.showOutpuLogAlways(Parameters.print_output_always);
		
		Parameters.min_size_overlapping=0;

		
		ontopair=Utilities.MOUSE2HUMAN;
		ontopair=Utilities.FMA2NCI;		
		//ontopair=Utilities.FMA2SNOMED;
		//ontopair=Utilities.SNOMED2NCI;
		
		//ontopair=HP2MP2016;
		//ontopair=DOID2ORDO2016;
		//ontopair=HP2MP2017;
		//ontopair=DOID2ORDO2017;
					
		String folder;
		
		
		if (ontopair==Utilities.FMA2NCI){
			
			folder = "fma2nci/";
			
			
																				
		}
		else if (ontopair==Utilities.FMA2SNOMED){
			
			
			
			folder = "fma2snomed/";
			
				
				
					
		}
		else if (ontopair==Utilities.SNOMED2NCI){
			
			
			folder = "fma2nci/";
			
			
		}
		else if (ontopair==Utilities.MOUSE2HUMAN){
			
			folder = "mouse/";
			
			
				
		}
		
		else if (ontopair==HP2MP2016){
			
			folder = "hp2mp/";
			
			
		}
		
		else  if (ontopair==DOID2ORDO2016) {
			
			folder = "doid2ordo/";
			
			
		}
		else if (ontopair==HP2MP2017){
			
			folder = "hp2mp2017/";
			
			
		}
		
		else { //if (ontopair==DOID2ORDO2017) {
			
			folder = "doid2ordo2017/";
			
			
		}
		
		
		
		
		

		try {
			//overlapping.createPartitionedMatchingTasks(uri1, uri2);
			
			
			String output_path = "/home/ernesto/Documents/OAEI_2017.5/overlapping/tasks/";
			
			
			//number of tasks
			int[] num_tasks={1,2,5,10,20,50,100,200};
			
			
			
			
			
			for (int j=0; j<num_tasks.length; j++){
			
				
				StatisticsTimeMappings.setCurrentInitTime();
				
					//for (MatchingTask mtask : tasks){
					for (int id_task = 0; id_task<num_tasks[j]; id_task++){
						
						String path = output_path + folder + num_tasks[j] + "/" + id_task + "/";
						
						
						String source= path + "source.owl";
						String target= path + "target.owl";
						
						if (new File(source).exists())
							loadAndUnloadOWLOntology("file:" + source);
						else
							System.err.println(source);
						if (new File(target).exists())
							loadAndUnloadOWLOntology("file:" + target);
						else
							System.err.println(target);
					}
					
					
					
					double loading_time = StatisticsTimeMappings.getRunningTime();
					//System.out.println("System or storage time for "+ num_tasks[j] + " partitions: " + loading_time);
					System.out.println(num_tasks[j] + "\t" + loading_time);
					
					
			}
			
			
			
		} catch (OWLOntologyCreationException e) {
			e.printStackTrace();
		} catch (Exception e) {
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

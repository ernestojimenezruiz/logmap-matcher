/*******************************************************************************
 * Copyright 2018 by The Alan Turing Institute
 * 
 *******************************************************************************/
package uk.ac.ox.krr.logmap2.test.oaei.interactivity;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import uk.ac.ox.krr.logmap2.LogMap2_Matcher;
import uk.ac.ox.krr.logmap2.Parameters;
import uk.ac.ox.krr.logmap2.io.OutPutFilesManagerStatic;
import uk.ac.ox.krr.logmap2.mappings.objects.MappingObjectStr;
import uk.ac.ox.krr.logmap2.oaei.oracle.LocalOracle;
import uk.ac.ox.krr.logmap2.oaei.oracle.OracleManager;
import uk.ac.ox.krr.logmap2.oaei.reader.MappingsReaderManager;
import uk.ac.ox.krr.logmap2.oaei.reader.RDFAlignReader;
import uk.ac.ox.krr.logmap2.test.oaei.OAEITask;
import uk.ac.ox.krr.logmap2.test.oaei.StandardMeasures;
import uk.ac.ox.krr.logmap2.utilities.Timer;
import uk.ac.ox.krr.logmap_lite.LogMap_Lite;

/**
 *
 * @author ernesto
 * Created on 22 April 2022
 *
 */
public abstract class TestOAEITrackWithOracle {
	
	
	List<OAEITask> tasks = new ArrayList<OAEITask>();
	
	Timer t;
	
	protected boolean SAVE_MAPPINGS = false;
	protected String PATH = "/tmp/logmap-alignment";
	protected String OUTPUT_FILE_TEMPLATE="";
	protected String PATH_TO_ORACLE="";
	
	
	
	
	public TestOAEITrackWithOracle(String output_folder_mappings){
		
		//with partial output path to save mappings
		PATH = output_folder_mappings;
		SAVE_MAPPINGS = true;

		setUp();
		
	}
	
	public TestOAEITrackWithOracle(){
		
		//Parameters.print_output_always=true;
		//Parameters.use_overlapping=false;
		
		setUp();
		
	}
	
	
	protected abstract void setUp(); 
	
	
	
	public void evaluateTasks() throws MalformedURLException, Exception{
	
		for (OAEITask task : tasks){
			System.out.println(task.getTaskName());
			evaluateTask(task);
		}
		
	}
	
	
	
	private void printMissedCasses(Set<MappingObjectStr> prediction, Set<MappingObjectStr> reference){
		Set<MappingObjectStr> difference = new HashSet<MappingObjectStr>();
		difference.addAll(reference);
		difference.removeAll(prediction);
		
		for (MappingObjectStr mapping : difference){
			System.out.println(mapping.getIRIStrEnt1() + "  " + mapping.getIRIStrEnt2() + "  " + mapping.getConfidence());
		}
	}
	
	
	
	private void printIntersection(Set<MappingObjectStr> prediction, Set<MappingObjectStr> reference){
		Set<MappingObjectStr> intersection = new HashSet<MappingObjectStr>();
		intersection.addAll(prediction);
		intersection.retainAll(reference);
		
		for (MappingObjectStr mapping : intersection){
			System.out.println(mapping.getIRIStrEnt1() + "  " + mapping.getIRIStrEnt2() + "  " + mapping.getConfidence());
		}
	}
	
	
	public void evaluateTask(OAEITask task) throws MalformedURLException, Exception{
		
		double matching_time;
		
		t = new Timer();
		
		//Setting up oracle
		OracleManager.setLocalOracle(true);		
		LocalOracle.loadLocalOraculoLLM(PATH_TO_ORACLE);
		
		
		LogMap2_Matcher logmap;
		
		if (SAVE_MAPPINGS){
			//TODO
			OUTPUT_FILE_TEMPLATE=PATH + task.getTaskName() + "-";
			System.out.println("Saving mappings to " + PATH);
			//saveLogMapMappings(logmap.getLogmap2_Mappings());
			//Save all formats and also discarded: done now internally
			logmap = new LogMap2_Matcher(task.getSource(), task.getTarget(), OUTPUT_FILE_TEMPLATE, false);
		}
		else {
			logmap = new LogMap2_Matcher(task.getSource(), task.getTarget());
		}
		
		
		//TEST LogMap Lite
		//LogMap_Lite logmap_lite = new LogMap_Lite();
		//logmap_lite.align(task.getSource(), task.getTarget());
		
		
		/*System.out.println("Size mappings: "+ logmap.getLogmap2_Mappings().size());
		System.out.println("Size anchors: "+ logmap.getLogmap2_anchors().size());
		System.out.println("Size discarded: "+ logmap.getLogmap2_DiscardedMappings().size());
		System.out.println("Size hard discarded: "+ logmap.getLogmap2_HardDiscardedMappings().size());
		System.out.println("Size conflicting: "+ logmap.getLogmap2_ConflictiveMappings().size());*/
		
		
		
		matching_time = t.durationMilisecons();
		t.pause();
		
		
		if (task.getReference()!=null && !task.getReference().equals("")) {
			
			RDFAlignReader readerReference = 
					new RDFAlignReader(new URL(task.getReference()));
			
			
			/*for (MappingObjectStr m : logmap.getLogmap2_DiscardedMappings()){
				if (m.getIRIStrEnt1().toLowerCase().contains("reservoir") || m.getIRIStrEnt2().contains("Reservoir"))
					System.out.println(m.getIRIStrEnt1() + "  " + m.getIRIStrEnt2() + "  " + m.getConfidence());
			}*/
			
			
			//printMissedCasses(logmap.getLogmap2_Mappings(), readerReference.getMappingObjects());
			//printIntersection(logmap.getLogmap2_Mappings(), readerReference.getMappingObjects());
			
			StandardMeasures.computeStandardMeasures(logmap.getLogmap2_Mappings(), readerReference.getMappingObjects());
			//System.out.println(readerReference.getMappingObjects().size());
			
			
			System.out.println(task.getTaskName() + "\t" + matching_time + "\t" + logmap.getLogmap2_Mappings().size() + "\t" + StandardMeasures.getPrecision()  + "\t" + StandardMeasures.getRecall()  + "\t" + StandardMeasures.getFscore());
		}
		else {
			System.out.println("No reference alignment given.");
		}
		
			
		
	}


	
	
	
	

}

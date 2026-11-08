/*******************************************************************************
 * Copyright 2017 by the Department of Informatics (University of Oslo)
 * 
 *    This file is part of the Ontology Services Toolkit 
 *
 *******************************************************************************/
package uk.ac.ox.krr.logmap2.utilities;


import java.util.HashSet;
import java.util.Set;

import uk.ac.ox.krr.logmap2.mappings.objects.MappingObjectStr;

/**
 *
 * It reuses HasAlignment created by Daniel Faria so that it does take into account mappings with "?" or those that are unknown.
 *
 * @author ernesto
 * Created on 17 Oct 2017
 * Modified on 08 Nov 2026
 *
 */
public class StandardMeasures {
	
	
	private static double precision=0.0;
	private static double recall=0.0;
	private static double fscore=0.0;
	/**
	 * @return the precision
	 */
	public static double getPrecision() {
		return precision;
	}
	
	/**
	 * @return the recall
	 */
	public static double getRecall() {
		return recall;
	}
	
	/**
	 * @return the fscore
	 */
	public static double getFscore() {
		return fscore;
	}		
	
	
	/**
	 * Used in BioML 2025
	 * @param system
	 * @param reference
	 * @param entities_to_ignore
	 */
	public static void computeStandardMeasures(Set<MappingObjectStr> system, Set<MappingObjectStr> reference, Set<String> entities_to_ignore){
		
		Set<MappingObjectStr> system_reduced = new HashSet<MappingObjectStr>(); 
		
		for (MappingObjectStr mapping : system) {
			if (entities_to_ignore.contains(mapping.getIRIStrEnt1()) || entities_to_ignore.contains(mapping.getIRIStrEnt2()))
				continue;
			
			system_reduced.add(mapping);
		}
				
		
		computeStandardMeasures(system_reduced, reference);
		
		
		
	}
	
	
	public static void computeStandardMeasures(Set<MappingObjectStr> system, Set<MappingObjectStr> reference){
		
		HashAlignment system_aligment = new HashAlignment(system);
		HashAlignment reference_aligment = new HashAlignment(reference);
		
		computeStandardMeasures(system_aligment, reference_aligment);		
		
	}
	
	
	public static void computeStandardMeasures(HashAlignment system, HashAlignment reference){
		
		evaluationParameters(reference.evaluation(system));		
		
	}
	
	
	private static void evaluationParameters(int[] classif)
	{
		
		if(classif[0]+classif[1] == 0 || classif[0]+classif[2] == 0){
			precision=recall=fscore=0.0;
			return;
		}
		
		//As TP, FP, FN, FN
		//Precision
		precision = Math.min(Math.round(classif[0] * 1000.0 / (classif[0]+classif[1]))/1000.0, 1.0);
		//Recall
		recall = Math.min(Math.round(classif[0] * 1000.0 / (classif[0]+classif[2]))/1000.0, 1.0);
		//F-measure
		fscore = Math.round((2000.0 * precision * recall) / (precision + recall))/1000.0;
		
}
	
	
	
	
	
	
	

}

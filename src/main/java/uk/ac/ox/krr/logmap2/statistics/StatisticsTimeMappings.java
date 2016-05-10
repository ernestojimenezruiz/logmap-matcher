package uk.ac.ox.krr.logmap2.statistics;

import java.util.Calendar;

public class StatisticsTimeMappings {
	
	//Follow steps algorithm...
		
	private static double parsing_time=0.0;
	
	private static double overlapping_time=0.0;
	
	private static double lexical_indexation_time=0.0;
	
	private static double upperbound_mappings_time=0.0;
		
	private static double output_mappings_time=0.0;
	
	private static double structural_indexation_time=0.0;
	
	private static double confidence_values_time=0.0;
	
	private static double repair_time=0.0;
		
	private static long init_global;
	
	private static long current_init=0;
	
	

	/**
	 * @return the parsing_time
	 */
	public static double getParsing_time() {
		return parsing_time;
	}

	/**
	 * @param parsing_time to add
	 */
	public static void addParsing_time(double parsing_time) {
		StatisticsTimeMappings.parsing_time += parsing_time;
	}

	/**
	 * @return the overlapping_time
	 */
	public static double getOverlapping_time() {
		return overlapping_time;
	}

	/**
	 * @param overlapping_time to add
	 */
	public static void addOverlapping_time(double overlapping_time) {
		StatisticsTimeMappings.overlapping_time += overlapping_time;
	}

	/**
	 * @return the lexical_indexation_time
	 */
	public static double getLexical_indexation_time() {
		return lexical_indexation_time;
	}

	/**
	 * @param lexical_indexation_time to add
	 */
	public static void addLexical_indexation_time(double lexical_indexation_time) {
		StatisticsTimeMappings.lexical_indexation_time += lexical_indexation_time;
	}

	/**
	 * @return the upperbound_mappings_time
	 */
	public static double getUpperbound_mappings_time() {
		return upperbound_mappings_time;
	}

	/**
	 * @param upperbound_mappings_time to add
	 */
	public static void addUpperbound_mappings_time(double upperbound_mappings_time) {
		StatisticsTimeMappings.upperbound_mappings_time += upperbound_mappings_time;
	}

	/**
	 * @return the output_mappings_time
	 */
	public static double getOutput_mappings_time() {
		return output_mappings_time;
	}

	/**
	 * @param output_mappings_time to add
	 */
	public static void addOutput_mappings_time(double output_mappings_time) {
		StatisticsTimeMappings.output_mappings_time += output_mappings_time;
	}

	/**
	 * @return the confidence_values_time
	 */
	public static double getConfidence_values_time() {
		return confidence_values_time;
	}

	/**
	 * @param confidence_values_time to add
	 */
	public static void addConfidence_values_time(double confidence_values_time) {
		StatisticsTimeMappings.confidence_values_time += confidence_values_time;
	}

	/**
	 * @return the structural_indexation_time
	 */
	public static double getStructural_indexation_time() {
		return structural_indexation_time;
	}

	/**
	 * @param structural_indexation_time to add
	 */
	public static void addStructural_indexation_time(
			double structural_indexation_time) {
		StatisticsTimeMappings.structural_indexation_time += structural_indexation_time;
	}

	/**
	 * @return the repair_time
	 */
	public static double getRepair_time() {
		return repair_time;
	}

	/**
	 * @param repair_time to add
	 */
	public static void addRepair_time(double repair_time) {
		StatisticsTimeMappings.repair_time += repair_time;
	}
	
	
	/**
	 * Get current time in milliseconds
	 * @return
	 */
	public static long getCurrentTimeInMillis(){
		return Calendar.getInstance().getTimeInMillis();
	}
	
		
	/**
	 * Get running time in seconds for given initial and ending times
	 * @param init
	 * @param fin
	 * @return
	 */
	public static double getRunningTime(long init, long end){
		return (double)((double)end-(double)init)/1000.0;
	}
	
	/**
	 * Get running time in seconds from given "initial" time
	 * @param init
	 * @return
	 */
	public static double getRunningTime(long init){
		return (double)((double)getCurrentTimeInMillis()-(double)init)/1000.0;
	}
	
	/**
	 * Get running time in seconds from previously initialised "current_init" time
	 * @param init
	 * @return
	 */
	public static double getRunningTime(){
		return getRunningTime(current_init);
	}
	
	public static void setCurrentInitTime(){
		current_init = getCurrentTimeInMillis();
	}
	
	
	/**
	 * Set the initial time
	 */
	public static void setInitGlobalTime(){
		init_global = getCurrentTimeInMillis();
	}
	
	
	/**
	 * Gets total running time in seconds
	 * @return
	 */
	public static double getTotalRunningTime(){
		return getRunningTime(init_global);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	

}

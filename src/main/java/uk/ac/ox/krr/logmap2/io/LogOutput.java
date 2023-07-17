/*******************************************************************************
 * Copyright 2012 by the Department of Computer Science (University of Oxford)
 * 
 *    This file is part of LogMap.
 * 
 *    LogMap is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU Lesser General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 * 
 *    LogMap is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU Lesser General Public License for more details.
 * 
 *    You should have received a copy of the GNU Lesser General Public License
 *    along with LogMap.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package uk.ac.ox.krr.logmap2.io;

import java.util.logging.Level;
import java.util.logging.Logger;



public class LogOutput {

	//assumes the current class is called logger
	private final static Logger LOGGER = Logger.getLogger(LogOutput.class.getName()); 

	
	private static boolean show_output=true;
	private static boolean show_output_always=false;
	
	
	public static void setLoggerLevel(Level level){
		LOGGER.setLevel(level);		
	}
	
	
	
	public static void print(double d){
		if (show_output)
			print(String.valueOf(d));
	}
	
	public static void print(int i){
		if (show_output)
			print(String.valueOf(i));
	}
	
	
	public static void print(String str){
		
		if (show_output)
			System.out.println(str);
		
	}
	
	public static void printError(String str){
		
		if (show_output || show_output_always)
			System.err.println(str);
		
	}
	
	
	public static void printStatistics(String str){
		LOGGER.log(Level.INFO, str);
	}
	
	
	
	public static void showOutpuLog(boolean show){
		show_output=show;
	}

	public static void showOutpuLogAlways(boolean show){
		show_output_always=show;
	}
	
	public static void printAlways(String str){
		if (show_output_always)
			System.out.println(str);
	}
	
	
	

}

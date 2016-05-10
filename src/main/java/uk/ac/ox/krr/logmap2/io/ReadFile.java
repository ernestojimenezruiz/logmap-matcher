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

import java.io.BufferedReader;

import java.io.File;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InputStream;



/**
 * This program reads a text file line by line, using a BufferedReader
 * 
 */
public class ReadFile  {
	
	private BufferedReader rBuffer=null;
	
	
	public ReadFile (InputStream isr_file) throws Exception {
		
		//try {
			rBuffer = new BufferedReader(new InputStreamReader(isr_file));
		//}
		//catch (Exception e) {
    	//	e.printStackTrace();
    	//}
	}
	
	public ReadFile (String fstring) throws FileNotFoundException{

		//System.err.println(fstring);
	    
		File file = new File(fstring);
		//System.out.print("File " +  file.exists());
		
	    if (file.exists()){
	    	try {
	    		rBuffer = new BufferedReader(new FileReader(fstring));    
	    
	    	}	    	 
	    	catch (IOException e) {
	    		e.printStackTrace();
	    	}
	    }
	    else {
	    	throw new FileNotFoundException("The file '"+ fstring + "' doesn't exist.");
	    	//new Exception("The file '"+ fstring + "' doesn't exist.");
	    	
	    }
	}
	
	
	
	public ReadFile (File file) throws FileNotFoundException{
		
	    if (file.exists()){
	    	try {
	    		rBuffer = new BufferedReader(new FileReader(file));    
	    
	    	}	    	 
	    	catch (IOException e) {
	    		e.printStackTrace();
	    	}
	    }
	    else {
	    	throw new FileNotFoundException("The file '"+ file.getAbsolutePath() + "' doesn't exist.");	    	
	    }
	}
	
	
	
	
	public String readLine(){
		try {
			return rBuffer.readLine();
    
    	}  
    	catch (Exception e) {
    		System.err.println("An error occurred reading the file: " + e.getMessage());
    		e.printStackTrace();
    	}
    	return "";
    	


	}
	
	public void closeBuffer(){
		try {
			rBuffer.close();
		}
		catch (IOException e) {
    		e.printStackTrace();
    	}
	}
	
	

		
}

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

import java.io.BufferedWriter;


//import java.io.File;
import java.io.FileWriter;
import java.io.File;
//import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * This program writes a text file line by line, using a BufferedWritter
 * 
 */
public class WriteFile  {
	
	private BufferedWriter wBuffer=null;
	
	public WriteFile (String fstring){

		this(fstring, false);
	}
	
	public WriteFile (File file){

		this(file, false);
	}
	
	
	
	public WriteFile (String fstring, boolean append){
		
		//System.err.println(fstring);	    
		File file = new File(fstring);
	    if (!file.exists()){
	    	append=false;
	    }
	    
	    try {
	    		wBuffer = new BufferedWriter(new FileWriter(fstring, append));    
	    
	    }	    	 
	    catch (IOException e) {
	    	e.printStackTrace();
	    }
	    //}
	    //else {
	    //	new FileNotFoundException("The file '"+ fstring + "' doesn't exist.");
	    //}
		
	}
	
	
	public WriteFile (File file, boolean append){
		
		//System.err.println(fstring);	    
		
	    if (!file.exists()){
	    	append=false;
	    }
	    
	    try {
	    		wBuffer = new BufferedWriter(new FileWriter(file, append));    
	    
	    }	    	 
	    catch (IOException e) {
	    	e.printStackTrace();
	    }
	    //}
	    //else {
	    //	new FileNotFoundException("The file '"+ fstring + "' doesn't exist.");
	    //}
		
	}
	
	
	public void writeLine(String line){
		try {
			wBuffer.write(line+"\n");
    
    	}  
    	catch (IOException e) {
    		System.err.println("An error occurred writing the file: " + e.getLocalizedMessage() + " " +e.getMessage());
    		e.printStackTrace();
    	}
    	return;
    	


	}
	
	
	public void writeEmptyLine(){
		try {
			wBuffer.write("\n");    
    	}  
    	catch (IOException e) {
    		System.err.println("An error occurred writing the file: " + e.getLocalizedMessage() + " " +e.getMessage());
    		e.printStackTrace();
    	}
    	return;
    	


	}
	
	public void closeBuffer(){
		try {
			wBuffer.close();
		}
		catch (IOException e) {
    		e.printStackTrace();
    	}
	}
	
	

		
}

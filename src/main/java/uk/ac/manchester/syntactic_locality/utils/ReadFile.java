package uk.ac.manchester.syntactic_locality.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * This program reads a text file line by line, using a BufferedReader
 * 
 */
public class ReadFile  {
	
	private BufferedReader rBuffer=null;
	
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
	
	public String readLine(){
		try {
			return rBuffer.readLine();
    
    	}  
    	catch (Exception e) {
    		System.err.println("An error occurred reading the file: " + e.getMessage());
    		//e.printStackTrace();
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
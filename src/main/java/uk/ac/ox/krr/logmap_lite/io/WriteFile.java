package uk.ac.ox.krr.logmap_lite.io;

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
	
	public void closeBuffer(){
		try {
			wBuffer.close();
		}
		catch (IOException e) {
  		e.printStackTrace();
  	}
	}
	
	

		
}
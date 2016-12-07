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
package uk.ac.ox.krr.logmap2.web_service;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.File;

import javax.servlet.ServletException;
import javax.servlet.SingleThreadModel;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import uk.ac.ox.krr.logmap2.io.ReadFile;
import uk.ac.ox.krr.logmap2.io.WriteFile;

public class OntologyLibraryRequest  extends HttpServlet implements SingleThreadModel {
	
	
	private String base_uri;
	private String base_path;
	
	
	public void doGet(HttpServletRequest req, HttpServletResponse rsp)   
            throws ServletException, IOException {   
		
		rsp.setContentType("text/html");   
		PrintWriter out = rsp.getWriter();   
		
		base_uri = getProtocolName(req.getProtocol()) + "://" + req.getServerName() + ":" + req.getServerPort();
		
		out.println("<html>");   
		out.println("<head><title> Request Type: GET </title></head>");
		out.println("<script type=\"text/javascript\"> window.location=\""+base_uri+"\"</script>");
		
		
		out.println("<body>");   
		out.println("<p>Redirecting to <a href=\""+ base_uri + "\">" + base_uri +"</p>");
		out.println("</body></html>");   
	} 
	
	
	private String getProtocolName(String protocol){
        
        int slash = protocol.indexOf('/');
        if(slash==-1)
            return protocol;
        else
            return ((String) protocol.subSequence(0,slash)).toLowerCase();
    }
	
	
	/**
	 * Call LogMap with given parameters
	 */
     public void doPost(HttpServletRequest req, HttpServletResponse rsp)   
                   throws ServletException, IOException {    	 
    	 
    	
    	 
    	 rsp.setContentType("text/html");
     	 PrintWriter out = rsp.getWriter();   
    	 
    	 
    	 base_path = getServletContext().getRealPath("/ontologies");
    	 
    	 
    	 out.println("<option value=\"\">-- Choose Ontology URI from LogMap's library --</option>");
    	 
    	 
    	 
    	 String library_file = base_path + "/library.txt";
        	 
    	 File file  = new File(library_file);
    	 
    	 if (file.exists()){
    		 //int id=0;
    		 
	    	 ReadFile reader = new ReadFile(library_file);
	    	 String line;
	    	 while ((line = reader.readLine()) != null){	    		 
	    		 out.println("<option value=\""+ line + "\">" + line + "</option>");
	    		//id++;
	    	 }
	    	 reader.closeBuffer();        	        
         }
    	 
    	 
    	 
    	//<select name="ontos_lib1" id="ontos_lib1">
		//	<option value="">-- Choose Ontology URI from LogMap's library --</option>
		//	<option value="0">optaaaaaaaaaaaaaaaaaaaaaion1</option>
	    //		<option value="1">option2</option>
		//	<option value="2">option3</option>
		//	<option value="3">option4</option>
		//</select>
    	 
    	 
    	 
    	 
    	 
     }
	
	
	
	
	

}

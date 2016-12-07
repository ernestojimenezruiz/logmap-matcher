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


import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.SingleThreadModel;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServlet;


public class UploadFileRequest extends HttpServlet implements SingleThreadModel {
	
	private final int DEFAULT_BUFFER_SIZE = 1024 * 4;
	
	private String base_path;
	
	private String base_uri;
	
	String day;
	String mounth;
	String year;
	String hour;
	String minute;
	String second;
	String milisecond;
	
	String date4folder;
	String relative_output_path;
	
	
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
	
	
	public void doPost(HttpServletRequest req, HttpServletResponse rsp)   
            throws ServletException, IOException {    	 
	    try {
	    	
	    	
	    	 day = DateManager.getCurrentDay();
	         mounth = DateManager.getCurrentMonth();
	         year = DateManager.getCurrentYear();
	    	
	    	hour = DateManager.getCurrentHour();
			minute = DateManager.getCurrentMinute();
			second = DateManager.getCurrentSecond();
			milisecond = DateManager.getCurrentMilisecond();
	    	
	    	//ajax2.setRequestHeader("Content-Type", "multipart/form-data");
	    	//ajax2.setRequestHeader("Cache-Control", "no-cache");
	    	//ajax2.setRequestHeader("X-File-Type", file.type);	
	    	//ajax2.setRequestHeader("X-File-Name", file.fileName);
	    	//ajax2.setRequestHeader("X-File-Size", file.fileSize);
	    	
	    	//req.getHeader("X-File-Name");

	    	rsp.setContentType("text/html");   
	    	PrintWriter out = rsp.getWriter();	    	
	    	
	    	
	    	int dataLength = req.getContentLength();
	    	String filename = req.getHeader("X-File-Name");
	    	
	    	if (filename==null || filename.equals("") || filename.equals("undefined")){
	    		filename = "ontology" + "_" + day + "_" + mounth + "_" + year + "__" + hour  + "_" + minute  + "_" + second  + "_" + milisecond;
	    	}
	    	filename = filename.replaceAll(" ", "");
	    	
	    	//out.println("<p><b>"+ dataLength+ "</b>   <b>" + req.getHeader("X-File-Name")+  "</b>  <b>" + req.getHeader("X-File-Size")+"</p>");
	    	
	    		    	    
	    	base_path = getServletContext().getRealPath("/ontologies");
	    	
	    	String port="";
	    	if (req.getServerPort()!=80){
	    		port = ":" + req.getServerPort();
	    	}
	    	
	    	//Relative path within tomcat
	 	    String tomcat_path = getServletContext().getInitParameter("path");
	    	base_uri = getProtocolName(req.getProtocol()) + "://" + req.getServerName() + port + "/" + tomcat_path;
	    	
	    	
	    	
	    	
			
			
			date4folder = day + "_" + mounth + "_" + year;//  + "__" + hour  + "_" + minute  + "_" + second  + "_" + milisecond;			
			relative_output_path = "/matching_" + date4folder;
			
			
			//Create output folder
			String output_folder = base_path + relative_output_path;
			File f = new File(output_folder);
			if (!f.exists()){
				f.mkdir();
			}
	    	
	    		    	
	    	String uploaded_uri = base_uri  + "/ontologies" + relative_output_path + "/" + filename;
	    		    	
	    	
	    	int bytes = storeFile(req.getInputStream(), new FileOutputStream(output_folder + "/" + filename));
	    	
	    	out.println("Uploaded <i>" + bytes + "</i> bytes of <i>" + dataLength + "</i> bytes|" + uploaded_uri);
	    	
	        
	    } catch (Exception e) {
	        throw new ServletException("Cannot parse multipart request.", e);
	    }

	    // ...
	}
	
	
	
	private String getProtocolName(String protocol){
        
        int slash = protocol.indexOf('/');
        if(slash==-1)
            return protocol;
        else
            return ((String) protocol.subSequence(0,slash)).toLowerCase();
    }
	
	
	/**
	 * To store an input stream
	 * @param input
	 * @param output
	 * @return
	 * @throws IOException
	 */
	public int storeFile(InputStream input, OutputStream output) throws IOException {
			
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        int count = 0;
        int n = 0;
        while (-1 != (n = input.read(buffer)))
        {
            output.write(buffer, 0, n);
            count += n;
        }
        
        return count;
    }


}

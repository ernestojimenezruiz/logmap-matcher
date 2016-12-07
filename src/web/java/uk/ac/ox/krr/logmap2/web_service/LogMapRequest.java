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

import java.io.*;
import java.util.Set;
import java.util.HashSet;
import java.util.Calendar;
import java.util.concurrent.TimeoutException;

import javax.servlet.*;   
import javax.servlet.http.*;   

import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import uk.ac.ox.krr.logmap2.io.ReadFile;
import uk.ac.ox.krr.logmap2.io.WriteFile;
import uk.ac.ox.krr.logmap2.reasoning.ReasonerManager;
import uk.ac.ox.krr.logmap2.utilities.Utilities;



public class LogMapRequest extends HttpServlet implements SingleThreadModel {   
     
	private String day;
	private String month;
	private String year;
	//String hour;
	//String minute;
	//String second;
	//String milisecond;
	
	//String action;
	private String name;
	private String org;
	private String prj;
	private String email;
	private String uri1;
	private String uri2;
	private String logmap_version;
	
	private String base_path;
	private String base_uri;
	private String base_path_onto_library;
	 
	private String output_path;
	private String output_uri;
	
	private boolean useReasoner=false;
	private boolean useELReasoner=false;
	private boolean useMOREReasoner=false;
	
	private boolean storeURIs=false;
	
	private HTMLResultsFileManager output_file_manager;
	
	
	
	//private static final long serialVersionUID = 1L;

	
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
      
	
	/**
	 * Call LogMap with given parameters
	 */
     public void doPost(HttpServletRequest req, HttpServletResponse rsp)   
                   throws ServletException, IOException {    	 
    	 
    	rsp.setContentType("text/html");
    	PrintWriter out = rsp.getWriter();   
    
    	//action = req.getParameter("action");
     
       	uri1 = req.getParameter("uri1"); //It may be local?? do        
       	uri2 = req.getParameter("uri2");
              
       	logmap_version = req.getParameter("logmap");
      
        name = req.getParameter("name");
       
        org = req.getParameter("org");
        
        prj = req.getParameter("prj");
       
        email = req.getParameter("email");
        
        useReasoner = Boolean.valueOf(req.getParameter("reasoner"));
        
        useELReasoner = Boolean.valueOf(req.getParameter("elk"));
        
        useMOREReasoner = Boolean.valueOf(req.getParameter("more"));
        
        storeURIs = Boolean.valueOf(req.getParameter("store"));
        
        
        
      //For log
       day = DateManager.getCurrentDay();
       month = DateManager.getCurrentMonth();
       year = DateManager.getCurrentYear();
       
       
       base_path = getServletContext().getRealPath("/output");
       
       base_path_onto_library =  getServletContext().getRealPath("/ontologies");
       
       
       
       String port="";
	   if (req.getServerPort()!=80){
	   		port = ":" + req.getServerPort();
	   }
	   
	   //Relative path within tomcat
	   String tomcat_path = getServletContext().getInitParameter("path");
       base_uri = getProtocolName(req.getProtocol()) + "://" + req.getServerName() + port + "/" + tomcat_path;
       System.out.println("Base uri: " + base_uri + "\nTomcat path: " + tomcat_path);
       
       //getServletContext().getServerInfo());
       
       
       //Create Output file
       output_file_manager = new HTMLResultsFileManager(base_path, base_uri, name, logmap_version, uri1, uri2);
       
       
       //REVISE parameters!! To detect further errors!
       //String action = req.getParameter("action");
       //String item = req.getParameter("item");       
       //LogMap2_launch launch = new LogMap2_launch(Integer.valueOf(item));       
       //int value = launch.getValue();
       
       
       //Call LogMap... before or after sending data??
       //Create new thread??
       
       out.println("<br><p><input type=\"button\" value=\"Back to request form\" onClick=\"window.location.reload()\"></p>");
       
       out.println("<fieldset>");
       out.println("<p><b>Your request has been received. Many thanks " + name +" for using LogMap's Web facility.</b></p>");
       out.println("<ul>");
       //out.println("<li><b>Ontology 1:</b> " + uri1 + "</li>");
       //out.println("<li><b>Ontology 2:</b> " + uri2 + "</li>");
       out.println("<li><b>Ontology 1:</b> " + "<a href=\""+uri1 +"\" target=\"_blank\">" + uri1 +  "</a></li>");
       out.println("<li><b>Ontology 2:</b> " + "<a href=\""+uri2 +"\" target=\"_blank\">" + uri2 +  "</a></li>");
       out.println("<li><b>Version:</b> " + logmap_version + "</li>");       
       out.println("</ul>");
       
       //Commented to avoid people putting fake emails 
       //out.println("<p><b>The progress of the matching process can be followed from:</b> " + 
       //	   "<a href=\""+output_file_manager.getHTMLResultsURI()+"\" target=\"_blank\">" + output_file_manager.getHTMLResultsURI() + 
       //	   "</a>   <FONT COLOR=\"red\"><i>(please, record this link)</i></FONT></p>");
       
       //out.println("<p><b>We have also sent you an email to</b> '" + email + "' <b>with this information.</b></p>");
       
       out.println("<p><b>We have sent you an e-mail to</b> '" + email + "' <b>with a 'link' to the progress and results of the matching task.</b></p>");
       
       
       out.println("</fieldset>");
       
       out.println("<p><input type=\"button\" value=\"Back to request form\" onClick=\"window.location.reload()\"></p>");
       
       
       output_path =  base_path + output_file_manager.getRelativeOutputPath();
       output_uri = base_uri + "/output" + output_file_manager.getRelativeOutputPath();
       
       
       
       
       
    
       
       
       //CALL LOGMAP and SEND STATUS--> give HTML manager
       try{
    	   LogMapThread logmap = new LogMapThread(
    			   day, month, year, name, org, prj, email, uri1, uri2, logmap_version, base_path, base_path_onto_library, base_uri, output_path, output_uri, output_file_manager, useReasoner, useELReasoner, useMOREReasoner, storeURIs);
    			   
    	   new Thread(logmap).start();
       }
       catch (Exception e){
    	   //out.println("<p>ERROR CALLING LOGMAP</p>");
    	   //out.println(e.getMessage());
       }
           
       //out.println("<p>CALL LOGMAP OK</p>");
     
     }
     
     
     
    
     
     
     private String getProtocolName(String protocol){
         
         int slash = protocol.indexOf('/');
         if(slash==-1)
             return protocol;
         else
             return ((String) protocol.subSequence(0,slash)).toLowerCase();
     }
     
     
     
     
     
     
     
     /**
      * Sends mail and calls LogMap
      * @author root
      *
      */
     class LogMapThread implements Runnable {
    	 
    	 
    	 String T_day;
    	 String T_month;
    	 String T_year;
    	 String T_name;
    	 String T_org;
    	 String T_prj;
    	 String T_email;
    	 String T_uri1;
    	 String T_uri2;
    	 String T_logmap_version;
    		
    	 String T_base_path;
    	 String T_base_path_onto_library;
    	 String T_base_uri;
    		 
    	 String T_output_path;
    	 String T_output_uri;
    		
    	 HTMLResultsFileManager T_output_file_manager;
    	 
    	 boolean T_useReasoner;
    	 boolean T_useELReasoner;
    	 boolean T_useMOREReasoner;
    	 
    	 boolean T_storeURIs;
    	 
    	 boolean T_interactivity;
    	 
    	 int id_task;
    	
    	 int version;
    	 
    	 
    	 LogMapThread(
    			 String day,
    			 String month,
    			 String year,  		
	    		 String name,
	    		 String org,
	    		 String prj,
	    		 String email,
	    		 String uri1,
	    		 String uri2,
	    		 String logmap_version,	    		
	    		 String base_path,
	    		 String base_path_onto_library,
	    		 String base_uri,	    		 
	    		 String output_path,
	    		 String output_uri,
	    		 HTMLResultsFileManager output_file_manager,
	    		 boolean useReasoner,
	    		 boolean useELReasoner,
	    		 boolean useMOREReasoner,
	    		 boolean storeURIs) {	    		 
    		 
    		 T_day=day;
        	 T_month=month;
        	 T_year=year;
        	 T_name=name;
        	 T_org=org;
        	 T_prj=prj;
        	 T_email=email;
        	 T_uri1=uri1;
        	 T_uri2=uri2;
        	 T_logmap_version=logmap_version;
        		
        	 T_base_path=base_path;
        	 T_base_path_onto_library=base_path_onto_library;
        	 T_base_uri=base_uri;
        		 
        	 T_output_path=output_path;
        	 T_output_uri=output_uri;
        	 //T_output_file_manager=output_file_manager;//.clone();
        	 T_output_file_manager=output_file_manager.duplicate();
        	 
        	 T_useReasoner=useReasoner;
        	 T_useELReasoner=useELReasoner;
        	 T_useMOREReasoner=useMOREReasoner;
        	 
        	 T_storeURIs=storeURIs;
    	 }
    	 
    	 
    	 
    	 private void writeEntryLog() throws Exception{
        	 
        	 id_task=100;
        	 
        	 String log_file = T_base_path + "/log.txt";
        	 
        	 
        	 WriteFile log = new WriteFile(log_file, true);//append
        	 
        	 //Action refers to the origin of URIS (local or web)
        	 log.writeLine(T_year + "|" + T_month + "|" + T_day + "|" + T_name + "|" + T_org + "|" + T_prj+ "|" + T_email + "|" + T_uri1 + "|" + T_uri2 + 
        			 "|" + T_logmap_version + "|" + "|" + T_output_file_manager.getHTMLResultsURI());
        	 
        	 log.closeBuffer();
        	 
        	 
        	 ReadFile reader = new ReadFile(log_file);		 
    		 while (reader.readLine() != null){
    			 id_task++;
    		 }
    		 reader.closeBuffer();
        	 
         }
    	 
    	 
    	 private void writeLibrary() throws Exception{
        	 
        	 String library_file = T_base_path_onto_library + "/library.txt";
        	 
        	 Set<String> currentURIs = new HashSet<String>();
        	 ReadFile reader = new ReadFile(library_file); 
        	 String line;
 			 while ((line = reader.readLine()) != null){ 			 
 				currentURIs.add(line);
 			 }
 			 reader.closeBuffer();
        	 
        	 
        	 WriteFile lib_writer = new WriteFile(library_file, true);//append
        	 
        	 if (!currentURIs.contains(T_uri1)){
        		 lib_writer.writeLine(T_uri1);
        		 currentURIs.add(T_uri1);
        	 }
        	 if (!currentURIs.contains(T_uri2)){
        		 lib_writer.writeLine(T_uri2);
        	 }        	 
        	  
        	 lib_writer.closeBuffer();
        	 currentURIs.clear();
        	 
        	 
        	 
         }
         
    	 private void sendMail(String mail, String subject, String text){
    		 
    		 String email = getServletContext().getInitParameter("email");
    		 String passwd_email = getServletContext().getInitParameter("passwd");
    		 
    		 new SendMail(mail, subject, text, email, passwd_email);
    	 }
    	 
         
         private void sendMailInit(){
        	 //Send mail
             //--------------------
        	         	 
             String subject = "LogMap: your matching task (id="+ id_task + ", email 1 of 2)";
             
             String reasoningTask="You will receive another email once the matching task has finished.\n\n";
        	 if (T_useReasoner || T_useELReasoner || T_useMOREReasoner){
        		 reasoningTask = "You will receive another two emails once the matching task and the satisfiability test have finished.\n\n";
        		 subject = "LogMap: your matching task (id="+ id_task + ", email 1 of 3)";
        	 }
        	 else if (T_interactivity){
        		 reasoningTask = "You will receive another two emails: (1) when the mappings are ready to be assessed (user interactivity) and (2) when the matching process has been finished.\n\n";
        		 subject = "LogMap: your matching task (id="+ id_task + ", email 1 of 2-3)";
        	 }
             
             String text_mail =  "Dear " + T_name + ",\n\n" +
          		  "Many thanks for using LogMap's Web facility. Your request has been successfully received:\n" +
          		  "\tOntology 1:" + T_uri1 + "\n" +
          		  "\tOntology 2:" + T_uri2 + "\n" +
          		  "\tVersion: " + T_logmap_version + "\n\n" + 
          		  "The progress of the matching process can be followed from: " + T_output_file_manager.getHTMLResultsURI() + "\n\n" +
          		  
				  reasoningTask +
          		            		   
          		  "Best regards\n" +
          		  "LogMap team.\n" +
        		  "----\n" +
        		  "LogMap's Web facility: " + T_base_uri;
      	   	//new SendMail(T_email, subject, text_mail);
            sendMail(T_email, subject, text_mail);
         }
         
         
         private void sendMailFinal(){
        	 //Send mail
             //--------------------
        	 
        	 String subject = "LogMap: your mappings are ready (id="+ id_task+ ", email 2 of 2)";
        	 String reasoningTask="";
        	 if (T_useReasoner || T_useELReasoner || T_useMOREReasoner){
        		 reasoningTask = "You will receive another email once the satisfiability test of the integrated ontology has finished.\n\n";
        		 subject = "LogMap: your mappings are ready (id="+ id_task+ ", email 2 of 3)";
        	 }
        	 
             
             String text_mail =  "Dear " + T_name + ",\n\n" +
          		  "Your task has been completed.\n" +
          		  "The output mappings and the integrated ontology can be retrieved from: " + T_output_file_manager.getHTMLResultsURI() + "\n\n" +
          		  
          		  reasoningTask +
          		  
    			  "If you detect any error or you are not satisfied with the output mappings, please reply to this email. We will try to answer you within the next 24 hours.\n\n" +
          		  
				  "Many thanks again for using LogMap's Web facility.\n\n" + 
          		  
          		  "Best regards\n" +
          		  "LogMap team.\n" +
        		  "----\n" +
        		  "LogMap's Web facility: " + T_base_uri;
      	   	//new SendMail(T_email, subject, text_mail);
             sendMail(T_email, subject, text_mail);
         }
         
         
         private void sendMailInteractivityReady(){
        	 //Send mail
             //--------------------
        	 String subject = "LogMap: your mappings are ready to be assessed (id="+ id_task+ ", email 2 of 3)";
        	 //Always 2 of 3 (we will send always a third mail! After reasoning or just after interactivity)
        	 
        	 String reasoningTask="";
        	 //if (T_useReasoner || T_useELReasoner){
        	 //	 reasoningTask = "You will receive another email once the satisfiability test of the integrated ontology has finished.\n\n";
        	 //	 subject = "LogMap: your mappings are ready (id="+ id_task+ ", email 2 of 3)";
        	 //}
        	 
             
             String text_mail =  "Dear " + T_name + ",\n\n" +
          		  //"Your task has been completed.\n" +
          		  " A subset of the mappings computed by LogMap requires your feedback: " + T_output_file_manager.getHTMLResultsURI() + "\n\n" +
          		  
          		  reasoningTask +
          		  
    			  "If you detect any error or you are not satisfied with interactivity process, please reply to this email. We will try to answer you within the next 24 hours.\n\n" +
          		  
				  "Many thanks again for using LogMap's Web facility.\n\n" + 
          		  
          		  "Best regards\n" +
          		  "LogMap team.\n" +
        		  "----\n" +
        		  "LogMap's Web facility: " + T_base_uri;
      	   	//new SendMail(T_email, subject, text_mail);
             sendMail(T_email, subject, text_mail);
         }
         
         
         private void sendMailReasoning(){
        	 //Send mail
             //--------------------        	 
             String subject = "LogMap: satisfiability test result (id="+ id_task + ", email 3 of 3)";
             String text_mail =  "Dear " + T_name + ",\n\n" +
          		  "The satisfiability test has been completed.\n" +
          		  "The result can be obtained from: " + T_output_file_manager.getHTMLResultsURI() + "\n\n" +
          		  
          		  "If you detect any error or you are not satisfied with the reasoning output, please reply to this email. We will try to answer you within the next 24 hours.\n\n" +
          		  
				  "Many thanks again for using LogMap's Web facility.\n\n" +
          		  
          		  "Best regards\n" +
          		  "LogMap team.\n" +
        		  "----\n" +
        		  "LogMap's Web facility: " + T_base_uri;
      	   	//new SendMail(T_email, subject, text_mail);
             sendMail(T_email, subject, text_mail);
         }
         
         
         
    	 private void sendMailError(){
    		 
    		 
    		 //Send mail
             //--------------------
             String subject = "LogMap: ERROR in your matching task (id="+ id_task + ", email 2 of 2)";
                         
        	 if (T_useReasoner || T_useELReasoner || T_useMOREReasoner || T_interactivity){
        		 subject = "LogMap: ERROR in your matching task (id="+ id_task + ", email 2 of 3)";
        	 }
             
             
             String text_mail =  "Dear " + T_name + ",\n\n" +
          		  "We have detected an error during the matching task: " + T_output_file_manager.getHTMLResultsURI() + "\n\n" +
            		      		  
    			  "We will diagnose the possible causes of the error and we will come back to you within the next 24 hours.\n\n" +
          		  
          		  "Best regards\n" +
          		  "LogMap team.\n" +
          		  "----\n" +
          		  "LogMap's Web facility: " + T_base_uri;
      	   	//new SendMail(T_email, subject, text_mail);
             sendMail(T_email, subject, text_mail);
    		 
    	 }
    	 
    	 
    	 
    	 private void sendMailErrorReasoning(){
    		 
    		 
    		 //Send mail
             //--------------------
             String subject = "LogMap: ERROR in the satisfiability test (id="+ id_task + ", email 3 of 3)";
             String text_mail =  "Dear " + T_name + ",\n\n" +
          		  "We have detected an error when reasoning with the integrated ontology: " + T_output_file_manager.getHTMLResultsURI() + "\n\n" +
            		      		  
    			  "We will diagnose the possible causes of the error and we will come back to you within the next 24 hours.\n\n" +
          		  
          		  "Best regards\n" +
          		 "LogMap team.\n" +
         		  "----\n" +
         		  "LogMap's Web facility: " + T_base_uri;
      	   	//new SendMail(T_email, subject, text_mail);
             sendMail(T_email, subject, text_mail);
    		 
    		 
    	 }
    	 
    	 
    	 private void sendMailErrorReasoningNoOWL2(){
    		 
    		 
    		 //Send mail
             //--------------------
             String subject = "LogMap: ERROR in the satisfiability test (id="+ id_task + ", email 3 of 3)";
             String text_mail =  "Dear " + T_name + ",\n\n" +
          		  "We have detected an error when reasoning with the integrated ontology: " + T_output_file_manager.getHTMLResultsURI() + "\n\n" +
            		
				  "One of the input ontologies may contain axioms outside OWL 2 DL (http://www.w3.org/TR/owl2-syntax/).\n\n" +
				  
    			  "We will diagnose the possible causes of the error and we will come back to you within the next 24 hours.\n\n" +
          		  
          		  "Best regards\n" +
          		 "LogMap team.\n" +
         		  "----\n" +
         		  "LogMap's Web facility: " + T_base_uri;
      	   	//new SendMail(T_email, subject, text_mail);
             sendMail(T_email, subject, text_mail); 
    		 
    	 }
    	 
    	 
    	 
    	 /**
    	  * @deprecated
    	  */
    	 private void sendMailErrorReasoningTimeout(){
    		 
    		 //Send mail
             //--------------------
             String subject = "LogMap: TIMEOUT in the satisfiability (id="+ id_task + ", email 3 of 3)";
             String text_mail =  "Dear " + T_name + ",\n\n" +
          		  "Timeout (1 hour) when reasoning with the integrated ontology: " + T_output_file_manager.getHTMLResultsURI() + "\n\n" +
            		      		  
    			  "We will diagnose the possible causes of the error and we will come back to you within the next 24 hours.\n\n" +
          		  
          		  "Best regards\n" +
          		  "LogMap team.\n" +
       		      "----\n" +
       		      "LogMap's Web facility: " + T_base_uri;
      	   	//new SendMail(T_email, subject, text_mail);
             sendMail(T_email, subject, text_mail);
    		 
    	 }
    	 
    	 
    	 
    	 private void sendMailParsing(){
    		 
    		 //Send mail
             //--------------------
             String subject = "LogMap: ERROR in your matching task (id="+ id_task + ", email 2 of 2)";
                         
        	 if (T_useReasoner || T_useELReasoner || T_useMOREReasoner || T_interactivity){
        		 subject = "LogMap: ERROR in your matching task (id="+ id_task + ", email 2 of 3)";
        	 }
             
             String text_mail =  "Dear " + T_name + ",\n\n" +
          		          		 
          		  "We have detected an error when parsing the input ontologies. Possible causes: the given URI is not accessible, the URI contains a non permitted character, or the given ontology format is not currently accepted.\n\n" + 
            				 		"Note that we accept the same ontology formats as the OWL API (http://owlapi.sourceforge.net/): " +
            				 		"RDF/XML, OWL/XML, OWL Functional, OBO, KRSS, and Turtle\n\n" +
          		        		  
    			  "If you are interested in using a different input format we could adapt LogMap to your requirements. Please please reply to this email " +  
    			  "and we will try to answer you within the next 24 hours.\n\n" +
          		  
          		  "Best regards\n" +
          		  "LogMap team.\n" +
       		      "----\n" +
       		      "LogMap's Web facility: " + T_base_uri;
      	   	//new SendMail(T_email, subject, text_mail);
             sendMail(T_email, subject, text_mail);
    		 
    	 }
    	 
    	 
    	 
    	 
    	 

         public void run() {
        	 
        	 //long id = Calendar.getInstance().getTimeInMillis();
        	         	 
        	 //Logmap
        	 //String [] args = new String[1];
        	 //LogMap2Main.main(args);
        	 try{
        		 
        		 T_interactivity = T_logmap_version.equals("LogMap with interactivity");
        		 
        		 //Update log
                 writeEntryLog();
            	 
            	 //Mail
            	 sendMailInit();
            	 
            	 //Sleep??
            	 
            	 
            	 boolean interactivity_finished = false;
            	 
	        	 if (T_logmap_version.equals("LogMap with reasoning")){
	        		 new LogMap_WebService(T_uri1, T_uri2, true, T_output_path, T_output_uri, T_output_file_manager);
	        		 version = Utilities.LOGMAP;
	        	 }
	        	 else if (T_logmap_version.equals("LogMap with interactivity")){
	        		 
	        		 int reasoner;
	        		 if (T_useReasoner) {
	        			 reasoner = ReasonerManager.HERMIT;
	        		 }
	        		 else if (T_useELReasoner){
	        			 reasoner = ReasonerManager.ELK;
	        		 }
	        		 else if (T_useMOREReasoner){
	        			 reasoner = ReasonerManager.MORe;
	        		 }
	        		 else {
	        			 reasoner = ReasonerManager.NONE;
	        		 }
	        		 
	        		 LogMapInteractivity_WebService ilogmap= new LogMapInteractivity_WebService(
	        				 T_uri1, T_uri2, true, T_output_path, T_output_uri, T_output_file_manager, 
	        				 T_email, T_name, String.valueOf(id_task), reasoner);
	        		 
	        		 interactivity_finished = ilogmap.isProcessFinished();
	        		 
	        		 version = Utilities.LOGMAPINTERACTIVITY;	        		 
	        	 }
	        	 else if (T_logmap_version.equals("LogMap without reasoning")){        		 				    
	        		 new LogMap_WebService(T_uri1, T_uri2, false, T_output_path, T_output_uri, T_output_file_manager);
	        		 version = Utilities.LOGMAPMENDUM;
	        	 }
	        	 else{
	        		 //Logmap Lite
	        		 //new LogMap_WebService(uri1, uri2, false, output_path, output_uri, output_file_manager);
	        		 new LogMapLite_WebService(T_uri1, T_uri2, T_output_path, T_output_uri, T_output_file_manager);
	        		 version=Utilities.LOGMAPLITE;
	        	 }
	        	 
	        	 
	        	 //Send Final Mail
	        	 //---------------------
	        	 if  (version == Utilities.LOGMAPINTERACTIVITY && !interactivity_finished){
	        		sendMailInteractivityReady(); 
	        		
	        		//TODO check if there are mappings to assess
	        	 }
	        	 else{     		 
	        		 sendMailFinal();
	        	 }
	        	 
	        	 
	        	 
	        	 //Store IRIS in library
	        	 if (storeURIs)
	        		 writeLibrary();
	        	 
	        	 
	        	 //Send Final Mail
	        	 if  (version != Utilities.LOGMAPINTERACTIVITY){//We will reason at the end of the process
	        	 
		        	 //T_output_file_manager
		        	 
		        	 if (T_useReasoner){
		        		 
		        		 //T_output_file_manager.updateProgress("Checking the satisfiability of the integrated ontology with HermiT...");
		        		 
		        		 //Call reasoning
		        		 //integrated onto IRI: T_output_file_manager.getIntegratedOntologyModulesIRIStr();
		        		 new HermiT_WebService(T_output_file_manager, version, T_output_path, T_output_uri);
		        		 
		        		 sendMailReasoning();
		        	 }
		        	 else if (T_useELReasoner){
		        		 
		        		 //T_output_file_manager.updateProgress("Checking the satisfiability of the integrated ontology with ELK...");
		        		 
		        		 //Call reasoning
		        		 //integrated onto IRI: T_output_file_manager.getIntegratedOntologyModulesIRIStr();
		        		 new ELK_WebService(T_output_file_manager, version, T_output_path, T_output_uri);
		        		 
		        		 sendMailReasoning();
		        	 }
		        	 
		        	 /*else if (T_useMOREReasoner){
		        		 
		        		 //T_output_file_manager.updateProgress("Checking the satisfiability of the integrated ontology with ELK...");
		        		 
		        		 //Call reasoning
		        		 //integrated onto IRI: T_output_file_manager.getIntegratedOntologyModulesIRIStr();
		        		 new MORe_WebService(T_output_file_manager, version, T_output_path, T_output_uri);
		        		 
		        		 sendMailReasoning();
		        	 }*/
	        	 }
        	 }
        	 catch (OWLOntologyCreationException e){
        		 T_output_file_manager.updateProgress(
        				 "<FONT COLOR=\"red\">We have detected an ERROR when parsing the input ontologies.</FONT> " +
        						"Possible causes: the given URI is not accessible, the URI contains a non permitted character, or the given ontology format is not currenly accepted. " +
        				 		"Note that we accept the same ontology formats as the <a haref=\"http://owlapi.sourceforge.net/\" target=\"_blank\">OWL API</a>: " +
        				 		"RDF/XML, OWL/XML, OWL Functional, OBO, KRSS, and Turtle. If you are interested in using a different input format we could adapt LogMap to your requirements. " +
        				 		"Please send us an email to \"logmap.om.tool@gmail.com\", we will try to answer you within the next 24 hours.");
        		 
        		 sendMailParsing();
        	 }
        	 catch (TimeoutException e){ //Not used any more
        		 T_output_file_manager.updateProgress("<FONT COLOR=\"red\">TIMEOUT when reasoning with the integrated ontology.</FONT>. " 
        				 + "We will diagnose the possible causes of the error and we will come back to you within the next 24 hours.\n\n");
        		 
        		 sendMailErrorReasoningTimeout();
        	 }
        	 
        	 catch (IllegalArgumentException e){
        		 
        		 T_output_file_manager.updateProgress("<FONT COLOR=\"red\">We have detected an ERROR when reasoning with the integrated ontology.</FONT> " +
        		 		"One of the input ontologies may contain axioms outside <a href=\"http://www.w3.org/TR/owl2-syntax/\" target=\"_blank\">OWL 2 DL</a>");     			
        		 
        		 sendMailErrorReasoningNoOWL2();
        		
        	 }
        	 
        	 catch (LogMapReasoningException e){
        		 T_output_file_manager.updateProgress("<FONT COLOR=\"red\">We have detected an ERROR when reasoning with the integrated ontology.</FONT> " 
        				 + "We will diagnose the possible causes of the error and we will come back to you within the next 24 hours.\n\n");
        		 
        		 sendMailErrorReasoning();
        	 }
        	 catch (Exception e){
        		 
        		 e.printStackTrace();
        		 
        		 T_output_file_manager.updateProgress("<FONT COLOR=\"red\">We have detected an ERROR in the matching task</FONT>. " 
        				 + "We will diagnose the possible causes of the error and we will come back to you within the next 24 hours.\n\n");
        		 
        		 sendMailError();
        	 }
         }
     }
 
     

    
    
}  

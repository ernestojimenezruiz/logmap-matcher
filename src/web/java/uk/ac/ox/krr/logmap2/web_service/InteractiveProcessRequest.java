package uk.ac.ox.krr.logmap2.web_service;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.SingleThreadModel;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntology;

import uk.ac.ox.krr.logmap2.OntologyLoader;
import uk.ac.ox.krr.logmap2.io.LogOutput;
import uk.ac.ox.krr.logmap2.mappings.objects.MappingObjectStr;
import uk.ac.ox.krr.logmap2.reasoning.ReasonerManager;
import uk.ac.ox.krr.logmap2.utilities.Utilities;
import uk.ac.ox.krr.logmap2.oaei.reader.MappingsReaderManager;

import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class InteractiveProcessRequest  extends HttpServlet implements SingleThreadModel {


	private String base_path;
	private String base_uri;
	
	private HTMLResultsFileManager output_file_manager;
	private InteractiveProcess_WebService interactiveProcess;
	
	private PrintWriter out;
	
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
	 * Call Interactivity request with given parameters
	 */
     public void doPost(HttpServletRequest req, HttpServletResponse rsp)   
                   throws ServletException, IOException {    	 
    	 
    	rsp.setContentType("text/html");
     	//PrintWriter out = rsp.getWriter();
    	out = rsp.getWriter();
    	
     	
     	//ajax.send("folder="+folder+"&feedback="+feedback);
     	String folder = req.getParameter("folder");
     	String feedback = req.getParameter("feedback");
     	String ontouri1 = req.getParameter("ontouri1");
     	String ontouri2 = req.getParameter("ontouri2");
     	
    	
     	System.out.println("Folder: '" + folder + "'");
     	System.out.println("feedback: '" + feedback + "'");
     	System.out.println("onto1: '" + ontouri1 + "'");
     	System.out.println("onto2: '" + ontouri2 + "'");
     	
     	//out.println("<br><p>" + folder + "</p>");
     	
     	//out.println("<br><p>" + feedback + "</p>");
     	
    	
    	base_path = getServletContext().getRealPath("/output") + folder;
        
        
        String port="";
 	    if (req.getServerPort()!=80){
 	   		port = ":" + req.getServerPort();
 	    }
 	    
 	    //Relative path within tomcat
 	    String tomcat_path = getServletContext().getInitParameter("path");
        
 	    base_uri = getProtocolName(req.getProtocol()) + "://" + req.getServerName() + port + "/" + tomcat_path;
        //getServletContext().getServerInfo());
        
        base_uri = base_uri + "/output" + folder;
        
        
        //Create Output file
        output_file_manager = new HTMLResultsFileManager(base_path, base_uri, ontouri1, ontouri2);
        
        
        //load mappings, apply feedback and show new information
        interactiveProcess = new InteractiveProcess_WebService(output_file_manager.getPathMappings2Assess());
        
        
        if (feedback.length()>0 && !feedback.equals("end")){
        	//apply feedback //TODO
        	//Update remaining mappings
        	interactiveProcess.applyFeedback(feedback);
        	
        	//store new mappings
        	interactiveProcess.storeSessionData();
        	
        }
        else if (feedback.equals("end")){
        	//Update remaining mappings
        	//TODO
    		//Apply automatic heuristics!
        	interactiveProcess.applyAutomaticHeuristics();
        	
        	interactiveProcess.storeSessionData();
        	
        }
        //else then empty feedback (session restart)
        	
        
        
        //if no remaining mappings then finish!! 
        if (interactiveProcess.getRemainingMappings() > 0){
                	
        	
	        MappingObjectInteractivity_WebService mapping;
	        
	        List<Integer> list_id_topmappings = interactiveProcess.getTopMappings2Show();
	        
	        //Header
	        printHeaderMappingField(
	        		list_id_topmappings.size(), 
	        		interactiveProcess.getRemainingMappings(), 
	        		interactiveProcess.getNumberUserInterventions());
	        
	        int num = 0;
	        
	        for (int i : list_id_topmappings){
	        	
	        	mapping = interactiveProcess.getListOrderedMappings2Ask().get(i);
	        	
	        	//print mappings
	        	printMapping(
						i, //important to identify mappings
						num, //important top access mapping in form
						mapping.getURI1(),
						mapping.getURI2(),
						mapping.getLabel1(),
						mapping.getLabel2(),
						mapping.getIde1(),
						mapping.getIde2(),
						mapping.getDirMapping(), 
						mapping.getSemSim(),
						mapping.getLexSim(),
						mapping.getSuperClasses1_Str(),
						mapping.getSuperClasses2_Str(),
						mapping.getSubClasses1_Str(),
						mapping.getSubClasses2_Str(),
						mapping.getSynonyms1_Str(),
						mapping.getSynonyms2_Str(),
						getMappingRepresentation(mapping.getMappingsInconflict()),
						getMappingRepresentation(mapping.getAmbiguousMappings())
						);
				
				num++;
	        	
	        }
	        
	        //Tail
	        printTailMappingField(list_id_topmappings.size());
    	
        }
        else{
        
        	//Updates NumberMappingAssessedByUser
        	interactiveProcess.checkUserAssessment();
        	
        	//Modify the file index.html (remove bottom (keep until) "<div id="mappings">")        	
        	//add button to refresh page (add new entry in progress)
        	
        	String progress = "<b>The interactivity process has finished.</b> ";
        	if (interactiveProcess.didUserFinihsedInteractivity()){
        		progress += "The user stopped the interactive process after '" + interactiveProcess.getNumberUserInterventions() + "' feedback iterations. " +
        				"The rest of the mappings will be assessed heuristically. ";
        	}
        	else{
        		progress += "The interactive process was completed after '" + interactiveProcess.getNumberUserInterventions() + "' feedback iterations. ";
        	}        	
        	progress += "Assessed mappings by user: '" + interactiveProcess.getNumberMappingAssessedByUser() + " out of " + interactiveProcess.getListOrderedMappings2Ask().size() + "'.";
        	
        	//Prints end of profress in div
        	printEndOfProcess(progress);
        	
        	//We remove interactivity div and we add update prgress
        	output_file_manager.updateProgressEndInteractivity(progress);
     	
        	
        	//New logmap thread for final diagnosis and reasoning if flag...
        	//and send mail... the process will be very similar...
        	//We only need to store mappings and get references to update progress!!
        	
        	

        	
        	try{
        		EndInteractivityThread endProcess = 
        				new EndInteractivityThread(
        						interactiveProcess.getName(),
        						interactiveProcess.getEmail(),
        						interactiveProcess.getIDTask(),
        						interactiveProcess.getReasoner(),
        						interactiveProcess.getListOrderedMappings2Ask(),
        						output_file_manager);
        		
        		new Thread(endProcess).start();
            }
            catch (Exception e){
         	   //out.println("<p>ERROR CALLING LOGMAP</p>");
         	   //out.println(e.getMessage());
            }
        	
        	
        	
        	
        }
             
               
        //End progress... it may also call this class but then a different process...
        //
     	
	
     }
     
     
	private String getProtocolName(String protocol){
         
         int slash = protocol.indexOf('/');
         if(slash==-1)
             return protocol;
         else
             return ((String) protocol.subSequence(0,slash)).toLowerCase();
     
	}
	
	
	
	private Set<String> getMappingRepresentation(Set<Integer> mappings_ids){
		
		Set<String> mappings_ids_Str = new HashSet<String>();
		
		String mapping_representation;
		
		//writer.writeLine(ns1 + " <b>" + label1 + "</b>   " + dir + "   " + ns2 + " <b>" + label2 + "</b>, " +
		
		for (int i : mappings_ids){
			
			mapping_representation = 
					"<b>" +
							interactiveProcess.getListOrderedMappings2Ask().get(i).getLabel1() + "   " +
							interactiveProcess.getListOrderedMappings2Ask().get(i).getDirMapping() + "   " +
							interactiveProcess.getListOrderedMappings2Ask().get(i).getLabel2() + "</b>&nbsp;&nbsp;&nbsp; " +
					"semantic sim: " + interactiveProcess.getListOrderedMappings2Ask().get(i).getSemSim() + "&nbsp;&nbsp;  lexical sim: " + interactiveProcess.getListOrderedMappings2Ask().get(i).getLexSim();
			
			mappings_ids_Str.add(mapping_representation);
			
		}
		
		
		return mappings_ids_Str;
		
	}
	
	
	
	private void printEndOfProcess(String progress){
		
		String button_refresh = "<input type=\"button\" value=\"Refresh\" onClick=\"window.location.reload()\">";
		
		out.println("<p>" + progress + " Refresh the window to see the results of the matching task: " + button_refresh + "</p>");
		
	}
	
	
	private void printHeaderMappingField(int showing, int remaining, int userFeedbacks){
		
		out.println("<fieldset>");
		out.println("<legend><b>Mappings requiring user feedback</b> " +
				"(showing "+ showing + " out of " + remaining + " remaining mappings). <b>Feedback iterations: </b>" + userFeedbacks + "</legend>");
		
		out.println("<ol>");
	
	}
	
	
	private void printTailMappingField(int showing){
		
		out.println("</ol>");		
		out.println("</fieldset>");
		
		//Hidden div num of showed mappings (size of structure)
		out.println("<div id=\"hiddeninfo1\"  style=\"DISPLAY: none\" >"); 
		out.println("<label id=\"numcurrentmappings\">" + showing + "</label>"); //					
		out.println("</div>");
		
	
	}
	
	
	private void printMapping(
			int id_mapping,//identifier mapping (its order in structure)
			int num_mapping, ///the number of mapping in form
			String uri1, 
			String uri2, 
			String label1, 
			String label2,
			int ide1,
			int ide2,
			String dir, 
			double scope,
			double lex,			
			String superclasses1,
			String superclasses2,
			String subclasses1,
			String subclasses2,
			String synonyms1,
			String synonyms2,
			Set<String> mappingsInConflict,
			Set<String> mappingsAmbiguous){
		
		
		//Put in the value
		
		//We access mapping by name (mapping0...mapping10)
		//The the selected value give as if a mapping is selected to be added or deleted (add_21_56)
		String radio_add = "<input type=\"radio\" name=\"mapping" + num_mapping + "\" id=\"add" + num_mapping + "\" value=\"add_" + id_mapping +  "\" checked/>";
		String radio_add_label = "<label for=\"add" + num_mapping + "\"><FONT COLOR=\"green\">Add</FONT></label>";
		
		//String radio_addheur = "<input type=\"radio\" name=\"mapping" + id_mapping + "\" id=\"addheur" + id_mapping + "\" value=\"addheur_" + ide1 + "-" + ide2 +  "\" checked/>";
		//String radio_addheur_label = "<label for=\"addheur" + id_mapping + "\"><FONT COLOR=\"green\">Add (ambiguity criteria)</FONT></label>";
		
		String radio_del = "<input type=\"radio\" name=\"mapping" + num_mapping + "\" id=\"del" + num_mapping + "\" value=\"del_" + id_mapping + "\"/>";
		String radio_del_label = "<label for=\"del" + num_mapping + "\"><FONT COLOR=\"red\">Discard</FONT></label>";
		
		//String radio_delheur = "<input type=\"radio\" name=\"mapping" + id_mapping + "\" id=\"delheur" + id_mapping + "\" value=\"delheur_" + ide1 + "-" + ide2 + "\"/>";
		//String radio_delheur_label = "<label for=\"delheur" + id_mapping + "\"><FONT COLOR=\"red\">Discard (ambiguity criteria)</FONT></label>";
		
		
		String check_amb_criteria = "<input type=\"checkbox\" name=\"amb" + num_mapping + "\" id=\"amb" + num_mapping + "\" value=\"Ambiguity criteria\" />";
        String check_amb_criteria_label = "<label for=\"amb" + num_mapping + "\">Use the ambiguity criteria</label>";
		
		out.println("<li class=\"mapping\">");
		out.println("<p>");
		
		out.println("<b>" + label1 + "   " + dir + "   " + label2 + "</b>" + "<br />" +
				//"semantic sim: <b>" + scope + "</b>,  lexical sim: <b>" + lex + "</b>&nbsp;&nbsp;&nbsp;&nbsp;" +
				"semantic sim: " + scope + "&nbsp;&nbsp;&nbsp;  lexical sim: " + lex + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
				
				"&nbsp;&nbsp;&nbsp;&nbsp;" + radio_add + radio_add_label + 
				//"&nbsp;&nbsp;" + radio_addheur + radio_addheur_label + 
				"&nbsp;&nbsp;&nbsp;&nbsp;" + radio_del + radio_del_label + 
				//"&nbsp;&nbsp;" + radio_delheur + radio_delheur_label +
				"&nbsp;&nbsp;&nbsp;&nbsp;" + check_amb_criteria + check_amb_criteria_label);
		
		out.println("<br /><br />"); 
				
		out.println(
				"<a onclick =\"javascript:ShowHide('DivURIs" + id_mapping + "')\" href=\"javascript:;\" >Show/Hide full URIs</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
				"<a onclick =\"javascript:ShowHide('DivSynonyms" + id_mapping + "')\" href=\"javascript:;\" >Show/Hide synonyms</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
				"<a onclick =\"javascript:ShowHide('DivScope" + id_mapping + "')\" href=\"javascript:;\" >Show/Hide scope</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
				"<a onclick =\"javascript:ShowHide('DivConflict" + id_mapping + "')\" href=\"javascript:;\" >Show/Hide mappings in conflict</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
				"<a onclick =\"javascript:ShowHide('DivAmbiguity" + id_mapping + "')\" href=\"javascript:;\" >Show/Hide ambiguous mappings</a>"
				);
		
		out.println("</p>");
		
		
		//Div IRIS
		//-------------
		out.println("<div id=\"DivURIs" + id_mapping + "\" style=\"DISPLAY: none\" >");
		out.println("<fieldset class=\"uris\">");
		out.println("<legend>Full URIs</legend>");
		//out.println("<label>Put info about direct superclasses</label>");
		out.println("<ul>");
		out.println("<li><b>URI 1</b>: " + uri1 +"</li>");
		out.println("<li><b>URI 2</b>: " + uri2 +"</li>");
		out.println("</ul>");
		out.println("</fieldset>");
		out.println("<br />");
		out.println("</div>");

		
		//Div synonyms
		//-------------
		out.println("<div id=\"DivSynonyms" + id_mapping + "\" style=\"DISPLAY: none\" >");
		out.println("<fieldset class=\"synonyms\">");
		out.println("<legend>Synonyms and alternative labels</legend>");
		//out.println("<label>Put info about direct superclasses</label>");
		out.println("<ul>");
		out.println("<li><b>Synonyms 1</b>: " + synonyms1 +"</li>");
		out.println("<li><b>Synonyms 2</b>: " + synonyms2 +"</li>");
		out.println("</ul>");
		out.println("</fieldset>");
		out.println("<br />");
		out.println("</div>");
		
		//Div Scope
		//-------------
		out.println("<div id=\"DivScope" + id_mapping + "\" style=\"DISPLAY: none\" >");
		out.println("<fieldset class=\"scope\">");
		out.println("<legend>Scope information</legend>");
		//out.println("<label>Put info about direct superclasses</label>");
		out.println("<ul>");
		
		out.println("<li>Superclasses for...");
		out.println("<ul>");		
		out.println("<li><b>" + label1 + "</b>: " + superclasses1 +"</li>");
		out.println("<li><b>" + label2 + "</b>: " + superclasses2 +"</li>");
		out.println("</ul>");
		out.println("<li>Subclasses for...");
		out.println("<ul>");
		out.println("<li><b>" + label1 + "</b>: " + subclasses1 +"</li>");
		out.println("<li><b>" + label2 + "</b>: " + subclasses2 +"</li>");
		
		out.println("</ul>");
		out.println("</fieldset>");
		out.println("<br />");
		out.println("</div>");
				
		
		//Div Conflict
		//-------------
		out.println("<div id=\"DivConflict" + id_mapping + "\" style=\"DISPLAY: none\" >");
		out.println("<fieldset class=\"conflict\">");
		out.println("<legend>Mappings in conflict</legend>");
		
		
		out.println("<label>There are '" + mappingsInConflict.size() + "' mappings in conflict.</label>");
		
		if (mappingsInConflict.size()>0){
			out.println("<br />");
			out.println("<br />");
			out.println("<label>If the current mapping is ADDED the following mappings will be DISCARDED.</label>");
			out.println("<br />");
			out.println("<br />");
			
			out.println("<ul>");
			for (String mapping_str : mappingsInConflict){
				out.println("<li>" + mapping_str + "</li>");
			}
			out.println("</ul>");
		}
		out.println("</fieldset>");
		out.println("<br />");
		out.println("</div>");		
				
		
		//Div ambiguity
		//-------------
		out.println("<div id=\"DivAmbiguity" + id_mapping + "\" style=\"DISPLAY: none\" >");
		out.println("<fieldset class=\"ambiguity\">");
		out.println("<legend>Ambiguous mappings</legend>");
		
		out.println("<label>There are '" + mappingsAmbiguous.size() + "' ambiguous mappings.</label>");
		
		if (mappingsAmbiguous.size()>0){
			out.println("<br />");
			out.println("<br />");
			out.println("<label>If the AMBIGUITY CRITERIA is used and the current mapping is ADDED (respectively DISCARDED) the following mappings will be DISCARDED (respectively ADDED).</label>");
			
			out.println("<br />");
			out.println("<br />");
			
			out.println("<ul>");
			for (String mapping_str : mappingsAmbiguous){
				out.println("<li>" + mapping_str + "</li>");
			}
			out.println("</ul>");
		}
		
		out.println("</fieldset>");
		out.println("<br />");
		out.println("</div>");
				
		out.println("<br />");
		
		out.println("</li>");
		
		
		
	}
	
	
	
	
	
	
    /**
     * Finish interactivity process and sends mail
     * It will call LogMap repair and reasoner
     * @author root
     *
     */
    class EndInteractivityThread implements Runnable {
    	
    	String T_name;
    	String T_email;
    	String T_id_task;
    	int T_reasoner;
    	
    	HTMLResultsFileManager T_output_file_manager;
    	
    	String T_base_uri;
    	
    	OWLOntology onto1;
    	OWLOntology onto2;
    	private Set<MappingObjectStr> fixed_mappings = new HashSet<MappingObjectStr>();
    	private Set<MappingObjectStr> candidate_mappings  = new HashSet<MappingObjectStr>();
    	
    	List<MappingObjectInteractivity_WebService> listMappings;
    	
    	
    	EndInteractivityThread(
    			String name, 
    			String mail,
    			String id_task,
    			int reasoner,
    			List<MappingObjectInteractivity_WebService> listMappings,
    			HTMLResultsFileManager htmlFileManager){
    		
    		
    		T_name = name;
    		T_email = mail;
    		T_id_task = id_task;
    		T_reasoner = reasoner;
    		
    		this.listMappings=listMappings;
    		
    		T_output_file_manager = htmlFileManager.duplicate2();
    		
    		T_base_uri = "http://csu6325.cs.ox.ac.uk/";
    		
    	}
    	
    	
    	//Init with progress manager and something else??
    	
    	//Create LoGmapRepair web service
    	
    	 public void run() {
    		 
    		 try{
    			 
    			 T_output_file_manager.updateProgress("Starting last Mapping repair step...");
    			 
    			 //TODO
    			 MappingsReaderManager mapping_manager;
    			 
    			 
    			 //Load reliable mappings
    			 mapping_manager = 
    					 new MappingsReaderManager(T_output_file_manager.getPathReliableMappings(), MappingsReaderManager.FlatFormat);
    			 
    			 fixed_mappings.addAll(mapping_manager.getMappingObjects());
    			 
    			 
    			 //Convert Mappings Interactivity MappingObjStr: -> candidates
    			 convertMappingsInteractivity();
    			 
    			 //Load mappings2review (properties and instances) -> candidates
    			 mapping_manager = 
    					 new MappingsReaderManager(T_output_file_manager.getPathMappings2Review(), MappingsReaderManager.FlatFormat);
    			
    			 candidate_mappings.addAll(mapping_manager.getMappingObjects());
    			 
    			 
    			 //Load Ontologies
    			 loadOntologies(T_output_file_manager.getURIModule1(), T_output_file_manager.getURIModule2());
    			 
    			 
    			 //Repair mappings (and store) --> show message (progress)
    			 
    			 new LogMapRepair_WebService(
    						onto1,
    						onto2, 
    						fixed_mappings,
    						candidate_mappings,
    						T_output_file_manager.getBasePath4Mappings(),
    						T_output_file_manager);
    					
    			 
    			 //Show integrated ontology + modules
    			 showIntegrationResultsInformation();
    			 
    			 
    			 //We do not wait for interactivity
    			 sendMailFinal();
    			 
    			 
    			 
    			 //Reasoning if marked!
    			 if (T_reasoner == ReasonerManager.HERMIT){
    				 
    				 T_output_file_manager.updateProgress("Setting up the reasoner...");
    					    				 
    				 new HermiT_WebService(
    						 T_output_file_manager, Utilities.LOGMAPINTERACTIVITY, T_output_file_manager.getOutputFolder(), T_output_file_manager.getURIFolder());
    			 }
    			 else if (T_reasoner == ReasonerManager.ELK){
    				 
    				 T_output_file_manager.updateProgress("Setting up the reasoner...");
    				 
    				 new ELK_WebService(
    						 T_output_file_manager, Utilities.LOGMAPINTERACTIVITY, T_output_file_manager.getOutputFolder(), T_output_file_manager.getURIFolder());
    			 }
    			/*else if (T_reasoner == ReasonerManager.MORe){
    				 
    				 T_output_file_manager.updateProgress("Setting up the reasoner...");
    				 
    				 new MORe_WebService(
    						 T_output_file_manager, Utilities.LOGMAPINTERACTIVITY, T_output_file_manager.getOutputFolder(), T_output_file_manager.getURIFolder());
    			 }
    			
    			*/
    			 
    			 
    			 
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
    	 
    	 /**
    	  * We load ontology modules. In case of error we load original ontologies.
    	  * We have detected potential errors when loading the created modules due to non allowed characters 
    	  * (this should be solved in OWL API)
    	  * 
    	  * @param iri_str1
    	  * @param iri_str2
    	  * @throws Exception
    	  */
    	 public void loadOntologies(String iri_str1, String iri_str2) {//throws Exception{
    		
    		
    		 
    		try{ 
    			loadModules(iri_str1, iri_str2);
    		}
    		catch (Exception e){
    			
    			try{ 
    				loadOriginalOntologies(iri_str1, iri_str2);
        		}
        		catch (Exception e2){
        			e2.printStackTrace();
        		}
    			
    		}
    			
    	}
    	 
    	 /**
    	  * We try to load  modules
    	  * 
    	  * @param iri_str1
    	  * @param iri_str2
    	  * @throws Exception
    	  */
    	 private void loadModules(String iri_str1, String iri_str2) throws Exception{
    		
    		 long init, fin;
    		 
    		 OntologyLoader onto_loader1;
    		 OntologyLoader onto_loader2;
    		 
    		
    		 init = Calendar.getInstance().getTimeInMillis();
    		 onto_loader1 = new OntologyLoader(iri_str1);		
    		 fin = Calendar.getInstance().getTimeInMillis();
    		 LogOutput.print("Time loading module ontology 1 (s): " + (float)((double)fin-(double)init)/1000.0);
	    			
    		 init = Calendar.getInstance().getTimeInMillis();
    		 onto_loader2 = new OntologyLoader(iri_str2);
    		 fin = Calendar.getInstance().getTimeInMillis();
    		 LogOutput.print("Time loading module ontology 2 (s): " + (float)((double)fin-(double)init)/1000.0);
    	
    			
    		onto1=onto_loader1.getOWLOntology();
    		onto2=onto_loader2.getOWLOntology();
    			
    	}
    	 
    	 
    	 /**
    	  * We try to load original ontologies.
    	  * 
    	  * @param iri_str1
    	  * @param iri_str2
    	  * @throws Exception
    	  */
    	 private void loadOriginalOntologies(String iri_str1, String iri_str2) throws Exception{
    		
    		 long init, fin;
    		 
    		 OntologyLoader onto_loader1;
    		 OntologyLoader onto_loader2;
    		 
    		
    		 //We load original ontologies!
    		 init = Calendar.getInstance().getTimeInMillis();
    		 onto_loader1 = new OntologyLoader(T_output_file_manager.getURIOntology1());		
    		 fin = Calendar.getInstance().getTimeInMillis();
    		 LogOutput.print("Time loading original ontology 1 (s): " + (float)((double)fin-(double)init)/1000.0);
	    			
    		 init = Calendar.getInstance().getTimeInMillis();
    		 onto_loader2 = new OntologyLoader(T_output_file_manager.getURIOntology2());
    		 fin = Calendar.getInstance().getTimeInMillis();
    		 LogOutput.print("Time loading original ontology 2 (s): " + (float)((double)fin-(double)init)/1000.0);
    			
    		 onto1=onto_loader1.getOWLOntology();
    		 onto2=onto_loader2.getOWLOntology();
    			
    	}
    	 
    	 
    	 private void convertMappingsInteractivity(){
    		 
    		 for (MappingObjectInteractivity_WebService mapping : listMappings){
    			 
    			 if (mapping.isAddedFlagActive()){
	    			 
	    			 //Always class mappings
	    			 candidate_mappings.add(new MappingObjectStr(
	    					 mapping.getURI1(), mapping.getURI2(), 
	    					 mapping.getConfidence(), 
	    					 Utilities.getIntegerRepresentation4Dir(mapping.getDirMapping()), MappingObjectStr.CLASSES));
    			 }    			 
    		 }
    		 
    	 }
    	 
    	 
    	 
    	 private void showIntegrationResultsInformation(){
    	
    		 String iri_integrated_onto = T_output_file_manager.getIntegratedOntologyIRIStr();
    		 String iri_integrated_onto_modules = T_output_file_manager.getIntegratedOntologyModulesIRIStr();
    		
    		 String iri_module1 = T_output_file_manager.getURIModule1();
    		 String iri_module2 = T_output_file_manager.getURIModule2();
    		 
    		
    		 
    		 T_output_file_manager.updateProgress("Full integrated ontology: " + 
    					"<a href=\"" + iri_integrated_onto + "\">[Original ontologies + mappings]</a>, " + 
    					//"<a href=\"" + iri_integrated_onto_modules + "\">[Overlapping ontologies + mappings]</a> " + 
    					"<i>(note that this ontology only imports the respective OWL files)</i>");
    		 
    		 T_output_file_manager.updateProgress("Overlapping ontology modules: " + 
    					"<a href=\"" + iri_module1 + "\">[Module 1]</a>, " + 
    					"<a href=\"" + iri_module2 + "\">[Module 2]</a>.");
    		 
    		 T_output_file_manager.updateProgress("Integrated ontology using overlappings/modules: " + 
    					//"<a href=\"" + iri_integrated_onto + "\">[Original ontologies + mappings]</a>, " + 
    					"<a href=\"" + iri_integrated_onto_modules + "\">[Overlapping ontologies + mappings]</a> " + 
    					"<i>(note that this ontology only imports the respective OWL files)</i>");
    		 
    		}
    	 
    	 
    	 
    	 private void sendMail(String mail, String subject, String text){
    		 
    		 String email = getServletContext().getInitParameter("email");
    		 String passwd_email = getServletContext().getInitParameter("passwd");
    		 
    		 
    		 String smtp_host = getServletContext().getInitParameter("smtphost");
    		 
    		 
    		 
    		 
    		 new SendMail(mail, subject, text, email, passwd_email, smtp_host);
    	 }
    	 
    	 
    	 
    	 private void sendMailFinal(){
        	 //Send mail
             //--------------------
        	 
        	 String subject = "LogMap: your mappings are ready (id="+ T_id_task+ ", email 3 of 3)";
        	
        	 
             
             String text_mail =  "Dear " + T_name + ",\n\n" +
          		  "Your task has been completed.\n" +
          		  "The output mappings and the integrated ontology can be retrieved from: " + T_output_file_manager.getHTMLResultsURI() + "\n\n" +
          		  
          		  //reasoningTask +
          		  
    			  "If you detect any error or you are not satisfied with the output mappings, please reply to this email. We will try to answer you within the next 24 hours.\n\n" +
          		  
				  "Many thanks again for using LogMap's Web facility.\n\n" + 
          		  
          		  "Best regards\n" +
          		  "LogMap team.\n" +
        		  "----\n" +
        		  "LogMap's Web facility: " + T_base_uri;
      	   	//new SendMail(T_email, subject, text_mail);
             sendMail(T_email, subject, text_mail);
         }
    	 
    	 
    	 private void sendMailError(){
    		 
    		 
    		 String subject = "LogMap: ERROR in your matching task (id="+ T_id_task + ", email 2 of 3)";
        	 
    		 
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
             String subject = "LogMap: ERROR in the satisfiability test (id="+ T_id_task + ", email 3 of 3)";
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
    	 
    	
    }
	
	
	
	
	
	
	
}

	




	//Gets information from active sessions: modules, index, and any other object list of mappings)
		//Keep only essential info...
		//It also gets user feedback (list of mappings to keep or to remove)
	
		//keep output file
		
		//Returns the new list of ordered mappings to assess (and updates vales... mappings in, mapping to assess and discarded)
		
		
		
		
		//First interactivity session of saved ones
		//perhaps the session must be loaded. in that case we should access the info from a folder (URI)..
		//The input will be the id number
		//Create folder with interactivity ids...
	
	
		
		//Cleaning with D&G: every time we have new mappings to review FROM USER!!
	
	
	
		//One of the options of this class will be to finish the process and apply automatic heuristics, save the session or ask for moremmm 
		
	
		//When finish
		//Clean with D&G
		//Clean again property and instance mappings
		//Print outpus, save mappins. save modules
	
	


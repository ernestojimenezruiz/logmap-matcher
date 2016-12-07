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

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.Set;
import java.util.HashSet;

import uk.ac.ox.krr.logmap2.OntologyLoader;
import uk.ac.ox.krr.logmap2.io.LogOutput;
import uk.ac.ox.krr.logmap2.io.WriteFile;
import uk.ac.ox.krr.logmap2.reasoning.HermiTAccess;
import uk.ac.ox.krr.logmap2.reasoning.ReasonerAccess;
import uk.ac.ox.krr.logmap2.utilities.Utilities;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.profiles.OWLProfileViolation;

/**
 * HermiT Access for web service
 * 
 * @author Ernesto
 *
 */
public class HermiT_WebService {
	
	public HermiT_WebService(HTMLResultsFileManager progess_manager, int version, String output_path, String output_uri) throws IllegalArgumentException, LogMapReasoningException {
		
		OntologyLoader loader;
		HermiTAccess hermit;
		
		
		
		
		try {
			//loader = new OntologyLoader(progess_manager.getIntegratedOntologyModulesIRIStr());
			loader = new OntologyLoader(progess_manager.getIntegratedOntologyIRIStr(), true);
			//We avoid logical axioms only to avoid violation on the DL profile due to annotations
		}
		catch (Exception e) {
			e.printStackTrace();
	    	throw new LogMapReasoningException();
	    }     
		
			
		
		
		try {
			
			
			
			progess_manager.updateProgress("Checking the satisfiability of the integrated ontology (" + loader.getDLNameOntology() + ") with HermiT reasoner...");
			
			
			
			
			
			
			
			//Ontology in OWL 2 DL profile
			//-------------------------------
			
			
			Set<OWLClass> unsatClassSet =  new HashSet<OWLClass>();
			Set<OWLClass> unknownClasses =  new HashSet<OWLClass>();
			
			System.out.println("\nLoading ontology in HermiT");
			hermit = new HermiTAccess(loader.getOWLOntologyManager(), loader.getOWLOntology(), true);//with factory??
			
			
			int resultSatTest;
			boolean timeout=false;	
			int numClasses=0;
			

			
			System.out.println("\nChecking consistency with HermiT");
			if (!hermit.isConsistent()){
				progess_manager.updateProgress("<FONT COLOR=\"red\">The integrated ontology is inconsistent.</FONT> Note that, one of the input ontologies might be the cause of the inconsistency.");
			}
			else {
			
				numClasses = loader.getClassesInSignature().size();
				
				//Classification
				//hermit.classifyOntology_withTimeout_throws_Exception(3600);//1h
				//unsatClassSet = hermit.getUnsatisfiableClasses();
				
				//System.out.println("\nChecking SAT with HermiT  " + loader.getClassesInSignature().size());
				//SAT checking
				for (OWLClass cls : loader.getClassesInSignature()){
					
					if (cls.equals(loader.getDataFactory().getOWLNothing()))
						continue;
					
					resultSatTest = hermit.isSatisfiable_withTimeout(cls, 40); //20 seconds
					
					System.out.println(cls.getIRI().toString() +  " " + resultSatTest);
					
					if (resultSatTest==ReasonerAccess.UNSAT){
						unsatClassSet.add(cls);
					}
					else if (resultSatTest==ReasonerAccess.UNKNOWN){
						unknownClasses.add(cls);
						
						if (unknownClasses.size()>100){ //Too many unknown classes 
							timeout=true;
							break;
						}					
					}
				}
				
				
				int unsat =  unsatClassSet.size();
				int unknown =  unknownClasses.size();
				
				if (unsat>0){
					
					String mesage="";
					
					if (version == Utilities.LOGMAP || version == Utilities.LOGMAPINTERACTIVITY){
						mesage = "Note that LogMap uses incomplete reasoning techniques and thus may fail to detect and repair unsatisfiable classes.";
					}
					else if (version == Utilities.LOGMAPMENDUM){
						mesage = "Note that the selected version of LogMap uses simple diagnosis to detect basic conflicts between mappings.";
					}
					else if (version == Utilities.LOGMAPLITE){
						mesage = "Note that LogMap Lite does not uses any kind of diagnosis of the extracted mappings.";
					}
					else{ //probably from interactivity
						mesage = "Note that LogMap uses incomplete reasoning techniques and thus may fail to detect and repair unsatisfiable classes.";
					}
					
					//Num classes
					progess_manager.updateProgress("The integrated ontology contains <FONT COLOR=\"red\">'" + unsat + "' unsatisfiable classes (out of " + numClasses +")</FONT>. " + mesage);
					
					//Classes file
					//We open/close it each time		
					WriteFile unsatClassesFile = new WriteFile(output_path + "/unsat_classes.txt");				
					for (OWLClass unsatcls : unsatClassSet){
						unsatClassesFile.writeLine(unsatcls.getIRI().toString());
					}
					unsatClassesFile.closeBuffer();
					
					String iri_unsatclasses_txt = output_uri + "/unsat_classes.txt";
					progess_manager.updateProgress("<FONT COLOR=\"red\">Unsatisfiable classes: " +						
							"<a href=\"" + iri_unsatclasses_txt + "\">[TXT format]</a></FONT>");
					//progess_manager.updateProgress("<FONT COLOR=\"red\">Furthermore, the integrated ontology is inconsistent.</FONT>");
					
				}
				
				else{
					progess_manager.updateProgress("The integrated ontology contains '0' unsatisfiable classes</FONT>");
					
				}
				
				
				if (unknown>0){
					
					if (timeout){
						//Num classes
						progess_manager.updateProgress("<FONT COLOR=\"red\">The reasoner fails to check the satisfiability of more than '" + unknown + "' classes (out of " + numClasses + ")</FONT>.");
								//"One of the input ontologies may contain axioms outside <a href=\"http://www.w3.org/TR/owl2-syntax/\" target=\"_blank\">OWL 2 DL</a>");
					}
					else{
						//Num classes
						progess_manager.updateProgress("<FONT COLOR=\"red\">The reasoner fails to check the satisfiability of '" + unknown + "' classes (out of " + numClasses + ")</FONT>.");
								//"One of the input ontologies may contain axioms outside <a href=\"http://www.w3.org/TR/owl2-syntax/\" target=\"_blank\">OWL 2 DL</a>");
					}
					
					//Classes file
					//We open/close it each time		
					WriteFile unknownClassesFile = new WriteFile(output_path + "/unknown_classes.txt");				
					for (OWLClass unknowncls : unknownClasses){
						unknownClassesFile.writeLine(unknowncls.getIRI().toString());
					}
					unknownClassesFile.closeBuffer();
					
					String iri_unknownclasses_txt = output_uri + "/unknown_classes.txt";
					progess_manager.updateProgress("<FONT COLOR=\"red\">Classes with unknown satisfiability: " +						
							"<a href=\"" + iri_unknownclasses_txt + "\">[TXT format]</a></FONT>");
					
					
				}
			}//if consistent
			
			
			//progess_manager.getRelativeOutputPath();
			
			//Clear...structures and ontology
			hermit.clearStructures();
			loader.clearOntology();
			
		}
		/*catch (TimeoutException e) {
			e.printStackTrace();
			loader.clearOntology();
	    	throw new TimeoutException();
		}*/
		/*catch (IllegalArgumentException e){
			e.printStackTrace();
			loader.clearOntology();
	    	throw new IllegalArgumentException();
		}*/
	    catch (Exception e) {
	    	
	    	//Throw exception only if inside the profile!
	    	if (!loader.isOntologyInOWL2DL()){
				
				progess_manager.updateProgress("<FONT COLOR=\"red\">One of the input ontologies is not in the " +
						"<a href=\"http://www.w3.org/TR/owl2-syntax/\" target=\"_blank\">OWL 2 DL</a> profile, " +
						"and hence HermiT reasoner could not cope with the integrated ontology.</FONT>");
				
				
				//write report 
				WriteFile violationsFile = new WriteFile(output_path + "/report_owl2dl_violations.txt");
				
				violationsFile.writeLine("HermiT message: " + e.getMessage() + "\n\n");
				
				//if (loader.getOWL2DLProfileViolation().size()>1)
					//violationsFile.writeLine("There are '" + loader.getOWL2DLProfileViolation().size() + "' OWL 2 DL profile violations:\n");
				violationsFile.writeLine("OWL 2 DL profile violations:\n");
				//else
				//	violationsFile.writeLine("There is one OWL 2 DL profile violation:\n");
				
				for (OWLProfileViolation violation : loader.getOWL2DLProfileViolation()){
					if (!violation.toString().contains("Use of undeclared"))//we avoid noise in report
						violationsFile.writeLine(violation.toString() + "\n");
				}
				violationsFile.closeBuffer();
				
				String iri_violations_txt = output_uri + "/report_owl2dl_violations.txt";
				progess_manager.updateProgress("<FONT COLOR=\"red\">OWL 2 DL violations report: " +
						"<a href=\"" + iri_violations_txt + "\">[TXT format]</a></FONT>");
				
				
				e.printStackTrace();
				loader.clearOntology();
				return;
			}
	    	
	    	
	    	e.printStackTrace();
	    	loader.clearOntology();
	    	throw new LogMapReasoningException();
	    }        
			
		
		
		
	}
	
	

}

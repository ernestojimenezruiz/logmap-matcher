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

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;

import uk.ac.ox.krr.logmap2.OntologyLoader;
import uk.ac.ox.krr.logmap2.io.WriteFile;
import uk.ac.ox.krr.logmap2.reasoning.ELKAccess;
import uk.ac.ox.krr.logmap2.reasoning.ReasonerAccess;
import uk.ac.ox.krr.logmap2.utilities.Utilities;

public class ELK_WebService {

	
	public ELK_WebService(
			HTMLResultsFileManager progess_manager, 
			int version, String output_path, String output_uri) throws IllegalArgumentException, LogMapReasoningException {
		
		OntologyLoader loader;
		ELKAccess elk;
		
		try {
			//loader = new OntologyLoader(progess_manager.getIntegratedOntologyModulesIRIStr());
			loader = new OntologyLoader(progess_manager.getIntegratedOntologyIRIStr());
		}
		catch (Exception e) {
			e.printStackTrace();
	    	throw new LogMapReasoningException();
	    }     
		
			
		
		
		try {
			
			Set<OWLClass> unsatClassSet =  new HashSet<OWLClass>();
			//Set<OWLClass> unknownClasses =  new HashSet<OWLClass>();
			
			progess_manager.updateProgress("Checking the satisfiability of the integrated ontology (" + loader.getDLNameOntology() + ") with ELK reasoner...");
			
			
			System.out.println("\nLoading ontology in ELK");
			elk = new ELKAccess(loader.getOWLOntologyManager(), loader.getOWLOntology(), true);//with factory??
			
			
			
			//int resultSatTest;
			boolean timeout=false;	
			int numClasses=0;
			
			
			
			//Not implemented in ELK v0.2 in the OWL PI version (only for Protege)
			System.out.println("\nChecking consistency with ELK");
			if (!elk.isConsistent()){
				progess_manager.updateProgress("<FONT COLOR=\"red\">The integrated ontology is inconsistent.</FONT> Note that, one of the input ontologies might be the cause of the inconsistency.");
			}
			else {
			
				numClasses = loader.getClassesInSignature().size();
				
				//Classification
				elk.classifyOntology_withTimeout_throws_Exception(3600);//1h
				unsatClassSet = elk.getUnsatisfiableClasses();
				
				//Sat checking: not sure if implemented with ELK
				/*for (OWLClass cls : loader.getClassesInSignature()){
					
					
					if (cls.equals(loader.getDataFactory().getOWLNothing()))
						continue;
					
					
					resultSatTest = elk.isSatisfiable_withTimeout(cls, 20); //5 seconds
					
					System.out.println(cls.getIRI().toString() +  " " + resultSatTest);
					
					if (resultSatTest==ReasonerAccess.UNSAT){
						unsatClassSet.add(cls);
					}
					else if (resultSatTest==ReasonerAccess.UNKNOWN){
						unknownClasses.add(cls);
						
						if (unknownClasses.size()>200){ //Too many unknown classes 
							timeout=true;
							break;
						}					
					}
				}*/
				
				
				int unsat =  unsatClassSet.size();
				//int unknown =  unknownClasses.size();
				
				if (unsat>0){
					
					String mesage="";
					
					if (version == Utilities.LOGMAP || version == Utilities.LOGMAPINTERACTIVITY){
						mesage = "Note that LogMap uses incomplete reasoning techniques and thus may fail to detect and repair unsatisfiable classes.";
					}
					else if (version == Utilities.LOGMAPMENDUM){
						mesage = "Note that the selected version of LogMap uses simple diagnosis to detect basic conflicts between mappings.";
					}
					else{
						mesage = "Note that LogMap Lite does not uses any kind of diagnosis of the extracted mappings.";
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
					progess_manager.updateProgress("The integrated ontology contains '0' unsatisfiable classes.");
							//"Note that ELK reasoner is not complete for ontologies others than " +
							//"<a href=\"http://www.w3.org/TR/owl2-profiles/#OWL_2_EL\" target=\"_blank\">OWL 2 EL</a>, and hence the integrated ontology may still " +
							//"contain unsatisfiable classes.");
					
				}
				
				if (!loader.isOntologyInOWL2EL()){
					progess_manager.updateProgress("<FONT COLOR=\"red\">One of the input ontologes is outside the " +  
							"<a href=\"http://www.w3.org/TR/owl2-profiles/#OWL_2_EL\" target=\"_blank\">OWL 2 EL profile</a></FONT>, " +
							"and hence ELK reasoner may fail to detect some of the unsatisfiable classes in the integrated ontology.");
				}
				
				
				/*if (unknown>0){
					
					if (timeout){
						//Num classes
						progess_manager.updateProgress("<FONT COLOR=\"red\">The reasoner failed to check the satisfiability of more than '" + unknown + "' classes (out of " + numClasses + ")</FONT>. " +
								"One of the input ontologies may contain axioms outside <a href=\"http://www.w3.org/TR/owl2-syntax/\" target=\"_blank\">OWL 2 DL</a>");
					}
					else{
						//Num classes
						progess_manager.updateProgress("<FONT COLOR=\"red\">The reasoner failed to check the satisfiability of '" + unknown + "' classes (out of " + numClasses + ")</FONT>. " +
								"One of the input ontologies may contain axioms outside <a href=\"http://www.w3.org/TR/owl2-syntax/\" target=\"_blank\">OWL 2 DL</a>");
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
					
					
				}//if unknown
				*/
				
			}//if consistent
			
			
			//progess_manager.getRelativeOutputPath();
			
			//Clear...structures and ontology
			elk.clearStructures();
			loader.clearOntology();
			
		}
		/*catch (TimeoutException e) {
			e.printStackTrace();
			loader.clearOntology();
	    	throw new TimeoutException();
		}*/
		catch (IllegalArgumentException e){
			e.printStackTrace();
			loader.clearOntology();
	    	throw new IllegalArgumentException();
		}
	    catch (Exception e) {
	    	e.printStackTrace();
	    	loader.clearOntology();
	    	throw new LogMapReasoningException();
	    }        
			
		
		
		
	}
	
	
	
}

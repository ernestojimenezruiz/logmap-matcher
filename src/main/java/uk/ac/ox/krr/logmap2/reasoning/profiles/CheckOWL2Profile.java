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
package uk.ac.ox.krr.logmap2.reasoning.profiles;

import org.semanticweb.owlapi.profiles.*;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import uk.ac.ox.krr.logmap2.OntologyLoader;

/**
 * 
 * Checks the profile of the integrated ontology
 * 
 * @author Ernesto
 *
 */
public class CheckOWL2Profile {
	
	OWLOntology onto;
	
	OWL2DLProfile owl2dlchecker = new OWL2DLProfile();
	
	OWL2ELProfile owl2elchecker = new OWL2ELProfile();
	
	OWLProfileReport report;
	
	
	public CheckOWL2Profile(OWLOntology onto){
		
		this.onto=onto;
		
	}
	
		
	public CheckOWL2Profile(){
		
	}
	
	public boolean isInOWL2ELProfile(OWLOntology onto){
		
		try {
			report = owl2elchecker.checkOntology(onto);
								
			for (OWLProfileViolation violation : report.getViolations()) {
				
				//Ignore dummy violations
				if (violation.toString().startsWith("Use of undeclared class") ||
					violation.toString().startsWith("Use of undeclared object property") ||
					violation.toString().startsWith("Use of undeclared data property") ||
					violation.toString().startsWith("Use of data range not in profile: xsd:") ||
					//violation.toString().startsWith("Cannot pun between properties") ||
					violation.toString().startsWith("Not enough operands; at least two needed:") ||
					violation.toString().startsWith("Use of reserved vocabulary for class IRI: rdf:List"))
				
					continue;
				
				//System.out.println("Violations: " + violation.toString());
				return false;
				
				
			}
			
			return true;
		}
		catch (Exception e) {
			//Detected error in some cases: No current axiom; is the walker being used outside of an ontology visit?
			return false;
		}
		
	}
		
	
	
	
	
	public OWLProfileReport getReport4OWL2DL(){
		
		return owl2dlchecker.checkOntology(onto);
		
		//for (OWLProfileViolation violation : report.getViolations()){
		//	System.out.println(violation);// +  "   "  + violation.getAxiom());
		//}
		
	}
	
	public OWLProfileReport getReport4OWL2EL(){
		
		return owl2elchecker.checkOntology(onto);
			
	}
	
	
	
	public static void main(String[] args) {
	
		try {
			//OntologyLoader loader = new OntologyLoader("http://dvcs.w3.org/hg/prov/raw-file/57833db7d5a4/ontology/ProvenanceOntology.owl");
		
			OntologyLoader loader = new OntologyLoader("http://www.co-ode.org/ontologies/pizza/pizza.owl");
			
			
			CheckOWL2Profile checker = new CheckOWL2Profile(loader.getOWLOntology());
			
			System.out.println("OWL 2 DL:\n" + checker.getReport4OWL2DL().isInProfile());
			System.out.println("");
			System.out.println("OWL 2 EL:\n" + checker.getReport4OWL2EL().isInProfile());
			
			
			
		}
		catch (Exception e){
			e.printStackTrace();
		}
		
		
	}

}

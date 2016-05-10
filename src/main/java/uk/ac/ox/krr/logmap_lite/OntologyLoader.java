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
package uk.ac.ox.krr.logmap_lite;

import org.semanticweb.owlapi.apibinding.OWLManager;

import org.semanticweb.owlapi.io.RDFXMLOntologyFormat;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import uk.ac.ox.krr.logmap2.io.LogOutput;
import uk.ac.ox.krr.logmap2.owlapi.SynchronizedOWLManager;
import uk.ac.ox.krr.logmap2.utilities.Utilities;

import java.util.List;
import java.util.HashSet;
import java.util.Set;



/**
 * This class will manage the loaded ontology
 * 
 *
 * @author Ernesto Jimenez-Ruiz
 * Created: Sep 6, 2011
 *
 */
public class OntologyLoader {
	
	protected OWLDataFactory dataFactory;
	protected OWLOntologyManager managerOnto;
	protected OWLOntology onto;
	
	protected String iri_onto_str;
	
	protected int size_signature;
	protected int size_classes;
	
	protected Set<OWLAxiom> axiomSet = new HashSet<OWLAxiom>();
	
	
	
	public OntologyLoader(String phy_iri_onto) throws OWLOntologyCreationException{
		//managerOnto = OWLManager.createOWLOntologyManager();
		managerOnto = SynchronizedOWLManager.createOWLOntologyManager();
		dataFactory=managerOnto.getOWLDataFactory();
		
		loadOWLOntology(phy_iri_onto);
			
	}
	
	
	private String getURIFromClasses(){
		for (OWLClass cls : onto.getClassesInSignature()){
			return Utilities.getNameSpaceFromURI(cls.getIRI().toString());
		}
		
		//Just in case we return default IRI
		return "http://logmap.cs.ox.ac.uk/ontology.owl";
		
	}
	
	
	public void loadOWLOntology(String phy_iri_onto) throws OWLOntologyCreationException  {		

		try {
			//If import cannot be loaded
			//TODO: deprecated??
			managerOnto.setSilentMissingImportsHandling(true);
			
			onto = managerOnto.loadOntology(IRI.create(phy_iri_onto));
			
			//The preclassification with condor has no ontology id
			if (onto.getOntologyID().getOntologyIRI()!=null){
				iri_onto_str=onto.getOntologyID().getOntologyIRI().toString(); //Give this iri to module
			}
			else {
				iri_onto_str=getURIFromClasses();
			}
			
			LogOutput.print("IRI: " + iri_onto_str);
			
			size_signature = onto.getSignature(true).size();
			size_classes = onto.getClassesInSignature(true).size();
			
			//LogOutput.print(iri_onto);
			
			
		}
		catch(Exception e){
			System.err.println("Error loading OWL ontology: " + e.getMessage());
			e.printStackTrace();
			throw new OWLOntologyCreationException(); //any error
		}
	}
	
	public void createAxiomSet(){
		axiomSet.addAll(onto.getAxioms());
	}
	
	public void clearAxiomSet(){
		axiomSet.clear();
	}
	
	public Set<OWLAxiom> getAxiomSet(){
		return axiomSet;
	}
	
	
	public void clearOntology(){
		managerOnto.removeOntology(onto);
		onto=null;
		managerOnto=null;
	}
	
	public OWLOntology getOWLOntology(){
		return onto;
	}
	
	public String getOntologyIRIStr(){
		return iri_onto_str;
	}
	
	public IRI getOntologyIRI(){
		return IRI.create(iri_onto_str);
	}
	
	
	public Set<OWLClass> getClassesInSignature(){
		return onto.getClassesInSignature();
	}
	
	public int getSignatureSize(){
		return size_signature;
	}
	
	public int getClassesInSignatureSize(){
		return size_classes;
	}
	
	public void applyChanges(List<OWLOntologyChange> listchanges){
		managerOnto.applyChanges(listchanges);
	}
	
	public OWLDataFactory getDataFactory(){
		return dataFactory;
	}
	
	
	public void saveOntology(String phy_iri_onto) throws Exception{
		
		managerOnto.saveOntology(onto, new RDFXMLOntologyFormat(), IRI.create(phy_iri_onto));
		
	}
	
	
	
}

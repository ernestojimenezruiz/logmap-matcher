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
package uk.ac.ox.krr.logmap2.owlapi;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.RDFXMLDocumentFormat;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;

/**
 * Performs synchronised calls to OWLManager from OWLAPI.
 * (We have detected some concurrency issue, that might be caused by the 
 * thread-unsafety of OWLManager)
 * @author Ernesto
 *
 */
public class SynchronizedOWLManager {
		
	public static synchronized OWLDataFactory createOWLDataFactory(){
		return new OWLDataFactoryImpl();
	}
	
	
	/**
	 * Concurrent safe method added in OWL APi 4
	 * @return
	 */
	public static synchronized OWLOntologyManager createConcurrentOWLOntologyManager(){
		return OWLManager.createConcurrentOWLOntologyManager();
	}
	
	
	/**
	 * Fix implemented in LogMap to allow the concurrent access to the OWLManager 
	 * @return
	 */
	public static synchronized OWLOntologyManager createOWLOntologyManager(){
		return OWLManager.createOWLOntologyManager();
	}
	
	
	public static synchronized void saveOntology(OWLOntology ontology, String IRIstr) throws Exception{
		
		OWLOntologyManager moduleManager = SynchronizedOWLManager.createOWLOntologyManager();
		
		//moduleManager.saveOntology(
		//		ontology, new RDFXMLOntologyFormat(), IRI.create(IRIstr));
		
		saveOntology(moduleManager, ontology, IRIstr);
		
	}
	
	public static synchronized void saveOntology(OWLOntologyManager moduleManager, OWLOntology ontology, String IRIstr) throws Exception{
		
		moduleManager.saveOntology(
				ontology, new RDFXMLDocumentFormat(), IRI.create(IRIstr));
	}
	
	

}

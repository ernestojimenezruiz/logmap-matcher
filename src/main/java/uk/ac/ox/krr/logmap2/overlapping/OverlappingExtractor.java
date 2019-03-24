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
package uk.ac.ox.krr.logmap2.overlapping;

import java.util.Set;


import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.parameters.Imports;
import org.semanticweb.owlapi.model.OWLAxiom;


/**
 * Abstracts class for overlapping. Given two ontologies will return 2 modules
 * representing the overlapping between them
 *  
 * @author ernesto
 *
 */
public abstract class OverlappingExtractor {
	
	
	protected OWLOntology module1;
	protected OWLOntology module2;
	
	protected Set<OWLAxiom> module1_tbox_axioms;
	protected Set<OWLAxiom> module2_tbox_axioms;
	
	

	/**
	 * Receives two IRIs as input
	 * @param iri_str1
	 * @param iri_str2
	 */
	public abstract void createOverlapping(
			String iri_str1, 
			String iri_str2) throws Exception;
	
	/**
	 * Receives two OWL ontologies as input
	 * @param onto1
	 * @param onto2
	 */
	public abstract void createOverlapping(
			OWLOntology onto1, 
			OWLOntology onto2) throws Exception;
	
	
	
	public OWLOntology getOverlappingOnto1(){
		return module1;
	}
	
	public OWLOntology getOverlappingOnto2(){
		return module2;
	}
	
	public Set<OWLAxiom> getTBOXOverlappingOnto1(){
		return module1_tbox_axioms;
	}
	
	public Set<OWLAxiom> getTBOXOverlappingOnto2(){
		return module2_tbox_axioms;
	}
	
	
	public void keepOnlyTBOXOverlapping(){
		keepOnlyTBOXOverlapping(true);
	}
	
	public void keepOnlyTBOXOverlapping(boolean removeModules){
		module1_tbox_axioms = module1.getTBoxAxioms(Imports.INCLUDED); //importsclosure
		module2_tbox_axioms = module2.getTBoxAxioms(Imports.INCLUDED); //importsclosure
		
		if (removeModules){
			module1=null;
			module2=null;
		}
		
	}
	
	
	

	public void clearModulesOverlapping(){
		
		module1=null;
		module2=null;
	
	}
	
	
	public abstract Set<OWLEntity> getBaseOverlappedEntities1();
	public abstract Set<OWLEntity> getBaseOverlappedEntities2();
	
	

}

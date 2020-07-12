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
package uk.ac.ox.krr.logmap2.reasoning;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import uk.ac.ox.krr.logmap2.owlapi.SynchronizedOWLManager;


public class ReasonerManager {
	
	public static int NONE = -1;
	public static int HERMIT = 0;
	public static int PELLET = 1;
	//Note that elk does not implement the justification extraction facility
	public static int ELK = 2;
	public static int FACTpp = 3;
	public static int TrOWL = 4;
	public static int MORe = 5;
	public static int STRUCTURAL = 6;
	
	
	//public enum reasoners { HERMIT, PELLET }
	
	

	
	public static ReasonerAccess getReasoner(
			int reasoner_id,
			OWLOntologyManager ontoManager, 
			OWLOntology onto, 
			boolean useFactory) throws Exception{
		
		if (reasoner_id == HERMIT){
			return new HermiTAccess(ontoManager, onto, useFactory);
		}
		//else if (reasoner_id == PELLET){
		//	return new PelletAccess(ontoManager, onto, useFactory);
		//}
		else if (reasoner_id == ELK){
			return new ELKAccess(ontoManager, onto, useFactory);
		}
		//else if (reasoner_id == FACTpp){
		//	return new FaCTppAccess(ontoManager, onto, useFactory);
		//}
		//else if (reasoner_id == TrOWL){
		//	return new TrOWLAccess(ontoManager, onto, useFactory);
		//}
		//else if (reasoner_id == MORe){
		//	return new MOReAccess(ontoManager, onto, useFactory);
		//}
		else {
			return new HermiTAccess(ontoManager, onto, useFactory);
		}
	}
	
	
	
	public static ReasonerAccess getMergedOntologyReasoner(
			int reasoner_id,			 
			OWLOntology onto1,
			OWLOntology onto2,
			OWLOntology m,
			boolean useFactory) throws Exception{
		
		return getMergedOntologyReasoner(reasoner_id, onto1.getAxioms(), onto2.getAxioms(), m.getAxioms(), useFactory);
				
		
	}
	
	
	public static ReasonerAccess getMergedOntologyReasoner(
			int reasoner_id,			 
			Set<OWLAxiom> onto1_ax,
			Set<OWLAxiom> onto2_ax,
			Set<OWLAxiom> m_ax,
			boolean useFactory) throws Exception{
		
				
		Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
		axioms.addAll(onto1_ax);
		axioms.addAll(onto2_ax);
		axioms.addAll(m_ax);
				
		//OWLOntologyManager managerMerged = OWLManager.createOWLOntologyManager();
		OWLOntologyManager managerMerged = SynchronizedOWLManager.createOWLOntologyManager();
		OWLOntology mergedOntology = managerMerged.createOntology(axioms, IRI.create("http://krr.ox.cs.ac.uk/logmap2/integration.owl"));
		
		
		if (reasoner_id == HERMIT){
			return new HermiTAccess(managerMerged, mergedOntology, useFactory);
		}
		//else if (reasoner_id == PELLET){
		//	return new PelletAccess(managerMerged, mergedOntology, useFactory);
		//}
		else if (reasoner_id == ELK){
			return new ELKAccess(managerMerged, mergedOntology, useFactory);
		}
		//else if (reasoner_id == FACTpp){
		//	return new FaCTppAccess(managerMerged, mergedOntology, useFactory);
		//}
		//else if (reasoner_id == TrOWL){
		//	return new TrOWLAccess(managerMerged, mergedOntology, useFactory);
		//}
		else {
			return new HermiTAccess(managerMerged, mergedOntology, useFactory);
		}
	}
	
	

}

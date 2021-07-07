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

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLDataFactory;

import uk.ac.manchester.syntactic_locality.ModuleExtractor;
import uk.ac.ox.krr.logmap2.Parameters;
import uk.ac.ox.krr.logmap2.io.LogOutput;
import uk.ac.ox.krr.logmap2.mappings.objects.MappingObjectStr;
import uk.ac.ox.krr.logmap2.owlapi.SynchronizedOWLManager;
import uk.ac.ox.krr.logmap2.utilities.Utilities;

public class OverlappingExtractor4Mappings {
	
	protected OWLOntology module1;
	protected OWLOntology module2;
	

	boolean is_overlapping_onto1=true;
	boolean is_overlapping_onto2=true;
	
	Set<OWLEntity> entities1 = new HashSet<OWLEntity>();
	Set<OWLEntity> entities2 = new HashSet<OWLEntity>();
	
	OWLDataFactory dataFactory;
	
	
	/**
	 * Receives two OWL ontologies as input and the set of input mappings to extract the overlapping
	 * @param onto1
	 * @param onto2
	 */
	public void createOverlapping(
			OWLOntology onto1, 
			OWLOntology onto2,
			Set<MappingObjectStr> mappings) throws Exception {
		
		long init, fin;
		
		init = Calendar.getInstance().getTimeInMillis();
		
		entities1.clear();
		entities2.clear();
		
		dataFactory = SynchronizedOWLManager.createOWLDataFactory();
		
		if (onto1.getSignature().size()<Parameters.min_size_overlapping)
			is_overlapping_onto1=false;
		
		if (onto2.getSignature().size()<Parameters.min_size_overlapping)
			is_overlapping_onto2=false;
		
		for (MappingObjectStr map : mappings){
			
			if (map.getTypeOfMapping()==Utilities.CLASSES){
				if (is_overlapping_onto1){
					entities1.add(dataFactory.getOWLClass(IRI.create(map.getIRIStrEnt1())));
				}
				if (is_overlapping_onto2){
					entities2.add(dataFactory.getOWLClass(IRI.create(map.getIRIStrEnt2())));
				}
			}
			else if (map.getTypeOfMapping()==Utilities.OBJECTPROPERTIES){
				if (is_overlapping_onto1){
					entities1.add(dataFactory.getOWLObjectProperty(IRI.create(map.getIRIStrEnt1())));
				}
				if (is_overlapping_onto2){
					entities2.add(dataFactory.getOWLObjectProperty(IRI.create(map.getIRIStrEnt2())));
				}
			}
			else if (map.getTypeOfMapping()==Utilities.DATAPROPERTIES){
				if (is_overlapping_onto1){
					entities1.add(dataFactory.getOWLDataProperty(IRI.create(map.getIRIStrEnt1())));
				}
				if (is_overlapping_onto2){
					entities2.add(dataFactory.getOWLDataProperty(IRI.create(map.getIRIStrEnt2())));
				}
			}
			else if (map.getTypeOfMapping()==Utilities.INSTANCES){
				if (is_overlapping_onto1){
					entities1.add(dataFactory.getOWLNamedIndividual(IRI.create(map.getIRIStrEnt1())));
				}
				if (is_overlapping_onto2){
					entities2.add(dataFactory.getOWLNamedIndividual(IRI.create(map.getIRIStrEnt2())));
				}
			}
		}
		
		

		if (is_overlapping_onto1){
			
			String uri_onto1 = "http://module/ontology1";
			if (onto1.getOntologyID().getOntologyIRI().isPresent())
				uri_onto1 = onto1.getOntologyID().getOntologyIRI().get().toString(); 
			
			ModuleExtractor module_extractor1 = new ModuleExtractor(
					onto1.getAxioms(), SynchronizedOWLManager.createOWLOntologyManager(), false, false, true, true, false);
			module1 = module_extractor1.getLocalityModuleForSignatureGroup(
					entities1, uri_onto1, false);		
			
			module_extractor1.clearStrutures();
			
			entities1.clear();
			
		}
		else {
			module1 = onto1;
		}
		
		
		if (is_overlapping_onto2){
			
			String uri_onto2 = "http://module/ontology2";
			if (onto2.getOntologyID().getOntologyIRI().isPresent())
				uri_onto2 = onto2.getOntologyID().getOntologyIRI().get().toString();
		
			ModuleExtractor module_extractor2 = new ModuleExtractor(
					onto2.getAxioms(), SynchronizedOWLManager.createOWLOntologyManager(), false, false, true, true, false);
			module2 = module_extractor2.getLocalityModuleForSignatureGroup(
					entities2, uri_onto2, false);
			
			module_extractor2.clearStrutures();
			
			entities2.clear(); 			
		}
		
		else {
			module2 = onto2;
		}
		
		
		
		fin = Calendar.getInstance().getTimeInMillis();
		LogOutput.print("\tSize module 1 (overlapping): " + module1.getSignature().size() + "  " + onto1.getSignature().size());
		LogOutput.print("\tSize module 2 (overlapping): " + module2.getSignature().size() + "  " + onto2.getSignature().size());
		LogOutput.print("\tTime extracting modules (s): " + (float)((double)fin-(double)init)/1000.0);
		
		
		
		
		
	}
	
	
	
	public OWLOntology getOverlappingOnto1(){
		return module1;
	}
	
	public OWLOntology getOverlappingOnto2(){
		return module2;
	}
	
	
	

}

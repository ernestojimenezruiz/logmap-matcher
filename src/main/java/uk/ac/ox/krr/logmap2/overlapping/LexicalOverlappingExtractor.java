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

import org.semanticweb.owlapi.apibinding.OWLManager;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.apibinding.OWLManager;

import uk.ac.ox.krr.logmap2.OntologyLoader;
import uk.ac.ox.krr.logmap2.Parameters;
import uk.ac.ox.krr.logmap2.io.ReadFile;
import uk.ac.ox.krr.logmap2.io.LogOutput;
import uk.ac.ox.krr.logmap2.lexicon.LexicalUtilities;
import uk.ac.ox.krr.logmap2.owlapi.SynchronizedOWLManager;
import uk.ac.ox.krr.logmap2.statistics.StatisticsTimeMappings;
import uk.ac.ox.krr.logmap2.utilities.Utilities;

//import uk.ac.manchester.syntactic_locality.*;
import uk.ac.manchester.syntactic_locality.OntologyModuleExtractor;

/**
 * Extracts the overlapping given two ontologies. The output will two ontology modules representing an overstimation of the overlapping
 * 
 *
 * @author Ernesto Jimenez-Ruiz
 * Created: Sep 6, 2011
 *
 */
public class LexicalOverlappingExtractor extends OverlappingExtractor{
	
	
	long init, fin;
	
	private String iri_str1;
	private String iri_str2;	
	private OWLOntology onto1;
	private OWLOntology onto2;
	
	Set<OWLEntity> entities1 = new HashSet<OWLEntity>();
	Set<OWLEntity> entities2 = new HashSet<OWLEntity>();
	
	
	private long size_onto1;
	private long size_onto2;
	
	boolean full_overlapping;
	
	
	
	//I_Sub isub = new I_Sub();
	
	private LexicalUtilities lexicalUtilities;

	
	/**
	 * Default constructor
	 */
	public LexicalOverlappingExtractor(LexicalUtilities lexicalUtilities, boolean full_overlapping){
		
		this.lexicalUtilities=lexicalUtilities;
		this.full_overlapping = full_overlapping;
		
	}
	
	/**
	 * Default constructor
	 */
	public LexicalOverlappingExtractor(LexicalUtilities lexicalUtilities){
		
		this(lexicalUtilities, false);
		
		
	}
			
	
	public long getSizeClassesOnto1(){
		return size_onto1;
	}
	
	
	public long getSizeClassesOnto2(){
		return size_onto2;
	}
	
	
	
	
	
	
	
	public void createOverlapping(String iri_str1, String iri_str2) throws OWLOntologyCreationException, Exception{
		this.iri_str1 = iri_str1;
		this.iri_str2 = iri_str2;
		
		createOverlapping(true);
		
	}
	
	public void createOverlapping(OWLOntology onto1, OWLOntology onto2) throws OWLOntologyCreationException, Exception{
		this.onto1 = onto1;
		this.onto2 = onto2;
		
		createOverlapping(false);
		
	}
	
	
	private void createOverlapping(boolean fromIRIs) throws OWLOntologyCreationException, Exception{
	
		OntologyLoader onto_loader1;
		OntologyLoader onto_loader2;
		
		boolean is_overlapping_onto1=true;
		boolean is_overlapping_onto2=true;
		
		double loading_time = 0.0;
		double overlapping_time = 0.0;
		
		
		
		//try{
		
		//Loading Ontology 1 and Light processing
		init = Calendar.getInstance().getTimeInMillis();
		if (fromIRIs)
			onto_loader1 = new OntologyLoader(iri_str1);
		else
			onto_loader1 = new OntologyLoader(onto1);
		
		
		size_onto1 = onto_loader1.getClassesInSignatureSize();
		
		if (onto_loader1.getSignatureSize()<Parameters.min_size_overlapping)
			is_overlapping_onto1=false;
		
		///fin = Calendar.getInstance().getTimeInMillis();
		//loading_time = (float)((double)fin-(double)init)/1000.0;
		loading_time = StatisticsTimeMappings.getRunningTime(init);
		StatisticsTimeMappings.addParsing_time(loading_time);				
		LogOutput.print("\tTime loading ontology 1 (s): " + loading_time);
		LogOutput.print("\tOntology 1 Axioms (s): " + onto_loader1.getOWLOntology().getAxiomCount());
		
		
		init = Calendar.getInstance().getTimeInMillis();
		OntologyProcessing4Overlapping overlapping1 = new OntologyProcessing4Overlapping(onto_loader1.getOWLOntology(), lexicalUtilities, full_overlapping, false);
		overlapping1.processOntologyClassLabels();
		overlapping1.setInvertedFile4Overlapping();
		
		if (is_overlapping_onto1){
			onto_loader1.createAxiomSet(); //we only keep set of axioms
			onto_loader1.clearOntology();
			overlapping1.clearOntoloy();
		}
		
		overlapping_time = StatisticsTimeMappings.getRunningTime(init);
		StatisticsTimeMappings.addOverlapping_time(overlapping_time);
		LogOutput.print("\tTime processing ontology 1 (s): " + overlapping_time);
		
		
		
		//Loading Ontology 2 and Light processing
		init = Calendar.getInstance().getTimeInMillis();
		if (fromIRIs)
			onto_loader2 = new OntologyLoader(iri_str2);
		else
			onto_loader2 = new OntologyLoader(onto2);
		
		size_onto2 = onto_loader2.getClassesInSignatureSize();
		
		if (onto_loader2.getSignatureSize()<Parameters.min_size_overlapping)
			is_overlapping_onto2=false;
		
		//fin = Calendar.getInstance().getTimeInMillis();
		loading_time = StatisticsTimeMappings.getRunningTime(init);
		StatisticsTimeMappings.addParsing_time(loading_time);
		LogOutput.print("\tTime loading ontology 2 (s): " + loading_time);
		LogOutput.print("\tOntology 2 Axioms (s): " + onto_loader2.getOWLOntology().getAxiomCount());
		
		
		
		
		init = Calendar.getInstance().getTimeInMillis();
		OntologyProcessing4Overlapping overlapping2 = new OntologyProcessing4Overlapping(onto_loader2.getOWLOntology(), lexicalUtilities, full_overlapping, false);
		overlapping2.processOntologyClassLabels();
		overlapping2.setInvertedFile4Overlapping();
		
		if (is_overlapping_onto2){
			onto_loader2.createAxiomSet();
			onto_loader2.clearOntology();
			overlapping2.clearOntoloy();
		}
		
		overlapping_time = StatisticsTimeMappings.getRunningTime(init);
		StatisticsTimeMappings.addOverlapping_time(overlapping_time);
		LogOutput.print("\tTime processing ontology 2 (s): " + overlapping_time);
		
		
		//Total loading time
		LogOutput.printAlways("LogMap 2 Total Loading Time (s): " + StatisticsTimeMappings.getParsing_time());
		
					
		//Intersects IF
		//Intersect and then create new ones... (review method intersection)
	
		init = Calendar.getInstance().getTimeInMillis();
		
		Set<Set<String>> if_weak_intersect;
		
		//We perform intersection an we only keep in inverted file the intersected elements
		if_weak_intersect = overlapping1.getWeakInvertedFile().keySet();
		if_weak_intersect.retainAll(overlapping2.getWeakInvertedFile().keySet());
		overlapping2.getWeakInvertedFile().keySet().retainAll(if_weak_intersect);

		LogOutput.print("\tSize IF intersected: " + if_weak_intersect.size());
		
		overlapping_time = StatisticsTimeMappings.getRunningTime(init);
		StatisticsTimeMappings.addOverlapping_time(overlapping_time);
		LogOutput.print("\tTime intersecting IF weak (s): " + overlapping_time);
		
		
		
		//Get entities 4 modules-overlapping		
		init = Calendar.getInstance().getTimeInMillis();
		
		
		
		/*double ambiguity1=0.0;
		double ambiguity2=0.0;
		
		for (Set<String> str_set: if_weak_intersect){
			
			ambiguity1 += overlapping1.getWeakInvertedFile().get(str_set).size();
			ambiguity2 += overlapping2.getWeakInvertedFile().get(str_set).size();
			
		}
		
		ambiguity1 = ambiguity1 / (double)if_weak_intersect.size();
		ambiguity2 = ambiguity2 / (double)if_weak_intersect.size();
		
		LogOutput.print("Ambiguity average 1: " + ambiguity1);
		LogOutput.print("Ambiguity average 2: " + ambiguity2);*/
		
		//double minscore;
		
		for (Set<String> str_set: if_weak_intersect){
			
			//minscore = 0.70;
			
			//Ambiguity
			//if (overlapping1.getWeakInvertedFile().get(str_set).size()>ambiguity1 && 
			//	overlapping2.getWeakInvertedFile().get(str_set).size()>ambiguity2){
			//	minscore = 0;
			//}
			
			for (int ide1 : overlapping1.getWeakInvertedFile().get(str_set)){
				entities1.add(overlapping1.getClass4identifier(ide1));
			}
			for (int ide2 : overlapping2.getWeakInvertedFile().get(str_set)){
				entities2.add(overlapping2.getClass4identifier(ide2));
			}
			
			/*for (int ide1 : overlapping1.getWeakInvertedFile().get(str_set)){
				
				for (int ide2 : overlapping2.getWeakInvertedFile().get(str_set)){
					
					if (isub.score(overlapping1.getLabel4identifier(ide1), overlapping2.getLabel4identifier(ide2)) > minscore){					
						entities1.add(overlapping1.getClass4identifier(ide1));
						entities2.add(overlapping2.getClass4identifier(ide2));
						continue;
					}
				}
			}
			
			for (int ide2 : overlapping2.getWeakInvertedFile().get(str_set)){	
				
				if (entities2.contains(overlapping2.getClass4identifier(ide2)))//Already in
					continue;
				
				for (int ide1 : overlapping1.getWeakInvertedFile().get(str_set)){
					
					if (isub.score(overlapping1.getLabel4identifier(ide1), overlapping2.getLabel4identifier(ide2)) > minscore){					
						entities1.add(overlapping1.getClass4identifier(ide1));
						entities2.add(overlapping2.getClass4identifier(ide2));
						continue;
					}
				}
			}*/
			
		}
		
		LogOutput.print("\tSize entities 1: " + entities1.size());
		LogOutput.print("\tSize entities 2: " + entities2.size());
		
		
		//We empty overlapping structures
		overlapping1.clearStructures();
		overlapping2.clearStructures();
		
		overlapping_time = StatisticsTimeMappings.getRunningTime(init);
		StatisticsTimeMappings.addOverlapping_time(overlapping_time);
		LogOutput.print("\tTime extracting entities4modules (s): " + overlapping_time);
		
		
		
		//Extract module (create new constructor)
		//Give onto IRI to module
		init = Calendar.getInstance().getTimeInMillis();
		
		if (is_overlapping_onto1){
			
			//Old extractor
			//ModuleExtractor module_extractor1 = new ModuleExtractor(
			//		onto_loader1.getAxiomSet(), SynchronizedOWLManager.createOWLOntologyManager(), false, false, true, true, false);
			//module1 = module_extractor1.getLocalityModuleForSignatureGroup(entities1, onto_loader1.getOntologyIRIStr(), false);		
			
			OntologyModuleExtractor module_extractor1 =
					new OntologyModuleExtractor(
							SynchronizedOWLManager.createOWLOntologyManager(),
							onto_loader1.getAxiomSet(),
							true,
							false,
							true);
			module1 = module_extractor1.extractAsOntology(
					entities1, 
					IRI.create(onto_loader1.getOntologyIRIStr()));
			
			
			//if (store){
				//module_extractor1.saveExtractedModule(iri_str1_out);
				//module_extractor1.saveExtractedModule(
				//		SynchronizedOWLManager.createOWLOntologyManager(),
				//		module1,
				//		iri_str1_out);
			//}
			
			module_extractor1.clearStrutures();
			
			onto_loader1.clearAxiomSet();
		}
		else {
			module1 = onto_loader1.getOWLOntology();
		}
		
		
		entities1.clear(); //TODO Are we returning which entities have a weak anchor??
		
		
		if (is_overlapping_onto2){
		
			//ModuleExtractor module_extractor2 = new ModuleExtractor(
			//		onto_loader2.getAxiomSet(), SynchronizedOWLManager.createOWLOntologyManager(), false, false, true, true, false);
			//module2 = module_extractor2.getLocalityModuleForSignatureGroup(entities2, onto_loader2.getOntologyIRIStr(), false);
			
			OntologyModuleExtractor module_extractor2 =
					new OntologyModuleExtractor(
							SynchronizedOWLManager.createOWLOntologyManager(),
							onto_loader2.getAxiomSet(),
							true,
							false,
							true);
			module2 = module_extractor2.extractAsOntology(
					entities2, 
					IRI.create(onto_loader2.getOntologyIRIStr()));
			
			
			//if (store){
			//	//module_extractor2.saveExtractedModule(iri_str2_out);
			//	module_extractor2.saveExtractedModule(
			//			SynchronizedOWLManager.createOWLOntologyManager(),
			//			module2,
			//			iri_str2_out);
			//}
			
			module_extractor2.clearStrutures();
			
			onto_loader2.clearAxiomSet();			
		}
		
		else {
			module2 = onto_loader2.getOWLOntology();
		}
		
		entities2.clear(); //TODO Are we returning which entities have a weak anchor??
		
		overlapping_time = StatisticsTimeMappings.getRunningTime(init);
		StatisticsTimeMappings.addOverlapping_time(overlapping_time);
		
		LogOutput.print("\tSize module 1: " + module1.getAxiomCount());
		LogOutput.print("\tSize module 2: " + module2.getAxiomCount());
		LogOutput.print("\tSize classes module 1: " + module1.getClassesInSignature().size() + "  " + onto_loader1.getClassesInSignatureSize());
		LogOutput.print("\tSize classes module 2: " + module2.getClassesInSignature().size() + "  " + onto_loader2.getClassesInSignatureSize());
		LogOutput.print("\tTime extracting modules (s): " + overlapping_time);
		
		

		
		
		
	}
	
	




	@Override
	public Set<OWLEntity> getBaseOverlappedEntities1() {
		return entities1;
	}




	@Override
	public Set<OWLEntity> getBaseOverlappedEntities2() {
		return entities2;
	}


	
	
	


}

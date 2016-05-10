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
	
	String file_gs_mappings;
	String file_logmap_mappings;
	
	Set<String> ent1_mappings_gs;
	Set<String> ent2_mappings_gs;
	
	Set<String> ent1_mappings_logmap;
	Set<String> ent2_mappings_logmap;
	
	Set<String> ent1_mappings_logmap_ok;
	Set<String> ent2_mappings_logmap_ok;
	
	String iri_str1_out;
	String iri_str2_out;
	
	
	boolean test=false;
	boolean store=false;
	
	
	private String iri_str1;
	private String iri_str2;	
	private OWLOntology onto1;
	private OWLOntology onto2;
	
	Set<OWLEntity> entities1 = new HashSet<OWLEntity>();
	Set<OWLEntity> entities2 = new HashSet<OWLEntity>();
	
	
	//I_Sub isub = new I_Sub();
	
	private LexicalUtilities lexicalUtilities;

	
	/**
	 * Default constructor
	 */
	public LexicalOverlappingExtractor(LexicalUtilities lexicalUtilities){
		
		this.lexicalUtilities=lexicalUtilities;
		
		
	}
			
	
	
	
	/**
	 * Compare with GS and LogMap 0.9	 
	 */
	public LexicalOverlappingExtractor(
			String file_mappings, 
			String logmap_mappings,
			String iri_str1_out, 
			String iri_str2_out,
			LexicalUtilities lexicalUtilities) throws Exception{
		
		this.lexicalUtilities=lexicalUtilities;
		
		
		this.file_gs_mappings=file_mappings;
		this.file_logmap_mappings=logmap_mappings;

		ent1_mappings_gs = new HashSet<String>();
		ent2_mappings_gs = new HashSet<String>();
		
		ent1_mappings_logmap = new HashSet<String>();
		ent2_mappings_logmap = new HashSet<String>();
		
		ent1_mappings_logmap_ok = new HashSet<String>();
		ent2_mappings_logmap_ok = new HashSet<String>();
		
		this.iri_str1_out=iri_str1_out;
		this.iri_str2_out=iri_str2_out;
		
		test=true;
		store=true;
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
		
		//try{
		
		//Loading Ontology 1 and Light processing
		init = Calendar.getInstance().getTimeInMillis();
		if (fromIRIs)
			onto_loader1 = new OntologyLoader(iri_str1);
		else
			onto_loader1 = new OntologyLoader(onto1);
		
		
		if (onto_loader1.getSignatureSize()<Parameters.min_size_overlapping)
			is_overlapping_onto1=false;
		
		fin = Calendar.getInstance().getTimeInMillis();
		loading_time = (float)((double)fin-(double)init)/1000.0;
		LogOutput.print("\tTime loading ontology 1 (s): " + (float)((double)fin-(double)init)/1000.0);
		LogOutput.print("\tOntology 1 Axioms (s): " + onto_loader1.getOWLOntology().getAxiomCount());
		
		
		init = Calendar.getInstance().getTimeInMillis();
		OntologyProcessing4Overlapping overlapping1 = new OntologyProcessing4Overlapping(onto_loader1.getOWLOntology(), lexicalUtilities);
		overlapping1.processOntologyClassLabels();
		overlapping1.setInvertedFile4Overlapping();
		
		if (is_overlapping_onto1){
			onto_loader1.createAxiomSet(); //we only keep set of axioms
			onto_loader1.clearOntology();
			overlapping1.clearOntoloy();
		}
		
		fin = Calendar.getInstance().getTimeInMillis();
		LogOutput.print("\tTime processing ontology 1 (s): " + (float)((double)fin-(double)init)/1000.0);
		
		
		
		//Loading Ontology 2 and Light processing
		init = Calendar.getInstance().getTimeInMillis();
		if (fromIRIs)
			onto_loader2 = new OntologyLoader(iri_str2);
		else
			onto_loader2 = new OntologyLoader(onto2);
		
		if (onto_loader2.getSignatureSize()<Parameters.min_size_overlapping)
			is_overlapping_onto2=false;
		
		fin = Calendar.getInstance().getTimeInMillis();
		loading_time += (float)((double)fin-(double)init)/1000.0;
		LogOutput.print("\tTime loading ontology 2 (s): " + (float)((double)fin-(double)init)/1000.0);
		LogOutput.print("\tOntology 2 Axioms (s): " + onto_loader2.getOWLOntology().getAxiomCount());
		
		
		
		
		init = Calendar.getInstance().getTimeInMillis();
		OntologyProcessing4Overlapping overlapping2 = new OntologyProcessing4Overlapping(onto_loader2.getOWLOntology(), lexicalUtilities);
		overlapping2.processOntologyClassLabels();
		overlapping2.setInvertedFile4Overlapping();
		
		if (is_overlapping_onto2){
			onto_loader2.createAxiomSet();
			onto_loader2.clearOntology();
			overlapping2.clearOntoloy();
		}
		
		fin = Calendar.getInstance().getTimeInMillis();
		LogOutput.print("\tTime processing ontology 2 (s): " + (float)((double)fin-(double)init)/1000.0);
		LogOutput.printAlways("LogMap 2 Loading Time (s): " + loading_time);
		
					
		//Intersects IF
		//Intersect and then create new ones... (review method intersection)
	
		init = Calendar.getInstance().getTimeInMillis();
		
		Set<Set<String>> if_weak_intersect;
		
		//We perform intersection an we only keep in inverted file the intersected elements
		if_weak_intersect = overlapping1.getWeakInvertedFile().keySet();
		if_weak_intersect.retainAll(overlapping2.getWeakInvertedFile().keySet());
		overlapping2.getWeakInvertedFile().keySet().retainAll(if_weak_intersect);

		LogOutput.print("\tSize IF intersected: " + if_weak_intersect.size());
		
		fin = Calendar.getInstance().getTimeInMillis();
		LogOutput.print("\tTime intersecting IF weak (s): " + (float)((double)fin-(double)init)/1000.0);
		
		
		
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
		
		fin = Calendar.getInstance().getTimeInMillis();
		LogOutput.print("\tTime extracting entities4modules (s): " + (float)((double)fin-(double)init)/1000.0);
		
		
		//Tests
		if (store) { //we add gold standard mappings to entities
			
			//Tests
			//Small overlapping  (only GS)
			//entities1.clear();
			//entities2.clear();
			
			//loadMappingsGS();			
			//LogOutput.print("\tSize entities 1: " + entities1.size());
			//LogOutput.print("\tSize entities 2: " + entities2.size());
		}
		
		
		
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
			
			
			if (store){
				//module_extractor1.saveExtractedModule(iri_str1_out);
				module_extractor1.saveExtractedModule(
						SynchronizedOWLManager.createOWLOntologyManager(),
						module1,
						iri_str1_out);
			}
			
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
			
			
			if (store){
				//module_extractor2.saveExtractedModule(iri_str2_out);
				module_extractor2.saveExtractedModule(
						SynchronizedOWLManager.createOWLOntologyManager(),
						module2,
						iri_str2_out);
			}
			
			module_extractor2.clearStrutures();
			
			onto_loader2.clearAxiomSet();			
		}
		
		else {
			module2 = onto_loader2.getOWLOntology();
		}
		
		entities2.clear(); //TODO Are we returning which entities have a weak anchor??
		
		fin = Calendar.getInstance().getTimeInMillis();
		LogOutput.print("\tSize module 1: " + module1.getAxiomCount());
		LogOutput.print("\tSize module 2: " + module2.getAxiomCount());
		LogOutput.print("\tSize classes module 1: " + module1.getClassesInSignature().size() + "  " + onto_loader1.getClassesInSignatureSize());
		LogOutput.print("\tSize classes module 2: " + module2.getClassesInSignature().size() + "  " + onto_loader2.getClassesInSignatureSize());
		LogOutput.print("\tTime extracting modules (s): " + (float)((double)fin-(double)init)/1000.0);
		
		
		
		
		//Only for evaluation purposes
		if (!test) return;
		
		
		//Get P&R: extend IF with stemming if necessary
		//Precission and Recall: compare labels from uris
		loadMappingsGS();
		loadMappingsLogmap(onto_loader1.getOntologyIRIStr(), onto_loader2.getOntologyIRIStr());
		
		Set<String> candidates_onto1 = new HashSet<String>();
		Set<String> candidates_onto2 = new HashSet<String>();
		
		//for (OWLEntity cls : entities1){
		for (OWLEntity cls : module1.getSignature()){
			candidates_onto1.add(Utilities.getEntityLabelFromURI(cls.getIRI().toString()));
		}
		for (OWLEntity cls : module2.getSignature()){
		//for (OWLEntity cls : entities2){
			candidates_onto2.add(Utilities.getEntityLabelFromURI(cls.getIRI().toString()));
		}
		
		LogOutput.print("ONTOLOGY 1");
		LogOutput.print("Recall GS mappings");
		getPrecisionAndRecallEntities(ent1_mappings_gs, candidates_onto1);
		LogOutput.print("Recall LogMap mappings");
		getPrecisionAndRecallEntities(ent1_mappings_logmap, candidates_onto1);
		LogOutput.print("Recall Good LogMap mappings");
		getPrecisionAndRecallEntities(ent1_mappings_logmap_ok, candidates_onto1);
		LogOutput.print("\tOverlapping size wrt whole ontology: " + (double)((double)module1.getClassesInSignature().size()*100.0/(double)onto_loader1.getClassesInSignatureSize()));
		
		LogOutput.print("ONTOLOGY 2");
		LogOutput.print("Recall GS mappings");
		getPrecisionAndRecallEntities(ent2_mappings_gs, candidates_onto2);
		LogOutput.print("Recall LogMap mappings");
		getPrecisionAndRecallEntities(ent2_mappings_logmap, candidates_onto2);
		LogOutput.print("Recall Good LogMap mappings");
		getPrecisionAndRecallEntities(ent2_mappings_logmap_ok, candidates_onto2);
		LogOutput.print("\tOverlapping size wrt whole ontology: " + (double)((double)module2.getClassesInSignature().size()*100.0/(double)onto_loader2.getClassesInSignatureSize()));
		
		
		//}
		//catch (Exception e){
		//	System.err.println(e.getMessage() + "   " + e.getCause());
		//	e.printStackTrace();
		//}
		

		
		
		
	}
	
	
	/**
	 * Compares candidate entities from ontologies with mapped entities through umls
	 */
	private void getPrecisionAndRecallEntities(Set<String> umls_entities, Set<String> candidate_entities){
		
		
		/*for (String cand : candidate_entities){
			if (!umls_entities.contains(cand)){
				LogOutput.print(cand);
			}
		}*/
		
		Set <String> intersection;
		double precision;
		double recall;
		
		/*int i=0;
		for (String e : umls_entities){
			LogOutput.print(e);
			i++;
			if (i==20)
				break;
			
		}
		LogOutput.print("\n\n\n");
		
		i=0;
		for (String e : candidate_entities){
			LogOutput.print(e);
			i++;
			if (i==20)
				break;
			
		}*/
		
		
		
		intersection=new HashSet<String>(umls_entities);
		
		intersection.retainAll(candidate_entities);
		
		
		precision=((double)intersection.size())/((double)candidate_entities.size());
		recall=((double)intersection.size())/((double)umls_entities.size());
	
		//LogOutput.print("\tPrecision: " + precision); Not important
		LogOutput.print("\tRecall: " + recall);
		
		
	}
	
	
	/**
	 * UMLS mappings will be our gold standard.
	 * @throws Exception
	 */
	private void loadMappingsGS() throws Exception{
	
		ReadFile reader = new ReadFile(file_gs_mappings);
		
		
		String line;
		String[] elements;
		
		line=reader.readLine();
		
		
		
		
		while (line!=null) {
			
			if (line.indexOf("|")<0){
				line=reader.readLine();
				continue;
			}
			
			elements=line.split("\\|");
			
			ent1_mappings_gs.add(Utilities.getEntityLabelFromURI(elements[0]));			
			ent2_mappings_gs.add(Utilities.getEntityLabelFromURI(elements[1]));
			 
			entities1.add(OWLManager.getOWLDataFactory().getOWLClass(IRI.create(elements[0])));
			entities2.add(OWLManager.getOWLDataFactory().getOWLClass(IRI.create(elements[1])));
			
				
			line=reader.readLine();
		}		
		
		reader.closeBuffer();
		
		
		LogOutput.print("GS Entities 1: " + ent1_mappings_gs.size());
		LogOutput.print("GS Entities 2: " + ent2_mappings_gs.size());
				
		
	}
	
	
	/**
	 * We loaf mappings extracted with logmap beta 0.9
	 */
	private void loadMappingsLogmap(String uri1, String uri2) throws Exception{
		
		OWLOntologyManager managerOnto;
		OWLOntology moduleonto;
		
		
		managerOnto = OWLManager.createOWLOntologyManager();
		
		moduleonto = managerOnto.loadOntology(IRI.create(file_logmap_mappings));
		
		for (OWLClass cls : moduleonto.getClassesInSignature()){
			
			if (Utilities.getNameSpaceFromURI(cls.getIRI().toString()).equals(uri1))
				ent1_mappings_logmap.add(Utilities.getEntityLabelFromURI(cls.getIRI().toString()));	
			else if (Utilities.getNameSpaceFromURI(cls.getIRI().toString()).equals(uri2))
				ent2_mappings_logmap.add(Utilities.getEntityLabelFromURI(cls.getIRI().toString()));
			
		}
		
		LogOutput.print("LogMap Entities 1: " + ent1_mappings_logmap.size());
		LogOutput.print("LogMap Entities 2: " + ent2_mappings_logmap.size());
		
		ent1_mappings_logmap_ok = new HashSet<String>(ent1_mappings_logmap);
		ent2_mappings_logmap_ok = new HashSet<String>(ent2_mappings_logmap);
		
		ent1_mappings_logmap_ok.retainAll(ent1_mappings_gs);
		ent2_mappings_logmap_ok.retainAll(ent2_mappings_gs);
		
		LogOutput.print("LogMap Entities 1 (Good): " + ent1_mappings_logmap_ok.size());
		LogOutput.print("LogMap Entities 2 (Good): " + ent2_mappings_logmap_ok.size());
		
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

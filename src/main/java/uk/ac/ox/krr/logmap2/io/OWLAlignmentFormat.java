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
package uk.ac.ox.krr.logmap2.io;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


import java.util.List;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.RDFXMLOntologyFormat;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import uk.ac.ox.krr.logmap2.owlapi.SynchronizedOWLManager;
import uk.ac.ox.krr.logmap2.utilities.Utilities;


public class OWLAlignmentFormat extends OutputMappingsFormat {

	
	List<OWLOntologyChange> listAxioms2Add= new ArrayList<OWLOntologyChange>();
	
	String logical_iri="http://www.cs.ox.ac.uk/isg/projects/LogMap/mappings.owl";
	
	OWLOntologyManager managerMappingsOnto;
	OWLOntology mappingsOnto;
	OWLDataFactory factory;
	
	Set<OWLNamedIndividual> indivSet = new HashSet<OWLNamedIndividual>();
	
	Set<OWLAnnotation> annSet = new HashSet<OWLAnnotation>();
	//String mappings_IRI="http://www.cs.ox.ac.uk/ontologies/oaei/owl/mappings.owl";
	String iri_measure_str = logical_iri + "#measure";
	String iri_entity1_str = logical_iri + "#entity1";
	String iri_entity2_str = logical_iri + "#entity2";
	String iri_relation_str = logical_iri + "#relation";
	
	
	
	public OWLAlignmentFormat(String output_file_str)  throws Exception{
		super(output_file_str);
		
		setOutput();
		
		//TODO: add annotation with confidence
	}
	
	/**
	 * Sets the manager and onto for OWL ontology
	 */
	protected void setOutput()  throws Exception{

		//managerMappingsOnto = OWLManager.createOWLOntologyManager();
		managerMappingsOnto = SynchronizedOWLManager.createOWLOntologyManager();
		mappingsOnto = managerMappingsOnto.createOntology(IRI.create(logical_iri));

		factory = managerMappingsOnto.getOWLDataFactory();
		
	}

	
	private OWLAxiom createEquivalenceMapping(OWLClass cls1, OWLClass cls2, Set<OWLAnnotation> annotations){
		
		return factory.getOWLEquivalentClassesAxiom(cls1, cls2, annotations);		
		
	}
	
	
	private OWLAxiom createDataPropertyEquivalenceMapping(OWLDataProperty dprop1, OWLDataProperty dprop2, Set<OWLAnnotation> annotations){
		
		return factory.getOWLEquivalentDataPropertiesAxiom(dprop1, dprop2, annotations);		
		
	}
	
	private OWLAxiom createObjectPropertyEquivalenceMapping(OWLObjectProperty oprop1, OWLObjectProperty oprop2, Set<OWLAnnotation> annotations){
		
		return factory.getOWLEquivalentObjectPropertiesAxiom(oprop1, oprop2, annotations);		
		
	}

	
	private OWLAxiom createSubClassOfMapping(OWLClass cls1, OWLClass cls2, Set<OWLAnnotation> annotations){
		
		return factory.getOWLSubClassOfAxiom(cls1, cls2, annotations);		
		
	}
	
	
	private OWLAxiom createSubDataPropertyMapping(OWLDataProperty dprop1, OWLDataProperty dprop2, Set<OWLAnnotation> annotations){
		
		return factory.getOWLSubDataPropertyOfAxiom(dprop1, dprop2, annotations);		
		
	}
	
	private OWLAxiom createSubObjectPropertyMapping(OWLObjectProperty oprop1, OWLObjectProperty oprop2, Set<OWLAnnotation> annotations){
		
		return factory.getOWLSubObjectPropertyOfAxiom(oprop1, oprop2, annotations);		
		
	}
	
	
	private OWLAxiom createSuperClassOfMapping(OWLClass cls1, OWLClass cls2, Set<OWLAnnotation> annotations){
		
		return factory.getOWLSubClassOfAxiom(cls2, cls1, annotations);		
		
	}
	
	private OWLAxiom createSuperDataPropertyMapping(OWLDataProperty dprop1, OWLDataProperty dprop2, Set<OWLAnnotation> annotations){
		
		return factory.getOWLSubDataPropertyOfAxiom(dprop2, dprop1, annotations);		
		
	}
	
	private OWLAxiom createSuperObjectPropertyMapping(OWLObjectProperty oprop1, OWLObjectProperty oprop2, Set<OWLAnnotation> annotations){
		
		return factory.getOWLSubObjectPropertyOfAxiom(oprop2, oprop1, annotations);		
		
	}
	
	private OWLAxiom createSameIndividualMapping(OWLNamedIndividual indiv1, OWLNamedIndividual indiv2, Set<OWLAnnotation> annotations){
		
		indivSet.clear();					
		indivSet.add(indiv1);
		indivSet.add(indiv2);
		
		return factory.getOWLSameIndividualAxiom(indivSet, annotations);
	}
	
	
	
	private Set<OWLAnnotation> getAnnotations4Axiom(String iri1, String iri2, double confidence, String relation){
		
		OWLLiteral confidence_literal;
		OWLAnnotation ann_measure;
		OWLAnnotationProperty ann_property;
		
		OWLLiteral ent1_literal;
		OWLAnnotation ann_ent1;
		OWLAnnotationProperty ann_property_ent1;

		OWLLiteral ent2_literal;
		OWLAnnotation ann_ent2;
		OWLAnnotationProperty ann_property_ent2;
		
		OWLLiteral relation_literal;
		OWLAnnotation ann_relation;
		OWLAnnotationProperty ann_property_relation;
		
		ann_property = factory.getOWLAnnotationProperty(IRI.create(iri_measure_str));		
		confidence_literal = factory.getOWLLiteral(confidence);		
		ann_measure = factory.getOWLAnnotation(ann_property, confidence_literal);
		
		ann_property_ent1 = factory.getOWLAnnotationProperty(IRI.create(iri_entity1_str));			
		ent1_literal = factory.getOWLLiteral(iri1);			
		ann_ent1 = factory.getOWLAnnotation(ann_property_ent1, ent1_literal);
		
		ann_property_ent2 = factory.getOWLAnnotationProperty(IRI.create(iri_entity2_str));			
		ent2_literal = factory.getOWLLiteral(iri2);			
		ann_ent2 = factory.getOWLAnnotation(ann_property_ent2, ent2_literal);
		
		ann_property_relation = factory.getOWLAnnotationProperty(IRI.create(iri_relation_str));			
		relation_literal = factory.getOWLLiteral(relation);			
		ann_relation = factory.getOWLAnnotation(ann_property_relation, relation_literal);
		
		
		//Annotations are added to the axiom
		annSet.clear();
		annSet.add(ann_measure);
		annSet.add(ann_ent1);
		annSet.add(ann_ent2);
		annSet.add(ann_relation);
		
		
		return annSet; 
		
		
		
	}
	
	
	/**
	 * Creates OWL axioms for mapping and adds it to the list
	 */
	public void addClassMapping2Output(String iri1, String iri2, int dir_mapping, double conf) {	
		
		if (dir_mapping==Utilities.EQ){
			listAxioms2Add.add(new AddAxiom(
					mappingsOnto,
					createEquivalenceMapping(
							factory.getOWLClass(IRI.create(iri1)),
							factory.getOWLClass(IRI.create(iri2)),
							getAnnotations4Axiom(iri1, iri2, conf, "="))));
		}
		else if (dir_mapping==Utilities.L2R){
			listAxioms2Add.add(new AddAxiom(
					mappingsOnto,
					createSubClassOfMapping(
							factory.getOWLClass(IRI.create(iri1)),
							factory.getOWLClass(IRI.create(iri2)),
							getAnnotations4Axiom(iri1, iri2, conf, "<"))));
		}
		else{ //if (dir_mapping==Utilities.R2L){
			listAxioms2Add.add(new AddAxiom(
					mappingsOnto,
					createSuperClassOfMapping(
							factory.getOWLClass(IRI.create(iri1)),
							factory.getOWLClass(IRI.create(iri2)),
							getAnnotations4Axiom(iri1, iri2, conf, ">"))));
		}
	}
	
	
	/**
	 * Creates OWL axioms for mapping and adds it to the list
	 */
	public void addDataPropMapping2Output(String iri1, String iri2, int dir_mapping, double conf) {	
		
		if (dir_mapping==Utilities.EQ){
			listAxioms2Add.add(new AddAxiom(
					mappingsOnto,
					createDataPropertyEquivalenceMapping(
							factory.getOWLDataProperty(IRI.create(iri1)),
							factory.getOWLDataProperty(IRI.create(iri2)),
							getAnnotations4Axiom(iri1, iri2, conf, "="))));
		}
		else if (dir_mapping==Utilities.L2R){
			listAxioms2Add.add(new AddAxiom(
					mappingsOnto,
					createSubDataPropertyMapping(
							factory.getOWLDataProperty(IRI.create(iri1)),
							factory.getOWLDataProperty(IRI.create(iri2)),
							getAnnotations4Axiom(iri1, iri2, conf, "<"))));
		}
		else{ //if (dir_mapping==Utilities.R2L){
			listAxioms2Add.add(new AddAxiom(
					mappingsOnto,
					createSuperDataPropertyMapping(
							factory.getOWLDataProperty(IRI.create(iri1)),
							factory.getOWLDataProperty(IRI.create(iri2)),
							getAnnotations4Axiom(iri1, iri2, conf, ">"))));
		}
	}

	
	
	/**
	 * Creates OWL axioms for mapping and adds it to the list
	 */
	public void addObjPropMapping2Output(String iri1, String iri2, int dir_mapping, double conf) {	
		
		if (dir_mapping==Utilities.EQ){
			listAxioms2Add.add(new AddAxiom(
					mappingsOnto,
					createObjectPropertyEquivalenceMapping(
							factory.getOWLObjectProperty(IRI.create(iri1)),
							factory.getOWLObjectProperty(IRI.create(iri2)),
							getAnnotations4Axiom(iri1, iri2, conf, "="))));
		}
		else if (dir_mapping==Utilities.L2R){
			listAxioms2Add.add(new AddAxiom(
					mappingsOnto,
					createSubObjectPropertyMapping(
							factory.getOWLObjectProperty(IRI.create(iri1)),
							factory.getOWLObjectProperty(IRI.create(iri2)),
							getAnnotations4Axiom(iri1, iri2, conf, "<"))));
		}
		else{ //if (dir_mapping==Utilities.R2L){
			listAxioms2Add.add(new AddAxiom(
					mappingsOnto,
					createSuperObjectPropertyMapping(
							factory.getOWLObjectProperty(IRI.create(iri1)),
							factory.getOWLObjectProperty(IRI.create(iri2)),
							getAnnotations4Axiom(iri1, iri2, conf, ">"))));
		}
	}
	
	
	public void addInstanceMapping2Output(String iri1, String iri2, double conf)  throws Exception {
		listAxioms2Add.add(new AddAxiom(
				mappingsOnto,
				createSameIndividualMapping(
						factory.getOWLNamedIndividual(IRI.create(iri1)),
						factory.getOWLNamedIndividual(IRI.create(iri2)),
						getAnnotations4Axiom(iri1, iri2, conf, "="))));
	}
	
	
	
	
	/**
	 * Saves owl ontology with mappings
	 */
	public void saveOutputFile() throws Exception{
		
		managerMappingsOnto.applyChanges(listAxioms2Add);		
		managerMappingsOnto.saveOntology(mappingsOnto, new RDFXMLOntologyFormat(), IRI.create(output_file));
		
	}
	
	public OWLOntology getOWLOntology() throws Exception{
		managerMappingsOnto.applyChanges(listAxioms2Add);
		return mappingsOnto;
		
	}
	
	
	

}

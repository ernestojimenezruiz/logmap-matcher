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
package uk.ac.ox.krr.logmap2.oaei.reader;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLSameIndividualAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationValueVisitor;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;


import uk.ac.ox.krr.logmap2.io.LogOutput;
import uk.ac.ox.krr.logmap2.mappings.objects.MappingObjectStr;
import uk.ac.ox.krr.logmap2.utilities.Utilities;

/**
 * Reads mappings from OWL ontology. Annotation properties are inside axioms
 * @author root
 *
 */
public class OWLAlignmentReader extends MappingsReader {

	
	OWLMappingAxiomVisitor4Reader owlMappingAxiomVisitor = new OWLMappingAxiomVisitor4Reader();
	ValueVisitor valueVisitor = new ValueVisitor();
	
	
	public OWLAlignmentReader(String owl_alignment_file) throws Exception{
		
		//try{
			OWLOntology onto_mappings = loadOntology(owl_alignment_file);
			
			for (OWLAxiom ax : onto_mappings.getAxioms()){
			
				//System.out.println(ax);
				
				owlMappingAxiomVisitor.reInitValues();
				
				ax.accept(owlMappingAxiomVisitor);
			
				/*System.out.println(owlMappingAxiomVisitor.getIRIEntity1() + "\n" +
						owlMappingAxiomVisitor.getIRIEntity2() + "\n" +
						owlMappingAxiomVisitor.getMeasure() + "\n" +
						owlMappingAxiomVisitor.getRelation() + "\n" +
						owlMappingAxiomVisitor.getTypeMapping());
				*/
				
				if (owlMappingAxiomVisitor.getIRIEntity1()==null || owlMappingAxiomVisitor.getIRIEntity2()==null
					|| owlMappingAxiomVisitor.getIRIEntity1().equals("") || owlMappingAxiomVisitor.getIRIEntity2().equals(""))
					continue; //no good axioms
				
				
				//Add for all axioms? I think so...
				mappings.add(new MappingObjectStr(
						owlMappingAxiomVisitor.getIRIEntity1(), 
						owlMappingAxiomVisitor.getIRIEntity2(), 
						owlMappingAxiomVisitor.getMeasure(),
						owlMappingAxiomVisitor.getRelation(),
						owlMappingAxiomVisitor.getTypeMapping()));	
			}
			
			LogOutput.print("Read OWL mapping objects: " + getMappingObjectsSize());
			
		//}
		//catch (Exception e){
		//	System.err.println("Error reading OWL mappings file: " + e.getMessage());
		//}
		
	}
	
	
	private OWLOntology loadOntology(String uri) throws Exception{
		System.out.println("Loading ontology " + uri);
		return loadOntology(IRI.create(uri));
	}
	
	private OWLOntology loadOntology(IRI uri) throws Exception{
		OWLOntologyManager ontologyManager=OWLManager.createOWLOntologyManager();
		return ontologyManager.loadOntology(uri);
		
	}
	
	
	
	
	public static void main(String[] args) {
		
		String mappings_path = "file:/usr/local/data/DataUMLS/UMLS_Onto_Versions/OAEI_datasets/Mappings_Tools_2012/";
		
		
		try{
			new OWLAlignmentReader(mappings_path + "logmap_small_fma2nci_new.owl");
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	
	
	
	
	/**
	 * 
	 * Mapping axiom visitor that extracts the annotations attached to each axioms.
	 * 
	 * @author Ernesto
	 *
	 */
	public class OWLMappingAxiomVisitor4Reader extends OWLMappingAxiomVisitor {
		
		private String iri1;
		private String iri2;
		private int relation;
		private int type;
		private double measure;
		
		public String getIRIEntity1(){
			return iri1;
		}
		public String getIRIEntity2(){
			return iri2;
		}
		public int getRelation(){
			return relation;
		}
		public int getTypeMapping(){
			return type;
		}
		
		public double getMeasure(){
			return measure;
		}
		
		public void setMeasure(double conf){
			measure=conf;
		}
		
		public void setRelation(int rel){
			relation=rel;
		}
		
		
		private void reInitValues(){
			iri1="";
			iri2="";
			type = Utilities.CLASSES;
			relation = Utilities.EQ;
		}
		
		
		
		private void treatAnnotation(OWLAnnotation ann){
			
			String property = ann.getProperty().getIRI().toString().toLowerCase();
			
			if (property.contains(MappingsReader.ENTITY1)){
				ann.getValue().accept(valueVisitor);
				iri1 = valueVisitor.getStringLiteral();
			}
			else if (property.contains(MappingsReader.ENTITY2)){
				ann.getValue().accept(valueVisitor);
				iri2 = valueVisitor.getStringLiteral();
			}
			else if (property.contains(MappingsReader.MEASURE)){
				ann.getValue().accept(valueVisitor); //It also sets value
			}
			else if (property.contains(MappingsReader.RELATION)){
				ann.getValue().accept(valueVisitor); //It also sets value
			}
			
			
		}
		
		
		//Unique axioms used in current OWL mappings
		public void visit(OWLSubClassOfAxiom ax){
			type = Utilities.CLASSES;
			
			//System.out.println(ax);
			
			for (OWLAnnotation ann : ax.getAnnotations()){
				
				treatAnnotation(ann);
				
			}
			
			
		}
		public void visit(OWLEquivalentClassesAxiom ax){
			
			relation = Utilities.EQ;
			type = Utilities.CLASSES;
			
			//if (ax.toString().contains("Vocal_fold")){
			//System.out.println(ax);
				
			for (OWLAnnotation ann : ax.getAnnotations()){
					
				treatAnnotation(ann);
					
			}
				
			//}
			
		}
		
		public void visit(OWLEquivalentObjectPropertiesAxiom ax){
			
			relation = Utilities.EQ;
			type = Utilities.OBJECTPROPERTIES;
			
			for (OWLAnnotation ann : ax.getAnnotations()){
				treatAnnotation(ann);
			}
			
		}
		public void visit(OWLSubObjectPropertyOfAxiom ax){
			
			type = Utilities.OBJECTPROPERTIES;
			
			for (OWLAnnotation ann : ax.getAnnotations()){	
				treatAnnotation(ann);
			}
			
		}
		
		
		public void visit(OWLEquivalentDataPropertiesAxiom ax){
			
			relation = Utilities.EQ;
			type = Utilities.DATAPROPERTIES;
			
			for (OWLAnnotation ann : ax.getAnnotations()){	
				treatAnnotation(ann);
			}
			
		}
		public void visit(OWLSubDataPropertyOfAxiom ax){
			type = Utilities.DATAPROPERTIES;
			
			for (OWLAnnotation ann : ax.getAnnotations()){	
				treatAnnotation(ann);
			}
		}
		
		
		public void visit(OWLSameIndividualAxiom ax){
			
			relation = Utilities.EQ;
			type = Utilities.INSTANCES;
			
			for (OWLAnnotation ann : ax.getAnnotations()){	
				treatAnnotation(ann);
			}
			
		}

	}
	
	
	
	public class ValueVisitor implements OWLAnnotationValueVisitor{
		
		String literal_str;
		
		public void visit(OWLLiteral literal){
			
			literal_str = literal.getLiteral();
			
			if (literal.isDouble()){
				owlMappingAxiomVisitor.setMeasure(literal.parseDouble());
			}
			else {
				if (literal_str.equals("=")){
					owlMappingAxiomVisitor.setRelation(Utilities.EQ);
				}
				else if (literal_str.equals(">")){
					owlMappingAxiomVisitor.setRelation(Utilities.R2L);
				}
				else if (literal_str.equals("<")){
					owlMappingAxiomVisitor.setRelation(Utilities.L2R);
				}
				
				
			}
			
		}
		
		public String getStringLiteral(){
			return literal_str;
		}
		
		public void visit(IRI iri){
			//Do nothing
		}
		
		public void visit(OWLAnonymousIndividual individual){
			//Do nothing
		}
		
		
		
	}

	
	
	
	
}

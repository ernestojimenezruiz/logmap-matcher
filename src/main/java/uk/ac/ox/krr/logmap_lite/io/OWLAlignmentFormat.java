package uk.ac.ox.krr.logmap_lite.io;

import java.util.ArrayList;




import java.util.List;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.RDFXMLDocumentFormat;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import uk.ac.ox.krr.logmap_lite.LogMap_Lite;
import uk.ac.ox.krr.logmap2.owlapi.SynchronizedOWLManager;


public class OWLAlignmentFormat extends OutputMappingsFormat {

	
	List<OWLOntologyChange> listAxioms2Add= new ArrayList<OWLOntologyChange>();
	
	String logical_iri="http://www.cs.ox.ac.uk/isg/projects/LogMap/mappings.owl";
	
	OWLOntologyManager managerMappingsOnto;
	OWLOntology mappingsOnto;
	OWLDataFactory factory;
	
	
	public OWLAlignmentFormat(String output_file_str)  throws Exception{
		super(output_file_str);
		
		setOutput();
		
		//TODO: add annotation with confidence
	}
	
	/**
	 * Sets the manager and onto for OWL ontology
	 */
	protected void setOutput()  throws Exception{

		//TODO!! This might be important in WEB logmap
		//managerMappingsOnto = OWLManager.createOWLOntologyManager();
		managerMappingsOnto = SynchronizedOWLManager.createOWLOntologyManager();
		mappingsOnto = managerMappingsOnto.createOntology(IRI.create(logical_iri));

		factory = managerMappingsOnto.getOWLDataFactory();
		
	}

	
	private OWLAxiom createEquivalenceMapping(OWLClass cls1, OWLClass cls2){
		
		return factory.getOWLEquivalentClassesAxiom(cls1, cls2);		
		
	}
	
	
	private OWLAxiom createDataPropertyEquivalenceMapping(OWLDataProperty dprop1, OWLDataProperty dprop2){
		
		return factory.getOWLEquivalentDataPropertiesAxiom(dprop1, dprop2);		
		
	}
	
	private OWLAxiom createObjectPropertyEquivalenceMapping(OWLObjectProperty oprop1, OWLObjectProperty oprop2){
		
		return factory.getOWLEquivalentObjectPropertiesAxiom(oprop1, oprop2);		
		
	}

	
	private OWLAxiom createSubClassOfMapping(OWLClass cls1, OWLClass cls2){
		
		return factory.getOWLSubClassOfAxiom(cls1, cls2);		
		
	}
	
	
	private OWLAxiom createSubDataPropertyMapping(OWLDataProperty dprop1, OWLDataProperty dprop2){
		
		return factory.getOWLSubDataPropertyOfAxiom(dprop1, dprop2);		
		
	}
	
	private OWLAxiom createSubObjectPropertyMapping(OWLObjectProperty oprop1, OWLObjectProperty oprop2){
		
		return factory.getOWLSubObjectPropertyOfAxiom(oprop1, oprop2);		
		
	}
	
	
	private OWLAxiom createSuperClassOfMapping(OWLClass cls1, OWLClass cls2){
		
		return factory.getOWLSubClassOfAxiom(cls2, cls1);		
		
	}
	
	private OWLAxiom createSuperDataPropertyMapping(OWLDataProperty dprop1, OWLDataProperty dprop2){
		
		return factory.getOWLSubDataPropertyOfAxiom(dprop2, dprop1);		
		
	}
	
	private OWLAxiom createSuperObjectPropertyMapping(OWLObjectProperty oprop1, OWLObjectProperty oprop2){
		
		return factory.getOWLSubObjectPropertyOfAxiom(oprop2, oprop1);		
		
	}
	
	private OWLAxiom createSameIndividualMapping(OWLNamedIndividual indiv1, OWLNamedIndividual indiv2){
		
		return factory.getOWLSameIndividualAxiom(indiv1, indiv2);
	}
	
	
	/**
	 * Creates OWL axioms for mapping and adds it to the list
	 */
	public void addClassMapping2Output(String iri1, String iri2, int dir_mapping, double conf) {	
		
		if (dir_mapping==LogMap_Lite.EQ){
			listAxioms2Add.add(new AddAxiom(
					mappingsOnto,
					createEquivalenceMapping(
							factory.getOWLClass(IRI.create(iri1)),
							factory.getOWLClass(IRI.create(iri2)))));
		}
		else if (dir_mapping==LogMap_Lite.L2R){
			listAxioms2Add.add(new AddAxiom(
					mappingsOnto,
					createSubClassOfMapping(
							factory.getOWLClass(IRI.create(iri1)),
							factory.getOWLClass(IRI.create(iri2)))));
		}
		else{ //if (dir_mapping==LogMap_Lite.R2L){
			listAxioms2Add.add(new AddAxiom(
					mappingsOnto,
					createSuperClassOfMapping(
							factory.getOWLClass(IRI.create(iri1)),
							factory.getOWLClass(IRI.create(iri2)))));
		}
	}
	
	
	/**
	 * Creates OWL axioms for mapping and adds it to the list
	 */
	public void addDataPropMapping2Output(String iri1, String iri2, int dir_mapping, double conf) {	
		
		if (dir_mapping==LogMap_Lite.EQ){
			listAxioms2Add.add(new AddAxiom(
					mappingsOnto,
					createDataPropertyEquivalenceMapping(
							factory.getOWLDataProperty(IRI.create(iri1)),
							factory.getOWLDataProperty(IRI.create(iri2)))));
		}
		else if (dir_mapping==LogMap_Lite.L2R){
			listAxioms2Add.add(new AddAxiom(
					mappingsOnto,
					createSubDataPropertyMapping(
							factory.getOWLDataProperty(IRI.create(iri1)),
							factory.getOWLDataProperty(IRI.create(iri2)))));
		}
		else{ //if (dir_mapping==LogMap_Lite.R2L){
			listAxioms2Add.add(new AddAxiom(
					mappingsOnto,
					createSuperDataPropertyMapping(
							factory.getOWLDataProperty(IRI.create(iri1)),
							factory.getOWLDataProperty(IRI.create(iri2)))));
		}
	}

	
	
	/**
	 * Creates OWL axioms for mapping and adds it to the list
	 */
	public void addObjPropMapping2Output(String iri1, String iri2, int dir_mapping, double conf) {	
		
		if (dir_mapping==LogMap_Lite.EQ){
			listAxioms2Add.add(new AddAxiom(
					mappingsOnto,
					createObjectPropertyEquivalenceMapping(
							factory.getOWLObjectProperty(IRI.create(iri1)),
							factory.getOWLObjectProperty(IRI.create(iri2)))));
		}
		else if (dir_mapping==LogMap_Lite.L2R){
			listAxioms2Add.add(new AddAxiom(
					mappingsOnto,
					createSubObjectPropertyMapping(
							factory.getOWLObjectProperty(IRI.create(iri1)),
							factory.getOWLObjectProperty(IRI.create(iri2)))));
		}
		else{ //if (dir_mapping==LogMap_Lite.R2L){
			listAxioms2Add.add(new AddAxiom(
					mappingsOnto,
					createSuperObjectPropertyMapping(
							factory.getOWLObjectProperty(IRI.create(iri1)),
							factory.getOWLObjectProperty(IRI.create(iri2)))));
		}
	}
	
	
	public void addInstanceMapping2Output(String iri1, String iri2, double conf)  throws Exception {
		listAxioms2Add.add(new AddAxiom(
				mappingsOnto,
				createSameIndividualMapping(
						factory.getOWLNamedIndividual(IRI.create(iri1)),
						factory.getOWLNamedIndividual(IRI.create(iri2))
						)
				));
	}
	
	
	/**
	 * Saves owl ontology with mappings
	 */
	public void saveOutputFile() throws Exception{
		
		managerMappingsOnto.applyChanges(listAxioms2Add);		
		managerMappingsOnto.saveOntology(mappingsOnto, new RDFXMLDocumentFormat(), IRI.create(output_file));
		
	}
	
	public OWLOntology getOWLOntology() throws Exception{
		managerMappingsOnto.applyChanges(listAxioms2Add);
		return mappingsOnto;
		
	}
	
	
	

}

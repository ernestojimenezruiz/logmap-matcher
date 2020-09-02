package uk.ac.ox.krr.logmap2.test.bioportal;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import uk.ac.ox.krr.logmap2.OntologyLoader;

public class testLoader {

	
	
	public static void main(String[] args) {
		
		String onto;
		String path = "file:/home/ernesto/Documents/BioPortal/Ontologies/";
		
		
		onto = "file:/home/ernesto/Documents/ImageAnnotation-SIRIUS/Images_annotations_snapshot/last_version_onto/Geological_annotation_ontology_v0.90.owl";
		//onto = path + "AFO.owl"; 
		onto = path + "CHMO.owl";
		//onto = path + "OBI.owl";
		//onto = path + "ERO.owl";
		//onto = path + "MS.owl";
		//onto = path + "BAO.owl";
		//onto = path + "EFO.owl";
		//onto = path + "MESH.ttl";
		//onto = path + "NCIT.owl";
		
		try {
			OntologyLoader loader = new OntologyLoader(onto);
			System.out.println(loader.getDLNameOntology());
			System.out.println(loader.getSignatureSize());
			
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}

package uk.ac.ox.krr.logmap2.test.oaei;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import uk.ac.ox.krr.logmap2.OntologyLoader;

public class TestSnomedPunning {

	
	public TestSnomedPunning(){
		
		if (true)
			return; 
		
		
		
		String uri = "file:/home/ernesto/Documents/Datasets/LargeBio/snomed20090131_replab.owl";
		
		try {
			
			OntologyLoader loader = new OntologyLoader(uri);
			
			Set<String> classes = new HashSet<String>();
			Set<String> properties = new HashSet<String>();
			
			for (OWLClass cls : loader.getOWLOntology().getClassesInSignature()) {
				classes.add(cls.toStringID());
			}
			
			for (OWLObjectProperty prop : loader.getOWLOntology().getObjectPropertiesInSignature()) {
				properties.add(prop.toStringID());
			}
			
			System.out.println(loader.getOWL2DLProfileViolation());
			
			//CAses both as class and property
			classes.retainAll(properties);
			
			for (String cls_str : classes) {
				System.out.println(cls_str);
			}
			
			
			
			
			
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	
	
	public static void main(String[] args) {
		new TestSnomedPunning();
	}

}

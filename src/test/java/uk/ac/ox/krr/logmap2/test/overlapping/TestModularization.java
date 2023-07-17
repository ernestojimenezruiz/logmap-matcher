package uk.ac.ox.krr.logmap2.test.overlapping;

import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import uk.ac.manchester.syntactic_locality.OntologyModuleExtractor;
import uk.ac.ox.krr.logmap2.OntologyLoader;
import uk.ac.ox.krr.logmap2.io.ReadFile;

public class TestModularization {

	public TestModularization(String uri_onto, String path_file_entities, String snomed_module_file) throws FileNotFoundException, OWLOntologyCreationException {

		ReadFile reader = new ReadFile(path_file_entities);
		String line = reader.readLine();
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		Set<OWLEntity> entities = new HashSet<OWLEntity>();
		
		while (line != null){
			
			//System.out.println(line);			
						
			IRI iri = IRI.create(line);
			
			entities.add(OWLManager.getOWLDataFactory().getOWLClass(iri));
			
			
			line = reader.readLine();
			
			
		}
		
		System.out.println(entities.size());
		
		OntologyLoader loader = new OntologyLoader(uri_onto);		
		OWLOntology onto = loader.getOWLOntology();
		
		OntologyModuleExtractor extractor = 
				new OntologyModuleExtractor(onto, true, true, false, true);
				
		OWLOntology module=extractor.extractAsOntology(manager, entities, IRI.create("http://snomed.info/"));
		
		
		System.out.println(module.getSignature().size());
		
		extractor.saveExtractedModule(manager, module, snomed_module_file);
		
		
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String path = "/home/ernesto/Documents/SNOMED-CT/";

		String snomed_ids = path + "snomed_ids.txt";
		String snomed = "file:" + path + "snomedct-201907.owl";
		String snomed_module = "file:" + path + "snomedct-201907_module.owl";
		
		try {
			new TestModularization(snomed, snomed_ids, snomed_module);
		} catch (FileNotFoundException | OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}

package uk.ac.ox.krr.logmap2.utilities;

import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import uk.ac.ox.krr.logmap2.OntologyLoader;
import uk.ac.ox.krr.logmap2.io.WriteFile;

/**
 * Entities not used in the reference alignment
 */
public class ExtractEntitiesNotInAlignmentBioML2025 {
	
	
	//Entities not in alignment contain the following:
	//<ann:use_in_alignment rdf:datatype="http://www.w3.org/2001/XMLSchema#boolean">false</ann:use_in_alignment>
	private final String annotation_iri = "http://oaei.ontologymatching.org/bio-ml/ann/use_in_alignment";
	
	
	
	public ExtractEntitiesNotInAlignmentBioML2025(String path_onto) throws OWLOntologyCreationException {
		
		
		OntologyLoader loader = new OntologyLoader("file:/" + path_onto);
		
		boolean entities_to_ignore=false;
		int number_entities_to_ignore=0;
		
		
		WriteFile writer = new WriteFile(path_onto + "_entities_not_in_alignment.txt");
		
		
		
		for (OWLClass cls :  loader.getClassesInSignature()){
			
			entities_to_ignore=false;
			
			
			for (OWLAnnotationAssertionAxiom annAx: loader.getOWLOntology().getAnnotationAssertionAxioms(cls.getIRI())) 
				if (annAx.getAnnotation().getProperty().getIRI().toString().equals(annotation_iri)) 
					if (annAx.getAnnotation().getValue().asLiteral().isPresent()) 
						if (annAx.getAnnotation().getValue().asLiteral().isPresent()) 
							if (annAx.getAnnotation().getValue().asLiteral().get().isBoolean())
								if (!annAx.getAnnotation().getValue().asLiteral().get().parseBoolean())	
									entities_to_ignore=true;
								
			
			if (entities_to_ignore) {
				writer.writeLine(cls.getIRI().toString());
				number_entities_to_ignore++;
			}
			
		}		
		
		writer.closeBuffer();
		System.out.println("Added to file " + number_entities_to_ignore + " classes not in alignment  out of " + loader.getClassesInSignatureSize());
		
		
	}
	
	
	public static void main(String[] args){
		
		
		String path_onto;
		
		path_onto="C:/Users/Ernes/Documents/OAEI-Bio-Tracks/bio-ml-2025/ncit-doid/ncit.owl";
		path_onto="C:/Users/Ernes/Documents/OAEI-Bio-Tracks/bio-ml-2025/ncit-doid/doid.owl";
		
		path_onto="C:/Users/Ernes/Documents/OAEI-Bio-Tracks/bio-ml-2025/omim-ordo/omim.owl";
		path_onto="C:/Users/Ernes/Documents/OAEI-Bio-Tracks/bio-ml-2025/omim-ordo/ordo.owl";
		
		//snomed-fma.body
		path_onto="C:/Users/Ernes/Documents/OAEI-Bio-Tracks/bio-ml-2025/snomed-fma.body/snomed.body.owl";
		//path_onto="C:/Users/Ernes/Documents/OAEI-Bio-Tracks/bio-ml-2025/snomed-fma.body/fma.body.owl";
		
		//snomed-ncit.neoplas
		//path_onto="C:/Users/Ernes/Documents/OAEI-Bio-Tracks/bio-ml-2025/snomed-ncit.neoplas/snomed.neoplas.owl";
		//path_onto="C:/Users/Ernes/Documents/OAEI-Bio-Tracks/bio-ml-2025/snomed-ncit.neoplas/ncit.neoplas.owl";
		
		//snomed-ncit.pharm
		//path_onto="C:/Users/Ernes/Documents/OAEI-Bio-Tracks/bio-ml-2025/snomed-ncit.pharm/snomed.pharm.owl";
		//path_onto="C:/Users/Ernes/Documents/OAEI-Bio-Tracks/bio-ml-2025/snomed-ncit.pharm/ncit.pharm.owl";
		
		
		try {
			new ExtractEntitiesNotInAlignmentBioML2025(path_onto);
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}	
	
	
	
	
	

}

package uk.ac.ox.krr.logmap2.test.oaei;

import java.io.File;

import org.semanticweb.owlapi.model.IRI;

import uk.ac.ox.krr.logmap2.OntologyLoader;
import uk.ac.ox.krr.logmap2.io.OutPutFilesManager;
import uk.ac.ox.krr.logmap2.mappings.objects.MappingObjectStr;
import uk.ac.ox.krr.logmap2.oaei.reader.MappingsReaderManager;

public class TestRDF2TXTMappings {
	
	
	public TestRDF2TXTMappings(String input_file, String iri_onto1, String iri_onto2) throws Exception{
		
		//1. Load mappings
		MappingsReaderManager reader = new MappingsReaderManager(input_file, MappingsReaderManager.OAEIFormat);
	
		//2. Output mappings in several formats
		OutPutFilesManager writer = new OutPutFilesManager();
		
		
		String output_file = input_file.replace(".rdf", "");		
		writer.createOutFiles(output_file, OutPutFilesManager.AllFlatFormats, "", "");
		//writer.createOutFiles(output_file, OutPutFilesManager.TurtleFormat, "", "");
		
		
		
		//3. Load ontologies to check the type of the mapping
		System.out.println(iri_onto1);
		System.out.println(iri_onto2);		
		OntologyLoader onto1 =  new OntologyLoader(iri_onto1);
		//System.out.println(onto1.getClassesInSignatureSize());
		
		for (MappingObjectStr mapping : reader.getMappingObjects()) {
			
			//We check one side, assuming mixed mappings are not in GT
			
			if (onto1.getOWLOntology().containsClassInSignature(IRI.create(mapping.getIRIStrEnt1())))
				mapping.setTypeOfMapping(MappingObjectStr.CLASSES);				
			else if (onto1.getOWLOntology().containsObjectPropertyInSignature(IRI.create(mapping.getIRIStrEnt1()))) 
				mapping.setTypeOfMapping(MappingObjectStr.OBJECTPROPERTIES);
			else if (onto1.getOWLOntology().containsDataPropertyInSignature(IRI.create(mapping.getIRIStrEnt1())))
				mapping.setTypeOfMapping(MappingObjectStr.DATAPROPERTIES);
			else if (onto1.getOWLOntology().containsIndividualInSignature(IRI.create(mapping.getIRIStrEnt1())))
				mapping.setTypeOfMapping(MappingObjectStr.INSTANCES);
			
		}
			
		
		
		//4. Add mappings to outut structures
		writer.addMappings(reader.getMappingObjects());
		writer.closeAndSaveFiles();
		
		
	}
	
	
	public static String getRightExtension(String path_no_extesion) {
		
		String[] extensions = new String[3];
		extensions[0]=".rdf";
		extensions[1]=".owl";
		extensions[2]=".ttl";
		
		
		File file_check;
		
		for (int i=0; i<extensions.length; i++) {
			file_check = new File(path_no_extesion + extensions[i]);
			if (file_check.exists())
				return extensions[i];
		}
		
		
		
		return ".owl"; //default
		
		
	}
	
	
	public static void main(String[] args){
		
		String path;
		
		//path = "C:/Users/Ernes/OneDrive/Documents/OAEI/anatomy-dataset/reference.rdf";
		
		//path = "C:/Users/Ernes/OneDrive/Documents/OAEI/largebio/fma-nci/oaei_FMA2NCI_UMLS_mappings_with_flagged_repairs.rdf";
		//path = "C:/Users/Ernes/OneDrive/Documents/OAEI/largebio/fma-snomed/oaei_FMA2SNOMED_UMLS_mappings_with_flagged_repairs.rdf";
		//path = "C:/Users/Ernes/OneDrive/Documents/OAEI/largebio/snomed-nci/oaei_SNOMED2NCI_UMLS_mappings_with_flagged_repairs.rdf";
		
		
		//String base_path = "C:/Users/Ernes/OAEI-INM713-IN3067/";
		String base_path = "C:/Users/Ernes/OAEI-Datasets-Guard/";
		
		path=base_path + "anatomy";
		path=base_path + "conference";
		path=base_path + "bio-ml";
		path=base_path + "circular-economy";
		path=base_path + "digital-humanities";
		path=base_path + "knowledge-graph";
		
		String onto_base_path = path + "/ontologies/";
		
		
		File directory = new File(path);
		String filenames[] = directory.list();
		String pattern=".rdf"; //For reference alignments
		String[] elements;

		
		String path_onto1;
		String path_onto2;		
		String iri_onto1;
		String iri_onto2;
		
		for(int i=0; i<filenames.length; i++){
			
			if (!filenames[i].contains(pattern)) 
				continue;
			
			elements = filenames[i].split("-|\\.rdf");
			
			
			//We need to check for the right ontology extensions
			path_onto1 = onto_base_path + elements[0]; //no extensions		
			iri_onto1 = "file:/" + path_onto1 + getRightExtension(path_onto1);
			
			path_onto2 = onto_base_path + elements[1]; //no extension
			iri_onto2 = "file:/" + path_onto2 + getRightExtension(path_onto2);
			
			
			
			try {
				new TestRDF2TXTMappings(path+ "/"+ filenames[i], iri_onto1, iri_onto2);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
			
		
		
		
		//SINLE CASE
		/*try {
			new TestRDF2TXTMappings(path);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
	}
	
	
	
	
	
	
	

}

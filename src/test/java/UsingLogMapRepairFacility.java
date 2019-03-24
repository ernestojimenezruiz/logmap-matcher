import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.MissingImportHandlingStrategy;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyLoaderConfiguration;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.parameters.Imports;

import uk.ac.ox.krr.logmap2.LogMap2_RepairFacility;
import uk.ac.ox.krr.logmap2.mappings.objects.MappingObjectStr;
import uk.ac.ox.krr.logmap2.oaei.reader.MappingsReaderManager;



/**
 * 
 * Example of using LogMap's repair facility. 
 * 
 * @author Ernesto
 *
 */
public class UsingLogMapRepairFacility {


	
	OWLOntology onto1;
	OWLOntology onto2;

	OWLOntologyManager onto_manager;
		
	
	
	public UsingLogMapRepairFacility(){
		
		
		try{
		
			Set<MappingObjectStr> input_mappings;
			
			//String onto1_iri = "file:/home/ernesto/oaei_2012/fma2nci/oaei2012_FMA_small_overlapping_nci.owl";
			//String onto2_iri = "file:/home/ernesto/oaei_2012/fma2nci/oaei2012_NCI_small_overlapping_fma.owl";
			//String onto2_iri = "file:/usr/ernesto/NewTest_FMA2SNOMED/fma3.2.owl";
            //String onto1_iri = "file:/usr/ernesto/NewTest_FMA2SNOMED/snomed-full-jan11-2013.owl";
            
            String onto1_iri = "file:/home/ernesto/Documents/OAEI_2016/Pistoia/HP.rdf";
            String onto2_iri = "file:/home/ernesto/Documents/OAEI_2016/Pistoia/MP.rdf";
            
            //String onto1_iri = "file:/home/ernesto/Documents/OAEI_2016/Pistoia/DOID.rdf";
            //String onto2_iri = "file:/home/ernesto/Documents/OAEI_2016/Pistoia/ORDO.rdf";
			
			
			onto_manager = OWLManager.createOWLOntologyManager();
			
			//onto_manager.setSilentMissingImportsHandling(true);		
			OWLOntologyLoaderConfiguration config = new OWLOntologyLoaderConfiguration();
			config.setMissingImportHandlingStrategy(MissingImportHandlingStrategy.SILENT);
			onto_manager.setOntologyLoaderConfiguration(config);
			
			
			onto1 = onto_manager.loadOntology(IRI.create(onto1_iri));			
			onto2 = onto_manager.loadOntology(IRI.create(onto2_iri));
			
			System.out.println("Ontology 1 size: " + onto1.getSignature(Imports.INCLUDED).size());
			System.out.println("Ontology 2 size: " + onto2.getSignature(Imports.INCLUDED).size());
			
			
			//Input from a file (RDF OAEI Alignment format)
			//Mappings are also accepted as TXT or OWL with an special format
			//String input_mappings_file = "/home/ernesto/oaei_2012/Mappings_Tools_2012/logmaplt_small_fma2nci.rdf";
			//String input_mappings_file = "/usr/ernesto/NewTest_FMA2SNOMED/mapping-output.txt";
			String input_mappings_file = "/home/ernesto/Documents/OAEI_2016/Pistoia/MergedMappings/HP_MP.txt";
			//String input_mappings_file = "/home/ernesto/Documents/OAEI_2016/Pistoia/MergedMappings/DOID_ORDO.txt";
			
			
			//MappingsReaderManager readermanager = new MappingsReaderManager(input_mappings_file, "RDF");
			MappingsReaderManager readermanager = new MappingsReaderManager(input_mappings_file, "TXT");
			input_mappings = readermanager.getMappingObjects();
			
			/*
			 * Custom input
			 * 			 
			input_mappings.add(
					new MappingObjectStr(
							"http://bioontology.org/projects/ontologies/fma/fmaOwlDlComponent_2_0#Vertebra",  //iri entity 1
							"http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl#Vertebra", 					  //iri entity 2
							1.0,		 				//Confidence
							MappingObjectStr.EQ,		//Direction  
							MappingObjectStr.CLASSES	//Type
							)
					);
			*/
			
			LogMap2_RepairFacility logmap2_repair = 
					new LogMap2_RepairFacility(
							onto1,				//Ontology1 
							onto2,				//Ontology2
							input_mappings,		//Input Mappings
							true,				//If the intersection or overlapping of the ontologies are extracted before the repair
							false);				//If the repair is performed in a two steps process (optimal) or in one cleaning step (more aggressive)
			
			
						
			//Set of mappings repaired by LogMap
			Set<MappingObjectStr> repaired_mappings = logmap2_repair.getCleanMappings();
			
			System.out.println("Num repaired mappings using LogMap: " + repaired_mappings.size());
			
			
			//Optional: check if input mappings lead to unsatisfiabilities using HermiT reasoner
			System.out.println("Satisfiability with input mappings");
			//logmap2_repair.checkSatisfiabilityInputMappings();
			
			//Optional: check if repaired mappings by LogMap still lead to unsatisfiabilities using HermiT reasoner
			System.out.println("Satisfiability with repaired mappings using LogMap");
			logmap2_repair.checkSatisfiabilityCleanMappings();
			
			
			
		}
		catch (Exception e){
			e.printStackTrace();
		}
		
	}
	

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		new UsingLogMapRepairFacility();

	}

}





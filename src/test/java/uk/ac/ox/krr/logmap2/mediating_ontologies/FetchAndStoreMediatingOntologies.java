package uk.ac.ox.krr.logmap2.mediating_ontologies;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLOntology;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import uk.ac.ox.krr.logmap2.GetRepresentativeLabelsSetForMappings;
import uk.ac.ox.krr.logmap2.LogMap2_Matcher;
import uk.ac.ox.krr.logmap2.OntologyLoader;
import uk.ac.ox.krr.logmap2.bioportal.MediatingOntologyExtractor;
import uk.ac.ox.krr.logmap2.mappings.objects.MappingObjectStr;
import uk.ac.ox.krr.logmap2.oaei.reader.MappingsReaderManager;

/**
 * Takes a parent folder with a config JSON file, reads the input ontologies and finds the mediating ontologies in Bioportal.
 * The class is opinionated in structure of the parent folder and location and name of the config file.
 * To run this pipeline please remember the arguments for the JVM 
 * -Xms500M -Xmx25G -DentityExpansionLimit=10000000 --add-opens=java.base/java.lang=ALL-UNNAMED
 * and the arguments for the program <full path to parent folder>.
 * For example, to run from command line for a 16GB RAM machine (with two different jar files for logmap and the test suite), please run
 * java -Xms500M -Xmx25G -DentityExpansionLimit=10000000 --add-opens=java.base/java.lang=ALL-UNNAMED
 *     -cp logmap-matcher-4.0-tests.jar:logmap-matcher-4.0.jar 
 *         uk.ac.ox.krr.logmap2.mediating_ontologies.RunMediatingOntologiesPipeline /home/valentina/git-repos/test-data/test-argparse/
 * For more details, refer to the README in the package.
 * @author valcar1980
 */
public class FetchAndStoreMediatingOntologies {

	
	public static void main(String[] args) {

		MediatingOntologiesUtils moUtils = new MediatingOntologiesUtils();
		moUtils.getParentFolder(args);
		moUtils.readConfigJSON();
		moUtils.createSubDirectoriesFromParent();
		

		// Expected input
		String onto1_iri = "file:" + moUtils.sourceOntoPath;
		String onto2_iri = "file:" + moUtils.targetOntoPath;
		String s2tFilePath = moUtils.sourceToTargetDirPath + "source2target";
		String storeOntoPath = moUtils.localOntoRepoPath;
		
		
		//Initialisations
		StoreMediatingOntologies moStorer = new StoreMediatingOntologies();
		List<String> moList = null;
		CreateMappingsBetweenTwoOntologies onto_mapper = new CreateMappingsBetweenTwoOntologies();
		Set<MappingObjectStr>  s2tOnto_mappings = Collections.emptySet();
		MediatingOntologiesUtils mo_fetcher = new MediatingOntologiesUtils();
		
		// Conditional input - list of mediating ontologies
		String listMOFilePath = null;
		if (moUtils.overrideMOnum == true) {
			listMOFilePath = moUtils.parentPath + "logmap_top" + Integer.toString(moUtils.maxMONum) + "_mediating_ontologies.txt";
		}else {
			//TODO
			System.out.println("Should read deault paramater and provide max number of mediating ontologies here.");
			listMOFilePath = moUtils.parentPath + "logmap_top10_mediating_ontologies.txt";
		}
		
		// If we have a file with the list of mediating ontologies, we can start reading from it already
		File listFile = new File(listMOFilePath);

		
		if (listFile.exists() && listFile.isFile()) {
			System.out.println("Mediating ontologies list already exists at " 
		+ listMOFilePath + " \n Skipping to fetching ontologies from list");
			try {
				moList = moStorer.getOntologyListFromFile(listMOFilePath);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} else {
		System.out.println("Mediating ontology list is missing, proceeding to create one.");


		// If ontology maps already exist, skip and move to checking whether the list of MO exists
		System.out.println("First, we check if we have mappings between source and target.");
		//TODO can we check without loading the whole file?
		Set<MappingObjectStr> checkS2TMappings = moUtils.readMappingsFromFile(moUtils.sourceToTargetDirPath); 
		if (checkS2TMappings.size()>0) {
			System.out.println("Found mappings between source and target, no need to create them.");

			// We need the representative labels to find the mediating ontologies
			Set<String> s2tRepLabels = Collections.emptySet();
			try {
			OntologyLoader loader1 = new OntologyLoader(onto1_iri);
			OntologyLoader loader2 = new OntologyLoader(onto2_iri);
			
			GetRepresentativeLabelsSetForMappings representativeLabelExtractor = 
					new GetRepresentativeLabelsSetForMappings(
							loader1.getOWLOntology(), 
							loader2.getOWLOntology(), 
							checkS2TMappings);
			
			s2tRepLabels = representativeLabelExtractor.getRepresentativeLabels();
			MediatingOntologyExtractor mo_extract = new MediatingOntologyExtractor(s2tRepLabels);
			moList = mo_extract.getSelectedMediatingOntologies();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		else
		{ 
			System.out.println("No mappings found between source and target, running Logmap now");
			LogMap2_Matcher onto_matcher= onto_mapper.createMappings(onto1_iri, onto2_iri, moUtils.maxMONum);
			s2tOnto_mappings = onto_matcher.getLogmap2_Mappings();
			moUtils.saveOntologyMappings(s2tOnto_mappings, s2tFilePath, onto1_iri, onto2_iri);
			/*
			 * Identify suitable mediating ontologies and store their label onto a list
			 */
			moList = mo_fetcher.extractMediatingOntologyList(onto_matcher);
		}
		
		// save moList to file
		mo_fetcher.saveListMediatingOntolgies(moList, listMOFilePath);
		
	}	


		/*
		 * Store all ontologies from list of mediating ontologies
		 */
		int countOnto = moList.size();
		System.out.println("There are " + countOnto + " mediating ontologies in the list");
		System.out.println("Starting Mediating Ontologies fetching step");

		
			int all_counter = 0; // all counts, including failed downloads
			int success_counter =0; // existing ontologies or successfully downloaded
			for (String ontoStr : moList) {
				all_counter += 1;
				
				if (success_counter ==10) {
					System.out.println("Stopping downloads, we have reached 10 ontologies");
					break;
				}
				System.out.println("Fetching ontology No.  " + all_counter + " from list,  label:  " + ontoStr);


				boolean isOntoThere = moStorer.checkOntoPath(ontoStr, storeOntoPath, ".owl");

				if (isOntoThere == true) {

					System.out.println("Ontology file already exists at location, skipping");
					success_counter++;
					continue;
				}
				else {
					try {
					OWLOntology moDownload = moStorer.CallBioPortal(ontoStr, storeOntoPath);
					System.out.println("Fetched ontology " + ontoStr);
	
					
					moStorer.saveOntology(ontoStr, moDownload, storeOntoPath);
					System.out.println("Stored ontology " + ontoStr);
					success_counter++;
					}
					catch (Exception e) 
					{
						System.out.println("Coudln't fetch " + ontoStr + "; skipping");
						continue;
						//e.printStackTrace();
					}

				}
					
				// we update the counter regardless of successful download
			}
		
		
		System.out.println("All Ontologies are stored in " + storeOntoPath);
		}
	}
		
		
		
		
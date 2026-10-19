/*
 * Use this class to create mappings between source and target and store them in folder. Input provided via config.
 * Useful because it allows to overwrite the max number of mediating ontologies
 */

//TODO Resume correct use of config/parameters to change max number of mediating ontologies

package uk.ac.ox.krr.logmap2.mediating_ontologies;

import java.io.File;
import java.util.Set;


import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.MissingImportHandlingStrategy;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyLoaderConfiguration;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import uk.ac.ox.krr.logmap2.LogMap2_Matcher;
import uk.ac.ox.krr.logmap2.mappings.objects.MappingObjectStr;

public class CreateMappingsBetweenTwoOntologies {
	

	public CreateMappingsBetweenTwoOntologies() {
		
	}
	
	public Set<MappingObjectStr> createMappings(String onto1_iri, String onto2_iri){
		OWLOntologyManager onto_manager = OWLManager.createOWLOntologyManager();
		// In case an import is broken
		OWLOntologyLoaderConfiguration config = new OWLOntologyLoaderConfiguration();
		// Important to reassign value, see https://github.com/owlcs/owlapi/issues/503
		config = config.setMissingImportHandlingStrategy(MissingImportHandlingStrategy.SILENT);
		onto_manager.setOntologyLoaderConfiguration(config);
		OWLOntology onto1 = null;
		OWLOntology onto2 = null;
		try {
		System.out.println("Loading the first ontology " + onto1_iri);
		onto1 = onto_manager.loadOntology(IRI.create(onto1_iri));
		}catch (Exception e) {
			System.out.println("Failed to load " + onto1_iri);
			return null;
		}
		try {
		System.out.println("Loading the second ontology " + onto2_iri);
		onto2 = onto_manager.loadOntology(IRI.create(onto2_iri));
		}catch ( Exception e) {
			System.out.println("Failed to load " + onto2_iri);
			return null;
		}
		System.out.println("Starting the matching task using LogMap2_Matcher");
		LogMap2_Matcher logmap2 = new LogMap2_Matcher(onto1, onto2);
		Set<MappingObjectStr> mappings = logmap2.getLogmap2_Mappings();
		System.out.println("Mapping completed, mappings count = " + mappings.size());
		
		
		return mappings;
	}
	

	public LogMap2_Matcher createMappings(String onto1_iri, String onto2_iri, int max_mediating_ontologies) {

		try {

			OWLOntologyManager onto_manager = OWLManager.createOWLOntologyManager();

			// In case an import is broken
			OWLOntologyLoaderConfiguration config = new OWLOntologyLoaderConfiguration();

			// Important to reassign value, see https://github.com/owlcs/owlapi/issues/503
			config = config.setMissingImportHandlingStrategy(MissingImportHandlingStrategy.SILENT);
			onto_manager.setOntologyLoaderConfiguration(config);
			System.out.println("Loading the first ontology " + onto1_iri);
			OWLOntology onto1 = onto_manager.loadOntology(IRI.create(onto1_iri));
			System.out.println("Loading the second ontology " + onto2_iri);
			OWLOntology onto2 = onto_manager.loadOntology(IRI.create(onto2_iri));

			System.out.println("Starting the matching task using LogMap2_Matcher");
			LogMap2_Matcher logmap2 = new LogMap2_Matcher(onto1, onto2, max_mediating_ontologies);
			// Optionally LogMap also accepts the IRI strings as input
			// LogMap2_Matcher logmap2 = new LogMap2_Matcher(onto1_iri, onto2_iri);
			System.out.println("Completing the matching task using LogMap2_Matcher");
			/*
			 * // Set of mappings computed my LogMap Set<MappingObjectStr> logmap2_mappings
			 * = logmap2.getLogmap2_Mappings(); // Get representative labels Set<String>
			 * logmap2_representative_labels = logmap2.getRepresentativeLabelsForMappings();
			 * System.out.println("Number of mappings computed by LogMap: " +
			 * logmap2_mappings.size()); return logmap2_mappings;
			 */
			return logmap2;
		} catch (Exception e) {
			e.printStackTrace();

		}
		return null;
	}
	
	

	public static void main(String[] args) {
		MediatingOntologiesUtils moUtils = new MediatingOntologiesUtils();
		moUtils.getParentFolder(args);
		moUtils.createSubDirectoriesFromParent();
		moUtils.readConfigJSON();
		Boolean readFromJSON = false;
		moUtils.getSource2TargetLogmapMappings(readFromJSON);
		
		System.out.println("We will store mappings in " + moUtils.source2targetLogmapMappingsPath);
		
		String onto1_iri = "file:" + moUtils.sourceOntoPath;
		String onto2_iri = "file:" + moUtils.targetOntoPath;
		//TODO check that the files exist before matching!
		System.out.println(onto1_iri + "\n" + onto2_iri);
		
		CreateMappingsBetweenTwoOntologies myLogMap = new CreateMappingsBetweenTwoOntologies();
		System.out.println("Mapping");
		long startTime = System.nanoTime();
		LogMap2_Matcher logmapMatcher = myLogMap.createMappings(onto1_iri, onto2_iri, 12);
		Set<MappingObjectStr>  onto_mappings = logmapMatcher.getLogmap2_Mappings();
		// printOntologyMappings(onto_mappings);
		// Set<String> representative_labels = onto_mappings.getRepresentativeLabelsForMappings();
		long endTime = System.nanoTime();
		System.out.println(
				"Map matching task completed.\t" + Math.floor((endTime - startTime) / 10e9) + " seconds elapsed");
		moUtils.saveOntologyMappings(onto_mappings, moUtils.source2targetLogmapMappingsPath, onto1_iri,onto2_iri);
		

	}

}
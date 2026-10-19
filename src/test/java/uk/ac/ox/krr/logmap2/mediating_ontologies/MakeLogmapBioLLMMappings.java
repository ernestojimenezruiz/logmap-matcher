/*
 * Where we do the work described that combines annotated composed mappings (from mediating ontologies) with mappings obtained by LogmapLLM to produce
 * LogmapBioLLM.
 */

package uk.ac.ox.krr.logmap2.mediating_ontologies;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.MissingImportHandlingStrategy;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyLoaderConfiguration;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import uk.ac.ox.krr.logmap2.LogMap3_RepairFacility;
import uk.ac.ox.krr.logmap2.io.ReadFile;
import uk.ac.ox.krr.logmap2.mappings.objects.MappingObjectStr;
import uk.ac.ox.krr.logmap2.oaei.reader.FlatAlignmentReader;
import uk.ac.ox.krr.logmap2.oaei.reader.MappingsReaderManager;

//import uk.ac.ox.krr.logmap2.oaei.reader.FlatAlignmentReader;

public class MakeLogmapBioLLMMappings {
	private static String repairStrategy;
	
	private static void setRepairStrategy(String repairOption) {
		if(repairOption.equals("single") || repairOption.equals("double")) {
		repairStrategy = repairOption;
		}else {
			System.out.println("Incorrect option for repair strategy. Only 'single' and 'double' are available");
		}
	}
	
	

	public static void main(String[] args) {

		/*
		 * Set-up
		 */
		MakeLogmapBioLLMMappings setup = new MakeLogmapBioLLMMappings();
		setRepairStrategy("single");
		MediatingOntologiesUtils moUtils = new MediatingOntologiesUtils();
		moUtils.getParentFolder(args);
		moUtils.createSubDirectoriesFromParent();// it will add new folders, only if they don't already exist
		moUtils.readConfigJSON();
		moUtils.createSubDirectoriesFromParent(); // in case something is missing, no overwrite should take place
		OWLOntologyManager onto_manager = OWLManager.createOWLOntologyManager();
		// In case an import is broken
		OWLOntologyLoaderConfiguration config = new OWLOntologyLoaderConfiguration();
		// Important to reassign value, see https://github.com/owlcs/owlapi/issues/503
		config = config.setMissingImportHandlingStrategy(MissingImportHandlingStrategy.SILENT);
		onto_manager.setOntologyLoaderConfiguration(config);

		/*
		 * Load source and target ontologies
		 */

		// source: onto1
		String onto1_iri = moUtils.sourceOntoPath;

		OWLOntology onto1 = null;

		try {
			System.out.println("Loading the first ontology " + onto1_iri);
			onto1 = onto_manager.loadOntology(IRI.create("file:" + onto1_iri));
		} catch (Exception e) {
			System.out.println("Failed to load " + onto1_iri);
		}

		// target: onto2
		String onto2_iri = moUtils.targetOntoPath;
		OWLOntology onto2 = null;

		try {
			System.out.println("Loading the second ontology " + onto2_iri);
			onto2 = onto_manager.loadOntology(IRI.create("file:" + onto2_iri));

		} catch (Exception e) {
			System.out.println("Failed to load " + onto2_iri);
		}

		/*
		 * Load LogmapLLM mappings
		 */

		String parentPath = moUtils.LogmapBioLLMMappingsPath;

		// Load the mappings found by Logmap LLM (default and mutual subsumption)
		// Get mappings from LogmapLLM Default
		Set<MappingObjectStr> llmDefaultMappings = moUtils.readMappingsFromFile(moUtils.logmapLLMDefaultPath);
		Set<MappingObjectStr> llmMutualSubMappings = moUtils.readMappingsFromFile(moUtils.logmapLLMMSubPath);

		// Load composed mappings after they have been annotated using LLM (default and
		// mutual subsumption)

		Set<MappingObjectStr> llmTrueComposedMapsWithLLMDefault = moUtils
				.readAnnotatedMappingsFromTSV(moUtils.annotatedComposedLLMDefaultPath, "onlyTrue");
		Set<MappingObjectStr> llmTrueComposedMapsWithLLMMSub = moUtils
				.readAnnotatedMappingsFromTSV(moUtils.annotatedComposedLLMMSubtPath, "onlyTrue");

		// Read only the mappings that were annotated as True

		// LLM Default
		System.out.println("Composed mappings that are true according to LLM(Default): "
				+ llmTrueComposedMapsWithLLMDefault.size() + " mappings");
		Set<MappingObjectStr> LogmapLLMBio_default = new HashSet<>();
		LogmapLLMBio_default.addAll(llmDefaultMappings);
		System.out.println("Mappings found by LogmapLLM(Default): " + llmDefaultMappings.size() + " mappings");
		LogmapLLMBio_default.addAll(llmTrueComposedMapsWithLLMDefault);
		System.out.println("Combining these two together, LogmapBioLLM(Default) contains: "
				+ LogmapLLMBio_default.size() + " mappings");
		// Save the mappings
		String LogmapLLMBio_defaultName = parentPath + "logmapBioLLMDefaultResults";
		moUtils.saveOntologyMappings(LogmapLLMBio_default, LogmapLLMBio_defaultName, onto1_iri, onto2_iri);

		// LLM Mutual Subsumption
		System.out.println("Composed mappings that are true according to LLM(mutual subsumption): "
				+ llmTrueComposedMapsWithLLMMSub.size() + " mappings");
		Set<MappingObjectStr> LogmapLLMBio_msub = new HashSet<>();
		LogmapLLMBio_msub.addAll(llmMutualSubMappings);
		System.out.println(
				"Mappings found by LogmapLLM(MutualSubsumption): " + llmMutualSubMappings.size() + " mappings");
		LogmapLLMBio_msub.addAll(llmTrueComposedMapsWithLLMMSub);
		System.out.println("Combining these two together, LogmapBioLLM(MutualSubsumption) contains: "
				+ LogmapLLMBio_msub.size() + " mappings");
		// Save the mappings
		String LogmapLLMBio_msubName = parentPath + "logmapBioLLMMSubResults";
		moUtils.saveOntologyMappings(LogmapLLMBio_msub, LogmapLLMBio_msubName, onto1_iri, onto2_iri);

		/*
		 * Single-step repair strategy
		 */
		if(repairStrategy.equals("single")) {
		// fixed_mappings are llmDefaultMappings
		// mappings2review are llmTrueComposedMapsWithLLMDefault
		LogMap3_RepairFacility TrueWithDefault_repair = new LogMap3_RepairFacility(onto1, onto2, llmDefaultMappings,
				llmTrueComposedMapsWithLLMDefault);
		Set<MappingObjectStr> LogmapBioLLM_default_r = TrueWithDefault_repair.getCleanMappings();
		System.out.println("LLM Default - Size of fixed_mappings:" + llmDefaultMappings.size() + "\tmappings to review:"
				+ llmTrueComposedMapsWithLLMDefault.size() + "\t repaired mappings: " + LogmapBioLLM_default_r.size());

		LogmapBioLLM_default_r.addAll(llmDefaultMappings);
		// save mappings
		String LogmapLLmBio_default_rName = parentPath + "logmapBioLLMDefaultResults_repaired";
		moUtils.saveOntologyMappings(LogmapBioLLM_default_r, LogmapLLmBio_default_rName, onto1_iri, onto2_iri);

		// fixed_mappings are llmMutualSubMappings
		// mappings2review are llmTrueComposedMapsWithLLMDefault
		LogMap3_RepairFacility TrueWithMSub_repair = new LogMap3_RepairFacility(onto1, onto2, llmMutualSubMappings,
				llmTrueComposedMapsWithLLMMSub);
		Set<MappingObjectStr> LogmapBioLLM_msub_r = TrueWithMSub_repair.getCleanMappings();
		System.out.println("LLM MSub - Size of fixed_mappings:" + llmMutualSubMappings.size() + "\tmappings to review:"
				+ llmTrueComposedMapsWithLLMMSub.size() + "\t repaired mappings: " + LogmapBioLLM_msub_r.size());

		LogmapBioLLM_msub_r.addAll(llmMutualSubMappings);

		String LogmapLLmBio_msub_rName = parentPath + "logmapBioLLMMSubResults_repaired";
		moUtils.saveOntologyMappings(LogmapBioLLM_msub_r, LogmapLLmBio_msub_rName, onto1_iri, onto2_iri);
		}
		
		if(repairStrategy.equals("double")) {
			System.out.println("Double-step repair strategy not implemented yet!");
		}

	}
}

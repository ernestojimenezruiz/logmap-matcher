package uk.ac.ox.krr.logmap2.oaei.oracle;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLOntology;

import uk.ac.ox.krr.logmap2.LogMap2_RepairFacility;
import uk.ac.ox.krr.logmap2.LogMap3_RepairFacility;
import uk.ac.ox.krr.logmap2.OntologyLoader;
import uk.ac.ox.krr.logmap2.io.LogOutput;
import uk.ac.ox.krr.logmap2.io.OutPutFilesManager;
import uk.ac.ox.krr.logmap2.io.ReadFile;
import uk.ac.ox.krr.logmap2.mappings.objects.MappingObjectStr;
import uk.ac.ox.krr.logmap2.oaei.reader.MappingsReaderManager;
import uk.ac.ox.krr.logmap2.reasoning.ReasonerManager;
import uk.ac.ox.krr.logmap2.reasoning.SatisfiabilityIntegration;
import uk.ac.ox.krr.logmap2.utilities.StandardMeasures;

public class MergeAxiomsFromOracle {
	
	public MergeAxiomsFromOracle(
			String path_mappings, 
			String path_oracle_annotations, 
			String path_output,
			String iri_onto1,
			String iri_onto1_path,
			String iri_onto2,
			String iri_onto2_path,
			String reference_path,
			String path_entities_ignore1,
			String path_entities_ignore2) throws Exception {
		
		
		MappingsReaderManager mapping_reader = new MappingsReaderManager(path_mappings, MappingsReaderManager.OAEIFormat);
		
		OutPutFilesManager mapping_manager = new OutPutFilesManager();
		
		
		boolean two_steps_repair;
		two_steps_repair = true; //DEFAULT
		//two_steps_repair = false;
		
		
		//Loads ontos
		LogOutput.printAlways("Loading ontologies...");
		OntologyLoader loader1 = new OntologyLoader(iri_onto1_path);
		OntologyLoader loader2 = new OntologyLoader(iri_onto2_path);
		LogOutput.printAlways("...Done");
				
		
		
		Set<MappingObjectStr> logmapllm_mappings = new HashSet<MappingObjectStr>();
		Set<MappingObjectStr> composed_mappings = new HashSet<MappingObjectStr>();
		
		//Read base mappings		
		logmapllm_mappings.addAll(mapping_reader.getMappingObjects());
		//Read oracle annotations
		composed_mappings.addAll(readAnnotatedMappingsFromTSV(path_oracle_annotations));
		
		System.out.println("Original LogMapLLM mappings: " + logmapllm_mappings.size());
		
		if (two_steps_repair) {
		
			//We fix logmap-llm mappings and try to fix composed mappings
			LogMap3_RepairFacility logmap3repair = new LogMap3_RepairFacility(
							loader1.getOWLOntology(), 
							loader2.getOWLOntology(),
							logmapllm_mappings,
							composed_mappings);
			
			
			
			//Clean mappings only contains the repaired subset from the composition
			System.out.println("Repaired composed mappings (logmap3): " + logmap3repair.getCleanMappings().size());
			
			
			
			//Add repaired ones
			logmapllm_mappings.addAll(logmap3repair.getCleanMappings());
			System.out.println("LogMapLLM Including repaired composed mappings: " + logmapllm_mappings.size());
			System.out.println("\n");
		}
		else {
			logmapllm_mappings.addAll(composed_mappings);
			System.out.println("LogMapLLM Including composed mappings (no repair): " + logmapllm_mappings.size());
			System.out.println("\n");
		}
		
		
		
		SatisfiabilityIntegration.setReasoner(ReasonerManager.ELK);
					
		
		
		//Repair all together just in case
		LogMap2_RepairFacility logmap2repair = new LogMap2_RepairFacility(
				loader1.getOWLOntology(), 
				loader2.getOWLOntology(), 
				logmapllm_mappings,
				false,
				false, //always optimal?
				true, //satisfiability_check,
				path_output);
		
		
		
		System.out.println("Repaired mappings (ALL): " + logmap2repair.getCleanMappings().size());
		
		
		mapping_manager.createOutFiles(path_output, OutPutFilesManager.AllFlatFormats, iri_onto1, iri_onto2);
		
		mapping_manager.addMappings(logmap2repair.getCleanMappings());
		
		
		mapping_manager.closeAndSaveFiles();
		
		
				
		//String reference_path,
		//String path_entities_ignore1,
		//String path_entities_ignore2
		
		
		
	}
	
	
	
	/**
	 * Load mappings from annotated TSV file
	 * Each row has tab-separated elements and it is expected to have the following structure:
	 * Source,Target,Prediction,Confidence
	 * For example:
	 * http://human.owl #NCI_C49191 	http://mouse.owl#MA_0000702		=	0.47 	CLS		False
	 * @param fullPath
	 */
	public static Set<MappingObjectStr> readAnnotatedMappingsFromTSV(String fullPath) {
		
		Set<MappingObjectStr> mapSet = new HashSet<MappingObjectStr>();
		try {
			
			File tsv = new File(fullPath);
			ReadFile reader = new ReadFile(tsv);
				
			int countTrue = 0;
			int countFalse = 0;
			for (String line = reader.readLine(); line != null; line = reader.readLine()) {
				String[] lineElements;
				//System.out.println(line);
				if (line.startsWith("#") || !line.startsWith("http")){ //skip comments and header row
					continue;
				}
				
				if (line.indexOf("\t")<0){
					continue;
				}
				
				lineElements=line.split("\t");
					
				//System.out.println(lineElements[0] + "  " + lineElements[1]  + "  " + lineElements[5]);
				
				if (Boolean.parseBoolean(lineElements[5].toLowerCase())) {
					//System.out.println("Found some truth!");
					//TODO it might not be equivalence, I need to check elements[2]
					//TODO it might not be a CLS equivalence, I need to check and remove 0
					MappingObjectStr formattedMap = new MappingObjectStr(lineElements[0],
							lineElements[1], Double.valueOf(lineElements[3]), MappingObjectStr.EQ,0);
				
					mapSet.add(formattedMap);
						
					countTrue++;
				}
				else {
					countFalse++;
				}
			}
			
			reader.closeBuffer();
			System.out.println("Num mapping in oracle: " + countTrue);
			System.out.println("Num mapping NOT in oracle: " + countFalse);
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return mapSet;
		
	}
	
	
	private static Set<String> readEntitiesToIgnore(String file) throws FileNotFoundException {
		
		ReadFile reader = new ReadFile(file);
		
		String line;
				
		line=reader.readLine();
		
		Set<String> entities_to_ignore = new HashSet<String>();

		
		
		while (line!=null) {
			
			if (line.length()>1) 
				entities_to_ignore.add(line);
			
			line=reader.readLine();
			
		}
		
		
		reader.closeBuffer();
		
		return entities_to_ignore;
	}
	
	
	public static void testStandatMeasuresBioML2025() {
		
		String path_system = "C:/Users/Ernes/Documents/logmap-llm-bio-tracks/bio-ml-2025/logmap-llm-gemini2.5flash-oaei2025/";
		String path_ref = "C:/Users/Ernes/Documents/OAEI-Bio-Tracks/bio-ml-2025/ncit-doid/";
		
		String ref = path_ref + "ncit-doid-ref.rdf";
		String system = path_system + "bioml-ncit-doid-logmap-llm_mappings.rdf"; 
		
		MappingsReaderManager mapping_reader_system = new MappingsReaderManager(system, MappingsReaderManager.OAEIFormat);
		MappingsReaderManager mapping_reader_ref = new MappingsReaderManager(ref, MappingsReaderManager.OAEIFormat);
				
		
		Set<String> entities_to_ignore = new HashSet<String>();
		try {
			
			entities_to_ignore.addAll(readEntitiesToIgnore(path_ref + "ncit.owl_entities_not_in_alignment.txt"));
			entities_to_ignore.addAll(readEntitiesToIgnore(path_ref + "doid.owl_entities_not_in_alignment.txt"));
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		StandardMeasures.computeStandardMeasures(mapping_reader_system.getMappingObjects(), mapping_reader_ref.getMappingObjects(), entities_to_ignore);
		
		System.out.println(StandardMeasures.getPrecision() + " - " + StandardMeasures.getRecall() + " - " + StandardMeasures.getFscore());
		
		
				
	}
	
	
	
	public static void main(String[] args) {
		
		testStandatMeasuresBioML2025();
		
		if (true)
			return;
		
		
		
		//For Three tasks
		String path_mappings; 
		String path_oracle_annotations; 
		String path_output;
		String path_track;
		String iri_onto1;
		String iri_onto2;
		String iri_onto1_path;
		String iri_onto2_path;
		
		
		
		
		int test; 
		//test=1;
		//test=2;
		test=3;
		
		
		
		int NCITDOID = 1;
		int SNOMEDFMA = 2;
		int SNOMEDNCIT = 3;
		
		int NCITDOID2025 = 4;
		int OMIMORDO2025 = 5;
		int SNOMEDFMABody2025 = 6;
		int SNOMEDNCITNeoplas2025 = 7;
		int SNOMEDNCITPharm2025 = 8;
		
		int FMANCIlargebio = 9;
		int FMASNOMEDlargebio = 10;
		int SNOMEDNCIlargebio = 11;
		
		
		String track;
		
		if (test<4)
			track = "bio-ml-2026/";
		else if (test>8) 
			track = "largebio-2021/";		
		else
			track = "bio-ml-2025/";
		
		String llm_type = "logmap-llm-mutual-subsumption/";
		
		String base_path="C:/Users/Ernes/Documents/";		
		String base_mappings_path= base_path + "logmap-llm-bio-tracks/" + track + llm_type;
		
		String oracle_path = base_path + "bioportal-composed-llm-annotation/" + track;
		
		
		//All
		path_track = base_path + "OAEI-Bio-Tracks/" + track;
		
				
				
				
		
		if (test==NCITDOID) {
		
			//NCIT-DOID
			path_mappings = base_mappings_path + "logmap-llm-ms-ncit-doid.rdf";
			path_oracle_annotations = oracle_path + "bioml2026-ncit-doid-all-composed-minus-logmapllm-mutualsub.annotated.tsv";
					
			iri_onto1="http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl";
			iri_onto2="http://purl.obolibrary.org/obo/doid.owl";
			iri_onto1_path="file:/" + path_track + "NCIT.owl";
			iri_onto2_path="file:/" + path_track + "DOID.owl";
		}
		else if (test==SNOMEDFMA) {
				
			//SNOMED_FMA
			path_mappings = base_mappings_path + "logmap-llm-ms-snomed-fma.rdf";
			path_oracle_annotations = oracle_path + "bioml2026-snomed-fma-all-composed-minus-logmapllm-mutualsub.annotated.tsv";
			
			iri_onto1="http://snomed.info/sct/900000000000207008";
			iri_onto2="http://purl.org/sig/ont/fma.owl";
			iri_onto1_path="file:/" + path_track + "SNOMED.owl";
			iri_onto2_path="file:/" + path_track + "FMA.owl";
		}
		
		else if (test==SNOMEDNCIT) {
		
			//SNOMED_NCIT
			path_mappings = base_mappings_path + "logmap-llm-ms-snomed-ncit.rdf";
			path_oracle_annotations = oracle_path + "bioml2026-snomed-ncit-all-composed-minus-logmapllm-mutualsub.annotated.tsv";
			
			iri_onto1="http://snomed.info/sct/900000000000207008";
			iri_onto2="http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl";
			iri_onto1_path="file:/" + path_track + "SNOMED.owl";
			iri_onto2_path="file:/" + path_track + "NCIT.owl";
		}
		else { //if (test==NCITDOID2025){
			
			//NCIT-DOID 2025
			path_mappings = base_mappings_path + "logmap-llm-ms-ncit-doid.rdf";
			path_oracle_annotations = oracle_path + "bioml2026-ncit-doid-all-composed-minus-logmapllm-mutualsub.annotated.tsv";
					
			iri_onto1="http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl";
			iri_onto2="http://purl.obolibrary.org/obo/doid.owl";
			iri_onto1_path="file:/" + path_track + "NCIT.owl";
			iri_onto2_path="file:/" + path_track + "DOID.owl";
			
			
			
		}
		
		
		
		
		//For all
		path_output = path_mappings.replace("logmap-llm-ms", "logmapbio-llm-ms");
		path_output = path_output.replace("logmap-llm-bio-tracks", "logmapbio-llm");
		path_output = path_output.replace("logmap-llm-mutual-subsumption/", "");
		
		System.out.println("Output path: " + path_output);
		
		try {
			new MergeAxiomsFromOracle(
					path_mappings,
					path_oracle_annotations,
					path_output,
					iri_onto1,
					iri_onto1_path,
					iri_onto2,
					iri_onto2_path,
					"",
					"",
					"");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	

}

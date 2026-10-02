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

public class MergeAxiomsFromOracle {
	
	public MergeAxiomsFromOracle(
			String path_mappings, 
			String path_oracle_annotations, 
			String path_output,
			String iri_onto1,
			String iri_onto1_path,
			String iri_onto2,
			String iri_onto2_path) throws Exception {
		
		
		MappingsReaderManager mapping_reader = new MappingsReaderManager(path_mappings, MappingsReaderManager.OAEIFormat);
		
		OutPutFilesManager mapping_manager = new OutPutFilesManager();
		
		
		boolean two_steps_repair;
		//two_steps_repair = true;
		two_steps_repair = false;
		
		
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
		
		
		
			
		
		
		//Repair all together just in case
		LogMap2_RepairFacility logmap2repair = new LogMap2_RepairFacility(
				loader1.getOWLOntology(), 
				loader2.getOWLOntology(), 
				logmapllm_mappings,
				false,
				false, //always optimal?
				false, //satisfiability_check,
				path_output);
		
		
		
		System.out.println("Repaired mappings (ALL): " + logmap2repair.getCleanMappings().size());
		
		
		mapping_manager.createOutFiles(path_output, OutPutFilesManager.AllFlatFormats, iri_onto1, iri_onto2);
		
		mapping_manager.addMappings(logmap2repair.getCleanMappings());
		
		
		mapping_manager.closeAndSaveFiles();
		
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
	
	
	
	public static void main(String[] args) {
		
		//For Three tasks
		String path_mappings; 
		String path_oracle_annotations; 
		String path_output;
		String path_ontos;
		String iri_onto1;
		String iri_onto2;
		String iri_onto1_path;
		String iri_onto2_path;
		
		
		String base_path="C:/Users/Ernes/Documents/";
		
		String base_mappings_path= base_path + "logmap-llm-bio-tracks/bio-ml-2026/logmap-llm-mutual-subsumption/";
		
		String oracle_path = base_path + "bioml-2026-composed-mapppings-llm-annotation/";
		
		
		//All
		path_ontos = base_path + "bio-ml-2026-ontos/";
		
		
		int test; 
		//test=1;
		//test=2;
		test=3;
		
		
		
		int NCITDOID = 1;
		int SNOMEDFMA = 2;
		int SNOMEDNCIT = 3;
		
		
		if (test==NCITDOID) {
		
			//NCIT-DOID
			path_mappings = base_mappings_path + "logmap-llm-ms-ncit-doid.rdf";
			path_oracle_annotations = oracle_path + "bioml2026-ncit-doid-all-composed-minus-logmapllm-mutualsub.annotated.tsv";
					
			iri_onto1="http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl";
			iri_onto2="http://purl.obolibrary.org/obo/doid.owl";
			iri_onto1_path="file:/" + path_ontos + "NCIT.owl";
			iri_onto2_path="file:/" + path_ontos + "DOID.owl";
		}
		else if (test==SNOMEDFMA) {
				
			//SNOMED_FMA
			path_mappings = base_mappings_path + "logmap-llm-ms-snomed-fma.rdf";
			path_oracle_annotations = oracle_path + "bioml2026-snomed-fma-all-composed-minus-logmapllm-mutualsub.annotated.tsv";
			
			iri_onto1="http://snomed.info/sct/900000000000207008";
			iri_onto2="http://purl.org/sig/ont/fma.owl";
			iri_onto1_path="file:/" + path_ontos + "SNOMED.owl";
			iri_onto2_path="file:/" + path_ontos + "FMA.owl";
		}
		
		else { //if (test==SNOMEDNCIT) {
		
			//SNOMED_NCIT
			path_mappings = base_mappings_path + "logmap-llm-ms-snomed-ncit.rdf";
			path_oracle_annotations = oracle_path + "bioml2026-snomed-ncit-all-composed-minus-logmapllm-mutualsub.annotated.tsv";
			
			iri_onto1="http://snomed.info/sct/900000000000207008";
			iri_onto2="http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl";
			iri_onto1_path="file:/" + path_ontos + "SNOMED.owl";
			iri_onto2_path="file:/" + path_ontos + "NCIT.owl";
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
					iri_onto2_path);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	

}

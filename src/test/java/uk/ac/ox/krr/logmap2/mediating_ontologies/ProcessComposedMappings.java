/*
 * Where we take the mappings obtained via each mediating ontology and we subtract those that logmap had already found when aligning the source and target. 
 */




package uk.ac.ox.krr.logmap2.mediating_ontologies;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


import uk.ac.ox.krr.logmap2.mappings.objects.MappingObjectStr;
import uk.ac.ox.krr.logmap2.oaei.reader.FlatAlignmentReader;
import uk.ac.ox.krr.logmap2.oaei.reader.MappingsReaderManager;

public class ProcessComposedMappings{
	public List<String> availableStatistics = new ArrayList<String>();
	
	public ProcessComposedMappings(){}
	public ProcessComposedMappings(Boolean saveToCSV) {
		if(saveToCSV == true) {
			this.availableStatistics.add("ontology_label");
			this.availableStatistics.add("all_composed_mappings_count");
			this.availableStatistics.add("unique_mappings_count_minus_logmap");
			this.availableStatistics.add("unique_mappings_count_minus_logmap_llm");

		}
		else {
			System.out.println("You chose not to save stats to file.");
		}
		
	}
	
	
	//TODO check automatically if the mapping order source-target needs to be swapped to target-source for comparinson with reference
	/**
	 * If source and target are not in the same order during comparison of two sets of mappings,
	 * this function allows you to invert the mapping order. For example, if one set is obtained with onto1-onto2, but
	 * the other set is onto2-onto1, you use this function before comparing the alignment results.
	 * @param AMappings
	 * @return
	 */
	public Set<MappingObjectStr> swapMappingOrder(Set<MappingObjectStr> AMappings){
		Set<MappingObjectStr> swappedMappings = new HashSet<MappingObjectStr>();
		for (MappingObjectStr map: AMappings) {
			int mappingDirection = map.getMappingDirection();
			int newMappingDirection = mappingDirection;
			if(mappingDirection==MappingObjectStr.SUB) {
			//TODO swap to SUP
				newMappingDirection = MappingObjectStr.SUP;
			}else if (mappingDirection==MappingObjectStr.SUP) {
				//TODO swap to SUB
				newMappingDirection = MappingObjectStr.SUB;

			}
			//If EQ or Flagged no swap necessary
		
			//In any case we swap source and target
			MappingObjectStr swappedMap = new MappingObjectStr(map.getIRIStrEnt2(),
				map.getIRIStrEnt1(), map.getConfidence(),newMappingDirection,map.getTypeOfMapping());
			swappedMappings.add(swappedMap);
		}
		
		return swappedMappings;
	}
	/**
	 * Performs set subtraction A\B between sets of mappings. Works for any two sets of mappings A and B. 
	 * @param AMappings
	 * @param BMappings
	 * @return mapSubtracted;
	 */
	public Set<MappingObjectStr> mappingSetSubtraction(Set<MappingObjectStr> AMappings, Set<MappingObjectStr> BMappings){
		
		//
		Set<MappingObjectStr> mapSubtracted = new HashSet<MappingObjectStr>(AMappings);
		mapSubtracted.removeAll(BMappings);
		//
		System.out.println("Performing A - B, A has " + AMappings.size() + " items, B has " + BMappings.size());
		System.out.println(" Subtraction yielded " + mapSubtracted.size() + " items.");		
		return mapSubtracted;
	}
	
	public HashMap<String, FlatAlignmentReader> makeMappingReadersFromDirectory(String mappingsDirectory){
		
		HashMap<String,FlatAlignmentReader> readersArray = new HashMap<String, FlatAlignmentReader>();
		File listPath = new File(mappingsDirectory);
		File[] listOnto = listPath.listFiles();
		String moComposedMappingsPath = null;
		
		for( File f: listOnto) {
			boolean is_txt = f.getName().endsWith(".txt");
			if(is_txt==false) {
				System.out.println("Not a txt " + f);
				continue;
			}else {
				
				System.out.println("Filtering new mappings for " + f.getName());
				moComposedMappingsPath = mappingsDirectory + f.getName();
				FlatAlignmentReader newreader = null;
				String ontoLabel = f.getName().substring(0, f.getName().lastIndexOf('.'));
				try {
					
					newreader = new FlatAlignmentReader(moComposedMappingsPath);
				}
				catch(Exception e){
					System.out.println("Failed to read composed mapping for " + f);
				}
				
				readersArray.put(ontoLabel, newreader);
				

			}
			
		}
		
		
		return readersArray;
	}
	
	public HashMap<String,String> createDataRow(){
		HashMap<String,String> dataRow = new HashMap<String,String>();
	
	for (String item: this.availableStatistics) {
		dataRow.put(item, null);
	
	}
	return dataRow;
	}
	
	public void addHeaderToCSV(Writer writer, String eol) {
		Integer noColumns = this.availableStatistics.size();
		try {
			  for (Integer i= 0; i < noColumns-1; i++) {
				  String item = this.availableStatistics.get(i);
			    writer.append(item)
			          .append(',');
			  }
			  String lastItem = this.availableStatistics.get(noColumns -1);
			  writer.append(lastItem);
			  writer.append(eol);
			} catch (IOException ex) {
			  ex.printStackTrace(System.err);
			}
	}
	
	public void addRowToCSV(Writer writer, String eol, HashMap<String, String> dataRow) throws Exception {
		// Check that the items in the dataRow match the class list of available Stats

		Integer noColumns = this.availableStatistics.size();
		if (dataRow.size() != noColumns) {
			throw new Exception("dataRow does not match csv header length");
		}
		try {
			for (Integer i= 0; i < noColumns-1; i++)  {
				String item = this.availableStatistics.get(i); // hashmap key
				writer.append(dataRow.get(item)).append(','); //hashmap value
				// .append(eol);
			}
			String lastItem = this.availableStatistics.get(noColumns -1);
			writer.append(dataRow.get(lastItem)); //hashmap value
			writer.append(eol);
		} catch (IOException ex) {
			ex.printStackTrace(System.err);
		}
	}
	

	public static void main(String[] args) {

		Boolean saveToCSV = true;

		ProcessComposedMappings moProcess = new ProcessComposedMappings(saveToCSV); // saveToCSV = true;
		System.out.println(moProcess.availableStatistics);
		MediatingOntologiesUtils moUtils = new MediatingOntologiesUtils();
		moUtils.getParentFolder(args);
		moUtils.readConfigJSON();
		moUtils.createSubDirectoriesFromParent(); //if the subdir already exists it doesn't create it nor overwrite it
		
		String onto1_iri = "file:" + moUtils.sourceOntoPath;
		String onto2_iri = "file:" + moUtils.targetOntoPath;

		String moMappingsPath = moUtils.composedMappingsPath;
		String newMappingsPath = moUtils.newUniqueMappingsPath;

		String llmDefaultMapsFile = moUtils.logmapLLMDefaultPath;
		String llmMutualSubMapsFile = moUtils.logmapLLMMSubPath;
		String storeDiffLlm = moUtils.parentPath + "composed-mappings-for-llm-annotation/";
		File dir = new File(storeDiffLlm);
		if (!dir.exists()) dir.mkdir();

		
		try {
			// readFromJSON = false, it gets file from default folder store-source-target 
			moUtils.getSource2TargetLogmapMappings(false);
			//System.out.println(moUtils.source2targetLogmapMappingsPath);
			Set<MappingObjectStr> mapSource2Target = moUtils.readMappingsFromFile(moUtils.source2targetLogmapMappingsPath); 


			//TODO what is the difference between the two readers?
			//MappingsReaderManager llmDefaultmappingReader = new MappingsReaderManager(llmDefaultMapsFile, "RDF");
			//MappingsReaderManager llmMutualSubmappingReader = new MappingsReaderManager(llmMutualSubMapsFile, "RDF");

			System.out.println("Original set of mappings contains " + mapSource2Target.size() + " mappings");
			
			//Get mappings from LogmapLLM Default
			Set<MappingObjectStr>  llmDefaultMappings = Collections.emptySet();
			try {
			llmDefaultMappings = moUtils.readMappingsFromFile(llmDefaultMapsFile);
			System.out.println("Logmap LLM (default) set of mappings contains " + llmDefaultMappings.size() + " mappings");

			}catch(Exception e) {
				e.printStackTrace();
			}
			
			Set<MappingObjectStr> llmMutualSubMappings = Collections.emptySet();
			try {
			llmMutualSubMappings = moUtils.readMappingsFromFile(llmMutualSubMapsFile);
			System.out.println("Logmap LLM (mutual subsumption) set of mappings contains " + llmMutualSubMappings.size() + " mappings");

			}catch(Exception e) {
				e.printStackTrace();
			}


			
			// Prepare the single file with all composed mappings from all mediating ontologies
			String allComposedName = storeDiffLlm + "all-composed";
			Set<MappingObjectStr> allComposedCollect = new HashSet<MappingObjectStr>();
			
			// Prepare the single file with all new mappings from all mediating ontologies minus all the mappings from logmap $MC_{all}^{Lmap}$ 
			String allComposedMinusLogMapName = storeDiffLlm + "all-composed-minus-source2target";
			Set<MappingObjectStr> allComposedMinusLogMapCollect = new HashSet<MappingObjectStr>();
			
			
			// Save stats to file
			//String timestamp = Instant.now().toString();
			//File file = new File(moUtils.parentPath + "Statistics-" + timestamp +".csv");
			File file = new File(moUtils.parentPath + "preAnnotationStatistics.csv");
			// Create a File and append if it already exists.
			//TODO do not append if file already exists, instead create a new one
			Writer writer = new FileWriter(file, true);
			//Reader reader = new FileReader(file);
			String eol = System.getProperty("line.separator");
			//System.out.println("EOL: " + eol + "is here");
			moProcess.addHeaderToCSV(writer, eol);

			HashMap<String,FlatAlignmentReader> readersArray = moProcess.makeMappingReadersFromDirectory(moMappingsPath);
			for( String ontoLabel: readersArray.keySet()) {
					HashMap<String,String> statsRow = moProcess.createDataRow();

					Set<MappingObjectStr> moComposedMappings = readersArray.get(ontoLabel).getMappingObjects();
					System.out.println("Mediating ontology gave a total of " + moComposedMappings.size() + " mappings");
					
					allComposedCollect.addAll(moComposedMappings);
					Set<MappingObjectStr> newMappings = moProcess.mappingSetSubtraction(moComposedMappings, mapSource2Target);
					System.out.println("Of which new mappings are " + newMappings.size());
					String newMapPath = newMappingsPath + ontoLabel;
					moUtils.saveOntologyMappings(newMappings, newMapPath, onto1_iri, onto2_iri);
					
					allComposedMinusLogMapCollect.addAll(newMappings);
					statsRow.replace("ontology_label", ontoLabel);
					statsRow.replace("all_composed_mappings_count", String.valueOf(moComposedMappings.size()));
					statsRow.replace("unique_mappings_count_minus_logmap", String.valueOf(newMappings.size()));
					moProcess.addRowToCSV(writer,eol,statsRow);
				}

			writer.close();
			//reader.close();
			//Create single file with all composed mappings (from all mediating ontologies)
			moUtils.saveOntologyMappings(allComposedCollect, allComposedName, onto1_iri, onto2_iri);
			
			// Create single file with all new mappings
			moUtils.saveOntologyMappings(allComposedMinusLogMapCollect, allComposedMinusLogMapName, onto1_iri, onto2_iri);

			//TODO LLM subtraction - done all at once
			//Set<MappingObjectStr> reversedAllComposedCollect = moProcess.swapMappingOrder(allComposedCollect);
			//Set<MappingObjectStr> ComposedMappingsMinusLlmDefault = moProcess.mappingSetSubtraction(reversedAllComposedCollect,llmDefaultMappings);
			Set<MappingObjectStr> ComposedMappingsMinusLlmDefault = moProcess.mappingSetSubtraction(allComposedCollect,llmDefaultMappings);
			String allComposedMinusLLMDefaultName = storeDiffLlm + "all-composed-minus-llm-default";
			moUtils.saveOntologyMappings(ComposedMappingsMinusLlmDefault, allComposedMinusLLMDefaultName, onto1_iri, onto2_iri);
			

			//Set<MappingObjectStr> ComposedMappingsMinusLlmMutualSub = moProcess.mappingSetSubtraction(reversedAllComposedCollect,llmMutualSubMappings);
			Set<MappingObjectStr> ComposedMappingsMinusLlmMutualSub = moProcess.mappingSetSubtraction(allComposedCollect,llmMutualSubMappings);
			String allComposedMinusLLMMutualSubName = storeDiffLlm + "all-composed-minus-llm-mutualsub";
			moUtils.saveOntologyMappings(ComposedMappingsMinusLlmMutualSub, allComposedMinusLLMMutualSubName, onto1_iri, onto2_iri);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}


				


	
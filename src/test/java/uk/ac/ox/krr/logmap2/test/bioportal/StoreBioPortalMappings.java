package uk.ac.ox.krr.logmap2.test.bioportal;

import java.io.FileNotFoundException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import uk.ac.ox.krr.logmap2.bioportal.RESTBioPortalAccess;
import uk.ac.ox.krr.logmap2.io.OutPutFilesManager;
import uk.ac.ox.krr.logmap2.io.WriteFile;
import uk.ac.ox.krr.logmap2.bioportal.BioPortalMapping;

/**
 * 
 * @author Ernesto
 *
 */
public class StoreBioPortalMappings {
		
		
	
	public StoreBioPortalMappings() throws Exception{
	
		RESTBioPortalAccess bioportal = new RESTBioPortalAccess();

		Map<String, Set<String>> ontologyPairs = new HashMap<String, Set<String>>();
		
		boolean localExperiment=false;
		
		if (localExperiment){
			String local_file = setUpLocalExperiment(ontologyPairs);
			
			for (String onto1 : ontologyPairs.keySet()){
				getMappingsFromLocalFile(local_file, onto1, ontologyPairs.get(onto1));				
			}
		}
		else if (bioportal.isActive()) {
			
			setUpExperiment(ontologyPairs);
			
			for (String onto1 : ontologyPairs.keySet()){
				getMappingsFromBioPortal(bioportal, onto1, ontologyPairs.get(onto1));				
			}
			
			
		}
		
	}
	
	
	private String setUpLocalExperiment(Map<String, Set<String>> ontologyPairs) {
		
		/*ontologyPairs.put("FMA", new HashSet<String>());
		ontologyPairs.get("FMA").add("EP");
		ontologyPairs.get("FMA").add("EHDA");
		ontologyPairs.get("FMA").add("MA");
		ontologyPairs.get("FMA").add("UBERON");
		ontologyPairs.get("FMA").add("ZFA");
		ontologyPairs.get("FMA").add("SDO");
		ontologyPairs.get("FMA").add("NCIT");
		
		return "/home/ernesto/Documents/BioPortalMappings/FMA_all_mappings2.txt";
		*/
		
		ontologyPairs.put("EP", new HashSet<String>());
		ontologyPairs.get("EP").add("FMA");
		
		//"/home/ejimenez-ruiz/Documents/BioPortal/Mappings/";
		return "/home/ernesto/Documents/BioPortalMappings/EP/EP_all_mappings.txt";
	}
	
	private void setUpExperiment(Map<String, Set<String>> ontologyPairs) {
		
		/*ontologyPairs.put("BP", new HashSet<String>());
		ontologyPairs.get("BP").add("SNOMEDCT");
		ontologyPairs.get("BP").add("SYN");
		ontologyPairs.get("BP").add("BDO");
		ontologyPairs.get("BP").add("SBO");
		*/
		
		
		/*ontologyPairs.put("FMA", new HashSet<String>());
		ontologyPairs.get("FMA").add("EP");
		ontologyPairs.get("FMA").add("EHDA");
		ontologyPairs.get("FMA").add("MA");
		ontologyPairs.get("FMA").add("UBERON");
		ontologyPairs.get("FMA").add("ZFA");
		ontologyPairs.get("FMA").add("SDO");
		ontologyPairs.get("FMA").add("NCIT");*/
		
		
		//ontologyPairs.put("EP", new HashSet<String>());
		//ontologyPairs.get("EP").add("FMA");
		//ontologyPairs.put("EHDA", new HashSet<String>());
		//ontologyPairs.get("EHDA").add("FMA");
		//ontologyPairs.put("MA", new HashSet<String>());
		//ontologyPairs.get("MA").add("FMA");
		//ontologyPairs.put("ZFA", new HashSet<String>());
		//ontologyPairs.get("ZFA").add("FMA");
		//ontologyPairs.put("SDO", new HashSet<String>());
		//ontologyPairs.get("SDO").add("FMA");
		//ontologyPairs.put("UBERON", new HashSet<String>());
		//ontologyPairs.get("UBERON").add("FMA");
		
		
		
		ontologyPairs.put("CMR-QA", new HashSet<String>());
		ontologyPairs.get("CMR-QA").add("SNOMEDCT");
		ontologyPairs.get("CMR-QA").add("UBERON");
		ontologyPairs.get("CMR-QA").add("FMA");
		ontologyPairs.get("CMR-QA").add("SNOMEDCT");
		ontologyPairs.get("CMR-QA").add("RADLEX");
		ontologyPairs.get("CMR-QA").add("RCD");
		ontologyPairs.get("CMR-QA").add("IOBC");
		ontologyPairs.get("CMR-QA").add("ONL-MR-DA");
		ontologyPairs.get("CMR-QA").add("MEDLINEPLUS");
		ontologyPairs.get("CMR-QA").add("BIM");
		ontologyPairs.get("CMR-QA").add("IAO");
		ontologyPairs.get("CMR-QA").add("FBbi");
		ontologyPairs.get("CMR-QA").add("EDAM-BIOIMAGING");
		ontologyPairs.get("CMR-QA").add("MEDDRA");
		ontologyPairs.get("CMR-QA").add("NCIT");
		ontologyPairs.get("CMR-QA").add("MESH");
		ontologyPairs.get("CMR-QA").add("LOINC");
		
		
		
		
		/*ontologyPairs.put("NCIT", new HashSet<String>());
		ontologyPairs.get("NCIT").add("BDO");
		ontologyPairs.get("NCIT").add("EP");
		ontologyPairs.get("NCIT").add("CCONT");
		ontologyPairs.get("NCIT").add("EFO");
		ontologyPairs.get("NCIT").add("FMA");
		ontologyPairs.get("NCIT").add("OMIM");
		*/
		
		/*ontologyPairs.put("BDO", new HashSet<String>());
		ontologyPairs.get("BDO").add("NCIT");
		ontologyPairs.put("EP", new HashSet<String>());
		ontologyPairs.get("EP").add("NCIT");
		ontologyPairs.put("CCONT", new HashSet<String>());
		ontologyPairs.get("CCONT").add("NCIT");
		ontologyPairs.put("EFO", new HashSet<String>());
		ontologyPairs.get("EFO").add("NCIT");
		ontologyPairs.put("OMIM", new HashSet<String>());
		ontologyPairs.get("OMIM").add("NCIT");
		*/
		
		
		/*ontologyPairs.put("ZFA", new HashSet<String>());
		ontologyPairs.get("ZFA").add("CCONT");
		ontologyPairs.get("ZFA").add("EFO");
		ontologyPairs.get("ZFA").add("EHDA");
		ontologyPairs.get("ZFA").add("MA");
		ontologyPairs.get("ZFA").add("TAO");
		ontologyPairs.get("ZFA").add("UBERON");
		
		
		ontologyPairs.put("SNPO", new HashSet<String>());
		ontologyPairs.get("SNPO").add("SO");
		
		ontologyPairs.put("SDO", new HashSet<String>());
		ontologyPairs.get("SDO").add("EP");
		*/
		
		
	}


	private void getMappingsFromBioPortal(
			RESTBioPortalAccess bioportal, String source_onto, Set<String> target_ontos) throws Exception{
		
		long end, init;

		System.out.println("\nExtracting BioPortal Mappings between " + source_onto + " and " + target_ontos);
		
		init = Calendar.getInstance().getTimeInMillis();
		
		WriteFile writer = new WriteFile("/home/ejimenez-ruiz/Documents/BioPortal/Mappings/" + source_onto + "_all_mappings.txt", false);
		
		Set<BioPortalMapping> mapping_set = new HashSet<BioPortalMapping>();
		
		int starting_page = 1;
		int pagesize = 500;
		
		bioportal.getMappingsForGivenOntologies(source_onto, target_ontos, mapping_set, starting_page, pagesize, writer);
		
		saveBioPortalMappings(source_onto, target_ontos, mapping_set);
		
		writer.closeBuffer();
				
		mapping_set.clear();
		
		end = Calendar.getInstance().getTimeInMillis();
		
		System.out.println("\tTIME: " + (double) ((double) end - (double) init) / 1000.0);
		
		
	}
	
	
	private void getMappingsFromLocalFile(
			String file, String source_onto, Set<String> target_ontos) throws Exception {
		
		System.out.println("\nExtracting Local Mappings between " + source_onto + " and " + target_ontos + " from " + file);
		
		
		saveBioPortalMappings(
				source_onto, 
				target_ontos, ReadBioPortalMappingsFromFile.getBioPortalMappings(file));
		
		
	}
	
	
	private void saveBioPortalMappings(String source_onto, Set<String> target_ontos, Set<BioPortalMapping> mapping_set) throws Exception{
		
		OutPutFilesManager outPutFilesManager = new OutPutFilesManager();
		
		
		for (String target_onto : target_ontos){
		
			outPutFilesManager.createOutFiles(
						"/home/ejimenez-ruiz/Documents/BioPortal/Mappings/" + source_onto + "_" + target_onto,
						OutPutFilesManager.AllFormats,
						"http://data.bioontology.org/ontologies/"+source_onto,
						"http://data.bioontology.org/ontologies/"+target_onto);
						
			for (BioPortalMapping mapping : mapping_set){
				
				if (mapping.getTargetOntology().equals(target_onto)){
				
					outPutFilesManager.addClassMapping2Files(
							mapping.getSourceEntity(),
							mapping.getTargetEntity(),
							mapping.getMappingDirection(),
							mapping.getConfidence());
				}
			}
			
			outPutFilesManager.closeAndSaveFiles();
		}
		
		
	}
		

	
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		try {
			new StoreBioPortalMappings();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

		

}

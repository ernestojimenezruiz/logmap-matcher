package uk.ac.ox.krr.logmap2.test.bioportal;

import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Set;

import uk.ac.ox.krr.logmap2.io.ReadFile;
import uk.ac.ox.krr.logmap2.bioportal.BioPortalMapping;

public class ReadBioPortalMappingsFromFile {
	
	//MAPPING SOURCES
	private static final String XREF = "xref";  //related_match (filter)
	private static final String LOOM = "loom";  //close_match
	private static final String CUI = "cui";	//exact_match
	private static final String NULL = "null";  //error?	(filter)
	private static final String SAMEURIS = "same_uris";	//(filter)
	private static final String SAMEURI = "same_uri";	//(filter)
	private static final String MRMAP = "mrmap";	//close_match

	
	
	
	public static Set<BioPortalMapping> getBioPortalMappings(String file) throws FileNotFoundException{
		
		ReadFile reader = new ReadFile(file);
		//System.out.println(file);
		
		String[] elements;
		String current_line;
		
		Set<BioPortalMapping> mapping_objects = new HashSet<BioPortalMapping>();
		
		
		//http://sig.uw.edu/fma#Neural_network_of_compartment_of_female_abdomen|http://purl.obolibrary.org/obo/FMA_260494|FMA|FMA-SUBSET|loom
		//uri1|uri2|onto1|onto2|source1;source2
		
		
		while ((current_line = reader.readLine())!=null){
			
			
			if (current_line.indexOf("|")<0){
				continue;
			}
			
			elements=current_line.split("\\|");
			
			Set<String> sources = string2Set(elements[4].toLowerCase());
			
			//System.out.println(current_line + "  " +  sources);
			
			if (filterMappingBySource(sources))
				continue;
			
			mapping_objects.add(
					new BioPortalMapping(
							elements[0], elements[1], elements[2], elements[3], sources));
			
		}
		
		return mapping_objects;
		
	}
	
	private static Set<String> string2Set(String sources_str){
		
		Set<String> sources = new HashSet<String>();
		
		if (sources_str.indexOf(";")<0)
			sources.add(sources_str);
		else{
			String[] elements = sources_str.split(";");
			for (String s : elements){
				sources.add(s);
			}
		}
		
		return sources;
		
	}
	
	
	/**
	 * We filter if null, sameuri or xref
	 * @param sources
	 * @return
	 */
	private static boolean filterMappingBySource(Set<String> sources){
		
		//if (sources.isEmpty())
		//	return true;
		
		//Ignore mappings to itself!
		//Same URI appears in different ontologies, but there is no need to add a mapping since the URI is the same		
	    if (sources.contains(SAMEURIS) || sources.contains(SAMEURI))
			return true;
		
		if (sources.contains(LOOM) || sources.contains(CUI) || sources.contains(MRMAP))
			return false;
		
		
		return true; ////only XREF, NULL, NOSOURCE
		
	}
	
	
	
 
	
	
}


/*package uk.ac.ox.krr.logmap2.test.oaei;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;


import uk.ac.ox.krr.logmap2.LogMap2_MappingRanking;
import uk.ac.ox.krr.logmap2.OntologyLoader;
import uk.ac.ox.krr.logmap2.mappings.objects.MappingObjectStr;

public class TestNewOAEILargeBio2022 {
	
	
	//Read Json file?
	
	//Read Onto
	
	
	public TestNewOAEILargeBio2022() throws Exception {
	
		
		String URIPath = "file:/home/ernesto/Documents/OAEI2022/subs_data/UMLS/snomed2ncit.pharm/";
		
		
		String onto1_uri = URIPath + "snomed.pharm.owl";
		String onto2_uri = URIPath + "ncit.pharm.owl";
		
		
		OntologyLoader onto1_loader = new OntologyLoader(onto1_uri);
		OntologyLoader onto2_loader = new OntologyLoader(onto2_uri);
	
		
		
		LogMap2_MappingRanking logmap_ranker = new LogMap2_MappingRanking(onto1_loader.getOWLOntology(), onto2_loader.getOWLOntology());
		
		
		
		Set<MappingObjectStr> input_mappings = new HashSet<MappingObjectStr>();
		
		
		//Nafarelin acetate		
		input_mappings.add(new MappingObjectStr("http://snomed.info/id/109047000", "http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl#C1910")); //Gonadotropin-releasing Hormone Analog
		input_mappings.add(new MappingObjectStr("http://snomed.info/id/109047000", "http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl#C96432")); //Estrogen Receptor Agonist GTx-758
		input_mappings.add(new MappingObjectStr("http://snomed.info/id/109047000", "http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl#C863")); //Testosterone Propionate
		input_mappings.add(new MappingObjectStr("http://snomed.info/id/109047000", "http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl#C167035")); //Nisterime Acetate
		
				
		
		TreeSet<MappingObjectStr> ranked_mappings = logmap_ranker.rankedMappings(input_mappings);
		
		
	
		
		Iterator<MappingObjectStr> it = ranked_mappings.descendingIterator();		
		MappingObjectStr map;
		while (it.hasNext()){			
			map = it.next();
			System.out.println(map + " " + map.getConfidence());
		}
		
		
		
		
		
	
	}
	

	
	public static void main(String[] args){
		
		try {
			new TestNewOAEILargeBio2022();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
}
*/
package uk.ac.ox.krr.logmap2.background;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import uk.ac.ox.krr.logmap2.mappings.objects.MappingObjectStr;
import uk.ac.ox.krr.logmap2.oaei.reader.MappingsReaderManager;

/**
 * Instances may not a rich information in terms of class types, or class types may not be very informative.
 * However they may contain metainformation as annotation or as role assertions about categories 
 * (e.g. dbpedia categories, doremus music categories, etc.).
 * 
 * This class loads backgound knowledge about mappings between categories which are available 
 * outside the ontologies to be matched
 * 
 * @author ernesto
 *
 */
public class CategoryMappingsLoader {
	
	
	private Map<String, Set<String>> category_mappings = new HashMap<String, Set<String>>();
	
	
	public void loadMappings(String path){
		
		File directory = new File(path);
		
		//This is given in OAEI distribution
		if (!directory.exists())
			return;
		
		String filenames[] = directory.list();
		
		MappingsReaderManager manager;
		
		for(int i=0; i<filenames.length; i++){
			
			if (filenames[i].contains(".rdf")){
				///Mostly equivalences
				manager = new MappingsReaderManager(directory.getAbsolutePath()+"/"+filenames[i], MappingsReaderManager.OAEIFormat);
				//System.out.println("Mapping categories: " +directory.getAbsolutePath() + "/"+ filenames[i] +  " " + manager.getMappingObjectsSize());
				addMappings(manager.getMappingObjects());
			}			
			else if (filenames[i].contains(".csv")){				
				//Read and ignore lines starting with #
				//Broader - Narrower relationships as csv
				manager = new MappingsReaderManager(directory.getAbsolutePath()+"/"+filenames[i], MappingsReaderManager.FlatFormat);
				//System.out.println("Mapping categories: " +directory.getAbsolutePath() + "/"+ filenames[i] +  " " + manager.getMappingObjectsSize());
				addMappings(manager.getMappingObjects());
			}			
		}	
		
	}
	
	
	
	private void addMappings(Set<MappingObjectStr> mappings){
		
		for (MappingObjectStr mapping : mappings){
			//A super category is compatible with subcategories
			//e.g. and opera would be compatible with opera_comique
			
			if (mapping.getMappingDirection()==MappingObjectStr.EQ){
				addMapping(mapping.getIRIStrEnt1(), mapping.getIRIStrEnt2());
				addMapping(mapping.getIRIStrEnt2(), mapping.getIRIStrEnt1());
			}
			else if (mapping.getMappingDirection()==MappingObjectStr.SUB){
				addMapping(mapping.getIRIStrEnt2(), mapping.getIRIStrEnt1());
			}
			else if (mapping.getMappingDirection()==MappingObjectStr.SUP){
				addMapping(mapping.getIRIStrEnt1(), mapping.getIRIStrEnt2());
			}
			
		}
	}
	
	private void addMapping(String uri1, String uri2){
		if (!category_mappings.containsKey(uri1))
			category_mappings.put(uri1, new HashSet<String>());
		
		category_mappings.get(uri1).add(uri2);
		
	}
	
	
	public Map<String, Set<String>> getCategoryMappings(){
		return category_mappings;
	}

	
	public boolean hasMappings(String uri){
		return category_mappings.containsKey(uri);
	}
	
	public Set<String> getMappings(String uri){		
		return category_mappings.get(uri);		
	}
	
	
	public Set<String> getMappingsWithCheck(String uri){
		if (category_mappings.containsKey(uri))
			return category_mappings.get(uri);
		else
			return new HashSet<String>();
	}
	
	

}

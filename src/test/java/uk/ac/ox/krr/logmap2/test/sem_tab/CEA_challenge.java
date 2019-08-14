package uk.ac.ox.krr.logmap2.test.sem_tab;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import com.opencsv.CSVReader;

import uk.ac.ox.krr.logmap2.LogMap2_Matcher;
import uk.ac.ox.krr.logmap2.OntologyLoader;
import uk.ac.ox.krr.logmap2.io.OutPutFilesManager;
import uk.ac.ox.krr.logmap2.io.ReadFile;
import uk.ac.ox.krr.logmap2.io.WriteFile;
import uk.ac.ox.krr.logmap2.mappings.objects.MappingObjectStr;
import uk.ac.ox.krr.logmap2.test.oaei.StandardMeasures;
import uk.ac.ox.krr.logmap2.utilities.Timer;

public class CEA_challenge {

	
	Set<MappingObjectStr> mappings = new HashSet<MappingObjectStr>();
	
	String dbpedia_ontology_str;
	String dbpedia_fragments_folder;
	OWLOntology dbpedia_ontology;
	String out_file;
	String target_file;
	
	Set<String> target_uris = new HashSet<String>(); 
	
	
	
	public CEA_challenge(String folder, OWLOntology dbpedi_onto, String target, String out_file) {
		
		this.out_file = out_file;
		
		this.target_file=target;
		readTarget();
		
		this.dbpedia_ontology = dbpedi_onto;
		
		
		try (Stream<Path> paths = Files.walk(Paths.get(folder))) {
			
			Timer t = new Timer();
			
			paths
		        .filter(Files::isRegularFile)
		        .forEach(matcherConsumer); //System.out::println
		    
			
			System.out.println("Discovered mappings: " + mappings.size());
			
			
			//Store mappings
			saveMappings();
			
			
			System.out.println("Total time: " + t.duration() + " (s)"); 
			
			
			
		    
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		
	}
	
	
	
	public CEA_challenge(String folder_rdf_tables, String folder_dbpedia_fragments, String target, String out_file) {
		
		this.out_file = out_file;
		this.dbpedia_fragments_folder = folder_dbpedia_fragments;
		
		this.target_file=target;
		readTarget();
		
		
		try (Stream<Path> paths = Files.walk(Paths.get(folder_rdf_tables))) {
			
			Timer t = new Timer();
			
			paths
		        .filter(Files::isRegularFile)
		        .forEach(logMapMatcherConsumer); //System.out::println
		        //.forEach(printConsumer);
		    
			
			System.out.println("Discovered mappings: " + mappings.size());
			
			
			//Store mappings
			saveMappings();
			
			
			System.out.println("Total time: " + t.duration() + " (s)"); 
			
			
			
		    
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		
	}
	
	
	
	private void readTarget() {
		
		
		try {

			String line;
			String uri;
			
			String ns = "http://www.semanticweb.org/challenge/sem-tab#";
			
			/*
			ReadFile reader = new ReadFile(target_file);
			while ((line = reader.readLine()) != null){
				
				String[] elements = line.split(",");
				
				if (elements.length<3)
					continue;
				
				uri = ns + "tab-" + elements[0].replace("\"", "") + "-col-" +  elements[1].replace("\"", "") + "-row-"+  elements[2].replace("\"", "");
				
				System.out.println(uri);
				target_uris.add(uri);
				
				
			}*/
			 CSVReader reader = new CSVReader(new FileReader(target_file));
			 String [] nextRecord;//nextRecord
			 while ((nextRecord = reader.readNext()) != null) {
				 
				 if (nextRecord.length<3)
						continue;
					
				uri = ns + "tab-" + nextRecord[0].replace("\"", "") + "-col-" +  nextRecord[1].replace("\"", "") + "-row-"+  nextRecord[2].replace("\"", "");
					
				target_uris.add(uri);
				 
			 }
			 System.out.println("Number of targets: " + target_uris.size());
			 reader.close();
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
				
		
	}
	
	
	public static OWLOntology loadOntology(String ontology_uri) {
		try {
			
			Timer t = new Timer();
			OntologyLoader loader = new OntologyLoader(ontology_uri);
			
			System.out.println("Loading target ontology time: " + t.duration() + " (s)"); 
			
			return loader.getOWLOntology();
			
		
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			System.err.println("Error loading: " + ontology_uri);
		}
		
		return null;
		
	}
	
	
	
	
	/**
	 * Stores mappings in CEA challenge format: “Table ID”, “Column ID”, “Row ID”, “DBpedia entity”
	 * @throws Exception 
	 */
	private void saveMappings() throws Exception {
		
		//AS OAEI
		OutPutFilesManager manager = new OutPutFilesManager();
		
		manager.createOutFiles(out_file.replace(".csv", ""), OutPutFilesManager.FlatFormat, "http://www.semanticweb.org/challenge/sem-tab#", "http://dbpedia.org/ontology/");
		
		
		//Set<MappingObjectStr> out_put_mappings = new HashSet<MappingObjectStr>();
		
		
		
		//AS CEA challenge
		WriteFile writer = new WriteFile(out_file);
		
		String tmp;
		String table;
		String col;
		String row;
		
		Map<String, MappingObjectStr> mapings_output = new HashMap<String, MappingObjectStr>();
		
		
		for (MappingObjectStr m : mappings) {
			
			//Split accordingly: ns:tab-8468806_0_4382447409703007384-col-1-row-1
			if (m.getIRIStrEnt1().indexOf("tab-")<0 || m.getIRIStrEnt1().indexOf("-col-")<0 || m.getIRIStrEnt1().indexOf("-row-")<0) {
				//System.out.println(m.getIRIStrEnt1() + " " + m.getIRIStrEnt2());
				continue;
			}
			
			if (!target_uris.contains(m.getIRIStrEnt1()))  // not in targets
				continue;
			
			//keep 1-1
			if (!mapings_output.containsKey(m.getIRIStrEnt1()))
				mapings_output.put(m.getIRIStrEnt1(), m);
			else {
				if (m.getConfidence() > mapings_output.get(m.getIRIStrEnt1()).getConfidence()){
					mapings_output.put(m.getIRIStrEnt1(), m);
				}
			}
			
		}
		
		
		//Mappings involving traget cells and 1-1 
		for(String key : mapings_output.keySet()) {
			
			MappingObjectStr m = mapings_output.get(key);
			
			tmp = m.getIRIStrEnt1().split("tab-")[1]; //8468806_0_4382447409703007384-col-1-row-1
			
			table = tmp.split("-col-")[0];
			tmp = tmp.split("-col-")[1]; //1-row-1
			
			col = tmp.split("-row-")[0];
			row = tmp.split("-row-")[1];
			
			writer.writeLine("\"" + table + "\",\"" + col + "\",\"" + row + "\",\"" + m.getIRIStrEnt2() + "\"");
			
			//flat oaei format for internal tests
			manager.addMapping(m);		
			
		}
		
		
		manager.closeAndSaveFiles();
		
		writer.closeBuffer();
		
	}
	
	
	
	
	
	
	
	
	
	
	Consumer<Path> printConsumer = new Consumer<Path>() {
	    public void accept(Path name) {
	        System.out.println(name.toUri().toString() + " " + name.getFileName());
	    };
	};

	
	/**
	 * Perform the alignment between the tables and dbpedia ontology
	 */
	Consumer<Path> matcherConsumer = new Consumer<Path>() {
	    public void accept(Path name) {
	        
	    	System.out.println("Table: "+ name.getFileName());
	    	
	    	Timer t = new Timer();
	    	
	    	try {
	    		
	    		OntologyLoader loader = new OntologyLoader(name.toUri().toString());
			
				LogMap2_Matcher logmap = new LogMap2_Matcher(
						loader.getOWLOntology(), dbpedia_ontology);
				
				
				System.out.println("\tTask time: " + t.durationMilisecons() + " (ms)");
				//t.pause();
				
				//Keep only best match for a cell. CEA only expects the best match
				//1 cell -> 1 dbpedia entity
				MappingObjectStr m_best;
				for (MappingObjectStr m1 : logmap.getLogmap2_Mappings()) {
					
					m_best = m1;
					for (MappingObjectStr m2 : logmap.getLogmap2_Mappings()) {					
						if (m1.equals(m2))
							continue;
						//same origin cell
						if (m_best.getIRIStrEnt1().equals(m2.getIRIStrEnt1()) && m_best.getConfidence()<m2.getConfidence()) { 
							m_best = m2;
						}
					}
					
					mappings.add(m_best);
					
				}
				
				
			
	    	} catch (OWLOntologyCreationException e) {
				// TODO Auto-generated catch block
				System.err.println("Error loading: " + name.toUri().toString());
			}
			
			 
			//mappings.addAll(logmap.getLogmap2_Mappings());
	    	
	    	
	    };
	};
	
	
	Consumer<Path> logMapMatcherConsumer = new Consumer<Path>() {
	    public void accept(Path name) {
	        
	    	//System.out.println("Table: "+ name.getFileName());
	    	
	    	Timer t = new Timer();
	    	
	    	try {
	    		
	    		//OntologyLoader loader = new OntologyLoader(name.toUri().toString());
	    		
	    		File file = new File(dbpedia_fragments_folder + name.getFileName());
	    		
	    		if (file.exists()) {
	    			
	    			String dbpedia_fragment_for_table = dbpedia_fragments_folder + name.getFileName();
	    			String table_rdf = name.toString();
	    			
	    			System.out.println(table_rdf);
	    			System.out.println(dbpedia_fragment_for_table);
	    			
	    			OntologyLoader loader1 = new OntologyLoader(new File(table_rdf));
	    			OntologyLoader loader2 = new OntologyLoader(new File(dbpedia_fragment_for_table));
	    			
	    			//for (OWLNamedIndividual e : loader1.getOWLOntology().getIndividualsInSignature())
	    			//	System.out.println(e.toStringID());
	    			
	    			
					LogMap2_Matcher logmap = new LogMap2_Matcher(
							loader1.getOWLOntology(), loader2.getOWLOntology());
							//loader.getOWLOntology(), dbpedia_ontology);
					
					
					System.out.println("\tTask time: " + t.durationMilisecons() + " (ms)");
					//t.pause();
					
					//Keep only best match for a cell. CEA only expects the best match
					//1 cell -> 1 dbpedia entity
					MappingObjectStr m_best;
					for (MappingObjectStr m1 : logmap.getLogmap2_Mappings()) {
						//System.out.println(m1);
						m_best = m1;
						for (MappingObjectStr m2 : logmap.getLogmap2_Mappings()) {					
							if (m1.equals(m2))
								continue;
							//same origin cell
							if (m_best.getIRIStrEnt1().equals(m2.getIRIStrEnt1()) && m_best.getConfidence()<m2.getConfidence()) { 
								m_best = m2;
							}
						}
						
						mappings.add(m_best);
						
					}
	    		}
	    		else {
	    			System.out.println(dbpedia_fragments_folder + name.getFileName() + " does not exixt.");
	    		}
			
	    	} catch (Exception e) {
				// TODO Auto-generated catch block
				System.err.println("Error: " + name.toUri().toString());
			}
			
			 
			//mappings.addAll(logmap.getLogmap2_Mappings());
	    	
	    	
	    };
	};

			
	
	


	
	public static void main (String[] args) {
		
		String base_path = "/home/ejimenez-ruiz/Documents/ATI_AIDA/TabularSemantics/Challenge";
		
		String folder;
		String folder_dbpedia_fragments;
		String dbpedia_onto;
		String out_file;
		String target;
		
		//Low Precision may also be due to missing redirections in GT (round 1)				
		//keep 1-1 mappings only
		int round=2;
		boolean types=true;
		boolean ontology=true; //With or without dbpedia taxonomy
		boolean single_rdf_tables_file = false;
		boolean multiple_dbpedia_fragments = true;
		
		
		if (round==1) {
			folder = base_path + "/Round1/CEA_RDF_tables_r1";
			folder_dbpedia_fragments = base_path + "/Round1/dbpedia_fragments/";
			dbpedia_onto = "file:" + base_path + "/Round1/dbpedia_round1";
			out_file = base_path + "/Round1/logmap/logmap_mappings_round1.csv";
			target = base_path + "/Round1/CEA_Round1_Targets.csv";
		}
		else {
			folder = base_path + "/Round2/CEA_RDF_tables_r2";
			//folder = base_path + "/Round2/CEA_RDF_tables_r2_test";
			folder_dbpedia_fragments = base_path + "/Round2/dbpedia_fragments/";
			dbpedia_onto =  "file:" + base_path + "/Round2/dbpedia_round2";
			out_file = base_path + "/Round2/logmap/logmap_mappings_round2.csv";
			target = base_path + "/Round2/CEA/cea_target.csv";
		}
		
		if (ontology)//with tax
			dbpedia_onto+=".ttl";
		else
			dbpedia_onto+="_no_tax.ttl";
			
		if (types)
			folder+="_types/";
		else
			folder+="/";
		
		if (single_rdf_tables_file)
			folder+="single_file/";
		
		
		
		
		
		//We use one fragment per table
		if (multiple_dbpedia_fragments)
			new CEA_challenge(folder, folder_dbpedia_fragments, target, out_file);
		else
			new CEA_challenge(folder, loadOntology(dbpedia_onto), target, out_file);
		
	}

}
	
	
	

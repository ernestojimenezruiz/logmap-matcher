package uk.ac.ox.krr.logmap2.test.sem_tab;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

import uk.ac.ox.krr.logmap2.LogMap2_Matcher;
import uk.ac.ox.krr.logmap2.io.OutPutFilesManager;
import uk.ac.ox.krr.logmap2.io.WriteFile;
import uk.ac.ox.krr.logmap2.mappings.objects.MappingObjectStr;
import uk.ac.ox.krr.logmap2.test.oaei.StandardMeasures;
import uk.ac.ox.krr.logmap2.utilities.Timer;

public class CEA_challenge {

	
	Set<MappingObjectStr> mappings = new HashSet<MappingObjectStr>();
	
	String dbpedia_ontology;
	String out_file;
	
	public CEA_challenge(String folder, String dbpedia_ontology, String out_file) {
		
		this.dbpedia_ontology = dbpedia_ontology;
		this.out_file = out_file;
		
		
		try (Stream<Path> paths = Files.walk(Paths.get(folder))) {
			
			Timer t = new Timer();
			
			paths
		        .filter(Files::isRegularFile)
		        .forEach(matcherConsummer); //System.out::println
		    
			
			System.out.println("Discovered mappings: " + mappings.size());
			
			
			//Store mappings
			saveMappings();
			
			
			System.out.println("Total time: " + t.duration() + " (s)"); 
			
			
			
		    
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		
	}
	
	
	/**
	 * Stores mappings in CEA challenge format: “Table ID”, “Column ID”, “Row ID”, “DBpedia entity”
	 * @throws Exception 
	 */
	private void saveMappings() throws Exception {
		
		//AS OAEI
		OutPutFilesManager manager = new OutPutFilesManager();
		
		manager.createOutFiles(out_file.replace(".csv", ""), OutPutFilesManager.FlatFormat, "http://www.semanticweb.org/challenge/sem-tab#", "http://dbpedia.org/ontology/");
		manager.addMappings(mappings);		
		manager.closeAndSaveFiles();
		
		
		
		//AS CEA challenge
		WriteFile writer = new WriteFile(out_file);
		
		String tmp;
		String table;
		String col;
		String row;
		for (MappingObjectStr m : mappings) {
			//Split accordingly: ns:tab-8468806_0_4382447409703007384-col-1-row-1
			if (m.getIRIStrEnt1().indexOf("tab-")<0 || m.getIRIStrEnt1().indexOf("-col-")<0 || m.getIRIStrEnt1().indexOf("-row-")<0) {
				System.out.println(m.getIRIStrEnt1() + " " + m.getIRIStrEnt2());
				continue;
			}
			
			tmp = m.getIRIStrEnt1().split("tab-")[1]; //8468806_0_4382447409703007384-col-1-row-1
			
			table = tmp.split("-col-")[0];
			tmp = tmp.split("-col-")[1]; //1-row-1
			
			col = tmp.split("-row-")[0];
			row = tmp.split("-row-")[1];
			
			writer.writeLine("\"" + table + "\",\"" + col + "\",\"" + row + "\",\"" + m.getIRIStrEnt2() + "\"");
			
		}
		
		writer.closeBuffer();
		
	}
	
	
	
	
	
	
	
	
	
	
	Consumer<Path> printConsumer = new Consumer<Path>() {
	    public void accept(Path name) {
	        System.out.println(name.toUri().toString());
	    };
	};

	
	/**
	 * Perform the alignment between the tables and dbpedia ontology
	 */
	Consumer<Path> matcherConsummer = new Consumer<Path>() {
	    public void accept(Path name) {
	        
	    	System.out.println("Table: "+ name.getFileName());
	    	
	    	Timer t = new Timer();
	    	

			LogMap2_Matcher logmap = new LogMap2_Matcher(
					name.toUri().toString(), dbpedia_ontology);
			
			
			System.out.println("\tTask time: " + t.durationMilisecons() + " (ms)");
			//t.pause();
			
			
			//TODO keep 1-1 mapping!! CEA only expects the best match
			//1 cell -> 1 dbpedia entity
			//Low Precision may also be due to missing redirections in GT (round 1) 
			mappings.addAll(logmap.getLogmap2_Mappings());
	    	
	    	
	    };
	};

			
	
	


	
	public static void main (String[] args) {
		
		String base_path = "/home/ejimenez-ruiz/Documents/ATI_AIDA/TabularSemantics/Challenge";
		
		String folder;
		String dbpedia_onto;
		String out_file;
		
		int round=1;
		boolean types=true;
		
		if (round==1) {
			folder = base_path + "/Round1/CEA_RDF_tables_r1";
			dbpedia_onto = "file:" + base_path + "/Round1/dbpedia_round1.ttl";
			out_file = base_path + "/Round1/logmap/logmap_mappings_round1.csv";
		}
		else {
			folder = base_path + "/Round2/CEA_RDF_tables_r2";
			dbpedia_onto =  "file:" + base_path + "/Round2/dbpedia_round2.ttl";
			out_file = base_path + "/Round2/logmap/logmap_mappings_round2.csv";
		}
		
		if (types)
			folder+="_types/";
		else
			folder+="/";
		
		new CEA_challenge(folder, dbpedia_onto, out_file);
		
		
	}

}
	
	
	

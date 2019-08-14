package uk.ac.ox.krr.logmap2.test.sem_tab;

import java.io.FileReader;
import java.net.URL;

import com.opencsv.CSVReader;

import uk.ac.ox.krr.logmap2.io.OutPutFilesManager;
import uk.ac.ox.krr.logmap2.io.ReadFile;
import uk.ac.ox.krr.logmap2.oaei.reader.FlatAlignmentReader;
import uk.ac.ox.krr.logmap2.oaei.reader.RDFAlignReader;
import uk.ac.ox.krr.logmap2.test.oaei.StandardMeasures;

public class CEA_challenge_score {

	
	public CEA_challenge_score() {
		
		
		
	}
	
	
	
	private void transformGT(String gt_file) throws Exception {
		

		String line;
		String uri1;
		//String uri2;
		
		String ns = "http://www.semanticweb.org/challenge/sem-tab#";
		
		
		
		OutPutFilesManager manager = new OutPutFilesManager();
		
		manager.createOutFiles(gt_file.replace(".csv", ""), OutPutFilesManager.FlatFormat, "http://www.semanticweb.org/challenge/sem-tab#", "http://dbpedia.org/ontology/");
		
		
		
		/*ReadFile reader = new ReadFile(gt_file);
		while ((line = reader.readLine()) != null){
			
			String[] elements = line.split(",");
			
			if (elements.length<4)
				continue;
			
			uri1 = ns + "tab-" + elements[0].replace("\"", "") + "-col-" +  elements[1].replace("\"", "") + "-row-"+  elements[2].replace("\"", "");
			String[] uris = elements[3].replace("\"", "").split(" ");
			
			for (String uri2 : uris)
				manager.addInstanceMapping2Files(uri1, uri2, 1.0);
			
			
		}*/
		
		
		
		CSVReader reader = new CSVReader(new FileReader(gt_file));
		 String [] nextRecord;//nextRecord
		 
		 while ((nextRecord = reader.readNext()) != null) {
			 
			
				if (nextRecord.length<4)
					continue;
				
				uri1 = ns + "tab-" + nextRecord[0].replace("\"", "") + "-col-" +  nextRecord[1].replace("\"", "") + "-row-"+  nextRecord[2].replace("\"", "");
				String[] uris = nextRecord[3].replace("\"", "").split(" ");
				
				for (String uri2 : uris)
					manager.addInstanceMapping2Files(uri1, uri2, 1.0);
			 
		 }
		 reader.close();
		
		
		

		manager.closeAndSaveFiles();
		
		
		
	}
	
	
	
	
	private void getStandardMeausres(String mappings, String reference) throws Exception {
		
		FlatAlignmentReader readerMappings = 
				new FlatAlignmentReader(mappings);
		
		FlatAlignmentReader readerReference = 
				new FlatAlignmentReader(reference);
		
		
		StandardMeasures.computeStandardMeasures(readerMappings.getMappingObjects(), readerReference.getMappingObjects());
		
		System.out.println(readerMappings.getMappingObjectsSize()  + "\t" + readerReference.getMappingObjectsSize());
		System.out.println(StandardMeasures.getPrecision()  + "\t" + StandardMeasures.getRecall()  + "\t" + StandardMeasures.getFscore());
		
	
	}
	
	
	
	
	public static void main (String[] args) {
		
		CEA_challenge_score ceaScore = new CEA_challenge_score();
		
		
		
		String base_path = "/home/ejimenez-ruiz/Documents/ATI_AIDA/TabularSemantics/Challenge";
		String cea_gt;
		String cea_gt_txt;
		String logmap;
		
		int round=2;
		if (round==1) {
			cea_gt = base_path + "/Round1/CEA_Round1_gt.csv";
			cea_gt_txt = base_path + "/Round1/CEA_Round1_gt.txt";
			logmap = base_path + "/Round1/logmap/logmap_mappings_round1.txt";
		}
		else {
			cea_gt = base_path + "/Round2/CEA/cea_gt.csv";
			cea_gt_txt = base_path + "/Round2/CEA/cea_gt.txt";
			logmap = base_path + "/Round2/logmap/logmap_mappings_round2.txt";
		}
		
		
		try {
			//ceaScore.transformGT(cea_gt);
			ceaScore.getStandardMeausres(logmap, cea_gt_txt);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
	
	}
	
	
	
	
}

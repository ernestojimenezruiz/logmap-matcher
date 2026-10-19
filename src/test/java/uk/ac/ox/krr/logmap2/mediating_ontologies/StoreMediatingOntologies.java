package uk.ac.ox.krr.logmap2.mediating_ontologies;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.RDFXMLDocumentFormat;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDocumentFormat;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import uk.ac.ox.krr.logmap2.Parameters;
import uk.ac.ox.krr.logmap2.bioportal.RESTBioPortalAccess;

public class StoreMediatingOntologies {
	
	// constructor
	public StoreMediatingOntologies() {}
			
	public List<String> getOntologyListFromFile(String filePath) throws IOException {
		
		// list that holds strings of a file
        List<String>  listOfStrings = new ArrayList<String>();
      
        // load data from file
        BufferedReader bf = new BufferedReader(new FileReader(filePath));
      
        // read entire line as string
        String line = bf.readLine();
      
        // checking for end of file
        while (line != null) {
        	if(line.startsWith("#")) {
        		System.out.println("Skipping MO " + line + "from list of ontologies.");
                line = bf.readLine();

        	}
        	else {
            listOfStrings.add(line);
            line = bf.readLine();

        	}
        }
      
        // closing bufferreader object
        bf.close();
      		
		return listOfStrings;
	}	
			
	public void saveOntology(String ontoLabel, OWLOntology inputOntology, String basePath){
	
	
			//Create a file for the new format
			String mo_output_path = basePath+ ontoLabel + ".owl";
			System.out.println("Storing ontology at " + mo_output_path);
			File outFile= new File(mo_output_path);
			OWLOntologyManager onto_manager = OWLManager.createOWLOntologyManager();
			OWLDocumentFormat format = new RDFXMLDocumentFormat();
			IRI outputStream = IRI.create(outFile);
			try {
				onto_manager.saveOntology(inputOntology, format, outputStream);
			} catch (OWLOntologyStorageException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
		
	public OWLOntology CallBioPortal(String onto_label, String base_output_path) {
		
		RESTBioPortalAccess bioportal = new RESTBioPortalAccess();
		if (bioportal.isActive()) {
		
			System.out.println("BioPortal is active: ");
			
			OWLOntology moDownload = bioportal.downLoadOntology(onto_label, 3);
			System.out.println("Downloaded ontology" + moDownload.getOntologyID());
			
			return moDownload;
		}
		else {
			// TODO return an empty ontology or throw exception
			return null;
		}
	}
	
	public void saveOntologiesFromList() {
		//TODO clean up the main
		
	}
	
	public boolean checkOntoPath(String ontoStr, String basePath, String fileExtension) {
		// TODO Auto-generated method stub
		String ontoPath = basePath + ontoStr + fileExtension;
		File ontoFile = new File(ontoPath);
		
		if (ontoFile.exists() && ontoFile.isFile()) {
			//System.out.println(ontoFile.getName());
			return true;
		}else {
		return false;
		}
	}
		
	
	public static void main(String[] args) {
		
	
		
		String basePath ="/home/valentina/git-repos/test-data/test_onto_output/test-mediating-store/";
//		String ontoLabel = "MIO";
			StoreMediatingOntologies moStorer = new StoreMediatingOntologies();
//		OWLOntology moDownload = moStorer.CallBioPortal(ontoLabel, basePath);
//		moStorer.saveOntology(ontoLabel, moDownload, basePath);
		
		String listFile = "/home/valentina/git-repos/test-data/test_onto_output/test-mediating-store/logmap_top10_mediating_ontologies.txt";
		try {
			List<String> moList = moStorer.getOntologyListFromFile(listFile);
			int countOnto = moList.size();
			System.out.println("There are" + countOnto + "mediating ontologies in the list");
			
			int counter = 1;
			for (String ontoStr: moList) {
				
				System.out.println("Fetching ontology No.  " + counter + " label:  " +  ontoStr);

				// TODO if file exists, skip
				
				boolean isOntoThere = moStorer.checkOntoPath(ontoStr, basePath, ".owl");
				
				if (isOntoThere == true) {
				
					System.out.println("Ontology file already exists at location, skipping");
					continue;
				}
				OWLOntology moDownload = moStorer.CallBioPortal(ontoStr, basePath);
				System.out.println("Fetched ontology" + ontoStr);

				//TODO store the ontology
				moStorer.saveOntology(ontoStr, moDownload, basePath);
				System.out.println("Stored ontology" + ontoStr);
				
				counter+=1;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("All Stored");
	}
}
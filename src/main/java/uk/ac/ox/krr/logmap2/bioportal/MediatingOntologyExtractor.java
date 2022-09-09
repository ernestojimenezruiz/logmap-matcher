package uk.ac.ox.krr.logmap2.bioportal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.semanticweb.owlapi.model.OWLOntology;

import uk.ac.ox.krr.logmap2.Parameters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;



public class MediatingOntologyExtractor {
	
	private RESTBioPortalAccess bioportal;
	
	private TreeSet<CandidateMediatingOntology> ordered_candidate_mo  = new TreeSet<CandidateMediatingOntology>(new CandidateMediatingOntologyComparator());
	
	//This structure will be updated wrt the results of the BioPortal calls
	private Map<String, CandidateMediatingOntology> candiateMOs = new HashMap<String, CandidateMediatingOntology>();
	
	private List<String> selectedMediatingOntologies = new ArrayList<String>();
	
	private Set<String> representative_labels;
	
	private Set<String> acronyms = new HashSet<String>();
	
	//Ontologies producing known errors
	private Set<String> bad_ontologies = new HashSet<String>();
	
	
	
	private final int MAX_SIZE_ONTOLOGY=20000;
	
	private final int MAX_NUM_CALLS=200;
	private final int MIN_NUM_CALLS=35;
	
	
	private final int SIZE_SELECTED_ONTOS=Parameters.max_mediating_ontologies; //10	
	public final int MAX_MO_FOR_MATCHING=Parameters.max_mediating_ontologies; //5 or 10
	
	private int num_calls=0;
	
	
	/**
	 * This class extracts the set of most suitable mediating ontologies 
	 * for a given set of concept labels from ontology 1 and ontology 2
	 * @param representative_labels_onto1
	 * @param representative_labels_onto2
	 */
	public MediatingOntologyExtractor(
			Set<String> representative_labels){
	
		bioportal = new RESTBioPortalAccess();
		
		if (bioportal.isActive()){
			
			//TODO To speed up process
			//This could be commented since we already ignore those ontologies giving errors or not contributing to the composed mappings
			bad_ontologies.add("OPE");//gives an error
			//bad_ontologies.add("SNOMEDCT"); //not available
			bad_ontologies.add("NIFSTD"); //error when downloading it
			bad_ontologies.add("RADLEX"); //interesting concepts only in individuals
			//bad_ontologies.add("FMA"); //the download is a zip file and gives an error
			bad_ontologies.add("SOPHARM");//error downloading
			bad_ontologies.add("EP");//Basically it only contaisn terms from the Imported FMA, but is is imported from outside bioPortal (error downloading)
			bad_ontologies.add("GO-PLUS"); //Error accessing metadata
			bad_ontologies.add("DINTO"); //Error accessing importing ontology
			bad_ontologies.add("HHEAR"); //Error accessing importing ontology
			
			
			this.representative_labels=representative_labels;
			
			extractMediatingOntologies();
			
			//if (Parameters.print_output_always)
				printMediatingOntologies();
			
			
		}
		else{
			System.out.println("BioPotal is not accessible. No mediating ontology has been extracted.");
		}
		
	}
	
	private void printMediatingOntologies() {
		
		System.out.println("Necessary number of calls to BioPortal: " + num_calls);
		for (int i=0; i<selectedMediatingOntologies.size(); i++){
			System.out.println(selectedMediatingOntologies.get(i));
			System.out.println("\tPos hits: " + candiateMOs.get(selectedMediatingOntologies.get(i)).getPositiveHits());
			//System.out.println("\tSynonyms: " + candiateMOs.get(selectedMediatingOntologies.get(i)).getNumberOfProvidedSynonyms());
			System.out.println("\tSize: " + candiateMOs.get(selectedMediatingOntologies.get(i)).getNumberOfClasses());
			System.out.println("\tLanguage: " + candiateMOs.get(selectedMediatingOntologies.get(i)).getOntologyLanguage());
		}
		
	}

	private void extractMediatingOntologies(){
		
		for (String rep_label : representative_labels){
			
			num_calls++;
			
			extractSuitableOntologiesForLabel(rep_label);
			
			
			if (num_calls>MAX_NUM_CALLS){
				createOrderedMediatingOntologiesList();
				break;
			}
			//We check every 10 calls if any change in the ranking
			else if (num_calls%10==0){
				
				//if top 5 not changed in 10 calls then we stop (20 calls)
				if (!createOrderedMediatingOntologiesList())
					break;
			}	
			
		}
		
	}
	
	private void extractSuitableOntologiesForLabel(String label){
		try {
		    
			acronyms.clear();
			
			int size_synset;
			//TODO We do not require exact correspondence?
			//If no exact we may get many crap and more than one page... time may is affected dramatically
			JsonNode jsonConcepts = bioportal.getConcepts4Label(label, true);	
			
			//If an ontology appears several times for a concept
			for (JsonNode concept : jsonConcepts) {
				
				//TODO Something has change din BioPortal and no synonyms are given for UBERON
				//if (concept.has(bioportal.SYNONYM)) {
					
					String acronym = bioportal.getOntologyAcronym(concept
							.get(bioportal.LINKS).get(bioportal.ONTOLOGY).asText());
					
					//Consider only one positive hit per label
					//For example EHDA has 39 positive hits for only one label!!
					//Also ignore ontologies that produce an error. E.g. have a wrong OWL format
					if (acronyms.contains(acronym) || bad_ontologies.contains(acronym))
						continue;
					
					acronyms.add(acronym);
					
					size_synset=0;
					if (concept.has(bioportal.SYNONYM))
						size_synset=concept.get(bioportal.SYNONYM).size();
					
					//System.out.println("\t"+acronym);
					
					//Already in list
					if (candiateMOs.containsKey(acronym)){
						
						candiateMOs.get(acronym).increasePositiveHits();
						candiateMOs.get(acronym).increaseNumberOfProvidedSynonyms(size_synset);
						
					}
					else{
						
						//We call bioportal about metrics
						//System.out.println(acronym);
						BioPortalOntologyInfo onto_bio = bioportal.getMetricInfo4Onto(acronym);
						
						///if (onto_bio.getClasses()<=MAX_SIZE_ONTOLOGY){
							candiateMOs.put(
									acronym, 
									new CandidateMediatingOntology(
											onto_bio,
											size_synset));
						//}
					}
					
					

				//}

			}

		}


		catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			System.out.println("Error performing the BioPortal call for: "
					+ label + ". Mediating ontology search.");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Error performing the BioPortal call for: "
					+ label  + ". Mediating ontology search.");
		}	
	}
	
	
	

	
	
	
	/**
	 * Return "true" if changed top 5 OR still too early. We will call this method every X calls to bioportal
	 * @return
	 */
	private boolean createOrderedMediatingOntologiesList(){
		
		//Former order
		CandidateMediatingOntology[] former_order = new CandidateMediatingOntology[ordered_candidate_mo.size()];
		former_order = ordered_candidate_mo.toArray(former_order);
		
		//Clear and Add new elements from hashMap
		ordered_candidate_mo.clear();
		
		for (String key : candiateMOs.keySet()){
			ordered_candidate_mo.add(candiateMOs.get(key));
		}
		
		//New order
		CandidateMediatingOntology[] new_order = new CandidateMediatingOntology[ordered_candidate_mo.size()];
		new_order = ordered_candidate_mo.toArray(new_order);
		
		
		//Current selected ontologies
		//printMediatingOntologies();
		selectedMediatingOntologies.clear();
		int size = (new_order.length>=SIZE_SELECTED_ONTOS) ? SIZE_SELECTED_ONTOS : new_order.length;
		//int size = new_order.length;
		for (int i = 0; i < size; i++) {
			selectedMediatingOntologies.add(new_order[i].getOntologyAcronym());
		}
		
		
		//Not reached minimum num of calls
		if (num_calls<MIN_NUM_CALLS)
			return true;
		
		if (new_order.length>=5 && former_order.length>=5){
			
			//We require the top-5 MO to have more than 20 positive hits 
			if (new_order[4].getPositiveHits()<20)
				return true;
			
			//compare arrays
			for (int i = 0; i < 5; i++) {
				if (!new_order[i].equals(former_order[i])){
					return true;
				}
			}
			return false;
			//return true;
		}
		else{ //not enough elements yet
			return true;
		}
		
		
	}
	
	
	public int getNumCallsBioPortal(){
		return num_calls;
	}
	
	
	public List<String> getSelectedMediatingOntologies() {
		return selectedMediatingOntologies;
	}

	
	
	public OWLOntology downloadBioPortalOntology(String ontoAcronym, int attempts){
		return bioportal.downLoadOntology(ontoAcronym, attempts);
	}




	private class CandidateMediatingOntologyComparator implements Comparator<CandidateMediatingOntology> {

		@Override
		public int compare(CandidateMediatingOntology mo1, CandidateMediatingOntology mo2) {
		
			if (mo1.getSuitabilityValue()<mo2.getSuitabilityValue())
				return 1;
			else
				return -1;
		}
		
	}

}

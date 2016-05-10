package uk.ac.ox.krr.logmap2.bioportal;

public class CandidateMediatingOntology {
	
	private BioPortalOntologyInfo bioportal_ontology;
	
	private int positive_hits;
	
	private int num_sysnonyms;
	
	//To be suitable
	private final int MAX_SIZE_ONTOLOGY=100000;
	private final int MIN_SIZE_ONTOLOGY=10;
	
	
	public CandidateMediatingOntology(BioPortalOntologyInfo bio_onto, int syn_size){
		//A candidate ontology must at least have a positive hit with at least 1 provided synonym
		positive_hits = 1;
		num_sysnonyms = syn_size;
		
		bioportal_ontology=bio_onto; //data about acronym, num classes, depth, etc.
	
	}
	
	
	public void increasePositiveHits(){
		positive_hits++;
	}
	
	public void increaseNumberOfProvidedSynonyms(int syn_size){
		num_sysnonyms+=syn_size;
	}
	
	public int getPositiveHits(){
		return positive_hits;
	}
	
	public int getNumberOfProvidedSynonyms(){
		return num_sysnonyms;
	}
	
	public int getNumberOfClasses(){
		return bioportal_ontology.getNumberOfClasses();
	}
	
	public String getOntologyLanguage(){
		return bioportal_ontology.getOntologyLanguage();
	}
	
	public int getSuitabilityValue(){
		//we rank bad big ontologies or very small ones. Big ontologies are time-consuming to download from BioPortal. 
		if (bioportal_ontology.getNumberOfClasses()>MAX_SIZE_ONTOLOGY || bioportal_ontology.getNumberOfClasses()<MIN_SIZE_ONTOLOGY)
			return -1;
		
		//only ontologies than can be loaded with OWL API
		if (!bioportal_ontology.getOntologyLanguage().equals("OWL") && !bioportal_ontology.getOntologyLanguage().equals("OBO"))
			return -1;
		
		//currently synonyms is not a good way to discriminate, we may need to report this to bioportal
		return positive_hits;// + num_sysnonyms;
	}
	
	public String getOntologyAcronym(){
		return bioportal_ontology.getAcronym();
	}
	
	
	public int hashCode(){
		return 30*getOntologyAcronym().hashCode();
	}
	
	public boolean equals(Object obj) {
		if (obj==null)
			return false;
		if (obj instanceof CandidateMediatingOntology)
			return equals((CandidateMediatingOntology)obj);
		return false;
	}
	
	
	public boolean equals(CandidateMediatingOntology mo) {
		return this.getOntologyAcronym().equals(mo.getOntologyAcronym());
	}
	
	
	

}

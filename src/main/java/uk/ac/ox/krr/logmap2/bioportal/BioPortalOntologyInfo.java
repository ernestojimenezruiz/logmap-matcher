package uk.ac.ox.krr.logmap2.bioportal;

import org.semanticweb.owlapi.model.OWLOntology;

public class BioPortalOntologyInfo {

	
	private String acronym;
	private int classes;
	private int maxDepth;
	private String ontology_language;
	private OWLOntology ontology=null; 


	/**
	 * @param acronym
	 * @param classes
	 * @param maxDepth
	 */
	public BioPortalOntologyInfo(BioPortalOntologyInfo obj) {		
		this.acronym = obj.acronym;
		this.classes = obj.classes;
		this.maxDepth = obj.maxDepth;
		this.ontology_language=obj.ontology_language;
	}
	public BioPortalOntologyInfo(String acronym, int classes, int maxDepth, String language_onto) {
		
		this.acronym = acronym;
		this.classes = classes;
		this.maxDepth = maxDepth;
		this.ontology_language = language_onto;
	}
	
	/**
	 * @param acronym
	 */
	public BioPortalOntologyInfo(String acronym) {		
		this(acronym, 0, 0, "UNKNOWN");
	}
	
	
	
	public String getAcronym() {
		return acronym;
	}
	public void setAcronym(String acronym) {
		this.acronym = acronym;
	}
	public int getNumberOfClasses() {
		return classes;
	}
	
	public void setClasses(int classes) {
		this.classes = classes;
	}
	
	
	public String getOntologyLanguage(){
		return ontology_language;
	}
	
	public int getMaxDepth() {
		return maxDepth;
	}
	public void setMaxDepth(int maxDepth) {
		this.maxDepth = maxDepth;
	}
	
	public OWLOntology getOntology(){
		return ontology;
	}
	
	public void setOntology(OWLOntology onto){
		ontology=onto;
	}
	
}

package uk.ac.ox.krr.logmap2.partitioning;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;



public class MatchingTask {
	
	private OWLOntology sourceOntology;
	private OWLOntology targetOntology;
	
	private Set<String> signatureSourceOntology;
	private Set<String> signatureTargetOntology;
	
	
	
	//Default constructor
	public MatchingTask(){
		
	}
	
	public MatchingTask(OWLOntology sourceOntology, OWLOntology targetOntology){
		this.sourceOntology=sourceOntology;
		this.targetOntology=targetOntology;
		
		signatureSourceOntology = createSignatureStrOntology(sourceOntology);
		signatureTargetOntology = createSignatureStrOntology(targetOntology);
		
		
	}
	
	
	public OWLOntology getSourceOntology() {
		return sourceOntology;
	}
	public void setSourceOntology(OWLOntology onto1) {
		this.sourceOntology = onto1;
		signatureSourceOntology = createSignatureStrOntology(sourceOntology);
	}
	public OWLOntology getTargetOntology() {
		return targetOntology;
	}
	public void setTargetOntology(OWLOntology onto2) {
		this.targetOntology = onto2;
		signatureTargetOntology = createSignatureStrOntology(targetOntology);
	}
	
	
	
	/**
	 * @return the signatureSourceOntology
	 */
	public Set<String> getSignatureSourceOntology() {
		return signatureSourceOntology;
	}

	/**
	 * @param signatureSourceOntology the signatureSourceOntology to set
	 */
	public void setSignatureSourceOntology(Set<String> signatureSourceOntology) {
		this.signatureSourceOntology = signatureSourceOntology;
	}

	/**
	 * @return the signatureTargetOntology
	 */
	public Set<String> getSignatureTargetOntology() {
		return signatureTargetOntology;
	}

	/**
	 * @param signatureTargetOntology the signatureTargetOntology to set
	 */
	public void setSignatureTargetOntology(Set<String> signatureTargetOntology) {
		this.signatureTargetOntology = signatureTargetOntology;
	}
	
	
	
	
	public Set<String> createSignatureStrOntology(OWLOntology ont){
		
		Set<String> signature = new HashSet<String>();
		
		for (OWLEntity ent : ont.getSignature(true)){
			signature.add(ent.toStringID());
		}
		
		return signature;
	}
	
	
	
	public boolean equals(Object o){
		
		if  (o == null)
			return false;
		if (o == this)
			return true;
		if (!(o instanceof MatchingTask))
			return false;
		
		MatchingTask i =  (MatchingTask)o;
		
		return equals(i);
		
	}
	
	
	public boolean equals(MatchingTask m){
				
		if (!sourceOntology.equals(m.getSourceOntology()) || !targetOntology.equals(m.getTargetOntology())){
			return false;
		}
		return true;
	}
	
	
	public String toString(){
		
		return "Matching task between : <" + sourceOntology.getOntologyID().getOntologyIRI() + "> and <" + targetOntology.getOntologyID().getOntologyIRI() + ">";
		
	}
	
	public  int hashCode() {
		  int code = 10;
		  code = 40 * code + sourceOntology.hashCode();
		  code = 50 * code + targetOntology.hashCode();
		  return code;
	}

	
	
	

}

package uk.ac.ox.krr.logmap2.partitioning;

import org.semanticweb.owlapi.model.OWLOntology;



public class MatchingTask {
	
	private OWLOntology sourceOntology;
	private OWLOntology targetOntology;
	
	
	public OWLOntology getSourceOntology() {
		return sourceOntology;
	}
	public void setSourceOntology(OWLOntology onto1) {
		this.sourceOntology = onto1;
	}
	public OWLOntology getTargetOntology() {
		return targetOntology;
	}
	public void setTargetOntology(OWLOntology onto2) {
		this.targetOntology = onto2;
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

package uk.ac.ox.krr.logmap2.division;

import java.util.HashSet;

import java.util.Set;

import org.semanticweb.owlapi.formats.RDFXMLDocumentFormat;
import org.semanticweb.owlapi.formats.FunctionalSyntaxDocumentFormat;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.parameters.Imports;

import uk.ac.ox.krr.logmap2.owlapi.SynchronizedOWLManager;



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
		
		
		//TODO only for tests! We do not need ontology. Only entities
		sourceOntology.getOWLOntologyManager().removeOntology(sourceOntology);
		targetOntology.getOWLOntologyManager().removeOntology(targetOntology);
		sourceOntology=null;
		targetOntology=null;
		
		
	}
	
	
	
	public void clear(){
		signatureSourceOntology.clear();
		signatureTargetOntology.clear();
		
		
		sourceOntology.getOWLOntologyManager().removeOntology(sourceOntology);
		targetOntology.getOWLOntologyManager().removeOntology(targetOntology);
		sourceOntology=null;
		targetOntology=null;
		
		
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
		
		for (OWLEntity ent : ont.getSignature(Imports.INCLUDED)){
			signature.add(ent.toStringID());
		}
		
		return signature;
	}
	
	
	
	
	public void saveMatchingTask(String path) throws OWLOntologyStorageException{
		
		String prefix="";
		if (!path.startsWith("/"))
			prefix="/";
		
		String sufix="";
		if (!path.endsWith("/"))
			sufix="/";
		
		String uri_file1= "file:" + prefix + path + sufix + "source.owl";
		String uri_file2= "file:" + prefix + path + sufix + "target.owl";
		
		
		OWLOntologyManager manager = SynchronizedOWLManager.createOWLOntologyManager();
		
		
		//sourceOntology.getOWLOntologyManager().saveOntology(sourceOntology, new RDFXMLOntologyFormat(), IRI.create(uri_file1));
		//sourceOntology.getOWLOntologyManager().saveOntology(sourceOntology, new OWLFunctionalSyntaxOntologyFormat(), IRI.create(uri_file1));
		
		manager.saveOntology(sourceOntology, new RDFXMLDocumentFormat(), IRI.create(uri_file1));
		manager.saveOntology(targetOntology, new RDFXMLDocumentFormat(), IRI.create(uri_file2));		
		//manager.saveOntology(sourceOntology, new FunctionalSyntaxDocumentFormat(), IRI.create(uri_file1));
		//manager.saveOntology(targetOntology, new FunctionalSyntaxDocumentFormat(), IRI.create(uri_file2));
		
		//targetOntology.getOWLOntologyManager().saveOntology(targetOntology, new RDFXMLOntologyFormat(), IRI.create(uri_file2));
		//targetOntology.getOWLOntologyManager().saveOntology(targetOntology, new OWLFunctionalSyntaxOntologyFormat(), IRI.create(uri_file2));
		
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
		
		try {
			return "Matching task between : <" + sourceOntology.getOntologyID().getOntologyIRI().get() + "> and <" + targetOntology.getOntologyID().getOntologyIRI().get() + ">";
		}
		catch (Exception e) {
			return "Matching task between: one of the ontology URIs is Null";
		}
		
	}
	
	public  int hashCode() {
		  int code = 10;
		  code = 40 * code + sourceOntology.hashCode();
		  code = 50 * code + targetOntology.hashCode();
		  return code;
	}

	
	
	

}

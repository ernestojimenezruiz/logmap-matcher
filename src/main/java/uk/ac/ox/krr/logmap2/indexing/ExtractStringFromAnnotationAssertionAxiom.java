package uk.ac.ox.krr.logmap2.indexing;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.search.EntitySearcher;
import org.semanticweb.owlapi.search.Searcher;

import uk.ac.ox.krr.logmap2.Parameters;


/**
 * 
 * This class manages the annotation that LogMap currently accepts.
 * These annotation may appear as direct strings, anonymous/named individulas or even as data assertions.
 * 
 * @author Ernesto
 *
 */
public class ExtractStringFromAnnotationAssertionAxiom {
	
	
	private OWLAnonymousIndividual geneid_value;
	private OWLNamedIndividual namedIndiv;
	private IRI namedIndivIRI;
	
	//IRIS of alternative labels annotations 
	private String rdf_label_uri = "http://www.w3.org/2000/01/rdf-schema#label";
	//private String hasRelatedSynonym_uri = "http://www.geneontology.org/formats/oboInOwl#hasRelatedSynonym";
	//private String hasExactSynonym_uri   = "http://www.geneontology.org/formats/oboInOwl#hasExactSynonym";
	//private String nci_synonym_uri = "http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl#Synonym";
	//private String nci_umls_cui_uri = "http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl#UMLS_CUI";
	//private String fma_synonym_uri="http://bioontology.org/projects/ontologies/fma/fmaOwlDlComponent_2_0#Synonym";
	private String fma_name_uri="http://bioontology.org/projects/ontologies/fma/fmaOwlDlComponent_2_0#name";
	
	//IRIS 2 Ignore 
	//private String oboinowl = "http://www.geneontology.org/formats/oboInOwl";
	
	//OWLDataFactory datafactory;
	//OWLOntology onto;
	
	public ExtractStringFromAnnotationAssertionAxiom(){		
		
	}
	
	
	public String getAnntotationLanguage(OWLAnnotationAssertionAxiom entityAnnAx){
		
		String lang;
		
		try{
			lang = ((OWLLiteral)entityAnnAx.getAnnotation().getValue()).getLang();
		}
		catch (Exception e){
			System.err.println("Error accessing object.");
			lang="";
		}
		
		return lang;
		
		
	}
	
	
	
	public String getSingleLabel(OWLAnnotationAssertionAxiom entityAnnAx, OWLOntology onto, OWLDataFactory datafactory){
		
		String label_value="";
		
		String uri_ann = entityAnnAx.getAnnotation().getProperty().getIRI().toString();
					
		//Accepted URIs
		if (Parameters.accepted_annotation_URIs_for_classes.contains(uri_ann)){
			
			if (!(label_value=asDirectValue(entityAnnAx)).equals("")){
				return label_value;
			}
			if (!(label_value=asAnonymousIndividual(entityAnnAx, onto)).equals("")){
				return label_value;
			}
			if (!(label_value=asNamedIndividual(entityAnnAx, onto, datafactory)).equals("")){
				return label_value;
			}
			if (!(label_value=asNamedIndividualFMA(entityAnnAx, onto, datafactory)).equals("")){
				return label_value;
			}			
		}
		
		//Empty value otherwise
		return label_value;
		
	}
	
	
	public Set<String> getAnntotationString(
			OWLAnnotationAssertionAxiom entityAnnAx, OWLOntology onto, OWLDataFactory datafactory){
		
		String label_value="";
		
		String uri_ann = entityAnnAx.getAnnotation().getProperty().getIRI().toString();
					
		//TODO Perform translation when direct annotation value
		
		//System.err.println(uri_ann);
		
		
		//Accepted URIs
		if (Parameters.accepted_annotation_URIs_for_classes.contains(uri_ann)){
			
			if (!(label_value=asDirectValue(entityAnnAx)).equals("")){
				return processLongLabels(label_value);
			}
			if (!(label_value=asAnonymousIndividual(entityAnnAx, onto)).equals("")){
				return processLongLabels(label_value);
			}
			if (!(label_value=asNamedIndividual(entityAnnAx, onto, datafactory)).equals("")){
				return processLongLabels(label_value);
			}
			if (!(label_value=asNamedIndividualFMA(entityAnnAx, onto, datafactory)).equals("")){
				return processLongLabels(label_value);
			}
			
		}
		
		
		return new HashSet<String>(); //empty value
		
		
		
		
		
		
		
	}
	
	
	private String asDirectValue(OWLAnnotationAssertionAxiom entityAnnAx){
		try	{
			//LogOutput.print(((OWLLiteral)annAx.getAnnotation().getValue()).getLiteral());
			
			String label = ((OWLLiteral)entityAnnAx.getAnnotation().getValue()).getLiteral().toLowerCase();
			
			//System.err.println(entityAnnAx + " " + label);
			
			if (label==null || label.equals("null") || label.equals("")){
				//System.err.println("NULL LABEL: " + entityAnnAx);
				return "";
			}
			
					
			return label;
			
			
		}
		catch (Exception e){
			//In case of error. Accessing an object in an expected way				
			return "";
		}
	}
	
	
	/**
	 * As in Mouse and NCI anatomy. Annotations al rdf:labels in anonymous individuals
	 * It seems also GO ontology (to be checked)
	 * @param entityAnnAx
	 * @return
	 */
	private String asAnonymousIndividual(OWLAnnotationAssertionAxiom entityAnnAx, OWLOntology onto){
		try {
			geneid_value=((OWLAnonymousIndividual)entityAnnAx.getAnnotation().getValue()).asOWLAnonymousIndividual();//.getID()
			for (OWLAnnotationAssertionAxiom annGeneidAx : onto.getAnnotationAssertionAxioms(geneid_value)){
				
				if (annGeneidAx.getAnnotation().getProperty().getIRI().toString().equals(rdf_label_uri)){
					
					return ((OWLLiteral)annGeneidAx.getAnnotation().getValue()).getLiteral().toLowerCase();
				}
			}
			return "";
		}
		catch (Exception e){
			//In case of error. Accessing an object in an expected way
			return "";
		}
	}
	
	
	/**
	 * In some OBO like ontologies
	 * @param entityAnnAx
	 * @return
	 */
	private String asNamedIndividual(OWLAnnotationAssertionAxiom entityAnnAx, OWLOntology onto, OWLDataFactory datafactory){
		try {
			//It is an individual
			namedIndivIRI=(IRI)entityAnnAx.getAnnotation().getValue();				
			namedIndiv=datafactory.getOWLNamedIndividual(namedIndivIRI);
			
			
			for (OWLAnnotationAssertionAxiom annIdiv : EntitySearcher.getAnnotationAssertionAxioms(namedIndiv, onto)){
				
				
				if (annIdiv.getAnnotation().getProperty().getIRI().toString().equals(rdf_label_uri)){
					
					return ((OWLLiteral)annIdiv.getAnnotation().getValue()).getLiteral().toLowerCase();
				}
			}
			return "";
			
			
		}
		catch (Exception e){
			//In case of error. Accessing an object in an expected way
			return "";
		}
		
	}
	
	/**
	 * FMA originalannotations annotations appear as datatype assertions
	 * @param entityAnnAx
	 * @return
	 */
	private String asNamedIndividualFMA(OWLAnnotationAssertionAxiom entityAnnAx, OWLOntology onto, OWLDataFactory datafactory){
		
		try{
			//It is an individual
			namedIndivIRI=(IRI)entityAnnAx.getAnnotation().getValue();
			
			namedIndiv=datafactory.getOWLNamedIndividual(namedIndivIRI);
			
			//for (OWLAnnotation indivAnn : namedIndiv.getAnnotations(onto)){
			for (OWLLiteral literal_syn : Searcher.values(
					onto.getDataPropertyAssertionAxioms(namedIndiv), datafactory.getOWLDataProperty(IRI.create(fma_name_uri)))){ 
				
			
				return literal_syn.getLiteral().toLowerCase();
			}
			
			return "";
			
		}
		catch (Exception e){
			//In case of error. Accessing an object in an expected way
			return "";
		}
		
	}

	
	/**
	 * We deal with some definitions. Long ones are discarded.
	 * We also process translated annotations
	 * @param def
	 * @return
	 */
	private Set<String> processLongLabels(String label){
		
		Set<String> annotation_labels = new HashSet<String>(); 
		
		String words[];
		
		if (label.length()<2){
			return annotation_labels;
		}
		
		//For translated labels: used now in ontology processing tranlateLabel method (Sept 2017)
		//Label is: trans1|trans2|...|transx
		if (label.indexOf("|")>=0){
			String[] elements = label.split("\\|");
			for (String e : elements){
				annotation_labels.add(e);
			}
			return annotation_labels;
		}
			
		if (label.indexOf(".")<0 || label.length()<15){ //It is not a definition
			annotation_labels.add(label);
			return annotation_labels;
		}
		
		//LogOutput.print("\nDEF 1: " + label);
		
		label = label.split("\\.")[0]; //we keep first sentence
		
		//LogOutput.print("\nDEF 2: " + label);
		
		words = label.split(" ");
		
		if (words.length>12){
			return annotation_labels; //empty set
		}
		
		//LogOutput.print("\nDEF 3: " + label);
		
		annotation_labels.add(label);
		return annotation_labels;
		
	}

	

}

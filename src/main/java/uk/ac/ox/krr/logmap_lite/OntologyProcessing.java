package uk.ac.ox.krr.logmap_lite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.search.EntitySearcher;
import org.semanticweb.owlapi.search.Searcher;

import uk.ac.ox.krr.logmap2.Parameters;
import uk.ac.ox.krr.logmap2.io.LogOutput;
import uk.ac.ox.krr.logmap2.utilities.Utilities;
import uk.ac.ox.krr.logmap_lite.io.ReadFile;

import org.semanticweb.owlapi.apibinding.OWLManager;

public class OntologyProcessing {

	/** Inverted file for weak correspondences */
	private Map<Set<String>, Set<Integer>> invertedFileExactLabels = new HashMap<Set<String>, Set<Integer>>();
	private Map<Set<String>, Set<Integer>> invertedFileExactLabels_dProp = new HashMap<Set<String>, Set<Integer>>();
	private Map<Set<String>, Set<Integer>> invertedFileExactLabels_oProp = new HashMap<Set<String>, Set<Integer>>();
	private Map<Set<String>, Set<Integer>> invertedFileExactLabels_indiv = new HashMap<Set<String>, Set<Integer>>();
	
	private Map<Set<String>, Set<Integer>> invertedFileWeakLabels = new HashMap<Set<String>, Set<Integer>>();
	private Map<Integer, Set<List<String>>> identifier2labels = new HashMap<Integer, Set<List<String>>>();
	
	
	/** List of OWLClass. Each position represents a class index*/
	private List<OWLClass> listofOWLClass = new ArrayList<OWLClass>();
	private List<OWLDataProperty> listofOWLDProp = new ArrayList<OWLDataProperty>();
	private List<OWLObjectProperty> listofOWLOProp = new ArrayList<OWLObjectProperty>();
	private List<OWLNamedIndividual> listofOWLIndividual = new ArrayList<OWLNamedIndividual>();
	
	
	protected String rdf_label_uri = "http://www.w3.org/2000/01/rdf-schema#label";
	public String rdf_comment_uri = "http://www.w3.org/2000/01/rdf-schema#comment";
	protected String synonym_iri ="http://oaei.ontologymatching.org/annotations#synonym";
	private String hasRelatedSynonym_uri = "http://www.geneontology.org/formats/oboInOwl#hasRelatedSynonym";
	private String hasExactSynonym_uri   = "http://www.geneontology.org/formats/oboInOwl#hasExactSynonym";
	private String hasExactSynonym_uri2   = "http://www.geneontology.org/formats/oboInOWL#hasExactSynonym";
	
	private String skos_pref_label  = "http://www.w3.org/2004/02/skos/core#prefLabel";
	private String skos_alt_label  = "http://www.w3.org/2004/02/skos/core#altLabel";
	
	
	private String name_dprop_im_uri = "http://oaei.ontologymatching.org/2012/IIMBTBOX/name";
	private String article_dprop_im_uri = "http://oaei.ontologymatching.org/2012/IIMBTBOX/article";
	private String has_value_dprop_im_uri = "http://www.instancematching.org/IIMB2012/ADDONS#has_value";
	
	private String article_oprop_im_uri = "http://www.instancematching.org/IIMB2012/ADDONS#article";
	private String name_oprop_im_uri = "http://www.instancematching.org/IIMB2012/ADDONS#name";
	
	//oaei2013
	//It may not be useful since numbers are not considered as good labels
	private String population_dprop_im_uri = "http://dbpedia.org/ontology/populationTotal";
	private String birthName_dprop_im_uri = "http://dbpedia.org/ontology/birthName";	
	
	private String label_oprop_im_uri = "http://www.instancematching.org/label";
	private String curriculum_oprop_im_uri = "http://www.instancematching.org/curriculum";
	private String places_oprop_im_uri = "http://www.instancematching.org/places";
	
	private String name_dprop_im_uri_2015 = "http://islab.di.unimi.it/imoaei2015#name";
	
	
	
	private OWLOntology onto;
	
	private Set<String> stopwordsSet = new HashSet<String>();
	
	private boolean extract_overlapping;
	
	private PrecomputeIndexCombination precomputeIndexCombination = new PrecomputeIndexCombination();
	
	public OntologyProcessing(OWLOntology ont, boolean extract_overlapping)  throws Exception{
		
		onto=ont;
		this.extract_overlapping=extract_overlapping;
		precomputeIndexCombination.preComputeIdentifierCombination();
		loadStopWords();
		
		processOntologyClassLabels();
		processOntologyObjPropLabels();
		processOntologyDataPropLabels();
		//New
		processOntologyIndividualsLabels();
		
		precomputeIndexCombination.clearStructures();
		
	}
	
	
	public void clearOntoloy(){
		onto=null;
	}
	
	
	/**
	 * This method will also associate an index to each class
	 */
	private void processOntologyClassLabels() {
		
		
		
		Set<String> words=new HashSet<String>();
		
		int ident=0;
		
		//CLASSES
		//First we give a identifier to each class
		for (OWLClass cls : onto.getClassesInSignature(true)){ //Also imports
			
			if (!cls.isTopEntity() && !cls.isBottomEntity()){
				
				listofOWLClass.add(cls);
				if (extract_overlapping){
					identifier2labels.put(ident, new HashSet<List<String>>());
				}
				
				//We extract a fix number of labels (at most).
				for (String label : extractLabels4OWLEntity(cls, 6)){
				
					words.addAll(cleanLabel(label));
					
					if (words.size()>0){
							
						if (!invertedFileExactLabels.containsKey(words))
							invertedFileExactLabels.put(new HashSet<String>(words), new HashSet<Integer>());
							
						invertedFileExactLabels.get(words).add(ident);
						
						if (extract_overlapping){
							if (!invertedFileWeakLabels.containsKey(words))
								invertedFileWeakLabels.put(new HashSet<String>(words), new HashSet<Integer>());
							
							invertedFileWeakLabels.get(words).add(ident);				
						
							identifier2labels.get(ident).add(new ArrayList<String>(words));
						}
						
					}			
						
					//init
					words.clear();
					
				}//for labels
				
				ident++;
				
			}//if top-bottom
			
		}//for classes
		
		//System.out.println("\t\tClasses index: " + listofOWLClassIRIs.size());
	
		
		
		if (extract_overlapping){
			setInvertedFileWeak4Overlapping();
		}
		
	}
	
	
	/**
	 * This method will also associate an index to each dprop
	 */
	private void processOntologyDataPropLabels() {
		
		
		
		Set<String> words=new HashSet<String>();
		
		int ident=0;
		
		//DATA Prop
		//First we give a identifier to each class
		for (OWLDataProperty dProp : onto.getDataPropertiesInSignature(true)){ //also imports
			
			if (!dProp.isTopEntity() && !dProp.isBottomEntity()){
				
				listofOWLDProp.add(dProp);
				
				//We extract a fix number of labels (at most).
				for (String label : extractLabels4OWLEntity(dProp, 1)){
				
					words.addAll(cleanLabel(label));
					
					if (words.size()>0){
							
						if (!invertedFileExactLabels_dProp.containsKey(words))
							invertedFileExactLabels_dProp.put(new HashSet<String>(words), new HashSet<Integer>());
							
						invertedFileExactLabels_dProp.get(words).add(ident);
						
					}			
						
					//init
					words.clear();
					
				}//for labels
				
				ident++;
				
			}//if top-bottom
			
		}//for dProp
	}
	
	
	/**
	 * This method will also associate an index to each dprop
	 */
	private void processOntologyObjPropLabels() {
		
		Set<String> words=new HashSet<String>();
		
		int ident=0;
		
		//Object PROP
		//First we give a identifier to each class
		for (OWLObjectProperty oProp : onto.getObjectPropertiesInSignature(true)){ //also imports
			
			if (!oProp.isTopEntity() && !oProp.isBottomEntity()){
				
				listofOWLOProp.add(oProp);
				
				//We extract a fix number of labels (at most).
				for (String label : extractLabels4OWLEntity(oProp, 1)){
				
					words.addAll(cleanLabel(label));
					
					if (words.size()>0){
							
						if (!invertedFileExactLabels_oProp.containsKey(words))
							invertedFileExactLabels_oProp.put(new HashSet<String>(words), new HashSet<Integer>());
							
						invertedFileExactLabels_oProp.get(words).add(ident);
						
					}			
						
					//init
					words.clear();
					
				}//for labels
				
				ident++;
				
			}//if top-bottom
			
		}//for dProp
	}
	

	
	
	
	private void processOntologyIndividualsLabels(){
		
		//TODO
		//Intersect IF
		
		int ident=0;
		
		
		Set<String> words=new HashSet<String>();
		
		ExtractAcceptedLabelsFromRoleAssertions indivLabelExtractor =
				new ExtractAcceptedLabelsFromRoleAssertions();

		
		for (OWLNamedIndividual indiv : onto.getIndividualsInSignature(true)){//also imports
		
			listofOWLIndividual.add(indiv);
			
			//Ignore them
			if (indivLabelExtractor.isDummyIndividual(indiv)){
				//LogOutput.printAlways("DUMMY individual: " + indiv.getIRI().toString());
				ident++;
				continue;
			}
			
			
			
			
			for (String label : indivLabelExtractor.extractLexiconFromRoleAssertions(indiv)){
				
				words.addAll(cleanLabel(label));
				
				if (words.size()>0){
						
					if (!invertedFileExactLabels_indiv.containsKey(words))
						invertedFileExactLabels_indiv.put(new HashSet<String>(words), new HashSet<Integer>());
						
					invertedFileExactLabels_indiv.get(words).add(ident);
					
				}			
					
				//init
				words.clear();
				
			}//for labels
			
			
			ident++;
			
		}//for indiv
		
		
	}
	
	
	
	
	
	public Map<Set<String>, Set<Integer>> getInvertedFileExact(){
		return invertedFileExactLabels;
	}
	
	public Map<Set<String>, Set<Integer>> getInvertedFileExactIndividuals(){
		return invertedFileExactLabels_indiv;
	}
	
	public Map<Set<String>, Set<Integer>> getInvertedFileExactDataProp(){
		return invertedFileExactLabels_dProp;
	}
	
	public Map<Set<String>, Set<Integer>> getInvertedFileExactObjectProp(){
		return invertedFileExactLabels_oProp;
	}
	
	public Map<Set<String>, Set<Integer>> getWeakInvertedFile(){
		return invertedFileWeakLabels;
	}
	
	
	public String getIRI4identifier(int ident){
		return listofOWLClass.get(ident).getIRI().toString();		
	}
	
	public String getIRI4DPropIdentifier(int ident){
		return listofOWLDProp.get(ident).getIRI().toString();		
	}
	
	public String getIRI4OPropIdentifier(int ident){
		return listofOWLOProp.get(ident).getIRI().toString();		
	}
	
	public String getIRI4Individual(int ident){
		return listofOWLIndividual.get(ident).getIRI().toString();		
	}
	
	public String getLabel4identifier(int ident){
		return Utilities.getEntityLabelFromURI(listofOWLClass.get(ident).getIRI().toString());		
	}
	

	public OWLClass getOWLClass4identifier(int ident){
		return listofOWLClass.get(ident);		
	}
	
	
	
	
	public void clearStructures(){
		invertedFileExactLabels.clear();
		invertedFileExactLabels_indiv.clear();
		invertedFileExactLabels_dProp.clear();
		invertedFileExactLabels_oProp.clear();
		invertedFileWeakLabels.clear();
		listofOWLClass.clear();
		listofOWLDProp.clear();
		listofOWLOProp.clear();
		stopwordsSet.clear();
	}
	

	
	private Set<String> extractLabels4OWLEntity(OWLEntity ent, int max_size){
	
		Set<String> labels = new HashSet<String>(); 
		OWLAnonymousIndividual geneid_value;
		
		
		//We look for label first
		for (OWLAnnotationAssertionAxiom annAx : EntitySearcher.getAnnotationAssertionAxioms(ent, onto)){
					
			if (annAx.getAnnotation().getProperty().getIRI().toString().equals(rdf_label_uri) ||
					annAx.getAnnotation().getProperty().getIRI().toString().equals(synonym_iri) ||
					annAx.getAnnotation().getProperty().getIRI().toString().equals(skos_pref_label) ||
					annAx.getAnnotation().getProperty().getIRI().toString().equals(skos_alt_label)){
						
				labels.add(((OWLLiteral)annAx.getAnnotation().getValue()).getLiteral()); //No lower case yet
				
				
			}
			
			//Annotations in original Mouse Anatomy and NCI Anatomy
			//---------------------------------------------
			else if (annAx.getAnnotation().getProperty().getIRI().toString().equals(hasRelatedSynonym_uri) ||
					annAx.getAnnotation().getProperty().getIRI().toString().equals(hasExactSynonym_uri) ||
					annAx.getAnnotation().getProperty().getIRI().toString().equals(hasExactSynonym_uri2))
					{
				
				try{
									
					//It is an individual
					
					if (annAx.getAnnotation().getValue() instanceof OWLAnonymousIndividual) {
									
					
						geneid_value=((OWLAnonymousIndividual)annAx.getAnnotation().getValue()).asOWLAnonymousIndividual();//.getID()
						
						//System.out.println(annAx.getAnnotation().getValue());
						
						for (OWLAnnotationAssertionAxiom annGeneidAx : onto.getAnnotationAssertionAxioms(geneid_value)){
		
							
							if (annGeneidAx.getAnnotation().getProperty().getIRI().toString().equals(rdf_label_uri)){
								
								labels.add(((OWLLiteral)annGeneidAx.getAnnotation().getValue()).getLiteral().toString());//.toLowerCase();
								
														
							}
						
						}
					}
					//Named indiv
					else if (annAx.getAnnotation().getValue() instanceof IRI) {
						
						
						//It is an individual
						IRI namedIndivIRI=(IRI)annAx.getAnnotation().getValue();				
						OWLNamedIndividual namedIndiv=onto.getOWLOntologyManager().getOWLDataFactory().getOWLNamedIndividual(namedIndivIRI);
						
						
						for (OWLAnnotationAssertionAxiom annIdiv : EntitySearcher.getAnnotationAssertionAxioms(namedIndiv, onto)){
							
							
							if (annIdiv.getAnnotation().getProperty().getIRI().toString().equals(rdf_label_uri)){
								
								labels.add(((OWLLiteral)annIdiv.getAnnotation().getValue()).getLiteral().toLowerCase());
							}
						}
					
					}								
					
					
					
				}
			
				//Consider other cases with direct value or named individual
				catch (Exception e){
					
					//e.printStackTrace();
					try {
						
						//System.out.println(annAx.getAnnotation().getValue());
						
						labels.add(((OWLLiteral)annAx.getAnnotation().getValue()).getLiteral()); //No lower case yet
					}
					catch (Exception e1) {
						//e1.printStackTrace();
						System.err.println("Error accessing annotation: hasRelatedSynonym_uri or hasExactSynonym_uri");
					}
				}
				
			}
			
			if (labels.size()>=max_size)
				break;		
		}
		
		
		//If it doesn't exist any label then we use entity name
		if (labels.isEmpty()){
			labels.add(Utilities.getEntityLabelFromURI(ent.getIRI().toString()));
			
		}
		
		return labels;
		
	}
	
	
	/*private String getEntityLabelFromURI(String uriStr){
		//System.err.println("Error processing URI: "+ uriStr);
		try{
			if (uriStr.indexOf("#")>=0 && uriStr.indexOf("#")<uriStr.length()-1)
				return uriStr.split("#")[1];
		}
		catch (Exception e){
			System.err.println("Error processing URI: "+ uriStr);
		}
		return uriStr;
	}*/
	
	
	private String[] splitStringByCapitalLetter(String str){
		
		String pattern =

	        "(?<=[^\\p{Upper}])(?=\\p{Upper})"
	        // either there is anything that is not an uppercase character
	        // followed by an uppercase character

	        + "|(?<=[\\p{Lower}])(?=\\d)"
	        // or there is a lowercase character followed by a digit

	        //+ "|(?=\\d)(?<=[\\p{Lower}])"
	        ;

		return str.split(pattern);
		//return str.split("(?=\\p{Upper})");
		
	}
	
	
	private List<String> cleanLabel(String label_value){
		
		List<String> cleanWords=new ArrayList<String>();
		
		String[] words;
				
		label_value=label_value.replace(",", "");
		
		if (label_value.indexOf("_")>0){ //NCI and SNOMED
			words=label_value.split("_");
		}
		else if (label_value.indexOf(" ")>0){ //FMA
			words=label_value.split(" ");
		}
		//Split capitals...
		else{
			words=splitStringByCapitalLetter(label_value);
		}
		//else {
		//	words=new String[1];
		//	words[0]=label_value;
		//}
		
		//To lowercase
		
		
		for (int i=0; i<words.length; i++){
			
			words[i]=words[i].toLowerCase(); //to lower case
			
			if (words[i].length()>0){
			
				if (!getStopwordsSet().contains(words[i])){
				//if (!LexicalUtilities.getStopwordsSetExtended().contains(words[i])){
					//words[i].length()>2 &&  Not for exact IF: it may contain important numbers					
					cleanWords.add(words[i]);
				}				
			}			
		}
		
		
		
		return cleanWords;
		
	}
	
	
	private void setInvertedFileWeak4Overlapping(){
		
		setInvertedFileWeakLabels();
		
		identifier2labels.clear();
				
		//System.out.println("\t\tSize inverted file: " + invertedFileWeakLabels.size());
		
	}
	
	
	
	private void setInvertedFileWeakLabels(){
		
		//List<String> words;
		int max_size_labels=6;
		int max_size_words_missing=2;
		
		//for (int ident=0; ident < listofClassLabels.size(); ident++){
		for (int ident : identifier2labels.keySet()){
		
			//words = listofClassLabels.get(ident);
			for (List<String> words : identifier2labels.get(ident)){
		
			
				if (words.size()>max_size_labels)
					continue;
			
				if (words.size()>1){ //Smaller case 1 out of 2
					
					createWeakLabels4Identifier(words, ident, 1);// 1 missing word
				
					if (words.size()>3 && max_size_words_missing>1){ //Smaller case 2 out of 4
					
						createWeakLabels4Identifier(words, ident, 2);
					
						if (words.size()>5 && max_size_words_missing>2){ //Smaller case 3 out of 6
					
							createWeakLabels4Identifier(words, ident, 3);
					
							if (words.size()>7 && max_size_words_missing>3){ //Smaller case 4 out of 8
					
								createWeakLabels4Identifier(words, ident, 4);
							}
						}
					}
				
				}
			}
		}
		
		
	}
	
	
	
	
	 /** Combines the words in given list with 'x' missing words and stores the results in IF
	 * 
	 * @param cleanWords Clean label of concept
	 * @param ident Identifier of concepts
	 * @param missing_words Number of words to be discarded 
	 */
	private void createWeakLabels4Identifier(List<String> cleanWords, int ident, int missing_words){
		
		Set<String> combo = new HashSet<String>();
		
		//Fills identifierCombination
		
		//Set<Set<Integer>> combination_set = getIdentifierCombination(cleanWords.size(), missing_words);
		Set<Set<Integer>> combination_set = precomputeIndexCombination.getIdentifierCombination(cleanWords.size(), missing_words);
		
		for(Set<Integer> toExclude : combination_set){
			
			for (int pos=0; pos<cleanWords.size(); pos++){
				if (!toExclude.contains(pos))
					combo.add(cleanWords.get(pos));
				
			}
			
			if (!invertedFileWeakLabels.containsKey(combo))
				invertedFileWeakLabels.put(new HashSet<String>(combo), new HashSet<Integer>());
			
			invertedFileWeakLabels.get(combo).add(ident);
			
			combo.clear();
			
		}
		
		
	}
	
	
	
	private void loadStopWords() throws Exception{
		
		String line;
		
		ReadFile reader = new ReadFile(OntologyProcessing.class.getResourceAsStream("stopwords.txt"));
		
		while ((line = reader.readLine()) != null){
			if (!line.startsWith("#"))
				stopwordsSet.add(line);
		}
		reader.closeBuffer();
		
	}
	
	
	private Set<String> getStopwordsSet(){
		return stopwordsSet;	
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 
	 * Manages the extraction of the lexicon associated to an Individual that s presented as data asserion axioms 
	 * or object asssertion axioms. Currently is based on the IM track of the OAEI 2012
	 * 
	 * @author Ernesto
	 *
	 */
	private class ExtractAcceptedLabelsFromRoleAssertions{
		
	
		Set<String> lexiconValues4individual = new HashSet<String>();
		Set<String> accepted_data_uris = new HashSet<String>();
		Set<String> accepted_obj_uris = new HashSet<String>();
		Set<String> accepted_data_uris_deep2 = new HashSet<String>();
		String label_value;
		
		int max_size_name_label=0;
		int min_size_name_label=5000;
		
		
		ExtractAcceptedLabelsFromRoleAssertions(){
			
			
			
			accepted_data_uris.add(article_dprop_im_uri);
			accepted_data_uris.add(name_dprop_im_uri);
			accepted_data_uris.add(birthName_dprop_im_uri);
			accepted_data_uris.add(population_dprop_im_uri);
			
			accepted_data_uris.add(name_dprop_im_uri_2015);
			
			
			
			accepted_obj_uris.add(article_oprop_im_uri);
			accepted_obj_uris.add(name_oprop_im_uri);
			accepted_obj_uris.add(label_oprop_im_uri);
			accepted_obj_uris.add(places_oprop_im_uri);
			accepted_obj_uris.add(curriculum_oprop_im_uri);
			
			
			
			accepted_data_uris_deep2.add(has_value_dprop_im_uri);
			
		}
		

		
		protected Set<String> extractLexiconFromRoleAssertions(OWLNamedIndividual indiv){				
			
			lexiconValues4individual.clear();
			
			
			//Add label name
			label_value = Utilities.getEntityLabelFromURI(indiv.getIRI().toString());
			if (label_value.length()>2){
				lexiconValues4individual.add(label_value);
			}
			
			
			
			//We also add from rdfs:comments or rdfs:label
			//-----------------------------------------
			//Since the comments may be long we need to pre-process them
			for (OWLAnnotationAssertionAxiom indivAnnAx : EntitySearcher.getAnnotationAssertionAxioms(indiv, onto)){
				
								
				String uri_ann = indivAnnAx.getAnnotation().getProperty().getIRI().toString();
				
				
				if (rdf_comment_uri.equals(uri_ann) || rdf_label_uri.equals(uri_ann)){
					
					try{
						label_value = processLabel(
								((OWLLiteral)indivAnnAx.getAnnotation().getValue()).getLiteral().toLowerCase());
					}
					catch (Exception e){
						//In case of error.
						label_value =  "";
					}
					
					//Statistics
					if (label_value.length() > max_size_name_label){
						max_size_name_label=label_value.length();
					}
					
					if (label_value.length()>0 && label_value.length() < min_size_name_label){
						min_size_name_label=label_value.length();
					}
					
					if (label_value.length()>2){
						lexiconValues4individual.add(label_value);
					}
					
					
				}
				
			}//end extraction of comments
			
			
			
			//Datatype assertion
			for (String uri_indiv_ann : accepted_data_uris){
			
				
				for (OWLLiteral assertion_value : Searcher.values(
						onto.getDataPropertyAssertionAxioms(indiv), OWLManager.getOWLDataFactory().getOWLDataProperty(IRI.create(uri_indiv_ann)))
				){
					
					
					//LogOutput.print(indiv.getIRI().toString());
					
					label_value = processLabel(
							assertion_value.getLiteral().toLowerCase());
					
					//Statistics
					if (label_value.length() > max_size_name_label){
						max_size_name_label=label_value.length();
					}
					
					if (label_value.length()>0 && label_value.length() < min_size_name_label){
						min_size_name_label=label_value.length();
					}
					
					if (label_value.length()>2){
						lexiconValues4individual.add(label_value);
					}
					
				}
			}
			
			//OBject property assertions deep 1
			//-------------------------------------
			for (String uri_indiv_ann_deep1 : accepted_obj_uris){
				
				
				
				
				
				for (OWLIndividual assertion_value_indiv : Searcher.values(
						onto.getObjectPropertyAssertionAxioms(indiv), OWLManager.getOWLDataFactory().getOWLObjectProperty(IRI.create(uri_indiv_ann_deep1)))){
					
					//We only consider named individuals
					if (assertion_value_indiv.isNamed()){
						
						//Datatype assertion deep 2: has_value and others
						//----------------------------------------
						for (String uri_indiv_ann_deep2 : accepted_data_uris_deep2){
						
							
							
							for (OWLLiteral assertion_value_deep2 : Searcher.values(
									onto.getDataPropertyAssertionAxioms(assertion_value_indiv.asOWLNamedIndividual()), 
									OWLManager.getOWLDataFactory().getOWLDataProperty(IRI.create(uri_indiv_ann_deep2)))){
								
								
								label_value = processLabel(
										assertion_value_deep2.getLiteral().toLowerCase());
								
								//Statistics
								if (label_value.length() > max_size_name_label){
									max_size_name_label=label_value.length();
								}
								
								if (label_value.length()>2){
									lexiconValues4individual.add(label_value);
								}
								
							}
						}//end data assertions
						
						//Extract comment level 2
						//---------------------
						for (OWLAnnotationAssertionAxiom indivAnnAx_level2 : EntitySearcher.getAnnotationAssertionAxioms(assertion_value_indiv.asOWLNamedIndividual(), onto)){
							
							
							String uri_ann = indivAnnAx_level2.getAnnotation().getProperty().getIRI().toString();
							
							
							if (rdf_comment_uri.equals(uri_ann)){
								
								try{
									label_value = processLabel(
											((OWLLiteral)indivAnnAx_level2.getAnnotation().getValue()).getLiteral().toLowerCase());
								}
								catch (Exception e){
									//In case of error.
									label_value =  "";
								}
								
								//Statistics
								if (label_value.length() > max_size_name_label){
									max_size_name_label=label_value.length();
								}
								
								if (label_value.length()>0 && label_value.length() < min_size_name_label){
									min_size_name_label=label_value.length();
								}
								
								if (label_value.length()>2){
									lexiconValues4individual.add(label_value);
								}
								
								
							}
							
						}//end extraction of comments
						
					}
					
				}
			}
			
			
			return lexiconValues4individual;
		}

	
		/**
		 * 
		 * @param value
		 * @return Empty string if it is not valid
		 */
		private String processLabel(String value){
					
			String processedLabel="";
			
			//Reg expression to split text: //Split up to "&", ".", "(", "," ";", is, was, are, were
			//String reg_ex_split = "[&\\.,;(]";
			String reg_ex_split="[&\\,;(/]|(\\s)is(\\s)|(\\s)are(\\s)|(\\s)was(\\s)|(\\s)were(\\s)|(\\s)est(\\s)|(\\s)fut(\\s)|(\\s)un(\\s)|(\\s)a(\\s)|(\\s)an(\\s)";
			//No filter by point : St. Georges or St. John
			
			//Removing annoying acronyms
			//order of the British empire
			//processedLabel = value.replaceAll("obe", "");
			//fellow royal society
			//processedLabel = processedLabel.replaceAll("frs", "");
			
			int manegeable_lenght = 65;
			
			//short data assertion strings
			if (processedLabel.length()<=manegeable_lenght && !processedLabel.contains("<p>") && !processedLabel.contains("</p>")){
				
				//We still want to split and trim
				//we keep/split string up to the given character
				processedLabel = processedLabel.split(reg_ex_split)[0];
				//we remove white spaces at the end and begining of label
				processedLabel = processedLabel.trim();
				
				
				if (!isGoodLabel(processedLabel)){
					//LogOutput.print("Filtered: "+ value);
					return ""; //bad label
				}
				else{
					//LogOutput.print("GOOD: "+ value);
					return processedLabel;
				}
				
			}
			//Text with several paragraphs -> filter
			else {
				
				//Detect if it starts with <p> if not then it has been split 
				if (processedLabel.startsWith("<p>")){
					
					processedLabel = processedLabel.split("<p>")[1];
					
					//we keep/split string up to the given character
					processedLabel = processedLabel.split(reg_ex_split)[0];
					
					//we remove white spaces ate the endand beginning oflabel
					processedLabel = processedLabel.trim();
					
					if (processedLabel.length()<=manegeable_lenght){
						
						if (isGoodLabel(processedLabel)){
							//LogOutput.print("GOOD: "+ processedLabel);
							return processedLabel;
						}
					}
					
					//LogOutput.print("Filtered: "+ processedLabel);
					return "";
					
				}
				else {
					
					//For comments in RDFT oaei 2013. They do not contain <p>
					
					//we keep/split string up to the given character
					processedLabel = processedLabel.split(reg_ex_split)[0];
					
					//we remove white spaces at the end and begining of label
					processedLabel = processedLabel.trim();
					
					if (processedLabel.length()<=manegeable_lenght){
						
						if (isGoodLabel(processedLabel)){
							//LogOutput.printAlways("GOOD: "+ processedLabel);
							return processedLabel;
						}
						else{
							LogOutput.printAlways("BAD: "+ processedLabel);
						}
					}
					else{
						
						processedLabel = processedLabel.substring(0, manegeable_lenght);
						
						if (isGoodLabel(processedLabel)){
							//LogOutput.printAlways("REDUCED label 0-"+manegeable_lenght+": "+ processedLabel);
							return processedLabel;
						}
						
					}
					
					LogOutput.printAlways("Filtered: "+ processedLabel.substring(0, 50));
					
					
					
					return "";
				}
				
				
			}//if lenght
					
			
		}
		
		
		
		private boolean isGoodLabel(String label){
			
			//REGULAR EXPRESSIONS
			
			//we do not consider "y" as consonant for filtering purposes. After all "y" may have vowel phonetics.
			String consonant_regex = "[b-df-hj-np-tv-xz]";
			String more3_consonants_regex = consonant_regex + consonant_regex + consonant_regex + consonant_regex + "+";
			String more5_consonants_regex = consonant_regex + consonant_regex + consonant_regex + consonant_regex + consonant_regex + consonant_regex + "+";
			String vowel_regex = "[aeiou]"; //accented?? so far we are considering only "english"
			String more3_vowels_regex =  vowel_regex + vowel_regex + vowel_regex + vowel_regex + "+";
			String same_character_3_times = ".*(.)\\1\\1.*";
			//String space_character_3_times = ".*([ \t])\\1\\1.*";
			String space_character_3_times = ".*(\\s)\\1\\1.*";
			
			
			String[] words;
			
			//Detect if label has been randomly generated
						
			if (label.length()<3){ //very short strings
				return false;
			}
			
			if (label.contains("!") || label.contains("?"))
				return false;

			
			if (label.matches(space_character_3_times))
				return false;


						
			//Split in words
			words = label.split(" ");
			
			//At least one word with size >1
			boolean has_min_size_word = false;
			
			//has_min_size_word=true;
			
			for (String word : words){
				
				word = word.toLowerCase();
				//Accept roman numbers NOt in LogMap Lite
				//if (NormalizeNumbers.getRomanNumbers10().contains(word)){ 
				//	continue;
				//}
				//to also avoid : or other characteres after a rman number
				//if (word.length()>1 && NormalizeNumbers.getRomanNumbers10().contains(word.substring(0, word.length()-1))){					
					//LogOutput.print(word + " " + word.substring(0, word.length()-1) + " " + word.substring(0, word.length()-2));
				//	continue;
				//}
				
				if (word.equals("st")) //st johns
					continue;
				
				
				//Single characters different from "a"
				if (word.length()<2){ //if any of the contained words is a single character
					if (word.equals("a")){
						//LogOutput.print(word);
						continue;
					}
					return false;					
				}
				else{
					has_min_size_word=true;
				}
				
				
				
				if (word.matches(same_character_3_times)){ //Any character occurring three or more times together
					return false;
				}
				
				
				
				//Strange words
				//In english it is very rare to have more than 4 consonant together (e.g. angsts). In compounds words 5 it is possible (e.g. handspring)
				//Starting the word cluster is three consonants is the maximum number, as in "split".
				//More than 3 vowels together is also strange
				if (!word.startsWith("mc") && word.matches(more3_consonants_regex + ".*")){ //Starting with more thn 3 cons. //Mc surname like McGregor include
					return false;
				}
				if (word.matches(".*" + more5_consonants_regex + ".*")){ //more than 5 inside word
					return false;
				}
				if (word.matches(".*" + more3_vowels_regex + ".*")){ //more than three vowels
					return false;
				}
				//only consonants or only vowels
				if (word.matches(consonant_regex + "+") || word.matches(vowel_regex + "+")){
					return false;
				}				
			}
			
			if (!has_min_size_word)
				return false;
			
			
			return true; 		
			
		}	
		
		
		
		protected boolean isDummyIndividual(OWLNamedIndividual indiv){
			
			OWLObjectPropertyAssertionAxiom opaa;
			String prop_uri;
			
			
			//Check for oject property assertions deep 1 referenceing given individual
			//-------------------------------------------------------------------------
			//If referenced it is a dummy individual which should not be considered in the matching				
			for (OWLAxiom refAx : onto.getReferencingAxioms(indiv, true)){
				
				if (refAx instanceof OWLObjectPropertyAssertionAxiom){
					
					opaa = (OWLObjectPropertyAssertionAxiom)refAx;
					
					//Not the searched individual
					if (opaa.getObject().isAnonymous())
						continue;
					
					//Not the searched individual as assertion object
					if (!indiv.equals(opaa.getObject().asOWLNamedIndividual()))
						continue; //with next axiom
					
					//Check if object property is the used for dummy individuals 
					if (!opaa.getProperty().isAnonymous()){
						
						prop_uri = opaa.getProperty().asOWLObjectProperty().getIRI().toString();
						
						for (String op4indiv : Parameters.accepted_object_assertion_URIs_for_individuals){
							
							//The property is the one
							if (prop_uri.equals(op4indiv)){
								return true;
							}
						
						}	
						
					}
					
				
				}
				
			}
			
			return false;
			
			
			
		}
	
	
		
	}//end class


	
	


		
	
	

}

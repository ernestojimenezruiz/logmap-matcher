/*******************************************************************************
 * Copyright 2012 by the Department of Computer Science (University of Oxford)
 * 
 *    This file is part of LogMap.
 * 
 *    LogMap is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU Lesser General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 * 
 *    LogMap is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU Lesser General Public License for more details.
 * 
 *    You should have received a copy of the GNU Lesser General Public License
 *    along with LogMap.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package uk.ac.ox.krr.logmap2.overlapping;

import java.util.ArrayList;



import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.parameters.Imports;
import org.semanticweb.owlapi.search.EntitySearcher;

import uk.ac.ox.krr.logmap2.lexicon.LexicalUtilities;
import uk.ac.ox.krr.logmap2.utilities.Utilities;
import uk.ac.ox.krr.logmap2.utilities.PrecomputeIndexCombination;
import uk.ac.ox.krr.logmap2.indexing.ExtractStringFromAnnotationAssertionAxiom;
import uk.ac.ox.krr.logmap2.io.LogOutput;

/**
 * This class will process the class labels and create weak inverted files.
 * 
 *
 * @author Ernesto Jimenez-Ruiz
 * Created: Sep 7, 2011
 *
 */
public class OntologyProcessing4Overlapping {
	
	/** Inverted file for weak correspondences */
	protected Map<Set<String>, Set<Integer>> invertedFileWeakLabels = new HashMap<Set<String>, Set<Integer>>();
	//protected Map<Set<String>, Set<Integer>> invertedFileWeakLabelsL2 = new HashMap<Set<String>, Set<Integer>>();
	//protected Map<Set<String>, Set<Integer>> invertedFileWeakLabelsL3 = new HashMap<Set<String>, Set<Integer>>();
	
	/** List of OWLClasses. Each position represents index*/
	//List<OWLClass> listofOWLClass = new ArrayList<OWLClass>();
	Map<Integer, OWLClass> identifier2class = new HashMap<Integer, OWLClass>();
	
	
	Map<OWLClass, Integer> class2identifier = new HashMap<OWLClass, Integer>();
	
	
	//List<List<String>> listofClassLabels = new ArrayList<List<String>>(); //clean class labels (list-set)
	
	private Map<Integer, Set<List<String>>> identifier2stemmedlabels = new HashMap<Integer, Set<List<String>>>();
	
	//Map<Integer, String> ident2label = new HashMap<Integer, String>(); //clean class str labels
	
	
	protected String rdf_label_uri = "http://www.w3.org/2000/01/rdf-schema#label";
	protected String synonym_iri ="http://oaei.ontologymatching.org/annotations#synonym";
	
	OWLOntology onto;
	
		
	int max_size_labels=7; //6
	int max_size_words_missing=2; //2

	private PrecomputeIndexCombination precomputeIndexCombination = new PrecomputeIndexCombination();
	
	private LexicalUtilities lexicalUtilities;
	
	private boolean full_overlapping;
	private boolean use_class2identifier_index;
	
	private ExtractStringFromAnnotationAssertionAxiom annotationExtractor = new ExtractStringFromAnnotationAssertionAxiom();
	
	
	protected int ident=0;
	
	public OntologyProcessing4Overlapping(OWLOntology ont, LexicalUtilities lexicalUtilities, boolean full_overlapping, boolean use_class2identifier_index){
		this(ont, lexicalUtilities, full_overlapping, use_class2identifier_index, 0);
		
	}
	
	public OntologyProcessing4Overlapping(OWLOntology ont, LexicalUtilities lexicalUtilities, boolean full_overlapping, boolean use_class2identifier_index, int init_index){
		
		onto=ont;
		
		this.lexicalUtilities=lexicalUtilities;
		this.full_overlapping=full_overlapping;
		this.use_class2identifier_index=use_class2identifier_index;
		
		
		this.ident = init_index;
		
		
		//We precompute indexes
		 //precomputeIndexCombination.clearCombinations(); //Old calls
		 precomputeIndexCombination.preComputeIdentifierCombination();
		
		
	}
	
	
	public void clearOntoloy(){
		onto=null;
	}

	
	
	public int getLastidentifier(){
		return ident;
	}
	
	
	
	/**
	 * This methdos will also associate an index to each class
	 */
	public void processOntologyClassLabels(){
		
		Set<String> words=new HashSet<String>();
		Set<String> stemmed_words=new HashSet<String>();
		
		//int ident=0;
		
		
		//CLASSES
		//First we give a identifier to each class
		for (OWLClass cls : onto.getClassesInSignature(Imports.INCLUDED)){
			
			if (!cls.isTopEntity() && !cls.isBottomEntity()){
				
				//listofOWLClass.add(cls);
				identifier2class.put(ident,cls);
				
				if (use_class2identifier_index)
					class2identifier.put(cls, ident);
				
				//Extract labels
				//listofClassLabels.add(extractCleanLabel4OWLCls(cls));
				
				//We add labels to IF (exact and stemmed)
				
				//Fill words @depresecated
				//words.addAll(extractCleanLabel4OWLCls(cls));
				
				identifier2stemmedlabels.put(ident, new HashSet<List<String>>());
				
				
				//We extract a fix number of labels... not all to be faster
				//2-3 is already good extracting an overlapping with 98\% of recall
				for (String label : extractLabels4OWLCls(cls)){
				
					words.addAll(cleanLabel(label));
				
					//words=clean labels
					
					if (words.size()>0){
							
						if (!invertedFileWeakLabels.containsKey(words))
							invertedFileWeakLabels.put(new HashSet<String>(words), new HashSet<Integer>());
							
						invertedFileWeakLabels.get(words).add(ident);				
					}			
					
					
					//Fill stemming
					for (String str : words){
						stemmed_words.add(lexicalUtilities.getStemming4Word(str));
					}
				
					if (stemmed_words.size()>0){
						
						if (!invertedFileWeakLabels.containsKey(stemmed_words))
							invertedFileWeakLabels.put(new HashSet<String>(stemmed_words), new HashSet<Integer>());
						
						invertedFileWeakLabels.get(stemmed_words).add(ident);				
					}
					
					//listofClassLabels.add(ident, new ArrayList<String>(stemmed_words));
					identifier2stemmedlabels.get(ident).add(new ArrayList<String>(stemmed_words));
					
					//init
					words.clear();
					stemmed_words.clear();
					
				}//for labels
				
				ident++;
				
			}//if top-bottom
			
		}//for classes
		
		LogOutput.print("\t\tClasses index: " + identifier2class.keySet().size());//listofOWLClass.size());
		//LogOutput.print("\t\tClasses labels: " + listofClassLabels.size());
		LogOutput.print("\t\tClasses labels: " + identifier2stemmedlabels.size());//counts only one
		
	
	}
	
	
	
	public void setInvertedFile4Overlapping(){
		
		//Done while processing lavels
		//setInvertedFileExactLabels();
		//setInvertedFileStemmedLabels();
		
		setInvertedFileWeakLabels();
		
		//listofClassLabels.clear(); //Not necessary anymore
		identifier2stemmedlabels.clear();
		
				
		LogOutput.print("\t\tSize inverted file: " + invertedFileWeakLabels.size());
		
	}
	
	
	public Map<Set<String>, Set<Integer>> getWeakInvertedFile(){
		return invertedFileWeakLabels;
	}
	
	
	public OWLClass getClass4identifier(int ident){
		//return listofOWLClass.get(ident);
		return identifier2class.get(ident);
	}
	
	public int getIdentifier4Class(OWLClass cls){
		if (class2identifier.containsKey(cls))
			return class2identifier.get(cls);
		return -1;
	}
	
	/*public String getLabel4identifier(int ident){
		
		if (ident2label.containsKey(ident))
			return ident2label.get(ident);
		
		
		String label="";
		
		for (String str : listofClassLabels.get(ident)){
			label+=str;
		}
		
		ident2label.put(ident, label);
		
		return label;
		
	}*/
	
	
	public void clearClass2Identifier(){
		class2identifier.clear();
	}
	
	
	public void clearStructures(){
		invertedFileWeakLabels.clear();
		//listofOWLClass.clear();
		identifier2class.clear();
		class2identifier.clear();
		//listofClassLabels.clear();
		identifier2stemmedlabels.clear(); //already removed before
		//ident2label.clear();
		//precomputedCombinations.clear();
	}
	
	
	
	private Set<String> extractLabels4OWLCls(OWLClass cls){
		
		if (full_overlapping)
			return extractAllLabels4OWLCls(cls, 20);
		else
			return extractLimitedLabels4OWLCls(cls, 3);
				
	}

		
	/**
	 * Extracts only a limited number of labels (in rdf_label) 
	 * @param cls
	 * @param max_size
	 * @return
	 */
	private Set<String> extractLimitedLabels4OWLCls(OWLClass cls, int max_size){
	
		Set<String> labels = new HashSet<String>();
		
		
		
		//We look for label first
		for (OWLAnnotationAssertionAxiom annAx : EntitySearcher.getAnnotationAssertionAxioms(cls, onto)){
					
			if (annAx.getAnnotation().getProperty().getIRI().toString().equals(rdf_label_uri) ||
					annAx.getAnnotation().getProperty().getIRI().toString().equals(synonym_iri)){
						
				labels.add(((OWLLiteral)annAx.getAnnotation().getValue()).getLiteral()); //No lower case yet
				
				if (labels.size()>=max_size)
					break;		
			}
		}
		
		
		//Check if concept name is meaningful (not an ID)
		String name_class = Utilities.getEntityLabelFromURI(cls.getIRI().toString()); 
		//If it doesn't exist any label then we use entity name
		//TODO OJO with yujiao results
		if (labels.isEmpty() || !name_class.matches(".+[0-9][0-9][0-9]+")){
		//if (labels.isEmpty()){
			labels.add(name_class);
		}
		
		return labels;
		
	}
	
	
	
	/**
	 * Extracts all labels. It may be time consuming
	 * @param cls
	 * @param max_size
	 * @return
	 */
	private Set<String> extractAllLabels4OWLCls(OWLClass cls, int max_size){
		
			Set<String> labels = new HashSet<String>();
			
			//All labels
			for (OWLAnnotationAssertionAxiom annAx : EntitySearcher.getAnnotationAssertionAxioms(cls, onto)){
				
				
				labels.addAll(annotationExtractor.getAnntotationString(annAx, onto, onto.getOWLOntologyManager().getOWLDataFactory()));				
									
				if (labels.size()>=max_size)
						break;		
				
			}
			
			
			//Check if concept name is meaningful (not an ID)
			String name_class = Utilities.getEntityLabelFromURI(cls.getIRI().toString()); 
			//If it doesn't exist any label then we use entity name
			//TODO OJO with yujiao results
			if (labels.isEmpty() || !name_class.matches(".+[0-9][0-9][0-9]+")){
			//if (labels.isEmpty()){
				labels.add(name_class);
			}
			
			return labels;
			
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
			words=Utilities.splitStringByCapitalLetter(label_value);
		}
		//else {
		//	words=new String[1];
		//	words[0]=label_value;
		//}
		
		//To lowercase
		
		
		for (int i=0; i<words.length; i++){
			
			words[i]=words[i].toLowerCase(); //to lower case
			
			if (words[i].length()>0){
			
				if (!lexicalUtilities.getStopwordsSet().contains(words[i])){
				//if (!LexicalUtilities.getStopwordsSetExtended().contains(words[i])){
					//words[i].length()>2 &&  Not for exact IF: it may contain important numbers					
					cleanWords.add(words[i]);
				}				
			}			
		}
		
		
		
		return cleanWords;
		
	}
	
	
	/**
	 * Creates entry in exact occurrences map and adds label to class index
	 * @param cls
	 * @param ident
	 * @return
	 */
	private List<String> extractCleanLabel4OWLCls(OWLClass cls){
		
		String label_value="";
		
		List<String> cleanWords=new ArrayList<String>();
		
		String[] words;
		
		
		
		//We look for label first
		for (OWLAnnotationAssertionAxiom annAx : EntitySearcher.getAnnotationAssertionAxioms(cls, onto)){
			
			if (annAx.getAnnotation().getProperty().getIRI().toString().equals(rdf_label_uri)){
				
				label_value=((OWLLiteral)annAx.getAnnotation().getValue()).getLiteral();//.toLowerCase();
				
				//LogOutput.print("Label: " + label_value);
				break;
				
			}

		}
		
		//If it doesn't exist then we use entity name
		if (label_value.equals("")){
		
			label_value=Utilities.getEntityLabelFromURI(cls.getIRI().toString());
			
		}
		
		
		label_value=label_value.replace(",", "");
		
		if (label_value.indexOf("_")>0){ //NCI and SNOMED
			words=label_value.split("_");
		}
		else if (label_value.indexOf(" ")>0){ //FMA
			words=label_value.split(" ");
		}
		//Split capitals...
		else{
			words=Utilities.splitStringByCapitalLetter(label_value);
		}
		//else {
		//	words=new String[1];
		//	words[0]=label_value;
		//}
		
		//To lowercase
		
		
		for (int i=0; i<words.length; i++){
			
			words[i]=words[i].toLowerCase(); //to lower case
			
			if (words[i].length()>0){
			
				if (!lexicalUtilities.getStopwordsSet().contains(words[i])){
				//if (!LexicalUtilities.getStopwordsSetExtended().contains(words[i])){
					//words[i].length()>2 &&  Not for exact IF: it may contain important numbers					
					cleanWords.add(words[i]);
				}				
			}			
		}
		
		
		
		return cleanWords;
		
	}
	
	

	

	
	/*private void setInvertedFileExactLabels(){
			
		Set<String> words;
		
		for (int ident=0; ident < listofClassLabels.size(); ident++){
		
			words = new HashSet<String>(listofClassLabels.get(ident));
		
			if (words.size()>0){
				
				if (!invertedFileWeakLabels.containsKey(words))
					invertedFileWeakLabels.put(new HashSet<String>(words), new HashSet<Integer>());
				
				invertedFileWeakLabels.get(words).add(ident);				
			}			
		}
	}*/
	
	
	/*
	private void setInvertedFileStemmedLabels(){
		
		Set<String> stemmed_words = new HashSet<String>();
		
		for (int ident=0; ident < listofClassLabels.size(); ident++){
		
			//words = listofClassLabels.get(ident);
			stemmed_words.clear();
			
			for (String str : listofClassLabels.get(ident)){
				stemmed_words.add(LexicalUtilities.getStemming4Word(str));
			}
		
			if (stemmed_words.size()>0){
				
				if (!invertedFileWeakLabels.containsKey(stemmed_words))
					invertedFileWeakLabels.put(new HashSet<String>(stemmed_words), new HashSet<Integer>());
				
				invertedFileWeakLabels.get(stemmed_words).add(ident);				
			}			
		}
	}*/
	
	
	
	
	private void setInvertedFileWeakLabels(){
		
		//TODO
		//Extend with max size labels and max_size_missing_words: perform experiments and evaluate performance.
		//Currently set to 2
		
		
		//List<String> words;
		
		
		//for (int ident=0; ident < listofClassLabels.size(); ident++){
		for (int ident : identifier2stemmedlabels.keySet()){
		
			//words = listofClassLabels.get(ident);
			for (List<String> words : identifier2stemmedlabels.get(ident)){
		
			
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

	
	
	/**
	 * Combines the words in given list with 'x' missing words and stores the results in IF
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
		
	
	

}

package uk.ac.ox.krr.logmap2.indexing;

import java.io.FileNotFoundException;
import java.util.ArrayList;


import java.util.HashSet;


import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

//import org.apache.commons.lang3.StringUtils;


import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.model.parameters.Imports;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.impl.OWLClassNodeSet;
import org.semanticweb.owlapi.search.EntitySearcher;
import org.semanticweb.owlapi.search.Searcher;

import com.google.common.collect.Multimap;

import uk.ac.ox.krr.logmap2.Parameters;
import uk.ac.ox.krr.logmap2.indexing.entities.ClassIndex;
import uk.ac.ox.krr.logmap2.io.LogOutput;
import uk.ac.ox.krr.logmap2.lexicon.LexicalUtilities;
import uk.ac.ox.krr.logmap2.lexicon.NormalizeDate;
import uk.ac.ox.krr.logmap2.lexicon.NormalizeNumbers;
import uk.ac.ox.krr.logmap2.multilingual.TranslatorManager;
import uk.ac.ox.krr.logmap2.oaei.OAEI2015InstanceProcessing;
import uk.ac.ox.krr.logmap2.owlapi.SynchronizedOWLManager;
import uk.ac.ox.krr.logmap2.reasoning.ELKAccess;
import uk.ac.ox.krr.logmap2.reasoning.HermiTAccess;
import uk.ac.ox.krr.logmap2.reasoning.StructuralReasonerExtended;
import uk.ac.ox.krr.logmap2.reasoning.profiles.CheckOWL2Profile;
import uk.ac.ox.krr.logmap2.utilities.Lib;
import uk.ac.ox.krr.logmap2.utilities.PrecomputeIndexCombination;
import uk.ac.ox.krr.logmap2.utilities.Utilities;

/**
 * This class will extract the lexicon and structure of the given ontology
 * 
 *
 * @author Ernesto Jimenez-Ruiz
 * Created: Sep 12, 2011
 *
 */
public class OntologyProcessing {
	
	long init, fin;
	
	private OWLOntology onto;
	
	private IndexManager index;
	
	/*Exact match entries*/
	//protected Map<String, Integer> extact_occurrence_entries = new HashMap<String, Integer>();

	
	/**Exact match inverted file*/
	protected Map<Set<String>, Set<Integer>> invertedFileExact = new HashMap<Set<String>, Set<Integer>>();
	
	/**Exact match inverted file for data properties*/
	protected Map<Set<String>, Integer> invertedFileExactDataProp = new HashMap<Set<String>, Integer>();
	
	/**Exact match inverted file for object properties*/
	protected Map<Set<String>, Integer> invertedFileExactObjProp = new HashMap<Set<String>, Integer>();
	
	protected Map<Set<String>, Set<Integer>> invertedFileIndividuals = new HashMap<Set<String>, Set<Integer>>();
	//Only one word IF weak
	protected Map<String, Set<Integer>> invertedFileWeakIndividuals = new HashMap<String, Set<Integer>>();
	
	
	//Will contain a lexicon of the role assertions in indiv
	protected Map<String, Set<Integer>> invertedFileRoleassertions = new HashMap<String, Set<Integer>>();
	
	
	protected Map<Set<String>, Set<Integer>> invertedFileWeakLabelsStemming = new HashMap<Set<String>, Set<Integer>>();
	
	protected Map<Set<String>, Set<Integer>> invertedFileWeakLabels = new HashMap<Set<String>, Set<Integer>>();
	//protected Map<Set<String>, Set<Integer>> invertedFileWeakLabelsL2 = new HashMap<Set<String>, Set<Integer>>();
	//protected Map<Set<String>, Set<Integer>> invertedFileWeakLabelsL3 = new HashMap<Set<String>, Set<Integer>>();
	
	
	
	/**To index class names: necessary when loading GS and repairing mappings*/
	protected Map<String, Integer> className2Identifier = new HashMap<String, Integer>();
	
	/**To index data prop names: necessary when loading GS and repairing mappings*/
	protected Map<String, Integer> dataPropName2Identifier = new HashMap<String, Integer>();
	
	/**To index object prop names: necessary when loading GS and repairing mappings*/
	protected Map<String, Integer> objectPropName2Identifier = new HashMap<String, Integer>();
	
	/**To index instance names: necessary when loading GS and repairing mappings*/
	protected Map<String, Integer> individualName2Identifier = new HashMap<String, Integer>();
	
	
	/**USed when extracting taxonomy*/
	private Map<OWLClass, Integer> class2identifier = new HashMap<OWLClass, Integer>();
	private Map<String, Integer> classIri2identifier = new HashMap<String, Integer>();
	
	//For lexicon extraction. Indiv are related one to the other
	private Map<OWLNamedIndividual, Integer> inidividual2identifier = new HashMap<OWLNamedIndividual, Integer>();
	
	private Map<Integer, OWLClass> identifier2class = new HashMap<Integer, OWLClass>();
	
	//In this set we should include those classes we should not map
	//e.g. classes that are equivalent to Top (see cocus ontology)
	private Set<Integer> dangerousClasses = new HashSet<Integer>();
	
	
	private Set<String> alternative_labels_tmp = new HashSet<String>();
	
	
	//private HashMap<Integer, Set<Integer>> identifier2directkids= new HashMap<Integer, Set<Integer>>();	
	//private Map<Integer, Set<Integer>> identifier2directparents= new HashMap<Integer, Set<Integer>>();
	
	/**This set will be used to propagate equivalences (entities store equivalents)*/
	//TODO this set will need to be enriched with anchors... or at leats considered after indexing
	//In index
	//private Set<Integer> representativeNodes = new HashSet<Integer>();
	
	/* Importnat for a ordered assessment Now in index*/
	//protected Set<Integer> RootIdentifiers = new HashSet<Integer>();
	
	
	/*A^B->C axioms Now in Index*/
	//private Map<Set<Integer>, Integer> generalHornAxioms= new HashMap<Set<Integer>, Integer>();
	
	
	
	/** We use this structure to create weak labels*/
	//private Map<Integer, List<String>> identifier2stemmedlabel = new HashMap<Integer, List<String>>();
	//We also consider alternative labels
	private Map<Integer, Set<List<String>>> identifier2stemmedlabels = new HashMap<Integer, Set<List<String>>>();
	//Must be kept outside index to be removed after use and to attached to onto (i.e ontoprocessing object)
	
	/**
	 * Meaningful roots 
	 * @deprecated
	 */
	protected Set<Integer> MeaningfulRootIdentifiers = new HashSet<Integer>();
	/**Real roots 
	 * @deprecated
	 */
	protected Set<Integer> TaxRootIdentifiers = new HashSet<Integer>(); //The real ones
	
	
	//IRI labels annotations 
	private String rdf_label_uri = "http://www.w3.org/2000/01/rdf-schema#label";
	public static String deprecated_uri = "http://www.w3.org/2002/07/owl#deprecated";
	
	
	protected Set<OWLClass> obsolete_classes = new HashSet<OWLClass>();
	
	
	
	private String iri_onto = "http://krono.act.uji.es/ontology.owl";
	
	public String getOntoIRI() {return iri_onto; }
	
	private int id_onto;
	
	
	int num_syn=0;
	int toohigh_synset_cases=0;	
	
	
		
	private OWLReasoner reasoner;
	private int reasonerIdentifier;
	
	
	//TODO Ernesto: to avoid using all labels for weak mappings
	boolean use_all_labels_for_weak_mappings=false;
	
	
	private LexicalUtilities lexicalUtilities;
	
	private PrecomputeIndexCombination precomputeIndexCombination = new PrecomputeIndexCombination();
	
	private ExtractStringFromAnnotationAssertionAxiom annotationExtractor = new ExtractStringFromAnnotationAssertionAxiom();
	
	public ExtractAcceptedLabelsFromRoleAssertions roleAssertionLabelsExtractor = new ExtractAcceptedLabelsFromRoleAssertions();
	
	private ExtractCategoriesForIndividual categoryExtractor = new ExtractCategoriesForIndividual();
	
	
	//For multilingual module
	//private Translator translator = TranslatorManager.getCurrentTranslatorObject();
	private TranslatorManager translator = new TranslatorManager(Parameters.translator_id); //use all available translators
	
	
	
	/**
	 * 
	 * @param onto
	 * @param index
	 * @param lexicalUtilities
	 */
	public OntologyProcessing(OWLOntology onto, IndexManager index, LexicalUtilities lexicalUtilities){
		
		this.onto=onto;
		this.index=index;
		
		this.lexicalUtilities=lexicalUtilities;
		
		
		//We precompute indexes
		 //precomputeIndexCombination.clearCombinations(); //Old calls
		 precomputeIndexCombination.preComputeIdentifierCombination();
		
		
		//The preclassification with condor has no ontology id
		if (onto.getOntologyID().getOntologyIRI()!=null && onto.getOntologyID().getOntologyIRI().isPresent()){
			iri_onto=onto.getOntologyID().getOntologyIRI().get().toString();
		}
		//System.out.println(onto.getOntologyID());
		
		this.id_onto = this.index.addNewOntologyEntry(iri_onto);
		
		
		
	}
	
	public void clearOntologyRelatedInfo(){
		//TODO After extracting tax
		onto=null;
		class2identifier.clear();
		clearReasoner();
	}
	
	public Map<OWLClass, Integer> getClass2Identifier(){
		return class2identifier;
	}
	
	
	public int getIdentifier4ConceptIRI(String iri){
		if (classIri2identifier.containsKey(iri))
			return classIri2identifier.get(iri);
		else
			return -1;
	}
	
	
	public void clearInvertedFilesExact(){
		invertedFileExact.clear();
		//invertedFileExactDataProp.clear();
		//invertedFileExactObjProp.clear();	
	}
	
	public void clearInvertedFileStemming(){
	
		invertedFileWeakLabelsStemming.clear();
	}
	
	public void clearInvertedFileWeak(){
		
		invertedFileWeakLabels.clear();		

	}


	public void clearInvertedFilesStemmingAndWeak(){
	
		invertedFileWeakLabels.clear();		
		invertedFileWeakLabelsStemming.clear();
	}
	
	public void clearStemmedLabels(){
		//identifier2stemmedlabel.clear();
		identifier2stemmedlabels.clear();
	}
	
	public void clearInvertedFiles4properties(){
		invertedFileExactDataProp.clear();
		invertedFileExactObjProp.clear();
	}
	
	public void clearInvertedFiles4Individuals(){
		invertedFileIndividuals.clear();
		invertedFileWeakIndividuals.clear();
		invertedFileRoleassertions.clear();
	}
	
	
	

	
	
	
	
	
	/**
	 * @deprecated
	 * 
	 */
	private void clearTaxonomy(){
		//TODO after indexing or the global id2kids has been created 
		//identifier2directkids.clear();
		//identifier2directparents.clear();
		//representativeNodes.clear();
		//generalHornAxioms.clear();
	}
	
	
	public void clearReasoner(){
		//TODO after extracting tax!
		reasoner.dispose();
		reasoner=null;
	}
	
	
	
	public void precessLexicon() {
		precessLexicon(true);
	}
	
	/**
	 * We create a class identifier for each class, we also create inverted files
	 * 
	 */
	public void precessLexicon(boolean extractLabels) {
		
		
		init=Calendar.getInstance().getTimeInMillis();
		
		
		
		//LogOutput.print(onto.getClassesInSignature().size());
		
		//CLASSES				
		LogOutput.print("\nCLASSES: " + onto.getClassesInSignature(Imports.INCLUDED).size());
		
		//TODO
		//We need to add something
		//Was already fixed in a different place?
		//if (onto.getClassesInSignature(true).size()==0){
		//	OWLClass cls = SynchronizedOWLManager.createOWLDataFactory().getOWLClass(IRI.create("http://logmap.cs.ox.ac.uk/ontologies#TopClass"));
		//	precessLexiconClasses(cls, extractLabels);			
		//}
		//else {
		
		//TODO For the OAEI campaign we do not translate bigger ontologies
		//We have a limit in our google translate account
		if (onto.getClassesInSignature(Imports.INCLUDED).size()>300){
			Parameters.allow_multilingual=false;
		}
		
		
		//TODO Identify language and load local dictionary if any
		//Useful for the multifarm task where we need to translate each ontology servarl times.
		//Thus we translate for the first occurrence and we keep (locally) the translation for the next cases.
		//We assume that each ontology is in only one language 
		String lang="";
		if (Parameters.allow_multilingual){
			lang = identifyLanguageOfOntologyLabels();
			
			//System.out.println("LANGUAGE: " + lang);
			
			if (!lang.equals("") && !lang.equals(Parameters.target_lang)){
				//load dictionary 
				try {
					translator.loadDictionary(lang);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					LogOutput.printError("Dictionary file for '" + lang + "' not found.");// + e.getLocalizedMessage());
					//e.printStackTrace();
				}	
			}
				
			
		}
			
		
		 
		
		for (OWLClass cls : onto.getClassesInSignature(Imports.INCLUDED)){ //we also add imports
			
						
			if (!cls.isTopEntity() && !cls.isBottomEntity()){// && !ns_ent.equals(oboinowl)){
				
				processLexiconClasses(cls, extractLabels);
				
			}
				
		}//end For classes
		
		
		//DETECT HIGH REDUNDANCY ON EXACT IF
		//Added August 30, 2018
		if (Parameters.avoid_redundant_labels){
			Set<Integer> redundant_ids = new HashSet<Integer>();
			for (Set<String> entry : invertedFileExact.keySet()){
				if (invertedFileExact.get(entry).size()>Parameters.max_redundancy_labels){
					//Keep only entries for which only 1 alternative label exists
					for (int ident: invertedFileExact.get(entry)){
						if (index.getAlternativeLabels4ConceptIndex(ident).size()>1){//has lower case
							redundant_ids.add(ident);
						}
					}
					invertedFileExact.get(entry).removeAll(redundant_ids);
					redundant_ids.clear();
				}
			}
		}
		
		//System.out.println("NUM RDF LABEL: "+num_labels);
		
		
		//}//end If size classes
						
		
		//DATA PROPERTIES
		processLexiconDataProperties(extractLabels);
			
		
		
		//OBJECT PROPERTIES
		processLexiconObjectProperties(extractLabels);
		
		
		
		//INDIVIDUALS
		if (Parameters.perform_instance_matching){
			processNamedIndividuals(extractLabels);
			
			//DETECT HIGH REDUNDANCY ON EXACT IF
			//Added August 30
			
			if (Parameters.avoid_redundant_labels){
				Set<Integer> redundant_ids = new HashSet<Integer>();
				for (Set<String> entry : invertedFileIndividuals.keySet()){
					if (invertedFileIndividuals.get(entry).size()>Parameters.max_redundancy_labels){
						
						//if (entry.contains("sonates")){
						//	System.out.println("Redundancey: " + entry + "  " + invertedFileIndividuals.get(entry).size());
						//}
						
						//Keep only entries for which only 1 alternative label exists
						for (int ident: invertedFileIndividuals.get(entry)){
							
							//if (entry.contains("sonates")){
							//	System.out.println("\t" + ident + "  " + index.getAlternativeLabels4IndividualIndex(ident).size() + "  " + index.getAlternativeLabels4IndividualIndex(ident) + "  " + index.getIRIStr4IndividualIndex(ident));
							//}
							
							if (index.getAlternativeLabels4IndividualIndex(ident).size()>1){
								redundant_ids.add(ident);
							}
						}
						invertedFileIndividuals.get(entry).removeAll(redundant_ids);
						redundant_ids.clear();
					}
				}
			}
			
		}
		
		
		//Store local dictionary: es-en.txt
		//Add note that the dictionary is automatically extracted.
		if (Parameters.allow_multilingual){
			
			if (!lang.equals("") && !lang.equals(Parameters.target_lang)){
				
				//TODO we also store new translations if not in local or local is not allowed
				//store dictionary 
				translator.storeDictionary(lang);
				//}
				
				//TODO REMOVE if not testing!!!
				/*if (Parameters.is_test_mode_multilingual){
					WriteFile writer = new WriteFile(
							"/home/ernesto/Documents/OAEI_OM_2015/EVAL_2015/error_multilingual/total_characters_reduced.txt", true);
					//System.out.println("Num translated characters: " + translator.getNumberOfTranslatedCharacters());
					//System.out.println("Num calls to google: " + translator.getNumberOfGoogleTranslateCalls());
					writer.writeLine(String.valueOf(translator.getNumberOfTranslatedCharacters()));
					writer.closeBuffer();
				}*/
				
			}
			
		}
		
		
		
		
		
		
		LogOutput.print("Number of classes/labels: " + index.getSizeIndexClasses()); //Ojo it is a total
		LogOutput.print("\tNumber of labels + syn: " + num_syn);
		LogOutput.print("\tCases with huge combination of synonyms: " + toohigh_synset_cases);
		LogOutput.print("\tNumber of entries inverted file (exact): " + invertedFileExact.size());
		
		LogOutput.print("Number of dProp: " + index.getSizeDataProperties()); //Ojo it is a total
		LogOutput.print("\tNumber of dProp inverted file: " + invertedFileExactDataProp.size());
		
		LogOutput.print("Number of oProp: " + index.getSizeObjectProperties()); //Ojo it is a total
		LogOutput.print("\tNumber of oProp inverted file: " + invertedFileExactObjProp.size());
		
		LogOutput.print("Number of Indiv: " + index.getSizeIndexIndividuals()); //Ojo it is a total
		LogOutput.print("\tNumber of Indiv inverted file: " + invertedFileIndividuals.size());
		LogOutput.print("\tNumber of Indiv weak inverted file: " + invertedFileWeakIndividuals.size());
		LogOutput.print("\tNumber of Indiv Role assertions inverted file: " + invertedFileRoleassertions.size());
		
		
		
		
		/*int exactIFAmb=0;
		for (Set<String> setstr : invertedFileExact.keySet()){
			//LogOutput.print(setstr);
			if (invertedFileExact.get(setstr).size()>1)
				exactIFAmb++;
		}
		LogOutput.print("Ambiguity IF exact: " + exactIFAmb);*/
		
		
		//Labels are already indexed so we can safely remove annotations form onto
		//managerOnto.applyChanges(listchanges);
		
		
		/*for (int i=1900; i<2000; i++){
			if (identifier2ClassIndex.get(i).getAlternativeLabels().size()>1){
				LogOutput.print(i);
				LogOutput.print("\t"+identifier2ClassIndex.get(i).getLabel());
				LogOutput.print("\t"+identifier2ClassIndex.get(i).getEntityName());
				LogOutput.print("\t"+identifier2ClassIndex.get(i).getAlternativeLabels());
			}
		}*/
		
		
		fin = Calendar.getInstance().getTimeInMillis();
		LogOutput.print("Time setting labels and inverted files (s): " + (float)((double)fin-(double)init)/1000.0);
			
		
		

	}
	
	
	
	private String identifyLanguageOfOntologyLabels(){
		
		//We check a subset of classes
		int num_tests=0;
		
		String lang="";
		
		for (OWLClass cls : onto.getClassesInSignature(Imports.INCLUDED)){ //we also add imports
			if (!cls.isTopEntity() && !cls.isBottomEntity()){
				
				for (OWLAnnotationAssertionAxiom clsAnnAx : EntitySearcher.getAnnotationAssertionAxioms(cls, onto)){
					
					lang = annotationExtractor.getAnntotationLanguage(clsAnnAx);
					
					if (lang!=null && !lang.equals("")){
						
						//To fix misuses of ISO codes
						if (lang.equals("cn"))
							return "zh";
						if (lang.equals("cz"))
							return "cs";
											
						return lang;
					}
				
				}	
				num_tests++;
				if (num_tests>100) //to put a limit!
					return lang;
			}
		}
		
		return lang;
	}
	
	
	
	
	private Set<String> translateLabel(OWLAnnotationAssertionAxiom entityAnnAx, String label){
		
		Set<String> translation_labels = new HashSet<String>();
		
		//We only translate if lang is provided in a direct label
		String lang = annotationExtractor.getAnntotationLanguage(entityAnnAx);
			
		if (!lang.equals(Parameters.target_lang) && !lang.equals("")){
				
			//Call the translation module
			label = translator.getTranslation(label, lang);
			//LogOutput.printAlways(label + ":  " + translation);
			
			
			//For translated labels (case with many translations)
			//Label is: trans1|trans2|...|transx
			if (label.indexOf("|")>=0){
				String[] elements = label.split("\\|");
				for (String e : elements){
					translation_labels.add(e);
				}
				return translation_labels;
			}
				
		}
		
		translation_labels.add(label); //Original label or single translation
				
		return translation_labels;
	}
	
	
	
	
	private void processLexiconClasses(OWLClass cls, boolean extractLabels) {
		
		int ident;
		
		String ns_ent;
		String name;
		
		
		
		//TODO Added on Sept 7, 2018
		//Avoid deprecated cases. Possibly extend the ways a class is declared as deprecated
		if (isDeprecatedClass(cls)){
			//System.out.println("Filtering deprecated cls: " + cls.getIRI());
			return;
		}
		
		
		ns_ent=Utilities.getNameSpaceFromURI(cls.getIRI().toString());
		
		
		ident = index.addNewClassEntry();
		
		index.setOntologyId4Class(ident, id_onto);
		
		name = Utilities.getEntityLabelFromURI(cls.getIRI().toString());
				
		//if (name.equals("Mouse_Coccyx"))
		//	LogOutput.print("Here");
		
		index.setClassName(ident, name);
		
		//We store ns only if it is different to the ontology ns
		if (!ns_ent.equals("") && !ns_ent.equals(iri_onto)){
			index.setClassNamespace(ident, ns_ent);
		}
		
		
		/*LogOutput.print("\nENT:");
		LogOutput.print(iri_onto);
		LogOutput.print(cls.getIRI().toString());
		LogOutput.print(identifier2ClassIndex.get(ident).getNamespace());
		LogOutput.print(identifier2ClassIndex.get(ident).getEntityName());*/
		
		
		//We may need this structure for the taxonomy extraction...
		class2identifier.put(cls, ident);
		identifier2class.put(ident, cls);
		className2Identifier.put(name, ident);
		
		// Alessandro 21 April 2014
		//Useful for composed mappinsg as well
		classIri2identifier.put(cls.getIRI().toString(), ident);
		
						
		//Extract labels and alternative labels and create IFs
		if (extractLabels)
			createEntryInLexicalInvertedFiles4ClassLabels(cls, ident);
		
		//if (ident>30)
		//	break;
		
		
	}
	
	
	private void processLexiconDataProperties() {
		processLexiconDataProperties(true);
	}
	
	
	private void processLexiconDataProperties(boolean extractLabels) {
		
		
		List<String> cleanWords;
		String label;
		
		int ident;
		
		String ns_ent;
		String name;
		
		//DATA PROPERTIES
		for (OWLDataProperty dProp : onto.getDataPropertiesInSignature(Imports.INCLUDED)){ //also imports
			
			ns_ent=Utilities.getNameSpaceFromURI(dProp.getIRI().toString());
			
					
			ident = index.addNewDataPropertyEntry();	
			
			index.setOntologyId4DataProp(ident, id_onto);
				
			name = Utilities.getEntityLabelFromURI(dProp.getIRI().toString());
				
			index.setDataPropName(ident, name);
			
			dataPropName2Identifier.put(name, ident);
			
				
			//LogOutput.print(name);
				
			//We store ns only if it is different to the ontology ns
			if (!ns_ent.equals("") && !ns_ent.equals(iri_onto)){
				index.setDataPropNamespace(ident, ns_ent);
			}
				
			
			//Extract labels and alternative labels and create IFs
			cleanWords = processLabel(name);
			if (cleanWords.size()>0){
				if (extractLabels){
					invertedFileExactDataProp.put(new HashSet<String>(cleanWords), ident);
				}
			}
			
			label="";
			for (String word : cleanWords){
				label=label + word;				
			}			
			//label without spaces
			index.setDataPropLabel(ident, label);
			index.addAlternativeDataPropertyLabel(ident, label);
			cleanWords.clear();
			
			
			//Create alternative labels
			//--------------------------------
			List<String> cleanWordsAlternative = createAlternativeLabel(name);
			if (cleanWordsAlternative.size()>0){
				
				//Note that we do not add alternative label to inverted file
				//Inverted file only contains exact entries from main labels
			
				label=""; //label without spaces
				for (String word : cleanWordsAlternative){
					label=label + word;				
				}
				index.addAlternativeDataPropertyLabel(ident, label);
			}
			cleanWordsAlternative.clear();
			
			//----------------------------
			//Use rdf:label and others as alternative labels
			//----------------------------
			//TODO: labels might be in an annotation
			
			
			
			for (OWLAnnotationAssertionAxiom dPropAnnAx : EntitySearcher.getAnnotationAssertionAxioms(dProp, onto)){
				
				//String label_value = annotationExtractor.getAnntotationString(dPropAnnAx);
				
				LogOutput.print(name);
				
				
				//TODO Perform translation if necessary
				for (String label_value : annotationExtractor.getAnntotationString(dPropAnnAx, onto, index.getFactory())){
					
					if (Parameters.allow_multilingual){			
						alternative_labels_tmp.addAll(translateLabel(dPropAnnAx, label_value));
					}
					else{									
						alternative_labels_tmp.add(label_value);
					}
				
				}
				
				
				//Cleaning and Indexing
				for (String label_value : alternative_labels_tmp){
					
					
					LogOutput.print("\t  " + label_value);
					
					cleanWords = processLabel(label_value);
					
					if (cleanWords.size()>0){
						label=""; //label without spaces
						for (String word : cleanWords){
							
							if (label.length()==0)
								label= word;
							else{
								label+= "_" + word;
							}
								
						}
						index.addAlternativeDataPropertyLabel(ident, label);
											
					}
				}
				alternative_labels_tmp.clear();
			}
			
			
			
			//LogOutput.print(name);
			
			//Process domain
			//--------------------------------
			for (OWLClassExpression clsexp : EntitySearcher.getDomains(dProp, onto)){
				if (!clsexp.isAnonymous()){
					if (class2identifier.containsKey(clsexp.asOWLClass())){
						index.addDomainClass4DataProperty(ident, class2identifier.get(clsexp.asOWLClass()));
					}
				}
				else if (clsexp.getClassExpressionType()==ClassExpressionType.OBJECT_UNION_OF){									
					for (OWLClassExpression clsexpunion : clsexp.asDisjunctSet()){
						if (!clsexpunion.isAnonymous()){
							if (class2identifier.containsKey(clsexpunion.asOWLClass())){
								index.addDomainClass4DataProperty(ident, class2identifier.get(clsexpunion.asOWLClass()));
							}
						}
					}
				}
			}
			
			String range_type;
			
			//Process data ranges
			//--------------------------------
			for (OWLDataRange type : EntitySearcher.getRanges(dProp, onto)){
				if (type.isDatatype()){
					//LogOutput.print(type.asOWLDatatype() + " " + type.asOWLDatatype().getIRI() + " " + type.asOWLDatatype().isBuiltIn() +"  " +
					//		Utilities.getEntityLabelFromURI(type.asOWLDatatype().getIRI().toString()));
					
					try{
						if (type.asOWLDatatype().isBuiltIn()){
							range_type = type.asOWLDatatype().getBuiltInDatatype().getShortForm();
						}
						else{//we extract name from iri
							//range_type =  Utilities.getEntityLabelFromURI(Utilities.getEntityLabelFromURI(type.asOWLDatatype().getIRI().toString()));
							range_type =  Utilities.getEntityLabelFromURI(type.asOWLDatatype().getIRI().toString());
						}
					}
					catch (Exception e){ //In some cases the datatype is not built in an rises an error
						//range_type =  Utilities.getEntityLabelFromURI(Utilities.getEntityLabelFromURI(type.asOWLDatatype().getIRI().toString()));
						range_type =  Utilities.getEntityLabelFromURI(type.asOWLDatatype().getIRI().toString());
					}
					index.addRangeType4DataProperty(ident, range_type);
				}
				
			}
			
			
			//LogOutput.print("\tDOMAINS: " + identifier2DataPropIndex.get(ident).getDomainClassIndexes());
			//LogOutput.print("\tRANGES: " + identifier2DataPropIndex.get(ident).getRangeTypes());
			
							
			ident++;			
			
		}
		
		
	}
	
	private void processLexiconObjectProperties(){
		processLexiconObjectProperties(true);
	}
	
	private void processLexiconObjectProperties(boolean extractLabels){
		
		List<String> cleanWords;
		String label;
		
		int ident;
		
		String ns_ent;
		String name;
		
		
		//OBJECT PROPERTIES
		for (OWLObjectProperty oProp : onto.getObjectPropertiesInSignature(Imports.INCLUDED)){//also imports
			
			ns_ent=Utilities.getNameSpaceFromURI(oProp.getIRI().toString());
						
				
			
			ident = index.addNewObjectPropertyEntry();
			
			index.setOntologyId4ObjectProp(ident, id_onto);
				
			name = Utilities.getEntityLabelFromURI(oProp.getIRI().toString());
				
			index.setObjectPropName(ident, name);
			
			objectPropName2Identifier.put(name, ident);
			
			
			//identifier2ObjPropIndex.get(ident).setLabel(name);
			
			//LogOutput.print(name);
				
				
			//We store ns only if it is different to the ontology ns
			if (!ns_ent.equals("") && !ns_ent.equals(iri_onto)){
				index.setObjectPropNamespace(ident, ns_ent);
			}
				
			//Extract labels and alternative labels and create IFs
			cleanWords = processLabel(name);
			if (cleanWords.size()>0){
				if (extractLabels){
					invertedFileExactObjProp.put(new HashSet<String>(cleanWords), ident);
				}
			}
			
			label=""; //label without spaces
			for (String word : cleanWords){
				label=label + word;				
			}
			index.setObjectPropLabel(ident, label);
			index.addAlternativeObjectPropertyLabel(ident, label);
			cleanWords.clear();
			
			
			
			//Create alternative labels
			//--------------------------------
			List<String> cleanWordsAlternative = createAlternativeLabel(name);
			if (cleanWordsAlternative.size()>0){
				
				//Note that we do not add alternative label to inverted file
				//Inverted file only contains exact entries from main labels
			
				label=""; //label without spaces
				for (String word : cleanWordsAlternative){
					label=label + word;				
				}
				index.addAlternativeObjectPropertyLabel(ident, label);
			}
			cleanWordsAlternative.clear();
			
			
			
			//----------------------------
			//Use rdf:label and others as alternative labels
			//----------------------------
			//TODO: labels might be in an annotation
			for (OWLAnnotationAssertionAxiom oPropAnnAx : EntitySearcher.getAnnotationAssertionAxioms(oProp, onto)){
				
				//String label_value = annotationExtractor.getAnntotationString(oPropAnnAx);
				
				LogOutput.print(name);
				
				
				//TODO Perform translation if necessary
				for (String label_value : annotationExtractor.getAnntotationString(oPropAnnAx, onto, index.getFactory())){
					
					if (Parameters.allow_multilingual){			
						alternative_labels_tmp.addAll(translateLabel(oPropAnnAx, label_value));
					}
					else{									
						alternative_labels_tmp.add(label_value);
					}
				
				}
				
				
				//Cleaning and indexing
				for (String label_value : alternative_labels_tmp){
					
									
					LogOutput.print("\t  " + label_value);
					
					cleanWords = processLabel(label_value);
					
					if (cleanWords.size()>0){
						label=""; //label without spaces
						for (String word : cleanWords){
							
							if (label.length()==0)
								label= word;
							else{
								label+= "_" + word;
							}
								
						}
						//System.out.println(ident + "  " + label);
						index.addAlternativeObjectPropertyLabel(ident, label);
											
					}
				}
				alternative_labels_tmp.clear();
			}
			
			
			

			//Process Inverse properties and extend labels
			//--------------------------------------------
			String inverse_name;
			List<String> cleanWordsInverse;
			for (OWLObjectPropertyExpression propexp : EntitySearcher.getInverses(oProp, onto)){
				if (!propexp.isAnonymous()){
					inverse_name = Utilities.getEntityLabelFromURI(
							propexp.asOWLObjectProperty().getIRI().toString());
					
					//Reuse name of inverse property to create an alternative label for the property
					cleanWordsInverse = processInverseLabel(inverse_name);
					
					if (cleanWordsInverse.size()>0){
						
						//Note that we do not add alternative label to inverted file
						//Inverted file only contains exact entries from main labels
						
					
						label=""; //label without spaces
						for (String word : cleanWordsInverse){
							label=label + word;				
						}
						index.addAlternativeObjectPropertyLabel(ident, label);
					}
					cleanWordsInverse.clear();
				}
			}
			
			
			//Process domains
			//--------------------------------------------
			for (OWLClassExpression clsexp : EntitySearcher.getDomains(oProp, onto)){
				
				if (!clsexp.isAnonymous()){
					if (class2identifier.containsKey(clsexp.asOWLClass())){
						index.addDomainClass4ObjectProperty(ident, class2identifier.get(clsexp.asOWLClass()));
					}
				}
				else if (clsexp.getClassExpressionType()==ClassExpressionType.OBJECT_UNION_OF){									
					for (OWLClassExpression clsexpunion : clsexp.asDisjunctSet()){
						if (!clsexpunion.isAnonymous()){
							if (class2identifier.containsKey(clsexpunion.asOWLClass())){
								index.addDomainClass4ObjectProperty(ident, class2identifier.get(clsexpunion.asOWLClass()));
							}
						}
					}
				}
			
			}
			
			
			//Process ranges
			//--------------------------------------------
			for (OWLClassExpression clsexp : EntitySearcher.getRanges(oProp, onto)){
				if (!clsexp.isAnonymous()){
					if (class2identifier.containsKey(clsexp.asOWLClass())){
						index.addRangeClass4ObjectProperty(ident, class2identifier.get(clsexp.asOWLClass()));
					}
				}
				else if (clsexp.getClassExpressionType()==ClassExpressionType.OBJECT_UNION_OF){									
					for (OWLClassExpression clsexpunion : clsexp.asDisjunctSet()){
						if (!clsexpunion.isAnonymous()){
							if (class2identifier.containsKey(clsexpunion.asOWLClass())){
								index.addRangeClass4ObjectProperty(ident, class2identifier.get(clsexpunion.asOWLClass()));
							}
						}
					}
				}
			}
			
			//LogOutput.print(name);
			//LogOutput.print("\tDOMAINS: " + identifier2ObjPropIndex.get(ident).getDomainClassIndexes());
			//LogOutput.print("\tRANGES: " + identifier2ObjPropIndex.get(ident).getRangeClassIndexes());
							
			ident++;	//Is it necessary?	
			
		}
		
	}
	
	
	private void processNamedIndividuals(){
		processNamedIndividuals(true);
	}
	
	private void processNamedIndividuals(boolean extractLabels){
		
		Set<String> cleanWords = new HashSet<String>();
		List<String> cleanWordsList;
		
		String label;
		
		int ident;
		
		String ns_ent;
		String name;
		
		
		Set<String> altLabels = new HashSet<String>(); 
		
		
		String longestALabel;
		
		
		int num_dummy_indiv = 0;
		Set<OWLNamedIndividual> dummyIndividualsSet = new HashSet<OWLNamedIndividual>();
		
		
			
		for (OWLNamedIndividual indiv : onto.getIndividualsInSignature(Imports.INCLUDED)){//also imports
			
			ns_ent=Utilities.getNameSpaceFromURI(indiv.getIRI().toString());
			
			ident = index.addNewIndividualEntry();
			
			index.setOntologyId4Individual(ident, id_onto);
			
			inidividual2identifier.put(indiv, ident);
			
			
			//Name in URI
			name = Utilities.getEntityLabelFromURI(indiv.getIRI().toString());
			index.setIndividualName(ident, name);
			//We add a better label below if there are alternative labels
			index.setIndividualLabel(ident, name);
			
			individualName2Identifier.put(name, ident);

			//We store ns only if it is different to the ontology ns
			if (!ns_ent.equals("") && !ns_ent.equals(iri_onto)){
				index.setIndividualNamespace(ident, ns_ent);
			}
			
			
			//IF DUMMY INDIVIDUAL THEN DO NOT ADD ALTERNATIVE LABELS
			//In OAEI 2013 the ones that are referenced from property "http://www.instancematching.org/label"
			if (roleAssertionLabelsExtractor.isDummyIndividual(indiv)){
				//LogOutput.print("DUMMY individual: " + indiv.getIRI().toString());
				num_dummy_indiv++;
				dummyIndividualsSet.add(indiv);
				continue;
			}
			
			
			
					
			//EXTRACT ALTERNATIVE LABELS FOR INDIVIDUAL
			///-------------------------------------------
			//altLabels.add(name);
			
			
			//We add accepted assertions associated to the individual 
			//altLabels.addAll(extractNameFromDataAssertion(indiv));
			altLabels.addAll(roleAssertionLabelsExtractor.extractLexiconFromRoleAssertions(indiv)); //new
			
			
			//We add accepted annotations associated to the individual
			altLabels.addAll(extractAnnotations4Infividual(indiv));
			
			
			//TODO EXTRACT CATEGORIES
			//From annotations and (data and object) role assertions
			categoryExtractor.extract(indiv, ident);
			
			
			//add weak entries
			
			longestALabel="";
			
			//We treat Alt labels for individual: so far datatype assertion name and URI
			for (String alabel : altLabels){
				
				if (!isLabelAnIdentifier(alabel)){
				
					if (alabel.length()>longestALabel.length()){
						longestALabel=alabel;
					}
					
					index.addAlternativeIndividualLabel(ident, alabel.toLowerCase()); //lower case? I guess it is the same...
					
					cleanWordsList = processLabel(alabel, true); //sopwords!! //and lowecase
					
					//Add altlabels with order changed
					changeOrderAltLabelWords(ident, cleanWordsList);
					
					//wE do not want order in IFs entries
					cleanWords.addAll(cleanWordsList);
					
					
					//Weak IF
					String stemmedWord;
					for (String word : cleanWords){
						
						if (extractLabels){
						
							if (!invertedFileWeakIndividuals.containsKey(word))
								invertedFileWeakIndividuals.put(word, new HashSet<Integer>());
													
							invertedFileWeakIndividuals.get(word).add(ident);
							
							//Add stemmed word
							stemmedWord = lexicalUtilities.getStemming4Word(word);
							if (stemmedWord.length()>2){ //minimum 3 characters
								
								if (!invertedFileWeakIndividuals.containsKey(stemmedWord))
									invertedFileWeakIndividuals.put(stemmedWord, new HashSet<Integer>());
														
								invertedFileWeakIndividuals.get(stemmedWord).add(ident);
								
							}
						}
						
					}
					
					
					//Exact IF
					if (cleanWords.size()>0){
												
						if (extractLabels){
							if (!invertedFileIndividuals.containsKey(cleanWords))
								invertedFileIndividuals.put(new HashSet<String>(cleanWords), new HashSet<Integer>());
							
							//LogOutput.print(invertedFileExactIndividuals.get(cleanWords) + " "+cleanWords + " "+ ident);
							invertedFileIndividuals.get(cleanWords).add(ident);
						}
						
						cleanWords.clear();
						cleanWordsList.clear();//not really necessary
						
					}
					
				}//if ident
				
			}//end for alabels
			
			
			
			
			
			
			
			
			//Deprecated: seems to be wrong
			//We add an alternative label including all alternative labels
			//This will only be used to extract isub score
			//-------------------------------
			/*String new_alabel="";
			for (String altLabel : index.getAlternativeLabels4IndividualIndex(ident)){
				new_alabel += altLabel + " ";
			}
			new_alabel = new_alabel.trim();
			if (new_alabel.length()>3)
				index.addAlternativeIndividualLabel(ident, new_alabel);
			*/
			
			
			
			//We add a better label
			if (!longestALabel.equals("")){
				index.setIndividualLabel(ident, longestALabel);
			}
			
			
			//altLabels.clear();
			
					
			//*****
			//Deprecated (August 28, 2017): check  LogMapCore.createAndAssessInstanceMappings and MappingManager.addInstanceMapping
			//*****
			
			//Extract class types.
			//We only extract direct types. Inferred types will be extracted later when setting up the taxonomic data.
			//TODO: No ancestors considered at this stage
			
			
			/*boolean showIndividualInOutput=false;
			
			for (OWLClassExpression clsexp : indiv.getTypes(onto)){
				
				if (!clsexp.isAnonymous()){
					if (class2identifier.containsKey(clsexp.asOWLClass())){
						index.addType4Individual(ident, class2identifier.get(clsexp.asOWLClass()));
						
						
						
						if (Parameters.allowed_instance_types.contains(clsexp.asOWLClass().getIRI().toString()))
							showIndividualInOutput=true;
					}
				}
				else if (clsexp.getClassExpressionType()==ClassExpressionType.OBJECT_UNION_OF){									
					for (OWLClassExpression clsexpunion : clsexp.asDisjunctSet()){
						if (!clsexpunion.isAnonymous()){
							if (class2identifier.containsKey(clsexpunion.asOWLClass())){
								index.addType4Individual(ident, class2identifier.get(clsexpunion.asOWLClass()));
								if (Parameters.allowed_instance_types.contains(clsexp.asOWLClass().getIRI().toString()))
									showIndividualInOutput=true;
							}
						}
					}
				}
				//other not considered yet
			
			}
			
			//IN SOME INSTANCE MATCHING TASKS ONLY IT IS REQUIRED TO DISCOVER INDIVIDUAL OF CERTAIN CLASSES
			index.setShowInOutput4Individual(ident, showIndividualInOutput);
			*/
			//Deprecated
			
			
			
			
			
			//In main extract instances and assess....
			
			//LogOutput.print("INDIVIDUALS " + id_onto + ", " +ident);
			/*
			//LogOutput.print("\t" + indiv.getIRI().toString());
			//LogOutput.print("\t" + index.getName4IndividualIndex(ident));
			//LogOutput.print("\t" + index.getLabel4IndividualIndex(ident));
			LogOutput.print("\t" + index.getIRIStr4IndividualIndex(ident));
			LogOutput.print("\t" + altLabels);
			
			LogOutput.print("\tTYPES: " + index.getIndividualClassTypes4Identifier(ident));
			LogOutput.print("\tIF size: " + invertedFileExactIndividuals.size());
			if (invertedFileExactIndividuals.size()<5){
				LogOutput.print("\tIF size: " + invertedFileExactIndividuals);
			}*/
			
			altLabels.clear();
			
			
		}//end for individuals
		
		
		
		
		//Another for to extract lexicon from other labels (relationships with other indiv or data)
		//Must be after wars since we require "inidividual2identifier"
		
		if (extractLabels){
		
			OAEI2015InstanceProcessing oaei2015extractor = new OAEI2015InstanceProcessing();
			
			for (OWLNamedIndividual indiv : onto.getIndividualsInSignature(Imports.INCLUDED)){//also imports
				
				//We do not want to match dummy individuals!
				if (dummyIndividualsSet.contains(indiv))
					continue;
				
				for (String str_label : roleAssertionLabelsExtractor.extractExtendedLexiconFromRoleAssertions(indiv)){
					
									
					if (!invertedFileRoleassertions.containsKey(str_label))
						invertedFileRoleassertions.put(str_label, new HashSet<Integer>());
											
					invertedFileRoleassertions.get(str_label).add(inidividual2identifier.get(indiv));
					
				}
				
				
				
				
				//ADD REFERENCED INDIVIDUALS
				//We extract the set of referenced individuals: Cities for lives_in, Paper for author_of, etc			
				//Map<OWLObjectPropertyExpression, Set<OWLIndividual>> objProp2values = indiv.getObjectPropertyValues(onto);
				Multimap<OWLObjectPropertyExpression,OWLIndividual> objProp2values = EntitySearcher.getObjectPropertyValues(indiv, onto);
				
				for (OWLObjectPropertyExpression objprop : objProp2values.keySet()){
									
					for (OWLIndividual indiv_deep2 : objProp2values.get(objprop)){
						
						if (indiv_deep2.isAnonymous())
							continue;
						
						index.addReferencedIndividual4Individual(
								inidividual2identifier.get(indiv), inidividual2identifier.get(indiv_deep2));
					}
				}//for obj prop
				
				
				///CHARACTERISTICS
				//Add charactersitics for Authors: e.g. num publications and citations
				//Characteristic 1: publication count
				index.addCharactersitic4Individual(inidividual2identifier.get(indiv), oaei2015extractor.getPublicationCount(onto, indiv));
				//Characteristic 2: Number of citations
				index.addCharactersitic4Individual(inidividual2identifier.get(indiv), oaei2015extractor.getNumberOfCitations(onto, indiv));
				//Characteristic 3: active from
				index.addCharactersitic4Individual(inidividual2identifier.get(indiv), oaei2015extractor.getActiveFromYear(onto, indiv));
				
				
				//if (index.getIdentifier2IndividualIndexMap().get(inidividual2identifier.get(indiv)).showInOutput())
				//		LogOutput.printAlways(indiv.getIRI() + " referred: " + index.getReferencedIndividuals4Individual(inidividual2identifier.get(indiv)).size());
				
				
				
				
				
				
				
				
				
			}
			
			
			
			
			
		}
		
		//not necessary any more
		inidividual2identifier.clear();
		dummyIndividualsSet.clear();
		
		
		LogOutput.print("NUMBER OF DUMMY INDIVIDUALS: " + num_dummy_indiv);
		
		
		LogOutput.print("MAX SIZE ANNOTATIONS: " + roleAssertionLabelsExtractor.max_size_name_label);
		LogOutput.print("MIN SIZE ANNOTATIONS: " + roleAssertionLabelsExtractor.min_size_name_label);
		
	}
	
	
	/**
	 * Changes order of alternative words labels
	 */
	private void changeOrderAltLabelWords(int ident, List<String> cleanWordsList){
		
		String original="";
		String changed="";
		
		//Add same altlabel without 
		if (cleanWordsList.size()>0){
			
			original = cleanWordsList.get(0);
			for (int i=1; i<cleanWordsList.size(); i++) {
				original += " " + cleanWordsList.get(i);
			}
			
			changed = cleanWordsList.get(cleanWordsList.size()-1);
			for (int i=cleanWordsList.size()-2; i>-1; i--) {
				changed += " " + cleanWordsList.get(i);
			}
			
			index.addAlternativeIndividualLabel(ident, original);
			index.addAlternativeIndividualLabel(ident, changed);
			
		}
		
		
	}
	
	
	
	
	Set<String> lexiconValues4individual = new HashSet<String>();
	
	/**
	 * See Class @ExtractAcceptedLabelsFromRoleAssertions
	 * @deprecated
	 */
	private Set<String> extractNameFromDataAssertion(OWLNamedIndividual indiv){
		
		lexiconValues4individual.clear();
		
		String label_value;
		
		
		//roleAsserationExtractor

		//TODO
		//ceate class
		
		for (String uri_indiv_ann : Parameters.accepted_data_assertion_URIs_for_individuals){
		
			
			for (OWLLiteral assertion_value : Searcher.values(onto.getDataPropertyAssertionAxioms(indiv), index.getFactory().getOWLDataProperty(IRI.create(uri_indiv_ann)))){
				
				label_value = assertion_value.getLiteral().toLowerCase();
				
				if (label_value.length()>2){
					lexiconValues4individual.add(label_value);
				}
				
			}
		}
		
		return lexiconValues4individual;
		
		
	}
	
	
	private Set<String> extractAnnotations4Infividual(OWLNamedIndividual indiv){
		
		lexiconValues4individual.clear();
		
		//System.out.println(indiv.getIRI());
		
		//for (OWLAnnotation ann : indiv.getAnnotations(onto)){
		//	System.out.println(ann);
		//}
		
		for (OWLAnnotationAssertionAxiom indivAnnAx : EntitySearcher.getAnnotationAssertionAxioms(indiv, onto)){
		
			//label_value = annotationExtractor.getAnntotationString(indivAnnAx);
			//String uri_ann = indivAnnAx.getAnnotation().getProperty().getIRI().toString();
			//System.out.println("\tIndiv label: '" + uri_ann + "'  " + label_value);
			
			
			
			//TODO Perform translation if necessary
			for (String label_value : annotationExtractor.getAnntotationString(indivAnnAx, onto, index.getFactory())){
				
				if (Parameters.allow_multilingual){			
					alternative_labels_tmp.addAll(translateLabel(indivAnnAx, label_value));
				}
				else{									
					alternative_labels_tmp.add(label_value);
				}
			
			}
						
			//Cleaning and indexing
			for (String label_value : alternative_labels_tmp){
				
				if (label_value.length()>2){
					lexiconValues4individual.add(label_value);
				}
			}
			alternative_labels_tmp.clear();
					
		}//end class ann axioms
		
		return lexiconValues4individual;
	}
	
	
	
	
	private List<String> processLabel(String label){
		return processLabel(label, false);
	}
	
	
	/**
	 * This processes the property label and alternative labels, and create inverted files
	 * Used for properties and individuals. Classes have a more sophisticated processing
	 * @param label
	 * @param ident
	 */
	private List<String> processLabel(String label, boolean filterStopwords){
				
		String label_value;
		List<String> cleanWords = new ArrayList<String>();
		String[] words;
		
		label_value=label.replace(",", "");
		label_value=label.replace("-", "");
		
		if (label_value.indexOf("_")>0){
			words=label_value.split("_");
		}
		else if (label_value.indexOf(" ")>0){ 
			words=label_value.split(" ");
		}
		//Split capitals...
		else{
			words=Utilities.splitStringByCapitalLetter(label_value);
		}
		
		//shift=1;				

		label_value="";
		
		for (int i=0; i<words.length; i++){
			
			words[i]=words[i].toLowerCase(); //to lower case
			
			//We optionally filter stopwords
			if (words[i].length()>0 && (!filterStopwords || !lexicalUtilities.getStopwordsSet().contains(words[i]))){
			//if (words[i].length()>0 && (!filterStopwords || !lexicalUtilities.getStopwordsSetExtended().contains(words[i]))){				
				cleanWords.add(words[i]);
			}			
		}		
		
		
		
		
		
		
		return cleanWords;
		
	}
	
	/**
	 * This class processes the label of P2 (an inverse property of P1) and
	 * returns a candidate alternative label for P1.
	 * e.g: hasPart -> partOf
	 * e.g: authorOf -> hasAuthor 
	 * e.g. writtenBy -> written (would be similar to writes)
	 * e.g. isGivenBy ->given (would be closer to gives) 
	 * @param label
	 * @return
	 */
	private List<String> processInverseLabel(String label){
		
		List<String> words = processLabel(label);
		
		int lastIndex = words.size()-1;
		
		String firstWord = words.get(0).toLowerCase();
		String lastWord = words.get(lastIndex).toLowerCase();
		
		//LogOutput.printAlways(words.toString());
		
		if (firstWord.equals("has")){
			words.remove(0);
			words.add("of");
		}
		else if (lastWord.equals("of")){
			words.remove(lastIndex);
			words.add(0, "has");
		}
		else if (lastWord.equals("by")){
			words.remove(lastIndex);
			if (firstWord.equals("is")){
				words.remove(0);
			}
		}
		else{
			words.clear();
		}
		
		//LogOutput.printAlways(words.toString());
		
		return words;
		
	}
	
	
	/**
	 * Create alternative label for a given property
	 * e.g. hasName -> name
	 * e.g. isReviewedBy - > hasReviewer
	 * @param label
	 * @return
	 */
	private List<String> createAlternativeLabel(String label){
		
		List<String> words = processLabel(label);
		
		int lastIndex = words.size()-1;
		
		String firstWord = words.get(0).toLowerCase();
		String lastWord = words.get(lastIndex).toLowerCase();
		
		//LogOutput.printAlways(words.toString());
		
		if (firstWord.equals("has")){
			words.remove(0);
		}
		else if (lastWord.equals("by")){
			words.remove(lastIndex);
			if (firstWord.equals("is")){
				words.remove(0);
			}
			words.add(0, "has");
		}
		else{
			words.clear();
		}
		
		//LogOutput.printAlways(words.toString());
		
		return words;
		
		
		
	}
	
	
	
	
	
	

	/**
	 * This methid extracts and processes the class label and alternative labels, and create inverted files
	 * @param cls
	 */
	private void createEntryInLexicalInvertedFiles4ClassLabels(OWLClass cls, int ident){
				
		
		Set<String> cleanWords = extractCleanLabel4OWLCls(cls, ident);
		Set<String> stemmed_words=new HashSet<String>();
		
		
		String[] words;
		
		String cleanAltLabel;
		
		String cleanReverseAltLabel;
		
		String stemmedWord;
		
		
		
		//From label
		//we also add cases one word in case they should be matched to combo
		if (cleanWords.size()>0){
			if (!invertedFileExact.containsKey(cleanWords))
				invertedFileExact.put(new HashSet<String>(cleanWords), new HashSet<Integer>());
			
			invertedFileExact.get(cleanWords).add(ident);
			
		}
		
		//lala
		
		for (String str : cleanWords){
			
			stemmedWord = lexicalUtilities.getStemming4Word(str);
			if (!stemmedWord.isEmpty()){
				stemmed_words.add(stemmedWord);
			}
			//if (lexicalUtilities.getStemming4Word(str).equals("")){
			//	LogOutput.print(str + "->  stemming: '" + lexicalUtilities.getStemming4Word(str) +"'");
			//}
			
		}
			
		if (!invertedFileWeakLabelsStemming.containsKey(stemmed_words))
			invertedFileWeakLabelsStemming.put(new HashSet<String>(stemmed_words), new HashSet<Integer>());
			
		invertedFileWeakLabelsStemming.get(stemmed_words).add(ident);
		
		//We add to internal structure
		//identifier2stemmedlabel.put(ident, new ArrayList<String>(stemmed_words));
		identifier2stemmedlabels.put(ident, new HashSet<List<String>>());
		identifier2stemmedlabels.get(ident).add(new ArrayList<String>(stemmed_words));
		
		stemmed_words.clear();		
		cleanWords.clear();
		
		String stemmedAltLabel;
		
		Set<Integer> temp;
		
		//ALTERNATE LABELS
		for (String altlabel_value : extractAlternateLabels4OWLCls(cls, ident)){
			
			//TODO Lower case altlabel_value?? And not to lower case in annotation extraction??
			//TODO REvise this with time...
			
						
			cleanAltLabel = "";
			cleanReverseAltLabel = "";
			stemmedAltLabel = "";
		
			if (altlabel_value.length()>2){
			
				words=altlabel_value.split("_");  //Already pre-processed to be '_'
				
				for (int i=0; i<words.length; i++){
					
					if (!lexicalUtilities.getStopwordsSet().contains(words[i]) && words[i].length()>0){ //words[i].length()>2 &&  Not for exact if: it may contain important numbers
							
						cleanWords.add(words[i]);
						
						if (cleanAltLabel.length()==0){
							cleanAltLabel = words[i];
							cleanReverseAltLabel = words[i];
						}
						else {
							cleanAltLabel+= "_" + words[i];
							cleanReverseAltLabel = words[i] + "_" + cleanReverseAltLabel;
						}
							
						stemmedWord = lexicalUtilities.getStemming4Word(words[i]);
						if (stemmedWord.isEmpty())
							continue;
												
						stemmedAltLabel += "_" + stemmedWord;
						/*
						if (frequency4words.containsKey(words[i]))
							frequency4words.put(words[i], 1);
						else 
							frequency4words.put(words[i], frequency4words.get(words[i]) + 1);
						*/
																		
						index.addWordOccurrence(stemmedWord, ident);
						
					}
				}//end words
				
				if (cleanWords.size()>0){
					if (!invertedFileExact.containsKey(cleanWords))
						invertedFileExact.put(new HashSet<String>(cleanWords), new HashSet<Integer>());
					
					invertedFileExact.get(cleanWords).add(ident);
					
					
					
					//We add to altrnative labels
					index.addAlternativeClassLabel(ident, cleanAltLabel);
					if (Parameters.reverse_labels){ //isub score slightly changes if labels are reversed
						index.addAlternativeClassLabel(ident, cleanReverseAltLabel);
					}
					if (!stemmedAltLabel.isEmpty()){
						//System.out.println(stemmedAltLabel);
						index.addStemmedAltClassLabel(ident, (stemmedAltLabel = stemmedAltLabel.substring(1)));
					}
				}
				
				
				//STEMMING
				for (String str : cleanWords){
					
					stemmedWord = lexicalUtilities.getStemming4Word(str);
					if (!stemmedWord.isEmpty()){
						stemmed_words.add(stemmedWord);
					}
					//if (lexicalUtilities.getStemming4Word(str).equals("")){
					//	LogOutput.print(str + "->  stemming: '" + lexicalUtilities.getStemming4Word(str) + "'" + stemmedWord.isEmpty());
					//}
					
				}
					
				if (!invertedFileWeakLabelsStemming.containsKey(stemmed_words))
					invertedFileWeakLabelsStemming.put(new HashSet<String>(stemmed_words), new HashSet<Integer>());
					
				invertedFileWeakLabelsStemming.get(stemmed_words).add(ident);
				
				//We add stemmin also for weak
				if (use_all_labels_for_weak_mappings)
					identifier2stemmedlabels.get(ident).add(new ArrayList<String>(stemmed_words));
				
				
				stemmed_words.clear();		
				cleanWords.clear();
			}
			
		}//Alt labels
		
		
			
		
	}
	
	
	
	
	private boolean isDeprecatedClass(OWLClass cls){
		
		//if (obsolete_classes.contains(cls))
		//	return true;
		
		for (OWLAnnotationAssertionAxiom annAx : EntitySearcher.getAnnotationAssertionAxioms(cls, onto)){
			
			if (annAx.getAnnotation().getProperty().getIRI().toString().equals(deprecated_uri)){
				
				String ann_label=((OWLLiteral)annAx.getAnnotation().getValue()).getLiteral().toLowerCase();
				
				if (ann_label!=null && ann_label.equals("true")){
					//obsolete_classes.add(cls);
					return true;
				}									
			}
		}
		return false;
	}

	
	/**
	 * Do not consider class names if they are identifiers
	 * @return
	 */
	private boolean isLabelAnIdentifier(String label_value){
		
		return label_value.matches(".+[0-9][0-9][0-9]+") 
				|| label_value.matches("[0-9][0-9][0-9][0-9][0-9]+-[0-9]+") //library ontologies
				//|| label_value.matches("[0-9]+\\.[0-9]+(\\.[0-9]+)+");//library ontologies
				|| label_value.matches("[0-9]+(\\.[0-9]+)+")
				|| label_value.matches(".+[0-9]+.+[0-9]+.+[0-9]+.+") //instance matching ontologies
				|| label_value.matches("[a-zA-Z][0-9]+"); //PROCES MODEL MATCHING;
		
	}
	
	
	/**
	 * Creates entry in exact occurrences map and adds label to class index
	 * @param cls
	 * @param ident
	 * @return
	 */
	private Set<String> extractCleanLabel4OWLCls(OWLClass cls, int ident){
		
		String label_value="";
		
		String entry4ifexact;
		Set<String> cleanWords=new HashSet<String>();
		
		String[] words;
		
		int ambiguity_ident=1;
		
		//System.out.println(cls);
		//System.out.println(cls.getIRI().toString());
		
		String ann_label;
		
		//Check if concept name is meaningful (not an ID)
		label_value = index.getIdentifier2ClassIndexMap().get(ident).getEntityName(); 
		//if (label_value.matches(".+[0-9][0-9][0-9]+")){
		
		if (isLabelAnIdentifier(label_value)){
			
			//Otherwise We look for first non empty label (if no label we keepID)
			//---------------------------------------------------------------------
			for (OWLAnnotationAssertionAxiom annAx : EntitySearcher.getAnnotationAssertionAxioms(cls, onto)){
				
				//listchanges.add(new RemoveAxiom(onto, annAx)); //We remove all annotations
				
				if (annAx.getAnnotation().getProperty().getIRI().toString().equals(rdf_label_uri)){
					
					//LogOutput.print(((OWLLiteral)annAx.getAnnotation().getValue()).getLiteral());
					ann_label=((OWLLiteral)annAx.getAnnotation().getValue()).getLiteral();//.toLowerCase();
					
					if (ann_label!=null && !ann_label.equals("") && !ann_label.equals("null")){
						label_value=ann_label;
					}
					
					//LogOutput.print("Label: " + label_value);
					break;
					
				}

			}
			
		}
		
		
		
		
		//If it doesn't exist then we use entity name
		//if (label_value.equals("")){
		//	label_value=index.getIdentifier2ClassIndexMap().get(ident).getEntityName();
		//}
		
		
		//System.out.println(label_value);
		
		label_value=label_value.replace(",", "");
		
		
		if (label_value.startsWith("_")){
			label_value = label_value.substring(1, label_value.length());
		}
		if (label_value.endsWith("_")){
			label_value = label_value.substring(0, label_value.length()-1);
		}
		
		
		if (label_value.indexOf("_")>0){ //NCI and SNOMED
			words=label_value.split("_");
		}
		else if (label_value.indexOf(" ")>0){ //FMA
			words=label_value.split(" ");
		}
		//Split capitals...
		else{
			//label_value = Utilities.capitalPrepositions(label_value);
			words=Utilities.splitStringByCapitalLetter(label_value);
		}
		//else {
		//	words=new String[1];
		//	words[0]=label_value;
		//}
		
		//To lowercase
		
		
		
		
		//shift=1;				

		label_value="";
		entry4ifexact="";
		for (int i=0; i<words.length; i++){
			
			words[i]=words[i].toLowerCase(); //to lower case
			
			if (words[i].length()>0){
			
				//For IF
				entry4ifexact+=words[i];
				
				//For label
				label_value+=words[i] + "_";
				
				if (!lexicalUtilities.getStopwordsSet().contains(words[i])){ 
					//words[i].length()>2 &&  Not for exact IF: it may contain important numbers					
					cleanWords.add(words[i]);
				}				
			}			
		}
		
		
		//Check length!! or if it contains "_"
		if (label_value.length()>0){
			
			label_value = label_value.substring(0, label_value.length()-1);
			
			//Add to class index
			index.setClassLabel(ident, label_value);
		}
		else{
			//we add the whole IRI
			index.setClassLabel(ident, cls.getIRI().toString());
		}
		
		//System.out.println(cls.getIRI());
		//System.out.println(label_value);
		//System.out.println(cleanWords);
		
		return cleanWords;
		
		//Add to IF: not used any more
		/*if (entry4ifexact.length()>2){//Min 3 characteres
			
			//Ambiguity in labels of ontology (use concept name)
			if (extact_occurrence_entries.containsKey(entry4ifexact)){ 
				
				ambiguity_ident=extact_occurrence_entries.get(entry4ifexact);
				//We remove previous one
				extact_occurrence_entries.remove(entry4ifexact);
				
				//We create two news
				extact_occurrence_entries.put(getProcessedName4ConceptIndex(ambiguity_ident), ambiguity_ident);
				extact_occurrence_entries.put(getProcessedName4ConceptIndex(ident), ident);
				
				
				
			}
			else{
				extact_occurrence_entries.put(entry4ifexact, ident);
			}
		}*/
		
		
		
	}
	
	
	
	

	
	
	private Set<String> labels_set = new HashSet<String>();
	
	private void considerLabel(String label_value){
		
		labels_set.addAll(extendAlternativeLabel(label_value));
		
		//Expand with UMLS Lex Spelling variants
		if (lexicalUtilities.hasSpellingVariants(label_value)){
		//if (spelling_variants_map.containsKey(label_value)){
			for (String variant : lexicalUtilities.getSpellingVariants(label_value)){
			//for (String variant : spelling_variants_map.get(label_value)){
				labels_set.addAll(extendAlternativeLabel(variant));
			}
		}
	}
	
	

	
	
	/**
	 * Return alternate labels
	 * They could be represented using different annotation properties
	 * @param cls
	 * @return
	 */
	private Set<String> extractAlternateLabels4OWLCls(OWLClass cls, int ident){
		
		
		String label_value="";
		OWLAnonymousIndividual geneid_value;
		
		OWLNamedIndividual namedIndiv=null;
		IRI namedIndivIRI;
		
		
		//REINIT for each label
		labels_set.clear();
		
		
		//Use concept name as well!! Some times it is different to label
		//Avoid identifiers as concept names
		label_value = index.getIdentifier2ClassIndexMap().get(ident).getEntityName().toLowerCase();
		
		//if (!label_value.matches(".+[0-9][0-9][0-9]+")){
		if (!isLabelAnIdentifier(label_value)){
		
			//And extend with variants
			considerLabel(label_value);
			
			//LogOutput.print("name2Label: " + labels_set);
			
		}
		
		
		
		//LogOutput.print("GENIEID");
		
		for (OWLAnnotationAssertionAxiom clsAnnAx : EntitySearcher.getAnnotationAssertionAxioms(cls, onto)){
			
			//listchanges.add(new RemoveAxiom(onto, clsAnnAx)); //We remove all annotations
			
			//label_value = annotationExtractor.getAnntotationString(clsAnnAx);
			
			//LogOutput.printError(clsAnnAx.toString());
			
			
			//TODO Perform translation if necessary
			for (String l_value : annotationExtractor.getAnntotationString(clsAnnAx, onto, index.getFactory())){
				
				if (Parameters.allow_multilingual){			
					alternative_labels_tmp.addAll(translateLabel(clsAnnAx, l_value));
				}
				else{									
					alternative_labels_tmp.add(l_value);
				}
			
			}
						
			//Cleaning and indexing			
			for (String l_value : alternative_labels_tmp){
											
				//LogOutput.print("\t  " + l_value);
				
				if (l_value.length()>2){
					considerLabel(l_value);
				}
			}			
			alternative_labels_tmp.clear();
			
		}//end class ann axioms
		
		//LogOutput.print("\t" + labels_set.size());
		num_syn=num_syn+labels_set.size();
		
		return labels_set;
		
		
		
	}
	
	
	//int num_labels=0;
	

	
	
	
	
	
	/**
	 * Process given label (synonym) and extendes with wordnet synonyms
	 * @param label_value
	 */
	private Set<String> extendAlternativeLabel(String label_value){
		
		Set<String> set_syn = new HashSet<String>();
		
		List<Set<String>> wordi2syn = new ArrayList<Set<String>>(); 
		
		String[] words;
		int shift=1;
		
		String roman;
		
		//Replace "/" by _
		label_value=label_value.replaceAll("/", "_");
		
		if (label_value.indexOf(" ")>0){ //Synonyms
			words=label_value.split(" ");
		}
		else if (label_value.indexOf("_")>0){ //just in case
			words=label_value.split("_");
		}
		//Split capitals...
		else{
			words=Utilities.splitStringByCapitalLetter(label_value); //quite unlikely for alt labels
		}
		//else {
		//	words=new String[1];
		//	words[0]=label_value;
		//}
		
		
		shift=1;
		
		for (int i=0; i<words.length; i++){
			
			set_syn.add(words[i].replace(",", ""));
			
			//Synonym from wordnet: they are a bit noisy
			//if (IndexingUtilities.getLabel2wordnetsyn().containsKey(words[i])){
			//	set_syn.addAll(IndexingUtilities.getLabel2wordnetsyn().get(words[i]));
			//}

			//We use normalization from UMLS Specialist Lexicon 
			if (lexicalUtilities.hasNormalization(words[i])){
			//if (normalization_map.containsKey(words[i])){
				set_syn.addAll(lexicalUtilities.getNormalization(words[i]));
				//set_syn.addAll(normalization_map.get(words[i]));
			}
			
			
			//*****EXTEND WITH STEMMING
			//if(IndexingUtilities.getStemmingMap().containsKey(words[i])) {
				//set_syn.add(IndexingUtilities.getStemmingMap().get(words[i]));
			//}			
			else if (lexicalUtilities.isStemmingUp()){//Only if no normalization??
				set_syn.add(lexicalUtilities.getStemming4Word(words[i]));
			}
			
			//We normaliza numbers
			roman = lexicalUtilities.getRomanNormalization4Number(words[i]);
			if (!roman.equals("")){
				//LogOutput.print("\tROMAN   " +words[i] +"  " +  roman);
				set_syn.add(roman); 
			}
			
			
			
			wordi2syn.add(new HashSet<String>(set_syn));
			set_syn.clear();
		}
		
		
		long comb=1;
		
		//Too many combinations.... (max=50)
		for (Set<String> set : wordi2syn){
			comb=comb*set.size();
		}
	
		//LogOutput.print(comb);
		
		String label;
		if (comb>50 || comb<0){
			
			toohigh_synset_cases++;
			
			label="";
			for (int i=0; i<words.length-shift; i++){
				label+=words[i] + "_";
			}
			
			label+=words[words.length-shift];
			//LogOutput.print("\t" + label_value);
			set_syn.add(label);
			return set_syn;
		}				
		else { //We get combinations
			
			if (wordi2syn.size()==1){
				return wordi2syn.get(0);
			}
			
			return combineWordSynonyms(wordi2syn, wordi2syn.get(0), 1); 
		
		}		
		
	}
	
	
	
	
	
	private Set<String> combineWordSynonyms(List<Set<String>> wordi2syn, Set<String> currentSet, int index){
		
		Set<String> newSet = new HashSet<String>();
		
		for (String clabel: currentSet){
			
			for (String syn: wordi2syn.get(index)){
				
				newSet.add(clabel+ "_"+syn);
				//LogOutput.print(clabel+ "_"+syn);
				
			}
		}
		
		if (wordi2syn.size()<=index+1){
			return newSet;
		}
		else{
			return combineWordSynonyms(wordi2syn, newSet, index+1);
		}
		
		
	}
	
	
	
	/**
	 * Entries not matched in IF for stemmed labels
	 * @param entries
	 */
	public void addEntries2InvertedFileWeakLabels(Map<Set<String>, Set<Integer>> entries){
		
		invertedFileWeakLabels.putAll(entries);
		
		
	}
	
	
	public void setInvertedFileWeakLabels(){
		
		//List<String> list_words;
		
		//TODO: Default values
		int max_size_labels=8;
		int max_size_list_words_missing=3;
		
		
		//for (int ident : identifier2stemmedlabel.keySet()){
		for (int ident : identifier2stemmedlabels.keySet()){
			
			//list_words = identifier2stemmedlabel.get(ident);
			for (List<String> list_words : identifier2stemmedlabels.get(ident)){
		
			if (list_words.size()>max_size_labels)
				continue;
			
			if (list_words.size()>1){ //Smaller case 1 out of 2
				
				createWeakLabels4Identifier(list_words, ident, 1);// 1 missing word
				
				if (list_words.size()>3 && max_size_list_words_missing>1){ //Smaller case 2 out of 4
					
					createWeakLabels4Identifier(list_words, ident, 2);
					
					if (list_words.size()>5 && max_size_list_words_missing>2){ //Smaller case 3 out of 6
					
						createWeakLabels4Identifier(list_words, ident, 3);
					
						if (list_words.size()>7 && max_size_list_words_missing>3){ //Smaller case 4 out of 8
					
							createWeakLabels4Identifier(list_words, ident, 4);
						}
					}
				}
			}
			}
		}
	}


	/**
	 * Considers all labels from IF stemmed
	 * @deprecated review... something is wrong...
	 */
	public void setFullInvertedFileWeakLabels(){
		
		List<String> list_words = new ArrayList<String>();
		Set<Integer> identifiers;
		
		
		//TODO: Default values
		int max_size_labels=8;
		int max_size_list_words_missing=3;
		
		
		for (Set <String> stemmed_set : invertedFileWeakLabelsStemming.keySet()){
		
			if (stemmed_set.size()>max_size_labels)
				continue;
			
			list_words.addAll(stemmed_set);
			
			identifiers=invertedFileWeakLabelsStemming.get(stemmed_set);
			
			if (list_words.size()>1){ //Smaller case 1 out of 2
				
				createWeakLabels4Identifier(list_words, identifiers, 1);// 1 missing word
				
				if (list_words.size()>3 && max_size_list_words_missing>1){ //Smaller case 2 out of 4
					
					createWeakLabels4Identifier(list_words, identifiers, 2);
					
					if (list_words.size()>5 && max_size_list_words_missing>2){ //Smaller case 3 out of 6
					
						createWeakLabels4Identifier(list_words, identifiers, 3);
					
						if (list_words.size()>7 && max_size_list_words_missing>3){ //Smaller case 4 out of 8
					
							createWeakLabels4Identifier(list_words, identifiers, 4);
						}
					}
				}
			}
			list_words.clear();
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
		
		//if (combination_set.size()>10)
		//	System.out.println(cleanWords);
			
		
		for(Set<Integer> toExclude : combination_set){
			
			for (int pos=0; pos<cleanWords.size(); pos++){
				if (!toExclude.contains(pos))
					combo.add(cleanWords.get(pos));
				
			}
			
			//TODO: evaluate if set of words of combo are meaningful
			//are they too frequent??
			if (!invertedFileWeakLabels.containsKey(combo))
				invertedFileWeakLabels.put(new HashSet<String>(combo), new HashSet<Integer>());
			
			invertedFileWeakLabels.get(combo).add(ident);
			
			combo.clear();
			
		}
		
		
	}
	
	
	/**
	 * Combines the words in given list with 'x' missing words and stores the results in IF
	 * 
	 * @param cleanWords Clean label of concept
	 * @param ident Identifier of concepts
	 * @param missing_words Number of words to be discarded 
	 */
	private void createWeakLabels4Identifier(List<String> cleanWords, Set<Integer> identifiers, int missing_words){
		
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
			
			invertedFileWeakLabels.get(combo).addAll(identifiers);
			
			combo.clear();
			
		}
		
		
	}
	
	
	
	
	public int getIdentifier4ConceptName(String name){ 
		if (className2Identifier.containsKey(name))
			return className2Identifier.get(name);
		else
			return -1;
		
	}
	
	
	public int getIdentifier4ObjectPropName(String name){ 
		if (objectPropName2Identifier.containsKey(name))
			return objectPropName2Identifier.get(name);
		else
			return -1;
	}
	
	public int getIdentifier4DataPropName(String name){ 
		if (dataPropName2Identifier.containsKey(name))
			return dataPropName2Identifier.get(name);
		else
			return -1;
	}
	
	public int getIdentifier4InstanceName(String name){ 
		if (individualName2Identifier.containsKey(name))
			return individualName2Identifier.get(name);
		else
			return -1;
	}
	

	
	public Map<Set<String>, Set<Integer>> getInvertedFileExactMatching(){
		return invertedFileExact;
	}
	
	public Map<Set<String>, Integer> getInvertedFileExactMatching4DataProp(){
		return invertedFileExactDataProp;
	}
	
	public Map<Set<String>, Integer> getInvertedFileExactMatching4ObjProp(){
		return invertedFileExactObjProp;
	}
	
	
	public Map<Set<String>, Set<Integer>> getInvertedFileMatching4Individuals(){
		return invertedFileIndividuals;
	}
	
	public Map<String, Set<Integer>> getInvertedFileRoleAssertions(){
		return invertedFileRoleassertions;
	}
	
	//Weak IF
	public Map<String, Set<Integer>> getInvertedFileWeakMatching4Individuals(){
		return invertedFileWeakIndividuals;
	}
	
	
	public Map<Set<String>, Set<Integer>> getInvertedFileWeakLabelsStemming(){	
		return invertedFileWeakLabelsStemming;
	}
	
	public Map<Set<String>, Set<Integer>> getInvertedFileWeakLabels(){	
		return invertedFileWeakLabels;
	}
	
	
	
	/**
	 * Extract taxonomies
	 */
	public void setTaxonomicData() {
		
		//TODO
		//Only SNOMED has been pre-classified with Condor
		//Note that in the classification we just have information about the hierarchy
		/*if (isOntoPreclassified){
			init=Calendar.getInstance().getTimeInMillis();
			unloadOntology();
			loadOWLOntology(physical_iri_class);
			fin = Calendar.getInstance().getTimeInMillis();
			LogOutput.print("Time unloading Ontology and loading pre-classified ontology (s): " + (float)((double)fin-(double)init)/1000.0);
		}*/
		
		
		//init=Calendar.getInstance().getTimeInMillis();
		if (Parameters.reasoner.equals(Parameters.hermit)){
			setUpReasoner_HermiT();
		}
		//else if (Parameters.reasoner.equals(Parameters.more)){ //MORe
		//	setUpReasoner_MORe();
		//
		else if (Parameters.reasoner.equals(Parameters.elk)){ //eld
			setUpReasoner_ELK();
		}
		else{
			setUpStructuralReasoner();
		}
		
		//fin = Calendar.getInstance().getTimeInMillis();
		//LogOutput.print("Time classifying ontology (s): " + (float)((double)fin-(double)init)/1000.0);
		
		extractDangerousClasses();
		
		//init=Calendar.getInstance().getTimeInMillis();
		extractStringTaxonomiesAndDisjointness();
		//fin = Calendar.getInstance().getTimeInMillis();
		//LogOutput.print("Time Extracting Taxonomy (s): " + (float)((double)fin-(double)init)/1000.0);
		
		init = Calendar.getInstance().getTimeInMillis();
		extractGeneralHornAxioms(); //A^...^B->C
		fin = Calendar.getInstance().getTimeInMillis();
		LogOutput.print("Extracting General Axioms: " + (float)((double)fin-(double)init)/1000.0);
		
		

		//Extract
		if (Parameters.perform_instance_matching){			
			//We need the inferred types
			extractInferredTypes4Individuals();
			
		}
		
	}

	
	
	/**
	 * Structural reasoner
	 */
	private void setUpStructuralReasoner(){
		
		try {	
			reasoner = getIncompleteReasoner();
		}
		catch(Exception e){
			LogOutput.printError("Error setting up Structural reasoner: " + e.getMessage());
			e.printStackTrace();
		}
	}

	
	
	/*
	 * MORe or Structural reasoner	 
	private void setUpReasoner_MORe() {
		
		MOReAccess moreAccess=null;
				
		//Try first with Hermit if classifies in more than x seconds or error then structural  
				
		try {
		
						
			moreAccess = new MOReAccess(
					SynchronizedOWLManager.createOWLOntologyManager(),
					onto, false); //with factory i got problems
			
			
			//Timeout is not properly handled with MORe
			//moreAccess.classifyOntology_withTimeout_throws_Exception(17); 
			//We then do the timeout internally in MORe only over HermiT (or the OWL 2 reasoner)
			//MORe access invokes a version or MORe with timeout
			//moreAccess.classifyOntology(false); //no props
			moreAccess.classifyOntology_withTimeout_throws_Exception(Parameters.timeout); //in case of error or timeout
			
						
			if (moreAccess.isOntologyClassified()){
				reasoner = moreAccess.getReasoner();				
			}
			else {				
				LogOutput.print("Onto not classified with MORe. Using 'structural' reasoner instead.");
				reasoner = getIncompleteReasoner();
			}
				
			if (reasoner==null){
				LogOutput.print("Reasoner was null. Using 'structural' reasoner instead.");
				reasoner = getIncompleteReasoner();
			}
				
			
		}
		catch(Exception e){
			
			try {
				LogOutput.print("Error/timeout setting up MORe reasoner. Using 'structural' reasoner instead.");//\n\n" + e.getMessage() + "\n");
				
				reasoner = getIncompleteReasoner();
			}
			catch(Exception e2){
				System.err.println("Error setting up Structural reasoner: " + e2.getMessage());
				e2.printStackTrace();
			}
			
		}
		
		//if (reasoner.getReasonerName()!=null){
			//LogOutput.print("Reasoner name: " + reasoner.isConsistent() + ".\n");
			//LogOutput.print("Reasoner name: " + reasoner + ".\n");
			//LogOutput.print("Reasoner name: " + reasoner.getReasonerName() + ".\n");
		//}
	}*/
	
	
	
	/**
	 * HermiT or Structural reasoner
	 */
	private void setUpReasoner_HermiT() {
		
		HermiTAccess hermitAccess;
				
		//Try first with Hermit if classifies in more than x seconds or error then ELK  
				
		try {
		
			if (Parameters.reason_datatypes || onto.getDatatypesInSignature(Imports.INCLUDED).size()==0){
			//if (true){
				//hermitAccess = new HermiTReasonerAccess(onto, false);
				hermitAccess = new HermiTAccess(
						SynchronizedOWLManager.createOWLOntologyManager(),
						onto, false); //with factory i got problems
				
				hermitAccess.classifyOntology_withTimeout_throws_Exception(Parameters.timeout); //in case of error or timeout
							
				if (hermitAccess.isOntologyClassified()){
					reasoner = hermitAccess.getReasoner();				
				}
				else {				
					LogOutput.print("Onto not classified with HermT. Using 'ELK' reasoner instead.");
					setUpReasoner_ELK();
				}
			}
			else { //With OMT client gives an error :-(
				LogOutput.print("Ontology with datatypes. Using 'ELK' reasoner instead.");
				setUpReasoner_ELK();
			}
			
			if (reasoner==null){
				LogOutput.print("Reasoner was null. Using 'ELK' reasoner instead.");
				setUpReasoner_ELK();
			}
				
			
		}
		catch(Exception e){
			
			try {
				LogOutput.print("Error/timeout setting up HermiT reasoner. Using 'ELK' reasoner instead.");//\n\n" + e.getMessage() + "\n");
				//e.printStackTrace();
				setUpReasoner_ELK();
			}
			catch(Exception e2){
				LogOutput.printError("Error setting up ELK reasoner: " + e2.getMessage() + ". Using structural reasoner instead.");
				setUpStructuralReasoner();
			}
			
		}
		
		//if (reasoner.getReasonerName()!=null){
			//LogOutput.print("Reasoner name: " + reasoner.isConsistent() + ".\n");
			//LogOutput.print("Reasoner name: " + reasoner + ".\n");
			//LogOutput.print("Reasoner name: " + reasoner.getReasonerName() + ".\n");
		//}
	}
	
	
	
	/**
	 * ELK or Structural reasoner
	 */
	private void setUpReasoner_ELK() {
		
		CheckOWL2Profile profileChecker = new CheckOWL2Profile();
		
		//We identified some logical errors when using ELK, it seems the classification that provides is less complete than the structural reasoner
		//when the ontology is not EL!!!
		if (!profileChecker.isInOWL2ELProfile(onto)){
			LogOutput.printAlways(onto.getOntologyID().getOntologyIRI().get() + " NOT in OWL 2 EL profile. Using structural reasoner.");
			setUpStructuralReasoner();
			return;
		}
		
		LogOutput.printAlways(onto.getOntologyID().getOntologyIRI() + " in OWL 2 EL profile. Using ELK reasoner.");
		
		ELKAccess elkAccess;
				
		//Try first with ELK if classifies in more than x seconds or error then structural
		
			
				
		try {
		
			elkAccess = new ELKAccess(
					SynchronizedOWLManager.createOWLOntologyManager(),
					onto, false); //with factory i got problems
				
			elkAccess.classifyOntologyNoProperties();
							
			if (elkAccess.isOntologyClassified()){
				reasoner = elkAccess.getReasoner();				
			}
			else {				
				LogOutput.print("Onto not classified with ELK. Using 'structural' reasoner instead.");
				//reasoner = new StructuralReasonerExtended(onto);
				reasoner = getIncompleteReasoner();
			}
			
			if (reasoner==null){
				LogOutput.print("Reasoner was null. Using 'structural' reasoner instead.");
				//reasoner = new StructuralReasonerExtended(onto);
				reasoner = getIncompleteReasoner();
			}
				
			
		}
		catch(Exception e){
			
			try {
				LogOutput.print("Error/timeout setting up ELK reasoner. Using 'structural' reasoner instead.");//\n\n" + e.getMessage() + "\n");
				//reasoner = new StructuralReasonerExtended(onto);
				reasoner = getIncompleteReasoner();
			}
			catch(Exception e2){
				LogOutput.printError("Error setting up Structural reasoner: " + e2.getMessage());
			}
			
		}
		
		//if (reasoner.getReasonerName()!=null){
			//LogOutput.print("Reasoner name: " + reasoner.isConsistent() + ".\n");
			//LogOutput.print("Reasoner name: " + reasoner + ".\n");
			//LogOutput.print("Reasoner name: " + reasoner.getReasonerName() + ".\n");
		//}
	}
	
	
	private OWLReasoner getIncompleteReasoner() throws Exception{
		
		return new StructuralReasonerExtended(onto);
		/*ELKAccess elk = new ELKAccess(
				SynchronizedOWLManager.createOWLOntologyManager(),
				onto, false);
		return elk.getReasoner();*/
	}
	
	
	
	/**
	 * Dangerous class such that the ones equivalent to Top
	 */
	private void extractDangerousClasses(){
		
		//System.out.println(reasoner.getTopClassNode().getEntitiesMinusTop());
		
		for (OWLClass cls : reasoner.getTopClassNode().getEntitiesMinusTop()){
			System.out.println(cls.toString());
			dangerousClasses.add(class2identifier.get(cls));
			index.addDangerousClasses(class2identifier.get(cls));
		}
		
		for (int ide : dangerousClasses){
			LogOutput.print("DANGEROUS CLASS == TOP: "+ index.getName4ConceptIndex(ide));
		}
	}
	
	public Set<Integer> getDangerousClasses(){
		return dangerousClasses;
	}
	
	
	
	/**
	 * Create taxonomy (for interval labeling), and disjointness and equivalence (for internal control)
	 * @throws Exception
	 */
	private void extractStringTaxonomiesAndDisjointness() {
		
		
		//onto.
		
		int bignode=0;
		
		int equiv=0;
		int disj=0;
		
		//For all nodes
		Map<Node<OWLClass>,Integer> node2identifier = new HashMap<Node<OWLClass>,Integer>();
		
		int identRepresentative;
		int ident1;
		int ident2;
		//String identRepresentativeStr;
		OWLClass clsRepresentative; 
		
		
		
		//TOP CONCEPTS 
		NodeSet<OWLClass> topClasses = reasoner.getSubClasses(reasoner.getTopClassNode().getRepresentativeElement(), true);
		//Real root nodes
		//RootIdentifiers.clear();
		for (OWLClass cls : topClasses.getFlattened()){
			if (class2identifier.containsKey(cls)){
				//RootIdentifiers.add(class2identifier.get(cls));
				index.addRoot2Structure(class2identifier.get(cls));
			}
		}
		
		
		//All Subclasses
		NodeSet<OWLClass> nodeSet = reasoner.getSubClasses(reasoner.getTopClassNode().getRepresentativeElement(), false);
		
		
		
		//LogOutput.print(reasoner.getTopClassNode().getRepresentativeElement() + " " + nodeSet.getNodes().size());
		
		
		//We first check node size
		for (Node<OWLClass> node : nodeSet){
			
			if (node.isBottomNode() || node.isTopNode())
				continue;
			
			clsRepresentative=node.getRepresentativeElement();
			
			//Important to avoid non-class nodes like "DIRECTED-BINARY-RELATION" and also deprecated classes
			if (!class2identifier.containsKey(clsRepresentative)){
				//System.out.println("No id for class representative: " + clsRepresentative);
				continue;
			}
			
			identRepresentative=class2identifier.get(clsRepresentative);
			//WE WANT TO GUARANTEE THAT REPRESENTATIVE IS ALWAYS THE SAME
			node2identifier.put(node,identRepresentative);
			
			//Only for 'big' nodes
			if (node.getEntities().size()>1){
				bignode++;
				//representativeNodes.add(identRepresentative);
				index.addRepresentativeNode(identRepresentative);
			}
		}
		
		
		
		
		//TODO Meaningful roots and level are candidates to be deprecated
		//MEANINGFUL ROOTS: to avoid cases like SNOMED where there is an unique root apart from THING
		//This method should be used only for big ontologies!!
		//extractMeaningfulRoots();
		//LogOutput.print("Number of roots: " + MeaningfulRootIdentifiers.size());
		
		
		//HIERARCHY LEVELS FOR ENTITIES
		//extractHierarchyLevel(reasoner.getSubClasses(reasoner.getTopClassNode().getRepresentativeElement(), true), 1);
		
		
		for (Node<OWLClass> node : nodeSet){
			
			if (node.isBottomNode() || node.isTopNode())
				continue;
			
			if (!node2identifier.containsKey(node)) //Possibly deprecated class
				continue;
			
			
			
			
			identRepresentative = node2identifier.get(node);
			//clsRepresentative = identifier2class.get(identRepresentative);
			clsRepresentative = node.getRepresentativeElement();
			
			
			//Add root to class index (only representative?) Deprecated
			//index.getClassIndex(identRepresentative).setRoots(getMeaningfulRootsForIdentifier(identRepresentative));
			
			
			
			
			//SUBCLASSES: TAXONOMY
			if (!reasoner.getSubClasses(clsRepresentative, true).isEmpty()){
						
				//identifier2directkids.put(identRepresentative, new HashSet<Integer>());
				//Only representatives??
				index.getClassIndex(identRepresentative).setEmptyDirectSubClasses();
				
				for (Node<OWLClass> nodeSub : reasoner.getSubClasses(clsRepresentative, true).getNodes()){
					if (nodeSub.isBottomNode() || nodeSub.isTopNode())
						continue;
					
					//Required if filtered obsolete classes
					if (!node2identifier.containsKey(nodeSub))
						continue;
					
					//identifier2directkids.get(identRepresentative).add(node2identifier.get(nodeSub));//will give us direct kid identifiers
					//System.err.println(nodeSub);
					index.getClassIndex(identRepresentative).addDirectSubClass(node2identifier.get(nodeSub));
					
					
				}
			}
			

			//SUPERCLASSES: REVERSE TAXONOMY
			if (!reasoner.getSuperClasses(clsRepresentative, true).isEmpty()){
				
				//identifier2directparents.put(identRepresentative, new HashSet<Integer>());
				index.getClassIndex(identRepresentative).setEmptyDirectSuperClasses();
				
				for (Node<OWLClass> nodeSup : reasoner.getSuperClasses(clsRepresentative, true).getNodes()){
					if (nodeSup.isTopNode() || nodeSup.isBottomNode())
						continue;
					
					//identifier2directparents.get(identRepresentative).add(node2identifier.get(nodeSup));  //direct parents
					//System.out.println(identRepresentative + "  " + nodeSup + "  " + node2identifier.get(nodeSup));
					//Required if filtered obsolete classes
					if (!node2identifier.containsKey(nodeSup))
						continue;
					
					index.getClassIndex(identRepresentative).addDirectSuperClass(node2identifier.get(nodeSup));
					
					
				}
			}
			
			
			//WE STORE DISJ AND EQUIVALENCES FOR ALL NODE ENTITIES
			
			//DISJOINTNESS
			for (OWLClass nodeClass : node.getEntities()){ //for all node classes
				
				//Avoid top or nothing
				if (nodeClass.isTopEntity() || nodeClass.isBottomEntity())
					continue;
				
				//We check for each class. differnt disjoint axioms may affect different classes of same node
				//The reasoner has been adapted to extract only explicit disjointness
				//since implicit (all) disjointness is time conuming
				if (!reasoner.getDisjointClasses(nodeClass).isEmpty()){
					
					ident1=class2identifier.get(nodeClass);
								
					index.getClassIndex(ident1).setEmptyDisjointClasses();
					disj++;
					
					//LogOutput.print(nodeClass);
					//LogOutput.print("\t" + reasoner.getDisjointClasses(nodeClass).getNodes());
					
					for (Node<OWLClass> nodeDisj : reasoner.getDisjointClasses(nodeClass).getNodes()){
						
						for (OWLClass disjcls : nodeDisj.getEntities()){ //We add all
							
							//Avoid top or nothing
							if (disjcls.isTopEntity() || disjcls.isBottomEntity())
								continue;
							
							//To avoid non indexed, obsolete classes
							if (!class2identifier.containsKey(disjcls))
								continue;
							
							//TODO To avoid classes being disjoint with themselves
							if (ident1!=class2identifier.get(disjcls)){ 
								index.getClassIndex(ident1).addDisjointClass(class2identifier.get(disjcls));
								//Add both sides??
								//TODO:  //Not sure if neccessary
								index.getClassIndex(class2identifier.get(disjcls)).addDisjointClass(ident1);
							}
						}
					}					
				}
			}
			
			//EQUIVELNCE
			if (node.getEntities().size()>1){
				OWLClass[] nodeClasses= new OWLClass[node.getEntities().size()];
				nodeClasses = node.getEntities().toArray(nodeClasses);
				//LogOutput.print(nodeClasses[0]);
				
				for (int i=0; i<nodeClasses.length; i++){
					ident1=class2identifier.get(nodeClasses[i]);
					index.getClassIndex(ident1).setEmptyEquivalentClasses();
					equiv++;
					
					for (int j=0; j<nodeClasses.length; j++){
						if (i==j)
							continue;
						
						ident2=class2identifier.get(nodeClasses[j]);
						
						index.getClassIndex(ident1).addEquivalentClass(ident2);
						
						//Propagation of disjointness
						if (index.getClassIndex(ident1).hasDirectDisjointClasses()){
							
							if (!index.getClassIndex(ident2).hasDirectDisjointClasses()){
								index.getClassIndex(ident2).setEmptyDisjointClasses();
							}
							
							index.getClassIndex(ident2).addAllDisjointClasses(index.getClassIndex(ident1).getDisjointClasses());
						}
						
					}
				}
				
				
			}
		}
		
		
					
		node2identifier.clear();
		
		LogOutput.print("Representatives (aggregated): " + index.getRepresentativeNodes().size());
		LogOutput.print("Disjoint: " + disj);
		LogOutput.print("Equivalences: " + equiv);
		LogOutput.print("Big nodes: " + bignode);	
		//LogOutput.print("Taxonomy: " + Taxonomy);
		
	}
	
	
	/**
	 * Extract axioms of the form A^...^B- > C
	 * from subclassof or equivalence axioms
	 */
	
	Set<Integer> ausxSetOfClasses = new HashSet<Integer>();
	
	private void extractGeneralHornAxioms(){
		
		int idecls;
		
		//for (OWLClass cls: onto.getClassesInSignature()){
		for (OWLClass cls: class2identifier.keySet()){
			//LogOutput.print(cls.getIRI().toString());
			
			idecls=class2identifier.get(cls);
			
			for (OWLEquivalentClassesAxiom ax: onto.getEquivalentClassesAxioms(cls)){
				
				for (OWLClassExpression exp_equiv : ax.getClassExpressions()){
					
					addOWLClassExpresion2GeneralHornAxiom(idecls, exp_equiv);
					
				}//For expressions
				
			}//For equiv axioms
			
			for (OWLSubClassOfAxiom ax: onto.getSubClassAxiomsForSuperClass(cls)){
				
				addOWLClassExpresion2GeneralHornAxiom(idecls, ax.getSubClass());
				
			}//For subclass axioms
			
		}//For classes
		
		
		LogOutput.print("->General HORN Axioms: " + index.getGeneralHornAxiom().size());
		
		/*int i=0;
		String horn="";
		for (Set<Integer> body : generalHornAxioms.keySet()){
			i++;
			
			
			for (int b : body){
				horn+=index.getClassIndex(b).getLabel() + " ^ ";
			}
			
			horn+=" -> " + index.getClassIndex(generalHornAxioms.get(body)).getLabel();
			
			LogOutput.print(body + "->" + generalHornAxioms.get(body));
			LogOutput.print(horn);
			
			horn="";
			
			if (i>10){
				break;
			}
			
		}*/
		
	}
	
	
	/**
	 * We extract inferred class types for the individuals
	 */
	private void extractInferredTypes4Individuals(){
		
		for (int identIndiv : index.getIndividuaIdentifierSet()){
			
			for (Node<OWLClass> node_cls : reasoner.getTypes(
					index.getOWLNamedIndividual4IndividualIndex(identIndiv), true)){
				
				for (OWLClass cls : node_cls.getEntitiesMinusTop()){
					
					index.addType4Individual(identIndiv, class2identifier.get(cls));
					
				}				
			}
		}		
	}
	
	
	
	
	
	/**
	 * Checks if the expresion is valid for general HORN axioms
	 */
	private void addOWLClassExpresion2GeneralHornAxiom(int idecls, OWLClassExpression exp){
		
		
		ausxSetOfClasses.clear();
		
		//if (exp.isClassExpressionLiteral()){
		//	return;
		//}
		if (exp instanceof OWLObjectIntersectionOf){
			
			for (OWLClassExpression exp_intersect : ((OWLObjectIntersectionOf) exp).getOperands()){
				
				//if (exp_intersect.isClassExpressionLiteral()){ //A and notA are considered class expression literals
				if (!exp_intersect.isAnonymous()){
					ausxSetOfClasses.add(class2identifier.get(exp_intersect.asOWLClass()));
				}
				else{
					ausxSetOfClasses.clear();
					return;//do nothing
				}
				
			}
			
		}
		else{
			return; //do nothing
		}
		
		if (ausxSetOfClasses.size()>1){ //At least Two in the intersection 
			//class2identifier
			
			//if (!generalHornAxioms.containsKey(ausxSetOfClasses)){
			//	generalHornAxioms.put(new HashSet<Integer>(ausxSetOfClasses), idecls);
			//}
			index.addGeneralHornAxiom2Structure(ausxSetOfClasses, idecls);
		}
		
	}
	
	
	/*
	 * Set with representative for equivalence nodes
	 * Will be required after interval labelling indexing
	 * @return
	 
	public Set<Integer> getRepresentativeNodes(){
		return representativeNodes;
	}*/
	
	
	/*
	 * Important for the cleaning process
	 * @return
	 *
	public Set<Integer> getRootNodes(){
		return RootIdentifiers;
	}*/
	
	
	
	
	
	
	/*private Set<Integer> getMeaningfulRootsForIdentifier(int ident){
		
		Set<Integer> set =  new HashSet<Integer>();
		
		for (int ideroot : MeaningfulRootIdentifiers){
			if (isSubClassOf(ident, ideroot)){
				set.add(ideroot);
			}
		}
		
		return set;
		
	}*/
	
	
	
	
	int minNumberOfRoots=0;
	
	/**
	 * @deprecated
	 */
	private void extractMeaningfulRoots(){
		
		//TOP CONCEPTS 
		//(avoids possible top elements like in snomed)
		//Create here roots and discard Top concepts!
		//By name or by number? --> if == 1 then split
		NodeSet<OWLClass> topClasses = reasoner.getSubClasses(reasoner.getTopClassNode().getRepresentativeElement(), true);
		
		
		//Real root nodes
		TaxRootIdentifiers.clear();
		for (Node<OWLClass> node : topClasses.getNodes()){
			if (class2identifier.containsKey(node.getRepresentativeElement())){
				TaxRootIdentifiers.add(class2identifier.get(node.getRepresentativeElement()));
			}
		}
		
		
		
		//Meaningful roots. Might be different to the real ones
		if (onto.getClassesInSignature(Imports.INCLUDED).size()<500)
			minNumberOfRoots=4;
		else
			minNumberOfRoots=8;
			
		if (topClasses.getNodes().size()<minNumberOfRoots){
			MeaningfulRootIdentifiers.addAll(extractMeaningfulRoots(topClasses, 1));
			if (MeaningfulRootIdentifiers.size()>topClasses.getNodes().size())
				return;
		}
		
		//Level 0 roots
		MeaningfulRootIdentifiers.clear();
		for (Node<OWLClass> node : topClasses.getNodes()){
			if (class2identifier.containsKey(node.getRepresentativeElement())){
					MeaningfulRootIdentifiers.add(class2identifier.get(node.getRepresentativeElement()));
			}
		}
		
		
		
		//LogOutput.print("Number of roots: " + RootIdentifiers.size());
		
		
	}
		
	/**
	 * @deprecated
	 */
	private Set<Integer> extractMeaningfulRoots(NodeSet<OWLClass> nodes, int level){
		
		Set<Integer> mroots=new HashSet<Integer>();
		OWLClassNodeSet mrootsClass =  new OWLClassNodeSet();
		
		for (Node<OWLClass> node : nodes.getNodes()){
		
			for (Node<OWLClass> topNode : reasoner.getSubClasses(node.getRepresentativeElement(), true)){
				mrootsClass.addNode(topNode);
				if (class2identifier.containsKey(topNode.getRepresentativeElement())){
					mroots.add(class2identifier.get(topNode.getRepresentativeElement()));
				}
			}
		}
		if (mroots.size()>=minNumberOfRoots || level==3){ //we want to avoid infinite recursion
			return mroots;

		}
		else {
			return extractMeaningfulRoots(mrootsClass, level+1);
		}
		
	}
	
	
	/**
	 * @deprecated
	 */
	private void extractHierarchyLevel(NodeSet<OWLClass> classes, int level){
		
		//TOP CONCEPTS		
		//NodeSet<OWLClass> topClasses = reasoner.getSubClasses(reasoner.getTopClassNode().getRepresentativeElement(), true);
		
		int ident;
		
		for (Node<OWLClass> node : classes.getNodes()){
			if (class2identifier.containsKey(node.getRepresentativeElement())){
				ident = class2identifier.get(node.getRepresentativeElement());
				//We add the deeper level
				if (index.getClassIndex(ident).getHierarchyLevel()<level){ 
					index.getClassIndex(ident).setHierarchyLevel(level);
				}
			}
			
			extractHierarchyLevel(reasoner.getSubClasses(node.getRepresentativeElement(), true), level+1);
			
		}
		
		
	}
	
	
	public Set<Integer> getMeaningfulRoots(){
		return MeaningfulRootIdentifiers;
	}
	
	public Set<Integer> getRaelRoots(){
		return TaxRootIdentifiers;
	}
	
	
	
	public void clearFrequencyRelatedStructures(){
		singleWordInvertedIndex.clear();
		filteredInvertedIndex.clear();
	}
	
	
	private Map<String, Set<Integer>> singleWordInvertedIndex = new HashMap<String, Set<Integer>>();
	
	public Integer getFrequency(String word)
	{
		if (singleWordInvertedIndex.containsKey(word))
			return singleWordInvertedIndex.get(word).size();
		return 0;
	}
	
	private Map<Set<String>, Set<Integer>> filteredInvertedIndex = new HashMap<Set<String>, Set<Integer>>();
	
	public Map<Set<String>, Set<Integer>> getFilteredInvertedIndex()
	{
		return filteredInvertedIndex;
	}
	
	public void clearFilteredInvertedIndex()
	{
		filteredInvertedIndex.clear();
	}
	
	public void buildFilteredInvertedIndex(Set<String> stopWords)
	{
		ClassIndex cls;
		String [] words;
		Set<String> key, labels;

/*
		Set<String> specialKey = new HashSet<String>();
		specialKey.add("vas");
		specialKey.add("va");
		specialKey.add("def");
*/	

		for (Integer id : className2Identifier.values())
		{
			cls = index.getClassIndex(id);
			labels = cls.getStemmedAltLabels();
			
			if (labels == null)
			{
				Lib.debuginfo("A class named " + cls.getEntityName() + " has no labels!");
				continue;
			}
			
			for (String lab : labels)
			{
				key = new HashSet<String>();
				words = lab.split("_");
				for (String word : words)
					if (!stopWords.contains(word))
						key.add(word);
				
				if (!filteredInvertedIndex.containsKey(key))
				{
					filteredInvertedIndex.put(key, new HashSet<Integer>());
				}
				
				filteredInvertedIndex.get(key).add(id);
			}
		}
/*		
		Set<Entry<Set<String>,Set<Integer>>> temp = filteredInvertedIndex.entrySet();
		
		LogOutput.print(filteredInvertedIndex.size() + " " + temp.size());
		
		Entry<Set<String>, Set<Integer>> key1 = null, key2;
		for (Entry<Set<String>, Set<Integer>> entry : temp)
		{
			Lib.logInfo("- " + entry.getKey().toString());
			if (entry.getKey().equals(specialKey))
			{
				if (key1 == null)
					LogOutput.print(key1 = entry);
				else 
				{
					LogOutput.print(key2 = entry);
					LogOutput.print(key1.equals(key2) + " " + key1.hashCode() + " " + key2.hashCode());
					LogOutput.print(key1 + " "+ key2);
				}
			}
			
		}
		
		Lib.closeLog();
*/	}

	public Set<String> getSuperClass(int id, int TOTAL) 
	{
		Set<String> ret = new HashSet<String>();
		Set<Integer> superclasses;
		Set<Integer> visited = new HashSet<Integer>();
		ClassIndex cls;
		
		Queue<ClassIndex> q = new LinkedList<ClassIndex>();
		q.add(index.getClassIndex(id));
		visited.add(id);
		
		while (!q.isEmpty() && ret.size() < TOTAL)
		{
			superclasses = q.remove().getDirectSuperclasses();
			//TODO: Yujiao - check the OWLClassExpression
			
			for (int i: superclasses)
				if (!visited.contains(i))
				{
					q.add(cls = index.getClassIndex(i));
					visited.add(i);
					addStemmedAltLabels(ret, cls);
				}
		}
		
		return ret;
	}

	private void addStemmedAltLabels(Set<String> labels, ClassIndex cls) 
	{
		Set<String> newLabs = cls.getStemmedAltLabels();
		
		if (newLabs == null) return ;
		for (String lab : newLabs)
			labels.add(lab);
	}
	
	
	
	
	
	
	/**
	 * Manages the extraction of categories from annotations and role assetions
	 * @author ernesto
	 *
	 */
	private class ExtractCategoriesForIndividual{
		
		
		//Set<String> categories4individual = new HashSet<String>();
		
		ExtractCategoriesForIndividual(){
			
		}
		
		
		protected void extract(OWLNamedIndividual indiv, int ident){				
			
			//categories4individual.clear();
		
			
			String category_value;
			
			for (OWLAnnotationAssertionAxiom entityAnnAx : EntitySearcher.getAnnotationAssertionAxioms(indiv, onto)){
				
				String uri_ann = entityAnnAx.getAnnotation().getProperty().getIRI().toString();
				
				if (Parameters.accepted_property_URIs_for_instance_categories.contains(uri_ann)){									
					
					if (!(category_value=asDirectValue(entityAnnAx)).equals("")){												
						//categories4individual.add(category_value);											
						index.addCategory4Individual(ident, category_value);
					}
					else if (!(category_value=asNamedIndividual(entityAnnAx)).equals("")){						
						index.addCategory4Individual(ident, category_value);
						
						//Mappinsg obtained from doremuss datasets
						//Cross terminologies
						//if (category_value.equals("http://data.doremus.org/vocabulary/diabolo/genre/lied"))
						//	index.addCategory4Individual(ident, "http://data.doremus.org/vocabulary/iaml/genre/li");
						//Narrower-broader relationships
						//if (category_value.equals("http://data.doremus.org/vocabulary/iaml/genre/opc"))
						//	index.addCategory4Individual(ident, "http://data.doremus.org/vocabulary/iaml/genre/op");
					}
					
					
				}
				
				
			}
			
			
			for (String uri_for_categories : Parameters.accepted_property_URIs_for_instance_categories){
				
				try{
					
					for (OWLLiteral assertion_value : Searcher.values(onto.getDataPropertyAssertionAxioms(indiv), index.getFactory().getOWLDataProperty(IRI.create(uri_for_categories)))){
						
						
						category_value = assertion_value.getLiteral().toLowerCase();
						if (category_value!=null && !category_value.equals("null") && !category_value.equals("")){
							index.addCategory4Individual(ident, category_value);
							continue;
						}
						//categories4individual.add(assertion_value.getLiteral().toLowerCase());
						
						
					}
				}
				catch(Exception e){
					//do nothing
				}
				
				try{
					
					for (OWLIndividual assertion_value_indiv : Searcher.values(
							onto.getObjectPropertyAssertionAxioms(indiv), index.getFactory().getOWLObjectProperty(IRI.create(uri_for_categories)))){
						
						if (assertion_value_indiv.isNamed()){
							
							category_value = assertion_value_indiv.asOWLNamedIndividual().getIRI().toString();
							if (category_value!=null && !category_value.equals("null") && !category_value.equals("")){
								index.addCategory4Individual(ident, category_value);
								continue;
							}
							
							//categories4individual.add(assertion_value_indiv.asOWLNamedIndividual().getIRI().toString());
						}
						
					}
				}
				catch(Exception e){
					//do nothing
				}
				
			}
			
			
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
		 * 
		 * @param entityAnnAx
		 * @return
		 */
		private String asNamedIndividual(OWLAnnotationAssertionAxiom entityAnnAx){
			try {
				//It is an individual
				String namedIndivIRI=((IRI)entityAnnAx.getAnnotation().getValue()).toString();				
				
				if (namedIndivIRI==null || namedIndivIRI.equals("null") || namedIndivIRI.equals("")){
					//System.err.println("NULL LABEL: " + entityAnnAx);
					return "";
				}
				
				
				return namedIndivIRI;
				
				
			}
			catch (Exception e){
				//In case of error. Accessing an object in an unexpected way
				return "";
			}
			
		}
		
		
		
	}
	
	
	
	
	
	
	
	/**
	 * 
	 * Manages the extraction of the lexicon associated to an Individual that is presented as data asserion axioms 
	 * or object asssertion axioms. Currently is based on the IM track of the OAEI 2012
	 * 
	 * @author Ernesto
	 *
	 */
	private class ExtractAcceptedLabelsFromRoleAssertions{
		
	
		Set<String> lexiconValues4individual = new HashSet<String>();
		String label_value;
		
		int max_size_name_label=0;
		int min_size_name_label=5000;
		
		
		ExtractAcceptedLabelsFromRoleAssertions(){
			
		}
		
		
		protected boolean isDummyIndividual(OWLNamedIndividual indiv){
		
			OWLObjectPropertyAssertionAxiom opaa;
			String prop_uri;
			
			
			//Check for oject property assertions deep 1 referenceing given individual
			//-------------------------------------------------------------------------
			//If referenced it is a dummy individual which should not be considered in the matching				
			for (OWLAxiom refAx : onto.getReferencingAxioms(indiv, Imports.INCLUDED)){
				
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
		
		protected Set<String> extractLexiconFromRoleAssertions(OWLNamedIndividual indiv){				
			
			lexiconValues4individual.clear();
					
						
			
			//We also add from rdfs:comments
			//-----------------------------------------
			//Since the comments may be long we need to pre-process them
			for (OWLAnnotationAssertionAxiom indivAnnAx : EntitySearcher.getAnnotationAssertionAxioms(indiv, onto)){
				
								
				String uri_ann = indivAnnAx.getAnnotation().getProperty().getIRI().toString();
				
				
				if (Parameters.rdf_comment_uri.equals(uri_ann)){
					
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
			for (String uri_indiv_ann : Parameters.accepted_data_assertion_URIs_for_individuals){
			
				for (OWLLiteral assertion_value : Searcher.values(onto.getDataPropertyAssertionAxioms(indiv), index.getFactory().getOWLDataProperty(IRI.create(uri_indiv_ann)))){
					
					
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
				
			
			
			
			//OBject property assertions deep 1  (level 1 references a dummy individual)
			//-------------------------------------
			for (String uri_indiv_ann_deep1 : Parameters.accepted_object_assertion_URIs_for_individuals){
								
				for (OWLIndividual assertion_value_indiv : Searcher.values(
						onto.getObjectPropertyAssertionAxioms(indiv), index.getFactory().getOWLObjectProperty(IRI.create(uri_indiv_ann_deep1)))){
					
					//We only consider named individuals
					if (assertion_value_indiv.isNamed()){
						
						////DBPedia references											 
						//if (assertion_value_indiv.asOWLNamedIndividual().getIRI().toString().contains("dbpedia.org")){							
						//	label_value = processLabel(Utilities.getEntityLabelFromURI(assertion_value_indiv.asOWLNamedIndividual().getIRI().toString()));							
						//}
						//else{
						//Datatype assertion deep 2: has_value and others
						//----------------------------------------
						for (String uri_indiv_ann_deep2 : Parameters.accepted_data_assertion_URIs_for_individuals_deep2){
							
							
						
							for (OWLLiteral assertion_value_deep2 : Searcher.values(
									onto.getDataPropertyAssertionAxioms(assertion_value_indiv), index.getFactory().getOWLDataProperty(IRI.create(uri_indiv_ann_deep2)))){
								
								
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
						}//end for data assertion level 2
						
						
						//Extract comment or label! level 2
						//---------------------
						for (OWLAnnotationAssertionAxiom indivAnnAx_level2 : EntitySearcher.getAnnotationAssertionAxioms(assertion_value_indiv.asOWLNamedIndividual(), onto)){
							
							String uri_ann = indivAnnAx_level2.getAnnotation().getProperty().getIRI().toString();
							
							
							if (Parameters.rdf_comment_uri.equals(uri_ann) || Parameters.rdf_label_uri.equals(uri_ann)){
								
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
						//}//iff dbpedia
						
					}
					
				}
			}
			
			
			return lexiconValues4individual;
		}
		
		
		/**
		 * This method extracts lexicon from other properties. Like "lives in X". 
		 * In the end we will only keep those relationships that uniquely identifies an instance
		 * @param indiv
		 * @return
		 */
		protected Set<String> extractExtendedLexiconFromRoleAssertions(OWLNamedIndividual indiv){
			
			lexiconValues4individual.clear();
			
			//look forclean lexicon in data prop
			
			String label_name;
			
			//Map<OWLDataPropertyExpression, Set<OWLLiteral>> dataProp2values = indiv.getDataPropertyValues(onto);
			Multimap<OWLDataPropertyExpression, OWLLiteral> dataProp2values = EntitySearcher.getDataPropertyValues(indiv, onto);
			
			
			//We estract a data props lecicon like "date_12_12_2012", "age_18", etc...
			for (OWLDataPropertyExpression dataprop : dataProp2values.keySet()){
				
				if (dataprop.isAnonymous())
					continue;
				
				//we avoid properties already extracted
				if (Parameters.accepted_data_assertion_URIs_for_individuals.contains(
						dataprop.asOWLDataProperty().getIRI().toString())
					||
					Parameters.accepted_data_assertion_URIs_for_individuals_deep2.contains( //hasv_alue
							dataprop.asOWLDataProperty().getIRI().toString()))
					continue;
				
				
				//Init label with name of the property
				label_name = Utilities.getEntityLabelFromURI(dataprop.asOWLDataProperty().getIRI().toString());
				
				for (OWLLiteral literal : dataProp2values.get(dataprop)){
					
					//if (isGoodLabel(literal.getLiteral().toString())) //not useful in this case
					//We also normalize dates in case the give string is within one of the acceptred date formats
					lexiconValues4individual.add(label_name + "_" + NormalizeDate.normalize(literal.getLiteral().toString()));
				
				}
				
			}//for dtata prop
			
			//Map<OWLObjectPropertyExpression, Set<OWLIndividual>> objProp2values = indiv.getObjectPropertyValues(onto);
			Multimap<OWLObjectPropertyExpression, OWLIndividual> objProp2values = EntitySearcher.getObjectPropertyValues(indiv, onto);
			
			
			int ident;
			
			//We estract a data props lexicon like "lives_in_city", "spoken_in_lesotho"
			for (OWLObjectPropertyExpression objprop : objProp2values.keySet()){
				
				if (objprop.isAnonymous())
					continue;
				
				//we avoid properties already extracted
				//if (Parameters.accepted_object_assertion_URIs_for_individuals.contains(
				//		objprop.asOWLObjectProperty().getIRI().toString()))
				//	continue;
			
				//Init label with name of the property
				label_name = Utilities.getEntityLabelFromURI(objprop.asOWLObjectProperty().getIRI().toString());
				
				for (OWLIndividual indiv_deep2 : objProp2values.get(objprop)){
					
					if (indiv_deep2.isAnonymous())
						continue;
					
					
					if (!inidividual2identifier.containsKey(indiv_deep2.asOWLNamedIndividual())){
						continue;
					}
					
					ident = inidividual2identifier.get(indiv_deep2.asOWLNamedIndividual());
					
					//If has alternative labels, otherwise we cannot do much (since label will probably be a "randomly" generated identifier)
					//Dummy indiv has not alternative labels!!
					if (index.hasIndividualAlternativeLabels(ident)){  
						//we know it is a good label
						lexiconValues4individual.add(label_name + "_" + index.getLabel4IndividualIndex(ident));
					}
					else{
						
						//Deep 2
						///
						dataProp2values = EntitySearcher.getDataPropertyValues(indiv_deep2, onto);
						
						//We estract a data props lecicon like "date_12_12_2012", "age_18", etc...
						//in OAEI 2013 I this is not used much... since deep2 obj properties substitute another obj propertyes
						for (OWLDataPropertyExpression dataprop : dataProp2values.keySet()){
							
							if (dataprop.isAnonymous())
								continue;
							
							label_name = Utilities.getEntityLabelFromURI(dataprop.asOWLDataProperty().getIRI().toString());
						
							for (OWLLiteral literal : dataProp2values.get(dataprop)){
								
								//if (isGoodLabel(literal.getLiteral().toString()))
								lexiconValues4individual.add(label_name + "_" + NormalizeDate.normalize(literal.getLiteral().toString()));
							
							}
							
						}
						
						//Map<OWLObjectPropertyExpression, Set<OWLIndividual>> objProp2values_deep2 = indiv_deep2.getObjectPropertyValues(onto);
						Multimap<OWLObjectPropertyExpression, OWLIndividual> objProp2values_deep2 = EntitySearcher.getObjectPropertyValues(indiv_deep2, onto);
						
						
						for (OWLObjectPropertyExpression objectprop2 : objProp2values_deep2.keySet()){
							
							if (objectprop2.isAnonymous())
								continue;
							
							label_name = Utilities.getEntityLabelFromURI(objectprop2.asOWLObjectProperty().getIRI().toString());
						
							for (OWLIndividual indiv_deep3 : objProp2values_deep2.get(objectprop2)){
								
								if (indiv_deep3.isAnonymous())
									continue;
								
								int ident2 = inidividual2identifier.get(indiv_deep3.asOWLNamedIndividual());
								
								if (index.hasIndividualAlternativeLabels(ident2)){  
									lexiconValues4individual.add(label_name + "_" + index.getLabel4IndividualIndex(ident2));
								}							
							}
							
						}
						
					}
					
					
					
								
				}
				
				
			}//for obj prop
			
			return lexiconValues4individual;
			
			
		}

	
		
		
		
	
		/**
		 * 
		 * @param value
		 * @return Empty string if it is not valid
		 */
		public String processLabel(String value){
					
			String processedLabel="";
			
			//Reg expression to split text: //Split up to "&", ".", "(", "," ";", is, was, are, were, est/fut (french)
			//In this way we split long comments and we only get the important part as synonym
			//String reg_ex_split = "[&\\.,;(]";
			String reg_ex_split="[&\\,;(/\\[]|(\\s)is(\\s)|(\\s)are(\\s)|(\\s)was(\\s)|(\\s)were(\\s)|(\\s)est(\\s)|(\\s)fut(\\s)|(\\s)un(\\s)|(\\s)a(\\s)|(\\s)an(\\s)";
			//No filter by point : St. Georges or St. John
			
			//Removing annoying acronyms
			//order of the British empire
			//Never inside a word 
			processedLabel = value.replaceAll(" obe ", "");
			processedLabel = processedLabel.replaceAll(" obe", "");
			processedLabel = processedLabel.replaceAll("obe ", "");
			//fellow royal society
			processedLabel = processedLabel.replaceAll(" frs ", "");
			processedLabel = processedLabel.replaceAll(" frs", "");
			processedLabel = processedLabel.replaceAll("frs ", "");
			
			int manegeable_lenght = 65;
			
			//To remove non ascii spaces
			processedLabel =  processedLabel.replaceAll(String.valueOf((char) 160), " ");
			
			//System.out.println(processedLabel + " '"+processedLabel.indexOf(reg_ex_split)+"' " + processedLabel.split(reg_ex_split).length);
			if (processedLabel.split(reg_ex_split).length==0)
				return "";
			
			//short data assertion strings
			if (processedLabel.length()<=manegeable_lenght && !processedLabel.contains("<p>") && !processedLabel.contains("</p>")){
				
				//We still want to split and trim
				//we keep/split string up to the given character (twice in case mroe than one character)
				
				
				
				processedLabel = processedLabel.split(reg_ex_split)[0];
				processedLabel = processedLabel.split(reg_ex_split)[0];
				//we remove white spaces at the end and begining of label
				processedLabel = processedLabel.trim();
				//processedLabel = StringUtils.trim(processedLabel);
				
				
				
				return processedLabel;
				
				//In IM 2015 we may not want to filter in that way
				/*if (!isGoodLabel(processedLabel)){
					//LogOutput.printAlways("Filtered: "+ processedLabel);
					return ""; //bad label
				}
				else{
					//LogOutput.print("GOOD: "+ processedLabel);
				}*/
				
			}
			//Text with several paragraphs -> filter
			else {
				
				//Detect if it starts with <p> if not then it has been split 
				//For iimb
				if (processedLabel.startsWith("<p>")){
					
					processedLabel = processedLabel.split("<p>")[1];
					
					//we keep/split string up to the given character (twice in case mroe than one character)
					processedLabel = processedLabel.split(reg_ex_split)[0];
					processedLabel = processedLabel.split(reg_ex_split)[0];
					
					//we remove white spaces ate the endand beginning oflabel
					processedLabel = processedLabel.trim();
					//processedLabel = StringUtils.trim(processedLabel);
					
					if (processedLabel.length()<=manegeable_lenght){
						
						if (isGoodLabel(processedLabel)){
							//LogOutput.print("GOOD: "+ processedLabel);
							return processedLabel;
						}
					}
					
					LogOutput.print("Filtered: "+ processedLabel);
					return "";
					
				}
				else {
					
					//For comments in RDFT oaei 2013. They do not contain <p>
					
					//we keep/split string up to the given character (twice in case mroe than one character)
					processedLabel = processedLabel.split(reg_ex_split)[0];
					processedLabel = processedLabel.split(reg_ex_split)[0];
					
					//we remove white spaces at the end and begining of label
					//System.out.println("'" + processedLabel + "'");
					processedLabel = processedLabel.trim();
					//processedLabel = StringUtils.trim(processedLabel);
					//System.out.println("'" + processedLabel + "'");
					
					
					if (processedLabel.length()<=manegeable_lenght){
						
						if (isGoodLabel(processedLabel)){
							//LogOutput.printAlways("GOOD: "+ processedLabel);
							return processedLabel;
						}
						else{
							//LogOutput.printAlways("BAD: "+ processedLabel);
						}
					}
					else{
						
						processedLabel = processedLabel.substring(0, manegeable_lenght);
						
						if (isGoodLabel(processedLabel)){
							//LogOutput.print("REDUCED label 0-"+manegeable_lenght+": "+ processedLabel);
							return processedLabel;
						}
						
					}
					
					LogOutput.print("Filtered: "+ processedLabel);
					
					
					
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
				
				//Accept roman numbers
				if (NormalizeNumbers.getRomanNumbers10().contains(word)){ 
					continue;
				}
				
				//to also avoid : or other characteres after a rman number
				if (word.length()>1 && NormalizeNumbers.getRomanNumbers10().contains(word.substring(0, word.length()-1))){					
					//LogOutput.print(word + " " + word.substring(0, word.length()-1) + " " + word.substring(0, word.length()-2));
					continue;
				}
				
				if (word.equals("st") || word.equals("dr")) //st johns dr john
					continue;
				
				//if (word.equals("f")) //st johns
				//	continue;
				
				//Single characters different from "a"
				//A bit comflictive depending on the case
				//We now accept single letetrs in labels. Specially for names: i,e, "v carapella"
				
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
	
	
		
	}//end class
	
	
	
	
	
	
	
	

	
	
	OntologyProcessing(){
		
	}
	
	 public static void main(String[] args) {
				 
		OntologyProcessing p = new OntologyProcessing();
		p.roleAssertionLabelsExtractor.processLabel("Vicenza  , a city in north-eastern Italy, is the capital of the eponymous province in the Veneto region, at the northern base of the Monte Berico, straddling the Bacchiglione. Vicenza is approximately 60 km west of Venice and 200 km east of Milan. Vicenza is a thriving and cosmopolitan city, with a rich history and culture, and many museums, art galleries, piazzas, villas, churches and elegant Renaissance palazzi.");
		
		//System.out.println(StringUtils.trim("Vicenza  ")+"'");
		System.out.println("Vicenza  ".trim()+"'");
		System.out.println("Vicenza  , a city in north-eastern Italy, is the capital of the eponymous province in the Veneto region, at the northern base of the Monte Berico, straddling the Bacchiglione. Vicenza is approximately 60 km west of Venice and 200 km east of Milan. Vicenza is a thriving and cosmopolitan city, with a rich history and culture, and many museums, art galleries, piazzas, villas, churches and elegant Renaissance palazzi.".trim());
	 }

		
	

}

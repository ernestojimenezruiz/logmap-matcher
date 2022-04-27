package uk.ac.ox.krr.logmap2;

import uk.ac.ox.krr.logmap2.io.ReadFile;
import uk.ac.ox.krr.logmap2.multilingual.TranslatorManager;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.io.File;

public class Parameters {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Parameters.class);


	public static String deprecated_uri = "http://www.w3.org/2002/07/owl#deprecated";
	
	public static String rdf_label_uri = "http://www.w3.org/2000/01/rdf-schema#label";
	public static String rdf_comment_uri = "http://www.w3.org/2000/01/rdf-schema#comment";
	
	private static String skos_label_uri =    "http://www.w3.org/2004/02/skos/core#prefLabel";	                                        
	private static String skos_altlabel_uri = "http://www.w3.org/2004/02/skos/core#altLabel";
	
	private static String SYN_synonym = "http://purl.bioontology.org/ontology/SYN#synonym";											   
	
	private static String EFO_synonym = "http://www.ebi.ac.uk/efo/alternative_term";
	
	private static String foaf_name_uri = "http://xmlns.com/foaf/0.1/name";
	
	private static String NCI_synonym = "http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl#FULL_SYN";
	private static String OBO_synonym = "http://purl.obolibrary.org/obo/synonym";
	private static String CSEO_synonym = "http://scai.fraunhofer.de/CSEO#Synonym";
	
	
	private static String BIRNLEX_prefLabel = "http://bioontology.org/projects/ontologies/birnlex#preferred_label";
	private static String BIRNLEX_synonym = "http://bioontology.org/projects/ontologies/birnlex#synonyms";
	
	
	
	private static String hasRelatedSynonym_uri = "http://www.geneontology.org/formats/oboInOwl#hasRelatedSynonym";
	private static String hasExactSynonym_uri   = "http://www.geneontology.org/formats/oboInOwl#hasExactSynonym";
	
	private static String hasRelatedSynonym_uri2 = "http://www.geneontology.org/formats/oboInOWL#hasRelatedSynonym";
	private static String hasExactSynonym_uri2   = "http://www.geneontology.org/formats/oboInOWL#hasExactSynonym";
	
	private static String nci_synonym_uri = "http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl#Synonym";
	private static String fma_synonym_uri="http://bioontology.org/projects/ontologies/fma/fmaOwlDlComponent_2_0#Synonym";
	private static String hasDefinition_uri="http://www.geneontology.org/formats/oboInOwl#hasDefinition";
	private static String xbrl_label_uri="http://www.xbrl.org/2003/role/label";
	
	


	//NIVA USE CASE
	private static String ecotox_latin_name = "https://cfpub.epa.gov/ecotox#latinName";
	private static String ecotox_common_name = "https://cfpub.epa.gov/ecotox#commonName";
	private static String ncbi_synonym = "https://www.ncbi.nlm.nih.gov/taxonomy#synonym";
	private static String ncbi_scientific_name = "https://www.ncbi.nlm.nih.gov/taxonomy#scientific_name";

	
	
		
	private static String name_dprop_im_uri = "http://oaei.ontologymatching.org/2012/IIMBTBOX/name";
	private static String article_dprop_im_uri = "http://oaei.ontologymatching.org/2012/IIMBTBOX/article";
	
	private static String has_value_dprop_im_uri = "http://www.instancematching.org/IIMB2012/ADDONS#has_value";
		
	private static String article_oprop_im_uri = "http://www.instancematching.org/IIMB2012/ADDONS#article";
	private static String name_oprop_im_uri = "http://www.instancematching.org/IIMB2012/ADDONS#name";
	
	
	//2013
	private static String population_dprop_im_uri = "http://dbpedia.org/ontology/populationTotal";	
	private static String birthName_dprop_im_uri = "http://dbpedia.org/ontology/birthName";	
	
	private static String label_oprop_im_uri = "http://www.instancematching.org/label";
	private static String curriculum_oprop_im_uri = "http://www.instancematching.org/curriculum";
	private static String places_oprop_im_uri = "http://www.instancematching.org/places";
	
	//Other
	private static String abstract_dprop_im_uri = "http://dbpedia.org/ontology/abstract";
	private static String label_dprop_im_uri = "http://dbpedia.org/property/label";
	private static String name2_dprop_im_uri = "http://dbpedia.org/property/name";
		
	
	//2010 URIs
	private static String article_oprop_im_uri_2010 = "http://www.instancematching.org/IIMB2010/ADDONS#name";
	private static String name_oprop_im_uri_2010 = "http://www.instancematching.org/IIMB2010/ADDONS#article";
	
	private static String has_value_dprop_im_uri_2010 = "http://www.instancematching.org/IIMB2010/ADDONS#has_value";
	
	private static String name_dprop_im_uri_2010 = "http://oaei.ontologymatching.org/2010/IIMBTBOX/name";
	private static String article_dprop_im_uri_2010 = "http://oaei.ontologymatching.org/2010/IIMBTBOX/article";
	
	
	
	//IM 2015 URIS
	private static String name_dprop_im_uri_2015 = "http://islab.di.unimi.it/imoaei2015#name";
	
	
	private static boolean restrict_instance_types = false;
	
	
	public static boolean print_output = false; //false;
	public static boolean print_output_always = false; //false;
	
	
	public static boolean print_statistics = true; 
	
	
	
	public static double bad_score_scope = 0.0;
	
	public static double good_isub_anchors = 0.98;
	
	public static double good_isub_candidates = 0.95;
	
	public static double good_confidence = 0.50;
	
	public static double min_conf_pro_map = 0.75;
	
		
	public static double min_isub_instances = 0.80; //Updted 2021

	
	
	
	public static double good_sim_coocurrence = 0.08; //Tested one originally 0.09
	
	public static boolean avoid_redundant_labels = true;
	
	public static int max_redundancy_labels = 3;
	
	public static int max_ambiguity = 4;
	
	public static int good_ambiguity = 2;
	
	//Note that even if overlapping is set to true. It will only applied for big ontologies >15000 
	public static boolean use_overlapping = false;
	
	public static boolean second_chance_conflicts = true;
	
	public static int ratio_second_chance_discarded = 5;
	
	public static int min_size_overlapping = 15000; //5000
	
	public static boolean use_stemming = false;
	
	
	//set to false by default?
	public static boolean perform_property_matching = true; //true;
	public static boolean perform_instance_matching = true; //true;
	public static boolean output_instance_mappings = true; //true;
	
	public static boolean output_instance_mapping_files = false; //for statistics
	
	public static boolean output_class_mappings = true;
	public static boolean output_prop_mappings = true;
	
	//TODO Now with ignore types it may be solved this issue!
	public static boolean reason_datatypes = true; //with OM client gives error if true
	
	public static String structural = "Structural";
	public static String hermit = "HermiT";
	//public static String more = "MORe";
	public static String elk = "ELK";
	//public static String reasoner = hermit;
	//public static String reasoner = more;
	public static String reasoner = structural;
	//public static String reasoner = elk;
	
	//Timeout reasoner
	public static int timeout = 25;
	
	
	public static boolean output_equivalences_only = false;
	
	public static boolean use_umls_lexicon = true;
	
	public static boolean reverse_labels = false;
	
	public static boolean allow_interactivity = false;
	
	public static boolean allow_multilingual = true;
	public static boolean is_test_mode_multilingual = false;  //for testing, we only simulate translation
	public static boolean use_local_dict = true;
	public static boolean call_online_translator = true;
	//TODO default target language is english
	public static String target_lang = "en";
	//0= Google, 1= Microsoft, >=2 all
	public static int translator_id=TranslatorManager.ALL_TRANSLATORS;
	
		
	
	public static boolean allow_bioportal = false;
	public static int max_mediating_ontologies =10;
	public static double confidence_composed_mappings1 = 0.8;
	public static double confidence_composed_mappings2 = 0.7;
	
	
	
	public static boolean cleanD_G = true;
	public static boolean extractGlobal_D_G_Info = true;
	
	
	
	public static String path_chinese_segmenter_dict = "/home/ernesto/Documents/OAEI_2014_campaign/EVAL_2014/logmap2_package_oaei2014/conf/multilingual/dict_ictclas4j";
	
	//To store the translations from google codes to be used in next iterations (useful in Multifarm track)
	//Note that these folders are changed in the parameters file to not to point to local files
	//Dynamic (on-the-fly) dict folder
	public static String path_multilingual_tmp = "/home/ernesto/Documents/OAEI_OM_2015/EVAL_2015/dict_multilingual";
	//Static dictionary folder
	public static String path_multilingual_local = "/home/ernesto/Documents/OAEI_OM_2015/EVAL_2015/dict_multilingual/local";
	
	public static String path_mappings_categories = "/mappings/categories";
	
	public static Set<String> accepted_annotation_URIs_for_classes = new HashSet<String>();
	
	public static Set<String> accepted_data_assertion_URIs_for_individuals = new HashSet<String>();
	public static Set<String> accepted_data_assertion_URIs_for_individuals_deep2 = new HashSet<String>();
	public static Set<String> accepted_object_assertion_URIs_for_individuals = new HashSet<String>();
	
	
	//Some individuals contain links to dbpedia categories or other sort of categories which are very important for desambiguation
	public static Set<String> accepted_property_URIs_for_instance_categories = new HashSet<String>();
	
	
	
	
	//Some IM tracks require to match specific type of individuals: i.e. Persons
	public static Set<String> allowed_instance_types = new HashSet<String>();
	//The benchmark tracks does not consider in the output alignemnts
	public static Set<String> filter_entities = new HashSet<String>();
	
	
	//For thresholds file
	
	private static final String print_output_str = "print_output";
	
	private static final String bad_score_scope_str = "bad_score_scope";
	
	private static final String good_isub_anchors_str = "good_isub_anchors";
	
	private static final String good_isub_candidates_str = "good_isub_candidates";
	
	private static final String good_confidence_str = "good_confidence";
	
	private static final String good_sim_coocurrence_str = "good_sim_coocurrence";
	
	private static final String min_conf_pro_map_str = "min_conf_pro_map";
	
	private static final String confidence_composed_mappings_str1 = "confidence_composed_mappings1";
	private static final String confidence_composed_mappings_str2 = "confidence_composed_mappings2";
	
	private static final String avoid_redundant_labels_str = "avoid_redundant_labels";
	
	private static final String max_redundancy_labels_str = "max_redundancy_labels";
	
	
	private static final String max_ambiguity_str = "max_ambiguity";
	
	private static final String good_ambiguity_str = "good_ambiguity";
	 
	private static final String use_overlapping_str = "use_overlapping";
	
	private static final String min_size_overlapping_str = "min_size_overlapping";
	
	private static final String instance_matching_str = "instance_matching";
	
	private static final String property_matching_str = "property_matching";
	
	private static final String annotation_URI_str = "annotation_URI";
	
	private static final String category_URI_str = "category_URI";
	
	
	private static final String data_assertion_URI_Indiv_str = "data_assertion_URI_Indiv";
	private static final String data_assertion_URI_Indiv_deep2_str = "data_assertion_URI_Indiv_deep2";
	
	private static final String object_assertion_URI_Indiv_str = "object_assertion_URI_Indiv";
	
	
	private static final String restrict_individual_types_str = "restrict_individual_types";
	private static final String allowed_individual_type_str = "allowed_individual_type";
	private static final String filter_entity_str = "filter_entity";
	
	private static final String output_class_mappings_str = "output_class_mappings";
	private static final String output_prop_mappings_str = "output_prop_mappings";
	private static final String output_instance_mappings_str = "output_instance_mappings";
	private static final String output_instance_mapping_files_str = "output_instance_mapping_files";
	
	
	private static final String glogal_info_str = "glogal_info";	
	
	
	private static final String reason_datatypes_str = "reason_datatypes";
	
	private static final String second_chance_conflicts_str = "second_chance_conflicts";
	private static final String ratio_second_chance_discarded_str = "ratio_second_chance_discarded";
	
	
	//reasoner|MORe or HermiT
	private static final String reasoner_str = "reasoner";
	private static final String timeout_str = "timeout";
	
	
	
	private static final String output_equivalences_only_str = "output_equivalences_only";
	
	private static final String use_umls_lexicon_str = "use_umls_lexicon";
	
	private static final String use_stemming_str = "use_stemming";

	private static final String allow_bioportal_str = "allow_bioportal";
	
	private static final String max_mediating_ontologies_str = "max_mediating_ontologies";
	
	private static final String allow_interactivity_str = "allow_interactivity";
	
	private static final String allow_multilingual_str = "allow_multilingual";
	
	private static final String is_test_mode_multilingual_str = "is_test_mode_multilingual";
	
	private static final String use_local_dict_str = "use_local_dict";
	
	private static final String call_online_translator_str = "call_online_translator";
	
	private static final String path_chinese_segmenter_dict_str = "path_chinese_segmenter_dict";
	
	private static final String path_multilingual_tmp_str = "path_multilingual_tmp";
	
	private static final String path_multilingual_local_str = "path_multilingual_local";
	
	private static final String path_mappings_categories_str = "path_mappings_categories";
	
	private static final String reverse_labels_str = "reverse_labels";
	
	private static final String target_lang_str = "target_lang";
	
	private static final String translator_id_str = "translator_id";
	
	
	
	
	
	//Init of default accepted annotation/assertion uris
	static {
		//accepted_annotation_URIs = new HashSet<String>();
		accepted_annotation_URIs_for_classes.add(rdf_label_uri);
		accepted_annotation_URIs_for_classes.add(hasExactSynonym_uri);
		accepted_annotation_URIs_for_classes.add(hasRelatedSynonym_uri);
		
		accepted_annotation_URIs_for_classes.add(hasExactSynonym_uri2);
		//TODO
		//accepted_annotation_URIs_for_classes.add(hasRelatedSynonym_uri2);
		
		accepted_annotation_URIs_for_classes.add(nci_synonym_uri);
		accepted_annotation_URIs_for_classes.add(fma_synonym_uri);
		accepted_annotation_URIs_for_classes.add(hasDefinition_uri);
		accepted_annotation_URIs_for_classes.add(xbrl_label_uri);
		
		accepted_annotation_URIs_for_classes.add(skos_label_uri);
		accepted_annotation_URIs_for_classes.add(skos_altlabel_uri);
		accepted_annotation_URIs_for_classes.add(foaf_name_uri);
		
		accepted_annotation_URIs_for_classes.add(SYN_synonym);
		accepted_annotation_URIs_for_classes.add(EFO_synonym);
		
		accepted_annotation_URIs_for_classes.add(NCI_synonym);
		accepted_annotation_URIs_for_classes.add(OBO_synonym);
		accepted_annotation_URIs_for_classes.add(CSEO_synonym);
		
		accepted_annotation_URIs_for_classes.add(BIRNLEX_synonym);
		accepted_annotation_URIs_for_classes.add(BIRNLEX_prefLabel);
		
		accepted_annotation_URIs_for_classes.add(ecotox_common_name);
		accepted_annotation_URIs_for_classes.add(ecotox_latin_name);
		accepted_annotation_URIs_for_classes.add(ncbi_scientific_name);
		accepted_annotation_URIs_for_classes.add(ncbi_synonym);
		
		
		
		//OAEI IM 2015
		//accepted_data_assertion_URIs_for_individuals.add(name_dprop_im_uri_2015);
		//accepted_data_assertion_URIs_for_individuals.add("http://islab.di.unimi.it/imoaei2015#title");
		
		
		//Allowed class types IM 2015
		//allowed_instance_types.add("http://islab.di.unimi.it/imoaei2015#Person");
		//allowed_instance_types.add("http://www.bbc.co.uk/ontologies/creativework/BlogPost");
		//allowed_instance_types.add("http://www.bbc.co.uk/ontologies/creativework/NewsItem");
		//allowed_instance_types.add("http://www.bbc.co.uk/ontologies/creativework/Programme");
		
		
		//Entities to be filtered from there URIS. For example, in the Benchmark track they are not included in output
		filter_entities.add("http://xmlns.com/foaf/");
		filter_entities.add("http://www.w3.org/1999/02/22-rdf-syntax-ns");
		filter_entities.add("http://purl.org/dc/elements/");
		filter_entities.add("http://mouse.owl#UNDEFINED_part_of");
		filter_entities.add("http://human.owl#UNDEFINED_part_of");



		
		
		//OAEI IM 2012
		//Data
		accepted_data_assertion_URIs_for_individuals.add(name_dprop_im_uri);
		accepted_data_assertion_URIs_for_individuals.add(article_dprop_im_uri);
		
		//Data deep2
		accepted_data_assertion_URIs_for_individuals_deep2.add(has_value_dprop_im_uri);
		
		//Object
		accepted_object_assertion_URIs_for_individuals.add(name_oprop_im_uri);
		accepted_object_assertion_URIs_for_individuals.add(article_oprop_im_uri);
		
		//OAEI IM 2010
		//Data
		accepted_data_assertion_URIs_for_individuals.add(name_dprop_im_uri_2010);
		accepted_data_assertion_URIs_for_individuals.add(article_dprop_im_uri_2010);
		
		//Data deep2
		accepted_data_assertion_URIs_for_individuals_deep2.add(has_value_dprop_im_uri_2010);
		
		//Object
		accepted_object_assertion_URIs_for_individuals.add(name_oprop_im_uri_2010);
		accepted_object_assertion_URIs_for_individuals.add(article_oprop_im_uri_2010);
		
		
		//oaei 2013
		//Data
		accepted_data_assertion_URIs_for_individuals.add(birthName_dprop_im_uri);
		//It is a number and will be filtered
		//Shoyld be considered for the "role assertion inverted file"
		//accepted_data_assertion_URIs_for_individuals.add(population_dprop_im_uri);
		
		//Data deep2??
		//it is a comment
		
		//Object
		accepted_object_assertion_URIs_for_individuals.add(label_oprop_im_uri);
		accepted_object_assertion_URIs_for_individuals.add(curriculum_oprop_im_uri);
		accepted_object_assertion_URIs_for_individuals.add(places_oprop_im_uri);
		
		
		
		//Other accepted data assertions
		accepted_data_assertion_URIs_for_individuals.add(abstract_dprop_im_uri);
		accepted_data_assertion_URIs_for_individuals.add(label_dprop_im_uri);
		accepted_data_assertion_URIs_for_individuals.add(name2_dprop_im_uri);		
		
		
	}
	
	
	public static void setMinSize4Overlapping(int size){
		min_size_overlapping = size;
	}
	
	
	public static void readParameters(){
		readParameters("");
	}
	
	public static void readParameters(String path){
		
		try{
			
			//File file = new File("thresholds.txt");
			//System.out.println(file.getAbsolutePath() + "  " + file.exists());
			
			File file = new File (path + "parameters.txt");
			
			if (!file.exists()){
				//LogOutput.printAlways("Error reading LogMap parameters. File 'parameters.txt' is not available. Using default parameters.");
				LOGGER.info("Using default LogMap parameters");
				System.err.println("Error reading LogMap parameters. File 'parameters.txt' is not available. Using default parameters.");
				return;
			}
			
			LOGGER.info("Reading LogMap parameters from file.");
			
			
			//We reinit with URIs in file
			accepted_annotation_URIs_for_classes.clear();
			accepted_data_assertion_URIs_for_individuals.clear();
			allowed_instance_types.clear();
			filter_entities.clear();
			
			ReadFile reader = new ReadFile(path + "parameters.txt");
			//ReadFile reader = new ReadFile("/home/ernesto/OM_OAEI/logmap2_package/conf/thresholds.txt");
			
			String line;
			String[] elements;
			
			while ((line = reader.readLine()) != null){
				
				//Ignore commented lines
				if (line.startsWith("#")){
					continue;
				}
				
				if (line.indexOf("|")<0){
					continue;
				}
				//System.out.println(line);
				elements=line.split("\\|");
				
				if (elements[0].equals(print_output_str)){
					print_output = Boolean.valueOf(elements[1]);
				}
				else if (elements[0].equals(bad_score_scope_str)){
					bad_score_scope = Double.valueOf(elements[1]);
				}
				else if (elements[0].equals(good_isub_anchors_str)){
					good_isub_anchors = Double.valueOf(elements[1]);
				}
				else if (elements[0].equals(good_isub_candidates_str)){
					good_isub_candidates=Double.valueOf(elements[1]);
				}
				else if (elements[0].equals(good_confidence_str)){
					good_confidence = Double.valueOf(elements[1]);
				}
				else if (elements[0].equals(good_sim_coocurrence_str)){
					good_sim_coocurrence = Double.valueOf(elements[1]);
				}
				else if (elements[0].equals(min_conf_pro_map_str)){
					min_conf_pro_map = Double.valueOf(elements[1]);
				}
				
				else if (elements[0].equals(max_redundancy_labels_str)){
					max_redundancy_labels = Integer.valueOf(elements[1]);
				}
				else if (elements[0].equals(avoid_redundant_labels_str)){
					avoid_redundant_labels = Boolean.valueOf(elements[1]);
				}
				
				else if (elements[0].equals(max_ambiguity_str)){
					max_ambiguity = Integer.valueOf(elements[1]);
				}
				else if (elements[0].equals(good_ambiguity_str)){
					good_ambiguity = Integer.valueOf(elements[1]);
				}
				else if (elements[0].equals(use_overlapping_str)){
					use_overlapping = Boolean.valueOf(elements[1]);
				}
				else if (elements[0].equals(min_size_overlapping_str)){
					min_size_overlapping = Integer.valueOf(elements[1]);
				}
				else if (elements[0].equals(instance_matching_str)){
					perform_instance_matching = Boolean.valueOf(elements[1]);
				}
				
				
				else if (elements[0].equals(property_matching_str)){
					perform_property_matching = Boolean.valueOf(elements[1]);
				}
				
				
				
				else if (elements[0].equals(output_class_mappings_str)){
					output_class_mappings = Boolean.valueOf(elements[1]);
				}
				else if (elements[0].equals(output_prop_mappings_str)){
					output_prop_mappings = Boolean.valueOf(elements[1]);
				}
				else if (elements[0].equals(output_instance_mappings_str)){
					output_instance_mappings = Boolean.valueOf(elements[1]);
				}
				
				else if (elements[0].equals(output_instance_mapping_files_str)){
					output_instance_mapping_files = Boolean.valueOf(elements[1]);
				}
				
				
				
				else if (elements[0].equals(annotation_URI_str)){
					accepted_annotation_URIs_for_classes.add(elements[1]);
				}
				
				else if (elements[0].equals(category_URI_str)){
					accepted_property_URIs_for_instance_categories.add(elements[1]);
				}
				
				else if (elements[0].equals(data_assertion_URI_Indiv_str)){
					accepted_data_assertion_URIs_for_individuals.add(elements[1]);
				}
				else if (elements[0].equals(data_assertion_URI_Indiv_deep2_str)){
					accepted_data_assertion_URIs_for_individuals_deep2.add(elements[1]);
				}
				else if (elements[0].equals(object_assertion_URI_Indiv_str)){
					accepted_object_assertion_URIs_for_individuals.add(elements[1]);
				}
				else if (elements[0].equals(reason_datatypes_str)){
					reason_datatypes = Boolean.valueOf(elements[1]);
				}
				else if (elements[0].equals(reasoner_str)){
					reasoner = elements[1];
				}
				else if (elements[0].equals(timeout_str)){
					timeout = Integer.valueOf(elements[1]);
				}
				else if (elements[0].equals(output_equivalences_only_str)){
					output_equivalences_only = Boolean.valueOf(elements[1]);
				}
				
				else if (elements[0].equals(second_chance_conflicts_str)){
					second_chance_conflicts = Boolean.valueOf(elements[1]);
				}
				
				else if (elements[0].equals(ratio_second_chance_discarded_str)){
					ratio_second_chance_discarded = Integer.valueOf(elements[1]);
				}
				
				else if (elements[0].equals(use_umls_lexicon_str)){
					use_umls_lexicon = Boolean.valueOf(elements[1]);
				}
				else if (elements[0].equals(reverse_labels_str)){
					reverse_labels = Boolean.valueOf(elements[1]);
				}
				else if (elements[0].equals(allow_interactivity_str)){
					allow_interactivity = Boolean.valueOf(elements[1]);
				}
				else if (elements[0].equals(allow_multilingual_str)){
					allow_multilingual = Boolean.valueOf(elements[1]);
				}
				else if (elements[0].equals(is_test_mode_multilingual_str)){
					is_test_mode_multilingual = Boolean.valueOf(elements[1]);
				}
				else if (elements[0].equals(path_chinese_segmenter_dict_str)){
					path_chinese_segmenter_dict = elements[1];
				}
				else if (elements[0].equals(use_local_dict_str)){
					use_local_dict = Boolean.valueOf(elements[1]);
				}
				
				else if (elements[0].equals(call_online_translator_str)){
					call_online_translator = Boolean.valueOf(elements[1]);
				}
				
				
				else if (elements[0].equals(path_multilingual_tmp_str)){
					path_multilingual_tmp = elements[1];
				}
				else if (elements[0].equals(path_multilingual_local_str)){
					path_multilingual_local = elements[1];
				}				
				else if (elements[0].equals(path_mappings_categories_str)){
					path_mappings_categories = elements[1];
				}
				else if (elements[0].equals(target_lang_str)){
					target_lang = elements[1];
				}
				else if (elements[0].equals(translator_id_str)){
					translator_id = Integer.valueOf(elements[1]);
				}
				else if (elements[0].equals(allow_bioportal_str)){
					allow_bioportal = Boolean.valueOf(elements[1]);
				}
				else if (elements[0].equals(restrict_individual_types_str)){
					setRestrictInstanceTypes(Boolean.valueOf(elements[1]));
				}				
				
				else if (elements[0].equals(confidence_composed_mappings_str1)){
					confidence_composed_mappings1 = Double.valueOf(elements[1]);
				}
				else if (elements[0].equals(confidence_composed_mappings_str2)){
					confidence_composed_mappings2 = Double.valueOf(elements[1]);
				}
				
				
				else if (elements[0].equals(allowed_individual_type_str)){
					allowed_instance_types.add(elements[1]);
				}
				
				else if (elements[0].equals(filter_entity_str)){
					filter_entities.add(elements[1]);
				}
								
				else if (elements[0].equals(glogal_info_str)){
					extractGlobal_D_G_Info = Boolean.valueOf(elements[1]);
				}
				
				else if (elements[0].equals(max_mediating_ontologies_str)){
					max_mediating_ontologies = Integer.valueOf(elements[1]);
				}
				else if (elements[0].equals(use_stemming_str)){
					use_stemming = Boolean.valueOf(elements[1]);
				}
				
				
				
				
				
				
				
				
				
				
				
				
				
			}
			
			//System.out.println(accepted_annotation_URIs.size());
			//for (String str : accepted_annotation_URIs){
			//	System.out.println("Read: " + str);
			//}
			
			reader.closeBuffer();
		}
		catch (Exception e){
			//LogOutput.printAlways("Error reading LogMap 2 parameters file: " + e.getLocalizedMessage());
		}
		
	}


	public static boolean isRestrictInstanceTypesActive() {
		return restrict_instance_types;
	}


	public static void setRestrictInstanceTypes(boolean restrict_individual_types) {
		Parameters.restrict_instance_types = restrict_individual_types;
	}
	
	
}

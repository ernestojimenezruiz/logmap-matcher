package uk.ac.ox.krr.logmap2.bioportal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.MissingImportHandlingStrategy;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyLoaderConfiguration;
import org.semanticweb.owlapi.model.OWLOntologyManager;


import uk.ac.ox.krr.logmap2.io.LogOutput;
import uk.ac.ox.krr.logmap2.io.WriteFile;
import uk.ac.ox.krr.logmap2.mappings.objects.MappingObjectStr;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * New BioPortal Access using the new REST interface
 * (http://data.bioontology.org/documentation) See
 * https://github.com/ncbo/ncbo_rest_sample_code - for original examples
 * 
 * @author Ernesto
 * 
 */
public class RESTBioPortalAccess implements BioPortalAccess {

	private final String REST_URL = "http://data.bioontology.org";

	private final String API_KEY_Ernesto = "5b0306a7-d116-4aea-9a65-24f281599114";

	private final String API_KEY_Xi = "5736787d-b5dc-4e79-b60c-ca5f1e08a52b";

	private final ObjectMapper mapper = new ObjectMapper();

	// CUSTOM ACESS VARIABLES
	// Call to learn which are the most suitable ontologies
	private final int LEARNING_CALLS = 20;
	// Use top ontologies
	private final int MAX_ONTOLOGIES = 10;
	
	private final int MAX_SIZE_ONTOLOGY=20000;

	// BIOPORTAL CONSTANTS
	protected final String COLLECTION = "collection";
	protected final String PREFLABEL = "prefLabel";
	protected final String SYNONYM = "synonym";
	protected final String MAPPINGS = "mappings";
	protected final String LINKS = "links";
	protected final String SELF = "self";
	protected final String ONTOLOGY = "ontology";
	protected final String CLASSES = "classes";
	protected final String MAXDEPTH = "maxDepth";
	protected final String NEXTPAGE = "nextPage";
	protected final String ID = "@id";
	protected final String TYPE ="@type";
	protected final String PROCESS = "process";
	protected final String NAME = "name";
	protected final String PAGECOUNT = "pageCount";
	protected final String NOSOURCE = "nosource";
	protected final String owl_clas = "http://www.w3.org/2002/07/owl#Class";
	protected final String ONTOLOGY_LANGUAGE = "hasOntologyLanguage";
	

	//MAPPING SOURCES
	private final String XREF = "xref";  //related_match (filter)
	private final String LOOM = "loom";  //close_match
	private final String CUI = "cui";	//exact_match
	private final String NULL = "null";  //error?	(filter)
	private final String SAMEURIS = "same_uris";	//(filter)
	private final String SAMEURI = "same_uri";	//(filter)
	private final String MRMAP = "mrmap";	//close_match
	
	//New 2016
	private final String SOURCE = "source";
	
	
	// Those sources providing synonyms
	private Set<String> usefulSources = new HashSet<String>();

	private int numQuestionsBioPortal_searchAll = 0;

	/**
	 * We try to establish a simple connection with BioPortal to check if it is
	 * active
	 */
	public boolean isActive() {
			
        HttpURLConnection conn;
       
        try {
        	
        	//We perform a simple query over a small ontology. Currently it takes around half second
        	conn = getConnection(REST_URL + "/ontologies/BFO/metrics");
        	//conn = getConnection("http://data.bioontology.org/ontologies/BFO/classes/http%3A%2F%2Fwww.ifomis.org%2Fbfo%2F1.1%2Fspan%23Occurrent");
        	
            if (conn.getResponseCode()==200)
            	return true;
            
            System.out.println("BioPortal is not active: " + conn.getResponseCode());
            	 
        }
        catch (Exception e) {
        	
        }
        
       
        return false;
		
	
	}

	public Set<String> getSynonyms4Label(String label) {

		Set<String> synonyms = new HashSet<String>();

		try {

			JsonNode jsonConcepts = getConcepts4Label(label);

			for (JsonNode concept : jsonConcepts) {

				// System.out.println(concept.get(PREFLABEL).asText());
				if (concept.has(PREFLABEL)) {
					synonyms.add(concept.get(PREFLABEL).asText());
				}

				if (concept.has(SYNONYM)) {

					// We add the ontology to trusted sources
					usefulSources.add(getOntologyAcronym(concept.get(LINKS)
							.get(ONTOLOGY).asText()));
					// System.out.println("\t"+getOntologyAcronym(concept.get(LINKS).get(ONTOLOGY).asText()));

					for (JsonNode syn : concept.get(SYNONYM)) {
						synonyms.add(processSynonym(syn.asText()));
						// System.out.println("\t"+processSynonym(syn.asText()));

					}
				}

				// TODO Deal with mappings
				// System.out.println(concept.findValue(MAPPINGS));

			}

			// ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
			// System.out.println(writer.writeValueAsString(jsonConcepts));

		}

		catch (JsonProcessingException e) {
			System.out.println("Error performing the BioPortal call for: "
					+ label);
		} catch (IOException e) {
			System.out.println("Error performing the BioPortal call for: "
					+ label);
		}

		return synonyms;
	}

	
	

	//TODO To be done
	public Set<String> getSuitableOntologiesForLabels(Set<String> labels) {
		return null;
	}
	
	
	/**
	 * Get both synonyms and mappings from BioPortal - Added by Xi Chen
	 * Level 1 only
	 * 
	 * @param label
	 * @param synonyms
	 * @param mappings
	 */
	public void getSynonymsAndMappings4Label(String label, Set<String> synonyms, Set<String> set) {	
		try {
			JsonNode jsonConcepts = getConcepts4Label(label);
			for (JsonNode concept : jsonConcepts) {
//				synonyms.add(concept.get(PREFLABEL).asText());
//				if (concept.has(SYNONYM)) {
//					// We add the ontology to trusted sources
//					usefulSources.add(getOntologyAcronym(concept.get(LINKS)
//							.get(ONTOLOGY).asText()));
//
//					for (JsonNode syn : concept.get(SYNONYM)) {
//						synonyms.add(processSynonym(syn.asText()));
//					}
//				}
				//Deal with mappings
				String idUrl = concept.findValue(ID).asText();
				set.add(idUrl);				
			}
		} catch (IOException e) {
			System.out.println("Error performing the BioPortal call for: "
					+ label);
		}
	}
	
	/**arg0
	 * Get both synonyms and mappings from BioPortal - Added by Xi Chen
	 * Level 1 + level 2
	 * 
	 * @param label
	 * @param synonyms
	 * @param mappings
	 */
	public void getSynonymsAndMappings4Label2(String label, Set<String> synonyms, Set<String> set) {
		Set<String> mappingURLSet = new HashSet<String>();	
		try {
			JsonNode jsonConcepts = getConcepts4Label(label);
			for (JsonNode concept : jsonConcepts) {
//				synonyms.add(concept.get(PREFLABEL).asText());
//				if (concept.has(SYNONYM)) {
//					// We add the ontology to trusted sources
//					usefulSources.add(getOntologyAcronym(concept.get(LINKS)
//							.get(ONTOLOGY).asText()));
//
//					for (JsonNode syn : concept.get(SYNONYM)) {
//						synonyms.add(processSynonym(syn.asText()));
//					}
//				}
				//Deal with mappings
				String idUrl = concept.findValue(ID).asText();
				set.add(idUrl);	
				String url = concept.findValue(MAPPINGS).asText();
				mappingURLSet.add(url);		
			}
		} catch (IOException e) {
			System.out.println("Error performing the BioPortal call for: "
					+ label);
		}
		
		// Find level 2 mappings
		for (String url : mappingURLSet) {
			try {
				JsonNode jasonMapping = jsonToNode(getRequest(url));
				List<String> selfURL = jasonMapping.findValuesAsText(ID);
				set.addAll(selfURL);
			} catch (IOException e) {
				System.out.println("Error performing the BioPortal call for: "
						+ label);
			}
		}	
	}
	
	
	


		/*
		 * 
		 */
		public BioPortalOntologyInfo getMetricInfo4Onto(String ontoAcronym) {
			
			int classes = 0;
			int depth = 0;
			String language = "UNKNOWN";
			
			try {
				// JsonNode test =jsonToNode(getRequest(REST_URL +
				// "/ontologies/"+"UBERON"+"/metrics"));
				JsonNode ontologyNode = jsonToNode(getRequest(REST_URL
						+ "/ontologies/" + ontoAcronym + "/metrics"));
				//for test
				//System.out.println(REST_URL
				//		+ "/ontologies/" + ontoAcronym + "/metrics");
				
				
				//Only if 				
				if (ontologyNode.size() != 0){
					if (ontologyNode.has(CLASSES))
						classes = ontologyNode.get(CLASSES).asInt();
					
					if (ontologyNode.has(MAXDEPTH))
						depth = ontologyNode.get(MAXDEPTH).asInt();
					
					//Only for small ontologies (we save then a call)?
					//if (classes <= MAX_SIZE_ONTOLOGY){
					
					JsonNode ontologyNode2 = jsonToNode(getRequest(REST_URL
							+ "/ontologies/" + ontoAcronym + "/submissions"));
					
					//http://data.bioontology.org/ontologies/EHDA/submissions/
					
					if (ontologyNode2.size() != 0){
						if (ontologyNode2.isArray() && ontologyNode2.get(0).has(ONTOLOGY_LANGUAGE)){
							language = ontologyNode2.get(0).get(ONTOLOGY_LANGUAGE).asText();
						}					
					}
					
					
				}
				
				
				
				
					
				
				return new BioPortalOntologyInfo(
						ontoAcronym,
						classes,
						depth,
						language);
				
			} 
			catch (IOException e) {
				//e.printStackTrace();
				LogOutput.printError("Error getting metrics from bioportal ontology.");
			}

			return new BioPortalOntologyInfo(ontoAcronym);
		}
	

	
	
	
	
	
	
	
	
	
	

	public Set<String> getUsefulSources() {
		return usefulSources;
	}

	public int getNumberOfBioPortalCalls() {
		return numQuestionsBioPortal_searchAll;
	}

	/**
	 * Since we pass parameters in a GET call we cannot give unsupported
	 * characters or spaces
	 * 
	 * @param label
	 * @return
	 */
	private String processLabel(String label) {
		label = label.replaceAll("_", " ");
		try {
			return URLEncoder.encode(label, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return label;
		}

	}

	// TODO
	/**
	 * MOdified by Xi -- "(" might appear in the first index.
	 *  No sure if need handle the case like:
	 *  "(Meckel's diverticulum) or (persistent omphalomesenteric duct) or (persistent vitelline duct) (disorder)"
	 *  in http://data.bioontology.org/search?q=meckel%27s+diverticulum&exact_match=true&ontologies=RADLEX,REXO,NIFSTD,CCO,GO-EXT,MESH,GEXO,ONTOAD,NCIT,HUPSON,RETO,EFO,SNOMEDCT,UBERON,GO,ZFA,CL
	 * @param synonym
	 * @return
	 */
	private String processSynonym(String synonym) {

		// Remove suffixes like (morphologic abnormality) very common in
		// SNOMEDCT. 
		if (synonym.contains("(")) {
			int idx = synonym.indexOf("(");
			synonym = idx != 0 ? synonym.substring(0, idx - 1) : synonym;
		}
//		if (synonym.contains("("))
//			synonym = synonym.substring(0, synonym.indexOf("(") - 1);

		return synonym.toLowerCase();

	}

	public String getOntologyAcronym(String ontologyURI) {

		return ontologyURI.substring(ontologyURI.lastIndexOf("/") + 1);

	}

	/**
	 * Gets the sets of concepts which has an exact or not correspodence with the
	 * provided label
	 * 
	 * @param query
	 * @return
	 * @throws JsonProcessingException
	 * @throws IOException
	 */
	public JsonNode getConcepts4Label(String label, boolean exact)
			throws JsonProcessingException, IOException {

		numQuestionsBioPortal_searchAll++;

		String query = processLabel(label);

		
		//TODO
		//System.out.println(REST_URL + "/search?q=" + query
		//		+ getExactMatchParameter(true)/* + getFilterByOntology()*/);

		return jsonToNode(
				getRequest(REST_URL + "/search?q=" + query
						+ getExactMatchParameter(exact)/* + getFilterByOntology()*/))
				.get(COLLECTION);

	}
	
	
	/**
	 * Gets the sets of concepts which has an exact correspodence with the
	 * provided label
	 * 
	 * @param query
	 * @return
	 * @throws JsonProcessingException
	 * @throws IOException
	 */
	public JsonNode getConcepts4Label(String label)
			throws JsonProcessingException, IOException {

		return getConcepts4Label(label, true);

	}
	
	
	public void getMappingsForGivenOntologies(String source_onto, Set<String> target_ontos, Set<BioPortalMapping> mapping_objects, WriteFile writer){
		getMappingsForGivenOntologies(source_onto, target_ontos, mapping_objects, 1, 100, writer);
	}
	
	/**
	 * Given the id/acronym of two ontologies get the list of mappings between them
	 * @param source_onto1
	 * @param onto2
	 * @return
	 */
	public void getMappingsForGivenOntologies(String source_onto, Set<String> target_ontos, Set<BioPortalMapping> mapping_objects, int starting_page, int pagesize, WriteFile writer){
			
		//http://data.bioontology.org/ontologies/SNOMEDCT/mappings
		try {
			
			//String mapping_request = REST_URL + "/ontologies/"+source_onto+"/mappings";
			String mapping_request = REST_URL + "/ontologies/"+source_onto+"/mappings?" + "pagesize="+ pagesize + "&page="+starting_page;
			//String mapping_request = REST_URL + "/ontologies/"+source_onto+"/mappings?" + "page="+starting_page;
			
			JsonNode mappings_page = jsonToNode(getRequest(mapping_request));
						
			getMappingsFromPageForGivenOntologies(mapping_request, mappings_page, source_onto, target_ontos, mapping_objects, writer);
			
		} catch (JsonProcessingException e) {
			System.out.println("Error extracting the mappings for: " + source_onto + " and " + target_ontos);
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Error extracting the mappings for: " + source_onto + " and " + target_ontos);
			e.printStackTrace();
		}
		
				
		//TODO Some mappings created by CUI or by Loom  
		//(Lexical OWL Ontology Matcher: http://www.bioontology.org/wiki/index.php/LOOM)
		//See http://www.bioontology.org/wiki/index.php/BioPortal_Mappings
		
	
		
	}
	
	//Set<String> source_names = new HashSet<String>();
	/**
	 * Gets the mapping between two ontologies for a given page
	 * @param mappings_page
	 * @param source_onto
	 * @param onto2"@id"
	 * @throws JsonProcessingException
	 * @throws IOException
	 */
	private void getMappingsFromPageForGivenOntologies(
			String original_request, JsonNode mappings_page, String source_onto, Set<String> target_ontos, 
			Set<BioPortalMapping> mapping_objects, WriteFile writer) throws JsonProcessingException, IOException{
		
		
		
		int nextPageNumber=-1;
		if (!mappings_page.get(NEXTPAGE).isNull()){
			
			nextPageNumber = mappings_page.get(NEXTPAGE).asInt();
			
			if (nextPageNumber==1 || nextPageNumber==5 || nextPageNumber==100 || nextPageNumber==200 || nextPageNumber==500 || nextPageNumber==1000 || nextPageNumber==1500 || 
					nextPageNumber==2000 || nextPageNumber==2500 || nextPageNumber==2770 || nextPageNumber==3000 || nextPageNumber==3500 || nextPageNumber==4200 || nextPageNumber==4000
					|| nextPageNumber==4500 || nextPageNumber==5000 || nextPageNumber==5500 || nextPageNumber==6000)
				
				System.out.println("Getting page: " + nextPageNumber);
			
		}
		
		JsonNode nextPage = mappings_page.get(LINKS).get(NEXTPAGE);
		
		
		
		
		int numberOfPages = mappings_page.get(PAGECOUNT).asInt();
		
		
		
		//System.out.println(nextPage + "  "+  source_names);

		//List of mappings
		JsonNode mappings = mappings_page.get(COLLECTION);
		
		Set<String> sources;
		
		int numErroneous_mappings = 0;
		
		for (JsonNode mapping : mappings){
			
			try{
				
				sources = getMappingSources(mapping);
			
				if (mapping.get(CLASSES).isNull()){
					System.out.println("No CLASSES!!");
				}
				
				//List of classes in mapping (in principle only two)
				//Note that order is not respected... entity from onto1 may appear in second position)							
				if (mapping.get(CLASSES).isNull() || mapping.get(CLASSES).size()!=2){
					//System.out.println(mapping.get(CLASSES));
					numErroneous_mappings++;					
					continue;
				}				
				
			
				JsonNode classA = mapping.get(CLASSES).get(0);
				String onto_acronymA = getOntologyAcronym(classA.get(LINKS).get(ONTOLOGY).asText());				
				
				JsonNode classB = mapping.get(CLASSES).get(1);
				String onto_acronymB = getOntologyAcronym(classB.get(LINKS).get(ONTOLOGY).asText());
				
			
				String uri1;
				String uri2;
				String onto1acronym;
				String onto2acronym;
				String type1 = classA.get(TYPE).asText();
				String type2 = classB.get(TYPE).asText();
				if (!type1.equals(owl_clas) || !type2.equals(owl_clas)){
					System.out.println("MAPPING INVOLVING NO CLASSES!!");
				}
				
				
				if (source_onto.equals(onto_acronymB)){
					uri1 = classB.get(ID).asText();
					uri2 = classA.get(ID).asText();
					onto1acronym = onto_acronymB;
					onto2acronym = onto_acronymA;
				}
				else { //if (source_onto.equals(onto_acronymA)){
					uri1 = classA.get(ID).asText();
					uri2 = classB.get(ID).asText();
					onto1acronym = onto_acronymA;
					onto2acronym = onto_acronymB;
				}
				
				
				
				//Since its is rather slow we store all class mappings for future evaluation
				writer.writeLine(
						uri1 + "|" + 
						uri2 + "|" +
						onto1acronym + "|" +
						onto2acronym + "|" +
						set2string(sources));
				
				
				if (filterMappingBySource(sources)) //e.g. same_uris or null or xref
					continue;
				
				
				if (!target_ontos.contains(onto_acronymA) && !target_ontos.contains(onto_acronymB)){
					continue; //Not the mapping we were looking for
				}
								
				
				mapping_objects.add(
						new BioPortalMapping(
								uri1, uri2, onto1acronym, onto2acronym, sources));
			
			
			}
			catch (Exception e){
				//In case of error, continue with next one
				//e.printStackTrace();
				numErroneous_mappings++;
			}	
			
		}//end mapping
		
		if (numErroneous_mappings>0){
			System.out.println("\tNumber of mappings which led to an error: " + numErroneous_mappings + " in page before of " + nextPage);
		}
		
		//Recursivity till no more pages
		if (!nextPage.isNull() && nextPage.asText().length() != 0) {
			
			try{
				getMappingsFromPageForGivenOntologies(
						original_request,
						jsonToNode(getRequest(nextPage.asText())), 
						source_onto, target_ontos, mapping_objects, writer);
			}
			catch (Exception e){
				//In case of error, continue with next one				
				System.out.println("\tError accessing next page: " + nextPageNumber + " of " + numberOfPages +" -> "+  nextPage.asText());
				
				///try to access following one if any
				/*if (numberOfPages > nextPageNumber){
					
					nextPageNumber++;
				
					String newRequest;
					if (original_request.contains("page="))
						newRequest = original_request.split("page=")[0] + "page=" + nextPageNumber;
					else if (original_request.contains("pagesize="))
						newRequest = original_request + "&page=" + nextPageNumber;
					else
						newRequest = original_request + "?page=" + nextPageNumber;
					
					System.out.println("\tNew request: " + newRequest);
																	
					try{
						getMappingsFromPageForGivenOntologies(
							original_request,
							jsonToNode(getRequest(newRequest)), 
							source_onto, target_ontos, mapping_objects, writer);
					}
					catch (Exception e2){
						//In case of error, continue with next one				
						System.out.println("\tError accessing next page (2nd attempt): " + nextPageNumber + " of " + numberOfPages +" -> " + newRequest);
					}
				}*/
				
				
			}	
		}
		
		
		
	}
	
		
	
	private String set2string(Set<String> set){
		
		String string="";
		
		for (String s : set){
			string += s + ";";
		}
		return string.substring(0, string.length()-1); //we remove the last ";"
		
	}
	
	private Set<String> getMappingSources(JsonNode mapping){
		
		Set<String> source_list= new HashSet<String>();
		
		try {
		
			//List of mappings sources
			JsonNode mappings_sources = mapping.get(PROCESS);		
			
			for (JsonNode source : mappings_sources){		
				
				if (source.get(NAME).asText().equals(NULL))
					source_list.add(NOSOURCE);
				else
					source_list.add(source.get(NAME).asText());						
			}
			
			//May 2016: Now it appears directly under "source" and in capitals
			source_list.add(mapping.get(SOURCE).asText().toLowerCase());		
			
			//Detected a few cases without source
			if (source_list.isEmpty()){
				source_list.add(NOSOURCE);
			}
		}
		catch(Exception e) {
			System.err.println("Error accessing mapping sources.");
		}
		
		if (source_list.isEmpty())
			source_list.add(NOSOURCE);
		
		return source_list;
	}
	
	
	/**
	 * We filter if null, sameuri or xref
	 * @param sources
	 * @return
	 */
	private boolean filterMappingBySource(Set<String> sources){
		
		//if (sources.isEmpty())
		//	return true;
		
		//Ignore mappings to itself!
		//Same URI appears in different ontologies, but there is no need to add a mapping since the URI is the same
		if (sources.contains(SAMEURIS) || sources.contains(SAMEURI))
			return true;
		
		if (sources.contains(LOOM) || sources.contains(CUI) || sources.contains(MRMAP))
			return false;
		
		
		return true; ////only XREF, NULL, NOSOURCE
		
	}
	

	/**
	 * We will filter by ontologies only when we have learnt which are the most
	 * suitable ontologies (i.e. the ones providing synonyms). It is expected
	 * that filtering by ontology will speed up the process quite a lot
	 * 
	 * @return
	 */
	private String getFilterByOntology() {

		StringBuilder filter = new StringBuilder();
		if (numQuestionsBioPortal_searchAll > LEARNING_CALLS) {
			if (!usefulSources.isEmpty()) {

				filter.append("&ontologies=");

				for (String string : usefulSources) {
					filter.append(string);
					filter.append(",");
				}
				return filter.substring(0, filter.length() - 1);
			}
		}

		return filter.toString();
	}

	private String getExactMatchParameter(boolean exact) {
		return "&exact_match=" + String.valueOf(exact).toLowerCase();
	}

	private JsonNode jsonToNode(String json) throws JsonProcessingException,
			IOException {

		return mapper.readTree(json);

	}

	private String getRequest(String urlToGet) throws IOException {

		HttpURLConnection conn;
		BufferedReader rd;
		String line;
		String result = "";
		
		
		//In some cases it fails the connection. Try several times
		boolean success=false;
		int attempts=0;
		

		//TODO how many attempts?
		//while(!success && attempts<25){
		while(!success && attempts<3){	

			
			attempts++;
			
			try{
				conn = getConnection(urlToGet);

				rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));

				while ((line = rd.readLine()) != null) {
					result += line;
				}
				rd.close();
				
				if (!result.isEmpty())
					success=true;
			}
			
			catch(IOException e){
				System.out.println("Error accessing: " + urlToGet + "  Attempt: " + attempts);
			}
			
		}
		
		if (!success)
			throw new IOException(); //We throw error to check next page
		else if (attempts>1)
			System.out.println("SUCCESS accessing: " + urlToGet + "  Attempt: " + attempts);
				
		return result;
	}

	private HttpURLConnection getConnection(String urlToGet) throws IOException {

		URL url;
		HttpURLConnection conn;

		url = new URL(urlToGet);
		conn = (HttpURLConnection) url.openConnection();

		conn.setRequestMethod("GET");
		conn.setRequestProperty("Authorization", "apikey token="
				+ API_KEY_Ernesto);
		conn.setRequestProperty("Accept", "application/json");

		return conn;

	}
	
	
	public OWLOntology downLoadOntology(String ontoAcronym, int attempts) {
		
		HttpURLConnection conn;
		String urlToGet = REST_URL	+ "/ontologies/" + ontoAcronym + "/download";
		
		System.out.println("Downloading ontology " + ontoAcronym + ". Attempt: " + attempts);
		
		
		// Get hold of an ontology manager 
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		//Very important to avoid problems with ontologies
		OWLOntologyLoaderConfiguration config = new OWLOntologyLoaderConfiguration();
		config.setMissingImportHandlingStrategy(MissingImportHandlingStrategy.SILENT);
		manager.setOntologyLoaderConfiguration(config);
		
		
		OWLOntology MOOntology = null;
		try {
			
			conn = getConnection(urlToGet);
			
			conn.setReadTimeout(600000);//10 min / 600 seconds as timeout
			InputStream in = conn.getInputStream();
					
			MOOntology = manager.loadOntologyFromOntologyDocument(in);
			
			//manager.setOntologyDocumentIRI(loadOntology, IRI.create("http://bioportal,ontology.org/"+ontoAcronym));
			//manager.setOntologyFormat(loadOntology, new RDFXMLOntologyFormat());			
			//MOOntology = manager.createOntology(loadOntology.getAxioms(), IRI.create("http://bioportal.ontology.org/"+ontoAcronym));
			
			
			//System.out.println("Loaded ontology: " + loadOntology.toString());
			
			/*String fileName = (conn.getHeaderField("Content-Disposition").split("="))[1] ;
			fileName = fileName.split("\"")[1];
			System.out.println(fileName);
			//conn.getRequestProperty("filename");
			
			Files.copy(in, Paths.get("OAEI2013/"+fileName));
			//Files.copy(in, Paths.get("OAEI2013/"+ontoAcronym + ".owl"));
*/
	
		} catch (IOException e) {			
			if (attempts<3) //we try again
				return downLoadOntology(ontoAcronym, attempts+1);
			else
				System.err.println("Error downloading the ontology "+ ontoAcronym + " from BioPortal.");
		} catch (OWLOntologyCreationException e) {
			//e.printStackTrace();			
			if (attempts<3) //we try again
				return downLoadOntology(ontoAcronym, attempts+1);
			else{
				System.err.println("Error creating BioPortal ontology "+ ontoAcronym + " from input stream.");
				e.printStackTrace();
			}
		}
		
		//return loadOntology;
		return MOOntology;

	}	
	
	
	

	public static void main(String[] args) throws Exception {
		RESTBioPortalAccess bioportal = new RESTBioPortalAccess();

		long end, init;

		
		
		if (bioportal.isActive()) {
			
			System.out.println("BioPortal is active: ");

			init = Calendar.getInstance().getTimeInMillis();
			
			Set<MappingObjectStr> mapping_set = new HashSet<MappingObjectStr>();
			
			System.out.println("Axioms UBERON: " + bioportal.downLoadOntology("UBERON", 1).getAxiomCount());
			System.out.println("Axioms UBERON: " + bioportal.downLoadOntology("MP", 1).getAxiomCount());
			
			
			//bioportal.getMappingsForGivenOntologies("BP","BDO", mapping_set);
			
			//System.out.println("Source names: " + bioportal.source_names);
			
			/*
			bioportal.getSynonyms4Label("malignant_melanoma");
			System.out.println(bioportal.getNumberOfBioPortalCalls() + " "
					+ bioportal.getUsefulSources().size());
			end = Calendar.getInstance().getTimeInMillis();
			System.out
					.println((double) ((double) end - (double) init) / 1000.0);

			init = Calendar.getInstance().getTimeInMillis();
			bioportal.getSynonyms4Label("malignant_melanoma");
			// bioportal.getSynonyms4Label("ovarian follicle");
			System.out.println(bioportal.getNumberOfBioPortalCalls() + " "
					+ bioportal.getUsefulSources().size());
			*/
			
			
			end = Calendar.getInstance().getTimeInMillis();
			System.out
					.println("TIME: " + (double) ((double) end - (double) init) / 1000.0);
			

		}

	}


}

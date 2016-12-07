package uk.ac.ox.krr.logmap2.web_service;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Stack;
import java.util.TreeSet;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;

//import uk.ac.ox.krr.logmap2.interactive.InteractiveProcess;
import uk.ac.ox.krr.logmap2.Parameters;
import uk.ac.ox.krr.logmap2.indexing.IndexManager;
import uk.ac.ox.krr.logmap2.interactive.objects.MappingObjectInteractivity;
import uk.ac.ox.krr.logmap2.io.ReadFile;
import uk.ac.ox.krr.logmap2.io.WriteFile;
import uk.ac.ox.krr.logmap2.utilities.Utilities;

public class InteractiveProcess_WebService {
	
	private HTMLResultsFileManager progress_manager;

	private IndexManager index;
	
	private List<MappingObjectInteractivity_WebService> listOrderedMappings2Ask = 
			new ArrayList<MappingObjectInteractivity_WebService>(); 
		
	private String path_output;
	//private String iri_ouput;
	
	private String email;
	private String name;
	private String id_task;
	private int reasoner;
	
	private int numberFeedbackIterations=0;
	private int numberAssessedMappings=0; //directly
	
	private boolean userForcedEnd = false;
	
	
	private String path_mappings2assess;
	
	
	private int remainingMappings=0;
	
	/**New mappings (to process) added in interactivity (by user or heuristic)*/ 
	private Stack<MappingObjectInteractivity_WebService> new2Add;
	/**New mappings (to process) deleted in interactivity (by user or heuristic)*/
	private Stack<MappingObjectInteractivity_WebService> new2Del;
	
	//We will start interaction and apply heuristics...
	//This class will only complement the InteractiveProcess_Request..
	
	
	/**
	 * To be called from main LogMap process
	 * @param progress_manager
	 * @param mappings2ask
	 * @param index
	 * @param path_output
	 * @param iri_ouput
	 */
	public InteractiveProcess_WebService(
			HTMLResultsFileManager progress_manager,
			Set<MappingObjectInteractivity> mappings2ask,
			IndexManager index,
			String path_output,
			//String iri_ouput,
			String email,
			String name,
			String id_task,
			int reasoner){
		
		this.index=index;
		this.path_output=path_output;
		//this.iri_ouput=iri_ouput;
		this.path_mappings2assess = path_output + "/session.log";
		
		//Session data
		this.email = email;
		this.name = name;
		this.id_task = id_task;
		this.reasoner = reasoner;
		numberFeedbackIterations = 0; //
		
		
		//Create ordered mappings
		createOrderedMappings(mappings2ask);
		
		
		//Set URIS, Labels, synonyms,  superclasses and subclasses...
		setData4Mappings();
		
		
		//Extract ambiguity and conflicts
		extractConflicts();
		extractAmbiguities();
		
		//Store all mappings + id
		storeSessionData();
		
		/*System.out.println("Mappings2ask: " + 
		mapping_extractor.getListOfMappingsToAskUser().size() + " " + mappings2show.size());
		System.out.println("Mappings2ask: " + 
				mapping_extractor.getListOfMappingsToAskUser());
		System.out.println("Mappings2ask: " + mappings2show);
		*/
		
		
		progress_manager.addOutputMappingsDiv(listOrderedMappings2Ask.size());
		
		
		/* 
		int maxAxioms2show;
		
		//If size is 10 or less we show all
		if (listOrderedMappings2Ask.size()<11){
			 maxAxioms2show=listOrderedMappings2Ask.size();  
		}
		else{
			maxAxioms2show = 5;
		}
		
		progress_manager.setNumCurrentMappings(maxAxioms2show);
		
		
		
		
		// pogress
		progress_manager.addHeaderMappings2Ask_Form(maxAxioms2show, listOrderedMappings2Ask.size()); ///mappings2show.size()
		
		int num=0;
		
		for (int i=0; i<listOrderedMappings2Ask.size(); i++) { //show only subset...
			
			//If size is 10 or less we show all
			if (num+1 > maxAxioms2show){
				break;  //Note that we should keep track of mappings
			}
			//Note that we should keep track of mappings that have been already assessed
			
			// pogress
			progress_manager.addMapping2Form(
					i, //important to identify mappings
					num, //important top access mapping in form
					//listOrderedMappings2Ask.get(i).getNameSpace1(),
					//listOrderedMappings2Ask.get(i).getNameSpace2(),
					listOrderedMappings2Ask.get(i).getURI1(),
					listOrderedMappings2Ask.get(i).getURI2(),
					listOrderedMappings2Ask.get(i).getLabel1(),
					listOrderedMappings2Ask.get(i).getLabel2(),
					listOrderedMappings2Ask.get(i).getIde1(),
					listOrderedMappings2Ask.get(i).getIde2(),
					listOrderedMappings2Ask.get(i).getDirMapping(), 
					listOrderedMappings2Ask.get(i).getSemSim(),
					listOrderedMappings2Ask.get(i).getLexSim(),
					listOrderedMappings2Ask.get(i).getSuperClasses1_Str(),
					listOrderedMappings2Ask.get(i).getSuperClasses2_Str(),
					listOrderedMappings2Ask.get(i).getSubClasses1_Str(),
					listOrderedMappings2Ask.get(i).getSubClasses2_Str(),
					listOrderedMappings2Ask.get(i).getSynonyms1_Str(),
					listOrderedMappings2Ask.get(i).getSynonyms2_Str(),
					getMappingRepresentation(listOrderedMappings2Ask.get(i).getMappingsInconflict()),
					getMappingRepresentation(listOrderedMappings2Ask.get(i).getAmbiguousMappings())
					);
			
			num++;
			
		}

		//pogress
		progress_manager.addTailMappings2Ask_Form();
		*/
		
		
		
	}
	
	
	
	
	
	
	
	private void createOrderedMappings(Set<MappingObjectInteractivity> mappings2ask) {
		
		TreeSet<MappingObjectInteractivity> orderedSet = 
				new TreeSet<MappingObjectInteractivity>(new MappingInteractivityComparator());
		
					
		for (MappingObjectInteractivity mapping : mappings2ask){
		
			if (mapping.isAddedFlagActive() || mapping.isRemovedFlagActive()){
				continue;
			}
			
			orderedSet.add(mapping);
			
		}
		
		//Create ordered list
		Iterator<MappingObjectInteractivity> it = orderedSet.iterator();
		
		MappingObjectInteractivity mapping;
		
		int num=0;
		
		while (it.hasNext()){
			
			mapping = it.next();
			
			listOrderedMappings2Ask.add(new MappingObjectInteractivity_WebService(
					mapping.getIdentifierOnto1(),
					mapping.getIdentifierOnto2(),
					Utilities.getStringRepresentation4Dir(mapping.getDirMapping()), 
					Utilities.getRoundValue(mapping.getLexSim(), 3),
					Utilities.getRoundValue(mapping.getScope(), 3)
					));
		}
		
		
		orderedSet.clear();
	}

	
	private void setData4Mappings(){
		
		int ide1;
		int ide2;
		
		for (int i=0; i<listOrderedMappings2Ask.size(); i++){
			
					
			ide1 = listOrderedMappings2Ask.get(i).getIde1();
			ide2 = listOrderedMappings2Ask.get(i).getIde2();
			
			
			
			listOrderedMappings2Ask.get(i).setLabel1(index.getLabel4ConceptIndex(ide1));
			listOrderedMappings2Ask.get(i).setLabel2(index.getLabel4ConceptIndex(ide2));
			
			listOrderedMappings2Ask.get(i).setURI1(index.getIRIStr4ConceptIndex(ide1));
			listOrderedMappings2Ask.get(i).setURI2(index.getIRIStr4ConceptIndex(ide2));
			
					
			//Synonyms
			//---------------
			listOrderedMappings2Ask.get(i).setSynonyms1_Str(getStringFromSetStrings(index.getAlternativeLabels4ConceptIndex(ide1), "No synonyms"));
			listOrderedMappings2Ask.get(i).setSynonyms2_Str(getStringFromSetStrings(index.getAlternativeLabels4ConceptIndex(ide2), "No synonyms"));			
			
			
			//SUBCLASSES
			//--------------------
			//for (int sub1 : index.getDirectSubClasses4Identifier(ide1, false)){
			Set<String> set_strings = new HashSet<String>();
			
			for (int sub1 : index.getSubsetOfSubClasses4Identifier(ide1)){
				
				if (set_strings.size()>5)
					break;
					
				set_strings.add(index.getLabel4ConceptIndex(sub1));
				
			}
			listOrderedMappings2Ask.get(i).setSubClasses1_Str(getStringFromSetStrings(set_strings, "No subclasses"));
			
			
			set_strings.clear();
			
			//for (int sub2 : index.getDirectSubClasses4Identifier(ide2, false)){
			for (int sub2 : index.getSubsetOfSubClasses4Identifier(ide2)){
				
				if (set_strings.size()>5)
					break;
				
				set_strings.add(index.getLabel4ConceptIndex(sub2));
			}
			listOrderedMappings2Ask.get(i).setSubClasses2_Str(getStringFromSetStrings(set_strings, "No subclasses"));
			
			
			set_strings.clear();
			
						
			//SUPERCLASSES
			//--------------------------
			//for (int super1 : index.getDirectSuperClasses4Identifier(ide1, false)){
			for (int super1 : index.getSubsetOfSuperClasses4Identifier(ide1)){
				
				set_strings.add(index.getLabel4ConceptIndex(super1));
								
			}
			listOrderedMappings2Ask.get(i).setSuperClasses1_Str(getStringFromSetStrings(set_strings, "No superclasses"));
			
			set_strings.clear();
			
			//for (int super2 : index.getDirectSuperClasses4Identifier(ide2, false)){
			for (int super2 : index.getSubsetOfSuperClasses4Identifier(ide2)){
				
				set_strings.add(index.getLabel4ConceptIndex(super2));
				
			}
			listOrderedMappings2Ask.get(i).setSuperClasses2_Str(getStringFromSetStrings(set_strings, "No superclasses"));

			
		}
		
		
	}
	
	
	/**
	 * Serialise set of integers
	 * @param set
	 * @return
	 */
	private String getStringFromSetIntegers(Set<Integer> set){
		return Utilities.serializeSetIntegers(set);
	}
	
	
	private String getStringFromSetStrings(Set<String> set, String info_empty_set){
						
		if (set.size()==0){
			return info_empty_set;
		}
		
		String string_output="";
		
		for (String element : set){
			if (string_output.length()==0)
				string_output = element;			
			else
				string_output = string_output + ", " + element; 
		}		
		
		return string_output;
	}
	
	
	
	
	private void extractAmbiguities(){
		
		
		for (int i=0; i<listOrderedMappings2Ask.size()-1; i++) {
			for (int j=i+1; j<listOrderedMappings2Ask.size(); j++) {
				
				if (areMappingsAmbiguous(
						listOrderedMappings2Ask.get(i).getIde1(), 
						listOrderedMappings2Ask.get(i).getIde2(),
						listOrderedMappings2Ask.get(j).getIde1(),
						listOrderedMappings2Ask.get(j).getIde2())){
					
					listOrderedMappings2Ask.get(i).addAmbiguousMapping(j);
					listOrderedMappings2Ask.get(j).addAmbiguousMapping(i);
					
				}
				
			}
			
		}
		
		
	}
	
	private void extractConflicts(){
		
		
		for (int i=0; i<listOrderedMappings2Ask.size()-1; i++) {
			for (int j=i+1; j<listOrderedMappings2Ask.size(); j++) {
				
				if (areMappingsInConflict(
						listOrderedMappings2Ask.get(i).getIde1(), 
						listOrderedMappings2Ask.get(i).getIde2(),
						listOrderedMappings2Ask.get(j).getIde1(),
						listOrderedMappings2Ask.get(j).getIde2())){
					
					listOrderedMappings2Ask.get(i).addConflictiveMapping(j);
					listOrderedMappings2Ask.get(j).addConflictiveMapping(i);
					
				}
				
			}
			
		}
		
		
	}
	
	
	/**
	 * For mappings in conflict and ambiguous mappings
	 * @param mappings_ids
	 * @return
	 * @deprecated
	 */
	private Set<String> getMappingRepresentation(Set<Integer> mappings_ids){
		
		Set<String> mappings_ids_Str = new HashSet<String>();
		
		String mapping_representation;
		
		//writer.writeLine(ns1 + " <b>" + label1 + "</b>   " + dir + "   " + ns2 + " <b>" + label2 + "</b>, " +
		
		for (int i : mappings_ids){
			
			mapping_representation = 
					"<b>" +
					listOrderedMappings2Ask.get(i).getLabel1() + "   " +
					listOrderedMappings2Ask.get(i).getDirMapping() + "   " +
					listOrderedMappings2Ask.get(i).getLabel2() + "</b>&nbsp;&nbsp;&nbsp; " +
					"semantic sim: " + listOrderedMappings2Ask.get(i).getSemSim() + "&nbsp;&nbsp;  lexical sim: " + listOrderedMappings2Ask.get(i).getLexSim();
			
			mappings_ids_Str.add(mapping_representation);
			
		}
		
		
		return mappings_ids_Str;
		
	}
	
	
	
	
	
	
	
	private boolean areMappingsAmbiguous(int ideA, int ideB, int ideAA, int ideBB){
		
		
		//We have already checked they are not equivalent
		if (ideA==ideAA || ideB==ideBB){
			
			return true;
			
		}		
		
		return false;
		
	}
	
	
	
	//private double getConfidence(MappingObjectInteractivity m){
		//return m.getSimilarityList().get(1);
	//}
	
	
	
	private boolean areMappingsInConflict(int ideA, int ideB, int ideAA, int ideBB){
		
		boolean AequivAA=false;
		boolean BequivBB=false;
		
		boolean AcontAA=false;
		boolean AAcontA=false;
		boolean BcontBB=false;
		boolean BBcontB=false;
		
		boolean AdisjAA=false;
		boolean BdisjBB=false;
		
		boolean conflict=false;

		
		AequivAA = index.areEquivalentClasses(ideA, ideAA);
		
		BequivBB = index.areEquivalentClasses(ideB, ideBB);
		
		AcontAA = AequivAA || index.isSubClassOf(ideA, ideAA);
				
		AAcontA = AequivAA || index.isSubClassOf(ideAA, ideA);
		
		BcontBB = BequivBB || index.isSubClassOf(ideB, ideBB);
		
		BBcontB = BequivBB || index.isSubClassOf(ideBB, ideB);
	
		AdisjAA = !AcontAA && !AAcontA && index.areDisjoint(ideA, ideAA);  //over same ontology
		
		BdisjBB = !BcontBB && !BBcontB && index.areDisjoint(ideB, ideBB);
		
		//TODO New method
		//AdisjAA = (index.areDisjoint(ideA, ideAA) || index.isDisjointWithDescendants(ideA, ideAA) || index.isDisjointWithDescendants(ideAA, ideA));
		//BdisjBB = (index.areDisjoint(ideB, ideBB) || index.isDisjointWithDescendants(ideB, ideBB) || index.isDisjointWithDescendants(ideBB, ideB));
		
		
				
		conflict = ((AcontAA || AAcontA) && BdisjBB) || ((BcontBB || BBcontB) && AdisjAA);
		
		return conflict;
		
	}
	
	
	
	
	
	
	
	
	/**
	 * @return the orderedMappings2Ask
	 */
	public List<MappingObjectInteractivity_WebService> getListOrderedMappings2Ask() {
		return listOrderedMappings2Ask;
	}


	
	
	/**
	 * `s the mappings to be assessed by the user and some other session info
	 */
	public void storeSessionData(){
		
		// We open/close it each time
		WriteFile sessionWriter = new WriteFile(path_mappings2assess, false);
		//path_output + "/session.log", false);

		//We store the mappings

		String mapping_session_line;
		
		//store remaining mappings, number of user intervention
		//and other session information
		sessionWriter.writeLine(numberFeedbackIterations + "|" + email + "|" + name + "|" + id_task + "|" + reasoner);
		
		for (int i=0; i<listOrderedMappings2Ask.size(); i++){
		
			mapping_session_line = listOrderedMappings2Ask.get(i).getURI1();
			mapping_session_line += "|" +	listOrderedMappings2Ask.get(i).getURI2();
			
			mapping_session_line += "|" +	listOrderedMappings2Ask.get(i).getDirMapping();
			
			mapping_session_line += "|" +	listOrderedMappings2Ask.get(i).getLexSim();			
			mapping_session_line += "|" +	listOrderedMappings2Ask.get(i).getSemSim();
			
			mapping_session_line += "|" +	listOrderedMappings2Ask.get(i).getLabel1();
			mapping_session_line += "|" +	listOrderedMappings2Ask.get(i).getLabel2();
			
			mapping_session_line += "|" +	listOrderedMappings2Ask.get(i).getSuperClasses1_Str();
			mapping_session_line += "|" +	listOrderedMappings2Ask.get(i).getSuperClasses2_Str();
			
			mapping_session_line += "|" +	listOrderedMappings2Ask.get(i).getSubClasses1_Str();
			mapping_session_line += "|" +	listOrderedMappings2Ask.get(i).getSubClasses2_Str();
			
			
			mapping_session_line += "|" +	listOrderedMappings2Ask.get(i).getSynonyms1_Str();
			mapping_session_line += "|" +	listOrderedMappings2Ask.get(i).getSynonyms2_Str();
			
			//mapping_session += "|" +	listOrderedMappings2Ask.get(i).getMappingsInconflict();
			//mapping_session += "|" +	listOrderedMappings2Ask.get(i).getAmbiguousMappings();
			
			mapping_session_line += "|" +	getStringFromSetIntegers(listOrderedMappings2Ask.get(i).getMappingsInconflict());
			mapping_session_line += "|" +	getStringFromSetIntegers(listOrderedMappings2Ask.get(i).getAmbiguousMappings());
			
			mapping_session_line += "|" +	listOrderedMappings2Ask.get(i).isAddedFlagActive();
			mapping_session_line += "|" +	listOrderedMappings2Ask.get(i).isRemovedFlagActive();
			mapping_session_line += "|" +	listOrderedMappings2Ask.get(i).isAssessedFlagActive();
			
			sessionWriter.writeLine(mapping_session_line);
			
		}
		
		sessionWriter.closeBuffer();
				
	}
	
	
	
	public void clearStructures(){
		//TODO
		listOrderedMappings2Ask.clear();
	}
	
	
	
	
	
	/**
	 * To be called from interactive process
	 * @param path_mappings2assess
	 */
	public InteractiveProcess_WebService(String path_mappings2assess){
		
		try{
			
			//Read mappings to ask user
			readMappings2assess(path_mappings2assess);
			
			//
			
		}
		catch (Exception e){
			System.err.println("Error starting interactive session: ");
			e.printStackTrace();
		}
		
		
	}
	
	
	public void applyFeedback(String feedback){
		
		numberFeedbackIterations++;
		
		//Deserialise feedback and apply it
		//add_0_false;add_1_false;add_2_false;add_3_false;add_4_false;add_5_false
		
		String[] feedback_elements = feedback.split(";");
		
		String[] feedback_mapping;
		
		String action;
		int ide_mapping;
		boolean apply_heuristics;
		
		new2Add = new Stack<MappingObjectInteractivity_WebService>();
		new2Del = new Stack<MappingObjectInteractivity_WebService>();
		
		for (int i=0; i<feedback_elements.length; i++){
			
			if (feedback_elements[i].length()>0){
				feedback_mapping = feedback_elements[i].split("_");
				
				action = feedback_mapping[0];
				ide_mapping = Integer.valueOf(feedback_mapping[1]);
				apply_heuristics = Boolean.valueOf(feedback_mapping[2]);
				
				//TODO if it was already deleted means that the selection was in conflict with previous selections
				//If it was added the same, we give preference to previous selections/decisions
				if (!listOrderedMappings2Ask.get(ide_mapping).hasDecision()){
					
										
					if (action.equals("add")){
						listOrderedMappings2Ask.get(ide_mapping).setAddedFlag(true);
						listOrderedMappings2Ask.get(ide_mapping).setAssessedFlag(true);
						new2Add.add(listOrderedMappings2Ask.get(ide_mapping));
						
					}
					else{
						listOrderedMappings2Ask.get(ide_mapping).setRemovedFlag(true);
						listOrderedMappings2Ask.get(ide_mapping).setAssessedFlag(true);
						new2Del.add(listOrderedMappings2Ask.get(ide_mapping));
					}
					
					//Apply automatic decisions
					exploitImpactUserFeedback(apply_heuristics);
				}
				
			}			
		}
		
		//Update number of remaining mappings
		extractRemainingMappings();
		
	}
	
	
	private void extractRemainingMappings(){
		
		remainingMappings = 0;
		
		for (int i=0; i<listOrderedMappings2Ask.size(); i++){
			
			if (!listOrderedMappings2Ask.get(i).isAddedFlagActive() && !listOrderedMappings2Ask.get(i).isRemovedFlagActive()){
				
				remainingMappings++;
			
			}
		}
	}
	
	/**
	 * We apply automatic decision depending on the conflictive and ambiguous mappings
	 */
	private void exploitImpactUserFeedback(boolean apply_heuristics){
		
		//if apply_heuristics: never remove or add a mapping with a decision
		//hasDecision()
		
		MappingObjectInteractivity_WebService mapping;
		
		while (new2Add.size()>0 || new2Del.size()>0){
			
			if (new2Add.size()>0){
				
				mapping = new2Add.pop();
				
				//Marks them as deleted and add them to new2Del stack
				//removeMappingsInConflictWithAddition(mapping);
				
				for (int m_ide : mapping.getMappingsInconflict()){
					
					listOrderedMappings2Ask.get(m_ide).setRemovedFlag(true);
					new2Del.push(listOrderedMappings2Ask.get(m_ide));
				}
				
				if (apply_heuristics){
					
					for (int m_ide : mapping.getAmbiguousMappings()){
						//Only if not decision yet
						if (!listOrderedMappings2Ask.get(m_ide).hasDecision()){
							listOrderedMappings2Ask.get(m_ide).setRemovedFlag(true);
							new2Del.push(listOrderedMappings2Ask.get(m_ide));
						}
					}
				}
				
			
			}
			
			
			
			if (new2Del.size()>0){
				
				mapping = new2Del.pop();
				
				if (apply_heuristics){
					
					for (int m_ide : mapping.getAmbiguousMappings()){
						//Only if not decision yet
						if (!listOrderedMappings2Ask.get(m_ide).hasDecision()){
							listOrderedMappings2Ask.get(m_ide).setAddedFlag(true);
							new2Add.push(listOrderedMappings2Ask.get(m_ide));
						}
					}
				}
				
			}
			
			
		}
		
		
		
	}
	
	
	public void checkUserAssessment(){
		for (int i=0; i<listOrderedMappings2Ask.size(); i++){
			//Already assessed
			if (listOrderedMappings2Ask.get(i).isAssessedFlagActive()){
				numberAssessedMappings++;  //statistics
				continue;
			}
		}
	}
	
	
	
	public void applyAutomaticHeuristics(){
		
		userForcedEnd = true;
		
		//Based on lex and sem similarities
		//TODO
		for (int i=0; i<listOrderedMappings2Ask.size(); i++){
			
			//Already assessed: not necessary
			//if (listOrderedMappings2Ask.get(i).isAssessedFlagActive()){
			//	numberAssessedMappings++;  //statistics
			//	continue;
			//}
			
			if (listOrderedMappings2Ask.get(i).isAddedFlagActive() || listOrderedMappings2Ask.get(i).isRemovedFlagActive())
				continue;
			
			if (listOrderedMappings2Ask.get(i).getConfidence()>Parameters.good_confidence &&
				listOrderedMappings2Ask.get(i).getScope()>Parameters.bad_score_scope){
				
				listOrderedMappings2Ask.get(i).setAddedFlag(true);
				
			}
			else{
				listOrderedMappings2Ask.get(i).setRemovedFlag(true);
			}
					
			
		}
		
		//Update number of remaining mappings
		remainingMappings=0;
		
	}
	
	
	
	/**
	 * Get top mappings
	 * @return
	 */
	public List<Integer> getTopMappings2Show(){		
		
		List<Integer> topList =  new ArrayList<Integer>();
		
		//Max number
		int max;
		if (getRemainingMappings()<11){
			max = getRemainingMappings(); 
		}		
		else{
			max = 5;
		}
		
		
		for (int i=0; i<listOrderedMappings2Ask.size(); i++){
			
			//With a decision
			if (listOrderedMappings2Ask.get(i).isAddedFlagActive() || listOrderedMappings2Ask.get(i).isRemovedFlagActive())
				continue;
			
			topList.add(i);
			
			if (topList.size() >= max)
				break;
						
		}
		
		return topList;
		
	}

	
	public boolean didUserFinihsedInteractivity(){
		return userForcedEnd;
	}
	
	public int getRemainingMappings(){
		return remainingMappings;
	}
	
	/**
	 * Directly assessed by user
	 * @return
	 */
	public int getNumberMappingAssessedByUser(){
		return numberAssessedMappings; //
	}

	
	public int getReasoner(){
		return reasoner;
	}
	
	public int getNumberUserInterventions(){
		return numberFeedbackIterations;
	}
	
	public String getEmail(){
		return email;
	}
	
	public String getName(){
		return name;
	}
	
	public String getIDTask(){
		return id_task;
	}
	
	
	private void readMappings2assess(String path_mappings2assess) throws Exception{
		
		this.path_mappings2assess = path_mappings2assess;
		
		//READ FILE
		//http://mouse.owl#MA_0000350|http://human.owl#NCI_C33136|=|0.8|0.6|
		//molar|molar_tooth|tooth|permanent_tooth, head_and_neck_part, tooth, body_part|upper_jaw_molar, lower_jaw_molar|upper_jaw_molar, lower_jaw_molar|molar|molar_tooth|||false|false|false
		ReadFile reader = new ReadFile(path_mappings2assess);
		String line;
		String[] elements;
		
		//Read first line with session info
		//-------------------------------
		line = reader.readLine();
		
		//0|ermesto@cs.ox.ac.uk|Ernesto|-1
		if (line.contains("|")){
			
			elements=line.split("\\|");
			
			numberFeedbackIterations = Integer.valueOf(elements[0]);
			email = elements[1];
			name = elements[2];	
			id_task = elements[3];
			reasoner = Integer.valueOf(elements[4]);
		}
		
		
		
		remainingMappings = 0;
		
		while ((line = reader.readLine()) != null){
			
			if (line.contains("|")){
			
				elements=line.split("\\|");
				
				if (!Boolean.valueOf(elements[15]) && !Boolean.valueOf(elements[16]))
					remainingMappings++;
				
				
				listOrderedMappings2Ask.add(new MappingObjectInteractivity_WebService(
						elements[0],//URU1
						elements[1],//URU2
						elements[2],//dirmapping
						Double.valueOf(elements[3]), //lexSim
						Double.valueOf(elements[4]), //semSim
						elements[5], //label1
						elements[6], //label2
						elements[7], //super1
						elements[8], //super2
						elements[9], //sub1
						elements[10], //sub2
						elements[11], //syn1
						elements[12], //syn1
						elements[13], //conflict
						elements[14], //amb
						Boolean.valueOf(elements[15]), //added?
						Boolean.valueOf(elements[16]), //discarded?
						Boolean.valueOf(elements[17]) //assessed?
						));
			}
				
			
			
			
		}
		reader.closeBuffer();	
	}
	
	
	
	
	
	
	
	/**
	 * No comparator. for no order of mappings to ask
	 * @author Ernesto
	 *
	 */
	private class NoComparator implements Comparator<MappingObjectInteractivity> {
		
		public int compare(MappingObjectInteractivity m1, MappingObjectInteractivity m2) {
			
			return -1;
			
		}
		
	}
	
	
	//Order by....
	
	/**
	 * Comparator
	 * @author Ernesto
	 *
	 */
	private class MappingInteractivityComparator implements Comparator<MappingObjectInteractivity> {
		
		
		/**

		 * @param m1
		 * @param m2
		 * @return
		 */
		public int compare(MappingObjectInteractivity m1, MappingObjectInteractivity m2) {

			return orderByScopeAndLex(m1, m2);
		}
		

		
		
		public int orderByScopeAndLex(MappingObjectInteractivity m1, MappingObjectInteractivity m2){
			if (m1.getScope()< m2.getScope()){
				return 1;						
			}	
			else if (m1.getScope()==m2.getScope()){
			
				if (m1.getLexSim()< m2.getLexSim()){
					return 1;						
				}
				else{
					return -1;
				}
				
				
			}
			else{
				return -1;
			}
		}
		
				
	}

}

	
	
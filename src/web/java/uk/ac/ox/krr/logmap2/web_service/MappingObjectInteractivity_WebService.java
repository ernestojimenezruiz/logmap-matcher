package uk.ac.ox.krr.logmap2.web_service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

import uk.ac.ox.krr.logmap2.utilities.Utilities;


public class MappingObjectInteractivity_WebService {
	
	//The identifier will be the position in the list!!
	//int identifier;
	
	private boolean removedFlag=false; //used in heuristics and interactivity
	private boolean addedFlag=false; //used in interactivity only
	//mapping for which the user provided feedback (no heuristics nor conflicts applied)
	private boolean assessedDirectlyFlag=false; 
	
	
	//Important to assess weakened mappings by DandG
	//Moreover the user may also decide to split the mapping
	private String dirMapping; 
	
	private double semSim = 0.0;
	private double lexSim = 0.0;
	
	//Necessary to detect conflicts and ambiguity
	int ide1;
	int ide2;
	
	
	private String URI1;
	private String URI2;
	
	private String ns1; //Will be empty is equal to ontology ns
	private String ns2;
	
	
	
	private String label1;
	private String label2;
	
	//Subset of
	/*private Set<String> synonyms1 = new HashSet<String>();
	private Set<String> synonyms2 = new HashSet<String>();
	
	//Subset of
	private Set<String> superClasses1 = new HashSet<String>();
	private Set<String> superClasses2 = new HashSet<String>();
	
	//Subset of
	private Set<String> subClasses1 = new HashSet<String>();
	private Set<String> subClasses2 = new HashSet<String>();*/
	
	private String synonyms1;
	private String synonyms2;
	
	//Subset of
	private String superClasses1;
	private String superClasses2;
	
	//Subset of
	private String subClasses1;
	private String subClasses2;
	
	
	
	
	
	
	//They will point to the "list" of mappings
	private Set<Integer> mappingsInConflict = new HashSet<Integer>();
	private Set<Integer> ambiguousMappings = new HashSet<Integer>();
	
	
	/**
	 * When creating the first time
	 * @param ide1
	 * @param ide2
	 * @param dirMapping
	 * @param lexSim
	 * @param semSim
	 */
	public MappingObjectInteractivity_WebService(
			int ide1,
			int ide2,
			String dirMapping,
			double lexSim,
			double semSim){
		
		this.ide1=ide1;
		this.ide2=ide2;
		
		this.lexSim=lexSim;
		this.semSim=semSim;
		
		this.dirMapping=dirMapping;
		
	}
	
	
	/**
	 * When strating and interactive session
	 * @param ide1
	 * @param ide2
	 * @param dirMapping
	 * @param lexSim
	 * @param semSim
	 */
	public MappingObjectInteractivity_WebService(
			String URI1,
			String URI2,
			String dirMapping,
			double lexSim,
			double semSim,
			String label1,
			String label2,
			String superclasses1,
			String superclasses2,
			String subclasses1,
			String subclasses2,
			String synonyms1,
			String synonyms2,
			String list_mappings_inconflict,
			String list_ambiguous_mappings,
			boolean isAdded,
			boolean isDiscarded,
			boolean isAssessed){
		
		
		//Not important since the index is not used in the interactive process
		//Everything has been already pre-calculated
		this.ide1=-1;
		this.ide2=-1;
		
		this.URI1 = URI1;
		this.URI2 = URI2;
		
		this.label1 = label1;
		this.label2 = label2;
		
		this.dirMapping=dirMapping;
	
		this.lexSim = lexSim;
		this.semSim = semSim;
		
		this.superClasses1 = superclasses1;
		this.superClasses2 = superclasses2;
		
		this.subClasses1 = subclasses1;
		this.subClasses2 = subclasses2;
		
		this.synonyms1 = synonyms1;
		this.synonyms2 = synonyms2;
		
		this.addedFlag = isAdded;
		this.removedFlag = isDiscarded;
		this.assessedDirectlyFlag = isAssessed;
		
		//Deserialise strings
		if (list_mappings_inconflict.length()>0){			
			mappingsInConflict.addAll(Utilities.deserializeListIntegers(list_mappings_inconflict));			
		}
		
		if (list_ambiguous_mappings.length()>0){			
			ambiguousMappings.addAll(Utilities.deserializeListIntegers(list_ambiguous_mappings));			
		}
				
		
	}
	
	

	
	public int getIde1(){
		return ide1;
	}
	
	public int getIde2(){
		return ide2;
	}
	
	public String getLabel1(){
		return label1;
	}
	
	public String getLabel2(){
		return label2;
	}
	
	public String getURI1(){
		return URI1;
	}
	
	public String getURI2(){
		return URI2;
	}
	
	public void setLabel1(String label){
		label1=label;
	}
	
	public void setLabel2(String label){
		label2=label;
	}
	
	public void setURI1(String uri){
		URI1=uri;
	}
	
	public void setURI2(String uri){
		URI2=uri;
	}
	

	
	public double getScope(){
		return semSim;
	}
	
	public double getSemSim(){
		return semSim;
	}
	
	public double getLexSim(){
		return lexSim;
	}
	
	public double getConfidence(){
		return (lexSim+semSim)/2.0;
	}
	
	
	public String getDirMapping() {
		return dirMapping;
	}
	
	
	
	
	public boolean hasDecision() {
		return addedFlag || removedFlag;
	}
	
	public boolean isAssessedFlagActive() {
		return assessedDirectlyFlag;
	}
	
	public void setAssessedFlag(boolean assessedFlag) {
		this.assessedDirectlyFlag = assessedFlag;	
	}
	
	public boolean isRemovedFlagActive() {
		return removedFlag;
	}
	
	


	public void setRemovedFlag(boolean removedFlag) {
		this.removedFlag = removedFlag;
		if (removedFlag)
			this.addedFlag = !removedFlag;
	}


	public boolean isAddedFlagActive() {
		return addedFlag;
	}
	
	
	
	public void setAddedFlag(boolean addedFlag) {
		this.addedFlag = addedFlag;
		if (addedFlag)
			this.removedFlag = !addedFlag;
	}
	

	

	/**
	 * @param dirMapping the dirMapping to set
	 */
	public void setDirMapping(String dirMapping) {
		this.dirMapping = dirMapping;
	}
	
	
	
	
	
	
	public Set<Integer> getMappingsInconflict(){
		return mappingsInConflict;
	}
	
	public Set<Integer> getAmbiguousMappings(){
		return ambiguousMappings;
	}
	
	
	
	public String getSuperClasses1_Str(){
		return superClasses1;
	}
	
	
	public String getSuperClasses2_Str(){
		return superClasses2;
	}
	
	
	public String getSubClasses1_Str(){
		return subClasses1;
	}
	
	public String getSubClasses2_Str(){
		return subClasses2;
	}
	
	
	public String getSynonyms1_Str(){
		return synonyms1;
	}
	
	public String getSynonyms2_Str(){
		return synonyms2;
	}
	
	
	public void setSuperClasses1_Str(String str){
		superClasses1 = str;
	}
	
	
	public void setSuperClasses2_Str(String str){
		superClasses2 = str;
	}
	
	
	public void setSubClasses1_Str(String str){
		subClasses1 = str;
	}
	
	public void setSubClasses2_Str(String str){
		subClasses2 = str;
	}
	
	
	public void setSynonyms1_Str(String str){
		synonyms1 = str;
	}
	
	public void setSynonyms2_Str(String str){
		synonyms2 = str;
	}
	
	
	
	/*public Set<String> getSuperClasses1(){
		return superClasses1;
	}
	
	
	public Set<String> getSuperClasses2(){
		return superClasses2;
	}
	
	
	public Set<String> getSubClasses1(){
		return subClasses1;
	}
	
	public Set<String> getSubClasses2(){
		return subClasses2;
	}
	
	
	public Set<String> getSynonyms1(){
		return synonyms1;
	}
	
	public Set<String> getSynonyms2(){
		return synonyms2;
	}*/
	
	
	
	/*public void addSynonyms1(String syn1){
		synonyms1.add(syn1);
	}
	
	public void addSynonyms2(String syn2){
		synonyms2.add(syn2);
	}
	
	
	public void addSubClass1(String cls1){
		subClasses1.add(cls1);
	}
	
	public void addSubClass2(String cls2){
		subClasses2.add(cls2);
	}
	
	
	
	public void addSuperClass1(String cls1){
		superClasses1.add(cls1);
	}
	
	public void addSuperClass2(String cls2){
		superClasses2.add(cls2);
	}
	*/
	
	public void addConflictiveMapping(int mapping_id){
		mappingsInConflict.add(mapping_id);
	}
	
	public void addAmbiguousMapping(int mapping_id){
		ambiguousMappings.add(mapping_id);
	}
	
	
	
	
	

	
	
	
	/*public String getSuperClasses1_Str(){
		String string_classes="";
		
		for (String cls : superClasses1){
			if (string_classes.length()==0)
				string_classes = cls;			
			else
				string_classes = string_classes + ", " + cls; 
		}
		
		if (string_classes.length()==0){
			string_classes = "No superclasses";
		}
		

		return string_classes;
	}
	
	public String getSuperClasses2_Str(){
		String string_classes="";
		
		for (String cls : superClasses2){
			if (string_classes.length()==0)
				string_classes = cls;			
			else
				string_classes = string_classes + ", " + cls; 
		}
		
		if (string_classes.length()==0){
			string_classes = "No superclasses";
		}
		return string_classes;
		
	}
	
	
	
	public String getSubClasses1_Str(){
		String string_classes="";
		
		for (String cls : subClasses1){
			if (string_classes.length()==0)
				string_classes = cls;			
			else
				string_classes = string_classes + ", " + cls; 
		}
		if (string_classes.length()==0){
			string_classes = "No subclasses";
		}
		return string_classes;
	}
	
	public String getSubClasses2_Str(){
		String string_classes="";

		for (String cls : subClasses2){
			if (string_classes.length()==0)
				string_classes = cls;			
			else
				string_classes = string_classes + ", " + cls; 
		}
		if (string_classes.length()==0){
			string_classes = "No subclasses";
		}
		return string_classes;
	}*/
	
	


	

	
	
}

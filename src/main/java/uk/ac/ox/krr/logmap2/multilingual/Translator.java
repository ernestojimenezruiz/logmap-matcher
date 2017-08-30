package uk.ac.ox.krr.logmap2.multilingual;

import java.io.File;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.ictclas4j.bean.SegResult;
import org.ictclas4j.segment.SegTag2;

import uk.ac.ox.krr.logmap2.Parameters;
import uk.ac.ox.krr.logmap2.io.LogOutput;
import uk.ac.ox.krr.logmap2.io.ReadFile;
import uk.ac.ox.krr.logmap2.io.WriteFile;
import uk.ac.ox.krr.logmap2.utilities.Utilities;

public abstract class Translator {
	
	
	protected int num_tranlated_characters=0;
	protected int num_calls=0;
	
	
	//We store translation to avoid (redundant) google translations calls
	//TODO We currently assume that there is only one language for each object of the GoogleBasedTranslator and MicrosoftBasedTranslator
	private HashMap<String,String> localTranslationMap = new HashMap<String,String>();
	private HashMap<String,String> ontheFlyTranslationMap = new HashMap<String,String>();

	
	
	
	public String getTranslation(String label, String originLang){
		return getTranslation(label, originLang, Parameters.target_lang);
	}
	
	
	
	public void reverseDictionary(String originLang, String targetLang){
		
		//Load
		Parameters.target_lang=targetLang; //"it"
		loadDictionary(originLang);//"en"
		
		
		//Reverse
		HashMap<String,String> tmp = new HashMap<String,String>();
		
		for (String key: ontheFlyTranslationMap.keySet()){
			tmp.put(ontheFlyTranslationMap.get(key), key);
		}
		
		ontheFlyTranslationMap.clear();
		ontheFlyTranslationMap.putAll(tmp);	
		
		//Store
		Parameters.target_lang=originLang; //"en"
		storeDictionary(targetLang); //"it"
		
	}
	
	
	
	public void loadDictionary(String language) {

		File file;
				
		if (Parameters.use_local_dict){
								
			//Load local dictionary
			file = new File(Parameters.path_multilingual_local + "/"+language+ "-" + Parameters.target_lang + "." + getTranslatorName());
			
			//System.out.println("Trying to access local dictionary: " + file.getAbsolutePath());
			
			try {
				loadDictionary(file, localTranslationMap);
				//System.out.println("\tNumber of entries local dictionary: " + localTranslationMap.size());
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				LogOutput.printError(e.getMessage());
				//System.out.println("\tError accessing local dictionary");
			}			
		}
		
		//Load on the fly dictionary if available		
		file = new File(Parameters.path_multilingual_tmp + "/"+language+ "-" + Parameters.target_lang + "." + getTranslatorName());
		
		//System.out.println("Trying to access on-the-fly dictionary: " + file.getAbsolutePath());
		
		try {
			loadDictionary(file, ontheFlyTranslationMap);
			//System.out.println("\t Number of entries on-the-fly dictionary: " + ontheFlyTranslationMap.size());
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			//LogOutput.printError(e.getMessage());
			LogOutput.printError("\tError accessing on-the-fly dictionary");
		}
		
		
	}


	public void storeDictionary(String language) {
		
		if (!ontheFlyTranslationMap.isEmpty()){
			
			//We store the on the fly dictionary
			File file = new File(Parameters.path_multilingual_tmp + "/"+language+ "-" + Parameters.target_lang + "." + getTranslatorName());
			
			storeDictionary(file, ontheFlyTranslationMap);
		}
		
	}
	
	
	
	public boolean isInLocalDictionary(String text) {		
		return localTranslationMap.containsKey(text) || ontheFlyTranslationMap.containsKey(text);
	}



	public String getLocalTranslation(String text) {		
		if (localTranslationMap.containsKey(text))
			return localTranslationMap.get(text);
		else 
			return ontheFlyTranslationMap.get(text);
	}
	
	
	public void addTranslation2Map(String text, String translatedText){
		ontheFlyTranslationMap.put(text, translatedText);
	}
	
		
	
	
	
		
	private void loadDictionary(File file, HashMap<String,String> translationMap) throws FileNotFoundException{
		
		//Loads local dictionary (if allowed) and on-the-fly dictionary if available
		
		
		ReadFile reader =  new ReadFile(file);
		
		String line;
		String[] elements;
		
		line=reader.readLine();		
		
		while (line!=null) {
			
			try {
				
				if (line.startsWith("#")){
					line=reader.readLine();
					continue;
				}
				
				if (line.indexOf("|")<0){					
					line=reader.readLine();
					continue;
				}
				
				elements=line.split("\\|");
				
				if (elements.length!=2){
					LogOutput.printError("Error reading local translations file. Line: " + line + ".\n");// + e.getMessage());
					//System.out.println("Error reading local translations file. Line: " + line + ".\n");// + e.getMessage());
					line=reader.readLine();
					continue;
				}
				
				
				//One translation per line. Currently only one translation per word or set of words
				translationMap.put(elements[0], elements[1]);
				
				line=reader.readLine();
				
			}
			catch (Exception e){
				LogOutput.printError("Error reading local translations file. Line: " + line + ".\n");// + e.getMessage());
				//System.out.println("Error reading local translations file. Line: " + line + ".\n");// + e.getMessage());
				line=reader.readLine(); //we keep reading...
				continue;
			}
			
		}
		
		reader.closeBuffer();
				
	
	}
	
	
	/**
	 * On the fly dictionary
	 * @param language
	 */
	private void storeDictionary(File file, HashMap<String,String> translationMap){
		
		WriteFile writer =  new WriteFile(file);//We do not append because we write the whole dictionary again
		
		writer.writeLine("#This file is initially empty and it will be populated with 'on-the-fly' translations obtained from google/microsoft translate.");
		
		for (String key : translationMap.keySet()){
			//One translation per line. Currently only one translation per word or set of words
			writer.writeLine(key+"|"+translationMap.get(key));
		}
		
		writer.closeBuffer();
		
	}
	
	
	public int getNumberOfTranslatedCharacters(){
		return num_tranlated_characters;
	}
	
	public int getNumberOfTranslateCalls(){
		return num_calls;
	}
	
	
	
	public abstract String getTranslatorName();
	
	
	public abstract String getToolAPITranslation(String label, String originLang, String targetLang) throws Exception;
	
	
	
	public String getTranslation(String label, String originLangStr, String targetLangStr) {
		
		//TODO We allow multiple translation, but we return a String trans1|trans2|...|transX
		
		ArrayList<String> split_label = new ArrayList<String>();
		
		String sentence_like_label;
		
		Set<String> translations = new HashSet<String>();
		
		try{
			
			String translated_label = getToolAPITranslation(label, originLangStr, targetLangStr);
			
			if (!translated_label.equals(label)) { 
				//Google is able to translate sentences, 
				//however it fails for composed words like ProgramCommitee or Program_Commitee. We need the to split into individual words
				translations.add(translated_label);
			}
			
			//Chinese labels
			if (originLangStr.equals("cn")){
				split_label = splitChineseLabel(label);
				
				translations.add(getTranslationWordByWord(split_label, originLangStr, targetLangStr));
				
			}
			//
			//Other languages
			else{
				
				split_label = splitRegularLabel(label);
				
				if (split_label.size()>0){
				
					//Translate word by word
					translations.add(getTranslationWordByWord(split_label, originLangStr, targetLangStr));
					
					//We try to create a sentence from concept with
					//char separation = " "
					sentence_like_label = getSentenceFromArray(split_label);
					if (!sentence_like_label.equals(label))
						translations.add(getToolAPITranslation(sentence_like_label, originLangStr, targetLangStr));
				}
				
			}
			
			//Transform to String
			return getStringFromTranslationsSet(translations);
			
		}
		catch(Exception e){
			//e.printStackTrace();
			LogOutput.printAlways("Error translating '"+ label + "'. " + e.getMessage());
			return label;
		}
	}
	
	
	private String getTranslationWordByWord(
			ArrayList<String> split_label, String originLangStr, String targetLangStr) throws Exception {

		String translation = "";
		
		for (String word : split_label){
			
			if (translation.length()==0)
				translation = getToolAPITranslation(word, originLangStr, targetLangStr);
			else
				translation += " " + getToolAPITranslation(word, originLangStr, targetLangStr);
			
		}
		
		
		return translation;
	}
	
	
	private String getSentenceFromArray(ArrayList<String> words){
		
		StringBuilder sentence = new StringBuilder();
	    for (int i = 0, il = words.size(); i < il; i++) {
	        if (i > 0)
	            sentence.append(" ");
	        sentence.append(words.get(i));
	    }
	    return sentence.toString();
		
	}
	
	
	private String getStringFromTranslationsSet(Set<String> translations){
		StringBuilder translation = new StringBuilder();
		
		for (String t : translations){
			if (translation.toString().length()>0)
				translation.append("|");
			
			translation.append(t);
		}
		
		return translation.toString();
		
	}



	/**
	 * Regular label in other languages than Chinese
	 * @param label_value
	 * @return
	 */
	private ArrayList<String> splitRegularLabel(String label_value){
		
		ArrayList<String> result = new ArrayList<String>();
		
		String[] words;
		
		label_value=label_value.replace(",", "");
		
		
		if (label_value.startsWith("_")){
			label_value = label_value.substring(1, label_value.length());
		}
		if (label_value.endsWith("_")){
			label_value = label_value.substring(0, label_value.length()-1);
		}
		
		
		if (label_value.indexOf("_")>0){ //e.g. in NCI and SNOMED
			words=label_value.split("_");
		}
		else if (label_value.indexOf(" ")>0){ //e.g. in FMA
			words=label_value.split(" ");
		}
		//Split capitals...
		else{
			words=Utilities.splitStringByCapitalLetter(label_value);
		}
		
		for (int index = 0; index < words.length; index++) {
			result.add(words[index]);
		}
		
		return result;
		
	}
	
	
	
	
	
	/**
	 * Split using the ictcla guidelines
	 * @param label
	 * @param targetLangStr
	 * @return
	 */
	private ArrayList<String> splitChineseLabel(String label) {
		
		ArrayList<String> result = new ArrayList<String>();
		
		try{
			
			label = label.replaceAll("_", "");
			
			// chinese， call ictclas
			SegTag2 st = new SegTag2(1);
			
			SegResult sr = st.split(label);
			String rawResult = sr.getFinalResult();
			
			// ictcla ​​results format and can not be used directly, need to be processed before adding into the string result
			String[] words = rawResult.split(" ");
			for (int index = 0; index < words.length; index++) {
				StringBuffer buffer = new StringBuffer(words[index]);
				String word = buffer.substring(0, buffer.indexOf("/"));
				result.add(word);
			}
		}
		catch (Exception e){
			LogOutput.printError("Error spliting label: " + label);
			result.clear();
			result.add(label);
		}
		
		return result;
		
		
	
	}
	
	
	
	

}

package uk.ac.ox.krr.logmap2.multilingual;

import java.io.FileNotFoundException;
import java.util.Vector;

import uk.ac.ox.krr.logmap2.Parameters;


public class TranslatorManager {

	public static final int GOOGLE_BASED_TRANSLATOR=0;
	public static final int MICROSOFT_BASED_TRANSLATOR=1;
	public static final int ALL_TRANSLATORS=9;
	
	public static int CURRENT_TRANSLATOR=GOOGLE_BASED_TRANSLATOR;
	
	private Vector<Translator> translators = new Vector<Translator>();
	
	
	public TranslatorManager(){
		this(ALL_TRANSLATORS);
	}
	
	public TranslatorManager(int translator_id){
		switch (translator_id){
			case GOOGLE_BASED_TRANSLATOR:
				translators.add(new GoogleBasedTranslator());
				break;
			case MICROSOFT_BASED_TRANSLATOR:
				translators.add(new MicrosoftBasedTranslator());
				break;
			//Add other cases
		  	default:
		  		translators.add(new GoogleBasedTranslator());
		  		translators.add(new MicrosoftBasedTranslator());
		}
	}
	
	
	
	public void loadDictionary(String language) throws FileNotFoundException{
		for (Translator translator : translators){
			translator.loadDictionary(language);
		}
	}
	
	public void storeDictionary(String language){
		for (Translator translator : translators){
			translator.storeDictionary(language);
		}		
	}
	
	public void reverseDictionary(String originLang, String targetLang){
		for (Translator translator : translators){
			translator.reverseDictionary(originLang, targetLang);
		}		
	}
	
	
	
	public int getNumberOfTranslatedCharacters(){
		
		int translated_chars = 0;
		
		for (Translator translator : translators){
			translated_chars+=translator.getNumberOfTranslatedCharacters();
		}		
		
		return translated_chars;
		
	}
	
	public int getNumberOfTranslateCalls(){
		
		int num_calls = 0;
		
		for (Translator translator : translators){
			num_calls+=translator.getNumberOfTranslateCalls();
		}
		
		return num_calls;
		
		
	}
		
	
	
	public String getTranslation(String label, String originLang){
		
		StringBuilder translation = new StringBuilder();
		
		for (Translator translator : translators){
			//Each translator returns a list of translations as trans1|trans2|....|trans_n
			if (translation.toString().length()>0)
				translation.append("|");
		
			translation.append(translator.getTranslation(label, originLang, Parameters.target_lang));
		}
		
		return translation.toString();
		
	}
	
	
	
	
	
	
	
	
	public static Translator getTranslatorObject(int translator_id){
		  switch (translator_id){
			case GOOGLE_BASED_TRANSLATOR:
				return new GoogleBasedTranslator();
			case MICROSOFT_BASED_TRANSLATOR:
				return new MicrosoftBasedTranslator();
			//Add other cases
		  	default:
		  		return new GoogleBasedTranslator();
		}
		
	}
	
	public static Translator getCurrentTranslatorObject(){
		return getTranslatorObject(CURRENT_TRANSLATOR);
		 		
	}
	
	public static void setCurrentTranslator(int translator){
		CURRENT_TRANSLATOR = translator;
	}
	
	
	
	public static void main(String[] args){
		
		TranslatorManager manager = new TranslatorManager();
		
		manager.reverseDictionary("en", "it");
		
	}
	
	
	
	
	
	
	
}

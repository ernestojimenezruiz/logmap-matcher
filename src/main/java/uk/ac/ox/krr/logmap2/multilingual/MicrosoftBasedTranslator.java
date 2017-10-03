package uk.ac.ox.krr.logmap2.multilingual;

import java.util.HashMap;
import java.util.Map;

import uk.ac.ox.krr.logmap2.Parameters;
import uk.ac.ox.krr.logmap2.io.LogOutput;

import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;

public class MicrosoftBasedTranslator extends Translator{

	
	//This structure maps the language in rdf:label to language of Microsoft API translation 
	public final static Map<String, Language> LanguageMap = new HashMap<String, Language>() {
		{
		
			/*put("cn", Language.CHINESE_SIMPLIFIED);
			put("en", Language.ENGLISH);
			put("de", Language.GERMAN);
			put("es", Language.SPANISH);
			put("fr", Language.FRENCH);
			put("nl", Language.DUTCH);
			put("pt", Language.PORTUGUESE);
			put("ru", Language.RUSSIAN);
			put("cz", Language.CZECH);
			put("it", Language.ITALIAN);
			put("ar", Language.ARABIC);
			*/
			
			for (Language lang : Language.values()){
				if (lang.toString().length()==2) //we avoid zh-CHS and zh-CHT					
					put(lang.toString(), lang);
			}
			
			//There is also chinese traditional
			put("zh", Language.CHINESE_SIMPLIFIED);
			
			//To keep compatibility with owl OAEI codes
			put("cn", Language.CHINESE_SIMPLIFIED);
			put("cz", Language.CZECH);
			
			//for (String key : keySet()){
			//	System.out.println(key + "  " + get(key).name());
			//}
			
					
		}

	};
	

	@Override
	public String getToolAPITranslation(String label, String originLang,
			String targetLang) throws Exception {
		return getMicrosoftTranslation(label, originLang, targetLang);
	}
	
	
	private String getMicrosoftTranslation(String text, String originLangStr, String targetLangStr) throws Exception {
		
		if (text.equals("") || text.equals(" ")) //avoid empty text
			return "";
		
		//String translatedText;
		String translatedText=text;// + "_" + num_tranlated_characters;
		
		//Check if text is in local dictionary
		if (isInLocalDictionary(text))
			return getLocalTranslation(text);
		
		else if(Parameters.call_online_translator){		
		
			//Set credential
	        Translate.setClientId("LogMapMultilingualOntologyMatcher");
	        Translate.setClientSecret("YMWwAB7c3qRQ+QoBnH/dtZx8C9kFqQObhqZpmGGdE4E=");
	
	        
	        Language originLang = LanguageMap.get(originLangStr);
			Language targetLang = LanguageMap.get(targetLangStr);
			
			num_tranlated_characters+=text.length();
			num_calls++;
			
			LogOutput.printAlways("Translating: '" + text + "' from " + originLangStr + " to " + targetLangStr + " using Microsoft API. Num chars translated so far: " + num_tranlated_characters);
	        
	
			
			//Microsoft call
			if (!Parameters.is_test_mode_multilingual){
				try{
					translatedText = Translate.execute(text, originLang, targetLang);
				}
				catch (Exception e){
					LogOutput.printError("Error Translating: '" + text + "' from " + originLangStr + " to " + targetLangStr + " using Microsoft API. " + e.getMessage());
					return "";
				}
				//System.out.println(translatedText.length());
			}
			else{		//This is for test only!
				translatedText=text + "_" + num_tranlated_characters + "_m";
			}
			
			
		    //System.out.println(translatedText);
			
			//Store in on the fly dictionary
			addTranslation2Map(text, translatedText);
		}
		
	    return translatedText;

		
	}
	
	
	
	@Override
	public String getTranslatorName() {
		return "microsoft";
	}

	




	
	
	
	
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
	
		
		MicrosoftBasedTranslator translator = new MicrosoftBasedTranslator();
		
		System.out.println("'"+translator.getTranslation("MedicoCabecera", "es", "en")+"'");
		//System.out.println("'"+translator.getTranslation("MedicoCabecera", "es", "ar")+"'");
		
		for (Language l : Language.values()){
			if (l.toString().length()>1)
			System.out.println(l.toString().length() + "  " +l.toString().substring(0, 2) + "  " + l.name() + "  "  + l.toString() );
		}
		
		System.out.println(Language.ENGLISH.toString());
		System.out.println(Language.CHINESE_SIMPLIFIED.toString());
		System.out.println(Language.CHINESE_TRADITIONAL.toString());
		System.out.println(Language.CZECH);
		
			
	}




}

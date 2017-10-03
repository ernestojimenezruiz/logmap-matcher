package uk.ac.ox.krr.logmap2.multilingual;

import java.util.HashMap;
import java.util.Map;

import uk.ac.ox.krr.logmap2.Parameters;
import uk.ac.ox.krr.logmap2.io.LogOutput;

import com.google.api.GoogleAPI;
import com.google.api.GoogleAPIException;
import com.google.api.translate.Language;
import com.google.api.translate.Translate;

/**
 * Translator based on google translate. It only translates to English
 * @author yuan gong and ernesto
 *
 */
public class GoogleBasedTranslator extends Translator{
	

	//This structure maps the language in rdf:label to language of Google translation 
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
				put("ar", Language.ARABIC);*/
				
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
	
	
	
	
	public GoogleBasedTranslator(){
		super();
	}
	
	
	

	@Override
	public String getToolAPITranslation(String label, String originLang,
			String targetLang) throws Exception {
		return getGoogleTranslation(label, originLang, targetLang);
	}
	

	



	private String getGoogleTranslation(String text, String originLangStr, String targetLangStr) throws GoogleAPIException {
		
		if (text.equals("") || text.equals(" ")) //avoid empty text
			return "";
		
		//String translatedText;
		String translatedText=text;// + "_" + num_tranlated_characters;
		
		//Check if text is in local dictionary
		if (isInLocalDictionary(text))
			return getLocalTranslation(text);
		
		else if(Parameters.call_online_translator){		
			// Set Google key here
			//GoogleAPI.setHttpReferrer("http://code.google.com/p/google-api-translate-java/");
			//GoogleAPI.setKey("AIzaSyALd_XsjljQ0U0n8SB_3q6Iocc8kPeLsCo"); //api yuan gong
			GoogleAPI.setHttpReferrer("https://code.google.com/p/logmap-matcher/");
			//GoogleAPI.setKey("AIzaSyCXIH0M0Ya4WpnbHYIqNrRC4wXOqtszQuU"); //university of oxford api, max 100,000. Old key
			GoogleAPI.setKey("AIzaSyCOXm6fqYcqJtpFSrlMsgAy1VPkgNcrD2k"); //New key
			
			
			Language originLang = LanguageMap.get(originLangStr);
			Language targetLang = LanguageMap.get(targetLangStr);
			
			num_tranlated_characters+=text.length();
			num_calls++;
			
			LogOutput.printAlways("Translating: '" + text + "' from " + originLangStr + " to " + targetLangStr + " using Google API. Num chars translated so far: " + num_tranlated_characters);
			
			//Google call
			if (!Parameters.is_test_mode_multilingual){
				try{
					translatedText = Translate.DEFAULT.execute(text, originLang, targetLang);
				}
				catch (Exception e){
					LogOutput.printError("Error Translating: '" + text + "' from " + originLangStr + " to " + targetLangStr + " using Google API. " + e.getMessage());
					e.printStackTrace();
					return "";
				}
				//System.out.println(translatedText.length());
			}
			else{ //This is for test only!
				translatedText=text + "_" + num_tranlated_characters + "_g";
			}
			
			
		    //System.out.println(translatedText);
			
			//Store in on-the-fly dictionary
			addTranslation2Map(text, translatedText);		
		}
		
	    return translatedText;

	}
	
	

	
	
	@Override
	public String getTranslatorName() {
		return "google";
	}


	
	
	
	
	
	
	public static void main(String[] args){
	
		GoogleBasedTranslator translator = new GoogleBasedTranslator();
		
		System.out.println("'"+translator.getTranslation("MedicoCabecera", "es", "ar")+"'");
		//System.out.println("'"+translator.getTranslation("coche", "es", "en")+"'");
		
		
		
		//System.out.println(Language.ENGLISH.toString());
		
	
	}





	

	



}

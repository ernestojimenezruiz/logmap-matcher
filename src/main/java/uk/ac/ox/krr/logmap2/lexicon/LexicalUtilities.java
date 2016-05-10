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
package uk.ac.ox.krr.logmap2.lexicon;

import java.io.InputStream;

import java.util.*;
import java.util.zip.GZIPInputStream;

import uk.ac.ox.krr.logmap2.lexicon.stemming.StemmerManager;
import uk.ac.ox.krr.logmap2.io.ReadFile;
import uk.ac.ox.krr.logmap2.io.LogOutput;



/**
 * Manages the lexical variants provided by WordNet, UMLSLexicon, Stemmers and number normalization
 * 
 *
 * @author Ernesto Jimenez-Ruiz
 * Created: Sep 11, 2011
 *
 */
public class LexicalUtilities {
	
	
	private ReadFile reader;
	private InputStream in;
	
	private String line;
	
	private String[] elements;
	
	
	private Map<String, Set<String>> spelling_variants_map = new HashMap<String, Set<String>>();
	private Map<String, Set<String>> normalization_map = new HashMap<String, Set<String>>();
	
	
	private final String lex_spl_file="Lex_spelling_LRSPL.gz";
	private final String lex_norm_file="Lex_norm_LRNOM.gz";
	private final String lex_plur_file="Lex_plurals_LRAGR.gz";
	
	
	private Set<String> stopwordsSet = new HashSet<String>();
	private Set<String> stopwordsSetExtended = new HashSet<String>();

	
	private Map<String, Set<String>> label2wordnetsyn =  new HashMap<String, Set<String>>();
	
	
	private Map<String, String> word2stemming =  new HashMap<String, String>();
	
	
	private boolean useStemming=false;
	
	
	public void clearStructures(){
		//stopwordsSet.clear();
		stopwordsSetExtended.clear();
		label2wordnetsyn.clear();
		word2stemming.clear();
		spelling_variants_map.clear();
		normalization_map.clear();
		
	}
	
	public void clearStopWordsSet()
	{
		stopwordsSet.clear();
	}
	
	public Set<String> getStopwordsSet(){
		return stopwordsSet;	
	}
	
	public Set<String> getStopwordsSetExtended(){
		return stopwordsSetExtended;
	}
	
	public Map<String, Set<String>> getLabel2wordnetsyn(){
		return label2wordnetsyn;
	}
	
	
	
	
	
	
	
	public void loadWordNetSynonyms(String wordnet_syn_file) throws Exception
	{
		ReadFile reader = new ReadFile(wordnet_syn_file);
		
		//String line;
		
		//String[] elements;
		String[] syn;
			
		Set<String> setsyn = new HashSet<String>();
		//Set<String> setsyn_ext = new HashSet<String>();
		
		
		while ((line = reader.readLine()) != null){
			
			//System.out.println(line);
			
			if (line.contains("|")){
				
				elements=line.split("\\|");
				
				if (elements.length>1){
					
					if (elements[1].contains(":")){
						syn = elements[1].split(":");
					}
					else {
						syn= new String[1];
						syn[0]=elements[1];
					}
					
					for (int i=0; i<syn.length; i++){
						if (!elements[0].equals(syn[i].toLowerCase())){
							/*if (syn[i].toLowerCase().matches("[^a-z]+")){
								setsyn_ext.add(syn[i].toLowerCase());
							}*/
							if (syn[i].toLowerCase().matches("[a-z_]+")){
								setsyn.add(syn[i].toLowerCase());
							}
							/*else{
								if (syn[i].toLowerCase().matches("[a-z_]+")){
									setsyn_ext.add(syn[i].toLowerCase());
								}
							}*/
						}
					}
					
					if (setsyn.size()>0){// || setsyn_ext.size()>0){
						label2wordnetsyn.put(elements[0], new HashSet<String>());
						//if (setsyn.size()<6){ //
							label2wordnetsyn.get(elements[0]).addAll(setsyn);
						//}
						
						/*if (label2wordnetsyn.get(elements[0]).size()<4 && setsyn.size()<3){ //we try to avoid rare synonyms in entries with several candidates
							label2wordnetsyn.get(elements[0]).addAll(setsyn_ext);
						}*/
						//System.out.println(label2wordnetsyn);
						setsyn.clear();
						//setsyn_ext.clear();
					}
				}
				
			}
			
			
		}
		reader.closeBuffer();
		LogOutput.print("Size syn: " + label2wordnetsyn.size());
		
	}
		
	
	
	

	public void setStemming(boolean use_stemming){
		useStemming=use_stemming;
	}
	
	public boolean isStemmingUp(){
		return useStemming;
	}
	
	public void setStemmer(){
		
		//Best results paice
		StemmerManager.setStemmerType(StemmerManager.STEMMER_PAICE);
		
		
		//StemmerManager.setStemmerType(StemmerManager.STEMMER_PORTER);
		//StemmerManager.setStemmerType(StemmerManager.STEMMER_LOVINS);
		//StemmerManager.setStemmerType(StemmerManager.STEMMER_LOVINSITER);
		//Config.setStemmerType(Config.STEMMER_LOVINS);
		//Config.setStemmerType(Config.STEMMER_LOVINSITER);
		//Config.setStemmerType(Config.STEMMER_PAICE);
		//STEMMER_PORTER
		//STEMMER_PORTER2
		//STEMMER_LOVINS
		//STEMMER_LOVINSITER 
		//STEMMER_PAICE
		
	}

	
	
	public String getStemming4Word(String str){
		
		if (word2stemming.containsKey(str)){
			return word2stemming.get(str);
		}
		
		String stemmed_word = StemmerManager.getStemmer().stem(str);
		
		word2stemming.put(str, stemmed_word);
		
		
		return stemmed_word;

		
	}
	
	
	public void loadStopWords() throws Exception{
		
		reader = new ReadFile(LexicalUtilities.class.getResourceAsStream("stopwords.txt"));
		
		while ((line = reader.readLine()) != null){
			if (!line.startsWith("#"))
				stopwordsSet.add(line);
		}
		reader.closeBuffer();
		
	}

	public void loadStopWordsExtended() throws Exception{
		
		reader = new ReadFile(LexicalUtilities.class.getResourceAsStream("stopwords.txt"));
		
		
		while ((line = reader.readLine()) != null){
			if (!line.startsWith("#"))
				stopwordsSetExtended.add(line);
			else
				stopwordsSetExtended.add(line.substring(1));
		}
		reader.closeBuffer();
		
	}
	
	
	
	public boolean hasSpellingVariants(String str){
		return spelling_variants_map.containsKey(str);
	}
	
	public boolean hasNormalization(String str){
		return normalization_map.containsKey(str);
	}
	
	
	public Set<String> getSpellingVariants(String str){
		return spelling_variants_map.get(str);
	}
	
	public Set<String> getNormalization(String str){
		return normalization_map.get(str);
	}
	
	
	public String getRomanNormalization4Number(String word){
		return NormalizeNumbers.getRomanNormalization(word);
	}
	
	
	
	public void loadUMLSLexiconResources(){
		
		long init, fin;
		
		init=Calendar.getInstance().getTimeInMillis();
		
		try{
			load_UMLS_SpecialistLex_SpellingVariants();
			load_UMLS_SpecialistLex_Plurals();
			load_UMLS_SpecialistLex_Normalization();
		}
		catch (Exception e){
			System.err.println("Error loading UMLS lexicon sources: " + e.getMessage());
			e.printStackTrace();
		}
		
		fin = Calendar.getInstance().getTimeInMillis();
		LogOutput.print("Time loading UMLS lexicon sources (s): " + (float)((double)fin-(double)init)/1000.0);
		
	}
	
	
	private void load_UMLS_SpecialistLex_SpellingVariants() throws Exception{
		
		
		//----------------------------
		//Reading Spelling file
		//E0000934|Besnier Boeck disease|Besnier-Boeck disease|
		in = new GZIPInputStream(LexicalUtilities.class.getResourceAsStream(lex_spl_file));
		reader = new ReadFile(in);
		
		
		
		while ((line = reader.readLine()) != null){
			
			//LogOutput.print(line);
			
			if (line.contains("|")){
				
				elements=line.split("\\|");
				
				if (elements.length>2){
					
					if (!elements[1].toLowerCase().equals(elements[2].toLowerCase())){
						
						if (!spelling_variants_map.containsKey(elements[1].toLowerCase())){
							spelling_variants_map.put(elements[1].toLowerCase(), new HashSet<String>());
						}
						spelling_variants_map.get(elements[1].toLowerCase()).add(elements[2].toLowerCase());
						
					}
				}
			}
		}
		
		reader.closeBuffer();
		
		
		//LogOutput.print("Number of entries spelling variants map: " + spelling_variants_map.size());
		
	}
	
	
	
	private void load_UMLS_SpecialistLex_Plurals() throws Exception{
		
		
		//---------------------------
		//Reading Plurals file
		//E0010541|arthritides|noun|count(thr_plur)|arthritis|arthritis|
		in = new GZIPInputStream(LexicalUtilities.class.getResourceAsStream(lex_plur_file));
		reader = new ReadFile(in);
		
		while ((line = reader.readLine()) != null){
			
			//LogOutput.print(line);
			
			if (line.contains("|")){
				
				elements=line.split("\\|");
				
				if (elements.length>5){
					
					if (!normalization_map.containsKey(elements[1].toLowerCase())){
						normalization_map.put(elements[1].toLowerCase(), new HashSet<String>());
					}
					//[5] base form. [4] citation form
					normalization_map.get(elements[1].toLowerCase()).add(elements[5].toLowerCase());
				}
			}
		}
		
		
		reader.closeBuffer();
		
		//LogOutput.print("Number of entries plurals map: " + normalization_map.size());
		
		
	}
	
	
	private void load_UMLS_SpecialistLex_Normalization() throws Exception{
		
		
		//---------------------------
		//Reading Normalization file
		//E0338556|recumbency|noun|E0052316|recumbent|adj|
		in = new GZIPInputStream(LexicalUtilities.class.getResourceAsStream(lex_norm_file));
		reader = new ReadFile(in);
		
		while ((line = reader.readLine()) != null){
			
			//LogOutput.print(line);
			
			if (line.contains("|")){
				
				elements=line.split("\\|");
				
				if (elements.length>4){
					
					if (!normalization_map.containsKey(elements[1].toLowerCase())){
						normalization_map.put(elements[1].toLowerCase(), new HashSet<String>());
					}
					normalization_map.get(elements[1].toLowerCase()).add(elements[4].toLowerCase());
				}
			}
		}
		
		
		reader.closeBuffer();
		
		
		
	
		//LogOutput.print("Number of entries plurals+normalization map: " + normalization_map.size());
		
		
	}
	
	
	
}

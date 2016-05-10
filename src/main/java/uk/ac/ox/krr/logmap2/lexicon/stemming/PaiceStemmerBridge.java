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
package uk.ac.ox.krr.logmap2.lexicon.stemming;

/**
 * Bridge class for the Paice/Husk stemming algorithm.
 * 
 * Bridged interface and class: Stemmer, PaiceStemmer.
 * 
 * Note: The algorithm offers two variants: stripping prefixes
 * or leaving them. This two options are selected in the 
 * PaiceStemmerBridge class through the constructor.
 * The set of considered prefixes is:
 *  { "kilo", "micro", "milli", "intra", "ultra",
 *    "mega", "nano", "pico", "pseudo" }
 * (from the method stripPrefixes in PaiceStemmer)
 * 
 * @author Ant�n Morant
 */
public class PaiceStemmerBridge implements Stemmer {
	
	//Avoid absolute references
	//public static final String STEMRULES_FILE = "./src/uk/ac/ox/cs/stemming/stemrules.txt";
	public static final String STEMRULES_FILE = "stemrules.txt";
	
	private PaiceStemmer stemmer;
	
	public PaiceStemmerBridge(boolean stripPreffixes) {
		if(stripPreffixes)
			this.stemmer = new PaiceStemmer(STEMRULES_FILE, "/p");
		else
			this.stemmer = new PaiceStemmer(STEMRULES_FILE, "");
	}

	@Override
	public String stem(String word) {
		return stemmer.stripAffixes(word);
	}	
	
}

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
 * Bridge class for the Lovins stemming algorithm in its iterated version.
 * 
 * Bridged interface and class: Stemmer, IteratedLovinsStemmer.
 * 
 * Note: The iterated version applies the algorithm to its output until it does not change.
 * 
 * @author Ant�n Morant
 */
public class LovinsIterStemmerBridge implements Stemmer {
	private IteratedLovinsStemmer stemmer;
	
	public LovinsIterStemmerBridge() {
		this.stemmer = new IteratedLovinsStemmer();
	}

	@Override
	public String stem(String word) {		
		return stemmer.stem(word);
	}	
	
}

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


public class StemmerManager {

	private static Stemmer stemmer;
	
	public final static int STEMMER_PORTER = 0;
	public final static int STEMMER_PORTER2 = 1;
	public final static int STEMMER_LOVINS = 2;
	public final static int STEMMER_LOVINSITER = 3;
	public final static int STEMMER_PAICE = 4;
	
	private static int stemmerType;
	
	public static void setStemmerType(int aStemmerType) {
		stemmerType = aStemmerType;
		stemmer = null; //So that the old type Stemmer object is replaced by getStemmer()
	}
	
	public static Stemmer getStemmer() {
		if(stemmer==null) {
			switch(stemmerType) {
				case STEMMER_PORTER:
					stemmer = new PorterStemmerBridge();
					break;
				//case STEMMER_PORTER2:
				//	stemmer = new Porter2StemmerBridge();
				//	break;
				case STEMMER_LOVINS:
					stemmer = new LovinsStemmerBridge();
					break;
				case STEMMER_LOVINSITER:
					stemmer = new LovinsIterStemmerBridge();
					break;
				case STEMMER_PAICE:
					stemmer = new PaiceStemmerBridge(true);
					break;
				default:
					throw new IllegalStateException("Invalid value for Config.stemmerType");
			}
		}
		return stemmer;
	}
	
}

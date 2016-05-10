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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.Set;
import java.util.TreeSet;



/**
 * Input: file voc.txt, located in the src/stemming path and
 * containing one word per line.
 * 
 * Output: files stemmed_voc_i.txt (for i=0,1,...), located in
 * the project path and containing one stemmed word per line. 
 * 
 * @author Ant�n Morant
 */
public class StemmerTester {

	public static final String PATH = "./files/test/";
	public static final String FILE_NAME = "voc.txt";
	
	
	public static void main(String[] args) {
		//Include here new stemmers to be tested
		Stemmer[] stemmers = {
				new PorterStemmerBridge(),
				new LovinsStemmerBridge(),
				new LovinsIterStemmerBridge(),
				new PaiceStemmerBridge(false),
				new PaiceStemmerBridge(true)
				};
		PrintWriter[] outs = new PrintWriter[stemmers.length];
		PrintWriter diffOut; 
		
		try {
			
			//Open input file
			BufferedReader in = new BufferedReader(new FileReader(PATH + FILE_NAME));
			//Open output files
			for(int i=0; i<outs.length; i++) {
				outs[i] = new PrintWriter(PATH + "stemmed_voc_" + i + ".txt");
			}
			//Open output file to log words with different stemming results
			diffOut = new PrintWriter(PATH + "stemming_diff.txt");
			
			//Read each word (1 line = 1 word)
			while(in.ready()) {
				String line = in.readLine();
				String[] stemmedVersions = new String[stemmers.length];
				Set<String> stemmedVersionsSet = new TreeSet<String>();
				
				//Stem the word with each stemmer, and print result to corresponding file.
				for(int i=0; i<stemmers.length; i++) {
					stemmedVersions[i] = stemmers[i].stem(line);
					stemmedVersionsSet.add(stemmedVersions[i]);
					outs[i].println(stemmedVersions[i]);
				}
				
				//If there are different versions of the stemmed word, log
				if(stemmedVersionsSet.size() > 1) {
					diffOut.print(line + "\t|");
					for(String version : stemmedVersions) {
						diffOut.print(" " + version);
					}
					diffOut.println();
				}
			}
			
			//Close output files
			for(int i=0; i<outs.length; i++) {
				outs[i].close();
			}
			diffOut.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}

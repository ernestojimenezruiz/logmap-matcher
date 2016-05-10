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

import java.io.IOException;

public class StemmingLauncher {
	
	public final static String PATH = "./files/";

	public static void main(String[] args) {
		
		/* 
		 * Enter here input files with words to be stemmed.
		 * File format: One word per line.
		 * 
		 * Note: Extension is assumed to be ".txt", otherwise output file name
		 *       must be changed later in this method.
		 */
		String[] inputFiles = new String[] {
			"FMA2.0_bag_of_words.txt",
			"NCI08.05d_bag_of_words.txt",
			//"SNMD20090131_bag_of_words.txt",
			//"nci_anatomy_2010_bag_of_words.txt",
			//"mouse_anatomy_2010_bag_of_words.txt"
		};

		
		/*
		 * Change here Stemming algorithm to be used if desired
		 * by passing a different object to the FileStemmer constructor.
		 */
						
		FileStemmer fs = new FileStemmer(new PaiceStemmerBridge(true));
		//FileStemmer fs = new FileStemmer(new LovinsIterStemmerBridge());
		
		for(int i=0; i<inputFiles.length; i++) {
			System.out.println("Stemming file: " + inputFiles[i]);
			String outputFile = inputFiles[i].replace(".txt", "_stemmed.txt");
			try {
				fs.stemFromFile(PATH + inputFiles[i], PATH + outputFile);
			} catch (IOException e) {
				e.printStackTrace();
			}	
		}
	}
}

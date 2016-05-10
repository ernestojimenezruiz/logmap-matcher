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
import java.io.IOException;
import java.io.PrintStream;

public class FileStemmer {
	
	public final static String SEPARATOR_CHAR = "|";

	private Stemmer stemmer;
	
	public FileStemmer(Stemmer stemmer) {
		this.stemmer = stemmer;
	}
	
	public void stemFromFile(String inputFile, String outputFile) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(inputFile));
		PrintStream out = new PrintStream(outputFile);
		
		while(in.ready()) {
			String line = in.readLine(); //each line must contain one word (exactly)
			out.println(line + SEPARATOR_CHAR + stemmer.stem(line));
		}
		
		in.close();
		out.close();
	}
	
}

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
import java.util.HashMap;
import java.util.Map;

public class StemmingMapLoader {
	
	private Map<String, String> stemmingMap;
	
	public Map<String, String> getStemmingMap() {
		return stemmingMap;
	}
	
	public void loadStemmingMap(String file) throws IOException {
		stemmingMap = new HashMap<String, String>();
		BufferedReader in = new BufferedReader(new FileReader(file));
		
		while(in.ready()) {
			String line = in.readLine();
			String[] parts = line.split("\\" + FileStemmer.SEPARATOR_CHAR);
			if(parts.length == 2) {
				stemmingMap.put(parts[0], parts[1]);
			} else {
				//Ignore line silently, or include next lines to report error:
				//System.err.println("StemmingMapLoader: Wrong input line format in file " + file);
				//System.err.println("Line will be ignored:");
				//System.err.println(line);
			}
		}
		
		in.close();
	}
	
}

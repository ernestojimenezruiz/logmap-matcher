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
package uk.ac.ox.krr.logmap2.oaei.reader;

import java.util.Set;

import uk.ac.ox.krr.logmap2.mappings.objects.MappingObjectStr;



/**
 * Manages the reading of the input mappings in TXT, RDF or OWL
 * @author Ernesto
 *
 */
public class MappingsReaderManager {

	
	public static final String OWLFormat="OWL";
	public static final String OAEIFormat="RDF";
	public static final String FlatFormat="TXT";

	MappingsReader mappingsReader;
		
	public MappingsReaderManager(String file_mappings, String format){
		
		try{
			
			if (format.equals(OWLFormat)){
				mappingsReader = new OWLAlignmentReader(file_mappings);
			}
			else if (format.equals(OAEIFormat)){
				mappingsReader = new RDFAlignReader(file_mappings);
			}
			else if (format.equals(FlatFormat)){
				mappingsReader = new FlatAlignmentReader(file_mappings);
			}
			else {
				System.err.println("The given format ('" + format + "') is not currently supported. Accepted: 'OWL', 'RDF' and 'TXT'");
				mappingsReader = new EmptyReader();
			}
		}
		catch (Exception e){
			System.err.println("Error reading mappings file: " + format + "\n" + e.getMessage());
			e.printStackTrace();
			mappingsReader = new EmptyReader();
		}
		
	}
	
	
	
	public Set<MappingObjectStr> getMappingObjects(){
		return mappingsReader.getMappingObjects();
	}
	
	public int getMappingObjectsSize(){
		return mappingsReader.getMappingObjectsSize();
	}
	
	
}

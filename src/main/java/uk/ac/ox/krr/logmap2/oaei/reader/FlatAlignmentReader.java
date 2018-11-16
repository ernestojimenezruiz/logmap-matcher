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


import uk.ac.ox.krr.logmap2.io.LogOutput;
import uk.ac.ox.krr.logmap2.io.OAEIRDFAlignmentFormat;
import uk.ac.ox.krr.logmap2.io.ReadFile;
import uk.ac.ox.krr.logmap2.mappings.objects.MappingObjectStr;
import uk.ac.ox.krr.logmap2.utilities.Utilities;

/**
 * 
 * Reads mappings of the form: iri1|iri2|relation|confidence|type 
 * 
 * 
 * @author Ernesto Jimenez Ruiz
 *
 */
public class FlatAlignmentReader extends MappingsReader {
	
	
	public FlatAlignmentReader(String text_alignment_file) throws Exception{
		
		ReadFile reader = new ReadFile(text_alignment_file);
		
		String line;
		String[] elements;
		
		line=reader.readLine();
		
		int dir;
		
		int type;
		
		while (line!=null) {
			
			try {
				if (line.startsWith("#") || (line.indexOf("|")<0 && line.indexOf(",")<0 && line.indexOf("\t")<0)){
					line=reader.readLine();
					continue;
				}
				
				if (line.indexOf("|")>0)
					elements=line.split("\\|");
				else if (line.indexOf(",")>0)
					elements=line.split("\\,");
				else
					elements=line.split("\\t");
				
				/*if (elements.length<4){
					line=reader.readLine();
					continue;
				}*/
					
				
				if (elements.length==2) {
					//System.out.println(elements[0] + "--" + elements[1]);
					mappings.add(new MappingObjectStr(elements[0], elements[1], 1.0, Utilities.EQ));
				}
				else {
					if (elements[2].equals(">")){
						dir = Utilities.R2L;
					}
					else if (elements[2].equals("<")){
						dir = Utilities.L2R;
					}
					else {
						dir = Utilities.EQ;
					}				
					
					if (elements.length==4){
						mappings.add(new MappingObjectStr(elements[0], elements[1], Double.valueOf(elements[3]), dir));
					}
					else if (elements.length==5){
						
						if (elements[4].equals(Utilities.CLASSES_STR)){
							type = Utilities.CLASSES;
						}
						else if (elements[4].equals(Utilities.DATAPROPERTIES_STR)){
							type 	= Utilities.DATAPROPERTIES;
						}
						else if (elements[4].equals(Utilities.OBJECTPROPERTIES_STR)){
							type = Utilities.OBJECTPROPERTIES;
						}
						else if (elements[4].equals(Utilities.INSTANCES_STR)){
							type = Utilities.INSTANCES;
						}
						else {
							type = Utilities.UNKNOWN;
						}
						
						mappings.add(new MappingObjectStr(elements[0], elements[1], Double.valueOf(elements[3]), dir, type));
					}
				}
				
				
				
				line=reader.readLine();
				
			}
			catch (Exception e){
				System.err.println("Error reading TXT mappings file. Line: " + line + ".\n" + e.getMessage());
				line=reader.readLine(); //we keep reading...
			}
			
		}		
		
		reader.closeBuffer();
		
		LogOutput.print("Read TXT mapping objects: " + getMappingObjectsSize());
		
		
	}
	

	
	public static void main(String[] args) {
		
		//String mappings_path = "/usr/local/data/DataUMLS/UMLS_Onto_Versions/OAEI_datasets/Mappings_Tools_2012/";
		
		String mappings_path = "/home/ejimenez-ruiz/Documents/OAEI_2018/Tracks/KER-game-im/v2/";
		
		
		
		try{
			FlatAlignmentReader reader = new FlatAlignmentReader(mappings_path + "alignment.csv");
			
			
			OAEIRDFAlignmentFormat writer = new OAEIRDFAlignmentFormat(mappings_path + "alignment.rdf", "http://islab.di.unimi.it/content/gamesonto", "http://islab.di.unimi.it/content/gamesonto");
			
			for (MappingObjectStr mapping : reader.getMappingObjects()) {
				writer.addInstanceMapping2Output(mapping.getIRIStrEnt1(), mapping.getIRIStrEnt2(), mapping.getConfidence());
			}
			
			writer.saveOutputFile();
			
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	
}

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
package uk.ac.ox.krr.logmap2.io;

import uk.ac.ox.krr.logmap2.utilities.Utilities;
import uk.ac.ox.krr.logmap2.io.WriteFile;

/**
 * Used in the IM track
 * @author ernesto
 *
 */
public class FlatTSVAlignmentFormat extends OutputMappingsFormat {

	WriteFile writer;
	
	
	public FlatTSVAlignmentFormat(String output_file_str)  throws Exception{
		super(output_file_str);
		
		setOutput();
		
	}
	
	protected void setOutput() throws Exception {
	
		//System.out.println("lala");
		writer =  new WriteFile(output_file);
		

	}

	
	public void addClassMapping2Output(String iri_str1, String iri_str2, int dir_mapping, double conf) throws Exception {
		addMapping2Output(iri_str1, iri_str2, dir_mapping, conf, Utilities.CLASSES_STR);
	}
	
	public void addDataPropMapping2Output(String iri_str1, String iri_str2, int dir_mapping, double conf) throws Exception {
		addMapping2Output(iri_str1, iri_str2, dir_mapping, conf, Utilities.DATAPROPERTIES_STR);
	}
	
	public void addObjPropMapping2Output(String iri_str1, String iri_str2, int dir_mapping, double conf) throws Exception{
		addMapping2Output(iri_str1, iri_str2, dir_mapping, conf, Utilities.OBJECTPROPERTIES_STR);
	}
	
	public void addInstanceMapping2Output(String iri_str1, String iri_str2, double conf)  throws Exception {
		addMapping2Output(iri_str1, iri_str2, Utilities.EQ, conf, Utilities.INSTANCES_STR);
	}
	
	
	
	private void addMapping2Output(String iri_str1, String iri_str2, int dir_mapping, double conf, String typeMapping)  throws Exception{
		
		String line = iri_str1 + "\t" + iri_str2 + "\t" + getRoundConfidence(conf);
		
		writer.writeLine(line);

	}

	
	public void saveOutputFile() throws Exception {
		
		writer.closeBuffer();

	}

}

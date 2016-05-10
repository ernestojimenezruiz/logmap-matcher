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

public class FlatAlignmentFormat extends OutputMappingsFormat {

	WriteFile writer;
	
	
	public FlatAlignmentFormat(String output_file_str)  throws Exception{
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
	
	
	/**
	 * only for statistics
	 */
	public void addClassMapping2Output(String iri_str1, String iri_str2, int dir_mapping, double conf, String inGS) throws Exception {
		
		//double rounded_conf = (double)Math.round(conf*100.0)/100.0;
		
				String line = iri_str1 + "|" + iri_str2;
				
				if (dir_mapping==Utilities.EQ){
					line=line+"|=";
				}
				else if (dir_mapping==Utilities.L2R){
					line=line+"|<";
				}
				else{ //if (dir_mapping==Utilities.R2L){
					line=line+"|>";
				}
				
				line=line+"|"+getRoundConfidence(conf);
				
				line=line+"|"+inGS;
				
				writer.writeLine(line);
	}
	
	
	/**
	 * only for instance statistics
	 */
	public void addInstanceMapping2Output(String iri_str1, String iri_str2, 
			String rel_mapping, double lex_sim, double comp_factor, double scope) throws Exception {
		
		//double rounded_conf = (double)Math.round(conf*100.0)/100.0;
		
		String line = iri_str1 + "|" + iri_str2 + "|" + lex_sim + "|" + comp_factor + "|" + scope;
				
		writer.writeLine(line);
	
	}
	
	
	
	private void addMapping2Output(String iri_str1, String iri_str2, int dir_mapping, double conf, String typeMapping)  throws Exception{
		
		//double rounded_conf = (double)Math.round(conf*100.0)/100.0;
				
		String line = iri_str1 + "|" + iri_str2;
		
		if (dir_mapping==Utilities.EQ){
			line=line+"|=";
		}
		else if (dir_mapping==Utilities.L2R){
			line=line+"|<";
		}
		else{ //if (dir_mapping==Utilities.R2L){
			line=line+"|>";
		}
		
		line=line+"|"+getRoundConfidence(conf);
		
		line=line+"|"+typeMapping;
		
		writer.writeLine(line);

	}

	
	public void saveOutputFile() throws Exception {
		
		writer.closeBuffer();

	}

}

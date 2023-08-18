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

import java.util.Vector;

/**
 * Static Class: not recommended for LogMap web service 
 * Manages the output 
 * 
 * @author Ernesto
 *
 */
public class OutPutFilesManagerStatic {
	
	public static final int OWLFormat=0;
	public static final int OAEIFormat=1;
	public static final int FlatFormat=2;
	public static final int FlatTSVFormat=3;
	public static final int AllFormats=4;
	
	private static Vector<OutputMappingsFormat> file_formats = new Vector<OutputMappingsFormat>(); 
	
	
	public static void createOutFiles(String output_file, int file_type, String oiri1, String oiri2) throws Exception{
		
		file_formats.clear();
		
		if (file_type==OWLFormat){
			if (output_file.startsWith("/"))
				file_formats.add(new OWLAlignmentFormat("file:"+output_file+".owl"));
			else
				file_formats.add(new OWLAlignmentFormat("file:/"+output_file+".owl"));
		}
		else if (file_type==OAEIFormat){
			file_formats.add(new OAEIRDFAlignmentFormat(output_file+".rdf", oiri1, oiri2));
		}
		else if (file_type==FlatFormat){
			file_formats.add(new FlatAlignmentFormat(output_file+".txt"));
		}
		else if (file_type==FlatTSVFormat){
			file_formats.add(new FlatAlignmentFormat(output_file+".tsv"));
		}
		else { //if (file_type==AllFormat){
			if (output_file.startsWith("/"))
				file_formats.add(new OWLAlignmentFormat("file:"+output_file+".owl"));
			else
				file_formats.add(new OWLAlignmentFormat("file:/"+output_file+".owl"));
			file_formats.add(new OAEIRDFAlignmentFormat(output_file+".rdf", oiri1, oiri2));
			file_formats.add(new FlatAlignmentFormat(output_file+".txt"));
			file_formats.add(new FlatTSVAlignmentFormat(output_file+".tsv"));

		}
		
		//Already in constructor
		//for (int i=0; i<file_formats.size(); i++){
		//	file_formats.get(i).setOutput();
		//}
		
	}
	
	
	
	public static void addClassMapping2Files(String iri_str1, String iri_str2, int dir_mapping, double conf) throws Exception{
		
		for (int i=0; i<file_formats.size(); i++){
			file_formats.get(i).addClassMapping2Output(iri_str1, iri_str2, dir_mapping, conf);
		}
		
	}
	
	public static void addDataPropMapping2Files(String iri_str1, String iri_str2, int dir_mapping, double conf) throws Exception{
		
		for (int i=0; i<file_formats.size(); i++){
			file_formats.get(i).addDataPropMapping2Output(iri_str1, iri_str2, dir_mapping, conf);
		}
		
	}

	public static void addObjPropMapping2Files(String iri_str1, String iri_str2, int dir_mapping, double conf) throws Exception{
	
		for (int i=0; i<file_formats.size(); i++){
			file_formats.get(i).addObjPropMapping2Output(iri_str1, iri_str2, dir_mapping, conf);
		}
	
	}
	
	
	public static void addInstanceMapping2Files(String iri_str1, String iri_str2, double conf) throws Exception{
		
		for (int i=0; i<file_formats.size(); i++){
			file_formats.get(i).addInstanceMapping2Output(iri_str1, iri_str2, conf);
		}
	
	}
	
	
	public static void closeAndSaveFiles() throws Exception{

		for (int i=0; i<file_formats.size(); i++){
			file_formats.get(i).saveOutputFile();
		}
	}
	
	
	

}

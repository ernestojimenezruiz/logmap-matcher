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

import java.util.Set;
import java.util.Vector;

import uk.ac.ox.krr.logmap2.mappings.objects.MappingObjectStr;

/**
 *  
 * Manages the output mapping file formats: OWL, TXT, RDF alignment  
 * 
 * @author Ernesto
 *
 */
public class OutPutFilesManager {
	
	public static final int OWLFormat=0;
	public static final int OAEIFormat=1;
	public static final int FlatFormat=2;
	public static final int FlatTSVFormat=3;
	public static final int AllFormats=4;
	
	private Vector<OutputMappingsFormat> file_formats = new Vector<OutputMappingsFormat>(); 
	
	
	public void createOutFiles(String output_file, int file_type, String oiri1, String oiri2) throws Exception{
		
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
	
	
	
	public void addMapping(MappingObjectStr mapping) throws Exception {
		
		if (mapping.isClassMapping()){
			addClassMapping2Files(mapping.getIRIStrEnt1(), mapping.getIRIStrEnt2(), mapping.getMappingDirection(), mapping.getConfidence());
		}
		else if(mapping.isObjectPropertyMapping()) {
			addObjPropMapping2Files(mapping.getIRIStrEnt1(), mapping.getIRIStrEnt2(), mapping.getMappingDirection(), mapping.getConfidence());
		}
		else if(mapping.isDataPropertyMapping()) {
			addDataPropMapping2Files(mapping.getIRIStrEnt1(), mapping.getIRIStrEnt2(), mapping.getMappingDirection(), mapping.getConfidence());
		}
		else if(mapping.isInstanceMapping()) {	
			addInstanceMapping2Files(mapping.getIRIStrEnt1(), mapping.getIRIStrEnt2(), mapping.getConfidence());
		}
		else {
			addClassMapping2Files(mapping.getIRIStrEnt1(), mapping.getIRIStrEnt2(), mapping.getMappingDirection(), mapping.getConfidence());
		}
		
	}
	
	
	public void addMappings(Set<MappingObjectStr> mappings) throws Exception {
		
		for (MappingObjectStr mapping : mappings) {
			addMapping(mapping);
		}
	}
	
	
	
	public void addClassMapping2Files(String iri_str1, String iri_str2, int dir_mapping, double conf) throws Exception{
		
		for (int i=0; i<file_formats.size(); i++){
			file_formats.get(i).addClassMapping2Output(iri_str1, iri_str2, dir_mapping, conf);
		}
		
	}
	
	public void addDataPropMapping2Files(String iri_str1, String iri_str2, int dir_mapping, double conf) throws Exception{
		
		for (int i=0; i<file_formats.size(); i++){
			file_formats.get(i).addDataPropMapping2Output(iri_str1, iri_str2, dir_mapping, conf);
		}
		
	}

	public void addObjPropMapping2Files(String iri_str1, String iri_str2, int dir_mapping, double conf) throws Exception{
	
		for (int i=0; i<file_formats.size(); i++){
			file_formats.get(i).addObjPropMapping2Output(iri_str1, iri_str2, dir_mapping, conf);
		}
	
	}
	
	
	public void addInstanceMapping2Files(String iri_str1, String iri_str2, double conf) throws Exception{
		
		for (int i=0; i<file_formats.size(); i++){
			file_formats.get(i).addInstanceMapping2Output(iri_str1, iri_str2, conf);
		}
	
	}
	
	
	public void closeAndSaveFiles() throws Exception{

		for (int i=0; i<file_formats.size(); i++){
			file_formats.get(i).saveOutputFile();
		}
	}
	
	
	

}

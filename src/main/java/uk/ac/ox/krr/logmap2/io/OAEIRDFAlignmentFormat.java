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
 * RDF Alignment format
 * 
 *
 * @author Ernesto Jimenez-Ruiz
 * Created: Sep 11, 2011
 *
 */
public class OAEIRDFAlignmentFormat extends OutputMappingsFormat {

	WriteFile writer;
	
	
	public OAEIRDFAlignmentFormat(String output_file_str, String oiri1, String oiri2) throws Exception{
		super(output_file_str);
		
		setOutput();
		
		printHeader(oiri1, oiri2);
		
	}
	
	
	protected void setOutput() throws Exception {
	
		writer =  new WriteFile(output_file);
		
		
	}

	
	private void printHeader(String oiri1, String oiri2){
		writer.writeLine("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		
		writer.writeLine("<rdf:RDF xmlns=\"http://knowledgeweb.semanticweb.org/heterogeneity/alignment\""); 
		writer.writeLine("\txmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\""); 
		writer.writeLine("\txmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\">");
		
		writer.writeLine("");
		
		writer.writeLine("<Alignment>");
		writer.writeLine("<xml>yes</xml>");
		writer.writeLine("<level>0</level>");
		writer.writeLine("<type>??</type>");

		writer.writeLine("<onto1>" + oiri1 +"</onto1>");
		writer.writeLine("<onto2>" + oiri2 +"</onto2>");
		writer.writeLine("<uri1>" + oiri1 +"</uri1>");
		writer.writeLine("<uri2>" + oiri2 +"</uri2>");
		
	}
	
	private void printTail(){
		
		writer.writeLine("</Alignment>");
		writer.writeLine("</rdf:RDF>");
		
	}
	
	
	public void addClassMapping2Output(String iri_str1, String iri_str2, int dir_mapping, double conf) throws Exception {
		addMapping2Output(iri_str1, iri_str2, dir_mapping, conf);
	}
	
	public void addDataPropMapping2Output(String iri_str1, String iri_str2, int dir_mapping, double conf) throws Exception {
		addMapping2Output(iri_str1, iri_str2, dir_mapping, conf);
	}
	
	public void addObjPropMapping2Output(String iri_str1, String iri_str2, int dir_mapping, double conf) throws Exception{
		addMapping2Output(iri_str1, iri_str2, dir_mapping, conf);
	}
	
	public void addInstanceMapping2Output(String iri_str1, String iri_str2, double conf)  throws Exception {
		addMapping2Output(iri_str1, iri_str2, Utilities.EQ, conf);
	}
	
	
	
	private void addMapping2Output(String iri_str1, String iri_str2, int dir_mapping, double conf) {
		
		writer.writeLine("<map>");
		writer.writeLine("\t<Cell>");
		
		writer.writeLine("\t\t<entity1 rdf:resource=\"" + iri_str1 +"\"/>");
		writer.writeLine("\t\t<entity2 rdf:resource=\"" + iri_str2 +"\"/>");
			
		writer.writeLine("\t\t<measure rdf:datatype=\"xsd:float\">" + getRoundConfidence(conf) + "</measure>");
		
		if (dir_mapping==Utilities.EQ){
			
			writer.writeLine("\t\t<relation>=</relation>");
			
		}
		else if (dir_mapping==Utilities.L2R){ //Subclass
			
			writer.writeLine("\t\t<relation>&lt;</relation>");
		}
		
		else{ //if (dir_mapping==Utilities.R2L){ //Superclass
			
			writer.writeLine("\t\t<relation>&gt;</relation>");
			
		}
		
		writer.writeLine("\t</Cell>");
		writer.writeLine("</map>");

	}

	
	public void saveOutputFile() throws Exception {
		
		printTail();
		
		writer.closeBuffer();

	}

}

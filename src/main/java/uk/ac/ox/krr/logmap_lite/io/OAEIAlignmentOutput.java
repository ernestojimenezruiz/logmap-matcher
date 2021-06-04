package uk.ac.ox.krr.logmap_lite.io;


import java.io.File;


import java.io.FileWriter;
import java.net.URL;

import uk.ac.ox.krr.logmap_lite.LogMap_Lite;

public class OAEIAlignmentOutput extends OutputMappingsFormat {
	
	
	File alignmentFile;
	FileWriter fw;
	
	/**
	 * Same format than OAEIRDFAlignmentFormat, but with different ouput.
	 * SEALS requires the creation of a temporal file and returning its URL
	 * @param name
	 */
	public OAEIAlignmentOutput(String name, String oiri1, String oiri2) throws Exception{
		super(name);
		
		setOutput();
		
		printHeader(oiri1, oiri2);
		
	}
	
	
	protected void setOutput() throws Exception {
	
		alignmentFile = File.createTempFile(output_file, ".rdf");
		fw = new FileWriter(alignmentFile);
		
		
	}

	
	private void printHeader(String oiri1, String oiri2) throws Exception{
		fw.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
		
		fw.write("<rdf:RDF xmlns=\"http://knowledgeweb.semanticweb.org/heterogeneity/alignment\"\n"); 
		fw.write("\txmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"); 
		fw.write("\txmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\">\n");
		
		fw.write("\n");
		
		fw.write("<Alignment>\n");
		fw.write("<xml>yes</xml>\n");
		fw.write("<level>0</level>\n");
		fw.write("<type>??</type>\n");

		fw.write("<onto1>" + oiri1 +"</onto1>\n");
		fw.write("<onto2>" + oiri2 +"</onto2>\n");
		fw.write("<uri1>" + oiri1 +"</uri1>\n");
		fw.write("<uri2>" + oiri2 +"</uri2>\n");
		
	}
	
	private void printTail() throws Exception{
		
		fw.write("</Alignment>\n");
		fw.write("</rdf:RDF>\n");
		
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
		addMapping2Output(iri_str1, iri_str2, LogMap_Lite.EQ, conf);
	}
	
	
	
	private void addMapping2Output(String iri_str1, String iri_str2, int dir_mapping, double conf) throws Exception{
		
		fw.write("<map>\n");
		fw.write("\t<Cell>\n");
		
		fw.write("\t\t<entity1 rdf:resource=\"" + iri_str1 +"\"/>\n");
		fw.write("\t\t<entity2 rdf:resource=\"" + iri_str2 +"\"/>\n");
			
		fw.write("\t\t<measure rdf:datatype=\"xsd:float\">" + getRoundConfidence(conf) + "</measure>\n");
		
		if (dir_mapping==LogMap_Lite.EQ){
			
			fw.write("\t\t<relation>=</relation>\n");
			
		}
		else if (dir_mapping==LogMap_Lite.L2R){ //Subclass
			
			fw.write("\t\t<relation>&lt;</relation>\n");
		}
		
		else{ //if (dir_mapping==LogMap_Lite.R2L){ //Superclass
			
			fw.write("\t\t<relation>&gt;</relation>\n");
			
		}
		
		fw.write("\t</Cell>\n");
		fw.write("</map>\n");

	}

	
	public void saveOutputFile() throws Exception{
		
		printTail();
		
		fw.flush();
		fw.close();
		
	}
	
	public URL returnAlignmentFile() throws Exception{
		return alignmentFile.toURI().toURL();
	}
}

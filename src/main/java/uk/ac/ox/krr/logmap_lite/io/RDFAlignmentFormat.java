package uk.ac.ox.krr.logmap_lite.io;


import uk.ac.ox.krr.logmap_lite.LogMap_Lite;

/**
 * RDF Alignment format
 * 
 *
 * @author Ernesto Jimenez-Ruiz
 * Created: Sep 11, 2011
 *
 */
public class RDFAlignmentFormat extends OutputMappingsFormat {

	WriteFile writer;
	
	
	public RDFAlignmentFormat(String output_file_str, String oiri1, String oiri2) throws Exception{
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
		addMapping2Output(iri_str1, iri_str2, LogMap_Lite.EQ, conf);
	}
	
	
	
	private void addMapping2Output(String iri_str1, String iri_str2, int dir_mapping, double conf) {
		
		writer.writeLine("<map>");
		writer.writeLine("\t<Cell>");
		
		writer.writeLine("\t\t<entity1 rdf:resource=\"" + iri_str1 +"\"/>");
		writer.writeLine("\t\t<entity2 rdf:resource=\"" + iri_str2 +"\"/>");
			
		writer.writeLine("\t\t<measure rdf:datatype=\"xsd:float\">" + getRoundConfidence(conf) + "</measure>");
		
		if (dir_mapping==LogMap_Lite.EQ){
			
			writer.writeLine("\t\t<relation>=</relation>");
			
		}
		else if (dir_mapping==LogMap_Lite.L2R){ //Subclass
			
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
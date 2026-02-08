package uk.ac.ox.krr.logmap2.io;

import uk.ac.ox.krr.logmap2.mappings.objects.MappingObjectStr;

public class TurtleAlignmentFormat extends OutputMappingsFormat {

	WriteFile writer;
	
	
	public TurtleAlignmentFormat(String output_file_str)  throws Exception{
		super(output_file_str);
		
		setOutput();
		
	}
	
	protected void setOutput() throws Exception {
	
		//System.out.println("lala");
		writer =  new WriteFile(output_file);
		

	}

	
	public void addClassMapping2Output(String iri_str1, String iri_str2, int dir_mapping, double conf) throws Exception {
			
		if (dir_mapping==MappingObjectStr.SUB)
			addMapping2Output(iri_str1, iri_str2, "<http://www.w3.org/2000/01/rdf-schema#subClassOf>");
		else if (dir_mapping==MappingObjectStr.SUP)
			addMapping2Output(iri_str2, iri_str1, "<http://www.w3.org/2000/01/rdf-schema#subClassOf>");
		else
			addMapping2Output(iri_str1, iri_str2, "<http://www.w3.org/2002/07/owl#equivalentClass>");
	}
	
	public void addDataPropMapping2Output(String iri_str1, String iri_str2, int dir_mapping, double conf) throws Exception {
				
		if (dir_mapping==MappingObjectStr.SUB)
			addMapping2Output(iri_str1, iri_str2, "<http://www.w3.org/2000/01/rdf-schema#subPropertyOf>");
		else if (dir_mapping==MappingObjectStr.SUP)
			addMapping2Output(iri_str2, iri_str1, "<http://www.w3.org/2000/01/rdf-schema#subPropertyOf>");
		else			
			addMapping2Output(iri_str1, iri_str2, "<http://www.w3.org/2002/07/owl#equivalentProperty>");
	}
	
	public void addObjPropMapping2Output(String iri_str1, String iri_str2, int dir_mapping, double conf) throws Exception{
		addMapping2Output(iri_str1, iri_str2, "<http://www.w3.org/2002/07/owl#equivalentProperty>");
	}
	
	public void addInstanceMapping2Output(String iri_str1, String iri_str2, double conf)  throws Exception {
		addMapping2Output(iri_str1, iri_str2, "<http://www.w3.org/2002/07/owl#sameAs>");
	}
	
	
	
	

	
	
	private void addMapping2Output(String iri_str1, String iri_str2, String predicate)  throws Exception{
		
		//double rounded_conf = (double)Math.round(conf*100.0)/100.0;
		
						
		String line = "<" + iri_str1 + "> "+ predicate + " <" + iri_str2 + "> .";
		
		
		writer.writeLine(line);

	}

	
	public void saveOutputFile() throws Exception {
		
		writer.closeBuffer();

	}
	
}
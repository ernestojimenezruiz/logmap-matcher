package uk.ac.ox.krr.logmap2.io;

import uk.ac.ox.krr.logmap2.utilities.Utilities;

import java.io.File;
import java.io.FileWriter;
import java.net.URL;


/**
 * Returns file as a temporal file via an URL
 * @author Ernesto
 *
 */
public class FlatAlignmentFormatURL extends OutputMappingsFormat {

	File alignmentFile;
	FileWriter fw;
	
	
	public FlatAlignmentFormatURL(String output_file_str)  throws Exception{
		super(output_file_str);
		
		setOutput();
		
	}
	
	protected void setOutput() throws Exception {
	
		alignmentFile = File.createTempFile(output_file, ".txt");
		fw = new FileWriter(alignmentFile);
				

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
				
				fw.write(line);
	}
	
	
	/**
	 * only for instance statistics
	 */
	public void addInstanceMapping2Output(String iri_str1, String iri_str2, 
			String rel_mapping, double lex_sim, double comp_factor, double scope) throws Exception {
		
		//double rounded_conf = (double)Math.round(conf*100.0)/100.0;
		
		String line = iri_str1 + "|" + iri_str2 + "|" + lex_sim + "|" + comp_factor + "|" + scope;
				
		fw.write(line);
	
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
		
		fw.write(line);

	}

	
	public void saveOutputFile() throws Exception {
		
		fw.flush();
		fw.close();

	}
	
	
	public URL returnFlatAlignmentFile() throws Exception{
		return alignmentFile.toURI().toURL();
	}

}

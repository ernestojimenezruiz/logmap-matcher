package uk.ac.ox.krr.logmap2.io;

import uk.ac.ox.krr.logmap2.utilities.Utilities;

public class ExtendedFlatTSVAlignmentFormat extends OutputMappingsFormat {

		WriteFile writer;
		
		
		public ExtendedFlatTSVAlignmentFormat(String output_file_str)  throws Exception{
			super(output_file_str);
			
			setOutput();
			
		}
		
		protected void setOutput() throws Exception {
		
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
		
		
		
		public void addClassMapping2Output(String iri_str1, String label1, String iri_str2, String label2,  int dir_mapping, double conf) throws Exception {
			addMapping2Output(iri_str1, label1, iri_str2, label2, dir_mapping, conf, Utilities.CLASSES_STR);
		}
		
		public void addDataPropMapping2Output(String iri_str1, String label1,  String iri_str2, String label2,  int dir_mapping, double conf) throws Exception {
			addMapping2Output(iri_str1, label1, iri_str2, label2, dir_mapping, conf, Utilities.DATAPROPERTIES_STR);
		}
		
		public void addObjPropMapping2Output(String iri_str1, String label1, String iri_str2, String label2,  int dir_mapping, double conf) throws Exception{
			addMapping2Output(iri_str1, label1, iri_str2, label2, dir_mapping, conf, Utilities.OBJECTPROPERTIES_STR);
		}
		
		public void addInstanceMapping2Output(String iri_str1, String label1,  String iri_str2, String label2, double conf)  throws Exception {
			addMapping2Output(iri_str1, label1, iri_str2, label2, Utilities.EQ, conf, Utilities.INSTANCES_STR);
		}
		
		
		
		private void addMapping2Output(String iri_str1, String iri_str2, int dir_mapping, double conf, String typeMapping)  throws Exception{
			
			String line = iri_str1 + "\t" + iri_str2;
			
			
			if (dir_mapping==Utilities.EQ){
				line=line+"\t=";
			}
			else if (dir_mapping==Utilities.L2R){
				line=line+"\t<";
			}
			else{ //if (dir_mapping==Utilities.R2L){
				line=line+"\t>";
			}
			
			line=line+"\t"+getRoundConfidence(conf);
			
			line=line+"\t"+typeMapping;
			
			
			
			
			writer.writeLine(line);

		}

		
		
		private void addMapping2Output(String iri_str1, String label1, String iri_str2, String label2, int dir_mapping, double conf, String typeMapping)  throws Exception{
			
			String line = iri_str1 + "\t" + label1 + "\t" + iri_str2 + "\t" + label2;
			
			
			if (dir_mapping==Utilities.EQ){
				line=line+"\t=";
			}
			else if (dir_mapping==Utilities.L2R){
				line=line+"\t<";
			}
			else{ //if (dir_mapping==Utilities.R2L){
				line=line+"\t>";
			}
			
			//line=line+"\t"+getRoundConfidence(conf);
			
			line=line+"\t"+typeMapping;
			
			
			
			
			writer.writeLine(line);

		}
		
		public void saveOutputFile() throws Exception {
			
			writer.closeBuffer();

		}


}

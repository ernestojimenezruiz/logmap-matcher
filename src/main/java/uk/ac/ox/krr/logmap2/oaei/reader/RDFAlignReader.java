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
package uk.ac.ox.krr.logmap2.oaei.reader;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

import uk.ac.ox.krr.logmap2.io.LogOutput;
import uk.ac.ox.krr.logmap2.mappings.objects.MappingObjectStr;
import uk.ac.ox.krr.logmap2.utilities.Utilities;


/**
 * This class transforms a RDF alignment (XML) file in a set of 
 * MappingObjectStr objects.
 * 
 * @author Ernesto
 *
 */
public class RDFAlignReader extends MappingsReader {

	public static final String CELL = "Cell";
	/*public static final String ENTITY1 = "entity1";
	public static final String ENTITY2 = "entity2";
	public static final String RELATION = "relation";
	public static final String MEASURE = "measure";*/
	
	//Used in old gold standards
	private static final String ALIGNMENTENTITY1="alignmententity1";
	private static final String ALIGNMENTENTITY2 ="alignmententity2";
		
	private static final String ALIGNMENTRELATION="alignmentrelation";
	private static final String ALIGNMENTMEASURE="alignmentmeasure";
	
	
	
	
	
	public RDFAlignReader(URL url_rdf_alignment_file) throws Exception {
		this(url_rdf_alignment_file.openStream());
		
	}
	
	public RDFAlignReader(String rdf_alignment_file) throws Exception {
		this(new FileInputStream(new File(rdf_alignment_file)));
	}
	
	
	public RDFAlignReader(InputStream is) throws Exception {
		
		//File xmlFile = new File(rdf_alignment_file);		
		//InputStream is = new FileInputStream(xmlFile);
		
		XMLInputFactory factory = XMLInputFactory.newInstance();
		XMLStreamReader reader = factory.createXMLStreamReader(is);
		
		mappings.clear();
		
		String iri_str1="";
		String iri_str2="";
		String relation="";
		double confidence=0.0;
		int dir_relation;
		
		//int next=0;
		while(reader.hasNext())
		{	
			if(reader.getEventType()==XMLStreamConstants.START_ELEMENT){

				//System.out.println(next++);
	
				if (reader.hasName()){
			    	
			    	 if (reader.getLocalName().equals(RDFAlignReader.CELL)){
			    			iri_str1="";
			    			iri_str2="";
			    			relation="";
			    			confidence=0.0;
			    	 }
			    	 else if (reader.getLocalName().equals(RDFAlignReader.ENTITY1) ||
			    			 reader.getLocalName().equals(RDFAlignReader.ALIGNMENTENTITY1)){
			    		 
			    		 if (reader.getAttributeCount()>0){
				    		//System.out.println("Att: " + reader.getAttributeValue(0));
			    			 iri_str1 = reader.getAttributeValue(0); 
			    		 }
			    		 
			    	 }
			    	 
			    	 else if (reader.getLocalName().equals(RDFAlignReader.ENTITY2) ||
			    			 reader.getLocalName().equals(RDFAlignReader.ALIGNMENTENTITY2)){
			    		 
			    		 if (reader.getAttributeCount()>0){
					    	//System.out.println("Att: " + reader.getAttributeValue(0));
				    		iri_str2 = reader.getAttributeValue(0); 	
			    		 }
			    		 
			    	 }
			    	 
			    	 else if (reader.getLocalName().equals(RDFAlignReader.RELATION) ||
			    			 reader.getLocalName().equals(RDFAlignReader.ALIGNMENTRELATION)){
			    		 
			    		 //System.out.println("TExt: " + reader.getElementText());
			    		 relation = reader.getElementText();
			    		 
			    	 }
			    	 
			    	 else if (reader.getLocalName().equals(RDFAlignReader.MEASURE) ||
			    			 reader.getLocalName().equals(RDFAlignReader.ALIGNMENTMEASURE)){
			    		 
			    		 //System.out.println("TExt: " + reader.getElementText());
			    		 confidence = Double.valueOf(reader.getElementText());
			    		 
			    	 }
			    	
		    	}
			    
			    
			}
			else if(reader.getEventType()==XMLStreamConstants.END_ELEMENT){
			
				 if (reader.hasName()){
					 
					 if (reader.getLocalName().equals(RDFAlignReader.CELL)){
						  
						  /*System.out.println(next++);
						  System.out.println(iri_str1);
						  System.out.println(iri_str2);
						  System.out.println(relation);
						  System.out.println(confidence);*/
						  
						  
						  if (relation.equals(">")){
							  dir_relation = Utilities.R2L;
							  //System.out.println("R2L");
						  }
						  else if (relation.equals("<")){
							  dir_relation = Utilities.L2R;
							  //System.out.println("L2R");
						  }
						  else if (relation.equals("?")){
							  dir_relation = Utilities.Flagged;
							  //System.out.println("L2R");
						  }
						  else { //Any other case: ie Hertuda/Hotmatch does not use "="
							  dir_relation = Utilities.EQ;
							  //System.out.println("=");
						  }
							
						  mappings.add(new MappingObjectStr(iri_str1, iri_str2, confidence, dir_relation));
						  
					  }
				 }
				 
				 //Add to object if everything is ok!
				
			}
			
		    
		    
		    reader.next();
		}//end while

		LogOutput.print("Read RDF Align mapping objects: " + getMappingObjectsSize());
		
	}
	
	
	

}

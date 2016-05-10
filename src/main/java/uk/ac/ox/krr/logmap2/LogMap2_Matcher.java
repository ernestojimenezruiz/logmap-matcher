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
package uk.ac.ox.krr.logmap2;

import java.util.Set;
import java.util.HashSet;

import java.util.List;
import java.util.ArrayList;


import uk.ac.ox.krr.logmap2.mappings.objects.MappingObjectStr;
import uk.ac.ox.krr.logmap2.utilities.Utilities;

import org.semanticweb.owlapi.model.OWLOntology;



public class LogMap2_Matcher {
	
	LogMap2Core logmap2;	
	Set<MappingObjectStr> logmap2_mappings = new HashSet<MappingObjectStr>();
	Set<String> representative_labels = new HashSet<String>();
	
	
	
	/**
	 * LogMap access from command line
	 * See uk.ac.ox.krr.logmap2.Parameters.java to adapt LogMap 2
	 * 
	 */
	public LogMap2_Matcher(String iri_onto1, String iri_onto2, String output_path, boolean eval_impact){
		
		//LogOutput.showOutpuLog(true);
		Parameters.readParameters();
		
		try {
			logmap2 = new LogMap2Core(iri_onto1, iri_onto2, output_path, eval_impact);
			createObjectMappings();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	

	/**
	 * LogMap access from java application
	 * See uk.ac.ox.krr.logmap2.Parameters.java to adapt LogMap 2
	 * @param iri_onto1 Strig with IRI ontology 1 (source)
	 * @param iri_onto2 Strig with IRI ontology 2 (target)
	 */
	public LogMap2_Matcher(String iri_onto1, String iri_onto2){
		
		//LogOutput.showOutpuLog(true);
		Parameters.readParameters();
		
		try {
			logmap2 = new LogMap2Core(iri_onto1, iri_onto2);
			createObjectMappings();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	/**
	 * LogMap access from java application
	 * See uk.ac.ox.krr.logmap2.Parameters.java to adapt LogMap 2
	 * @param iri_onto1 Strig with IRI ontology 1 (source)
	 * @param iri_onto2 Strig with IRI ontology 2 (target)
	 */
	public LogMap2_Matcher(OWLOntology onto1, OWLOntology onto2){
		
		//LogOutput.showOutpuLog(true);
		Parameters.readParameters();
		
		try {
			logmap2 = new LogMap2Core(onto1, onto2);
			createObjectMappings();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	/**
	 * LogMap access from java application
	 * See uk.ac.ox.krr.logmap2.Parameters.java to adapt LogMap 2
	 * @param iri_onto1 Strig with IRI ontology 1 (source)
	 * @param iri_onto2 Strig with IRI ontology 2 (target)
	 */
	public LogMap2_Matcher(OWLOntology onto1, OWLOntology onto2, boolean only_anchors){
		
		//LogOutput.showOutpuLog(true);
		Parameters.readParameters();
		
		try {
			logmap2 = new LogMap2Core(onto1, onto2, only_anchors);
			createObjectMappings();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	
	/**
	 * LogMap access from java application.
	 * Input mappings are accepted (e.g. composed mappings when using a MO)
	 * See uk.ac.ox.krr.logmap2.Parameters.java to adapt LogMap 2
	 * @param iri_onto1 Strig with IRI ontology 1 (source)
	 * @param iri_onto2 Strig with IRI ontology 2 (target)
	 */
	public LogMap2_Matcher(OWLOntology onto1, OWLOntology onto2, Set<MappingObjectStr> input_mappings){
		
		//LogOutput.showOutpuLog(true);
		Parameters.readParameters();
		
		try {
			logmap2 = new LogMap2Core(onto1, onto2, input_mappings);
			createObjectMappings();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	/**
	 * LogMap access from java application.
	 * Input mappings are accepted (e.g. composed mappings when using a MO or )
	 * See uk.ac.ox.krr.logmap2.Parameters.java to adapt LogMap 2
	 * If are_input_mappings_validated = true, then the input mappings are added to the anchor set as they are  
	 */
	public LogMap2_Matcher(OWLOntology onto1, OWLOntology onto2, Set<MappingObjectStr> input_mappings, boolean are_input_mappings_validated){
		
		//LogOutput.showOutpuLog(true);
		Parameters.readParameters();
		
		try {
			logmap2 = new LogMap2Core(onto1, onto2, input_mappings, are_input_mappings_validated);
			createObjectMappings();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	
	
	/**
	 * Returns the mappings extracted by LogMap
	 * @return
	 */
	public Set<MappingObjectStr> getLogmap2_Mappings(){
		return logmap2_mappings;
	}
	
	
	public Set<String> getRepresentativeLabelsForMappings(){
		return representative_labels;
	}
	
	
	public String getIRIOntology1(){
		return logmap2.getIRIOntology1();
	}
	
	public String getIRIOntology2(){
		return logmap2.getIRIOntology2();
	}

	public OWLOntology getOWLOntology4Mappings() throws Exception{
		return logmap2.getOWLOntology4Mappings();
	}
	
	private void createObjectMappings(){
		

		
		int dir_mapping;
		
		
		try {
			
			if (Parameters.output_class_mappings){
			
				for (int ide1 : logmap2.getClassMappings().keySet()){
					for (int ide2 : logmap2.getClassMappings().get(ide1)){
						
						dir_mapping = logmap2.getDirClassMapping(ide1, ide2);
						
						if (dir_mapping!=Utilities.NoMap){
							
							//We add labels from both ontos
							if (representative_labels.size()%2 == 0)
								representative_labels.add(logmap2.getLabel4ConceptIdentifier(ide2));
							else
								representative_labels.add(logmap2.getLabel4ConceptIdentifier(ide1));
							
							
							if (dir_mapping!=Utilities.R2L){						
							
								//GSs in OAIE only contains, in general, equivalence mappings
								if (Parameters.output_equivalences_only){
									dir_mapping=Utilities.EQ;
								}
									
								
								logmap2_mappings.add(
										new MappingObjectStr(
												logmap2.getIRI4ConceptIdentifier(ide1), 
												logmap2.getIRI4ConceptIdentifier(ide2), 
												logmap2.getConfidence4ConceptMapping(ide1, ide2), 
												dir_mapping,
												Utilities.CLASSES));
								
							}
							else{
								
								if (Parameters.output_equivalences_only){
									dir_mapping=Utilities.EQ;
								}
								
								logmap2_mappings.add(
										new MappingObjectStr(								
										logmap2.getIRI4ConceptIdentifier(ide2),
										logmap2.getIRI4ConceptIdentifier(ide1),										
										logmap2.getConfidence4ConceptMapping(ide1, ide2),
										dir_mapping,
										Utilities.CLASSES));
							}
						}
					} //for ide2
				}//for ide1
			}
			
			
			if (Parameters.output_prop_mappings){
			
				for (int ide1 : logmap2.getDataPropMappings().keySet()){		
					
					logmap2_mappings.add(
							new MappingObjectStr(			
								logmap2.getIRI4DataPropIdentifier(ide1),
								logmap2.getIRI4DataPropIdentifier(logmap2.getDataPropMappings().get(ide1)),
								logmap2.getConfidence4DataPropConceptMapping(ide1, logmap2.getDataPropMappings().get(ide1)),
								Utilities.EQ,  
								Utilities.DATAPROPERTIES));
				}
				
				for (int ide1 : logmap2.getObjectPropMappings().keySet()){
						
					logmap2_mappings.add(
							new MappingObjectStr(
								logmap2.getIRI4ObjectPropIdentifier(ide1),
								logmap2.getIRI4ObjectPropIdentifier(logmap2.getObjectPropMappings().get(ide1)),
								logmap2.getConfidence4ObjectPropConceptMapping(ide1, logmap2.getObjectPropMappings().get(ide1)),
								Utilities.EQ,
								Utilities.OBJECTPROPERTIES));
				}
			}
			

			//Output for individuals
			if (Parameters.perform_instance_matching && Parameters.output_instance_mappings){
				
				for (int ide1 : logmap2.getInstanceMappings().keySet()){
					for (int ide2 : logmap2.getInstanceMappings().get(ide1)){
					
						logmap2_mappings.add(
								new MappingObjectStr(
								logmap2.getIRI4InstanceIdentifier(ide1), 
								logmap2.getIRI4InstanceIdentifier(ide2), 
								logmap2.getConfidence4InstanceMapping(ide1, ide2),
								Utilities.EQ,
								Utilities.INSTANCES));
						
					}
				}
				
				
			}
			
			logmap2.clearIndexStructures();
			
		}
		catch (Exception e){
			System.err.println("Error creating object mappings");
			//e.printStackTrace();
		}
		
		
	}
	
	
	
	
	
	private static String getHelpMessage(){
		return "LogMap 2 requires four parameters:\n" +
				"\t1. IRI ontology 1. e.g.: http://myonto1.owl  or  file:/C://myonto1.owl  or  file:/usr/local/myonto1.owl\n" +
				"\t2. IRI ontology 2. e.g.: http://myonto2.owl  or  file:/C://myonto2.owl  or  file:/usr/local/myonto2.owl\n" +
				"\t3. Full output path for mapping files and overlapping modules/fragments. e.g. /usr/local/output_path/ or C://output_path/\n" +
				"\t4. Classify the input ontologies together with the mappings. e.g. true or false";
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		
		String onto1;
		String onto2;
		
		
		//TESTS
		/*try {
			String irirootpath = "file:/usr/local/data/DataUMLS/UMLS_Onto_Versions/OAEI_datasets/oaei_2012/fma2nci/";
			
			onto1 = irirootpath + "oaei2012_FMA_small_overlapping_nci.owl";
			onto2 = irirootpath + "oaei2012_NCI_small_overlapping_fma.owl";
			
			
			//System.out.println("Loading ontologies...");
			//OntologyLoader loader1 = new OntologyLoader(onto1);
			//OntologyLoader loader2 = new OntologyLoader(onto2);
			//System.out.println("...Done");			
			//LogMap2_launch logmap = new LogMap2_launch(loader1.getOWLOntology(), loader2.getOWLOntology());
			LogMap2_launch logmap = new LogMap2_launch(onto1, onto2);
			//LogMap2Main logmap = new LogMap2Main(onto1, onto2);
			
			System.out.println("Mappings: " + logmap.getLogmap2_Mappings().size());
			
		}
		catch (Exception e){
			e.printStackTrace();
		}
		
		//ENd tests
		if (true)
			return;
		*/
		
		if (args.length==1){
			
			if (args[0].toLowerCase().contains("help")){
				System.out.println("HELP:\n" + getHelpMessage());
				return;
			}
			
		}
		
		
		String outputpath;
		boolean eval_impact;
		
		
		if (args.length!=4){
			System.out.println(getHelpMessage());
			return;

		}
		else{
			onto1=args[0];
			onto2=args[1];
			outputpath=args[2];
			eval_impact=Boolean.valueOf(args[3]);
		}

		new LogMap2_Matcher(onto1, onto2, outputpath, eval_impact);
		
		
	}
	

}

package uk.ac.ox.krr.logmap2;

import java.net.URL;

import java.util.Calendar;

import uk.ac.ox.krr.logmap2.oaei.oracle.OracleManager;
import uk.ac.ox.krr.logmap2.utilities.Utilities;
import uk.ac.ox.krr.logmap2.io.LogOutput;
import uk.ac.ox.krr.logmap2.io.OAEIAlignmentOutput;

/**
 * This classes manages the required wrapper of LogMap 2 in order to be accept and provide the required input and output data for the SEALS OAEI client.
 * Since 2021 we are using the MELT platform in the OAEI: https://github.com/ernestojimenezruiz/logmap-melt 
 * @author Ernesto
 *
 */
public class LogMap2_OAEI {
	
	private long init_tot, fin;
	private double total_time=0.0;
	
	LogMap2Core logmap2;
	
	OAEIAlignmentOutput alignment_output;
	
	
	public LogMap2_OAEI(){
		
		//LogOutput.showOutpuLog(false);
		//Oraculo.unsetStatusOraculo();
		
		
	}
	
	public void align(URL source, URL target) throws Exception{
		
		init_tot = Calendar.getInstance().getTimeInMillis();
		
		//Done in Bridge
		//Parameters.readParameters();
		
		OracleManager.allowOracle(Parameters.allow_interactivity);
		
		
		//System.out.println(Parameters.path_chinese_segmenter_dict);
		//System.out.println(Parameters.path_multilingual_tmp);
		
		
		logmap2 = new LogMap2Core(source.toURI().toString(), target.toURI().toString());
		
		fin = Calendar.getInstance().getTimeInMillis();
		
		//System.out.println("Matching Time (s): " + (float)((double)fin-(double)init_tot)/1000.0);
		total_time = (float)((double)fin-(double)init_tot)/1000.0;
		//total_time = total_time - time_loading;
		//System.out.println("Time loading ontos (s): " + time_loading);
		//System.out.println("Is Oracle active? " + Oraculo.isActive() + "  " + Oraculo.getStatusOraculo());
		if (OracleManager.isActive()){
			System.out.println("\tNumber of questions to oracle: " + OracleManager.getNumberOfQuestions());
		}
		//System.out.println("LogMap 2 Total Matching Time (s): " + total_time);
		
	}
	
	
	
	private boolean ignoreMapping(String uri_entity){
		//ignore mappings involving entities containing these uris
		for (String uri : Parameters.filter_entities)
			if (uri_entity.contains(uri))
				return true;		
		return false;
	}
	
	
	
	public URL returnAlignmentFile() throws Exception{

		
		alignment_output = new OAEIAlignmentOutput("alignment", logmap2.getIRIOntology1(), logmap2.getIRIOntology2());
		
		int dir_mapping;
		
		if (Parameters.output_class_mappings){
		
			for (int ide1 : logmap2.getClassMappings().keySet()){
				
				if (ignoreMapping(logmap2.getIRI4ConceptIdentifier(ide1)))
					continue;
								
				for (int ide2 : logmap2.getClassMappings().get(ide1)){
					
					dir_mapping = logmap2.getDirClassMapping(ide1, ide2);
					
					if (ignoreMapping(logmap2.getIRI4ConceptIdentifier(ide2)))
						continue;	
					
					
					//Avoid same URI mappings
					if (logmap2.getIRI4ConceptIdentifier(ide1).equals(logmap2.getIRI4ConceptIdentifier(ide2)))
						continue;
					
					
					
					if (dir_mapping!=Utilities.NoMap){
						
						if (dir_mapping!=Utilities.R2L){						
						
							//GSs in OAIE only contains, in general, equivalence mappings
							if (Parameters.output_equivalences_only){
								dir_mapping=Utilities.EQ;
							}
							
							//TODO Filter cases where IRIs are the same
								
							alignment_output.addClassMapping2Output(
									logmap2.getIRI4ConceptIdentifier(ide1),
									logmap2.getIRI4ConceptIdentifier(ide2),
									dir_mapping,
									logmap2.getConfidence4ConceptMapping(ide1, ide2)
									);
						}
						else{
							
							if (Parameters.output_equivalences_only){
								dir_mapping=Utilities.EQ;
							}
							
							alignment_output.addClassMapping2Output(								
									logmap2.getIRI4ConceptIdentifier(ide2),
									logmap2.getIRI4ConceptIdentifier(ide1),
									dir_mapping,
									logmap2.getConfidence4ConceptMapping(ide1, ide2)
									);
						}
					}
				}
			}
		}
		
		
		if (Parameters.output_prop_mappings){
			
					
			for (int ide1 : logmap2.getDataPropMappings().keySet()){
				
				//ignore mappings involving entities containing these uris
				if (ignoreMapping(logmap2.getIRI4DataPropIdentifier(ide1))||
					ignoreMapping(logmap2.getIRI4DataPropIdentifier(logmap2.getDataPropMappings().get(ide1))))
					continue;
				
				//Avoid same URI mappings
				if (logmap2.getIRI4DataPropIdentifier(ide1).equals(logmap2.getIRI4DataPropIdentifier(logmap2.getDataPropMappings().get(ide1))))
					continue;
				
				
				alignment_output.addDataPropMapping2Output(
							logmap2.getIRI4DataPropIdentifier(ide1),
							logmap2.getIRI4DataPropIdentifier(logmap2.getDataPropMappings().get(ide1)),
							Utilities.EQ,  
							logmap2.getConfidence4DataPropConceptMapping(ide1, logmap2.getDataPropMappings().get(ide1))//1.0
							);
			}
			
			for (int ide1 : logmap2.getObjectPropMappings().keySet()){
			
				//ignore mappings involving entities containing these uris
				if (ignoreMapping(logmap2.getIRI4ObjectPropIdentifier(ide1))||
					ignoreMapping(logmap2.getIRI4ObjectPropIdentifier(logmap2.getObjectPropMappings().get(ide1))))
					continue;
			
				
				//Avoid same URI mappings
				if (logmap2.getIRI4ObjectPropIdentifier(ide1).equals(logmap2.getIRI4ObjectPropIdentifier(logmap2.getObjectPropMappings().get(ide1))))
					continue;
				
				
				alignment_output.addObjPropMapping2Output(
							logmap2.getIRI4ObjectPropIdentifier(ide1),
							logmap2.getIRI4ObjectPropIdentifier(logmap2.getObjectPropMappings().get(ide1)),
							Utilities.EQ, 
							logmap2.getConfidence4ObjectPropConceptMapping(ide1, logmap2.getObjectPropMappings().get(ide1))//1.0
							);
			}
		}
		

		//Output for individuals
		if (Parameters.perform_instance_matching && Parameters.output_instance_mappings){
			
			if (Parameters.output_instance_mapping_files){
				int type;
				for (int ide1 : logmap2.getInstanceMappings4OutputType().keySet()) {
					
					for (int ide2 : logmap2.getInstanceMappings4OutputType().get(ide1).keySet()){
					
						
						//Avoid same URI mappings
						if (logmap2.getIRI4InstanceIdentifier(ide1).equals(logmap2.getIRI4InstanceIdentifier(ide2)))
							continue;
						
						
						type = logmap2.getInstanceMappings4OutputType().get(ide1).get(ide2);
												
						if (type<=1){
							alignment_output.addInstanceMapping2Output(
									logmap2.getIRI4InstanceIdentifier(ide1), 
									logmap2.getIRI4InstanceIdentifier(ide2), 
									logmap2.getConfidence4InstanceMapping(ide1, ide2)
								);					
						}
					}
				}
			}
			else{
				for (int ide1 : logmap2.getInstanceMappings().keySet()){
					for (int ide2 : logmap2.getInstanceMappings().get(ide1)){
						
						//Avoid same URI mappings
						if (logmap2.getIRI4InstanceIdentifier(ide1).equals(logmap2.getIRI4InstanceIdentifier(ide2)))
							continue;
					
						alignment_output.addInstanceMapping2Output(
								logmap2.getIRI4InstanceIdentifier(ide1), 
								logmap2.getIRI4InstanceIdentifier(ide2), 
								logmap2.getConfidence4InstanceMapping(ide1, ide2)
							);					
					}
				}
			}
			
			
		}
		
		
		alignment_output.saveOutputFile();
		
		logmap2.clearIndexStructures();
		
		return alignment_output.returnAlignmentFile();
		
	}

}

package uk.ac.ox.krr.logmap2;

import uk.ac.ox.krr.logmap2.oaei.reader.MappingsReaderManager;
import uk.ac.ox.krr.logmap_lite.LogMap_Lite;
import uk.ac.ox.krr.logmap2.oaei.FromRDFAlign2OWL;


public class LogMap2_CommandLine {

	
	private static String getHelpMessage(){
		return "LogMap 2 can operate as an ontology matching system (MATCHER/EVALUATION) or as a mapping debugging system (DEBUGGER). " +
				"Additionally it also converts mappings from RDF-OAEI format to OWL.\n\n" +
	
				"LogMap 2 MATCHER facility requires 5 parameters:\n" +
				"\t1. MATCHER. To use the matching functionality.\n" +
				"\t2. IRI ontology 1. e.g.: http://myonto1.owl  or  file:/C://myonto1.owl  or  file:/usr/local/myonto1.owl\n" +
				"\t3. IRI ontology 2. e.g.: http://myonto2.owl  or  file:/C://myonto2.owl  or  file:/usr/local/myonto2.owl\n" +
				"\t4. Full output path for mapping files and overlapping modules/fragments. e.g. /usr/local/output_path/ or (windows) C://output_path/ or /C://output_path/\n" +
				"\t5. Classify the input ontologies together with the mappings. e.g. true or false\n\n" +
				"\tFor example: java -jar logmap2_standalone.jar MATCHER file:/home/ontos/cmt.owl file:/home/ontos/ekaw.owl /home/mappings/output/ true\n\n\n" +
				
				
				"LogMap 2 MATCHER-BIO facility requires 4 parameters:\n" +
				"\t1. MATCHER-BIO. To use the matching functionality and using BioPortal to extract mediating ontologies.\n" +
				"\t2. IRI ontology 1. e.g.: http://myonto1.owl  or  file:/C://myonto1.owl  or  file:/usr/local/myonto1.owl\n" +
				"\t3. IRI ontology 2. e.g.: http://myonto2.owl  or  file:/C://myonto2.owl  or  file:/usr/local/myonto2.owl\n" +
				"\t4. Full output path for mapping files. e.g. /usr/local/output_path/ or (windows) C://output_path/ or /C://output_path/\n" +
				"\tFor example: java -jar logmap2_standalone.jar MATCHER file:/home/ontos/cmt.owl file:/home/ontos/ekaw.owl /home/mappings/output/ true\n\n\n" +
				
				
				"LogMap 2 EVALUATION facility requires 6 parameters:\n" +
				"\t1. EVALUATION. To use the matching + evaluation functionality against reference mappings.\n" +
				"\t2. IRI ontology 1. e.g.: http://myonto1.owl  or  file:/C://myonto1.owl  or  file:/usr/local/myonto1.owl\n" +
				"\t3. IRI ontology 2. e.g.: http://myonto2.owl  or  file:/C://myonto2.owl  or  file:/usr/local/myonto2.owl\n" +
				"\t4. Reference mappings (RDF alignment format). e.g.: /usr/local/reference_mappings.rdf\n" +
				"\t5. Full output path for mapping files and overlapping modules/fragments. e.g. /usr/local/output_path/ or (windows) C://output_path/ or /C://output_path/\n" +
				"\t6. Classify the input ontologies together with the mappings. e.g. true or false\n\n" +
				"\tFor example: java -jar logmap2_standalone.jar EVALUATION file:/home/ontos/cmt.owl file:/home/ontos/ekaw.owl /home/refs/ref-cmt-ekaw.rdf /home/mappings/output/ true\n\n\n" +
				
				"LogMap 2 DEBUGGER facility requires 8 parameters:\n" +
				"\t1. DEBUGGER. To use the debugging facility.\n" +
				"\t2. IRI ontology 1. e.g.: http://myonto1.owl  or  file:/C://myonto1.owl  or  file:/usr/local/myonto1.owl\n" +
				"\t3. IRI ontology 2. e.g.: http://myonto2.owl  or  file:/C://myonto2.owl  or  file:/usr/local/myonto2.owl\n" +
				"\t4. Format mappings e.g.: OWL  or  RDF  or  TXT\n" +
				"\t5. Full IRI or full Path:\n" +
				"\t\ta. Full IRI of input mappings if OWL format. e.g.: file:/C://mymappings.owl  or  file:/usr/local/mymappings.owl  or http://mymappings.owl\n" +
				"\t\tb. or Full path of input mappings if formats RDF or TXT. e.g.: C://mymappings.rdf  or /usr/local/mymappings.txt\n" +
				"\t6. Full output path for the repaired mappings: e.g. /usr/local/output_path or (windows) C://output_path or /C://output_path/\n" +
				"\t7. Extract modules for repair?: true or false\n" +
				"\t8. Check satisfiability after repair using HermiT? true or false\n\n" +
				"\tFor example: java -jar logmap2_standalone.jar DEBUGGER file:/home/ontos/cmt.owl file:/home/ontos/ekaw.owl " +
				"RDF /usr/local/mymappings.rdf /home/mappings/output false true\n\n\n" + 
				
				
				"LogMap 2 LITE facility requires 4 parameters:\n" +
				"\t1. MATCHER. To use the matching functionality.\n" +
				"\t2. IRI ontology 1. e.g.: http://myonto1.owl  or  file:/C://myonto1.owl  or  file:/usr/local/myonto1.owl\n" +
				"\t3. IRI ontology 2. e.g.: http://myonto2.owl  or  file:/C://myonto2.owl  or  file:/usr/local/myonto2.owl\n" +
				"\t4. Full output path for mapping files and overlapping modules/fragments. e.g. /usr/local/output_path/ or (windows) C://output_path/ or /C://output_path/\n" +
				"\tFor example: java -jar logmap2_standalone.jar LITE file:/home/ontos/cmt.owl file:/home/ontos/ekaw.owl /home/mappings/output/ \n\n\n" +
								
				
				"The RDF2OWL converter facility requires 4 parameters:\n" +
				"\t1. RDF2OWL. To transform from RDF-OAEI format to OWL. Note that the input ontologies are required to check the type of entity of the mapped IRIs.\n" +
				"\t2. IRI ontology 1. e.g.: http://myonto1.owl  or  file:/C://myonto1.owl  or  file:/usr/local/myonto1.owl\n" +
				"\t3. IRI ontology 2. e.g.: http://myonto2.owl  or  file:/C://myonto2.owl  or  file:/usr/local/myonto2.owl\n" +
				"\t4. Full path RDF mappings to be converted: e.g. C://mymappings.rdf  or  /usr/local/mymappings.rdf\n\n" +
				"\tFor example: java -jar logmap2_standalone.jar RDF2OWL file:/home/ontos/cmt.owl file:/home/ontos/ekaw.owl /usr/local/mymappings.rdf\n\n";
				
					
		
	}
	
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		try{
			
			if (args.length<1){// || (!args[0].equals("MATCHER") && !args[0].equals("DEBUGGER"))){
				
				System.out.println(getHelpMessage());				
			}
			
			else if (args[0].equals("RDF2OWL")){
				if (args.length!=4){
					System.out.println(getHelpMessage());				
				}
				else{
					new FromRDFAlign2OWL(args[1], args[2], args[3]);
				}
				
			}
						
			else if (args[0].equals("MATCHER")){
				
				if (args.length!=5){
					System.out.println(getHelpMessage());				
				}
				else {				
					new LogMap2_Matcher(args[1], args[2], args[3], Boolean.valueOf(args[4]));
					
				}
				
				
			}
			else if (args[0].equals("MATCHER-BIO")){
				
				if (args.length!=5){
					System.out.println(getHelpMessage());				
				}
				else {
					
					LogMap2_OAEI_BioPortal logmap_bio = new LogMap2_OAEI_BioPortal();
					
					logmap_bio.align(args[1], args[2]);
					logmap_bio.saveLogMapBioMappings(args[3]);
					
				}
				
				
			}
			
			
			
			
			else if (args[0].equals("EVALUATION")){
				
				if (args.length!=6){
					System.out.println(getHelpMessage());				
				}
				else {				
					new LogMap2_Matcher(args[1], args[2], args[3], args[4], Boolean.valueOf(args[5]));
					
				}
				
				
			}
			
			else if (args[0].equals("LITE")){
				
				if (args.length!=4){
					System.out.println(getHelpMessage());				
				}
				else {				
					new LogMap_Lite(args[1], args[2], args[3]);
					
				}
			}
			
			
			
			else if (args[0].equals("DEBUGGER")){
			
				String iri_onto1;
				String iri_onto2;
				String format_mappings;
				String input_mappings_path;
				String output_path;
				boolean overlapping;
				boolean satisfiability_check;
				
				MappingsReaderManager readermanager;
				OntologyLoader loader1;
				OntologyLoader loader2;
				
				if (args.length!=8){
					System.out.println(getHelpMessage());
					return;
				}
				else{				
					iri_onto1=args[1];
					iri_onto2=args[2];
					format_mappings=args[3];
					input_mappings_path=args[4];
					output_path=args[5];
					overlapping=Boolean.valueOf(args[6]);
					satisfiability_check=Boolean.valueOf(args[7]);
					
				}
				
				
				
				System.out.println("Loading ontologies...");
				loader1 = new OntologyLoader(iri_onto1);
				loader2 = new OntologyLoader(iri_onto2);
				System.out.println("...Done");
				
				
				readermanager = new MappingsReaderManager(input_mappings_path, format_mappings);
				
				new LogMap2_RepairFacility(
						loader1.getOWLOntology(), 
						loader2.getOWLOntology(), 
						readermanager.getMappingObjects(),
						overlapping,
						true, //always optimal
						satisfiability_check,
						output_path +  "/" + "mappings_repaired_with_LogMap");
				
				
				
			}
			else {
				System.out.println(getHelpMessage());
			}
			
			
			
		}
		catch(Exception e){
			System.out.println(getHelpMessage());
		}
		
		
	}

}

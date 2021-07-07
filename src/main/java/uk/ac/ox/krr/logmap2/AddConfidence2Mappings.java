package uk.ac.ox.krr.logmap2;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;

import uk.ac.ox.krr.logmap2.indexing.JointIndexManager;
import uk.ac.ox.krr.logmap2.indexing.OntologyProcessing;
import uk.ac.ox.krr.logmap2.io.LogOutput;
import uk.ac.ox.krr.logmap2.io.OutPutFilesManager;
import uk.ac.ox.krr.logmap2.io.ReadFile;
import uk.ac.ox.krr.logmap2.lexicon.LexicalUtilities;
import uk.ac.ox.krr.logmap2.mappings.CandidateMappingManager;
import uk.ac.ox.krr.logmap2.mappings.objects.MappingObjectStr;
import uk.ac.ox.krr.logmap2.oaei.reader.MappingsReaderManager;
import uk.ac.ox.krr.logmap2.utilities.Utilities;

/**
 * This class will add confidence based on the scope and the isub similarity 
 * to those mappings without confidence value (e.g. UMLS mappings) 
 * @author Ernesto
 *
 */
public class AddConfidence2Mappings {

	private long init_global, init, fin;
	
	
	private OntologyProcessing onto_process1;
	private OntologyProcessing onto_process2;
	
	private JointIndexManager index;
	
	private CandidateMappingManager mapping_manager;
	
	
	private OWLOntology onto1;
	private OWLOntology onto2;
	private Set<MappingObjectStr> input_mappings = new HashSet<MappingObjectStr>();
	private String ouput_file;
	
	
	
	public AddConfidence2Mappings(
			OWLOntology ont1,
			OWLOntology ont2, 
			Set<MappingObjectStr> mappings, 
			String outPutFileName){
		
		//init_global = init = Calendar.getInstance().getTimeInMillis();
		
		onto1 = ont1;
		onto2 = ont2;
		input_mappings = mappings;
		ouput_file = outPutFileName;
		
		try {
			setUpStructures();
			
			//When reading from RDF align there is no type
			//Also adds classes to structure/anchors
			associateType2Mappings();
			
			//We add new confidence to mappings
			addConfidenceToMapping();
			
			//Ouputmappings
			saveMappings();
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		onto_process1.clearReasoner();
		onto_process1.getClass2Identifier().clear();

		onto_process2.clearReasoner();
		onto_process2.getClass2Identifier().clear();
		
	}
	
	
	private void setUpStructures() throws Exception{
		
		
		//TODO showOutput!!		
		LogOutput.showOutpuLog(false);
		
		
		
		
		//Create Index and new Ontology Index...
		index = new JointIndexManager();
		
		
		onto_process1 = new OntologyProcessing(onto1, index, new LexicalUtilities());
		onto_process2 = new OntologyProcessing(onto2, index, new LexicalUtilities());
		
		
		
		mapping_manager = new CandidateMappingManager(index, onto_process1, onto_process2);
		
		
		
		//Extracts lexicon
		init = Calendar.getInstance().getTimeInMillis();
		onto_process1.precessLexicon(true);
		onto_process2.precessLexicon(true);
		fin = Calendar.getInstance().getTimeInMillis();
		LogOutput.print("Time indexing entities (s): " + (float)((double)fin-(double)init)/1000.0);

		
		//Extracts Taxonomy
		//Also extracts A^B->C
		init = Calendar.getInstance().getTimeInMillis();
		onto_process1.setTaxonomicData();
		onto_process2.setTaxonomicData();
		fin = Calendar.getInstance().getTimeInMillis();
		LogOutput.print("Time extracting structural information (s): " + (float)((double)fin-(double)init)/1000.0);
		
			
		
		
	}
	
	
	/**
	 * We associate type to mappings in case the object does indicate this.
	 */
	private void associateType2Mappings(){
		

		//TREAT GIVEN MAPPINGS

		int num_original_class_mappings=0;
		int num_original_dprop_mappings=0;
		int num_original_oprop_mappings=0;
		int num_original_instance_mappings=0;
		int num_mixed_mappings=0;
		
		
		for (MappingObjectStr map : input_mappings){
			
			//Detect the type of mapping: class, property or instance
			//In some cases it might be included
			if (onto1.containsClassInSignature(IRI.create(map.getIRIStrEnt1()), true)
				&& onto2.containsClassInSignature(IRI.create(map.getIRIStrEnt2()), true)) {
				
				map.setTypeOfMapping(Utilities.CLASSES);
				
				//We add mapping to anchors. Important to get scope
				addClassMapping(map);
				
				num_original_class_mappings++;
				
				
			}
			else if (onto1.containsObjectPropertyInSignature(IRI.create(map.getIRIStrEnt1()), true)
					&& onto2.containsObjectPropertyInSignature(IRI.create(map.getIRIStrEnt2()), true)) {
					
				map.setTypeOfMapping(Utilities.OBJECTPROPERTIES);
				
				num_original_oprop_mappings++;
				
			
			}
			else if (onto1.containsDataPropertyInSignature(IRI.create(map.getIRIStrEnt1()), true)
				&& onto2.containsDataPropertyInSignature(IRI.create(map.getIRIStrEnt2()), true)) {
				
				map.setTypeOfMapping(Utilities.DATAPROPERTIES);
				
				num_original_dprop_mappings++;
				
			}
			
			else if (onto1.containsIndividualInSignature(IRI.create(map.getIRIStrEnt1()), true)
					&& onto2.containsIndividualInSignature(IRI.create(map.getIRIStrEnt2()), true)) {
				
				map.setTypeOfMapping(Utilities.INSTANCES);
				
				num_original_instance_mappings++;
				
			}
			else {
				System.out.println("Mixed Entities or entities not in signature of ontologies: ");
				System.out.println("\t" + map.getIRIStrEnt1());
				System.out.println("\t" + map.getIRIStrEnt2());
				
				num_mixed_mappings++;
				
			}
			
			
		}
		
		
		
		LogOutput.printAlways("Num original mappings: " + input_mappings.size());
		LogOutput.printAlways("\tNum original class mappings: " + num_original_class_mappings);
		LogOutput.printAlways("\tNum original object property mappings: " + num_original_oprop_mappings);
		LogOutput.printAlways("\tNum original data property mappings: " + num_original_dprop_mappings);			
		LogOutput.printAlways("\tNum original instance mappings: " + num_original_instance_mappings);
		LogOutput.printAlways("\tNum mixed mappings: " + num_mixed_mappings);
		
		
		
	}
	
	

	
	
	/**
	 * Adds mappings to structures
	 * @param map
	 */
	private void addClassMapping(MappingObjectStr map){
		
		
		
		int ide1;
		int ide2;
		
		
				
		//Translate from mapping 2 index
		ide1=onto_process1.getIdentifier4ConceptName(Utilities.getEntityLabelFromURI(map.getIRIStrEnt1()));
		ide2=onto_process2.getIdentifier4ConceptName(Utilities.getEntityLabelFromURI(map.getIRIStrEnt2()));
		
		
		//We only consider classes
		if (ide1<0 || ide2<0){
			LogOutput.print("Classes not found in ontology.");
			LogOutput.print("\t" + ide1 + "  " + map.getIRIStrEnt1());
			LogOutput.print("\t" + ide2 + "  " + map.getIRIStrEnt2());
			return;
		}
		
			
		if (map.getMappingDirection()==Utilities.EQ){
			mapping_manager.addSubMapping2ListOfAnchors(ide1, ide2);
			mapping_manager.addSubMapping2ListOfAnchors(ide2, ide1);
				
		}
		else if (map.getMappingDirection()==Utilities.L2R){
			mapping_manager.addSubMapping2ListOfAnchors(ide1, ide2);
		
		}
		else{
			mapping_manager.addSubMapping2ListOfAnchors(ide2, ide1);
		
		}
			
				
	}
	
	
	private void addConfidenceToMapping(){
		
		int ide1;
		int ide2;
		
		
		for (MappingObjectStr map : input_mappings){
						
			if (map.getTypeOfMapping()==Utilities.CLASSES){
				
				ide1=onto_process1.getIdentifier4ConceptName(Utilities.getEntityLabelFromURI(map.getIRIStrEnt1()));
				ide2=onto_process2.getIdentifier4ConceptName(Utilities.getEntityLabelFromURI(map.getIRIStrEnt2()));
				
				//Get isub and scope
				map.setConfidenceMapping(
						0.5 * mapping_manager.getIsubScore4ConceptsLabels(ide1, ide2) +
						0.5 * mapping_manager.getScopeISUB4Neighbourhood(ide1, ide2));
				
				
			}
			else if (map.getTypeOfMapping()==Utilities.OBJECTPROPERTIES){ 
			
				ide1=onto_process1.getIdentifier4ObjectPropName(Utilities.getEntityLabelFromURI(map.getIRIStrEnt1()));
				ide2=onto_process2.getIdentifier4ObjectPropName(Utilities.getEntityLabelFromURI(map.getIRIStrEnt2()));
				
				//Use only isub
				map.setConfidenceMapping(mapping_manager.getIsubScore4ObjectPropertyLabels(ide1, ide2));
				
				
				
			}
			
			else if (map.getTypeOfMapping()==Utilities.DATAPROPERTIES){ 
				
				ide1=onto_process1.getIdentifier4DataPropName(Utilities.getEntityLabelFromURI(map.getIRIStrEnt1()));
				ide2=onto_process2.getIdentifier4DataPropName(Utilities.getEntityLabelFromURI(map.getIRIStrEnt2()));
				
				//Use only isub
				//Use only isub
				map.setConfidenceMapping(mapping_manager.getIsubScore4DataPropertyLabels(ide1, ide2));
				
			}
			else if (map.getTypeOfMapping()==Utilities.INSTANCES){ 
			
				ide1=onto_process1.getIdentifier4InstanceName(Utilities.getEntityLabelFromURI(map.getIRIStrEnt1()));
				ide2=onto_process2.getIdentifier4InstanceName(Utilities.getEntityLabelFromURI(map.getIRIStrEnt2()));
				
				//Use only isub
				map.setConfidenceMapping(mapping_manager.getIsubScore4IndividualLabels(ide1, ide2));
				
			}
			
			
		}
		
		
	}

	
	private void saveMappings(){
		
		OutPutFilesManager outPutFilesManager = new OutPutFilesManager();
		
		try {
			outPutFilesManager.createOutFiles(
					//logmap_mappings_path + "Output/mappings",
					//path + "/" + file_name,
					//outPutFileName + "/" + "repaired_mappings",
					ouput_file,
					OutPutFilesManager.AllFormats,
					onto1.getOntologyID().getOntologyIRI().get().toString(),
					onto2.getOntologyID().getOntologyIRI().get().toString());
			
			
			
			for (MappingObjectStr map : input_mappings){
				
				if (map.getTypeOfMapping()==Utilities.CLASSES){
					
					outPutFilesManager.addClassMapping2Files(
							map.getIRIStrEnt1(),
							map.getIRIStrEnt2(),
							map.getMappingDirection(), 
							map.getConfidence()
							);
				}
				
				else if (map.getTypeOfMapping()==Utilities.OBJECTPROPERTIES){ 
				
					outPutFilesManager.addObjPropMapping2Files(
							map.getIRIStrEnt1(),
							map.getIRIStrEnt2(),
							map.getMappingDirection(), 
							map.getConfidence()
							);
					
				}
				
				else if (map.getTypeOfMapping()==Utilities.DATAPROPERTIES){ 
					
					outPutFilesManager.addDataPropMapping2Files(
							map.getIRIStrEnt1(),
							map.getIRIStrEnt2(),
							map.getMappingDirection(), 
							map.getConfidence()
							);
				}
				
				else if (map.getTypeOfMapping()==Utilities.INSTANCES){ 
					outPutFilesManager.addInstanceMapping2Files(
							map.getIRIStrEnt1(),
							map.getIRIStrEnt2(),
							//map.getMappingDirection(), 
							map.getConfidence()
							);
					
				}
			}
			
			
			outPutFilesManager.closeAndSaveFiles();
			
			
		}
		catch (Exception e){
			System.err.println("Error saving mappings...");
			e.printStackTrace();
		}
		
		
	}
	
	
	


	/**
	 * Load Gold Standard Mappings
	 * @throws Exception
	 */
	private static Set<MappingObjectStr> loadInputMappings(String path) throws Exception{
	
		ReadFile reader = new ReadFile(path);
		
		Set<MappingObjectStr> mappings = new HashSet<MappingObjectStr>();
		
		String line;
		String[] elements;
		
		line=reader.readLine();
		
				
		while (line!=null) {
			
			if (line.indexOf("|")<0){
				System.out.println("Wrong line: " + line);
				line=reader.readLine();
				continue;
			}
			
			elements=line.split("\\|");
			
			MappingObjectStr mapping = new MappingObjectStr(elements[0], elements[1]);
			
			if (!mappings.contains(mapping)){	
				mappings.add(mapping);
			}
			else{
				System.out.println("Duplicated line: " + line);
			}
				
			line=reader.readLine();
		}		
		
		reader.closeBuffer();
	
		return mappings;
		
	}
		

	
	private static void StatisticsOAEI2012() throws Exception{
		
		int onto_pair;
		//onto_pair = Utilities.FMA2NCI;
		//onto_pair = Utilities.FMA2SNOMED;
		onto_pair = Utilities.SNOMED2NCI;

		StatisticsOAEI2012("/usr/local/data/DataUMLS/UMLS_Onto_Versions/", onto_pair);
		
	}

		
	/**
	 * Used to extract statistics from OAEI 2012 tool outputs
	 */
	private static void StatisticsOAEI2012(String path_base, int ontoPair) throws Exception{

		long init,fin;
		
		String format_mappings;
		
		MappingsReaderManager readermanager;
		OntologyLoader loader1;
		OntologyLoader loader2;
		
		//String base_path = "/usr/local/data/DataUMLS/UMLS_Onto_Versions/";
		String base_path = path_base;
		int onto_pair = ontoPair;
		

		String rootpath;
		String rootpath_fma2nci = base_path + "OAEI_datasets/oaei_2012/fma2nci/";
		String rootpath_fma2snomed = base_path + "OAEI_datasets/oaei_2012/fma2snmd/";
		String rootpath_snomed2nci = base_path + "OAEI_datasets/oaei_2012/snmd2nci/";
		
		String irirootpath;
		String irirootpath_fma2nci = "file:" + rootpath_fma2nci;
		String irirootpath_fma2snomed = "file:" + rootpath_fma2snomed;
		String irirootpath_snomed2nci = "file:" +  rootpath_snomed2nci;
		
		//String irirootpath = "file:/usr/local/data/DataUMLS/UMLS_Onto_Versions/OAEI_datasets/fma2nci_dataset/";	
		//String mappings_path = "/usr/local/data/DataUMLS/UMLS_Onto_Versions/OAEI_datasets/OutputAlcomo/";
		
		String onto1;
		String onto2;
		String pattern;
		String extension;
		
		
				
		format_mappings = MappingsReaderManager.FlatFormat;
		
		String path_input_mappings;
		String output_file = base_path + "OAEI_datasets/oaei_2013/";
		
		if (onto_pair==Utilities.FMA2NCI){
			
			path_input_mappings = base_path + "OAEI_datasets/UMLS_txt_mappings/onto_mappings_FMA_NCI_dirty.txt";

			irirootpath = irirootpath_fma2nci;
			
			onto1 = irirootpath + "oaei2012_FMA_whole_ontology.owl";
			onto2 = irirootpath + "oaei2012_NCI_whole_ontology.owl";
			
			output_file+="onto_mappings_FMA_NCI_dirty_confidence";
			
		}
		else if (onto_pair==Utilities.FMA2SNOMED){
			
			path_input_mappings = base_path + "OAEI_datasets/UMLS_txt_mappings/onto_mappings_FMA_SNOMED_dirty.txt";
			
			irirootpath = irirootpath_fma2snomed;
			
			onto1 = irirootpath_fma2nci + "oaei2012_FMA_whole_ontology.owl";
			onto2 = irirootpath + "oaei2012_SNOMED_whole_ontology.owl.zip";
			
			output_file+="onto_mappings_FMA_SNOMED_dirty_confidence";
			
		}
		else {
			
			path_input_mappings = base_path + "OAEI_datasets/UMLS_txt_mappings/onto_mappings_SNOMED_NCI_dirty.txt";
			
			irirootpath = irirootpath_snomed2nci;
			onto2 = irirootpath_fma2nci + "oaei2012_NCI_whole_ontology.owl";
			onto1 = irirootpath_fma2snomed + "oaei2012_SNOMED_whole_ontology.owl.zip";
			
			output_file+="onto_mappings_SNOMED_NCI_dirty_confidence";
			
		}
		
		
		loadInputMappings(path_input_mappings);
		if (true)
			return;	
				
		LogOutput.printAlways("Loading ontologies...");
		loader1 = new OntologyLoader(onto1);
		loader2 = new OntologyLoader(onto2);
		LogOutput.printAlways("...Done");
		
		
		new AddConfidence2Mappings(
				loader1.getOWLOntology(),
				loader2.getOWLOntology(),
				loadInputMappings(path_input_mappings),
				output_file);
				
		
	}
	
	public static void main(String[] args) {
		
		try {
			StatisticsOAEI2012();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	
}

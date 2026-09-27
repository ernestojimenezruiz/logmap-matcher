package uk.ac.ox.krr.logmap2;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.parameters.Imports;

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

public class GetRepresentativeLabelsSetForMappings {
	
	private long init_global, init, fin;
	
	private OntologyProcessing onto_process1;
	private OntologyProcessing onto_process2;
	
	private JointIndexManager index;
	
	private CandidateMappingManager mapping_manager;
	
	
	private OWLOntology onto1;
	private OWLOntology onto2;
	private Set<MappingObjectStr> input_mappings = new HashSet<MappingObjectStr>();
	private String ouput_file;
	
	
	private Set<String> representativeLabels = new HashSet<String>();
	
	
	
	
	public GetRepresentativeLabelsSetForMappings(
			OWLOntology ont1,
			OWLOntology ont2, 
			Set<MappingObjectStr> mappings) { 
			//String outPutFileName){
		
		//init_global = init = Calendar.getInstance().getTimeInMillis();
		
		onto1 = ont1;
		onto2 = ont2;
		input_mappings = mappings;
		//ouput_file = outPutFileName;
		
		try {
			setUpStructures();
			
			//When reading from RDF align there is no type
			//Also adds classes to structure/anchors
			createRepresentativeLabels();
			
					
			
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
		LogOutput.showOutpuLogAlways(true);
		
		
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
	private void createRepresentativeLabels(){
		

		//TREAT GIVEN MAPPINGS

		int num_original_class_mappings=0;
		int num_original_dprop_mappings=0;
		int num_original_oprop_mappings=0;
		int num_original_instance_mappings=0;
		int num_mixed_mappings=0;
		
		
		int id1;
		int id2;
		
		
		int counter_labels = 0;
		
		
		//Associate mapping type first
		for (MappingObjectStr map : input_mappings){
			
			//Detect the type of mapping: class, property or instance
			//In some cases it might be included
			if (onto1.containsClassInSignature(IRI.create(map.getIRIStrEnt1()), Imports.INCLUDED)
				&& onto2.containsClassInSignature(IRI.create(map.getIRIStrEnt2()), Imports.INCLUDED)) {
				
				map.setTypeOfMapping(Utilities.CLASSES);
				
				//We add mapping to anchors. Important to get scope
				//addClassMapping(map);
				
				num_original_class_mappings++;
				
				
				id1 = onto_process1.getIdentifier4ConceptIRI(map.getIRIStrEnt1());
				id2 = onto_process2.getIdentifier4ConceptIRI(map.getIRIStrEnt2());
				
				//We add labels from both ontos
				if (counter_labels%2 == 0) {
					representativeLabels.add(index.getLabel4ConceptIndex(id1));
				}
				else {
					representativeLabels.add(index.getLabel4ConceptIndex(id2));
				}
				
				
				
				
				
			}
			else if (onto1.containsObjectPropertyInSignature(IRI.create(map.getIRIStrEnt1()), Imports.INCLUDED)
					&& onto2.containsObjectPropertyInSignature(IRI.create(map.getIRIStrEnt2()), Imports.INCLUDED)) {
					
				map.setTypeOfMapping(Utilities.OBJECTPROPERTIES);
				
				num_original_oprop_mappings++;

				
				id1 = onto_process1.getIdentifier4ObjectPropName(Utilities.getEntityNameFromURI(map.getIRIStrEnt1()));
				id2 = onto_process2.getIdentifier4ObjectPropName(Utilities.getEntityNameFromURI(map.getIRIStrEnt2()));
				
				//We add labels from both ontos
				if (counter_labels%2 == 0) {
					representativeLabels.add(index.getLabel4ObjPropIndex(id1));
				}
				else {
					representativeLabels.add(index.getLabel4ObjPropIndex(id2));
				}
				
				
			
			}
			else if (onto1.containsDataPropertyInSignature(IRI.create(map.getIRIStrEnt1()), Imports.INCLUDED)
				&& onto2.containsDataPropertyInSignature(IRI.create(map.getIRIStrEnt2()), Imports.INCLUDED)) {
				
				map.setTypeOfMapping(Utilities.DATAPROPERTIES);
				
				num_original_dprop_mappings++;
				
				id1 = onto_process1.getIdentifier4DataPropName(Utilities.getEntityNameFromURI(map.getIRIStrEnt1()));
				id2 = onto_process2.getIdentifier4DataPropName(Utilities.getEntityNameFromURI(map.getIRIStrEnt2()));
				
				
				//We add labels from both ontos
				if (counter_labels%2 == 0) {
					representativeLabels.add(index.getLabel4DataPropIndex(id1));
				}
				else {
					representativeLabels.add(index.getLabel4DataPropIndex(id2));
				}
				
				
				
			}
			
			else if (onto1.containsIndividualInSignature(IRI.create(map.getIRIStrEnt1()), Imports.INCLUDED)
					&& onto2.containsIndividualInSignature(IRI.create(map.getIRIStrEnt2()), Imports.INCLUDED)) {
				
				map.setTypeOfMapping(Utilities.INSTANCES);
				
				num_original_instance_mappings++;
				
				id1 = onto_process1.getIdentifier4InstanceName(Utilities.getEntityNameFromURI(map.getIRIStrEnt1()));
				id2 = onto_process2.getIdentifier4InstanceName(Utilities.getEntityNameFromURI(map.getIRIStrEnt2()));
				
				
				//We add labels from both ontos
				if (counter_labels%2 == 0) {
					representativeLabels.add(index.getLabel4IndividualIndex(id1));
				}
				else {
					representativeLabels.add(index.getLabel4IndividualIndex(id2));
				}
				
			}
			else {
				System.out.println("Mixed Entities or entities not in signature of ontologies: ");
				System.out.println("\t" + map.getIRIStrEnt1());
				System.out.println("\t" + map.getIRIStrEnt2());
				
				num_mixed_mappings++;
				
			}
			
			
			
			
			//representativeLabels.add(ouput_file);
			counter_labels++;
			
		}
		
		
		
		LogOutput.printAlways("Num original mappings: " + input_mappings.size());
		LogOutput.printAlways("\tNum original class mappings: " + num_original_class_mappings);
		LogOutput.printAlways("\tNum original object property mappings: " + num_original_oprop_mappings);
		LogOutput.printAlways("\tNum original data property mappings: " + num_original_dprop_mappings);			
		LogOutput.printAlways("\tNum original instance mappings: " + num_original_instance_mappings);
		LogOutput.printAlways("\tNum mixed mappings: " + num_mixed_mappings);
		
		
		LogOutput.printAlways("Counter: " + counter_labels);
		
		
		
	}
	
	
	
	public Set<String> getRepresentativeLabels(){
		return representativeLabels;
	}
	

	

	
	private void saveMappings(){
		
		
		
		//TODO Create extended TSV file
		
		
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
	
	
	

		


	
	public static void main(String[] args) {
		
		try {
			
			OntologyLoader loader1 = new OntologyLoader("file:/C:/Users/Ernes/OneDrive/Documents/OAEI/anatomy/mouse.owl");
			OntologyLoader loader2 = new OntologyLoader("file:/C:/Users/Ernes/OneDrive/Documents/OAEI/anatomy/human.owl");
			
			MappingsReaderManager mappingReader = new MappingsReaderManager("C:/Users/Ernes/OneDrive/Documents/OAEI/anatomy/reference.rdf", "RDF"); 
			
			GetRepresentativeLabelsSetForMappings representativeLabelExtractor = 
				new GetRepresentativeLabelsSetForMappings(
						loader1.getOWLOntology(), 
						loader2.getOWLOntology(), 
						mappingReader.getMappingObjects());
			
			
			System.out.println(representativeLabelExtractor.getRepresentativeLabels().size());
			
			for (String label : representativeLabelExtractor.getRepresentativeLabels()) {
				System.out.println(label);
			}
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	

}

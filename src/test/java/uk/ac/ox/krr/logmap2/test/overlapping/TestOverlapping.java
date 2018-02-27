package uk.ac.ox.krr.logmap2.test.overlapping;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import uk.ac.ox.krr.logmap2.LogMap2Core;
import uk.ac.ox.krr.logmap2.Parameters;
import uk.ac.ox.krr.logmap2.io.LogOutput;
import uk.ac.ox.krr.logmap2.io.ReadFile;
import uk.ac.ox.krr.logmap2.lexicon.LexicalUtilities;
import uk.ac.ox.krr.logmap2.mappings.objects.MappingObjectStr;
import uk.ac.ox.krr.logmap2.oaei.reader.RDFAlignReader;
import uk.ac.ox.krr.logmap2.overlapping.LexicalOverlappingExtractor;
import uk.ac.ox.krr.logmap2.statistics.StatisticsTimeMappings;
import uk.ac.ox.krr.logmap2.utilities.Utilities;
import uk.ac.ox.krr.logmap_lite.LogMap_Lite;


public class TestOverlapping {

	
	
	/**
	 * Compares candidate entities from ontologies with mapped entities through umls
	 */
	private static double getRecallOverlapping(Set<String> reference_entities, Set<String> candidate_entities){
		
		
		/*for (String cand : candidate_entities){
			if (!umls_entities.contains(cand)){
				LogOutput.print(cand);
			}
		}*/
		
		Set <String> intersection;
		Set <String> difference;
		double precision;
		double recall;
		
		/*int i=0;
		for (String e : umls_entities){
			LogOutput.print(e);
			i++;
			if (i==20)
				break;
			
		}
		LogOutput.print("\n\n\n");
		
		i=0;
		for (String e : candidate_entities){
			LogOutput.print(e);
			i++;
			if (i==20)
				break;
			
		}*/
		
		
		
		intersection=new HashSet<String>(reference_entities);
		difference=new HashSet<String>(reference_entities);
		
		intersection.retainAll(candidate_entities);
		difference.removeAll(candidate_entities);
		
		
		precision=((double)intersection.size())/((double)candidate_entities.size());
		recall=((double)intersection.size())/((double)reference_entities.size());
	
		System.out.println("Difference: " + difference.size());
		for (String ent : difference){
			System.out.println("\t" + ent);
		}
		
		//LogOutput.print("\tPrecision: " + precision); Not important
		LogOutput.printAlways("\tRecall: " + recall);
		
		return recall;
		
	}
	
	
	/**
	 * UMLS mappings will be our gold standard.
	 * @throws Exception
	 */
	private static void loadMappingsTXT(String file_mappings, Set<String> entities1, Set<String> entities2) throws Exception{
	
		ReadFile reader = new ReadFile(file_mappings);
		
		
		String line;
		String[] elements;
		
		line=reader.readLine();
		
		while (line!=null) {
			
			if (line.indexOf("|")<0){
				line=reader.readLine();
				continue;
			}
			
			elements=line.split("\\|");
			
			entities1.add(elements[0]);			
			entities2.add(elements[1]);
			 
				
			line=reader.readLine();
		}		
		
		reader.closeBuffer();
		
		
		//LogOutput.print("Entities Ref Alignment 1: " + entities1.size());
		//LogOutput.print("Entities Ref Alignment 2: " + entities2.size());
				
		
	}
	
	
	

	/**
	 * UMLS mappings will be our gold standard.
	 * @throws Exception
	 */
	private static void loadMappingsRDF(String file_mappings, Set<String> entities1, Set<String> entities2) throws Exception{
	
		RDFAlignReader reader = new RDFAlignReader(file_mappings);
		
		for (MappingObjectStr mapping : reader.getMappingObjects()){
			
			entities1.add(mapping.getIRIStrEnt1());			
			entities2.add(mapping.getIRIStrEnt2());
			
		}
		
				
	}
	
	
	

	
	

	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		//long init, fin;
		
		String uri1;
		String uri2;
		
		String file_gs_mappings; 
		String file_logmapbio_mappings;
		String file_logmap2_mappings;
		
		
		Set<String> entities1_gs = new HashSet<String>();
		Set<String> entities2_gs = new HashSet<String>();
		
		Set<String> entities1_logmap = new HashSet<String>();
		Set<String> entities2_logmap = new HashSet<String>();
		
		Set<String> entities1_logmapbio = new HashSet<String>();
		Set<String> entities2_logmapbio = new HashSet<String>();
						
				
		
		
		int ontopair = 0;
				
		Parameters.readParameters();
		
		Parameters.print_output = false;
		Parameters.print_output_always = true;
		
		LogOutput.showOutpuLog(Parameters.print_output);
		LogOutput.showOutpuLogAlways(Parameters.print_output_always);
		
		Parameters.min_size_overlapping=0;
		boolean useExtendedLabels=true;
		
		String task;
		
		double recall1;
		double recall2;
		
		double recall1_logmapbio;
		double recall2_logmapbio;
		
		double recall1_logmap;
		double recall2_logmap;
		
		double overlappingratio1;
		double overlappingratio2;
		
		ontopair=Utilities.MOUSE2HUMAN;
		//ontopair=Utilities.FMA2NCI;		
		//ontopair=Utilities.FMA2SNOMED;
		//ontopair=Utilities.SNOMED2NCI;
		
					
		String path = "/home/ernesto/Documents/BackUp_Mar_20_2014/data/DataUMLS/UMLS_Onto_Versions/OAEI_datasets/oaei_2013/";
		String irirootpath = "file:" + path;
		
		
		if (ontopair==Utilities.FMA2NCI){
			
			uri1 = irirootpath + "oaei2013_FMA_whole_ontology.owl";
			uri2 = irirootpath + "oaei2013_NCI_whole_ontology.owl";

			task="FMA-NCI";
			
			
			file_gs_mappings = path + "oaei2013_FMA2NCI_repaired_UMLS_mappings.txt";				
			//file_logmap_mappings = "file:/home/ernesto/Documents/BackUp_Mar_20_2014/data/DataUMLS/UMLS_Onto_Versions/ISWC_LogMap0.9_Mappings/FMA2NCI_logmap_mappings.owl";
			file_logmapbio_mappings="/home/ernesto/Documents/OAEI_2016/EVAL_2016/MAPPINGS/LargeBio/LogMapBio-largebio-fma_nci_whole_2016.rdf";
			file_logmap2_mappings="/home/ernesto/Documents/OAEI_2016/EVAL_2016/MAPPINGS/LargeBio/LogMap-largebio-fma_nci_whole_2016.rdf";
				
																				
		}
		else if (ontopair==Utilities.FMA2SNOMED){
			
			uri1 = irirootpath + "oaei2013_FMA_whole_ontology.owl";
			//uri2 = irirootpath + "oaei2013_SNOMED_extended_overlapping_fma_nci.owl";			
			uri2 = "file:/home/ernesto/Documents/BackUp_Mar_20_2014/data/DataUMLS/UMLS_Onto_Versions/OAEI_datasets/snomed20090131_replab.owl";
			
			
			task="FMA-SNOMED";
			
			
			file_gs_mappings = path + "oaei2013_FMA2SNOMED_repaired_UMLS_mappings.txt";
				
			//file_logmap_mappings = "file:/home/ernesto/Documents/BackUp_Mar_20_2014/data/DataUMLS/UMLS_Onto_Versions/ISWC_LogMap0.9_Mappings/FMA2SNMD_logmap_mappings.owl";
			file_logmapbio_mappings="/home/ernesto/Documents/OAEI_2016/EVAL_2016/MAPPINGS/LargeBio/LogMapBio-largebio-fma_snomed_whole_2016.rdf";
			file_logmap2_mappings="/home/ernesto/Documents/OAEI_2016/EVAL_2016/MAPPINGS/LargeBio/LogMap-largebio-fma_snomed_whole_2016.rdf";
				
				
					
		}
		else if (ontopair==Utilities.SNOMED2NCI){
			
			uri1 = "file:/home/ernesto/Documents/BackUp_Mar_20_2014/data/DataUMLS/UMLS_Onto_Versions/OAEI_datasets/snomed20090131_replab.owl";
			//uri1 = irirootpath + "oaei2013_SNOMED_extended_overlapping_fma_nci.owl";
			uri2 = irirootpath + "oaei2013_NCI_whole_ontology.owl";

			task="SNOMED-NCI";
			
			file_gs_mappings = path + "oaei2013_SNOMED2NCI_repaired_UMLS_mappings.txt";
				
			//file_logmap_mappings = "file:/home/ernesto/Documents/BackUp_Mar_20_2014/data/DataUMLS/UMLS_Onto_Versions/ISWC_LogMap0.9_Mappings/SNMD2NCI_logmap_mappings.owl";
			file_logmapbio_mappings="/home/ernesto/Documents/OAEI_2016/EVAL_2016/MAPPINGS/LargeBio/LogMapBio-largebio-snomed_nci_whole_2016.rdf";
			file_logmap2_mappings="/home/ernesto/Documents/OAEI_2016/EVAL_2016/MAPPINGS/LargeBio/LogMap-largebio-snomed_nci_whole_2016.rdf";
			
			
		}
		else {// if (ontopair==Utilities.MOUSE2HUMAN){
			
			task="MOUSE";
			
			uri1= "file:/home/ernesto/Documents/BackUp_Mar_20_2014/data/DataUMLS/UMLS_Onto_Versions/Anatomy/2012/mouse2012.owl";
			uri2= "file:/home/ernesto/Documents/BackUp_Mar_20_2014/data/DataUMLS/UMLS_Onto_Versions/Anatomy/2012/human2012.owl";
								
			file_gs_mappings = "/home/ernesto/Documents/BackUp_Mar_20_2014/data/DataUMLS/UMLS_Onto_Versions/Anatomy/2012/reference2012.txt";
			
			file_logmapbio_mappings="/home/ernesto/Documents/OAEI_2016/EVAL_2016/MAPPINGS/LargeBio/LogMapBio-Anatomy.rdf";
			file_logmap2_mappings="/home/ernesto/Documents/OAEI_2016/EVAL_2016/MAPPINGS/LargeBio/LogMap-Anatomy.rdf";
			
			//file_logmap_mappings = "/home/ernesto/Documents/OAEI_OM_2015/EVAL_2015/MAPPINGS/Mouse_logmap2_Output";
			
			
			
		}
		
		
		
		
		
		try{
			//init = StatisticsTimeMappings.getCurrentTimeInMillis();
						
			LogOutput.printAlways("OVERLAPPING: ");
			long init = StatisticsTimeMappings.getCurrentTimeInMillis();			
			
			LexicalUtilities lexicalUtilities = new LexicalUtilities();
			LexicalOverlappingExtractor overlappingExtractor = new LexicalOverlappingExtractor(lexicalUtilities, useExtendedLabels);
			
			overlappingExtractor.createOverlapping(uri1, uri2);
			
					
			overlappingExtractor.getOverlappingOnto1();
			
			LogOutput.printAlways("Time extracting overlapping (s): " + StatisticsTimeMappings.getRunningTime(init));
			
			
			
			//Load GS mappings and baseline LogMap mappings
			loadMappingsTXT(file_gs_mappings, entities1_gs, entities2_gs);
			loadMappingsRDF(file_logmapbio_mappings, entities1_logmapbio, entities2_logmapbio);
			loadMappingsRDF(file_logmap2_mappings, entities1_logmap, entities2_logmap);
			
			
			Set<String> candidates_onto1 = new HashSet<String>();
			Set<String> candidates_onto2 = new HashSet<String>();
			
			for (OWLEntity cls : overlappingExtractor.getOverlappingOnto1().getSignature()){
				candidates_onto1.add(cls.getIRI().toString());
			}
			for (OWLEntity cls : overlappingExtractor.getOverlappingOnto2().getSignature()){
				candidates_onto2.add(cls.getIRI().toString());
			}
			
			LogOutput.printAlways("ONTOLOGY 1");
			LogOutput.printAlways("Recall GS mappings: " + entities1_gs.size() +" vs " + candidates_onto1.size());			
			recall1=getRecallOverlapping(entities1_gs, candidates_onto1);
			LogOutput.print("Recall LogMap mappings");
			recall1_logmap = getRecallOverlapping(entities1_logmap, candidates_onto1);
			LogOutput.print("Recall LogMap Bio mappings");
			recall1_logmapbio = getRecallOverlapping(entities1_logmapbio, candidates_onto1);
			
			
			overlappingratio1=(double)((double)overlappingExtractor.getOverlappingOnto1().getClassesInSignature().size()*100.0/(double)overlappingExtractor.getSizeClassesOnto1());
			LogOutput.printAlways("\tOverlapping size wrt whole ontology: " + overlappingratio1);
			
			LogOutput.printAlways("ONTOLOGY 2");
			LogOutput.print("Recall GS mappings: " + entities2_gs.size() +" vs " + candidates_onto2.size());
			
			recall2=getRecallOverlapping(entities2_gs, candidates_onto2);
			LogOutput.print("Recall LogMap mappings");
			recall2_logmap = getRecallOverlapping(entities2_logmap, candidates_onto2);
			LogOutput.print("Recall LogMap Bio mappings");
			recall2_logmapbio = getRecallOverlapping(entities2_logmapbio, candidates_onto2);
			

			overlappingratio2=(double)((double)overlappingExtractor.getOverlappingOnto2().getClassesInSignature().size()*100.0/(double)overlappingExtractor.getSizeClassesOnto2());
			LogOutput.printAlways("\tOverlapping size wrt whole ontology: " + overlappingratio2);
			
							
			
			LogOutput.printAlways(task +"\t" + useExtendedLabels + "\t" + StatisticsTimeMappings.getParsing_time() +"\t" + StatisticsTimeMappings.getOverlapping_time() +"\t" + Utilities.getRoundValue(overlappingratio1, 0) + "\t" + Utilities.getRoundValue(recall1,3) + "\t" + Utilities.getRoundValue(recall1_logmap,3) + "\t" + Utilities.getRoundValue(recall1_logmapbio,3)  + "\t" + Utilities.getRoundValue(overlappingratio2, 0) + "\t" + Utilities.getRoundValue(recall2, 3) + "\t" + Utilities.getRoundValue(recall2_logmap,3) + "\t" + Utilities.getRoundValue(recall2_logmapbio,3));
			
			
			
		}
		catch (Exception e){
			e.printStackTrace();
		}
		
	}
	
	
	
}

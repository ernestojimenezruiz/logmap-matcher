package uk.ac.ox.krr.logmap2.test.overlapping;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.io.IRIDocumentSource;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.MissingImportHandlingStrategy;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyLoaderConfiguration;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.parameters.Imports;

import uk.ac.ox.krr.logmap2.mappings.objects.MappingObjectStr;
import uk.ac.ox.krr.logmap2.oaei.reader.RDFAlignReader;
import uk.ac.ox.krr.logmap2.owlapi.SynchronizedOWLManager;
import uk.ac.ox.krr.logmap2.utilities.Utilities;

public class AbstractTestDivisionAlignmentTask {
	
	
	public static final int HP2MP2016=15;
	public static final int DOID2ORDO2016=16;
	
	public static final int HP2MP2017=17;
	public static final int DOID2ORDO2017=18;
	
	
	
	protected static String uri1;
	protected static String uri2;
	protected static String file_gs_rdf; //gold standard ground truct
	protected static String file_consensus_rdf; //alternative consensus
	
	
	//String path = "/home/ernesto/Documents/BackUp_Mar_20_2014/data/DataUMLS/UMLS_Onto_Versions/OAEI_datasets/oaei_2013/";
	protected static String path = "/home/ejimenez-ruiz/Documents/OAEI_Datasets/";
			
	protected static String irirootpath = "file:" + path;
			
	protected static String folder;
	
	
	
	protected static OWLOntology onto1;
	protected static OWLOntology onto2;
	
	
	protected static int size_onto1;
	protected static int size_onto2;
	
	
	
	//String output_path = "/home/ernesto/Documents/OAEI_2017.5/overlapping/tasks_advanced/";
	//String path_sizes = "/home/ernesto/Documents/OAEI_2017.5/overlapping/task_sizes_advanced/";
	
	protected static String base_output_path = "/home/ejimenez-ruiz/Documents/ATI_AIDA/DivisionMatchingTask/experiments-ijcai/";
	
	
	
	protected static void setMatchingCase(int ontopair) {
		
		if (ontopair==Utilities.FMA2NCI){
			
			uri1 = irirootpath + "largebio/" + "oaei2013_FMA_whole_ontology.owl";
			uri2 = irirootpath + "largebio/" + "oaei2013_NCI_whole_ontology.owl";

			//task="FMA-NCI";
			
			file_gs_rdf = path + "largebio/" + "reference_alignment/oaei2013_FMA2NCI_original_UMLS_mappings_with_confidence.rdf";
			
			
			//consensus 2
			//file_consensus_rdf = path + "largebio/" + "consensus-2018/Consensus-2-largebio-fma_nci.rdf";
			
			//consensus 3
			file_consensus_rdf = path + "largebio/" + "consensus-2018/Consensus-3-largebio-fma_nci.rdf";
			
			
			folder = "fma2nci/";
				
																				
		}
		else if (ontopair==Utilities.FMA2SNOMED){
			
			uri1 = irirootpath + "largebio/" + "oaei2013_FMA_whole_ontology.owl";
			//uri2 = irirootpath + "largebio/" + "snomed20090131_replab.owl";
			uri2 = irirootpath + "largebio/" + "oaei2013_SNOMED_extended_overlapping_fma_nci.owl";
			
			//"file:/home/ernesto/Documents/BackUp_Mar_20_2014/data/DataUMLS/UMLS_Onto_Versions/OAEI_datasets/snomed20090131_replab.owl";
			
			
			folder = "fma2snomed/";
			
			//task="FMA-SNOMED";
			
				
			file_gs_rdf = path + "largebio/" + "reference_alignment/oaei2013_FMA2SNOMED_original_UMLS_mappings_with_confidence.rdf";
			
			
			//consensus 2
			//file_consensus_rdf = path + "largebio/" + "consensus-2018/Consensus-2-largebio-fma_snomed.rdf";
			
			//consensus 3
			file_consensus_rdf = path + "largebio/" + "consensus-2018/Consensus-3-largebio-fma_snomed.rdf";
			
			
				
					
		}
		else if (ontopair==Utilities.SNOMED2NCI){
			
			uri1 = irirootpath + "largebio/" + "oaei2013_SNOMED_extended_overlapping_fma_nci.owl";
			//uri1 = irirootpath + "largebio/" + "snomed20090131_replab.owl";
			uri2 = irirootpath + "largebio/" + "oaei2013_NCI_whole_ontology.owl";

			//task="SNOMED-NCI";
			
			folder = "snomed2nci/";
			
			
			file_gs_rdf = path + "largebio/" + "reference_alignment/oaei2013_SNOMED2NCI_original_UMLS_mappings_with_confidence.rdf";
			
			
			//consensus 2
			//file_consensus_rdf = path + "largebio/" + "consensus-2018/Consensus-2-largebio-snomed_nci.rdf";
			
			//consensus 3
			file_consensus_rdf = path + "largebio/" + "consensus-2018/Consensus-3-largebio-snomed_nci.rdf";
				
			
		}
		else if (ontopair==Utilities.MOUSE2HUMAN){
			
			//task="MOUSE";
			
			folder = "mouse/";
			//file:/home/ernesto/Documents/BackUp_Mar_20_2014/data/DataUMLS/UMLS_Onto_Versions/Anatomy/2012/
			uri1= irirootpath + "mouse/" + "mouse2012.owl";
			uri2= irirootpath + "mouse/" + "human2012.owl";
								
			file_gs_rdf = path + "mouse/" + "/reference2012.rdf";
			
			//consensus 2
			//file_consensus_rdf = path + "mouse/" + "/Consensus-2-anatomy.rdf";
			
			//consensus 3
			file_consensus_rdf = path + "mouse/" + "/Consensus-3-anatomy.rdf";
			
			//consensus 4
			//file_consensus_rdf = path + "mouse/" + "/Consensus-4-anatomy.rdf";
			
			
			
				
		}
		
		else if (ontopair==HP2MP2016){
			
			folder = "hp2mp/";
			
			String path2 = path + "/phenotype/2016/";
			String iri_path2 = "file:"+ path2;
			
			uri1 = iri_path2 + "HP.rdf";
			uri2 = iri_path2 + "MP.rdf";
			
			//silver 2 (no gt available)
			file_gs_rdf = path2 + "Silver-hp-mp-2.rdf";
			//silver 3
			file_consensus_rdf = path2 + "Silver-hp-mp-3.rdf";
			
		}
		
		else  if (ontopair==DOID2ORDO2016) {
			
			folder = "doid2ordo/";
			
			String path2 = path + "/phenotype/2016/";
			String iri_path2 = "file:"+ path2;
			
			uri1 = iri_path2 + "DOID.rdf";
			uri2 = iri_path2 + "ORDO.rdf";
			
			//silver 2 (not gt available)
			file_gs_rdf = path2 + "Silver-doid-ordo-2.rdf";
			//silver 3
			file_consensus_rdf = path2 + "Silver-doid-ordo-3.rdf";
			
		}
		else if (ontopair==HP2MP2017){
			
			folder = "hp2mp2017/";
			
			String path2 = path + "/phenotype/2017/";
			String iri_path2 = "file:"+ path2;
			
			uri1 = iri_path2 + "hp_noimports.owl";
			uri2 = iri_path2 + "mp_noimports.owl";
			
			//silver 2
			file_gs_rdf = path2 + "selected/Silver-hp-mp-2.rdf";
			
		}
		
		else { //if (ontopair==DOID2ORDO2017) {
			
			folder = "doid2ordo2017/";
			
			String path2 = path + "/phenotype/2017/";
			String iri_path2 = "file:"+ path2;
			
			uri1 = iri_path2 + "doid_noimports.owl";
			uri2 = iri_path2 + "ordo.owl";
			
			//silver 2
			file_gs_rdf = path2 + "selected/Silver-doid-ordo-2.rdf";
			
		}
		
	}
	
	
	
	
	/*protected static Set<MappingObjectStr> loadMappingsRDF(String file_mappings) throws Exception{
		
		RDFAlignReader reader = new RDFAlignReader(file_mappings);
		
		return reader.getMappingObjects();
		
	}*/
	
	protected static Set<MappingObjectStr> loadMappingsRDF(String file_mappings) throws Exception{
		
		File file = new File(file_mappings);
		if (!file.exists()){
			System.err.println("Missing mappings file: " + file_mappings);
			return Collections.emptySet();
		}
			
		try{
			RDFAlignReader reader = new RDFAlignReader(file_mappings);
			
			return reader.getMappingObjects();
		}
		catch(Exception e){
			System.err.println("Error reading: " +  file_mappings);
			//return new HashSet<MappingObjectStr>();
			return Collections.emptySet();
		}
		
	}
	
	
	
	protected static OWLOntology loadOWLOntology(String phy_iri_onto) throws OWLOntologyCreationException{		

		try {
			
			OWLOntologyManager managerOnto;
			managerOnto = SynchronizedOWLManager.createOWLOntologyManager();			
			//managerOnto.setSilentMissingImportsHandling(true);	
			OWLOntologyLoaderConfiguration conf = new OWLOntologyLoaderConfiguration();
			conf.setMissingImportHandlingStrategy(MissingImportHandlingStrategy.SILENT);			
			return managerOnto.loadOntologyFromOntologyDocument(
					new IRIDocumentSource(IRI.create(phy_iri_onto)), conf);
			
						
		}
		catch(Exception e){
			System.err.println("Error loading OWL ontology: " + e.getMessage());
			//e.printStackTrace();
			throw new OWLOntologyCreationException();
		}
	}
	
	
	
	
	
	protected static void loadOntologies(int onto_pair) throws OWLOntologyCreationException {
		onto1 = loadOWLOntology(uri1);
		onto2 = loadOWLOntology(uri2);
		
		size_onto1 = onto1.getSignature(Imports.INCLUDED).size();
		size_onto2 = onto2.getSignature(Imports.INCLUDED).size();
		
		
		//For snomed cases we use size of full ontology
		int size_full_snomed = 306591;
		if (onto_pair==Utilities.FMA2SNOMED){
			size_onto2=size_full_snomed;
		}
		else if (onto_pair==Utilities.SNOMED2NCI){
			size_onto1=size_full_snomed;
		}
		
		
	}
	
	
	
	
	

}

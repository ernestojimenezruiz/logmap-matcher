package uk.ac.ox.krr.logmap2.test.oaei;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.formats.RDFXMLDocumentFormat;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.parameters.Imports;

import uk.ac.manchester.syntactic_locality.ModuleExtractor;
import uk.ac.ox.krr.logmap2.OntologyLoader;
import uk.ac.ox.krr.logmap2.owlapi.SynchronizedOWLManager;

/**
 * This class extracts locality modules from the fragments of the OAEI Bio-ML 2022 track.
 * THis way in the Bio-ML 2023 track self-contained ontologies will be used (i.e., modules)  
 * 
 * @author ernesto
 *
 */
public class CreateModulesForBioMLTrack {
	
		
	private OWLOntology createModule(OWLOntology onto, OWLOntology signature_onto) {
		
		return createModule(onto, signature_onto.getSignature(Imports.INCLUDED));
		
	}
	
	
	private OWLOntology createModule(OWLOntology onto, Set<OWLEntity> signature) {
		
		//In case the ontology does not have an URI
		String uri_onto = "http://oaei.ontologymatching.org/bio-ml/module-" + Calendar.getInstance().getTimeInMillis();
		if (onto.getOntologyID().getOntologyIRI().isPresent())
			uri_onto = onto.getOntologyID().getOntologyIRI().get().toString(); 
		
		ModuleExtractor module_extractor = new ModuleExtractor(
				onto.getAxioms(), SynchronizedOWLManager.createOWLOntologyManager(), false, false, true, true, false);
		
		return module_extractor.getLocalityModuleForSignatureGroup(
				signature, uri_onto, false);		
		
		//module_extractor1.clearStrutures();
		//signature.clear();
	}

	
	

	
	
	private void createModuleForBioMLTask(String uri_original_onto, String uri_fragment) throws OWLOntologyCreationException, OWLOntologyStorageException {
		
		OWLOntology module;
		
		OntologyLoader original_onto;
		OntologyLoader fragment_onto; //Bio-ML 2022
		
		
		original_onto = new OntologyLoader(uri_original_onto);
		fragment_onto = new OntologyLoader(uri_fragment);
		
		module = createModule(original_onto.getOWLOntology(), fragment_onto.getOWLOntology());
		
		
		//Get statistics: signature and logical axioms and annotations
		System.out.println("\n\n"+uri_fragment);
		System.out.println("Full ontology sizes (axioms, logical axioms, signature): " + original_onto.getOWLOntology().getAxiomCount()+"," + 
				original_onto.getOWLOntology().getLogicalAxiomCount()+","+ original_onto.getOWLOntology().getSignature().size());
		System.out.println("Original fragment sizes (axioms, logical axioms, signature): " + fragment_onto.getOWLOntology().getAxiomCount()+"," + 
				fragment_onto.getOWLOntology().getLogicalAxiomCount()+","+ fragment_onto.getOWLOntology().getSignature().size());
		System.out.println("NEW module sizes (axioms, logical axioms, signature): " + module.getAxiomCount()+"," + 
				module.getLogicalAxiomCount()+","+ module.getSignature().size());
		
		//Merge module M and fragment: not required in case it included axioms not in O
		//module.getOWLOntologyManager().addAxioms(module,fragment_onto.getOWLOntology().getAxioms(Imports.INCLUDED));
		//System.out.println("NEW module sizes - merged (axioms, logical axioms, signature): " + module.getAxiomCount()+"," + 
		//		module.getLogicalAxiomCount()+","+ module.getSignature().size());
		
		
		//Save module without annotations
		module.getOWLOntologyManager().saveOntology(
				module, new RDFXMLDocumentFormat(), IRI.create(uri_fragment.replace(".owl", "-module.owl")));

		
		
		//Add annotations for entities in M and not in fragment
		OWLDataFactory dataf = module.getOWLOntologyManager().getOWLDataFactory();
		String uri_annotation_prop = "http://oaei.ontologymatching.org/bio-ml/ann/use_in_alignment";
		Set<OWLAxiom> annotations = new HashSet<OWLAxiom>();
		
		Set<OWLEntity> new_entities = new HashSet<OWLEntity>();
		new_entities.addAll(module.getSignature());
		new_entities.removeAll(fragment_onto.getOWLOntology().getSignature());
		
		for (OWLEntity e : new_entities){
				
			OWLAnnotationProperty aprop = dataf.getOWLAnnotationProperty(
						IRI.create(uri_annotation_prop));
			OWLLiteral avalue = dataf.getOWLLiteral(false);
			OWLAnnotation annotation = dataf.getOWLAnnotation(aprop, avalue);
				
			OWLAnnotationAssertionAxiom ax = dataf.getOWLAnnotationAssertionAxiom(
								e.getIRI(), annotation);
			annotations.add(ax);
				
		}
		System.out.println("Added " + annotations.size() + " new annotations.");
		module.getOWLOntologyManager().addAxioms(module, annotations);
		
		
		
		//Save M with annotations 
		//Save module without annotations
		module.getOWLOntologyManager().saveOntology(
				module, new RDFXMLDocumentFormat(), IRI.create(uri_fragment.replace(".owl", "-module-annotations.owl")));

		
			
	}
	
	
	
	
	
	
	
	
	
	
	public static void main(String[] args) {
		
		CreateModulesForBioMLTrack modules_bioml = new CreateModulesForBioMLTrack();
		
		//MONDO
		String path_ontos_mondo = "file:/home/ernesto/Documents/OAEI-2023/Bio-ML/MONDO/raw_data/";
		String doid_mondo= "doid_v1.2.owl";
		String nci_mondo= "ncit_v18.05d.owl";  
		String omim_mondo="omim_mondo.owl";
		String ordo_mondo="ordo_v3.2.owl";
		
		//Fragments
		String path_mondo_fragments_equiv = "file:/home/ernesto/Documents/OAEI-2023/Bio-ML/MONDO/equiv_match/ontos/";
		String doid_m_e = "doid.owl";
		String ncit_m_e = "ncit.owl";
		String omim_m_e = "omim.owl";  
		String ordo_m_e = "ordo.owl";
		
		String path_mondo_fragments_subs = "file:/home/ernesto/Documents/OAEI-2023/Bio-ML/MONDO/subs_match/ontos/";
		//doid.subs.owl  ncit.owl  omim.owl  ordo.subs.owl
		String doid_m_s = "doid.subs.owl";
		String ncit_m_s = "ncit.owl";
		String omim_m_s = "omim.owl";  
		String ordo_m_s = "ordo.subs.owl";
		
		try {
			
			modules_bioml.createModuleForBioMLTask(path_ontos_mondo+doid_mondo, path_mondo_fragments_equiv+doid_m_e);
			modules_bioml.createModuleForBioMLTask(path_ontos_mondo+doid_mondo, path_mondo_fragments_subs+doid_m_s);
			
			modules_bioml.createModuleForBioMLTask(path_ontos_mondo+nci_mondo, path_mondo_fragments_equiv+ncit_m_e);
			modules_bioml.createModuleForBioMLTask(path_ontos_mondo+nci_mondo, path_mondo_fragments_subs+ncit_m_s);
			
			modules_bioml.createModuleForBioMLTask(path_ontos_mondo+omim_mondo, path_mondo_fragments_equiv+omim_m_e);
			modules_bioml.createModuleForBioMLTask(path_ontos_mondo+omim_mondo, path_mondo_fragments_subs+omim_m_s);
			
			modules_bioml.createModuleForBioMLTask(path_ontos_mondo+ordo_mondo, path_mondo_fragments_equiv+ordo_m_e);
			modules_bioml.createModuleForBioMLTask(path_ontos_mondo+ordo_mondo, path_mondo_fragments_subs+ordo_m_s);
			
			
		} catch (OWLOntologyCreationException | OWLOntologyStorageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		//UMLS
		String path_ontos_umls = "file:/home/ernesto/Documents/OAEI-2023/Bio-ML/UMLS/raw_data/";
		//fma_4.14.0.owl  ncit_21.02d.owl  snomed_US20210901.owl
		String fma_umls = "fma_4.14.0.owl"; 
		String nci_umls = "ncit_21.02d.owl";
		String snomed_umls = "snomed_US20210901.owl";
		
		//Fragments
		String path_umls_fragments_equiv = "file:/home/ernesto/Documents/OAEI-2023/Bio-ML/UMLS/equiv_match/ontos/";
		//fma.body.owl  ncit.neoplas.owl  ncit.pharm.owl  snomed.body.owl  snomed.neoplas.owl  snomed.pharm.owl
		String fma_body_u_e = "fma.body.owl";
		String nci_neoplas_u_e = "ncit.neoplas.owl";  
		String nci_pharm_u_e = "ncit.pharm.owl";  
		String snomed_body_u_e = "snomed.body.owl";  
		String snomed_neoplas_u_e = "snomed.neoplas.owl"; 
		String snomed_pharm_u_e = "snomed.pharm.owl";

		String path_umls_fragments_subs = "file:/home/ernesto/Documents/OAEI-2023/Bio-ML/UMLS/subs_match/ontos/";
		//fma.body.subs.owl  ncit.neoplas.subs.owl  ncit.pharm.subs.owl  snomed.body.owl  snomed.neoplas.owl  snomed.pharm.owl
		String fma_body_u_s = "fma.body.subs.owl";
		String nci_neoplas_u_s = "ncit.neoplas.subs.owl";  
		String nci_pharm_u_s = "ncit.pharm.subs.owl";  
		String snomed_body_u_s = "snomed.body.owl";  
		String snomed_neoplas_u_s = "snomed.neoplas.owl"; 
		String snomed_pharm_u_s = "snomed.pharm.owl";
		
		
		
		try {
			
			modules_bioml.createModuleForBioMLTask(path_ontos_umls+fma_umls, path_umls_fragments_equiv+fma_body_u_e);
			modules_bioml.createModuleForBioMLTask(path_ontos_umls+fma_umls, path_umls_fragments_subs+fma_body_u_s);
			//
			modules_bioml.createModuleForBioMLTask(path_ontos_umls+nci_umls, path_umls_fragments_equiv+nci_neoplas_u_e);
			modules_bioml.createModuleForBioMLTask(path_ontos_umls+nci_umls, path_umls_fragments_equiv+nci_pharm_u_e);
			modules_bioml.createModuleForBioMLTask(path_ontos_umls+nci_umls, path_umls_fragments_subs+nci_neoplas_u_s);
			modules_bioml.createModuleForBioMLTask(path_ontos_umls+nci_umls, path_umls_fragments_subs+nci_pharm_u_s);
			
			modules_bioml.createModuleForBioMLTask(path_ontos_umls+snomed_umls, path_umls_fragments_equiv+snomed_body_u_e);
			modules_bioml.createModuleForBioMLTask(path_ontos_umls+snomed_umls, path_umls_fragments_equiv+snomed_neoplas_u_e);
			modules_bioml.createModuleForBioMLTask(path_ontos_umls+snomed_umls, path_umls_fragments_equiv+snomed_pharm_u_e);
			modules_bioml.createModuleForBioMLTask(path_ontos_umls+snomed_umls, path_umls_fragments_subs+snomed_body_u_s);
			modules_bioml.createModuleForBioMLTask(path_ontos_umls+snomed_umls, path_umls_fragments_subs+snomed_neoplas_u_s);
			modules_bioml.createModuleForBioMLTask(path_ontos_umls+snomed_umls, path_umls_fragments_subs+snomed_pharm_u_s);
			
			
			
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OWLOntologyStorageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}

}

package uk.ac.manchester.syntactic_locality;

import java.net.URI;

import java.rmi.server.UID;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//import org.mindswap.pellet.utils.MultiValueMap;
import org.semanticweb.owlapi.io.RDFXMLOntologyFormat;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.OWLEntityCollector;

import org.semanticweb.owlapi.apibinding.OWLManager;

public class ModuleExtractor {

	private OWLEntityCollector axiomSignatureCollector;
	
	private Set<OWLAxiom> ontologyAxioms;
	
	private OWLOntologyManager ontologyManager;
	
	private OWLOntology module;
	
	private SyntacticLocalityChecker localityChecker;
	
	private boolean considerAnnotations=false;
	
	private boolean considerImportsClosure=true;
	
	private boolean ignoreAssertions=false;
	
	private Set<OWLEntity> collectedEntities;
	

	private OWLDataFactory dataFactory;
	
	/*
	 * Not used: this index does not speed up the execution
	 * @deprecated
	 */
	//private MultiValueMap<OWLEntity, OWLAxiom> entity2Axioms;
	//private boolean useIndexes=false;
	
	private Set<OWLAxiom> moduleAxioms;
	
	
	private Set<OWLEntity> moduleSignature=new HashSet<OWLEntity>();
	
	private List<OWLEntity> newMembers = new ArrayList<OWLEntity>();
	
	
	
	/**
	 * 
	 * @param ontology Ontology to be modularized (already locally loaded but not indexed)
	 * @param dualConcepts Type of interpretation for concepts outside the signature 
	 * @param dualRoles Type of interpretation for properties outside the signature
	 * @param considerImportsClosure Treats the imported ontologies as well
	 * @param considerEntityAnnotations Annotations are always local, but it could be interesting to maintain them in modules
	 * @param ignoreAssertions The module will only extract the corresponding part from the TBOX
	 */
	public ModuleExtractor(OWLOntology ontology, boolean dualConcepts, boolean dualRoles, 
			boolean considerImportsClosure, boolean considerEntityAnnotations, boolean ignoreAssertions){
		
		ontologyManager = OWLManager.createOWLOntologyManager();
		
		dataFactory = ontologyManager.getOWLDataFactory();
		
		//entity2Axioms = new MultiValueMap<OWLEntity, OWLAxiom>();
		ontologyAxioms=new HashSet<OWLAxiom>();
		moduleAxioms = new HashSet<OWLAxiom>();
		
		
		collectedEntities=new HashSet<OWLEntity>();
		axiomSignatureCollector = new OWLEntityCollector(collectedEntities);
		
		//From axioms we do not want to collect data types
		axiomSignatureCollector.setCollectDatatypes(false);
		
		
		//Load Ontology/ies
		//Annotations treatment
		considerAnnotations=considerEntityAnnotations;
		this.considerImportsClosure=considerImportsClosure;
		this.ignoreAssertions=ignoreAssertions;
		indexOntology(ontology);
		//System.out.println("Ontology Axioms: " + ontologyAxioms.size());
		
		
		
		//Set localityChecker
		setLocalityChecker(dualConcepts, dualRoles, considerEntityAnnotations, ignoreAssertions);
		
		
	}
	
	
	/**
	 * 
	 * @param ontoAxioms
	 * @param dualConcepts
	 * @param dualRoles
	 * @param considerImportsClosure
	 * @param considerEntityAnnotations
	 * @param ignoreAssertions
	 */
	public ModuleExtractor(Set<OWLAxiom> ontoAxioms, boolean dualConcepts, boolean dualRoles, 
			boolean considerImportsClosure, boolean considerEntityAnnotations, boolean ignoreAssertions){
		
		ontologyManager = OWLManager.createOWLOntologyManager();
		
		dataFactory = ontologyManager.getOWLDataFactory();
		
		
		ontologyAxioms=ontoAxioms;
		moduleAxioms = new HashSet<OWLAxiom>();
		
		
		collectedEntities=new HashSet<OWLEntity>();
		axiomSignatureCollector = new OWLEntityCollector(collectedEntities);
		
		//From axioms we do not want to collect data types
		axiomSignatureCollector.setCollectDatatypes(false);
		
		
		//Load Ontology/ies
		//Annotations treatment
		considerAnnotations=considerEntityAnnotations;
		this.considerImportsClosure=considerImportsClosure;
		this.ignoreAssertions=ignoreAssertions;
		
		//Set localityChecker
		setLocalityChecker(dualConcepts, dualRoles, considerEntityAnnotations, ignoreAssertions);
		
		
	}
	
	
	/**
	 * This method uses a synchronised OWLOntologyManager
	 * @param ontoAxioms
	 * @param dualConcepts
	 * @param dualRoles
	 * @param considerImportsClosure
	 * @param considerEntityAnnotations
	 * @param ignoreAssertions
	 */
	public ModuleExtractor(Set<OWLAxiom> ontoAxioms, OWLOntologyManager ontoManager, boolean dualConcepts, boolean dualRoles, 
			boolean considerImportsClosure, boolean considerEntityAnnotations, boolean ignoreAssertions){
		
		//ontologyManager = OWLManager.createOWLOntologyManager();
		
		ontologyManager = ontoManager;
		
		dataFactory = ontologyManager.getOWLDataFactory();
		
		
		ontologyAxioms=ontoAxioms;
		moduleAxioms = new HashSet<OWLAxiom>();
		
		
		collectedEntities=new HashSet<OWLEntity>();
		axiomSignatureCollector = new OWLEntityCollector(collectedEntities);
		
		//From axioms we do not want to collect data types
		axiomSignatureCollector.setCollectDatatypes(false);
		
		
		//Load Ontology/ies
		//Annotations treatment
		considerAnnotations=considerEntityAnnotations;
		this.considerImportsClosure=considerImportsClosure;
		this.ignoreAssertions=ignoreAssertions;
		
		//Set localityChecker
		setLocalityChecker(dualConcepts, dualRoles, considerEntityAnnotations, ignoreAssertions, ontologyManager.getOWLDataFactory());
		
		
	}
	
	
	
	/**
	 * Default values for considerAnnotations=false, useIndexes=true, considerImportsClosure=true.
	 * @param ontology Ontology to be modularized
	 * @param dualConcepts Type of interpretation for concepts outside the signature
	 * @param dualRoles Type of interpretation for properties outside the signature
	 */
	public ModuleExtractor(OWLOntology ontology, boolean dualConcepts, boolean dualRoles){
		
		ontologyManager = OWLManager.createOWLOntologyManager();
		//entity2Axioms = new MultiValueMap<OWLEntity, OWLAxiom>();
		ontologyAxioms=new HashSet<OWLAxiom>();
		moduleAxioms = new HashSet<OWLAxiom>();
		
		dataFactory = ontologyManager.getOWLDataFactory();
		
		collectedEntities=new HashSet<OWLEntity>();
		axiomSignatureCollector = new OWLEntityCollector(collectedEntities);
		
		//From axioms we do not want to collect data types
		axiomSignatureCollector.setCollectDatatypes(false);
		
		//Load Ontology/ies
		indexOntology(ontology);
		//System.out.println("Ontology Axioms: " + ontologyAxioms.size());
		
		//Set localityChecker
		setLocalityChecker(dualConcepts, dualRoles, false, true);
		
	}
	
	
	
	private void indexOntology(OWLOntology ontology){
		//We consider annotationes
		
				
		
		if (considerImportsClosure) {
			try {
				indexImportedOntologies(ontologyManager.getImportsClosure(ontology));
			}
			catch(Exception e){
			
				//We do not consider direct closure
				for (OWLAxiom ax : ontology.getAxioms()) {
					if (considerAnnotations || !(ax instanceof OWLAnnotationAxiom))
						ontologyAxioms.add(ax);
				}
				
			}
			//indexImportedOntologies(ontologyManager.getDirectImports(ontology));
		}
		else {
			for (OWLAxiom ax : ontology.getAxioms()) {
				if (considerAnnotations || !(ax instanceof OWLAnnotationAxiom))
					ontologyAxioms.add(ax);
			}
			//ontologyAxioms.addAll(ontology.getAxioms());
		}
		
		
		System.out.println("Ontology Axioms: " + ontologyAxioms.size());
		
		
	}
	
	
	private void indexImportedOntologies(Set<OWLOntology> ontologies) {
		for(OWLOntology ontology : ontologies) { 
			//if (useIndexes)
			//	loadIndexedOntology(ontology);
			//else
			for (OWLAxiom ax : ontology.getAxioms()) {
				if (considerAnnotations || !(ax instanceof OWLAnnotationAxiom))
					ontologyAxioms.add(ax);
			}
		}
	}

	
	/*
	 * @deprecated
	 * 
	 *
	private void loadIndexedOntology(OWLOntology ontology) {
		for (OWLAxiom ax : ontology.getAxioms()) {
			if (considerAnnotations || !(ax instanceof OWLAnnotationAxiom)) {
				for (OWLEntity ent : getAxiomSignature(ax)){
					entity2Axioms.add(ent, ax);
				}
			}
		}
	}*/
	
	
	

	
	private void setLocalityChecker(boolean dualConcepts, boolean dualRoles, boolean considerEntityAnnotations, boolean ignoreAssertions, OWLDataFactory dataFactory) {
		
		localityChecker = new SyntacticLocalityChecker(dualConcepts, dualRoles, considerEntityAnnotations, ignoreAssertions, dataFactory);
		
	    
	}
	
	
	private void setLocalityChecker(boolean dualConcepts, boolean dualRoles, boolean considerEntityAnnotations, boolean ignoreAssertions) {
		
		localityChecker = new SyntacticLocalityChecker(dualConcepts, dualRoles, considerEntityAnnotations, ignoreAssertions);
		
	    
	}
	
	
	
	
	private Set<OWLEntity> getAxiomSignature(OWLAxiom ax) {
		
		collectedEntities.clear();
		
		axiomSignatureCollector.reset(collectedEntities);
		
		ax.accept(axiomSignatureCollector);

		//System.out.println("Ax: " + ax);
		//System.out.println("\tEntities: " + collectedEntities);
		
		//return axiomSignatureCollector.getObjects();
		return collectedEntities;
	}
	
	
	/*
	 * 
	 * @param ent
	 * @return
	 * @deprecated
	 *
	private Set<OWLAxiom> getEntityAxioms(OWLEntity ent) {
		
		if (entity2Axioms.containsKey(ent))
			return entity2Axioms.get(ent);
		else
			return Collections.emptySet();
	}*/
	
	
	
	
	public OWLOntology getLocalityModuleForSignatureGroup(Set<OWLEntity> signature){
		
		return getLocalityModuleForSignatureGroup(signature, "http://krono.ac.uji.es/Links/ontologies/module" + new UID() + ".owl");
		
	}
	
	
	
	public OWLOntology getLocalityModuleForSignatureGroup(Set<OWLEntity> signature, String moduleURIStr){
		return getLocalityModuleForSignatureGroup(signature, moduleURIStr, true);
	}
	
	public OWLOntology getLocalityModuleForSignatureGroup(Set<OWLEntity> signature, String moduleURIStr, boolean reInitManager){
		
		long init, fin;
		init=Calendar.getInstance().getTimeInMillis();
		
		moduleAxioms.clear();
		
		IRI moduleIRI = IRI.create(moduleURIStr.replace("\\", "//"));
		
		//Creates moduleSignature and moduleaxioms
		//if (useIndexes)
		//	extractModuleAxiomsForGroupSignatureIndex(signature); 
		//else
		extractModuleAxiomsForGroupSignature(signature);
		
		fin=Calendar.getInstance().getTimeInMillis();
		//System.out.println("Time extract module axioms (" + moduleAxioms.size() + " axioms) (s): "  + (double)((double)fin-(double)init)/1000.0);
		
		return getModuleFromAxioms(moduleAxioms, moduleIRI, reInitManager);
		
	}
	
	
	
	
	
	
	
	public OWLOntologyManager getOntoModuleManager(){
		return ontologyManager;
	}
	
	public OWLOntology getModuleFromAxioms(Set<OWLAxiom> moduleAxioms, IRI moduleIri) {
		return getModuleFromAxioms(moduleAxioms, moduleIri, true);
	}
	
	public OWLOntology getModuleFromAxioms(Set<OWLAxiom> moduleAxioms, IRI moduleIri, boolean reInitManager) {
		
		//we need to reinit manager (when many calls to extract modules)
		if (reInitManager)
			ontologyManager = OWLManager.createOWLOntologyManager();
		
		long init, fin;
		init=Calendar.getInstance().getTimeInMillis();
		
		//OWLOntology module=null;
		
		try {
			module = ontologyManager.createOntology(moduleIri);
			List<OWLOntologyChange> ontoChanges = new ArrayList<OWLOntologyChange>();
			for(OWLAxiom axiom : moduleAxioms) {
				ontoChanges.add(new AddAxiom(module, axiom));
			}
			ontologyManager.applyChanges(ontoChanges);
		}
		
		catch(Exception e) {
			System.out.println("Error creating module ontology from axioms.");
			
		}

		
		fin=Calendar.getInstance().getTimeInMillis();
		//System.out.println("Time create OWLOntology for module (s): " + (double)((double)fin-(double)init)/1000.0);
		
		return module;
	}
	
	
	
	
	
	
	/*
	 * Extract the common (upper/lower) Module for a set of entities passed as a signature
	 * You **Do NOT*** need firstly to call extracModules()
	 * Using index structure and considering only axioms that involve entities form signature
	 * This method is slower
	 * Author Ernesto 
	 * @deprecated
	 * 
	 
	private Set<OWLAxiom> extractModuleAxiomsForGroupSignatureIndex(Set<OWLEntity> signature) {
		
		//The signature to be used for the locality checking will be the one of the provisional module
		//At least a module will contain the entities from the signature
		moduleSignature = signature;
		//moduleSignature.addAll(signature); //Current module signature
		
		int previousSize = 0;  //ModuleSignature size is 0 we don't extract any element 
		//(That's correct??) May be yoy can get non-local axiom for the empty signature, but actually
		//you are interested in a module related to your signature 

		
		while(moduleSignature.size() != previousSize) {
			previousSize = moduleSignature.size();

			for (OWLEntity ent : moduleSignature) {
				//Axioms involving only elements from signature
				//Should we consider all axioms???
				for (OWLAxiom ax : getEntityAxioms(ent)) { 
					
					//We consider annotationes --> NOw in locality checker
					//if (considerAnnotations && (ax instanceof OWLAnnotationAxiom) && !moduleAxioms.contains(ax) )
					//	moduleAxioms.add(ax);
					
					//We don't want to consider it again
					if (!moduleAxioms.contains(ax) && !localityChecker.isLocalAxiom(ax, moduleSignature)) {
						moduleAxioms.add(ax);
						
						for (OWLEntity newEnt : getAxiomSignature(ax)) {
							if(!moduleSignature.contains(newEnt))
								newMembers.add(newEnt); //We don't want to modify module within the iterations
						}
					}
				}
			}
				
			moduleSignature.addAll(newMembers);
			
			newMembers.clear();
			
		}
		
		return moduleAxioms;
		
	}*/
	
	
	
	/**
	 * Extract the common (upper/lower) Module for a set of entities passed as a signature
	 * You **Do NOT*** need firstly to call extracModules()
	 * Author Ernesto 
	 * 
	 */
	public Set<OWLAxiom> extractModuleAxiomsForGroupSignature(Set<OWLEntity> signature) {
		
		//The signature to be used for the locality checking will be the one of the provisional module
		//At least a module will contain the entities from the signature
		moduleSignature = signature;
		moduleAxioms.clear();
		//moduleSignature.addAll(signature); //Current module signature
		

		int previousSize = 0;  //ModuleSignature size is 0 we don't extract any element 
		//(That's correct??) May be yoy can get non-local axiom for the empty signature, but actually
		//you are interested in a module related to your signature 
		
		

		//To ensure that entities in signature will belong to the module
		//Declaration axiom are non local
		for (OWLEntity ent : moduleSignature) {
			moduleAxioms.add(dataFactory.getOWLDeclarationAxiom(ent));
		}
		
		
		
		while(moduleSignature.size() != previousSize) {
			previousSize = moduleSignature.size();

			for (OWLAxiom ax : ontologyAxioms) { 
				
				//We consider annotationes -->> NOw in locality checker
				//if (considerAnnotations && (ax instanceof OWLAnnotationAxiom) && !moduleAxioms.contains(ax) )
				//	moduleAxioms.add(ax);
				
				if (!moduleAxioms.contains(ax) && !localityChecker.isLocalAxiom(ax, moduleSignature)) { //We don't want to consider it again
					moduleAxioms.add(ax);
						
					for (OWLEntity newEnt : getAxiomSignature(ax)) {
						if(!moduleSignature.contains(newEnt) && !newEnt.equals(Thing())) //No Thing class within signature
							newMembers.add(newEnt); //We don't want to modify module within the iterations
					}
				}
			}
			
			
			
			moduleSignature.addAll(newMembers);
			//moduleMembers.addAll(newMembers);
			newMembers.clear();
		}
		
		
		
		return moduleAxioms;
		
	}
	
	
	
	
	
	/**
	 * Extracts only the module entities  for a set of entities passed as a signature.
	 * This method is used when the user is only intereseted in the entrities and not in the axioms  
	 * You **Do NOT*** need firstly to call extracModules()
	 * Author Ernesto 
	 * 
	 */
	public Set<OWLEntity> extractModuleEntitiesForGroupSignature(Set<OWLEntity> signature) {
		
		//The signature to be used for the locality checking will be the one of the provisional module
		//At least a module will contain the entities from the signature
		moduleSignature = signature;
		//moduleSignature.addAll(signature); //Current module signature
		

		int previousSize = 0;  //ModuleSignature size is 0 we don't extract any element 
		//(That's correct??) May be yoy can get non-local axiom for the empty signature, but actually
		//you are interested in a module related to your signature 
						
		
		while(moduleSignature.size() != previousSize) {
			previousSize = moduleSignature.size();

			for (OWLAxiom ax : ontologyAxioms) { 
				
				
				
				if (!localityChecker.isLocalAxiom(ax, moduleSignature)) { //We don't want to consider it again
					
					for (OWLEntity newEnt : getAxiomSignature(ax)) {
						if(!moduleSignature.contains(newEnt) && !newEnt.equals(Thing())) //No Thing class within signature
							newMembers.add(newEnt); //We don't want to modify module within the iterations
					}
				}
			}
			
			
			
			moduleSignature.addAll(newMembers);
			newMembers.clear();
		}
		
		
		
		return moduleSignature;
		
	}
	
	
	
	/**
	 * Extracts only the module entities  for an entity passed as a signature.
	 * This method is used when the user is only intereseted in the entrities and not in the axioms  
	 * You **Do NOT*** need to call extracModules() first
	 * Author Ernesto 
	 * 
	 */
	public Set<OWLEntity> extractModuleEntitiesForEntity(OWLEntity entity) {
		
		//The signature to be used for the locality checking will be the one of the provisional module
		//At least a module will contain the entities from the signature
		moduleSignature.clear();
		moduleSignature.add(entity);
		//moduleSignature.addAll(signature); //Current module signature
		

		int previousSize = 0;  //ModuleSignature size is 0 we don't extract any element 
		//(That's correct??) May be yoy can get non-local axiom for the empty signature, but actually
		//you are interested in a module related to your signature 
						
		
		while(moduleSignature.size() != previousSize) {
			previousSize = moduleSignature.size();

			for (OWLAxiom ax : ontologyAxioms) { 
				
				
				
				if (!localityChecker.isLocalAxiom(ax, moduleSignature)) { //We don't want to consider it again
					
					for (OWLEntity newEnt : getAxiomSignature(ax)) {
						if(!moduleSignature.contains(newEnt) && !newEnt.equals(Thing())) //No Thing class within signature
							newMembers.add(newEnt); //We don't want to modify module within the iterations
					}
				}
			}
			
			
			
			moduleSignature.addAll(newMembers);
			newMembers.clear();
		}
		
		
		
		return moduleSignature;
		
	}
	
	
	
	
	
	
	/**
	 * Extract the common (upper/lower) Module for an entity passed as a signature
	 * You **Do NOT*** need to call extracModules() first
	 * Author Ernesto 
	 * 
	 */
	public Set<OWLAxiom> extractModuleAxiomsForEntity(OWLEntity entity) {
		
		//The signature to be used for the locality checking will be the one of the provisional module
		//At least a module will contain the entities from the signature
		moduleSignature.clear();
		moduleAxioms.clear();
		
		moduleSignature.add(entity);
		//moduleSignature.addAll(signature); //Current module signature
		

		int previousSize = 0;  //ModuleSignature size is 0 we don't extract any element 
		//(That's correct??) May be yoy can get non-local axiom for the empty signature, but actually
		//you are interested in a module related to your signature 
		
		
		

		//To ensure that entities in signature will belong to the module
		//Declaration axiom are non local
		for (OWLEntity ent : moduleSignature) {
			moduleAxioms.add(dataFactory.getOWLDeclarationAxiom(ent));
		}
		
		
		
		while(moduleSignature.size() != previousSize) {
			previousSize = moduleSignature.size();

			for (OWLAxiom ax : ontologyAxioms) { 
				
				//We consider annotationes -->> NOw in locality checker
				//if (considerAnnotations && (ax instanceof OWLAnnotationAxiom) && !moduleAxioms.contains(ax) )
				//	moduleAxioms.add(ax);
				
				if (!moduleAxioms.contains(ax) && !localityChecker.isLocalAxiom(ax, moduleSignature)) { //We don't want to consider it again
					moduleAxioms.add(ax);
						
					for (OWLEntity newEnt : getAxiomSignature(ax)) {
						if(!moduleSignature.contains(newEnt) && !newEnt.equals(Thing())) //No Thing class within signature
							newMembers.add(newEnt); //We don't want to modify module within the iterations
					}
				}
			}
			
			
			
			moduleSignature.addAll(newMembers);
			//moduleMembers.addAll(newMembers);
			newMembers.clear();
		}
		
		
		
		return moduleAxioms;
		
	}
	
	 public void saveExtractedModule(String physicalModuleURI) {
	    	//OWLOntologyManager ontologyModuleManager = OWLManager.createOWLOntologyManager();
	    	
	        try {
	        	ontologyManager.saveOntology(module, new RDFXMLOntologyFormat(), IRI.create(physicalModuleURI));
	        }
	        catch (Exception e) {
	        	System.err.println("Error saving module\n" + e.getLocalizedMessage());
	        	e.printStackTrace();
	        }
	    }
	
	
	
	
	
	
	public void clearStrutures(){

		collectedEntities.clear();
		ontologyAxioms.clear();

		moduleAxioms.clear();
		
		moduleSignature.clear();
		
		newMembers.clear();
		
		//ontologyManager.removeOntology(module);
		//module=null;
		//ontologyManager=null;
		
	}
	
	
	private OWLClass Thing() {
        return ontologyManager.getOWLDataFactory().getOWLThing();
	}
	
	
	
	
	
	
}
package uk.ac.manchester.syntactic_locality;

import java.util.ArrayList;



import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Map;

import org.semanticweb.owlapi.formats.RDFXMLDocumentFormat;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
//import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.modularity.OntologySegmenter;

import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.parameters.Imports;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.search.EntitySearcher;

import uk.ac.ox.krr.logmap2.owlapi.SynchronizedOWLManager;

//import uk.ac.manchester.cs.owlapi.modularity.ModuleType;
//import uk.ac.manchester.cs.owlapi.modularity.SyntacticLocalityModuleExtractor;

/**
 * 
 * This class implements locality modules: bottom, top and star
 * 
 * @author Ernesto
 *
 */
public class OntologyModuleExtractor implements OntologySegmenter {

	
	private OWLOntologyManager ontologyManager;
	private OWLDataFactory dataFactory;
	
	private Set<OWLAxiom> ontologyAxioms;// = new HashSet<OWLAxiom>();
	
	private Map<OWLEntity, Set<OWLAxiom>> entity2Axioms = new HashMap<OWLEntity, Set<OWLAxiom>>();
			
	private SyntacticLocalityChecker localityChecker;
	
	private boolean considerAnnotations=false;
			
	private boolean ignoreAssertions=false;
		
		
	private Set<OWLAxiom> moduleAxioms = new HashSet<OWLAxiom>();
	
	
	private Set<OWLEntity> moduleSignature=new HashSet<OWLEntity>();
	
	private Set<OWLEntity> inputSignature=new HashSet<OWLEntity>();
	
	
	private List<OWLEntity> newMembers = new ArrayList<OWLEntity>();
	
	private boolean useOptimization;
	
	private boolean includeDeclarationAxioms=false;
	
	
	public static enum TYPEMODULE {
			BOTTOM_LOCALITY, TOP_LOCALITY, BOTTOM_TOP_LOCALITY, STAR
	}
	
	private TYPEMODULE typeModule = TYPEMODULE.BOTTOM_LOCALITY;
	
	
	/**
	 * Default constructor. 
	 * @param ontology
	 */
	public OntologyModuleExtractor(OWLOntology ontology){
		
		this(ontology, true, false, false, false);		
		
	}
	
	
	/**
	 * 
	 * @param ontology OWLOntology to be modularised
	 * @param considerImportsClosure Treats the imported ontologies as well
	 * @param considerEntityAnnotations Annotations are always local, but it could be interesting to maintain them in modules
	 * @param ignoreAssertions The module will only extract the corresponding part from the TBOX
	 */
	public OntologyModuleExtractor(
			OWLOntology ontology, 
			boolean considerImportsClosure, 
			boolean considerEntityAnnotations, 
			boolean ignoreAssertions,
			boolean useOptimization){
		
		this(getAxioms(
				ontology, 
				considerImportsClosure, 
				considerEntityAnnotations, 
				ignoreAssertions), 
				considerEntityAnnotations, 
				ignoreAssertions,
				useOptimization);
		
	}
	

	
	/**
	 * Default constructor. 
	 * @param ontoAxioms
	 */
	public OntologyModuleExtractor(Set<OWLAxiom> ontoAxioms){
		
		this(ontoAxioms, false, false, false);
		
	}
	
	
	
	/**
	 * Constructor like OWL API one (uk.ac.manchester.cs.owlapi.modularity.SyntacticLocalityModuleExtractor)
	 * @param manager
	 * @param ontology
	 */
	public OntologyModuleExtractor(OWLOntologyManager manager, OWLOntology ontology, TYPEMODULE typeModule){
		
		ontologyManager = manager;		
		ontologyAxioms=ontology.getAxioms();
		
		this.typeModule = typeModule;
		
		this.considerAnnotations=false;
		this.ignoreAssertions=false;
		
		this.useOptimization=true;
		
		indexEntities();
		
	}
	
	
	/**
	 * Main constructor
	 * @param ontoAxioms
	 * @param considerEntityAnnotations
	 * @param ignoreAssertions
	 * @param useOptimization
	 */
	public OntologyModuleExtractor(
			Set<OWLAxiom> ontoAxioms, 
			boolean considerEntityAnnotations,	
			boolean ignoreAssertions,
			boolean useOptimization){
		
		//ontologyAxioms.addAll(ontoAxioms);
		ontologyAxioms=ontoAxioms;
		
		this.considerAnnotations=considerEntityAnnotations;
		this.ignoreAssertions=ignoreAssertions;
		
				
		//System.out.println("Ontology Axioms to modularize: " + ontologyAxioms.size());
		
		this.useOptimization=useOptimization;
		
		if (useOptimization)
			indexEntities();
		
	}
	
	/**
	 * Main constructor with manager 
	 * @param manager
	 * @param ontoAxioms
	 * @param considerEntityAnnotations
	 * @param ignoreAssertions
	 * @param useOptimization
	 */
	public OntologyModuleExtractor(
			OWLOntologyManager manager,
			Set<OWLAxiom> ontoAxioms, 
			boolean considerEntityAnnotations,	
			boolean ignoreAssertions,
			boolean useOptimization){
		
		ontologyManager = manager;
		
		//ontologyAxioms.addAll(ontoAxioms);
		ontologyAxioms=ontoAxioms;
		
		this.considerAnnotations=considerEntityAnnotations;
		this.ignoreAssertions=ignoreAssertions;
		
				
		//System.out.println("Ontology Axioms to modularize: " + ontologyAxioms.size());
		
		this.useOptimization=useOptimization;
		
		if (useOptimization)
			indexEntities();
		
	}
	
	
	public void clearStrutures(){

		ontologyAxioms.clear();

		moduleAxioms.clear();
		
		moduleSignature.clear();
		
		newMembers.clear();
		
		entity2Axioms.clear();				
			
		inputSignature.clear();
		
	}
	
	
	private void indexEntities() {
		
		for (OWLAxiom ax : ontologyAxioms) {
			for (OWLEntity ent : ax.getSignature()){
				if (!entity2Axioms.containsKey(ent)){
					entity2Axioms.put(ent, new HashSet<OWLAxiom>());
				}
				entity2Axioms.get(ent).add(ax);
			}
		}
	}
	
	
	/**
	 * Returns module axioms. The module should have been previously extracted.
	 * @return
	 */
	public Set<OWLAxiom> getModuleAxioms(){
		return moduleAxioms;
	}
	
	
	/**
	 * Returns module entities. The module should have been previously extracted.
	 * @return
	 */
	public Set<OWLEntity> getModuleEntities(){
		return moduleSignature;
	}
	
	
	/**
	 * Return the OWLOntology correspondent to the module. The module should have been previously extracted.
	 * A default uri is assigned
	 * @return
	 * @throws OWLOntologyCreationException
	 */
	public OWLOntology getModuleOntology() throws OWLOntologyCreationException {
		return getModuleOntology("http://krono.act.uji.es/ontologies/module.owl");
	}
	
	
	/**
	 * Return the OWLOntology correspondent to the module. The module should have been previously extracted.
	 * @param iri_str
	 * @return
	 * @throws OWLOntologyCreationException
	 */
	public OWLOntology getModuleOntology(String iri_str) throws OWLOntologyCreationException {
		return ontologyManager.createOntology(moduleAxioms, IRI.create(iri_str));
	}
	
	
	
	/*private void setOntologyAxioms(Set<OWLAxiom> newOntologyAxioms){
		
		ontologyAxioms.clear();
		ontologyAxioms.addAll(newOntologyAxioms);
		
	}*/
	
	private Set<OWLAxiom> getOntologyAxioms(){
		return ontologyAxioms;
	}
	
			
	/**
	 * Get axioms from ontology depending on requirements
	 * @param ontology
	 * @param considerImportsClosure
	 * @param considerEntityAnnotations
	 * @param ignoreAssertions
	 * @return
	 */
	private static Set<OWLAxiom> getAxioms(
			OWLOntology ontology,
			boolean considerImportsClosure, 
			boolean considerEntityAnnotations, 
			boolean ignoreAssertions){
	
		Set<OWLAxiom> axioms = new HashSet<OWLAxiom>(); 
		
		Imports imports;
		
		if (considerImportsClosure)
			imports = Imports.INCLUDED;
		else	
			imports = Imports.EXCLUDED;
				
		
		//axioms.addAll(ontology.getGeneralClassAxioms());
		axioms.addAll(ontology.getTBoxAxioms(imports));
		axioms.addAll(ontology.getRBoxAxioms(imports));
		if (!ignoreAssertions){
			axioms.addAll(ontology.getABoxAxioms(imports));
		}
		
		if (considerEntityAnnotations){
			
			for (OWLClass cls : ontology.getClassesInSignature(imports)){
				axioms.addAll(EntitySearcher.getAnnotationAssertionAxioms(cls, ontology));
				//axioms.addAll(ontology.getDeclarationAxioms(cls));
				
			}
			
			for (OWLObjectProperty oprop : ontology.getObjectPropertiesInSignature(imports)){
				axioms.addAll(EntitySearcher.getAnnotationAssertionAxioms(oprop, ontology));
				//axioms.addAll(ontology.getDeclarationAxioms(oprop));
			}
			
			for (OWLDataProperty dprop : ontology.getDataPropertiesInSignature(imports)){
				axioms.addAll(EntitySearcher.getAnnotationAssertionAxioms(dprop, ontology));
				//axioms.addAll(ontology.getDeclarationAxioms(dprop));
			}
			
			for (OWLAnnotationProperty aprop : ontology.getAnnotationPropertiesInSignature()){
				axioms.addAll(EntitySearcher.getAnnotationAssertionAxioms(aprop, ontology));
				//axioms.addAll(ontology.getDeclarationAxioms(aprop));
			}
			
			if (!ignoreAssertions){
				for (OWLNamedIndividual indiv : ontology.getIndividualsInSignature(imports)){
					axioms.addAll(EntitySearcher.getAnnotationAssertionAxioms(indiv, ontology));
					//axioms.addAll(ontology.getDeclarationAxioms(indiv));
				}
				
				//TODO In pizza.owl gives an error
				//for (OWLAnonymousIndividual aindiv : ontology.getAnonymousIndividuals()){
				//	axioms.addAll(ontology.getAnnotationAssertionAxioms(aindiv));
				//}
				
				
				
			}
		}	
		
		/*for (OWLAxiom ax : ontology.getAxioms()){
			if (!axioms.contains(ax)){
				
				System.out.println(ax);
				
			}
		}*/
			
		
		return axioms;
		
	}
	
	
	/**
	 * Interface from OWLAPI OntologySegmenter. We extract by the type of module defined in constructor or Bottom module by default
	 */
	public Set<OWLAxiom> extract(Set<OWLEntity> signature) {
		
		if (ontologyManager!=null)
			return extractModule4Entities(signature, typeModule, ontologyManager);
		else
			return extractModule4Entities(signature, typeModule);
	}


	/**
	 * Not implemented
	 */
	public Set<OWLAxiom> extract(Set<OWLEntity> arg0, int arg1, int arg2,
			OWLReasoner arg3) {
		// TODO Auto-generated method stub
		return null;
	}


	/**
	 *  
	 */
	public OWLOntology extractAsOntology(OWLOntologyManager manager, Set<OWLEntity> signature, IRI iri, TYPEMODULE typeModule)
			throws OWLOntologyCreationException {
		
		return manager.createOntology(extractModule4Entities(signature, typeModule, manager), iri);				
	}
	
	/**
	 *  
	 */
	public OWLOntology extractAsOntology(OWLOntologyManager manager, Set<OWLEntity> signature, IRI iri)
			throws OWLOntologyCreationException {
		
		return manager.createOntology(extractModule4Entities(signature, TYPEMODULE.BOTTOM_LOCALITY, manager), iri);				
	}
	
	
	
	/**
	 *  Interface from OWLAPI OntologySegmenter. 
	 *  We extract by the type of module defined in constructor or Bottom module by default
	 */
	public OWLOntology extractAsOntology(Set<OWLEntity> signature, IRI iri)
			throws OWLOntologyCreationException {

		OWLOntologyManager manager;
		if (ontologyManager!=null)
			manager=ontologyManager;
		else 
			manager = SynchronizedOWLManager.createOWLOntologyManager();
		
		return manager.createOntology(extractModule4Entities(signature, typeModule, manager), iri);				
	}


	/**
	 * Not implemented
	 */
	public OWLOntology extractAsOntology(Set<OWLEntity> signature, IRI arg1,
			int arg2, int arg3, OWLReasoner arg4)
			throws OWLOntologyCreationException {
		// TODO Auto-generated method stub
		//Not Implemented		
		return null;
	}
	
	
	
	
	
	public Set<OWLAxiom> extractModule4Entity(OWLEntity entity, TYPEMODULE typeModule) {
		return extractModule4Entity(entity, typeModule, SynchronizedOWLManager.createOWLOntologyManager());
	}
	
	public Set<OWLAxiom> extractModule4Entity(OWLEntity entity, TYPEMODULE typeModule, OWLOntologyManager ontologyManager) {
		
		//init manager
		this.ontologyManager = ontologyManager;		
		dataFactory = this.ontologyManager.getOWLDataFactory();
		
		//Input signature	
		inputSignature.clear();
		inputSignature.add(entity);
		
		
		
		extractModule(typeModule);
		
		
		return moduleAxioms;
		
	}
	
	
	
	public Set<OWLAxiom> extractModule4Entities(Set<OWLEntity> signature, TYPEMODULE typeModule) {
		return extractModule4Entities(signature, typeModule, SynchronizedOWLManager.createOWLOntologyManager());
	}
	
	
	public Set<OWLAxiom> extractModule4Entities(Set<OWLEntity> signature, TYPEMODULE typeModule, OWLOntologyManager ontologyManager) {
		
		//init manager
		this.ontologyManager = ontologyManager;		
		dataFactory = this.ontologyManager.getOWLDataFactory();
		
		//we init input signature
		inputSignature.clear();
		inputSignature.addAll(signature);
		
		
		extractModule(typeModule);		
		
		return moduleAxioms;
		
		
	}
	
	
	
	private void extractModule(TYPEMODULE typeModule){

		switch (typeModule) {
			case BOTTOM_LOCALITY:
				extractModule(false, false);
				break;
		                
			case TOP_LOCALITY:
				extractModule(true, true);
				break;
	                      
			case BOTTOM_TOP_LOCALITY:  //LUM modules
				//Star 2 iterations
				extractStarModule(ontologyAxioms, 2);
	        	break;
	                     
	        case STAR:  //STAR modules. Until a fix point is reached
	        	extractStarModule(ontologyAxioms, 1000); //we put in any case a limit to the recursivity (give always a high "even" value)
	        	break;
	        default:	        	
	        	extractModule(false, false); //bottom module
	        	break;
		 
		}
		
		
	}
	
	
	
	public void setIncludeDeclarationAxioms(boolean includeDeclarationAxioms){
		this.includeDeclarationAxioms = includeDeclarationAxioms;
	}
	
	
	private void extractModule(boolean dualConcepts, boolean dualRoles){
		extractModule(ontologyAxioms, dualConcepts, dualRoles); //by default we give the original ontology axioms
	}
	
	
	/**
	 * General module extractor (single bottom or top locality module for the input signature (inputSignature) and ontology axioms (ontologyAxioms))
	 * @param dualConcepts
	 * @param dualRoles
	 */
	private void extractModule(Set<OWLAxiom> axioms, boolean dualConcepts, boolean dualRoles){
		
		
				
		//We initialise extractor
		localityChecker = new SyntacticLocalityChecker(dualConcepts, dualRoles, considerAnnotations, ignoreAssertions, dataFactory);
				
		
		//We reinit the moduleaxioms
		moduleAxioms.clear();
		
		//we init with input signature
		moduleSignature.clear();
		moduleSignature.addAll(inputSignature);
		
		
		int previousSize = 0;  //ModuleSignature size is 0 we don't extract any element 
		//(That's correct??) May be yoy can get non-local axiom for the empty signature, but actually
		//you are interested in a module related to your signature 
		
		
		//To ensure that entities in signature will belong to the module
		//Declaration axioms are non local
		if (includeDeclarationAxioms){
			for (OWLEntity ent : moduleSignature) {
				moduleAxioms.add(dataFactory.getOWLDeclarationAxiom(ent)); 
			}
		}
		
		
		//We have a preliminary step, where we only check axioms involving "current" signature
		//This may avoid some iterations
		//Optimization from:
		//Dmitry Tsarkov. Improved Algorithms for Module Extraction and Atomic Decomposition (DL 2012)
		if (useOptimization){			
			
			while(moduleSignature.size() != previousSize) {
				
				previousSize = moduleSignature.size();

				//We only check axioms involving the entities
				for (OWLEntity ent : moduleSignature){
					
					//We cannot assume all entities are indexed. Only those appearing in a axioms (see indexEntities)
					//if (!entity2Axioms.containsKey(ent))
					//	continue;
					try{
						for (OWLAxiom ax : entity2Axioms.get(ent)) {
																				
							//only axioms to be considered
							if (axioms.contains(ax) && !moduleAxioms.contains(ax) && !localityChecker.isLocalAxiom(ax, moduleSignature)) { //We don't want to consider it again
								moduleAxioms.add(ax);
									
								for (OWLEntity newEnt : ax.getSignature()) {
									if(!moduleSignature.contains(newEnt) && !newEnt.equals(dataFactory.getOWLThing())) //No Thing class within signature
										newMembers.add(newEnt); //We don't want to modify module within the iterations
								}
							}
						}
					}
					catch (Exception e){ //in case entity was not indexed
						continue;
					}
				}
				
				
				
				moduleSignature.addAll(newMembers);
				newMembers.clear();
			}
			
		}//end if optimization
		
		
		//Reinit to enter next step!
		previousSize = 0;
				
		while(moduleSignature.size() != previousSize) {
			previousSize = moduleSignature.size();

			for (OWLAxiom ax : axioms) { 
				
								
				if (!moduleAxioms.contains(ax) && !localityChecker.isLocalAxiom(ax, moduleSignature)) { //We don't want to consider it again
					moduleAxioms.add(ax);
						
					for (OWLEntity newEnt : ax.getSignature()) {
						if(!moduleSignature.contains(newEnt) && !newEnt.equals(dataFactory.getOWLThing())) //No Thing class within signature
							newMembers.add(newEnt); //We don't want to modify module within the iterations
					}
				}
			}
			
			
			
			moduleSignature.addAll(newMembers);
			newMembers.clear();
		}
		
		
	}
	
	

	
	
	
	
	
	
	private Set<OWLAxiom> axiomsIterations = new HashSet<OWLAxiom>();
	
		
	/**
	 * Extracts module alternating top and botton locality extractions.
	 * 2 iterations: LUM modules or top-bottom modules 
	 * Star: until a fix point is reached
	 * @param iterations
	 */
	private void extractStarModule(Set<OWLAxiom> axioms, int iterations){
		
		boolean type = (iterations % 2 == 0); //if we start with even -> top locality
		
		//System.out.println("\tIteration: " + iterations + ", module type: " + type);
		
		//extracts module entities and axioms
		extractModule(axioms, type, type);
			
			
		//If the axioms being modularise == module axioms then we are done
		//Only stop when odd -> bottom locality (considering we started with even)
		if (!type && (axiomsIterations.size() == getModuleAxioms().size())){
			return;
		}
			
				
		iterations--;
		//If iterations = 0 we are done!
		if (iterations<=0){
			return;
		}
		
		//Set new axioms to modularise
		axiomsIterations.clear();
		axiomsIterations.addAll(getModuleAxioms()); //we create new structure since module axioms will be cleared
		
				
		//Recursivity
		extractStarModule(axiomsIterations, iterations);
			
		
	}



	 public void saveExtractedModule(OWLOntologyManager manager, OWLOntology module, String physicalModuleURI) {
	    	//OWLOntologyManager ontologyModuleManager = SynchronizedOWLManager.createOWLOntologyManager();
	    	
	        try {
	        	manager.saveOntology(module, new RDFXMLDocumentFormat(), IRI.create(physicalModuleURI));
	        }
	        catch (Exception e) {
	        	System.err.println("Error saving module\n" + e.getLocalizedMessage());
	        	e.printStackTrace();
	        }
	    }
	
	
	
	
	
	
}

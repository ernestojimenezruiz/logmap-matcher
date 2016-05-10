package uk.ac.manchester.syntactic_locality;

import java.rmi.server.UID;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
//import java.net.URI;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import uk.ac.manchester.syntactic_locality.utils.*;


/**
 * This class serves as intermediary between the applications and the ModuleExtractor, managing the the iteration i module extraction when 
 * user are interested in combined module extraction like Lower of Upper modules. Also it makes the translation from type of module
 * (upper, lower, lum...) to 'dualConcept' and 'dualRole' meaning (Use ModuleExtractorManager.UPPER_MODULE or Constants.UPPER_MODULE).
 * @author Ernesto
 * Temporal Knowledge Bases Group
 * Jaume I University of Castellon
 * Created: 08/03/2008
 * Modified: -
 */
public class ModuleExtractorManager {
	
	//DUAL CONCEPTS AND DUAL ROLES
	public static final String LOWER_MODULE="LOWER_MODULE";
	public static final String LM="LM";
	
	//NON DUAL CONCEPTS AND NON DUAL ROLES
	public static final String UPPER_MODULE="UPPER_MODULE";
	public static final String UM="UM";
	
	public static final String LOWER_UPPER_MODULE="LOWER_UPPER_MODULE";
	public static final String LUM="LUM";
	
	public static final String UPPER_LOWER_MODULE="UPPER_LOWER_MODULE";
	public static final String ULM="ULM";
	
	//ONLY DUAL CONCEPTS
	public static final String DUAL_CONCEPTS_MODULE="DUAL_CONCEPTS_MODULE";
	public static final String DCM="DCM";
	
	//ONLY DUAL ROLES
	public static final String DUAL_ROLES_MODULE="DUAL_ROLES_MODULE";
	public static final String DRM="DRM";
	
	private boolean dualConcepts;
	
	private boolean dualRoles;
	
	private boolean secondIteration;  //Extract the "inverse module" of the previous extracted module
	
	private Set<OWLEntity> foreignSignature1;
	private Set<OWLEntity> foreignSignature2;

	//Modularizers
	private ModuleExtractor modularizer_Iteration1;
	private ModuleExtractor modularizer_Iteration2;
	
	//private OWLOntologyManager externalOntologyManager;
	private OWLOntology ontoToModularize;
	
	private OWLOntology module;
	
	private boolean considerAnnotations=false;
	
	private boolean considerImportsClosure=true;
	
	//private Set<OWLAxiom> moduleAxioms;
	
	
	public ModuleExtractorManager(OWLOntology ontology, String ModuleType){
		this(ontology, ModuleType, true, false, false);
		
	}
	
	public ModuleExtractorManager(OWLOntology ontology,	String ModuleType, 
			boolean considerImportsClosure, boolean considerAnnotations, boolean ignoreAnnotations){
		
		ontoToModularize=ontology;
		
		//Sets dualConcepts, dualRoles and seconIteration boolean variables
		getModuleType(ModuleType);
		
		this.considerAnnotations=considerAnnotations;
		this.considerImportsClosure=considerImportsClosure;
		
		//Construct modularizer (also creates structure for ontology)
		modularizer_Iteration1 =  new ModuleExtractor(ontoToModularize, dualConcepts, dualRoles, considerImportsClosure, considerAnnotations, ignoreAnnotations);
	
	}
	
	
	/**
	 * Necessary toi construct a module for an arbitriary set of axioms
	 * @param moduleAxioms
	 * @param moduleUri
	 * @return
	 */
	public OWLOntology getModuleFromAxioms(Set<OWLAxiom> moduleAxioms, IRI moduleIri) {
		
		OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
		
		OWLOntology module=null;
		
		try {
			module = ontologyManager.createOntology(moduleIri);
			List<OWLOntologyChange> ontoChanges = new ArrayList<OWLOntologyChange>();
			for(OWLAxiom axiom : moduleAxioms) {
				ontoChanges.add(new AddAxiom(module, axiom));
			}
			ontologyManager.applyChanges(ontoChanges);
		}
		
		catch(Exception e) {
			System.out.println("Error creating module ontology from extende set of axioms.");
			
		}

		//System.out.println("Time create OWLOntology for module (s): " + (double)((double)fin-(double)init)/1000.0);
		
		return module;
	}
	
	
	
	/**
	 * Transform type of module to 'dualConcept' and 'dualRole' meaning
	 * @param ModuleType
	 */
	private void getModuleType(String ModuleType){
		
		if (ModuleType.equals(UPPER_MODULE) || ModuleType.equals(UM)) {
			dualConcepts = false;
			dualRoles = false;
			secondIteration=false;
		}
		else if (ModuleType.equals(LOWER_MODULE) || ModuleType.equals(LM)) {
			dualConcepts = true;
			dualRoles = true;
			secondIteration=false;
		}
		else if (ModuleType.equals(LOWER_UPPER_MODULE) || ModuleType.equals(LUM)) {
			dualConcepts = false;
			dualRoles = false;
			secondIteration=true;
		}
		
		else if (ModuleType.equals(UPPER_LOWER_MODULE) || ModuleType.equals(ULM)) {
			dualConcepts = true;
			dualRoles = true;
			secondIteration=true;
		}
		
		else if (ModuleType.equals(DUAL_CONCEPTS_MODULE) || ModuleType.equals(DCM)) {
			dualConcepts = true;
			dualRoles = false;
			secondIteration=false;
		}
		else if (ModuleType.equals(DUAL_ROLES_MODULE) || ModuleType.equals(DRM)) {
			dualConcepts = false;
			dualRoles = true;
			secondIteration=false;
		}
		else {
			System.err.println("The given module type is unknown. An Upper Module will be extracted.");
			dualConcepts = false;
			dualRoles = false;
			secondIteration=false;
		}
		
	}
	
	
	
    
    
    /**
     * 
     *
     */
    public OWLOntology extractModule(HashSet<OWLEntity> signature){
    	return extractModule(signature, "http://krono.ac.uji.es/Links/ontologies/module" + new UID() + ".owl");
    }
	
    
    
	/**
	 * 
	 *
	 */
	public OWLOntology extractModule(HashSet<OWLEntity> signature, String moduleURIStr) { //give URI!!!
		
		
		
	    //We extract upper module of lower module, or lower module of the upper module
	    //------------------------------------------------------------------------------
	    if (secondIteration) {
	    	
	    	//We need two signatures, since the signature is modified in moduleExtractor. If we create a new set in extractor we spedd down the excution
			//so it's better to duplicate now the structure
	    	//foreignSignature1=new HashSet<OWLEntity>(signature);
	    	foreignSignature1=new HashSet<OWLEntity>(signature);
			foreignSignature2=new HashSet<OWLEntity>(signature);
	    	
	    	//URI auxModuleURI = URI.create("http://krono.act.uji.es/Links/ontologies/temporalModule.owl");
	    	String  auxModuleURI = "http://krono.act.uji.es/Links/ontologies/temporalModule.owl";
	    	
	    	//It is important to use different URIs for first and second ioteration modules
	    	module = modularizer_Iteration1.getLocalityModuleForSignatureGroup(foreignSignature1, auxModuleURI);
		    System.out.println("Module size (axioms) Iter 1: " + module.getAxioms().size());
		    
		    if (module.getAxioms().size() == 0 && module.getClassesInSignature().size()==0) {
		    	System.out.println("(Empty module, so not necessary iteration) Module size (axioms) Iter 2: "  + module.getAxioms().size());
		    }
		    else {
		    	//Inverted
		    	modularizer_Iteration2 =  new ModuleExtractor(module, !dualConcepts, !dualRoles, considerImportsClosure, considerAnnotations, false);
		    	module = modularizer_Iteration2.getLocalityModuleForSignatureGroup(foreignSignature2, moduleURIStr);
		    	
		    	System.out.println("Module size (axioms) Iter 2: "  + module.getAxioms().size());
		    }	
	    }
	    else {
	    	foreignSignature1=new HashSet<OWLEntity>(signature);
	    	module = modularizer_Iteration1.getLocalityModuleForSignatureGroup(foreignSignature1, moduleURIStr);
		    //System.out.println("Module size (axioms): " + module.getAxioms().size());
	    }
	    		
	    
	    return module;
	}
	

	public IRI getModuleIRI(){
		return module.getOntologyID().getOntologyIRI();
	}
	
	
    public OWLOntology getExtractedModule(){
    	return module;
    }
    
    public Set<OWLAxiom> getModuleAxioms(){
    	return module.getAxioms();
    }
    
    
    public int getNumberOfAxiomsExtractedModule(){
    	return module.getAxioms().size();
    }

    
    public int getNumberOfClassesExtractedModule(){
    	return module.getClassesInSignature().size();
    }

    public int getNumberOfIndividualsExtractedModule(){
    	return module.getIndividualsInSignature().size();
    }
    
    public int getNumberOfRolesExtractedModule(){
    	return module.getDataPropertiesInSignature().size() + module.getObjectPropertiesInSignature().size();
    }
    
    

    
    public int getNumberOfAxiomsOntoToModularize(){
    	return ontoToModularize.getAxioms().size();
    }

    public int getNumberOfClassesOntoToModularize(){
    	return ontoToModularize.getClassesInSignature().size();
    }

    public int getNumberOfIndividualsOntoToModularize(){
    	return ontoToModularize.getIndividualsInSignature().size();
    }
    
    public int getNumberOfRolesOntoToModularize(){
    	return ontoToModularize.getDataPropertiesInSignature().size() + ontoToModularize.getObjectPropertiesInSignature().size();
    }

    
    

	
}

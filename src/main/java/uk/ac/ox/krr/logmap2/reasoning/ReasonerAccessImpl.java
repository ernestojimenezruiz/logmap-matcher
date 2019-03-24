/*******************************************************************************
 * Copyright 2012 by the Department of Computer Science (University of Oxford)
 * 
 *    This file is part of LogMap.
 * 
 *    LogMap is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU Lesser General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 * 
 *    LogMap is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU Lesser General Public License for more details.
 * 
 *    You should have received a copy of the GNU Lesser General Public License
 *    along with LogMap.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package uk.ac.ox.krr.logmap2.reasoning;

import java.util.ArrayList;



import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

//import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.util.DLExpressivityChecker;
import org.semanticweb.owlapi.util.InferredAxiomGenerator;
import org.semanticweb.owlapi.util.InferredEquivalentClassAxiomGenerator;
import org.semanticweb.owlapi.util.InferredOntologyGenerator;
import org.semanticweb.owlapi.util.InferredSubClassAxiomGenerator;

import uk.ac.ox.krr.logmap2.Parameters;
import uk.ac.ox.krr.logmap2.io.LogOutput;
import uk.ac.ox.krr.logmap2.owlapi.SynchronizedOWLManager;
//import uk.ac.ox.krr.ontoevol.visitor.ELActiveConceptsVisitor;

public abstract class ReasonerAccessImpl extends ReasonerAccess {

	protected long init, fin;

	protected OWLOntology ontoBase;
	protected OWLOntologyManager ontoManager;
	protected OWLDataFactory datafactory;

	protected OWLReasoner reasoner;
	protected OWLReasonerFactory reasonerFactory;
	
	protected Set<OWLAxiom> closure;
	protected int closure_lang = ReasonerAccess.LSUB; //Default language
	
	protected Set<OWLClassExpression> activeConcepts;
	
	protected String reasonerName = "";
	
	protected boolean isClassified = false;
	
	private String DLNameOnto;
	
	
	public ReasonerAccessImpl(OWLOntologyManager ontoManager, OWLOntology onto, boolean useFactory) throws Exception{
		
		this.ontoManager=ontoManager;
		this.ontoBase=onto;
		datafactory=ontoManager.getOWLDataFactory();
		
		closure = new HashSet<OWLAxiom>();
		
		
		//Set<OWLOntology> importsClosure = this.ontoManager.getImportsClosure(ontoBase);        
        //DLExpressivityChecker checker = new DLExpressivityChecker(importsClosure);
        //DLNameOnto = checker.getDescriptionLogicName();
        
        
		setUpReasoner(useFactory);
		
	}
	
	public void clearStructures(){
		closure.clear();
		reasoner.dispose();
		
	}
	
	public void dispose(){
		reasoner.dispose();
	}
	
	public void interrupt(){
		reasoner.interrupt();
	}
	
	
	protected void setUpReasoner(boolean withFactory) throws Exception{
		
	}
	
	
	public String getDLNameOntology(){
		return DLNameOnto;
	}
	
	

	public boolean isOntologyClassified(){
		return isClassified;
	}
	
	

    
    public void classifyOntology_withTimeout(int timeoutSecs) {
    	
    	isClassified = false;

    	ExecutorService executor = Executors.newFixedThreadPool(1);
    	
        //set the executor thread working
        final Future<?> future = executor.submit(new Runnable() {
            public void run() {
                try {
                	classifyOntologyNoProperties(); //If timeout never class properties
                	//classifyOntology();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });

        //check the outcome of the executor thread and limit the time allowed for it to complete
        try {
            future.get(timeoutSecs, TimeUnit.SECONDS);
            future.cancel(true);
            executor.shutdown();
        }
        catch (TimeoutException e) {
        	
        	//ExecutionException: deliverer threw exception
            //TimeoutException: didn't complete within downloadTimeoutSecs
            //InterruptedException: the executor thread was interrupted

        	LogOutput.print("Time out classifying with HermiT. Using 'structural' reasoner instead.");
        	
        	isClassified = false;
        	
        	reasoner.interrupt();
        	reasoner.dispose();
        	
            //interrupts the worker thread if necessary
            future.cancel(true);
            executor.shutdown();

            
        
    	}
        catch (Exception e) {
        	LogOutput.print("Error classifying ontology with " + reasoner.getReasonerName());// + "\n" + e.getMessage() + "\n" + e.getLocalizedMessage());
        	e.printStackTrace();
        }        
    }
    
    
    
    /**
     * Used from LogMap's web service
     * @param timeoutSecs
     * @throws Exception
     */
    public void classifyOntology_withTimeout_throws_Exception(int timeoutSecs) throws Exception{

    	isClassified = false;
    	
    	ExecutorService executor = Executors.newFixedThreadPool(1);
    	
        //set the executor thread working
        final Future<?> future = executor.submit(new Runnable() {
            public void run() {
                try {
                	classifyOntologyNoProperties(); //If timeout never class properties
                	//classifyOntology();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });

        //check the outcome of the executor thread and limit the time allowed for it to complete
        try {
            future.get(timeoutSecs, TimeUnit.SECONDS);
            future.cancel(true);
            executor.shutdown();
        }
        catch (TimeoutException e) {
        	
        	isClassified = false;
        	
        	reasoner.interrupt();
        	reasoner.dispose();
        	
            //interrupts the worker thread if necessary
            future.cancel(true);
            executor.shutdown();
            LogOutput.print("Timeout classifying ontology with " + reasoner.getReasonerName());
            throw new TimeoutException();
    	}
        catch (Exception e) {
        	isClassified = false;

        	reasoner.dispose();
        	
        	//interrupts the worker thread if necessary
            future.cancel(true);
            executor.shutdown();
        	
            //e.printStackTrace();
            LogOutput.print("Error classifying ontology with " + reasoner.getReasonerName());
            //e.printStackTrace();
        	throw new Exception();
    	
        }        
    }
    
    
    
	
    
    public void classifyOntologyNoProperties() throws Exception{
    	classifyOntology(false);
    }
	
    public void classifyOntology() throws Exception{
    	classifyOntology(true);
    }
	
	
	
	
	
	public void classifyOntology(boolean classproperties) throws Exception{
	
		isClassified = false;
		
		try {
			//ontoManager is does not contains ontoBase. It was loaded witha different manager
			//Set<OWLOntology> importsClosure = ontoManager.getImportsClosure(ontoBase);        
			Set<OWLOntology> importsClosure = new HashSet<OWLOntology>();
			importsClosure.add(ontoBase);
			DLExpressivityChecker checker = new DLExpressivityChecker(importsClosure);
	        //System.out.println("Expressivity Ontology: " + checker.getDescriptionLogicName());
			
			init=Calendar.getInstance().getTimeInMillis();
			LogOutput.print("\nClassifying '" + checker.getDescriptionLogicName() + "' Ontology with " + reasonerName + "... ");
	        reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);
	        
	        
	        
	        //reasoner.getPrecomputableInferenceTypes()
	        
	        //The may help for the queries
	        if (classproperties){
	        	reasoner.precomputeInferences(InferenceType.DATA_PROPERTY_HIERARCHY);
	        	reasoner.precomputeInferences(InferenceType.OBJECT_PROPERTY_HIERARCHY);
	        }
	        
	        //Only if instance matching should be performed
	        if (Parameters.perform_instance_matching){
	        	reasoner.precomputeInferences(InferenceType.CLASS_ASSERTIONS);
	        }
	        
	        //reasoner.precomputeInferences(InferenceType.DISJOINT_CLASSES);//Only explicit, we have extended method!
	        
	        //hermit.realise();
			fin = Calendar.getInstance().getTimeInMillis();
			LogOutput.print("Done, Time (s): " + (float)((double)fin-(double)init)/1000.0 + "\n");
			
			isClassified = true;
		}
		catch (Exception e){
			//LogOutput.print("Error classifying ontology with " + reasoner.getReasonerName() + "\n" + e.getMessage() + "\n" + e.getLocalizedMessage());
			LogOutput.print("Error classifying ontology with " + reasonerName + "\n" + e.getMessage() + "\n" + e.getLocalizedMessage());
			//e.printStackTrace();
			throw new Exception();
			//e.printStackTrace();
		}
		
	}

	
	public OWLOntology getOntology(){
		return ontoBase;
	}
		
	public OWLReasoner getReasoner() {
		return reasoner;
	}
	
	
	public OWLReasonerFactory getReasonerFactory() {
		return reasonerFactory;
	}
	
	public boolean isConsistent(){
		return reasoner.isConsistent();
	}

	
	
	public boolean isEntailed(OWLAxiom ax){
		return reasoner.isEntailed(ax);
	}
	
	
	
	public boolean isSubClassOf(OWLClass cls1, OWLClass cls2) {
		return reasoner.isEntailed(datafactory.getOWLSubClassOfAxiom(cls1, cls2));
	}


	

	public boolean areDisjointClasses(OWLClass cls1, OWLClass cls2) {
		
		
		return !reasoner.isSatisfiable(datafactory.getOWLObjectIntersectionOf(cls1, cls2));
		
	}
	
	
	public boolean areEquivalentClasses(OWLClass cls1, OWLClass cls2) {
		return reasoner.isEntailed(datafactory.getOWLEquivalentClassesAxiom(cls1, cls2));
	}
	
	
	public boolean isSatisfiable(OWLClass cls){
		return reasoner.isSatisfiable(cls);
	}
	
	
	private OWLClassExpression current_cls;
	private int resultClassEval;
	
	 
	
	public void doWork_SatCls() {
		
		try{
			if (reasoner.isSatisfiable(current_cls))
				resultClassEval=SAT;
			else
				resultClassEval=UNSAT;
		}
		catch (Exception e) {
			if (!current_cls.isAnonymous())
	        	System.out.println("Unknown class: " + current_cls.asOWLClass().getIRI().toString());
			
	        resultClassEval = UNKNOWN;	    	
	    }
    	
    }
	
	public int isSatisfiable_withTimeout(OWLClassExpression cls, int timeoutSecs){
		
		current_cls = cls;
		resultClassEval = UNKNOWN;
		
		ExecutorService executor = Executors.newFixedThreadPool(1);
    	
        //set the executor thread working
        final Future<?> future = executor.submit(new Runnable() {
            public void run() {
                try {
                	doWork_SatCls(); //If timeout never class properties

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });

        //check the outcome of the executor thread and limit the time allowed for it to complete
        try {
            future.get(timeoutSecs, TimeUnit.SECONDS);
            future.cancel(true);
            executor.shutdown();
        }
        catch (TimeoutException e) {
        	
        	//ExecutionException: deliverer threw exception
            //TimeoutException: didn't complete within downloadTimeoutSecs
            //InterruptedException: the executor thread was interrupted

        	
        	resultClassEval = UNKNOWN;
        	
            //interrupts the worker thread if necessary
            future.cancel(true);
            executor.shutdown();

            
        
    	}
        catch (Exception e) {
        	//LogOutput.printError("Error checking satisfiability with " + reasoner.getReasonerName() + "\n" + e.getMessage() + "\n" + e.getLocalizedMessage());
        	resultClassEval = UNKNOWN;
    	
        }
        
        return resultClassEval;
		
	}
	
	
	
	
	
	
	
	public boolean hasUnsatisfiableClasses(){
		if (getUnsatisfiableClasses().size()>0)
			return true;
		
		return false;
	}
	
	
	public Set<OWLClass> getUnsatisfiableClasses() {
		return getUnsatisfiableClasses(false);
	}
	
	
	private Set<OWLClass> getUnsatisfiableClasses(boolean print) {
		
		try{
		
			Set<OWLClass> set;
		
			//Now the reasoner return a node structure. A Node contains the
			//set of entities which are equivalent
			Node<OWLClass> node = reasoner.getUnsatisfiableClasses();
		
			//set = node.getEntities();
			set = node.getEntitiesMinusBottom();
			
			//set.remove(datafactory.getOWLNothing());
			
			if (!set.isEmpty()) {
				if (print)
					System.err.println("The following classes are unsatisfiable: ");
				
				for(OWLClass cls : set) {
					if (print)
						System.err.println(" " + cls);
				}
			}
			else{
				if (print)
					System.out.println("There are '0' unsatisfiable classes.");
			}
			
			return set;
		}
		catch (Exception e){
			System.err.println("Error when invoking the reasoner to get unsatisfiable classes.");
			return new HashSet<OWLClass>();
		}
		
	}

	
	public void setLanguage4Closure(int language) {
		closure_lang = language;
	}

	
	public void createClosure(int language) {
		
		setLanguage4Closure(language);

		createClosure();
		
	}
	
	
	/**
	 * To be reviewed... was not complete enough (see Semantic difference)
	 */
	public void createClosure() {
						
		switch (closure_lang) {
			case ReasonerAccess.LSUB:
				createClosureLSub();
				break;
		
			/*case ReasonerAccess.LBASIC:
				createClosureLBasic();
				break;
				
			case ReasonerAccess.LACTIVE:
				createClosureLActive();
				break;
			*/
			default:
				break;
		}
		

	}
	
	
	public Set<OWLAxiom> getClosure() {
		return closure;
	}
	
	
	
	
	/**
	 * Those class expression occurring in the ontology.	
	 */
	private Set<OWLClassExpression> getActiveClassExpressions(){
		//TODO
		return activeConcepts;
	}
	
	
	
	/**
	 * This closure will involve subclass axioms
	 */
	private void createClosureLSub(){
				
		try {
			
			init=Calendar.getInstance().getTimeInMillis();
			LogOutput.print("Extracting Lsub closure... ");
	        
			for (OWLClass cls : reasoner.getUnsatisfiableClasses()) {
	        
				if (!cls.isOWLNothing()) {
	                   OWLAxiom unsatAx = ontoManager.getOWLDataFactory().getOWLSubClassOfAxiom(cls,
	                		   ontoManager.getOWLDataFactory().getOWLNothing());
	                   
	                   //Consider unsat axiom!
	                   closure.add(unsatAx);
	            }	           
			}
	           
           //axiomsInferredOnt.addAll(inferredAxioms);
           
           //OWLOntologyManager classifiedOntoMan = OWLManager.createOWLOntologyManager();
		   OWLOntologyManager classifiedOntoMan = SynchronizedOWLManager.createOWLOntologyManager();
		   
		   IRI iri;
		   if (ontoBase.getOntologyID().getOntologyIRI().isPresent()) 
			   iri= ontoBase.getOntologyID().getOntologyIRI().get();
		   else 
			   iri = IRI.create("http://inferred-ontology.owl");
		   
		   
           OWLOntology inferredOnt = classifiedOntoMan.createOntology(iri);
           InferredOntologyGenerator ontGen = new InferredOntologyGenerator(
        		   reasoner, new ArrayList<InferredAxiomGenerator<? extends OWLAxiom>>());
           //InferredOntologyGenerator ontGen = new InferredOntologyGenerator(reasoner);
           
           
           ontGen.addGenerator(new InferredEquivalentClassAxiomGenerator());
           ontGen.addGenerator(new InferredSubClassAxiomGenerator());
           
           //Fills inferred onto
           ontGen.fillOntology(classifiedOntoMan.getOWLDataFactory(), inferredOnt);
         
           
           //Getting closure without axioms "subclass of Thing"	           
           for (OWLAxiom ax : inferredOnt.getAxioms()){
        	   //closure.add(ax); // IT IS NO REDUNDANT!!
	           if (ax instanceof OWLSubClassOfAxiom) {
       		
	        	   //TODO: it may be the case we want to show this axioms...
       				if (!((OWLSubClassOfAxiom) ax).getSuperClass().isOWLThing() && 
       						!ontoBase.containsAxiom(ax)) {
       					//ConsiderAxiom
       					closure.add(ax);	       				
       				}		           
	           }       			
           }	
           	           
           fin = Calendar.getInstance().getTimeInMillis();           
           LogOutput.print("Done, Time (s): " + (float)((double)fin-(double)init)/1000.0);
           LogOutput.print("Closure:\n" + closure.size());
           
           
   		   //OTHER GENERATORS
   		   //ontGen.addGenerator(new InferredClassAssertionAxiomGenerator());
   		   //ontGen.addGenerator(new InferredPropertyAssertionGenerator());
   		   //Original computational cost is really high! With extension we can extract only eplicit disjointness	   		   
           //ontGen.addGenerator(new InferredDisjointClassesAxiomGenerator());
           
           //ontGen.addGenerator(new InferredDataPropertyCharacteristicAxiomGenerator());	           
           //ontGen.addGenerator(new InferredEquivalentDataPropertiesAxiomGenerator());
           //ontGen.addGenerator(new InferredSubDataPropertyAxiomGenerator());
       
           //ontGen.addGenerator(new InferredEquivalentObjectPropertyAxiomGenerator());
           //ontGen.addGenerator(new InferredInverseObjectPropertiesAxiomGenerator());
           //ontGen.addGenerator(new InferredObjectPropertyCharacteristicAxiomGenerator());
           //ontGen.addGenerator(new InferredSubObjectPropertyAxiomGenerator());
           
	           
	       }
	       catch (Exception e) {
	           e.printStackTrace();
	           //return new ArrayList<OWLAxiom>();
	       }
		
		
		
	}
	

	/*
	 * 
	 
	private void createClosureLBasic(){
		
		//Must be included		
		createClosureLSub();
		
		
		//Set active concepts (atomic + exists (one level))
		ELActiveConceptsVisitor visitor = new ELActiveConceptsVisitor();
						
		for (OWLAxiom ax : ontoBase.getTBoxAxioms(false)){
			activeConcepts.addAll(visitor.getActiveConcepts4Axiom(ax, true));
		}
		
		
		
		
		//Combine active concepts for LBasic
		OWLAxiom candidate_ax;
		for (OWLClassExpression cls_exp1 : activeConcepts){
			for (OWLClassExpression cls_exp2 : activeConcepts){
				
				if (cls_exp1.equals(cls_exp2) || //Tautology
					(cls_exp1.isClassExpressionLiteral() && cls_exp2.isClassExpressionLiteral()) || //Already in classification
					(cls_exp1.isAnonymous() && cls_exp2.isAnonymous())){ //At least one should be non anonymous
					continue;
				}
				
				candidate_ax = datafactory.getOWLSubClassOfAxiom(cls_exp1, cls_exp2);
				
				if (isEntailed(candidate_ax)){
					closure.add(candidate_ax);
				}
				
				
			}
			
		}
		
		
		
		
		
		
	}
	
	
	
	private void createClosureLActive(){
		
		//Must be included
		createClosureLSub();
		
		//Set all active concepts (different levels)
		ELActiveConceptsVisitor visitor = new ELActiveConceptsVisitor();
		
		for (OWLAxiom ax : ontoBase.getTBoxAxioms(false)){
			activeConcepts.addAll(visitor.getActiveConcepts4Axiom(ax, false));
		}
		
		
		//Combine active concepts for LActive
		OWLAxiom candidate_ax;
		for (OWLClassExpression cls_exp1 : activeConcepts){
			for (OWLClassExpression cls_exp2 : activeConcepts){
				
				if (cls_exp1.equals(cls_exp2) || //Tautology
					(cls_exp1.isClassExpressionLiteral() && cls_exp2.isClassExpressionLiteral())){ //Already in classification
					continue;
				}
						
				candidate_ax = datafactory.getOWLSubClassOfAxiom(cls_exp1, cls_exp2);
				
				if (isEntailed(candidate_ax)){
					closure.add(candidate_ax);
				}
						
						
			}
					
		}
		
		
	}*/


	
	
	
	


}

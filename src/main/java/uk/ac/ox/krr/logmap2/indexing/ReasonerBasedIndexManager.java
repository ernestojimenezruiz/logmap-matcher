package uk.ac.ox.krr.logmap2.indexing;

import java.util.Calendar;


import org.semanticweb.owlapi.apibinding.OWLManager;
//import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;

import uk.ac.ox.krr.logmap2.io.LogOutput;
import uk.ac.ox.krr.logmap2.owlapi.SynchronizedOWLManager;
import uk.ac.ox.krr.logmap2.reasoning.ReasonerAccess;


/**
 * 
 * This class uses a DL reasoner to answer taxonomy related queries
 * 
 * @author Ernesto
 *
 */
public class ReasonerBasedIndexManager extends JointIndexManager {

	private ReasonerAccess jointreasoner;
	private OWLDataFactory dataFactory; 
	
	public ReasonerBasedIndexManager(){
		
		//dataFactory = OWLManager.getOWLDataFactory();
		dataFactory = SynchronizedOWLManager.createOWLDataFactory();
		
	}
	
	
	public void setJointReasoner(ReasonerAccess jointreasoner){
		this.jointreasoner=jointreasoner;
	}
	
	
	
	public boolean isSubClassOf(int cIdent1, int cIdent2){
		
		calls_tax_question++;
		init = Calendar.getInstance().getTimeInMillis();
		
		//System.out.println("hello");
		
		boolean answer;
		
		answer =  isSubClassOf(
				getOWLClass4ConceptIndex(cIdent1),
				getOWLClass4ConceptIndex(cIdent2)
				//OWLManager.getOWLDataFactory().getOWLClass(getIRI4ConceptIndex(cIdent1)),
				//OWLManager.getOWLDataFactory().getOWLClass(getIRI4ConceptIndex(cIdent2))
				);
		
		fin = Calendar.getInstance().getTimeInMillis();
		time_tax_question += (float)((double)fin-(double)init)/1000.0;
		
		return answer;
		
	}
	
	public boolean isSubClassOf(OWLClass cls1, OWLClass cls2){
		return jointreasoner.isEntailed(
				dataFactory.getOWLSubClassOfAxiom(cls1, cls2));
		
	}
	
	
	public boolean isSuperClassOf(int cIdent1, int cIdent2){
		return isSubClassOf(cIdent2, cIdent1);
	}
	
	
	
	public boolean areEquivalentClasses(int cIdent1, int cIdent2){
		return areEquivalent(
				getOWLClass4ConceptIndex(cIdent1),
				getOWLClass4ConceptIndex(cIdent2)
				//OWLManager.getOWLDataFactory().getOWLClass(getIRI4ConceptIndex(cIdent1)),
				//OWLManager.getOWLDataFactory().getOWLClass(getIRI4ConceptIndex(cIdent2))
				);
	}
	
	
	public boolean areEquivalent(OWLClass cls1, OWLClass cls2){
		return jointreasoner.isEntailed(
				dataFactory.getOWLEquivalentClassesAxiom(cls1, cls2));
	}
	
	
	public boolean areDisjoint(int cIdent1, int cIdent2){
		
		calls_disj_question++;
		init = Calendar.getInstance().getTimeInMillis();
		
		boolean answer;
		
		answer = areDisjoint(
				getOWLClass4ConceptIndex(cIdent1),
				getOWLClass4ConceptIndex(cIdent2)
				//OWLManager.getOWLDataFactory().getOWLClass(getIRI4ConceptIndex(cIdent1)),
				//OWLManager.getOWLDataFactory().getOWLClass(getIRI4ConceptIndex(cIdent2))
				);
		
		fin = Calendar.getInstance().getTimeInMillis();
		time_disj_question += (float)((double)fin-(double)init)/1000.0;
		
		return answer;
		
	}
	
	public boolean areDisjoint(OWLClass cls1, OWLClass cls2){
		//return !jointreasoner.isSatisfiable(
		//		OWLManager.getOWLDataFactory().getOWLObjectIntersectionOf(cls1, cls2));
		
		
		int state = jointreasoner.isSatisfiable_withTimeout(
				dataFactory.getOWLObjectIntersectionOf(cls1, cls2),
				10);
		
		if (state == ReasonerAccess.UNSAT){
			return true;			
		}
		else if (state == ReasonerAccess.UNKNOWN){
			unknown_disj_question++;
			LogOutput.printAlways("\tUNKNOWN DISJ call. Test: " + calls_disj_question);			
		}
		
		return false; //sat or unknown
		
	}
	
	
	
	
	
	
	
}

package uk.ac.ox.krr.logmap2.reasoning;

import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.impl.OWLClassNodeSet;

/**
 * Class to extract explicit disjointness axioms
 * @author ernesto
 *
 */
public class DisjointnessAxiomExtractor {

	/**
	 * OWLDisjointAxiom(A,B,C)
	 * @param reasoner
	 * @param ontology
	 * @param cls
	 * @return
	 */
	public static OWLClassNodeSet getExplicitOWLDisjointnessAxioms(OWLReasoner reasoner, OWLOntology ontology, OWLClass cls){
		
		OWLClassNodeSet nodeSet = new OWLClassNodeSet();
		
		for (OWLDisjointClassesAxiom ax : ontology.getDisjointClassesAxioms(cls)) {                	
            for (OWLClassExpression op : ax.getClassExpressions()) {
                if (!op.isAnonymous() && !op.equals(cls)) { //Op must be differnt to ce
                    nodeSet.addNode(reasoner.getEquivalentClasses(op));
                }
            }
        }
		
		return nodeSet;
		
	}
	
	/**
	 * A ^ B -> bottom
	 * @param reasoner
	 * @param ontology
	 * @param cls
	 * @author Shuo Zhang
	 * @return
	 */
	public static OWLClassNodeSet getExplicitDLDisjointnessAxioms(OWLReasoner reasoner, OWLOntology ontology, OWLClass cls){
		
		OWLClassNodeSet nodeSet = new OWLClassNodeSet();
		
		OWLClassExpression subExp;
    	Set<OWLClassExpression> set;
        for (OWLSubClassOfAxiom sax : ontology.getSubClassAxiomsForSuperClass(OWLManager.getOWLDataFactory().getOWLNothing())) {
        	subExp = sax.getSubClass();
        	if (subExp instanceof OWLObjectIntersectionOf) {
        		set = subExp.asConjunctSet();
        		if (set.contains(cls) && set.size() == 2) {
        			for (OWLClassExpression op : set) {
        				if (!op.equals(cls) && !op.isAnonymous()) {
        					nodeSet.addNode(reasoner.getEquivalentClasses(op));
        					break;
        				}
        			}
        		}
        	}
        } 
		
        return nodeSet;
        
	}
	
	
	/**
	 * A subClassOc not B
	 * @param reasoner
	 * @param ontology
	 * @param cls
	 * @author Shuo Zhang
	 * @return
	 */
	public static OWLClassNodeSet getDisjointnessAxiomsWithNegation(OWLReasoner reasoner, OWLOntology ontology, OWLClass cls){
		
		OWLClassNodeSet nodeSet = new OWLClassNodeSet();
		
		
		OWLClassExpression sub;
        OWLClassExpression sup;
        OWLSubClassOfAxiom sax2;
        OWLClassExpression exp;
        for (OWLClassAxiom ax : ontology.getAxioms(cls.asOWLClass())) { ///  check
        	if (ax instanceof OWLSubClassOfAxiom) {
        		sax2 = (OWLSubClassOfAxiom) ax;
        		sup = sax2.getSuperClass();
        		
        		if (sup instanceof OWLObjectComplementOf) {
        			sub = sax2.getSubClass();
        			if (sub.equals(cls)) {
        				exp = ((OWLObjectComplementOf) sup).getOperand();
        				if (!exp.isAnonymous()) {
        					nodeSet.addNode(reasoner.getEquivalentClasses(exp));
        				}
        			} else if (!sub.isAnonymous()) {
        				exp = ((OWLObjectComplementOf) sup).getOperand();
        				if (exp.equals(cls)) {
        					nodeSet.addNode(reasoner.getEquivalentClasses(sub));
        				}
        			}
        		}
        	}
        }
		
		
		return nodeSet;
		
	}
	

	
}

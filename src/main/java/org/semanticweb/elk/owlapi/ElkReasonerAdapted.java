package org.semanticweb.elk.owlapi;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.impl.OWLClassNodeSet;

import uk.ac.ox.krr.logmap2.reasoning.DisjointnessAxiomExtractor;

public class ElkReasonerAdapted extends ElkReasoner{

	
	public ElkReasonerAdapted(OWLOntology rootOntology){
		super(rootOntology, false, new ElkReasonerConfiguration());
	}
	
	/**
	 * Getting all disjoint classes is costly. We get only explicit disjointness.
	 * We will complete with questions (A intersection B) later  if necessary
	 */
	public NodeSet<OWLClass> getDisjointClasses(OWLClassExpression ce) {
        
		 OWLClassNodeSet nodeSet = new OWLClassNodeSet();
	        
	     if (!ce.isAnonymous()) {
		     for (OWLOntology ontology : getRootOntology().getImportsClosure()) {	        	
			        nodeSet.addAllNodes(DisjointnessAxiomExtractor.getExplicitOWLDisjointnessAxioms(this, ontology, ce.asOWLClass()).getNodes());
			        nodeSet.addAllNodes(DisjointnessAxiomExtractor.getExplicitOWLDisjointnessAxioms(this, ontology, ce.asOWLClass()).getNodes());
			        nodeSet.addAllNodes(DisjointnessAxiomExtractor.getExplicitOWLDisjointnessAxioms(this, ontology, ce.asOWLClass()).getNodes());
		     }
	     }
	        
	     return nodeSet;
		
		/*if (!ce.isAnonymous()) {                   	
        	for (OWLOntology ontology : getRootOntology().getImportsClosure()) {
        		
                for (OWLDisjointClassesAxiom ax : ontology.getDisjointClassesAxioms(ce.asOWLClass())) {                	
                    for (OWLClassExpression op : ax.getClassExpressions()) {
                        if (!op.isAnonymous() && !op.equals(ce)) { //Op must be differnt to ce
                            nodeSet.addNode(getEquivalentClasses(op));
                        }
                    }
                }
            }
        } */       
        
    }

}

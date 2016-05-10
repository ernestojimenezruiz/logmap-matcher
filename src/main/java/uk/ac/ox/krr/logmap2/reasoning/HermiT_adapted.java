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

import org.semanticweb.HermiT.Configuration;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.BufferingMode;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;
import org.semanticweb.owlapi.reasoner.impl.OWLClassNodeSet;

public class HermiT_adapted extends Reasoner{
	
	
	public HermiT_adapted(OWLOntology rootOntology, Configuration conf) throws Exception{
		super(conf, rootOntology);
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
    	}*/
    }
	
}

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

import org.semanticweb.owlapi.reasoner.structural.*;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.*;
import org.semanticweb.owlapi.reasoner.impl.OWLClassNodeSet;

/**
 * Repairs a bugs in StructuralReasoner.getDisjointClasses
 * @author ernesto
 *
 */
public class StructuralReasonerExtended extends StructuralReasoner{

	public StructuralReasonerExtended(OWLOntology rootOntology) {
		super(rootOntology,  new SimpleConfiguration(), BufferingMode.NON_BUFFERING);
	}
	
	
	/*public NodeSet<OWLClass> getSubClasses(OWLClassExpression ce, boolean direct){
		try {
			return super.getSubClasses(ce, direct);
		}
		catch (Exception e){
			
			System.err.println(e.getMessage() +  " " + e.getCause());
			e.printStackTrace();
			return null;
		}
	}*/
	
	
	/**
	 * It was an error in original method. the result set contained both the given class and its equivalents.
	 */
	public NodeSet<OWLClass> getDisjointClasses(OWLClassExpression ce) {
        //super.ensurePrepared();
        OWLClassNodeSet nodeSet = new OWLClassNodeSet();
        if (!ce.isAnonymous()) {
            for (OWLOntology ontology : getRootOntology().getImportsClosure()) {
                for (OWLDisjointClassesAxiom ax : ontology.getDisjointClassesAxioms(ce.asOWLClass())) {
                    for (OWLClassExpression op : ax.getClassExpressions()) {
                        if (!op.isAnonymous() && !op.equals(ce)) { //Op must be differnt to ce
                            nodeSet.addNode(getEquivalentClasses(op));
                        }
                    }
                }
            }
        }
        
        
        
        return nodeSet;
    }
	
	
	public boolean isSubClassOf(OWLClass cls1, OWLClass cls2) {
		return getSubClasses(cls2, false).getFlattened().contains(cls1);
		//Checks only asserted axioms!!
		//isEntailed(super.getDataFactory().getOWLSubClassOfAxiom(cls1, cls2));
	}
	
	
	public boolean areEquivalent(OWLClass cls1, OWLClass cls2) {
		return (getEquivalentClasses(cls1).getEntities().contains(cls2)) ||
				(getEquivalentClasses(cls2).getEntities().contains(cls1));
	}
	
	
	
    public NodeSet<OWLClass> getSubClasses(OWLClassExpression ce, boolean direct) throws InconsistentOntologyException, ClassExpressionNotInProfileException, FreshEntitiesException, ReasonerInterruptedException, TimeOutException {
    	 
    	OWLClassNodeSet ns = new OWLClassNodeSet();
        
        try{        	
        	//System.out.println("Class ok: " + ce.toString());        	
        	return super.getSubClasses(ce, direct);
        }
        catch (StackOverflowError e){
    		//to catch StackOverflowError error
    		System.err.println("StackOverflowError in Structural reasoner: getSubClasses for Class " + ce.toString());
    		return ns;
    	}
    }

    public NodeSet<OWLClass> getSuperClasses(OWLClassExpression ce, boolean direct) throws InconsistentOntologyException, ClassExpressionNotInProfileException, FreshEntitiesException, ReasonerInterruptedException, TimeOutException {
        
    	OWLClassNodeSet ns = new OWLClassNodeSet();
        
        try{
        	return super.getSuperClasses(ce, direct);
        }
        catch (StackOverflowError e){
    		//to catch StackOverflowError error
    		System.err.println("StackOverflowError in Structural reasoner: getSuperClasses for Class " + ce.toString());
    		return ns;
    	}
        
        
    }


	
	public String getReasonerName(){
		return "Extended Structural Reasoner";
	}
	
	
}

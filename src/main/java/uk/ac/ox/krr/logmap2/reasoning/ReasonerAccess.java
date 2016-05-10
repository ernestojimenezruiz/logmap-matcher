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

import java.util.Set;




import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;


/**
 * This abstract class defines the interface for the classes built on top of the reasoner
 * @author Ernesto Jimenez Ruiz
 *
 */
public abstract class ReasonerAccess {
	
	//Language of subclassof relationships between atomic concepts
	public static final int LSUB = 0;
	//Language of subclassof relationships between atomic concepts and "A sub (exist R.B)"  and "(exist R.B) sub A"
	public static final int LBASIC = 1;
	//Language of subclassof relationships between active concepts (i.e. all class expression explicitly occurring in the ontology)
	public static final int LACTIVE = 2;
	
	public static final int SAT = 0;
	public static final int UNSAT = 1;
	public static final int UNKNOWN = 2;
	
	public abstract boolean isOntologyClassified();
	
	public abstract void classifyOntology_withTimeout(int timeoutSecs);
	
	public abstract void classifyOntology_withTimeout_throws_Exception(int timeoutSecs) throws Exception;
	
	public abstract void classifyOntology() throws Exception;
	
	public abstract void classifyOntology(boolean class_properties) throws Exception;
	
	public abstract OWLOntology getOntology();
	
	public abstract OWLReasoner getReasoner();
	
	public abstract OWLReasonerFactory getReasonerFactory();
	
	public abstract boolean isSatisfiable(OWLClass cls);
	
	public abstract int isSatisfiable_withTimeout(OWLClassExpression cls, int timeoutSecs);
	
	public abstract boolean isEntailed(OWLAxiom ax);
			
	public abstract boolean isSubClassOf(OWLClass cls1, OWLClass cls2);
		
	public abstract boolean areDisjointClasses(OWLClass cls1, OWLClass cls2);
	
	public abstract boolean isConsistent();
	
	public abstract boolean areEquivalentClasses(OWLClass cls1, OWLClass cls2);

	public abstract void setLanguage4Closure(int language);
	
	public abstract void createClosure();
	
	public abstract void createClosure(int language);
	
	public abstract Set<OWLAxiom> getClosure();
	
	public abstract Set<OWLClass> getUnsatisfiableClasses();
	
	public abstract boolean hasUnsatisfiableClasses();
	
	public abstract void clearStructures();
	
	//public abstract void setActiveClassExpressions(Set<OWLClassExpression> active_concepts);
	//public abstract Set<OWLClassExpression> getActiveClassExpressions();
	
}

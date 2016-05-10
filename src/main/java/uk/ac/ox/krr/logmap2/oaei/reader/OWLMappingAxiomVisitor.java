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
package uk.ac.ox.krr.logmap2.oaei.reader;

import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLAsymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLAxiomVisitor;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDatatypeDefinitionAxiom;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointUnionAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalDataPropertyAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLHasKeyAxiom;
import org.semanticweb.owlapi.model.OWLInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLIrreflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLNegativeDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLReflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLSameIndividualAxiom;
import org.semanticweb.owlapi.model.OWLSubAnnotationPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;
import org.semanticweb.owlapi.model.OWLSymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLTransitiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.SWRLRule;

public abstract class OWLMappingAxiomVisitor implements OWLAxiomVisitor {

	
	//Unique axioms used in current OWL mappings
	public abstract void visit(OWLSubClassOfAxiom arg0);
	public abstract void visit(OWLEquivalentClassesAxiom arg0);
	public abstract void visit(OWLEquivalentObjectPropertiesAxiom arg0);
	public abstract void visit(OWLSubObjectPropertyOfAxiom arg0);
	public abstract void visit(OWLEquivalentDataPropertiesAxiom arg0);
	public abstract void visit(OWLSubDataPropertyOfAxiom arg0);
	public abstract void visit(OWLSameIndividualAxiom arg0);
	
	
	

	
	public void visit(OWLAnnotationAssertionAxiom arg0) {
	
	}

	public void visit(OWLSubAnnotationPropertyOfAxiom arg0) {
	
	}

	public void visit(OWLAnnotationPropertyDomainAxiom arg0) {
	
	}

	public void visit(OWLAnnotationPropertyRangeAxiom arg0) {
	
	}

	public void visit(OWLDeclarationAxiom arg0) {
	
	}
	
	
	public void visit(OWLNegativeObjectPropertyAssertionAxiom arg0) {
	
	}

	public void visit(OWLAsymmetricObjectPropertyAxiom arg0) {
	
	}

	public void visit(OWLReflexiveObjectPropertyAxiom arg0) {
	
	}

	
	public void visit(OWLDisjointClassesAxiom arg0) {
	
	}

	public void visit(OWLDataPropertyDomainAxiom arg0) {
	
	}


	public void visit(OWLObjectPropertyDomainAxiom arg0) {
		
	}
	

	public void visit(OWLNegativeDataPropertyAssertionAxiom arg0) {
	
	}

	
	public void visit(OWLDifferentIndividualsAxiom arg0) {
	
	}

	public void visit(OWLDisjointDataPropertiesAxiom arg0) {
	
	}

	public void visit(OWLDisjointObjectPropertiesAxiom arg0) {
	
	}

	public void visit(OWLObjectPropertyRangeAxiom arg0) {
	
	}

	public void visit(OWLObjectPropertyAssertionAxiom arg0) {
	
	}

	public void visit(OWLFunctionalObjectPropertyAxiom arg0) {
	
	}

	

	public void visit(OWLDisjointUnionAxiom arg0) {
	
	}

	public void visit(OWLSymmetricObjectPropertyAxiom arg0) {
	
	}

	
	public void visit(OWLDataPropertyRangeAxiom arg0) {
	
	}

	public void visit(OWLFunctionalDataPropertyAxiom arg0) {
	
	}

	

	public void visit(OWLClassAssertionAxiom arg0) {
	
	}

	public void visit(OWLDataPropertyAssertionAxiom arg0) {

	}

	
	public void visit(OWLTransitiveObjectPropertyAxiom arg0) {
	
	}

	public void visit(OWLIrreflexiveObjectPropertyAxiom arg0) {
	
	}

	
	public void visit(OWLInverseFunctionalObjectPropertyAxiom arg0) {
	
	}

	
	

	
	public void visit(OWLSubPropertyChainOfAxiom arg0) {
	
	}

	
	public void visit(OWLInverseObjectPropertiesAxiom arg0) {
	
	}

	
	public void visit(OWLHasKeyAxiom arg0) {
	
	}

	
	public void visit(OWLDatatypeDefinitionAxiom arg0) {
	
	}

	
	public void visit(SWRLRule arg0) {
	
	}

}

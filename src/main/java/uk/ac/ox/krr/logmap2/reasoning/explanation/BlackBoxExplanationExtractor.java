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
package uk.ac.ox.krr.logmap2.reasoning.explanation;


import java.util.Collections;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import org.semanticweb.owlapi.debugging.DebuggerClassExpressionGenerator;

import uk.ac.ox.krr.logmap2.owlapi.SynchronizedOWLManager;

import com.clarkparsia.owlapi.explanation.*;

/**
 * 
 * This class uses and extends com.clarkparsia.owlapi.explanation.BlackBoxExplanation
 * Integrated within the OWLAPI
 * 
 * @author Ernesto Jimenez Ruiz
 * Nov 12, 2011
 *
 */
public class BlackBoxExplanationExtractor {

	
	BlackBoxExplanation bbexp;
	
	final ExplanationGenerator debugger;
	
	OWLDataFactory dataFactory;
	
	ExplanationProgressManager monitor;
	
	
	public BlackBoxExplanationExtractor(
			OWLOntology ontology, 
			OWLReasonerFactory reasonerFactory, 
			OWLReasoner reasoner,			
			int maxExplanations){
		this(ontology, reasonerFactory, reasoner, Collections.EMPTY_SET, maxExplanations);
	}
	
	public BlackBoxExplanationExtractor(
			OWLOntology ontology, 
			OWLReasonerFactory reasonerFactory, 
			OWLReasoner reasoner,
			Set<OWLAxiom> axioms2consider, //Only consider justifications with these axioms
			int maxExplanations){
		
		//dataFactory = OWLManager.getOWLDataFactory();
		dataFactory = SynchronizedOWLManager.createOWLDataFactory();
		
		bbexp = new BlackBoxExplanation(ontology, reasonerFactory, reasoner);
	        
		HSTExplanationGenerator hstGen = new HSTExplanationGenerator(bbexp);
		
		monitor = new  ExplanationProgressManager(axioms2consider, maxExplanations);
		hstGen.setProgressMonitor(monitor);
	        
		debugger = hstGen;
	                
	    
		
		
	}
	
	
	public void handleExplanations(OWLAxiom ax){
		
		monitor.setClearMonitor();
		
			    
	    SatisfiabilityConverter satCon = new SatisfiabilityConverter(dataFactory);
	    final OWLClassExpression desc = satCon.convert(ax);				
		debugger.getExplanations(desc); //Not necessary
		
	}
	
	public Set<Set<OWLAxiom>> getExplanations(){
		return monitor.getSetOfExplanantions();
	}
	
	
    public int getNumberExplanations(){
    	return monitor.getNumberExplanations();
    }

    public int getNumberExplanationSearch(){
    	return monitor.getNumberExplanationSearch();
    }
	
	
	public void setMaxExplanations(int maxExp){
    	monitor.setMaxExplanations(maxExp);
    }
	
	public void setMaxExplanationSearch(int maxSearch){
    	monitor.setMaxExplanationSearch(maxSearch);
    }
	
	public void setTimeOut(long miliseconds){
    	monitor.setMaxTimeOut(miliseconds);
    }
	
	public boolean isTimedOut(){
		return monitor.isCancelled();
	}
	
	
	
	/*public Set<Set<OWLAxiom>> getExplanations(OWLAxiom ax, int maxExplanations){
		
		//In old codes, seems not bein used in owl api
		//DebuggerClassExpressionGenerator gen = new DebuggerClassExpressionGenerator(dataFactory);
	    //ax.accept(gen);
	    
	    SatisfiabilityConverter satCon = new SatisfiabilityConverter(dataFactory);
	    final OWLClassExpression desc = satCon.convert(ax);
		
	   	    
	    return debugger.getExplanations(desc, maxExplanations);
	    
	    //System.out.println(ax + "  " + desc );
        //System.out.println(debugger);
	    
	}*/
	
	
	
}

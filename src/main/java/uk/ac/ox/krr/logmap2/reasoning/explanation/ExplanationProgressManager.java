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


import java.util.Set;
import java.util.HashSet;

import org.semanticweb.owlapi.model.OWLAxiom;

import uk.ac.ox.krr.logmap2.io.LogOutput;

import com.clarkparsia.owlapi.explanation.util.ExplanationProgressMonitor;


/**
 * This class is necessary to monitor the extraction of justifications. 
 * Otherwise we are unable to track the complete extraction
 * @author Ernesto Jimenez Ruiz
 * Nov 20, 2011
 *
 */
public class ExplanationProgressManager implements ExplanationProgressMonitor { 
	
		
	private Set<Set<OWLAxiom>> SetOfJustAxioms = new HashSet<Set<OWLAxiom>>();
	
	boolean cancelled=false;
	
	
	//Max is important to speed up experiments
	private int maxExplanations=50;
	private int numberExplanations=0;
		
	private int maxExplanationSearch=500; //reduce if necessary
	private int explanationSearch=0;
	
	private Set<OWLAxiom> axioms2consider;
	
	//miliseconds
	private long max_time = 60000; //60s
	private long time_out;
	
	
	public ExplanationProgressManager(Set<OWLAxiom> axioms2consider, int maxExplanations){
		
		this.axioms2consider = axioms2consider;
		this.maxExplanations = maxExplanations;
		cancelled=false;
	}
	
	
	
	
	
	 public boolean isCancelled() {
		 if (System.currentTimeMillis() > time_out){
			//LogOutput.print("Time out. Found explanations: " + numberExplanations);
			cancelled=true;
		 }
		 
	     return cancelled;
	     
	    }


	  
	 public void foundExplanation(Set<OWLAxiom> set) {
	     handleFoundExplanation(set);		 
	  }


	 public void foundAllExplanations() {
		 cancelled=true;
		//LogOutput.print("Found All explanations: " + numberExplanations);
	 }
	    
	    
	  private void handleFoundExplanation(Set<OWLAxiom> explanation){
	    	
		  	if (cancelled)//Stop!!
		  		return;
		  
		  	explanationSearch++;
		  	
	    	
	    	if (!SetOfJustAxioms.contains(explanation)){
	    		
	    		//Intersection if axioms2consider is non empty
	    		if (axioms2consider.size()>0)
	    			explanation.retainAll(axioms2consider);
	    		
	    		
	    		if (explanation.size()>0){
	    			
	    			//LogOutput.print("EXP: " + explanation.toString());
	    			
		    		SetOfJustAxioms.add(explanation);
		    		
		    		numberExplanations++;
		    		//LogOutput.print("Found explanation: " + numberExplanations);
	    		}
	    		
	    	}
	    	//LogOutput.print("Found explanation (SEARCH): " + explanationSearch);
	    	
			
			//Cancell situation
			//Max of explanations + max number of equal explanantions for our interests 
			//if (numEqualSets>=maxNumEqualSets || numberExplanations>=maxExplanations){
			if (numberExplanations>=maxExplanations || explanationSearch > maxExplanationSearch
					|| System.currentTimeMillis() > time_out){
				//LogOutput.print("Time out. Found explanations: " + numberExplanations);
				cancelled=true;
				//foundAllExplanations();
					
			}
		}
	    
	    
	    public Set<Set<OWLAxiom>> getSetOfExplanantions(){
	    	return SetOfJustAxioms;
	    }
	    
	    public void setClearMonitor(){
	    	SetOfJustAxioms.clear();
	    	cancelled=false;
	    	numberExplanations=0;
			explanationSearch=0;
			
			time_out = System.currentTimeMillis() + max_time;
		    
		    
	    	
	    }

	    
	    public void setMaxExplanations(int maxExp){
	    	maxExplanations = maxExp;
	    }
	    
	    public void setMaxExplanationSearch(int maxSearch){
	    	maxExplanationSearch = maxSearch;
	    }


	    
	    public void setMaxTimeOut(long milisec){
	    	max_time = milisec;
	    }

		
	    
	    
	    public int getNumberExplanations(){
	    	return numberExplanations;
	    }
	
	    public int getNumberExplanationSearch(){
	    	return explanationSearch;
	    }
	
	
	

}

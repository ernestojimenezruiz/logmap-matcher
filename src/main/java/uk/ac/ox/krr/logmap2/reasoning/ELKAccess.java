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

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import org.semanticweb.elk.owlapi.ElkReasonerAdapted;
import org.semanticweb.elk.owlapi.ElkReasonerFactory;


/**
 * ELK reasoner access
 * @author Ernesto Jimenez Ruiz
 * Nov 18, 2011
 *
 */
public class ELKAccess extends ReasonerAccessImpl {
	
	public ELKAccess(OWLOntologyManager ontoManager, OWLOntology onto, boolean useFactory) throws Exception{		
		super(ontoManager, onto, useFactory);
		
	}
	
	
	protected void setUpReasoner(boolean useFactory) throws Exception{
		
		Logger.getLogger("org.semanticweb.elk").setLevel(Level.OFF);
		
		reasonerFactory = new ElkReasonerFactory();
		
		if (useFactory){	 
			reasoner = reasonerFactory.createReasoner(ontoBase);
		}		
		else{
			reasoner=new ElkReasonerAdapted(ontoBase);
		}
		
		reasonerName = "ELK";

		
	}

}

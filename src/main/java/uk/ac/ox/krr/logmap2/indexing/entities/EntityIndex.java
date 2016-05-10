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
package uk.ac.ox.krr.logmap2.indexing.entities;

import uk.ac.ox.krr.logmap2.indexing.labelling_schema.Node;

public abstract class EntityIndex {

	
	protected int onto_index;
	
	protected int index;
	
	//Interval labelling index
	protected Node node;
	
	protected String namespace; 
	
	
	//Lexical Indexation
	protected String name4Entitity;	
	protected String label4Entity;
	
	
	/**
	 * In order to identify ontology of entity
	 * @param entityName
	 */
	public void setOntologyId(int ontoindex){
		onto_index=ontoindex;
	}
	
	public int getOntologyId(){
		return onto_index;
	}
	
	public boolean equals(String entityname)
	{
		return name4Entitity.equals(entityname);
	}
	
	public void setEntityName(String entityName){
		name4Entitity=entityName;
	}
	
	/**
	 * The class name in the URI			
	 * @return
	 */
	public String getEntityName(){
		return name4Entitity;
	}
	
	
	public void setLabel(String label){
		label4Entity=label;
	}
	
	/**
	 * Usually represents the preferred class name
	 * @return
	 */
	public String getLabel(){
		return label4Entity;
	}
	
	
	/**
	 * Given IRI within the ontology
	 * @param baseIRI
	 * @return
	 */
	public String getIRI(String baseIRI){
		if (hasDifferentNamespace()){
			if (namespace.equals(name4Entitity)){
				return namespace; //Cases in which uri has not '#'
			}
			else {
				//For URIS like http://ontology.dumontierlab.com/hasReference
				if (namespace.endsWith("/"))
					return namespace + name4Entitity;
				else
					return namespace + "#" + name4Entitity;
			}
		}
		//
		if (baseIRI.endsWith("/")){
			return baseIRI +  name4Entitity;
		}
		else {
			return baseIRI + "#" + name4Entitity;
		}
	}
	
	
	/**
	 * @param namespace the namespace to set
	 */
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	
	


	/**
	 * @return the namespace of the entity if it has one
	 */
	public String getNamespace() {
		if (hasDifferentNamespace())
			return namespace;
		else
			return "";
	}
	
	
	/**
	 * If the namespace of the entity is different from the ontology namespace we store ir
	 * @return
	 */
	public boolean hasDifferentNamespace(){
		if (namespace==null || namespace.equals(""))
			return false;
		return true;		
	}
	
	
	

	
}

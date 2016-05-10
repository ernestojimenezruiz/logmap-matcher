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

import java.util.HashSet;
import java.util.Set;

import uk.ac.ox.krr.logmap2.mappings.objects.MappingObjectStr;

/**
 * 
 * Abstracts class for mapping readers
 * 
 * @author Ernesto Jimenez Ruiz
 *
 */
public abstract class MappingsReader {

	public static final String ENTITY1 = "entity1";
	public static final String ENTITY2 = "entity2";
	public static final String RELATION = "relation";
	public static final String MEASURE = "measure";
	
	protected Set<MappingObjectStr> mappings = new HashSet<MappingObjectStr>();
	
	
	
	public Set<MappingObjectStr> getMappingObjects(){
		return mappings;
	}
	
	public int getMappingObjectsSize(){
		return mappings.size();
	}
	
}

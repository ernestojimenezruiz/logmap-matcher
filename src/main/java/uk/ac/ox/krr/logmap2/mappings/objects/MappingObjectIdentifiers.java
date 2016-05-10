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
package uk.ac.ox.krr.logmap2.mappings.objects;

public class MappingObjectIdentifiers extends MappingObject {

	//private int dir_mapping;
	
	//DIR IMPLICATION
	//public static int L2R=0; //P->Q
	//public static int R2L=-1; //P<-Q
	//public static int EQ=-2; //P<->Q

	
	
	
	
	public MappingObjectIdentifiers(int ide1, int ide2){
		//this(ide1, ide2, 0.0);
		ident_onto1=ide1;
		ident_onto2=ide2;
	}
	
	/*public MappingObjectIdentifiers(int ide1, int ide2, double score){
	
		ident_onto1=ide1;
		ident_onto2=ide2;
		confidence=score;
		
	}*/
	

	
	
	
	
}

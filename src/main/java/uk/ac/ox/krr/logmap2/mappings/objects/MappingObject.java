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


public abstract class MappingObject {

	protected int ident_onto1;
	protected int ident_onto2;
	
	
	
	
	public int getIdentifierOnto1(){
		return ident_onto1;
	}
	
	public int getIdentifierOnto2(){
		return ident_onto2;
	}
	
	
	public void setIdentifierOnto1(int ident){
		this.ident_onto1 = ident;
	}
	
	public void setIdentifierOnto2(int ident){
		this.ident_onto2 = ident;
	}
	
	
	
	
	
	public boolean equals(Object o){
		
		if  (o == null)
			return false;
		if (o == this)
			return true;
		if (!(o instanceof MappingObject))
			return false;
		
		MappingObject i =  (MappingObject)o;
		
		return equals(i);
		
	}
	
	
	public boolean equals(MappingObject m){
		
		if (ident_onto1!=m.getIdentifierOnto1() || ident_onto2!=m.getIdentifierOnto2()){
			return false;
		}
		return true;
	}
	
	public String toString(){
		return "<"+ident_onto1+"=="+ident_onto2+">";
	}
	
	public  int hashCode() {
		  int code = 10;
		  code = 40 * code + ident_onto1;
		  code = 40 * code + ident_onto2;
		  return code;
	}
	
	
	
}

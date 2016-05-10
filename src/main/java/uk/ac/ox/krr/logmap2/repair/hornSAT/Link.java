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
package uk.ac.ox.krr.logmap2.repair.hornSAT;


public class Link {

	
	private int labelH;
	private int target;
	
	
	public Link(int label, int targetlink){
		labelH=label;
		target=targetlink;
	}
	
	public int getLabelLink(){
		return labelH;	
	}
	
	public int getTargetLink(){
		return target;
	}
	
	
	public String toString(){
		return "< h=" + labelH + ", t="+target + " >"; 
	}
	
	
	
	public boolean equals(Object o){
		
		//System.out.print(this + " =?" + o);
		
		if  (o == null)
			return false;
		if (o == this)
			return true;
		if (!(o instanceof Link))
			return false;
		
		Link i =  (Link)o;
		
		return equals(i);
		
	}
	
	
	
	public boolean equals(Link i){
		
		if (labelH!=i.getLabelLink() || target!=i.getTargetLink()){
			return false;
		}
		
		return true;

	}
	
	public  int hashCode() {
		  int code = 10;
		  code = 40 * code + labelH;
		  code = 50 * code + target;			  
		  return code;
	}
	
}

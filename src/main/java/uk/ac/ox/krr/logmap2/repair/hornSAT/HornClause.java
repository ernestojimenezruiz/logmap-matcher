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

import java.util.*;




public class HornClause{
	
	private int labelh;
	private int labelh_pair=-1; //when it is not set
	
	private Set<Integer> leftHandSideSet = new HashSet<Integer>();
	//For practical issues, we only consider clauses of the type p1^p2->p3 at most (only three propositions)???
	private int leftHandSide1;
	private int leftHandSide2;
	private int rightHandSide;
	
	private int origin; //onto1, onto2, mapping
	private int dirImplication;
	
	public static final int MAP=0;
	public static final int ONTO=1;
	//public static int ONTO1=1;	
	//public static int ONTO2=2;
	public static final int FIXEDMAP=3;
	
	//DIR IMPLICATION
	public static final int L2R=0; //P->Q
	public static final int R2L=-1; //P<-Q
	
	
	
	//Ontology concepts are indexed from '0' onwards
	private int TRUE = -1; //Node true
	private int FALSE = -2; //Node false
	
	
	
	/**
	 * Cases p->q
	 * @param lefttHS1 Origin proposition
	 * @param rightHS Target proposition
	 * @param label Label of the clause used in D&Galgorithm
	 * @param type Origin of clause: onto1, onto2, or mappings
	 */
	public HornClause(int leftHS1, int rightHS, int label, int type){
	
		this(leftHS1, rightHS, label, type, L2R); //Default

	}
	
	
	/**
	 * Cases p->q or p<-q
	 * @param lefttHS1 Origin proposition
	 * @param rightHS Target proposition
	 * @param label Label of the clause used in D&Galgorithm
	 * @param type Origin of clause: onto1, onto2, or mappings
	 * @param dir Direction of implication
	 */
	public HornClause(int leftHS1, int rightHS, int label, int type, int dir){
	
		labelh=label;
		leftHandSide1=leftHS1;
		leftHandSide2=TRUE;//we intersect with TRUE/TOP
		rightHandSide=rightHS;
		origin=type;
		
		dirImplication=dir; 

	}
	
	
	
	

	
	/**
	 * Cases p^q->r or p^q->false
	 * @param lefttHS1 Origin proposition 1
	 * @param lefttHS2 Origin proposition 2
	 * @param rightHS Target proposition
	 * @param label Label of the clause used in D&Galgorithm
	 * @param type Origin of clause: onto1, onto2, or mappings
	 */
	public HornClause(int leftHS1, int leftHS2, int rightHS, int label, int type, int dir){
	
		
		labelh=label;
		leftHandSide1=leftHS1;
		leftHandSide2=leftHS2;
		rightHandSide=rightHS;
		origin=type;
		
		dirImplication=dir;

	}
	
	/**
	 * Cases p^q->r or p^q->false
	 * @param lefttHS1 Origin proposition 1
	 * @param lefttHS2 Origin proposition 2
	 * @param rightHS Target proposition
	 * @param label Label of the clause used in D&Galgorithm
	 * @param type Origin of clause: onto1, onto2, or mappings
	 */
	public HornClause(Set<Integer> leftHS_set, int rightHS, int label, int type, int dir){
	
		
		labelh=label;
		leftHandSideSet=leftHS_set; //Only in this case is not empty
		rightHandSide=rightHS;
		origin=type;
		
		dirImplication=dir;

	}
	
	
	
	public int getLabel(){
		return labelh;
	}
	
	
	/**
	 * Only for equivalence mappings since we split between p->q and p<-q
	 * @return
	 */
	public int getLabelPair(){
		return labelh_pair;
	}
	
	public void setLabelPair(int label){
		labelh_pair=label;
	}
	
	
	
	public Set<Integer> getLeftHSSet(){
		return leftHandSideSet;
	}
	
	
	public int getLeftHS1(){
		return leftHandSide1;
	}
	
	
	
	public int getLeftHS2(){
		return leftHandSide2;
	}
	
	
	public int getRightHS(){
		return rightHandSide;
	}
	
	
	public int getOrigin(){
		return origin;
	}
	
	public int getDirImplication(){
		return dirImplication;
	}
	
	
	public String toString(){
				
		//int shift=DowlingGallierHornSAT.shift_onto2;
		
		String str="";
		int LHS1;
		int LHS2=TRUE;
		int RHS=FALSE;
		
		if (leftHandSideSet.size()>0){
			RHS=rightHandSide;
			for (int lhs: leftHandSideSet){
				str+=lhs+"^";
			}
		}
		else{	
			LHS1=leftHandSide1;
			
			if (origin==ONTO){			
				LHS2=leftHandSide2;
				RHS=rightHandSide;
				
			}
			
			else if (origin==MAP || origin==FIXEDMAP){
				if (rightHandSide>FALSE)
					RHS=rightHandSide;//-shift;
			}
			
			
			str=String.valueOf(LHS1);		
			
			if (LHS2>TRUE){		
					str+="^"+LHS2;
			}		
		}
		
		
		
		if (dirImplication==L2R)
			str+=" -> " + RHS + " (" + origin +")" + " (" + labelh +")";
		else
			str+=" <- " + RHS + " (" + origin +")" + " (" + labelh +")";
		
		return str;
		
	}
	
	
	
	public boolean equals(Object o){
		
		//System.out.print(this + " =?" + o);
		
		if  (o == null)
			return false;
		if (o == this)
			return true;
		if (!(o instanceof HornClause))
			return false;
		
		HornClause hc =  (HornClause)o;
		
		return equals(hc);
		
	}
	
	
	
	public boolean equals(HornClause hc){
		
		//For equality we do not consider the labels (it is dependent to the current d&g setting) 
		
		if (leftHandSideSet.size()>0){
			
			if (//labelh!=hc.getLabel() || 
					leftHandSideSet.equals(hc.getLeftHSSet()) ||
					rightHandSide!=hc.getRightHS() ||
					//origin!=hc.getOrigin() ||
					dirImplication!=hc.getDirImplication()){
				return false;
			}
		}
		
		else if (//labelh!=hc.getLabel() || 
				leftHandSide1!=hc.getLeftHS1() ||
				leftHandSide2!=hc.getLeftHS2() ||
				rightHandSide!=hc.getRightHS() ||
				//origin!=hc.getOrigin() ||
				dirImplication!=hc.getDirImplication()){
			return false;
		}
		
		return true;

	}
	
	public  int hashCode() {
			
		  int code = 10;
		  
		  if (leftHandSideSet.size()>0){
			  code = 50 * code + leftHandSideSet.hashCode();
		  }
		  else{
			  //code = 40 * code + labelh;
			  code = 50 * code + leftHandSide1;
			  code = 60 * code + leftHandSide2;
		  }
		  code = 70 * code + rightHandSide;
		  //code = 80 * code + (origin+5);
		  code = 90 * code + dirImplication;
		  return code;
	}
	
	
	
	public static void main(String[] args) {
		
		Set<HornClause> set1 = new HashSet<HornClause>();
		Set<HornClause> set2 = new HashSet<HornClause>();
		
		
		
		HornClause h1 = new HornClause(45, 1902, 1, MAP, L2R);
		HornClause h2 = new HornClause(45, 1902, 3, MAP, L2R);
		
		set1.add(h1);
		//set1.add(h2);
		
		set2.add(h2);
		
		System.out.println(h1.equals(h2));
		System.out.println(h2.equals(h1));
		
		System.out.println(set2.contains(h1));
		System.out.println(set1.contains(h2));
		
		
		
	}

}

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



public class MappingObjectStr {

	String Iri_ent1_str;
	String Iri_ent2_str;
	double confidence;
	
	double lexical_conf;
	double scope_conf;
	
	int dir_mappings;
	int typeMappings;//classes, properties, instances

	
	
	//DIR IMPLICATION
	public static final int SUB = 0; //L2R=0; //P->Q
	public static final int SUP=-1; //R2L P<-Q
	public static final int EQ=-2; //P<->Q
	//-3 is reserved in LigMap for otehr things
	public static final int Flagged=-4; //Flagged mappings in Largebio
	 
	
	
	//TYPE OF MAPPING
	public static final int CLASSES=0;
	public static final int DATAPROPERTIES=1;
	public static final int OBJECTPROPERTIES=2;
	public static final int INSTANCES=3;
	public static final int UNKNOWN=4;
	
	
	
	public MappingObjectStr(String iri_ent1, String iri_ent2){
		
		Iri_ent1_str=iri_ent1;
		Iri_ent2_str=iri_ent2;
		confidence=-1;
		dir_mappings=MappingObjectStr.EQ;
		typeMappings = MappingObjectStr.UNKNOWN;
	}
	
	public MappingObjectStr(String iri_ent1, String iri_ent2, double conf){
		
		Iri_ent1_str=iri_ent1;
		Iri_ent2_str=iri_ent2;
		confidence=conf;
		dir_mappings=MappingObjectStr.EQ;
		typeMappings = MappingObjectStr.UNKNOWN;
		
	}
	
	public MappingObjectStr(String iri_ent1, String iri_ent2, double conf, int dir_mapping){
		
		Iri_ent1_str=iri_ent1;
		Iri_ent2_str=iri_ent2;
		confidence=conf;
		dir_mappings=dir_mapping;
		typeMappings = MappingObjectStr.UNKNOWN;		
	}
	
    public MappingObjectStr(String iri_ent1, String iri_ent2, double conf, int dir_mapping, int typeMapping){
		
		Iri_ent1_str=iri_ent1;
		Iri_ent2_str=iri_ent2;
		confidence=conf;
		dir_mappings=dir_mapping;
		typeMappings = typeMapping;		
	}
    
    
    public MappingObjectStr(MappingObjectStr m){		
		this(m.getIRIStrEnt1(), m.getIRIStrEnt2(), m.getConfidence(), m.getMappingDirection(), m.getTypeOfMapping());		
	}
	

	public int getMappingDirection(){
		return dir_mappings;
	}
	
	public String getIRIStrEnt1(){
		return Iri_ent1_str;
		
	}
	
	public String getIRIStrEnt2(){
		return Iri_ent2_str;
		
	}
	
	public double getConfidence(){
		return confidence;
		
	}
	
	public boolean isClassMapping(){
		return getTypeOfMapping()==CLASSES;
	}
	
	public boolean isInstanceMapping(){
		return getTypeOfMapping()==INSTANCES;
	}
	
	public boolean isDataPropertyMapping(){
		return getTypeOfMapping()==DATAPROPERTIES;
	}
	
	public boolean isObjectPropertyMapping(){
		return getTypeOfMapping()==OBJECTPROPERTIES;
	}
	
	public int getTypeOfMapping(){
		return typeMappings;
		
	}
	
	public void setTypeOfMapping(int type){
		typeMappings = type;
		
	}
	
	
	public void setDirMapping(int dir){
		dir_mappings = dir;		
	}
	
	public void setIRIStrEnt1(String iri1){
		Iri_ent1_str=iri1;
		
	}
	
	public void setIRIStrEnt2(String iri2){
		Iri_ent2_str=iri2;
		
	}
	
	
	public void setConfidenceMapping(double conf){
		confidence = conf;
		
	}
	
	public void setLexicalConfidenceMapping(double conf){
		lexical_conf = conf;
		
	}
	
	public void setScopeConfidenceMapping(double conf){
		scope_conf = conf;
		
	}
	
	
	/**
	 * Gets lexical score of a mapping. Based on ISUB algorithm
	 * @return
	 */
	public double getLexicalConfidenceMapping(){
		return lexical_conf;
		
	}
	
	/**
	 * Gets scope/structural score of a mapping
	 * @return
	 */
	public double getStructuralConfidenceMapping(){
		return scope_conf;
		
	}
	
	
	
	
	public boolean equals(Object o){
		
		if  (o == null)
			return false;
		if (o == this)
			return true;
		if (!(o instanceof MappingObjectStr))
			return false;
		
		MappingObjectStr i =  (MappingObjectStr)o;
		
		return equals(i);
		
	}
	
	
	public boolean equals(MappingObjectStr m){
		
		//TODO: maybe the mapping is in the other from ent2 to ent1
		if (!Iri_ent1_str.equals(m.getIRIStrEnt1()) || !Iri_ent2_str.equals(m.getIRIStrEnt2())){
			return false;
		}
		return true;
	}
	
	public String toString(){
		if (dir_mappings==EQ)
			return "<"+Iri_ent1_str+"=="+Iri_ent2_str+">";
		else if (dir_mappings==SUB)
			return "<"+Iri_ent1_str+"<"+Iri_ent2_str+">";
		else
			return "<"+Iri_ent1_str+">"+Iri_ent2_str+">";
	}
	
	public  int hashCode() {
		  int code = 10;
		  code = 40 * code + Iri_ent1_str.hashCode();
		  code = 50 * code + Iri_ent2_str.hashCode();
		  return code;
	}
	
}

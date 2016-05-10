package uk.ac.ox.krr.logmap2.bioportal;

import java.util.HashSet;
import java.util.Set;

public class BioPortalMapping {

	String source_onto;
	String target_onto;
	
	String source_entity;
	String target_entity;
	
	//http://www.bioontology.org/wiki/index.php/BioPortal_Mappings
	Set<String> mapping_sources = new HashSet<String>(); 
		
	double confidence;
	int dir_mapping;
	int typeMapping;//classes, properties, instances

	
	//DIR IMPLICATION
	public static final int SUB = 0; //L2R=0; //P->Q
	public static final int SUP=-1; //R2L P<-Q
	public static final int EQ=-2; //P<->Q
	 
	
	
	//TYPE OF MAPPING
	public static final int CLASSES=0;
	public static final int DATAPROPERTIES=1;
	public static final int OBJECTPROPERTIES=2;
	public static final int INSTANCES=3;
	public static final int UNKNOWN=4;
	
	
	
	
	
	public BioPortalMapping(
			String source_entity, 
			String target_entity, 
			String source_onto, 
			String target_onto,
			Set<String> sources){
		
		this.source_entity=source_entity;
		this.target_entity=target_entity; 
		this.source_onto=source_onto; 
		this.target_onto=target_onto;
		
		mapping_sources.addAll(sources);
		
		confidence = 1.0;
		dir_mapping = EQ;
		typeMapping = CLASSES;
		
	}
	
	

	public String getSourceEntity(){
		return source_entity;
		
	}
	
	public String getTargetEntity(){
		return target_entity;
		
	}
	
	public String getSourceOntology(){
		return source_onto;
		
	}
	
	public String getTargetOntology(){
		return target_onto;
		
	}
	
	
	public int getMappingDirection(){
		return dir_mapping;
	}
	
	
	public double getConfidence(){
		return confidence;
		
	}
	
	
	public int getTypeOfMapping(){
		return typeMapping;
		
	}
	
	public void setTypeOfMapping(int type){
		typeMapping = type;
		
	}
	
	public void setConfidenceMapping(double conf){
		confidence = conf;
		
	}
	
	public void setMappingDirection(int dir){
		dir_mapping=dir;
	}
	
	
	public boolean equals(Object o){
		
		if  (o == null)
			return false;
		if (o == this)
			return true;
		if (!(o instanceof BioPortalMapping))
			return false;
		
		BioPortalMapping i =  (BioPortalMapping)o;
		
		return equals(i);
		
	}


	public boolean equals(BioPortalMapping m){
		
		if (!source_entity.equals(m.getSourceEntity()) 
				|| !target_entity.equals(m.getTargetEntity())
				|| !source_onto.equals(m.getSourceOntology())
				|| !target_onto.equals(m.getTargetOntology())){
			return false;
		}
		return true;
	}
	
	public String toString(){
		return "<"+source_onto + ":" +source_entity+"=="+target_onto+":"+target_entity+">";
	}
	
	public  int hashCode() {
		  int code = 10;
		  code = 40 * code + source_entity.hashCode();
		  code = 50 * code + target_entity.hashCode();
		  code = 60 * code + source_onto.hashCode();
		  code = 80 * code + target_onto.hashCode();
		  return code;
	}
	
	
	
}

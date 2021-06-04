package uk.ac.ox.krr.logmap_lite;


public class MappingObjectStr {

	String Iri_ent1_str;
	String Iri_ent2_str;
	double confidence;
	int sourceMapping; //0: exact, l1, l2, l3
	
	
	
	public MappingObjectStr(String iri_ent1, String iri_ent2){
		
		Iri_ent1_str=iri_ent1;
		Iri_ent2_str=iri_ent2;
		confidence=1.0;
		sourceMapping=0;
	}
	
	public MappingObjectStr(String iri_ent1, String iri_ent2, double conf){
		
		Iri_ent1_str=iri_ent1;
		Iri_ent2_str=iri_ent2;
		confidence=conf;
		sourceMapping=0;
		
	}
	
public MappingObjectStr(String iri_ent1, String iri_ent2, double conf, int sourcemapping){
		
		Iri_ent1_str=iri_ent1;
		Iri_ent2_str=iri_ent2;
		confidence=conf;
		sourceMapping=sourcemapping;
		
	}
	

	public int getSourcemapping(){
		return sourceMapping;
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
		return "<"+Iri_ent1_str+"=="+Iri_ent2_str+">";
	}
	
	public  int hashCode() {
		  int code = 10;
		  code = 40 * code + Iri_ent1_str.hashCode();
		  code = 40 * code + Iri_ent2_str.hashCode();
		  return code;
	}
	
}

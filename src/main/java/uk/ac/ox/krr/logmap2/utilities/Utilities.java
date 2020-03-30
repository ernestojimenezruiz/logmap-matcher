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
package uk.ac.ox.krr.logmap2.utilities;

import java.util.ArrayList;


import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.util.NamespaceUtil;
import org.semanticweb.owlapi.util.OntologyIRIShortFormProvider;

import uk.ac.ox.krr.logmap2.indexing.labelling_schema.Interval;
import uk.ac.ox.krr.logmap2.io.LogOutput;


public class Utilities {
	
	
	//LOGMAP VERSION
	public static final int LOGMAP = 0;
	public static final int LOGMAPMENDUM = 1;
	public static final int LOGMAPLITE = 2;
	public static final int LOGMAPINTERACTIVITY = 3;
	
	
	//WEAK MAPPINGS LEVELS
	public static final int WEAK_LEVEL1=1;
	public static final int WEAK_LEVEL2=2;
	public static final int WEAK_LEVEL3=3;
	
	//ORIGIN of AXIOM CLAUSE
	public static final int MAP=0;
	public static final int ONTO1=1;	
	public static final int ONTO2=2;
	
	//DIR IMPLICATION
	public static final int L2R=0; //P->Q
	public static final int R2L=-1; //P<-Q
	public static final int EQ=-2; //P<->Qversion = Utilities.LOGMAPMENDUM;
	public static final int NoMap=-3; 
	public static final int Flagged=-4; //Flagged mappinsg in Largebio 
	
	
	//TYPE OF MAPPING
	public static final int CLASSES=0;
	public static final int DATAPROPERTIES=1;
	public static final int OBJECTPROPERTIES=2;
	public static final int INSTANCES=3;
	public static final int UNKNOWN=4;
	
	public static final String CLASSES_STR="CLS";
	public static final String DATAPROPERTIES_STR="DPROP";
	public static final String OBJECTPROPERTIES_STR="OPROP";
	public static final String INSTANCES_STR="INST";
	
	
	//Reasoner
	public static final int STRUCTURAL_REASONER=0;
	public static final int HERMIT_REASONER=1;
	public static final int CONDOR_INPUT=2;
	
	//Ontos
	public static final int FMA=0;
	public static final int NCI=1;
	public static final int SNOMED=2;
	public static final int Chemo=3;
	public static final int NCIAn=4;
	public static final int Mouse=5;
	public static final int ontoA=6;
	public static final int ontoB=7;
	
	//Pairs
	public static final int FMA2NCI=0;
	public static final int FMA2SNOMED=1;
	public static final int SNOMED2NCI=2;
	public static final int SNOMED2LUCADA=3;
	public static final int OntoA2OntoB=4;
	public static final int MOUSE2HUMAN=5;
	public static final int NCIpeque2FMA=6;
	public static final int NCI2FMApeque=7;
	public static final int NCI2LUCADA=8;
	public static final int FMA2LUCADA=9;
	public static final int LIBRARY=10;
	public static final int CONFERENCE=11;
	public static final int INSTANCE=12;
	public static final int MULTILINGUAL=13;
	
	
	public static final int CONFLICT=0;
	public static final int DANG_EQUIV=1;
	public static final int DANG_SUB=2;
	
	public static final int NOCONFLICT=3;
	
	public static final int DISPARATE=4;
	//public static final int DISP_PATH=5;
	
	public static final int COMPATIBLE=6;

	
	public static String getStringRepresentation4Dir(int dir){
		
		if (dir == EQ){
			return "=";
		}
		else if (dir == L2R){
			return "<";
		}
		else{
			return ">";
		}
		
	}
	
	
	public static int getIntegerRepresentation4Dir(String dir){
		
		if (dir.equals("=")){
			return EQ;
		}
		else if (dir.equals("<")){
			return L2R;
		}
		else{
			return R2L;
		}
		
	}
	
	
	public static double getRoundValue(double value, int decimals){
		//double aux = 10.0 * ((double)decimals);
		double aux = Math.pow(10.0, (double)decimals);
		
		return (double)Math.round(value*aux)/aux; 
	}
	
	public static String serializeIntervals(List<Interval> listIntervals){
		
		String intervalsStr="";
		
		for (int i=0; i<listIntervals.size()-1; i++){
			intervalsStr+=listIntervals.get(i).serialize() +",";
		}
		if (listIntervals.size()>0)
			intervalsStr+=listIntervals.get(listIntervals.size()-1).serialize();
		
		return intervalsStr;
	}

	/**
	 * (Anton)
	 * @param listIntervals
	 * @return
	 */
	public static String serializeIntervals(Set<Interval> setIntervals){		
		String intervalsStr="";		
		Iterator<Interval> it = setIntervals.iterator();
		
		if(it.hasNext())
			intervalsStr += it.next().serialize();
		
		while(it.hasNext()) {
			Interval i = it.next();
			intervalsStr += "," + i.serialize();
		}
		
		return intervalsStr;
	}
	
	
	
	public static Set<Interval> deserializeIntervals(String intervalsStr){
		//List<Interval> listIntervals = new ArrayList<Interval>();
		Set<Interval> listIntervals = new HashSet<Interval>();
		
		
		String[] listIndexes = intervalsStr.split(",");
		
		for (int i=0; i<listIndexes.length-1; i+=2){
			
			listIntervals.add(new Interval(
					Integer.valueOf(listIndexes[i]),
					Integer.valueOf(listIndexes[i+1])));
		
		}
		
		return listIntervals;
	}
	
	
	public static String serializeListIntegers(List<Integer> list){
		
		String liststr="";
		
		if (list.size()>0){
			for (int i=0; i<list.size()-1; i++){
				liststr+=list.get(i)+",";
			}
			liststr+=list.get(list.size()-1);
		}			
		return liststr;
	}
	
	/**
	 * (Anton)
	 * @param set
	 * @return
	 */
	public static String serializeSetIntegers(Set<Integer> set){
		String setstr="";		
		Iterator<Integer> it = set.iterator();
		
		if(it.hasNext())
			setstr += it.next();
		
		while(it.hasNext()) {
			int i = it.next();
			setstr += "," + i;
		}
		
		return setstr;
	}
	
	
	//private static Set<Integer> list_int = new HashSet<Integer>();
	//private static Set<String> list_str = new HashSet<String>();
	
	public static Set<Integer> deserializeListIntegers(String serializedlist){
		
		Set<Integer> list_int = new HashSet<Integer>();
		//list_int.clear();
		
		String[] elements;
		
		if (serializedlist.indexOf(",")>0){
			elements=serializedlist.split(",");
			for (int i=0; i<elements.length; i++)
				list_int.add(Integer.valueOf(elements[i]));
		}
		else{
			list_int.add(Integer.valueOf(serializedlist));
		}
			
		//System.out.println(list);
		return list_int;
	}
	
	
	public static String serializeListStrings(List<String> list){
		
		String liststr="";
		
		if (list.size()>0){
			for (int i=0; i<list.size()-1; i++){
				liststr+=list.get(i)+",";
			}
			liststr+=list.get(list.size()-1);
		}			
		return liststr;
	}
	
	
	public static Set<String> deserializeListStrings(String serializedlist){
		
		Set<String> list_str = new HashSet<String>();
		//list_str.clear();
		
		String[] elements;
		
		if (serializedlist.indexOf(",")>0){
			elements=serializedlist.split(",");
			for (int i=0; i<elements.length; i++)
				list_str.add(elements[i]);
		}
		else{
			list_str.add(serializedlist);
		}
			
		//System.out.println(list);
		return list_str;
	}
	
	
	public static String deserializeListStrings2String(String serializedlist){ //removes comas
		
		//Set<String> list_str = new HashSet<String>();
		String str="";
		//list_str.clear();
		
		String[] elements;
		
		if (serializedlist.indexOf(",")>0){
			elements=serializedlist.split(",");
			for (int i=0; i<elements.length; i++)
				str+=elements[i];
		}
		else{
			str=serializedlist;
		}
		
		//System.out.println(list);
		return str;
	}
	
	
	
	/**
	 * @deprecated
	 * @param uriStr
	 * @return
	 */
	public static String getEntityLabelFromURI_original(String uriStr){
		if (uriStr.indexOf("#")>=0)
			return uriStr.split("#")[1];
		return uriStr;
	}
	
	
	public static String getEntityLabelFromURI(String uriStr){
		
		//LogOutput.print(uriStr + "  " + uriStr.indexOf("#") + "  " + uriStr.split("#").length);
		
		if (uriStr.indexOf("#")>=0){// && uriStr.indexOf("#")<uriStr.length()-1){
			
			if (uriStr.split("#").length>1){
				//LogOutput.print(uriStr.split("#")[1]);
				//Problem if there are several "#"
				//return uriStr.split("#")[1];
				
				int index = uriStr.indexOf("#"); //First occurrence
				return uriStr.substring(index+1);
				
			}
			else{
				LogOutput.print("Empty label: " + uriStr);
				return "empty"+ Calendar.getInstance().getTimeInMillis();
			}
		}
		//else {
		//For URIS like http://ontology.dumontierlab.com/hasReference
		int index = uriStr.lastIndexOf("/");
		if (index>=0){
			//LogOutput.print(uriStr.substring(index+1));
			//if 
			return uriStr.substring(index+1);
		}
		//}
		LogOutput.print("Complete URI: " + uriStr);
		
		return uriStr;
	}
	
	
	/**
	 * @deprecated
	 * @param uriStr
	 * @return
	 */
	public static String getNameSpaceFromURI_original(String uriStr){
		if (uriStr.startsWith("http")){
			if (uriStr.indexOf("#")>=0)
				return uriStr.split("#")[0];
			else
				return uriStr;
		}
		else
			return "";
		
	}
	
	
	public static String getNameSpaceFromURI(String uriStr){
		if (uriStr.startsWith("http")){
			if (uriStr.indexOf("#")>=0){
				return uriStr.split("#")[0];
			}
			else {
				//For URIS like http://ontology.dumontierlab.com/hasReference
				int index = uriStr.lastIndexOf("/");
				if (index>=0){
					return uriStr.substring(0, index+1);
				}
			}
			return uriStr;
		}
		else
			return "";
		
	}
	
	
	
	
	public static String[] splitStringByCapitalLetter(String str){
		
		//We first capital prepositions
		str = capitalPrepositions(str);
		
		String pattern =

	        "(?<=[^\\p{Upper}])(?=\\p{Upper})"
	        // either there is anything that is not an uppercase character
	        // followed by an uppercase character

	        + "|(?<=[\\p{Lower}])(?=\\d)"
	        // or there is a lowercase character followed by a digit

	        //+ "|(?=\\d)(?<=[\\p{Lower}])"
	        ;

		return str.split(pattern);
		//return str.split("(?=\\p{Upper})");
		
	}
	
	public static String capitalPrepositions(String str){
		
		//In some cases "of", "by", "with", "and", "for" are not in capitals within the label
		
		str = str.replaceAll("of(?=\\p{Upper})", "Of");
		str = str.replaceAll("with(?=\\p{Upper})", "With");
		str = str.replaceAll("for(?=\\p{Upper})", "For");
		str = str.replaceAll("and(?=\\p{Upper})", "And");
		str = str.replaceAll("by(?=\\p{Upper})", "By");
		str = str.replaceAll("to(?=\\p{Upper})", "To");
		str = str.replaceAll("on(?=\\p{Upper})", "On");
		str = str.replaceAll("in(?=\\p{Upper})", "In");
		
		return str;
		
	}
	
	
	
	
	
	
	
	public static void main(String[] args) {
		
		String[] words2 = splitStringByCapitalLetter("SoyBean");
		
		for (String w : words2) {
			System.out.println(w);
		}
		
		
		if (true)
			return;
		
		
		String uriStr = "http://www.semanticweb.org/challenge/sem-tab#tab-2._Bundesliga_South_(1974%E2%80%9381)#4-col-0-row-36";
		
		int index = uriStr.indexOf("#"); //First occurrence
		System.out.println(uriStr.substring(index+1));
		
		
		if (true)
			return;
		
		OntologyIRIShortFormProvider xutils = new OntologyIRIShortFormProvider();
		
		System.out.println(xutils.getShortForm(IRI.create("http://eu.optique.ontology/statoil_sota_ontology_demo.owl#lalal")));
		
		System.out.println(xutils.getShortForm(IRI.create("http://www.optique-project.eu/resource/NPD_small_schema_demo/npd_schema/wellbore_exploration_all#lalal2")));
		
		
		if (true)
			return;
		
		//"AJCCStagingSystem7thEd";
		
		Date d1 = new Date("1913-09-13");
		Date d2 = new Date("13, Sep 1913");
				
		
		System.out.println(d1.toString());
		System.out.println(d2.toString());
		
		
		//String regex="[&\\.,;(]|is|are|was|were";
		String regex="([&\\.,;(]|is|are|was|were)";
		
		System.out.println ("123&345: " + "123&345".matches(".*"+regex+".*") + "  ." + "123&345".split(regex)[0] + " -- " + "123&345".split(regex)[1]);
		System.out.println ("123.345: " + "123.345".matches(".*"+regex+".*") + "  ." + "123.345".split(regex)[0] + " -- " + "123.345".split(regex)[1]);
		System.out.println ("123,345: " + "123,345".matches(".*"+regex+".*") + "  ." + "123,345".split(regex)[0] + " -- " + "123,345".split(regex)[1]);
		System.out.println ("123;345: " + "123;345".matches(".*"+regex+".*") + "  ." + "123;345".split(regex)[0] + " -- " + "123;345".split(regex)[1]);
		System.out.println ("123(345: " + "123(345".matches(".*"+regex+".*") + "  ." + "123(345".split(regex)[0] + " -- " + "123(345".split(regex)[1]);
		System.out.println ("123 is 345: " + "123 is 345".matches(".*"+regex+".*") + "  ." + "123 is 345".split(regex)[0] + " -- " + "123 is 345".split(regex)[1]);
		System.out.println ("123 was 345: " + "123 was 345".matches(".*"+regex+".*") + "  ." + "123 was 345".split(regex)[0] + " -- " + "123 was 345".split(regex)[1]);
		System.out.println ("123 were 345: " + "123 were 345".matches(".*"+regex+".*") + "  ." + "123 were 345".split(regex)[0] + " -- " + "123 were 345".split(regex)[1]);
		System.out.println ("123 are 345: " + "123 are 345".matches(".*"+regex+".*") + "  ." + "123 are 345".split(regex)[0] + " -- " + "123 are 345".split(regex)[1]);

		
		System.out.println("Name for http://www.opengis.net/citygml/appearance/2.0/  : '" + getEntityLabelFromURI("http://www.opengis.net/citygml/appearance/2.0/") + "'");
		System.out.println("NS for http://www.opengis.net/citygml/appearance/2.0/  : '" + getNameSpaceFromURI("http://www.opengis.net/citygml/appearance/2.0/") + "'");
		
		int classId = 345;
		
		Interval descOrderInterval = new Interval(-classId,-classId);
		
		System.out.println("Interval negative: " + descOrderInterval + "  "  + (descOrderInterval.getLeftBound()<0));
		
		
		if (true)
			return;
		
		
		String reg_ex=".*(.)\\1+.*";
		
		String reg_ex2 = ".*(.)\\1\\1.*";
		String reg_ex3 = ".*(\\s)\\1\\1.*";
		 
		System.out.println ("			laaala	lalaaa".matches(reg_ex3));
		System.out.println ("laaala  			lalaaa".matches(reg_ex3));
		System.out.println ("laaala   lalaaa".matches(reg_ex3));
		System.out.println("");
		
		
		/*System.out.println("cool:  " + "cool".matches(reg_ex));
		System.out.println("aa:  " + "aa".matches(reg_ex));
		System.out.println("aaa:  " + "aaa".matches(reg_ex));
		System.out.println("aba:  " + "aba".matches(reg_ex));
		
		System.out.println("");*/
		
		System.out.println("cool:  " + "cool".matches(reg_ex2));
		System.out.println("aa:  " + "aa".matches(reg_ex2));
		System.out.println("aaa:  " + "aaa".matches(reg_ex2));
		System.out.println("aba:  " + "aba".matches(reg_ex2));
		System.out.println("ccaaadd:  " + "ccaaadd".matches(reg_ex2));
		System.out.println("ccaadd:  " + "ccaadd".matches(reg_ex2));
		System.out.println("jaaabbbj:  " + "jaaabbj".matches(reg_ex2));
		
		String str = "assddffffadfdd..o";
		
		System.out.println (str.matches(reg_ex));
		//System.out.println (str.replaceAll(reg_ex, "$1"));

		
		//System.out.println (str.matches(reg_ex2));
		//System.out.println (str.replaceAll(reg_ex2, "$1"));
		
		
		if (true)  return;
		
		
		String uri ="http://ontology.dumontierlab.com/inheresIn";
		System.out.println(getEntityLabelFromURI(uri));
		System.out.println(getNameSpaceFromURI(uri));
		
		
		String uri2 ="http://ontology.dumontierlab.com/onto.owl#inheresIn";
		System.out.println(getEntityLabelFromURI(uri2));
		System.out.println(getNameSpaceFromURI(uri2));
		
		if (true)
			return;
		
		String[] words = splitStringByCapitalLetter("AJCCStagingSystem7thEd");
		
		for (int i=0; i<words.length; i++){
			System.out.println(words[i]);
		}
		
		words = splitStringByCapitalLetter("WHOPerformanceStatusGrade4");
		for (int i=0; i<words.length; i++){
			System.out.println(words[i]);
		}		
		
		
		System.out.println("SurgeryFollowedbyAdjuvantChemotherapyPlan");
		System.out.println(Utilities.capitalPrepositions("SurgeryFollowedbyAdjuvantChemotherapyPlan"));
		System.out.println("");
		
		System.out.println("ConcurrentChemotherapyandRadiotherapyPlan");
		System.out.println(Utilities.capitalPrepositions("ConcurrentChemotherapyandRadiotherapyPlan"));
		System.out.println("");
		
		
		System.out.println("InductionChemotherapytoDownstageBeforeSurgeryPlan");
		System.out.println(Utilities.capitalPrepositions("InductionChemotherapytoDownstageBeforeSurgeryPlan"));
		System.out.println("");
		
		System.out.println("PneumonectomywithTrachebronchoplasty");
		System.out.println(Utilities.capitalPrepositions("PneumonectomywithTrachebronchoplasty"));
		System.out.println("");
		
		System.out.println("ExcisionofSegmentofLung");
		System.out.println(Utilities.capitalPrepositions("ExcisionofSegmentofLung"));
		System.out.println("");
		
		System.out.println("ProcedureforLungLesion");
		System.out.println(Utilities.capitalPrepositions("ProcedureforLungLesion"));
		System.out.println("");
		
		System.out.println("OpenExcisionofLesionofLung");
		System.out.println(Utilities.capitalPrepositions("OpenExcisionofLesionofLung"));
		System.out.println("");
		
		

		
		
		
		
		
	}
	
	
	
	
	
	
	
	
	
	
	
}

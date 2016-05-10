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
package uk.ac.ox.krr.logmap2.lexicon;

import java.util.*;

public class NormalizeNumbers {
	
	private final static String[] CARDINALS_STR_ARRAY = {
		"zero",
		"one",
		"two",
		"three",
		"four",
		"five",
		"six",
		"seven",
		"eight",
		"nine",
		"ten"
	};
	
	private final static String[] CARDINALS_ARRAY = {
		"0",
		"1",
		"2",
		"3",
		"4",
		"5",
		"6",
		"7",
		"8",
		"9",
		"10"
	};
	
	private final static String[] ORDINALS_STR_ARRAY = {
		"zeroth",
		"first",
		"second",
		"third",
		"fourth",
		"fifth",
		"sixth",
		"seventh",
		"eighth",
		"ninth",
		"tenth"
	};
	
	private final static String[] ORDINALS_ARRAY = {
		"0th",
		"1st",
		"2nd",
		"4rd",
		"4th",
		"5th",
		"6th",
		"7th",
		"8th",
		"9th",
		"10th"
	};
	
	private final static String[] ROMANS_ARRAY = {
		"0", //Workaround: No roman representation, but need to fill index 0
		"i",
		"ii",
		"iii",
		"iv",
		"v",
		"vi",
		"vii",
		"viii",
		"ix",
		"x"
	};
	
	private static List<String> CARDINALS_STR=new ArrayList<String>();
	private static List<String> CARDINALS=new ArrayList<String>();
	private static List<String> ORDINALS_STR=new ArrayList<String>();
	private static List<String> ORDINALS=new ArrayList<String>();	
	private static List<String> ROMANS=new ArrayList<String>();
	
	
	static {
		for (String str: ROMANS_ARRAY){
			ROMANS.add(str);
		}
		
		for (String str: CARDINALS_STR_ARRAY){
			CARDINALS_STR.add(str);
		}
		
		for (String str: CARDINALS_ARRAY){
			CARDINALS.add(str);
		}
		
		for (String str: ORDINALS_STR_ARRAY){
			ORDINALS_STR.add(str);
		}
		
		for (String str: ORDINALS_ARRAY){
			ORDINALS.add(str);
		}
	}
	
	
	public static String getRomanNormalization(String word){
		
		int index;
		
		index = CARDINALS_STR.indexOf(word);		
		if (index>-1)
			return ROMANS.get(index);
		
		index = CARDINALS.indexOf(word);		
		if (index>-1)
			return ROMANS.get(index);
		
		index = ORDINALS_STR.indexOf(word);		
		if (index>-1)
			return ROMANS.get(index);
		
		index = ORDINALS.indexOf(word);		
		if (index>-1)
			return ROMANS.get(index);
		
		return "";
	}
	
	public static List<String> getRomanNumbers10(){
		return ROMANS;
	}


}

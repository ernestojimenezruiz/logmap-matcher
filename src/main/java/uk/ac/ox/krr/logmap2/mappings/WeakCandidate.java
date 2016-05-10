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
package uk.ac.ox.krr.logmap2.mappings;

import java.io.*;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import uk.ac.ox.krr.logmap2.indexing.IndexManager;
import uk.ac.ox.krr.logmap2.indexing.OntologyProcessing;
import uk.ac.ox.krr.logmap2.indexing.entities.ClassIndex;
import uk.ac.ox.krr.logmap2.utilities.Lib;
import uk.ac.ox.krr.logmap2.utilities.Pair;

public class WeakCandidate extends Pair<Integer, Integer> implements Comparator<WeakCandidate> {
	
	public static final double MINSCORE = 0;
	public static final int WEIGHTED = 0;
	public static final int WEIGHTEDFILTER = 1;
	public static final int WEIGHTEDFILTERMATCH = 2;

	private OntologyProcessing m_o1 = null;
	private OntologyProcessing m_o2 = null;
	private IndexManager m_index = null;
	private double m_score = 0;
	private String rest1 = "", rest2 = ""; 
	
	public WeakCandidate(int id1, int id2, Set<String> label1, Set<String> label2,
			OntologyProcessing o1, OntologyProcessing o2, int type)
	{
		super(id1, id2);
		m_o1 = o1;
		m_o2 = o2;
		getScore_weighted(id1, id2, label1, label2, type);
	}
	
	private void getScore_weighted(int id1, int id2, Set<String> lab1, Set<String> lab2, int type)
	{
		if (type == WEIGHTEDFILTER)
		{
			Set<String> super1 = m_o1.getSuperClass(id1, 10);
			Set<String> super2 = m_o2.getSuperClass(id2, 10);
			
			for (String str : super1)
				for (String word : str.split("_"))
					if (!lab1.contains(word))
						lab2.remove(word);
			
			for (String str : super2)
				for (String word : str.split("_"))
					if (!lab2.contains(word))
						lab1.remove(word);
		}
		
		m_score = getScore_weighted(lab1, lab2);
	}
	
	private double getScore_weighted(Set<String> lab1, Set<String> lab2)
	{
		double sum = 0, value = 0, temp;
		Set<String> combo = new HashSet<String>();

		for (String word : lab1)
		{
			temp = 1. / m_o1.getFrequency(word);
			sum += temp;
			if (lab2.contains(word))
				value += temp;
			rest1 += word;
		}
		
		for (String word : lab2)
		{
			temp = 1. / m_o2.getFrequency(word);
			sum += temp;
			if (lab1.contains(word))
				value += temp;
			rest2 += word;
		}

		
		return sum == 0 ? 1. : value / sum;
	}
	
	public double getScore(I_Sub isub)
	{
		return isub.score(rest1, rest2);
	}
	
	/**
	 *  for frequency formula
	 */
	public WeakCandidate(Set<String> combo, int id1, int id2, String label1, String label2, IndexManager index, int f_combo)
	{
		super(id1, id2);
		m_index = index;
		getScore_frequency(combo, label1, label2, f_combo);
	}
	
	private void getScore_frequency(Set<String> combo, String label1, String label2, int f_combo)
	{
		// TODO Yujiao - get the score of label1 and label 2
		Set<String> set1 = new HashSet<String>();
		Set<String> set2 = new HashSet<String>();
		
		for (String word : label1.split("_"))
			if (!combo.contains(word))
				set1.add(word);
		
		for (String word : label2.split("_"))
			if (!combo.contains(word))
				set2.add(word);
		
		Set<Integer> list1 = m_index.getCooccurrenceOfWords(set1);
		Set<Integer> list2 = m_index.getCooccurrenceOfWords(set2);
		
		int total = m_index.getIdentifier2ClassIndexMap().size();
		
//		double pa = list1 == null ? 1. : (double)list1.size() / total; 
//		double pb = list2 == null ? 1. : (double)list2.size() / total;
		int fa = list1 == null ? total : list1.size();
		int fb = list2 == null ? total : list2.size();
		
		if ((f_combo - fa) * (f_combo - fb) < 0)
		{
			m_score = 0;
			return ;
		}
		
		set1.addAll(set2);
		Set<Integer> list = m_index.intersectSet(list1, list2);
		int fab = list == null ? total : list.size();
		
		set1.clear();
		set2.clear();
		if (list1 != null) list1.clear();
		if (list2 != null) list2.clear();
		if (list != null) list.clear();
		
//		double min;
//		connection = fab / (min = Math.min(fa, fb));
//		gap = Math.abs(fa - fb) / min;
		m_score = 2. * fab / (fa + fb);
//		score = 2 * ((double)Math.min(fa, fb)) / total;
	}
	
	public double getScore()
	{
		return m_score;
	}
	
	@Override // 1: c1 < c2, for sort < < < < 
	public int compare(WeakCandidate c1, WeakCandidate c2) {
		int delta = Lib.dcmp(c1.m_score - c2.m_score);
		return - delta;
	}

	public int compareTo(WeakCandidate that) // 1: this > that
	{
		return - compare(this, that);
	}
	
	public boolean equals(Object obj) {
		return compare(this, (WeakCandidate)obj) == 0;
	}

}

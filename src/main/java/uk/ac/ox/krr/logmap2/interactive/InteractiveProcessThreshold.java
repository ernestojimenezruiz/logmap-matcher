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
package uk.ac.ox.krr.logmap2.interactive;

import java.io.BufferedWriter;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.*;

import uk.ac.ox.krr.logmap2.indexing.IndexManager;
import uk.ac.ox.krr.logmap2.mappings.MappingManager;
import uk.ac.ox.krr.logmap2.mappings.objects.MappingObjectStr;
import uk.ac.ox.krr.logmap2.repair.AnchorAssessment;
import uk.ac.ox.krr.logmap2.utilities.Lib;
import uk.ac.ox.krr.logmap2.utilities.Utilities;
import uk.ac.ox.krr.logmap2.interactive.objects.MappingObjectInteractivity;
import uk.ac.ox.krr.logmap2.io.*;



/**
 * 
 * This method must be integrated within iLogmap with GUI.
 * Currently it is not used in LogMap 2
 * @author Yujiao Zhou
 * @deprecated
 *
 */
public class InteractiveProcessThreshold extends InteractiveProcess
{
	
	
	boolean considerDiscarded = false;

	int left, right;
	private MappingManager m_manager;
	private AnchorAssessment m_assessment;
	
	private List<MappingObjectInteractivity> mappings2ask = new ArrayList<MappingObjectInteractivity>();
	private Set<Integer> mappingsAsked = new HashSet<Integer>();

	
	public InteractiveProcessThreshold(IndexManager index, MappingManager mapping_manager){
	
		m_manager = mapping_manager;
		m_assessment = new AnchorAssessment(index, m_manager);

		setScore2mappings();
		
		int goodcandidate = 0;
		for (MappingObjectInteractivity mapping : mappings2ask)
		{
			if (m_manager.isMappingInGoldStandard(
					mapping.getIdentifierOnto1(), 
					mapping.getIdentifierOnto2())){
				
				mapping.setInGSMappings();
				++goodcandidate;
			}
		}
		
		LogOutput.print("total number of good candidates in 2ASK set: " + goodcandidate);
		
		Collections.sort(mappings2ask, new MappingInteractivityComparator4Similarity());
		
		MappingObjectInteractivity mapping;
		
		try
		{
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("/auto/users/yzhou/2ask.txt")));
		for (int i = 0; i < mappings2ask.size(); ++i)
		{
			mapping = mappings2ask.get(i);
			writer.write(mapping.getIdentifierOnto1() + " " + mapping.getIdentifierOnto2() + " " 
					+ mapping.getScore() + " " + mapping.isInGSMappings() + "\n");
		}
		writer.close();
		} 
		catch (java.io.IOException e)
		{
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void startInteractiveProcess()
	{
		boundaryDecision();

		cleanMappingsAndRecordPrecisionAndRecall();
		
		LogOutput.print((right - left) + "\t" + (left + 1) + "\t" + right + "\n"
				+ mappingsAsked.size() + "\t" + mappings2ask.size() + "\n" 
				+  precision + "\n" +  recall + "\n" +  Fmeasure);
	}
	
	private void boundaryDecision()
	{
		left = 0;
		right = mappings2ask.size() - 1;
		int mid;
		
		while (left < right)
		{
			mid = (left + right + 1) / 2;
			if (review(mid))
				left = mid;
			else 
				right = mid - 1;
		}
		
		int temp = left;
		
		LogOutput.print("finished 1");
		
		right = left;
		left = 0;
		while (left < right)
		{
			mid = (left + right) / 2;
			if (!review(mid))
				left = mid + 1;
			else
				right = mid;
		}
		
		right = temp;
		LogOutput.print("finished 2");
		
	}

	private void markMappingAndAdd2List(MappingObjectInteractivity mapping, boolean inGSMappings)
	{
		if (inGSMappings){
			mapping.setAddedFlag(true);
//			new2Add.add(m);
			
//			good_marked++;
			
			//User additions
			m_manager.addMappingAddedByUser2Structure(mapping.getIdentifierOnto1(), mapping.getIdentifierOnto2());
			m_manager.addMappingAddedByUser2Structure(mapping.getIdentifierOnto2(), mapping.getIdentifierOnto1());
			
		}
		else {
			mapping.setRemovedFlag(true);
//			new2Del.add(mapping);
//			bad_marked++;
		}
		
	}

	private void cleanMappingsAndRecordPrecisionAndRecall() 
	{
		MappingObjectInteractivity mapping;
		//We add mappings from current interactivity status
		for (int i = left + 1; i <= right; ++i) {
			
			mapping = mappings2ask.get(i);
			if (!mapping.isRemovedFlagActive()) {
				
				//m_manager.addSubMapping2Mappings2Review(mapping.getIdentifierOnto1(), mapping.getIdentifierOnto2());
				//m_manager.addSubMapping2Mappings2Review(mapping.getIdentifierOnto2(), mapping.getIdentifierOnto1());
				if (mapping.getDirMapping()==Utilities.EQ || mapping.getDirMapping()==Utilities.L2R)
					m_manager.addSubMapping2Mappings2Review(mapping.getIdentifierOnto1(), mapping.getIdentifierOnto2());
				
				if (mapping.getDirMapping()==Utilities.EQ || mapping.getDirMapping()==Utilities.R2L)
					m_manager.addSubMapping2Mappings2Review(mapping.getIdentifierOnto2(), mapping.getIdentifierOnto1());
			
			}
		}
		
		//We also add weakened mappings
		//They are already in initial list
		//m_manager.assesWeakenedMappingsDandG(false, false);
		
		
		//Clean "Mappings2Review" mappings D&G
		try {
			m_assessment.CheckSatisfiabilityOfIntegration_DandG(m_manager.getMappings2Review());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  //With Fixed mappings!
		
		
		
		//With Anchors and clean mappinsg 2 review
		getPrecisionAndRecall();
		
		///We clear them
		m_manager.getMappings2Review().clear();
		
		
	}
	
	private void getPrecisionAndRecall()
	{
		Set <MappingObjectStr> intersection;
		
		//We set current mappings to get provisional P&R
		setStringMappings();
			
		
		intersection=new HashSet<MappingObjectStr>(m_manager.getStringLogMapMappings());
		intersection.retainAll(m_manager.getStringGoldStandardAnchors());
		
		precision=((double)intersection.size())/((double)m_manager.getStringLogMapMappings().size());
		recall=((double)intersection.size())/((double)m_manager.getStringGoldStandardAnchors().size());

		Fmeasure = (2*recall*precision)/(precision+recall);
		
		//Sets anchors (it also clears structure)
		m_manager.setStringAnchors();
	}

	private void setStringMappings()
	{
		//Sets anchors (it also clears structure)
		m_manager.setStringAnchors();
		
		
		//Set mappings to review
		for (int ide1 : m_manager.getMappings2Review().keySet()){
		
			for (int ide2 : m_manager.getMappings2Review().get(ide1)){
				
				m_manager.addStringAnchor(ide1, ide2);
				
			}
			
		}
		
		
		//TODO Add Weakened mappings (only for P&R)
		//Note that removed ones are not added
		for (int ide1 : m_manager.getWeakenedDandGMappings().keySet()){
			
			for (int ide2 : m_manager.getWeakenedDandGMappings().get(ide1)){
						
				if (m_manager.isMappingInConflictWithFixedMappings(ide1, ide2) || 
						m_manager.isMappingInferred(ide1, ide2) ||
						m_manager.isMappingInConflictiveSet(ide1, ide2)){
					continue;
				}
				
				m_manager.addStringAnchor(ide1, ide2);
				
			}
		}		
	}

	private boolean review(int index)
	{
		int mark = 2; 
		mark = review(index, mark);
		
		if (index > 0) mark = review(index - 1, mark);
		if (mark == 0) return true;
		else if (mark == 2) return false;
		
		if (index + 1 < mappings2ask.size()) mark = review(index + 1, mark);
		if (mark == 0) return true;
		else return false;
	}
	
	private int review(int index, int mark)
	{
		MappingObjectInteractivity mapping = mappings2ask.get(index);
		boolean flag = mapping.isInGSMappings();
		if (!mappingsAsked.contains(index))
			markMappingAndAdd2List(mapping, flag);
		mappingsAsked.add(index);
		
		return flag ? mark - 1 : mark;
	}
	
	private void setScore2mappings()
	{
		;
		//Consider mappings 2 ask and discarded as well!
		for (MappingObjectInteractivity mapping : m_manager.getListOfMappingsToAskUser())
		{
			mapping.setScore(m_manager);
			mappings2ask.add(mapping);
		}
		
		if (considerDiscarded){
			MappingObjectInteractivity m;
			
			for (int ideA : m_manager.getDiscardedMappings().keySet()){
				for (int ideB : m_manager.getDiscardedMappings().get(ideA)){
					
					if (ideA<ideB)
					{
						mappings2ask.add(m = new MappingObjectInteractivity(ideA, ideB));
						m.setScore(m_manager);
					}
					
				}
			}
		}
		
		
	}

	@Override
	public void endInteractiveProcess(boolean filter)
	{
		// TODO Auto-generated method stub
		
	}
	
	public void endInteractiveProcess()
	{
		// TODO Auto-generated method stub
		
	}

}

class MappingInteractivityComparator4Similarity implements Comparator<MappingObjectInteractivity>
{

	@Override
	public int compare(MappingObjectInteractivity o1, MappingObjectInteractivity o2)
	{
		return Lib.dcmp(o2.getScore() - o1.getScore());
	}
	
}

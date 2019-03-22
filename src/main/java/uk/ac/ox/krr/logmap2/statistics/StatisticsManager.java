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
package uk.ac.ox.krr.logmap2.statistics;

import uk.ac.ox.krr.logmap2.io.LogOutput;
import uk.ac.ox.krr.logmap2.mappings.*;
import uk.ac.ox.krr.logmap2.interactive.objects.MappingObjectInteractivity;

/**
 * This class will manage the set of statistics produced by LogMap
 * @author Ernesto
 *
 */
public class StatisticsManager {
	
	
	public static int MFinal;
	public static int MFinal_ok;
	public static int MMissing;

	
	public static int Mall = 0;
	public static int Mall_ok = 0;
	
	public static int Manchors = 0;
	public static int Manchors_ok = 0;
	
	public static int Mask = 0;
	public static int Mask_ok = 0;
	
	public static int Mask_heur = 0;
	
	public static int Msplit = 0;
	public static int Msplit_ok = 0;
	
	public static int Mdisc = 0;
	public static int Mdisc_ok = 0;
	
	public static int Mharddisc = 0;
	public static int Mharddisc_ok = 0;
	
	public static int Mconf = 0;
	public static int Mconf_ok = 0;
	
	public static int Mconf_dg = 0;
	public static int Mconf_dg_ok = 0;
	
	public static double precision_anc = 0.0;
	public static double recall_anc = 0.0;
	public static double fmeasure_anc = 0.0;
		
	
	
	
	
	public static MappingManager mapping_manager;
	
	
	public static void setMappingManager(MappingManager m_manager){
		mapping_manager = m_manager;
	}
	

	
	
	
	public static void extractStatisticsAnchors(){
		
		//Initial number
		for (int ide1 : mapping_manager.getLogMapMappings().keySet()){
			for (int ide2 : mapping_manager.getLogMapMappings().get(ide1)){
				if (mapping_manager.isId1SmallerThanId2(ide1, ide2)){
					
					Manchors++;
					
					if (mapping_manager.isMappingInGoldStandard(ide1, ide2)){
						Manchors_ok++;
					}
					
				}
			}
		}
		
	}
	
	
	public static void extractStatisticsMappings2Ask(){
		Mask=mapping_manager.getListOfMappingsToAskUser().size();
		//for (int i=0; i<mapping_manager.getListOfMappingsToAskUser().size(); i++){
		for (MappingObjectInteractivity mapping : mapping_manager.getListOfMappingsToAskUser()){
			if (mapping_manager.isMappingInGoldStandard(
					mapping.getIdentifierOnto1(),
					mapping.getIdentifierOnto2())){
				
				Mask_ok++;
			}
		}
	}
	
	
	
	public static void extracStatisticsDiscardedMappings(){
		for (int ide1 : mapping_manager.getDiscardedMappings().keySet()){
			for (int ide2 : mapping_manager.getDiscardedMappings().get(ide1)){
				
				//check only one side
				if (mapping_manager.isId1SmallerThanId2(ide1, ide2)){
					
					Mdisc++;
					
					if (mapping_manager.isMappingInGoldStandard(ide1, ide2)){
						Mdisc_ok++;
					}					
				}
			}
		}
	}
	
	public static void extracStatisticsHardDiscardedMappings(){
	
		for (int ide1 : mapping_manager.getHardDiscardedMappings().keySet()){
			for (int ide2 : mapping_manager.getHardDiscardedMappings().get(ide1)){
				
				if (mapping_manager.isMappingInDiscardedSet(ide1, ide2) || 
						mapping_manager.isMappingInDiscardedSet(ide2, ide1)){
					continue;
				}
				
				//check only one side
				if (mapping_manager.isId1SmallerThanId2(ide1, ide2)){
														
					Mharddisc++;
					
					
					if (mapping_manager.isMappingInGoldStandard(ide1, ide2)){
						Mharddisc_ok++;
					}
				}
			}
		}
	}
	
	
	public static void extracStatisticsConflictiveMappings(){
		
		for (int ide1 : mapping_manager.getConflictiveMappings().keySet()){
			for (int ide2 : mapping_manager.getConflictiveMappings().get(ide1)){
				
				if (!mapping_manager.isId1SmallerThanId2(ide1, ide2)){
					if (mapping_manager.getConflictiveMappings().containsKey(ide2) && 
						mapping_manager.getConflictiveMappings().get(ide2).contains(ide1)){
						
						continue; //Already visited
					}
				}
				Mconf++;
				
				if (mapping_manager.isMappingInGoldStandard(ide1, ide2) ||
						mapping_manager.isMappingInGoldStandard(ide2, ide1)){
					
					Mconf_ok++;
				}
				
			}
		}
	}
	
	public static void extracStatisticsConflictiveMappings_D_G(){
		
		for (int ide1 : mapping_manager.getConflictiveMappings_D_G().keySet()){
			for (int ide2 : mapping_manager.getConflictiveMappings_D_G().get(ide1)){
				
				if (!mapping_manager.isId1SmallerThanId2(ide1, ide2)){
					if (mapping_manager.getConflictiveMappings_D_G().containsKey(ide2) && 
						mapping_manager.getConflictiveMappings_D_G().get(ide2).contains(ide1)){
						
						continue; //Already visited
					}
				}
				Mconf_dg++;
				
				if (mapping_manager.isMappingInGoldStandard(ide1, ide2) ||
						mapping_manager.isMappingInGoldStandard(ide2, ide1)){
					
					Mconf_dg_ok++;
				}
				
			}
		}
		
	}
	
	
	public static void addStatisticsSplitMapping(int ide1, int ide2){
		
		Msplit++;
		if (mapping_manager.isMappingInGoldStandard(ide1, ide2)){						
			Msplit_ok++;
		}
		
	}
	
	public static void addStatisticsMappingsAll(int ide1, int ide2){
		
		Mall++;
		if (mapping_manager.isMappingInGoldStandard(ide1, ide2)){						
			Mall_ok++;
		}
		
	}
	
	
	
	
	public static void setMFinal(int size_m){
		MFinal = size_m;
	}
	
	public static void setGoodMFinal(int size_m){
		MFinal_ok = size_m;
	}
	
	public static void setMMissing(int size_m){
		MMissing = size_m;
	}
	
	
	public static int getMFinal(){
		return MFinal;
	}
	
	public static int getGoodMFinal(){
		return MFinal_ok;
	}
	
	public static int getMMissing(){
		return MMissing;
	}
	
	
	
	public static void setPrecisionAndRecallAnchors(int numGS){
		precision_anc=((double)StatisticsManager.Manchors_ok)/((double)StatisticsManager.Manchors);
		recall_anc=((double)StatisticsManager.Manchors_ok)/((double)numGS);
		fmeasure_anc = (2.0*precision_anc*recall_anc)/(precision_anc+recall_anc);
	}
	
	
	
	public static void reInitValues(){
		
		MFinal = 0;
		MFinal_ok = 0;
		MMissing = 0;
		
		Mall = 0;
		Mall_ok = 0;
		
		Manchors = 0;
		Manchors_ok = 0;
		
		precision_anc =0.0;
		recall_anc =0.0;
		fmeasure_anc =0.0;
		
		Mask = 0;
		Mask_ok = 0;
		Mask_heur = 0;
		
		Msplit = 0;
		Msplit_ok = 0;
		
		Mdisc = 0;
		Mdisc_ok = 0;
		
		Mharddisc = 0;
		Mharddisc_ok = 0;
		
		Mconf = 0;
		Mconf_ok = 0;
		
		Mconf_dg = 0;
		Mconf_dg_ok = 0;
		
	}
	
	
	
	
	
	public static void printStatisticsLogMap_mappings(){
	
		LogOutput.printAlways("---------------------");
		LogOutput.printAlways("All Candidate mappings: " + Mall);
		LogOutput.printAlways("Good Candidate mappings: " + Mall_ok);
		LogOutput.printAlways("---------------------");
		LogOutput.printAlways("LogMap anchors: " + Manchors);
		LogOutput.printAlways("Good LogMap anchors: " + Manchors_ok);
		LogOutput.printAlways("Precision LogMap anchors: " + precision_anc);
		LogOutput.printAlways("Recall LogMap anchors: " + recall_anc);
		LogOutput.printAlways("Fmeasure LogMap anchors: " + fmeasure_anc);
		LogOutput.printAlways("---------------------");
		LogOutput.printAlways("LogMap 2 ask mappings: " + Mask);
		LogOutput.printAlways("Good LogMap 2ask mappings: " + Mask_ok);		
		LogOutput.printAlways("---------------------");
		LogOutput.printAlways("LogMap split mappings: " + Msplit);
		LogOutput.printAlways("Good LogMap split mappings: " + Msplit_ok);
		LogOutput.printAlways("---------------------");
		LogOutput.printAlways("LogMap discarded mappings: " + Mdisc);
		LogOutput.printAlways("Good LogMap discarded mappings: " + Mdisc_ok);
		LogOutput.printAlways("---------------------");
		LogOutput.printAlways("LogMap hard discarded mappings: " + Mharddisc);
		LogOutput.printAlways("Good LogMap hard discarded mappings: " + Mharddisc_ok);
		LogOutput.printAlways("---------------------");
		LogOutput.printAlways("LogMap conflictive mappings: " + Mconf);
		LogOutput.printAlways("Good LogMap conflictive mappings: " + Mconf_ok);		
		LogOutput.printAlways("---------------------");
		LogOutput.printAlways("LogMap conflictive mappings D&G: " + Mconf_dg);
		LogOutput.printAlways("Good LogMap conflictive mappings D&G: " + Mconf_dg_ok);		
		LogOutput.printAlways("---------------------");
		LogOutput.printAlways("All Candidates recount: " + (Manchors+Mask+Msplit+Mdisc+Mharddisc+Mconf));
		LogOutput.printAlways("Good Candidates recount: " + (Manchors_ok+Mask_ok+Msplit_ok+Mdisc_ok+Mharddisc_ok+Mconf_ok));
		LogOutput.printAlways("---------------------");	
	}
	
	
	
	public static void printMappingsAskedHeur(){
		LogOutput.printAlways("\n---------------------");
		LogOutput.printAlways("LogMap asked heuristics: " + Mask_heur);
		LogOutput.printAlways("---------------------\n");
	}
	
	
	
	
	
	
}

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
package uk.ac.ox.krr.logmap2.repair;

import java.util.Calendar;

import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.Vector;

import uk.ac.ox.krr.logmap2.Parameters;
import uk.ac.ox.krr.logmap2.io.LogOutput;
import uk.ac.ox.krr.logmap2.indexing.IndexManager;
import uk.ac.ox.krr.logmap2.mappings.MappingManager;
import uk.ac.ox.krr.logmap2.mappings.CandidateMappingManager;
import uk.ac.ox.krr.logmap2.repair.hornSAT.DowlingGallierHornSAT;
import uk.ac.ox.krr.logmap2.repair.hornSAT.HornClause;
import uk.ac.ox.krr.logmap2.repair.hornSAT.Link;
import uk.ac.ox.krr.logmap2.utilities.PrecomputeIndexCombination;
import uk.ac.ox.krr.logmap2.io.*;

/**
 * This class will assess the validity of extracted anchors using D&G algorithm
 * 
 *
 * @author Ernesto Jimenez-Ruiz
 * Created: Oct 10, 2011
 *
 */
public class AnchorAssessment {

	
	private DowlingGallierHornSAT dgSat;
	
	private Set<HornClause> hornMappings2Remove;
	
	/**Discarded mappings**/
	//private Map<Integer, Set<Link>> generalIgnoreLinks;
	
	/**Used in repair to discover which mappings must be deleted*/
	private Map<Integer, Set<Link>> ignoreLinks = new HashMap<Integer, Set<Link>>();
	
	
	private IndexManager index;
	
	//private OntologyProcessing onto_process1;
	//private OntologyProcessing onto_process2;
	
	private MappingManager mapping_extractor;
	
	
	
	private Set<Integer> unSATvisited;

	private Set<Integer> SATvisited;
	
	private Set<Integer> SAT;
	
	//Temporal set
	private Set<Integer> allUNSAT = new HashSet<Integer>();
	
	
	private Map<Integer, Boolean> unsatClasses2repaired;

		
	//private Set<HornClause> plan = new HashSet<HornClause>();
	//private Set<Integer> mappingsInrepair = new HashSet<Integer>();
	
	//private Set<Integer> classes_with_hard_cases;
	int hard_cases_still2solve=0;
	
	//private  Map<Integer,Vector<Set<HornClause>>> unsatClasses2repairMappingSet;
	
	private Map<Integer,Set<HornClause>> unsatClasses2conflictiveMappings;
	
	//Global information about the repairs
	private Map<HornClause,Integer> mapping2NumOfConflicts;	
	
	
	
	double averageConflictiveMappings=0.0;
	int maxConflictiveMappings=0;
	int minConflictiveMappings=10000;
	
	double averageRepairMappings=0.0;
	int maxRepairMappings=0;
	
	int minRepairMappings=10000;
	
	double averageSizeRepair=0.0;
	int maxSizeRepair=0;
	int minSizeRepair=10000;
	
	private PrecomputeIndexCombination precomputeIndexCombination = new PrecomputeIndexCombination();
	
	
	boolean useProjection=false;
	boolean dealWithHardCases=false;

	
	boolean repairInstanceMappings = false;
	
	
	
	
	
	//Must at least control 3 structures: mappings, fixed mappinsg and mappppimngs to ignore
	//Will use structures from: onto processing (tax, disj, equiv, and general axioms) and index (to discover conflicts of new mappings)
	//Note that currently index contains the most of structures (tax, dij, equiv...)
	//
	//Call from Lexical mapping extractor.... then structures are controled there
	//We just need to create the methods
	
	
	
	/**
	 * Constructor
	 * @param index
	 * @param mapping_extractor
	 */
	public AnchorAssessment(
			IndexManager index,
			MappingManager mapping_extractor){
		
		this.index=index;
		this.mapping_extractor=mapping_extractor;
		
				
		//We precompute indexes		 
		precomputeIndexCombination.preComputeIdentifierCombination();
		
		
		
	}
	
	
	
	private void initStructures(){
		
		allUNSAT.clear();
		
		unSATvisited=new HashSet<Integer>();
		
		SATvisited=new HashSet<Integer>();
		
		SAT=new HashSet<Integer>();
		
		//unsatIntervals_onto1=new HashSet<Interval>();
		
		unsatClasses2conflictiveMappings= new HashMap<Integer,Set<HornClause>>();
		
		//classes_with_hard_cases=new HashSet<Integer>();
		hard_cases_still2solve=0;
		
		//unsatClasses2repairMappingSet = new HashMap<Integer,Vector<Set<HornClause>>>();
		
		//unsatClasses2sizerepair = new HashMap<Integer,Integer>();
		
		//mappings2Remove4onto1 = new HashSet<HornClause>();
		//mappings2Remove4onto2 = new HashSet<HornClause>();
		hornMappings2Remove = new HashSet<HornClause>();
		
		unsatClasses2repaired = new HashMap<Integer, Boolean>();
		
		ignoreLinks = new HashMap<Integer, Set<Link>>();	
		
		
		
	}
	
	
	
	private void setDowlingAndGallier(
			boolean useProjection,
			Map<Integer, Set<Integer>> mappings2repair){
		
		setDowlingAndGallier(useProjection, mappings2repair, false);
		
	}
	
	
	
	private void setDowlingAndGallier(
			boolean useProjection,
			Map<Integer, Set<Integer>> mappings2repair,
			boolean addClassTypes){
	
		Set<Integer> mapped_entities = new HashSet<Integer>();
		
		//We include all entities. Note that we store both directions ofmappings
		mapped_entities.addAll(mapping_extractor.getFixedMappings().keySet());
		//
		mapped_entities.addAll(mappings2repair.keySet());//Do they represent mappings L2R or R2L?
		
		if (useProjection){
			index.setSmallProjection4MappedEntities(mapped_entities);
		}
		
			
		dgSat = new DowlingGallierHornSAT(
				index.getDirectSubClasses(useProjection),
				index.getEquivalentClasses(),//inludes both sides
				index.getDirectDisjointClasses(),
				index.getGeneralHornAxiom(),
				addClassTypes,
				index.getDirectIndividualClassTypes(),
				mapping_extractor.getFixedMappings(),
				mappings2repair,
				hornMappings2Remove);
		
		
	}
	
	
	/**
	 * This method evaluates the satisfiability of the integration together withh the given mappings
	 * @param mappings2repair
	 * @throws Exception
	 */
	public void CountSatisfiabilityOfIntegration_DandG(
			Map<Integer, Set<Integer>> mappings2repair) {
		
		
		initStructures();		
		if(mapping2NumOfConflicts == null)
			mapping2NumOfConflicts = new HashMap<HornClause,Integer>();
		else 
			mapping2NumOfConflicts.clear();
				
		
		//---------------------------------------		
		// ALL ONTOLOGY
		//---------------------------------------
		LogOutput.print("\n\nCOUNT SAT ALL ONTOLOGY");
		LogOutput.print("-------------------------");
		useProjection=false;
		dealWithHardCases=false;
		
		setDowlingAndGallier(useProjection, mappings2repair);
				
		//LogOutput.print("Repairing from Roots: " + index.getRootIdentifiers().size());
		CountSatisfiabilityOfIntegration_DandG(index.getRootIdentifiers());
		
		
		LogOutput.print("\tUNSATifiabilities found with Dowling and Gallier (approximation): " + unSATvisited.size());
		//LogOutput.print("UNSAT found (count): " + unSATvisited.size());
		//LogOutput.print("SAT found: " + SATvisited.size());
		
		unSATvisited.clear(); //we need to clear structure
		SATvisited.clear();
		
		
		for (HornClause mapping : mapping2NumOfConflicts.keySet()){
			LogOutput.printAlways(mapping + ", conflict: " + mapping2NumOfConflicts.get(mapping));
		}
		
		
		
	}
	
	
	Set<Integer> allSubClasses = new HashSet<Integer>();
	
	
	private void getSubclasses4Identifiers(Set<Integer> classes){
		
		allSubClasses.addAll(classes);
		
		if (classes.size()<1) //exit condition
			return;
		
		Set<Integer> subClasses = new HashSet<Integer>();
		
		for (int ide : classes){
			subClasses.addAll(index.getDirectSubClasses4Identifier(ide, false));
		}
		
		
		getSubclasses4Identifiers(subClasses);
		
		
	}
	
	private void CountSatisfiabilityOfIntegration_DandG(Set<Integer> classes){
		
		boolean satisfiable;
						
		for (int cls : classes){ //end recursivity if empty set
		
			//Check if there is a superclass with same conflictive mappings
			
		
			//if (unSAT_onto1.contains(cls) || SAT_onto1.contains(cls)  || isClassInUnsatIntervals_onto1(cls)){ //we already know that is unsat or not (already visited)
			//Already visited
			if (unSATvisited.contains(cls) || SATvisited.contains(cls)){ //perhaps unsat in subclasses are caused by other pairs of mappings
				continue;
			}
			
								
			satisfiable=dgSat.isSatisfiable(cls);				
						
			
			if (satisfiable){				
				SATvisited.add(cls);				
				CountSatisfiabilityOfIntegration_DandG(index.getDirectSubClasses4Identifier(cls, useProjection));
							
			}
			else { //UNSAT
				
				//To keep conflictiveness
				//Based on Alessandro's code
				for (HornClause clause : dgSat.getMappingsInvolvedInError()) {
					if(mapping2NumOfConflicts.containsKey(clause)){
						int updatedVal = 1+mapping2NumOfConflicts.get(clause);
						mapping2NumOfConflicts.put(clause, updatedVal);
					}
					else
						mapping2NumOfConflicts.put(clause, 1);
				}
				//end conflictiveness extraction
				
		
				unSATvisited.add(cls);
				
				
				//We check conflictive mappings of all classes
				CountSatisfiabilityOfIntegration_DandG(index.getDirectSubClasses4Identifier(cls, useProjection));
				
				//Uncommnent: if used for statistics only
				//getSubclasses4Identifiers(index.getDirectSubClasses4Identifier(cls, useProjection));
				//unSATvisited.addAll(allSubClasses);				
				//allSubClasses.clear();			
								
								
			}
									
			
		}
		
		
		
	}
	
	


	
	
	
	/**
	 * This method evaluates the satisfiability of the integration together withh the given mappings
	 * @param mappings2repair
	 * @throws Exception
	 */
	public void CheckSatisfiabilityOfIntegration_DandG(
			Map<Integer, Set<Integer>> mappings2repair) {
		
		
		
		averageConflictiveMappings=0.0;
		maxConflictiveMappings=0;
		minConflictiveMappings=10000;
		
		averageRepairMappings=0.0;
		maxRepairMappings=0;
		minRepairMappings=10000;
		
		averageSizeRepair=0.0;
		maxSizeRepair=0;
		minSizeRepair=10000;
		
		
		long inittotal,init1,fin1;
		
		initStructures();
		
		
		//Optional check to count the total unsat classes
		//LogOutput.print("INITIAL UNSATISFIABILITY");
		//CountSatisfiabilityOfIntegration_DandG();
		
		
		inittotal = Calendar.getInstance().getTimeInMillis();
		
		//It is usefull when a huge number of unsat is found
		boolean useSmallProjection=true;
		if (useSmallProjection){
			//---------------------------------------		
			//SMALL PROJECTION
			//---------------------------------------
			LogOutput.print("\n\nSMALL PROJECTION");
			LogOutput.print("-------------------------");
			init1 = Calendar.getInstance().getTimeInMillis();
			useProjection=true;
			dealWithHardCases=false;
			
			setDowlingAndGallier(useProjection, mappings2repair);
			
			
			//LogOutput.print("Repairing from Roots: " + index.getRootIdentifiers().size());
			CheckSatisfiabilityOfIntegration_DandG(index.getRootIdentifiers());
			
			SAT.addAll(SATvisited);
			
				
			LogOutput.print("UNSAT found: " + unSATvisited.size());
			//LogOutput.print("SAT found: " + SATvisited.size());
			
			unSATvisited.clear(); //we need to clear structure
			SATvisited.clear();
			
			LogOutput.print("Remaining hard cases onto from small projection (1 Iter): " + hard_cases_still2solve);
			
			fin1 = Calendar.getInstance().getTimeInMillis();
			LogOutput.print("\tTime ckecking satisfiability with D&G (Small projection, simple cases) (s): " + (float)((double)fin1-(double)init1)/1000.0);

		
			//HARD CASES
			//---------
			if (hard_cases_still2solve>0){
				
				LogOutput.print("Solving hard cases...");
				
				hard_cases_still2solve=0;//we reinit
				
				dealWithHardCases=true; //the unique difference is in the repair
				init1 = Calendar.getInstance().getTimeInMillis();
				
				CheckSatisfiabilityOfIntegration_DandG(index.getRootIdentifiers());
				
				SAT.addAll(SATvisited);
					
				unSATvisited.clear(); //we need to clear structure
				SATvisited.clear();
		
				LogOutput.print("Remaining hard cases onto from small projection (2 Iter): " + hard_cases_still2solve);
				
				fin1 = Calendar.getInstance().getTimeInMillis();
				LogOutput.print("\tTime ckecking satisfiability with D&G (Small projection, hard cases) (s): " + (float)((double)fin1-(double)init1)/1000.0);
			
			}
		}
		
		//We will visit all again if necessary
		SAT.clear();
		
		//---------------------------------------		
		// ALL ONTOLOGY
		//---------------------------------------
		LogOutput.print("\n\nALL ONTOLOGY");
		LogOutput.print("-------------------------");
		init1 = Calendar.getInstance().getTimeInMillis();
		useProjection=false;
		dealWithHardCases=false;
		
		setDowlingAndGallier(useProjection, mappings2repair);
		
		
		//LogOutput.print("Repairing from Roots: " + index.getRootIdentifiers().size());
		CheckSatisfiabilityOfIntegration_DandG(index.getRootIdentifiers());
		
		SAT.addAll(SATvisited);
			
		LogOutput.print("UNSAT found: " + unSATvisited.size());
		//LogOutput.print("SAT found: " + SATvisited.size());
		
		unSATvisited.clear(); //we need to clear structure
		SATvisited.clear();
		
		LogOutput.print("Remaining hard cases all onto (1 Iter): " + hard_cases_still2solve);
		
		
		fin1 = Calendar.getInstance().getTimeInMillis();
		LogOutput.print("\tTime ckecking satisfiability with D&G (Big projection, simple cases) (s): " + (float)((double)fin1-(double)init1)/1000.0);

		
		
		//HARD CASES
		//---------
		if (hard_cases_still2solve>0){
			
			LogOutput.print("Solving hard cases...");
			
			hard_cases_still2solve=0;//we reinit
			
			dealWithHardCases=true;
		
			init1 = Calendar.getInstance().getTimeInMillis();
			
			CheckSatisfiabilityOfIntegration_DandG(index.getRootIdentifiers());
			
			SAT.addAll(SATvisited);
				
			unSATvisited.clear(); //we need to clear structure
			SATvisited.clear();
	
			LogOutput.print("REMAINING HARD CASES for all onto (2 Iter): " + hard_cases_still2solve);
			
			fin1 = Calendar.getInstance().getTimeInMillis();
			LogOutput.print("Time ckecking satisfiability with D&G (Big projection, hard cases) (s): " + (float)((double)fin1-(double)init1)/1000.0);

		}		
		
		//--------------------------------
		//REMOVE MAPPINGS FROM STRUCTURES
		//---------------------------------
		remove_weaken_ConflictiveMappings2(mappings2repair);
		
		
		
		
		//STATISTICS
		LogOutput.print("\nAverage mappings per conflict: " + averageConflictiveMappings/(double)(unsatClasses2conflictiveMappings.size()));
		LogOutput.print("Max mappings per conflict: " + maxConflictiveMappings);
		LogOutput.print("Min mappings per conflict: " + minConflictiveMappings);
		
		LogOutput.print("Average size of repair: " + averageSizeRepair/(double)(unsatClasses2conflictiveMappings.size()));
		LogOutput.print("Max size of repair: " + maxSizeRepair);
		LogOutput.print("Min size of repair: " + minSizeRepair);
		
		LogOutput.print("Average mappings in repair: " + averageRepairMappings/(double)(unsatClasses2conflictiveMappings.size()));
		LogOutput.print("Max mappings per repair: " + maxRepairMappings);
		LogOutput.print("Min mappings per repair: " + minRepairMappings);
		
		
		
	}
	
	
	
	
	
	
	
	private void CheckSatisfiabilityOfIntegration_DandG(Set<Integer> classes){
		
		boolean satisfiable;
		
		boolean plan_was_found;
						
		for (int cls : classes){ //end recursivity if empty set
		
			//Check if there is a superclass with same conflictive mappings
			
		
			//if (unSAT_onto1.contains(cls) || SAT_onto1.contains(cls)  || isClassInUnsatIntervals_onto1(cls)){ //we already know that is unsat or not (already visited)
			//Already visited
			if (unSATvisited.contains(cls) || SATvisited.contains(cls)){ //perhaps unsat in subclasses are caused by other pairs of mappings
				continue;
			}
			
			//Known to be sat
			if (SAT.contains(cls) || (unsatClasses2repaired.containsKey(cls) && unsatClasses2repaired.get(cls)))
				satisfiable=true;
			else {					
				satisfiable=dgSat.isSatisfiable(cls);				
			}
						
			
			if (satisfiable){				
				SATvisited.add(cls);
				
				//LogOutput.print("Subclasses: " + index.getDirectSubClasses4Identifier(cls, useProjection).size());
				CheckSatisfiabilityOfIntegration_DandG(index.getDirectSubClasses4Identifier(cls, useProjection));
							
			}
			else { //UNSAT
				
				unSATvisited.add(cls);			
				allUNSAT.add(cls);
				
				//We use all mappings in error
				dgSat.setConflictiveMappingsAsInvolvedMappings();
				
				
				//In some cases there are side effecte between mappings and we need to collect more mappings
				completeSetOfConflictiveMappings(cls, dgSat.getConflictiveMappings());  
				

				//only if thre is not a class with same set of conflictive mappings
				//We store unsat class with set of conflictive classes
				//if (!unsatClasses2conflictiveMappings_onto1.values().contains(dgSat.getConflictiveMappings())){
				//if (!unsatClasses2conflictiveMappings_onto1.containsValue(dgSat.getConflictiveMappings())){
					
				///Note: superclasses may be repaired easier and the conflict still be present for the subclass
				//It seems that this only happen in a few cases but still
					
				unsatClasses2conflictiveMappings.put(cls, new HashSet<HornClause>(dgSat.getConflictiveMappings()));
										
					
				averageConflictiveMappings+=dgSat.getConflictiveMappings().size();
				if (dgSat.getConflictiveMappings().size()>maxConflictiveMappings)
					maxConflictiveMappings=dgSat.getConflictiveMappings().size();
				if (dgSat.getConflictiveMappings().size()<minConflictiveMappings)
					minConflictiveMappings=dgSat.getConflictiveMappings().size();
					
				//LogOutput.print(numUnsat + ". Unsat onto1: " + cls);
				//LogOutput.print("\tConflicts Mappings: " + dgSat.getConflictiveMappings().size() + " - " + dgSat.getConflictiveMappings());
				
				//Gets reapir plan!
				plan_was_found=createRepairPlans(cls);				
							
					
				/*LogOutput.print("Onto 1");
				LogOutput.print("Mappings in the conflict: " + dgSat.getMappingsInvolvedInError().size());
				LogOutput.print("\t" + dgSat.getMappingsInvolvedInError());
				LogOutput.print("Conflictive Mappings: " + dgSat.getConflictiveMappings().size());
				LogOutput.print("\t" + dgSat.getConflictiveMappings() + "\n");
				*/
				
				//We also analyze subclasses just in case we get new conflictive mappings
				if (plan_was_found){
					//num_cases++;
					CheckSatisfiabilityOfIntegration_DandG(index.getDirectSubClasses4Identifier(cls, useProjection));
				}
				else{
					//If father is a hard case, the children will be too! Then we add all of them to hard case structure structures
					unsatClasses2conflictiveMappings.remove(cls);//We will analyze them later
					//addSubClasses2hardcases_onto1(index_onto1.getDirectSubClasses4Identifier(cls, withmodule));
				}
				
			}
									
			
		}
		
		
		
	}
	
	
	
	

	
	
	
	
	
	
	/**
	 * This method evaluates the satisfiability of the integration together with the given mappings
	 * We only consider individuals in the cleaning
	 * @param mappings2repair
	 * @throws Exception
	 */
	public void CheckSatisfiabilityOfIntegration_DandG_Individuals(
			Map<Integer, Set<Integer>> mappings2repair) {
		
		
		
		averageConflictiveMappings=0.0;
		maxConflictiveMappings=0;
		minConflictiveMappings=10000;
		
		averageRepairMappings=0.0;
		maxRepairMappings=0;
		minRepairMappings=10000;
		
		averageSizeRepair=0.0;
		maxSizeRepair=0;
		minSizeRepair=10000;
		
		
		long inittotal,init1,fin1;
		
		initStructures();
		
		
		//Optional check to count the total unsat classes
		//LogOutput.print("INITIAL UNSATISFIABILITY");
		//CountSatisfiabilityOfIntegration_DandG();
		
		
		inittotal = Calendar.getInstance().getTimeInMillis();
		
		
		
		//We will visit all again if necessary
		SAT.clear();
		
		//---------------------------------------		
		// ALL ONTOLOGY
		//---------------------------------------
		LogOutput.print("\n\nALL ONTOLOGY");
		LogOutput.print("-------------------------");
		init1 = Calendar.getInstance().getTimeInMillis();
		useProjection=false;
		dealWithHardCases=false;
		
		setDowlingAndGallier(useProjection, mappings2repair, true); //we add indiv2classtypes
		
		
		CheckSatisfiabilityOfIntegration_DandG_Individuals(index.getIndividuaIdentifierSet());
		
		SAT.addAll(SATvisited);
			
		LogOutput.print("UNSAT found: " + unSATvisited.size());
		//LogOutput.print("SAT found: " + SATvisited.size());
		
		unSATvisited.clear(); //we need to clear structure
		SATvisited.clear();
		
		LogOutput.print("Remaining hard cases all onto (1 Iter): " + hard_cases_still2solve);
		
		
		fin1 = Calendar.getInstance().getTimeInMillis();
		LogOutput.print("\tTime ckecking satisfiability with D&G (Big projection, simple cases) (s): " + (float)((double)fin1-(double)init1)/1000.0);

		
		
		//HARD CASES
		//---------
		if (hard_cases_still2solve>0){
			
			LogOutput.print("Solving hard cases...");
			
			hard_cases_still2solve=0;//we reinit
			
			dealWithHardCases=true;
		
			init1 = Calendar.getInstance().getTimeInMillis();
			
			CheckSatisfiabilityOfIntegration_DandG(index.getIndividuaIdentifierSet());
			
			SAT.addAll(SATvisited);
				
			unSATvisited.clear(); //we need to clear structure
			SATvisited.clear();
	
			LogOutput.print("REMAINING HARD CASES for all onto (2 Iter): " + hard_cases_still2solve);
			
			fin1 = Calendar.getInstance().getTimeInMillis();
			LogOutput.print("Time ckecking satisfiability with D&G (Big projection, hard cases) (s): " + (float)((double)fin1-(double)init1)/1000.0);

		}		
		
		//--------------------------------
		//REMOVE MAPPINGS FROM STRUCTURES (individuals)
		//---------------------------------
		removeConflictiveInstanceMappings(mappings2repair);
		
		
		
		
		//STATISTICS
		LogOutput.print("\nAverage mappings per conflict: " + averageConflictiveMappings/(double)(unsatClasses2conflictiveMappings.size()));
		LogOutput.print("Max mappings per conflict: " + maxConflictiveMappings);
		LogOutput.print("Min mappings per conflict: " + minConflictiveMappings);
		
		LogOutput.print("Average size of repair: " + averageSizeRepair/(double)(unsatClasses2conflictiveMappings.size()));
		LogOutput.print("Max size of repair: " + maxSizeRepair);
		LogOutput.print("Min size of repair: " + minSizeRepair);
		
		LogOutput.print("Average mappings in repair: " + averageRepairMappings/(double)(unsatClasses2conflictiveMappings.size()));
		LogOutput.print("Max mappings per repair: " + maxRepairMappings);
		LogOutput.print("Min mappings per repair: " + minRepairMappings);
		
		
		
	}
	
	
	
	
	
	
	
	private void CheckSatisfiabilityOfIntegration_DandG_Individuals(Set<Integer> individuals){
		
		boolean satisfiable;
		
		boolean plan_was_found;
						
		for (int indiv : individuals){ //end recursivity if empty set
		
			
			//Already visited
			if (unSATvisited.contains(indiv) || SATvisited.contains(indiv)){ //perhaps unsat in subclasses are caused by other pairs of mappings
				continue;
			}
			
			//Known to be sat
			if (SAT.contains(indiv) || (unsatClasses2repaired.containsKey(indiv) && unsatClasses2repaired.get(indiv)))
				satisfiable=true;
			else {					
				satisfiable=dgSat.isSatisfiable(indiv);				
			}
						
			
			if (satisfiable){				
				SATvisited.add(indiv);
							
			}
			else { //UNSAT
				
				unSATvisited.add(indiv);			
				allUNSAT.add(indiv);
				
				//We use all mappings in error
				dgSat.setConflictiveMappingsAsInvolvedMappings();
				
				
				//In some cases there are side effecte between mappings and we need to collect more mappings
				completeSetOfConflictiveMappings(indiv, dgSat.getConflictiveMappings());  
				
					
				unsatClasses2conflictiveMappings.put(indiv, new HashSet<HornClause>(dgSat.getConflictiveMappings()));
										
					
				averageConflictiveMappings+=dgSat.getConflictiveMappings().size();
				if (dgSat.getConflictiveMappings().size()>maxConflictiveMappings)
					maxConflictiveMappings=dgSat.getConflictiveMappings().size();
				if (dgSat.getConflictiveMappings().size()<minConflictiveMappings)
					minConflictiveMappings=dgSat.getConflictiveMappings().size();
					
				//LogOutput.print(numUnsat + ". Unsat onto1: " + cls);
				//LogOutput.print("\tConflicts Mappings: " + dgSat.getConflictiveMappings().size() + " - " + dgSat.getConflictiveMappings());
				
				//Gets reapir plan!
				plan_was_found=createRepairPlans(indiv);				
							
					
				/*LogOutput.print("Onto 1");
				LogOutput.print("Mappings in the conflict: " + dgSat.getMappingsInvolvedInError().size());
				LogOutput.print("\t" + dgSat.getMappingsInvolvedInError());
				LogOutput.print("Conflictive Mappings: " + dgSat.getConflictiveMappings().size());
				LogOutput.print("\t" + dgSat.getConflictiveMappings() + "\n");
				*/
				
				//We also analyze subclasses just in case we get new conflictive mappings
				if (plan_was_found){
					
					//Do nothing: instances has no subclasses
				}
				else{
					//If father is a hard case, the children will be too! Then we add all of them to hard case structure structures
					unsatClasses2conflictiveMappings.remove(indiv);//We will analyze them later
					
				}
				
			}
									
			
		}
		
		
		
	}
	
	
	
	
	
	
	public int getNumRepairedUnsatClasses(){
		return allUNSAT.size();
	}
	
	
	
	/**
	 * This method evaluates the satisfiability of the integration together withh the given mappings
	 * @param mappings2repair
	 * @throws Exception
	 */
	public void CheckSatisfiabilityOfConcreteClasses_DandG(
			Map<Integer, Set<Integer>> mappings2repair, Set<Integer> classes2check) throws Exception{
		
		
		long inittotal,init1,fin1;
		
		
		LogOutput.print("SAT visited all: " + SAT.size());
		LogOutput.print("All UNSAT visited all: " + allUNSAT.size());
		LogOutput.print("New classes to check: " + classes2check.size());
		for (int ide : classes2check){
			if (SAT.contains(ide)){
				LogOutput.print("\t" + ide + " was visited.");
			}
			
			if (allUNSAT.contains(ide)){
				LogOutput.print("\t" + ide + " was unsat.");
			}
		}
		
		
		initStructures();
		
		
		inittotal = Calendar.getInstance().getTimeInMillis();
		
		//---------------------------------------		
		// ALL ONTOLOGY
		//---------------------------------------
		LogOutput.print("\n\nALL ONTOLOGY");
		LogOutput.print("-------------------------");
		init1 = Calendar.getInstance().getTimeInMillis();
		useProjection=false;
		dealWithHardCases=false;
		
		setDowlingAndGallier(useProjection, mappings2repair);
		
		
		//LogOutput.print("Repairing from Roots: " + index.getRootIdentifiers().size());
		CheckSatisfiabilityOfIntegration_DandG(classes2check);
		
		SAT.addAll(SATvisited);
			
		LogOutput.print("UNSAT found: " + unSATvisited.size());
		LogOutput.print("SAT found: " + SATvisited.size());
		
		unSATvisited.clear(); //we need to clear structure
		SATvisited.clear();
		
		LogOutput.print("Remaining hard cases all onto: " + hard_cases_still2solve);
		
		
		fin1 = Calendar.getInstance().getTimeInMillis();
		LogOutput.print("\tTime ckecking satisfiability with D&G (Small projection, simple cases) (s): " + (float)((double)fin1-(double)init1)/1000.0);

		
		
		
		
	}
	
	
	
	/**
	 * We complete the set of confluctive mappings
	 * @param entity
	 * @param onto
	 * @param mappings2ignore
	 */
	private void completeSetOfConflictiveMappings(int entity, Set<HornClause> mappings2ignore){
		
		try{
			
			//Ignore all mappings: if unsat -> add new ones#
			ignoreLinks.clear();		
			
			for (HornClause clause : mappings2ignore){
							
				if (clause.getDirImplication()==HornClause.L2R){
											
					addParticularIgnoreLink(
							clause.getLeftHS1(),
							clause.getLabel(),
							clause.getRightHS());												
																	
				}
				else {
					
					addParticularIgnoreLink(
							clause.getRightHS(),
							clause.getLabel(),
							clause.getLeftHS1());
					
				}
			}
				
		
		
			//LogOutput.print(ignoreLinks.size() + " " + ignoreLinks);
			
			if (!dgSat.isSatisfiable(entity, ignoreLinks)){
				
				LogOutput.print("Case with more erroneous mappings! " + entity + "  " + dgSat.getConflictiveMappings().size());
				
				//We add new mappings in error
				dgSat.incrementConflictiveMappingsWithNewInvolvedMappings();
				
				LogOutput.print("\t" + dgSat.getConflictiveMappings().size());
				
				//We check again				
				//Review this stop condition....
				if (dgSat.getConflictiveMappings().size()!=mappings2ignore.size() && dgSat.getConflictiveMappings().size()>0){
					completeSetOfConflictiveMappings(entity, dgSat.getConflictiveMappings());
				}
				else{
					LogOutput.print("No mappings detected in inconsistency. The ontology was probably inconsistent before alignment.");
				}
			}
		}
		catch (Exception e){
			System.err.println("ERROR: " + e.getMessage());
		}
		
		
	}
	
	private void addParticularIgnoreLink(int origin, int label, int target){
		
		if (!ignoreLinks.containsKey(origin))
			ignoreLinks.put(origin, new HashSet<Link>());
		
		ignoreLinks.get(origin).add(new Link(label, target));
		
	}
	
	
	
	/**
	 * Extract repair plans using the conflictive mappings reported by D&G
	 * @param entity
	 * @return
	 */
	private boolean createRepairPlans(int entity){
		
		ignoreLinks.clear();		
		
		unsatClasses2repaired.put(entity, false);
		
		//Not used
		//We create a new entry for the entity
		//if (!unsatClasses2repairMappingSet.containsKey(entity))
		//	unsatClasses2repairMappingSet.put(entity, new Vector<Set<HornClause>>());
	
		
		RepairmentPlan selectedPlan = null;		
		boolean no_exit=true;
		int size_plan=1;
		
		Vector<HornClause> conflictiveMappings = new Vector<HornClause>(dgSat.getConflictiveMappings());
		Set<Set<Integer>> mappingCombinations;
		
		//Note that we give preference to smaller plans (i.e. less mappins)
		//So that if a repair plan with 1 mapping is found we do not look to plans with 2 mappings
		while (selectedPlan == null && no_exit){
			
			//Useful for hard cases. We remove everything
			if (conflictiveMappings.size()<=size_plan){
				mappingCombinations = precomputeIndexCombination.getMaxCombination(conflictiveMappings.size());
			}
			else{
				mappingCombinations = precomputeIndexCombination.getIdentifierCombination(conflictiveMappings.size(), size_plan);
			}
			
			
			Queue<RepairmentPlan> plans = orderPlans(mappingCombinations, conflictiveMappings);
			
			LogOutput.print("PLANS of size " + size_plan + " for entity "+ entity + ": " + plans.size());
			//LogOutput.print("Mappings to delete later: " + hornMappings2Remove.toString());
			//LogOutput.print("Mappings to delete now: " + dgSat.getGeneralLink2Ignore().toString());
			
			while(!plans.isEmpty() && selectedPlan==null) {
				
				RepairmentPlan plan = plans.poll();
				
				LogOutput.printAlways("Evaluating plan of size: " + plan.getMappings().size() + ", conflict: " + plan.getconflictiveness() + ", confidence: " + plan.getConfidence());
				
				ignoreLinks.clear();
				for(HornClause mapping : plan.getMappings()) {
					if (mapping.getDirImplication()==HornClause.L2R){
						addParticularIgnoreLink(mapping.getLeftHS1(), mapping.getLabel(), mapping.getRightHS());	
					} else {
						addParticularIgnoreLink(mapping.getRightHS(), mapping.getLabel(), mapping.getLeftHS1());
					}
				}
				if (dgSat.isSatisfiable(entity, ignoreLinks)){
					selectedPlan = plan;
					LogOutput.printAlways("Plan found!");
					//unsatClasses2repairMappingSet.get(entity).add(plan.getMappings());
				}
			}
			//Stop while
			
			
			
			//Plan found: we repair on the fly
			if(selectedPlan != null) {
				//There is a plan
				unsatClasses2repaired.put(entity, true);
				
				hornMappings2Remove.addAll(selectedPlan.getMappings());//For future D&G settings
				
				for (HornClause clausemap : selectedPlan.getMappings()){ //For current D&G setting
					//General!Ignorelinks
					//TODO we add both sides to easy cleaning
					//TODO Later weakened mappings will be assessed again
					/*if (clausemap.getDirImplication()==HornClause.L2R){
						dgSat.addGeneralLink2Ignore(clausemap.getLeftHS1(), clausemap.getLabel(), clausemap.getRightHS());
					}
					else {
						dgSat.addGeneralLink2Ignore(clausemap.getRightHS(), clausemap.getLabel(), clausemap.getLeftHS1());
					}*/
					dgSat.addGeneralLink2Ignore(clausemap.getLeftHS1(), clausemap.getLabel(), clausemap.getRightHS());
					dgSat.addGeneralLink2Ignore(clausemap.getRightHS(), clausemap.getLabel(), clausemap.getLeftHS1());
					
				}
			}
			else{//No plan found
				no_exit = continueWithNextPlan(entity, size_plan);
				
				if (no_exit){
					if (dealWithHardCases){ //we remove all involved mappings. No "easy" plan found for this case
						size_plan = conflictiveMappings.size();
					}
					else {
						size_plan++;//Continue
					}
				}
			}
			
			
		}//end while plan search
	
		
		
		//STATISTICS
		if (selectedPlan != null){ //plan found
			
			//Note that since mappingsinrepair is no longer computed,
			//the size of the selected plan for repairment is used instead for statistics.
			
			averageRepairMappings+=selectedPlan.getMappings().size();
			if (selectedPlan.getMappings().size()>maxRepairMappings)
				maxRepairMappings=selectedPlan.getMappings().size();
			if (selectedPlan.getMappings().size()<minRepairMappings)
				minRepairMappings=selectedPlan.getMappings().size();
					
			//unsatClasses2sizerepair.put(entity, size_plan);
			
			averageSizeRepair+=size_plan;
			if (size_plan>maxSizeRepair)
				maxSizeRepair=size_plan;
			if (size_plan<minSizeRepair)
				minSizeRepair=size_plan;
			
			return true; //plan
		}
		
		return false; //no plan
		
	}
	
	
	
	private boolean continueWithNextPlan(int entity, int size_plan){
		
		if (size_plan>=dgSat.getConflictiveMappings().size()){
			//No plan
			
			//classes_with_hard_cases.add(entity);
			
			LogOutput.print("NO PLAN!!!  " + entity + "  " + dgSat.getConflictiveMappings().size() + "  " + size_plan);
			//LogOutput.print("Mappings in new error: " + dgSat.getMappingsInvolvedInError().size() + "  "+ dgSat.getMappingsInvolvedInError());
			//LogOutput.print("Mappings in conflict: " + dgSat.getConflictiveMappings().size() + "  "+ dgSat.getConflictiveMappings());
			//LogOutput.print("Ignored links: " + ignoreLinks.size() + "  "+ ignoreLinks);
			
			unsatClasses2repaired.remove(entity);
			//unsatClasses2repairMappingSet.remove(entity);
			
			if (dealWithHardCases){
				hard_cases_still2solve++;
			}				
			
			return false; //no_exit=false;
		} 
		
		else if (!dealWithHardCases && ((dgSat.getConflictiveMappings().size()>40  && size_plan>=1) || 
				(dgSat.getConflictiveMappings().size()>30 && size_plan>=2) ||
				(dgSat.getConflictiveMappings().size()>20 && size_plan>=3) || 
				(dgSat.getConflictiveMappings().size()>15 && size_plan>=4))){
			//|| (dgSat.getConflictiveMappings().size()>10 && size_plan>=6)){
		//hard case
		//To avoid hard cases. they will be repaired later				
		
			//classes_with_hard_cases.add(entity);
			
			LogOutput.print("HARD CASE!!!" + entity + "  " + dgSat.getConflictiveMappings().size() + "  " + size_plan);
			unsatClasses2repaired.remove(entity);
			//unsatClasses2repairMappingSet.remove(entity);
			
			hard_cases_still2solve++;
		
			return false; //no_exit=false;
		
		} 
		
		else if (dealWithHardCases && ((dgSat.getConflictiveMappings().size()>40  && size_plan>=2) || 
				(dgSat.getConflictiveMappings().size()>30 && size_plan>=3) ||
				(dgSat.getConflictiveMappings().size()>25 && size_plan>=4) || 
				(dgSat.getConflictiveMappings().size()>15 && size_plan>=5))){
			//hard case again
			//classes_with_hard_cases.add(entity);  
			
			LogOutput.print("HARD CASE WITHOUT PLAN!!!" + entity + "  " + dgSat.getConflictiveMappings().size() + "  " + size_plan);
			hard_cases_still2solve++;
			
			
			return true; //no_exit=true; //Continue but deleting all
			
		}	
		else { //Continue with next group
			return true;
		}
	}
	
	
	private double getConfidence4Clause(HornClause mapping_clause){
		
		//String ide1 = index.getLabel4ConceptIndex(mapping_clause.getLeftHS1());
		//String ide2 =  index.getLabel4ConceptIndex(mapping_clause.getRightHS());
		//LogOutput.print("\tISUB: " + ide1 + " - " + ide2 + " " + mapping_clause.getDirImplication());
		//LogOutput.print("\tISUB: " + ide1 + " -> " + ide2 + " " + mapping_extractor.getISUB4Mapping(mapping_clause.getLeftHS1(), mapping_clause.getRightHS()));
		//LogOutput.print("\tSCOPE:  " + ide1 + " -> " + ide2 + " " + mapping_extractor.getScope4Mapping(mapping_clause.getLeftHS1(), mapping_clause.getRightHS()));
		
		return mapping_extractor.getConfidence4Mapping(mapping_clause.getLeftHS1(), mapping_clause.getRightHS());
	}
	
	
	private double getConflictiness4Clause(HornClause mapping_clause){
		
		if (mapping2NumOfConflicts!=null && mapping2NumOfConflicts.containsKey(mapping_clause))
			return mapping2NumOfConflicts.get(mapping_clause);
		else{
			LogOutput.printAlways("\t"+mapping_clause + " without conflictness");
			return 0;		
		}
			
	}
	
	
	/**
	 * Orders a set of candidate plans according to their confidences
	 * (computed for each plan by adding their mapping confidences)
	 * @param mappingCombinations Candidate plans (still not proven to repair the ontology)
	 * @param conflictiveMappings HornClauses representing all conflictive mappings. Integers in mappingCombinations refer to their index here.
	 * @return
	 * @author Anton Morant
	 * @date August, 2011
	 */
	private Queue<RepairmentPlan> orderPlans(Set<Set<Integer>> mappingCombinations,
			Vector<HornClause> conflictiveMappings) {
		
		Queue<RepairmentPlan> plans = new PriorityQueue<RepairmentPlan>(mappingCombinations.size(), new RepairmentPlanComparator());
		
		for(Set<Integer> combination : mappingCombinations) {
			
			Set<HornClause> mappings = new HashSet<HornClause>(); 
			double confidence = 0;
			int conflictiveness = 0;
			
			for(int mappingId : combination) {
				HornClause mapping = conflictiveMappings.get(mappingId);
				mappings.add(mapping);
				//confidence
				confidence += getConfidence4Clause(mapping);
				//conflictiveness			
				if (Parameters.extractGlobal_D_G_Info){
					conflictiveness += getConflictiness4Clause(mapping);
				}
				
			}
			plans.add(new RepairmentPlan(mappings, confidence, conflictiveness));
			
		}
		return plans;
	}
	
	
	
	
	

	
	
	
	/**
	 * Remove/Weaken mappings from structures
	 */
	private void remove_weaken_ConflictiveMappings2(Map<Integer, Set<Integer>> mappings2repair){
		
		int target;
		int origin; 
		
		int weakened=0;
		int discarded=0;
		
		
		//To check if both sides must be deleted
		for (HornClause clause : hornMappings2Remove){ 
		
			//In current setting we only delete HashMap (e.g. exact mappings), we do not maintain a new structure with MappingObjects
			
			
			if (clause.getDirImplication()==HornClause.L2R){
				origin=clause.getLeftHS1();
				target=clause.getRightHS();
				
			}
			else{
				origin=clause.getRightHS();
				target=clause.getLeftHS1();
			}
			
			
			
			//Remove Conflictive clause
			if (mappings2repair.containsKey(origin)){
				mappings2repair.get(origin).remove(target);
				if (mappings2repair.get(origin).size()==0){
					mappings2repair.remove(origin);
				}
			}
			
			mapping_extractor.addSubMapping2ConflictiveAnchors(origin, target, true);
			
		
			//Alessandro - 17 June 2014: you need to check if the input 
			//mapping was a subsumption, otherwise you will add the reversed 
			//mapping that was not in the input alignment! (added OR condition)
			
			//If the other side of the mappings was already discarded
			if (mapping_extractor.isMappingInConflictiveSet(target, origin) 
				|| !mapping_extractor.isMappingInAnchors(origin, target)){ //added second OR
				weakened--;
				discarded++;
				
				mapping_extractor.removeSubMappingFromWeakenedDandGMappings(origin, target);
				
			}
			else{
				weakened++;
				
				//Remove other side as well (we only keep in weakened)
				if (mappings2repair.containsKey(target)){
					if (mappings2repair.get(target).contains(origin)){
						mappings2repair.get(target).remove(origin);
						if (mappings2repair.get(target).size()==0){
							mappings2repair.remove(target);
						}
						
						//Add other side to weakend, "only" if it existed
						//Bug detected by Alessandro Solinando. August 2014
						//fixed weakening for monodirectional mappings that was creating "phantom" mappings not in the input alignement
						mapping_extractor.addSubMapping2WeakenedDandGAnchors(target, origin);
						
					}
				}
				
				
				//mapping_extractor.Mconf_dg++;
				//if (mapping_extractor.isMappingInGoldStandard(origin, target) || mapping_extractor.isMappingInGoldStandard(target, origin))
				//	mapping_extractor.Mconf_dg_ok++;
				
			}
			
			
			
		}//End for clauses
		
		
		
		
		LogOutput.print("\nDISCARDED MAPPINGS: " + discarded + " - ");// + mapping_extractor.getDircardedAnchors().size());
		LogOutput.print("WEAKENED MAPPINGS: " + weakened  + " - " + mapping_extractor.getWeakenedDandGMappings().size());
		LogOutput.print("Clauses 2 ignore: " + hornMappings2Remove.size());
		
	}
	
	
	
	
	/**
	 * Remove mappings from structures
	 */
	private void removeConflictiveInstanceMappings(Map<Integer, Set<Integer>> mappings2repair){
		
		int target;
		int origin; 
		
	
		//To check if both sides must be deleted
		for (HornClause clause : hornMappings2Remove){ 
		
			
			if (clause.getDirImplication()==HornClause.L2R){
				origin=clause.getLeftHS1();
				target=clause.getRightHS();
				
			}
			else{
				origin=clause.getRightHS();
				target=clause.getLeftHS1();
			}
			
			
			
			//Remove Conflictive clause both sides
			//Instance mappings will be only same_as axioms
			
			if (mappings2repair.containsKey(origin)){
				mappings2repair.get(origin).remove(target);
				if (mappings2repair.get(origin).size()==0){
					mappings2repair.remove(origin);
				}
			}
			if (mappings2repair.containsKey(target)){
				mappings2repair.get(target).remove(origin);
				if (mappings2repair.get(target).size()==0){
					mappings2repair.remove(target);
				}
			}
			
			
		}//End for clauses
	
		
	}
	
	
	
	
	
	
	
	
	

	
	
	
	
	

		
	
	
}

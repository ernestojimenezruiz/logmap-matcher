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
package uk.ac.ox.krr.logmap2.reasoning.explanation;

import java.util.*;

import uk.ac.ox.krr.logmap2.io.LogOutput;
import uk.ac.ox.krr.logmap2.utilities.PrecomputeIndexCombination;


import org.semanticweb.owlapi.model.*;



/**
 * This class will extract repair plans of smallest size
 * (Not all minimal plans are guaranteed to be extracted).
 * @author Ernesto Jimenez Ruiz
 * Nov 12, 2011
 *
 */
public class PlanExtractor {
	
	private Map<Integer, OWLAxiom> ident2axiom;// = new HashMap<Integer, OWLAxiom>();
	
	private Map<OWLAxiom, Set<Integer>> axioms2justifications;// = new HashMap<OWLAxiom, Set<Integer>>();
	
	
	//Ordered by impact.
	//private List<OWLAxiom> listOrderedAxioms = new ArrayList<OWLAxiom>();	
	protected TreeSet<OWLAxiom> listOrderedAxioms;  
	
	
	private PrecomputeIndexCombination precomputeIndexCombination = new PrecomputeIndexCombination();
	
	
	
	//Ordered by size
	private List<Set<Integer>> setOfRepairPlans_int;// = new ArrayList<Set<Integer>>();
	private List<Set<OWLAxiom>> setOfRepairPlans_ax;// = new ArrayList<Set<OWLAxiom>>();
	
		
	
	private int num_justifications;
	
	/**
	 * We need to remove at least one axiom from each justifications.
	 * The key is to index in how many justification an axiom appears and then select
	 * small sets of axioms which cancel all justifications
	 * @param allJustifications
	 */
	public PlanExtractor(List<Set<OWLAxiom>> allJustifications){
		
		 ident2axiom = new HashMap<Integer, OWLAxiom>();
		 axioms2justifications = new HashMap<OWLAxiom, Set<Integer>>();
		 setOfRepairPlans_int = new ArrayList<Set<Integer>>();
		 setOfRepairPlans_ax = new ArrayList<Set<OWLAxiom>>();
		
		
		//We precompute indexes
		 precomputeIndexCombination.clearCombinations(); //Old calls
		 precomputeIndexCombination.preComputeIdentifierCombination();
		
		
		num_justifications = allJustifications.size();
		
		
		int ident=0;
		
		for (int index_just=0; index_just<allJustifications.size(); index_just++){
			
			for (OWLAxiom ax : allJustifications.get(index_just)){
				
				if (!axioms2justifications.containsKey(ax)){
					axioms2justifications.put(ax, new HashSet<Integer>());
					//ident2axiom.put(ident, ax);
					//ident++;
				}
				axioms2justifications.get(ax).add(index_just);
			}
		}
		
		//Order axioms by impact
		orderAxiomsByImact();
		
		Iterator<OWLAxiom> it = listOrderedAxioms.iterator();
		
		while (it.hasNext()){
		
			//We add identifiers in order
			ident2axiom.put(ident, it.next());
			ident++;
			
		}
		
		
		
		
		
		//LogOutput.print(axioms2justifications.toString());
		
	}
	
	
	/**
	 * We extract plans with a limit in number and size of axioms in plans
	 * @param maxNumberOfPlans
	 * @param maxSizeOfPlans
	 * @deprecated Not suitable for mappings assessment
	 */
	public void extractPlans_old(int maxNumberOfPlans, int maxSizeOfPlans){
		
		setOfRepairPlans_int.clear();
		setOfRepairPlans_ax.clear();
		
		Set<Set<Integer>> candidate_plans;
		
		int maxSize4Plans;
		
		LogOutput.printAlways("\tAxioms for plans: " + axioms2justifications.size());
		
		//In case combination num is higher than num of axioms
		if (maxSizeOfPlans>axioms2justifications.size())
			maxSize4Plans = axioms2justifications.size();
		else
			maxSize4Plans = maxSizeOfPlans;
		
		
		
		/**
		 * We extract plans in order
		 */
		for (int size_plan=1; size_plan<=maxSize4Plans; size_plan++){
			
			candidate_plans = precomputeIndexCombination.getIdentifierCombination(axioms2justifications.size(), size_plan);
			
			//LogOutput.print(candidate_plans.toString());
			LogOutput.print("Candidates size: " + candidate_plans.size());
			
			//TODO Limit size!!
			if (candidate_plans.size()>50000){//Max combination size! (for tests 50000)
				
				
				//size_plan=maxSize4Plans-size_plan;
				//LogOutput.printAlways("Reducing combinations number.");
				//continue; //we limit number of candidates...
				
				setOfRepairPlans_int.add(
						new HashSet<Integer>(
								precomputeIndexCombination.getMaxCombinationSet(
										axioms2justifications.size())));
				
				LogOutput.printAlways("Adding plan involving all axioms.");
				break;//We delete all!!
				
			}
			
			//Remove from set those sets containing id's already in repair_plans			
			for (Set<Integer> candidate_plan : candidate_plans){
				
				if (isaGoodPlan(candidate_plan)){
					
					//System.out.println(candidate_plan.size());
					setOfRepairPlans_int.add(new HashSet<Integer>(candidate_plan));
					
				}
				
				if (setOfRepairPlans_int.size()>=maxNumberOfPlans){
					break; //We do not look for best level of combination
				}
			}
			
			if (setOfRepairPlans_int.size()>=maxNumberOfPlans){
				break; //We do not look for best level of combination
			}
			
			
			//**We are not looking for all plans...
			if (!setOfRepairPlans_int.isEmpty())
				break;
			
			
			//System.out.println("Size candidate plan: " + size_plan + "  " + maxSize4Plans);
			
			if (size_plan>=maxSize4Plans){
				if (setOfRepairPlans_int.isEmpty()){
					System.out.println("Incrementing size candidate plan: " + size_plan + "  " + maxSize4Plans);
					maxSize4Plans++;//we increment max size, till we find a plan
				}
				
			}
			
		}
		
		
		
		for(int i=0; i<setOfRepairPlans_int.size(); i++){
			setOfRepairPlans_ax.add(new HashSet<OWLAxiom>(getAxioms4PlanIde(i)));
		}
		
		
		//return setOfRepairPlans;
		
		
	}
	
	
	

	/**
	 * We extract plans with a limit in number and size of axioms in plans
	 * @param maxNumberOfPlans
	 * @param maxSizeOfPlans
	 */
	public void extractPlans(){
		
		setOfRepairPlans_int.clear();
		setOfRepairPlans_ax.clear();
		
		Set<Set<Integer>> candidate_plans;
		
		int maxSize4Plans = 20;
		
		int combineOver;
		
		
		
		if (axioms2justifications.size()>20){
			combineOver = getNumberOfAxiomsWithHighImpact(1);
		}
		else{
			combineOver = axioms2justifications.size();
		}

		
		//combineOver = getNumberOfAxiomsWithHighImpact(1);
		
		LogOutput.printAlways("\tAxioms for plans: " + axioms2justifications.size());
		LogOutput.printAlways("\tAxioms for plans (reduced): " + combineOver);
		
		//In case combination num is higher than num of axioms
		if (combineOver > 150){
			maxSize4Plans = 1;
		}
		else if (combineOver > 50){
			maxSize4Plans = 2;
		}
		else if (combineOver > 20){
			maxSize4Plans = 3;
		}
		else if (combineOver > 15){
			maxSize4Plans = 4;
		}
		else {
			maxSize4Plans = combineOver;
		}
		
		
		
		/**
		 * We extract plans in order
		 */
		for (int size_plan=1; size_plan<=maxSize4Plans; size_plan++){
			
			candidate_plans = precomputeIndexCombination.getIdentifierCombination(combineOver, size_plan);
			
			//LogOutput.print(candidate_plans.toString());
			LogOutput.print("Candidates size: " + candidate_plans.size());
			
			
			//Remove from set those sets containing id's already in repair_plans			
			for (Set<Integer> candidate_plan : candidate_plans){
				
				if (isaGoodPlan(candidate_plan)){
					
					//System.out.println(candidate_plan.size());
					setOfRepairPlans_int.add(new HashSet<Integer>(candidate_plan));
					
				}				
				
			}
			
			
			//**We are not looking for all plans...
			if (!setOfRepairPlans_int.isEmpty())
				break;
			
			
		}
		
		//createIncrementalPlan();
		
		//If no repair from combinations (e.g. too hard cases involving a huge number of combinations)	
		if (setOfRepairPlans_int.isEmpty()){
									
			//We seek for plan
			setOfRepairPlans_ax.add(new HashSet<OWLAxiom>(
					createIncrementalPlan())); //in the worst case all axioms will be added
			
			//ADD EVERYTHING: EASY OPTION
			/*setOfRepairPlans_int.add(
					new HashSet<Integer>(
							PrecomputeIndexCombination.getMaxCombinationSet(
									axioms2justifications.size())));
			
			LogOutput.printAlways("Adding plan involving all axioms.");*/
			
		}
		else{
			for(int i=0; i<setOfRepairPlans_int.size(); i++){
				setOfRepairPlans_ax.add(new HashSet<OWLAxiom>(getAxioms4PlanIde(i)));
				
				//System.out.println("PLAN " + i);
				//for (OWLAxiom ax : getAxioms4PlanIde(i)){
				//	System.out.println("\t"+ax);
				//}
				
			}
			
			
			
		}
		
		
		
		
		
		
		
		
		//return setOfRepairPlans;
		
		
	}
	
	
	
	private int getNumberOfAxiomsWithHighImpact(int min_impact){
		
		int count=0;
		
		for (OWLAxiom ax : axioms2justifications.keySet()){
			
			if (getImpact(ax)>1)
				count++;
			
		}
		
		return count;
		
		
	}
	
	
	
	private Set<Integer> justification_ids = new HashSet<Integer>();
	
	private boolean isaGoodPlan(Set<Integer> candidate_plan){
		
		justification_ids.clear();
		
		for (Set<Integer> repair : setOfRepairPlans_int){
			if (candidate_plan.containsAll(repair)){//new candidate is not minimal
				return false;
			}
		}
		
		for (int ide_ax : candidate_plan){
			
			justification_ids.addAll(axioms2justifications.get(ident2axiom.get(ide_ax)));
			
			if (justification_ids.size()==num_justifications){
				return true; //There are axioms involving all justifications
			}
			
		}
		
		//Not a repair
		return false;
		
		
		
		
	}
	
	
	private boolean isaGoodPlanAx(Set<OWLAxiom> candidate_plan){
		
		justification_ids.clear();
		
		for (OWLAxiom ax : candidate_plan){
			
			justification_ids.addAll(axioms2justifications.get(ax));
			
			if (justification_ids.size()==num_justifications){
				return true; //There are axioms involving all justifications
			}
			
		}
		
		//Not a repair
		return false;
		
		
		
		
	}
	
	
	
	
	private List<Set<Integer>> getAllPlansInt(){
		
		return setOfRepairPlans_int;
	}
	
	
	public List<Set<OWLAxiom>> getAllPlansAx(){
		
		return setOfRepairPlans_ax;
	}
		
	
	
	
	/**
	 * We add axioms in order till we get a good plan.
	 * Not necessarily a minimal plan
	 */
	private Set<OWLAxiom> createIncrementalPlan(){
		
		Iterator<OWLAxiom> it = listOrderedAxioms.iterator();
		
		Set<OWLAxiom> axioms_plan = new HashSet<OWLAxiom>();
		
		while (it.hasNext()){
				
			axioms_plan.add(it.next());
			
			if (isaGoodPlanAx(axioms_plan))
				break;			
			
		}
		
		/*System.out.println("PLAN INCREMENTAL");
		for (OWLAxiom ax : axioms_plan){
			System.out.println("\t"+ax);
		}*/
		
		return axioms_plan;
		
		
	}
	
	
	
	/**
	 * We order by impac, that is occurrence number in justifications
	 */
	private void orderAxiomsByImact(){
		
		listOrderedAxioms = new TreeSet<OWLAxiom>(new RepairMappingComparator());
		
		for (OWLAxiom ax : axioms2justifications.keySet()){
			listOrderedAxioms.add(ax);
		}
		
		/*Iterator<OWLAxiom> it = listOrderedAxioms.iterator();		
		while (it.hasNext()){		
				
			OWLAxiom ax = it.next();
			
			System.out.println("Impact Axiom " + ax + "  " + axioms2justifications.get(ax).size());
			
		}*/
		
	}
	
	
	
	
	
	
	private Set<OWLAxiom> set_axioms = new HashSet<OWLAxiom>();
	private Set<OWLAxiom> getAxioms4PlanIde(int idePlan){
		
		set_axioms.clear();
		
		for (int ide : setOfRepairPlans_int.get(idePlan)){
			set_axioms.add(ident2axiom.get(ide));
		}
		
		return set_axioms;
	}
	
	
	private Set<OWLAxiom> getAxioms4PlanInt(Set<Integer> plan_int){
		
		set_axioms.clear();
		
		for (int ide : plan_int){
			set_axioms.add(ident2axiom.get(ide));
		}
		
		return set_axioms;
	}
	
	
	
	public int getImpact(OWLAxiom ax){
		
		return axioms2justifications.get(ax).size();
		
	}
	
	
	/**
	 * Comparator
	 * @author Ernesto
	 *
	 */
	private class RepairMappingComparator implements Comparator<OWLAxiom> {
		
		/**
		 * We order by Impact
		 */
		public int compare(OWLAxiom m1, OWLAxiom m2) {

		
			if (getImpact(m1)<getImpact(m2)){
				return 1;						
			}					
			else{
				return -1;
			}
			
		}
		
	}
		
	
	
	
	

}

package uk.ac.ox.krr.logmap2.indexing;

import java.util.ArrayList;

import java.util.Calendar;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;

import uk.ac.ox.krr.logmap2.indexing.labelling_schema.Interval;
import uk.ac.ox.krr.logmap2.indexing.labelling_schema.IntervalLabelledHierarchy;
import uk.ac.ox.krr.logmap2.indexing.labelling_schema.PreIntervalLabelledHierarchy;
import uk.ac.ox.krr.logmap2.io.LogOutput;


/**
 * This class will gather the indexes of both ontologies
 * 
 *
 * @author Ernesto Jimenez-Ruiz
 * Created: Sep 12, 2011
 *
 */
public class JointIndexManager extends IndexManager {

	
	
	
	/**
	 * Scope to know if the entities in a mppings share scopes, or have somethin in common
	 * @param ide
	 * @return
	 */
	public Set<Integer> getScope4Identifier_Big(int ide){
		
		//We extract a big scope, still with a limit
		return getScope4Identifier(ide, 10, 10, 1000);
		
	}
	
	
	/**
	 * Scope to discover new mappings
	 * @param ide
	 * @return
	 */
	public Set<Integer> getScope4Identifier_Condifence(int ide){
		
		//TODO Store scope?? Better not...
		//3 levels of subclasses and 10 of superclasses
		return getScope4Identifier(ide, 3, 10, 50);
		
	}
	
	
	/**
	 * Scope to extract confidence in mappings
	 * @param ide
	 * @return
	 */
	public Set<Integer> getScope4Identifier_Expansion(int ide){
		return getScope4Identifier(ide, 2, 2, 50);
		
	}
	
	
	public Set<Integer> getSubsetOfSuperClasses4Identifier(int ide){
		return getScope4Identifier(ide, 0, 3, 10);
		
	}
	
	public Set<Integer> getSubsetOfSubClasses4Identifier(int ide){
		return getScope4Identifier(ide, 3, 0, 10);
		
	}
	
	
	
	public Set<Integer> getScope4Identifier(int ide, int sub_levels, int super_levels, int max_size_subclasses){
		
		Set<Integer> scope = new HashSet<Integer>();
		
		allSuperClasses.clear();
		allSubClasses.clear();
		
		if (sub_levels>0){
			getSubclasses4Identifiers(getDirectSubClasses4Identifier(ide, false), sub_levels, max_size_subclasses);
			scope.addAll(allSubClasses);
		}
		
		if (super_levels>0){
			getSuperclasses4Identifiers(getDirectSuperClasses4Identifier(ide, false), super_levels);
			scope.addAll(allSuperClasses);
		}		
		//ide is not in scope
		
		return scope;
		
	}
	
	
	Set<Integer> allSuperClasses = new HashSet<Integer>();
	Set<Integer> allSubClasses = new HashSet<Integer>();
	
	private void getSubclasses4Identifiers(Set<Integer> classes, int level, int max_size_subclasses){
		
		allSubClasses.addAll(classes);
		
		if (level<1 || classes.size()<1 || allSubClasses.size() > max_size_subclasses) //exit condition
			return;
		
		Set<Integer> subClasses = new HashSet<Integer>();
		
		for (int ide : classes){
			subClasses.addAll(getDirectSubClasses4Identifier(ide, false));
		}
		
		
		getSubclasses4Identifiers(subClasses, level-1, max_size_subclasses);
		
		
	}
	
	
	private void getSuperclasses4Identifiers(Set<Integer> classes, int level){
		
		allSuperClasses.addAll(classes);
		
		if (level<1 || classes.size()<1) //exit condition
			return;
		
		Set<Integer> superClasses = new HashSet<Integer>();
		
		for (int ide : classes){
			superClasses.addAll(getDirectSuperClasses4Identifier(ide, false));
		}
		
		
		getSuperclasses4Identifiers(superClasses, level-1);
		
		
	}
	
	
	/**
	 * We set the small projection, that is we create a set of identifiers to consider (module)
	 * Note that currently all identifiers for all ontologies are treated from same structure, that is
	 * we have only one index
	 * @param mapped_entities_identifiers
	 */
	public void setSmallProjection4MappedEntities(Set<Integer> mapped_entities_identifiers){
		
		//Set<Integer> entities = new HashSet<Integer>(mapped_entities_identifiers);
		identifiersInModule.clear();
		identifiersInModule.addAll(mapped_entities_identifiers);
		
		/*Set<Integer> superClasses = new HashSet<Integer>();
		
		for (int ide : mapped_entities_identifiers){
			superClasses.addAll(getDirectSuperClasses4Identifier(ide, false));
		}*/
		
		
		//We get all superclasses
		getSuperclasses4Identifiers(mapped_entities_identifiers, 500);//We want to extract everything
		
		identifiersInModule.addAll(allSuperClasses);
		
		LogOutput.print("Size projection: " + identifiersInModule.size());
		
	}
	
	

	
	
	private void duplicateDirectSubClasses(){
		
		ident2DirectSubClasses_integration = new HashMap<Integer, Set<Integer>>();
	
		for (int parent:  getDirectSubClasses(false).keySet()){
			
			if (!ident2DirectSubClasses_integration.containsKey(parent)){
				ident2DirectSubClasses_integration.put(parent, new HashSet<Integer>());
			}
		
			for (int kid : getDirectSubClasses(false).get(parent)){
				ident2DirectSubClasses_integration.get(parent).add(kid);
			}
			
		}
		
	}
	
	
	
	/**
	 * 
	 * Returns the adapted map iden2direct subclasses considering the given mappings.
	 * We want to consider an integrated hierarchy
	 */
	private void setAdaptedMap4DirectSubclasses(Map<Integer, Set<Integer>> exact_mappings){//we have only exact mappings and equiv!
		
		//ident2DirectSubClasses_integration = new HashMap<Integer, Set<Integer>>(getDirectSubClasses(false));
		//TODO we need to create a completely new object. Sets in map are not duplicated
		duplicateDirectSubClasses();
		
		
		representativesFromMappings = new HashSet<Integer>();
		
		LogOutput.print("Original entries DirectSubclasses: " + ident2DirectSubClasses_integration.size());
		LogOutput.print("Original entries DirectSubclasses: " + getDirectSubClasses(false).size());
		
		
		for (int ide_rep : exact_mappings.keySet()){
			for (int ide_equiv : exact_mappings.get(ide_rep)){
				
				if (ide_rep > ide_equiv){ //we only consider one of the sides (Note that there are only exact mappings for indexing)
					break;
				}
				
				
				/*if (!ident2DirectSubClasses_integration.containsKey(ide_equiv) || 
						ident2DirectSubClasses_integration.get(ide_equiv).size()<1){
					continue;//In case there is nothing to add
				}*/
				
				
				//Keep representatives
				representativesFromMappings.add(ide_rep);
				
				//LogOutput.print("Size before: " + ident2DirectSubClasses_integration.get(ide_rep).size());
				//LogOutput.print("Size equiv: " + ident2DirectSubClasses_integration.get(ide_equiv).size());
				
				
				//Deal with parents
				for (int ide_parent : getDirectSuperClasses4Identifier(ide_equiv, false)){
					if (ident2DirectSubClasses_integration.containsKey(ide_parent)){//just in case
						ident2DirectSubClasses_integration.get(ide_parent).add(ide_rep);
						ident2DirectSubClasses_integration.get(ide_parent).remove(ide_equiv);
					}
					
				}
				
				
				//Deal with kids
				if (ident2DirectSubClasses_integration.containsKey(ide_equiv)){
					
					if (!ident2DirectSubClasses_integration.containsKey(ide_rep)){
						ident2DirectSubClasses_integration.put(ide_rep, new HashSet<Integer>());
					}
					
					/*if (ident2DirectSubClasses_integration.get(ide_equiv).size()>0){
						LogOutput.print("Original entries DirectSubclasses new: " + ident2DirectSubClasses_integration.get(ide_rep).size());
						LogOutput.print("Original entries DirectSubclasses base: " + getDirectSubClasses(false).get(ide_rep).size());
					}*/
					
					//We add all direc subclasses of equivalent class to representative
					ident2DirectSubClasses_integration.get(ide_rep).addAll(ident2DirectSubClasses_integration.get(ide_equiv));
					
					/*if (ident2DirectSubClasses_integration.get(ide_equiv).size()>0){
						LogOutput.print("Adapted entries DirectSubclasses new: " + ident2DirectSubClasses_integration.get(ide_rep).size());
						LogOutput.print("Adapted entries DirectSubclasses base: " + getDirectSubClasses(false).get(ide_rep).size());
					}*/
					//We remove occurrence of equivalente (we only want to consider representatives)
					ident2DirectSubClasses_integration.get(ide_equiv).clear();
					ident2DirectSubClasses_integration.remove(ide_equiv);
				}
				
				//LogOutput.print("Size after: " + ident2DirectSubClasses_integration.get(ide_rep).size());
				
				
			}
		}
		
		LogOutput.print("Adapted entries DirectSubclasses: " + ident2DirectSubClasses_integration.size());
		LogOutput.print("Adapted entries DirectSubclasses: " + getDirectSubClasses(false).size());
		
		LogOutput.print("Representatives from Mappings: " + representativesFromMappings.size());
		LogOutput.print("Mapping entries (sub mappings): " + exact_mappings.keySet().size());
		
	}
	
	
	

	
	
	
	/**
	 * This method will set up the interval labelling index. To the end it will adapt the 
	 * ident2directkids structure in order to take into account equivalence mappings
	 *  
	 */
	public void setIntervalLabellingIndex(Map<Integer, Set<Integer>> exact_mappings) {
		
		
		//TODO we need to create new identifier2directkids with both ontologies + anchors + representatives
		setAdaptedMap4DirectSubclasses(exact_mappings);
		
		
		//Create interval labelling
		//Fixed bug about cycles detected by Alessandro Solimando (August 2014)
		IntervalLabelledHierarchy interval_schema = 
			new PreIntervalLabelledHierarchy(
					dealWithCyclesIntervalLabellingSchema(true)); 
		
		//Old version
		//IntervalLabelledHierarchy interval_schema = 
		//		new PreIntervalLabelledHierarchy(
		//				getIdent2DirectSubClasses_Integration());
		//
		
		
		
		//Structures
		for (int ident : interval_schema.getClassesToNodesMap().keySet()){ 
			
			identifier2ClassIndex.get(ident).setNode(interval_schema.getClassesToNodesMap().get(ident));
			
			//Uncomment for unsatisfiability tests (see disjoint intervals)
			//preOrderDesc2Identifier.put(interval_schema.getClassesToNodesMap().get(ident).getDescOrder(), ident);
			
			//Not used...
			//preOrderAnc2Identifier.put(interval_schema.getClassesToNodesMap().get(ident).getAscOrder(), ident);
		}
		
		
		
		//Propagate preoreder (anc and desc) to equivalences equivalences
		//PROPAGATION of labels for EQUIVALENCES
		for (int iRep : getRepresentativeNodes()){
			if (identifier2ClassIndex.get(iRep).hasEquivalentClasses()){			
				for (int iEquiv : identifier2ClassIndex.get(iRep).getEquivalentClasses()){
					identifier2ClassIndex.get(iEquiv).setNode(identifier2ClassIndex.get(iRep).getNode());
				}
			}
		}
		
		//Propagation to equivalent entities from mappings
		for (int iRep : getRepresentativesFromMappings()){
			
			if (exact_mappings.containsKey(iRep)){//Just in case...
				for (int iEquiv : exact_mappings.get(iRep)){
					identifier2ClassIndex.get(iEquiv).setNode(identifier2ClassIndex.get(iRep).getNode());
					
					//LogOutput.print("REP: " + identifier2ClassIndex.get(iEquiv).getNode().getDescIntervals().toString());
					//LogOutput.print("\tEQUIV: " + identifier2ClassIndex.get(iEquiv).getNode().getDescIntervals().toString());
					
				}
			}
			
		}
		
		
		//Create disjoint intervals
		//setDisjointIntervals();
		createDisjointIntervalsStructure();
		
		//Create independent roots
		//createIdependentRootsStructure();
		
		//Fast check
		checkBasicSatisfiability();

	}
	
	
	
	/**
	 * Fixed a source of StackOverflow when computing the indexes: added "LightTarjan" class for identifying cycles. 
	 * It also encodes equivalences detected by the light tarjan
	 * (details about tarjan: http://en.wikipedia.org/wiki/Tarjan%27s_strongly_connected_components_algorithm) 
	 * @param fixCycles
	 * @author Alessandro solimando
	 * @return
	 */
	private HashMap<Integer, Set<Integer>> dealWithCyclesIntervalLabellingSchema(boolean fixCycles){
				
		HashMap<Integer, Set<Integer>> ontoHierarchy = null;
		Set<Set<Integer>> nonTrivialSCCs = new HashSet<>();
				
		
		if (fixCycles){
			ontoHierarchy = getIdent2DirectSubClasses_Integration();
			Map<Integer,Set<Integer>> sccs = 
					new LightTarjan().executeTarjan(ontoHierarchy);
	
			for (Set<Integer> scc : sccs.values()) {
				if(scc.size() > 1){
					nonTrivialSCCs.add(scc);
					
	//				System.out.println("SCC: " + scc);
					// choose a representing node
					Integer represId = scc.iterator().next();
					Set<Integer> sccMinusRepr = new HashSet<>(scc);
					sccMinusRepr.remove(represId);
					
	//				System.out.println("ReprNode: " + represId);
	//				System.out.println("Repr: " + ontoHierarchy.get(represId));
	
					// OUT-ARCS: preserve nodes different from repr and reachable from scc
					for (Integer id : sccMinusRepr){
						ontoHierarchy.get(id).removeAll(scc);
						ontoHierarchy.get(represId).addAll(ontoHierarchy.get(id));
	//					System.out.println(id + ": " + ontoHierarchy.get(id));
					}
	
					// the other nodes must disappear
					ontoHierarchy.get(represId).removeAll(sccMinusRepr);
	
	//				System.out.println("Repr: " + ontoHierarchy.get(represId));
					
					// IN-ARCS: for each node not in the scc remove the other  
					// nodes from the children and add repr
					for (Entry<Integer, Set<Integer>> e : ontoHierarchy.entrySet()) {
						if(!scc.contains(e.getKey())){
							int pre = e.getValue().size();
							e.getValue().removeAll(sccMinusRepr);
							
							if(e.getValue().size() != pre){
								e.getValue().add(represId);
	//							System.out.println(e.getKey() + " removed " + 
	//									(pre - e.getValue().size()));
	//							System.out.println(e.getKey() + " " + e.getValue());
							}
						}
					}					
				}
			}
			if(!nonTrivialSCCs.isEmpty()){
				LogOutput.printAlways("Fixed " + nonTrivialSCCs.size() + " cycle(s)");
				// check if no more cycles exists
				sccs = new LightTarjan().executeTarjan(ontoHierarchy);
				int unsolved = 0;
				for (Set<Integer> scc : sccs.values())
					if(scc.size() > 1){
						++unsolved;
						for (Integer i : scc)
							System.out.println(i + ": " + ontoHierarchy.get(i));
					}
				if(unsolved > 0)
					throw new Error(unsolved + " unsolved SCCs");
			}
			
			
			// set as equivalent all the elements in the nontrivial SCCs
			for (Set<Integer> scc : nonTrivialSCCs) {
				for (Integer i : scc) {
					Set<Integer> sccMinusSelf = new HashSet<>(scc);
					sccMinusSelf.remove(i);
					if(identifier2ClassIndex.get(i).getEquivalentClasses() == null)
						identifier2ClassIndex.get(i).setEquivalentClasses(new HashSet<>(sccMinusSelf));
					else
						identifier2ClassIndex.get(i).getEquivalentClasses().addAll(sccMinusSelf);
				}
			}
		}
		
		
		if (ontoHierarchy == null) 
			return getIdent2DirectSubClasses_Integration();
		else
			return ontoHierarchy;
		
		
		
		
	}
	

	
	
	
	
	
	/**
	 * Creates disjoint intervals structure.
	 * Note that adjacent intervals are merged
	 */
	private void createDisjointIntervalsStructure(){
		
		//ORIGINAL no MERGED
		/*init = Calendar.getInstance().getTimeInMillis();
		for (int icls : Class2DisjointClasses.keySet()){
			for (Interval cls_interval : Identifier2DescIntervals.get(icls)){
				interval2disjointness.put(cls_interval, new ArrayList<Interval>());
			
				for (int disjcls : Class2DisjointClasses.get(icls)){
					for (Interval disjcls_interval : Identifier2DescIntervals.get(disjcls)){
						interval2disjointness.get(cls_interval).add(disjcls_interval);
					}
				}
			}
		}
		
		LogOutput.print(interval2disjointness);
		fin = Calendar.getInstance().getTimeInMillis();
		LogOutput.print("Time Getting Disjoint Intervals (s): " + (float)((double)fin-(double)init)/1000.0);
		*/
		
		
		long init1, fin1;
		init1 = Calendar.getInstance().getTimeInMillis();
		List<Interval> list_intervals = new ArrayList<Interval>();
		Interval[] array_intervals;
		Interval current_interval; 
		
		interval2disjointIntervals.clear();
		
		
		int wrong_preorder=0;
		int wrong_desc_intervals=0;
		
		for (int icls : identifier2ClassIndex.keySet()){
			
			//Check for classes with preorder -1
			if (identifier2ClassIndex.get(icls).getNode().getDescOrder()<0){
				//LogOutput.print("Class with wrong preorder: " + icls + " - " + getName4ConceptIndex(icls) +  "  "  + identifier2ClassIndex.get(icls).getNode().getDescOrder());
				//LogOutput.print("\t Intervals: " + identifier2ClassIndex.get(icls).getNode().getDescIntervals());
				wrong_preorder++;
				continue; //Do not add to disjoint intervals
			}
			
			//Check if a class with good preorder has a wrong descendant interval
			for (Interval cls_interval : identifier2ClassIndex.get(icls).getNode().getDescIntervals()){
				if (cls_interval.getLeftBound()<0 || cls_interval.getRightBound()<0){
					//LogOutput.print("Class with wrong interval descendant: " + icls + " - " + getName4ConceptIndex(icls));
					//LogOutput.print("\t Intervals: " + identifier2ClassIndex.get(icls).getNode().getDescIntervals());
					wrong_desc_intervals++;
				}
			}
			
			
		
			//August 2014
			//Bug detected and corrected thanks to Alessandro Solimando
			//there was a bug that was overwriting disjointness intervals for children classes when propagating that of the parent class. 
			//Now it correctly merges the two set of intervals.
			if (identifier2ClassIndex.get(icls).hasDirectDisjointClasses()){

				
				//We create list of disjoint intervals
				for (int disjcls : identifier2ClassIndex.get(icls).getDisjointClasses()){
					for (Interval disjcls_interval : identifier2ClassIndex.get(disjcls).getNode().getDescIntervals()){

						//Only correct intervals
						//Do not add negative intervals <-1,-1>  or <-id, -id>
						if (disjcls_interval.getLeftBound()>=0 && disjcls_interval.getRightBound()>=0){
							list_intervals.add(disjcls_interval);
						}
				
					}
				}

				if (list_intervals.size()>=3){

					//First Sort
					array_intervals = new Interval[list_intervals.size()];
					array_intervals = list_intervals.toArray(array_intervals);

					_Quicksort(array_intervals, 0, array_intervals.length-1);


					//Merge

					list_intervals.clear(); //we already have our sorted array

					current_interval=array_intervals[0];

					for (int i=1; i< array_intervals.length; i++){

						if (current_interval.isAdjacentTo(array_intervals[i])){
							current_interval = current_interval.getUnionWith(array_intervals[i]);
						}
						else {
							list_intervals.add(current_interval);
							current_interval=array_intervals[i];
						}														
					}
					list_intervals.add(current_interval);

					for (Interval cls_interval :  identifier2ClassIndex.get(icls).getNode().getDescIntervals()){
						List<Interval> tmpList = new LinkedList<>(list_intervals);

						if(interval2disjointIntervals.containsKey(cls_interval)){
							tmpList.addAll(interval2disjointIntervals.get(cls_interval));
							tmpList = mergeIntervals(tmpList);
							//continue;
						}							
						interval2disjointIntervals.put(cls_interval, 
								new HashSet<Interval>(tmpList));
					}
				}

				else if (list_intervals.size()==2){
					List<Interval> originalList = list_intervals.get(0).getUnionWithList(list_intervals.get(1));
					for (Interval cls_interval : identifier2ClassIndex.get(icls).getNode().getDescIntervals()){
						List<Interval> tmpList = new LinkedList<>(originalList);
						if(interval2disjointIntervals.containsKey(cls_interval)){
							tmpList.addAll(interval2disjointIntervals.get(cls_interval));
							tmpList = mergeIntervals(tmpList);
							//continue;
						}							
						interval2disjointIntervals.put(cls_interval, 
								new HashSet<Interval>(tmpList));
					}
				}

				else if (list_intervals.size()==1){ //Only one
					for (Interval cls_interval : identifier2ClassIndex.get(icls).getNode().getDescIntervals()){

						List<Interval> originalList = new LinkedList<>(list_intervals);

						if(interval2disjointIntervals.containsKey(cls_interval)){
							originalList.addAll(interval2disjointIntervals.get(cls_interval));
							originalList = mergeIntervals(originalList);
							//continue;
						}							
						interval2disjointIntervals.put(cls_interval, 
								new HashSet<Interval>(originalList));
					}
				}
				//else if list_intervals.size()==o do nothing


				//Empty structure
				list_intervals.clear();
			}
			
		}
		
		LogOutput.print("Classes with wrong/negative preorder (-1 or -d): " + wrong_preorder);
		LogOutput.print("Classes with wrong/negative descendants intervals (<-1,-1> or <-id,-id>): " + wrong_desc_intervals);
		
		//LogOutput.print(interval2disjointIntervals.toString());
		
		
		fin1 = Calendar.getInstance().getTimeInMillis();
		LogOutput.print("Time Getting Disjoint Intervals (merged) (s): " + (float)((double)fin1-(double)init1)/1000.0);
		
	}
	
	/**
	 * 
	 * @param list_intervals
	 * @author Alessandro Solimando
	 * @return
	 */
	public List<Interval> mergeIntervals(List<Interval> list_intervals){
		//		List<Interval> mergedList = new ArrayList<>();
		//		for (Interval i : list_intervals)
		//			mergedList.add(new Interval(i.getLeftBound(),i.getRightBound()));

		if (list_intervals.size()>=3){			
			//First Sort
			Interval [] array_intervals = new Interval[list_intervals.size()];
			array_intervals = list_intervals.toArray(array_intervals);

			_Quicksort(array_intervals, 0, array_intervals.length-1);

			//Merge			
			list_intervals.clear(); //we already have our sorted array

			Interval current_interval=array_intervals[0];

			for (int i=1; i< array_intervals.length; i++){

				if (current_interval.isAdjacentTo(array_intervals[i])){
					current_interval = current_interval.getUnionWith(array_intervals[i]);
				}
				else {
					list_intervals.add(current_interval);
					current_interval=array_intervals[i];
				}														
			}
			list_intervals.add(current_interval);
		}

		else if (list_intervals.size()==2){
			list_intervals = 
					list_intervals.get(0).getUnionWithList(
							list_intervals.get(1));
		}

		//else if list_intervals.size()<=1 do nothing
		return list_intervals;
	}
	
	
	/**
	 * Checks basic unsatisfiability
	 */
	private void checkBasicSatisfiability(){
		
		boolean sat=true;
		unsatisfiableClassesILS.clear();
		
		for (Interval interv1 : interval2disjointIntervals.keySet()){
			
			for (Interval disj_interv : interval2disjointIntervals.get(interv1)){
				
				if (interv1.hasNonEmptyIntersectionWith(disj_interv)){
					
					LogOutput.print(
							"Classes in '" + 
							interv1.getIntersectionWith(disj_interv) + 
							"' are unsatisfiable");
					LogOutput.print("Involved intervals: " + interv1 + "  "  + disj_interv);// + "  " + interval2disjointIntervals.get(interv1));
					
					
					for (int pre = interv1.getIntersectionWith(disj_interv).getLeftBound(); 
							pre<=interv1.getIntersectionWith(disj_interv).getRightBound(); 
							pre++){
						
						if (getIdentifier4PreorderDesc(pre)>0){
							unsatisfiableClassesILS.add(getIdentifier4PreorderDesc(pre));
						}
						else{
							LogOutput.print("\tPreorder has not identifier");
						}
						
					}
					
					LogOutput.print("\t" + interv1 + "   "  + disj_interv);

					sat=false;
						
					
				}
			}	
		}
		
		if (sat){
			LogOutput.print("There are non unsatisfiable clases (non-empty intersection of disjoint intervals))");
		}
			
		
	}
	
	
	
	
	
	/**
	 * Given an identifier gets its disjoint intervals
	 * 
	 * @param cIdent
	 * @return
	 */
	public List<Interval> getDisjointIntervals4Identifier(int cIdent){
		
		List<Interval> disj_intervals=new ArrayList<Interval>();
		
		//it will only be necessary to compare one of the intervals for each entity
		int preorder = getPreOrderNumber(cIdent);
		
		
		//It may appear in several entries
		for (Interval disj_int1 : interval2disjointIntervals.keySet()){
			
			if (disj_int1.containsIndex(preorder)){
				
				for (Interval disj_int2 : interval2disjointIntervals.get(disj_int1)){
					
					disj_intervals.add(disj_int2);
					
				}	
			}
		}
		return disj_intervals; 
	}
	
	
	
	
	
	
	
	
	//private Interval sorted_intervals[];	
	private void _Quicksort(Interval matrix[], int a, int b)
	{
		//sorted_intervals = new Interval[matrix.length];
		Interval buf;
		int from = a;
		int to = b;
		Interval pivot = matrix[(from+to)/2];
		do {
			
			while(from <= b && matrix[from].hasLowerLeftBoundThan(pivot)){
				from++;
			}
			while(to >= a && matrix[to].hasGreaterLeftBoundThan(pivot)){
				to--;
			}
			if(from <= to){
				buf = matrix[from];
				matrix[from] = matrix[to];
				matrix[to] = buf;
				from++; to--;
			}
		}while(from <= to);
		
		if(a < to) {
			_Quicksort(matrix, a, to);
		}
		if(from < b){
			_Quicksort(matrix, from, b);
		}
		
		//sorted_intervals = matrix;
		
	}
	
		
		
	
}

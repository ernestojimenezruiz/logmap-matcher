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
package uk.ac.ox.krr.logmap2.repair.hornSAT;

import java.util.*;



import uk.ac.ox.krr.logmap2.io.LogOutput;





/**
 * 
 * This class implements the Dowling and Gallier algorithm (1984) to check the 
 * satisfiability of Propositional Horn formulae
 * 
 * @author ernesto
 * 
 *
 */
public class DowlingGallierHornSAT {
	
	//Keep only disjointness and mappings?? aasasa
	private Map<Integer, HornClause> clauses = new HashMap<Integer, HornClause>();
	
	private Set<HornClause> conflictive_disjointness  = new HashSet<HornClause>(); ///temporal...
	private Set<HornClause> conflictive_mappings = new HashSet<HornClause>();
	private Set<HornClause> mappings_involved_in_error = new HashSet<HornClause>();
	private Set<HornClause> disjointness_involved_in_error = new HashSet<HornClause>();
	
	//Propositions to 'parents'(targets) 
	private Map<Integer, Set<Link>> FS = new HashMap<Integer, Set<Link>>();
	//Links to ignore
	private Map<Integer, Set<Link>> additionalIgnoreFS = new HashMap<Integer, Set<Link>>();
	private Map<Integer, Set<Link>> generalIgnoreFS = new HashMap<Integer, Set<Link>>();
	
	private Vector<Integer> N = new Vector<Integer>();
	private Vector<Integer> N_copy;// = new Vector<Integer>();
	
	private Stack<Integer> Q = new Stack<Integer>();
	
	//Not necessary (optional)
	private Set<Integer> R = new HashSet<Integer>();
	
	
	
	//Ontology concepts are indexed from '0' onwards
	private int TRUE = -1; //Node true
	private int FALSE = -2; //Node false
	
	private int clause_num=0;
	
	private int last_onto1_id;	
	
	
	//private IndexManager index;
	
	
	/**
	 * General Test constructor
	 * @param fromTrue
	 * @param taxonomyReversed
	 * @param disjointness
	 */
	public DowlingGallierHornSAT(
			Set<Integer> fromTrue,
			Map<Integer, Set<Integer>> taxonomyReversed,
			Map<Integer, Set<Integer>> disjointness){		
		
		
		clause_num=0;
		FS.put(TRUE, new HashSet<Link>());
		
		addTrueClauses(fromTrue, 0);
		
		addTaxClauses(taxonomyReversed, 0);
		
		addDisjClauses(disjointness, 0);
		
		
		Q.addAll(fromTrue);
		R.addAll(fromTrue);
		
		LogOutput.print(FS.toString());
		LogOutput.print(N.toString());
		LogOutput.print(Q.toString());
		
		//Pi1 ^ Pi2 -> Pj (Not considered yet)
		
	}
	
	
	
	
	

	
	/**
	 * 
	 * Sets up info for Ontologies. This method contains fixed mappings
	 * @param taxonomyReversed1 class 2 set of parents onto1
	 * @param taxonomyReversed2 class 2 set of parents onto2
	 * @param disjointness1 class 2 set of disjoint classes onto1
	 * @param disjointness2 class 2 set of disjoint classes onto2
	 * @param fixedmappings fixed mappings already repaired
	 * @param mappings set of mappings 1:n
	 * @param mappings2remove Already known as conflictive mappings
	 * @deprecated
	 */
	public DowlingGallierHornSAT(
			//IndexManager index, 
			Map<Integer, Set<Integer>> taxonomyReversed1,
			Map<Integer, Set<Integer>> taxonomyReversed2,
			Map<Integer, Set<Integer>> disjointness1,
			Map<Integer, Set<Integer>> disjointness2,
			Map<Set<Integer>, Integer> generalHornAxioms1,
			Map<Set<Integer>, Integer> generalHornAxioms2,
			Map<Integer, Set<Integer>> fixedmappings,
			Map<Integer, Set<Integer>> mappings,
			Set<HornClause> mappings2ignore){		
		
		
		//Currently used to know to which ontology belongs and identifier (the order)
		//this.index=index;
		
		
		clause_num = 0;
		
		
		//*FS.put(TRUE, new HashSet<Link>());
		//addTrueClauses(fromTrue1, 0);
		//addTrueClauses(fromTrue2, shift);
		
	
		addTaxClauses(taxonomyReversed1, HornClause.ONTO);
		addTaxClauses(taxonomyReversed2, HornClause.ONTO);
		
		
		addDisjClauses(disjointness1, HornClause.ONTO);
		addDisjClauses(disjointness2, HornClause.ONTO);
		
		
		//Pi1 ^ Pi2 -> Pj (Not considered yet)
		addGeneralHornAxioms(generalHornAxioms1, HornClause.ONTO);
		addGeneralHornAxioms(generalHornAxioms2, HornClause.ONTO);
		
		//Mappings to review
		addMappingClauses1N(mappings,mappings2ignore, false);
		
		//Fixed mappings
		addMappingClauses1N(fixedmappings,mappings2ignore, true);
		
		
		N.add(0);//We add the space for T->P
		
				
		
		
	}
	
	
	
	/**
	 * /**
	 * Sets up info for Ontologies. This method contains fixed mappings
	 * @param taxonomy ide 2 set of ""children""
	 * @param disjointness class 2 set of disjoint classes onto1
	 * @param generalHornAxioms axioms p^q->r
	 * @param indivClassTypes individuals are treated as prop variables and types as superclasses 
	 * @param fixedmappings fixed mappings already repaired
	 * @param mappings set of mappings 1:n
	 * @param mappings2remove Already known as conflictive mappings
	 */
	public DowlingGallierHornSAT(
			Map<Integer, Set<Integer>> taxonomy,
			Map<Integer, Set<Integer>> equivalences,
			Map<Integer, Set<Integer>> disjointness,
			Map<Set<Integer>, Integer> generalHornAxioms,
			boolean addClassTypes,
			Map<Integer, Set<Integer>> indivClassTypes,
			Map<Integer, Set<Integer>> fixedmappings,
			Map<Integer, Set<Integer>> mappings,
			Set<HornClause> mappings2ignore){		
		
		
		//Currently used to know to which ontology belongs and identifier (the order)
		//this.index=index;
		
		
		clause_num = 0;
		
		
		//*FS.put(TRUE, new HashSet<Link>());
		//addTrueClauses(fromTrue1, 0);
		//addTrueClauses(fromTrue2, shift);
		
	
		addTaxClauses(taxonomy, HornClause.ONTO);
		
		if (addClassTypes){
			addIndivClassTypeClauses(indivClassTypes, HornClause.ONTO);
		}
		
			
		addEquivalentClauses(equivalences, HornClause.ONTO);
	
		addDisjClauses(disjointness, HornClause.ONTO);
		
		
		//Pi1 ^ Pi2 -> Pj (Not considered yet)
		addGeneralHornAxioms(generalHornAxioms, HornClause.ONTO);
		
		
		//Mappings to review
		addMappingClauses1N(mappings,mappings2ignore, false);
		
		//Fixed mappings
		addMappingClauses1N(fixedmappings,mappings2ignore, true);
		
		
		N.add(0);//We add the space for T->P
		
				
		
		
	}
	
	
	
	
	
	
	/**
	 * For general constructor
	 * @return
	 */
	public boolean isSatisfiable(){
		
		/*Set<HornClause> error_clauses = VisitGraph();
		if (error_clauses.size()>0){
			LogOutput.print("These clauses are the cause of error: ");
			LogOutput.print(error_clauses);
			return false;
		}
		return true;*/
		
		boolean satisfiable=VisitGraph();
		
		if (!satisfiable){
			LogOutput.print("These clauses are the cause of error: ");
			LogOutput.print(mappings_involved_in_error.toString());
			LogOutput.print(disjointness_involved_in_error.toString());
		}
			
		return satisfiable;
		
		
	}
	
	
	
	public boolean isSatisfiable(int entity){
		return isSatisfiable(entity, Collections.EMPTY_MAP);
	}
	
	
	/**
	 * Check is the entity is unsatisfiable
	 * @param entity
	 * @param onto
	 * @param ignoreLinks We use to this structure to ignore mappings in the checking. 
	 * This will be useful to find repair plans
	 * @return
	 */
	public boolean isSatisfiable(int entity, Map<Integer, Set<Link>> ignoreLinks){
	
		
		
		

		//Cannot be here!
		//conflictive_mappings.clear();
		
		mappings_involved_in_error.clear();
		disjointness_involved_in_error.clear();
		
		//Reinit
		N_copy = new Vector<Integer>(N);
		Q.clear();
		R.clear();		
		R.add(entity);
		Q.add(entity);
		
		Link link = new Link(clause_num, entity);
		
		FS.put(TRUE, new HashSet<Link>()); //We remove ir each time		
		FS.get(TRUE).add(link);
		
		additionalIgnoreFS=ignoreLinks;
		//LogOutput.print(ignoreFS);
		
		//LogOutput.print(N.size() + "  " + clause_num);
		
		//N.add(0); //In mconstructor
		//clause_num++; //Not necessary?
		
		boolean satisfiable=VisitGraph();
		
		/*if (!satisfiable){
			LogOutput.print("UNSAT Entity : " + entity + ", from Onto: " + onto);
			LogOutput.print(conflictive_mappings);
			LogOutput.print(disjoint_clauses);
		}*/
			
		return satisfiable;
				
	}
	

	
	
	
	
	
	
	/**
	 * We add general mappings/links to ignore. Mappings which are known that are conflictive
	 * @param cls
	 * @param link
	 */
	public void addGeneralLink2Ignore(int origin, int label, int target){
		if (!generalIgnoreFS.containsKey(origin))
			generalIgnoreFS.put(origin, new HashSet<Link>());
		
		generalIgnoreFS.get(origin).add(new Link(label, target));
		
	}
	
	public Map<Integer, Set<Link>> getGeneralLink2Ignore(){
		return generalIgnoreFS;
	}
	
	
	
	
	
	/**
	 * D&G returns the set of mappings that in the paths to get an unsatisfiability error, but some
	 * of them may not be involved in the conflict (not sure about that...)
	 * @return
	 */
	public Set<HornClause> getMappingsInvolvedInError(){
		return mappings_involved_in_error;		
	}
	
	
	public void setConflictiveMappingsAsInvolvedMappings(){
		conflictive_mappings.clear();
		conflictive_mappings.addAll(mappings_involved_in_error);
	}
	
	public void incrementConflictiveMappingsWithNewInvolvedMappings(){		
		conflictive_mappings.addAll(mappings_involved_in_error);
	}
	
	
	
	public void setConflictiveDisjointnessAsInvolvedDisjointness(){
		conflictive_disjointness.clear();
		conflictive_disjointness.addAll(disjointness_involved_in_error);
	}
	
	
	/**
	 * Returns the real set of mappings involved in the conflict
	 * @return
	 */
	public Set<HornClause> getConflictiveMappings(){
		return conflictive_mappings;
	}
	
	public Set<HornClause> getConflictiveDisjointness(){
		return conflictive_disjointness;
	}
	
	public void addConflictiveMapping(HornClause mapping){
		conflictive_mappings.add(mapping);
	}
	
	public void clearConflictiveMappings(){
		conflictive_mappings.clear();
	}
	
	public Set<HornClause> getDisjointnessInvolvedInError(){
		return disjointness_involved_in_error;		
	}
	
	
	
	
	
	
	//Only for test constructor
	//True clauses ar only added to check the satisfiability of an entity
	private void addTrueClauses(Set<Integer> fromTrue, int shift){
		
		//P -> True
		for (int init : fromTrue){
			
			FS.get(TRUE).add(new Link(clause_num, init+shift));
			N.add(0);
			clause_num++;
			
		}
	}
	
	
	private void addTaxClauses(Map<Integer, Set<Integer>> taxonomy, int origin){
		
		//child -> parent
		for (int parent : taxonomy.keySet()){
			
			for (int child : taxonomy.get(parent)){				
				
				clauses.put(clause_num, new HornClause(child, parent, clause_num, origin, HornClause.L2R));
				
				if (!FS.containsKey(child)){
					FS.put(child, new HashSet<Link>());
				}
				
				FS.get(child).add(new Link(clause_num, parent));  //A link for each parent
				N.add(1); //Archs with this clause number (clause_num)
				clause_num++;
				
			}
		}
		
		LogOutput.print("TAX clause num: " + clause_num);
		
	}
	
	private void addIndivClassTypeClauses(Map<Integer, Set<Integer>> classTypes, int origin){
		
		//indiv -> types
		for (int indiv_child : classTypes.keySet()){
			
			for (int cls_parent : classTypes.get(indiv_child)){				
				
				clauses.put(clause_num, new HornClause(indiv_child, cls_parent, clause_num, origin, HornClause.L2R));
				
				if (!FS.containsKey(indiv_child)){
					FS.put(indiv_child, new HashSet<Link>());
				}
				
				FS.get(indiv_child).add(new Link(clause_num, cls_parent));  //A link for each parent
				N.add(1); //Archs with this clause number (clause_num)
				clause_num++;
				
			}
		}
		
		LogOutput.print("INDIV CLASS TYPES clause num: " + clause_num);
		
	}
	
	
	private void addEquivalentClauses(Map<Integer, Set<Integer>> equivalents, int origin_clause){
		
		//child -> parent
		for (int origin : equivalents.keySet()){
			
			for (int target : equivalents.get(origin)){				
				
				clauses.put(clause_num, new HornClause(origin, target, clause_num, origin_clause, HornClause.L2R));
				
				if (!FS.containsKey(origin)){
					FS.put(origin, new HashSet<Link>());
				}
				
				FS.get(origin).add(new Link(clause_num, target));  //A link for each parent
				N.add(1); //Archs with this clause number (clause_num)
				clause_num++;
				
			}
		}
		
		LogOutput.print("Equiv clause num: " + clause_num);
		
		
		
	}
	
	
	/**
	 * 
	 * @param disjointness
	 * @param origin
	 */
	private void addDisjClauses(Map<Integer, Set<Integer>> disjointness, int origin){
		
		//TODO Can we have dijointness involving entities from two ontologies??
		
		//To avoid the addition of duplicate clauses: P^Q -> False  and  Q^P -> False
		Set<Pair> disj_clauses = new HashSet<Pair>();
		Pair pair1;
		//Pair pair2;
		
		
		//Pi ^ Pj -> False
		for (int ent : disjointness.keySet()){
			
			if (!FS.containsKey(ent)){
				FS.put(ent, new HashSet<Link>());
			}
			
			for (int disj : disjointness.get(ent)){
				
				pair1 = new Pair(ent, disj);
				
				
				if (disj_clauses.contains(pair1))
					continue;
				
				disj_clauses.add(pair1);
				//pair2 = new Pair(disj+shift, ent+shift);
				//disj_clauses.add(pair2);
				
				
				clauses.put(clause_num, new HornClause(ent, disj, FALSE, clause_num, origin, HornClause.L2R));
				
				FS.get(ent).add(new Link(clause_num, FALSE)); //A link to FALSE for each clause but different labels 
				
				if (!FS.containsKey(disj)){
					FS.put(disj, new HashSet<Link>());
				}
				FS.get(disj).add(new Link(clause_num, FALSE));
				
				N.add(2);  //Only 2 in the disjointness
				clause_num++;
				
				
				
			}
		}
		
		//LogOutput.print("Disj clauses: " + disj_clauses.size());
		//LogOutput.print("Disj clauses: " + disj_clauses);
		disj_clauses.clear();
		
		LogOutput.print("D&G DISJ clause num: " + clause_num);
	}

	
	
	private void addGeneralHornAxioms(Map<Set<Integer>, Integer> generalHornAxioms, int origin){
		
		int head;
		
		for (Set<Integer> body : generalHornAxioms.keySet()){
			
			head = generalHornAxioms.get(body);
			
			for (Integer ent : body){
			
				if (!FS.containsKey(ent)){
					FS.put(ent, new HashSet<Link>());
				}
				
				//must accept a set!!
				clauses.put(clause_num, new HornClause(body, head, clause_num, origin, HornClause.L2R));
				
				FS.get(ent).add(new Link(clause_num, head)); 
				//A link to head for each entity in body (same labels: clasue_num) 
												
				
			}
			
			N.add(body.size());
			clause_num++;
			
			
		}
		
		LogOutput.print("D&G general axioms clause num: " + clause_num);
		
	}
	
	
	
	
	private void addMappingClauses1N(
			Map<Integer, Set<Integer>> mappings,			
			Set<HornClause> mappings2ignore,
			boolean fixedMap){
		
		int type_mapping;
		int clause_type;
		
		if (fixedMap) {
			type_mapping=HornClause.FIXEDMAP;
		}
		else {
			type_mapping=HornClause.MAP;
		}
		
		//type_mapping=HornClause.MAP;
		
		
		HornClause clause;
		
		
		int num=0;
		
		for (int origin : mappings.keySet()){ //From onto1 or onto2
		
			
			for (int target : mappings.get(origin)){//To onto1 or 2
				
				//To know the order of the ontologies of the involved entities
				//if (index.getClassIndex(origin).getOntologyId()<index.getClassIndex(target).getOntologyId()){
				//TODO This may be changed....
				//TODO Yujiao - this is strange here
				if (origin<target){ //smaller indexes are in smaller index ontologies (this is just to keep and order)
					clause_type=HornClause.L2R;
					//Clause
					clause =  new HornClause(origin, target, clause_num, type_mapping, clause_type);//
				}
				else{
					clause_type=HornClause.R2L;
					//Clause
					clause =  new HornClause(target, origin, clause_num, type_mapping, clause_type);//
				}
				
				
				
				
				if (!mappings2ignore.contains(clause)){
					
					num++;
				
					if (!FS.containsKey(origin)){
						FS.put(origin, new HashSet<Link>());
					}
				
									
					clauses.put(clause_num, clause);
					//clauses.get(clause_num).setLabelPair(clause_num+1); //nOT USED ANYMORE
					
					FS.get(origin).add(new Link(clause_num, target));
					N.add(1); 
					clause_num++;
				}
				
				
				
			}//end for ent2
		}//ent for ent1
		
		LogOutput.print("D&G mappings clause num: " + clause_num);
		LogOutput.print("D&G mapping links: " + num + "  " + mappings2ignore.size());
	
	}
	
	
	
	
	
	
	
	/**
	 * 
	 * @return if the graph is satisfiable or not
	 */
	private boolean VisitGraph(){
		
		//private Set<Integer> Q2 = new HashSet<Integer>();		
		int P;
		
		boolean satisfiable=true;		
		
		while (!Q.empty()){
			
			P = Q.pop();
						
			if (FS.containsKey(P)){
				
				for (Link link : FS.get(P)){
					
					
					//LogOutput.print("Visited link: " + link);
					
					if (generalIgnoreFS.containsKey(P)){
						if (generalIgnoreFS.get(P).contains(link)){
							//LogOutput.print("Ignored: " + link);
							continue;
						}
					}
					
					//We ignore given link, and we do not visit it
					if (additionalIgnoreFS.containsKey(P)){
						if (additionalIgnoreFS.get(P).contains(link)){
							//LogOutput.print("Ignored: " + link);
							continue;
						}
					}
					
					
					//TODO: Analyze for LogMap2 (*)
					//MUST BE COMMENTED
					//Only mappings. Mapping in conflict!
					//Too many mappings per conflict?? 
					//if (clauses.get(link.getLabelLink()).getOrigin()==HornClause.MAP){
					//	mappings_involved_in_error.add(clauses.get(link.getLabelLink()));						
					//}
					
					
					
					//Replaces element 
					N_copy.set(link.getLabelLink(), N_copy.get(link.getLabelLink())-1);
					
					if (N_copy.get(link.getLabelLink())==0){  //We get false from all paths possibles
					
						
						if (link.getTargetLink()==FALSE){
							disjointness_involved_in_error.add(clauses.get(link.getLabelLink()));
							satisfiable=false;
							//return visitied_clauses;	do not return!!
						}
						else if (!R.contains(link.getTargetLink())){  
						//if (!R.contains(link.getTargetLink())){
							Q.push(link.getTargetLink());
							R.add(link.getTargetLink());
							
							//Only mappings
							//TODO put it back if necessaru (*)
							if (clauses.get(link.getLabelLink()).getOrigin()==HornClause.MAP){
								mappings_involved_in_error.add(clauses.get(link.getLabelLink()));
								
								//NOT USED, MAPPING CLAUSES ARE INSERTED INDEPENDENTLY....
								//We add both direction of the mappings if it has two		
								//Maybe it is not necessary anymore
								/*if (clauses.get(link.getLabelLink()).getLabelPair()>-1){
									mappings_involved_in_error.add(
											clauses.get(
													clauses.get(link.getLabelLink()).getLabelPair())); //They are inserted in order
								}*/	
							}
							
						}
						
					}	
				}	
			}
			//else
			//	LogOutput.print("lala  " + P);
		}
		
		//if (satisfiable)		
		//	return Collections.emptySet();
		
		return satisfiable;
		
		
	}
	
	
	
	public static void main(String[] args) {
		
		Set<Integer> fromTrue1 = new HashSet<Integer>();
		Map<Integer, Set<Integer>> tax1 = new HashMap<Integer,Set<Integer>>();
		Map<Integer, Set<Integer>> disjointness1 = new HashMap<Integer,Set<Integer>>();
		
		Map<Integer, Set<Integer>> types = new HashMap<Integer,Set<Integer>>();
		
		Set<Integer> fromTrue2 = new HashSet<Integer>();
		

		Map<Integer, Set<Integer>> mappings= new HashMap<Integer, Set<Integer>>();
		
		
		disjointness1.put(2, new HashSet<Integer>());		
		disjointness1.get(2).add(3);
		
		disjointness1.put(7, new HashSet<Integer>());	
		disjointness1.get(7).add(10);
		
		
		tax1.put(1, new HashSet<Integer>());
		tax1.put(2, new HashSet<Integer>());
		tax1.put(3, new HashSet<Integer>());
		tax1.put(4, new HashSet<Integer>());
		tax1.get(1).add(2);
		tax1.get(1).add(3);
		tax1.get(3).add(4);
		tax1.get(4).add(5);
		
		
		tax1.put(6, new HashSet<Integer>());
		tax1.put(7, new HashSet<Integer>());
		tax1.get(6).add(7);
		tax1.get(6).add(10);
		tax1.get(7).add(8);
		tax1.get(7).add(9);
		
		
		mappings.put(3, new HashSet<Integer>());
		mappings.get(3).add(7);
		
		mappings.put(4, new HashSet<Integer>());
		mappings.get(4).add(10);
		
		mappings.put(7, new HashSet<Integer>());
		mappings.get(7).add(3);
		
		mappings.put(10, new HashSet<Integer>());
		mappings.get(10).add(4);
		
		/*mappings.put(2, new HashSet<Integer>());
		mappings.get(2).add(8);
		mappings.put(8, new HashSet<Integer>());
		mappings.get(8).add(2);*/
		
		//fromTrue1.add(1);
		//fromTrue2.add(1);
		
		//fromTrue1.add(1);
		//fromTrue2.add(3);
		
		//fromTrue.add(5);
		//fromTrue.add(6);
		//fromTrue.add(1);
		//fromTrue1.add(2);
		//fromTrue.add(4);
		
		Map<Integer, Set<Link>> ignoreFS = new HashMap<Integer, Set<Link>>();
		ignoreFS.put(4, new HashSet<Link>());
		ignoreFS.get(4).add(new Link(13, 10));
		ignoreFS.put(10, new HashSet<Link>());
		ignoreFS.get(10).add(new Link(15, 4));
		//ignoreFS.put(9, new HashSet<Link>());
		//ignoreFS.get(9).add(new Link(11, 1));
		
		//DowlingGallierHornSAT dgSat = new DowlingGallierHornSAT(fromTrue, tax, disjointness);
		
		
		//DowlingGallierHornSAT dgSat = new DowlingGallierHornSAT(fromTrue1, fromTrue2, tax1, tax2, disjointness1, disjointness2, mappings, shift);
		//LogOutput.print("Is sat?: " + dgSat.isSatisfiable());
		
		Map<Set<Integer>, Integer> generalHornAxioms1 = new HashMap<Set<Integer>, Integer>();
		Map<Integer, Set<Integer>> fixedmappings = new HashMap<Integer, Set<Integer>>();
		Map<Integer, Set<Integer>> equiv = new HashMap<Integer, Set<Integer>>();
		equiv.put(4, new HashSet<Integer>());
		equiv.get(4).add(44);
		equiv.put(44, new HashSet<Integer>());
		equiv.get(44).add(4);
		
		Set<HornClause> mappings2ignore = new HashSet<HornClause>();		

		LogOutput.showOutpuLog(true);
		
		DowlingGallierHornSAT dgSat = new DowlingGallierHornSAT(tax1, equiv, disjointness1, generalHornAxioms1, false, types, fixedmappings, mappings, mappings2ignore);
		LogOutput.print("Is sat 1?: " + dgSat.isSatisfiable(1));
		
		LogOutput.print("Is sat 1i?: " + dgSat.isSatisfiable(1, ignoreFS));
		LogOutput.print(dgSat.getMappingsInvolvedInError().toString());
		
		LogOutput.print("Is sat 2?: " + dgSat.isSatisfiable(2));
		LogOutput.print("Is sat 2i?: " + dgSat.isSatisfiable(2, ignoreFS));
		LogOutput.print(dgSat.getMappingsInvolvedInError().toString());
		
		LogOutput.print("Is sat 3?: " + dgSat.isSatisfiable(3));
		LogOutput.print("Is sat 3i?: " + dgSat.isSatisfiable(3, ignoreFS));
		LogOutput.print(dgSat.getMappingsInvolvedInError().toString());
		
		LogOutput.print("Is sat 4?: " + dgSat.isSatisfiable(4));
		LogOutput.print(dgSat.getMappingsInvolvedInError().toString());
		LogOutput.print("Is sat 4i?: " + dgSat.isSatisfiable(4, ignoreFS));
		LogOutput.print(dgSat.getMappingsInvolvedInError().toString());
		
		LogOutput.print("Is sat 44?: " + dgSat.isSatisfiable(44));
		LogOutput.print(dgSat.getMappingsInvolvedInError().toString());
		LogOutput.print("Is sat 44i?: " + dgSat.isSatisfiable(44, ignoreFS));
		LogOutput.print(dgSat.getMappingsInvolvedInError().toString());
		
		LogOutput.print("Is sat 5?: " + dgSat.isSatisfiable(5));
		LogOutput.print(dgSat.getMappingsInvolvedInError().toString());
		LogOutput.print("Is sat 5i?: " + dgSat.isSatisfiable(5, ignoreFS));
		LogOutput.print(dgSat.getMappingsInvolvedInError().toString());
		
		LogOutput.print("Is sat 6?: " + dgSat.isSatisfiable(6));
		LogOutput.print("Is sat 6i?: " + dgSat.isSatisfiable(6, ignoreFS));
		LogOutput.print(dgSat.getMappingsInvolvedInError().toString());
		
		LogOutput.print("Is sat 7?: " + dgSat.isSatisfiable(7));
		LogOutput.print("Is sat 7i?: " + dgSat.isSatisfiable(7, ignoreFS));
		LogOutput.print(dgSat.getMappingsInvolvedInError().toString());
		
		LogOutput.print("Is sat 8?: " + dgSat.isSatisfiable(8));
		LogOutput.print("Is sat 8i?: " + dgSat.isSatisfiable(8, ignoreFS));
		LogOutput.print(dgSat.getMappingsInvolvedInError().toString());
		
		LogOutput.print("Is sat 9?: " + dgSat.isSatisfiable(9));
		LogOutput.print("Is sat 9i?: " + dgSat.isSatisfiable(9, ignoreFS));
		LogOutput.print(dgSat.getMappingsInvolvedInError().toString());
		
		LogOutput.print("Is sat 10?: " + dgSat.isSatisfiable(10));
		LogOutput.print(dgSat.getMappingsInvolvedInError().toString());
		LogOutput.print("Is sat 10i?: " + dgSat.isSatisfiable(10, ignoreFS));
		LogOutput.print(dgSat.getMappingsInvolvedInError().toString());
	
	}
	
	
	protected class Pair{
		
		int elem1;
		int elem2;
		
		
		public Pair(int el1, int el2){
			
			elem1=el1;
			elem2=el2;
			
		}
		
		public int getElement1(){
			return elem1;
		}
		public int getElement2(){
			return elem2;
		}
				
		public boolean equals(Object o){
			
			//System.out.print(this + " =?" + o);
			
			if  (o == null)
				return false;
			if (o == this)
				return true;
			if (!(o instanceof Pair))
				return false;
			
			Pair i =  (Pair)o;
			
			return equals(i);
			
		}
		
		
		
		public boolean equals(Pair i){
			
			if (elem1==i.getElement1() && elem2==i.getElement2()){
				//System.out.print(this + " ==" + i);
				return true;
			}
			
			if (elem1==i.getElement2() && elem2==i.getElement1()){
				//System.out.print(this + " ==" + i);			
				return true;
			}
			
			//System.out.print(this + " !=" + i);
			return false;

		}
		
		public  int hashCode() {
			  int code = 10;
			  code = 40 * code * elem1 * elem2;			  
			  return code;
		}

		
		public String toString(){
			return "<" + elem1 +", "+elem2 +">";
		}
		
		
	}
	


	
	
	

}

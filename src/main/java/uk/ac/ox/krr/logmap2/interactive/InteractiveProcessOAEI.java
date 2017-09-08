package uk.ac.ox.krr.logmap2.interactive;

import java.util.ArrayList;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.TreeSet;

import uk.ac.ox.krr.logmap2.indexing.IndexManager;
import uk.ac.ox.krr.logmap2.interactive.objects.MappingObjectInteractivity;
import uk.ac.ox.krr.logmap2.io.LogOutput;
import uk.ac.ox.krr.logmap2.mappings.MappingManager;
import uk.ac.ox.krr.logmap2.oaei.oracle.OracleManager;
import uk.ac.ox.krr.logmap2.utilities.Utilities;


/**
 * 
 * Interactive process exploiting the OAEI oracle
 * 
 * @author Ernesto
 *
 */
public class InteractiveProcessOAEI extends InteractiveProcess {

	
	private List<MappingObjectInteractivity> listOrderedMappings2Ask = 
			new ArrayList<MappingObjectInteractivity>(); 
	
	private IndexManager index;	
	private MappingManager mapping_manager;
	
	//private int numberAssessedMappings=0; //directly
	//private int remainingMappings=0;
	
	private boolean apply_heuristics;
	private boolean ask_everything;
	
	/**New mappings (to process) added in interactivity (by user or heuristic)*/ 
	private Stack<MappingObjectInteractivity> new2Add;
	/**New mappings (to process) deleted in interactivity (by user or heuristic)*/
	private Stack<MappingObjectInteractivity> new2Del;
	
	
	public InteractiveProcessOAEI(
			IndexManager index, 
			MappingManager mapping_manager,
			boolean ask_everything,
			boolean apply_heuristics){
		
		this.index=index;
		this.mapping_manager = mapping_manager;
		
		this.apply_heuristics=apply_heuristics;
		this.ask_everything = ask_everything;
		
		LogOutput.printAlways("Creating ordered mappings to ask to the (simulated) user...");
		createOrderedMappings();
		
		if (!ask_everything){
			if (apply_heuristics)
				extractAmbiguities();
			
			extractConflicts();
		}
		
	}
	
	
	
	
	
	@Override
	public void startInteractiveProcess() {

		new2Add = new Stack<MappingObjectInteractivity>();
		new2Del = new Stack<MappingObjectInteractivity>();
		
		//applyFeedback
		//For all elements in the list of mappings to ask!
		//Ask oarculo is not decission yet in mapping
		//Count assessed directly and by means of conflict
		//Exploit method!
		for (MappingObjectInteractivity mapping : listOrderedMappings2Ask) {
			
			if (!mapping.hasDecision()){
				
				LogOutput.printAlways("Asking mapping...");
				
				if (OracleManager.isMappingValid(
						index.getIRIStr4ConceptIndex(mapping.getIdentifierOnto1()),
						index.getIRIStr4ConceptIndex(mapping.getIdentifierOnto2()))){
					
					mapping.setAddedFlag(true);
					mapping.setAssessedFlag(true);
					if (!ask_everything)
						new2Add.add(mapping);
				}
				else{
					mapping.setRemovedFlag(true);
					mapping.setAssessedFlag(true);
					if (!ask_everything)
						new2Del.add(mapping);
				}
				
				//Apply automatic decisions
				if (!ask_everything)
					exploitImpactUserFeedback();
				
				
			}
			
		}
		

	}

	@Override
	public void endInteractiveProcess() {
		
		for (MappingObjectInteractivity mapping : listOrderedMappings2Ask) {		
		
			if (mapping.isAddedFlagActive()){
			
				if (mapping.getDirMapping()==Utilities.EQ || mapping.getDirMapping()==Utilities.L2R)
					mapping_manager.addSubMapping2Mappings2Review(mapping.getIdentifierOnto1(), mapping.getIdentifierOnto2());
				
				if (mapping.getDirMapping()==Utilities.EQ || mapping.getDirMapping()==Utilities.R2L)
					mapping_manager.addSubMapping2Mappings2Review(mapping.getIdentifierOnto2(), mapping.getIdentifierOnto1());
			}
			else{
				
				//Add them as conflictive
				//TODO remaining mappings then apply automatic heuristics...
				
				if (mapping.getDirMapping()==Utilities.EQ || mapping.getDirMapping()==Utilities.L2R)
					mapping_manager.addSubMapping2ConflictiveAnchors(mapping.getIdentifierOnto1(), mapping.getIdentifierOnto2());
				
				if (mapping.getDirMapping()==Utilities.EQ || mapping.getDirMapping()==Utilities.R2L)
					mapping_manager.addSubMapping2ConflictiveAnchors(mapping.getIdentifierOnto2(), mapping.getIdentifierOnto1());
				
				
			}
		}

	}
	
	
	@Override
	public void endInteractiveProcess(boolean filter) {
		// TODO Auto-generated method stub
		
		//not used filter
		endInteractiveProcess();
		
	}
	
	
	private void createOrderedMappings() {
		
		 TreeSet<MappingObjectInteractivity> orderedSet = 
					new TreeSet<MappingObjectInteractivity>(new MappingInteractivityComparator());
		
		
		 for (MappingObjectInteractivity mapping : mapping_manager.getListOfMappingsToAskUser()){
		
			orderedSet.add(mapping);
			
		}
		
		
		//Create ordered list
		Iterator<MappingObjectInteractivity> it = orderedSet.iterator();

		while (it.hasNext()){
			listOrderedMappings2Ask.add(it.next());
		}
		
		
		orderedSet.clear();

	}
	
	
	private void extractAmbiguities(){
		
		
		for (int i=0; i<listOrderedMappings2Ask.size()-1; i++) {
			for (int j=i+1; j<listOrderedMappings2Ask.size(); j++) {
				
				if (areMappingsAmbiguous(
						listOrderedMappings2Ask.get(i).getIdentifierOnto1(), 
						listOrderedMappings2Ask.get(i).getIdentifierOnto2(),
						listOrderedMappings2Ask.get(j).getIdentifierOnto1(),
						listOrderedMappings2Ask.get(j).getIdentifierOnto2())){
					
					listOrderedMappings2Ask.get(i).addAmbiguousMapping(j);
					listOrderedMappings2Ask.get(j).addAmbiguousMapping(i);
					
				}
				
			}
			
		}
		
		
	}
	
	private void extractConflicts(){
		
		
		for (int i=0; i<listOrderedMappings2Ask.size()-1; i++) {
			for (int j=i+1; j<listOrderedMappings2Ask.size(); j++) {
				
				if (areMappingsInConflict(
						listOrderedMappings2Ask.get(i).getIdentifierOnto1(), 
						listOrderedMappings2Ask.get(i).getIdentifierOnto2(),
						listOrderedMappings2Ask.get(j).getIdentifierOnto1(),
						listOrderedMappings2Ask.get(j).getIdentifierOnto2())){
					
					listOrderedMappings2Ask.get(i).addConflictiveMapping(j);
					listOrderedMappings2Ask.get(j).addConflictiveMapping(i);
					
				}
				
			}
			
		}
		
		
	}
	
	
	
	
	/**
	 * We apply automatic decision depending on the conflictive and ambiguous mappings
	 */
	private void exploitImpactUserFeedback(){
		
		//if apply_heuristics: never remove or add a mapping with a decision
		//hasDecision()
		
		MappingObjectInteractivity mapping;
		
		while (new2Add.size()>0 || new2Del.size()>0){
			
			if (new2Add.size()>0){
				
				mapping = new2Add.pop();
				
				//Marks them as deleted and add them to new2Del stack
				//removeMappingsInConflictWithAddition(mapping);
				
				for (int m_ide : mapping.getMappingsInconflict()){
					
					listOrderedMappings2Ask.get(m_ide).setRemovedFlag(true);
					new2Del.push(listOrderedMappings2Ask.get(m_ide));
				}
				
				if (apply_heuristics){
					
					for (int m_ide : mapping.getAmbiguousMappings()){
						//Only if not decision yet
						if (!listOrderedMappings2Ask.get(m_ide).hasDecision()){
							listOrderedMappings2Ask.get(m_ide).setRemovedFlag(true);
							new2Del.push(listOrderedMappings2Ask.get(m_ide));
						}
					}
				}
				
			
			}
			
			
			
			if (new2Del.size()>0){
				
				mapping = new2Del.pop();
				
				if (apply_heuristics){
					
					for (int m_ide : mapping.getAmbiguousMappings()){
						//Only if not decision yet
						if (!listOrderedMappings2Ask.get(m_ide).hasDecision()){
							listOrderedMappings2Ask.get(m_ide).setAddedFlag(true);
							new2Add.push(listOrderedMappings2Ask.get(m_ide));
						}
					}
				}
				
			}
			
			
		}
		
		
		
	}
	
	
	
	
	
	private boolean areMappingsAmbiguous(int ideA, int ideB, int ideAA, int ideBB){
		
		
		//We have already checked they are not equivalent
		if (ideA==ideAA || ideB==ideBB){
			
			return true;
			
		}		
		
		return false;
		
	}
	
	
	
	//private double getConfidence(MappingObjectInteractivity m){
		//return m.getSimilarityList().get(1);
	//}
	
	
	
	private boolean areMappingsInConflict(int ideA, int ideB, int ideAA, int ideBB){
		
		boolean AequivAA=false;
		boolean BequivBB=false;
		
		boolean AcontAA=false;
		boolean AAcontA=false;
		boolean BcontBB=false;
		boolean BBcontB=false;
		
		boolean AdisjAA=false;
		boolean BdisjBB=false;
		
		boolean conflict=false;

		
		AequivAA = index.areEquivalentClasses(ideA, ideAA);
		
		BequivBB = index.areEquivalentClasses(ideB, ideBB);
		
		AcontAA = AequivAA || index.isSubClassOf(ideA, ideAA);
				
		AAcontA = AequivAA || index.isSubClassOf(ideAA, ideA);
		
		BcontBB = BequivBB || index.isSubClassOf(ideB, ideBB);
		
		BBcontB = BequivBB || index.isSubClassOf(ideBB, ideB);
	
		//over same ontology
		AdisjAA = !AcontAA && !AAcontA && (index.areDisjoint(ideA, ideAA));// || index.isDisjointWithDescendants(ideA, ideAA) || index.isDisjointWithDescendants(ideAA, ideA));
		
		BdisjBB = !BcontBB && !BBcontB && (index.areDisjoint(ideB, ideBB));// || index.isDisjointWithDescendants(ideB, ideBB) || index.isDisjointWithDescendants(ideBB, ideB));
				
				
		conflict = ((AcontAA || AAcontA) && BdisjBB) || ((BcontBB || BBcontB) && AdisjAA);
		
		return conflict;
		
	}
	
	
	
	
	
	
	/**
	 * No comparator. for no order of mappings to ask
	 * @author Ernesto
	 *
	 */
	private class NoComparator implements Comparator<MappingObjectInteractivity> {
		
		public int compare(MappingObjectInteractivity m1, MappingObjectInteractivity m2) {
			
			return -1;
			
		}
		
	}
	
	
	//Order by....
	
	/**
	 * Comparator
	 * @author Ernesto
	 *
	 */
	private class MappingInteractivityComparator implements Comparator<MappingObjectInteractivity> {
		
		
		/**

		 * @param m1
		 * @param m2
		 * @return
		 */
		public int compare(MappingObjectInteractivity m1, MappingObjectInteractivity m2) {

			return orderByScopeAndLex(m1, m2);
		}
		

		
		
		public int orderByScopeAndLex(MappingObjectInteractivity m1, MappingObjectInteractivity m2){
			if (m1.getScope()< m2.getScope()){
				return 1;						
			}	
			else if (m1.getScope()==m2.getScope()){
			
				if (m1.getLexSim()< m2.getLexSim()){
					return 1;						
				}
				else{
					return -1;
				}
				
				
			}
			else{
				return -1;
			}
		}
	}




}

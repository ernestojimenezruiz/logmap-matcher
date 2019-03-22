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

import uk.ac.ox.krr.logmap2.indexing.IndexManager;
import uk.ac.ox.krr.logmap2.mappings.MappingManager;
import uk.ac.ox.krr.logmap2.reasoning.ReasonerAccess;
import uk.ac.ox.krr.logmap2.reasoning.ReasonerManager;
import uk.ac.ox.krr.logmap2.reasoning.StructuralReasonerExtended;
import uk.ac.ox.krr.logmap2.overlapping.OverlappingExtractor;
import uk.ac.ox.krr.logmap2.reasoning.explanation.*;

import uk.ac.ox.krr.logmap2.io.LogOutput;
import uk.ac.ox.krr.logmap2.utilities.ELAxiomVisitor;
import uk.ac.ox.krr.logmap2.utilities.Utilities;



import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import java.util.*;


/**
 * 
 * This class repairs all unsatisfiable concepts deleting conflictive mappings using full reasoning.
 * 
 * @author Ernesto
 *
 */
public class AnchorsAssessmentFullReasoning {
	
	private IndexManager index;
	
	private MappingManager mapping_extractor;
	
	private OverlappingExtractor overlappingExtractor;
	
	private ReasonerAccess reasoner_access;

	private BlackBoxExplanationExtractor explanations_onto;
	
	//We only want to fin an explanation involving an axiom
	private int max_explanations = 1;
	
	//private int maxNumberOfPlans = 250; //Max number of plans
	
	//private int maxSizeOfPlans = 12; //Max size axioms of a plan
	
	private int reasoner_id = ReasonerManager.HERMIT;
	
	private List<Set<OWLAxiom>> justifications_unsat = new ArrayList<Set<OWLAxiom>>();	
	
	private List<Set<OWLAxiom>> repair_plans;
	private PlanExtractor planExtractor;
	
	private Map<OWLClass, Integer> owlclass2identifier = new HashMap<OWLClass, Integer>();
	private Map<OWLDataProperty, Integer> owldprop2identifier = new HashMap<OWLDataProperty, Integer>();
	private Map<OWLObjectProperty, Integer> owloprop2identifier = new HashMap<OWLObjectProperty, Integer>();
	
	private MappingAxiomVisitor mappingVisitor = new MappingAxiomVisitor();
	
	private Set<OWLAxiom> mappingAxioms = new HashSet<OWLAxiom>();
	private Set<OWLAxiom> mappingAxioms2repair = new HashSet<OWLAxiom>();
	
	
	private boolean review_anchors=true;
	
	//For top order of unsat classes
	private StructuralReasonerExtended strctReasoner;
	
	
	private long init, fin;
	
	
	/**
	 * 
	 * @param index
	 * @param mapping_extractor
	 * @param overlappingExtractor
	 */
	public AnchorsAssessmentFullReasoning(
			int ReasonerID,
			IndexManager index,
			MappingManager mapping_extractor,
			OverlappingExtractor overlappingExtractor,
			boolean review_anchors) throws Exception{
		
		 
		this.reasoner_id = ReasonerID;
		
		this.index=index;
		this.mapping_extractor=mapping_extractor;
		this.overlappingExtractor=overlappingExtractor;
	
		this.review_anchors=review_anchors;
		
		setUpReasoner(false);
		
		
	}
	
	
	/**
	 * Checks unsatisfiability without classifying
	 * @throws Exception
	 */
	public void checkUnsatisfiability() throws Exception{
		
		LogOutput.printAlways("\nUNSATISFIABILITY");
		
		
		//Transform mappings 2 owl
		getOWLAxioms4Mappings();
				
		//Reason
		setUpReasoner(false);
		
		init = Calendar.getInstance().getTimeInMillis();
		
		int unsat=0;
		int unknown=0;
		
		int num_class=0;
		
		int state;
		
		for (OWLClass cls: reasoner_access.getOntology().getClassesInSignature()){
			
			//LogOutput.printAlways("Is unsat?? " + cls.toString());
			
			/*if (!reasoner_access.isSatisfiable(cls)){
				unsat++;
				LogOutput.printAlways(cls + "-  UNSAT");
			}
			else{
				LogOutput.printAlways(cls + "-  SAT");
			}*/
			
			num_class++;
			
			state = reasoner_access.isSatisfiable_withTimeout(cls, 10);
			
			if (state == reasoner_access.UNSAT){
				unsat++;
				//LogOutput.printAlways(cls + "-  UNSAT");
			}
			else if (state == reasoner_access.UNKNOWN){
				unknown++;
				LogOutput.printAlways("\t" + cls.getIRI().toString() + "-  UNKNOWN SAT. Test: " + num_class);
			}
			
			
		}
		
		fin = Calendar.getInstance().getTimeInMillis();
		
		double time = (float)((double)fin-(double)init)/1000.0;
		
		
		LogOutput.printAlways("\tUNSAT classes: " + unsat + " of " + reasoner_access.getOntology().getClassesInSignature().size());
		LogOutput.printAlways("\tUNKNOWN classes: " + unknown + " of " + reasoner_access.getOntology().getClassesInSignature().size());
		LogOutput.printAlways("\tTIME checking unsatisfiability (s): " + time  + 
				", Averageper class: " + Utilities.getRoundValue(
						(time/((double)reasoner_access.getOntology().getClassesInSignature().size())),
						8));
	}
	
	
	/**
	 * Checks unsatisfiability without classifying
	 * @throws Exception
	 */
	public boolean checkIfAnyUnsat() throws Exception{
		
		LogOutput.printAlways("\nUNSATISFIABILITY");
		
		long initlocal = Calendar.getInstance().getTimeInMillis();
		
		
		int state;
		int num_class=0;
		
		for (OWLClass cls: reasoner_access.getOntology().getClassesInSignature()){
			
			num_class++;
			
			state = reasoner_access.isSatisfiable_withTimeout(cls, 10);
			
			if (state == reasoner_access.UNSAT){
				return true;
			}
			else if (state == reasoner_access.UNKNOWN){
				LogOutput.printAlways("\t" + cls.getIRI().toString() + "-  UNKNOWN SAT. Test: " + num_class);
			}
			
			
		}
		
		fin = Calendar.getInstance().getTimeInMillis();
		
		double time = (float)((double)fin-(double)initlocal)/1000.0;
		
		return false;
		
		
	}
	
	
	
	/**
	 * Classifies and repairs unsat
	 * @throws Exception
	 */
	public void classifyAndRepairUnsatisfiability() throws Exception{
	
		
		init = Calendar.getInstance().getTimeInMillis();
		

		//Reason
		LogOutput.printAlways("\nCLASSIFYING");
		
		//Transform mappings 2 owl
		getOWLAxioms4Mappings();
		
		setUpReasoner(true);		
		setUpBlackBosExplanationManager();
		
		//Will be reused
		strctReasoner = new StructuralReasonerExtended(reasoner_access.getOntology());
		
		//ONLY Statistics
		//LogOutput.printAlways("\nSTATISTICS EXPLANATIONS (UNSAT: " + reasoner_access.getUnsatisfiableClasses().size() + ").");
		//repairUnsatisfiability(50);
		
		
		int it=0;
		
		LogOutput.printAlways("\nREPAIR GREEDY ALGO:");
		
		while (reasoner_access.hasUnsatisfiableClasses()){
			
			LogOutput.printAlways("\tUNSAT CLASSES iter  " + it + ": " + reasoner_access.getUnsatisfiableClasses().size());
			
			//Split unsat and solve!!
			if (!repairUnsatisfiability())
				break;
			
			applyBestRepairPlan();
			
			//Clear previous structures
			clearStructures();
			
			//Trasnform mappings 2 owl
			getOWLAxioms4Mappings();
			
			//Reason
			//setUpReasoner(false);			
			//Any unsat
			//if (checkIfAnyUnsat()){ //classify and reinit blackbox
			//	reasoner_access.classifyOntology();
			//	setUpBlackBosExplanationManager();
			//}
			//else{ //We are done
			//	reasoner_access.getUnsatisfiableClasses().clear();
			//}
			
			//Reason
			LogOutput.printAlways("\tCLASSIFYING");
			setUpReasoner(true);
			setUpBlackBosExplanationManager();
			
			it++;
			
		}
		
		fin = Calendar.getInstance().getTimeInMillis();
		
		double time = (float)((double)fin-(double)init)/1000.0;
		LogOutput.printAlways("\tTIME GREEDY Repair Algorithm (s): " + time);
		LogOutput.printAlways("\tUNSAT CLASSES after cleaning: " + reasoner_access.getUnsatisfiableClasses().size());
		
		//We do not clean last reasoner
		//It is necessary for index
		
	}
	
	
	
	public ReasonerAccess getReasoner(){
		return reasoner_access;
	}
	
	
	
	
	
	
	private void getOWLAxioms4Mappings(){
		
		//owlclass2identifier.clear();
		//owldprop2identifier.clear();
		//owloprop2identifier.clear();
		
		
		mappingAxioms.clear();
		mappingAxioms2repair.clear();
		
		//Non fixed mappings yet
		if (review_anchors){
		
			for (int ide1 : mapping_extractor.getLogMapMappings().keySet()){
				for (int ide2 : mapping_extractor.getLogMapMappings().get(ide1)){
					
					if (ide1<ide2){
					
					//We add only one side. This may help with process
					/*mapping_axioms.add(
							index.getFactory().getOWLSubClassOfAxiom(
									index.getOWLClass4ConceptIndex(ide1),
									index.getOWLClass4ConceptIndex(ide2)								
									)
							);*/
						mappingAxioms.add(
								index.getFactory().getOWLEquivalentClassesAxiom(
										index.getOWLClass4ConceptIndex(ide1),
										index.getOWLClass4ConceptIndex(ide2)								
										)
								);
						
						mappingAxioms2repair.add(
								index.getFactory().getOWLEquivalentClassesAxiom(
										index.getOWLClass4ConceptIndex(ide1),
										index.getOWLClass4ConceptIndex(ide2)								
										)
								);
					
						owlclass2identifier.put(index.getOWLClass4ConceptIndex(ide1), ide1);
						owlclass2identifier.put(index.getOWLClass4ConceptIndex(ide2), ide2);
					}
				}			
			}
		}
		else {
			
			//THERE ARE FIXED MAPPINGS
			
			for (int ide1 : mapping_extractor.getFixedMappings().keySet()){
				for (int ide2 : mapping_extractor.getFixedMappings().get(ide1)){
					
					if (ide1<ide2){
						
						//Do not touch
						mappingAxioms.add(
								index.getFactory().getOWLEquivalentClassesAxiom(
										index.getOWLClass4ConceptIndex(ide1),
										index.getOWLClass4ConceptIndex(ide2)								
										)
								);
					
						owlclass2identifier.put(index.getOWLClass4ConceptIndex(ide1), ide1);
						owlclass2identifier.put(index.getOWLClass4ConceptIndex(ide2), ide2);
					}
				}			
			}
			
			for (int ide1 : mapping_extractor.getMappings2Review().keySet()){
				for (int ide2 : mapping_extractor.getMappings2Review().get(ide1)){
					
					if (ide1<ide2){
						
						mappingAxioms.add(
								index.getFactory().getOWLEquivalentClassesAxiom(
										index.getOWLClass4ConceptIndex(ide1),
										index.getOWLClass4ConceptIndex(ide2)								
										)
								);
					
						mappingAxioms2repair.add(
								index.getFactory().getOWLEquivalentClassesAxiom(
										index.getOWLClass4ConceptIndex(ide1),
										index.getOWLClass4ConceptIndex(ide2)								
										)
								);
						
						owlclass2identifier.put(index.getOWLClass4ConceptIndex(ide1), ide1);
						owlclass2identifier.put(index.getOWLClass4ConceptIndex(ide2), ide2);
					}
				}			
			}
			
			
		}
		
		
		
		
		int ide2;
		for (int ide1 : mapping_extractor.getDataPropertyAnchors().keySet()){
			
			ide2 = mapping_extractor.getDataPropertyAnchors().get(ide1);
			
			//We add equivalence
			mappingAxioms.add(
						index.getFactory().getOWLEquivalentDataPropertiesAxiom(
								index.getOWLDataProperty4PropertyIndex(ide1),
								index.getOWLDataProperty4PropertyIndex(ide2)								
								)
						);
			
			mappingAxioms2repair.add(
					index.getFactory().getOWLEquivalentDataPropertiesAxiom(
							index.getOWLDataProperty4PropertyIndex(ide1),
							index.getOWLDataProperty4PropertyIndex(ide2)								
							)
					);
			
			
			
			owldprop2identifier.put(index.getOWLDataProperty4PropertyIndex(ide1), ide1);
			owldprop2identifier.put(index.getOWLDataProperty4PropertyIndex(ide2), ide2);
			
			
		}
		
		for (int ide1 : mapping_extractor.getObjectPropertyAnchors().keySet()){
			
			ide2 = mapping_extractor.getObjectPropertyAnchors().get(ide1);
			
			//We add equivalence
			mappingAxioms.add(
						index.getFactory().getOWLEquivalentObjectPropertiesAxiom(
								index.getOWLObjectProperty4PropertyIndex(ide1),
								index.getOWLObjectProperty4PropertyIndex(ide2)								
								)
						);
			
			//We add equivalence
			mappingAxioms2repair.add(
						index.getFactory().getOWLEquivalentObjectPropertiesAxiom(
								index.getOWLObjectProperty4PropertyIndex(ide1),
								index.getOWLObjectProperty4PropertyIndex(ide2)								
								)
						);
			
			owloprop2identifier.put(index.getOWLObjectProperty4PropertyIndex(ide1), ide1);
			owloprop2identifier.put(index.getOWLObjectProperty4PropertyIndex(ide2), ide2);
		}
		
		
		LogOutput.printAlways("\tNUMBER OF MAPPINGS: " + mappingAxioms.size() + " (to repair: " + mappingAxioms2repair.size() + ").");

		
	}
	
	
	
	public void clearStructures(){
		reasoner_access.clearStructures();
		
		owlclass2identifier.clear();
		owldprop2identifier.clear();
		owloprop2identifier.clear();
		
		mappingAxioms.clear();
		mappingAxioms2repair.clear();
		
		explanations_onto=null;
		
		justifications_unsat.clear();
		
	}
	
	
	
	
	
	private void setUpReasoner(boolean classify) throws Exception{
		
		reasoner_access = ReasonerManager.getMergedOntologyReasoner(
				reasoner_id, 
				overlappingExtractor.getTBOXOverlappingOnto1(),
				overlappingExtractor.getTBOXOverlappingOnto2(),
				mappingAxioms,				
				true);
		
		
		if (classify)
			reasoner_access.classifyOntology();
		

		
	}
	
	
	private void setUpBlackBosExplanationManager(){
		explanations_onto = new BlackBoxExplanationExtractor(
				reasoner_access.getOntology(), 
				reasoner_access.getReasonerFactory(),
				reasoner_access.getReasoner(),
				mappingAxioms2repair,//axioms to repair
				max_explanations
				);
	}
	
	
	/**
	 * Only for statistic purposes
	 * @return
	 */
	private void repairUnsatisfiability(int maxExplanations){
	
		explanations_onto.setMaxExplanations(maxExplanations);
		//explanations_onto.setMaxExplanationSearch(500);
		
		init = Calendar.getInstance().getTimeInMillis();
		
		int number=0;
		
		int numExplanations=0;
		int numExplanationsSearch=0;
		
		for (OWLClass cls : reasoner_access.getUnsatisfiableClasses()){
			
			
			//System.out.println("\nUNSAT CLASS: " + cls.getIRI().toString());
			number++;
			
			//Subclass of nothing
			explanations_onto.handleExplanations(
					index.getFactory().getOWLSubClassOfAxiom(cls, index.getFactory().getOWLNothing()));
			
			numExplanations+=explanations_onto.getNumberExplanations();
			numExplanationsSearch+=explanations_onto.getNumberExplanationSearch();
			
			if (explanations_onto.isTimedOut()){
				LogOutput.print("Time out in explanation extraction");			
				//return false; //TODO Allow output...
			}
			
			
			if (number==10)
				break;
			
							
		}
		
		fin = Calendar.getInstance().getTimeInMillis();
	
		double time = (float)((double)fin-(double)init)/1000.0;
		
	
		LogOutput.printAlways("\tAverage Explanations per unsat (s): " + Utilities.getRoundValue(
						//(time/((double)reasoner_access.getUnsatisfiableClasses().size())),
						(numExplanations/((double)number)),
						4));
		
		LogOutput.printAlways("\tAverage Explanation Search per unsat (s): " + Utilities.getRoundValue(
				//(time/((double)reasoner_access.getUnsatisfiableClasses().size())),
				(numExplanationsSearch/((double)number)),
				4));
		
		
		LogOutput.printAlways("\tTIME getting explanations per unsat classes (s): " + time  + 
				", Average per unsat class: " + Utilities.getRoundValue(
						//(time/((double)reasoner_access.getUnsatisfiableClasses().size())),
						(time/((double)number)),
						4));
		
		
	}
	
	

	
	
	private boolean repairUnsatisfiability(){	
		
		int max_unsat4repair=100; //used 50 before
		int num=0;
		
		//Just one explanations for greedy algo!
		explanations_onto.setMaxExplanations(1);
		
		//Reinit reasoner every x repaired classes
		Set<OWLClass> topUnsat = new HashSet<OWLClass>();
		
		
		//If bigger we need to reduce the number a bit random. There is no point of extracting all topunsat
		if (reasoner_access.getUnsatisfiableClasses().size()<5000){
			
			List<OWLClass> initialUnsat = new ArrayList<OWLClass>();
			Set<OWLClass> excluded = new HashSet<OWLClass>();
												
			initialUnsat.addAll(reasoner_access.getUnsatisfiableClasses());
			
			//Problem if unsat are >10,000!!
			boolean isTop;
			//boolean i_sub_j;
			//boolean i_sup_j;
			
			
			//Order classes and repair only top classes
			for (int i=0; i<initialUnsat.size(); i++){
				
				if (excluded.contains(initialUnsat.get(i)))
					continue; //is not a top class
				
				isTop=true;
				
				for (int j=0; j<initialUnsat.size(); j++){
					if (i==j)
						continue;
					
					//reasoneracces does not work... if they are unsat!!
					//i_sup_j = strctReasoner.isSubClassOf(initialUnsat.get(j), initialUnsat.get(i));
					//i_sub_j = strctReasoner.isSubClassOf(initialUnsat.get(i), initialUnsat.get(j));
					
					if (strctReasoner.areEquivalent(initialUnsat.get(i), initialUnsat.get(j))){ //equivalence
						excluded.add(initialUnsat.get(j)); //we repair only one side
						continue; //
					}				
					//else if (i_sup_j){ //exclude j
					else if (strctReasoner.isSubClassOf(initialUnsat.get(j), initialUnsat.get(i))){
						excluded.add(initialUnsat.get(j));
					}
					//else if (i_sub_j){ //exclude i
					else if (strctReasoner.isSubClassOf(initialUnsat.get(i), initialUnsat.get(j))){
						isTop=false;
						break;
					}
					
				}//For j 
				
				//is top
				if (isTop){
					topUnsat.add(initialUnsat.get(i));
					if (topUnsat.size()>=max_unsat4repair) //Will also improve times if we stop here...
						break;
				}
				
			}//for i
			
			initialUnsat.clear();
			excluded.clear();
		}
		
			
		System.out.println("TopUnsat: " + topUnsat.size());
		
		
		if (topUnsat.isEmpty()){
			//topUnsat.addAll(reasoner_access.getUnsatisfiableClasses());
			topUnsat = reasoner_access.getUnsatisfiableClasses(); //we just make reference
		}
		
		
		
		for (OWLClass cls : topUnsat){
				
				//System.out.println("\nUNSAT CLASS: " + cls.getIRI().toString());
				
				//Subclass of nothing
				explanations_onto.handleExplanations(
						index.getFactory().getOWLSubClassOfAxiom(cls, index.getFactory().getOWLNothing()));
				
				if (explanations_onto.isTimedOut()){
					//LogOutput.print("Time out in explanation extraction");			
					//return false; //TODO Allow output...
				}
				
				justifications_unsat.addAll(explanations_onto.getExplanations());
				
				//We repair only max_repair unsat classes each time
				num++;
				if (num>max_unsat4repair) //only is all unsat classes is used. topUnsat contains at most 20
					break;
			
		}
		
		topUnsat.clear();
		
		//LogOutput.print(("Justifications size: " + justifications_Cdel.get(0).size());
		//LogOutput.print(("\tJustifications:\n" + justifications_Cdel);
		
		//Get repairs plans P (a few of them)		
		planExtractor = new PlanExtractor(justifications_unsat);
		planExtractor.extractPlans();
		repair_plans = planExtractor.getAllPlansAx();
		
		LogOutput.printAlways("\tRepair plans number: " + repair_plans.size());
		
		//LogOutput.print(("\tRepair plans:\n" + repair_plans);
		//for (Set<OWLAxiom> repair : repair_plans){
		//	LogOutput.print(repair.toString());					
		//}
		
		if (repair_plans.size()==0){
			LogOutput.print("No repairs were found!");
			return false;
		}		
		
		return true;
		
		
	}
	
	
	/**
	 * Gest the repair with less confidence
	 * @return
	 */
	private void applyBestRepairPlan(){
		
		Set<OWLAxiom> best_repair=null;
				
		double min_conf = 10000;
		double conf;
		
		mappingVisitor.setDeleteAxiom(false);//used in confidence
		
		for (Set<OWLAxiom> repair : repair_plans){
			
			conf = getConfidence4Plan(repair);
			
			if (min_conf > conf){
				min_conf = conf;
				best_repair = repair;
			}
		}
		
		mappingVisitor.setDeleteAxiom(true);
		
		for (OWLAxiom ax : best_repair){			
			ax.accept(mappingVisitor);
			//It also deletes axiom from structures
			//TODO add to conflicts
		}
		
		
		
		
		
		
	}
	
	
	private double getConfidence4Plan(Set<OWLAxiom> repair){
		
		double conf = 0.0;
		
		for (OWLAxiom ax : repair){			
			ax.accept(mappingVisitor);
			conf += mappingVisitor.conf;			
		}
		
		return conf;
	}
	
	
	
	
	
	
	
	
	public class MappingAxiomVisitor extends ELAxiomVisitor {
		
		public int ide1;		
		public int ide2;
		public int type;//0 subclass, 1: equivclas, 2: oprop, 3: dprop
		public double conf;
		private boolean deleteAxiom=false; //Indicates if the axioms must be deleted from anchors
		
		public void setDeleteAxiom(boolean delete_axiom){
			deleteAxiom = delete_axiom;
		}
		
		public void visit(OWLSubClassOfAxiom ax) {
			ide1 = owlclass2identifier.get(ax.getSubClass().asOWLClass());
			ide2 = owlclass2identifier.get(ax.getSuperClass().asOWLClass());
			type = 0;
			conf = mapping_extractor.getConfidence4Mapping(ide1, ide2);
			
			deleteSubMapping();
			
			
		}
		
		public void visit(OWLEquivalentClassesAxiom ax) {
			ide1 = owlclass2identifier.get(ax.getClassExpressionsAsList().get(0).asOWLClass());
			ide2 = owlclass2identifier.get(ax.getClassExpressionsAsList().get(1).asOWLClass());
			type = 1;
			conf = mapping_extractor.getConfidence4Mapping(ide1, ide2);
			
			deleteEquivMapping();
		
		}
		
		
		public void visit(OWLEquivalentObjectPropertiesAxiom ax) {
			
			Iterator<OWLObjectPropertyExpression> it = ax.getProperties().iterator();
			
			ide1 = owloprop2identifier.get(it.next().asOWLObjectProperty());
			ide2 = owloprop2identifier.get(it.next().asOWLObjectProperty());
			type = 2;
			conf = mapping_extractor.getConfidence4ObjectPropertyAnchor(ide1, ide2);
			
			if (deleteAxiom){
				mapping_extractor.getObjectPropertyAnchors().remove(ide1);
			}
			
			
		}
		
		public void visit(OWLEquivalentDataPropertiesAxiom ax) {
			
			Iterator<OWLDataPropertyExpression> it = ax.getProperties().iterator();
			ide1 = owldprop2identifier.get(it.next().asOWLDataProperty());
			ide2 = owloprop2identifier.get(it.next().asOWLDataProperty());
			type = 3;
			conf = mapping_extractor.getConfidence4DataPropertyAnchor(ide1, ide2);
			
			if (deleteAxiom){
				mapping_extractor.getDataPropertyAnchors().remove(ide1);
			}
			
		}
		
		
		private void deleteSubMapping(){
			
			if (deleteAxiom){
				
				if (review_anchors){	
					mapping_extractor.removeSubMappingFromStructure(ide1, ide2);
				}
				else {
					mapping_extractor.removeSubMappingFromMappings2Review(ide1, ide2);
				}
				
				mapping_extractor.addSubMapping2ConflictiveAnchors(ide1, ide2);
				
			}
		}
		
		private void deleteEquivMapping(){
			
			if (deleteAxiom){
				
				if (review_anchors){	
					mapping_extractor.removeSubMappingFromStructure(ide1, ide2);
					mapping_extractor.removeSubMappingFromStructure(ide2, ide1);
				}
				else {
					mapping_extractor.removeSubMappingFromMappings2Review(ide1, ide2);
					mapping_extractor.removeSubMappingFromMappings2Review(ide2, ide1);
				}
				
				mapping_extractor.addSubMapping2ConflictiveAnchors(ide1, ide2);
				mapping_extractor.addSubMapping2ConflictiveAnchors(ide2, ide1);
				
			}
		}
		
	}
	
	
	
	
	
	
	

}

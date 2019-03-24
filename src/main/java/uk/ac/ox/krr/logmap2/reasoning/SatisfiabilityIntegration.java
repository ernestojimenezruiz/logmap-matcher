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
package uk.ac.ox.krr.logmap2.reasoning;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.RDFXMLDocumentFormat;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import uk.ac.ox.krr.logmap2.io.LogOutput;
import uk.ac.ox.krr.logmap2.utilities.Utilities;

public class SatisfiabilityIntegration {
	
	
	//DEFAULT SETTINGS
	private static int timeoutClassification=300;//seconds
	private static int timeoutClassSatisfiabilityCheck=10;//seconds
	private static int REASONER = ReasonerManager.HERMIT;
	

	private String rootPath = "file:/usr/local/data/DataUMLS/UMLS_Onto_Versions/LogMap2_Mappings/";
	private String sufix = "_logmap2_Output/";
	private String module1str = "module1.owl";
	private String module2str = "module2.owl";
	private String mappingstr = "mappings.owl";
	
	private OWLDataFactory factory;
	private OWLOntologyManager managerOnto1;
	private OWLOntologyManager managerOnto2;
	private OWLOntologyManager managerMappings;
	private OWLOntologyManager managerMerged;

	private OWLOntology moduleonto1;
	private OWLOntology moduleonto2;
	private OWLOntology mergedOntology;
	private OWLOntology mappingsOntology;
	
	private String mappingsOntologyFile;
	private String moduleonto1file;
	private String moduleonto2file;

	private String pair_str;
	
	long init, fin;

	
	ReasonerAccess reasonerAccess;
	
	int resultClassEval;
	
	final int SAT = 0;
	final int UNSAT = 1;
	final int UNKNOWN = 2;
	final int KNOWN = 3;
	
	OWLClass current_cls;
	
	
	private int unsat = 0;
	
	
	private boolean useTimeOut=true;
	
	
	private boolean checkSatisfiabilityIndividually;
	
	
	
	public SatisfiabilityIntegration(
			Set<OWLAxiom> o1_ax, 
			Set<OWLAxiom> o2_ax,
			Set<OWLAxiom> mappings_ax,
			boolean useTimeout,
			boolean useFactory) throws Exception{
		this(o1_ax, o2_ax, mappings_ax, false, useTimeout, useFactory);
	}
	
	
	
	public SatisfiabilityIntegration(
			Set<OWLAxiom> o1_ax, 
			Set<OWLAxiom> o2_ax,
			Set<OWLAxiom> mappings_ax,
			boolean checkSatisfiabilityIndividually, //no classification, check for each class
			boolean useTimeout,
			boolean useFactory) throws Exception{
		

		this.checkSatisfiabilityIndividually = checkSatisfiabilityIndividually;
		 

		this.useTimeOut=useTimeout;
		
			
		//Setting up reasoner
				init=Calendar.getInstance().getTimeInMillis();
				reasonerAccess = createMergedReasonerAccess(REASONER, o1_ax, o2_ax, mappings_ax, useFactory);
				
				checkSatisfiabilityIntegration();
				fin=Calendar.getInstance().getTimeInMillis();
				
				LogOutput.printAlways("Time extracting unsat classes (s): " + (float)((double)fin-(double)init)/1000.0);
				
				
				//for (OWLClass unsat : reasonerAccess.getUnsatisfiableClasses()){
				//	
				//}
	}
	
	
	public SatisfiabilityIntegration(
			OWLOntology o1, 
			OWLOntology o2, 
			OWLOntology mappings,
			boolean useTimeout,
			boolean useFactory) throws Exception{
		this(o1, o2, mappings, false, useTimeout, useFactory);
	}
	
	
	public SatisfiabilityIntegration(
			OWLOntology o1, 
			OWLOntology o2, 
			OWLOntology mappings,
			boolean checkSatisfiabilityIndividually, //no classification, check for each class
			boolean useTimeout,
			boolean useFactory) throws Exception{
		
		
		 moduleonto1 = o1;
		 moduleonto2 = o2;
		 mappingsOntology = mappings;
		 
		 this.useTimeOut=useTimeout;
		
		 this.checkSatisfiabilityIndividually = checkSatisfiabilityIndividually;
		 
		//Setting up reasoner
				reasonerAccess = createMergedReasonerAccess(REASONER, moduleonto1, moduleonto2, mappingsOntology, useFactory);
		 		//reasonerAccess = createMergedReasonerAccess(ReasonerManager.ELK, moduleonto1, moduleonto2, mappingsOntology, useFactory);
				
				init=Calendar.getInstance().getTimeInMillis();
				checkSatisfiabilityIntegration();
				fin=Calendar.getInstance().getTimeInMillis();
				
				LogOutput.printAlways("Time extracting unsat classes (s): " + (float)((double)fin-(double)init)/1000.0);
				
				
				//for (OWLClass unsat : reasonerAccess.getUnsatisfiableClasses()){
				//	
				//}
	}
	
	
	
	
	public SatisfiabilityIntegration(boolean timeOutClassification) throws Exception{
		
		this.useTimeOut=timeOutClassification;
		
		
		
		
		//setOntologies();
		setNonFixedOntologies();
		
		init=Calendar.getInstance().getTimeInMillis();
		loadOntologies();
		fin=Calendar.getInstance().getTimeInMillis();
		LogOutput.printAlways("Time loading ontologies (s): " + (float)((double)fin-(double)init)/1000.0);
		
		//Setting up reasoner
		reasonerAccess = createMergedReasonerAccess(REASONER, moduleonto1, moduleonto2, mappingsOntology, true);
		
		init=Calendar.getInstance().getTimeInMillis();
		checkSatisfiabilityIntegration();
		fin=Calendar.getInstance().getTimeInMillis();
		
		LogOutput.printAlways("Time extracting unsat classes (s): " + (float)((double)fin-(double)init)/1000.0);
		
		
		//for (OWLClass unsat : reasonerAccess.getUnsatisfiableClasses()){
		//	
		//}
		
		
		
	}
	

	public static void setTimeoutClassification(int timeout_seconds){
		timeoutClassification = timeout_seconds;
	}

	public static void setTimeoutClassSatisfiabilityCheck(int timeout_seconds){
		timeoutClassSatisfiabilityCheck = timeout_seconds;
	}
	

	public static void setReasoner(int reasoner_id){
		REASONER = reasoner_id;
	}
	
	public boolean hasUnsatClasses(){
		return (unsat>0);
	}
	
	public int getNumUnsatClasses(){
		return unsat;
	}
	
	
	private void createMergedOntology(OWLOntology O1, OWLOntology O2, OWLOntology M) throws Exception{
		Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
		axioms.addAll(O1.getAxioms());
		axioms.addAll(O2.getAxioms());
		axioms.addAll(M.getAxioms());
				
		managerMerged = OWLManager.createOWLOntologyManager();
		mergedOntology = managerMerged.createOntology(axioms, IRI.create("http://krono.act.uji.es/mappings/Integration.owl"));
		
		//System.out.println("Storing merged ontology: ");
		//managerMerged.saveOntology(mergedOntology, new RDFXMLOntologyFormat(), IRI.create("file:/usr/local/data/ConfOntosOAEI/cmt_cocus.owl")); //RDFXMLOntologyFormat
		
		
		
		LogOutput.printAlways("Number of classes integration: " + mergedOntology.getClassesInSignature().size());
	}
	
	
	private ReasonerAccess createMergedReasonerAccess(int reasoner_id, OWLOntology O1, OWLOntology O2, OWLOntology M, boolean useFactory) throws Exception{
		
		return ReasonerManager.getMergedOntologyReasoner(reasoner_id, O1, O2, M, useFactory);
		
	}
	
	
	private ReasonerAccess createMergedReasonerAccess(int reasoner_id, Set<OWLAxiom> O1, Set<OWLAxiom> O2, Set<OWLAxiom> M, boolean useFactory) throws Exception{
		
		return ReasonerManager.getMergedOntologyReasoner(reasoner_id, O1, O2, M, useFactory);
		
	}
	


	
	
	
	
	
	
	private void setNonFixedOntologies(){
		
		//setFMA2NCI();
		//setFMA2SNOMED();
		setSNOMED2NCI();
		mappingsOntologyFile = rootPath + pair_str + sufix + mappingstr;
		
		//moduleonto1file = "file:/usr/local/data/DataUMLS/UMLS_Onto_Versions/OAEI_datasets/fma2nci_dataset/oaei2012_FMA_big_overlapping_nci.owl";
		//moduleonto2file = "file:/usr/local/data/DataUMLS/UMLS_Onto_Versions/OAEI_datasets/fma2nci_dataset/oaei2012_NCI_big_overlapping_fma.owl";
		
		//moduleonto1file = "file:/usr/local/data/DataUMLS/UMLS_Onto_Versions/OAEI_datasets/fma2snmd_dataset/oaei2012_FMA_big_overlapping_snomed.owl";
		//moduleonto2file = "file:/usr/local/data/DataUMLS/UMLS_Onto_Versions/OAEI_datasets/fma2snmd_dataset/oaei2012_SNOMED_big_overlapping_fma.owl";
		
		
		moduleonto1file = "file:/usr/local/data/DataUMLS/UMLS_Onto_Versions/OAEI_datasets/fma2nci_dataset/oaei2012_FMA_whole_ontology.owl";
		//moduleonto2file = "file:/usr/local/data/DataUMLS/UMLS_Onto_Versions/OAEI_datasets/fma2nci_dataset/oaei2012_NCI_whole_ontology.owl";
		moduleonto2file = "file:/usr/local/data/DataUMLS/UMLS_Onto_Versions/OAEI_datasets/snomed20090131_replab.owl.zip";
		
		//mappingsOntologyFile = "file:/usr/local/data/DataUMLS/UMLS_Onto_Versions/OAEI_datasets/Mappings_Tools/oaei2012_FMA2NCI_voted_mappings.owl";
		//mappingsOntologyFile = "file:/usr/local/data/DataUMLS/UMLS_Onto_Versions/OAEI_datasets/OutputAlcomo/oaei2012_FMA2NCI_repaired_UMLS_mappings_alcomo2.owl";
		//mappingsOntologyFile = "file:/usr/local/data/DataUMLS/UMLS_Onto_Versions/OAEI_datasets/fma2nci_dataset/FMA2NCI_repaired_UMLS_mappings.owl";
		
		mappingsOntologyFile = "file:/usr/local/data/DataUMLS/UMLS_Onto_Versions/OAEI_datasets/OutputAlcomo/oaei2012_FMA2SNMD_repaired_UMLS_mappings_alcomo2.owl";
		
		//mappingsOntologyFile = "file:/usr/local/data/DataUMLS/UMLS_Onto_Versions/OAEI_datasets/fma2snmd_dataset/FMA2SNMD_repaired_UMLS_mappings.owl";
		
		
		//mappingsOntologyFile = "file:/usr/local/data/DataUMLS/UMLS_Onto_Versions/OAEI_datasets/snmd2nci_dataset/SNMD2NCI_repaired_UMLS_mappings.owl";
		
		
		//moduleonto1file = "file:/home/ernesto/ontologies/unsatOntology.owl";
		//moduleonto2file = "file:/home/ernesto/ontologies/unsatOntology.owl";
		//mappingsOntologyFile  = "file:/home/ernesto/ontologies/unsatOntology.owl";
		
		
		//mappingsOntologyFile = "file:/usr/local/data/DataUMLS/UMLS_Onto_Versions/Aroma_results/Aroma_FMA2NCI_wholeOntologies.owl";
		//moduleonto1file = "file:/usr/local/data/DataUMLS/UMLS_Onto_Versions/OAEI_datasets/fma2nci_dataset/oaei2012_FMA_whole_ontology.owl";
		//moduleonto2file = "file:/usr/local/data/DataUMLS/UMLS_Onto_Versions/OAEI_datasets/fma2nci_dataset/oaei2012_NCI_whole_ontology.owl";
		//moduleonto1file = "file:/usr/local/data/DataUMLS/UMLS_Onto_Versions/snomed20090131_replab.owl";
		//moduleonto2file = "file:/usr/local/data/DataUMLS/UMLS_Onto_Versions/OAEI_datasets/fma2nci_dataset/oaei2012_NCI_whole_ontology.owl";
		
		//moduleonto1file = "file:/usr/local/data/DataUMLS/UMLS_Onto_Versions/OAEI_datasets/snmd2nci_dataset/oaei2012_SNOMED_small_overlapping_nci.owl";
		//moduleonto2file = "file:/usr/local/data/DataUMLS/UMLS_Onto_Versions/OAEI_datasets/snmd2nci_dataset/oaei2012_NCI_small_overlapping_snomed.owl";
		
		String path = "/usr/local/data/DataUMLS/UMLS_Onto_Versions/OAEI_datasets/oaei_2013/";
		String irirootpath = "file:" + path;
		
		moduleonto1file = irirootpath + "oaei2013_SNOMED_extended_overlapping_fma_nci.owl";
		moduleonto2file = irirootpath + "oaei2013_NCI_whole_ontology.owl";
		mappingsOntologyFile = "file:/usr/local/data/DataUMLS/UMLS_Onto_Versions/LogMap2_Mappings/SNOMED2NCI_logmap2_Output/logmap2_mappings.owl";
		
	}
	
	private void setOntologies(){
		//setFMA2NCI();
		//setFMA2SNOMED();
		setSNOMED2NCI();
		//setNCI2LUCADA();
		//setSNOMED2LUCADA();
		
		
		mappingsOntologyFile = rootPath + pair_str + sufix + mappingstr;
		moduleonto1file = rootPath + pair_str + sufix + module1str;
		moduleonto2file = rootPath + pair_str + sufix + module2str;
	}
	
	
	
	
	
	private void loadOntologies() throws Exception {		

		managerOnto1 = OWLManager.createOWLOntologyManager();
		managerOnto2 = OWLManager.createOWLOntologyManager();
		managerMappings = OWLManager.createOWLOntologyManager();
		
		factory = managerOnto1.getOWLDataFactory();
	
		mappingsOntology = managerMappings.loadOntology(IRI.create(mappingsOntologyFile));
		
		moduleonto1 = managerOnto1.loadOntology(IRI.create(moduleonto1file));
		
		moduleonto2 = managerOnto2.loadOntology(IRI.create(moduleonto2file));
		
		
	 
	}
	
	
	public void clear(){
		reasonerAccess.clearStructures();
	}
	
	int sat_result = 0;
	int unknown = 0;
	int num_class = 0;
	
	private void evaluateClassSatisfiabilityTimeOut(OWLClass cls){
		
		num_class++;
		
		sat_result = reasonerAccess.isSatisfiable_withTimeout(cls, timeoutClassSatisfiabilityCheck);
		
		if (num_class==100 || num_class==5000 || num_class==10000 || 
				num_class==15000 || num_class==25000 || num_class==50000 || num_class==100000 || 
				num_class==150000 || num_class==200000  || num_class==250000 || num_class==300000 || num_class==350000)
			LogOutput.print("\t" + cls.getIRI().toString() + "-  Tested: " + num_class);
		
		
		if (sat_result == ReasonerAccess.UNSAT){
			unsat++;
			//System.out.println("\t" + cls.getIRI().toString() + "-  UNSAT: " + num_class);
		}
		else if (sat_result == ReasonerAccess.UNKNOWN){
			unknown++;
			LogOutput.print("\t" + cls.getIRI().toString() + "-  UNKNOWN SAT. Test: " + num_class);
		}
		else {
			//System.out.println("\t" + cls.getIRI().toString() + "-  SAT: " + num_class);
		}
		
	}
	
	private void evaluateClassSatisfiability(OWLClass cls){
		
		num_class++;		
				
		if (!reasonerAccess.isSatisfiable(cls)){
			unsat++;
			//LogOutput.printAlways(cls + "-  UNSAT");
		}		
		
	}
	
	
	private void checkSatisfiabilityIntegration() throws Exception{
		
		
		if (checkSatisfiabilityIndividually){
			
			unknown = 0;
			num_class = 0;
			unsat=0;
			
			if (useTimeOut){
				
				
				/*for (OWLClass cls : moduleonto1.getClassesInSignature(true)){
					evaluateClassSatisfiabilityTimeOut(cls);	
				}
				
				for (OWLClass cls : moduleonto2.getClassesInSignature(true)){
					evaluateClassSatisfiabilityTimeOut(cls);
				}*/
				
				
				LogOutput.printAlways("Checking satisfiability integration: ");
				for (OWLClass cls : reasonerAccess.getOntology().getClassesInSignature(true)){
					evaluateClassSatisfiabilityTimeOut(cls);
				}
				
				LogOutput.printAlways("\tUNSAT classes: " + unsat + " of " + num_class);
				LogOutput.printAlways("\tUNKNOWN classes: " + unknown + " of " + num_class);
				
				
			}
			else {
				for (OWLClass cls : moduleonto1.getClassesInSignature(true)){
					evaluateClassSatisfiability(cls);	
				}
				
				for (OWLClass cls : moduleonto2.getClassesInSignature(true)){
					evaluateClassSatisfiability(cls);
				}
				
				LogOutput.printAlways("UNSAT classes: " + unsat + " of " + num_class);
			}
			
			
		}
		else {
			if (useTimeOut){
				reasonerAccess.classifyOntology_withTimeout(timeoutClassification);//300-5min for small cases is enough
				//reasonerAccess.classifyOntology_withTimeout_throws_Exception(300);
			}
			else{
				reasonerAccess.classifyOntology();
			}
			
			if (reasonerAccess.isOntologyClassified()){
				unsat = reasonerAccess.getUnsatisfiableClasses().size();
				LogOutput.printAlways("UNSAT classes: " + unsat);
				if (unsat<10){ //TODO ontly a subset
					for (OWLClass cls : reasonerAccess.getUnsatisfiableClasses()){
						LogOutput.printAlways("\t" +cls.getIRI().toString());
					}
				}
			}
			else{
				LogOutput.printAlways("Timeout or error classigying ontology.");
			}
		}
	}
	
	
	
	
	
	
	
	
	private void setFMA2NCI(){
		pair_str ="FMA2NCI";		
	}
		
	private void setFMA2SNOMED(){
		pair_str ="FMA2SNOMED";
	}
	
	private void setSNOMED2NCI(){
		pair_str ="SNOMED2NCI";
	}

	private void setNCI2LUCADA(){
		pair_str ="NCI2LUCADA";
	}

	private void setSNOMED2LUCADA(){
		pair_str ="SNOMED2LUCADA";
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		try{
			new SatisfiabilityIntegration(false);
		
		}
		catch (Exception e){
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

}

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
package uk.ac.ox.krr.logmap2.io;

import org.semanticweb.owlapi.apibinding.OWLManager;

import org.semanticweb.owlapi.model.*;


import uk.ac.ox.krr.logmap2.utilities.Utilities;

/**
 * 
 * This class transforms mappings files in OWL format to the OAEI RDF Alignment
 * 
 * @author Ernesto Jimenez-Ruiz
 *
 */
public class OWL2RDFAlignment {

	protected OWLOntologyManager managerOnto;
	protected OWLOntology onto;
	
	protected String iri_onto1;
	protected String iri_onto2;
	
	protected double conf = 100.0;
	
	
	OutPutFilesManager outPutFilesManager = new OutPutFilesManager();
	
	
	public OWL2RDFAlignment(
			String owl_file_path,
			String oaei_file_path,
			String iri_onto1,
			String iri_onto2) throws Exception {
		
		
		this.iri_onto1 = iri_onto1;
		this.iri_onto2 = iri_onto2;
		
		
		
		
		outPutFilesManager.createOutFiles(
				oaei_file_path, 
				OutPutFilesManager.OAEIFormat,
				iri_onto1,
				iri_onto2);
		
		System.out.println("Load ontology: ");
		loadOWLMappings(owl_file_path);
		
		outPutFilesManager.closeAndSaveFiles();
		
	}
	
	
	private void setSilentMissingImportStrategy() {
		//In case an import is broken
		OWLOntologyLoaderConfiguration config = new OWLOntologyLoaderConfiguration();
		config.setMissingImportHandlingStrategy(MissingImportHandlingStrategy.SILENT);
		managerOnto.setOntologyLoaderConfiguration(config);
	}
	
	
	private void loadOWLMappings(String phy_iri_onto) throws Exception{
		
		managerOnto = OWLManager.createOWLOntologyManager();

		//If import cannot be loaded
		setSilentMissingImportStrategy();
		
		onto = managerOnto.loadOntology(IRI.create(phy_iri_onto));
		
		OWLAxiomVisitor mVisitor = new MappingVisitor();
		
		for (OWLAxiom ax : onto.getAxioms()){
			
			ax.accept(mVisitor);						
		}
		
		
		
	}
	
	
	
	
	
	
	/** 
	 * @param args
	 */	
	public static void main(String[] args) {
		//TODO 
		//System.out.println("Hola");
		//System.err.println("Hola");
		
		String pair_path;		
		String pair_path_out;
		
		String rootPath = "/usr/local/data/DataUMLS/UMLS_Onto_Versions/";
		String irirootPath = "file:/usr/local/data/DataUMLS/UMLS_Onto_Versions/";
		
		String fma = "http://bioontology.org/projects/ontologies/fma/fmaOwlDlComponent_2_0";
		String nci = "http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl";
		String snmd = "http://www.ihtsdo.org/snomed";

		
		try{
						
			//FMA 2 NCI
			pair_path = irirootPath + "FMA2NCI/UMLS_debugging/FMA2NCI_original_UMLS_mappings.owl";
			pair_path_out = rootPath + "FMA2NCI/UMLS_debugging/FMA2NCI_original_UMLS_mappings";
			new OWL2RDFAlignment(pair_path, pair_path_out, fma, nci);
			
			
			pair_path = irirootPath + "FMA2SNOMED/UMLS_debugging/FMA2SNMD_original_UMLS_mappings.owl";
			pair_path_out = rootPath + "FMA2SNOMED/UMLS_debugging/FMA2SNMD_original_UMLS_mappings";
			new OWL2RDFAlignment(pair_path, pair_path_out, fma, snmd);
			
			pair_path = irirootPath + "SNOMED2NCI/UMLS_debugging/SNMD2NCI_original_UMLS_mappings.owl";
			pair_path_out = rootPath + "SNOMED2NCI/UMLS_debugging/SNMD2NCI_original_UMLS_mappings";
			new OWL2RDFAlignment(pair_path, pair_path_out, snmd, nci);
			
			
			pair_path = irirootPath + "FMA2NCI/UMLS_debugging/FMA2NCI_repaired_UMLS_mappings.owl";
			pair_path_out = rootPath + "FMA2NCI/UMLS_debugging/FMA2NCI_repaired_UMLS_mappings";
			new OWL2RDFAlignment(pair_path, pair_path_out, fma, nci);
			
			pair_path = irirootPath + "FMA2SNOMED/UMLS_debugging/FMA2SNMD_repaired_UMLS_mappings.owl";
			pair_path_out = rootPath + "FMA2SNOMED/UMLS_debugging/FMA2SNMD_repaired_UMLS_mappings";
			new OWL2RDFAlignment(pair_path, pair_path_out, fma, snmd);
			
			pair_path = irirootPath + "SNOMED2NCI/UMLS_debugging/SNMD2NCI_repaired_UMLS_mappings.owl";		
			pair_path_out = rootPath + "SNOMED2NCI/UMLS_debugging/SNMD2NCI_repaired_UMLS_mappings";
			new OWL2RDFAlignment(pair_path, pair_path_out, snmd, nci);
			
		}
		catch (Exception e){
			e.printStackTrace();
		}
		
		
		

	}
	

	
	public class MappingVisitor implements OWLAxiomVisitor {
		
		
		
		private boolean isFromOnto1(String iri1){
			if (Utilities.getNameSpaceFromURI(iri1).equals(iri_onto1)){
				return true;
			}
			return false;
		}
		
		
		
		
		public void visit(OWLSubClassOfAxiom axiom) {
			 
			 String iri1 = axiom.getSubClass().asOWLClass().getIRI().toString();
			 String iri2 = axiom.getSuperClass().asOWLClass().getIRI().toString();
			 
			 try{
				 if (isFromOnto1(iri1)){				 
					 				 
					 outPutFilesManager.addClassMapping2Files(
						iri1,
						iri2,
						Utilities.L2R, 
				 		conf);
				 }
				 else {
					 outPutFilesManager.addClassMapping2Files(
						iri2,
						iri1,
						Utilities.R2L, 
						conf);
				 }
				 
			 }
			 catch (Exception e){
				 e.printStackTrace();
			 }
				 
			
		 }
		 
		
		 public void visit(OWLEquivalentClassesAxiom axiom) {
		    
			 
			 String iri1 = axiom.getClassExpressionsAsList().get(0).asOWLClass().getIRI().toString();
			 String iri2 = axiom.getClassExpressionsAsList().get(1).asOWLClass().getIRI().toString();
			 
			 try{
				 if (isFromOnto1(iri1)){				 
					 				 
					 outPutFilesManager.addClassMapping2Files(
						iri1,
						iri2,
						Utilities.EQ, 
				 		conf);
				 }
				 else {
					 outPutFilesManager.addClassMapping2Files(
						iri2,
						iri1,
						Utilities.EQ, 
						conf);
				 }
				 
			 }
			 catch (Exception e){
				 e.printStackTrace();
			 }
			 
		 }	
		 

		 public void visit(OWLEquivalentObjectPropertiesAxiom axiom) {

		 }

		 public void visit(OWLSubObjectPropertyOfAxiom axiom) {
		    
		 }

		 public void visit(OWLSubDataPropertyOfAxiom axiom) {
		   
		 }
		 
		 public void visit(OWLEquivalentDataPropertiesAxiom axiom) {
		    
		 }

		@Override
		public void visit(OWLAnnotationAssertionAxiom arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void visit(OWLSubAnnotationPropertyOfAxiom arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void visit(OWLAnnotationPropertyDomainAxiom arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void visit(OWLAnnotationPropertyRangeAxiom arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void visit(OWLDeclarationAxiom arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void visit(OWLNegativeObjectPropertyAssertionAxiom arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void visit(OWLAsymmetricObjectPropertyAxiom arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void visit(OWLReflexiveObjectPropertyAxiom arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void visit(OWLDisjointClassesAxiom arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void visit(OWLDataPropertyDomainAxiom arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void visit(OWLObjectPropertyDomainAxiom arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void visit(OWLNegativeDataPropertyAssertionAxiom arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void visit(OWLDifferentIndividualsAxiom arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void visit(OWLDisjointDataPropertiesAxiom arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void visit(OWLDisjointObjectPropertiesAxiom arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void visit(OWLObjectPropertyRangeAxiom arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void visit(OWLObjectPropertyAssertionAxiom arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void visit(OWLFunctionalObjectPropertyAxiom arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void visit(OWLDisjointUnionAxiom arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void visit(OWLSymmetricObjectPropertyAxiom arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void visit(OWLDataPropertyRangeAxiom arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void visit(OWLFunctionalDataPropertyAxiom arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void visit(OWLClassAssertionAxiom arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void visit(OWLDataPropertyAssertionAxiom arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void visit(OWLTransitiveObjectPropertyAxiom arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void visit(OWLIrreflexiveObjectPropertyAxiom arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void visit(OWLInverseFunctionalObjectPropertyAxiom arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void visit(OWLSameIndividualAxiom arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void visit(OWLSubPropertyChainOfAxiom arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void visit(OWLInverseObjectPropertiesAxiom arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void visit(OWLHasKeyAxiom arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void visit(OWLDatatypeDefinitionAxiom arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void visit(SWRLRule arg0) {
			// TODO Auto-generated method stub
			
		}



		
	}
	
	
}







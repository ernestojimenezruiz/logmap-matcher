package uk.ac.ox.krr.logmap2.test.oaei.interactivity;

import uk.ac.ox.krr.logmap2.test.oaei.OAEITask;

public class TestBioML_SNOMED_NCIT_Pharma_WithLocalOracle extends TestOAEITrackWithOracle{

	@Override
	protected void setUp() {
		
		boolean extended_questions = false;
		boolean llm_oracle = false;
		int error_rate = 20;

		setIputOutputFiles("bio-ml/snomed-ncit.pharm", "bioml-snomed-ncit.pharm",  extended_questions, llm_oracle, error_rate);
		
		
		
		tasks.add(
				new OAEITask(
						URI_PATH + "snomed.pharm.owl",  //source
						URI_PATH + "ncit.pharm.owl",  //target
						//URI_PATH + "reference.rdf",   //reference mappings if any
						URI_PATH + "refs_equiv/snomed-ncit.pharm-ref.rdf", //no reference
						"bioml-snomed-ncit-pharm"
				));
		
	}
	
	
	public static void main(String[] args){
		
		TestBioML_SNOMED_NCIT_Pharma_WithLocalOracle test = new TestBioML_SNOMED_NCIT_Pharma_WithLocalOracle();
		
		
		try {
			test.evaluateTasks();
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}


}

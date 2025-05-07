package uk.ac.ox.krr.logmap2.test.oaei.interactivity;

import uk.ac.ox.krr.logmap2.test.oaei.OAEITask;


public class TestLargeBio_SNOMED_NCI_WithLocalOracle extends TestOAEITrackWithOracle{

	@Override
	protected void setUp() {
		
		boolean extended_questions = false;
		boolean llm_oracle = false;
		int error_rate = 20;

		setIputOutputFiles("largebio/snomed-nci", "largebio-snomed-nci",  extended_questions, llm_oracle, error_rate);
		
		tasks.add(
				new OAEITask(
						//URI_PATH + "oaei_SNOMED_small_overlapping_nci.owl",  //source
						URI_PATH + "oaei_SNOMED_extended_overlapping_fma_nci.owl",
						//URI_PATH + "oaei_NCI_small_overlapping_snomed.owl",  //target
						URI_PATH + "oaei_NCI_whole_ontology.owl",
						URI_PATH + "oaei_SNOMED2NCI_UMLS_mappings_with_flagged_repairs.rdf",   //reference mappings if any
						//"", //no reference
						"largebio-snomed-nci"
				));
		
	}
	
	
	public static void main(String[] args){
		
		TestLargeBio_SNOMED_NCI_WithLocalOracle test = new TestLargeBio_SNOMED_NCI_WithLocalOracle();
		
		
		try {
			test.evaluateTasks();
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}


}

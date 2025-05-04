package uk.ac.ox.krr.logmap2.test.oaei.interactivity;

import uk.ac.ox.krr.logmap2.test.oaei.OAEITask;

public class TestLargeBio_FMA_NCI_WithLocalOracle extends TestOAEITrackWithOracle{

	@Override
	protected void setUp() {
		
		
		boolean extended_questions = false;		

		setIputOutputFiles("largebio/fma-nci", "largebio-fma-nci",  extended_questions);
		
				
		tasks.add(
				new OAEITask(
						//URI_PATH + "oaei_FMA_small_overlapping_nci.owl",  //source
						URI_PATH + "oaei_FMA_whole_ontology.owl",
						//URI_PATH + "oaei_NCI_small_overlapping_fma.owl",  //target
						URI_PATH + "oaei_NCI_whole_ontology.owl",
						URI_PATH + "oaei_FMA2NCI_UMLS_mappings_with_flagged_repairs.rdf",   //reference mappings if any
						//"", //no reference
						"largebio-fma-nci"
				));
		
	}
	
	
	public static void main(String[] args){
		
		TestLargeBio_FMA_NCI_WithLocalOracle test = new TestLargeBio_FMA_NCI_WithLocalOracle();
		
		
		try {
			test.evaluateTasks();
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}


}

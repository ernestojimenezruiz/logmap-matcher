package uk.ac.ox.krr.logmap2.test.oaei.interactivity;

import uk.ac.ox.krr.logmap2.test.oaei.OAEITask;

public class TestBioML_NCIT_DOID_WithLocalOracle extends TestOAEITrackWithOracle{

	@Override
	protected void setUp() {
		
		boolean extended_questions = true;		

		setIputOutputFiles("bio-ml/ncit-doid", "bioml-ncit-doid",  extended_questions);
		
		
		tasks.add(
				new OAEITask(
						URI_PATH + "ncit.owl",  //source
						URI_PATH + "doid.owl",  //target
						//uri_path + "reference.rdf",   //reference mappings if any
						URI_PATH + "refs_equiv/ncit-doid-ref.rdf", //no reference
						"bioml-ncit-doid"
				));
		
	}
	
	
	public static void main(String[] args){
		
		TestBioML_NCIT_DOID_WithLocalOracle test = new TestBioML_NCIT_DOID_WithLocalOracle();
		
		
		try {
			test.evaluateTasks();
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}


}

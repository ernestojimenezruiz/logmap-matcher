package uk.ac.ox.krr.logmap2.test.oaei.interactivity;


import uk.ac.ox.krr.logmap2.test.oaei.OAEITask;

public class TestBioML_OMIM_ORDO_WithLocalOracle extends TestOAEITrackWithOracle{
	
	

	@Override
	protected void setUp() {
		
		
		boolean extended_questions = true;		

		setIputOutputFiles("bio-ml/omim-ordo", "bioml-omim-ordo",  extended_questions);		
			 
				
		tasks.add(
				new OAEITask(
						URI_PATH + "omim.owl",  //source
						URI_PATH + "ordo.owl",  //target
						//uri_path + "reference.rdf",   //reference mappings if any
						URI_PATH + "refs_equiv/omim-ordo-ref.rdf", //no reference
						"bioml-omim-ordo"
				));
		/*tasks.add(
				new OAEITask(
						uri_path + "flat_ontology_food_terms_based_on_piyathilake_dataset.ttl",  //source
						uri_path + "foodon-merged.owl",  //target
						//uri_path + "AML-food.rdf",   //reference mappings if any
						"", //no reference
						"food-terms"
				));
		*/
	}
	
	
	public static void main(String[] args){
		
		TestBioML_OMIM_ORDO_WithLocalOracle test = new TestBioML_OMIM_ORDO_WithLocalOracle();
		
		
		try {
			test.evaluateTasks();
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}


}

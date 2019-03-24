/*******************************************************************************
 * Copyright 2018 by The Alan Turing Institute
 * 
 *******************************************************************************/
package uk.ac.ox.krr.logmap2.test.oaei;

import uk.ac.ox.krr.logmap2.Parameters;

/**
 *
 * @author ernesto
 * Created on 6 Sep 2018
 *
 */
public class TestPhenotypeTrack extends TestOAEITrack{

	public TestPhenotypeTrack(){
		super();
	}
	
	
	@Override
	protected void setUp() {
		
		//Current results
		//hp-mp		32883.0		3108	0.593	0.856	0.701
		//doid-ordo	14517.0		2323	0.927	0.814	0.867
		//Removing sam uri mappings
		//hp-mp		31155.0		2130	0.865	0.856	0.86
		//doid-ordo	14127.0		2323	0.927	0.814	0.867

		
		
		String uri_path = "file:/home/ejimenez-ruiz/Documents/OAEI_2018/Tracks/Phenotype/";
		
		tasks.add(
				new OAEITask(
						uri_path + "hp_noimports.owl",
						uri_path + "mp_noimports.owl",
						uri_path + "Consensus-3-hp-mp.rdf",
						"hp-mp"
				));
		
		tasks.add(
				new OAEITask(
						uri_path + "doid_noimports.owl",
						uri_path + "ordo.owl",
						uri_path + "Consensus-3-doid-ordo.rdf",
						"doid-ordo"
				));
		
		

		Parameters.output_class_mappings=true;
		Parameters.output_prop_mappings=true;
		
		
	}
	
	
	
	public static void main(String[] args){
		
		
		TestPhenotypeTrack test = new TestPhenotypeTrack();
		
		try {
			test.evaluateTasks();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}

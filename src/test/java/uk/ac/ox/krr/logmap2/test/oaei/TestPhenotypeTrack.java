/*******************************************************************************
 * Copyright 2018 by The Alan Turing Institute
 * 
 *******************************************************************************/
package uk.ac.ox.krr.logmap2.test.oaei;

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

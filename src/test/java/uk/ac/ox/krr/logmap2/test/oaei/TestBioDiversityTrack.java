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
public class TestBioDiversityTrack extends TestOAEITrack{

	public TestBioDiversityTrack(){
		super();
	}
	
	
	@Override
	protected void setUp() {
		
		
		//Added trait as stopword, as it was adding some noise in a few cases
		//Current results
		//envo-sweet	15174.0		583		0.839	0.738	0.785
		//flopo-pto		19181.0		236		0.814	0.787	0.8
		
		
		//String uri_path = "file:/home/ernesto/Documents/OAEI_2018/BioDIV/";
		String uri_path = "file:/home/ejimenez-ruiz/Documents/OAEI_2018/Tracks/BioDiv/";
		
		tasks.add(
				new OAEITask(
						uri_path + "envo.owl",
						uri_path + "sweet.owl",
						uri_path + "envo-sweet.rdf",
						"envo-sweet"
				));
		
		tasks.add(
				new OAEITask(
						uri_path + "flopo.owl",
						uri_path + "pto.owl",
						uri_path + "flopo-pto.rdf",
						"flopo-pto"
				));
		
	}
	
	
	
	public static void main(String[] args){
		
		TestBioDiversityTrack test = new TestBioDiversityTrack();
		
		try {
			test.evaluateTasks();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}

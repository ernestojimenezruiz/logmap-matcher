/*******************************************************************************
 * Copyright 2018 by The Alan Turing Institute
 * 
 *******************************************************************************/
package uk.ac.ox.krr.logmap2.test.oaei;

/**
 *
 * @author ernesto
 * Created on 7 Sep 2018
 *
 */
public class TestIIMBTrack extends TestOAEITrack{

	//2012 results: http://www.dit.unitn.it/~p2p/OM-2012/oaei12_paper0.pdf
	//In particular, test cases from 1 to 20 contain changes in data format
	//(misspelling, errors in text, etc); test cases 21 to 40 contain changes in structure (proper-
	//ties missing, RDF triples changed); 41 to 60 contain logical changes (class membership
	//changed, logical errors); finally, test cases 61 to 80 contain a mix of the previous.
	
	
	
	static final int SANDBOX=0;
	static final int IIMB=1;
	static final int IIMB_LARGE=2;
	public static int test_case;
	
	int num_tasks;
	String uri_path;
	String ontoName;
	
	
	
	
	
	
	
	public TestIIMBTrack(){
		super();	
	}
	
	
	
	@Override
	protected void setUp() {
		
		//init
		initStructures();
		
		//create tasks
		String taskName;
		
		for (int folder=1; folder<=num_tasks;folder++){//instance
			
			taskName = convert2ThreeDigitStrNumber(folder);
			
			tasks.add(
					new OAEITask(
							uri_path + ontoName,
							uri_path + taskName + "/" + ontoName,
							uri_path + taskName + "/" + "refalign.rdf",
							taskName
					));
		}
		
		
		
	}
	
	
	
	
	private void initStructures(){
		
		if (test_case==SANDBOX){
			num_tasks=11;
			uri_path = "file:/home/ernesto/Documents/OAEI_2018/iimb/sandbox/";
			ontoName="sandbox.owl";			
		}
		else if (test_case==IIMB){
			num_tasks=80;
			uri_path = "file:/home/ernesto/Documents/OAEI_2018/iimb/iimb/";
			ontoName="onto.owl";
		}
		else if (test_case==IIMB_LARGE){
			num_tasks=80;
			uri_path = "file:/home/ernesto/Documents/OAEI_2018/iimb/iimb_large/";
			ontoName="onto.owl";
		}
	}

	
	
	private String convert2ThreeDigitStrNumber(int number){
		
		String three_digits = String.valueOf(number);
		
		if (three_digits.length()==1)
			three_digits="00" + three_digits;
		else if (three_digits.length()==2)
			three_digits="0" + three_digits;
		
		return three_digits;
		
	}
	
	
	
	public static void main(String[] args) {
	
		//test_case = SANDBOX;
		test_case = IIMB;
		//test_case = IIMB_LARGE;
		
		TestIIMBTrack test = new TestIIMBTrack();
		
		try {
			test.evaluateTasks();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}

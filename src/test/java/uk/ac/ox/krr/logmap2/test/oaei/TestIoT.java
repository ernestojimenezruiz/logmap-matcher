package uk.ac.ox.krr.logmap2.test.oaei;

public class TestIoT extends TestOAEITrack{

	public TestIoT(){
		super();
		
	}

	public TestIoT(String output_folder_mappings){
		//super();
		super(output_folder_mappings); //with partial output path to save mappings
		
	}
	
	@Override
	protected void setUp() {
		// TODO Auto-generated method stub
		tasks.add(
				new OAEITask(
						"http://sensormeasurement.appspot.com/m3#",
						"http://purl.org/iot/ontology/fiesta-iot#",
						"",
						"m3-fiesta"
				));
		
		tasks.add(
				new OAEITask(
						"http://ontology.tno.nl/saref.ttl",
						"https://saref.etsi.org/core/v3.1.1/saref.rdf",
						"",
						"oldsaref-saref"
				));
		
	} 

	
public static void main(String[] args){
		
		TestIoT test = new TestIoT("/tmp/logmap-alignment"); //with partial output path to save mappings
		
		try {
			test.evaluateTasks();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	
}

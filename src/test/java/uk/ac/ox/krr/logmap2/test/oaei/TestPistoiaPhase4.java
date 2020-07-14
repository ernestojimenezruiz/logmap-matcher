package uk.ac.ox.krr.logmap2.test.oaei;

import uk.ac.ox.krr.logmap2.Parameters;

public class TestPistoiaPhase4 extends TestOAEITrack{

	public TestPistoiaPhase4(){
		super();
	}
	
	
	
	@Override
	protected void setUp() {
		
		String uri_path_ontos = "file:/home/ernesto/Documents/BioPortal/Ontologies/";
		String uri_path_mappings = "file:/home/ernesto/Documents/BioPortal/Mappings/";
		
		
		tasks.add(
				new OAEITask(
						uri_path_ontos + "EFO.owl",
						uri_path_ontos + "MESH.ttl",
						uri_path_mappings + "EFO_MESH.rdf",
						"efo-mesh"
				));
		
		
		

		Parameters.output_class_mappings=true;
		//Parameters.output_prop_mappings=true;
		
		Parameters.print_output=true;
		Parameters.print_output_always=true;
		
	}
	
	
	public static void main(String[] args){
		
		
		TestPistoiaPhase4 test = new TestPistoiaPhase4();
		
		try {
			test.evaluateTasks();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	

}

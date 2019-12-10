package uk.ac.ox.krr.logmap2.test.oaei;

import uk.ac.ox.krr.logmap2.Parameters;

public class TestNIVA  extends TestOAEITrack{

	public TestNIVA(){
		super();
		
		Parameters.perform_instance_matching = true;
		Parameters.output_instance_mappings = true;
		
		Parameters.print_output=true;
		Parameters.print_output_always=true;
		Parameters.use_overlapping=true;
		
	}
	
	
	@Override
	protected void setUp() {
		
		
		String uri_path = "file:/home/ernesto/Documents/NIVA/";
		
		/*tasks.add(
				new OAEITask(
						uri_path + "reduced_ecotox.nt",
						uri_path + "reduced_taxonomy.nt",
						uri_path + "AML-ecotox-ncbi-reduced.rdf",
						"ecotox-ncbi"
				));
		*/
		tasks.add(
				new OAEITask(
						uri_path + "ecotox.nt",
						uri_path + "reduced_taxonomy0.nt",
						uri_path + "AML-ecotox-ncbi-reduced.rdf",
						"ecotox-ncbi-0"
				));
		
		tasks.add(
				new OAEITask(
						uri_path + "ecotox.nt",
						uri_path + "reduced_taxonomy1.nt",
						uri_path + "AML-ecotox-ncbi-reduced.rdf",
						"ecotox-ncbi-1"
				));
		
		tasks.add(
				new OAEITask(
						uri_path + "ecotox.nt",
						uri_path + "reduced_taxonomy2.nt",
						uri_path + "AML-ecotox-ncbi-reduced.rdf",
						"ecotox-ncbi-2"
				));
		
		tasks.add(
				new OAEITask(
						uri_path + "ecotox.nt",
						uri_path + "reduced_taxonomy3.nt",
						uri_path + "AML-ecotox-ncbi-reduced.rdf",
						"ecotox-ncbi-3"
				));
		
		tasks.add(
				new OAEITask(
						uri_path + "ecotox.nt",
						uri_path + "reduced_taxonomy4.nt",
						uri_path + "AML-ecotox-ncbi-reduced.rdf",
						"ecotox-ncbi-4"
				));
		
		tasks.add(
				new OAEITask(
						uri_path + "ecotox.nt",
						uri_path + "reduced_taxonomy5.nt",
						uri_path + "AML-ecotox-ncbi-reduced.rdf",
						"ecotox-ncbi-5"
				));
		
		tasks.add(
				new OAEITask(
						uri_path + "ecotox.nt",
						uri_path + "reduced_taxonomy6.nt",
						uri_path + "AML-ecotox-ncbi-reduced.rdf",
						"ecotox-ncbi-6"
				));
		
		tasks.add(
				new OAEITask(
						uri_path + "ecotox.nt",
						uri_path + "reduced_taxonomy7.nt",
						uri_path + "AML-ecotox-ncbi-reduced.rdf",
						"ecotox-ncbi-7"
				));
		
		tasks.add(
				new OAEITask(
						uri_path + "ecotox.nt",
						uri_path + "reduced_taxonomy8.nt",
						uri_path + "AML-ecotox-ncbi-reduced.rdf",
						"ecotox-ncbi-8"
				));
		
		tasks.add(
				new OAEITask(
						uri_path + "ecotox.nt",
						uri_path + "reduced_taxonomy9.nt",
						uri_path + "AML-ecotox-ncbi-reduced.rdf",
						"ecotox-ncbi-9"
				));
		
		tasks.add(
				new OAEITask(
						uri_path + "ecotox.nt",
						uri_path + "reduced_taxonomy10.nt",
						uri_path + "AML-ecotox-ncbi-reduced.rdf",
						"ecotox-ncbi-10"
				));
		
		tasks.add(
				new OAEITask(
						uri_path + "ecotox.nt",
						uri_path + "reduced_taxonomy11.nt",
						uri_path + "AML-ecotox-ncbi-reduced.rdf",
						"ecotox-ncbi-11"
				));
		
	}
	
	
public static void main(String[] args){
		
		TestNIVA test = new TestNIVA();
		
		try {
			test.evaluateTasks();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	

}

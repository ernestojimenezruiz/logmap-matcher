/*******************************************************************************
 * Copyright 2018 by The Alan Turing Institute
 * 
 *******************************************************************************/
package uk.ac.ox.krr.logmap2.test.oaei;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ernesto
 * Created on 7 Sep 2018
 *
 */
public class CreateIIMBSEALS { //extends TestOAEITrack{

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
	String path;
	String path_seals;
	String uri_path;
	String ontoName;
	
	
	
	List<String> tasks_names = new ArrayList<String>();
	List<String> target_ontologies = new ArrayList<String>();
	List<String> references = new ArrayList<String>();
	
	
	
	
	
	public CreateIIMBSEALS(){
		//super();	
		
	
		
		setUp();
		
		printHeader();
		printSuits();
		printSuit();
		printOntologies();
		printReferences();
		printFooter();
	}
	
	
	
	protected void setUp() {
		
		//init
		initStructures();
		
		//create tasks
		String taskName;
		
		//File ontoS = new File(path + ontoName+".owl");
		//ontoS.renameTo(new File(path_seals+ontoName+".owl"));
		
		for (int folder=1; folder<=num_tasks;folder++){//instance
			
			taskName = convert2ThreeDigitStrNumber(folder);
			
			tasks_names.add(taskName);
			target_ontologies.add(taskName + "-target");
			references.add(taskName + "-ref");
			
			//File ontoT = new File(path + taskName + "/" + ontoName);
			//ontoT.renameTo(new File(path_seals + taskName + ".owl"));
			
			//File ref = new File(path + taskName + "/" + "refalign.rdf");
			//ref.renameTo(new File(path_seals + taskName + "-ref.rdf"));
			
			
			/*tasks.add(
					new OAEITask(
							uri_path + ontoName,
							uri_path + taskName + "/" + ontoName,
							uri_path + taskName + "/" + "refalign.rdf",
							taskName
					));
					*/
		}
		
		
		
	}
	
	
	private void printHeader() {
		System.out.println("<rdf:RDF");
		System.out.println("\t xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"");
		System.out.println("\t xmlns:seals=\"http://www.seals-project.eu/ontologies/SEALSMetadata.owl#\"");
		System.out.println("\t xmlns:dc=\"http://purl.org/dc/terms/\" >"); 
	}
	
	
	String uri_test = "http://www.seals-project.eu/alignment/IIMBSuiteTest";
	
	private void printSuits(){
	
		System.out.println("");
		
		System.out.println("\t<rdf:Description rdf:about=\""+ uri_test + "\">");
		System.out.println("\t\t<rdf:type rdf:resource=\"http://www.seals-project.eu/ontologies/SEALSMetadata.owl#Suite\"/>");
		
		for (String task : tasks_names)
			System.out.println("\t\t <seals:hasSuiteItem rdf:resource=\""+ uri_test + "#" + task + "\"/>");
		
		System.out.println("\t</rdf:Description>");
		
	}
	
	
	
	private void printSuit(){
		
		//for (String task : tasks_names) {
		for (int i=0; i<tasks_names.size(); i++) {
		
			System.out.println("");
			System.out.println("\t<rdf:Description rdf:about=\""+ uri_test + "#" + tasks_names.get(i)  + "\">");
		
			System.out.println("\t\t<rdf:type rdf:resource=\"http://www.seals-project.eu/ontologies/SEALSMetadata.owl#SuiteItem\"/>");
			
			System.out.println("\t\t<seals:hasDataItem rdf:resource=\"" + uri_test + "#" + ontoName + "-source" + "\"/>");
			System.out.println("\t\t<seals:hasDataItem rdf:resource=\"" + uri_test + "#" + target_ontologies.get(i) + "\"/>");
			System.out.println("\t\t<seals:hasDataItem rdf:resource=\"" + uri_test + "#" + references.get(i) + "\"/>");

			System.out.println("\t\t<dc:identifier>" + tasks_names.get(i) + "</dc:identifier>");
			System.out.println("\t</rdf:Description>");
			
		}
		
		
		
		
	}
	
	private void printOntologies(){
		
		System.out.println("");
		
		System.out.println("\t<rdf:Description rdf:about=\""+ uri_test + "#" + ontoName + "-source"  + "\">");
		System.out.println("\t\t<rdf:type rdf:resource=\"http://www.seals-project.eu/ontologies/SEALSMetadata.owl#DataItem\"/>");
		System.out.println("\t\t<seals:hasComponentType>source</seals:hasComponentType>");
		System.out.println("\t\t<seals:isLocatedAt>"+ ontoName +".owl" + "</seals:isLocatedAt>");
		System.out.println("\t\t<dc:identifier>" + ontoName + "-source" + "</dc:identifier>");
		System.out.println("\t</rdf:Description>");
		
		for (int i=0; i<tasks_names.size(); i++) {
			
			System.out.println("");
			
			System.out.println("\t<rdf:Description rdf:about=\""+ uri_test + "#" + target_ontologies.get(i)  + "\">");
			System.out.println("\t\t<rdf:type rdf:resource=\"http://www.seals-project.eu/ontologies/SEALSMetadata.owl#DataItem\"/>");
			System.out.println("\t\t<seals:hasComponentType>target</seals:hasComponentType>");
			System.out.println("\t\t<seals:isLocatedAt>"+ tasks_names.get(i) +".owl" + "</seals:isLocatedAt>");
			System.out.println("\t\t<dc:identifier>" + target_ontologies.get(i) + "</dc:identifier>");
			System.out.println("\t</rdf:Description>");
			
		}
		
		
	}
	
	
	private void printReferences(){
		
		for (int i=0; i<references.size(); i++) {
			System.out.println("");
			
			System.out.println("\t<rdf:Description rdf:about=\""+ uri_test + "#" + references.get(i)  + "\">");
			System.out.println("\t\t<rdf:type rdf:resource=\"http://www.seals-project.eu/ontologies/SEALSMetadata.owl#DataItem\"/>");
			System.out.println("\t\t<seals:hasComponentType>reference</seals:hasComponentType>");
			System.out.println("\t\t<seals:isLocatedAt>"+ references.get(i) +".rdf" + "</seals:isLocatedAt>");
			System.out.println("\t\t<dc:identifier>" + references.get(i) + "</dc:identifier>");
			System.out.println("\t</rdf:Description>");
			
		}
		
	
		
	}
	
	
	private void printFooter() {
		System.out.println("</rdf:RDF>");
	}
	
	
	
	
	
	private void initStructures(){
		
		if (test_case==SANDBOX){
			num_tasks=11;
			uri_path = "file:/home/ernesto/Documents/OAEI_2018/iimb/sandbox/";
			path_seals ="/home/ejimenez-ruiz/Documents/OAEI_2018/Tracks/IIMB/sandbox_seals/";
			ontoName="sandbox";			
		}
		else if (test_case==IIMB){
			num_tasks=80;
			path ="/home/ejimenez-ruiz/Documents/OAEI_2018/Tracks/IIMB/iimb/";
			path_seals ="/home/ejimenez-ruiz/Documents/OAEI_2018/Tracks/IIMB/iimb_seals/";
			uri_path = "file:" + path;
			ontoName="onto";
		}
		//else if (test_case==IIMB_LARGE){
		//	num_tasks=80;
		//	uri_path = "file:/home/ernesto/Documents/OAEI_2018/iimb/iimb_large/";
		//	ontoName="onto.owl";
		//}
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
		
		CreateIIMBSEALS test = new CreateIIMBSEALS();
		
		try {
			//test.evaluateTasks();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}

package uk.ac.ox.krr.logmap2.test.overlapping;

import java.util.List;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import uk.ac.ox.krr.logmap2.Parameters;
import uk.ac.ox.krr.logmap2.division.MatchingTask;
import uk.ac.ox.krr.logmap2.division.OverlappingEstimation;

public class TestOverlappingOverestimation {

	
	
	public TestOverlappingOverestimation(String onto1, String onto2, String output_path) throws OWLOntologyCreationException, Exception {
		
		
		OverlappingEstimation overlapping = new OverlappingEstimation(); 
	
	
		List<MatchingTask> tasks = overlapping.createPartitionedMatchingTasks(onto1, onto2);
		
	
		//There is only one in this case
		for (MatchingTask task : tasks) {
			
			task.saveMatchingTask(output_path);
			
		}
	}
	
	
	
	
	public static void main(String[] args) {
		
		String path = "/home/ernesto/Documents/SNOMED-CT/";

		String cometa = "file:" + path + "cometa_labels_onto_reduced.ttl";
		String snomed = "file:" + path + "snomedct-201907.owl";
		
	
		try {
			new TestOverlappingOverestimation(cometa, snomed, path);
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	
}

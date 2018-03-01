/**
 * 
 */
package uk.ac.ox.krr.logmap2.partitioning;

import java.util.List;

import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

/**
 * 
 * This is an abstract class to define the main methods to be implemented by a partitioning algorithm specialised in 
 * Ontology Alignment. The partitioning algorithm will receive as input two ontologies and will output a number of sub-matching tasks.
 * A MatchingTask is composed by two subontologies or modules of the input ontologies.     
 * @author ernesto
 *
 */
public abstract class OntologyAlignmentPartitioning {
	
	
	public abstract List<MatchingTask> createPartitionedMatchingTasks(OWLOntology source, OWLOntology target, int num_tasks) throws OWLOntologyCreationException, Exception;
	
	
	public abstract List<MatchingTask> createPartitionedMatchingTasks(String sourceIRIStr, String targetIRIStr, int num_tasks) throws OWLOntologyCreationException, Exception;
	
	

}

package uk.ac.ox.krr.logmap2.division;

import java.util.List;

import org.semanticweb.owlapi.io.IRIDocumentSource;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.MissingImportHandlingStrategy;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyLoaderConfiguration;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import uk.ac.ox.krr.logmap2.owlapi.SynchronizedOWLManager;

/**
 * 
 * This is an abstract class to define the main methods to be implemented by a partitioning algorithm specialised in 
 * Ontology Alignment. The partitioning algorithm will receive as input two ontologies and will output a number of sub-matching tasks.
 * A MatchingTask is composed by two subontologies or modules of the input ontologies.     
 * @author ernesto
 *
 */
public interface OntologyAlignmentDivision {
	
	
	public List<MatchingTask> createPartitionedMatchingTasks(OWLOntology source, OWLOntology target) throws OWLOntologyCreationException, Exception;
	
	
	public List<MatchingTask> createPartitionedMatchingTasks(String sourceIRIStr, String targetIRIStr) throws OWLOntologyCreationException, Exception;
	
	
	

}

package uk.ac.ox.krr.logmap2.partitioning;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import uk.ac.ox.krr.logmap2.Parameters;
import uk.ac.ox.krr.logmap2.lexicon.LexicalUtilities;
import uk.ac.ox.krr.logmap2.overlapping.LexicalOverlappingExtractor;

/**
 * This class extract an overestimation of the overlapping of the input ontologies. Given two input ontologies it returns a matching task 
 * composed by a overestimated module 1 from ontology 1 (source) and a overestimated module 2 from ontology 2 (target) 
 * representing an overestimated overlapping of the input ontologies
 * @author ernesto
 *
 */
public class OverlappingEstimation extends  OntologyAlignmentPartitioning{

	@Override
	public Set<MatchingTask> createPartitionedMatchingTasks(OWLOntology source, OWLOntology target) throws OWLOntologyCreationException, Exception {
		
		Parameters.use_overlapping=true;
		Parameters.min_size_overlapping=0;
		
		LexicalUtilities lexicalUtilities = new LexicalUtilities();
		
		LexicalOverlappingExtractor overlappingExtractor = new LexicalOverlappingExtractor(lexicalUtilities, true);
		
		
		overlappingExtractor.createOverlapping(source, target);
		
		MatchingTask task = new MatchingTask();
		
		task.setSourceOntology(overlappingExtractor.getOverlappingOnto1());
		task.setTargetOntology(overlappingExtractor.getOverlappingOnto2());
		
		Set<MatchingTask> tasks = new HashSet<MatchingTask>();
		
		return tasks;
	}

	@Override
	public Set<MatchingTask> createPartitionedMatchingTasks(
			String sourceIRIStr, String targetIRIStr)
			throws OWLOntologyCreationException, Exception {
		
		Parameters.use_overlapping=true;
		Parameters.min_size_overlapping=0;
		
		LexicalUtilities lexicalUtilities = new LexicalUtilities();
		
		LexicalOverlappingExtractor overlappingExtractor = new LexicalOverlappingExtractor(lexicalUtilities, true);
		
		
		overlappingExtractor.createOverlapping(sourceIRIStr, targetIRIStr);
		
		MatchingTask task = new MatchingTask();
		
		task.setSourceOntology(overlappingExtractor.getOverlappingOnto1());
		task.setTargetOntology(overlappingExtractor.getOverlappingOnto2());
		
		Set<MatchingTask> tasks = new HashSet<MatchingTask>();
		
		return tasks;
	}
	

}

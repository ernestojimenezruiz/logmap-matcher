package uk.ac.ox.krr.logmap2;

/**
 * LogMapLLM-Bio: extension of LogMapBio with LLM functionalities to select composed mappings
 * @author Ernesto
 */
public class LogMapLLM_BioPortal {
	
	
	/*
	 * Algorithm idea:
	 * 
	 * 1. Run LogMapLLm on input ontologies + input from Oracle/LLM    (LLM Input)
	 * 2. Mediating ontology matching
	 * - Extraction of MOs
	 * 		- Local pipeline option with pre-downloaded ontologies (top-10, top-15)
	 * 		- Connection to BioPortal for extraction
	 * - Perform matching with (only) LogMap to get composed mappings (repair could be switched off if necessary)
	 * - Select(a)/Ask(b) fraction of mappings to ask the LLM      (LLM Input)
	 * - Perform final repair
	 * 3. Extract statistics of contribution per MO, and mapping with different votes and similarity. Sensitivity/Specificity on composed mappings to ask
	 * 4. Compute final P&R
	 * 
	 */
	

}

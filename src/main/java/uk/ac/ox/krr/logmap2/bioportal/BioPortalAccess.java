package uk.ac.ox.krr.logmap2.bioportal;

import java.util.Set;

public interface BioPortalAccess {


	public boolean isActive();
	
	public Set<String> getSynonyms4Label(String label);
	
	public Set<String> getSuitableOntologiesForLabels(Set<String> labels);
	
}

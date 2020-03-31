package uk.ac.ox.krr.logmap2.utilities;


/**
 * A Mapping Relation between two Ontology entities.
 * 
 * @author Daniel Faria, Dominique Ritze
 * Extracted from Extracted from eu.sealsproject.omt.client
 */

public enum Relation
{
    EQUIVALENCE ("="),
    SUBSUMES (">"),
    SUBSUMED_BY ("<"),
    UNKNOWN ("?");
    
    private String label;
    
    private Relation(String l)
    {
    	label = l;
    }
    
    public Relation reverse()
    {
    	switch(this)
    	{
    		case EQUIVALENCE: return EQUIVALENCE; 
    		case SUBSUMES: return SUBSUMED_BY;
    		case SUBSUMED_BY: return SUBSUMES;
    		case UNKNOWN: return UNKNOWN;
    		default: return null;
    	}
    }
    
    public static Relation parse(String l)
    {
    	for(Relation r : Relation.values())
    		if(l.equals(r.label))
    			return r;
   		return null;
    }
    
    public String toString()
    {
    	return label;
    }
}
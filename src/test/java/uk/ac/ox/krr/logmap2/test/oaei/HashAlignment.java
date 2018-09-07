package uk.ac.ox.krr.logmap2.test.oaei;

import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import uk.ac.ox.krr.logmap2.mappings.objects.MappingObjectStr;


/**
 * A HashMap-based Alignment representation with enables virtually
 * instantaneous searching for mappings, and linear-time Alignment
 * evaluation: 
 * Extracted from eu.sealsproject.omt.client
 * 
 * @author Daniel Faria
 */

public class HashAlignment
{
	private HashMap<String,HashMap<String,Vector<Relation>>> alignment;
	private int size;
	
	/**
	 * Constructs a new empty HashAlignment
	 */
	public HashAlignment()
	{
		alignment = new HashMap<String,HashMap<String,Vector<Relation>>>();
		size = 0;
	}
	
	
	
	/**
	 * Constructs a HashAlignment that is a copy of the given HashAlignment
	 * @param other: the HashAlignment to copy
	 */
	public HashAlignment(HashAlignment other)
	{
		this();
		for(String source : other.alignment.keySet())
			for(String target : other.alignment.get(source).keySet())
				for(Relation r : other.alignment.get(source).get(target))
					add(source, target, r);
	}
	
	/**
	 * Constructs a HashAlignment from as Set of MappingObjectStr
	 * 
	 */
	public HashAlignment(Set<MappingObjectStr> mappings)
	{
		this();
		for(MappingObjectStr mapping : mappings){
			
			if (mapping.getMappingDirection()==MappingObjectStr.EQ){
				add(mapping.getIRIStrEnt1(), mapping.getIRIStrEnt2(), Relation.EQUIVALENCE);
			}
			else if (mapping.getMappingDirection()==MappingObjectStr.SUB){
				add(mapping.getIRIStrEnt1(), mapping.getIRIStrEnt2(), Relation.SUBSUMED_BY);
			}
			else if (mapping.getMappingDirection()==MappingObjectStr.SUP){
				add(mapping.getIRIStrEnt1(), mapping.getIRIStrEnt2(), Relation.SUBSUMES);
			}
			else{
				add(mapping.getIRIStrEnt1(), mapping.getIRIStrEnt2(), Relation.UNKNOWN);
			}
			
			
			
		}
				
					
	}

	/**
	 * Adds a mapping to the HashAlignment if it is new (i.e., the HashAlignment
	 * doesn't contain the mapping or its reverse)
	 * @param uri1: the URI of the source ontology entity
	 * @param uri2: the URI of the target ontology entity
	 * @param r: the mapping relation between the entities
	 */
	public void add(String uri1, String uri2, Relation r)
	{
		//If the mapping already exists in the HashAlignment, return
		if(this.contains(uri1, uri2, r))
			return;
		//Otherwise, we can increment the size and add the mapping
		size++;
		//If the classes are already mapped, add the new relation
		if(this.contains(uri1, uri2))
			alignment.get(uri1).get(uri2).add(r);
		else if(this.contains(uri2, uri1))
			alignment.get(uri2).get(uri1).add(r.reverse());
		//Otherwise
		else
		{
			//We can create a new list with the relation
			Vector<Relation> rel = new Vector<Relation>();
			rel.add(r);
			//If the source class is in the alignment, we just update its HashMap
			if(alignment.containsKey(uri1))
				//by adding the target and the relation list
				alignment.get(uri1).put(uri2, rel);
			//Otherwise, we need to create a new HashMap for the source
			else
			{
				HashMap<String,Vector<Relation>> map = new HashMap<String,Vector<Relation>>();
				//update it with the relation
				map.put(uri2, rel);
				//and add it to the alignment
				alignment.put(uri1, map);
			}
		}
	}
	
	/**
	 * Adds all mappings in the given HashAlignment to this
	 * @param other: the HashAlignment to add to this
	 */
	public void add(HashAlignment other)
	{
		for(String source : other.alignment.keySet())
			for(String target : other.alignment.get(source).keySet())
				for(Relation r : other.alignment.get(source).get(target))
					add(source, target, r);
	}
	
	/**
	 * Checks if two entities are mapped in the HashAlignment
	 * regardless of relation or direction
	 * @param uri1: the URI of the source ontology entity
	 * @param uri2: the URI of the target ontology entity
	 * @param r: the mapping relation between the entities
	 * @return whether uri1 and uri2 are mapped in the HashAlignment
	 */
	public boolean contains(String uri1, String uri2)
	{
		return  alignment.containsKey(uri1) && alignment.get(uri1).containsKey(uri2);
	}
	
	/**
	 * Checks if a given mapping is contained in the HashAlignment
	 * @param uri1: the URI of the source ontology entity
	 * @param uri2: the URI of the target ontology entity
	 * @param r: the mapping relation between the entities
	 * @return whether the mapping is containing in the HashAlignment
	 */
	public boolean contains(String uri1, String uri2, Relation r)
	{
		return (alignment.containsKey(uri1) && alignment.get(uri1).containsKey(uri2) && alignment.get(uri1).get(uri2).contains(r)) ||
				(alignment.containsKey(uri2) &&	alignment.get(uri2).containsKey(uri1) && alignment.get(uri2).get(uri1).contains(r.reverse()));
	}
	
	/**
	 * @param other: the HashAlignment to evaluate with this
	 * @return the classification of the other HashAlignment using this as
	 * a reference: {True Positives, False Positives, False Negatives}
	 */
	public int[] evaluation(HashAlignment other)
	{
		//Evaluation matrix
		int[] eval = new int[3];
		//True Positives
		eval[0] = 0;
		//False Positives
		eval[1] = other.size();
		//False Negatives
		eval[2] = 0;
		//Iterate through this alignment
		for(String source : this.alignment.keySet())
		{
			for(String target : this.alignment.get(source).keySet())
			{
				//If there is an unknown relation between the classes, we ignore the mapping
				if(this.alignment.get(source).get(target).contains(Relation.UNKNOWN))
				{
					//And if it is in the other HashAlignment we discount
					//it, so it doesn't count as a False Positive
					if(other.contains(source, target))
						eval[1]--;
					continue;
				}
				//Otherwise, we check if the mapping with each relation (normally just one)
				//is in the other HashAlignment
				for(Relation r : this.alignment.get(source).get(target))
				{
					//If so, it is a True Positive
					if(other.contains(source, target, r))
						eval[0]++;
					//Otherwise, it is a False Negative
					else
						eval[2]++;
				}
			}
		}
		//The False Positives are simply given by the mappings in the other
		//HashAlignment (which we already counted) that aren't True Positives
		//or unknowns (which we already discounted)
		eval[1] -= eval[0];
		return eval;
	}

	
	/**
	 * @return the set of source ontology entity URIs in this alignment
	 */
	public Set<String> getSources()
	{
		return alignment.keySet();
	}
	
	/**
	 * @param source: the source ontology entity URI to retrieve
	 * @return the set of target ontology entity URIs aligned to
	 * the given entity in this alignment
	 */
	public Set<String> getTargets(String source)
	{
		if(alignment.containsKey(source))
			return alignment.get(source).keySet();
		return null;
	}
	
	/**
	 * @param source: the source ontology entity URI to retrieve
	 * @param target: the target ontology entity URI to retrieve
	 * @return the relation between the given entities
	 */
	public Vector<Relation> getRelations(String source, String target)
	{
		if(alignment.containsKey(source) && alignment.get(source).containsKey(target))
			return alignment.get(source).get(target);
		return null;
	}
	
	/**
	 * Removes a mapping from the HashAlignment if it exists
	 * @param uri1: the URI of the source ontology entity
	 * @param uri2: the URI of the target ontology entity
	 * @param r: the mapping relation between the entities
	 */
	public void remove(String uri1, String uri2, Relation r)
	{
		if(alignment.containsKey(uri1) && alignment.get(uri1).containsKey(uri2) && alignment.get(uri1).get(uri2).contains(r))
		{
			if(r != Relation.UNKNOWN)
				size--;
			alignment.get(uri1).get(uri2).remove(r);
			if(alignment.get(uri1).get(uri2).isEmpty())
				alignment.get(uri1).remove(uri2);
			if(alignment.get(uri1).isEmpty())
				alignment.remove(uri1);
		}
		else if(alignment.containsKey(uri2) && alignment.get(uri2).containsKey(uri1) &&	alignment.get(uri2).get(uri1).contains(r.reverse()))
		{
			if(r != Relation.UNKNOWN)
				size--;
			alignment.get(uri2).get(uri1).remove(r.reverse());
			if(alignment.get(uri2).get(uri1).isEmpty())
				alignment.get(uri2).remove(uri1);
			if(alignment.get(uri2).isEmpty())
				alignment.remove(uri2);
		}
	}
	
	/**
	 * @return the size of the HashAlignment
	 */
	public int size()
	{
		return size;
	}
}
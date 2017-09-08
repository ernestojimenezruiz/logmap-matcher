package uk.ac.ox.krr.logmap2.oaei.oracle;

//TODO
//Uncomment for OAEI
import eu.sealsproject.omt.client.interactive.Oracle;


import uk.ac.ox.krr.logmap2.io.LogOutput;


/**
 * Oracle interface with OAEI's oracle
 * @author ernesto
 *
 */
public class OAEIOracle extends Oraculo{

	
	@Override
	public boolean isMappingValid(String uri1, String uri2) {
		
		//Commend for OAEI
		//return false;
		
		
		//TODO
		///Uncomment for OAEI
		try{
			
			//if (Oracle.check(uri1, uri2, Oracle.Relation.EQUIVALENCE)){
			if (Oracle.check(uri1, uri2, "=")){				
				LogOutput.printAlways("Oracle.check(uri1, uri2, =)");
				return true;
			}
			//else if (Oracle.check(uri1, uri2, Oracle.Relation.SUBSUMED_BY)){
			else if (Oracle.check(uri1, uri2, "<")){				
				
				LogOutput.printAlways("Oracle.check(uri1, uri2, <)");
				return true;
			}
			//else if (Oracle.check(uri1, uri2, Oracle.Relation.SUBSUMES)){
			else if (Oracle.check(uri1, uri2, ">")){
				
				LogOutput.printAlways("Oracle.check(uri1, uri2, =)");
				return true;
			}
			else {
				LogOutput.printAlways("Not in oracle: Oracle.check(uri1, uri2, ?)");
				
				return false;
			}
			//return Oracle.check(uri1, uri2, Oracle.Relation.EQUIVALENCE) || 
			//		   Oracle.check(uri1, uri2, Oracle.Relation.SUBSUMED_BY) || 
			//		   Oracle.check(uri1, uri2, Oracle.Relation.SUBSUMES);
		}
		catch (Exception e){
			LogOutput.printAlways("Error asking Oracle");
			return false;
		}
		///Uncomment for OAEI
	}
	
	
	@Override
	public boolean isActive() {
		//TODO
		//Uncomment for OAEI
		return Oracle.isInteractive();						
		
		//Comment for OAEI
		//return false;
	}

}

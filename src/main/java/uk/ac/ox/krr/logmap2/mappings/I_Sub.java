/*
 * Created on 2004

 */
package uk.ac.ox.krr.logmap2.mappings;

import java.util.Calendar;

import uk.ac.ox.krr.logmap2.lexicon.LexicalUtilities;

/**
 * @author Giorgos Stoilos
 * 
 * This class implements the string matching method proposed in the paper
 * "A String Metric For Ontology Alignment", published in ISWC 2005 
 *
 */
public class I_Sub{
	
	public double score( String st1 , String st2 ){
		
		if( (st1 == null) || (st2 == null) ){
				return -1;
		}
		
		String s1 = st1.toLowerCase();
		String s2 = st2.toLowerCase();
		
		s1 = normalizeString( s1 , '.' );
		s2 = normalizeString( s2 , '.' );
		s1 = normalizeString( s1 , '_' );
		s2 = normalizeString( s2 , '_' );
		s1 = normalizeString( s1 , ' ' );
		s2 = normalizeString( s2 , ' ' );

		int l1 = s1.length(); // length of s
		int l2 = s2.length(); // length of t

		int L1 = l1;
		int L2 = l2;

		if ((L1 == 0) && (L2 == 0))
			return 1;
		if ((L1 == 0) || (L2 == 0))
			return -1;

		double common = 0;
		int best = 2;

		int max = Math.min(l1, l2); // the maximal length of a subs

		while( s1.length() >0 && s2.length() >0 && best !=0 ){
			best = 0; // the best subs length so far

			l1 = s1.length(); // length of s
			l2 = s2.length(); // length of t

			int i = 0; // iterates through s1
			int j = 0; // iterates through s2

			int startS2 = 0;
			int endS2 = 0;
			int startS1 = 0;
			int endS1 = 0;
			int p=0;

			for( i = 0; (i < l1) && (l1 - i > best); i++) {
				j = 0;
				while (l2 - j > best) {
					int k = i;
					for(;(j < l2) && (s1.charAt(k) != s2.charAt(j)); j++);
						//System.out.println( s1.charAt( k ) + " " + s2.charAt( j ) );
			
					if (j != l2) { // we have found a starting point
						//System.out.println( "j: " + j );
						p = j;
						for (j++, k++;
							(j < l2) && (k < l1) && (s1.charAt(k) == s2.charAt(j));
							j++, k++);
						if( k-i > best){
							best = k-i;
							startS1 = i;
							endS1 = k;
							startS2 = p;
							endS2 = j;	
						}
						//best = Math.max(best, k - i);
					}
				}
			}
			//Vector v = new Vector();
			//if( startS1 != endS1 )
			//	System.out.println(  s1.substring( startS1 , endS1 ) );
			char[] newString = new char[ s1.length() - (endS1 - startS1) ];
		
			j=0;
			for( i=0 ;i<s1.length() ; i++ ){
				if( i>=startS1 && i< endS1 )
					continue;
				newString[j++] = s1.charAt( i );			
			}

			s1 = new String( newString );

			newString = new char[ s2.length() - ( endS2 - startS2 ) ];
			j=0;
			for( i=0 ;i<s2.length() ; i++ ){
				if( i>=startS2 && i< endS2 )
					continue;
				newString[j++] = s2.charAt( i );
			}
			s2 = new String( newString );

			//if( (startS1 < 1 || startS1 > 2 )
			//	||	(startS2 < 1 || startS2 > 2) && startS1 != startS2  )
			//	best--;

			if( best > 2 )
				common += best;
			else
				best = 0;

		//System.out.println( s1 + ":" + s2 );
		//System.out.println( "StartS1 : " + startS1 + " EndS1: " + endS1 );
		//System.out.println( "StartS2 : " + startS2 + " EndS2: " + endS2 );
		}

		double commonality = 0;
		double scaledCommon = (double)(2*common)/(L1+L2);
		commonality = scaledCommon;

		double winklerImprovement = winklerImprovement( st1 , st2 , commonality );
		double dissimilarity = 0;

		double rest1 = L1 - common;
		double rest2 = L2 - common;

		double unmatchedS1 = Math.max( rest1 , 0 );
		double unmatchedS2 = Math.max( rest2 , 0 );
		unmatchedS1 = rest1/L1;
		unmatchedS2 = rest2/L2;
		
		/**
		 * Hamacher Product
		 */
		double suma = unmatchedS1 + unmatchedS2;
		double product = unmatchedS1 * unmatchedS2;
		double p = 0.6;   //For 1 it coincides with the algebraic product
		if( (suma-product) == 0 )
			dissimilarity = 0;
		else
			dissimilarity = (product)/(p+(1-p)*(suma-product));
		
		return commonality - dissimilarity + winklerImprovement;
	}
	
	private double winklerImprovement( String s1 , String s2 , double commonality ){
		
		int i;
		//int n = Math.min( 4 , Math.min( s1.length() , s2.length() ) );
		int n = Math.min( s1.length() , s2.length() );
		for( i=0 ; i<n ; i++ )
			if( s1.charAt( i ) != s2.charAt( i ) )
				break;
		
		double commonPrefixLength = Math.min( 4 , i );
		//double commonPrefixLength = i;
		double winkler = commonPrefixLength*0.1*(1-commonality);

		return winkler;
	}

	/* (non-Javadoc)
	 * @see com.wcohen.ss.AbstractStringDistance#explainScore(com.wcohen.ss.api.StringWrapper, com.wcohen.ss.api.StringWrapper)
	 */
	public String explainScore(String s, String t) {
		return null;
	}
	
	public String normalizeString( String str , char remo ){
		
		StringBuffer strBuf = new StringBuffer(); 
		
		int j=0;
		for( int i=0 ; i<str.length() ; i++ ){
			if( str.charAt( i ) != remo )
				strBuf.append( str.charAt( i ) );
		}
		return strBuf.toString();		
	}
	
	
	public static void main(String[] args) {
		I_Sub isub = new I_Sub();
		
		long init, fin;
		
		init=Calendar.getInstance().getTimeInMillis();
		
		LexicalUtilities lexicalUtilities =new LexicalUtilities();
		
		System.out.println(lexicalUtilities.getStemming4Word("Bergame"));
		System.out.println(lexicalUtilities.getStemming4Word("Bergamo"));
		
		System.out.println(
				isub.score("Comté Milwaukee", "Milwaukee_County"));
		
		System.out.println(
				isub.score("Milwaukee Comté", "Milwaukee_County"));
		
		System.out.println(
				isub.score("Milwaukee Comté", "Milwaukee"));
		
		
		
		System.out.println(
				isub.score("JuvenileArthritis", "ArthritisJuvenile"));
		System.out.println(
				isub.score("Juvenile_Arthritis", "Juvenile_Rheumatoid_Arthritis"));
		System.out.println(
				isub.score("Juvenile_Arthritis", "Juvenile_Arthritis"));
		System.out.println(
				isub.score("Juvenile_Arthritis", "JuvenileArthritis"));
		System.out.println(
				isub.score("larynx_muscle", "laryngeal_muscle"));
		System.out.println(
				isub.score("lalaarthritis", "thritis"));
		System.out.println("aoaoaoa\n" + 
				isub.score("arterialcircle", "circular"));
		
		System.out.println("Moresby: " + isub.score("Moresby Port", "Moresby P."));
		System.out.println("Moresby: " + isub.score("PortMoresby", "Moresby P."));
		System.out.println("Moresby: " + isub.score("Port_Moresby", "Moresby"));
		
		System.out.println("email: " + isub.score("hasanemail", "Email"));
		System.out.println("conference: " + isub.score("ConferenceEvent", "Conference"));
		
		
		fin = Calendar.getInstance().getTimeInMillis();
		System.out.println("Time I-Sub (s): " + (double)((double)fin-(double)init)/1000.0);
		
	}
	
	
}

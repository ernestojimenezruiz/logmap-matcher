package uk.ac.ox.krr.logmap2.lexicon;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NormalizeDate {
	
	
	private static SimpleDateFormat formatter_in = new SimpleDateFormat();
	private static SimpleDateFormat formatter_out = new SimpleDateFormat("dd-MMM-yyyy");
	
	//Used date patterns
	private static String date_format1 = "yyyy-MM-dd";
	private static String date_format2 = "yyyy-MMM-dd";
	private static String date_format3 = "dd-MM-yyyy";
	private static String date_format4 = "dd-MMM-yyyy";
	private static String date_format5 = "dd, MMM yyyy";
	private static String date_format6 = "dd MMM yyyy";
	private static String date_format7 = "yyyy MMM dd";
	private static String date_format8 = "yyyy/MM/dd";
	private static String date_format9 = "yyyy/MMM/dd";
	private static String date_format10 = "dd/MM/yyyy";
	private static String date_format11 = "dd/MMM/yyyy";
	
	//private static List<String> date_formats = new ArrayList<String>();
	private static Set<String> date_formats = new HashSet<String>();
	
	
	static {
		date_formats.add(date_format1);
		date_formats.add(date_format2);
		date_formats.add(date_format3);
		date_formats.add(date_format4);
		date_formats.add(date_format5);
		date_formats.add(date_format6);
		date_formats.add(date_format7);
		date_formats.add(date_format8);
		date_formats.add(date_format9);
		date_formats.add(date_format10);
		date_formats.add(date_format11);
	}
	
	
	/**
	 * Normalizes a give data (string format) to dd-MMM-yyyy.
	 * If the give string is not a date or not in the accepted 
	 * formats it returns the same string
	 * @param date_str
	 * @return
	 */
	public static String normalize(String date_str){
		
		String normalized_date;
		
		for (String pattern : date_formats){
			
			if (!(normalized_date=applyPattern(date_str, pattern)).equals("")){
				return normalized_date;
			}
		}
		
		return date_str;

	}
	
	
	@SuppressWarnings("deprecation")
	private static String applyPattern(String date_str, String pattern){
		try {
			formatter_in.applyPattern(pattern);
			
			Date date = formatter_in.parse(date_str);
			
			//System.out.println(date_str + "  " +date.getYear() + "\n");
			
			if (date.getYear()<-1000){//wrong year (or too old <900 which is unlikely to appear) then wrong parsing
				return "";
			}
			
			return formatter_out.format(date);
			
		}
		catch (Exception e){
			return "";
		}
		
	}
	
	
	
	
	/**
	 * @param args
	 * @throws ParseException 
	 */
	public static void main(String[] args) throws ParseException {
		
		
		System.out.println(NormalizeDate.normalize("1815-11-02"));
		System.out.println(NormalizeDate.normalize("02, Nov 1815"));
		
		/*System.out.println(NormalizeDate.normalize("2009-01-31"));
		System.out.println(NormalizeDate.normalize("23, August 2012"));
		System.out.println(NormalizeDate.normalize("23 August 2012"));
		System.out.println(NormalizeDate.normalize("2003 Sep 15"));
		System.out.println(NormalizeDate.normalize("La casa es azul"));
		System.out.println(NormalizeDate.normalize("2003/Sep/15"));
		System.out.println(NormalizeDate.normalize("2003_Sep_15"));
		*/
		
	
		String strDate1 = "2009-01-31";
        SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = formatter1.parse(strDate1);
        //System.out.println("dd-MMM-yyyy date is ==>"+date1);
        
        String strDate2 = "23, August 2012";
        SimpleDateFormat formatter2 = new SimpleDateFormat("dd, MMM yyyy");
        Date date2 = formatter2.parse(strDate2);
        //System.out.println("dd-MMM-yyyy date is ==>"+date1);
        
        SimpleDateFormat formatter3 = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate1 = formatter3.format(date1);
        String formattedDate2 = formatter3.format(date2);
        
        System.out.println("dd-MMM-yyyy date is ==>"+formattedDate1);
        System.out.println("dd-MMM-yyyy date is ==>"+formattedDate2);
        
        //http://docs.oracle.com/javase/1.4.2/docs/api/java/text/SimpleDateFormat.html
        //	http://stackoverflow.com/questions/15296150/date-format-parsing-java
        //	http://stackoverflow.com/questions/15296150/date-format-parsing-java

	}

}

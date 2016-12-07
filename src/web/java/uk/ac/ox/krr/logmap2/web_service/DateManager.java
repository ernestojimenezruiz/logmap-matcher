/*******************************************************************************
 * Copyright 2012 by the Department of Computer Science (University of Oxford)
 * 
 *    This file is part of LogMap.
 * 
 *    LogMap is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU Lesser General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 * 
 *    LogMap is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU Lesser General Public License for more details.
 * 
 *    You should have received a copy of the GNU Lesser General Public License
 *    along with LogMap.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package uk.ac.ox.krr.logmap2.web_service;

import java.util.Calendar;
import java.util.TimeZone;

public class DateManager {
	
	
	//private static String time_zone = "GMT+1";
	private static final String time_zone = "Europe/London";
	
	public static String getCurrentYear(){
		
		return conver2String(Calendar.getInstance(TimeZone.getTimeZone(time_zone)).get(Calendar.YEAR));
		
	}
	
	public static String getCurrentMonth(){
		
		//First month is 0
		return conver2String(Calendar.getInstance(TimeZone.getTimeZone(time_zone)).get(Calendar.MONTH)+1);
	    
	}
	
	public static String getCurrentDay(){
		
		return conver2String(Calendar.getInstance(TimeZone.getTimeZone(time_zone)).get(Calendar.DAY_OF_MONTH));
		
	}
	
	public static String getCurrentHour(){
		
		return conver2String(Calendar.getInstance(TimeZone.getTimeZone(time_zone)).get(Calendar.HOUR_OF_DAY));
		
	}

	public static String getCurrentMinute(){
		
		return conver2String(Calendar.getInstance(TimeZone.getTimeZone(time_zone)).get(Calendar.MINUTE));
		
	}

	public static String getCurrentSecond(){
		
		return conver2String(Calendar.getInstance(TimeZone.getTimeZone(time_zone)).get(Calendar.SECOND));
		
	}
	
	public static String getCurrentMilisecond(){
		
		return conver2String(Calendar.getInstance(TimeZone.getTimeZone(time_zone)).get(Calendar.MILLISECOND));
		
	}

	
	
	
	private static String conver2String(int value){
		
		String str_value = String.valueOf(value); 
		if (str_value.length()==1){
			str_value="0"+str_value;//we normalize
		}
		return str_value;
	}
	
}

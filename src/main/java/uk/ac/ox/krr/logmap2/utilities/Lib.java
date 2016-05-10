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
package uk.ac.ox.krr.logmap2.utilities;

import java.io.*;

import uk.ac.ox.krr.logmap2.io.LogOutput;


public class Lib {

	private final static double EPS = 1e-6;
	
	public static int dcmp(double x)
	{
		if (x < -EPS) return -1;
		else if (x > EPS) return 1;
		else return 0;
	}
	
	public static void debuginfo(String s)
	{
		LogOutput.print("aoaoaoaoao~~~~~  " + s);
	}
	
	private static BufferedWriter writer = null;
	
	public static void logInfo(String info)
	{
		try {
			if (writer == null)
				writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("/auto/users/yzhou/log.txt")));

			writer.write(info + "\n");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void closeLog()
	{
		if (writer != null)
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		writer = null;
	}
	
}

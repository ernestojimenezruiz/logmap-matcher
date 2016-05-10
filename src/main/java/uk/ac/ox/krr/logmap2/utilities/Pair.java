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

import java.util.Comparator;

public class Pair<T1, T2> implements Comparable< Pair<T1, T2> > {
  protected final T1 m_key;
  protected final T2 m_value;
  
  public Pair(T1 key, T2 value) {
    m_key   = key;
    m_value = value;
  }
  
  public T1 getKey() {
    return m_key;
  }
  
  public T2 getValue() {
    return m_value;
  }
  
  public String toString() {
    //System.out.println("in toString()");
    StringBuffer buff = new StringBuffer();
      buff.append("Key: ");
      buff.append(m_key);
      buff.append("\tValue: ");
      buff.append(m_value);
    return(buff.toString() );
  }
  public int compareTo( Pair<T1, T2> p1 ) { 
    //System.out.println("in compareTo()");
    if ( null != p1 ) { 
      if ( p1.equals(this) ) { 
        return 0; 
      } else if ( p1.hashCode() > this.hashCode() ) { 
            return 1;
      } else if ( p1.hashCode() < this.hashCode() ) { 
        return -1;  
      }
    }
    return(-1);
  }
  
  @SuppressWarnings("unchecked")
  public boolean equals( Object o ) {
	  return equals((Pair<T1, T2>)o);
  }
  
  public boolean equals( Pair<T1, T2> p1 ) { 
//    System.out.println("in equals()");
    if ( null != p1 ) { 
      if ( p1.m_key.equals( this.m_key ) && p1.m_value.equals( this.m_value ) ) { 
        return(true);
      }
    }
    return(false);
  }
  
  public int hashCode() { 
    int hashCode = m_key.hashCode() + (31 * m_value.hashCode());
    //System.out.println("in hashCode() [" + Integer.toString(hashCode) + "]");
    return(hashCode);
  }
  
}

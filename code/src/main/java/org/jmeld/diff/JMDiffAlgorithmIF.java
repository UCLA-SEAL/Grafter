/*
   JMeld is a visual diff and merge tool.
   Copyright (C) 2007  Kees Kuip
   This library is free software; you can redistribute it and/or
   modify it under the terms of the GNU Lesser General Public
   License as published by the Free Software Foundation; either
   version 2.1 of the License, or (at your option) any later version.
   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   Lesser General Public License for more details.
   You should have received a copy of the GNU Lesser General Public
   License along with this library; if not, write to the Free Software
   Foundation, Inc., 51 Franklin Street, Fifth Floor,
   Boston, MA  02110-1301  USA
 */
package org.jmeld.diff;

import org.jmeld.*;

public interface JMDiffAlgorithmIF
{
  public void checkMaxTime(boolean checkMaxTime);

  public JMRevision diff(Object[] orig, Object[] rev)
      throws JMeldException, MaxTimeExceededException;
  
  public JMRevision diff(Object[] orig, Object[] rev, int start1, int end1, int start2, int end2)
	  throws JMeldException, MaxTimeExceededException;
}

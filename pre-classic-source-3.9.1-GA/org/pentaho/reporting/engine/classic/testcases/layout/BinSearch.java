/*
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2009 Pentaho Corporation.  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.testcases.layout;

import java.util.Arrays;

/**
 * Creation-Date: 25.04.2007, 22:29:10
 *
 * @author Thomas Morgner
 */
public class BinSearch
{
  private static final int searchLinear(final long[] data, long value)
  {
    for (int i = 0; i < data.length; i++)
    {
      final long breakPosition = data[i];
      if (breakPosition >= value)
      {
        return i;
      }
    }
    return data.length;
  }

  public static void main(final String[] args)
  {
    final long[] data = new long[4000];
    for (int i = 0; i < data.length; i++)
    {
      data[i] = (long) (1000 * Math.random());
    }

    final long[] keysToSearch = new long[4000];
    for (int i = 0; i < keysToSearch.length; i++)
    {
      keysToSearch[i] = (long) (1000 * Math.random());
    }

    Arrays.sort (data);

    final long startTimeLin = System.currentTimeMillis();
    for (int i = 0; i < keysToSearch.length; i++)
    {
      final long pos = keysToSearch[i];
      searchLinear(data, pos);
    }
    final long endTimeLin = System.currentTimeMillis();
    System.out.println ("Linear search: " + (endTimeLin - startTimeLin));


    final long startTimeBin = System.currentTimeMillis();
    for (int i = 0; i < keysToSearch.length; i++)
    {
      final long pos = keysToSearch[i];
      Arrays.binarySearch(data, pos);
    }
    final long endTimeBin = System.currentTimeMillis();
    System.out.println ("Binary search: " + (endTimeBin - startTimeBin));
  }
}

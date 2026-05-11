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
 * Copyright (c) 2001 - 2009 Object Refinery Ltd, Pentaho Corporation and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.states.crosstab;

import java.util.ArrayList;

import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.states.ReportStateKey;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

/**
 * Computed structural data of a crosstab. It basically contains the full dataset of the column axis, which then allows
 * us to inject artificial rows into the dataset.
 * <p/>
 * We have the assumption, that the data is already pre-sorted in some way and that all rows are given in that order. As
 * the order can be arbitrary, we do not attempt to sort or assume that items are comparable. This model is only
 * guaranteed to work well, if the data set is properly normalized. A sane MDX datasource is guaranteed to return such a
 * normalized dataset.
 *
 * @author Thomas Morgner
 */
public class OrderedMergeCrosstabSpecification implements CrosstabSpecification
{
  private int insertationCursor;
  private ArrayList entries;
  private String[] columnSet;
  private ReportStateKey key;

  public OrderedMergeCrosstabSpecification(final ReportStateKey key, final String[] columnSet)
  {
    if (key == null)
    {
      throw new NullPointerException();
    }
    if (columnSet == null)
    {
      throw new NullPointerException();
    }

    this.key = key;
    // todo: Make sure we allow a empty column set. This may produce funny crosstabs, but it must not fail hard.
    this.columnSet = (String[]) columnSet.clone();
    this.entries = new ArrayList();
  }

  public int indexOf(final int start, final Object[] key)
  {
    if (key == null)
    {
      throw new NullPointerException();
    }
    if (start < 0)
    {
      throw new IndexOutOfBoundsException();
    }

    final int size = entries.size();
    for (int i = start; i < size; i++)
    {
      final Object[] objects = (Object[]) entries.get(i);
      if (ObjectUtilities.equalArray(key, objects))
      {
        return i;
      }
    }
    return -1;
  }

  public String[] getColumnNames()
  {
    return (String[]) columnSet.clone();
  }

  public ReportStateKey getKey()
  {
    return key;
  }

  public void startRow()
  {
    insertationCursor = 0;
  }

  public void endRow()
  {
  }

  public void add(final DataRow dataRow)
  {
    final Object[] newKey = new Object[columnSet.length];
    for (int i = 0; i < columnSet.length; i++)
    {
      final String columnName = columnSet[i];
      newKey[i] = dataRow.get(columnName);
    }

    insertationCursor = findInserationPoint(newKey, insertationCursor);
    if (insertationCursor < entries.size())
    {
      final Object[] existingKey = (Object[]) entries.get(insertationCursor);
      if (ObjectUtilities.equalArray(existingKey, newKey))
      {
        // key already exists 
        return;
      }
    }
    entries.add(insertationCursor, newKey);
    insertationCursor += 1;
  }

  private String printKey(final Object[] data)
  {
    final StringBuilder s = new StringBuilder("{");
    for (int i = 0; i < data.length; i++)
    {
      if (i > 0)
      {
        s.append(',');
      }
      s.append(data[i]);
    }
    return s.append('}').toString();
  }

  private int findInserationPoint(final Object[] key, final int inserationPoint)
  {
    for (int i = inserationPoint; i < entries.size(); i++)
    {
      final Object[] existingKey = (Object[]) entries.get(i);
      if (ObjectUtilities.equalArray(existingKey, key))
      {
        return i;
      }
    }
    return inserationPoint;
  }

  public int size()
  {
    return entries.size();
  }

  public Object[] getKeyAt(final int column)
  {
    final Object[] data = (Object[]) entries.get(column);
    return (Object[]) data.clone();
  }
//
//  public static void main(String[] args)
//  {
//    final String[] NAMES = {"Product", "Year"};
//    final DataRow r1 = new StaticDataRow (NAMES, new Object[]{"Planes", new Integer(2004)});
//    final DataRow r2 = new StaticDataRow (NAMES, new Object[]{"Planes", new Integer(2005)});
//    final DataRow r3 = new StaticDataRow (NAMES, new Object[]{"Planes", new Integer(2001)});
//    final DataRow r4 = new StaticDataRow (NAMES, new Object[]{"Planes", new Integer(2002)});
//    final DataRow r5 = new StaticDataRow (NAMES, new Object[]{"Planes", new Integer(2003)});
//
//    CrosstabSpecification cs = new CrosstabSpecification(NAMES);
//    cs.startRow();
//    cs.add(r1);
//    cs.add(r2);
//    cs.endRow();
//    cs.startRow();
//    cs.add(r3);
//    cs.add(r4);
//    cs.add(r5);
//    cs.endRow();
//  }
}

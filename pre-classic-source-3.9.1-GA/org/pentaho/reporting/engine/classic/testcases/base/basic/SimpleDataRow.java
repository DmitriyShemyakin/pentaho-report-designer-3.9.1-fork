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

package org.pentaho.reporting.engine.classic.testcases.base.basic;

import java.util.ArrayList;

import org.pentaho.reporting.engine.classic.core.DataRow;

/**
 * @deprecated can be replaced by a StaticDataRow.
 */
public class SimpleDataRow implements DataRow
{
  private ArrayList names;
  private ArrayList values;

  public SimpleDataRow ()
  {
    names = new ArrayList();
    values = new ArrayList();
  }

  public String[] getColumnNames()
  {
    return (String[]) names.toArray(new String[names.size()]);
  }

  /**
   * Returns the column position of the column, expression or function with the given name
   * or -1 if the given name does not exist in this DataRow.
   *
   * @param name the item name.
   * @return the item index.
   */
  public int findColumn (String name)
  {
    return names.indexOf(name);
  }

  /**
   * Returns the value of the expression or column in the tablemodel using the given
   * column number as index. For functions and expressions, the <code>getValue()</code>
   * method is called and for columns from the tablemodel the tablemodel method
   * <code>getValueAt(row, column)</code> gets called.
   *
   * @param col the item index.
   * @return the value.
   *
   * @throws IllegalStateException if the datarow detected a deadlock.
   */
  public Object get (int col)
  {
    return values.get(col);
  }

  /**
   * Returns the value of the function, expression or column using its specific name. The
   * given name is translated into a valid column number and the the column is queried.
   * For functions and expressions, the <code>getValue()</code> method is called and for
   * columns from the tablemodel the tablemodel method <code>getValueAt(row,
   * column)</code> gets called.
   *
   * @param col the item index.
   * @return the value.
   *
   * @throws IllegalStateException if the datarow detected a deadlock.
   */
  public Object get (String col)
          throws IllegalStateException
  {
    // todo implement me
    int index = names.indexOf(col);
    if (index == -1)
    {
      return null;
    }
    return values.get(index);
  }

  /**
   * Returns the number of columns, expressions and functions and marked ReportProperties
   * in the report.
   *
   * @return the item count.
   */
  public int getColumnCount ()
  {
    return values.size();
  }

  /**
   * Returns the name of the column, expression or function. For columns from the
   * tablemodel, the tablemodels <code>getColumnName</code> method is called. For
   * functions, expressions and report properties the assigned name is returned.
   *
   * @param col the item index.
   * @return the name.
   */
  public String getColumnName (int col)
  {
    return (String) names.get(col);
  }

  public void add (String name, Object value)
  {
    names.add(name);
    values.add(value);
  }

  public boolean isChanged(String name)
  {
    return false;
  }

  public boolean isChanged(int index)
  {
    return false;
  }
}

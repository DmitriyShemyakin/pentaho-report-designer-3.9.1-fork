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

package org.pentaho.reporting.designer.core.editor.report.layouting;

import java.util.ArrayList;

import org.pentaho.reporting.engine.classic.core.DataRow;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class DesignerRecordingFieldsDataRow implements DataRow
{
  private DataRow parent;
  private ArrayList fields;

  public DesignerRecordingFieldsDataRow(final DataRow parent)
  {
    if (parent == null)
    {
      throw new NullPointerException();
    }
    this.fields = new ArrayList();
    this.parent = parent;
  }

  public void clear()
  {
    this.fields.clear();
  }

  /**
   * Returns the value of the function, expression or column using its specific name. The given name is translated into
   * a valid column number and the the column is queried. For functions and expressions, the <code>getValue()</code>
   * method is called and for columns from the tablemodel the tablemodel method <code>getValueAt(row, column)</code>
   * gets called.
   *
   * @param col the item index.
   * @return the value.
   */
  public Object get(final String col)
  {
    if (fields.contains(col) == false)
    {
      fields.add(col);
    }
    return parent.get(col);
  }

  public String[] getColumnNames()
  {
    return parent.getColumnNames();
  }

  public String[] getFields()
  {
    return (String[]) fields.toArray(new String[fields.size()]);
  }

  /**
   * Checks whether the value contained in the column has changed since the last advance-operation.
   *
   * @param name the name of the column.
   * @return true, if the value has changed, false otherwise.
   */
  public boolean isChanged(final String name)
  {
    return false;
  }
}

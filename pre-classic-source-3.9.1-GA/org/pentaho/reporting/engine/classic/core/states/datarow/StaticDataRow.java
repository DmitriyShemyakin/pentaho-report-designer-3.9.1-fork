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

package org.pentaho.reporting.engine.classic.core.states.datarow;

import org.pentaho.reporting.engine.classic.core.DataRow;

/**
 * This is a static datarow holding a value for each name in the datarow. This datarow does not hold dataflags and thus
 * does not track the changes done to the data inside.
 * <p/>
 * The StaticDataRow is a derived view and is used to provide a safe collection of the values of the previous datarow.
 *
 * @author Thomas Morgner
 * @deprecated Moved into main package, as this class is of general usefulness.
 */
public class StaticDataRow extends org.pentaho.reporting.engine.classic.core.StaticDataRow
{
  public StaticDataRow()
  {
  }

  protected StaticDataRow(final StaticDataRow dataRow)
  {
    super(dataRow);
  }

  public StaticDataRow(final DataRow dataRow)
  {
    super(dataRow);
  }

  public StaticDataRow(final String[] names, final Object[] values)
  {
    super(names, values);
  }
}

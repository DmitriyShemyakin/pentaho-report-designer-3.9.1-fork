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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.states.crosstab.CrosstabSpecification;

/**
 * A datarow that acts as padding source. It overrides the columns from either report-data or expressions datarow with
 * values collected by the crosstab-specification. It also provides padding for the advance calls. <p/> There are three
 * padding scenarios that must be covered by this data-row: <ul> <li>Leading columns are missing <p> The
 * crosstab-specification's current cursor position is not at the computed position for the current column key (the
 * column values read from the actual data-row). Therefore the system has to insert fake columns until the cursor has
 * advanced to the current position. As we are effectively duplicating rows, this may lead to corrupted data if we
 * duplicate non-group-columns. </p></li> <li>Inner columns are missing <p> After an advance, the computed column-key
 * position is greater than the current cursor position. Therefore the system has to insert fake data until the
 * positions match again. </p></li> <li>Trailing columns are missing <p> If the advance would trigger a group-break,
 * check whether the current cursor position is already at the end of the columns list. If not, stay on the current row
 * and insert as many fake rows as needed. </p></li> </ul> The last two cases may be consolidated into one case.
 *
 * @author Thomas Morgner
 */
public class PaddingDataRow
{
  private static final Log logger = LogFactory.getLog(PaddingDataRow.class);

  private int currentCursorPosition;
  private CrosstabSpecification crosstabSpecification;
  private Object[] key;
  private String[] columnNames;


  public PaddingDataRow(final PaddingDataRow dataRow)
  {
    this.currentCursorPosition = dataRow.currentCursorPosition;
    this.crosstabSpecification = dataRow.crosstabSpecification;
    this.key = (Object[]) dataRow.key.clone();
    this.columnNames = dataRow.columnNames;
  }

  public PaddingDataRow(final CrosstabSpecification crosstabSpecification)
  {
    if (crosstabSpecification == null)
    {
      throw new NullPointerException();
    }
    this.crosstabSpecification = crosstabSpecification;
    this.columnNames = this.crosstabSpecification.getColumnNames();
    this.key = new Object[columnNames.length];
  }

  /**
   * Do we need a case1 padding?
   *
   * @param globalView
   * @return the number of rows needed for the pre-padding or zero if no pre-padding is required.
   */
  public int getPrePaddingRows(final DataRow globalView)
  {
    if (key.length == 0)
    {
      return 0;
    }

    for (int i = 0; i < key.length; i++)
    {
      key[i] = globalView.get(columnNames[i]);
    }

    final int computedPosition = crosstabSpecification.indexOf(currentCursorPosition, key);
    if (computedPosition < 0)
    {
      // not found, so all remaining columns must be padded. This will be handled by the post padding.
      // logger.debug("Pre: NF " + computedPosition + " CurrentPos: " + currentCursorPosition + " Key: " + printKey(key));
      return 0;
    }
    //logger.debug("Pre:  F " + computedPosition + " CurrentPos: " + currentCursorPosition + " Key: " + printKey(key));
    //logger.debug("Pre: 2F " + (computedPosition - currentCursorPosition));
    return computedPosition - currentCursorPosition;
  }

  public int getCurrentCursorPosition()
  {
    return currentCursorPosition;
  }

  public int getCrosstabColumnCount()
  {
    return crosstabSpecification.size();
  }

  private String printKey(final Object[] data)
  {
    final StringBuffer s = new StringBuffer("{");
    for (int i = 0; i < data.length; i++)
    {
      if (i > 0)
      {
        s.append(',');
      }
      s.append(data[i]);
    }
    return s + "}";
  }

  /**
   * After an advance, do we have to delay the advance and insert some extra rows? If we delay, then the advance has to
   * be undone and we have to get marked for either case2 or case3.
   *
   * @param globalView
   * @return the number of rows we have to insert for post-padding. If no padding is needed, zero is returned.
   */
  public int getPostPaddingRows(final DataRow globalView)
  {
    if (key.length == 0)
    {
      return 0;
    }


    for (int i = 0; i < key.length; i++)
    {
      key[i] = globalView.get(columnNames[i]);
    }

    final int computedPosition = crosstabSpecification.indexOf(currentCursorPosition, key);
    if (computedPosition < 0)
    {
      // not found, so all remaining columns must be padded.
      //logger.debug("Post: NF " + (crosstabSpecification.size() - currentCursorPosition));
      // -1 as the size is not a position; the last position is (size - 1)
      return crosstabSpecification.size() - currentCursorPosition - 1;
    }
    //logger.debug("Post:  F " + (computedPosition - currentCursorPosition));
    return computedPosition - currentCursorPosition;
  }

  public PaddingDataRow advance()
  {
    final PaddingDataRow dataRow = new PaddingDataRow(this);
    dataRow.currentCursorPosition += 1;
    return dataRow;
  }

  public void activate(final MasterDataRow dataRow)
  {
    if (key.length == 0)
    {
      return;
    }


    final Object[] currentColumn = crosstabSpecification.getKeyAt(currentCursorPosition);
    for (int i = 0; i < columnNames.length; i++)
    {
      final MasterDataRowChangeEvent chEvent =
          new MasterDataRowChangeEvent(MasterDataRowChangeEvent.COLUMN_UPDATED, columnNames[i], currentColumn[i]);
      logger.debug("Messing around with Column: " + columnNames[i] + " = " + currentColumn[i]);
      dataRow.dataRowChanged(chEvent);
    }
  }

  public PaddingDataRow resetRowCursor()
  {
    // logger.debug("################### Reset rowcounter");

    final PaddingDataRow dataRow = new PaddingDataRow(this);
    dataRow.currentCursorPosition = 0;
    return dataRow;
  }
}

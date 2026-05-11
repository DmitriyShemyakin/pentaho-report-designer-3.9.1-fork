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
 * Copyright (c) 2005 - 2009 Pentaho Corporation, Object Refinery Limited and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.testcases.bugs;

import java.awt.Dimension;

import org.pentaho.reporting.engine.classic.testcases.BaseTest;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Cell;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Rectangle;
import com.lowagie.text.Table;
import com.lowagie.text.rtf.RtfWriter2;
import com.lowagie.text.rtf.table.RtfCell;

/**
 * Creation-Date: 24.03.2006, 13:21:11
 *
 * @author Thomas Morgner
 */
public class RtfTableTest extends BaseTest
{
  public static void testRtfTable()
          throws DocumentException
  {
    Document document = new Document(new Rectangle(455, 600), 0, 0, 0, 0);
    RtfWriter2.getInstance(document, System.out);
    document.open();

    Table table = new Table (7, 2);
    boolean[][] occupiedCells = new boolean[7][2];
    float[] cellWidths = { 10, 10, 10, 10, 10, 10, 10 };
    table.setWidths(cellWidths);

    addCell(occupiedCells, table, 0, 0, 4, 2);
    addCell(occupiedCells, table, 4, 0, 2, 2);
    addCell(occupiedCells, table, 6, 0, 1, 1);
    addCell(occupiedCells, table, 6, 1, 1, 1);
/*
    addCell(occupiedCells, table, 0, 2, 3, 1);
    addCell(occupiedCells, table, 3, 2, 3, 1);
    addCell(occupiedCells, table, 6, 2, 1, 1);
    addCell(occupiedCells, table, 0, 3, 2, 1);
    addCell(occupiedCells, table, 2, 3, 3, 1);
    addCell(occupiedCells, table, 5, 3, 1, 1);
    addCell(occupiedCells, table, 6, 3, 1, 1);
    addCell(occupiedCells, table, 0, 4, 2, 1);
    addCell(occupiedCells, table, 2, 4, 3, 1);
    addCell(occupiedCells, table, 5, 4, 1, 1);
    addCell(occupiedCells, table, 6, 4, 1, 1);
    addCell(occupiedCells, table, 0, 5, 1, 1);
    addCell(occupiedCells, table, 1, 5, 1, 1);
    addCell(occupiedCells, table, 2, 5, 1, 1);
    addCell(occupiedCells, table, 3, 5, 1, 1);
    addCell(occupiedCells, table, 4, 5, 1, 1);
    addCell(occupiedCells, table, 5, 5, 1, 1);
    addCell(occupiedCells, table, 6, 5, 1, 1);
    addCell(occupiedCells, table, 0, 6, 3, 1);
    addCell(occupiedCells, table, 3, 6, 3, 1);
    addCell(occupiedCells, table, 6, 6, 1, 1);
    addCell(occupiedCells, table, 0, 7, 3, 1);
    addCell(occupiedCells, table, 3, 7, 3, 1);
    addCell(occupiedCells, table, 6, 7, 1, 1);
    addCell(occupiedCells, table, 0, 8, 2, 1);
    addCell(occupiedCells, table, 2, 8, 3, 1);
    addCell(occupiedCells, table, 5, 8, 1, 1);
    addCell(occupiedCells, table, 6, 8, 1, 1);
    addCell(occupiedCells, table, 0, 9, 2, 1);
    addCell(occupiedCells, table, 2, 9, 3, 1);
    addCell(occupiedCells, table, 5, 9, 1, 1);
    addCell(occupiedCells, table, 6, 9, 1, 1);
    addCell(occupiedCells, table, 0, 10, 1, 1);
    addCell(occupiedCells, table, 1, 10, 1, 1);
    addCell(occupiedCells, table, 2, 10, 1, 1);
    addCell(occupiedCells, table, 3, 10, 1, 1);
    addCell(occupiedCells, table, 4, 10, 1, 1);
    addCell(occupiedCells, table, 5, 10, 1, 1);
    addCell(occupiedCells, table, 6, 10, 1, 1);
    addCell(occupiedCells, table, 0, 11, 3, 1);
    addCell(occupiedCells, table, 3, 11, 3, 1);
    addCell(occupiedCells, table, 6, 11, 1, 1);
    addCell(occupiedCells, table, 0, 12, 3, 1);
    addCell(occupiedCells, table, 3, 12, 3, 1);
    addCell(occupiedCells, table, 6, 12, 1, 1);
    addCell(occupiedCells, table, 0, 13, 2, 1);
    addCell(occupiedCells, table, 2, 13, 3, 1);
    addCell(occupiedCells, table, 5, 13, 1, 1);
    addCell(occupiedCells, table, 6, 13, 1, 1);
    addCell(occupiedCells, table, 0, 14, 2, 1);
    addCell(occupiedCells, table, 2, 14, 3, 1);
    addCell(occupiedCells, table, 5, 14, 1, 1);
    addCell(occupiedCells, table, 6, 14, 1, 1);
    addCell(occupiedCells, table, 0, 15, 1, 1);
    addCell(occupiedCells, table, 1, 15, 1, 1);
    addCell(occupiedCells, table, 2, 15, 1, 1);
    addCell(occupiedCells, table, 3, 15, 1, 1);
    addCell(occupiedCells, table, 4, 15, 1, 1);
    addCell(occupiedCells, table, 5, 15, 1, 1);
    addCell(occupiedCells, table, 6, 15, 1, 1);
    addCell(occupiedCells, table, 0, 16, 3, 1);
    addCell(occupiedCells, table, 3, 16, 3, 1);
    addCell(occupiedCells, table, 6, 16, 1, 1);
    addCell(occupiedCells, table, 0, 17, 3, 1);
    addCell(occupiedCells, table, 3, 17, 3, 1);
    addCell(occupiedCells, table, 6, 17, 1, 1);
    addCell(occupiedCells, table, 0, 18, 2, 1);
    addCell(occupiedCells, table, 2, 18, 3, 1);
    addCell(occupiedCells, table, 5, 18, 1, 1);
    addCell(occupiedCells, table, 6, 18, 1, 1);
    addCell(occupiedCells, table, 0, 19, 2, 1);
    addCell(occupiedCells, table, 2, 19, 3, 1);
    addCell(occupiedCells, table, 5, 19, 1, 1);
    addCell(occupiedCells, table, 6, 19, 1, 1);
    addCell(occupiedCells, table, 0, 20, 1, 1);
    addCell(occupiedCells, table, 1, 20, 1, 1);
    addCell(occupiedCells, table, 2, 20, 1, 1);
    addCell(occupiedCells, table, 3, 20, 1, 1);
    addCell(occupiedCells, table, 4, 20, 1, 1);
    addCell(occupiedCells, table, 5, 20, 1, 1);
    addCell(occupiedCells, table, 6, 20, 1, 1);
    addCell(occupiedCells, table, 0, 21, 3, 1);
    addCell(occupiedCells, table, 3, 21, 3, 1);
    addCell(occupiedCells, table, 6, 21, 1, 1);
    addCell(occupiedCells, table, 0, 22, 3, 1);
    addCell(occupiedCells, table, 3, 22, 3, 1);
    addCell(occupiedCells, table, 6, 22, 1, 1);
    addCell(occupiedCells, table, 0, 23, 2, 1);
    addCell(occupiedCells, table, 2, 23, 3, 1);
    addCell(occupiedCells, table, 5, 23, 1, 1);
    addCell(occupiedCells, table, 6, 23, 1, 1);
    addCell(occupiedCells, table, 0, 24, 2, 1);
    addCell(occupiedCells, table, 2, 24, 3, 1);
    addCell(occupiedCells, table, 5, 24, 1, 1);
    addCell(occupiedCells, table, 6, 24, 1, 1);
    addCell(occupiedCells, table, 0, 25, 1, 1);
    addCell(occupiedCells, table, 1, 25, 1, 1);
    addCell(occupiedCells, table, 2, 25, 1, 1);
    addCell(occupiedCells, table, 3, 25, 1, 1);
    addCell(occupiedCells, table, 4, 25, 1, 1);
    addCell(occupiedCells, table, 5, 25, 1, 1);
    addCell(occupiedCells, table, 6, 25, 1, 1);
    addCell(occupiedCells, table, 0, 26, 3, 1);
    addCell(occupiedCells, table, 3, 26, 3, 1);
    addCell(occupiedCells, table, 6, 26, 1, 1);
    addCell(occupiedCells, table, 0, 27, 1, 1);
    addCell(occupiedCells, table, 1, 27, 1, 1);
    addCell(occupiedCells, table, 2, 27, 1, 1);
    addCell(occupiedCells, table, 3, 27, 1, 1);
    addCell(occupiedCells, table, 4, 27, 1, 1);
    addCell(occupiedCells, table, 5, 27, 1, 1);
    addCell(occupiedCells, table, 6, 27, 1, 1);
    addCell(occupiedCells, table, 0, 28, 6, 2);
    addCell(occupiedCells, table, 6, 28, 1, 1);
    addCell(occupiedCells, table, 6, 29, 1, 1);
*/    
    document.add(table);
    assertTrue(true);
  }

  private static void addCell(final boolean[][] occupiedCells,
                              final Table table,
                              final int x,
                              final int y,
                              final int cs,
                              final int rs) throws
          BadElementException
  {
    final Dimension dimension = table.getDimension();
    if (x + cs > dimension.width) throw new IllegalStateException();
    if (y + rs > dimension.height) throw new IllegalStateException();

    Cell cell = new RtfCell();
    cell.setColspan(cs);
    cell.setRowspan(rs);
    table.addCell(cell, y, x);

    for (int col = 0; col < cs; col++)
    {
      for (int row = 0; row < rs; row++)
      {
        if (occupiedCells[x + col][y + row] == false)
        {
          occupiedCells[x + col][y + row] = true;
        }
        else
        {
          throw new IllegalStateException("Overlapping element at " + col + ", " + row);
        }
      }
    }
  }
}

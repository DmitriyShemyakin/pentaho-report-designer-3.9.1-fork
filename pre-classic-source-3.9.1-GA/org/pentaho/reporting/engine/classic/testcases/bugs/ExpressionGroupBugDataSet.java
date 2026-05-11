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
 * Copyright (c) 2000 - 2009 Pentaho Corporation, Simba Management Limited and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.testcases.bugs;

import javax.swing.table.AbstractTableModel;

/**
 * @author user
 */
public class ExpressionGroupBugDataSet extends AbstractTableModel
{
  
  /**
   * 
   */
  public ExpressionGroupBugDataSet()
  {
  }
  
  
/*
  RESERVE_L U/Y
  ------------- ----
  */
  
  private static final Object[] DATA = new Object[] {
    new Double (0.0),
    new Double (0.0 ),
    new Double (0.0 ),
    new Double (0.0 ),
    new Double (0.0 ),
    new Double (0.0 ),
    new Double (0.0 ),
    new Double (0.0 ),
    new Double (0.0 ),
    new Double (0.0 ),
    new Double (0.0 ),
    new Double (-1.0 ),
    new Double (-10.0 ),
    new Double (-18.18 ),
    new Double (-30.0 ),
    new Double (-40.0 ),
    new Double (-40.0 ),
    new Double (-45.45 ),
    new Double (-50.0 ),
    new Double (-50.0 ),
    new Double (-50.0 ),
    new Double (-60.0 ),
    new Double (-60.61 ),
    new Double (-65.0 ),
    new Double (-70.0 ),
    new Double (-80.0 ),
    new Double (-100.0 ),
    new Double (-100.0 ),
    new Double (-100.0 ),
    new Double (-100.0 ),
    new Double (-100.0 ),
    new Double (-100.0 ),
    new Double (-100.0 ),
    new Double (-105.0 ),
    new Double (-110.0 ),
    new Double (-120.0 ),
    new Double (-150.0 ),
    new Double (-150.0 ),
    new Double (-150.0 ),
    new Double (-153.3 ),
    new Double (-200.0 ),
    new Double (-200.0 ),
    new Double (-200.0 ),
    new Double (-200.0 ),
    new Double (-200.0 ),
    new Double (-200.0 ),
    new Double (-200.0 ),
    new Double (-200.0 ),
    new Double (-200.0 ),
    new Double (-200.0 ),
    new Double (-212.12 ),
    new Double (-250.0 ),
    new Double (-250.0 ),
    new Double (-250.0 ),
    new Double (-250.0 ),
    new Double (-250.0 ),
    new Double (-250.0 ),
    new Double (-250.0 ),
    new Double (-250.0 ),
    new Double (-250.0 ),
    new Double (-250.0 ),
    new Double (-250.0 ),
    new Double (-250.0 ),
    new Double (-250.0 ),
    new Double (-250.0 ),
    new Double (-250.0 ),
    new Double (-250.0 ),
    new Double (-250.0 ),
    new Double (-250.0 ),
    new Double (-250.0 ),
    new Double (-250.0 ),
    new Double (-250.0 ),
    new Double (-250.0 ),
    new Double (-250.0 ),
    new Double (-250.0 ),
    new Double (-250.0 ),
    new Double (-275.0 ),
    new Double (-275.0 ),
    new Double (-275.0 ),
    new Double (-275.0 ),
    new Double (-285.0 ),
    new Double (-300.0 ),
    new Double (-300.0 ),
    new Double (-300.0 ),
    new Double (-325.0 ),
    new Double (-339.35 ),
    new Double (-350.0 ),
    new Double (-350.0 ),
    new Double (-350.0 ),
    new Double (-350.0 ),
    new Double (-350.0 ),
    new Double (-350.0 ),
    new Double (-375.0 ),
    new Double (-380.0 ),
    new Double (-390.0 ),
    new Double (-400.0 ),
    new Double (-400.0 ),
    new Double (-400.0 ),
    new Double (-400.0 ),
    new Double (-400.0 ),
    new Double (-400.0 ),
    new Double (-400.0 ),
    new Double (-400.0 ),
    new Double (-437.88 ),
    new Double (-450.0 ),
    new Double (-500.0 ),
    new Double (-500.0 ),
    new Double (-500.0 ),
    new Double (-500.0 ),
    new Double (-500.0 ),
    new Double (-500.0 ),
    new Double (-500.0 ),
    new Double (-500.0 ),
    new Double (-500.0 ),
    new Double (-500.0 ),
    new Double (-500.0 ),
    new Double (-500.0 ),
    new Double (-500.0 ),
    new Double (-500.0 ),
    new Double (-500.0 ),
    new Double (-500.0 ),
    new Double (-500.0 ),
    new Double (-500.0 ),
    new Double (-500.0 ),
    new Double (-500.0 ),
    new Double (-500.0 ),
    new Double (-550.0 ),
    new Double (-550.0 ),
    new Double (-600.0 ),
    new Double (-600.0 ),
    new Double (-600.0 ),
    new Double (-600.0 ),
    new Double (-600.0 ),
    new Double (-700.0 ),
    new Double (-700.0 ),
    new Double (-700.0 ),
    new Double (-750.0 ),
    new Double (-766.52 ),
    new Double (-800.0 ),
    new Double (-800.0 ),
    new Double (-810.0 ),
    new Double (-910.0 ),
    new Double (-1000.0 ),
    new Double (-1000.0 ),
    new Double (-1000.0 ),
    new Double (-1000.0 ),
    new Double (-1000.0 ),
    new Double (-1000.0 ),
    new Double (-1000.0 ),
    new Double (-1000.0 ),
    new Double (-1000.0 ),
    new Double (-1000.0 ),
    new Double (-1000.0 ),
    new Double (-1000.0 ),
    new Double (-1000.0 ),
    new Double (-1000.0 ),
    new Double (-1000.0 ),
    new Double (-1000.0 ),
    new Double (-1000.0 ),
    new Double (-1000.0 ),
    new Double (-1000.0 ),
    new Double (-1000.0 ),
    new Double (-1000.0 ),
    new Double (-1000.0 ),
    new Double (-1000.0 ),
    new Double (-1000.0 ),
    new Double (-1050.0 ),
    new Double (-1100.0 ),
    new Double (-1100.0 ),
    new Double (-1200.0 ),
    new Double (-1200.0 ),
    new Double (-1200.0 ),
    new Double (-1200.0 ),
    new Double (-1360.0 ),
    new Double (-1363.64 ),
    new Double (-1400.0 ),
    new Double (-1400.0 ),
    new Double (-1400.0 ),
    new Double (-1400.0 ),
    new Double (-1500.0 ),
    new Double (-1500.0 ),
    new Double (-1500.0 ),
    new Double (-1600.0 ),
    new Double (-1609.69 ),
    new Double (-1750.0 ),
    new Double (-2000.0 ),
    new Double (-2000.0 ),
    new Double (-2000.0 ),
    new Double (-2000.0 ),
    new Double (-2000.0 ),
    new Double (-2000.0 ),
    new Double (-2000.0 ),
    new Double (-2000.0 ),
    new Double (-2000.0 ),
    new Double (-2200.0 ),
    new Double (-2200.0 ),
    new Double (-2408.32 ),
    new Double (-2450.0 ),
    new Double (-2452.86 ),
    new Double (-2500.0 ),
    new Double (-2500.0 ),
    new Double (-2500.0 ),
    new Double (-2500.0 ),
    new Double (-2500.0 ),
    new Double (-2500.0 ),
    new Double (-2500.0 ),
    new Double (-2650.0 ),
    new Double (-2800.0 ),
    new Double (-3000.0 ),
    new Double (-3000.0 ),
    new Double (-3000.0 ),
    new Double (-3000.0 ),
    new Double (-3000.0 ),
    new Double (-3000.0 ),
    new Double (-3000.0 ),
    new Double (-3000.0 ),
    new Double (-3100.0 ),
    new Double (-3433.93 ),
    new Double (-3500.0 ),
    new Double (-4000.0 ),
    new Double (-4000.0 ),
    new Double (-4159.82 ),
    new Double (-4200.0 ),
    new Double (-4500.0 ),
    new Double (-4500.0 ),
    new Double (-4500.0 ),
    new Double (-4870.0 ),
    new Double (-5100.0 ),
    new Double (-5672.24 ),
    new Double (-6500.0 ),
    new Double (-6750.0 ),
    new Double (-6900.0 ),
    new Double (-7805.0 ),
    new Double (-8600.0 ),
    new Double (-8738.31 ),
    new Double (-9000.0 ),
    new Double (-9258.14 ),
    new Double (-10000.0 ),
    new Double (-10000.0 ),
    new Double (-10100.0 ),
    new Double (-11000.0 ),
    new Double (-12100.0 ),
    new Double (-15000.0 ),
    new Double (-15000.0 ),
    new Double (-15000.0 ),
    new Double (-16200.0 ),
    new Double (-16500.0 ),
    new Double (-17000.0 ),
    new Double (-17250.0 ),
    new Double (-22000.0 ),
    new Double (-25000.0 ),
    new Double (-25300.0 ),
    new Double (-30000.0 ),
    new Double (-30000.0 ),
    new Double (-50000.0 ),
    new Double (-50000.0 ),
    new Double (-56000.0 ),
    new Double (-60000.0 ),
    new Double (-67500.0 ),
    new Double (-70000.0 ),
    new Double (-70000.0 ),
    new Double (-70000.0 ),
    new Double (-70000.0 ),
    new Double (-75000.0 ),
    new Double (-85000.0 ),
    new Double (-150000.0 ),
    new Double (-185000.0 ),
    new Double (-258000.0 ),
    new Double (-333000.0 )
  };
  /** 
   * @see javax.swing.table.TableModel#getColumnClass(int)
   * 
   * @param columnIndex
   * @return
   */
  public Class getColumnClass(int columnIndex)
  {
    return Double.class;
  }

  /** 
   * @see javax.swing.table.TableModel#getColumnName(int)
   * 
   * @param column
   * @return
   */
  public String getColumnName(int column)
  {
    if (column == 0)
    {
      return "LBL_QUERY_RESE_reserveL";
    }
    else
    {
      return super.getColumnName(column);
    }
  }

  /** 
   * @see javax.swing.table.TableModel#getColumnCount()
   * 
   * @return
   */
  public int getColumnCount()
  {
    return 1;
  }

  /** 
   * @see javax.swing.table.TableModel#getRowCount()
   * 
   * @return
   */
  public int getRowCount()
  {
    return DATA.length;
  }

  /** 
   * @see javax.swing.table.TableModel#getValueAt(int, int)
   * 
   * @param rowIndex
   * @param columnIndex
   * @return
   */
  public Object getValueAt(int rowIndex, int columnIndex)
  {
    return DATA[rowIndex];
  }
}

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

package org.pentaho.reporting.engine.classic.core.function;

import javax.swing.table.TableModel;

import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.Element;

/**
 * This functions checks the tablemodel and shows the named band, if there is no data available.
 *
 * @author Thomas Morgner
 * @deprecated Use a Style-Expression or make proper use of the No-Data-Band
 */
public class HideElementIfDataAvailableExpression extends AbstractElementFormatFunction
{
  /**
   * Default Constructor.
   */
  public HideElementIfDataAvailableExpression()
  {
  }

  /**
   * Applies the computed visiblity to all child elements of the given band.
   *
   * @param b the visibility.
   */
  protected void processRootBand(final Band b)
  {
    final boolean visible = (isDataAvailable() == false);
    final Element[] elements = FunctionUtilities.findAllElements(b, getElement());
    for (int i = 0; i < elements.length; i++)
    {
      final Element element = elements[i];
      element.setVisible(visible);
    }

  }

  /**
   * Computes, if there is data available. This checks the report's data-row and searches for the number of rows in the
   * underlying tablemodel.
   *
   * @return true, if the tablemodel contains data, false otherwise.
   */
  private boolean isDataAvailable()
  {
    final ExpressionRuntime runtime = getRuntime();
    if (runtime == null)
    {
      return false;
    }
    final TableModel data = runtime.getData();
    if (data == null)
    {
      return false;
    }
    if (data.getRowCount() == 0)
    {
      return false;
    }
    return true;
  }

  /**
   * Return the current expression value. <P> The value depends (obviously) on the expression implementation.
   *
   * @return the value of the function.
   */
  public Object getValue()
  {
    if (isDataAvailable())
    {
      return Boolean.TRUE;
    }
    return Boolean.FALSE;
  }
}

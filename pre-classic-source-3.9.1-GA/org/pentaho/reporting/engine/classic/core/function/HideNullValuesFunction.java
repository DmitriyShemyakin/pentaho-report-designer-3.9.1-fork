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

import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.Element;

/**
 * Hides the specified elements if the given field contains empty strings or zero numbers.
 *
 * @author Thomas Morgner
 * @deprecated This should be done using Style-Expressions.
 */
public class HideNullValuesFunction extends AbstractElementFormatFunction
{
  /**
   * The name of the data-row column that is checked for null-values.
   */
  private String field;

  /**
   * Default Constructor.
   */
  public HideNullValuesFunction()
  {
  }

  /**
   * Returns the field used by the function. The field name corresponds to a column name in the report's data-row.
   *
   * @return The field name.
   */
  public String getField()
  {
    return field;
  }

  /**
   * Sets the field name for the function. The field name corresponds to a column name in the report's data-row.
   *
   * @param field the field name.
   */
  public void setField(final String field)
  {
    this.field = field;
  }

  /**
   * Applies the computed visiblity to all child elements of the given band.
   *
   * @param b the visibility.
   */
  protected void processRootBand(final Band b)
  {
    final Element[] elements =
        FunctionUtilities.findAllElements(b, getElement());
    final boolean visible = computeVisibility();

    for (int i = 0; i < elements.length; i++)
    {
      final Element element = elements[i];
      element.setVisible(visible);
    }
  }

  /**
   * Computes the visiblity of the specified element.
   *
   * @return true, if the element should be visible, false otherwise.
   */
  protected boolean computeVisibility()
  {
    final Object value = getDataRow().get(getField());
    if (value == null)
    {
      return false;
    }
    if (value instanceof String)
    {
      final String strValue = (String) value;
      return strValue.trim().length() > 0;
    }
    if (value instanceof Number)
    {
      final Number numValue = (Number) value;
      return numValue.doubleValue() != 0;
    }
    return true;
  }
}

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
 * Triggers the visiblity of an element based on the boolean value read from the defined field.
 *
 * @author Thomas Morgner
 * @deprecated add a style-expression for the visible style-key instead.
 */
public class ElementVisibilityFunction extends AbstractElementFormatFunction
{
  /**
   * The field name of the data-row column from where to read the boolean value.
   */
  private String field;

  /**
   * Default Constructor.
   */
  public ElementVisibilityFunction()
  {
  }

  /**
   * Returns the field name of the data-row column from where to read the boolean value.
   *
   * @return the field name.
   */
  public String getField()
  {
    return field;
  }

  /**
   * Defines field name of the data-row column from where to read the boolean value.
   *
   * @param field the name of the field.
   */
  public void setField(final String field)
  {
    this.field = field;
  }

  /**
   * Returns whether the element will be visible or not.
   *
   * @return Boolean.TRUE or Boolean.FALSE.
   */
  public Object getValue()
  {
    if (isVisible())
    {
      return Boolean.TRUE;
    }
    else
    {
      return Boolean.FALSE;
    }
  }

  /**
   * Applies the visibility to all elements of the band with the given name.
   *
   * @param b the root band.
   */
  protected void processRootBand(final Band b)
  {
    final boolean visible = isVisible();
    final Element[] elements = FunctionUtilities.findAllElements(b, getElement());
    for (int i = 0; i < elements.length; i++)
    {
      final Element element = elements[i];
      element.setVisible(visible);
    }

  }

  /**
   * Computes the visiblity of the element.
   *
   * @return true, if the field contains the Boolean.TRUE object, false otherwise.
   */
  protected boolean isVisible()
  {
    return Boolean.TRUE.equals(getDataRow().get(getField()));
  }
}

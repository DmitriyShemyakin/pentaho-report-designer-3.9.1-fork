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

package org.pentaho.reporting.engine.classic.core.filter.types.bands;

import java.util.Locale;

import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;
import org.pentaho.reporting.engine.classic.core.metadata.ElementTypeRegistry;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class CrosstabColumnGroupType implements ElementType
{
  public CrosstabColumnGroupType()
  {
  }

  /**
   * A band that serves a specific purpose within a slotted parent should return "true" here. Plain elemetns and bands
   * that can be freely combined should return false. </p>
   *
   * @return true, if the usage is restricted.
   */
  public boolean isRestricted()
  {
    return true;
  }

  public ElementMetaData getMetaData()
  {
    return ElementTypeRegistry.getInstance().getElementType("crosstab-column-group");
  }

  /**
   * Returns the current value for the data source.
   *
   * @param runtime the expression runtime that is used to evaluate formulas and expressions when computing the value of
   *                this filter.
   * @param element the element for which the data is computed.
   * @return the value.
   */
  public Object getValue(final ExpressionRuntime runtime, final Element element)
  {
    // Always null. Bands have no return value.
    return null;
  }

  public Object getDesignValue(final ExpressionRuntime runtime, final Element element)
  {
    return null;
  }

  /**
   * Clones this <code>DataSource</code>.
   *
   * @return the clone.
   * @throws CloneNotSupportedException this should never happen.
   */
  public Object clone() throws CloneNotSupportedException
  {
    return super.clone();
  }

  public void configureDesignTimeDefaults(final Element element, final Locale locale)
  {

  }
}
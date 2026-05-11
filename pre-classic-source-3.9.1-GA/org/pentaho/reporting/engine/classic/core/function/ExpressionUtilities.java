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

import java.util.ResourceBundle;

/**
 * A collection of utility methods which may be useful for expression-implementors.
 *
 * @author Thomas Morgner
 */
public class ExpressionUtilities
{
  /**
   * Hidden default constructor.
   */
  private ExpressionUtilities()
  {
  }

  /**
   * Retursn the default resource-bundle. The name of the default resource-bundle is defined in the report
   * configuration.
   *
   * @param expression the expression that asked for the resource-bundle.
   * @return the resource-bundle.
   */
  public static ResourceBundle getDefaultResourceBundle(final Expression expression)
  {
    if (expression == null)
    {
      throw new NullPointerException("Expression is null");
    }
    final String resourceBundleName =
        expression.getReportConfiguration().getConfigProperty
            ("org.pentaho.reporting.engine.classic.core.ResourceBundle");
    return expression.getResourceBundleFactory().getResourceBundle(resourceBundleName);
  }
}

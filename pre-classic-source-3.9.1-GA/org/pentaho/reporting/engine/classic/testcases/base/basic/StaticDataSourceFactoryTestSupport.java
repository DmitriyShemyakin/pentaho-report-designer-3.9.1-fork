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

package org.pentaho.reporting.engine.classic.testcases.base.basic;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import junit.framework.TestCase;

/**
 * @deprecated moved into engine core
 */
public class StaticDataSourceFactoryTestSupport extends DefaultTableModel
{
  /**
   * Constructs a default <code>DefaultTableModel</code> which is a table of
   * zero columns and zero rows.
   */
  public StaticDataSourceFactoryTestSupport()
  {
  }

  public StaticDataSourceFactoryTestSupport(String parameter, int parameter2)
  {
    if ("test".equals(parameter) == false || parameter2 != 5)
    {
      throw new IllegalStateException();
    }
  }


  public TableModel createParametrizedTableModel (int i1, String s1)
  {
    TestCase.assertEquals("Passing primitive parameters failed", 5, i1);
    TestCase.assertEquals("Passing object parameters failed", "test", s1);
    return new DefaultTableModel();
  }

  public TableModel createSimpleTableModel ()
  {
    return new DefaultTableModel();
  }

}

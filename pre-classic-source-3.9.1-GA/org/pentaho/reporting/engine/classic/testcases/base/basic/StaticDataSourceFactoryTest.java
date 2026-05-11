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

import javax.swing.table.TableModel;
import javax.swing.table.DefaultTableModel;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.StaticDataFactory;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.states.datarow.StaticDataRow;

/**
 * @deprecated moved into engine core
 */
public class StaticDataSourceFactoryTest extends TestCase
{
  public StaticDataSourceFactoryTest(String string)
  {
    super(string);
  }

  public void testDataFactory () throws ReportDataFactoryException
  {
    final StaticDataRow sdr = new StaticDataRow(new String[]{"parameter1", "parameter2"},
        new Object[]{"test", new Integer(5)});

    assertNotNull(sdr.get("parameter1"));
    assertNotNull(sdr.get("parameter2"));

    StaticDataFactory sfd = new StaticDataFactory();
    assertNotNull(sfd.queryData
        ("org.pentaho.reporting.engine.classic.testcases.base.basic.StaticDataSourceFactoryTest#createSimpleTableModel", sdr));
    assertNotNull(sfd.queryData
        ("org.pentaho.reporting.engine.classic.testcases.base.basic.StaticDataSourceFactoryTest#createSimpleTableModel()", sdr));
    assertNotNull(sfd.queryData
        ("org.pentaho.reporting.engine.classic.testcases.base.basic.StaticDataSourceFactoryTest#createParametrizedTableModel(parameter2,parameter1)", sdr));

    assertNotNull(sfd.queryData
        ("org.pentaho.reporting.engine.classic.testcases.base.basic.StaticDataSourceFactoryTestSupport", sdr));
    assertNotNull(sfd.queryData
        ("org.pentaho.reporting.engine.classic.testcases.base.basic.StaticDataSourceFactoryTestSupport#createSimpleTableModel", sdr));
    assertNotNull(sfd.queryData
        ("org.pentaho.reporting.engine.classic.testcases.base.basic.StaticDataSourceFactoryTestSupport#createSimpleTableModel()", sdr));
    assertNotNull(sfd.queryData
        ("org.pentaho.reporting.engine.classic.testcases.base.basic.StaticDataSourceFactoryTestSupport#createParametrizedTableModel(parameter2,parameter1)", sdr));

    assertNotNull(sfd.queryData
        ("org.pentaho.reporting.engine.classic.testcases.base.basic.StaticDataSourceFactoryTestSupport()", sdr));
    assertNotNull(sfd.queryData
        ("org.pentaho.reporting.engine.classic.testcases.base.basic.StaticDataSourceFactoryTestSupport()#createSimpleTableModel", sdr));
    assertNotNull(sfd.queryData
        ("org.pentaho.reporting.engine.classic.testcases.base.basic.StaticDataSourceFactoryTestSupport()#createSimpleTableModel()", sdr));
    assertNotNull(sfd.queryData
        ("org.pentaho.reporting.engine.classic.testcases.base.basic.StaticDataSourceFactoryTestSupport()#createParametrizedTableModel(parameter2,parameter1)", sdr));

    assertNotNull(sfd.queryData
        ("org.pentaho.reporting.engine.classic.testcases.base.basic.StaticDataSourceFactoryTestSupport(parameter1,parameter2)", sdr));
    assertNotNull(sfd.queryData
        ("org.pentaho.reporting.engine.classic.testcases.base.basic.StaticDataSourceFactoryTestSupport(parameter1,parameter2)#createSimpleTableModel", sdr));
    assertNotNull(sfd.queryData
        ("org.pentaho.reporting.engine.classic.testcases.base.basic.StaticDataSourceFactoryTestSupport(parameter1,parameter2)#createSimpleTableModel()", sdr));
    assertNotNull(sfd.queryData
        ("org.pentaho.reporting.engine.classic.testcases.base.basic.StaticDataSourceFactoryTestSupport(parameter1,parameter2)#createParametrizedTableModel(parameter2,parameter1)", sdr));
  }

  public static TableModel createParametrizedTableModel (int i1, String s1)
  {
    assertEquals("Passing primitive parameters failed", 5, i1);
    assertEquals("Passing object parameters failed", "test", s1);
    return new DefaultTableModel();
  }

  public static TableModel createSimpleTableModel ()
  {
    return new DefaultTableModel();
  }
}

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

import java.awt.Color;
import java.awt.Rectangle;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ElementAlignment;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.elementfactory.TextFieldElementFactory;
import org.pentaho.reporting.engine.classic.testcases.base.functionality.FunctionalityTestLib;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.StaticDataFactory;
import org.pentaho.reporting.engine.classic.core.style.FontDefinition;

/**
 * @deprecated moved into engine core
 */
public class SubReportParameterPassingTest extends TestCase
{
  public SubReportParameterPassingTest(String string)
  {
    super(string);
  }

  public void testParameterPassing() throws Exception
  {
    ClassicEngineBoot.getInstance().start();

    MasterReport report = new MasterReport();
    StaticDataFactory staticDataFactory = new StaticDataFactory();
    report.setDataFactory(staticDataFactory);
    report.setQuery("org.pentaho.reporting.engine.classic.testcases.base.basic.SubReportParameterPassingTest#createMainTableModel()");

    Element textElement =
        TextFieldElementFactory.createStringElement
            ("reportField1", new Rectangle(0, 0, 100, 20), Color.BLACK,
                ElementAlignment.LEFT, ElementAlignment.TOP,
                new FontDefinition("Arial", 12), "-", "c1");
    report.getItemBand().addElement(textElement);

    SubReport subReport = new SubReport();
    subReport.addInputParameter("c1", "c1");
    subReport.setQuery("org.pentaho.reporting.engine.classic.testcases.base.basic.SubReportParameterPassingTest#createSubReportTableModel(c1)");
    Element subReportTextElement =
        TextFieldElementFactory.createStringElement
            ("subreportField1", new Rectangle(20, 0, 100, 20),
                Color.RED, ElementAlignment.LEFT,
                ElementAlignment.TOP, new FontDefinition("Arial", 12), "-", "t1");
    subReport.getItemBand().addElement(subReportTextElement);

    report.getItemBand().addSubReport(subReport);

    FunctionalityTestLib.execGraphics2D(report);

  }

  public static TableModel createMainTableModel()
  {
    System.out.println("TestDataFactory.createTableModel");
    return new DefaultTableModel(new String[][]{{"1.1", "1.2"}, {"2.1", "2.2"}}, new String[]{"c1", "c2"});
  }


  public static TableModel createSubReportTableModel(String param1)
  {
    assertNotNull(param1);

    System.out.println("TestDataFactory.createTableModel");
    System.out.println("param1 = " + param1);

    return new DefaultTableModel(new String[][]{{"1.1:" + param1, "1.2:" + param1}, {"2.1:" + param1, "2.2:" + param1}}, new String[]{"t1", "t2"});
  }
}

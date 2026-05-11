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
 * Copyright (c) 2001 - 2009 Object Refinery Ltd, Pentaho Corporation and Contributors.  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.testcases.bugs;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.swing.table.TableModel;
import javax.swing.table.DefaultTableModel;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.demo.util.ReportDefinitionException;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.HtmlReportUtil;
import org.pentaho.reporting.engine.classic.core.modules.misc.tablemodel.CSVTableModelProducer;
import org.pentaho.reporting.engine.classic.testcases.BaseTest;
import org.pentaho.reporting.engine.classic.testcases.TestSystem;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

/**
 * Creation-Date: Feb 21, 2007, 4:01:57 PM
 *
 * @author Thomas Morgner
 */
public class Pre200Test extends BaseTest
{
  public Pre200Test()
  {
  }
  private static final String largeText = "l?nea 4 descripci?n ampliada. texto largo utiizado para ampliar la descripci?n de un art?culo, puede contener hasta 2000 car?cteres de texto, no se imprime si no contiene nada.\n" +
      "Repito 2:\n" +
//      "l?nea 12 descripci?n ampliada. texto largo utiizado para ampliar la descripci?n de un art?culo, puede contener hasta 2000 car?cteres de texto, no se imprime si no contiene nada.\n" +
//      "Repito 3:\n" +
//      "l?nea 12 descripci?n ampliada. texto largo utiizado para ampliar la descripci?n de un art?culo, puede contener hasta 2000 car?cteres de texto, no se imprime si no contiene nada.\n" +
//      "Repito 4:\n" +
//      "l?nea 12 descripci?n ampliada. texto largo utiizado para ampliar la descripci?n de un art?culo, puede contener hasta 2000 car?cteres de texto, no se imprime si no contiene nada.\n" +
//      "Repito 5:\n" +
      "l?nea 12 descripci?n ampliada. texto largo utiizado para ampliar la descripci?n de un art?culo, puede contener hasta 2000 car?cteres de texto, no se imprime si no contiene nada.\n" +
      "Repito 6:\n" +
      "l?nea 12 descripci?n ampliada. texto largo utiiza";
  /**
   * Returns the URL of the XML definition for this report.
   *
   * @return the URL of the report definition.
   */
  public URL getReportDefinitionSource()
  {
    return ObjectUtilities.getResourceRelative
        ("pre200.xml", Pre200Test.class); //$NON-NLS-1$
  }

  public  void testPre123() throws ReportProcessingException, IOException, ReportDefinitionException
  {
    ClassicEngineBoot.getInstance().start();
    String outputFilename = getReportOutputPath() + "/report.html"; //$NON-NLS-1$
    final MasterReport report = parseReport(getReportDefinitionSource());
    HtmlReportUtil.createStreamHTML(report, outputFilename);
    assertTrue(true);
  }

  public static void main(String[] args) throws ReportDefinitionException, ReportProcessingException, IOException
  {
    ClassicEngineBoot.getInstance().start();
    final Pre200Test test = new Pre200Test();
    final MasterReport report = test.parseReport(test.getReportDefinitionSource());

    final InputStream stream = ObjectUtilities.getResourceRelativeAsStream("pre200a.csv", Pre200Test.class);
    CSVTableModelProducer csvTableModelProducer = new CSVTableModelProducer(stream);
    csvTableModelProducer.setColumnNameFirstLine(true);
    final TableModel tableModel = csvTableModelProducer.parse();
    csvTableModelProducer.close();

    final DefaultTableModel model = copyInto(tableModel);
    model.setValueAt(largeText, 3,2);

    report.setDataFactory(new TableDataFactory(report.getQuery(), model));
    TestSystem.showPreview(report);
  }

  public static DefaultTableModel copyInto (final TableModel model)
  {
    final int count = model.getColumnCount();
    final String[] names = new String[count];
    for (int i = 0; i < names.length; i++)
    {
      names[i] = model.getColumnName(i);
    }

    final Object[][] data = new Object[model.getRowCount()][count];
    for (int i = 0; i < data.length; i++)
    {
      final Object[] rowData = new Object[count];
      for (int j = 0; j < rowData.length; j++)
      {
        rowData[j] = model.getValueAt(i, j);
      }
      data[i] = rowData;
    }

    return new DefaultTableModel(data, names);
  }
}
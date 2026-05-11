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

package org.pentaho.reporting.engine.classic.testcases.subreport;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.demo.util.ReportDefinitionException;
import org.pentaho.reporting.engine.classic.core.modules.misc.tablemodel.JoiningTableModel;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.HtmlReportUtil;
import org.pentaho.reporting.engine.classic.core.modules.parser.extwriter.ReportWriterException;
import org.pentaho.reporting.engine.classic.testcases.BaseTest;

/**
 * The MultiReportDemo combines data from multiple table models into one single
 * report.
 * <p/>
 * For a detailed explaination of the demo have a look at the file <a
 * href="multireport.html">file</a>'.
 *
 * @author Ramaiz Mansoor
 * @deprecated
 */
public class SubReportTest extends BaseTest
{
  /**
   * The data for the report.
   */
  private final TableModel data;

  public SubReportTest()
  {
    this.data = createJoinedTableModel();
  }

  private TableModel createFruitTableModel()
  {
    final String[] names = new String[]{"Id Number", "Cat", "Fruit"};
    final Object[][] data = new Object[][]{
        {"I1", "A", "Apple"},
        {"I2", "A", "Orange"},
        {"I2", "A", "Orange"},
        {"I2", "A", "Orange"},
        {"I2", "A", "Orange"},
        {"I2", "A", "Orange"},
        {"I2", "A", "Orange"},
        {"I2", "A", "Orange"},
        {"I2", "A", "Orange"},
        {"I3", "B", "Water melon"},
        {"I3", "B", "Water melon"},
        {"I3", "B", "Water melon"},
        {"I3", "B", "Water melon"},
        {"I3", "B", "Water melon"},
        {"I3", "B", "Water melon"},
        {"I3", "B", "Water melon"},
        {"I3", "B", "Water melon"},
        {"I4", "B", "Strawberry"},
    };
    return new DefaultTableModel(data, names);
  }

  private TableModel createColorTableModel()
  {
    final String[] names = new String[]{"Number", "Group", "Color"};
    final Object[][] data = new Object[][]{
        {new Integer(1), "X", "Red"},
        {new Integer(2), "X", "Green"},
        {new Integer(3), "Y", "Yellow"},
        {new Integer(3), "Y", "Yellow"},
        {new Integer(4), "Y", "Blue"},
        {new Integer(4), "Y", "Blue"},
        {new Integer(5), "Z", "Orange"},
        {new Integer(5), "Z", "Orange"},
        {new Integer(5), "Z", "Orange"},
        {new Integer(6), "Z", "White"},
        {new Integer(6), "Z", "White"},
        {new Integer(6), "Z", "White"},
    };
    return new DefaultTableModel(data, names);
  }

  private TableModel createJoinedTableModel()
  {
    final JoiningTableModel jtm = new JoiningTableModel();
    jtm.addTableModel("Color", createColorTableModel());
    jtm.addTableModel("Fruit", createFruitTableModel());
    return jtm;
  }

  public void testSubReport()
      throws ReportProcessingException, IOException, ReportDefinitionException, ReportWriterException
  {
    // initialize JFreeReport
    ClassicEngineBoot.getInstance().start();
     
    URL reportTemplateFilename = SubReportTest.class.getResource("joined-report.xml"); //$NON-NLS-1$
    
    String outputFilename = getReportOutputPath() + "/subreport.html"; //$NON-NLS-1$

    // Create a JFreeReport from the XML file
    MasterReport report = parseReport(reportTemplateFilename);

    // Create the data for the report
    report.setDataFactory(new TableDataFactory("default", data)); //$NON-NLS-1$
    FileOutputStream outputStream = new FileOutputStream(new File(outputFilename));
    HtmlReportUtil.createStreamHTML(report, outputStream); 
    
    // Done!
    System.err.println("Done!"); //$NON-NLS-1$

  }

}

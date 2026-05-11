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
 * Copyright (c) 2009 Pentaho Corporation.  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.testcases.samplereport;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import org.custommonkey.xmlunit.DetailedDiff;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.testcases.XMLBaseTest;
import org.pentaho.reporting.engine.classic.testcases.output.xml.XmlDebugReportUtil;

public class OrderDetailReportXMLTest extends XMLBaseTest {

  public void testOrderDetailReportUsingPageableOutput() {
    String reportTemplateFilename = reportDefinitionPath + "/OrderDetailReport.xml"; //$NON-NLS-1$
    String inputFileName = reportInputPath + "/xml/PageableOrderDetailReport.xml"; //$NON-NLS-1$    
    String outputFilename = reportOutputPath + "/PageableOrderDetailReport.xml"; //$NON-NLS-1$
    String logFile = reportOutputPath + "/PageableOrderDetailReportLog.txt"; //$NON-NLS-1$
    List allDifferences = null;
    try {
      // Get the name of the report definition filename
      File reportDefinition = new File(reportTemplateFilename);
      if (!reportDefinition.exists() || !reportDefinition.isFile()) {
        throw new Exception("invalid report template filename"); //$NON-NLS-1$
      }

      // initialize JFreeReport
      ClassicEngineBoot.getInstance().start();

      // Create a JFreeReport from the XML file
      MasterReport report = parseReport(reportDefinition);

      // Create the data for the report
      OrderDetailReportData reportData = new OrderDetailReportData();
      TableDataFactory tableDataFactory = new TableDataFactory("default", reportData);//$NON-NLS-1$
      report.setDataFactory(tableDataFactory);
      XmlDebugReportUtil.createPageable(report, outputFilename);
      // Comparing the File just generated with the golden version
      DetailedDiff myDiff = new DetailedDiff(compareXML(readFileAsString(inputFileName),
          readFileAsString(outputFilename)));
      allDifferences = myDiff.getAllDifferences();

      if (myDiff != null && myDiff.toString() != null) {
        FileOutputStream outputStream = new FileOutputStream(logFile);
        outputStream.write(myDiff.toString().getBytes());
        outputStream.flush();
        outputStream.close();
      }
      // Done!
      System.err.println("Done!");//$NON-NLS-1$
    } catch (Throwable t) {
      t.printStackTrace();
    }
    if (allDifferences != null) {
      assertEquals(0, allDifferences.size());
    } else {
      assertTrue(true);
    }
  }

  public void testOrderDetailReportUsingFlowTableOutput() {
    String reportTemplateFilename = reportDefinitionPath + "/OrderDetailReport.xml"; //$NON-NLS-1$
    String inputFileName = reportInputPath + "/xml/FlowTableOrderDetailReport.xml"; //$NON-NLS-1$
    String outputFilename = reportOutputPath + "/FlowTableOrderDetailReport.xml"; //$NON-NLS-1$
    String logFile = reportOutputPath + "/FlowTableOrderDetailReportLog.txt"; //$NON-NLS-1$
    List allDifferences = null;
    try {
      // Get the name of the report definition filename
      File reportDefinition = new File(reportTemplateFilename);
      if (!reportDefinition.exists() || !reportDefinition.isFile()) {
        throw new Exception("invalid report template filename"); //$NON-NLS-1$
      }

      // initialize JFreeReport
      ClassicEngineBoot.getInstance().start();

      // Create a JFreeReport from the XML file
      MasterReport report = parseReport(reportDefinition);

      // Create the data for the report
      OrderDetailReportData reportData = new OrderDetailReportData();
      TableDataFactory tableDataFactory = new TableDataFactory("default", reportData);//$NON-NLS-1$
      report.setDataFactory(tableDataFactory);
      XmlDebugReportUtil.createFlowTable(report, outputFilename);
      // Comparing the File just generated with the golden version
      DetailedDiff myDiff = new DetailedDiff(compareXML(readFileAsString(inputFileName),
          readFileAsString(outputFilename)));
      allDifferences = myDiff.getAllDifferences();

      if (myDiff != null && myDiff.toString() != null) {
        FileOutputStream outputStream = new FileOutputStream(logFile);
        outputStream.write(myDiff.toString().getBytes());
        outputStream.flush();
        outputStream.close();
      }
      // Done!
      System.err.println("Done!");//$NON-NLS-1$
    } catch (Throwable t) {
      t.printStackTrace();
    }
    if (allDifferences != null) {
      assertEquals(0, allDifferences.size());
    } else {
      assertTrue(true);
    }
  }

}

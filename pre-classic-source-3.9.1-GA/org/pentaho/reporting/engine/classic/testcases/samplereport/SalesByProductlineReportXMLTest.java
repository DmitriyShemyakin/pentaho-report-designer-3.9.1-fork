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

public class SalesByProductlineReportXMLTest extends XMLBaseTest {

  public void testSalesByProductReportUsingPageableOutput() {
    String reportTemplateFilename = reportDefinitionPath + "/Sales_by_Productline.xml"; //$NON-NLS-1$
    String inputFileName = reportInputPath + "/xml/PageableSalesByProductlineReport.xml"; //$NON-NLS-1$    
    String outputFilename = reportOutputPath + "/PageableSalesByProductlineReport.xml"; //$NON-NLS-1$      
    String logFile = reportOutputPath + "/PageableSalesByProductlineReportLog.txt"; //$NON-NLS-1$
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
      SalesByProductlineReportData reportData = new SalesByProductlineReportData();
      TableDataFactory tableDataFactory = new TableDataFactory("default", reportData);//$NON-NLS-1$
      report.setDataFactory(tableDataFactory);
      report.getParameterValues().put("baseURL", "http://localhost:8080/pentaho");//$NON-NLS-1$ //$NON-NLS-2$
      report.getParameterValues().put("employee", "");//$NON-NLS-1$ //$NON-NLS-2$
      report.getParameterValues().put("product", "");//$NON-NLS-1$ //$NON-NLS-2$
      report.getParameterValues().put("productline", "Ships");//$NON-NLS-1$ //$NON-NLS-2$
      report.getParameterValues().put("territory", "");//$NON-NLS-1$ //$NON-NLS-2$
      report.getParameterValues().put("territory_name", "All Territories");//$NON-NLS-1$ //$NON-NLS-2$
      report.getParameterValues().put("employee_name", "All Employees");//$NON-NLS-1$ //$NON-NLS-2$
      report.getParameterValues().put("product_name", "All Products");//$NON-NLS-1$ //$NON-NLS-2$
      report.getParameterValues().put("productline_name", "All Product Lines");//$NON-NLS-1$ //$NON-NLS-2$
      report.getParameterValues().put("time_stop", "2005-05-31");//$NON-NLS-1$ //$NON-NLS-2$
      report.getParameterValues().put("time_start", "2005-01-01");//$NON-NLS-1$ //$NON-NLS-2$
      XmlDebugReportUtil.createPageable(report, outputFilename);
      //XmlDebugReportUtil.createFlowTable(report, outputFilename);
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

  public void testSalesByProductReportUsingFlowTableOutput() {
    String reportTemplateFilename = reportDefinitionPath + "/Sales_by_Productline.xml"; //$NON-NLS-1$
    String inputFileName = reportInputPath + "/xml/FlowTableSalesByProductlineReport.xml"; //$NON-NLS-1$    
    String outputFilename = reportOutputPath + "/FlowTableSalesByProductlineReport.xml"; //$NON-NLS-1$      
    String logFile = reportOutputPath + "/FlowTableSalesByProductlineReportLog.txt"; //$NON-NLS-1$
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
      SalesByProductlineReportData reportData = new SalesByProductlineReportData();
      TableDataFactory tableDataFactory = new TableDataFactory("default", reportData);//$NON-NLS-1$
      report.setDataFactory(tableDataFactory);
      report.getParameterValues().put("baseURL", "http://localhost:8080/pentaho");//$NON-NLS-1$ //$NON-NLS-2$
      report.getParameterValues().put("employee", "");//$NON-NLS-1$ //$NON-NLS-2$
      report.getParameterValues().put("product", "");//$NON-NLS-1$ //$NON-NLS-2$
      report.getParameterValues().put("productline", "Ships");//$NON-NLS-1$ //$NON-NLS-2$
      report.getParameterValues().put("territory", "");//$NON-NLS-1$ //$NON-NLS-2$
      report.getParameterValues().put("territory_name", "All Territories");//$NON-NLS-1$ //$NON-NLS-2$
      report.getParameterValues().put("employee_name", "All Employees");//$NON-NLS-1$ //$NON-NLS-2$
      report.getParameterValues().put("product_name", "All Products");//$NON-NLS-1$ //$NON-NLS-2$
      report.getParameterValues().put("productline_name", "All Product Lines");//$NON-NLS-1$ //$NON-NLS-2$
      report.getParameterValues().put("time_stop", "2005-05-31");//$NON-NLS-1$ //$NON-NLS-2$
      report.getParameterValues().put("time_start", "2005-01-01");//$NON-NLS-1$ //$NON-NLS-2$
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

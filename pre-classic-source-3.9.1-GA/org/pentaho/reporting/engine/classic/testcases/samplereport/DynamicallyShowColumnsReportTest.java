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

import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.PdfReportUtil;
import org.pentaho.reporting.engine.classic.core.modules.output.table.csv.CSVReportUtil;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.HtmlReportUtil;
import org.pentaho.reporting.engine.classic.core.modules.output.table.xls.ExcelReportUtil;
import org.pentaho.reporting.engine.classic.testcases.BaseTest;

public class DynamicallyShowColumnsReportTest extends BaseTest {

  public void testDynamicallyShowColumnsReportInPdf() {
    String reportTemplateFilename = getReportDefinitionPath() + "/dynamically_show_columns.xml"; //$NON-NLS-1$
    String inputFileName = getReportInputPath() + "/pdf/DynamicallyShowColumnsReport.pdf"; //$NON-NLS-1$
    String outputFilename = getReportOutputPath() + "/DynamicallyShowColumnsReport.pdf"; //$NON-NLS-1$
    String logFile = getReportOutputPath() + "/DynamicallyShowColumnsReportLogFilePdf.txt"; //$NON-NLS-1$
    boolean isEqual = false;
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
      DynamicallyShowColumnsReportData reportData = new DynamicallyShowColumnsReportData();
      TableDataFactory tableDataFactory = new TableDataFactory("default", reportData);//$NON-NLS-1$
      report.setDataFactory(tableDataFactory);
      report.getParameterValues().put("SQLC1_name", "IN STOCK");//$NON-NLS-1$ //$NON-NLS-2$
      report.getParameterValues().put("SQLC2_name", "COST");//$NON-NLS-1$ //$NON-NLS-2$
      report.getParameterValues().put("SQLC3_name", "MSRP");//$NON-NLS-1$ //$NON-NLS-2$
      report.getParameterValues().put("ShowCol", "Show_Column3");//$NON-NLS-1$ //$NON-NLS-2$
      report.getParameterValues().put("productline", "Motorcycles");//$NON-NLS-1$ //$NON-NLS-2$

      FileOutputStream outputStream = new FileOutputStream(outputFilename);
      //HtmlReportUtil.createStreamHTML(report, outputStream);
      PdfReportUtil.createPDF(report, outputStream);
      if (outputStream != null) {
        outputStream.flush();
        outputStream.close();
      }
      isEqual = comparePdf(inputFileName, outputFilename, logFile);

    } catch (Throwable t) {
      t.printStackTrace();
    }
    assertTrue(isEqual);
  }

  public void testDynamicallyShowColumnsReportInHtml() {
    String reportTemplateFilename = getReportDefinitionPath() + "/dynamically_show_columns.xml"; //$NON-NLS-1$
    String inputFileName = getReportInputPath() + "/html/DynamicallyShowColumnsReport.html"; //$NON-NLS-1$
    String outputFilename = getReportOutputPath() + "/DynamicallyShowColumnsReport.html"; //$NON-NLS-1$
    //String logFile = reportOutputPath + "/DynamicallyShowColumnsReportLogFilehtml.txt"; //$NON-NLS-1$    
    boolean isEqual = false;
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
      DynamicallyShowColumnsReportData reportData = new DynamicallyShowColumnsReportData();
      TableDataFactory tableDataFactory = new TableDataFactory("default", reportData);//$NON-NLS-1$
      report.setDataFactory(tableDataFactory);
      report.getParameterValues().put("SQLC1_name", "IN STOCK");//$NON-NLS-1$ //$NON-NLS-2$
      report.getParameterValues().put("SQLC2_name", "COST");//$NON-NLS-1$ //$NON-NLS-2$
      report.getParameterValues().put("SQLC3_name", "MSRP");//$NON-NLS-1$ //$NON-NLS-2$
      report.getParameterValues().put("ShowCol", "Show_Column3");//$NON-NLS-1$ //$NON-NLS-2$
      report.getParameterValues().put("productline", "Motorcycles");//$NON-NLS-1$ //$NON-NLS-2$

      FileOutputStream outputStream = new FileOutputStream(outputFilename);
      HtmlReportUtil.createStreamHTML(report, outputStream);
      //    Comparing the File just generated with the golden version
      if (outputStream != null) {
        outputStream.flush();
        outputStream.close();
      }
      //isEqual = compareHtml(inputFileName, outputFilename, logFile);
      isEqual = contentsEqual(inputFileName, outputFilename, true);
    } catch (Throwable t) {
      t.printStackTrace();
    }
    assertTrue(isEqual);
  }

  public void testDynamicallyShowColumnsReportInExcel() {
    String reportTemplateFilename = getReportDefinitionPath() + "/dynamically_show_columns.xml"; //$NON-NLS-1$
    String inputFileName = getReportInputPath() + "/xls/DynamicallyShowColumnsReport.xls"; //$NON-NLS-1$
    String outputFilename = getReportOutputPath() + "/DynamicallyShowColumnsReport.xls"; //$NON-NLS-1$
    String logFile = getReportOutputPath() + "/DynamicallyShowColumnsReportLogFileXls.txt"; //$NON-NLS-1$
    boolean isEqual = false;
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
      DynamicallyShowColumnsReportData reportData = new DynamicallyShowColumnsReportData();
      TableDataFactory tableDataFactory = new TableDataFactory("default", reportData);//$NON-NLS-1$
      report.setDataFactory(tableDataFactory);
      report.getParameterValues().put("SQLC1_name", "IN STOCK");//$NON-NLS-1$ //$NON-NLS-2$
      report.getParameterValues().put("SQLC2_name", "COST");//$NON-NLS-1$ //$NON-NLS-2$
      report.getParameterValues().put("SQLC3_name", "MSRP");//$NON-NLS-1$ //$NON-NLS-2$
      report.getParameterValues().put("ShowCol", "Show_Column3");//$NON-NLS-1$ //$NON-NLS-2$
      report.getParameterValues().put("productline", "Motorcycles");//$NON-NLS-1$ //$NON-NLS-2$

      FileOutputStream outputStream = new FileOutputStream(outputFilename);

      ExcelReportUtil.createXLS(report, outputStream);
      //    Comparing the File just generated with the golden version
      if (outputStream != null) {
        outputStream.flush();
        outputStream.close();
      }
      isEqual = compareXLS(inputFileName, outputFilename, logFile);

    } catch (Throwable t) {
      t.printStackTrace();
    }
    assertTrue(isEqual);
  }

  public void testDynamicallyShowColumnsReportInCsv() {
    String reportTemplateFilename = getReportDefinitionPath() + "/dynamically_show_columns.xml"; //$NON-NLS-1$
    String inputFileName = getReportInputPath() + "/csv/DynamicallyShowColumnsReport.csv"; //$NON-NLS-1$ 
    String outputFilename = getReportOutputPath() + "/DynamicallyShowColumnsReport.csv"; //$NON-NLS-1$
    boolean isEqual = false;
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
      DynamicallyShowColumnsReportData reportData = new DynamicallyShowColumnsReportData();
      TableDataFactory tableDataFactory = new TableDataFactory("default", reportData);//$NON-NLS-1$
      report.setDataFactory(tableDataFactory);
      report.getParameterValues().put("SQLC1_name", "IN STOCK");//$NON-NLS-1$ //$NON-NLS-2$
      report.getParameterValues().put("SQLC2_name", "COST");//$NON-NLS-1$ //$NON-NLS-2$
      report.getParameterValues().put("SQLC3_name", "MSRP");//$NON-NLS-1$ //$NON-NLS-2$
      report.getParameterValues().put("ShowCol", "Show_Column3");//$NON-NLS-1$ //$NON-NLS-2$
      report.getParameterValues().put("productline", "Motorcycles");//$NON-NLS-1$ //$NON-NLS-2$

      CSVReportUtil.createCSV(report, outputFilename);
      //    Comparing the File just generated with the golden version
      isEqual = contentsEqual(inputFileName, outputFilename, false);

    } catch (Throwable t) {
      t.printStackTrace();
    }
    assertTrue(isEqual);
  }
}

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
import java.io.IOException;

import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.PdfReportUtil;
import org.pentaho.reporting.engine.classic.core.modules.output.table.csv.CSVReportUtil;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.HtmlReportUtil;
import org.pentaho.reporting.engine.classic.core.modules.output.table.xls.ExcelReportUtil;
import org.pentaho.reporting.engine.classic.testcases.BaseTest;


public class CalculatedColumnsReportTest extends BaseTest {

  public void testCalculatedColumnsReportInPdf() {
    String reportTemplateFilename = getReportDefinitionPath() + "/calculated.xml"; //$NON-NLS-1$
    String inputFileName = getReportInputPath() + "/pdf/CalculatedColumnReport.pdf"; //$NON-NLS-1$
    String outputFilename = getReportOutputPath() + "/CalculatedColumnReport.pdf"; //$NON-NLS-1$
    String logFile = getReportOutputPath() + "/CalculatedColumnReportLogFilePdf.txt"; //$NON-NLS-1$
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
      CalculatedColumnsReportData reportData = new CalculatedColumnsReportData();
      TableDataFactory tableDataFactory = new TableDataFactory("default", reportData);//$NON-NLS-1$
      report.setDataFactory(tableDataFactory);
      FileOutputStream outputStream = new FileOutputStream(new File(outputFilename));
      PdfReportUtil.createPDF(report, outputStream);
      isEqual = comparePdf(inputFileName, outputFilename, logFile);
      // Done!
    } catch (Throwable t) {
      t.printStackTrace();
    }
    assertTrue(isEqual);
  }

  public void testCalculatedColumnsReportInHtml() {
    String reportTemplateFilename = getReportDefinitionPath() + "/calculated.xml"; //$NON-NLS-1$
    String inputFileName = getReportInputPath() + "/html/CalculatedColumnReport.html"; //$NON-NLS-1$
    String outputFilename = getReportOutputPath() + "/CalculatedColumnReport.html"; //$NON-NLS-1$
    // String logFile = reportOutputPath + "/CalculatedColumnReportLogFileHtml.txt"; //$NON-NLS-1$    
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
      CalculatedColumnsReportData reportData = new CalculatedColumnsReportData();
      TableDataFactory tableDataFactory = new TableDataFactory("default", reportData);//$NON-NLS-1$
      report.setDataFactory(tableDataFactory);
      FileOutputStream outputStream = new FileOutputStream(new File(outputFilename));
      HtmlReportUtil.createStreamHTML(report, outputStream);
      //    Comparing the File just generated with the golden version
      isEqual = contentsEqual(inputFileName, outputFilename, true);
      //isEqual = compareHtml(inputFileName, outputFilename, logFile);
      
    } catch (Throwable t) {
      t.printStackTrace();
    }
    assertTrue(isEqual);
  }

  public void testCalculatedColumnsReportInExcel() {
    String reportTemplateFilename = getReportDefinitionPath() + "/calculated.xml"; //$NON-NLS-1$
    String inputFileName = getReportInputPath() + "/xls/CalculatedColumnReport.xls"; //$NON-NLS-1$
    String outputFilename = getReportOutputPath() + "/CalculatedColumnReport.xls"; //$NON-NLS-1$
    String logFile = getReportOutputPath() + "/CalculatedColumnReportLogFileXls.txt"; //$NON-NLS-1$
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
      CalculatedColumnsReportData reportData = new CalculatedColumnsReportData();
      TableDataFactory tableDataFactory = new TableDataFactory("default", reportData);//$NON-NLS-1$
      report.setDataFactory(tableDataFactory);
      FileOutputStream outputStream = new FileOutputStream(new File(outputFilename));

      ExcelReportUtil.createXLS(report, outputStream);
      //    Comparing the File just generated with the golden version
      isEqual = compareXLS(inputFileName, outputFilename, logFile);

    } catch (Throwable t) {
      t.printStackTrace();
    }
    assertTrue(isEqual);
  }

  public void testCalculatedColumnsReportInCsv() {
    String reportTemplateFilename = getReportDefinitionPath() + "/calculated.xml"; //$NON-NLS-1$
    String inputFileName = getReportInputPath() + "/csv/CalculatedColumnReport.csv"; //$NON-NLS-1$ 
    String outputFilename = getReportOutputPath() + "/CalculatedColumnReport.csv"; //$NON-NLS-1$
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
      CalculatedColumnsReportData reportData = new CalculatedColumnsReportData();
      TableDataFactory tableDataFactory = new TableDataFactory("default", reportData);//$NON-NLS-1$
      report.setDataFactory(tableDataFactory);
      CSVReportUtil.createCSV(report, outputFilename);
      //    Comparing the File just generated with the golden version
      isEqual = contentsEqual(inputFileName, outputFilename, false);

    } catch (Throwable t) {
      t.printStackTrace();
    }
    assertTrue(isEqual);
  }
  
  public static void main(String[] args) throws IOException
  {
    CalculatedColumnsReportTest test = new CalculatedColumnsReportTest();
    try {
      test.setUp();
      test.testCalculatedColumnsReportInPdf();
      test.testCalculatedColumnsReportInHtml();
      test.testCalculatedColumnsReportInExcel();
      test.testCalculatedColumnsReportInCsv();
    } finally {
      test.tearDown();
      BaseTest.shutdown();
    }
  }
}

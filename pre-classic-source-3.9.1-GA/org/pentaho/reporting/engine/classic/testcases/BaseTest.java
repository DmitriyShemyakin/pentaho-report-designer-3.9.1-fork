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
 * Copyright (c) 2006 - 2009 Pentaho Corporation..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.testcases;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import com.lowagie.text.pdf.PdfReader;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.XMLTestCase;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.demo.util.ReportDefinitionException;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

public abstract class BaseTest extends XMLTestCase
{

  private String reportDefinitionPath;
  private String reportInputPath;
  private String reportOutputPath;

  public BaseTest(String arg0)
  {
    super(arg0);
  }

  public BaseTest()
  {
  }

  public String getReportDefinitionPath()
  {
    return reportDefinitionPath;
  }

  public String getReportInputPath()
  {
    return reportInputPath;
  }

  public String getReportOutputPath()
  {
    return reportOutputPath;
  }

  public void setUp() throws IOException
  {
    ClassicEngineBoot.getInstance().start();
    Properties props = new Properties();
    final InputStream inStream = ObjectUtilities.getResourceAsStream
        ("testsettings.properties", BaseTest.class);
    try
    {
      props.load(inStream);//$NON-NLS-1$
    }
    finally
    {
      inStream.close();
    }
    reportDefinitionPath = props.getProperty("REPORT_DEFINITION_PATH"); //$NON-NLS-1$
    reportInputPath = props.getProperty("REPORT_INPUT_PATH"); //$NON-NLS-1$
    reportOutputPath = props.getProperty("REPORT_OUTPUT_PATH"); //$NON-NLS-1$
  }

  public void tearDown()
  {
  }

  public static void shutdown()
  {

  }

  protected void startTest()
  {
  }

  public void dispose()
  {
  }

  protected void finishTest()
  {
    dispose();
  }

  protected static MasterReport parseReport(File reportFile) throws Exception
  {
    if (reportFile == null)
    {
      throw new ReportDefinitionException("ReportDefinition Source is invalid");
    }

    try
    {
      ResourceManager manager = new ResourceManager();
      manager.registerDefaults();
      Resource res = manager.createDirectly(reportFile, MasterReport.class);
      return (MasterReport) res.getResource();
    }
    catch (Exception e)
    {
      throw new ReportDefinitionException("Parsing failed", e);
    }
  }

  protected static MasterReport parseReport(URL in) throws ReportDefinitionException
  {
    if (in == null)
    {
      throw new ReportDefinitionException("ReportDefinition Source is invalid");
    }

    try
    {
      ResourceManager manager = new ResourceManager();
      manager.registerDefaults();
      Resource res = manager.createDirectly(in, MasterReport.class);
      return (MasterReport) res.getResource();
    }
    catch (Exception e)
    {
      throw new ReportDefinitionException("Parsing failed", e);
    }
  }

  protected static boolean comparePdf(String inputFileName1, String inputFileName2, String outputFileName)
      throws Exception
  {
    // Comparison Routine
    boolean equal = true;
    PdfReader reader1 = new PdfReader(inputFileName1);
    PdfReader reader2 = new PdfReader(inputFileName2);
    FileOutputStream outputStream = new FileOutputStream(outputFileName);
/*    if (reader1.getFileLength() != reader2.getFileLength()) {
      equal = false;
      String dataToWrite = "Files are of different length. One is " + reader1.getFileLength() + " and the other is  " + reader2.getFileLength() + "\n"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      outputStream.write(dataToWrite.getBytes());
    } else */
    if (reader1.getNumberOfPages() != reader2.getNumberOfPages())
    {
      equal = false;
      String dataToWrite = "Number of pages are different in both files. One has " + reader1.getNumberOfPages() + " and the other has  " + reader2.getNumberOfPages() + "\n"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      outputStream.write(dataToWrite.getBytes());
    }
    else
    {
      for (int i = 1; i <= reader1.getNumberOfPages(); i++)
      {
        byte[] data1 = reader1.getPageContent(i);
        byte[] data2 = reader2.getPageContent(i);
        for (int j = 0; i < data1.length; i++)
        {
          if (data1[j] != data2[j])
          {
            equal = false;
            String dataToWrite = "Page #" + i + "has different data. File 1 has" + data1[j] + "Whereas File 2 has " + data2[j] + "\n"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            outputStream.write(dataToWrite.getBytes());
          }
        }
      }
    }
    if (equal)
    {
      String dataToWrite = "Files are identical";//$NON-NLS-1$
      outputStream.write(dataToWrite.getBytes());
    }
    outputStream.flush();
    outputStream.close();
    reader1.close();
    reader2.close();
    return equal;
  }


  protected final boolean compareHtml(String inputFileName1, String inputFileName2, String outputFileName)
      throws Exception
  {

    DetailedDiff myDiff = new DetailedDiff(compareXML(readFileAsString(inputFileName1),
        readFileAsString(inputFileName2)));
    List allDifferences = myDiff.getAllDifferences();

    if (myDiff != null && myDiff.toString() != null)
    {
      FileOutputStream outputStream = new FileOutputStream(outputFileName);
      outputStream.write(myDiff.toString().getBytes());
      outputStream.flush();
      outputStream.close();
    }
    if (allDifferences.size() > 0)
    {
      return false;
    }
    else
    {
      return true;
    }
  }


  /**
   * Returns <code>true</code> if both input streams byte contents is identical.
   *
   * @param file1 first input to contents compare
   * @param file2 second input to contents compare
   * @return <code>true</code> if content is equal
   */
  protected static boolean contentsEqual(String file1, String file2, boolean ignoreWhitespace)
      throws Exception
  {
    FileInputStream is1 = new FileInputStream(file1);
    FileInputStream is2 = new FileInputStream(file2);
    try
    {
      if (is1 == is2)
      {
        return true;
      }
      if (is1 == null && is2 == null)
      { // no byte contents
        return true;
      }
      if (is1 == null || is2 == null)
      {// only one has contents
        return false;
      }
      while (true)
      {
        int c1 = is1.read();
        while (ignoreWhitespace && isWhitespace(c1))
        {
          c1 = is1.read();
        }
        int c2 = is2.read();
        while (ignoreWhitespace && isWhitespace(c2))
        {
          c2 = is2.read();
        }
        if (c1 == -1 && c2 == -1)
        {
          return true;
        }
        if (c1 != c2)
        {
          break;
        }
      }
    }
    catch (IOException ex)
    {
    }
    finally
    {
      try
      {
        try
        {
          if (is1 != null)
          {
            is1.close();
          }
        }
        finally
        {
          if (is2 != null)
          {
            is2.close();
          }
        }
      }
      catch (IOException e)
      {
        //    Ignore
      }
    }
    return false;
  }

  protected static boolean compareXLS(String file1, String file2, String file3) throws Exception
  {
    boolean equal = true;
    FileInputStream inputStream = null;
    FileInputStream inputStream2 = null;
    FileOutputStream outputStream = null;
    try
    {
      inputStream = new FileInputStream(new File(file1));
      inputStream2 = new FileInputStream(new File(file2));
      outputStream = new FileOutputStream(new File(file3));
      HSSFWorkbook hssf = new HSSFWorkbook(inputStream);
      HSSFWorkbook hssf2 = new HSSFWorkbook(inputStream2);
      // Check for the work book to be not empty
      // Check if the sheet count is same for both books
      if (hssf.getNumberOfSheets() != hssf2.getNumberOfSheets())
      {

        String dataToWrite = "Number of sheets are not equal" + "\n"; //$NON-NLS-1$ //$NON-NLS-2$
        outputStream.write(dataToWrite.getBytes());
        return false;
      }

      // Iterate through the sheets
      for (int i = 0; i < hssf.getNumberOfSheets(); i++)
      {
        HSSFSheet sheet1 = hssf.getSheetAt(i);
        HSSFSheet sheet2 = hssf2.getSheetAt(i);
        // check if the sheet is not null
        if (sheet1 == null || sheet2 == null)
        {
          equal = false;
          String dataToWrite = "One of the sheet is null" + "\n"; //$NON-NLS-1$ //$NON-NLS-2$
          outputStream.write(dataToWrite.getBytes());
          continue;
        }

        int rowCount1 = sheet1.getLastRowNum();
        int rowCount2 = sheet2.getLastRowNum();
        // check if rows are the same for both books
        if (rowCount1 != rowCount2)
        {
          equal = false;
          continue;
        }

        if (compareSheet(i, sheet1, sheet2, outputStream) == false)
        {
          equal = false;
        }
      }

      if (equal)
      {
        String dataToWrite = "Files are identical";//$NON-NLS-1$
        outputStream.write(dataToWrite.getBytes());
      }
    }
    catch (FileNotFoundException fnf)
    {
      fnf.printStackTrace();
    }
    finally
    {
      try
      {
        try
        {
          if (inputStream != null)
          {
            inputStream.close();
          }
        }
        finally
        {
          try
          {
            if (inputStream2 != null)
            {
              inputStream2.close();
            }
          }
          finally
          {
            if (outputStream != null)
            {
              outputStream.flush();
              outputStream.close();
            }
          }
        }

      }
      catch (IOException e)
      {
        //    Ignore
      }
    }

    return equal;
  }

  private static boolean compareSheet (final int sheetNumber,
                                final HSSFSheet sheet1, 
                                final HSSFSheet sheet2,
                                final OutputStream outputStream) throws IOException
  {
    boolean equal = true;

    // iterate through the rows
    int rowCount1 = sheet1.getLastRowNum();
    for (int j = sheet1.getFirstRowNum(); j <= rowCount1; j++)
    {
      HSSFRow row1 = sheet1.getRow(j);
      HSSFRow row2 = sheet2.getRow(j);
      // check if the rows are not null
      if (row1 == null && row2 == null)
      {
        continue;
      }
      
      if (row1 == null || row2 == null)
      {
        String dataToWrite = "Sheet #" + sheetNumber + "Row # " + j + "has a different data.File 1 Cell has " + row1 + "Whereas File2 has " + row2 + "\n"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        outputStream.write(dataToWrite.getBytes());
        return false;
      }

      Iterator it1 = row1.cellIterator();
      Iterator it2 = row2.cellIterator();
      while (it1.hasNext() && it2.hasNext())
      {
        HSSFCell cell1 = (HSSFCell) it1.next();
        HSSFCell cell2 = (HSSFCell) it2.next();
        if (cell1 == null || cell2 == null)
        {
          String dataToWrite = "Sheet # " + sheetNumber + "Row # " + j + "Cell # " + cell1.getCellNum() + "has a different data.. File 1 Cell has " + cell1 + "Whereas File2 has " + cell2 + "\n"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
          outputStream.write(dataToWrite.getBytes());
          return false;
        }

        HSSFCellStyle cell1Style = cell1.getCellStyle();
        HSSFCellStyle cell2Style = cell2.getCellStyle();
        if (cell1Style == null || cell2Style == null)
        {
          String dataToWrite = "Sheet # " + sheetNumber + "Row # " + j + "Cell # " + cell1.getCellNum() + "has a no cell style.. File 1 Cell has " + cell1 + "Whereas File2 has " + cell2 + "\n";
          outputStream.write(dataToWrite.getBytes());
          return false;
        }
        else
        {
          short cell1BorderBottom = cell1Style.getBorderBottom();
          short cell1Alignment = cell1Style.getAlignment();
          short cell1BorderLeft = cell1Style.getBorderLeft();
          short cell1BorderRight = cell1Style.getBorderRight();
          short cell1BorderTop = cell1Style.getBorderTop();
          short cell1BorderColor = cell1Style.getBottomBorderColor();
          short cell1DataFormat = cell1Style.getDataFormat();
          short cell1BackgroundColor = cell1Style.getFillBackgroundColor();
          short cell1FillForegroundColor = cell1Style.getFillForegroundColor();
          short cell1FillPattern = cell1Style.getFillPattern();
          short cell1LeftBorderColor = cell1Style.getLeftBorderColor();
          short cell1RightBorderColor = cell1Style.getRightBorderColor();
          short cell1VerticalAlignment = cell1Style.getVerticalAlignment();
          boolean cell1WrapText = cell1Style.getWrapText();
          short cell1Rotation = cell1Style.getRotation();

          short cell2BorderBottom = cell2Style.getBorderBottom();
          short cell2Alignment = cell2Style.getAlignment();
          short cell2BorderLeft = cell2Style.getBorderLeft();
          short cell2BorderRight = cell2Style.getBorderRight();
          short cell2BorderTop = cell2Style.getBorderTop();
          short cell2BorderColor = cell2Style.getBottomBorderColor();
          short cell2DataFormat = cell2Style.getDataFormat();
          short cell2BackgroundColor = cell2Style.getFillBackgroundColor();
          short cell2FillForegroundColor = cell2Style.getFillForegroundColor();
          short cell2FillPattern = cell2Style.getFillPattern();
          short cell2LeftBorderColor = cell2Style.getLeftBorderColor();
          short cell2RightBorderColor = cell2Style.getRightBorderColor();
          short cell2VerticalAlignment = cell2Style.getVerticalAlignment();
          boolean cell2WrapText = cell2Style.getWrapText();
          short cell2Rotation = cell2Style.getRotation();
          if (cell1BorderBottom != cell2BorderBottom)
          {
            equal = false;
            String dataToWrite = "Sheet # " + sheetNumber + "Row # " + j + "Cell # " + cell1.getCellNum() + "has a different Bottom Border" + "\n"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
            outputStream.write(dataToWrite.getBytes());
          }
          if (cell1Alignment != cell2Alignment)
          {
            equal = false;
            String dataToWrite = "Sheet # " + sheetNumber + "Row # " + j + "Cell # " + cell1.getCellNum() + "has a different Cell Alignment" + "\n"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
            outputStream.write(dataToWrite.getBytes());
          }
          if (cell1BorderLeft != cell2BorderLeft)
          {
            equal = false;
            String dataToWrite = "Sheet # " + sheetNumber + "Row # " + j + "Cell # " + cell1.getCellNum() + "has a different Left Border" + "\n"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
            outputStream.write(dataToWrite.getBytes());
          }
          if (cell1BorderRight != cell2BorderRight)
          {
            equal = false;
            String dataToWrite = "Sheet # " + sheetNumber + "Row # " + j + "Cell # " + cell1.getCellNum() + "has a different Right Border" + "\n"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
            outputStream.write(dataToWrite.getBytes());
          }
          if (cell1BorderTop != cell2BorderTop)
          {
            equal = false;
            String dataToWrite = "Sheet # " + sheetNumber + "Row # " + j + "Cell # " + cell1.getCellNum() + "has a different Top Border" + "\n"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
            outputStream.write(dataToWrite.getBytes());
          }
          if (cell1BorderColor != cell2BorderColor)
          {
            equal = false;
            String dataToWrite = "Sheet # " + sheetNumber + "Row # " + j + "Cell # " + cell1.getCellNum() + "has a different Border Color" + "\n";   //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
            outputStream.write(dataToWrite.getBytes());
          }
          if (cell1DataFormat != cell2DataFormat)
          {
            equal = false;
            String dataToWrite = "Sheet # " + sheetNumber + "Row # " + j + "Cell # " + cell1.getCellNum() + "has a different Data Format" + "\n"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
            outputStream.write(dataToWrite.getBytes());
          }
          if (cell1BackgroundColor != cell2BackgroundColor)
          {
            equal = false;
            String dataToWrite = "Sheet # " + sheetNumber + "Row # " + j + "Cell # " + cell1.getCellNum() + "has a different Background Color" + "\n"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
            outputStream.write(dataToWrite.getBytes());
          }
          if (cell1FillForegroundColor != cell2FillForegroundColor)
          {
            equal = false;
            String dataToWrite = "Sheet # " + sheetNumber + "Row # " + j + "Cell # " + cell1.getCellNum() + "has a different Foreground Color" + "\n"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
            outputStream.write(dataToWrite.getBytes());
          }
          if (cell1FillPattern != cell2FillPattern)
          {
            equal = false;
            String dataToWrite = "Sheet # " + sheetNumber + "Row # " + j + "Cell # " + cell1.getCellNum() + "has a different Fill Pattern" + "\n"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
            outputStream.write(dataToWrite.getBytes());
          }
          if (cell1LeftBorderColor != cell2LeftBorderColor)
          {
            equal = false;
            String dataToWrite = "Sheet # " + sheetNumber + "Row # " + j + "Cell # " + cell1.getCellNum() + "has a different Left Border Color" + "\n";   //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
            outputStream.write(dataToWrite.getBytes());
          }
          if (cell2RightBorderColor != cell1RightBorderColor)
          {
            equal = false;
            String dataToWrite = "Sheet # " + sheetNumber + "Row # " + j + "Cell # " + cell1.getCellNum() + "has a different Right Border Color" + "\n";   //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
            outputStream.write(dataToWrite.getBytes());
          }
          if (cell1VerticalAlignment != cell2VerticalAlignment)
          {
            equal = false;
            String dataToWrite = "Sheet # " + sheetNumber + "Row # " + j + "Cell # " + cell1.getCellNum() + "has a different Vertical Alignment" + "\n"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
            outputStream.write(dataToWrite.getBytes());
          }
          if (cell1Rotation != cell2Rotation)
          {
            equal = false;
            String dataToWrite = "Sheet # " + sheetNumber + "Row # " + j + "Cell # " + cell1.getCellNum() + "has a different Cell Rotation" + "\n"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
            outputStream.write(dataToWrite.getBytes());
          }
          if (cell1WrapText != cell2WrapText)
          {
            equal = false;
            String dataToWrite = "Sheet # " + sheetNumber + "Row # " + j + "Cell # " + cell1.getCellNum() + "has a different Wrap Text" + "\n"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
            outputStream.write(dataToWrite.getBytes());
          }
        }
        String cell1Value = cell1.toString();
        String cell2Value = cell2.toString();
        if (cell1Value != null && cell2Value != null)
        {
          if (cell1Value.equals(cell2Value) == false)
          {
            equal = false;
            String dataToWrite = "Sheet # " + sheetNumber + "Row # " + j + "Cell # " + cell1.getCellNum() + "has a different data. File 1 Cell has " + cell1Value + "Whereas File2 has " + cell2Value + "\n"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
            outputStream.write(dataToWrite.getBytes());
          }
        }
        else
        {
          equal = false;
          String dataToWrite = "Sheet # " + sheetNumber + "Row # " + j + "Cell # " + cell1.getCellNum() + "has a different data. File 1 Cell has " + cell1Value + "Whereas File2 has " + cell2Value + "\n"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
          outputStream.write(dataToWrite.getBytes());
        }
      }
    }
    return equal;
  }

  private static boolean isWhitespace(int c)
  {
    if (c == -1)
    {
      return false;
    }
    return Character.isWhitespace((char) c);
  }

  protected static String readFileAsString(String filePath)
      throws java.io.IOException
  {
    StringBuilder fileData = new StringBuilder(1000);
    BufferedReader reader = new BufferedReader(
        new FileReader(filePath));
    char[] buf = new char[1024];
    int numRead = 0;
    while ((numRead = reader.read(buf)) != -1)
    {
      String readData = String.valueOf(buf, 0, numRead);
      fileData.append(readData);
      buf = new char[1024];
    }
    reader.close();
    return fileData.toString();
  }
}

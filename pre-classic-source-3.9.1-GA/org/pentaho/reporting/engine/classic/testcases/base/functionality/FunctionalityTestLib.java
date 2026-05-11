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
 * Copyright (c) 2000 - 2009 Pentaho Corporation, Simba Management Limited and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.testcases.base.functionality;

import java.io.OutputStream;
import java.util.ArrayList;

import org.pentaho.reporting.engine.classic.core.EmptyReportException;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportParameterValidationException;
import org.pentaho.reporting.engine.classic.demo.ancient.demo.DemoFrontend;
import org.pentaho.reporting.engine.classic.demo.ancient.demo.layouts.ComponentDrawingDemoHandler;
import org.pentaho.reporting.engine.classic.demo.ancient.demo.layouts.internalframe.InternalFrameDrawingDemoHandler;
import org.pentaho.reporting.engine.classic.demo.ancient.demo.functions.PaintComponentDemoHandler;
import org.pentaho.reporting.engine.classic.demo.ancient.demo.huge.VeryLargeReportDemo;
import org.pentaho.reporting.engine.classic.demo.features.loading.FileLoadingDemo;
import org.pentaho.reporting.engine.classic.demo.util.DemoHandler;
import org.pentaho.reporting.engine.classic.demo.util.DemoSelector;
import org.pentaho.reporting.engine.classic.demo.util.InternalDemoHandler;
import org.pentaho.reporting.engine.classic.demo.util.XmlDemoHandler;
import org.pentaho.reporting.engine.classic.testcases.base.functionality.demohandler.EmptyReportTestHandler;
import org.pentaho.reporting.engine.classic.testcases.base.functionality.demohandler.FontNotExistCrashTestHandler;
import org.pentaho.reporting.engine.classic.testcases.base.functionality.demohandler.SubReportProcessingCrashTestHandler;
import org.pentaho.reporting.engine.classic.testcases.base.functionality.demohandler.TextAlignmentFailureTestHandler;
import org.pentaho.reporting.engine.classic.testcases.base.functionality.demohandler.VerticalAlignmentFailureTestHandler;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.graphics.PrintReportProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.PdfOutputProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.plaintext.PlainTextReportUtil;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.base.PageableReportProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.csv.CSVReportUtil;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.HtmlReportUtil;
import org.pentaho.reporting.engine.classic.core.modules.output.table.rtf.RTFReportUtil;
import org.pentaho.reporting.engine.classic.core.modules.output.table.xls.ExcelReportUtil;
import org.pentaho.reporting.engine.classic.core.util.NullOutputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FunctionalityTestLib
{
  private static final Log logger = LogFactory.getLog(FunctionalityTestLib.class);
  private FunctionalityTestLib()
  {
  }

  public static InternalDemoHandler[] getAllDemoHandlers()
  {
    final DemoSelector selector = DemoFrontend.createDemoInfo();
    final ArrayList demos = new ArrayList();
    collectDemos(demos, selector);
    return (InternalDemoHandler[]) demos.toArray(
            new InternalDemoHandler[demos.size()]);
  }

  private static void collectDemos(final ArrayList list, final DemoSelector selector)
  {
    final DemoHandler[] demoHandlers = selector.getDemos();
    for (int i = 0; i < demoHandlers.length; i++)
    {
      final DemoHandler demoHandler = demoHandlers[i];
      if (demoHandler instanceof InternalDemoHandler)
      {
        if (demoHandler instanceof VeryLargeReportDemo)
        {
          // Insanely large; slows down the whole run. Test separately if you have to, or use
          // the performance test suite.
          continue;
        }
        if (demoHandler instanceof PaintComponentDemoHandler)
        {
          // danger of deadlock due to a JDK bug when rendering JColorPanes.
          continue;
        }
        if (demoHandler instanceof ComponentDrawingDemoHandler)
        {
          // danger of deadlock due to a JDK bug when rendering JColorPanes.
          continue;
        }
        if (demoHandler instanceof InternalFrameDrawingDemoHandler)
        {
          // danger of deadlock due to a JDK bug when rendering JColorPanes.
          continue;
        }
        if (demoHandler instanceof FileLoadingDemo)
        {
          // danger of deadlock due to a JDK bug when rendering JColorPanes.
          continue;
        }
        list.add(demoHandler);
      }
    }

    list.add(new FontNotExistCrashTestHandler());
    //list.add(new PRE34());
    list.add(new SubReportProcessingCrashTestHandler());
    list.add(new TextAlignmentFailureTestHandler());
    list.add(new VerticalAlignmentFailureTestHandler());
    list.add(new EmptyReportTestHandler());

    final DemoSelector[] childs = selector.getChilds();
    for (int i = 0; i < childs.length; i++)
    {
      final DemoSelector child = childs[i];
      collectDemos(list, child);
    }
  }


  public static XmlDemoHandler[] getAllXmlDemoHandlers()
  {
    final DemoSelector selector = DemoFrontend.createDemoInfo();
    final ArrayList demos = new ArrayList();
    collectXmlDemos(demos, selector);
    return (XmlDemoHandler[]) demos.toArray(new XmlDemoHandler[demos.size()]);
  }

  private static void collectXmlDemos(final ArrayList list, final DemoSelector selector)
  {
    final DemoHandler[] demoHandlers = selector.getDemos();
    for (int i = 0; i < demoHandlers.length; i++)
    {
      final DemoHandler demoHandler = demoHandlers[i];
      if (demoHandler instanceof XmlDemoHandler)
      {
        if (demoHandler instanceof VeryLargeReportDemo == false)
        {
          list.add(demoHandler);
        }
      }
    }

    list.add(new FontNotExistCrashTestHandler());
    //list.add(new PRE34());
    list.add(new TextAlignmentFailureTestHandler());
    list.add(new VerticalAlignmentFailureTestHandler());

    final DemoSelector[] childs = selector.getChilds();
    for (int i = 0; i < childs.length; i++)
    {
      final DemoSelector child = childs[i];
      collectXmlDemos(list, child);
    }
  }

  public static boolean createPlainText(final MasterReport report)
  {
    try
    {
      PlainTextReportUtil.createPlainText(report, new NullOutputStream(), 10, 15);
      return true;
    }
    catch (ReportParameterValidationException p)
    {
      return true;
    }
    catch (Exception rpe)
    {
      logger.debug("Failed to execute plain text: ", rpe);
      return false;
    }
  }

  public static void createRTF(final MasterReport report)
          throws Exception
  {
    try
    {
      RTFReportUtil.createRTF(report, new NullOutputStream());
    }
    catch(IndexOutOfBoundsException ibe)
    {
      // this is a known iText bug that does not get fixed.
    }
    catch (ReportParameterValidationException p)
    {
      // reports that have mandatory parameters are ok to fail.
    }
  }

  public static void createCSV(final MasterReport report)
          throws Exception
  {
    try
    {
      CSVReportUtil.createCSV(report, new NullOutputStream(), null);
    }
    catch (ReportParameterValidationException e)
    {

    }
  }

  public static void createXLS(final MasterReport report)
          throws Exception
  {
    try
    {
    ExcelReportUtil.createXLS(report, new NullOutputStream());
    }
    catch (ReportParameterValidationException e)
    {

    }
  }

  public static void createStreamHTML(final MasterReport report)
          throws Exception
  {
    try
    {
    HtmlReportUtil.createStreamHTML(report, new NullOutputStream());
    }
    catch (ReportParameterValidationException e)
    {

    }
  }

  public static void createZIPHTML(final MasterReport report)
          throws Exception
  {
    try
    {
    HtmlReportUtil.createZIPHTML(report, new NullOutputStream(), "report.html");
    }
    catch (ReportParameterValidationException e)
    {

    }
  }

  public static boolean execGraphics2D(final MasterReport report)
  {
    try
    {
      final PrintReportProcessor proc = new PrintReportProcessor(report);
      final int nop = proc.getNumberOfPages();
      if (proc.isError())
      {
        if (proc.getErrorReason() instanceof ReportParameterValidationException)
        {
          return true;
        }
        return false;
      }
      if (nop == 0)
      {
        return false;
      }
      for (int i = 0; i < nop; i++)
      {
        if (proc.getPageDrawable(i) == null)
        {
          return false;
        }
      }
      proc.close();
      return true;
    }
    catch (ReportParameterValidationException p)
    {
      // reports that have mandatory parameters are ok to fail.
      return true;
    }
    catch (EmptyReportException ere)
    {
      return true;
    }
    catch (Exception e)
    {
      logger.error("Generating Graphics2D failed.", e);
      return false;
    }
  }

  /**
   * Saves a report to PDF format.
   *
   * @param report the report.
   * @return true or false.
   */
  public static boolean createPDF(final MasterReport report)
  {
    OutputStream out = new NullOutputStream();
    try
    {
      final PdfOutputProcessor outputProcessor = new PdfOutputProcessor(report.getConfiguration(), out, report.getResourceManager());
      final PageableReportProcessor proc = new PageableReportProcessor(report, outputProcessor);
      proc.processReport();
      return true;
    }
    catch (ReportParameterValidationException e)
    {
     return true;
    }
    catch (Exception e)
    {
      logger.error("Writing PDF failed.", e);
      return false;
    }
  }
}

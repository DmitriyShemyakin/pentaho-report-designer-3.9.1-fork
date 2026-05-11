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
 * Copyright (c) 2007 - 2009 Pentaho Corporation, .  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.testcases.output.xml;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.demo.ancient.demo.huge.VeryLargeReportDemo;
import org.pentaho.reporting.engine.classic.demo.util.ReportDefinitionException;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.base.PageableReportProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.FlowReportProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.StreamReportProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.HtmlReportUtil;

/**
 * Utility class to provide an easy to use default implementation of RTF exports.
 *
 * @author Thomas Morgner
 */
public final class XmlDebugReportUtil
{
  /**
   * Default Constructor.
   */
  private XmlDebugReportUtil()
  {
  }

  public static void createStreamTable (final MasterReport report, final String filename)
          throws IOException, ReportProcessingException
  {
    OutputStream fout = new BufferedOutputStream(new FileOutputStream(filename));
    try
    {
      createStreamTable(report, fout);
      fout.close();
      fout = null;
    }
    finally
    {
      if (fout != null)
      {
        try
        {
          fout.close();
        }
        catch(Exception e)
        {
          // ignore
        }
      }
    }
  }

  public static void createStreamTable(final MasterReport report, final OutputStream outputStream)
      throws ReportProcessingException
  {
    final XmlTableDebugOutputProcessor target = new XmlTableDebugOutputProcessor(report.getConfiguration(), outputStream, false);
    final StreamReportProcessor proc = new StreamReportProcessor(report, target);
    proc.processReport();
    proc.close();
  }


  public static void createFlowTable (final MasterReport report, final String filename)
          throws IOException, ReportProcessingException
  {
    OutputStream fout = new BufferedOutputStream(new FileOutputStream(filename));
    try
    {
      createFlowTable(report, fout);
      fout.close();
      fout = null;
    }
    finally
    {
      if (fout != null)
      {
        try
        {
          fout.close();
        }
        catch(Exception e)
        {
          // ignore
        }
      }
    }
  }

  public static void createFlowTable(final MasterReport report, final OutputStream outputStream)
      throws ReportProcessingException
  {
    final XmlTableDebugOutputProcessor target = new XmlTableDebugOutputProcessor(report.getConfiguration(), outputStream, true);
    final FlowReportProcessor proc = new FlowReportProcessor(report, target);
    proc.processReport();
    proc.close();
  }


  /**
   * Saves a report to rich-text format (RTF).
   *
   * @param report   the report.
   * @param filename target file name.
   * @throws org.pentaho.reporting.engine.classic.core.ReportProcessingException if the report processing failed.
   * @throws java.io.IOException               if there was an IOerror while processing the
   *                                   report.
   */
  public static void createPageable (final MasterReport report, final String filename)
          throws IOException, ReportProcessingException
  {
    OutputStream fout = new BufferedOutputStream(new FileOutputStream(filename));
    try
    {
      createPageable(report, fout);
      fout.close();
      fout = null;
    }
    finally
    {
      if (fout != null)
      {
        try
        {
          fout.close();
        }
        catch(Exception e)
        {
          // ignore
        }
      }
    }
  }

  public static void createPageable(final MasterReport report, final OutputStream outputStream)
      throws ReportProcessingException
  {
    final XmlPageDebugOutputProcessor target = new XmlPageDebugOutputProcessor(report.getConfiguration(), outputStream);
    final PageableReportProcessor proc = new PageableReportProcessor(report, target);
    proc.processReport();
    proc.close();
  }

  public static void main(String[] args) throws ReportDefinitionException, ReportProcessingException, IOException
  {
    ClassicEngineBoot.getInstance().start();
    //final BookstoreDemo demo = new BookstoreDemo();
    final VeryLargeReportDemo demo = new VeryLargeReportDemo();
    //final GroupsDemo demo = new GroupsDemo();
    final MasterReport report = demo.createReport();
//    createPageable(report, "/tmp/report-pageable.xml");
//    createStreamTable(report, "/tmp/report-stream.xml");
//    createFlowTable(report, "/tmp/report-flow.xml");
    HtmlReportUtil.createDirectoryHTML(report, "/tmp/report-flow.html");
  }
}

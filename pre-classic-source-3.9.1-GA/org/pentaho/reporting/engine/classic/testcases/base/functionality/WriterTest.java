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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.net.URL;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.demo.util.XmlDemoHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.ReportGenerator;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.base.ArrayClassFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.base.URLClassFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.datasource.DefaultDataSourceFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.elements.DefaultElementFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.objects.BandLayoutClassFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.objects.DefaultClassFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.stylekey.DefaultStyleKeyFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.stylekey.PageableLayoutStyleKeyFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.templates.DefaultTemplateCollection;
import org.pentaho.reporting.engine.classic.core.modules.parser.extwriter.ReportConverter;
import org.pentaho.reporting.engine.classic.core.modules.parser.extwriter.ReportWriter;
import org.pentaho.reporting.engine.classic.core.modules.parser.extwriter.ReportWriterException;
import org.xml.sax.InputSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class WriterTest extends TestCase
{
  private static final Log logger = LogFactory.getLog(WriterTest.class);

  public WriterTest(final String s)
  {
    super(s);
    ClassicEngineBoot.getInstance().start();
  }

  protected void setUp() throws Exception
  {
    ClassicEngineBoot.getInstance().start();
  }

  public void testConvertReport() throws Exception
  {
    final ReportConverter rc = new ReportConverter();
    XmlDemoHandler[] handlers = FunctionalityTestLib.getAllXmlDemoHandlers();
    for (int i = 0; i < handlers.length; i++)
    {
      XmlDemoHandler handler = handlers[i];
      final URL url = handler.getReportDefinitionSource();
      assertNotNull("Failed to locate " + url, url);

      final ByteArrayOutputStream bo = new ByteArrayOutputStream();
      try
      {
        rc.convertReport(url, url,
            new OutputStreamWriter (bo, "UTF-16"), "UTF-16");
        final byte[] buf = bo.toByteArray();
        final String s = new String(buf, "UTF-16");
        final ByteArrayInputStream bin = new ByteArrayInputStream(buf);
        ReportGenerator.getInstance().parseReport(new InputSource(bin), url);
      }
      catch (Exception e)
      {
        logger.debug("Failed to write or parse " + url, e);
        logger.debug (bo.toString("UTF-16"));
        fail();
      }
    }
  }

  public void testWriteReport() throws Exception
  {
    XmlDemoHandler[] handlers = FunctionalityTestLib.getAllXmlDemoHandlers();
    for (int i = 0; i < handlers.length; i++)
    {
      XmlDemoHandler handler = handlers[i];
      final URL url = handler.getReportDefinitionSource();
      assertNotNull("Failed to locate " + url, url);

      final ByteArrayOutputStream bo = new ByteArrayOutputStream();
      MasterReport report = null;
      try
      {
        report = ReportGenerator.getInstance().parseReport(url);
//        ReportBuilderHints ph = report.getReportBuilderHints();
//        if (ph == null)
//        {
//          continue;
//        }
//        String type = (String) ph.getHint(report, "parser.type", String.class);
//        if (type == null)
//        {
//          continue;
//        }
      }
      catch (Exception e)
      {
        logger.debug("Failed to parse " + url, e);
        fail();
      }
      try
      {
        final ReportWriter writer = new ReportWriter
          (report, "UTF-16", ReportWriter.createDefaultConfiguration(report));
        writer.addClassFactoryFactory(new URLClassFactory());
        writer.addClassFactoryFactory(new DefaultClassFactory());
        writer.addClassFactoryFactory(new BandLayoutClassFactory());
        writer.addClassFactoryFactory(new ArrayClassFactory());

        writer.addStyleKeyFactory(new DefaultStyleKeyFactory());
        writer.addStyleKeyFactory(new PageableLayoutStyleKeyFactory());
        writer.addTemplateCollection(new DefaultTemplateCollection());
        writer.addElementFactory(new DefaultElementFactory());
        writer.addDataSourceFactory(new DefaultDataSourceFactory());

        final OutputStreamWriter owriter = new OutputStreamWriter (bo, "UTF-16");
        writer.write(owriter);
        owriter.close();
      }
      catch (Exception e)
      {
        logger.debug("Failed to write " + url, e);
        fail();
      }

      try
      {
        final ByteArrayInputStream bin = new ByteArrayInputStream(bo.toByteArray());
        ReportGenerator.getInstance().parseReport(new InputSource(bin), url);
      }
      catch (Exception e)
      {
        logger.debug("Failed to (re)parse " + url, e);
        logger.debug (bo.toString("UTF-16"));
        fail();
      }
    }
  }


  public static void main(String[] args)
      throws IOException, ReportWriterException
  {
    ClassicEngineBoot.getInstance().start();
    final MasterReport report = new MasterReport();
    final ReportWriter writer = new ReportWriter
      (report, "UTF-16", ReportWriter.createDefaultConfiguration(report));
    writer.addClassFactoryFactory(new URLClassFactory());
    writer.addClassFactoryFactory(new DefaultClassFactory());
    writer.addClassFactoryFactory(new BandLayoutClassFactory());
    writer.addClassFactoryFactory(new ArrayClassFactory());

    writer.addStyleKeyFactory(new DefaultStyleKeyFactory());
    writer.addStyleKeyFactory(new PageableLayoutStyleKeyFactory());
    writer.addTemplateCollection(new DefaultTemplateCollection());
    writer.addElementFactory(new DefaultElementFactory());
    writer.addDataSourceFactory(new DefaultDataSourceFactory());

    final OutputStreamWriter owriter = new OutputStreamWriter (System.out, "UTF-8");
    writer.write(owriter);
    owriter.close();

  }
}

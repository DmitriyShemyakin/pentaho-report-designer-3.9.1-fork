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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.demo.util.XmlDemoHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.ReportGenerator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ParseTest extends TestCase
{
  private static final Log logger = LogFactory.getLog(ParseTest.class);

  public ParseTest(final String s)
  {
    super(s);
  }

  protected void setUp() throws Exception
  {
    ClassicEngineBoot.getInstance().start();
  }

  /**
   * Parses, clones and serializes the reports ..
   * @throws Exception
   */
  public void testParseReport() throws Exception
  {
    final XmlDemoHandler[] handlers = FunctionalityTestLib.getAllXmlDemoHandlers();
    for (int i = 0; i < handlers.length; i++)
    {
      final XmlDemoHandler handler = handlers[i];
      final URL url = handler.getReportDefinitionSource();
      assertNotNull("Failed to locate " + url, url);
      try
      {
        final MasterReport report = ReportGenerator.getInstance().parseReport(url);
        assertNotNull(report);
        report.clone();

        final ByteArrayOutputStream bo = new ByteArrayOutputStream();
        final ObjectOutputStream out = new ObjectOutputStream(bo);
        out.writeObject(report);

        final ObjectInputStream oin = new ObjectInputStream
          (new ByteArrayInputStream(bo.toByteArray()));
        final MasterReport e2 = (MasterReport) oin.readObject();
        assertNotNull(e2); // cannot assert equals, as this is not implemented.

      }
      catch (Exception e)
      {
        logger.debug("Failed to parse " + url, e);
        fail();
      }
    }
  }
}

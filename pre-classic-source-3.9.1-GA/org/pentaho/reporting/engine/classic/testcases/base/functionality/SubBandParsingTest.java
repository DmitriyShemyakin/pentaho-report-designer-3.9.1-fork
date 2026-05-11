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

import java.net.URL;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.ReportGenerator;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

/**
 * @deprecated
 */
public class SubBandParsingTest extends TestCase
{
  public SubBandParsingTest()
  {
  }

  public SubBandParsingTest(final String s)
  {
    super(s);
  }

  protected void setUp() throws Exception
  {
    ClassicEngineBoot.getInstance().start();
  }

  public void testParsing()
  {
    try
    {
      final URL url = ObjectUtilities.getResourceRelative
          ("resources/subband.xml", SubBandParsingTest.class);
      assertNotNull(url);
      final MasterReport report = ReportGenerator.getInstance().parseReport(url);

      final Band band = report.getReportHeader();
      assertEquals(2, band.getElementCount());
      for (int i = 0; i < 2; i++)
      {
        final Band subband = (Band) band.getElement(i);
        assertEquals(2, subband.getElementCount());
        for (int x = 0; x < 2; x++)
        {
          final Band bandLowest = (Band) subband.getElement(x);
          assertTrue(bandLowest.getElementCount() > 0);
        }
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  public void testPreview()
  {
    try
    {
      final URL url = ObjectUtilities.getResourceRelative
          ("resources/subband.xml", SubBandParsingTest.class);
      assertNotNull(url);
      final MasterReport report = ReportGenerator.getInstance().parseReport(url);
      assertTrue(FunctionalityTestLib.execGraphics2D(report));
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }

//    final ReportWriter rc = new ReportWriter
//        (report, "UTF-8", ReportWriter.createDefaultConfiguration(report));
//    final OutputStreamWriter owriter = new OutputStreamWriter (System.out, "UTF-8");
//    rc.write(owriter);
//    owriter.close();
  }
}

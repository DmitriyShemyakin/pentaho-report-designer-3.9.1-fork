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

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.demo.util.InternalDemoHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ExportTest extends TestCase
{
  private static final Log logger = LogFactory.getLog(ExportTest.class);
  public ExportTest(final String s)
  {
    super(s);
    ClassicEngineBoot.getInstance().start();
  }

  public void testConvertReport() throws Exception
  {
    final InternalDemoHandler[] handlers = FunctionalityTestLib.getAllDemoHandlers();
    for (int i = 0; i < handlers.length; i++)
    {
      final InternalDemoHandler handler = handlers[i];

//      if (handler instanceof StackedLayoutXMLDemoHandler == false &&
//          handler instanceof StackedLayoutAPIDemoHandler == false)
//      {
//        continue;
//      }
      final MasterReport report = handler.createReport();
      logger.debug ("Processing " + handler.getDemoName());
      assertNotNull(report);

      logger.debug("   GRAPHICS2D ..");
      assertTrue(FunctionalityTestLib.execGraphics2D(report));
      logger.debug("   PDF ..");
      assertTrue(FunctionalityTestLib.createPDF(report));
      logger.debug("   CSV ..");
      FunctionalityTestLib.createCSV(report);
      logger.debug("   PLAIN_TEXT ..");
      assertTrue(FunctionalityTestLib.createPlainText(report));
// RTF will always crash, as the iText guys dont fix that bug ..
//      Log.debug("   RTF ..");
//      FunctionalityTestLib.createRTF(report);
      logger.debug("   STREAM_HTML ..");
      FunctionalityTestLib.createStreamHTML(report);
      logger.debug("   EXCEL ..");
      FunctionalityTestLib.createXLS(report);
      logger.debug("   ZIP_HTML ..");
      FunctionalityTestLib.createZIPHTML(report);
    }
  }


}

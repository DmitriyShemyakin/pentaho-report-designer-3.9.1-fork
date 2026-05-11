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

package org.pentaho.reporting.engine.classic.testcases.base.functionality;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.demo.util.InternalDemoHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Creation-Date: 05.08.2007, 12:24:47
 *
 * @author Thomas Morgner
 */
public abstract class AbstractReportExecuteTest extends TestCase
{
  private static final Log logger = LogFactory.getLog(AbstractReportExecuteTest.class);

  public AbstractReportExecuteTest(final String string)
  {
    super(string);
  }

  protected void setUp() throws Exception
  {
    ClassicEngineBoot.getInstance().start();
  }

  protected void performExportTest(final InternalDemoHandler handler) throws Exception
  {
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

  protected void performExportTest(final MasterReport report, final String name) throws Exception
  {
    logger.debug ("Processing " + name);
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

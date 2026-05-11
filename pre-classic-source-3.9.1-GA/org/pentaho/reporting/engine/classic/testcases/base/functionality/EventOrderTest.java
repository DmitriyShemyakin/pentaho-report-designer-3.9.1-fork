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

import java.awt.geom.Rectangle2D;
import javax.swing.table.DefaultTableModel;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ElementAlignment;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.demo.ancient.demo.groups.GroupsDemo;
import org.pentaho.reporting.engine.classic.core.elementfactory.LabelElementFactory;
import org.pentaho.reporting.engine.classic.core.event.PageEventListener;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.function.AbstractFunction;
import org.pentaho.reporting.engine.classic.core.function.EventMonitorFunction;
import org.pentaho.reporting.engine.classic.core.function.PageFunction;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This test relies on old and now invalid behaviour. It assumes that page-events are fired before any other
 * state-processing takes place. As with 0.8.9, page events are fired in the prepare-events, and the page-count
 * from the page-function is not in sync with the pages from the state.
 *
 * @deprecated 
 */
public class EventOrderTest extends TestCase
{
  private static final Log logger = LogFactory.getLog(EventOrderTest.class);

  public EventOrderTest()
  {
  }

  public EventOrderTest(final String s)
  {
    super(s);
  }

  protected void setUp() throws Exception
  {
    ClassicEngineBoot.getInstance().start();
  }

  private MasterReport getReport() throws Exception
  {
    final MasterReport report = new MasterReport();
    report.getReportHeader().addElement(LabelElementFactory.createLabelElement
        (null, new Rectangle2D.Float(0, 0, 150, 20), null,
            ElementAlignment.LEFT, null, "Text"));

    report.getReportFooter().addElement(LabelElementFactory.createLabelElement
        (null, new Rectangle2D.Float(0, 0, 150, 20), null,
            ElementAlignment.LEFT, null, "Text"));

    report.getPageHeader().addElement(LabelElementFactory.createLabelElement
        (null, new Rectangle2D.Float(0, 0, 150, 20), null,
            ElementAlignment.LEFT, null, "Text"));

    report.getPageFooter().addElement(LabelElementFactory.createLabelElement
        (null, new Rectangle2D.Float(0, 0, 150, 20), null,
            ElementAlignment.LEFT, null, "Text"));

    report.getItemBand().addElement(LabelElementFactory.createLabelElement
        (null, new Rectangle2D.Float(0, 0, 150, 20), null,
            ElementAlignment.LEFT, null, "Text"));

    report.getGroup(0).getHeader().addElement(LabelElementFactory.createLabelElement
        (null, new Rectangle2D.Float(0, 0, 150, 20), null,
            ElementAlignment.LEFT, null, "Text"));

    report.getGroup(0).getFooter().addElement(LabelElementFactory.createLabelElement
        (null, new Rectangle2D.Float(0, 0, 150, 20), null,
            ElementAlignment.LEFT, null, "Text"));

    report.addExpression(new EventOrderFunction("event-order"));
    report.addExpression(new EventMonitorFunction("event-monitor"));
    return report;
  }

  public void testEventOrder() throws Exception
  {
    setUp();
    final MasterReport report = getReport();
    final DefaultTableModel model = new DefaultTableModel(2, 1);
    model.setValueAt("0-0", 0, 0);
    model.setValueAt("0-1", 1, 0);
    report.setDataFactory(new TableDataFactory
        ("default", model));

    logger.debug("   GRAPHICS2D ..");
    assertTrue(FunctionalityTestLib.execGraphics2D(report));
    logger.debug("   PDF ..");
    assertTrue(FunctionalityTestLib.createPDF(report));
    logger.debug("   CSV ..");
    FunctionalityTestLib.createCSV(report);
    logger.debug("   PLAIN_TEXT ..");
    assertTrue(FunctionalityTestLib.createPlainText(report));
    logger.debug("   RTF ..");
    FunctionalityTestLib.createRTF(report);
    logger.debug("   STREAM_HTML ..");
    FunctionalityTestLib.createStreamHTML(report);
    logger.debug("   EXCEL ..");
    FunctionalityTestLib.createXLS(report);
    logger.debug("   ZIP_HTML ..");
    FunctionalityTestLib.createZIPHTML(report);
  }

  public void testPageCount() throws Exception
  {
    GroupsDemo demo = new GroupsDemo();
    MasterReport report = demo.createReport();
    final PageFunction pf = new PageFunction("JUnit-Page");
    pf.setDependencyLevel(2);
    report.addExpression(pf);
//    report.addExpression(new PageVerifyFunction("pf-verify"));

    assertTrue(FunctionalityTestLib.execGraphics2D(report));
  }
}

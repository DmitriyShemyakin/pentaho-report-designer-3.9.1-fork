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

package org.pentaho.reporting.engine.classic.testcases.base.basic;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportDefinition;
import org.pentaho.reporting.engine.classic.core.layout.style.ManualBreakIndicatorStyleSheet;
import org.pentaho.reporting.engine.classic.core.layout.style.SimpleStyleSheet;
import org.pentaho.reporting.engine.classic.core.states.ReportDefinitionImpl;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;
import org.pentaho.reporting.engine.classic.core.style.BandDefaultStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;

/**
 * @deprecated moved into engine core
 */
public class ReportDefinitionTest extends TestCase
{

  public ReportDefinitionTest(final String s)
  {
    super(s);
  }

  protected void setUp() throws Exception
  {
    ClassicEngineBoot.getInstance().start();
  }

  public void testReport () throws Exception
  {
    final MasterReport report = new MasterReport();
    final ElementStyleSheet es = report.getStyleSheetCollection().createStyleSheet("test");
    es.setStyleProperty(ElementStyleKeys.HREF_TITLE, "Hello World!");
    report.getReportHeader().getStyle().addParent(es);
    assertEquals(report.getReportHeader().getStyle().getStyleProperty(ElementStyleKeys.HREF_TITLE), "Hello World!");

    final ReportDefinition rd = new ReportDefinitionImpl(report, report.getPageDefinition());
    assertEquals(rd.getReportHeader().getStyle().getStyleProperty(ElementStyleKeys.HREF_TITLE), "Hello World!");

    // redefining the outside stylesheet must not change the stylesheet
    // inside the report definition.
    // check for deep-cloning!
    es.setStyleProperty(ElementStyleKeys.HREF_TITLE, "Hello Little Green Man!");
    assertSame(rd.getReportHeader().getStyle().getStyleProperty(ElementStyleKeys.HREF_TITLE), "Hello World!");

  }

  public void testStylesToArray ()
  {
    final ManualBreakIndicatorStyleSheet mbis =
        new ManualBreakIndicatorStyleSheet(BandDefaultStyleSheet.getBandDefaultStyle());
    final StyleSheet manualBreakBoxStyle = new SimpleStyleSheet(mbis);

    final int styleCount = (StyleKey.getDefinedStyleKeyCount());
    assertTrue(styleCount > 0);
    final Object[] objects = manualBreakBoxStyle.toArray();
    assertNotNull(objects);
    assertEquals(objects.length, styleCount);
  }


}

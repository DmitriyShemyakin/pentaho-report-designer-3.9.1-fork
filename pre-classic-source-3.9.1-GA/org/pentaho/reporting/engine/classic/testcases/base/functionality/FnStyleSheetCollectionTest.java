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
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.Group;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.demo.util.ReportDefinitionException;
import org.pentaho.reporting.engine.classic.demo.ancient.demo.cards.SimpleCardDemoHandler;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.StyleSheetCollection;

public class FnStyleSheetCollectionTest extends TestCase
{
  public FnStyleSheetCollectionTest()
  {
  }

  public FnStyleSheetCollectionTest(String string)
  {
    super(string);
  }

  public void testCollectStyleSheets () throws ReportDefinitionException
  {
    setup();
    final SimpleCardDemoHandler cardDemoHandler = new SimpleCardDemoHandler();
    MasterReport report = cardDemoHandler.createReport();
    assertStyleCollectionConnected(report);
    assertNotNull(report.getStyleSheetCollection().getStyleSheet("right-band"));
  }

  public void testCollectStyleSheetsClone () throws ReportDefinitionException,
          CloneNotSupportedException
  {
    setup();
    final SimpleCardDemoHandler cardDemoHandler = new SimpleCardDemoHandler();
    MasterReport report = cardDemoHandler.createReport();
    report = (MasterReport) report.clone();

    assertStyleCollectionConnected(report);
/*
    Iterator it = report.getStyleSheetCollection().keys();
    while (it.hasNext())
    {
      Log.debug (it.next());
    }
*/
    assertNotNull(report.getStyleSheetCollection().getStyleSheet("right-band"));
  }


  private void assertStyleCollectionConnected(final MasterReport report)
  {
    final StyleSheetCollection con = report.getStyleSheetCollection();
    assertStyleCollectionConnected (report.getPageFooter (), con);
    assertStyleCollectionConnected (report.getPageHeader (), con);
    assertStyleCollectionConnected (report.getReportFooter (), con);
    assertStyleCollectionConnected (report.getReportHeader (), con);
    assertStyleCollectionConnected (report.getItemBand (), con);

    final int groupCount = report.getGroupCount ();
    for (int i = 0; i < groupCount; i++)
    {
      final Group g = report.getGroup (i);
      assertStyleCollectionConnected (g.getFooter (), con);
      assertStyleCollectionConnected (g.getHeader (), con);
    }
  }

  private void assertStyleCollectionConnected(final Band band, final StyleSheetCollection sc)
  {
    // todo
  }

  private void assertStylesConnected (final ElementStyleSheet es, final StyleSheetCollection sc)
  {
    if (es.isGlobalDefault())
    {
      return;
    }

  }

  private void setup()
  {
    // initialize JFreeReport
    ClassicEngineBoot.getInstance().start();
  }
}

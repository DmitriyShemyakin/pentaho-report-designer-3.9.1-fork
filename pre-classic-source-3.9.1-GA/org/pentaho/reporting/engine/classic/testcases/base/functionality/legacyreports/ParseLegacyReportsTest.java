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
 * Copyright (c) 2009 Pentaho Corporation.  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.testcases.base.functionality.legacyreports;

import java.net.URL;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class ParseLegacyReportsTest extends TestCase
{
  private static final String[] REPORTS = new String[]{
      "activitylog-report.xml",
      "advertising.xml",
      "animal-report.xml",
      "auto-table.prpt",
      "basic-chart-ext.xml",
      "basic-chart-simple.xml",
      "bookstore.xml",
      "color-report.xml",
      "component-drawing.xml",
      "conditional-group-demo.xml",
      "council.xml",
      "country-report-ext.xml",
      "country-report-security.xml",
      "country-report.xml",
      "data-groups.xml",
      "fonts.xml",
      "footer-demo1.xml",
      "footer-demo2.xml",
      "fruit-report.xml",
      "i18n.xml",
      "interactivity.prpt",
      "invoice-combined-advertising.xml",
      "invoice-combined-invoice.xml",
      "invoice-combined-joined.xml",
      "invoice.xml",
      "item-hiding.xml",
      "joined-report.xml",
      "label.report",
      "large-report.xml",
      "lgpl.xml",
      "log-events.xml",
      "lunch-report.xml",
      "multi-chart-ext.xml",
      "multi-chart-simple.xml",
      "multipage-country-report.xml",
      "office-report.xml",
      "opensource.xml",
      "paint-component.xml",
      "parameters.prpt",
      "patient.xml",
      "percentage.xml",
      "rowbanding.xml",
      "sbarcodes.prpt",
      "sbarcodes-simple.xml",
      "shape-and-drawable.xml",
      "sparkline-simple.xml",
      "sparklines.prpt",
      "sql-datasource.report",
      "sql-subreport-ext.report",
      "sql-subreport.prpt",
      "sql-subreport.report",
      "stacked-layout-ext.xml",
      "stacked-layout.xml",
      "stylesheet-definition.xml",
      "stylesheets.xml",
      "subreport-joined3-report.xml",
      "subreport-joined-report.xml",
      "subreports2-animal-report.xml",
      "subreports2-color-report.xml",
      "subreports2-fruit-report.xml",
      "subreports2-joined3-report.xml",
      "subreports2-joined-report-ext.xml",
      "subreports2-joined-report.xml",
      "subreports2-one-file-subreport.xml",
      "surveyscale.xml",
      "swing-icons.xml",
      "table-chart-simple.xml",
      "text-elements.report",
      "trafficlighting.xml",
      "usercards.xml",
      "waybill.xml",
  };

  public ParseLegacyReportsTest(final String arg0)
  {
    super(arg0);
  }

  public ParseLegacyReportsTest()
  {
  }

  protected void setUp() throws Exception
  {
    ClassicEngineBoot.getInstance().start();
  }

  public void testAllReports() throws ResourceException
  {
    final ResourceManager resManager = new ResourceManager();
    resManager.registerDefaults();

    for (int i = 0; i < REPORTS.length; i++)
    {
      final String report = REPORTS[i];
      final URL url = ParseLegacyReportsTest.class.getResource(report);
      if (url == null)
      {
        throw new IllegalStateException("No definition for " + report);
      }

      try
      {
        final Resource res = resManager.createDirectly(url, MasterReport.class);
        final Object resource = res.getResource();
        assertNotNull(resource);
      }
      finally
      {
        System.out.println("Failed: " + report);
      }
    }
  }
}

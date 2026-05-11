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
 * Copyright (c) 2001 - 2009 Object Refinery Ltd, Pentaho Corporation and Contributors.  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.testcases.bugs;

import java.net.URL;
import java.io.IOException;
import java.awt.print.PageFormat;

import javax.swing.table.DefaultTableModel;

import org.pentaho.reporting.engine.classic.testcases.BaseTest;
import org.pentaho.reporting.engine.classic.testcases.TestSystem;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.SimplePageDefinition;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.demo.util.ReportDefinitionException;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class Pre268Test extends BaseTest
{
  public Pre268Test()
  {
  }

  /**
   * Returns the URL of the XML definition for this report.
   *
   * @return the URL of the report definition.
   */
  public URL getReportDefinitionSource()
  {
    return ObjectUtilities.getResourceRelative
        ("report.xml", Pre268Test.class); //$NON-NLS-1$
  }

  public void test268() throws ReportProcessingException, IOException, ReportDefinitionException
  {
    final MasterReport report = parseReport(getReportDefinitionSource());

    final TableDataFactory tdf = new TableDataFactory();
    tdf.addTable("default", new DefaultTableModel(10, 10));
    tdf.addTable("SubQ1", new DefaultTableModel(11, 10));
    report.setDataFactory(tdf);
    
    final PageFormat pageFormat = report.getPageDefinition().getPageFormat(0);
//    pageFormat.setOrientation(PageFormat.LANDSCAPE);
    report.setPageDefinition(new SimplePageDefinition(pageFormat));
    TestSystem.showPreview(report);
    //PdfReportUtil.createPDF(report, "/tmp/pre268.pdf");

  }
}

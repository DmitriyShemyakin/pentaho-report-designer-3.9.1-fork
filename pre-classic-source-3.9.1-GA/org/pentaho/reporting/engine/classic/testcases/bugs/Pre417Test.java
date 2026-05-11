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

package org.pentaho.reporting.engine.classic.testcases.bugs;

import java.io.IOException;
import java.net.URL;
import javax.swing.table.DefaultTableModel;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.demo.util.ReportDefinitionException;
import org.pentaho.reporting.engine.classic.testcases.BaseTest;
import org.pentaho.reporting.engine.classic.testcases.TestSystem;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class Pre417Test extends BaseTest
{
  public Pre417Test()
  {
  }

  public Pre417Test(final String s)
  {
    super(s);
  }

  /**
   * Returns the URL of the XML definition for this report.
   *
   * @return the URL of the report definition.
   */
  public URL getReportDefinitionSource()
  {
    return ObjectUtilities.getResourceRelative
        ("pre417.prpt", Pre343Test.class); //$NON-NLS-1$
  }

  public void testPre112() throws ReportProcessingException, IOException, ReportDefinitionException
  {
    ClassicEngineBoot.getInstance().start();
//     String outputFilename = getReportOutputPath() + "/report-282.html"; //$NON-NLS-1$
    final MasterReport report = parseReport(getReportDefinitionSource());
    report.setDataFactory(new TableDataFactory(report.getQuery(), new DefaultTableModel(10, 10)));
    //HtmlReportUtil.createStreamHTML(report, outputFilename);
    TestSystem.showPreview(report);
    assertTrue(true);
  }

}

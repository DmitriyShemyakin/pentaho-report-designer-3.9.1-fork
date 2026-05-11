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

import junit.framework.TestCase;
import org.jfree.util.ObjectUtilities;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.ReportGenerator;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.PdfReportUtil;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.HtmlReportUtil;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class IncomeStatementTest extends TestCase
{
  public IncomeStatementTest()
  {
  }

  public IncomeStatementTest(final String s)
  {
    super(s);
  }

  protected void setUp() throws Exception
  {
    ClassicEngineBoot.getInstance().start();
  }

  public void testPre401() throws Exception
  {
    // Boot the system

    // Load the report
    final ReportGenerator generator = ReportGenerator.getInstance();
    final MasterReport report = generator.parseReport
        (ObjectUtilities.getResourceRelative("IncomeStatement.xml", IncomeStatementTest.class));
    // Create the report and export to the supplied output filename
    PdfReportUtil.createPDF(report, "report.pdf");
    HtmlReportUtil.createStreamHTML(report, "bug.html");
  }

}

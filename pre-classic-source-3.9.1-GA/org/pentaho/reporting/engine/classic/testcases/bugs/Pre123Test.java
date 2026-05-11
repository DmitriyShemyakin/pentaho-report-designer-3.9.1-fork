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

package org.pentaho.reporting.engine.classic.testcases.bugs;

import java.io.IOException;
import java.net.URL;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.demo.util.ReportDefinitionException;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.HtmlReportUtil;
import org.pentaho.reporting.engine.classic.testcases.BaseTest;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

/**
 * Creation-Date: Feb 21, 2007, 4:01:57 PM
 *
 * @author Thomas Morgner
 */
public class Pre123Test extends BaseTest
{
  public Pre123Test()
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
        ("pre123.xml", Pre123Test.class); //$NON-NLS-1$
  }

  public  void testPre123() throws ReportProcessingException, IOException, ReportDefinitionException
  {
    ClassicEngineBoot.getInstance().start();
    String outputFilename = getReportOutputPath() + "/report.html"; //$NON-NLS-1$
    HtmlReportUtil.createStreamHTML(parseReport(getReportDefinitionSource()), outputFilename);
    assertTrue(true);
  }
}

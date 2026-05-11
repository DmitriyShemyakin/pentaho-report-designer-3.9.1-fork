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
import java.io.StringReader;
import java.io.File;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.ReportGenerator;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.xml.sax.InputSource;

/**
 * Creation-Date: 27.11.2007, 17:20:07
 *
 * @author Thomas Morgner
 */
public class Pre180 extends TestCase
{
  public Pre180(String string)
  {
    super(string);
  }


  protected void setUp() throws Exception
  {
    ClassicEngineBoot.getInstance().start();
  }

  public void testSimpleReportDefinition () throws IOException, ResourceException
  {
    String report = "<report></report>";
    StringReader reader = new StringReader(report);
    final InputSource source = new InputSource();
    source.setCharacterStream(reader);
    assertNotNull(ReportGenerator.getInstance().parseReport(source, new File("/tmp").toURL()));
  }

  public void testExtendedReportDefinition () throws IOException, ResourceException
  {
    String report = "<report-definition></report-definition>";
    StringReader reader = new StringReader(report);
    final InputSource source = new InputSource();
    source.setCharacterStream(reader);
    assertNotNull(ReportGenerator.getInstance().parseReport(source, new File("/tmp").toURL()));
  }
}

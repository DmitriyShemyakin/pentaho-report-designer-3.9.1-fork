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

import java.net.URL;

import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.ReportGenerator;
import org.pentaho.reporting.engine.classic.testcases.BaseTest;
import org.pentaho.reporting.engine.classic.testcases.base.functionality.FunctionalityTestLib;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

public class Pre84ReportTest extends BaseTest
{
  private static final String URLNAME = "org/pentaho/reporting/engine/classic/testcases/bugs/resource/Pre84Test.xml";

  public Pre84ReportTest()
  {
  }

  public static void testPre84Report() throws Exception
  {
    ClassicEngineBoot.getInstance().start();
    final URL in = ObjectUtilities.getResource(URLNAME, Pre84ReportTest.class);
    final MasterReport report = ReportGenerator.getInstance().parseReport(in);
    assertTrue(FunctionalityTestLib.execGraphics2D(report));    
  }
}

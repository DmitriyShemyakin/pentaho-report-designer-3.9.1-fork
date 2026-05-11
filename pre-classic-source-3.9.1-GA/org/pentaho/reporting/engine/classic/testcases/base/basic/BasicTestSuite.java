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

import junit.framework.Test;
import junit.framework.TestSuite;
import org.pentaho.reporting.engine.classic.testcases.base.basic.function.FunctionTestSuite;
import org.pentaho.reporting.engine.classic.testcases.base.basic.io.IOTestSuite;
import org.pentaho.reporting.engine.classic.testcases.base.basic.modules.ModuleTestSuite;
import org.pentaho.reporting.engine.classic.testcases.base.basic.preview.PreviewTestSuite;
import org.pentaho.reporting.engine.classic.testcases.base.basic.style.StyleTestSuite;
import org.pentaho.reporting.engine.classic.testcases.base.basic.util.UtilTestSuite;

public class BasicTestSuite extends TestSuite
{
  public BasicTestSuite(final String s)
  {
    super(s);
    addTestSuite(BandTest.class);
    addTestSuite(ElementTest.class);
    addTestSuite(GroupTest.class);
    addTestSuite(GroupListTest.class);
    addTestSuite(JFreeReportTest.class);
    addTestSuite(ReportDefinitionTest.class);
    addTest(new FunctionTestSuite(FunctionTestSuite.class.getName()));
    addTest(new IOTestSuite(IOTestSuite.class.getName()));
    addTest(new UtilTestSuite(UtilTestSuite.class.getName()));
    addTest(new StyleTestSuite(StyleTestSuite.class.getName()));
    addTest(new PreviewTestSuite(PreviewTestSuite.class.getName()));
    addTest(new ModuleTestSuite(ModuleTestSuite.class.getName()));
    addTestSuite(MessageFormatSupportTest.class);
  }

  /**
   * Dummmy method to silence the checkstyle test.
   */
  public void dummy()
  {
  }

  public static Test suite()
  {
    return new BasicTestSuite(BasicTestSuite.class.getName());
  }
}

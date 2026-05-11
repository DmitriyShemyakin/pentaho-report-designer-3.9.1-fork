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

package org.pentaho.reporting.engine.classic.testcases.base.basic.util;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;

public class ReportConfigurationTest extends TestCase
{
  private static final String key = "org.pentaho.reporting.engine.classic.core.layout.fontrenderer.UseAliasing";

  public ReportConfigurationTest()
  {
  }

  public ReportConfigurationTest(final String s)
  {
    super(s);
  }

  public void testBasicFunctionality ()
  {
    final String value = ClassicEngineBoot.getInstance().getGlobalConfig().getConfigProperty(key);
    assertNotNull(value);

    System.setProperty(key, value + "-sysprop");
    final String value2 = ClassicEngineBoot.getInstance().getEditableConfig().getConfigProperty(key);
    assertNotNull(value2);
    assertNotSame(value, value2);

    ClassicEngineBoot.getInstance().getEditableConfig().setConfigProperty(key, value + "-repconf");
    final String value3 = ClassicEngineBoot.getInstance().getGlobalConfig().getConfigProperty(key);
    assertNotNull(value3);
    assertNotSame(value, value3);
    assertNotSame(value2, value3);

    ClassicEngineBoot.getInstance().getEditableConfig().setConfigProperty(key, null);
    System.getProperties().remove(key);

  }
}

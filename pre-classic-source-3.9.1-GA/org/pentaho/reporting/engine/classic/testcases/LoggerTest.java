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
 * Copyright (c) 2005 - 2009 Pentaho Corporation, Object Refinery Limited and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.testcases;

import junit.framework.TestCase;


/**
 * Creation-Date: 23.03.2006, 19:59:54
 *
 * @deprecated Useless now
 */
public class LoggerTest extends TestCase
{
  public static void testLogger()
  {
    System.setProperty("org.jfree.base.LogTarget", "org.jfree.logger.jcl.JakartaLogTarget");
    System.setProperty("org.jfree.base.LogAutoInit", "true");
    System.setProperty("org.jfree.DebugDefault", "true");

    assertTrue(true);
  }
}

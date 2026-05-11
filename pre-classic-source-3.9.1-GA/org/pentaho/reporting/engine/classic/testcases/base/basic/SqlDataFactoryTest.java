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

package org.pentaho.reporting.engine.classic.testcases.base.basic;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql.SQLParameterLookupParser;
import org.pentaho.reporting.engine.classic.core.util.ReportParameterValues;

/**
 * @deprecated moved into core tests
 *
 * @author Thomas Morgner
 */
public class SqlDataFactoryTest extends TestCase
{
  public SqlDataFactoryTest()
  {
  }

  public SqlDataFactoryTest(final String s)
  {
    super(s);
  }

  protected void setUp() throws Exception
  {
    ClassicEngineBoot.getInstance().start();
  }

  public void testParsing()
  {
    final String query = "SELECT\n" +
        "     `wayne$`.`Created`,\n" +
        "     `wayne$`.`Severity`,\n" +
        "     `wayne$`.`Status`\n" +
        "FROM\n" +
        "     `wayne$`";

    final SQLParameterLookupParser parser = new SQLParameterLookupParser(true);
    final String translatedQuery = parser.translateAndLookup(query, new ReportParameterValues());
    assertEquals(query, translatedQuery);
  }
}

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

package org.pentaho.reporting.engine.classic.testcases.base.basic;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.filter.MessageFormatSupport;
import org.pentaho.reporting.libraries.base.util.CSVTokenizer;

/**
 * @deprecated moved into engine core
 */
public class MessageFormatSupportTest extends TestCase
{
  public MessageFormatSupportTest (String s)
  {
    super(s);
  }

  public void testQuotedExample ()
  {
    final String example = "$(\"customer.firstName\") $(\"customer.lastName\")";
    final MessageFormatSupport support = new MessageFormatSupport();
    support.setFormatString(example);
    assertEquals("CompiledFormat", "{0} {1}", support.getCompiledFormat());
  }

  public void testCSVTokenizer ()
  {
    final String example = "\"Test\"";
    CSVTokenizer tokenizer = new CSVTokenizer(example, ",", "\"");
    assertTrue("Tokenizer has at least one element", tokenizer.hasMoreTokens());
    assertEquals(tokenizer.nextToken(), "Test");
  }

  public void testComplexReplacement ()
  {
    MessageFormatSupport support = new MessageFormatSupport();
    support.setFormatString("$(null,number,integer), $(dummy), $(null,date), $(null,number,integer)");
    SimpleDataRow sdr = new SimpleDataRow();
    sdr.add("null", null);
    sdr.add("dummy", "Content");

    String text = support.performFormat(sdr);
    assertEquals("Expected content w/o nullString", "<null>, Content, <null>, <null>", text);
    System.out.println(text);

    support.setNullString("-");
    String ntext = support.performFormat(sdr);
    assertEquals("Expected content w nullString", "-, Content, -, -",ntext);
    System.out.println(ntext);
  }
}

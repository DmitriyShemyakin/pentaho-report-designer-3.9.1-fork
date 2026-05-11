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

import java.text.Format;
import java.text.MessageFormat;

import junit.framework.TestCase;

public class MessageFormatTest extends TestCase
{
  public static void testMessageFormt()
  {
    MessageFormat format = new MessageFormat("{1} {0,number,integer}"); //$NON-NLS-1$
    System.out.println(format.format(new Object[]{new Integer (1), new Integer (1)}));

    Format[] fmt = format.getFormatsByArgumentIndex();

    for (int i = 0; i < fmt.length; i++)
    {
      Format format1 = fmt[i];
      System.out.println(format1);

    }

    format.setFormat(1, null);
    System.out.println(format.format(new Object[]{"-", "a"}));//$NON-NLS-1$ //$NON-NLS-2$
    assertTrue(true);
  }
}

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

import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;

import junit.framework.TestCase;

/**
 * Creation-Date: 01.10.2005, 19:28:55
 *
 * @author Thomas Morgner
 */
public class FontTest extends TestCase
{
  protected static final String TEST_STRING = "asdfasdfasdfisdfifuasdffif"; //$NON-NLS-1$

  public FontTest()
  {
  }

  private static double computeWidth (final Font font,
                               FontRenderContext frc, char[] string)
  {
    Rectangle2D rect = font.getStringBounds(string, 0, string.length, frc);
    return rect.getWidth();
  }

  private static double computeWidth (double[] widths, char[] string)
  {
    double width = 0;
    for (int i = 0; i < string.length; i++)
    {
      char c = string[i];
      width += widths[c - 'a'];
    }
    return width;
  }

  public static void testFont ()
  {
    Font font = new Font ("Coronet", Font.ITALIC, 80); //$NON-NLS-1$
    FontRenderContext frc = new FontRenderContext(null, true, true);
    char[] chars = new char[('z' - 'a')];
    double[] charW = new double[chars.length];

    for (int i = 0; i < chars.length; i++)
    {
      chars[i] = (char) ('a' + i);
      Rectangle2D rect = font.getStringBounds("" + chars[i], frc); //$NON-NLS-1$
      charW[i] = rect.getWidth();
    }

    System.out.println(TEST_STRING);
    final char[] string = TEST_STRING.toCharArray();
    System.out.println(computeWidth(font, frc, string));
    System.out.println(computeWidth(charW, string));
    assertTrue(true);
  }
}

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
 * Copyright (c) 2001 - 2009 Object Refinery Ltd, Pentaho Corporation and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.util;

/**
 * String utility functions. Provides functions to parse floats, ints and boolean values.
 *
 * @author Thomas Morgner
 */
public final class StringUtil
{
  /**
   * Default Constructor.
   */
  private StringUtil()
  {
  }

  /**
   * Parses the given string and returns the parsed integer value or the given default if the parsing failed.
   *
   * @param value        the to be parsed string
   * @param defaultValue the default value
   * @return the parsed string.
   */
  public static int parseInt(final String value, final int defaultValue)
  {
    if (value == null)
    {
      return defaultValue;
    }
    try
    {
      return Integer.parseInt(value);
    }
    catch (Exception e)
    {
      return defaultValue;
    }
  }


  /**
   * Parses the given string and returns the parsed integer value or the given default if the parsing failed.
   *
   * @param value        the to be parsed string
   * @param defaultValue the default value
   * @return the parsed string.
   */
  public static float parseFloat(final String value, final float defaultValue)
  {
    if (value == null)
    {
      return defaultValue;
    }
    try
    {
      return Float.parseFloat(value);
    }
    catch (Exception e)
    {
      return defaultValue;
    }
  }

  /**
   * Parses the given string into a boolean value. This returns true, if the string's value is "true".
   *
   * @param attribute    the string that should be parsed.
   * @param defaultValue the default value, in case the string is null.
   * @return the parsed value.
   */
  public static boolean parseBoolean(final String attribute, final boolean defaultValue)
  {
    if (attribute == null)
    {
      return defaultValue;
    }
    if ("true".equals(attribute))
    {
      return true;
    }
    return false;
  }
}

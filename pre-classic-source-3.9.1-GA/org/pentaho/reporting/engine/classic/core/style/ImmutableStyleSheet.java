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

package org.pentaho.reporting.engine.classic.core.style;

import java.io.Serializable;

import org.pentaho.reporting.engine.classic.core.util.IntegerCache;

/**
 * Creation-Date: 26.06.2006, 11:25:02
 *
 * @author Thomas Morgner
 * @deprecated This class is not used anywhere. It will be removed in 0.8.11.
 */
public class ImmutableStyleSheet extends AbstractStyleSheet implements Serializable
{
  private ElementStyleSheet styleSheet;

  public ImmutableStyleSheet(final ElementStyleSheet styleSheet)
  {
    if (styleSheet == null)
    {
      throw new NullPointerException();
    }
    this.styleSheet = styleSheet;
  }

  public boolean getBooleanStyleProperty(final StyleKey key)
  {
    return styleSheet.getBooleanStyleProperty(key);
  }

  public boolean getBooleanStyleProperty(final StyleKey key,
                                         final boolean defaultValue)
  {
    return styleSheet.getBooleanStyleProperty(key, defaultValue);
  }

  public FontDefinition getFontDefinitionProperty()
  {
    return styleSheet.getFontDefinitionProperty();
  }

  /**
   * Returns an integer style.
   *
   * @param key the style key.
   * @param def the default value.
   * @return the style value.
   */
  public int getIntStyleProperty(final StyleKey key, final int def)
  {
    final Number i = (Number) getStyleProperty(key, IntegerCache.getInteger(def));
    return i.intValue();
  }

  /**
   * Returns an double style.
   *
   * @param key the style key.
   * @param def the default value.
   * @return the style value.
   */
  public double getDoubleStyleProperty(final StyleKey key, final double def)
  {
    final Number i = (Number) getStyleProperty(key, new Double(def));
    return i.intValue();
  }

  public Object getStyleProperty(final StyleKey key)
  {
    return styleSheet.getStyleProperty(key);
  }

  public Object getStyleProperty(final StyleKey key, final Object defaultValue)
  {
    return styleSheet.getStyleProperty(key, defaultValue);
  }

  public Object[] toArray()
  {
    return styleSheet.toArray();
  }
}

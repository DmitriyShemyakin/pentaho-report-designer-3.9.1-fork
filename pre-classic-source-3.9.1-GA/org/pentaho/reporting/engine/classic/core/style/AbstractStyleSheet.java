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

import org.pentaho.reporting.engine.classic.core.util.InstanceID;

/**
 * Creation-Date: 22.04.2007, 16:48:33
 *
 * @author Thomas Morgner
 */
public abstract class AbstractStyleSheet implements StyleSheet
{

  /**
   * The instance id of this ElementStyleSheet. This id is shared among all clones.
   */
  private InstanceID id;

  protected AbstractStyleSheet()
  {
    this.id = new InstanceID();
  }

  /**
   * Returns the value of a style.  If the style is not found in this style-sheet, the code looks in the parent
   * style-sheets.  If the style is not found in any of the parent style-sheets, then <code>null</code> is returned.
   *
   * @param key the style key.
   * @return the value.
   */
  public Object getStyleProperty(final StyleKey key)
  {
    return getStyleProperty(key, null);
  }

  /**
   * Returns a boolean style (defaults to false if the style is not found).
   *
   * @param key the style key.
   * @return <code>true</code> or <code>false</code>.
   */
  public boolean getBooleanStyleProperty(final StyleKey key)
  {
    final Boolean b = (Boolean) getStyleProperty(key, null);
    if (b == null)
    {
      return false;
    }
    return b.booleanValue();
  }

  /**
   * Returns a boolean style.
   *
   * @param key          the style key.
   * @param defaultValue the default value.
   * @return true or false.
   */
  public boolean getBooleanStyleProperty(final StyleKey key, final boolean defaultValue)
  {
    final Boolean b = (Boolean) getStyleProperty(key, null);
    if (b == null)
    {
      return defaultValue;
    }
    return b.booleanValue();
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
    final Number i = (Number) getStyleProperty(key, null);
    if (i == null)
    {
      return def;
    }
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
    final Number i = (Number) getStyleProperty(key, null);
    if (i == null)
    {
      return def;
    }
    return i.doubleValue();
  }


  /**
   * Returns the font for this style-sheet.
   *
   * @return the font.
   * @deprecated Do not use this method, access the font properties directly. This method will be removed in 0.8.11.
   */
  public FontDefinition getFontDefinitionProperty()
  {
    final String name = (String) getStyleProperty(TextStyleKeys.FONT);
    final int size = getIntStyleProperty(TextStyleKeys.FONTSIZE, -1);
    final boolean bold = getBooleanStyleProperty(TextStyleKeys.BOLD);
    final boolean italic = getBooleanStyleProperty(TextStyleKeys.ITALIC);
    final boolean underlined = getBooleanStyleProperty(TextStyleKeys.UNDERLINED);
    final boolean strike = getBooleanStyleProperty(TextStyleKeys.STRIKETHROUGH);
    final boolean embed = getBooleanStyleProperty(TextStyleKeys.EMBEDDED_FONT);
    final String encoding = (String) getStyleProperty(TextStyleKeys.FONTENCODING);

    return new FontDefinition(name, size, bold, italic, underlined, strike, encoding, embed);
  }


  /**
   * Returns the ID of the stylesheet. The ID does identify an element stylesheet an all all cloned instances of that
   * stylesheet.
   *
   * @return the ID of this stylesheet.
   */
  public InstanceID getId()
  {
    return id;
  }

  public long getChangeTracker()
  {
    return 0;
  }

  public StyleSheet derive() throws CloneNotSupportedException
  {
    final AbstractStyleSheet s = (AbstractStyleSheet) super.clone();
    s.id = new InstanceID();
    return s;
  }
}

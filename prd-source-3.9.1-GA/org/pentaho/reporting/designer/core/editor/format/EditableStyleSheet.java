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

package org.pentaho.reporting.designer.core.editor.format;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.pentaho.reporting.designer.core.Messages;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;
import org.pentaho.reporting.engine.classic.core.style.StyleSheetCarrier;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class EditableStyleSheet extends ElementStyleSheet
{
  private class EditorStyleSheetCarrier implements StyleSheetCarrier
  {
    private ElementStyleSheet styleSheet;

    private EditorStyleSheetCarrier(final ElementStyleSheet styleSheet)
    {
      this.styleSheet = styleSheet;
    }

    public ElementStyleSheet getStyleSheet()
    {
      return styleSheet;
    }

    public boolean isSame(final ElementStyleSheet style)
    {
      return style.getId() == styleSheet.getId();
    }

    public void invalidate()
    {
    }

    public Object clone() throws CloneNotSupportedException
    {
      return super.clone();
    }
  }

  private HashSet<StyleKey> editedKeys;
  private HashSet<StyleKey> removedKeys;
  private HashMap<StyleKey, Object> parentValues;

  public EditableStyleSheet(final ElementStyleSheet parent)
  {
    super(Messages.getString("EditableStyleSheet.EditableStyle"));
    editedKeys = new HashSet<StyleKey>();
    removedKeys = new HashSet<StyleKey>();
    parentValues = new HashMap<StyleKey, Object>();
    if (parent != null)
    {
      addParent(parent);
      final StyleKey[] definedPropertyNamesArray = parent.getDefinedPropertyNamesArray();
      for (int i = 0; i < definedPropertyNamesArray.length; i++)
      {
        final StyleKey styleKey = definedPropertyNamesArray[i];
        if (styleKey == null)
        {
          continue;
        }
        setStyleProperty(styleKey, parent.getStyleProperty(styleKey));
      }
      final StyleKey[] propertyKeys = parent.getPropertyKeys();
      for (int i = 0; i < propertyKeys.length; i++)
      {
        final StyleKey propertyKey = propertyKeys[i];
        parentValues.put(propertyKey, parent.getStyleProperty(propertyKey));
      }
    }
    editedKeys.clear();
    removedKeys.clear();
  }


  public void clearEdits()
  {
    editedKeys.clear();
    removedKeys.clear();

    final StyleKey[] propertyKeys = getPropertyKeys();
    for (int i = 0; i < propertyKeys.length; i++)
    {
      final StyleKey propertyKey = propertyKeys[i];
      parentValues.put(propertyKey, getStyleProperty(propertyKey));
    }
  }
  
  /**
   * Sets a style property (or removes the style if the value is <code>null</code>).
   *
   * @param key   the style key (<code>null</code> not permitted).
   * @param value the value.
   * @throws NullPointerException if the given key is null.
   * @throws ClassCastException   if the value cannot be assigned with the given key.
   */
  public void setStyleProperty(final StyleKey key, final Object value)
  {
    final Object styleProperty = parentValues.get(key);
    if (styleProperty == value || ObjectUtilities.equal(styleProperty, value))
    {
      return;
    }
    editedKeys.add(key);
    if (value == null)
    {
      removedKeys.add(key);
    }
    else
    {
      removedKeys.remove(key);
    }
    super.setStyleProperty(key, value);
  }

  /**
   * Returns the value of a style.  If the style is not found in this style-sheet, the code looks in the parent
   * style-sheets.  If the style is not found in any of the parent style-sheets, then the default value (possibly
   * <code>null</code>) is returned.
   *
   * @param key          the style key.
   * @param defaultValue the default value (<code>null</code> permitted).
   * @return the value.
   */
  public Object getStyleProperty(final StyleKey key, final Object defaultValue)
  {
    if (removedKeys.contains(key))
    {
      return defaultValue;
    }
    return super.getStyleProperty(key, defaultValue);
  }

  /**
   * Returns an enumeration of all local property keys.
   *
   * @return an enumeration of all localy defined style property keys.
   */
  public Iterator getDefinedPropertyNames()
  {
    return Arrays.asList(getDefinedPropertyNamesArray()).iterator();
  }

  public StyleKey[] getDefinedPropertyNamesArray()
  {
    final StyleKey[] keys = getPropertyKeys();
    for (int i = 0; i < keys.length; i++)
    {
      final StyleKey key = keys[i];
      if (editedKeys.contains(key) == false)
      {
        keys[i] = null;
      }
    }
    return keys;
  }

  protected StyleSheetCarrier createCarrier(final ElementStyleSheet styleSheet)
  {
    return new EditorStyleSheetCarrier(styleSheet);
  }
}

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

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import org.pentaho.reporting.libraries.base.util.FloatDimension;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.serializer.SerializerHelper;

/**
 * An element style-sheet contains zero, one or many attributes that affect the appearance of report elements.  For each
 * attribute, there is a predefined key that can be used to access that attribute in the style sheet.
 * <p/>
 * Every report element has an associated style-sheet.
 * <p/>
 * A style-sheet maintains a list of parent style-sheets.  If an attribute is not defined in a style-sheet, the code
 * refers to the parent style-sheets to see if the attribute is defined there.
 * <p/>
 * All StyleSheet entries are checked against the StyleKeyDefinition for validity.
 * <p/>
 * As usual, this implementation is not synchronized, we need the performance during the reporting.
 *
 * @author Thomas Morgner
 * @noinspection UnnecessaryUnboxing
 */
public abstract class ElementStyleSheet extends AbstractStyleSheet
    implements Serializable, StyleChangeListener, Cloneable
{
  /**
   * A key for the 'minimum size' of an element. This style property is not inherited from the parent band.
   *
   * @deprecated use the minimum-width and minimum-height style-keys instead.
   */
  public static final StyleKey MINIMUMSIZE = ElementStyleKeys.MINIMUMSIZE;
  /**
   * A key for the 'maximum size' of an element. This style property is not inherited from the parent band.
   *
   * @deprecated use the minimum-width and minimum-height style-keys instead.
   */
  public static final StyleKey MAXIMUMSIZE = ElementStyleKeys.MAXIMUMSIZE;

  /**
   * A key for the 'preferred size' of an element. This style property is not inherited from the parent band.
   *
   * @deprecated use the minimum-width and minimum-height style-keys instead.
   */
  public static final StyleKey PREFERREDSIZE = ElementStyleKeys.PREFERREDSIZE;

  /**
   * A key for an element's 'visible' flag.
   *
   * @deprecated use ElementStyleKeys instead..
   */
  public static final StyleKey VISIBLE = ElementStyleKeys.VISIBLE;

  /**
   * A key for the 'paint' used to color an element. For historical reasons, this key requires a color value.
   *
   * @deprecated use ElementStyleKeys instead..
   */
  public static final StyleKey PAINT = ElementStyleKeys.PAINT;

  /**
   * A key for the 'stroke' used to draw an element. (This now only applies to shape and drawable-elements.)
   *
   * @deprecated use ElementStyleKeys instead..
   */
  public static final StyleKey STROKE = ElementStyleKeys.STROKE;

  /**
   * A key for the horizontal alignment of an element.
   *
   * @deprecated use ElementStyleKeys instead..
   */
  public static final StyleKey ALIGNMENT = ElementStyleKeys.ALIGNMENT;

  /**
   * A key for the vertical alignment of an element.
   *
   * @deprecated use ElementStyleKeys instead..
   */
  public static final StyleKey VALIGNMENT = ElementStyleKeys.VALIGNMENT;

  /**
   * A key for an element's 'scale' flag.
   *
   * @deprecated use ElementStyleKeys instead..
   */
  public static final StyleKey SCALE = ElementStyleKeys.SCALE;

  /**
   * A key for an element's 'keep aspect ratio' flag.
   *
   * @deprecated use ElementStyleKeys instead..
   */
  public static final StyleKey KEEP_ASPECT_RATIO = ElementStyleKeys.KEEP_ASPECT_RATIO;

  /**
   * A key for the dynamic height flag for an element.
   *
   * @deprecated use ElementStyleKeys instead..
   */
  public static final StyleKey DYNAMIC_HEIGHT = ElementStyleKeys.DYNAMIC_HEIGHT;

  /**
   * @deprecated use ElementStyleKeys instead..
   */
  public static final StyleKey HREF_TARGET = ElementStyleKeys.HREF_TARGET;

  /**
   * Specifies the anchor tag's target window for opening the link.
   *
   * @deprecated use ElementStyleKeys instead..
   */
  public static final StyleKey HREF_WINDOW = ElementStyleKeys.HREF_WINDOW;

  /**
   * The StyleKey for the user defined cell data format.
   *
   * @deprecated use ElementStyleKeys instead..
   */
  public static final StyleKey EXCEL_WRAP_TEXT = ElementStyleKeys.EXCEL_WRAP_TEXT;

  /**
   * The StyleKey for the user defined cell data format.
   *
   * @deprecated use ElementStyleKeys instead..
   */
  public static final StyleKey EXCEL_DATA_FORMAT_STRING = ElementStyleKeys.EXCEL_DATA_FORMAT_STRING;

  /**
   * A key for the 'font family' used to draw element text.
   *
   * @deprecated use ElementStyleKeys instead..
   */
  public static final StyleKey FONT = TextStyleKeys.FONT;

  /**
   * A key for the 'font size' used to draw element text.
   *
   * @deprecated use ElementStyleKeys instead..
   */
  public static final StyleKey FONTSIZE = TextStyleKeys.FONTSIZE;

  /**
   * A key for the 'font size' used to draw element text.
   *
   * @deprecated use ElementStyleKeys instead..
   */
  public static final StyleKey LINEHEIGHT = TextStyleKeys.LINEHEIGHT;

  /**
   * A key for an element's 'bold' flag.
   *
   * @deprecated use ElementStyleKeys instead..
   */
  public static final StyleKey BOLD = TextStyleKeys.BOLD;

  /**
   * A key for an element's 'italic' flag.
   *
   * @deprecated use ElementStyleKeys instead..
   */
  public static final StyleKey ITALIC = TextStyleKeys.ITALIC;

  /**
   * A key for an element's 'underlined' flag.
   *
   * @deprecated use ElementStyleKeys instead..
   */
  public static final StyleKey UNDERLINED = TextStyleKeys.UNDERLINED;

  /**
   * A key for an element's 'strikethrough' flag.
   *
   * @deprecated use ElementStyleKeys instead..
   */
  public static final StyleKey STRIKETHROUGH = TextStyleKeys.STRIKETHROUGH;

  /**
   * A key for an element's 'embedd' flag.
   *
   * @deprecated use ElementStyleKeys instead..
   */
  public static final StyleKey EMBEDDED_FONT = TextStyleKeys.EMBEDDED_FONT;

  /**
   * A key for an element's 'embedd' flag.
   *
   * @deprecated use ElementStyleKeys instead..
   */
  public static final StyleKey FONTENCODING = TextStyleKeys.FONTENCODING;

  /**
   * The string that is used to end a text if not all text fits into the element.
   *
   * @deprecated use ElementStyleKeys instead..
   */
  public static final StyleKey RESERVED_LITERAL = TextStyleKeys.RESERVED_LITERAL;

  /**
   * The Layout Cacheable stylekey. Set this stylekey to false, to define that the element is not cachable. This key
   * defaults to true.
   *
   * @deprecated use ElementStyleKeys instead..
   */
  public static final StyleKey TRIM_TEXT_CONTENT = TextStyleKeys.TRIM_TEXT_CONTENT;

  /**
   * A singleton marker for the cache.
   */
  private static final Object UNDEFINED_VALUE = new Object();

  /**
   * The style-sheet name.
   */
  private String name;

  /**
   * Storage for the parent style sheets (if any).
   */
  private ArrayList<StyleSheetCarrier> parents;

  /**
   * Storage for readonly style sheets.
   */
  private ElementDefaultStyleSheet globalDefaultStyleSheet;
  private ElementStyleSheet cascadeStyleSheet;

  /**
   * Parent style sheet cache.
   */
  private transient StyleSheetCarrier[] parentsCached;

  /**
   * The keys for the properties that have been explicitly set on the element.
   */
  private StyleKey[] propertyKeys;

  /**
   * The properties that have been explicitly set on the element.
   */
  private transient Object[] properties;
  private byte[] source;

  private static final byte SOURCE_UNDEFINED = 0;
  private static final byte SOURCE_FROM_PARENT = 1;
  private static final byte SOURCE_DIRECT = 2;

  /**
   * Style change support.
   */
  private transient StyleChangeSupport styleChangeSupport;

  private long changeTracker;
  private static final StyleKey[] EMPTY_KEYS = new StyleKey[0];
  private static final StyleSheetCarrier[] EMPTY_PARENTS = new StyleSheetCarrier[0];

  /**
   * Creates a new element style-sheet with the given name.  The style-sheet initially contains no attributes, and has
   * no parent style-sheets.
   *
   * @param name the name (<code>null</code> not permitted).
   */
  protected ElementStyleSheet(final String name)
  {
    if (name == null)
    {
      throw new NullPointerException("ElementStyleSheet constructor: name is null.");
    }
    this.name = name;
    this.styleChangeSupport = new StyleChangeSupport(this);
    this.propertyKeys = StyleKey.getDefinedStyleKeys();
    if (propertyKeys[0] == null)
    {
      throw new IllegalStateException();
    }
  }

  /**
   * Creates a new element style-sheet with the given name.  The style-sheet initially contains no attributes, and has
   * no parent style-sheets.
   *
   * @param name the name (<code>null</code> not permitted).
   * @deprecated no longer used. Will be removed in the next iteration.
   */
  protected ElementStyleSheet(final String name,
                              final ElementStyleSheet deriveParent)
  {
    if (name == null)
    {
      throw new NullPointerException("ElementStyleSheet constructor: name is null.");
    }
    if (deriveParent == null)
    {
      throw new NullPointerException("ElementStyleSheet constructor: parent cannot be null.");
    }
    this.name = name;
    this.styleChangeSupport = new StyleChangeSupport(this);
    // These arrays are immutable ..
    this.propertyKeys = deriveParent.propertyKeys;
    if (propertyKeys[0] == null)
    {
      throw new IllegalStateException();
    }
    this.properties = new Object[propertyKeys.length];
    this.source = new byte[propertyKeys.length];
    // Cached properties are not always needed ..
    this.changeTracker = deriveParent.changeTracker;
  }

  /**
   * Returns <code>true</code> if caching is allowed, and <code>false</code> otherwise.
   *
   * @return A boolean.
   * @deprecated has no effect. Always return true.
   */
  public final boolean isAllowCaching()
  {
    return true;
  }

  public long getChangeTracker()
  {
    return changeTracker;
  }

  /**
   * Returns true, if the given key is locally defined, false otherwise.
   *
   * @param key the key to test
   * @return true, if the key is local, false otherwise.
   */
  public boolean isLocalKey(final StyleKey key)
  {
    if (source == null)
    {
      return false;
    }
    final int identifier = key.identifier;
    if (source.length <= identifier)
    {
      return false;
    }
    return source[identifier] == SOURCE_DIRECT;
  }

  /**
   * Sets the flag that controls whether or not caching is allowed.
   *
   * @param allowCaching the flag value.
   * @deprecated has no effect - there is always some caching now
   */
  public void setAllowCaching(final boolean allowCaching)
  {
  }

  /**
   * Returns the name of the style-sheet.
   *
   * @return the name (never <code>null</code>).
   */
  public String getName()
  {
    return name;
  }

  /**
   * Adds a parent style-sheet. This method adds the parent to the beginning of the list, and guarantees, that this
   * parent is queried first.
   *
   * @param parent the parent (<code>null</code> not permitted).
   */
  public void addParent(final ElementStyleSheet parent)
  {
    addParent(0, parent);
  }

  /**
   * Adds a parent style-sheet. Parents on a lower position are queried before any parent with an higher position in the
   * list.
   *
   * @param position the position where to insert the parent style sheet
   * @param parent   the parent (<code>null</code> not permitted).
   * @throws IndexOutOfBoundsException if the position is invalid (pos &lt; 0 or pos &gt;= numberOfParents)
   */
  public void addParent(final int position, final ElementStyleSheet parent)
  {
    if (parent == null)
    {
      throw new NullPointerException("ElementStyleSheet.addParent(...): parent is null.");
    }
    if (parent.isSubStyleSheet(this) == false)
    {
      final StyleSheetCarrier carrier = createCarrier(parent);
      if (carrier == null)
      {
        throw new IllegalArgumentException
            ("The given StyleSheet cannot be added to this stylesheet.");
      }
      if (parents == null)
      {
        parents = new ArrayList<StyleSheetCarrier>();
      }
      parents.add(position, carrier);
      parentsCached = null;
      changeTracker += 1;
    }
    else
    {
      throw new IllegalArgumentException("Cannot add parent as child.");
    }
  }

  protected abstract StyleSheetCarrier createCarrier(ElementStyleSheet styleSheet);


  /**
   * Checks whether the given element stylesheet is already added as child into the stylesheet tree.
   *
   * @param parent the element that should be tested.
   * @return true, if the element is a child of this element style sheet, false otherwise.
   */
  protected boolean isSubStyleSheet(final ElementStyleSheet parent)
  {
    if (parents == null)
    {
      return false;
    }
    final int size = parents.size();
    for (int i = 0; i < size; i++)
    {
      final StyleSheetCarrier ca = (StyleSheetCarrier) parents.get(i);
      final ElementStyleSheet es = ca.getStyleSheet();
      if (es == parent)
      {
        return true;
      }
      if (es.isSubStyleSheet(parent) == true)
      {
        return true;
      }
    }
    return false;
  }

  /**
   * Removes a parent style-sheet.
   *
   * @param parent the style-sheet to remove (<code>null</code> not permitted).
   */
  public void removeParent(final ElementStyleSheet parent)
  {
    if (parent == null)
    {
      throw new NullPointerException("ElementStyleSheet.removeParent(...): parent is null.");
    }
    if (parents == null)
    {
      return;
    }
    final Iterator it = parents.iterator();
    while (it.hasNext())
    {
      final StyleSheetCarrier carrier = (StyleSheetCarrier) it.next();
      if (carrier.isSame(parent))
      {
        it.remove();
        carrier.invalidate();
        parentsCached = null;
        changeTracker += 1;
      }
    }
  }

  /**
   * Returns a list of the parent style-sheets.
   * <p/>
   * The list is unmodifiable.
   *
   * @return the list.
   */
  public ElementStyleSheet[] getParents()
  {
    if (parentsCached == null)
    {
      this.parentsToCache();
    }
    final ElementStyleSheet[] styleSheets = new ElementStyleSheet[parentsCached.length];
    for (int i = 0; i < styleSheets.length; i++)
    {
      styleSheets[i] = parentsCached[i].getStyleSheet();
    }
    return styleSheets;
  }

  /**
   * Returns the global default (if defined).
   *
   * @return the list.
   */
  public ElementDefaultStyleSheet getGlobalDefaultStyleSheet()
  {
    return globalDefaultStyleSheet;
  }

  public void setGlobalDefaultStyleSheet(final ElementDefaultStyleSheet defaultStyleSheet)
  {
    if (this.globalDefaultStyleSheet == defaultStyleSheet)
    {
      return;
    }

    this.globalDefaultStyleSheet = defaultStyleSheet;

    pruneCachedEntries();
  }

  public ElementStyleSheet getCascadeStyleSheet()
  {
    return cascadeStyleSheet;
  }

  public void setCascadeStyleSheet(final ElementStyleSheet cascadeStyleSheet)
  {
    if (this.cascadeStyleSheet == cascadeStyleSheet)
    {
      return;
    }

    if (this.cascadeStyleSheet != null)
    {
      this.cascadeStyleSheet.removeListener(this);
    }
    this.cascadeStyleSheet = cascadeStyleSheet;

    pruneCachedEntries();

    if (this.cascadeStyleSheet != null)
    {
      this.cascadeStyleSheet.addListener(this);
    }
  }

  private void pruneCachedEntries()
  {
    if (source != null && properties != null)
    {
      for (int i = 0; i < source.length; i++)
      {
        if (source[i] == SOURCE_FROM_PARENT)
        {
          source[i] = SOURCE_UNDEFINED;
          properties[i] = null;
        }
      }
    }
  }

  public final Object[] toArray()
  {
    final StyleKey[] keys = propertyKeys;
    final Object[] data = new Object[keys.length];
    if (source == null)
    {
      source = new byte[keys.length];
      properties = new Object[keys.length];
    }

    for (int i = 0; i < keys.length; i++)
    {
      final StyleKey key = keys[i];
      if (key == null)
      {
        throw new NullPointerException();
      }
      final int identifier = key.identifier;
      final byte sourceHint = source[identifier];
      if (sourceHint == SOURCE_UNDEFINED)
      {
        data[identifier] = getStyleProperty(key);
      }
      else
      {
        data[identifier] = properties[identifier];
      }
    }
    return data;
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
    final Object legacyVal = handleGetLegacyKeys(key);
    if (legacyVal == ElementStyleSheet.UNDEFINED_VALUE)
    {
      return defaultValue;
    }
    else if (legacyVal != null)
    {
      return legacyVal;
    }

    final int identifier = key.identifier;
    if (properties != null)
    {
      if (properties.length <= identifier)
      {
        throw new IllegalStateException();
      }

      final byte source = this.source[identifier];
      if (source != SOURCE_UNDEFINED)
      {
        final Object value = properties[identifier];
        if (value == null)
        {
          return defaultValue;
        }
        return value;
      }
    }

    // parents must always be queried ...
    parentsToCache();
    for (int i = 0; i < parentsCached.length; i++)
    {
      final ElementStyleSheet st = parentsCached[i].getStyleSheet();
      final Object value = st.getStyleProperty(key, null);
      if (value == null)
      {
        continue;
      }
      putInCache(key, value, SOURCE_FROM_PARENT);
      return value;
    }

    if (key.isInheritable())
    {
      if (cascadeStyleSheet != null)
      {
        final Object value = cascadeStyleSheet.getStyleProperty(key, null);
        if (value != null)
        {
          putInCache(key, value, SOURCE_FROM_PARENT);
          return value;
        }
      }
    }

    if (globalDefaultStyleSheet != null)
    {
      final Object value = globalDefaultStyleSheet.getStyleProperty(key, null);
      if (value != null)
      {
        putInCache(key, value, SOURCE_FROM_PARENT);
        return value;
      }
    }

    putInCache(key, null, SOURCE_FROM_PARENT);
    return defaultValue;
  }

  /**
   * Puts an object into the cache (if caching is enabled).
   *
   * @param key   the stylekey for that object
   * @param value the object.
   */
  private void putInCache(final StyleKey key, final Object value, final byte sourceHint)
  {
    if (properties == null)
    {
      final int definedStyleKeyCount = propertyKeys.length;
      properties = new Object[definedStyleKeyCount];
      source = new byte[definedStyleKeyCount];
    }

    final int identifier = key.identifier;
    properties[identifier] = value;
    source[identifier] = sourceHint;
  }

  /**
   * Sets a boolean style property.
   *
   * @param key   the style key (<code>null</code> not permitted).
   * @param value the value.
   * @throws NullPointerException if the given key is null.
   * @throws ClassCastException   if the value cannot be assigned with the given key.
   */
  public void setBooleanStyleProperty(final StyleKey key, final boolean value)
  {
    if (value)
    {
      setStyleProperty(key, Boolean.TRUE);
    }
    else
    {
      setStyleProperty(key, Boolean.FALSE);
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
    if (key == null)
    {
      throw new NullPointerException("ElementStyleSheet.setStyleProperty: key is null.");
    }
    if (handleSetLegacyKeys(key, value))
    {
      return;
    }

    final int identifier = key.identifier;
    if (value == null)
    {
      if (properties != null)
      {
        if (properties[identifier] == null)
        {
          return;
        }

        // invalidate the cache ..
        putInCache(key, null, SOURCE_UNDEFINED);

        changeTracker += 1;
        properties[identifier] = null;
        styleChangeSupport.fireStyleRemoved(key);
      }
      return;
    }

    if (key.getValueType().isAssignableFrom(value.getClass()) == false)
    {
      throw new ClassCastException("Value for key " + key.getName()
          + " is not assignable: " + value.getClass()
          + " is not assignable from " + key.getValueType());
    }
    if (properties == null)
    {
      final int definedStyleKeyCount = propertyKeys.length;
      properties = new Object[definedStyleKeyCount];
      source = new byte[definedStyleKeyCount];
    }

    if (ObjectUtilities.equal(properties[identifier], value))
    {
      // no need to change anything ..
      return;
    }

    // invalidate the cache ..
    putInCache(key, value, SOURCE_DIRECT);
    changeTracker += 1;

    styleChangeSupport.fireStyleChanged(key, value);
  }

  private boolean handleSetLegacyKeys(final StyleKey key, final Object value)
  {
    if (value == null)
    {
      return false;
    }
    if (key == ElementStyleKeys.ABSOLUTE_POS)
    {
      final Point2D point = (Point2D) value;
      setStyleProperty(ElementStyleKeys.POS_X, new Float(point.getX()));
      setStyleProperty(ElementStyleKeys.POS_Y, new Float(point.getY()));
      return true;
    }
    if (key == ElementStyleKeys.MINIMUMSIZE)
    {
      final Dimension2D dim = (Dimension2D) value;
      setStyleProperty(ElementStyleKeys.MIN_WIDTH, new Float(dim.getWidth()));
      setStyleProperty(ElementStyleKeys.MIN_HEIGHT, new Float(dim.getHeight()));
      return true;
    }
    if (key == ElementStyleKeys.MAXIMUMSIZE)
    {
      final Dimension2D dim = (Dimension2D) value;
      setStyleProperty(ElementStyleKeys.MAX_WIDTH, new Float(dim.getWidth()));
      setStyleProperty(ElementStyleKeys.MAX_HEIGHT, new Float(dim.getHeight()));
      return true;
    }
    if (key == ElementStyleKeys.PREFERREDSIZE)
    {
      final Dimension2D dim = (Dimension2D) value;
      setStyleProperty(ElementStyleKeys.WIDTH, new Float(dim.getWidth()));
      setStyleProperty(ElementStyleKeys.HEIGHT, new Float(dim.getHeight()));
      return true;
    }
    if (key == ElementStyleKeys.BORDER_BOTTOM_LEFT_RADIUS)
    {
      final Dimension2D dim = (Dimension2D) value;
      setStyleProperty(ElementStyleKeys.BORDER_BOTTOM_LEFT_RADIUS_WIDTH, new Float(dim.getWidth()));
      setStyleProperty(ElementStyleKeys.BORDER_BOTTOM_LEFT_RADIUS_HEIGHT, new Float(dim.getHeight()));
      return true;
    }
    if (key == ElementStyleKeys.BORDER_BOTTOM_RIGHT_RADIUS)
    {
      final Dimension2D dim = (Dimension2D) value;
      setStyleProperty(ElementStyleKeys.BORDER_BOTTOM_RIGHT_RADIUS_WIDTH, new Float(dim.getWidth()));
      setStyleProperty(ElementStyleKeys.BORDER_BOTTOM_RIGHT_RADIUS_HEIGHT, new Float(dim.getHeight()));
      return true;
    }
    if (key == ElementStyleKeys.BORDER_TOP_LEFT_RADIUS)
    {
      final Dimension2D dim = (Dimension2D) value;
      setStyleProperty(ElementStyleKeys.BORDER_TOP_LEFT_RADIUS_WIDTH, new Float(dim.getWidth()));
      setStyleProperty(ElementStyleKeys.BORDER_TOP_LEFT_RADIUS_HEIGHT, new Float(dim.getHeight()));
      return true;
    }
    if (key == ElementStyleKeys.BORDER_TOP_RIGHT_RADIUS)
    {
      final Dimension2D dim = (Dimension2D) value;
      setStyleProperty(ElementStyleKeys.BORDER_TOP_RIGHT_RADIUS_WIDTH, new Float(dim.getWidth()));
      setStyleProperty(ElementStyleKeys.BORDER_TOP_RIGHT_RADIUS_HEIGHT, new Float(dim.getHeight()));
      return true;
    }
    return false;
  }

  private Object handleGetLegacyKeys(final StyleKey key)
  {
    if (key == ElementStyleKeys.ABSOLUTE_POS)
    {
      final Float x = (Float) getStyleProperty(ElementStyleKeys.POS_X);
      final Float y = (Float) getStyleProperty(ElementStyleKeys.POS_Y);
      if (x == null || y == null)
      {
        return ElementStyleSheet.UNDEFINED_VALUE;
      }
      return new Point2D.Double(x.doubleValue(), y.doubleValue());
    }
    if (key == ElementStyleKeys.MINIMUMSIZE)
    {
      final Float w = (Float) getStyleProperty(ElementStyleKeys.MIN_WIDTH);
      final Float h = (Float) getStyleProperty(ElementStyleKeys.MIN_HEIGHT);
      if (w == null || h == null)
      {
        return ElementStyleSheet.UNDEFINED_VALUE;
      }
      return new FloatDimension(w.floatValue(), h.floatValue());
    }
    if (key == ElementStyleKeys.MAXIMUMSIZE)
    {
      final Float w = (Float) getStyleProperty(ElementStyleKeys.MAX_WIDTH);
      final Float h = (Float) getStyleProperty(ElementStyleKeys.MAX_HEIGHT);
      if (w == null || h == null)
      {
        return ElementStyleSheet.UNDEFINED_VALUE;
      }
      return new FloatDimension(w.floatValue(), h.floatValue());
    }
    if (key == ElementStyleKeys.PREFERREDSIZE)
    {
      final Float w = (Float) getStyleProperty(ElementStyleKeys.WIDTH);
      final Float h = (Float) getStyleProperty(ElementStyleKeys.HEIGHT);
      if (w == null || h == null)
      {
        return ElementStyleSheet.UNDEFINED_VALUE;
      }
      return new FloatDimension(w.floatValue(), h.floatValue());
    }
    if (key == ElementStyleKeys.BORDER_BOTTOM_LEFT_RADIUS)
    {
      final Float w = (Float) getStyleProperty(ElementStyleKeys.BORDER_BOTTOM_LEFT_RADIUS_WIDTH);
      final Float h = (Float) getStyleProperty(ElementStyleKeys.BORDER_BOTTOM_LEFT_RADIUS_HEIGHT);
      if (w == null || h == null)
      {
        return ElementStyleSheet.UNDEFINED_VALUE;
      }
      return new FloatDimension(w.floatValue(), h.floatValue());
    }
    if (key == ElementStyleKeys.BORDER_BOTTOM_RIGHT_RADIUS)
    {
      final Float w = (Float) getStyleProperty(ElementStyleKeys.BORDER_BOTTOM_RIGHT_RADIUS_WIDTH);
      final Float h = (Float) getStyleProperty(ElementStyleKeys.BORDER_BOTTOM_RIGHT_RADIUS_HEIGHT);
      if (w == null || h == null)
      {
        return ElementStyleSheet.UNDEFINED_VALUE;
      }
      return new FloatDimension(w.floatValue(), h.floatValue());
    }
    if (key == ElementStyleKeys.BORDER_TOP_LEFT_RADIUS)
    {
      final Float w = (Float) getStyleProperty(ElementStyleKeys.BORDER_TOP_LEFT_RADIUS_WIDTH);
      final Float h = (Float) getStyleProperty(ElementStyleKeys.BORDER_TOP_LEFT_RADIUS_HEIGHT);
      if (w == null || h == null)
      {
        return ElementStyleSheet.UNDEFINED_VALUE;
      }
      return new FloatDimension(w.floatValue(), h.floatValue());
    }
    if (key == ElementStyleKeys.BORDER_TOP_RIGHT_RADIUS)
    {
      final Float w = (Float) getStyleProperty(ElementStyleKeys.BORDER_TOP_RIGHT_RADIUS_WIDTH);
      final Float h = (Float) getStyleProperty(ElementStyleKeys.BORDER_TOP_RIGHT_RADIUS_HEIGHT);
      if (w == null || h == null)
      {
        return ElementStyleSheet.UNDEFINED_VALUE;
      }
      return new FloatDimension(w.floatValue(), h.floatValue());
    }
    return null;
  }

  /**
   * Creates and returns a copy of this object. After the cloning, the new StyleSheet is no longer registered with its
   * parents.
   *
   * @return a clone of this instance.
   * @see Cloneable
   */
  public Object clone()
      throws CloneNotSupportedException
  {
    try
    {
      final ElementStyleSheet sc = (ElementStyleSheet) super.clone();
      if (properties != null)
      {
        sc.properties = properties.clone();
      }
      if (source != null)
      {
        sc.source = source.clone();
      }
      //noinspection CloneCallsConstructors
      sc.styleChangeSupport = new StyleChangeSupport(sc);
      if (sc.parents != null)
      {
        parentsToCache();
        sc.parents = (ArrayList<StyleSheetCarrier>) parents.clone();
        sc.parents.clear();
        sc.parentsCached = new StyleSheetCarrier[parentsCached.length];
        for (int i = 0; i < parentsCached.length; i++)
        {
          final StyleSheetCarrier carrier = (StyleSheetCarrier) parentsCached[i].clone();
          sc.parentsCached[i] = carrier;
          sc.parents.add(carrier);
        }
      }
      sc.cascadeStyleSheet = null;
      sc.globalDefaultStyleSheet = globalDefaultStyleSheet;
      sc.pruneCachedEntries();
      return sc;
    }
    catch (CloneNotSupportedException cne)
    {
      throw new IllegalStateException("Clone failed.");
    }
  }

  public StyleSheet derive()
      throws CloneNotSupportedException
  {
    try
    {
      final ElementStyleSheet sc = (ElementStyleSheet) super.derive();
      if (properties != null)
      {
        sc.properties = properties.clone();
      }
      if (source != null)
      {
        sc.source = source.clone();
      }
      //noinspection CloneCallsConstructors
      sc.styleChangeSupport = new StyleChangeSupport(sc);
      if (sc.parents != null)
      {
        parentsToCache();
        sc.parents = (ArrayList<StyleSheetCarrier>) parents.clone();
        sc.parents.clear();
        sc.parentsCached = new StyleSheetCarrier[parentsCached.length];
        for (int i = 0; i < parentsCached.length; i++)
        {
          final StyleSheetCarrier carrier = (StyleSheetCarrier) parentsCached[i].clone();
          sc.parentsCached[i] = carrier;
          sc.parents.add(carrier);
        }
      }
      sc.cascadeStyleSheet = null;
      sc.globalDefaultStyleSheet = globalDefaultStyleSheet;
      sc.pruneCachedEntries();
      return sc;
    }
    catch (final CloneNotSupportedException cne)
    {
      throw new IllegalStateException("Clone failed.");
    }
  }

  protected StyleSheetCarrier[] getParentReferences()
  {
    parentsToCache();
    return parentsCached;
  }

  /**
   * Clones the style-sheet. The assigned parent style sheets are not cloned. The stylesheets are not assigned to the
   * contained stylesheet collection, you have to reassign them manually ...
   *
   * @return the clone.
   */
  public ElementStyleSheet getCopy()
      throws CloneNotSupportedException
  {
    return (ElementStyleSheet) clone();
  }

  /**
   * Creates the cached object array for the parent element style sheets.
   */
  private void parentsToCache()
  {
    if (parents == null)
    {
      parentsCached = ElementStyleSheet.EMPTY_PARENTS;
      return;
    }

    if (parentsCached == null)
    {
      parentsCached = (StyleSheetCarrier[])
          parents.toArray(new StyleSheetCarrier[parents.size()]);
    }
  }

  /**
   * Returns the font for this style-sheet.
   *
   * @return the font.
   * @deprecated This method will be removed in the next version.
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

    return new FontDefinition(name, size, bold, italic, underlined, strike,
        encoding, embed);
  }

  /**
   * Sets the font for this style-sheet.
   *
   * @param font the font (<code>null</code> not permitted).
   * @deprecated This method will be removed in the next version.
   */
  public void setFontDefinitionProperty(final FontDefinition font)
  {
    if (font == null)
    {
      throw new NullPointerException("ElementStyleSheet.setFontStyleProperty: font is null.");
    }
    setStyleProperty(TextStyleKeys.FONT, font.getFontName());
    setStyleProperty(TextStyleKeys.FONTSIZE, new Integer(font.getFontSize()));
    setBooleanStyleProperty(TextStyleKeys.BOLD, font.isBold());
    setBooleanStyleProperty(TextStyleKeys.ITALIC, font.isItalic());
    setBooleanStyleProperty(TextStyleKeys.UNDERLINED, font.isUnderline());
    setBooleanStyleProperty(TextStyleKeys.STRIKETHROUGH, font.isStrikeThrough());
    setBooleanStyleProperty(TextStyleKeys.EMBEDDED_FONT, font.isEmbeddedFont());
    setStyleProperty(TextStyleKeys.FONTENCODING, font.getFontEncoding(null));
  }

  /**
   * Returns an enumeration of all local property keys.
   *
   * @return an enumeration of all localy defined style property keys.
   */
  public Iterator getDefinedPropertyNames()
  {
    final ArrayList al = new ArrayList();
    if (source != null)
    {
      for (int i = 0; i < source.length; i++)
      {
        if (source[i] == SOURCE_DIRECT)
        {
          al.add(propertyKeys[i]);
        }
      }
    }
    return Collections.unmodifiableList(al).iterator();
  }

  public StyleKey[] getDefinedPropertyNamesArray()
  {
    if (source == null)
    {
      return ElementStyleSheet.EMPTY_KEYS;
    }

    final StyleKey[] retval = propertyKeys.clone();
    for (int i = 0; i < source.length; i++)
    {
      if (source[i] != SOURCE_DIRECT)
      {
        retval[i] = null;
      }
    }
    return retval;
  }

  /**
   * Adds a {@link StyleChangeListener}.
   *
   * @param l the listener.
   */
  public void addListener(final StyleChangeListener l)
  {
    styleChangeSupport.addListener(l);
  }

  /**
   * Removes a {@link StyleChangeListener}.
   *
   * @param l the listener.
   */
  public void removeListener(final StyleChangeListener l)
  {
    styleChangeSupport.removeListener(l);
  }

  /**
   * Forwards a change event notification to all registered {@link StyleChangeListener} objects.
   *
   * @param source the source of the change.
   * @param key    the style key.
   * @param value  the new value.
   */
  public void styleChanged(final ElementStyleSheet source, final StyleKey key,
                           final Object value)
  {
    if (source == this)
    {
      return;
    }

    if (key.isInheritable() == false)
    {
      return;
    }

    if (source != null && this.source != null)
    {
      if (this.source[key.identifier] == SOURCE_DIRECT)
      {
        return;
      }
    }

    changeTracker += 1;
    putInCache(key, value, SOURCE_FROM_PARENT);

    styleChangeSupport.fireStyleChanged(key, value);
  }

  /**
   * Forwards a change event notification to all registered {@link StyleChangeListener} objects.
   *
   * @param source the source of the change.
   * @param key    the style key.
   */
  public void styleRemoved(final ElementStyleSheet source, final StyleKey key)
  {
    if (source == this)
    {
      return;
    }
    if (key.isInheritable() == false)
    {
      return;
    }
    if (source != null && this.source != null)
    {
      if (this.source[key.identifier] == SOURCE_DIRECT)
      {
        return;
      }
    }

    changeTracker += 1;
    putInCache(key, null, SOURCE_UNDEFINED);
    styleChangeSupport.fireStyleRemoved(key);
  }

  /**
   * Helper method for serialization.
   *
   * @param out the output stream where to write the object.
   * @throws IOException if errors occur while writing the stream.
   */
  private void writeObject(final ObjectOutputStream out)
      throws IOException
  {
    out.defaultWriteObject();
    if (properties == null)
    {
      out.writeInt(0);
    }
    else
    {
      final int size = properties.length;
      out.writeInt(size);
      for (int i = 0; i < size; i++)
      {
        final Object value = properties[i];
        SerializerHelper.getInstance().writeObject(value, out);
      }
    }
  }

  /**
   * Helper method for serialization.
   *
   * @param in the input stream from where to read the serialized object.
   * @throws IOException            when reading the stream fails.
   * @throws ClassNotFoundException if a class definition for a serialized object could not be found.
   */
  private void readObject(final ObjectInputStream in)
      throws IOException, ClassNotFoundException
  {
    in.defaultReadObject();
    final int size = in.readInt();

    propertyKeys = StyleKey.getDefinedStyleKeys();
    styleChangeSupport = new StyleChangeSupport(this);
    parentsCached = null;

    if (size == 0)
    {
      properties = null;
      return;
    }

    if (size != propertyKeys.length)
    {
      throw new IOException("Encountered a different style-system configuration. This report cannot be deserialized.");
    }
    if (propertyKeys[0] == null)
    {
      throw new IllegalStateException();
    }
    properties = new Object[size];

    if (size == 0)
    {
      return;
    }
    final Object[] values = new Object[size];
    final SerializerHelper serHelper = SerializerHelper.getInstance();
    for (int i = 0; i < size; i++)
    {
      values[i] = serHelper.readObject(in);
    }

    for (int i = 0; i < size; i++)
    {
      final StyleKey key = propertyKeys[i];
      if (key != null)
      {
        final int identifier = key.identifier;
        properties[identifier] = values[i];
      }
    }
  }

  /**
   * Returns true, if this stylesheet is one of the global default stylesheets. Global default stylesheets are
   * unmodifiable and shared among all element stylesheets.
   *
   * @return true, if this is one of the unmodifiable global default stylesheets, false otherwise.
   */
  public boolean isGlobalDefault()
  {
    return false;
  }

  /**
   * Returns the property keys. This must return the same set of keys as a call to StyleSheet.getDefinedKeys(),
   * but it allows us to avoid the synchronization on that call.
   * 
   * @return the local copy of the style keys.
   */
  public StyleKey[] getPropertyKeys()
  {
    return propertyKeys.clone();
  }
}

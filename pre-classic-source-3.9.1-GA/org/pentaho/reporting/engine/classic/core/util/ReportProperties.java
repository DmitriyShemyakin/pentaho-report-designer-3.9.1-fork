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

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;

import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.libraries.base.util.LinkedMap;

/**
 * The report properties is a hashtable with string keys. ReportProperties are bound to a report as a general purpose
 * storage. ReportProperties bound to a JFreeReport object are visible to all generated report-state chains. A
 * ReportState will inherit all ReportProperties bound to the JFreeReport-object when the ReportState.StartState object
 * is created.  Properties bound to the report definition after the report state is created are not visible to the
 * ReportState and its children.
 * <p/>
 * ReportProperties bound to a ReportState are not visible to the report definition (the JFreeReport object), but are
 * visible to all ReportStates of that ReportState-chain. So when you add a property at the end of a report run to a
 * ReportState, the value of this property will be visible to all ReportStates when the report is restarted at a certain
 * point.
 * <p/>
 * ReportProperties can be seen as a stateless shared report internal storage area. All functions have access to the
 * properties by using the ReportState.getProperty() and ReportState.setProperty() functions.
 * <p/>
 * For a list of defined default properties, have a look at the {@link MasterReport} class.
 *
 * @author Thomas Morgner
 * @deprecated This is no longer valid. The properties are a weird concept and are inherently unclean. They have been
 *             replaced by the report environment and the bundle metadata.
 */
public class ReportProperties implements Serializable, Cloneable
{
  /**
   * Storage for the properties.
   */
  private LinkedMap properties;

  /**
   * The fall-back property-collection.
   */
  private ReportProperties masterProperties;

  /**
   * Copy constructor.
   *
   * @param props an existing ReportProperties instance.
   */
  public ReportProperties(final ReportProperties props)
  {
    try
    {
      this.properties = (LinkedMap) props.properties.clone();
    }
    catch (CloneNotSupportedException e)
    {
      throw new IllegalStateException("Should not happen");
    }
  }

  /**
   * Default constructor.
   */
  public ReportProperties()
  {
    this.properties = new LinkedMap();
  }

  /**
   * Adds a property to this properties collection. If a property with the given name exist, the property will be
   * replaced with the new value. If the value is null, the property will be removed.
   *
   * @param key   the property key.
   * @param value the property value.
   */
  public void put(final String key, final Object value)
  {
    if (key == null)
    {
      throw new NullPointerException
          ("ReportProperties.put (..): Parameter 'key' must not be null");
    }
    if (value == null)
    {
      this.properties.remove(key);
    }
    else
    {
      this.properties.put(key, value);
    }
  }

  /**
   * Retrieves the value stored for a key in this properties collection.
   *
   * @param key the property key.
   * @return The stored value, or <code>null</code> if the key does not exist in this collection.
   */
  public Object get(final String key)
  {
    if (key == null)
    {
      throw new NullPointerException
          ("ReportProperties.get (..): Parameter 'key' must not be null");
    }
    return get(key, null);
  }

  /**
   * Retrieves the value stored for a key in this properties collection, and returning the default value if the key was
   * not stored in this properties collection.
   *
   * @param key          the property key.
   * @param defaultValue the default value to be returned when the key is not stored in this properties collection.
   * @return The stored value, or the default value if the key does not exist in this collection.
   */
  public Object get(final String key, final Object defaultValue)
  {
    if (key == null)
    {
      throw new NullPointerException
          ("ReportProperties.get (..): Parameter 'key' must not be null");
    }
    final Object o = this.properties.get(key);
    if (o == null)
    {
      if (masterProperties != null)
      {
        return masterProperties.get(key, defaultValue);
      }
      return defaultValue;
    }
    return o;
  }

  /**
   * Returns all property keys as enumeration.
   *
   * @return an enumeration of the property keys.
   */
  public Iterator keys()
  {
    return Arrays.asList(this.properties.keys()).iterator();
  }

  /**
   * Removes all properties stored in this collection.
   */
  public void clear()
  {
    this.properties.clear();
  }

  /**
   * Checks whether the given key is stored in this collection of ReportProperties.
   *
   * @param key the property key.
   * @return true, if the given key is known.
   */
  public boolean containsKey(final String key)
  {
    if (key == null)
    {
      throw new NullPointerException
          ("ReportProperties.containsKey (..): Parameter key must not be null");
    }
    return this.properties.containsKey(key);
  }

  /**
   * Clones the properties.
   *
   * @return a copy of this ReportProperties object.
   * @throws CloneNotSupportedException this should never happen.
   */
  public Object clone()
      throws CloneNotSupportedException
  {
    final ReportProperties p = (ReportProperties) super.clone();
    p.properties = (LinkedMap) this.properties.clone();
    p.masterProperties = null;
    return p;
  }

  /**
   * Returns true, if there is at least one marked property.
   *
   * @return true, if there are some properties marked, false otherwise.
   */
  public boolean containsMarkedProperties()
  {
    return true;
  }

  /**
   * Returns the fall-back property-collection. If defined, this collection will be used if a queried property is not
   * defined in this collection.
   *
   * @return the fall-back collection.
   */
  public ReportProperties getMasterProperties()
  {
    return masterProperties;
  }

  /**
   * Defines the fall-back property-collection. If defined, this collection will be used if a queried property is not
   * defined in this collection.
   *
   * @param masterProperties the fall-back collection.
   */
  public void setMasterProperties(final ReportProperties masterProperties)
  {
    this.masterProperties = masterProperties;
  }

  /**
   * Returns all defined keys as string-array.
   *
   * @return the keys as array.
   */
  public String[] keyArray()
  {
    return (String[]) properties.keys(new String[properties.size()]);
  }

  /**
   * Returns the number of entries in this collection.
   *
   * @return the number of properties defined here.
   */
  public int size()
  {
    return properties.size();
  }
}

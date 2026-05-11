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
 * Copyright (c) 2009 Pentaho Corporation..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.filter.types;

import java.sql.Blob;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;
import org.pentaho.reporting.engine.classic.core.metadata.ElementTypeRegistry;
import org.pentaho.reporting.libraries.base.util.IOUtils;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

/**
 * Todo: Document me!
 * <p/>
 * Date: 01.06.2009
 * Time: 13:35:08
 *
 * @author Thomas Morgner.
 */
public class ExternalElementType implements ElementType
{
  public static final ElementType INSTANCE = new ExternalElementType();

  private transient ElementMetaData elementType;
  private static final Log logger = LogFactory.getLog(ExternalElementType.class);

  public ExternalElementType()
  {
  }

  public void configureDesignTimeDefaults(final Element element, final Locale locale)
  {

  }

  public ElementMetaData getMetaData()
  {
    if (elementType == null)
    {
      elementType = ElementTypeRegistry.getInstance().getElementType("external-element-field");
    }
    return elementType;
  }

  public Object getDesignValue(final ExpressionRuntime runtime, final Element element)
  {
    final Object staticValue = ElementTypeUtils.queryStaticValue(element);
    if (staticValue != null)
    {
      return staticValue;
    }
    return ElementTypeUtils.queryFieldName(element);
  }

  /**
   * Returns the current value for the data source.
   *
   * @param runtime the expression runtime that is used to evaluate formulas and expressions when computing the value of
   *                this filter.
   * @param element the element for which the data is computed.
   * @return the value.
   */
  public Object getValue(final ExpressionRuntime runtime, final Element element)
  {
    if (runtime == null)
    {
      throw new NullPointerException("Runtime must never be null.");
    }
    if (element == null)
    {
      throw new NullPointerException("Element must never be null.");
    }

    final Object value = ElementTypeUtils.queryFieldOrValue(runtime, element);
    if (value != null)
    {
      final Object filteredValue = filter(runtime, element, value);
      if (filteredValue != null)
      {
        return filteredValue;
      }
    }
    final Object nullValue = element.getAttribute(AttributeNames.Core.NAMESPACE, AttributeNames.Core.NULL_VALUE);
    return filter(runtime, element, nullValue);
  }

  private Object filter(final ExpressionRuntime runtime, final Element element, final Object value)
  {
    if (value instanceof Element)
    {
      return value;
    }

    try
    {
      final ResourceKey contentBase = runtime.getProcessingContext().getContentBase();
      final ResourceManager resManager = runtime.getProcessingContext().getResourceManager();
      final ResourceKey key;
      if (value instanceof ResourceKey)
      {
        key = (ResourceKey) value;
      }
      else if (value instanceof Blob)
      {
        final Blob b = (Blob) value;
        final byte[] data = IOUtils.getInstance().readBlob(b);
        key = resManager.createKey(data);
      }
      else if (value instanceof String)
      {
        final Object baseURL = element.getAttribute(AttributeNames.Core.NAMESPACE, AttributeNames.Core.CONTENT_BASE);
        if (baseURL != null)
        {
          final ResourceKey baseKey = resManager.createKey(baseURL);
          key = resManager.deriveKey(baseKey, (String) value);
        }
        else
        {
          key = resManager.deriveKey(contentBase, (String) value);
        }
      }
      else
      {
        key = resManager.createKey(value);
      }
      if (key == null)
      {
        return null;
      }

      Class target;
      Object targetRaw = element.getAttribute(AttributeNames.Core.NAMESPACE, AttributeNames.Core.TARGET_TYPE);
      if (targetRaw instanceof String)
      {
        final ClassLoader loader = ObjectUtilities.getClassLoader(ExternalElementType.class);
        target = Class.forName((String) targetRaw, false, loader);

        if (target == null)
        {
          return null;
        }
      }
      else
      {
        target = SubReport.class;
      }

      final Resource resource = resManager.create(key, contentBase, target);
      final Object resourceContent = resource.getResource();
      if (resourceContent instanceof Element)
      {
        return resourceContent;
      }
    }
    catch (Exception e)
    {
      logger.warn("Failed to load content using value " + value, e);
    }
    return null;
  }

  /**
   * Clones this <code>DataSource</code>.
   *
   * @return the clone.
   * @throws CloneNotSupportedException this should never happen.
   */
  public Object clone() throws CloneNotSupportedException
  {
    return super.clone();
  }
}

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

package org.pentaho.reporting.engine.classic.core.filter.types;

import java.util.Locale;

import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.filter.ResourceMessageFormatFilter;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;
import org.pentaho.reporting.engine.classic.core.metadata.ElementTypeRegistry;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class ResourceMessageType implements ElementType
{
  private ResourceMessageFormatFilter messageFormatFilter;
  private transient ElementMetaData elementType;

  public ResourceMessageType()
  {
    messageFormatFilter = new ResourceMessageFormatFilter();
  }

  public ElementMetaData getMetaData()
  {
    if (elementType == null)
    {
      elementType = ElementTypeRegistry.getInstance().getElementType("resource-message");
    }
    return elementType;
  }

  public Object getDesignValue(final ExpressionRuntime runtime, final Element element)
  {
    final Object resourceMessageRaw = ElementTypeUtils.queryStaticValue(element);
    final String resourceKey = resourceMessageRaw == null ? "<null>" : resourceMessageRaw.toString();
    return resourceKey;
  }

  /**
   * Returns the current value for the data source.
   *
   * @param runtime the expression runtime that is used to evaluate formulas and expressions when computing the value of
   *                this filter.
   * @param element the element from which to read attribute.
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

    final Object nullValue = element.getAttribute
        (AttributeNames.Core.NAMESPACE, AttributeNames.Core.NULL_VALUE);
    final Object message = ElementTypeUtils.queryStaticValue(element);
    if (message == null)
    {
      return nullValue;
    }

    final Object resourceId = element.getAttribute
        (AttributeNames.Core.NAMESPACE, AttributeNames.Core.RESOURCE_IDENTIFIER);
    if (resourceId == null)
    {
      return nullValue;
    }

    messageFormatFilter.setFormatKey(String.valueOf(message));
    messageFormatFilter.setResourceIdentifier(String.valueOf(resourceId));

    final Object messageNullValue = element.getAttribute
        (AttributeNames.Core.NAMESPACE, AttributeNames.Core.MESSAGE_NULL_VALUE);
    if (messageNullValue != null)
    {
      messageFormatFilter.setNullString(String.valueOf(messageNullValue));
    }
    else if (nullValue != null)
    {
      messageFormatFilter.setNullString(String.valueOf(nullValue));
    }
    else
    {
      messageFormatFilter.setNullString(null);
    }

    final Object value = messageFormatFilter.getValue(runtime, element);
    if (value == null)
    {
      return nullValue;
    }
    return String.valueOf(value);
  }

  /**
   * Clones this <code>DataSource</code>.
   *
   * @return the clone.
   * @throws CloneNotSupportedException this should never happen.
   */
  public Object clone() throws CloneNotSupportedException
  {
    final ResourceMessageType clone = (ResourceMessageType) super.clone();
    if (clone.messageFormatFilter != null)
    {
      clone.messageFormatFilter = (ResourceMessageFormatFilter) messageFormatFilter.clone();
    }
    return clone;
  }

  public void configureDesignTimeDefaults(final Element element, final Locale locale)
  {

  }
}
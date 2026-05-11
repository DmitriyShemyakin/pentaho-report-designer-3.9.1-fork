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
 * Copyright (c) 2008 - 2009 Pentaho Corporation, .  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.ext.charting;

import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.filter.types.ContentType;
import org.pentaho.reporting.engine.classic.core.filter.types.ElementTypeUtils;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ElementTypeRegistry;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

/**
 * Todo: Document me!
 *
 * @author : Thomas Morgner
 */
public class ChartElementType extends ContentType
{
  private transient ElementMetaData elementType;

  public ChartElementType()
  {
  }

  public ElementMetaData getMetaData()
  {
    if (elementType == null)
    {
      elementType = ElementTypeRegistry.getInstance().getElementType("pentaho-chart");
    }
    return elementType;
  }

  public Object getValue(final ExpressionRuntime runtime,
                         final Element element)
  {
    final Object value = ElementTypeUtils.queryFieldOrValue(runtime, element);
    if (value == null)
    {
      return filter(runtime, element,
          element.getAttribute(AttributeNames.Core.NAMESPACE, AttributeNames.Core.NULL_VALUE));
    }

    try
    {
      final ResourceManager resourceManager = runtime.getProcessingContext().getResourceManager();
      final ResourceKey chartKey;
      if (value instanceof String)
      {
        chartKey = resourceManager.deriveKey(runtime.getProcessingContext().getContentBase(), (String) value);
      }
      else
      {
        chartKey = resourceManager.createKey(value);
      }
      
      return null;
    }
    catch (ResourceException e)
    {
      return filter(runtime, element,
          element.getAttribute(AttributeNames.Core.NAMESPACE, AttributeNames.Core.NULL_VALUE));
    }
//    catch (ChartProcessingException e)
//    {
//      return filter(runtime, element,
//          element.getAttribute(AttributeNames.Core.NAMESPACE, AttributeNames.Core.NULL_VALUE));
//    }

  }

  public Object clone() throws CloneNotSupportedException
  {
    return super.clone();
  }
}

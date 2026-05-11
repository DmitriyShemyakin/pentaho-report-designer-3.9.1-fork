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

package org.pentaho.reporting.engine.classic.core.metadata;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.util.HashMap;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class DefaultReportPreProcessorMetaData extends AbstractMetaData implements ReportPreProcessorMetaData
{
  private Class expressionType;
  private HashMap properties;
  private BeanInfo beanInfo;
  private boolean autoProcessor;
  
  public DefaultReportPreProcessorMetaData(final String bundleLocation,
                                           final String keyPrefix,
                                           final boolean expert,
                                           final boolean preferred,
                                           final boolean hidden,
                                           final boolean deprecated,
                                           final Class expressionType,
                                           final HashMap attributes,
                                           final BeanInfo beanInfo,
                                           final boolean autoProcessor,
                                           final boolean experimental,
                                           final int compatibilityLevel)
  {
    super(expressionType.getName(), bundleLocation, keyPrefix, expert,
        preferred, hidden, deprecated, experimental, compatibilityLevel);
    if (attributes == null)
    {
      throw new NullPointerException();
    }
    if (beanInfo == null)
    {
      throw new NullPointerException();
    }

    this.autoProcessor = autoProcessor;
    this.expressionType = expressionType;
    this.properties = (HashMap) attributes.clone();
    this.beanInfo = beanInfo;
  }

  public Class getPreProcessorType()
  {
    return expressionType;
  }

  public ReportPreProcessorPropertyMetaData getPropertyDescription(final String name)
  {
    return (ReportPreProcessorPropertyMetaData) properties.get(name);
  }

  public String[] getPropertyNames()
  {
    return (String[]) properties.keySet().toArray
        (new String[properties.size()]);
  }

  public ReportPreProcessorPropertyMetaData[] getPropertyDescriptions()
  {
    return (ReportPreProcessorPropertyMetaData[]) properties.values().toArray
        (new ReportPreProcessorPropertyMetaData[properties.size()]);
  }

  public BeanInfo getBeanDescriptor() throws IntrospectionException
  {
    return beanInfo;
  }

  public boolean isAutoProcessor()
  {
    return autoProcessor;
  }
}
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

import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;

import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

public class DefaultReportPreProcessorPropertyMetaData extends AbstractMetaData
    implements ReportPreProcessorPropertyMetaData
{
  private boolean mandatory;
  private String propertyRole;
  private PropertyDescriptor propertyDescriptor;
  private String propertyEditorClass;
  private boolean computed;
  private ReportPreProcessorPropertyCore reportPreProcessorCore;

  public DefaultReportPreProcessorPropertyMetaData(final String name,
                                                   final String bundleLocation,
                                                   final String keyPrefix,
                                                   final boolean expert,
                                                   final boolean preferred,
                                                   final boolean hidden,
                                                   final boolean deprecated,
                                                   final boolean mandatory,
                                                   final boolean computed,
                                                   final String propertyRole,
                                                   final PropertyDescriptor propertyDescriptor,
                                                   final String propertyEditorClass,
                                                   final ReportPreProcessorPropertyCore reportPreProcessorCore,
                                                   final boolean experimental,
                                                   final int compatibilityLevel)
  {
    super(name, bundleLocation, keyPrefix, expert, preferred, hidden, deprecated, experimental, compatibilityLevel);
    if (propertyRole == null)
    {
      throw new NullPointerException();
    }
    if (propertyDescriptor == null)
    {
      throw new NullPointerException();
    }
    if (reportPreProcessorCore == null)
    {
      throw new NullPointerException();
    }

    this.reportPreProcessorCore = reportPreProcessorCore;
    this.computed = computed;
    this.propertyEditorClass = propertyEditorClass;
    this.mandatory = mandatory;
    this.propertyRole = propertyRole;
    this.propertyDescriptor = propertyDescriptor;
  }

  public boolean isComputed()
  {
    return computed;
  }

  public Class getPropertyType()
  {
    return propertyDescriptor.getPropertyType();
  }

  public String getPropertyRole()
  {
    return propertyRole;
  }

  public boolean isMandatory()
  {
    return mandatory;
  }

  public String[] getReferencedFields(final Expression element, final Object attributeValue)
  {
    return reportPreProcessorCore.getReferencedFields(this, element, attributeValue);
  }

  public String[] getReferencedGroups(final Expression element, final Object attributeValue)
  {
    return reportPreProcessorCore.getReferencedGroups(this, element, attributeValue);
  }

  public String[] getReferencedElements(final Expression expression, final Object attributeValue)
  {
    return reportPreProcessorCore.getReferencedElements(this, expression, attributeValue);
  }

  public ResourceReference[] getReferencedResources(final Expression expression,
                                                    final Object attributeValue,
                                                    final Element reportElement,
                                                    final ResourceManager resourceManager)
  {
    return reportPreProcessorCore.getReferencedResources(this, expression, attributeValue, reportElement, resourceManager);
  }

  public PropertyDescriptor getBeanDescriptor()
  {
    return propertyDescriptor;
  }

  public PropertyEditor getEditor()
  {
    if (propertyEditorClass == null)
    {
      return null;
    }
    return (PropertyEditor) ObjectUtilities.loadAndInstantiate
        (propertyEditorClass, DefaultAttributeMetaData.class, PropertyEditor.class);
  }

  public String[] getExtraCalculationFields()
  {
    return reportPreProcessorCore.getExtraCalculationFields(this);
  }
}

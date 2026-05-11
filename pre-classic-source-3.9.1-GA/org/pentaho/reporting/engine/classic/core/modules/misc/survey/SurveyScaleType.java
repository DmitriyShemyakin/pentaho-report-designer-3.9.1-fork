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

package org.pentaho.reporting.engine.classic.core.modules.misc.survey;

import java.util.Locale;

import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.filter.types.ContentType;
import org.pentaho.reporting.engine.classic.core.filter.types.ElementTypeUtils;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ElementTypeRegistry;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class SurveyScaleType extends ContentType
{
  private transient ElementMetaData elementType;

  public ElementMetaData getMetaData()
  {
    if (elementType == null)
    {
      elementType = ElementTypeRegistry.getInstance().getElementType("survey-scale");
    }
    return elementType;
  }

  public Object getDesignValue(final ExpressionRuntime runtime, final Element element)
  {
    final Object value = ElementTypeUtils.queryFieldOrValue(runtime, element);
    Number[] numbers = ElementTypeUtils.getData(value);
    if (numbers == null)
    {
      numbers = new Number[]{new Integer(1), new Integer(2), new Integer(4)};
    }

    final int lowest = ElementTypeUtils.getIntAttribute
        (element, SurveyModule.NAMESPACE, SurveyModule.LOWEST, 1);
    final int highest = ElementTypeUtils.getIntAttribute
        (element, SurveyModule.NAMESPACE, SurveyModule.HIGHEST, 5);

    final Number rangeLowerBound = ElementTypeUtils.getNumberAttribute
        (element, SurveyModule.NAMESPACE, SurveyModule.RANGE_LOWER_BOUND, null);
    final Number rangeUpperBound = ElementTypeUtils.getNumberAttribute
        (element, SurveyModule.NAMESPACE, SurveyModule.RANGE_UPPER_BOUND, null);

    final SurveyScale drawable = new SurveyScale(lowest, highest, numbers);
    drawable.setRangeLowerBound(rangeLowerBound);
    drawable.setRangeUpperBound(rangeUpperBound);
    drawable.setDrawBorder(false);
    return (drawable);
  }

  public void configureDesignTimeDefaults(final Element element, final Locale locale)
  {
    element.setAttribute(SurveyModule.NAMESPACE, SurveyModule.LOWEST, new Integer(1));
    element.setAttribute(SurveyModule.NAMESPACE, SurveyModule.HIGHEST, new Integer(5));
  }

  public Object getValue(final ExpressionRuntime runtime, final Element element)
  {
    final Object value = ElementTypeUtils.queryFieldOrValue(runtime, element);
    final Number[] numbers = ElementTypeUtils.getData(value);
    if (numbers == null)
    {
      final Object nullValue = element.getAttribute(AttributeNames.Core.NAMESPACE, AttributeNames.Core.NULL_VALUE);
      return filter(runtime, element, nullValue);
    }

    final int lowest = ElementTypeUtils.getIntAttribute
        (element, SurveyModule.NAMESPACE, SurveyModule.LOWEST, 1);
    final int highest = ElementTypeUtils.getIntAttribute
        (element, SurveyModule.NAMESPACE, SurveyModule.HIGHEST, 5);

    final Number rangeLowerBound = ElementTypeUtils.getNumberAttribute
        (element, SurveyModule.NAMESPACE, SurveyModule.RANGE_LOWER_BOUND, null);
    final Number rangeUpperBound = ElementTypeUtils.getNumberAttribute
        (element, SurveyModule.NAMESPACE, SurveyModule.RANGE_UPPER_BOUND, null);

    final SurveyScale drawable = new SurveyScale(lowest, highest, numbers);
    drawable.setRangeLowerBound(rangeLowerBound);
    drawable.setRangeUpperBound(rangeUpperBound);
    drawable.setDrawBorder(false);
    return (drawable);
  }

  public Object clone() throws CloneNotSupportedException
  {
    return super.clone();
  }
}

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

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.filter.FormatSpecification;
import org.pentaho.reporting.engine.classic.core.filter.RawDataSource;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;
import org.pentaho.reporting.engine.classic.core.metadata.ElementTypeRegistry;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class DateFieldType implements ElementType, RawDataSource
{
  private transient ElementMetaData elementType;
  private transient SimpleDateFormat dateFormat;
  private transient Locale locale;
  private transient String formatString;
  private transient TimeZone timeZone;

  public DateFieldType()
  {
  }

  public ElementMetaData getMetaData()
  {
    if (elementType == null)
    {
      elementType = ElementTypeRegistry.getInstance().getElementType("date-field");
    }
    return elementType;
  }

  public Object getDesignValue(final ExpressionRuntime runtime, final Element element)
  {
    Object formatStringRaw =
        element.getAttribute(AttributeNames.Core.NAMESPACE, AttributeNames.Core.FORMAT_STRING);
    final Object staticValue = ElementTypeUtils.queryStaticValue(element);
    if (staticValue instanceof Date)
    {
      if (formatStringRaw == null)
      {
        // return the default to-string behavior of java.util.Date
        formatStringRaw = "EEE MMM dd HH:mm:ss zzz yyyy";
      }
      final Locale locale = runtime.getResourceBundleFactory().getLocale();
      final TimeZone timeZone = runtime.getResourceBundleFactory().getTimeZone();
      final SimpleDateFormat dateFormat = new SimpleDateFormat(String.valueOf(formatStringRaw), locale);
      dateFormat.setDateFormatSymbols(new DateFormatSymbols(locale));
      dateFormat.setTimeZone(timeZone);
      return dateFormat.format(staticValue);
    }
    return ElementTypeUtils.queryFieldName(element);
  }

  /**
   * Returns the unformated raw value. Whether that raw value is useable for the export is beyond the scope of this API
   * definition, but providing access to {@link Number} or {@link java.util.Date} objects is a good idea.
   *
   * @param runtime the expression runtime that is used to evaluate formulas and expressions when computing the value of
   *                this filter.
   * @param element
   * @return the raw data.
   */
  public Object getRawValue(final ExpressionRuntime runtime, final Element element)
  {
    if (runtime == null)
    {
      throw new NullPointerException("Runtime must never be null.");
    }
    if (element == null)
    {
      throw new NullPointerException("Element must never be null.");
    }

    final Object retval = ElementTypeUtils.queryFieldOrValue(runtime, element);
    if (retval instanceof Date == false)
    {
      return element.getAttribute(AttributeNames.Core.NAMESPACE, AttributeNames.Core.NULL_VALUE);
    }
    return retval;
  }

  /**
   * Returns information about the formatstring that was used to transform a raw-value into a formatted text. Not all
   * elements will make use of a format-string. These elements will return {@link org.pentaho.reporting.engine.classic.core.filter.FormatSpecification#TYPE_UNDEFINED}
   * in that case.
   *
   * @param runtime             the Expression runtime used to possibly compute the raw-value.
   * @param element             the element to which this datasource is added.
   * @param formatSpecification the format specification (can be null).
   * @return a filled format specififcation. If the
   *         <code>formatSpecification</code> parameter was not null, this given instance is reused.
   */
  public FormatSpecification getFormatString(final ExpressionRuntime runtime,
                                             final Element element,
                                             FormatSpecification formatSpecification)
  {
    if (formatSpecification == null)
    {
      formatSpecification = new FormatSpecification();
    }

    final Object formatStringRaw =
        element.getAttribute(AttributeNames.Core.NAMESPACE, AttributeNames.Core.FORMAT_STRING);

    if (formatStringRaw == null)
    {
      // return the default to-string behavior of java.util.Date
      formatSpecification.redefine(FormatSpecification.TYPE_DATE_FORMAT, "dd.MM.yyyy HH:mm:ss");
    }
    else
    {
      formatSpecification.redefine(FormatSpecification.TYPE_DATE_FORMAT, String.valueOf(formatStringRaw));
    }
    return formatSpecification;
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

    final Object retval = ElementTypeUtils.queryFieldOrValue(runtime, element);
    if (retval instanceof Date == false)
    {
      return element.getAttribute(AttributeNames.Core.NAMESPACE, AttributeNames.Core.NULL_VALUE);
    }

    Object formatStringRaw =
        element.getAttribute(AttributeNames.Core.NAMESPACE, AttributeNames.Core.FORMAT_STRING);
    if (formatStringRaw == null)
    {
      // return the default to-string behavior of java.util.Date
      formatStringRaw = "EEE MMM dd HH:mm:ss zzz yyyy";
    }

    try
    {
      final Locale locale = runtime.getResourceBundleFactory().getLocale();
      final TimeZone timeZone = runtime.getResourceBundleFactory().getTimeZone();
      if (dateFormat == null)
      {
        this.formatString = String.valueOf(formatStringRaw);
        this.locale = locale;
        this.timeZone = timeZone;
        this.dateFormat = new SimpleDateFormat(formatString, locale);
        this.dateFormat.setDateFormatSymbols(new DateFormatSymbols(locale));
        this.dateFormat.setTimeZone(timeZone);
      }
      else
      {
        if (ObjectUtilities.equal(formatString, formatStringRaw) == false)
        {
          this.formatString = String.valueOf(formatStringRaw);
          this.dateFormat.applyPattern(formatString);
        }

        if (ObjectUtilities.equal(this.locale, locale) == false)
        {
          this.locale = locale;
          this.dateFormat.setDateFormatSymbols(new DateFormatSymbols(locale));
        }

        if (ObjectUtilities.equal(this.timeZone, timeZone) == false)
        {
          this.timeZone = timeZone;
          this.dateFormat.setTimeZone(timeZone);
        }
      }

      return dateFormat.format(retval);
    }
    catch (Exception e)
    {
      return element.getAttribute(AttributeNames.Core.NAMESPACE, AttributeNames.Core.NULL_VALUE);
    }
  }

  /**
   * Clones this <code>DataSource</code>.
   *
   * @return the clone.
   * @throws CloneNotSupportedException this should never happen.
   */
  public Object clone() throws CloneNotSupportedException
  {
    final DateFieldType o = (DateFieldType) super.clone();
    if (o.dateFormat != null)
    {
      o.dateFormat = (SimpleDateFormat) dateFormat.clone();
    }
    return o;
  }

  public void configureDesignTimeDefaults(final Element element, final Locale locale)
  {

  }
}
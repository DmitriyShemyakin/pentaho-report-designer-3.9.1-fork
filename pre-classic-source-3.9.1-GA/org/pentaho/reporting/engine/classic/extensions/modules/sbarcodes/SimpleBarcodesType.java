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

package org.pentaho.reporting.engine.classic.extensions.modules.sbarcodes;

import java.awt.Color;
import java.util.Locale;

import net.sourceforge.barbecue.Barcode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.filter.types.ContentType;
import org.pentaho.reporting.engine.classic.core.filter.types.ElementTypeUtils;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ElementTypeRegistry;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;

/**
 * This <code>ElementType</code> is responsible to create the barcode object as defined by its definition named
 * <code>simple-barcodes</code>.
 *
 * @author Cedric Pronzato
 */
public class SimpleBarcodesType extends ContentType
{
  private static final Log logger = LogFactory.getLog(SimpleBarcodesType.class);
  private static final Color ALPHA = new Color(255, 255, 255, 0);
  private transient ElementMetaData elementType;

  public ElementMetaData getMetaData()
  {
    if (elementType == null)
    {
      elementType = ElementTypeRegistry.getInstance().getElementType("simple-barcodes");
    }
    return elementType;
  }

  public Object getDesignValue(final ExpressionRuntime runtime, final Element element)
  {
    Object value = ElementTypeUtils.queryStaticValue(element);
    if (value == null)
    {
      value = "Barcode";
    }

    return createBarcode(runtime, element, value);
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
    final Object value = ElementTypeUtils.queryFieldOrValue(runtime, element);
    if (value == null)
    {
      final Object nullValue = element.getAttribute(AttributeNames.Core.NAMESPACE, AttributeNames.Core.NULL_VALUE);
      return filter(runtime, element, nullValue);
    }
    return createBarcode(runtime, element, value);
  }

  private Object createBarcode(final ExpressionRuntime runtime,
                               final Element element,
                               final Object value)
  {
    // retrieve custim barcode attributes
    final String type = (String) element.getAttribute
        (SimpleBarcodesAttributeNames.NAMESPACE, SimpleBarcodesAttributeNames.TYPE_ATTRIBUTE);
    if (type == null)
    {
      final Object nullValue = element.getAttribute(AttributeNames.Core.NAMESPACE, AttributeNames.Core.NULL_VALUE);
      return filter(runtime, element, nullValue);
    }

    // common style attributes
    final Color color = (Color) element.getStyle().getStyleProperty(ElementStyleKeys.PAINT, Color.BLACK);
    final Color backgroundColor = (Color) element.getStyle().getStyleProperty(ElementStyleKeys.BACKGROUND_COLOR, ALPHA);

    // retrieve custom barcode styles
    final int barHeight = ElementTypeUtils.getIntAttribute(element, SimpleBarcodesModule.NAMESPACE, "bar-height", 10);
    final int barWidth = ElementTypeUtils.getIntAttribute(element, SimpleBarcodesModule.NAMESPACE, "bar-width", 1);
    final boolean showText = ElementTypeUtils.getBooleanAttribute
        (element, SimpleBarcodesModule.NAMESPACE, "show-text", true);
    final boolean checksum = ElementTypeUtils.getBooleanAttribute
        (element, SimpleBarcodesAttributeNames.NAMESPACE, SimpleBarcodesAttributeNames.CHECKSUM_ATTRIBUTE, true);

    try
    {
      // create barcde and set its properties
      final Barcode barcode = SimpleBarcodesUtility.createBarcode(value.toString(), type, checksum);
      if (barcode == null)
      {
        final Object nullValue = element.getAttribute(AttributeNames.Core.NAMESPACE, AttributeNames.Core.NULL_VALUE);
        return filter(runtime, element, nullValue);
      }
      barcode.setBackground(backgroundColor);
      barcode.setForeground(color);
      barcode.setDrawingText(showText);
      barcode.setBarWidth(barWidth);
      barcode.setBarHeight(barHeight);
      barcode.setOpaque(backgroundColor.getAlpha() == 255);
      return new BarcodeWrapper(barcode);
    }
    catch (Exception e)
    {
      if (logger.isInfoEnabled())
      {
        logger.info("Error creating barcode, falling back to null value", e);
      }
      final Object nullValue = element.getAttribute(AttributeNames.Core.NAMESPACE, AttributeNames.Core.NULL_VALUE);
      return filter(runtime, element, nullValue);
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
    return super.clone();
  }

  public void configureDesignTimeDefaults(final Element element, final Locale locale)
  {
    element.setAttribute(SimpleBarcodesAttributeNames.NAMESPACE, SimpleBarcodesAttributeNames.TYPE_ATTRIBUTE,
        SimpleBarcodesUtility.BARCODE_CODE128);
  }
}

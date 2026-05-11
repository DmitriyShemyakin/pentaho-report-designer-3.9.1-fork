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
 * Copyright (c) 2001 - 2009 Object Refinery Ltd, Pentaho Corporation and Contributors.  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.extensions.modules.sparklines.xml;

import java.awt.Color;
import java.io.IOException;

import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.BundleNamespaces;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.StyleWriterUtility;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.elements.AbstractElementWriteHandler;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.engine.classic.core.util.beans.ColorValueConverter;
import org.pentaho.reporting.engine.classic.extensions.modules.sparklines.SparklineModule;
import org.pentaho.reporting.engine.classic.extensions.modules.sparklines.SparklineStyleKeys;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public abstract class AbstractSparklineWriteHandler extends AbstractElementWriteHandler
{
  private static final ColorValueConverter colorValueConverter = new ColorValueConverter();

  protected AbstractSparklineWriteHandler()
  {
  }

  protected void writeStyleInformation(final XmlWriter writer, final ElementStyleSheet style) throws IOException
  {
    final String parentList = StyleWriterUtility.computeParentStyleList(style);

    if (parentList == null && style.getDefinedPropertyNamesArray().length == 0)
    {
      return;
    }

    final AttributeList attList = new AttributeList();
    if (parentList != null)
    {
      attList.setAttribute(BundleNamespaces.STYLE, "parent", parentList);
    }

    writer.writeTag(BundleNamespaces.STYLE, "element-style", attList, XmlWriter.OPEN);

    StyleWriterUtility.writeBandStyles(writer, style);
    StyleWriterUtility.writePageBandStyles(writer, style);
    StyleWriterUtility.writeCommonStyles(writer, style);
    StyleWriterUtility.writeContentStyles(writer, style);
    StyleWriterUtility.writeTextStyles(writer, style);
    StyleWriterUtility.writeSpatialStyles(writer, style);
    StyleWriterUtility.writeBorderStyles(writer, style);
    writeSparklineStyles(writer, style);
    writer.writeCloseTag();
  }

  protected void writeSparklineStyles(final XmlWriter writer, final ElementStyleSheet style)
      throws IOException
  {
    final AttributeList bandStyleAtts = new AttributeList();
    if (style.isLocalKey(SparklineStyleKeys.HIGH_COLOR))
    {
      final Color value = (Color) style.getStyleProperty(SparklineStyleKeys.HIGH_COLOR);
      bandStyleAtts.setAttribute(SparklineModule.NAMESPACE, "high-color", ColorValueConverter.colorToString(value));
    }
    if (style.isLocalKey(SparklineStyleKeys.MEDIUM_COLOR))
    {
      final Color value = (Color) style.getStyleProperty(SparklineStyleKeys.MEDIUM_COLOR);
      bandStyleAtts.setAttribute(SparklineModule.NAMESPACE, "medium-color", ColorValueConverter.colorToString(
          value));
    }
    if (style.isLocalKey(SparklineStyleKeys.LOW_COLOR))
    {
      final Color value = (Color) style.getStyleProperty(SparklineStyleKeys.LOW_COLOR);
      bandStyleAtts.setAttribute(SparklineModule.NAMESPACE, "low-color", ColorValueConverter.colorToString(value));
    }
    if (style.isLocalKey(SparklineStyleKeys.LAST_COLOR))
    {
      final Color value = (Color) style.getStyleProperty(SparklineStyleKeys.LAST_COLOR);
      bandStyleAtts.setAttribute(SparklineModule.NAMESPACE, "last-color", ColorValueConverter.colorToString(value));
    }

    if (bandStyleAtts.isEmpty() == false)
    {
      writer.writeTag(SparklineModule.NAMESPACE, "spark-styles", bandStyleAtts, XmlWriter.CLOSE);
    }
  }

}

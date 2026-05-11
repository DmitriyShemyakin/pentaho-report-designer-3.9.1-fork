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

package org.pentaho.reporting.engine.classic.core.function.sys;

import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.filter.DataSource;
import org.pentaho.reporting.engine.classic.core.filter.FormatSpecification;
import org.pentaho.reporting.engine.classic.core.filter.RawDataSource;
import org.pentaho.reporting.engine.classic.core.function.AbstractElementFormatFunction;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.function.StructureFunction;
import org.pentaho.reporting.engine.classic.core.states.LayoutProcess;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;

/**
 * The cell-format function is an internal structure function that copies the format-strings of any text-field into the
 * stylesheet of the element. This function does nothing if the current export type is not Table/Excel.
 *
 * @author Thomas Morgner
 */
public class CellFormatFunction extends AbstractElementFormatFunction implements StructureFunction
{
  /**
   * A reusable format-specification object.
   */
  private FormatSpecification formatSpecification;

  /**
   * Default Constructor.
   */
  public CellFormatFunction()
  {
  }

  public int getProcessingPriority()
  {
    return 30000;
  }

  /**
   * Overrides the dependency level to only execute this function on the pagination and content-generation level.
   *
   * @return LayoutProcess.LEVEL_PAGINATE.
   */
  public int getDependencyLevel()
  {
    return LayoutProcess.LEVEL_PAGINATE;
  }

  /**
   * Computes the format string. The format string is only computed if the element contains a raw-datasource and if the
   * element itself does not yet define an own excel-formatstring.
   *
   * @param b the band that should be processed.
   */
  protected void processRootBand(final Band b)
  {
    if (getRuntime().getExportDescriptor().startsWith("table/excel") == false)
    {
      return;
    }

    final int elementCount = b.getElementCount();
    for (int i = 0; i < elementCount; i++)
    {
      final ReportElement element = b.getElement(i);
      if (element instanceof Band)
      {
        processRootBand((Band) element);
      }
      else if (element instanceof Element)
      {
        final Element e = (Element) element;
        final DataSource source = e.getElementType();
        if (source instanceof RawDataSource)
        {
          final ElementStyleSheet style = element.getStyle();
          final String oldFormat = (String)
              style.getStyleProperty(ElementStyleKeys.EXCEL_DATA_FORMAT_STRING);
          if (oldFormat != null && oldFormat.length() > 0)
          {
            final Object attribute = element.getAttribute
                (AttributeNames.Internal.NAMESPACE, AttributeNames.Internal.EXCEL_CELL_FORMAT_AUTOCOMPUTE);
            if (Boolean.TRUE.equals(attribute) == false)
            {
              continue;
            }
          }
          final RawDataSource rds = (RawDataSource) source;
          formatSpecification = rds.getFormatString(getRuntime(), e, formatSpecification);
          if (formatSpecification.getType() == FormatSpecification.TYPE_DATE_FORMAT ||
              formatSpecification.getType() == FormatSpecification.TYPE_DECIMAL_FORMAT)
          {
            style.setStyleProperty
                (ElementStyleKeys.EXCEL_DATA_FORMAT_STRING, formatSpecification.getFormatString());
            element.setAttribute
                (AttributeNames.Internal.NAMESPACE, AttributeNames.Internal.EXCEL_CELL_FORMAT_AUTOCOMPUTE,
                    Boolean.TRUE);
          }
        }
      }
    }
  }

  public Expression getInstance()
  {
    final CellFormatFunction instance = (CellFormatFunction) super.getInstance();
    instance.formatSpecification = null;
    return instance;
  }
}

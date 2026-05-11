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
 * Copyright (c) 2007 - 2009 Pentaho Corporation, .  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.testcases.table;

import java.io.IOException;
import java.io.OutputStreamWriter;

import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.ReportAttributeMap;
import org.pentaho.reporting.engine.classic.core.layout.model.BorderCorner;
import org.pentaho.reporting.engine.classic.core.layout.model.BorderEdge;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.CellBackground;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.CellBackgroundProducer;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.SheetLayout;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.TableContentProducer;
import org.pentaho.reporting.engine.classic.core.util.beans.ColorValueConverter;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;

/**
 * Creation-Date: 22.08.2007, 12:08:55
 *
 * @author Thomas Morgner
 */
public class SheetLayoutPrinter
{
  public SheetLayoutPrinter()
  {
  }


  public static void print(final LogicalPageBox logicalPage,
                           final SheetLayout sheetLayout,
                           final TableContentProducer tableContentProducer)
  {

    ColorValueConverter colorValueConverter = new ColorValueConverter();
    CellBackgroundProducer cellBackgroundProducer = new CellBackgroundProducer(true, true);
    try
    {
      final XmlWriter writer = new XmlWriter(new OutputStreamWriter(System.out));
      writer.writeComment("Table Layout: ");
      writer.writeComment("Rows: " + sheetLayout.getRowCount());
      writer.writeComment("Columns: " + sheetLayout.getColumnCount());
      final int rows = sheetLayout.getRowCount();
      final int cols = sheetLayout.getColumnCount();
      writer.writeTag(null, "table", XmlWriter.OPEN);
      for (int row = 0; row < rows; row++)
      {
        writer.writeTag(null, "row", XmlWriter.OPEN);
        for (int col = 0; col < cols; col++)
        {
          final int sectionType = tableContentProducer.getSectionType(row, col);
          final CellBackground bg =
              cellBackgroundProducer.getBackgroundAt(logicalPage, sheetLayout, col, row, true, sectionType);
          if (bg == null)
          {
            writer.writeTag(null, "empty-cell", XmlWriter.CLOSE);
            continue;
          }

          if (bg.isOrigin() == false)
          {
            // A spanned cell ..
            writer.writeTag(null, "covered-cell", XmlWriter.CLOSE);
            continue;
          }

          final AttributeList attList = new AttributeList();
          final ReportAttributeMap attrs = bg.getAttributes();
          final Object nameAttr = attrs.getAttribute(AttributeNames.Core.NAMESPACE, AttributeNames.Core.NAME);
          if (nameAttr instanceof String)
          {
            attList.setAttribute(null, "content-idref",
                (String) nameAttr);
          }

          final String[] anchors = bg.getAnchors();
          if (anchors != null)
          {
            final StringBuilder anchor = new StringBuilder(100);
            for (int i = 0; i < anchors.length; i++)
            {
              if (i != 0)
              {
                anchor.append(", ");
              }
              anchor.append(anchors[i]);

            }
            attList.setAttribute(null, "anchor", anchor.toString());
          }
          if (bg.getBackgroundColor() != null)
          {
            attList.setAttribute(null, "background-color", ColorValueConverter.colorToString(bg.getBackgroundColor()));
          }

//          if (bg.getVerticalAlignment() != null)
//          {
//            attList.setAttribute(null, "vertical-alignment", String.valueOf(bg.getVerticalAlignment()));
//          }
//
          final BorderEdge top = bg.getTop();
          if (top != null)
          {
            attList.setAttribute(null, "border-top-color",
                ColorValueConverter.colorToString(top.getColor()));
            attList.setAttribute(null, "border-top-width",
                String.valueOf(StrictGeomUtility.toExternalValue(top.getWidth())));
            attList.setAttribute(null, "border-top-style",
                String.valueOf(top.getBorderStyle()));
          }

          final BorderEdge left = bg.getLeft();
          if (left != null)
          {
            attList.setAttribute(null, "border-left-color",
                ColorValueConverter.colorToString(left.getColor()));
            attList.setAttribute(null, "border-left-width",
                String.valueOf(StrictGeomUtility.toExternalValue(left.getWidth())));
            attList.setAttribute(null, "border-left-style",
                String.valueOf(left.getBorderStyle()));
          }

          final BorderEdge bottom = bg.getBottom();
          if (bottom != null)
          {
            attList.setAttribute(null, "border-bottom-color",
                ColorValueConverter.colorToString(bottom.getColor()));
            attList.setAttribute(null, "border-bottom-width",
                String.valueOf(StrictGeomUtility.toExternalValue(bottom.getWidth())));
            attList.setAttribute(null, "border-bottom-style",
                String.valueOf(bottom.getBorderStyle()));
          }

          final BorderEdge right = bg.getRight();
          if (right != null)
          {
            attList.setAttribute(null, "border-right-color",
                ColorValueConverter.colorToString(right.getColor()));
            attList.setAttribute(null, "border-right-width",
                String.valueOf(StrictGeomUtility.toExternalValue(right.getWidth())));
            attList.setAttribute(null, "border-right-style",
                String.valueOf(right.getBorderStyle()));
          }

          final BorderCorner topLeft = bg.getTopLeft();
          if (topLeft != null)
          {
            attList.setAttribute(null, "border-top-left-x",
                String.valueOf(StrictGeomUtility.toExternalValue(topLeft.getWidth())));
            attList.setAttribute(null, "border-top-left-y",
                String.valueOf(StrictGeomUtility.toExternalValue(topLeft.getHeight())));
          }


          final BorderCorner topRight = bg.getTopRight();
          if (topRight != null)
          {
            attList.setAttribute(null, "border-top-right-x",
                String.valueOf(StrictGeomUtility.toExternalValue(topRight.getWidth())));
            attList.setAttribute(null, "border-top-right-y",
                String.valueOf(StrictGeomUtility.toExternalValue(topRight.getHeight())));
          }


          final BorderCorner bottomLeft = bg.getBottomLeft();
          if (bottomLeft != null)
          {
            attList.setAttribute(null, "border-bottom-left-x",
                String.valueOf(StrictGeomUtility.toExternalValue(bottomLeft.getWidth())));
            attList.setAttribute(null, "border-bottom-left-y",
                String.valueOf(StrictGeomUtility.toExternalValue(bottomLeft.getHeight())));
          }


          final BorderCorner bottomRight = bg.getBottomRight();
          if (bottomRight != null)
          {
            attList.setAttribute(null, "border-bottom-right-x",
                String.valueOf(StrictGeomUtility.toExternalValue(bottomRight.getWidth())));
            attList.setAttribute(null, "border-bottom-right-y",
                String.valueOf(StrictGeomUtility.toExternalValue(bottomRight.getHeight())));
          }
          writer.writeTag(null, "cell", attList, XmlWriter.CLOSE);
        }
        writer.writeCloseTag();
      }
      writer.writeCloseTag();
      writer.flush();
    }
    catch (IOException ioe)
    {
      // Cannot happen ..
      throw new IllegalStateException("Failure while writing the debug-output");
    }
  }
}

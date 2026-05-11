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

package org.pentaho.reporting.engine.classic.testcases.output.xml;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import org.pentaho.reporting.engine.classic.core.InvalidReportStateException;
import org.pentaho.reporting.engine.classic.core.layout.model.BlockRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.Border;
import org.pentaho.reporting.engine.classic.core.layout.model.BorderCorner;
import org.pentaho.reporting.engine.classic.core.layout.model.BorderEdge;
import org.pentaho.reporting.engine.classic.core.layout.model.CanvasRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.InlineRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.PageAreaBox;
import org.pentaho.reporting.engine.classic.core.layout.model.PageGrid;
import org.pentaho.reporting.engine.classic.core.layout.model.ParagraphPoolBox;
import org.pentaho.reporting.engine.classic.core.layout.model.ParagraphRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.PhysicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderLength;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableReplacedContent;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableReplacedContentBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableText;
import org.pentaho.reporting.engine.classic.core.layout.model.SpacerRenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.context.BoxDefinition;
import org.pentaho.reporting.engine.classic.core.layout.model.context.StaticBoxLayoutProperties;
import org.pentaho.reporting.engine.classic.core.layout.output.LogicalPageKey;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.layout.output.PhysicalPageKey;
import org.pentaho.reporting.engine.classic.core.layout.process.IterateStructuralProcessStep;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.CellBackground;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.CellBackgroundProducer;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.SheetLayout;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.TableContentProducer;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.TableRectangle;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;
import org.pentaho.reporting.engine.classic.core.util.beans.ColorValueConverter;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictBounds;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;
import org.pentaho.reporting.libraries.xmlns.writer.DefaultTagDescription;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;

/**
 * Creation-Date: 20.10.2007, 16:40:57
 *
 * @author Thomas Morgner
 * @deprecated 
 */
public class XmlDocumentWriter extends IterateStructuralProcessStep
{
  private OutputStream outputStream;
  private StrictBounds drawArea;
  private XmlWriter xmlWriter;
  private static final String LAYOUT_OUTPUT_NAMESPACE = "http://jfreereport.sourceforge.net/namespaces/testing/layout-output";
  private DecimalFormat pointIntConverter;
  private DecimalFormat pointConverter;
  private boolean ignoreEmptyBorders = true;
  private CellBackgroundProducer cellBackgroundProducer;

  public XmlDocumentWriter(final OutputProcessorMetaData metaData, final OutputStream outputStream)
  {
    this.outputStream = outputStream;
    this.pointConverter = new DecimalFormat("0.####", new DecimalFormatSymbols(Locale.US));
    this.pointIntConverter = new DecimalFormat("0", new DecimalFormatSymbols(Locale.US));
    this.cellBackgroundProducer = new CellBackgroundProducer(true, true);
  }

  public void open() throws IOException
  {
    final DefaultTagDescription td = new DefaultTagDescription();
    td.addDefaultDefinition(LAYOUT_OUTPUT_NAMESPACE, false);
    td.addTagDefinition(LAYOUT_OUTPUT_NAMESPACE, "text", true);

    // prepare anything that might needed to be prepared ..
    final Writer writer = new BufferedWriter(new OutputStreamWriter(outputStream));
    this.xmlWriter = new XmlWriter(writer, td);
    this.xmlWriter.writeXmlDeclaration(null);
    final AttributeList attrs = new AttributeList();
    attrs.addNamespaceDeclaration("", LAYOUT_OUTPUT_NAMESPACE);
    xmlWriter.writeTag(LAYOUT_OUTPUT_NAMESPACE, "layout-output", attrs, XmlWriter.OPEN);
  }

  public void close() throws IOException
  {
    // close all ..
    xmlWriter.writeCloseTag();
    xmlWriter.close();
  }

  public void processPhysicalPage(final PageGrid pageGrid,
                                  final LogicalPageBox logicalPage,
                                  final int row,
                                  final int col,
                                  final PhysicalPageKey pageKey)
      throws IOException
  {
    final PhysicalPageBox page = pageGrid.getPage(row, col);
    if (page == null)
    {
      return;
    }

    final float width = (float) StrictGeomUtility.toExternalValue(page.getWidth());
    final float height = (float) StrictGeomUtility.toExternalValue(page.getHeight());
    final float marginLeft = (float) StrictGeomUtility.toExternalValue(page.getImageableX());
    final float marginRight = (float) StrictGeomUtility.toExternalValue
        (page.getWidth() - page.getImageableWidth() - page.getImageableX());
    final float marginTop = (float) StrictGeomUtility.toExternalValue(page.getImageableY());
    final float marginBottom = (float) StrictGeomUtility.toExternalValue
        (page.getHeight() - page.getImageableHeight() - page.getImageableY());

    final AttributeList pageAttributes = new AttributeList();
    pageAttributes.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "page-x", pointConverter.format(page.getGlobalX()));
    pageAttributes.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "page-y", pointConverter.format(page.getGlobalY()));
    pageAttributes.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "page-width", pointConverter.format(width));
    pageAttributes.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "page-height", pointConverter.format(height));
    pageAttributes.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "margin-top", pointConverter.format(marginTop));
    pageAttributes.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "margin-left", pointConverter.format(marginLeft));
    pageAttributes.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "margin-bottom", pointConverter.format(marginBottom));
    pageAttributes.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "margin-right", pointConverter.format(marginRight));

    xmlWriter.writeTag(LAYOUT_OUTPUT_NAMESPACE, "physical-page", pageAttributes, XmlWriter.OPEN);

    // and now process the box ..
    drawArea = new StrictBounds(page.getGlobalX(), page.getGlobalY(),
        page.getImageableWidth(), page.getImageableHeight());
    processPage(logicalPage);

    xmlWriter.writeCloseTag();
  }

  public void processLogicalPage(final LogicalPageKey key,
                                 final LogicalPageBox logicalPage)
      throws IOException
  {

    final float width = (float) StrictGeomUtility.toExternalValue(logicalPage.getPageWidth());
    final float height = (float) StrictGeomUtility.toExternalValue(logicalPage.getPageHeight());

    final AttributeList pageAttributes = new AttributeList();
    pageAttributes.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "logical-page-number", String.valueOf(key.getPosition()));
    pageAttributes.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "width", pointConverter.format(width));
    pageAttributes.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "height", pointConverter.format(height));

    xmlWriter.writeTag(LAYOUT_OUTPUT_NAMESPACE, "logical-page", pageAttributes, XmlWriter.OPEN);

    // and now process the box ..
    drawArea = new StrictBounds(0, 0, logicalPage.getPageWidth(), logicalPage.getPageHeight());
    processPage(logicalPage);

    xmlWriter.writeCloseTag();
  }

  public void processTableContent(final LogicalPageBox logicalPage,
                                  final TableContentProducer contentProducer) throws IOException
  {

    // Start a new page.
    final SheetLayout sheetLayout = contentProducer.getSheetLayout();
    final int columnCount = contentProducer.getColumnCount();
    final int rowCount = contentProducer.getRowCount();
    final int startRow = contentProducer.getFinishedRows();
    final int finishRow = contentProducer.getFilledRows();

    final AttributeList pageAttributes = new AttributeList();
    pageAttributes.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "col-count", pointConverter.format(columnCount));
    pageAttributes.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "row-count", pointConverter.format(rowCount));
    xmlWriter.writeTag(LAYOUT_OUTPUT_NAMESPACE, "table", pageAttributes, XmlWriter.OPEN);
    xmlWriter.writeTag(LAYOUT_OUTPUT_NAMESPACE, "cols", XmlWriter.OPEN);
    for (int i = 0; i < columnCount; i++)
    {
      final double cellWidth = StrictGeomUtility.toExternalValue(sheetLayout.getCellWidth(i, i + 1));
      xmlWriter.writeTag(LAYOUT_OUTPUT_NAMESPACE, "column", "width", pointConverter.format(cellWidth), XmlWriter.CLOSE);
    }
    xmlWriter.writeCloseTag();

    for (int row = startRow; row < finishRow; row++)
    {
      xmlWriter.writeTag(LAYOUT_OUTPUT_NAMESPACE, "row", XmlWriter.OPEN);

      for (short col = 0; col < columnCount; col++)
      {
        final RenderBox content = contentProducer.getContent(row, col);
        final int sectionType = contentProducer.getSectionType(row, col);

        if (content == null)
        {
          final CellBackground background = cellBackgroundProducer.getBackgroundAt
              (logicalPage, sheetLayout, col, row, true, sectionType);
          if (background == null)
          {
            xmlWriter.writeTag(LAYOUT_OUTPUT_NAMESPACE, "empty-cell", XmlWriter.CLOSE);
            continue;
          }

          // A empty cell with a defined background ..
          xmlWriter.writeTag(LAYOUT_OUTPUT_NAMESPACE, "empty-cell", createCellAttributes(background), XmlWriter.CLOSE);
          continue;
        }

        if (content.isCommited() == false)
        {
          throw new InvalidReportStateException("Uncommited content encountered");
        }

        final TableRectangle rectangle = sheetLayout.getTableBounds
            (content.getX(), content.getY(), content.getWidth(), content.getHeight(), null);
        if (rectangle.isOrigin(col, row) == false)
        {
          // A spanned cell ..
          xmlWriter.writeTag(LAYOUT_OUTPUT_NAMESPACE, "spanned-cell", XmlWriter.CLOSE);
          continue;
        }

        final CellBackground realBackground = cellBackgroundProducer.getBackgroundForBox
            (logicalPage, sheetLayout, rectangle.getX1(), rectangle.getY1(), rectangle.getColumnSpan(),
                rectangle.getRowSpan(), false, sectionType, content);

        final AttributeList attributeList;
        if (realBackground != null)
        {
          attributeList = createCellAttributes(realBackground);
        }
        else
        {
          attributeList = new AttributeList();
        }
        attributeList.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "row-span", String.valueOf(rectangle.getRowSpan()));
        attributeList.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "col-span", String.valueOf(rectangle.getColumnSpan()));
        attributeList.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "href", (String) content.getStyleSheet().getStyleProperty(
            ElementStyleKeys.HREF_TARGET));
        attributeList.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "href-window",
            (String) content.getStyleSheet().getStyleProperty(ElementStyleKeys.HREF_WINDOW));
        attributeList.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "href-title",
            (String) content.getStyleSheet().getStyleProperty(ElementStyleKeys.HREF_TITLE));

        // export the cell and all content ..
        xmlWriter.writeTag(LAYOUT_OUTPUT_NAMESPACE, "cell", attributeList, XmlWriter.OPEN);
        processBoxChilds(content);
        xmlWriter.writeCloseTag();
        content.setFinishedTable(true);
      }
      xmlWriter.writeCloseTag();
    }
    xmlWriter.writeCloseTag();
  }

  protected void processPage(final LogicalPageBox rootBox)
  {
    final StrictBounds pageBounds = drawArea;
    startProcessing(rootBox.getWatermarkArea());

    final BlockRenderBox headerArea = rootBox.getHeaderArea();
    final BlockRenderBox footerArea = rootBox.getFooterArea();
    final BlockRenderBox repeatFooterArea = rootBox.getRepeatFooterArea();
    final StrictBounds headerBounds =
        new StrictBounds(headerArea.getX(), headerArea.getY(), headerArea.getWidth(), headerArea.getHeight());
    final StrictBounds footerBounds =
        new StrictBounds(footerArea.getX(), footerArea.getY(), footerArea.getWidth(), footerArea.getHeight());
    final StrictBounds repeatFooterBounds = new StrictBounds
        (repeatFooterArea.getX(), repeatFooterArea.getY(), repeatFooterArea.getWidth(), repeatFooterArea.getHeight());
    final StrictBounds contentBounds = new StrictBounds
        (rootBox.getX(), headerArea.getY() + headerArea.getHeight(),
            rootBox.getWidth(), footerArea.getY() - headerArea.getHeight());
    this.drawArea = headerBounds;
    startProcessing(headerArea);
    this.drawArea = contentBounds;
    processBoxChilds(rootBox);
    this.drawArea = repeatFooterBounds;
    startProcessing(repeatFooterArea);
    this.drawArea = footerBounds;
    startProcessing(footerArea);
    this.drawArea = pageBounds;
  }

  protected final boolean isNodeVisible(final RenderNode rect2)
  {
    final long drawAreaX0 = drawArea.getX();
    final long drawAreaY0 = drawArea.getY();
    final long drawAreaX1 = drawAreaX0 + drawArea.getWidth();
    final long drawAreaY1 = drawAreaY0 + drawArea.getHeight();

    final long x = rect2.getX();
    final long y = rect2.getY();
    final long width = rect2.getWidth();
    final long height = rect2.getHeight();
    final long x2 = x + width;
    final long y2 = y + height;

    if (width == 0)
    {
      if (x2 < drawAreaX0)
      {
        return false;
      }
      if (x > drawAreaX1)
      {
        return false;
      }
    }
    else
    {
      if (x2 <= drawAreaX0)
      {
        return false;
      }
      if (x >= drawAreaX1)
      {
        return false;
      }
    }
    if (height == 0)
    {
      if (y2 < drawAreaY0)
      {
        return false;
      }
      if (y > drawAreaY1)
      {
        return false;
      }
    }
    else
    {
      if (y2 <= drawAreaY0)
      {
        return false;
      }
      if (y >= drawAreaY1)
      {
        return false;
      }
    }
    return true;
  }

  private AttributeList createBoxAttributeList(final RenderBox box)
  {
    final AttributeList attributeList = new AttributeList();
    final BoxDefinition definition = box.getBoxDefinition();
    final Border border = definition.getBorder();
    final StaticBoxLayoutProperties sblp = box.getStaticBoxLayoutProperties();

    final BorderEdge top = border.getTop();
    if (BorderEdge.EMPTY.equals(top) == false || ignoreEmptyBorders == false)
    {
      attributeList.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "border-top-color",
          ColorValueConverter.colorToString(top.getColor()));
      attributeList.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "border-top-width",
          String.valueOf(StrictGeomUtility.toExternalValue(sblp.getBorderTop())));
      attributeList.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "border-top-style",
          String.valueOf(top.getBorderStyle()));
    }

    final BorderEdge left = border.getLeft();
    if (BorderEdge.EMPTY.equals(left) == false || ignoreEmptyBorders == false)
    {
      attributeList.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "border-left-color",
          ColorValueConverter.colorToString(left.getColor()));
      attributeList.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "border-left-width",
          String.valueOf(StrictGeomUtility.toExternalValue(sblp.getBorderLeft())));
      attributeList.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "border-left-style",
          String.valueOf(left.getBorderStyle()));
    }

    final BorderEdge bottom = border.getBottom();
    if (BorderEdge.EMPTY.equals(bottom) == false || ignoreEmptyBorders == false)
    {
      attributeList.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "border-bottom-color",
          ColorValueConverter.colorToString(bottom.getColor()));
      attributeList.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "border-bottom-width",
          String.valueOf(StrictGeomUtility.toExternalValue(sblp.getBorderBottom())));
      attributeList.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "border-bottom-style",
          String.valueOf(bottom.getBorderStyle()));
    }

    final BorderEdge right = border.getRight();
    if (BorderEdge.EMPTY.equals(right) == false || ignoreEmptyBorders == false)
    {
      attributeList.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "border-right-color",
          ColorValueConverter.colorToString(right.getColor()));
      attributeList.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "border-right-width",
          String.valueOf(StrictGeomUtility.toExternalValue(sblp.getBorderRight())));
      attributeList.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "border-right-style",
          String.valueOf(right.getBorderStyle()));
    }

    final BorderCorner topLeft = border.getTopLeft();
    if (isEmptyCorner(topLeft) == false)
    {
      attributeList.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "border-top-left-x",
          String.valueOf(StrictGeomUtility.toExternalValue(topLeft.getWidth())));
      attributeList.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "border-top-left-y",
          String.valueOf(StrictGeomUtility.toExternalValue(topLeft.getHeight())));
    }

    final BorderCorner topRight = border.getTopRight();
    if (isEmptyCorner(topRight) == false)
    {
      attributeList.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "border-top-right-x",
          String.valueOf(StrictGeomUtility.toExternalValue(topRight.getWidth())));
      attributeList.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "border-top-right-y",
          String.valueOf(StrictGeomUtility.toExternalValue(topRight.getHeight())));
    }

    final BorderCorner bottomLeft = border.getBottomLeft();
    if (isEmptyCorner(bottomLeft) == false)
    {
      attributeList.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "border-bottom-left-x",
          String.valueOf(StrictGeomUtility.toExternalValue(bottomLeft.getWidth())));
      attributeList.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "border-bottom-left-y",
          String.valueOf(StrictGeomUtility.toExternalValue(bottomLeft.getHeight())));
    }

    final BorderCorner bottomRight = border.getBottomRight();
    if (isEmptyCorner(bottomRight) == false)
    {
      attributeList.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "border-bottom-right-x",
          String.valueOf(StrictGeomUtility.toExternalValue(bottomRight.getWidth())));
      attributeList.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "border-bottom-right-y",
          String.valueOf(StrictGeomUtility.toExternalValue(bottomRight.getHeight())));
    }

    if (sblp.getMarginTop() > 0)
    {
      attributeList.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "margin-top",
          String.valueOf(StrictGeomUtility.toExternalValue(sblp.getMarginTop())));
    }
    if (sblp.getMarginLeft() > 0)
    {
      attributeList.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "margin-left",
          String.valueOf(StrictGeomUtility.toExternalValue(sblp.getMarginLeft())));
    }
    if (sblp.getMarginBottom() > 0)
    {
      attributeList.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "margin-bottom",
          String.valueOf(StrictGeomUtility.toExternalValue(sblp.getMarginBottom())));
    }
    if (sblp.getMarginRight() > 0)
    {
      attributeList.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "margin-right",
          String.valueOf(StrictGeomUtility.toExternalValue(sblp.getMarginRight())));
    }

    if (definition.getPaddingTop() > 0)
    {
      attributeList.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "padding-top",
          String.valueOf(StrictGeomUtility.toExternalValue(definition.getPaddingTop())));
    }
    if (definition.getPaddingLeft() > 0)
    {
      attributeList.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "padding-left",
          String.valueOf(StrictGeomUtility.toExternalValue(definition.getPaddingLeft())));
    }
    if (definition.getPaddingBottom() > 0)
    {
      attributeList.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "padding-bottom",
          String.valueOf(StrictGeomUtility.toExternalValue(definition.getPaddingBottom())));
    }
    if (definition.getPaddingRight() > 0)
    {
      attributeList.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "padding-right",
          String.valueOf(StrictGeomUtility.toExternalValue(definition.getPaddingRight())));
    }

    attributeList.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "x",
        String.valueOf(StrictGeomUtility.toExternalValue(box.getX())));
    attributeList.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "y",
        String.valueOf(StrictGeomUtility.toExternalValue(box.getY())));
    attributeList.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "width",
        String.valueOf(StrictGeomUtility.toExternalValue(box.getWidth())));
    attributeList.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "height",
        String.valueOf(StrictGeomUtility.toExternalValue(box.getHeight())));

    final Color backgroundColor = (Color) box.getStyleSheet().getStyleProperty(ElementStyleKeys.BACKGROUND_COLOR);
    if (backgroundColor != null)
    {
      attributeList.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "background-color", ColorValueConverter.colorToString(
          backgroundColor));
    }

    final Color color = (Color) box.getStyleSheet().getStyleProperty(ElementStyleKeys.PAINT);
    if (color != null)
    {
      attributeList.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "color", ColorValueConverter.colorToString(color));
    }
    attributeList.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "font-face", (String) box.getStyleSheet().getStyleProperty(
        TextStyleKeys.FONT));
    final Object o = box.getStyleSheet().getStyleProperty(TextStyleKeys.FONTSIZE);
    attributeList.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "font-size", pointIntConverter.format(o));
    attributeList.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "font-style-bold", String.valueOf(
        box.getStyleSheet().getStyleProperty(TextStyleKeys.BOLD)));
    attributeList.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "font-style-italics", String.valueOf(
        box.getStyleSheet().getStyleProperty(TextStyleKeys.ITALIC)));
    attributeList.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "font-style-underline", String.valueOf(
        box.getStyleSheet().getStyleProperty(TextStyleKeys.UNDERLINED)));
    attributeList.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "font-style-strikethrough", String.valueOf(
        box.getStyleSheet().getStyleProperty(TextStyleKeys.STRIKETHROUGH)));

    attributeList.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "anchor", (String) box.getStyleSheet().getStyleProperty(
        ElementStyleKeys.ANCHOR_NAME));
    attributeList.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "href", (String) box.getStyleSheet().getStyleProperty(
        ElementStyleKeys.HREF_TARGET));
    attributeList.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "href-window", (String) box.getStyleSheet().getStyleProperty(
        ElementStyleKeys.HREF_WINDOW));
    attributeList.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "href-title", (String) box.getStyleSheet().getStyleProperty(
        ElementStyleKeys.HREF_TITLE));

    return attributeList;
  }

  private boolean isEmptyCorner(final BorderCorner corner)
  {
    if (ignoreEmptyBorders == false)
    {
      return false;
    }
    return corner.getWidth() == 0 && corner.getHeight() == 0;
  }

  private AttributeList createCellAttributes(final CellBackground border)
  {
    final AttributeList attributeList = new AttributeList();

    final BorderEdge top = border.getTop();
    if (BorderEdge.EMPTY.equals(top) == false || ignoreEmptyBorders == false)
    {
      attributeList.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "border-top-color",
          ColorValueConverter.colorToString(top.getColor()));
      attributeList.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "border-top-width",
          String.valueOf(StrictGeomUtility.toExternalValue(top.getWidth())));
      attributeList.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "border-top-style",
          String.valueOf(top.getBorderStyle()));
    }

    final BorderEdge left = border.getLeft();
    if (BorderEdge.EMPTY.equals(left) == false || ignoreEmptyBorders == false)
    {
      attributeList.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "border-left-color",
          ColorValueConverter.colorToString(left.getColor()));
      attributeList.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "border-left-width",
          String.valueOf(StrictGeomUtility.toExternalValue(left.getWidth())));
      attributeList.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "border-left-style",
          String.valueOf(left.getBorderStyle()));
    }

    final BorderEdge bottom = border.getBottom();
    if (BorderEdge.EMPTY.equals(bottom) == false || ignoreEmptyBorders == false)
    {
      attributeList.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "border-bottom-color",
          ColorValueConverter.colorToString(bottom.getColor()));
      attributeList.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "border-bottom-width",
          String.valueOf(StrictGeomUtility.toExternalValue(bottom.getWidth())));
      attributeList.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "border-bottom-style",
          String.valueOf(bottom.getBorderStyle()));
    }

    final BorderEdge right = border.getRight();
    if (BorderEdge.EMPTY.equals(right) == false || ignoreEmptyBorders == false)
    {
      attributeList.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "border-right-color",
          ColorValueConverter.colorToString(right.getColor()));
      attributeList.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "border-right-width",
          String.valueOf(StrictGeomUtility.toExternalValue(right.getWidth())));
      attributeList.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "border-right-style",
          String.valueOf(right.getBorderStyle()));
    }

    final BorderCorner topLeft = border.getTopLeft();
    if (isEmptyCorner(topLeft) == false)
    {
      attributeList.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "border-top-left-x",
          String.valueOf(StrictGeomUtility.toExternalValue(topLeft.getWidth())));
      attributeList.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "border-top-left-y",
          String.valueOf(StrictGeomUtility.toExternalValue(topLeft.getHeight())));
    }

    final BorderCorner topRight = border.getTopRight();
    if (isEmptyCorner(topRight) == false)
    {
      attributeList.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "border-top-right-x",
          String.valueOf(StrictGeomUtility.toExternalValue(topRight.getWidth())));
      attributeList.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "border-top-right-y",
          String.valueOf(StrictGeomUtility.toExternalValue(topRight.getHeight())));
    }

    final BorderCorner bottomLeft = border.getBottomLeft();
    if (isEmptyCorner(bottomLeft) == false)
    {
      attributeList.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "border-bottom-left-x",
          String.valueOf(StrictGeomUtility.toExternalValue(bottomLeft.getWidth())));
      attributeList.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "border-bottom-left-y",
          String.valueOf(StrictGeomUtility.toExternalValue(bottomLeft.getHeight())));
    }

    final BorderCorner bottomRight = border.getBottomRight();
    if (isEmptyCorner(bottomRight) == false)
    {
      attributeList.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "border-bottom-right-x",
          String.valueOf(StrictGeomUtility.toExternalValue(bottomRight.getWidth())));
      attributeList.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "border-bottom-right-y",
          String.valueOf(StrictGeomUtility.toExternalValue(bottomRight.getHeight())));
    }

    final Color backgroundColor = border.getBackgroundColor();
    if (backgroundColor != null)
    {
      attributeList.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "background-color", ColorValueConverter.colorToString(
          backgroundColor));
    }

    final String[] anchors = border.getAnchors();
    if (anchors.length > 0)
    {
      final StringBuilder anchorText = new StringBuilder(100);
      for (int i = 0; i < anchors.length; i++)
      {
        final String anchor = anchors[i];
        if (i == 0)
        {
          anchorText.append(" ");
        }
        anchorText.append(anchor);
      }
      attributeList.setAttribute(XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "anchor", anchorText.toString());
    }
    return attributeList;
  }

  protected boolean startBlockBox(final BlockRenderBox box)
  {

    try
    {
      if (box instanceof PageAreaBox)
      {
        final AttributeList list = createBoxAttributeList(box);
        list.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "type", box.getName());
        xmlWriter.writeTag(LAYOUT_OUTPUT_NAMESPACE, "page-area", list, XmlWriter.OPEN);
      }
      else if (box instanceof ParagraphRenderBox)
      {
        xmlWriter.writeTag(LAYOUT_OUTPUT_NAMESPACE, "p", createBoxAttributeList(box), XmlWriter.OPEN);
      }
      else
      {
        xmlWriter.writeTag(LAYOUT_OUTPUT_NAMESPACE, "block", createBoxAttributeList(box), XmlWriter.OPEN);
      }
      return true;
    }
    catch (IOException e)
    {
      throw new InvalidReportStateException(e.getMessage(), e);
    }
  }

  protected void finishBlockBox(final BlockRenderBox box)
  {
    try
    {
      xmlWriter.writeCloseTag();
    }
    catch (IOException e)
    {
      throw new InvalidReportStateException(e.getMessage(), e);
    }
  }

  protected boolean startInlineBox(final InlineRenderBox box)
  {
    try
    {
      if (box instanceof ParagraphPoolBox)
      {
        xmlWriter.writeTag(LAYOUT_OUTPUT_NAMESPACE, "line", createBoxAttributeList(box), XmlWriter.OPEN);
      }
      else
      {
        xmlWriter.writeTag(LAYOUT_OUTPUT_NAMESPACE, "inline", createBoxAttributeList(box), XmlWriter.OPEN);
      }
      return true;
    }
    catch (IOException e)
    {
      throw new InvalidReportStateException(e.getMessage(), e);
    }
  }

  protected void finishInlineBox(final InlineRenderBox box)
  {
    try
    {
      xmlWriter.writeCloseTag();
    }
    catch (IOException e)
    {
      throw new InvalidReportStateException(e.getMessage(), e);
    }
  }

  protected boolean startOtherBox(final RenderBox box)
  {
    try
    {
      xmlWriter.writeTag(LAYOUT_OUTPUT_NAMESPACE, "other-box", createBoxAttributeList(box), XmlWriter.OPEN);
      return true;
    }
    catch (IOException e)
    {
      throw new InvalidReportStateException(e.getMessage(), e);
    }
  }

  protected void finishOtherBox(final RenderBox box)
  {
    try
    {
      xmlWriter.writeCloseTag();
    }
    catch (IOException e)
    {
      throw new InvalidReportStateException(e.getMessage(), e);
    }
  }

  protected boolean startCanvasBox(final CanvasRenderBox box)
  {
    try
    {
      xmlWriter.writeTag(LAYOUT_OUTPUT_NAMESPACE, "canvas", createBoxAttributeList(box), XmlWriter.OPEN);
      return true;
    }
    catch (IOException e)
    {
      throw new InvalidReportStateException(e.getMessage(), e);
    }
  }

  protected void finishCanvasBox(final CanvasRenderBox box)
  {
    try
    {
      xmlWriter.writeCloseTag();
    }
    catch (IOException e)
    {
      throw new InvalidReportStateException(e.getMessage(), e);
    }
  }

  protected boolean startRowBox(final RenderBox box)
  {
    try
    {
      xmlWriter.writeTag(LAYOUT_OUTPUT_NAMESPACE, "row", createBoxAttributeList(box), XmlWriter.OPEN);
      return true;
    }
    catch (IOException e)
    {
      throw new InvalidReportStateException(e.getMessage(), e);
    }
  }

  protected void finishRowBox(final RenderBox box)
  {
    try
    {
      xmlWriter.writeCloseTag();
    }
    catch (IOException e)
    {
      throw new InvalidReportStateException(e.getMessage(), e);
    }
  }

  protected void processOtherNode(final RenderNode node)
  {
    try
    {
      if (node instanceof RenderableText)
      {
        final RenderableText text = (RenderableText) node;
        final AttributeList attributeList = new AttributeList();
        attributeList.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "x",
            String.valueOf(StrictGeomUtility.toExternalValue(node.getX())));
        attributeList.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "y",
            String.valueOf(StrictGeomUtility.toExternalValue(node.getY())));
        attributeList.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "width",
            String.valueOf(StrictGeomUtility.toExternalValue(node.getWidth())));
        attributeList.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "height",
            String.valueOf(StrictGeomUtility.toExternalValue(node.getHeight())));
        xmlWriter.writeTag(LAYOUT_OUTPUT_NAMESPACE, "text", attributeList, XmlWriter.OPEN);
        xmlWriter.writeTextNormalized(text.getRawText(), true);
        xmlWriter.writeCloseTag();

      }
      else if (node instanceof SpacerRenderNode)
      {
        final SpacerRenderNode spacer = (SpacerRenderNode) node;
        final AttributeList attributeList = new AttributeList();
        attributeList.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "width",
            String.valueOf(StrictGeomUtility.toExternalValue(node.getWidth())));
        attributeList.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "height",
            String.valueOf(StrictGeomUtility.toExternalValue(node.getHeight())));
        attributeList.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "preserve",
            String.valueOf(spacer.isDiscardable() == false));
        xmlWriter.writeTag(LAYOUT_OUTPUT_NAMESPACE, "spacer", attributeList, XmlWriter.CLOSE);
      }
    }
    catch (IOException e)
    {
      throw new InvalidReportStateException(e.getMessage(), e);
    }
  }

  protected void processRenderableContent(final RenderableReplacedContentBox box)
  {
    try
    {
      final RenderableReplacedContent prc = box.getContent();
      final AttributeList attributeList = new AttributeList();
      attributeList.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "x",
          String.valueOf(StrictGeomUtility.toExternalValue(box.getX())));
      attributeList.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "y",
          String.valueOf(StrictGeomUtility.toExternalValue(box.getY())));
      attributeList.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "width",
          String.valueOf(StrictGeomUtility.toExternalValue(box.getWidth())));
      attributeList.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "height",
          String.valueOf(StrictGeomUtility.toExternalValue(box.getHeight())));
      attributeList.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "source", String.valueOf(prc.getSource()));
      attributeList.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "content-width",
          String.valueOf(StrictGeomUtility.toExternalValue(prc.getContentWidth())));
      attributeList.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "content-height",
          String.valueOf(StrictGeomUtility.toExternalValue(prc.getContentHeight())));
      attributeList.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "requested-width",
          convertRenderLength(prc.getRequestedWidth()));
      attributeList.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "requested-height",
          convertRenderLength(prc.getRequestedHeight()));

      final Object o = prc.getRawObject();
      if (o != null)
      {
        attributeList.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "raw-object-type", o.getClass().getName());
      }
      else
      {
        attributeList.setAttribute(LAYOUT_OUTPUT_NAMESPACE, "raw-object-type", "null");
      }
      xmlWriter.writeTag(LAYOUT_OUTPUT_NAMESPACE, "replaced-content", attributeList, XmlWriter.CLOSE);
    }
    catch (IOException e)
    {
      throw new InvalidReportStateException(e.getMessage(), e);
    }
  }

  private String convertRenderLength(final RenderLength length)
  {
    if (length == null)
    {
      return null;
    }
    if (RenderLength.AUTO.equals(length))
    {
      return "auto";
    }
    if (length.isPercentage())
    {
      return pointConverter.format(StrictGeomUtility.toExternalValue(-length.getValue())) + "%";
    }
    return pointConverter.format(StrictGeomUtility.toExternalValue(length.getValue()));
  }

  protected void processParagraphChilds(final ParagraphRenderBox box)
  {
    processBoxChilds(box);
  }

}

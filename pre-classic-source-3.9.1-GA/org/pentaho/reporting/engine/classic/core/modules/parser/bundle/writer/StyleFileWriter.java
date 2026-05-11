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

package org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer;

import java.awt.Insets;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.CustomPageDefinition;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.PageDefinition;
import org.pentaho.reporting.engine.classic.core.PageFooter;
import org.pentaho.reporting.engine.classic.core.PageHeader;
import org.pentaho.reporting.engine.classic.core.ReportDefinition;
import org.pentaho.reporting.engine.classic.core.SimplePageDefinition;
import org.pentaho.reporting.engine.classic.core.Watermark;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.BundleNamespaces;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.BundleXmlModule;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.ExtParserModule;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.StyleSheetCollection;
import org.pentaho.reporting.engine.classic.core.util.PageFormatFactory;
import org.pentaho.reporting.libraries.docbundle.WriteableDocumentBundle;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;
import org.pentaho.reporting.libraries.xmlns.writer.DefaultTagDescription;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriterSupport;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class StyleFileWriter implements BundleWriterHandler
{

  public StyleFileWriter()
  {
  }

  /**
   * Returns a relatively high processing order indicating this BundleWriterHandler should be one of the last processed
   * @return the relative processing order for this BundleWriterHandler
   */
  public int getProcessingOrder()
  {
    return 100000;
  }

  /**
   * Writes a certain aspect into a own file. The name of file inside the bundle is returned as string. The file name
   * returned is always absolute and can be made relative by using the IOUtils of LibBase. If the writer-handler did not
   * generate a file on its own, it should return null.
   *
   * @param bundle the bundle where to write to.
   * @param state  the writer state to hold the current processing information.
   * @return the name of the newly generated file or null if no file was created.
   * @throws IOException           if any error occured
   * @throws BundleWriterException if a bundle-management error occured.
   */
  public String writeReport(final WriteableDocumentBundle bundle,
                            final BundleWriterState state)
      throws IOException, BundleWriterException
  {
    if (bundle == null)
    {
      throw new NullPointerException();
    }
    if (state == null)
    {
      throw new NullPointerException();
    }


    final BundleWriterState styleFileState = new BundleWriterState(state, "styles.xml");

    final OutputStream outputStream = new BufferedOutputStream(bundle.createEntry(styleFileState.getFileName(),
        "text/xml"));
    final DefaultTagDescription tagDescription = new DefaultTagDescription(
        ClassicEngineBoot.getInstance().getGlobalConfig(), BundleXmlModule.TAG_DEF_PREFIX);
    final XmlWriter writer = new XmlWriter(new OutputStreamWriter(outputStream, "UTF-8"), tagDescription, "  ", "\n");
    writer.writeXmlDeclaration("UTF-8");

    final AttributeList rootAttributes = new AttributeList();
    rootAttributes.addNamespaceDeclaration("", BundleNamespaces.STYLE);
    rootAttributes.addNamespaceDeclaration("layout", BundleNamespaces.LAYOUT);
    rootAttributes.addNamespaceDeclaration("core", AttributeNames.Core.NAMESPACE);
    rootAttributes.addNamespaceDeclaration("html", AttributeNames.Html.NAMESPACE);
    rootAttributes.addNamespaceDeclaration("swing", AttributeNames.Swing.NAMESPACE);
    rootAttributes.addNamespaceDeclaration("pdf", AttributeNames.Pdf.NAMESPACE);

    writer.writeTag(BundleNamespaces.STYLE, "style", rootAttributes, XmlWriterSupport.OPEN);

    final ReportDefinition report = styleFileState.getReport();
    if (report instanceof MasterReport)
    {
      // only the master report can carry page-definitions ..
      final PageDefinition definition = report.getPageDefinition();
      writePageDefinition(writer, definition);
    }

    // write list of styles
    final StyleSheetCollection styles = report.getStyleSheetCollection();
    final String[] styleNames = styles.getStyleNames();

    for (int i = 0; i < styleNames.length; i++)
    {
      final String name = styleNames[i];
      final ElementStyleSheet style = styles.getStyleSheet(name);

      StyleWriterUtility.writeStyleRule(BundleNamespaces.STYLE, "style-rule", writer, style, true);
    }

    // write layout processor section
    if (ExpressionWriterUtility.isGlobalLayoutExpressionActive(styleFileState))
    {
      writer.writeTag(BundleNamespaces.LAYOUT, "layout-processors", XmlWriterSupport.OPEN);
      ExpressionWriterUtility.writeGlobalLayoutExpressions(bundle, styleFileState, writer);
      writer.writeCloseTag();
    }

    // write watermark
    final Watermark watermark = report.getWatermark();
    final BundleElementWriteHandler watermarkHandler = BundleElementWriterFactory.createHandler(watermark);
    watermarkHandler.writeElement(bundle, styleFileState, writer, watermark);

    // write page-header
    final PageHeader pageHeader = report.getPageHeader();
    final BundleElementWriteHandler pageHeaderHandler = BundleElementWriterFactory.createHandler(pageHeader);
    pageHeaderHandler.writeElement(bundle, styleFileState, writer, pageHeader);

    // write page-footer
    final PageFooter pageFooter = report.getPageFooter();
    final BundleElementWriteHandler pageFooterHandler = BundleElementWriterFactory.createHandler(pageFooter);
    pageFooterHandler.writeElement(bundle, styleFileState, writer, pageFooter);


    writer.writeCloseTag();
    writer.close();

    return styleFileState.getFileName();
  }

  private void writePageDefinition(final XmlWriter writer, final PageDefinition definition)
      throws BundleWriterException, IOException
  {
    if (writer == null)
    {
      throw new NullPointerException();
    }
    if (definition == null)
    {
      throw new NullPointerException();
    }


    if (definition instanceof SimplePageDefinition)
    {
      final SimplePageDefinition sdef = (SimplePageDefinition) definition;
      final int pageCountHorizontal = sdef.getPageCountHorizontal();
      final int pageCountVertical = sdef.getPageCountVertical();
      final PageFormat pageFormat = sdef.getPageFormat();

      final AttributeList attr = new AttributeList();
      attr.setAttribute(ExtParserModule.NAMESPACE, "horizontal-span", String.valueOf(pageCountHorizontal));
      attr.setAttribute(ExtParserModule.NAMESPACE, "vertical-span", String.valueOf(pageCountVertical));
      buildPageFormatProperties(pageFormat, attr);
      writer.writeTag(BundleNamespaces.STYLE, "page-definition", attr, XmlWriterSupport.CLOSE);
    }
    else if (definition instanceof CustomPageDefinition)
    {
      throw new BundleWriterException("Cannot handle 'CustomPageDefinition' objects.");
    }
    else
    {
      throw new BundleWriterException("Cannot handle generic page-definition objects.");
    }
  }


  /**
   * Compiles a collection of page format properties.
   *
   * @param fmt    the pageformat
   * @param retval the attribute list
   * @return The properties.
   */
  private AttributeList buildPageFormatProperties(final PageFormat fmt,
                                                  final AttributeList retval)
  {
    if (fmt == null)
    {
      throw new NullPointerException();
    }
    if (retval == null)
    {
      throw new NullPointerException();
    }

    final Paper paper = fmt.getPaper();
    final int w = (int) paper.getWidth();
    final int h = (int) paper.getHeight();

    final String pageDefinition = PageFormatFactory.getInstance().getPageFormatName(w, h);
    if (pageDefinition != null)
    {
      retval.setAttribute(ExtParserModule.NAMESPACE,
          "pageformat", pageDefinition);
    }
    else
    {
      retval.setAttribute(ExtParserModule.NAMESPACE,
          "width", String.valueOf(w));
      retval.setAttribute(ExtParserModule.NAMESPACE,
          "height", String.valueOf(h));
    }

    final Insets borders = getBorders(paper);

    if (fmt.getOrientation() == PageFormat.REVERSE_LANDSCAPE)
    {
      retval.setAttribute(ExtParserModule.NAMESPACE,
          "orientation", "reverse-landscape");
      retval.setAttribute(ExtParserModule.NAMESPACE,
          "margin-top", String.valueOf(borders.right));
      retval.setAttribute(ExtParserModule.NAMESPACE,
          "margin-left", String.valueOf(borders.top));
      retval.setAttribute(ExtParserModule.NAMESPACE,
          "margin-bottom", String.valueOf(borders.left));
      retval.setAttribute(ExtParserModule.NAMESPACE,
          "margin-right", String.valueOf(borders.bottom));
    }
    else if (fmt.getOrientation() == PageFormat.PORTRAIT)
    {
      retval.setAttribute(ExtParserModule.NAMESPACE,
          "orientation", "portrait");
      retval.setAttribute(ExtParserModule.NAMESPACE,
          "margin-top", String.valueOf(borders.top));
      retval.setAttribute(ExtParserModule.NAMESPACE,
          "margin-left", String.valueOf(borders.left));
      retval.setAttribute(ExtParserModule.NAMESPACE,
          "margin-bottom", String.valueOf(borders.bottom));
      retval.setAttribute(ExtParserModule.NAMESPACE,
          "margin-right", String.valueOf(borders.right));
    }
    else
    {
      retval.setAttribute(ExtParserModule.NAMESPACE,
          "orientation", "landscape");
      retval.setAttribute(ExtParserModule.NAMESPACE,
          "margin-top", String.valueOf(borders.left));
      retval.setAttribute(ExtParserModule.NAMESPACE,
          "margin-left", String.valueOf(borders.bottom));
      retval.setAttribute(ExtParserModule.NAMESPACE,
          "margin-bottom", String.valueOf(borders.right));
      retval.setAttribute(ExtParserModule.NAMESPACE,
          "margin-right", String.valueOf(borders.top));
    }

    return retval;
  }

  /**
   * Returns the borders for the given paper.
   *
   * @param p the paper.
   * @return The borders.
   */
  private Insets getBorders(final Paper p)
  {
    return new Insets((int) p.getImageableY(), (int) p.getImageableX(),
        (int) (p.getHeight() - (p.getImageableY() + p.getImageableHeight())),
        (int) (p.getWidth() - (p.getImageableX() + p.getImageableWidth())));
  }

}

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

package org.pentaho.reporting.engine.classic.core.modules.parser.extwriter;

import java.io.IOException;
import java.util.ArrayList;

import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.Group;
import org.pentaho.reporting.engine.classic.core.ReportDefinition;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.ExtParserModule;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriterSupport;

/**
 * A styles writer.
 *
 * @author Thomas Morgner.
 */
public class StylesWriter extends AbstractXMLDefinitionWriter
{

  /**
   * Storage for the styles.
   */
  private final ArrayList reportStyles;

  /**
   * Creates a new styles writer.
   *
   * @param reportWriter the report writer.
   * @param xmlWriter    the current indention level.
   */
  public StylesWriter(final ReportWriterContext reportWriter, final XmlWriter xmlWriter)
  {
    super(reportWriter, xmlWriter);
    reportStyles = new ArrayList();
  }

  /**
   * Writes the ihnerited styles to a character stream writer. This will collect all inherited styles, ignoring all
   * styles which are directly bound to an element or which are global default stylesheets.
   *
   * @throws IOException           if there is an I/O problem.
   * @throws ReportWriterException if there is a problem writing the report.
   */
  public void write()
      throws IOException, ReportWriterException
  {
    final ElementStyleSheet[] styles = collectStyles();
    if (styles.length == 0)
    {
      return;
    }

    final XmlWriter xmlWriter = getXmlWriter();
    xmlWriter.writeTag(ExtParserModule.NAMESPACE, AbstractXMLDefinitionWriter.STYLES_TAG, XmlWriterSupport.OPEN);
    for (int i = 0; i < styles.length; i++)
    {
      final ElementStyleSheet style = styles[i];
      xmlWriter.writeTag(ExtParserModule.NAMESPACE, AbstractXMLDefinitionWriter.STYLE_TAG,
          "name", style.getName(), XmlWriterSupport.OPEN);

      final StyleWriter stW = new StyleWriter
          (getReportWriter(), style, xmlWriter);
      stW.write();

      xmlWriter.writeCloseTag();
    }

    xmlWriter.writeCloseTag();
  }

  /**
   * Collects styles from all the bands in the report. The returned styles are ordered so that parent style sheets are
   * contained before any child stylesheets in the array.
   *
   * @return The styles.
   */
  private ElementStyleSheet[] collectStyles()
  {
    final ReportDefinition report = getReport();
    collectStylesFromBand(report.getReportHeader());
    collectStylesFromBand(report.getReportFooter());
    collectStylesFromBand(report.getPageHeader());
    collectStylesFromBand(report.getPageFooter());
    collectStylesFromBand(report.getItemBand());
    for (int i = 0; i < report.getGroupCount(); i++)
    {
      final Group g = report.getGroup(i);
      collectStylesFromBand(g.getHeader());
      collectStylesFromBand(g.getFooter());
    }

    return (ElementStyleSheet[])
        reportStyles.toArray(new ElementStyleSheet[reportStyles.size()]);
  }

  /**
   * Collects the styles from a band.
   *
   * @param band the band.
   */
  private void collectStylesFromBand(final Band band)
  {
    collectStylesFromElement(band);

    final Element[] elements = band.getElementArray();
    for (int i = 0; i < elements.length; i++)
    {
      if (elements[i] instanceof Band)
      {
        collectStylesFromBand((Band) elements[i]);
      }
      else
      {
        collectStylesFromElement(elements[i]);
      }
    }

  }

  /**
   * Collects the styles from an element.
   *
   * @param element the element.
   */
  private void collectStylesFromElement(final Element element)
  {
    final ElementStyleSheet elementSheet = element.getStyle();

    final ElementStyleSheet[] parents = elementSheet.getParents();
    for (int i = 0; i < parents.length; i++)
    {
      final ElementStyleSheet es = parents[i];
      addCollectableStyleSheet(es);
    }
  }

  /**
   * Adds a defined stylesheet to the styles collection. If the stylesheet is one of the default stylesheets, then it is
   * not collected.
   *
   * @param es the element style sheet.
   */
  private void addCollectableStyleSheet(final ElementStyleSheet es)
  {
    if (es.isGlobalDefault())
    {
      return;
    }

    final ElementStyleSheet[] parents = es.getParents();
    for (int i = 0; i < parents.length; i++)
    {
      final ElementStyleSheet parentsheet = parents[i];
      addCollectableStyleSheet(parentsheet);
    }

    if (reportStyles.contains(es) == false)
    {
      reportStyles.add(es);
    }
  }
}

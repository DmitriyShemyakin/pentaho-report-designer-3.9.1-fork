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

package org.pentaho.reporting.engine.classic.extensions.parsers.reportdesigner.report;

import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.DetailsFooter;
import org.pentaho.reporting.engine.classic.core.DetailsHeader;
import org.pentaho.reporting.engine.classic.core.ItemBand;
import org.pentaho.reporting.engine.classic.core.NoDataBand;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.ReportParserUtil;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.IgnoreAnyChildReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class ReportGroupsReadHandler extends AbstractXmlReadHandler
{
  private ReportGroupReadHandler groupReadHandler;

  public ReportGroupsReadHandler()
  {
  }

  /**
   * Returns the handler for a child element.
   *
   * @param uri     the URI of the namespace of the current element.
   * @param tagName the tag name.
   * @param atts    the attributes.
   * @return the handler or null, if the tagname is invalid.
   * @throws SAXException if there is a parsing error.
   */
  protected XmlReadHandler getHandlerForChild(final String uri, final String tagName, final Attributes atts) throws SAXException
  {
    if (isSameNamespace(uri) == false)
    {
      return null;
    }
    if ("padding".equals(tagName))
    {
      return new IgnoreAnyChildReadHandler();
    }
    if ("property".equals(tagName))
    {
      return new IgnoreAnyChildReadHandler();
    }
    if ("child".equals(tagName))
    {
      final String type = atts.getValue(uri, "type");
      if ("org.pentaho.reportdesigner.crm.report.model.ReportGroup".equals(type))
      {
        groupReadHandler = new ReportGroupReadHandler();
        return groupReadHandler;
      }
    }
    return null;
  }

  /**
   * Done parsing.
   *
   * @throws SAXException if there is a parsing error.
   */
  protected void doneParsing() throws SAXException
  {
    super.doneParsing();
    final AbstractReportDefinition report = (AbstractReportDefinition)
        getRootHandler().getHelperObject(ReportParserUtil.HELPER_OBJ_REPORT_NAME);

    final ItemBand itemBand = report.getItemBand();
    final NoDataBand noDataBand = report.getNoDataBand();
    final DetailsFooter detailsFooter = report.getDetailsFooter();
    final DetailsHeader detailsHeader = report.getDetailsHeader();

    if (groupReadHandler != null)
    {
      report.setRootGroup(groupReadHandler.getGroup());
    }
    report.setItemBand(itemBand);
    report.setNoDataBand(noDataBand);
    report.setDetailsFooter(detailsFooter);
    report.setDetailsHeader(detailsHeader);

  }

  /**
   * Returns the object for this element or null, if this element does
   * not create an object.
   *
   * @return the object.
   * @throws SAXException if an parser error occured.
   */
  public Object getObject() throws SAXException
  {
    return null;
  }
}

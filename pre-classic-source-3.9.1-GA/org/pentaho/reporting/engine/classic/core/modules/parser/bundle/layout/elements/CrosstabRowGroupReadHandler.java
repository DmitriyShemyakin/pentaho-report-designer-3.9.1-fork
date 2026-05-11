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

package org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout.elements;

import org.pentaho.reporting.engine.classic.core.CrosstabRowGroup;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.Group;
import org.pentaho.reporting.engine.classic.core.GroupFooter;
import org.pentaho.reporting.engine.classic.core.GroupHeader;
import org.pentaho.reporting.engine.classic.core.CrosstabSummaryHeader;
import org.pentaho.reporting.engine.classic.core.CrosstabSummaryFooter;
import org.pentaho.reporting.engine.classic.core.CrosstabTitleHeader;
import org.pentaho.reporting.engine.classic.core.CrosstabTitleFooter;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.BundleNamespaces;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.xmlns.parser.StringReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class CrosstabRowGroupReadHandler extends AbstractElementReadHandler
{
  private GroupHeaderReadHandler headerReadHandler;
  private GroupFooterReadHandler footerReadHandler;
  private CrosstabSummaryHeaderBandReadHandler summaryHeaderBandReadHandler;
  private CrosstabSummaryFooterBandReadHandler summaryFooterBandReadHandler;
  private CrosstabTitleHeaderBandReadHandler titleHeaderBandReadHandler;
  private CrosstabTitleFooterBandReadHandler titleFooterBandReadHandler; 
  private GroupBodyReadHandler groupBodyReadHandler;
  private StringReadHandler fieldReadHandler;

  public CrosstabRowGroupReadHandler()
      throws ParseException
  {
    super("crosstab-row-group");
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
  protected XmlReadHandler getHandlerForChild(final String uri,
                                              final String tagName,
                                              final Attributes atts) throws SAXException
  {
    if (BundleNamespaces.LAYOUT.equals(uri))
    {
      if ("field".equals(tagName))
      {
        if (fieldReadHandler == null)
        {
          fieldReadHandler = new StringReadHandler();
        }
        return fieldReadHandler;
      }
      if ("group-header".equals(tagName))
      {
        if (headerReadHandler == null)
        {
          headerReadHandler = new GroupHeaderReadHandler();
        }
        return headerReadHandler;
      }
      if ("group-footer".equals(tagName))
      {
        if (footerReadHandler == null)
        {
          footerReadHandler = new GroupFooterReadHandler();
        }
        return footerReadHandler;
      }
      if ("crosstab-title-header".equals(tagName))
      {
        if (titleHeaderBandReadHandler == null)
        {
          titleHeaderBandReadHandler = new CrosstabTitleHeaderBandReadHandler();
        }
        return titleHeaderBandReadHandler;
      }
      if ("crosstab-title-footer".equals(tagName))
      {
        if (titleFooterBandReadHandler == null)
        {
          titleFooterBandReadHandler = new CrosstabTitleFooterBandReadHandler();
        }
        return titleFooterBandReadHandler;
      }
      if ("crosstab-summary-header".equals(tagName))
      {
        if (summaryHeaderBandReadHandler == null)
        {
          summaryHeaderBandReadHandler = new CrosstabSummaryHeaderBandReadHandler();
        }
        return summaryHeaderBandReadHandler;
      }
      if ("crosstab-summary-footer".equals(tagName))
      {
        if (summaryFooterBandReadHandler == null)
        {
          summaryFooterBandReadHandler = new CrosstabSummaryFooterBandReadHandler();
        }
        return summaryFooterBandReadHandler;
      }
      if ("crosstab-column-group-body".equals(tagName))
      {
        groupBodyReadHandler = new CrosstabColumnSubGroupBodyReadHandler();
        return groupBodyReadHandler;
      }
      if ("crosstab-row-group-body".equals(tagName))
      {
        groupBodyReadHandler = new CrosstabRowSubGroupBodyReadHandler();
        return groupBodyReadHandler;
      }
    }
    return super.getHandlerForChild(uri, tagName, atts);
  }

  /**
   * Done parsing.
   *
   * @throws SAXException if there is a parsing error.
   */
  protected void doneParsing() throws SAXException
  {
    super.doneParsing();

    final CrosstabRowGroup group = (CrosstabRowGroup) getElement();
    if (fieldReadHandler != null)
    {
      group.setField(fieldReadHandler.getResult());
    }
    if (headerReadHandler != null)
    {
      group.setHeader((GroupHeader) headerReadHandler.getElement());
    }
    if (footerReadHandler != null)
    {
      group.setFooter((GroupFooter) footerReadHandler.getElement());
    }

    if (summaryHeaderBandReadHandler!= null)
    {
      group.setSummaryHeader((CrosstabSummaryHeader) summaryHeaderBandReadHandler.getElement());
    }
    if (summaryFooterBandReadHandler != null)
    {
      group.setSummaryFooter((CrosstabSummaryFooter) summaryFooterBandReadHandler.getElement());
    }
    if (titleHeaderBandReadHandler != null)
    {
      group.setTitleHeader((CrosstabTitleHeader) titleHeaderBandReadHandler.getElement());
    }
    if (titleFooterBandReadHandler != null)
    {
      group.setTitleFooter((CrosstabTitleFooter) titleFooterBandReadHandler.getElement());
    }

    if (groupBodyReadHandler != null)
    {
      group.setBody(groupBodyReadHandler.getGroupBody());
    }
  }

  protected Element createElement(final String elementType)
  {
    return new CrosstabRowGroup();
  }

  public Group getGroup()
  {
    return (Group) getElement();
  }
}
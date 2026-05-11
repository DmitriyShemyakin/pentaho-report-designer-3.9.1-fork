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

import org.pentaho.reporting.engine.classic.core.DetailsFooter;
import org.pentaho.reporting.engine.classic.core.DetailsHeader;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.GroupBody;
import org.pentaho.reporting.engine.classic.core.GroupDataBody;
import org.pentaho.reporting.engine.classic.core.ItemBand;
import org.pentaho.reporting.engine.classic.core.NoDataBand;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class DataGroupBodyReadHandler extends AbstractElementReadHandler implements GroupBodyReadHandler
{
  private ItemBandReadHandler itemBandReadHandler;
  private NoDataBandReadHandler noDataBandReadHandler;
  private DetailsHeaderBandReadHandler detailsHeaderBandReadHandler;
  private DetailsFooterBandReadHandler detailsFooterBandReadHandler;

  public DataGroupBodyReadHandler()
      throws ParseException
  {
    super("group-data-body");
  }

  protected Element createElement(final String elementType)
  {
    return new GroupDataBody();
  }

  public GroupBody getGroupBody()
  {
    return (GroupBody) getElement();
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

    if (isSameNamespace(uri))
    {
      if ("details".equals(tagName))
      {
        if (itemBandReadHandler == null)
        {
          itemBandReadHandler = new ItemBandReadHandler();
        }
        return itemBandReadHandler;
      }
      if ("details-header".equals(tagName))
      {
        if (detailsHeaderBandReadHandler == null)
        {
          detailsHeaderBandReadHandler = new DetailsHeaderBandReadHandler();
        }
        return detailsHeaderBandReadHandler;
      }
      if ("details-footer".equals(tagName))
      {
        if (detailsFooterBandReadHandler == null)
        {
          detailsFooterBandReadHandler = new DetailsFooterBandReadHandler();
        }
        return detailsFooterBandReadHandler;
      }
      if ("no-data".equals(tagName))
      {
        if (noDataBandReadHandler == null)
        {
          noDataBandReadHandler = new NoDataBandReadHandler();
        }
        return noDataBandReadHandler;
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
    final GroupDataBody body = (GroupDataBody) getElement();
    if (noDataBandReadHandler != null)
    {
      body.setNoDataBand((NoDataBand) noDataBandReadHandler.getElement());
    }
    if (itemBandReadHandler != null)
    {
      body.setItemBand((ItemBand) itemBandReadHandler.getElement());
    }
    if (detailsFooterBandReadHandler != null)
    {
      body.setDetailsFooter((DetailsFooter) detailsFooterBandReadHandler.getElement());
    }
    if (detailsHeaderBandReadHandler != null)
    {
      body.setDetailsHeader((DetailsHeader) detailsHeaderBandReadHandler.getElement());
    }
  }
}

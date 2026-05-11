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

package org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout;

import org.pentaho.reporting.engine.classic.core.ReportDefinition;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.ReportParserUtil;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.StyleSheetCollection;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class StyleRuleReadHandler extends AbstractXmlReadHandler
{
  private ElementStyleSheet styleSheet;

  public StyleRuleReadHandler()
  {
  }

  /**
   * Starts parsing.
   *
   * @param attrs the attributes.
   * @throws SAXException if there is a parsing error.
   */
  protected void startParsing(final Attributes attrs) throws SAXException
  {
    final Object maybeReport = getRootHandler().getHelperObject
        (ReportParserUtil.HELPER_OBJ_REPORT_NAME);
    if (maybeReport instanceof ReportDefinition == false)
    {
      throw new ParseException("Invalid internal configuration for 'report-obj'", getLocator());
    }

    final ReportDefinition def = (ReportDefinition) maybeReport;
    final StyleSheetCollection styleSheetCollection = def.getStyleSheetCollection();

    final String name = attrs.getValue(getUri(), "name");
    if (name == null)
    {
      throw new ParseException("Required attribute 'name' is not defined.", getLocator());
    }
    styleSheet = styleSheetCollection.createStyleSheet(name);
    final String parent = attrs.getValue(getUri(), "parent");
    if (parent != null)
    {
      final String[] classes = StringUtils.split(parent, " ", "'");
      for (int i = 0; i < classes.length; i++)
      {
        final String aClass = classes[i];
        final ElementStyleSheet parentStyleSheet = styleSheetCollection.createStyleSheet(aClass);
        try
        {
          styleSheet.addParent(parentStyleSheet);
        }
        catch (IllegalArgumentException e)
        {
          throw new ParseException("Loop detected: Specified parent style-sheet is invalid here.", e, getLocator());
        }
      }
    }

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
    final StyleReadHandler readHandler = (StyleReadHandler)
        StyleReadHandlerFactory.getInstance().getHandler(uri, tagName);
    if (readHandler == null)
    {
      throw new ParseException("Unable to locate style-handler for <" + uri + '|' + tagName + '>', getLocator());
    }

    readHandler.setStyleSheet(styleSheet);
    return readHandler;
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws SAXException if an parser error occured.
   */
  public Object getObject() throws SAXException
  {
    return styleSheet;
  }
}

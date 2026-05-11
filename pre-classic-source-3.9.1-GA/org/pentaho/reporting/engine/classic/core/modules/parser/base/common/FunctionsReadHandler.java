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

package org.pentaho.reporting.engine.classic.core.modules.parser.base.common;

import java.util.ArrayList;

import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.PropertyAttributes;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.SAXException;

public class FunctionsReadHandler extends AbstractPropertyXmlReadHandler
{
  private AbstractReportDefinition report;
  private ArrayList expressionHandlers;
  private ArrayList propertyRefs;

  public FunctionsReadHandler(final AbstractReportDefinition report)
  {
    this.report = report;
    this.expressionHandlers = new ArrayList();
    this.propertyRefs = new ArrayList();
  }


  /**
   * Returns the handler for a child element.
   *
   * @param tagName the tag name.
   * @param attrs   the attributes.
   * @return the handler or null, if the tagname is invalid.
   * @throws org.xml.sax.SAXException if there is a parsing error.
   */
  protected XmlReadHandler getHandlerForChild(final String uri,
                                              final String tagName,
                                              final PropertyAttributes attrs)
      throws SAXException
  {
    if (isSameNamespace(uri) == false)
    {
      return null;
    }

    if ("expression".equals(tagName) || "function".equals(tagName))
    {
      final ExpressionReadHandler readHandler = new ExpressionReadHandler();
      expressionHandlers.add(readHandler);
      return readHandler;

    }
    else if ("property-ref".equals(tagName))
    {
      final PropertyReferenceReadHandler readHandler = new PropertyReferenceReadHandler();
      propertyRefs.add(readHandler);
      return readHandler;
    }
    return null;
  }

  /**
   * Done parsing.
   *
   * @throws org.xml.sax.SAXException if there is a parsing error.
   */
  protected void doneParsing()
      throws SAXException
  {
    for (int i = 0; i < expressionHandlers.size(); i++)
    {
      final ExpressionReadHandler readHandler = (ExpressionReadHandler) expressionHandlers.get(i);
      if (readHandler.getObject() != null)
      {
        report.addExpression((Expression) readHandler.getObject());
      }
    }

    final MasterReport master;
    if (report instanceof MasterReport)
    {
      master = (MasterReport) report;
    }
    else
    {
      master = null;
    }

    for (int i = 0; i < propertyRefs.size(); i++)
    {
      final PropertyReferenceReadHandler readHandler =
          (PropertyReferenceReadHandler) propertyRefs.get(i);
      final Object object = readHandler.getObject();
      if (object != null)
      {
        if (object instanceof String)
        {
          final String text = (String) object;
          if (text.length() == 0)
          {
            continue;
          }
        }

        if (master != null)
        {
          master.getParameterValues().put(readHandler.getPropertyName(), object);
          report.getProperties().put(readHandler.getPropertyName(), object);
        }
        else
        {
          report.setProperty(readHandler.getPropertyName(), object);
        }
      }
    }
  }


  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   */
  public Object getObject()
  {
    return null;
  }
}

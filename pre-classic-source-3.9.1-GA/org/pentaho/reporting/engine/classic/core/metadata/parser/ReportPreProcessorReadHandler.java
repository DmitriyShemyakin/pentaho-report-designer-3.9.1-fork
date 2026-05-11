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
 * Copyright (c) 2009 Pentaho Corporation..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.metadata.parser;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.util.ArrayList;
import java.util.HashMap;

import org.pentaho.reporting.engine.classic.core.metadata.DefaultReportPreProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.ReportPreProcessor;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.ReportParserUtil;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Todo: Document me!
 * <p/>
 * Date: 28.06.2009
 * Time: 21:37:47
 *
 * @author Thomas Morgner.
 */
public class ReportPreProcessorReadHandler extends AbstractXmlReadHandler
{
  private String bundleName;
  private Class expressionClass;
  private String prefix;

  private boolean expert;
  private boolean hidden;
  private boolean preferred;
  private boolean deprecated;
  private ArrayList attributeHandlers;
  private BeanInfo beanInfo;
  private HashMap properties;
  private boolean autoProcess;
  private boolean experimental;
  private int compatibilityLevel;

  public ReportPreProcessorReadHandler()
  {
    attributeHandlers = new ArrayList();
    properties = new HashMap();
  }

  /**
   * Starts parsing.
   *
   * @param attrs the attributes.
   * @throws org.xml.sax.SAXException if there is a parsing error.
   */
  protected void startParsing(final Attributes attrs) throws SAXException
  {
    bundleName = attrs.getValue(getUri(), "bundle-name");
    expert = "true".equals(attrs.getValue(getUri(), "expert"));
    hidden = "true".equals(attrs.getValue(getUri(), "hidden"));
    preferred = "true".equals(attrs.getValue(getUri(), "preferred"));
    deprecated = "true".equals(attrs.getValue(getUri(), "deprecated"));
    autoProcess = "true".equals(attrs.getValue(getUri(), "auto-process"));
    experimental = "true".equals(attrs.getValue(getUri(), "experimental")); // NON-NLS
    compatibilityLevel = ReportParserUtil.parseVersion(attrs.getValue(getUri(), "compatibility-level"));

    final String valueTypeText = attrs.getValue(getUri(), "class");
    if (valueTypeText == null)
    {
      throw new ParseException("Attribute 'class' is undefined", getLocator());
    }
    try
    {
      final ClassLoader loader = ObjectUtilities.getClassLoader(ExpressionReadHandler.class);
      expressionClass = Class.forName(valueTypeText, false, loader);
      if (ReportPreProcessor.class.isAssignableFrom(expressionClass) == false)
      {
        throw new ParseException("Attribute 'class' is not valid", getLocator());
      }
    }
    catch (ParseException pe)
    {
      throw pe;
    }
    catch (Exception e)
    {
      throw new ParseException("Attribute 'class' is not valid", e, getLocator());
    }
    try
    {
      beanInfo = Introspector.getBeanInfo(expressionClass);
    }
    catch (IntrospectionException e)
    {
      throw new ParseException("Cannot introspect specified " + expressionClass, getLocator());
    }

    prefix = "pre-processor." + expressionClass.getName();

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
    if (getUri().equals(uri) == false)
    {
      return null;
    }


    if ("property".equals(tagName))
    {
      final ReportPreProcessorPropertyReadHandler readHandler =
          new ReportPreProcessorPropertyReadHandler(beanInfo, bundleName, prefix + ".property.");
      attributeHandlers.add(readHandler);
      return readHandler;
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
    for (int i = 0; i < attributeHandlers.size(); i++)
    {
      final ReportPreProcessorPropertyReadHandler handler = (ReportPreProcessorPropertyReadHandler) attributeHandlers.get(i);
      final String attrName = handler.getName();
      properties.put(attrName, handler.getObject());
    }
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws SAXException if an parser error occured.
   */
  public Object getObject() throws SAXException
  {
    return new DefaultReportPreProcessorMetaData(bundleName, "pre-processor.", expert, preferred, hidden, deprecated,
        expressionClass, properties, beanInfo, autoProcess, experimental, compatibilityLevel);
  }
}

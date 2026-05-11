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

package org.pentaho.reporting.engine.classic.core.metadata.parser;

import org.pentaho.reporting.engine.classic.core.metadata.AttributeCore;
import org.pentaho.reporting.engine.classic.core.metadata.DefaultAttributeCore;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.ReportParserUtil;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class AttributeReadHandler extends AbstractXmlReadHandler
{
  private String namespace;
  private String name;
  private boolean preferred;
  private boolean mandatory;
  private boolean expert;
  private boolean hidden;
  private boolean computed;
  private boolean transientFlag;
  private Class valueType;
  private String valueRole;
  private boolean deprecated;
  private boolean bulk;
  private String propertyEditor;
  private boolean designTimeValue;
  private String bundle;
  private AttributeCore attributeCore;
  private boolean experimental;
  private int compatibilityLevel;
  
  public AttributeReadHandler(final String bundle)
  {
    this.bundle = bundle;
  }

  /**
   * Starts parsing.
   *
   * @param attrs the attributes.
   * @throws SAXException if there is a parsing error.
   */
  protected void startParsing(final Attributes attrs) throws SAXException
  {
    namespace = attrs.getValue(getUri(), "namespace"); // NON-NLS
    if (namespace == null)
    {
      throw new ParseException("Attribute 'namespace' is undefined", getLocator());
    }
    name = attrs.getValue(getUri(), "name"); // NON-NLS
    if (name == null)
    {
      throw new ParseException("Attribute 'name' is undefined", getLocator());
    }
    
    experimental = "true".equals(attrs.getValue(getUri(), "experimental")); // NON-NLS
    compatibilityLevel = ReportParserUtil.parseVersion(attrs.getValue(getUri(), "compatibility-level"));
    mandatory = "true".equals(attrs.getValue(getUri(), "mandatory")); // NON-NLS
    expert = "true".equals(attrs.getValue(getUri(), "expert")); // NON-NLS
    hidden = "true".equals(attrs.getValue(getUri(), "hidden")); // NON-NLS
    computed = "true".equals(attrs.getValue(getUri(), "computed")); // NON-NLS
    transientFlag = "true".equals(attrs.getValue(getUri(), "transient")); // NON-NLS
    preferred = "true".equals(attrs.getValue(getUri(), "preferred")); // NON-NLS
    deprecated = "true".equals(attrs.getValue(getUri(), "deprecated")); // NON-NLS
    bulk = "true".equals(attrs.getValue(getUri(), "prefer-bulk")); // NON-NLS
    designTimeValue = "true".equals(attrs.getValue(getUri(), "design-time-value")); // NON-NLS

    final String valueTypeText = attrs.getValue(getUri(), "value-type"); // NON-NLS
    if (valueTypeText == null)
    {
      throw new ParseException("Attribute 'value-type' is undefined", getLocator());
    }
    try
    {
      final ClassLoader classLoader = ObjectUtilities.getClassLoader(getClass());
      valueType = Class.forName(valueTypeText, false, classLoader);
    }
    catch (Exception e)
    {
      throw new ParseException("Attribute 'value-type' is not valid", e, getLocator());
    }

    valueRole = attrs.getValue(getUri(), "value-role"); // NON-NLS
    if (valueRole == null)
    {
      valueRole = "Value"; // NON-NLS
    }

    propertyEditor = attrs.getValue(getUri(), "propertyEditor"); // NON-NLS
    final String bundleFromAttributes = attrs.getValue(getUri(), "bundle-name"); // NON-NLS
    if (bundleFromAttributes != null)
    {
      bundle = bundleFromAttributes;
    }


    final String metaDataCoreClass = attrs.getValue(getUri(), "impl"); // NON-NLS
    if (metaDataCoreClass != null)
    {
      attributeCore = (AttributeCore) ObjectUtilities.loadAndInstantiate
          (metaDataCoreClass, AttributeReadHandler.class, AttributeCore.class);
      if (attributeCore == null)
      {
        throw new ParseException("Attribute 'impl' references a invalid AttributeCore implementation.", getLocator());
      }
    }
    else
    {
      attributeCore = new DefaultAttributeCore();
    }
  }

  public AttributeCore getAttributeCore()
  {
    return attributeCore;
  }

  public String getPropertyEditor()
  {
    return propertyEditor;
  }

  public String getNamespace()
  {
    return namespace;
  }

  public String getName()
  {
    return name;
  }

  public boolean isPreferred()
  {
    return preferred;
  }

  public boolean isMandatory()
  {
    return mandatory;
  }

  public boolean isExpert()
  {
    return expert;
  }

  public boolean isDeprecated()
  {
    return deprecated;
  }

  public boolean isHidden()
  {
    return hidden;
  }

  public boolean isComputed()
  {
    return computed;
  }

  public boolean isTransient()
  {
    return transientFlag;
  }

  public Class getValueType()
  {
    return valueType;
  }

  public boolean isBulk()
  {
    return bulk;
  }

  public String getValueRole()
  {
    return valueRole;
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws SAXException if an parser error occured.
   */
  public Object getObject() throws SAXException
  {
    return new AttributeDefinition(namespace, name, preferred, mandatory,
        expert, hidden, computed, transientFlag, valueType, valueRole, propertyEditor,
        deprecated, bulk, designTimeValue, bundle, attributeCore, experimental, compatibilityLevel);
  }

  public String getBundle()
  {
    return bundle;
  }

  public boolean isDesignTimeValue()
  {
    return designTimeValue;
  }

  public boolean isExperimental()
  {
    return experimental;
  }

  public int getCompatibilityLevel()
  {
    return compatibilityLevel;
  }
}

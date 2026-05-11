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

package org.pentaho.reporting.engine.classic.core.metadata;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.metadata.parser.ElementTypeCollection;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public final class ElementTypeRegistry
{
  private static final Log logger = LogFactory.getLog(ElementTypeRegistry.class);
  private HashMap namespaceMapping;
  private HashMap backend;
  private static ElementTypeRegistry instance;

  public static synchronized ElementTypeRegistry getInstance()
  {
    if (instance == null)
    {
      instance = new ElementTypeRegistry();
    }
    return instance;
  }

  private ElementTypeRegistry()
  {
    this.backend = new HashMap();
    this.namespaceMapping = new HashMap();
  }

  public void registerFromXml(final URL metaDataSource) throws IOException
  {
    if (metaDataSource == null)
    {
      throw new NullPointerException("Error: Could not find the element meta-data description file");
    }

    final ResourceManager resourceManager = new ResourceManager();
    resourceManager.registerDefaults();
    try
    {
      final Resource resource = resourceManager.createDirectly(metaDataSource, ElementTypeCollection.class);
      final ElementTypeCollection typeCollection = (ElementTypeCollection) resource.getResource();
      final ElementMetaData[] types = typeCollection.getElementTypes();
      for (int i = 0; i < types.length; i++)
      {
        final ElementMetaData metaData = types[i];
        ElementTypeRegistry.getInstance().registerElement(metaData);
      }
    }
    catch (Exception e)
    {
      ElementTypeRegistry.logger.debug("Failed:", e);
      throw new IOException("Error: Could not parse the element meta-data description file");
    }
  }

  public void registerElement(final ElementMetaData metaData)
  {
    if (metaData == null)
    {
      throw new NullPointerException();
    }
    this.backend.put(metaData.getName(), metaData);
  }

  public ElementMetaData[] getAllElementTypes()
  {
    return (ElementMetaData[]) backend.values().toArray(new ElementMetaData[backend.size()]);
  }

  public boolean isElementTypeRegistered(final String identifier)
  {
    if (identifier == null)
    {
      throw new NullPointerException();
    }
    return backend.containsKey(identifier);
  }

  public ElementMetaData getElementType(final String identifier) throws MetaDataLookupException
  {
    if (identifier == null)
    {
      throw new NullPointerException();
    }
    final ElementMetaData retval = (ElementMetaData) backend.get(identifier);
    if (retval == null)
    {
      throw new MetaDataLookupException("There is no meta-data defined for type '" + identifier + '\'');
    }
    return retval;
  }

  public String getNamespacePrefix(final String namespaceUri)
  {
    if (namespaceUri == null)
    {
      throw new NullPointerException();
    }
    return (String) namespaceMapping.get(namespaceUri);
  }

  public void registerNamespacePrefix(final String namespaceUri, final String namespacePrefix)
  {
    if (namespacePrefix == null)
    {
      throw new NullPointerException();
    }
    if (namespaceUri == null)
    {
      throw new NullPointerException();
    }
    namespaceMapping.put(namespaceUri, namespacePrefix);
  }
}

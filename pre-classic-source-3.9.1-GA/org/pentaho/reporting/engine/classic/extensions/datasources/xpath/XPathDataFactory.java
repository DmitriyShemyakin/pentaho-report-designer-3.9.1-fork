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
 * Copyright (c) 2008 - 2009 Pentaho Corporation, .  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.extensions.datasources.xpath;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import javax.swing.table.TableModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.ResourceBundleFactory;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.resourceloader.ResourceData;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class XPathDataFactory implements DataFactory, Cloneable
{
  private static final Log logger = LogFactory.getLog(XPathDataFactory.class);

  private HashMap<String, String> queries;
  private String xqueryDataFile;
  private transient ResourceManager resourceManager;
  private transient ResourceKey contextKey;

  public XPathDataFactory()
  {
    queries = new HashMap<String, String>();
    resourceManager = new ResourceManager();
    resourceManager.registerDefaults();
  }

  public String getXqueryDataFile()
  {
    return xqueryDataFile;
  }

  public void setXqueryDataFile(final String xqueryDataFile)
  {
    this.xqueryDataFile = xqueryDataFile;
  }

  public void setQuery(final String name, final String value)
  {
    if (value == null)
    {
      queries.remove(name);
    }
    else
    {
      queries.put(name, value);
    }
  }

  public String getQuery(final String name)
  {
    return queries.get(name);
  }

  public String[] getQueryNames()
  {
    return queries.keySet().toArray(new String[queries.size()]);
  }

  public void initialize(final Configuration configuration,
                         final ResourceManager resourceManager,
                         final ResourceKey contextKey,
                         final ResourceBundleFactory resourceBundleFactory)
  {
    if (resourceManager == null)
    {
      throw new NullPointerException();
    }
    this.resourceManager = resourceManager;
    this.contextKey = contextKey;
  }

  /**
   * Queries a datasource. The string 'query' defines the name of the query. The Parameterset given here may contain
   * more data than actually needed for the query.
   * <p/>
   * The parameter-dataset may change between two calls, do not assume anything, and do not hold references to the
   * parameter-dataset or the position of the columns in the dataset.
   *
   * @param query      the query string
   * @param parameters the parameters for the query
   * @return the result of the query as table model.
   * @throws ReportDataFactoryException if an error occured while performing the query.
   */
  public TableModel queryData(final String query, final DataRow parameters) throws ReportDataFactoryException
  {
    final String xpath = queries.get(query);
    if (xpath == null)
    {
      throw new ReportDataFactoryException("No such query");
    }

    final int queryLimitVal;
    final Object queryLimit = parameters.get(DataFactory.QUERY_LIMIT);
    if (queryLimit instanceof Number)
    {
      final Number i = (Number) queryLimit;
      queryLimitVal = i.intValue();
    }
    else
    {
      queryLimitVal = -1;
    }

    try
    {
      final ResourceData resource = load();
      return new XPathTableModel(resource, resourceManager, xpath, parameters, queryLimitVal);
    }
    catch (ResourceException re)
    {
      throw new ReportDataFactoryException("Failed to load XML data", re);
    }
  }

  private ResourceData load() throws ResourceException
  {
    try
    {
      final ResourceKey resourceKey;
      if (contextKey != null)
      {
        resourceKey = resourceManager.deriveKey(contextKey, getXqueryDataFile());
        return resourceManager.load(resourceKey);
      }
    }
    catch (ResourceException re)
    {
      // failed to load from context
      logger.debug("Failed to load datasource as derived path: " + re);
    }

    try
    {
      final ResourceKey resourceKey;
      resourceKey = resourceManager.createKey(new URL(getXqueryDataFile()));
      return resourceManager.load(resourceKey);
    }
    catch (ResourceException re)
    {
      logger.debug("Failed to load datasource as URL: " + re);
    }
    catch (MalformedURLException e)
    {
      //
    }

    try
    {
      final ResourceKey resourceKey;
      resourceKey = resourceManager.createKey(new File(getXqueryDataFile()));
      return resourceManager.load(resourceKey);
    }
    catch (ResourceException re)
    {
      // failed to load from context
      logger.debug("Failed to load datasource as file: " + re);
    }

    throw new ResourceException("Unable to load the resource");
  }

  public Object clone()
  {
    try
    {
      final XPathDataFactory dataFactory = (XPathDataFactory) super.clone();
      //noinspection unchecked
      dataFactory.queries = (HashMap<String, String>) queries.clone();
      return dataFactory;
    }
    catch (CloneNotSupportedException e)
    {
      throw new IllegalStateException("Failed to clone datafactory", e);
    }
  }

  /**
   * Returns a copy of the data factory that is not affected by its anchestor and holds no connection to the anchestor
   * anymore. A data-factory will be derived at the beginning of the report processing.
   *
   * @return a copy of the data factory.
   */
  public DataFactory derive()
  {
    return (XPathDataFactory) clone();
  }

  public void open() throws ReportDataFactoryException
  {

  }

  /**
   * Closes the data factory and frees all resources held by this instance.
   */
  public void close()
  {

  }

  /**
   * Checks whether the query would be executable by this datafactory. This performs a rough check, not a full query.
   *
   * @param query
   * @param parameters
   * @return
   */
  public boolean isQueryExecutable(final String query, final DataRow parameters)
  {
    return queries.containsKey(query);
  }

  public void cancelRunningQuery()
  {
    // TODO implement
  }
}

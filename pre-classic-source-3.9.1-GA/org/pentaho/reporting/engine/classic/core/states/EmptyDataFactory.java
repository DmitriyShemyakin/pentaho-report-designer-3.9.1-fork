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

package org.pentaho.reporting.engine.classic.core.states;

import javax.swing.table.TableModel;

import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.ResourceBundleFactory;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

/**
 * A datafactory that does not do any real work.
 *
 * @author Thomas Morgner
 */
public class EmptyDataFactory implements DataFactory
{
  private static final String[] EMPTY_NAMES = new String[0];

  public EmptyDataFactory()
  {
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
    throw new ReportDataFactoryException("This factory does not understand any of the queries.");
  }

  /**
   * Returns a copy of the data factory that is not affected by its anchestor and holds no connection to the anchestor
   * anymore. A data-factory will be derived at the beginning of the report processing.
   *
   * @return a copy of the data factory.
   */
  public DataFactory derive()
  {
    return this;
  }

  /**
   * Opens the data factory. This initializes everything. Performing queries on data factories which have not yet been
   * opened will result in exceptions.
   */
  public void open() throws ReportDataFactoryException
  {
    // no op.
  }

  /**
   * Closes the data factory and frees all resources held by this instance.
   */
  public void close()
  {
    // no op.
  }

  public void initialize(final Configuration configuration,
                         final ResourceManager resourceManager,
                         final ResourceKey contextKey,
                         final ResourceBundleFactory resourceBundleFactory)
  {

  }

  public Object clone()
  {
    try
    {
      return super.clone();
    }
    catch (CloneNotSupportedException e)
    {
      throw new IllegalStateException(e);
    }
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
    // none of the queries is executable here.
    return false;
  }


  public String[] getQueryNames()
  {
    return EMPTY_NAMES;
  }

  public void cancelRunningQuery()
  {
    // TODO implement
  }
}

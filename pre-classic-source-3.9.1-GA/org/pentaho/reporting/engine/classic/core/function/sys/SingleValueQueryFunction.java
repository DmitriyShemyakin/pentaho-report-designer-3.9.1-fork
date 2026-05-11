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

package org.pentaho.reporting.engine.classic.core.function.sys;

import javax.swing.table.TableModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.ParameterMapping;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.function.ColumnAggregationExpression;
import org.pentaho.reporting.engine.classic.core.function.Function;
import org.pentaho.reporting.engine.classic.core.states.QueryDataRowWrapper;
import org.pentaho.reporting.engine.classic.core.states.datarow.StaticDataRow;

/**
 * Fires a query against the data-source and returns a single value. The current data-row is used as source for the
 * parameters of the query. The parameters that should be used must be declared as list of fields.
 *
 * @author Thomas Morgner
 */
public class SingleValueQueryFunction extends ColumnAggregationExpression implements Function
{
  private static class QueryParametersDataRow extends StaticDataRow
  {
    private QueryParametersDataRow(final DataRow globalView,
                                   final ParameterMapping[] parameterMappings)
    {
      final String[] outerNames = new String[parameterMappings.length];
      final Object[] values = new Object[parameterMappings.length];
      for (int i = 0; i < parameterMappings.length; i++)
      {
        final ParameterMapping mapping = parameterMappings[i];
        final String name = mapping.getAlias();
        values[i] = globalView.get(name);
        outerNames[i] = mapping.getName();

      }
      setData(outerNames, values);
    }
  }

  private static final Log logger = LogFactory.getLog(SingleValueQueryFunction.class);
  /**
   * The last value queried by the function.
   */
  private transient Object value;
  /**
   * The name of the query that should be executed.
   */
  private String query;
  /**
   * The name of the column of the query's result-table that should be used. If undefined, the first column is used.
   */
  private String resultColumn;

  private int queryTimeout;

  /**
   * Default Constructor.
   */
  public SingleValueQueryFunction()
  {
  }

  public int getQueryTimeout()
  {
    return queryTimeout;
  }

  public void setQueryTimeout(final int queryTimeout)
  {
    this.queryTimeout = queryTimeout;
  }

  /**
   * Returns the name of the result-column. The result-column specified the name of the column of the query's
   * result-table that should be used. If undefined, the first column is used.
   *
   * @return the result column name.
   */
  public String getResultColumn()
  {
    return resultColumn;
  }

  /**
   * Defines the name of the result-column. The result-column specified the name of the column of the query's
   * result-table that should be used. If undefined, the first column is used.
   *
   * @param resultColumn the result column name.
   */
  public void setResultColumn(final String resultColumn)
  {
    this.resultColumn = resultColumn;
  }

  /**
   * Returns the query name.
   *
   * @return the query name.
   */
  public String getQuery()
  {
    return query;
  }

  /**
   * Defines the query name.
   *
   * @param query the query name.
   */
  public void setQuery(final String query)
  {
    this.query = query;
  }

  /**
   * Performs the query by collecting the data from the datarow and executing the query.
   *
   * @param event the report event that triggered the query call.
   */
  private void performQuery(final ReportEvent event)
  {
    if (query == null)
    {
      return;
    }
    value = null;
    try
    {
      final DataFactory dataFactory =
          event.getState().getFlowController().getDataFactory();
      final String[] fields = getField();
      final int length = fields.length;
      final ParameterMapping[] mappings = new ParameterMapping[length];
      for (int i = 0; i < length; i++)
      {
        mappings[i] = new ParameterMapping(fields[i], fields[i]);
      }

      final QueryParametersDataRow params = new QueryParametersDataRow(getDataRow(), mappings);
      final TableModel tableModel = dataFactory.queryData(query, new QueryDataRowWrapper(params, 1, queryTimeout));
      if (tableModel == null)
      {
        return;
      }
      final int columnCount = tableModel.getColumnCount();
      if (tableModel.getRowCount() == 0 || columnCount == 0)
      {
        return;
      }
      if (resultColumn == null)
      {
        value = tableModel.getValueAt(0, 0);
        return;
      }
      for (int i = 0; i < columnCount; i++)
      {
        if (resultColumn.equals(tableModel.getColumnName(i)))
        {
          value = tableModel.getValueAt(0, i);
          return;
        }
      }
      // do nothing ..
    }
    catch (Exception e)
    {
      SingleValueQueryFunction.logger.warn("SingleValueQueryFunction: Failed to perform query", e);
    }
  }

  /**
   * Calls the perform-query method.
   *
   * @param event the event.
   */
  public void reportInitialized(final ReportEvent event)
  {
    performQuery(event);
  }

  /**
   * Calls the perform-query method.
   *
   * @param event the event.
   */
  public void reportStarted(final ReportEvent event)
  {
    performQuery(event);
  }

  /**
   * Calls the perform-query method.
   *
   * @param event the event.
   */
  public void reportFinished(final ReportEvent event)
  {
    performQuery(event);
  }

  /**
   * Calls the perform-query method.
   *
   * @param event the event.
   */
  public void reportDone(final ReportEvent event)
  {
    performQuery(event);
  }

  /**
   * Calls the perform-query method.
   *
   * @param event the event.
   */
  public void groupStarted(final ReportEvent event)
  {
    performQuery(event);
  }

  /**
   * Calls the perform-query method.
   *
   * @param event the event.
   */
  public void groupFinished(final ReportEvent event)
  {
    performQuery(event);
  }

  /**
   * Calls the perform-query method.
   *
   * @param event the event.
   */
  public void itemsStarted(final ReportEvent event)
  {
    performQuery(event);
  }

  /**
   * Calls the perform-query method.
   *
   * @param event the event.
   */
  public void itemsFinished(final ReportEvent event)
  {
    performQuery(event);
  }

  /**
   * Calls the perform-query method.
   *
   * @param event the event.
   */
  public void itemsAdvanced(final ReportEvent event)
  {
    performQuery(event);
  }

  /**
   * Returns the query result.
   *
   * @return the query result.
   */
  public Object getValue()
  {
    return value;
  }
}

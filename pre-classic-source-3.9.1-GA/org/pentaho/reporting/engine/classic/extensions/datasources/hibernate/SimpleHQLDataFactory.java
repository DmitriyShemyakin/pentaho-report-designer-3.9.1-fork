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
 * Copyright (c) 2001 - 2009 Object Refinery Ltd, Pentaho Corporation and Contributors.  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.extensions.datasources.hibernate;

import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.ResourceBundleFactory;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

/**
 * Creation-Date: Jan 12, 2007, 5:41:18 PM
 *
 * @author Thomas Morgner
 */
public class SimpleHQLDataFactory implements DataFactory
{
  private transient Session session;
  private SessionProvider sessionProvider;

  public SimpleHQLDataFactory(final Session connection)
  {
    this(new StaticSessionProvider(connection));
  }

  public SimpleHQLDataFactory(final SessionProvider connectionProvider)
  {
    if (connectionProvider == null)
    {
      throw new NullPointerException();
    }
    this.sessionProvider = connectionProvider;
  }

  public SessionProvider getSessionProvider()
  {
    return sessionProvider;
  }

  private synchronized Session getSession() throws HibernateException
  {
    if (session == null)
    {
      session = sessionProvider.getSession();
    }
    return session;
  }

  public void initialize(final Configuration configuration,
                         final ResourceManager resourceManager,
                         final ResourceKey contextKey,
                         final ResourceBundleFactory resourceBundleFactory)
  {

  }

  /**
   * Queries a datasource. The string 'query' defines the name of the query. The Parameterset given here may contain
   * more data than actually needed.
   * <p/>
   * The dataset may change between two calls, do not assume anything!
   *
   * @param query
   * @param parameters
   * @return
   */
  public synchronized TableModel queryData(final String query, final DataRow parameters)
      throws ReportDataFactoryException
  {
    try
    {
      final Query pstmt = getSession().createQuery(query);
      final String[] params = pstmt.getNamedParameters();
      for (int i = 0; i < params.length; i++)
      {
        final String param = params[i];
        final Object pvalue = parameters.get(param);
        if (pvalue == null)
        {
          // this should work, but some driver are known to die here.
          // they should be fed with setNull(..) instead; something
          // we cant do as JDK1.2's JDBC does not define it.
          pstmt.setParameter(param, null);
        }
        else
        {
          pstmt.setParameter(param, pvalue);
        }
      }
      final Object queryLimit = parameters.get(DataFactory.QUERY_LIMIT);
      if (queryLimit instanceof Number)
      {
        final Number i = (Number) queryLimit;
        if (i.intValue() >= 0)
        {
          pstmt.setMaxResults(i.intValue());
        }
      }
      final Object queryTimeout = parameters.get(DataFactory.QUERY_TIMEOUT);
      if (queryTimeout instanceof Number)
      {
        final Number i = (Number) queryLimit;
        if (i.intValue() >= 0)
        {
          pstmt.setTimeout(i.intValue());
        }
      }
      final ScrollableResults res = pstmt.scroll(ScrollMode.FORWARD_ONLY);
      return generateDefaultTableModel(res, pstmt.getReturnAliases());
    }
    catch (Exception e)
    {
      throw new ReportDataFactoryException("Failed at query: " + query, e);
    }
  }


  /**
   * Generates a <code>TableModel</code> that gets its contents filled from a <code>ResultSet</code>. The column names
   * of the <code>ResultSet</code> will form the column names of the table model.
   * <p/>
   * Hint: To customize the names of the columns, use the SQL column aliasing (done with <code>SELECT nativecolumnname
   * AS "JavaColumnName" FROM ....</code>
   *
   * @param rs           the result set.
   * @param labelMapping defines, whether to use column names or column labels to compute the column index.
   * @return a closeable table model.
   * @throws SQLException if there is a problem with the result set.
   */
  public TableModel generateDefaultTableModel
  (final ScrollableResults rs, final String[] labelMapping)
      throws SQLException
  {
    final int colcount = labelMapping.length;

    final ArrayList rows = new ArrayList();
    while (rs.next())
    {
      final Object[] column = new Object[colcount];
      for (int i = 0; i < colcount; i++)
      {
        column[i] = rs.get(i);
      }
      rows.add(column);
    }

    final Object[] tempRows = rows.toArray();
    final Object[][] rowMap = new Object[tempRows.length][];
    for (int i = 0; i < tempRows.length; i++)
    {
      rowMap[i] = (Object[]) tempRows[i];
    }
    return new DefaultTableModel(rowMap, labelMapping);
  }

  public void open()
  {

  }

  public synchronized void close()
  {
    if (session == null)
    {
      return;
    }

    try
    {
      session.close();
    }
    catch (HibernateException e)
    {
      // we tried our very best ..
    }
    session = null;
  }

  /**
   * Derives a freshly initialized report data factory, which is independend of the original data factory. Opening or
   * Closing one data factory must not affect the other factories.
   *
   * @return
   */
  public DataFactory derive()
  {
    return (DataFactory) clone();
  }

  public Object clone()
  {
    try
    {
      final SimpleHQLDataFactory dataFactory = (SimpleHQLDataFactory) super.clone();
      dataFactory.session = null;
      return dataFactory;
    }
    catch (CloneNotSupportedException e)
    {
      // this should not happen ..
      throw new IllegalStateException("Clone failed?", e);
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
    return true;
  }

  public String[] getQueryNames()
  {
    return new String[0];
  }

  public void cancelRunningQuery()
  {
    // TODO implement
  }
}
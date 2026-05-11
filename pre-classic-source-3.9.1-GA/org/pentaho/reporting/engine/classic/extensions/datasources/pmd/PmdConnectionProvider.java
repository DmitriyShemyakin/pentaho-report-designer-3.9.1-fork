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
 * Copyright (c) 2009 Pentaho Corporation.  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.extensions.datasources.pmd;

import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;

import javax.swing.table.TableModel;

import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.metadata.model.Domain;
import org.pentaho.metadata.query.model.Query;
import org.pentaho.metadata.repository.IMetadataDomainRepository;
import org.pentaho.metadata.repository.InMemoryMetadataDomainRepository;
import org.pentaho.metadata.util.XmiParser;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql.DriverConnectionProvider;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql.JndiConnectionProvider;
import org.pentaho.reporting.libraries.resourceloader.ResourceData;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.pentaho.reporting.libraries.base.util.StringUtils;

public class PmdConnectionProvider implements IPmdConnectionProvider
{
  public PmdConnectionProvider()
  {
  }

  protected InputStream createStream(final ResourceManager manager,
                                   final ResourceKey contextKey,
                                   final String xmiFile) throws ResourceException
  {
    if (contextKey != null)
    {
      try
      {
        final ResourceKey resourceKey = manager.deriveKey(contextKey, xmiFile);
        final ResourceData data = manager.load(resourceKey);
        return data.getResourceAsStream(manager);
      }
      catch (ResourceException re)
      {
        // ignore, lets go on to the direct parsing as a local file
      }
    }

    final ResourceKey resourceKey = manager.createKey(new File(xmiFile));
    final ResourceData data = manager.load(resourceKey);
    return data.getResourceAsStream(manager);
  }

  public IMetadataDomainRepository getMetadataDomainRepository(final String domainId,
                                                               final ResourceManager resourceManager,
                                                               final ResourceKey contextKey,
                                                               final String xmiFile) throws ReportDataFactoryException
  {
    try
    {
      final InputStream stream = createStream(resourceManager, contextKey, xmiFile);
      try
      {
        final InMemoryMetadataDomainRepository repo = new InMemoryMetadataDomainRepository();
        final XmiParser parser = new XmiParser();
        final Domain domain = parser.parseXmi(stream);
        domain.setId(domainId);
        repo.storeDomain(domain, true);
        return repo;
      }
      finally
      {
        stream.close();
      }
    }
    catch (Exception e)
    {
      throw new ReportDataFactoryException("The Specified XMI File is invalid: " + xmiFile, e);
    }
  }

  public Connection createConnection(final DatabaseMeta databaseMeta,
                                     final String username,
                                     final String password) throws ReportDataFactoryException
  {
    final String realUser = (StringUtils.isEmpty(databaseMeta.getUsername())) ? username : databaseMeta.getUsername();
    final String realPassword = (StringUtils.isEmpty(databaseMeta.getPassword())) ? password : databaseMeta.getPassword();

    if (databaseMeta.getAccessType() == DatabaseMeta.TYPE_ACCESS_JNDI)
    {
      final String jndiName = databaseMeta.getDatabaseName();
      if (jndiName != null)
      {
        final JndiConnectionProvider connectionProvider = new JndiConnectionProvider();
        connectionProvider.setConnectionPath(jndiName);
        try
        {
          return connectionProvider.createConnection(realUser, realPassword);
        }
        catch (SQLException e)
        {
          throw new ReportDataFactoryException
              ("JNDI dataconnection was requested, but no connection could be established", e);
        }
      }
    }

    try
    {
      final String connectionInfo = databaseMeta.getURL();
      if (connectionInfo == null)
      {
        throw new ReportDataFactoryException("Unable to create a connection: DatabaseMeta does not contain any driver or connection info");
      }

       final String code = databaseMeta.getPluginId();
      final Map<String, String> map = databaseMeta.getExtraOptions();
      final Iterator<Map.Entry<String, String>> entryIterator = map.entrySet().iterator();

      final DriverConnectionProvider driverProvider = new DriverConnectionProvider();
      driverProvider.setDriver(databaseMeta.getDriverClass());
      driverProvider.setUrl(connectionInfo);
      while (entryIterator.hasNext())
      {
        final Map.Entry<String, String> entry = entryIterator.next();
        final String key = entry.getKey();
        final String realKey = key.substring(code.length() + 1);
        final String value = entry.getValue();
        if (DatabaseMeta.EMPTY_OPTIONS_STRING.equals(value))
        {
          driverProvider.setProperty(realKey, "");
        }
        else
        {
          driverProvider.setProperty(realKey, value);
        }
      }

      return driverProvider.createConnection(realUser, realPassword);
    }
    catch (Exception e)
    {
      throw new ReportDataFactoryException("Unable to create a connection", e);
    }
  }

  @Override
  public TableModel executeQuery(Query query, DataRow parameters)
      throws ReportDataFactoryException {
    throw new UnsupportedOperationException("The default PmdConnectionProvider does not yet implement alternative physical model execution engines.");
  }

}

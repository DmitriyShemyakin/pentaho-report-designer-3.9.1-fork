package org.pentaho.reporting.engine.classic.extensions.datasources.mondrian;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;
import javax.sql.DataSource;

import mondrian.olap.Connection;
import mondrian.olap.DriverManager;
import mondrian.olap.Util;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;

/**
 * Todo: Document me!
 * <p/>
 * Date: 15.04.2010
 * Time: 19:07:28
 *
 * @author Thomas Morgner.
 */
public class DefaultMondrianConnectionProvider implements MondrianConnectionProvider
{
  public DefaultMondrianConnectionProvider()
  {
  }

  protected String computeConnectionString(final Properties parameters)
  {
    final StringBuilder connectionStr = new StringBuilder(100);
    connectionStr.append("provider=mondrian");

    connectionStr.append("; ");
    connectionStr.append("Catalog=");
    connectionStr.append(parameters.getProperty("Catalog"));

    final Enumeration objectEnumeration = parameters.keys();
    while (objectEnumeration.hasMoreElements())
    {
      final String key = (String) objectEnumeration.nextElement();
      if ("Catalog".equals(key))
      {
        continue;
      }
      final Object value = parameters.getProperty(key);
      if (value != null)
      {
        connectionStr.append("; ");
        connectionStr.append(key);
        connectionStr.append("=");
        connectionStr.append(value);
      }
    }
    return connectionStr.toString();
  }

  public Connection createConnection(final Properties properties, final DataSource dataSource) throws ReportDataFactoryException
  {
    return DriverManager.getConnection(Util.parseConnectString(computeConnectionString(properties)), null, dataSource);
  }

  public Object getConnectionHash(final Properties properties) throws ReportDataFactoryException
  {
    final ArrayList<Object> list = new ArrayList<Object>();
    list.add(getClass().getName());
    list.add(properties.clone());
    return list;
  }
}

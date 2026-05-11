package org.pentaho.reporting.engine.classic.extensions.datasources.mondrian;

import java.io.Serializable;
import java.util.Properties;

import javax.sql.DataSource;

import mondrian.olap.Connection;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;

/**
 * Todo: Document me!
 * <p/>
 * Date: 15.04.2010
 * Time: 19:05:44
 *
 * @author Thomas Morgner.
 */
public interface MondrianConnectionProvider extends Serializable
{
  public Connection createConnection
      (final Properties properties, final DataSource dataSource) throws ReportDataFactoryException;

  Object getConnectionHash(final Properties properties) throws ReportDataFactoryException;
}

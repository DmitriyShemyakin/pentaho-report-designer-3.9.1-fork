package org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql;

import java.io.Serializable;
import java.sql.Connection;

import org.pentaho.reporting.engine.classic.core.DataRow;

/**
 * Todo: Document me!
 * <p/>
 * Date: 19.07.2010
 * Time: 18:55:53
 *
 * @author Thomas Morgner.
 */
public interface ParametrizationProvider extends Serializable
{
  public String rewriteQueryForParametrization(Connection connection, String query, DataRow parameters);
  public String[] getPreparedParameterNames();
}
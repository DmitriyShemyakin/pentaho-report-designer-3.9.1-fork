package org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql;

import java.io.Serializable;
import java.sql.Connection;

/**
 * Todo: Document me!
 * <p/>
 * Date: 19.07.2010
 * Time: 18:55:53
 *
 * @author Thomas Morgner.
 */
public interface ParametrizationProviderFactory extends Serializable
{
  public ParametrizationProvider create(Connection connection); 
}

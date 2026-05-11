package org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql;

import java.sql.Connection;

/**
 * Todo: Document me!
 * <p/>
 * Date: 20.07.2010
 * Time: 14:26:19
 *
 * @author Thomas Morgner.
 */
public class DefaultParametrizationProviderFactory implements ParametrizationProviderFactory
{
  public DefaultParametrizationProviderFactory()
  {
  }

  public ParametrizationProvider create(final Connection connection)
  {
    return new DefaultParametrizationProvider();
  }
}

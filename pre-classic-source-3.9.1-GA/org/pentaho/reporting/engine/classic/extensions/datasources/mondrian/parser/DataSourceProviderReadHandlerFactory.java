package org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.parser;

import java.util.Iterator;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractReadHandlerFactory;

/**
 * Todo: Document me!
 * <p/>
 * Date: 25.08.2009
 * Time: 10:07:21
 *
 * @author Thomas Morgner.
 */
public class DataSourceProviderReadHandlerFactory extends AbstractReadHandlerFactory
{
  private static final String PREFIX_SELECTOR =
      "org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.datasource-factory-prefix.";

  private static DataSourceProviderReadHandlerFactory readHandlerFactory;

  public DataSourceProviderReadHandlerFactory()
  {
  }

  protected Class getTargetClass()
  {
    return DataSourceProviderReadHandler.class;
  }

  public static synchronized DataSourceProviderReadHandlerFactory getInstance()
  {
    if (readHandlerFactory == null)
    {
      readHandlerFactory = new DataSourceProviderReadHandlerFactory();
      final Configuration config = ClassicEngineBoot.getInstance().getGlobalConfig();
      final Iterator propertyKeys = config.findPropertyKeys(PREFIX_SELECTOR);
      while (propertyKeys.hasNext())
      {
        final String key = (String) propertyKeys.next();
        final String value = config.getConfigProperty(key);
        if (value != null)
        {
          readHandlerFactory.configure(config, value);
        }
      }
    }
    return readHandlerFactory;
  }

}
package org.pentaho.reporting.engine.classic.extensions.drilldown.parser;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlResourceFactory;

/**
 * Todo: Document me!
 * <p/>
 * Date: 23.02.2010
 * Time: 13:50:36
 *
 * @author Thomas Morgner.
 */
public class DrillDownProfileResourceFactory extends AbstractXmlResourceFactory
{
  public DrillDownProfileResourceFactory()
  {
  }

  /**
   * Returns the configuration that should be used to initialize this factory.
   *
   * @return the configuration for initializing the factory.
   */
  protected Configuration getConfiguration()
  {
    return ClassicEngineBoot.getInstance().getGlobalConfig();
  }

  /**
   * Returns the expected result type.
   *
   * @return the result type.
   */
  public Class getFactoryType()
  {
    return DrillDownProfileCollection.class;
  }
}

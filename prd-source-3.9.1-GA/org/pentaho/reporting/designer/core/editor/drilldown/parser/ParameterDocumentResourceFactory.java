package org.pentaho.reporting.designer.core.editor.drilldown.parser;

import org.pentaho.reporting.designer.core.editor.drilldown.model.ParameterDocument;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlResourceFactory;

/**
 * Todo: Document me!
 * <p/>
 * Date: 13.08.2010
 * Time: 17:21:44
 *
 * @author Thomas Morgner.
 */
public class ParameterDocumentResourceFactory extends AbstractXmlResourceFactory
{
  public ParameterDocumentResourceFactory()
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
    return ParameterDocument.class;
  }
}

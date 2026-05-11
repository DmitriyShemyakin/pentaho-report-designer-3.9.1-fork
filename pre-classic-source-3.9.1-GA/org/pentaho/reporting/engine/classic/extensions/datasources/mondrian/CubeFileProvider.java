package org.pentaho.reporting.engine.classic.extensions.datasources.mondrian;

import java.io.Serializable;

import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;

/**
 * Todo: Document me!
 * <p/>
 * Date: 25.08.2009
 * Time: 10:21:53
 *
 * @author Thomas Morgner.
 */
public interface CubeFileProvider extends Serializable
{
  public String getDesignTimeFile();
  public String getCubeFile(final ResourceManager resourceManager, final ResourceKey contextKey) throws ReportDataFactoryException;

  public Object getConnectionHash();
}

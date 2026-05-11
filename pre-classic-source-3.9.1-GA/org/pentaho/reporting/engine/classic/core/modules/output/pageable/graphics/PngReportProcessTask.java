package org.pentaho.reporting.engine.classic.core.modules.output.pageable.graphics;

import org.pentaho.reporting.libraries.base.config.Configuration;

/**
 * Todo: Document me!
 * <p/>
 * Date: 16.02.11
 * Time: 14:12
 *
 * @author Thomas Morgner.
 */
public class PngReportProcessTask extends Graphics2DReportProcessTask
{
  public PngReportProcessTask()
  {
  }

  protected String computeMimeType(final Configuration configuration)
  {
    return "image/png";
  }
}

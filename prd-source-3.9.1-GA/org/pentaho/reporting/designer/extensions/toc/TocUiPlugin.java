package org.pentaho.reporting.designer.extensions.toc;

import org.pentaho.reporting.designer.core.AbstractReportDesignerUiPlugin;

/**
 * Todo: Document me!
 *
 * @author Thomas Morgner.
 */
public class TocUiPlugin extends AbstractReportDesignerUiPlugin
{
  public TocUiPlugin()
  {
  }

  public String[] getOverlaySources()
  {
    return new String[]{"org/pentaho/reporting/designer/extensions/toc/ui-overlay.xul"};
  }
}

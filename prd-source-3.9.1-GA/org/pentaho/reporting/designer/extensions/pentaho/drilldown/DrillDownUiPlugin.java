package org.pentaho.reporting.designer.extensions.pentaho.drilldown;

import org.pentaho.reporting.designer.core.AbstractReportDesignerUiPlugin;

/**
 * Todo: Document me!
 * <p/>
 * Date: 09.02.2010
 * Time: 18:19:06
 *
 * @author Thomas Morgner.
 */
public class DrillDownUiPlugin extends AbstractReportDesignerUiPlugin
{
  public DrillDownUiPlugin()
  {
  }

  public String[] getOverlaySources()
  {
    return new String[]{"org/pentaho/reporting/designer/extensions/pentaho/drilldown/ui-overlay.xul"};
  }

}

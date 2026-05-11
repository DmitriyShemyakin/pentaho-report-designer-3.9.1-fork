package org.pentaho.reporting.designer.extensions.pentaho.drilldown;

import org.pentaho.reporting.designer.core.editor.drilldown.basic.XulDrillDownUiProfile;
import org.pentaho.reporting.engine.classic.extensions.drilldown.DrillDownProfile;
import org.pentaho.reporting.engine.classic.extensions.drilldown.DrillDownProfileMetaData;

/**
 * Todo: Document me!
 * <p/>
 * Date: 05.08.2010
 * Time: 16:20:45
 *
 * @author Thomas Morgner.
 */
public class PentahoRemoteDrillDownUiProfile extends XulDrillDownUiProfile
{
  public PentahoRemoteDrillDownUiProfile()
  {
    final DrillDownProfile[] profiles = DrillDownProfileMetaData.getInstance().getDrillDownProfileByGroup("pentaho");
    final String[] profileNames = new String[profiles.length];
    for (int i = 0; i < profileNames.length; i++)
    {
      profileNames[i] = profiles[i].getName();
    }

    init(profileNames);
  }

  public int getOrderKey()
  {
    return 3000;
  }
}
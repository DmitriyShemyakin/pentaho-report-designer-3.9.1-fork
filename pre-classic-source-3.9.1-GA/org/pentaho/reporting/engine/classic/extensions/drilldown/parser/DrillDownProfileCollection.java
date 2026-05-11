package org.pentaho.reporting.engine.classic.extensions.drilldown.parser;

import java.io.Serializable;

import org.pentaho.reporting.engine.classic.extensions.drilldown.DrillDownProfile;

/**
 * Todo: Document me!
 * <p/>
 * Date: 23.02.2010
 * Time: 13:56:25
 *
 * @author Thomas Morgner.
 */
public class DrillDownProfileCollection implements Serializable
{
  private DrillDownProfile[] data;

  public DrillDownProfileCollection(final DrillDownProfile[] data)
  {
    this.data = data.clone();
  }

  public DrillDownProfile[] getData()
  {
    return data.clone();
  }
}

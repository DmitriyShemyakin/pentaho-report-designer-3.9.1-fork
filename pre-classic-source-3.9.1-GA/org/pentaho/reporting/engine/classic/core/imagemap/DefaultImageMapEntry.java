package org.pentaho.reporting.engine.classic.core.imagemap;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;

/**
 * Todo: Document me!
 * <p/>
 * Date: 26.02.2010
 * Time: 15:27:22
 *
 * @author Thomas Morgner.
 */
public class DefaultImageMapEntry extends AbstractImageMapEntry
{
  private static final float[] EMPTY_COORDS = new float[0];

  public DefaultImageMapEntry()
  {
  }

  public String getAreaType()
  {
    return "default";
  }

  public float[] getAreaCoordinates()
  {
    return EMPTY_COORDS;
  }

  public Shape getShape()
  {
    return new Rectangle2D.Double();
  }
}

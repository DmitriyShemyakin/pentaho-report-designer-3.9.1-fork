package org.pentaho.reporting.engine.classic.core.modules.output.table.base;

/**
* Todo: Document me!
* <p/>
* Date: 31.03.2010
* Time: 16:34:01
*
* @author Thomas Morgner.
*/
public class SheetLayoutTableCellDefinition
{
  protected static final int LINE_HINT_NONE = 0;
  protected static final int LINE_HINT_VERTICAL = 1;
  protected static final int LINE_HINT_HORIZONTAL = 2;

  private int lineType; // 0 none, 1 horizontal, 2 vertical
  private long coordinate;

  public SheetLayoutTableCellDefinition()
  {
    this.lineType = LINE_HINT_NONE;
  }

  public SheetLayoutTableCellDefinition(final int lineType, final long coordinate)
  {
    this.lineType = lineType;
    this.coordinate = coordinate;
  }

  public int getLineType()
  {
    return lineType;
  }

  public long getCoordinate()
  {
    return coordinate;
  }
}

package org.pentaho.reporting.engine.classic.core.modules.output.table.xls.helper;

import java.awt.Color;

/**
 * Todo: Document me!
 * <p/>
 * Date: 05.09.2009
 * Time: 15:41:37
 *
 * @author Thomas Morgner.
 */
public interface ExcelColorProducer
{
//  public HSSFColor getColor(final short index);

  /**
   * Returns the nearest indexed color for the palette (if palettes are used) or
   * -1 if no palette is used.
   *
   * @param awtColor
   * @return
   */
  public short getNearestColor(final Color awtColor);


}

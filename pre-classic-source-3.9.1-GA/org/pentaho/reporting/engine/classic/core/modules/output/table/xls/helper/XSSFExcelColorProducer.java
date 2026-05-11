package org.pentaho.reporting.engine.classic.core.modules.output.table.xls.helper;

import java.awt.Color;

import org.apache.poi.hssf.util.HSSFColor;

/**
 * Todo: Document me!
 * <p/>
 * Date: 25.08.2010
 * Time: 17:06:37
 *
 * @author Thomas Morgner.
 */
public class XSSFExcelColorProducer implements ExcelColorProducer
{
  public XSSFExcelColorProducer()
  {
  }

  public short getNearestColor(final Color awtColor)
  {
    return -1;
  }
}

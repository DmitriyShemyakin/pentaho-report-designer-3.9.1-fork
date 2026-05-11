/*
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2001 - 2009 Object Refinery Ltd, Pentaho Corporation and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.testcases.layout;

import org.pentaho.reporting.engine.classic.core.layout.output.FlowSelector;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.layout.output.LogicalPageKey;
import org.pentaho.reporting.engine.classic.core.layout.output.DisplayAllFlowSelector;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.AbstractTableOutputProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.SheetLayout;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.TableContentProducer;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.CellBackgroundProducer;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Creation-Date: 03.05.2007, 10:12:21
 *
 * @author Thomas Morgner
 */
public class DebugTableOutputProcessor extends AbstractTableOutputProcessor
{
  private static final Log logger = LogFactory.getLog(DebugTableOutputProcessor.class);

  private OutputProcessorMetaData metaData;
  private FlowSelector flowSelector;
  private CellBackgroundProducer cellBackgroundProducer;

  public DebugTableOutputProcessor(final Configuration configuration)
  {
    this.metaData = new DebugOutputProcessorMetaData(configuration);
    this.flowSelector = new DisplayAllFlowSelector();
    this.cellBackgroundProducer = new CellBackgroundProducer(true, true);
  }

  public FlowSelector getFlowSelector()
  {
    return flowSelector;
  }

  public void setFlowSelector(final FlowSelector flowSelector)
  {
    this.flowSelector = flowSelector;
  }

  public OutputProcessorMetaData getMetaData()
  {
    return metaData;
  }

  protected void processTableContent(final LogicalPageKey logicalPageKey,
                                     final LogicalPageBox logicalPage,
                                     final TableContentProducer contentProducer)
  {
   // if (true) return;

    final SheetLayout sheetLayout = contentProducer.getSheetLayout();
    // Lets print the sheet layout
    logger.debug("<table>");
    final int colCount = sheetLayout.getColumnCount();
    for (int col = 0; col < colCount; col++)
    {
      logger.debug("<col pos=" + sheetLayout.getXPosition(col) + " width=" + sheetLayout.getCellWidth(col) + " />");
    }

    final int rowCount = sheetLayout.getRowCount();
    for (int row = 0; row < rowCount; row++)
    {
      logger.debug ("<row pos=" +sheetLayout.getYPosition(row) + " height=" + sheetLayout.getRowHeight(row) + ">");
      for (int col = 0; col < colCount; col++)
      {
        logger.debug("  <cell>");

        final RenderBox content = contentProducer.getContent(row, col);
        if (content != null)
        {
          logger.debug("    <data name=\"" + content.getName() + "\">" + content + "</data>");
        }
        else
        {
          logger.debug("    <data />");
        }
        final int sectionType = contentProducer.getSectionType(row, col);
        final Object xx = cellBackgroundProducer.getBackgroundAt
            (logicalPage, sheetLayout, col, row, false, sectionType);
        logger.debug("    <background>" + xx + "</background>");
        logger.debug("  </cell>");
      }
      logger.debug ("</row>");
    }
    logger.debug("</table>");

  }
}

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
 * Copyright (c) 2007 - 2009 Pentaho Corporation, .  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.testcases.table;

import org.pentaho.reporting.engine.classic.testcases.table.model.ResultTable;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.output.AbstractOutputProcessor;
import org.pentaho.reporting.engine.classic.core.layout.output.ContentProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.output.LogicalPageKey;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.layout.ModelPrinter;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.TableLayoutProducer;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.SheetLayout;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.TableContentProducer;
import junit.framework.AssertionFailedError;

/**
 * Creation-Date: 21.08.2007, 20:50:27
 *
 * @author Thomas Morgner
 */
public class TableDebugOutputProcessor extends AbstractOutputProcessor
{
  private OutputProcessorMetaData metaData;
  private ResultTable resultTable;

  public TableDebugOutputProcessor(final OutputProcessorMetaData metaData)
  {
    this.metaData = metaData;
  }

  protected void processPaginationContent(final LogicalPageKey logicalPageKey, final LogicalPageBox logicalPage)
      throws ContentProcessingException
  {
    final TableLayoutProducer tableLayoutProducer = new TableLayoutProducer(metaData);
    tableLayoutProducer.update(logicalPage, false);
    final SheetLayout layout = tableLayoutProducer.getLayout();
    final TableContentProducer tcp = new TableContentProducer(layout, metaData);
    tcp.compute(logicalPage, false);
    try
    {
      // then add it to the layout-producer ..
      resultTable.validate(logicalPage, layout, tcp);
    }
    catch(AssertionFailedError afe)
    {
      ModelPrinter.print(logicalPage);
      SheetLayoutPrinter.print (logicalPage, layout, tcp);
      throw afe;
    }

  }

  protected void processPageContent(final LogicalPageKey logicalPageKey,
                                    final LogicalPageBox logicalPage)
      throws ContentProcessingException
  {
    throw new UnsupportedOperationException();
  }

  public OutputProcessorMetaData getMetaData()
  {
    return metaData;
  }

  public void setResultTable(final ResultTable resultTable)
  {
    this.resultTable = resultTable;
  }
}

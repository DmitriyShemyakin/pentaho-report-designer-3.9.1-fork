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

import org.pentaho.reporting.engine.classic.core.layout.AbstractRenderer;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.output.ContentProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.output.LayoutPagebreakHandler;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessor;
import org.pentaho.reporting.engine.classic.core.layout.process.FillPhysicalPagesStep;
import org.pentaho.reporting.engine.classic.core.layout.process.FlowPaginationStep;
import org.pentaho.reporting.engine.classic.core.layout.process.PaginationResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class exists only for driving the table-validation testcases. It is not a valid renderer
 * and exhibits major flaws if used elsewhere. You better do not use this outside the test-environment
 * or evil aliens will kidnap you an explain you in very detailed words what "Stay away from this class"
 * really means.
 *
 * @author Thomas Morgner
 */
public class TableDebugRenderer extends AbstractRenderer
{
  private static final Log logger = LogFactory.getLog(TableDebugRenderer.class);

  private FlowPaginationStep paginationStep;
  private int flowCount;
  private FillPhysicalPagesStep fillPhysicalPagesStep;

  public TableDebugRenderer(final TableDebugOutputProcessor outputProcessor)
  {
    super(outputProcessor);
    paginationStep = new FlowPaginationStep();
    fillPhysicalPagesStep = new FillPhysicalPagesStep();
  }

  protected boolean isPageFinished()
  {
    // dont care about that.
    return false;
  }

  protected void debugPrint(final LogicalPageBox pageBox)
  {
   // ModelPrinter.print(pageBox);
  }

  protected boolean performPagination(final LayoutPagebreakHandler handler,
                                      final boolean performOutput)
      throws ContentProcessingException
  {
    final OutputProcessor outputProcessor = getOutputProcessor();
    // next: perform pagination.
    final LogicalPageBox _pageBox = getPageBox();

    final LogicalPageBox clone = (LogicalPageBox) _pageBox.derive(true);
    final PaginationResult pageBreak = paginationStep.performPagebreak(clone);

    debugPrint(clone);
    setPagebreaks(getPagebreaks() + 1);
    clone.setAllVerticalBreaks(pageBreak.getAllBreaks());

    flowCount += 1;

    // A new page has been started. Recover the page-grid, then restart
    // everything from scratch. (We have to recompute, as the pages may
    // be different now, due to changed margins or page definitions)
    final long nextOffset = clone.computePageEnd();
    clone.setPageEnd(nextOffset);
    final long pageOffset = clone.getPageOffset();

    if (outputProcessor.isNeedAlignedPage())
    {
      final LogicalPageBox box = fillPhysicalPagesStep.compute(clone, pageOffset, nextOffset);
      logger.debug("Processing contents for Page " + flowCount + " Page-Offset: " + pageOffset + " -> " + nextOffset);

      outputProcessor.processContent(box);
    }
    else
    {
      logger.debug("Processing fast contents for Page " + flowCount + " Page-Offset: " + pageOffset + " -> " + nextOffset);
      outputProcessor.processContent(clone);
    }

    return false;
  }

  public int getPageCount()
  {
    return flowCount;
  }
}

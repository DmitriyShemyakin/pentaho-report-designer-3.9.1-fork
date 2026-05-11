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

package org.pentaho.reporting.engine.classic.core.states.process;

import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.RootLevelBand;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class BeginCrosstabFactHandler implements AdvanceHandler
{
  public static final AdvanceHandler HANDLER = new BeginCrosstabFactHandler();

  private BeginCrosstabFactHandler()
  {
  }

  public ProcessState advance(final ProcessState state) throws ReportProcessingException
  {
    final ProcessState next = state.deriveForAdvance();
    next.fireReportEvent();
    if (next.getNumberOfRows() == 0)
    {
      final RootLevelBand rootLevelBand = next.getReport().getNoDataBand();
      return InlineSubreportProcessor.processInline(next, rootLevelBand);
    }
    return next;
  }

  public ProcessState commit(final ProcessState next) throws ReportProcessingException
  {
    next.setInItemGroup(true);
    if (next.getNumberOfRows() > 0)
    {
      // this branch is only entered if we have data and the no-data band has *NOT* been printed.
      // in that case, the subreports of the no-databand will not be evaluated and so we can skip the
      // costly subreport steps ..
      next.setAdvanceHandler(ProcessCrosstabFactHandler.HANDLER);
      return next;
    }

    next.setAdvanceHandler(EndCrosstabFactHandler.HANDLER);

    final RootLevelBand rootLevelBand = next.getReport().getNoDataBand();
    return InlineSubreportProcessor.process(next, rootLevelBand);
  }

  public boolean isFinish()
  {
    return false;
  }

  public int getEventCode()
  {
    return ReportEvent.ITEMS_STARTED | ReportEvent.CROSSTABBING;
  }

  public boolean isRestoreHandler()
  {
    return false;
  }
}
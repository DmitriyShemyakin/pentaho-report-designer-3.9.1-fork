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

package org.pentaho.reporting.engine.classic.core.states;

import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.event.PageEventListener;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.function.OutputFunction;
import org.pentaho.reporting.engine.classic.core.function.StructureFunction;
import org.pentaho.reporting.engine.classic.core.states.datarow.InlineDataRowRuntime;

/**
 * Creation-Date: Dec 14, 2006, 5:05:39 PM
 *
 * @author Thomas Morgner
 */
public class InitialLayoutProcess implements LayoutProcess
{
  private static final StructureFunction[] EMPTY_FUNCTIONS = new StructureFunction[0];

  private InlineDataRowRuntime inlineDataRowRuntime;
  private OutputFunction outputFunction;
  private boolean outputFunctionIsPageListener;

  public InitialLayoutProcess(final OutputFunction outputFunction)
  {
    if (outputFunction == null)
    {
      throw new NullPointerException();
    }

    this.outputFunction = outputFunction;
    this.outputFunctionIsPageListener = (outputFunction instanceof PageEventListener);
  }

  public LayoutProcess getParent()
  {
    return null;
  }

  public boolean isPageListener()
  {
    return outputFunctionIsPageListener;
  }

  public OutputFunction getOutputFunction()
  {
    return outputFunction;
  }

  public void restart(final ReportState state) throws ReportProcessingException
  {
    if (inlineDataRowRuntime == null)
    {
      inlineDataRowRuntime = new InlineDataRowRuntime();
    }
    inlineDataRowRuntime.setState(state);

    final ExpressionRuntime oldRuntime;
    final OutputFunction outputFunction = getOutputFunction();
    if (outputFunction != null)
    {
      oldRuntime = outputFunction.getRuntime();
      outputFunction.setRuntime(inlineDataRowRuntime);
    }
    else
    {
      oldRuntime = null;
    }

    try
    {
      if (outputFunction != null)
      {
        outputFunction.restart(state);
      }
    }
    finally
    {
      if (outputFunction != null)
      {
        outputFunction.setRuntime(oldRuntime);
      }
    }
  }

  public StructureFunction[] getCollectionFunctions()
  {
    return EMPTY_FUNCTIONS;
  }

  public LayoutProcess deriveForStorage()
  {
    try
    {
      final InitialLayoutProcess lp = (InitialLayoutProcess) super.clone();
      lp.inlineDataRowRuntime = null;
      lp.outputFunction = outputFunction.deriveForStorage();
      return lp;
    }
    catch (final CloneNotSupportedException e)
    {
      throw new IllegalStateException();
    }
  }

  public LayoutProcess deriveForPagebreak()
  {
    try
    {
      final InitialLayoutProcess lp = (InitialLayoutProcess) super.clone();
      lp.inlineDataRowRuntime = null;
      lp.outputFunction = outputFunction.deriveForPagebreak();
      return lp;
    }
    catch (final CloneNotSupportedException e)
    {
      throw new IllegalStateException();
    }
  }

  public Object clone() throws CloneNotSupportedException
  {
    final InitialLayoutProcess lp = (InitialLayoutProcess) super.clone();
    lp.inlineDataRowRuntime = null;
    lp.outputFunction = (OutputFunction) outputFunction.clone();
    return lp;
  }

  public void fireReportEvent(final ReportEvent event)
  {
    final int type = event.getType();

    if (inlineDataRowRuntime == null)
    {
      inlineDataRowRuntime = new InlineDataRowRuntime();
    }
    inlineDataRowRuntime.setState(event.getState());

    final ExpressionRuntime oldRuntime;
    final OutputFunction outputFunction = getOutputFunction();
    if (outputFunction != null)
    {
      oldRuntime = outputFunction.getRuntime();
      outputFunction.setRuntime(inlineDataRowRuntime);
    }
    else
    {
      oldRuntime = null;
    }

    try
    {
      // first check the flagged events: Prepare, Page-Start, -end, cancel and rollback
      // before heading for the unflagged events ..

      if ((type & ReportEvent.PAGE_STARTED) == ReportEvent.PAGE_STARTED)
      {
        firePageStartedEvent(event);
      }
      else if ((type & ReportEvent.PAGE_FINISHED) == ReportEvent.PAGE_FINISHED)
      {
        firePageFinishedEvent(event);
      }
      else if ((type & ReportEvent.ITEMS_ADVANCED) == ReportEvent.ITEMS_ADVANCED)
      {
        fireItemsAdvancedEvent(event);
      }
      else if ((type & ReportEvent.ITEMS_FINISHED) == ReportEvent.ITEMS_FINISHED)
      {
        fireItemsFinishedEvent(event);
      }
      else if ((type & ReportEvent.ITEMS_STARTED) == ReportEvent.ITEMS_STARTED)
      {
        fireItemsStartedEvent(event);
      }
      else if ((type & ReportEvent.GROUP_FINISHED) == ReportEvent.GROUP_FINISHED)
      {
        fireGroupFinishedEvent(event);
      }
      else if ((type & ReportEvent.GROUP_STARTED) == ReportEvent.GROUP_STARTED)
      {
        fireGroupStartedEvent(event);
      }
      else if ((type & ReportEvent.REPORT_INITIALIZED) == ReportEvent.REPORT_INITIALIZED)
      {
        fireReportInitializedEvent(event);
      }
      else if ((type & ReportEvent.REPORT_DONE) == ReportEvent.REPORT_DONE)
      {
        fireReportDoneEvent(event);
      }
      else if ((type & ReportEvent.REPORT_FINISHED) == ReportEvent.REPORT_FINISHED)
      {
        fireReportFinishedEvent(event);
      }
      else if ((type & ReportEvent.REPORT_STARTED) == ReportEvent.REPORT_STARTED)
      {
        fireReportStartedEvent(event);
      }
      else
      {
        throw new IllegalArgumentException();
      }
    }
    finally
    {
      if (outputFunction != null)
      {
        outputFunction.setRuntime(oldRuntime);
      }
    }
  }

  private void fireItemsAdvancedEvent(final ReportEvent event)
  {
    final int activeLevel = event.getState().getLevel();
    if (activeLevel == LayoutProcess.LEVEL_PAGINATE)
    {
      outputFunction.itemsAdvanced(event);
    }
  }

  private void fireItemsStartedEvent(final ReportEvent event)
  {
    final int activeLevel = event.getState().getLevel();
    if (activeLevel == LayoutProcess.LEVEL_PAGINATE)
    {
      outputFunction.itemsStarted(event);
    }
  }

  private void fireItemsFinishedEvent(final ReportEvent event)
  {
    final int activeLevel = event.getState().getLevel();

    if (activeLevel == LayoutProcess.LEVEL_PAGINATE)
    {
      outputFunction.itemsFinished(event);
    }
  }

  private void fireGroupStartedEvent(final ReportEvent event)
  {
    final int activeLevel = event.getState().getLevel();
    if (activeLevel == LayoutProcess.LEVEL_PAGINATE)
    {
      outputFunction.groupStarted(event);
    }
  }

  private void fireGroupFinishedEvent(final ReportEvent event)
  {
    final int activeLevel = event.getState().getLevel();
    if (activeLevel == LayoutProcess.LEVEL_PAGINATE)
    {
      outputFunction.groupFinished(event);
    }
  }

  private void fireReportStartedEvent(final ReportEvent event)
  {
    final int activeLevel = event.getState().getLevel();
    if (activeLevel == LayoutProcess.LEVEL_PAGINATE)
    {
      outputFunction.reportStarted(event);
    }
  }

  private void fireReportDoneEvent(final ReportEvent event)
  {
    final int activeLevel = event.getState().getLevel();
    if (activeLevel == LayoutProcess.LEVEL_PAGINATE)
    {
      outputFunction.reportDone(event);
    }
  }

  private void fireReportFinishedEvent(final ReportEvent event)
  {
    final int activeLevel = event.getState().getLevel();
    if (activeLevel == LayoutProcess.LEVEL_PAGINATE)
    {
      outputFunction.reportFinished(event);
    }
  }

  private void fireReportInitializedEvent(final ReportEvent event)
  {
    final int activeLevel = event.getState().getLevel();
    if (activeLevel == LayoutProcess.LEVEL_PAGINATE)
    {
      outputFunction.reportInitialized(event);
    }
  }

  private void firePageStartedEvent(final ReportEvent event)
  {
    final int activeLevel = event.getState().getLevel();
    if (activeLevel == LayoutProcess.LEVEL_PAGINATE && outputFunctionIsPageListener)
    {
      final PageEventListener pel = (PageEventListener) outputFunction;
      pel.pageStarted(event);
    }
  }

  private void firePageFinishedEvent(final ReportEvent event)
  {
    final int activeLevel = event.getState().getLevel();
    if (activeLevel == LayoutProcess.LEVEL_PAGINATE && outputFunctionIsPageListener)
    {
      final PageEventListener pel = (PageEventListener) outputFunction;
      pel.pageFinished(event);
    }
  }
}

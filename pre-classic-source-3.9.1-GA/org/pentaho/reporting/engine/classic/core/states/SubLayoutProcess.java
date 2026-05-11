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

import org.pentaho.reporting.engine.classic.core.event.PageEventListener;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.function.OutputFunction;
import org.pentaho.reporting.engine.classic.core.function.StructureFunction;
import org.pentaho.reporting.engine.classic.core.states.datarow.InlineDataRowRuntime;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;

/**
 * Creation-Date: Dec 14, 2006, 5:05:39 PM
 *
 * @author Thomas Morgner
 */
public class SubLayoutProcess implements LayoutProcess
{
  private InlineDataRowRuntime inlineDataRowRuntime;
  private StructureFunction[] collectionFunctions;
  private boolean[] collectionFunctionIsPageListener;
  private boolean hasPageListener;
  private LayoutProcess parent;

  public SubLayoutProcess(final LayoutProcess parent,
                          final StructureFunction[] structureFunctions)
  {
    if (structureFunctions == null)
    {
      throw new NullPointerException();
    }
    if (parent == null)
    {
      throw new NullPointerException();
    }

    this.parent = parent;
    this.collectionFunctions = (StructureFunction[]) structureFunctions.clone();
    this.reinit();
  }

  public LayoutProcess getParent()
  {
    return parent;
  }

  private void reinit()
  {
    this.collectionFunctionIsPageListener = new boolean[collectionFunctions.length];
    if (parent != null)
    {
      this.hasPageListener = parent.isPageListener();
    }
    else
    {
      this.hasPageListener = false;
    }
    for (int i = 0; i < collectionFunctions.length; i++)
    {
      final StructureFunction fn = collectionFunctions[i];
      if (fn instanceof PageEventListener)
      {
        this.collectionFunctionIsPageListener[i] = true;
        this.hasPageListener = true;
      }
    }
  }

  public boolean isPageListener()
  {
    return hasPageListener;
  }

  public OutputFunction getOutputFunction()
  {
    return parent.getOutputFunction();
  }

  public void restart(final ReportState state) throws ReportProcessingException
  {
    parent.restart(state);
  }

  public StructureFunction[] getCollectionFunctions()
  {
    return (StructureFunction[]) collectionFunctions.clone();
  }

  public LayoutProcess deriveForStorage()
  {
    try
    {
      final SubLayoutProcess lp = (SubLayoutProcess) super.clone();
      if (parent != null)
      {
        lp.parent = parent.deriveForStorage();
      }
      lp.inlineDataRowRuntime = null;
      lp.collectionFunctions = (StructureFunction[]) collectionFunctions.clone();
      for (int i = 0; i < collectionFunctions.length; i++)
      {
        collectionFunctions[i] = (StructureFunction) collectionFunctions[i].clone();
      }
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
      final SubLayoutProcess lp = (SubLayoutProcess) super.clone();
      if (parent != null)
      {
        lp.parent = parent.deriveForPagebreak();
      }
      lp.inlineDataRowRuntime = null;
      lp.collectionFunctions = (StructureFunction[]) collectionFunctions.clone();
      lp.inlineDataRowRuntime = null;
      for (int i = 0; i < collectionFunctions.length; i++)
      {
        collectionFunctions[i] = (StructureFunction) collectionFunctions[i].clone();
      }
      return lp;
    }
    catch (final CloneNotSupportedException e)
    {
      throw new IllegalStateException();
    }
  }

  public Object clone() throws CloneNotSupportedException
  {
    final SubLayoutProcess lp = (SubLayoutProcess) super.clone();
    if (parent != null)
    {
      lp.parent = (LayoutProcess) parent.clone();
    }
    lp.inlineDataRowRuntime = null;
    lp.collectionFunctions = (StructureFunction[]) collectionFunctions.clone();
    lp.inlineDataRowRuntime = null;
    for (int i = 0; i < collectionFunctions.length; i++)
    {
      collectionFunctions[i] = (StructureFunction) collectionFunctions[i].clone();
    }
    return lp;
  }

  public void fireReportEvent(final ReportEvent event)
  {
    final int type = event.getType();

    final ExpressionRuntime[] oldRuntimes = new ExpressionRuntime[collectionFunctions.length];
    for (int i = 0; i < collectionFunctions.length; i++)
    {
      final StructureFunction function = collectionFunctions[i];
      oldRuntimes[i] = function.getRuntime();
    }

    if (inlineDataRowRuntime == null)
    {
      inlineDataRowRuntime = new InlineDataRowRuntime();
    }
    inlineDataRowRuntime.setState(event.getState());

    for (int i = 0; i < collectionFunctions.length; i++)
    {
      final StructureFunction function = collectionFunctions[i];
      function.setRuntime(inlineDataRowRuntime);
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
      for (int i = 0; i < collectionFunctions.length; i++)
      {
        final StructureFunction function = collectionFunctions[i];
        function.setRuntime(oldRuntimes[i]);
      }
    }

    if (parent != null)
    {
      parent.fireReportEvent(event);
    }
  }

  private void fireItemsAdvancedEvent(final ReportEvent event)
  {
    final int activeLevel = event.getState().getLevel();

    for (int i = 0; i < collectionFunctions.length; i++)
    {
      final StructureFunction function = collectionFunctions[i];
      if (activeLevel <= function.getDependencyLevel())
      {
        function.itemsAdvanced(event);
      }
    }
  }

  private void fireItemsStartedEvent(final ReportEvent event)
  {
    final int activeLevel = event.getState().getLevel();

    for (int i = 0; i < collectionFunctions.length; i++)
    {
      final StructureFunction function = collectionFunctions[i];
      if (activeLevel <= function.getDependencyLevel())
      {
        function.itemsStarted(event);
      }
    }
  }

  private void fireItemsFinishedEvent(final ReportEvent event)
  {
    final int activeLevel = event.getState().getLevel();

    for (int i = 0; i < collectionFunctions.length; i++)
    {
      final StructureFunction function = collectionFunctions[i];
      if (activeLevel <= function.getDependencyLevel())
      {
        function.itemsFinished(event);
      }
    }
  }

  private void fireGroupStartedEvent(final ReportEvent event)
  {
    final int activeLevel = event.getState().getLevel();

    for (int i = 0; i < collectionFunctions.length; i++)
    {
      final StructureFunction function = collectionFunctions[i];
      if (activeLevel <= function.getDependencyLevel())
      {
        function.groupStarted(event);
      }
    }
  }

  private void fireGroupFinishedEvent(final ReportEvent event)
  {
    final int activeLevel = event.getState().getLevel();

    for (int i = 0; i < collectionFunctions.length; i++)
    {
      final StructureFunction function = collectionFunctions[i];
      if (activeLevel <= function.getDependencyLevel())
      {
        function.groupFinished(event);
      }
    }
  }

  private void fireReportStartedEvent(final ReportEvent event)
  {
    final int activeLevel = event.getState().getLevel();

    for (int i = 0; i < collectionFunctions.length; i++)
    {
      final StructureFunction function = collectionFunctions[i];
      if (activeLevel <= function.getDependencyLevel())
      {
        function.reportStarted(event);
      }
    }
  }

  private void fireReportDoneEvent(final ReportEvent event)
  {
    final int activeLevel = event.getState().getLevel();

    for (int i = 0; i < collectionFunctions.length; i++)
    {
      final StructureFunction function = collectionFunctions[i];
      if (activeLevel <= function.getDependencyLevel())
      {
        function.reportDone(event);
      }
    }
  }

  private void fireReportFinishedEvent(final ReportEvent event)
  {
    final int activeLevel = event.getState().getLevel();

    for (int i = 0; i < collectionFunctions.length; i++)
    {
      final StructureFunction function = collectionFunctions[i];
      if (activeLevel <= function.getDependencyLevel())
      {
        function.reportFinished(event);
      }
    }
  }

  private void fireReportInitializedEvent(final ReportEvent event)
  {
    final int activeLevel = event.getState().getLevel();

    for (int i = 0; i < collectionFunctions.length; i++)
    {
      final StructureFunction function = collectionFunctions[i];
      if (activeLevel <= function.getDependencyLevel())
      {
        function.reportInitialized(event);
      }
    }
  }

  private void firePageStartedEvent(final ReportEvent event)
  {
    final int activeLevel = event.getState().getLevel();

    for (int i = 0; i < collectionFunctions.length; i++)
    {
      final StructureFunction function = collectionFunctions[i];
      if (activeLevel <= function.getDependencyLevel())
      {
        if (collectionFunctionIsPageListener[i])
        {
          final PageEventListener pel = (PageEventListener) function;
          pel.pageStarted(event);
        }
      }
    }
  }

  private void firePageFinishedEvent(final ReportEvent event)
  {
    final int activeLevel = event.getState().getLevel();

    for (int i = 0; i < collectionFunctions.length; i++)
    {
      final StructureFunction function = collectionFunctions[i];
      if (activeLevel <= function.getDependencyLevel())
      {
        if (collectionFunctionIsPageListener[i])
        {
          final PageEventListener pel = (PageEventListener) function;
          pel.pageFinished(event);
        }
      }
    }
  }
}
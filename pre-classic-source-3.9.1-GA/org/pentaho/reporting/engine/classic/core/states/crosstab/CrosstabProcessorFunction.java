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

package org.pentaho.reporting.engine.classic.core.states.crosstab;

import java.util.ArrayList;
import java.util.HashMap;

import org.pentaho.reporting.engine.classic.core.CrosstabColumnGroup;
import org.pentaho.reporting.engine.classic.core.CrosstabColumnGroupBody;
import org.pentaho.reporting.engine.classic.core.CrosstabGroup;
import org.pentaho.reporting.engine.classic.core.CrosstabOtherGroup;
import org.pentaho.reporting.engine.classic.core.CrosstabOtherGroupBody;
import org.pentaho.reporting.engine.classic.core.CrosstabRowGroup;
import org.pentaho.reporting.engine.classic.core.CrosstabRowGroupBody;
import org.pentaho.reporting.engine.classic.core.Group;
import org.pentaho.reporting.engine.classic.core.GroupBody;
import org.pentaho.reporting.engine.classic.core.MetaAttributeNames;
import org.pentaho.reporting.engine.classic.core.SubGroupBody;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.function.AbstractFunction;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.function.StructureFunction;
import org.pentaho.reporting.engine.classic.core.states.ReportState;
import org.pentaho.reporting.engine.classic.core.states.ReportStateKey;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributeContext;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributes;
import org.pentaho.reporting.engine.classic.core.wizard.DataSchema;
import org.pentaho.reporting.engine.classic.core.wizard.DefaultDataAttributeContext;
import org.pentaho.reporting.libraries.base.util.FastStack;

/**
 * Computes the column-axis values for all crosstabs in the current report and all of its subreports.
 *
 * @author Thomas Morgner
 */
public class CrosstabProcessorFunction extends AbstractFunction implements StructureFunction
{
  private FastStack processingStack;
  private HashMap results;
  private boolean resultsFinished;

  public CrosstabProcessorFunction()
  {
  }

  public int getProcessingPriority()
  {
    return Short.MIN_VALUE;
  }

  /**
   * Receives notification that the report has started.
   *
   * @param event the event.
   */
  public void reportStarted(final ReportEvent event)
  {
    if (event.getState().isSubReportEvent())
    {
      if (processingStack == null || processingStack.isEmpty())
      {
        return;
      }
      processingStack.push(null);
    }
  }

  /**
   * Receives notification that report generation has completed, the report footer was printed, no more output is done.
   * This is a helper event to shut down the output service.
   *
   * @param event The event.
   */
  public void reportDone(final ReportEvent event)
  {
    if (event.getState().isSubReportEvent())
    {
      if (processingStack == null || processingStack.isEmpty())
      {
        return;
      }

      final Object o = processingStack.pop();
      if (o != null)
      {
        throw new IllegalStateException("Expected to find 'start-subreport' marker, but got " + o + " instead.");
      }
    }
    else
    {
      // we completely processed the report for the very first time ...
      resultsFinished = true;
    }
  }

  /**
   * Receives notification that a group has started.
   *
   * @param event the event.
   */
  public void groupStarted(final ReportEvent event)
  {
    final ReportState state = event.getState();
    if (event.getLevel() == getDependencyLevel())
    {
      final Group group = event.getReport().getGroup(state.getCurrentGroupIndex());
      if (group instanceof CrosstabGroup)
      {
        final CrosstabGroup crosstabGroup = (CrosstabGroup) group;
        // yeay! we encountered a crosstab.
        if (processingStack == null)
        {
          processingStack = new FastStack();
        }
        final String[] columnSet = computeColumns(crosstabGroup);
        final ReportStateKey processKey = state.getProcessKey();
        final DataSchema dataSchema = getRuntime().getDataSchema();
        final DataAttributes tableAttributes = dataSchema.getTableAttributes();
        final DataAttributeContext context = new DefaultDataAttributeContext
            (getRuntime().getProcessingContext().getOutputProcessorMetaData(),
                getRuntime().getResourceBundleFactory().getLocale());

        final String mode = (String) tableAttributes.getMetaAttribute
            (MetaAttributeNames.Core.NAMESPACE, MetaAttributeNames.Core.CROSSTAB_MODE, String.class, context);
        if ("normalized".equals(mode))
        {
          processingStack.push(new OrderedMergeCrosstabSpecification(processKey, columnSet));
        }
        else
        {
          processingStack.push(new SortedMergeCrosstabSpecification(processKey, columnSet));
        }
        return;
      }

      if (processingStack == null || processingStack.isEmpty())
      {
        return;
      }

      final CrosstabSpecification csstate = (CrosstabSpecification) processingStack.peek();
      if (csstate == null)
      {
        return;
      }

      if (group instanceof CrosstabRowGroup)
      {
        if (group.getBody() instanceof CrosstabColumnGroupBody)
        {
          csstate.startRow();
        }
      }

      return;
    }

    if (results == null || processingStack == null || processingStack.isEmpty())
    {
      return;
    }

    // recall ..
    final Group group = event.getReport().getGroup(state.getCurrentGroupIndex());
    if (group instanceof CrosstabGroup)
    {
      final ReportStateKey processKey = state.getProcessKey();
      final CrosstabSpecification o = (CrosstabSpecification) results.get(processKey);
      if (o == null)
      {
        throw new IllegalStateException("Expected crosstab result, but got nothing at all");
      }
      processingStack.push(o);
    }
  }

  /**
   * Receives notification that a group has finished.
   *
   * @param event the event.
   */
  public void groupFinished(final ReportEvent event)
  {
    if (processingStack == null || processingStack.isEmpty())
    {
      return;
    }

    final CrosstabSpecification csstate = (CrosstabSpecification) processingStack.peek();
    if (csstate == null)
    {
      return;
    }

    final ReportState state = event.getState();
    if (event.getLevel() == getDependencyLevel())
    {
      final Group group = event.getReport().getGroup(state.getCurrentGroupIndex());
      if (group instanceof CrosstabGroup)
      {
        final CrosstabSpecification cs = (CrosstabSpecification) processingStack.pop();
        if (results == null)
        {
          results = new HashMap();
        }
        results.put(cs.getKey(), cs);
        return;
      }

      if (group instanceof CrosstabRowGroup)
      {
        if (group.getBody() instanceof CrosstabColumnGroupBody)
        {
          csstate.endRow();
        }
      }
      return;
    }

    if (results == null)
    {
      return;
    }

    final Group group = event.getReport().getGroup(state.getCurrentGroupIndex());
    if (group instanceof CrosstabGroup)
    {
      processingStack.pop();
    }
  }

  private String[] computeColumns(final CrosstabGroup crosstabGroup)
  {
    final ArrayList list = new ArrayList();
    GroupBody body = crosstabGroup.getBody();
    while (body != null)
    {
      if (body instanceof SubGroupBody)
      {
        final SubGroupBody sgBody = (SubGroupBody) body;
        final Group g = sgBody.getGroup();
        body = g.getBody();
        continue;
      }

      if (body instanceof CrosstabOtherGroupBody)
      {
        final CrosstabOtherGroupBody cogb = (CrosstabOtherGroupBody) body;
        final CrosstabOtherGroup otherGroup = cogb.getGroup();
        if (otherGroup.getField() != null)
        {
          list.add(otherGroup.getField());
        }
        body = otherGroup.getBody();
        continue;
      }

      if (body instanceof CrosstabRowGroupBody)
      {
        final CrosstabRowGroupBody cogb = (CrosstabRowGroupBody) body;
        final CrosstabRowGroup otherGroup = cogb.getGroup();
        if (otherGroup.getField() != null)
        {
          list.add(otherGroup.getField());
        }
        body = otherGroup.getBody();
        continue;
      }

      if (body instanceof CrosstabColumnGroupBody)
      {
        final CrosstabColumnGroupBody cogb = (CrosstabColumnGroupBody) body;
        final CrosstabColumnGroup otherGroup = cogb.getGroup();
        if (otherGroup.getField() != null)
        {
          list.add(otherGroup.getField());
        }
        body = otherGroup.getBody();
        continue;
      }

      break;
    }
    return (String[]) list.toArray(new String[list.size()]);
  }

  /**
   * Receives notification that a row of data is being processed.
   *
   * @param event the event.
   */
  public void itemsAdvanced(final ReportEvent event)
  {
    if (event.getLevel() == getDependencyLevel())
    {
      if (processingStack == null || processingStack.isEmpty())
      {
        return;
      }
      final CrosstabSpecification state = (CrosstabSpecification) processingStack.peek();
      if (state == null)
      {
        return;
      }
      state.add(getDataRow());
    }
  }

  /**
   * Return the current expression value.
   * <p/>
   * The value depends (obviously) on the expression implementation.
   *
   * @return the value of the function.
   */
  public Object getValue()
  {
    if (processingStack == null || processingStack.isEmpty())
    {
      return null;
    }
    if (resultsFinished == false)
    {
      return null;
    }
    return processingStack.peek();
  }

  /**
   * Checks whether this expression is a deep-traversing expression. Deep-traversing expressions receive events from all
   * sub-reports. This returns false by default, as ordinary expressions have no need to be deep-traversing.
   *
   * @return false.
   */
  public boolean isDeepTraversing()
  {
    return false;
  }

  /**
   * Returns the dependency level for the expression (controls evaluation order for expressions and functions).
   *
   * @return the level.
   */
  public int getDependencyLevel()
  {
    return Integer.MAX_VALUE;
  }

  /**
   * Clones the expression.  The expression should be reinitialized after the cloning. <P> Expressions maintain no
   * state, cloning is done at the beginning of the report processing to disconnect the expression from any other object
   * space.
   *
   * @return a clone of this expression.
   * @throws CloneNotSupportedException this should never happen.
   */
  public Object clone() throws CloneNotSupportedException
  {
    final CrosstabProcessorFunction cps = (CrosstabProcessorFunction) super.clone();
    if (processingStack == null || processingStack.isEmpty())
    {
      return cps;
    }
    cps.processingStack = (FastStack) processingStack.clone();
    return cps;
  }

  /**
   * Return a completly separated copy of this function. The copy does no longer share any changeable objects with the
   * original function.
   *
   * @return a copy of this function.
   */
  public Expression getInstance()
  {
    final CrosstabProcessorFunction cps = (CrosstabProcessorFunction) super.getInstance();
    cps.resultsFinished = false;
    cps.results = null;
    cps.processingStack = null;
    return cps;

  }
}

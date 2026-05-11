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

package org.pentaho.reporting.engine.classic.core.function;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;

import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.states.ReportStateKey;

/**
 * A report function that stores the result of a calculation for a group or the complete report. This function can be
 * used to convert simple running-functions into total-functions by wrapping them up. The wrapped up function will be
 * evaluated as usual and the result at the end of the report and/or end of the group will be stored in the
 * TotalCalculationFunction.
 *
 * @author Thomas Morgner
 */
public class TotalCalculationFunction extends AbstractFunction
{
  /**
   * A map of results, keyed by the process-key.
   */
  private transient HashMap results;

  /**
   * The field that should be evaluated.
   */
  private String field;
  /**
   * The name of the group on which to reset.
   */
  private String group;

  /**
   * The current result.
   */
  private transient Object result;
  /**
   * The global state key is used to store the result for the whole report.
   */
  private transient ReportStateKey globalStateKey;
  /**
   * The current group key is used to store the result for the current group.
   */
  private transient ReportStateKey groupStateKey;

  /**
   * Constructs a new function. <P> Initially the function has no name...be sure to assign one before using the
   * function.
   */
  public TotalCalculationFunction()
  {
    results = new HashMap();
  }

  /**
   * Receives notification that the report has started.
   *
   * @param event the event.
   */
  public void reportInitialized(final ReportEvent event)
  {
    globalStateKey = event.getState().getProcessKey();
    if (FunctionUtilities.isDefinedPrepareRunLevel(this, event))
    {
      results.clear();
      result = getDataRow().get(getField());
      results.put(globalStateKey, result);
    }
    else
    {
      result = results.get(globalStateKey);
    }
  }

  /**
   * Receives notification that a group has started.
   *
   * @param event the event.
   */
  public void groupStarted(final ReportEvent event)
  {
    if (FunctionUtilities.isDefinedGroup(getGroup(), event) == false)
    {
      // wrong group ...
      return;
    }

    groupStateKey = event.getState().getProcessKey();
    if (FunctionUtilities.isDefinedPrepareRunLevel(this, event))
    {
      result = getDataRow().get(getField());
      results.put(globalStateKey, result);
      results.put(groupStateKey, result);
    }
    else
    {
      // Activate the current group, which was filled in the prepare run.
      result = results.get(groupStateKey);
    }
  }


  /**
   * Receives notification that a group has finished.
   *
   * @param event the event.
   */
  public void groupFinished(final ReportEvent event)
  {
    if (FunctionUtilities.isDefinedGroup(getGroup(), event) == false)
    {
      // wrong group ...
      return;
    }

    if (FunctionUtilities.isDefinedPrepareRunLevel(this, event))
    {
      result = getDataRow().get(getField());
      results.put(globalStateKey, result);
      if (groupStateKey != null)
      {
        results.put(groupStateKey, result);
      }
    }
  }

  /**
   * Receives notification that the report has finished.
   *
   * @param event the event.
   */
  public void reportFinished(final ReportEvent event)
  {
    if (FunctionUtilities.isDefinedPrepareRunLevel(this, event))
    {
      result = getDataRow().get(getField());
      results.put(globalStateKey, result);
      if (groupStateKey != null)
      {
        results.put(groupStateKey, result);
      }
    }
  }

  /**
   * Returns the name of the group to be totalled.
   *
   * @return the group name.
   */
  public String getGroup()
  {
    return group;
  }

  /**
   * Defines the name of the group to be totalled. If the name is null, all groups are totalled.
   *
   * @param group the group name.
   */
  public void setGroup(final String group)
  {
    this.group = group;
  }

  /**
   * Returns the field used by the function. The field name corresponds to a column name in the report's data-row.
   *
   * @return The field name.
   */
  public String getField()
  {
    return field;
  }

  /**
   * Sets the field name for the function. The field name corresponds to a column name in the report's data-row.
   *
   * @param field the field name.
   */
  public void setField(final String field)
  {
    this.field = field;
  }

  /**
   * Return the current expression value. <P> The value depends (obviously) on the expression implementation.
   *
   * @return the value of the function.
   */
  public Object getValue()
  {
    return result;
  }

  /**
   * Helper method for serialization.
   *
   * @param in the input stream from where to read the serialized object.
   * @throws java.io.IOException    when reading the stream fails.
   * @throws ClassNotFoundException if a class definition for a serialized object could not be found.
   */
  private void readObject(final ObjectInputStream in)
      throws IOException, ClassNotFoundException
  {
    in.defaultReadObject();
    results = new HashMap();
  }

  public Expression getInstance()
  {
    final TotalCalculationFunction fn =
        (TotalCalculationFunction) super.getInstance();
    fn.results = new HashMap();
    return fn;
  }
}

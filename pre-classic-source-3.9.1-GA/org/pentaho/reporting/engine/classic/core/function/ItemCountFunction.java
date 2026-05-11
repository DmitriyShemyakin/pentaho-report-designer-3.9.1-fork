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

import org.pentaho.reporting.engine.classic.core.Group;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.states.ReportState;
import org.pentaho.reporting.engine.classic.core.util.IntegerCache;

/**
 * A report function that counts items in a report.  If the "group" property is set, the item count is reset to zero
 * whenever the group changes.
 *
 * @author Thomas Morgner
 */
public class ItemCountFunction extends AbstractFunction implements AggregationFunction
{
  /**
   * The item count.
   */
  private transient int count;

  /**
   * The name of the group on which to reset the count. This can be set to null to compute the count for the whole
   * report.
   */
  private String group;

  /**
   * Constructs an unnamed function. <P> This constructor is intended for use by the SAX handler class only.
   */
  public ItemCountFunction()
  {
  }

  /**
   * Constructs an item count report function.
   *
   * @param name The name of the function.
   * @throws NullPointerException if the name is null
   */
  public ItemCountFunction(final String name)
  {
    setName(name);
  }

  /**
   * Returns the current count value.
   *
   * @return the current count value.
   */
  protected int getCount()
  {
    return count;
  }

  /**
   * Defines the current count value.
   *
   * @param count the current count value.
   */
  protected void setCount(final int count)
  {
    this.count = count;
  }

  /**
   * Receives notification that a new report is about to start.  The item count is set to zero.
   *
   * @param event the event.
   */
  public void reportInitialized(final ReportEvent event)
  {
    setCount(0);
  }

  /**
   * Returns the name of the group (possibly null) for this function.  The item count is reset to zero at the start of
   * each instance of this group.
   *
   * @return the group name.
   */
  public String getGroup()
  {
    return group;
  }

  /**
   * Setss the name of the group for this function.  The item count is reset to zero at the start of each instance of
   * this group.  If the name is null, all items in the report are counted.
   *
   * @param group The group name.
   */
  public void setGroup(final String group)
  {
    this.group = group;
  }

  /**
   * Receives notification that a new group is about to start.  Checks to see if the group that is starting is the same
   * as the group defined for this function...if so, the item count is reset to zero.
   *
   * @param event Information about the event.
   */
  public void groupStarted(final ReportEvent event)
  {
    if (getGroup() == null)
    {
      return;
    }

    final ReportState state = event.getState();
    final Group group = event.getReport().getGroup(state.getCurrentGroupIndex());
    if (getGroup().equals(group.getName()))
    {
      setCount(0);
    }
  }

  /**
   * Received notification of a move to the next row of data.  Increments the item count.
   *
   * @param event Information about the event.
   */
  public void itemsAdvanced(final ReportEvent event)
  {
    setCount(getCount() + 1);
  }

  /**
   * Returns the number of items counted (so far) by the function.  This is either the number of items in the report, or
   * the group (if a group has been defined for the function).
   *
   * @return The item count.
   */
  public Object getValue()
  {
    return IntegerCache.getInteger(getCount());
  }
}

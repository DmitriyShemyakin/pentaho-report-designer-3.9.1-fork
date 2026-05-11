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

import java.math.BigDecimal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.Group;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;

/**
 * A report function that calculates the average of one field (column) from the TableModel. This function produces a
 * running total, no global total. The function can be used in two ways: <ul> <li>to calculate an average value for the
 * entire report;</li> <li>to calculate an average value within a particular group;</li> </ul> This function expects its
 * input values to be either java.lang.Number instances or Strings that can be parsed to java.lang.Number instances
 * using a java.text.DecimalFormat.
 * <p/>
 * The function undestands two parameters, the <code>field</code> parameter is required and denotes the name of an
 * ItemBand-field which gets summed up.
 * <p/>
 * The parameter <code>group</code> denotes the name of a group. When this group is started, the counter gets reseted to
 * null.
 *
 * @author Thomas Morgner
 */
public class ItemAvgFunction extends AbstractFunction implements FieldAggregationFunction
{
  private static final Log logger = LogFactory.getLog(ItemAvgFunction.class);
  /**
   * Useful constant for zero.
   */
  private static final BigDecimal ZERO = new BigDecimal(0.0);

  /**
   * Useful constant for one.
   */
  private static final BigDecimal ONE = new BigDecimal(1.0);

  /**
   * The item sum.
   */
  private transient BigDecimal sum;

  /**
   * The item count.
   */
  private transient BigDecimal itemCount;

  /**
   * The name of the group on which to reset the count. This can be set to null to compute the average for the whole
   * report.
   */
  private String group;
  /**
   * The name of the field from where to read the values.
   */
  private String field;
  /**
   * The scale-property defines the precission of the divide-operation.
   */
  private int scale;
  /**
   * The rounding-property defines the precission of the divide-operation.
   */
  private int roundingMode;

  /**
   * Constructs an unnamed function. Make sure to set a Name or function initialisation will fail.
   */
  public ItemAvgFunction()
  {
    sum = ItemAvgFunction.ZERO;
    itemCount = ItemAvgFunction.ZERO;
    scale = 14;
    roundingMode = BigDecimal.ROUND_HALF_UP;
  }

  /**
   * Constructs a named function. <P> The field must be defined before using the function.
   *
   * @param name The function name.
   */
  public ItemAvgFunction(final String name)
  {
    this();
    setName(name);
  }

  /**
   * Receives notification that a new report is about to start. <P> Does nothing.
   *
   * @param event Information about the event.
   */
  public void reportInitialized(final ReportEvent event)
  {
    this.sum = ItemAvgFunction.ZERO;
    itemCount = ItemAvgFunction.ZERO;
  }

  /**
   * Receives notification that a new group is about to start.  If this is the group defined for the function, then the
   * running total is reset to zero.
   *
   * @param event Information about the event.
   */
  public void groupStarted(final ReportEvent event)
  {
    final String mygroup = getGroup();
    if (mygroup == null)
    {
      return;
    }

    final Group group = event.getReport().getGroup(event.getState().getCurrentGroupIndex());
    if (getGroup().equals(group.getName()))
    {
      this.sum = ItemAvgFunction.ZERO;
      itemCount = ItemAvgFunction.ZERO;
    }
  }

  /**
   * Returns the group name.
   *
   * @return The group name.
   */
  public String getGroup()
  {
    return group;
  }

  /**
   * Sets the group name. <P> If a group is defined, the functions value is reset to zero at the start of every instance
   * of this group.
   *
   * @param name The group name (null permitted).
   */
  public void setGroup(final String name)
  {
    this.group = name;
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
   * Returns the defined rounding mode. This influences the precision of the divide-operation.
   *
   * @return the rounding mode.
   * @see java.math.BigDecimal#divide(java.math.BigDecimal,int)
   */
  public int getRoundingMode()
  {
    return roundingMode;
  }

  /**
   * Defines the rounding mode. This influences the precision of the divide-operation.
   *
   * @param roundingMode the rounding mode.
   * @see java.math.BigDecimal#divide(java.math.BigDecimal,int)
   */
  public void setRoundingMode(final int roundingMode)
  {
    this.roundingMode = roundingMode;
  }

  /**
   * Returns the scale for the divide-operation. The scale influences the precision of the division.
   *
   * @return the scale.
   */
  public int getScale()
  {
    return scale;
  }

  /**
   * Defines the scale for the divide-operation. The scale influences the precision of the division.
   *
   * @param scale the scale.
   */
  public void setScale(final int scale)
  {
    this.scale = scale;
  }


  /**
   * Receives notification that a row of data is being processed.
   *
   * @param event Information about the event.
   */
  public void itemsAdvanced(final ReportEvent event)
  {
    final Object fieldValue = event.getDataRow().get(getField());
    if (fieldValue instanceof Number == false)
    {
      return;
    }
    try
    {
      final Number n = (Number) fieldValue;
      sum = sum.add(new BigDecimal(n.toString()));
      itemCount = itemCount.add(ItemAvgFunction.ONE);
    }
    catch (final Exception e)
    {
      ItemAvgFunction.logger.error("ItemAvgFunction.advanceItems(): problem adding number.");
    }
  }

  /**
   * Returns the function value, in this case the average of all values of a specific column in the report's
   * TableModel.
   *
   * @return The function value.
   */
  public Object getValue()
  {
    if (itemCount.longValue() == 0)
    {
      return null;
    }
    else
    {
      return sum.divide(itemCount, scale, roundingMode);
    }
  }

  /**
   * Return a completly separated copy of this function. The copy does no longer share any changeable objects with the
   * original function.
   *
   * @return a copy of this function.
   */
  public Expression getInstance()
  {
    final ItemAvgFunction function = (ItemAvgFunction) super.getInstance();
    function.sum = ItemAvgFunction.ZERO;
    function.itemCount = ItemAvgFunction.ZERO;
    return function;
  }
}

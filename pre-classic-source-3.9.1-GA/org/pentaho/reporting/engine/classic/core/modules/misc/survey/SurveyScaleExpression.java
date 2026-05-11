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

package org.pentaho.reporting.engine.classic.core.modules.misc.survey;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.Arrays;

import org.pentaho.reporting.engine.classic.core.filter.types.ContentFieldType;
import org.pentaho.reporting.engine.classic.core.function.AbstractExpression;

/**
 * An expression that takes values from one or more fields in the current row of the report, builds a {@link
 * SurveyScale} instance that will present those values, and returns that instance as the expression result.  The fields
 * used by the expression are defined using properties named '0', '1', ... 'N', which need to be specified after the
 * expression is created. These fields should contain {@link Number} instances.The {@link SurveyScale} class implements
 * the Drawable interface, so it can be displayed using a {@link ContentFieldType}.
 */
public class SurveyScaleExpression extends AbstractExpression
{
  /**
   * The lowest value on the scale.
   */
  private int lowest;

  /**
   * The highest value on the scale.
   */
  private int highest;

  /**
   * An ordered list containing the fieldnames used in the expression.
   */
  private ArrayList fieldList;

  /**
   * The name of the field containing the lower bound of the highlighted range.
   */
  private String rangeLowerBoundField;

  /**
   * The name of the field containing the upper bound of the highlighted range.
   */
  private String rangeUpperBoundField;

  /**
   * The range paint.
   */
  private Paint rangePaint;

  /**
   * An optional shape that is used (if present) for the first data value.
   */
  private Shape overrideShape;

  /**
   * A flag that controls whether or not the override shape is filled or not filled.
   */
  private boolean overrideShapeFilled;

  public SurveyScaleExpression()
  {
    this(0, 1);
  }

  /**
   * Creates a new expression.
   *
   * @param lowest  the lowest value on the response scale.
   * @param highest the highest value on the response scale.
   */
  public SurveyScaleExpression(final int lowest, final int highest)
  {
    this(lowest, highest, null, null, null);
  }

  /**
   * Creates a new expression.
   *
   * @param lowest           the lowest value on the response scale.
   * @param highest          the highest value on the response scale.
   * @param lowerBoundsField the name of the field containing the lower bound of the highlighted range
   *                         (<code>null</code> permitted).
   * @param upperBoundsField the name of the field containing the upper bound of the highlighted range
   *                         (<code>null</code> permitted).
   * @param shape            a shape that will be used to override the shape displayed for the first series
   *                         (<code>null</code> permitted).
   */
  public SurveyScaleExpression(final int lowest,
                               final int highest,
                               final String lowerBoundsField,
                               final String upperBoundsField,
                               final Shape shape)
  {
    this.lowest = lowest;
    this.highest = highest;
    this.fieldList = new ArrayList();
    this.overrideShape = shape;
    this.overrideShapeFilled = false;
    this.rangeLowerBoundField = lowerBoundsField;
    this.rangeUpperBoundField = upperBoundsField;
    this.rangePaint = Color.lightGray;
  }

  /**
   * Returns the name of the field containing the lower bound of the range that is to be highlighted on the scale.
   *
   * @return A string (possibly <code>null</code>).
   */
  public String getRangeLowerBoundField()
  {
    return this.rangeLowerBoundField;
  }

  /**
   * Sets the name of the field containing the lower bound of the range that is to be highlighted on the scale.  Set
   * this to <code>null</code> if you have no range to highlight.
   *
   * @param field the field name (<code>null</code> permitted).
   */
  public void setRangeLowerBoundField(final String field)
  {
    this.rangeLowerBoundField = field;
  }

  /**
   * Returns the name of the field containing the upper bound of the range that is to be highlighted on the scale.
   *
   * @return A string (possibly <code>null</code>).
   */
  public String getRangeUpperBoundField()
  {
    return this.rangeUpperBoundField;
  }

  /**
   * Sets the name of the field containing the upper bound of the range that is to be highlighted on the scale.  Set
   * this to <code>null</code> if you have no range to highlight.
   *
   * @param field the field name (<code>null</code> permitted).
   */
  public void setRangeUpperBoundField(final String field)
  {
    this.rangeUpperBoundField = field;
  }

  /**
   * Returns the override shape.
   *
   * @return The override shape (possibly <code>null</code>).
   */
  public Shape getOverrideShape()
  {
    return this.overrideShape;
  }

  /**
   * Sets the override shape.  The {@link SurveyScale} is created with a set of default shapes, this method allows you
   * to replace the *first* shape if you need to (leave it as <code>null</code> otherwise).
   *
   * @param shape the shape (<code>null</code> permitted).
   */
  public void setOverrideShape(final Shape shape)
  {
    this.overrideShape = shape;
  }

  /**
   * Sets a flag that controls whether the override shape is filled or not.
   *
   * @param b the flag.
   */
  public void setOverrideShapeFilled(final boolean b)
  {
    this.overrideShapeFilled = b;
  }

  public int getLowest()
  {
    return lowest;
  }

  public void setLowest(final int lowest)
  {
    this.lowest = lowest;
  }

  public int getHighest()
  {
    return highest;
  }

  public void setHighest(final int highest)
  {
    this.highest = highest;
  }

  /**
   * Returns a {@link SurveyScale} instance that is set up to display the values in the current row.
   *
   * @return a {@link SurveyScale} instance.
   */
  public Object getValue()
  {
    final SurveyScale result =
        new SurveyScale(this.lowest, this.highest, collectValues());

    if (this.rangeLowerBoundField != null && this.rangeUpperBoundField != null)
    {
      final Number b0 = (Number) getDataRow().get(this.rangeLowerBoundField);
      final Number b1 = (Number) getDataRow().get(this.rangeUpperBoundField);
      result.setRangeLowerBound(b0);
      result.setRangeUpperBound(b1);
    }
    result.setRangePaint(this.rangePaint);
    if (this.overrideShape != null)
    {
      result.setShape(0, this.overrideShape);
      result.setShapeFilled(0, this.overrideShapeFilled);
    }
    return result;
  }

  /**
   * collects the values of all fields defined in the fieldList.
   *
   * @return an Objectarray containing all defined values from the datarow
   */
  private Number[] collectValues()
  {
    final Number[] retval = new Number[this.fieldList.size()];
    for (int i = 0; i < this.fieldList.size(); i++)
    {
      final String field = (String) this.fieldList.get(i);
      retval[i] = (Number) getDataRow().get(field);
    }
    return retval;
  }

  /**
   * Clones the expression.
   *
   * @return a copy of this expression.
   * @throws CloneNotSupportedException this should never happen.
   */
  public Object clone()
      throws CloneNotSupportedException
  {
    final SurveyScaleExpression fva = (SurveyScaleExpression) super.clone();
    fva.fieldList = (ArrayList) this.fieldList.clone();
    return fva;
  }

  public String[] getField()
  {
    return (String[]) fieldList.toArray(new String[fieldList.size()]);
  }

  public void setField(final String[] fields)
  {
    this.fieldList.clear();
    this.fieldList.addAll(Arrays.asList(fields));
  }

  public String getField(final int idx)
  {
    return (String) this.fieldList.get(idx);
  }

  public void setField(final int index, final String field)
  {
    if (fieldList.size() == index)
    {
      fieldList.add(field);
    }
    else
    {
      fieldList.set(index, field);
    }
  }

  public boolean isOverrideShapeFilled()
  {
    return overrideShapeFilled;
  }

  public Paint getRangePaint()
  {
    return rangePaint;
  }

  public void setRangePaint(final Paint rangePaint)
  {
    if (rangePaint == null)
    {
      throw new NullPointerException();
    }
    this.rangePaint = rangePaint;
  }
}

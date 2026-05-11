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

package org.pentaho.reporting.engine.classic.core;

import java.awt.BasicStroke;
import java.awt.Stroke;

import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;

/**
 * Used to draw shapes (typically lines and boxes) on a report band. The drawing style of the shapes contained in that
 * element can be controled by using the StyleKeys FILL_SHAPE and DRAW_SHAPE.
 *
 * @author David Gilbert
 * @author Thomas Morgner
 * @deprecated This class is no longer used and will be removed in the next version.
 */
public class ShapeElement extends Element
{
  /**
   * The default stroke.
   */
  public static final BasicStroke DEFAULT_STROKE = new BasicStroke(0.5f);

  /**
   * A key for the 'fill-shape' style.
   */
  public static final StyleKey FILL_SHAPE = ElementStyleKeys.FILL_SHAPE;

  /**
   * A key for the 'draw-shape' style.
   */
  public static final StyleKey DRAW_SHAPE = ElementStyleKeys.DRAW_SHAPE;

  /**
   * Constructs a shape element.
   */
  public ShapeElement()
  {
  }

  /**
   * Returns a string describing the element.  Useful for debugging.
   *
   * @return the string.
   */
  public String toString()
  {
    final StringBuilder b = new StringBuilder();
    b.append("ShapeElement={ name=");
    b.append(getName());
    b.append('}');
    return b.toString();
  }

  /**
   * Returns true if the element outline should be drawn, and false otherwise.
   * <p/>
   * This is determined by the element's style-sheet.
   *
   * @return true or false.
   */
  public boolean isShouldDraw()
  {
    return getStyle().getBooleanStyleProperty(ElementStyleKeys.DRAW_SHAPE);
  }

  /**
   * Returns true of the element should be filled, and false otherwise.
   * <p/>
   * This is determined by the element's style-sheet.
   *
   * @return true or false.
   */
  public boolean isShouldFill()
  {
    return getStyle().getBooleanStyleProperty(ElementStyleKeys.FILL_SHAPE);
  }

  /**
   * Sets a flag that controls whether or not the outline of the shape is drawn.
   *
   * @param shouldDraw the flag.
   */
  public void setShouldDraw(final boolean shouldDraw)
  {
    getStyle().setStyleProperty(ElementStyleKeys.DRAW_SHAPE, shouldDraw ? Boolean.TRUE : Boolean.FALSE);
    notifyNodePropertiesChanged();
  }

  /**
   * Sets a flag that controls whether or not the area of the shape is filled.
   *
   * @param shouldFill the flag.
   */
  public void setShouldFill(final boolean shouldFill)
  {
    getStyle().setStyleProperty(ElementStyleKeys.FILL_SHAPE, shouldFill ? Boolean.TRUE : Boolean.FALSE);
    notifyNodePropertiesChanged();
  }

  /**
   * Returns true if the shape should be scaled, and false otherwise.
   * <p/>
   * This is determined by the element's style-sheet.
   *
   * @return true or false.
   */
  public boolean isScale()
  {
    return getStyle().getBooleanStyleProperty(ElementStyleKeys.SCALE);
  }

  /**
   * Sets a flag that controls whether the shape should be scaled to fit the element bounds.
   *
   * @param scale the flag.
   */
  public void setScale(final boolean scale)
  {
    getStyle().setStyleProperty(ElementStyleKeys.SCALE, scale ? Boolean.TRUE : Boolean.FALSE);
    notifyNodePropertiesChanged();
  }

  /**
   * Returns true if the shape's aspect ratio should be preserved, and false otherwise.
   * <p/>
   * This is determined by the element's style-sheet.
   *
   * @return true or false.
   */
  public boolean isKeepAspectRatio()
  {
    return getStyle().getBooleanStyleProperty(ElementStyleKeys.KEEP_ASPECT_RATIO);
  }

  /**
   * Sets a flag that controls whether the shape should be scaled to fit the element bounds.
   *
   * @param kar the flag.
   */
  public void setKeepAspectRatio(final boolean kar)
  {
    getStyle().setStyleProperty(ElementStyleKeys.KEEP_ASPECT_RATIO, kar ? Boolean.TRUE : Boolean.FALSE);
    notifyNodePropertiesChanged();
  }

  /**
   * Returns the stroke.
   *
   * @return the stroke.
   */
  public Stroke getStroke()
  {
    return (Stroke) getStyle().getStyleProperty(ElementStyleKeys.STROKE);
  }

  /**
   * Sets the stroke.
   *
   * @param stroke the stroke.
   */
  public void setStroke(final Stroke stroke)
  {
    getStyle().setStyleProperty(ElementStyleKeys.STROKE, stroke);
    notifyNodePropertiesChanged();
  }
}

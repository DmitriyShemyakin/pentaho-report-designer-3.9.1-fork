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

package org.pentaho.reporting.engine.classic.core.layout;

import org.pentaho.reporting.engine.classic.core.style.FontDefinition;

/**
 * The DefaultLayoutSupport uses the AWT to estaminate the content sizes. A LayoutSupport contains all methods required
 * to estaminate sizes for the content-creation.
 *
 * @author Thomas Morgner
 * @deprecated Like the layout support itself, this class is deprecated. There is no safe way how functions could
 *             compute valid text-sizes before the layouting starts.
 */
public class DefaultLayoutSupport implements LayoutSupport
{
  private boolean useMaxLineHeight;
  private boolean imageResolutionMapping;

  /**
   * Default-Constructor.
   */
  public DefaultLayoutSupport(final boolean useMaxLineHeight,
                              final boolean imageResolutionMapping)
  {
    this.imageResolutionMapping = imageResolutionMapping;
    this.useMaxLineHeight = useMaxLineHeight;
  }

  /**
   * Creates a size calculator for the current state of the output target.  The calculator is used to calculate the
   * string width and line height and later maybe more...
   *
   * @param font the font.
   * @return the size calculator.
   * @throws SizeCalculatorException if there is a problem with the output target.
   */
  public SizeCalculator createTextSizeCalculator(final FontDefinition font)
      throws SizeCalculatorException
  {
    return DefaultSizeCalculator.getDefaultSizeCalculator(font, useMaxLineHeight);
  }

  /**
   * Returns the element alignment. Elements will be layouted aligned to this border, so that <code>mod(X,
   * horizontalAlignment) == 0</code> and <code>mod(Y, verticalAlignment) == 0</code>. Returning 0 will disable the
   * alignment.
   *
   * @return the vertical alignment grid boundry
   */
  public float getVerticalAlignmentBorder()
  {
    return 0;
  }

  /**
   * Returns the element alignment. Elements will be layouted aligned to this border, so that <code>mod(X,
   * horizontalAlignment) == 0</code> and <code>mod(Y, verticalAlignment) == 0</code>. Returning 0 will disable the
   * alignment.
   *
   * @return the vertical alignment grid boundry
   */
  public float getHorizontalAlignmentBorder()
  {
    return 0;
  }

  /**
   * Returns the element alignment. Elements will be layouted aligned to this border, so that <code>mod(X,
   * horizontalAlignment) == 0</code> and <code>mod(Y, verticalAlignment) == 0</code>. Returning 0 will disable the
   * alignment.
   * <p/>
   * Q&D Hack: Save some cycles of processor time by computing that thing only once.
   *
   * @return the vertical alignment grid boundry
   */
  public long getInternalHorizontalAlignmentBorder()
  {
    return 0;
  }

  /**
   * Returns the element alignment. Elements will be layouted aligned to this border, so that <code>mod(X,
   * horizontalAlignment) == 0</code> and <code>mod(Y, verticalAlignment) == 0</code>. Returning 0 will disable the
   * alignment.
   * <p/>
   * Q&D Hack: Save some cycles of processor time by computing that thing only once.
   *
   * @return the vertical alignment grid boundry
   */
  public long getInternalVerticalAlignmentBorder()
  {
    return 0;
  }

  public boolean isImageResolutionMappingActive()
  {
    return imageResolutionMapping;
  }
}

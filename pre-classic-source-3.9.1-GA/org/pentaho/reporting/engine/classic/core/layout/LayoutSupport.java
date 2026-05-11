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
 * The LayoutSupport contains all methods required to estaminate sizes for the content-creation.
 *
 * @author Thomas Morgner
 * @deprecated This one should be removed. It is no longer needed by the system and may lead to invalid results.
 *             Functions should not make assumptions about the size of the report elements.
 */
public interface LayoutSupport
{

  /**
   * Creates a size calculator for the current state of the output target.  The calculator is used to calculate the
   * string width and line height and later maybe more...
   *
   * @param font the font.
   * @return the size calculator.
   * @throws SizeCalculatorException if there is a problem with the output target.
   */
  public SizeCalculator createTextSizeCalculator(FontDefinition font)
      throws SizeCalculatorException;

  /**
   * Returns the element alignment. Elements will be layouted aligned to this border, so that <code>mod(X,
   * horizontalAlignment) == 0</code> and <code>mod(Y, verticalAlignment) == 0</code>. Returning 0 will disable the
   * alignment.
   *
   * @return the vertical alignment grid boundry
   */
  public float getVerticalAlignmentBorder();

  /**
   * Returns the element alignment. Elements will be layouted aligned to this border, so that <code>mod(X,
   * horizontalAlignment) == 0</code> and <code>mod(Y, verticalAlignment) == 0</code>. Returning 0 will disable the
   * alignment.
   *
   * @return the vertical alignment grid boundry
   */
  public float getHorizontalAlignmentBorder();

  /**
   * Returns the element alignment. Elements will be layouted aligned to this border, so that <code>mod(X,
   * horizontalAlignment) == 0</code> and <code>mod(Y, verticalAlignment) == 0</code>. Returning 0 will disable the
   * alignment.
   * <p/>
   * Q&D Hack: Save some cycles of processor time by computing that thing only once.
   *
   * @return the vertical alignment grid boundry
   */
  public long getInternalVerticalAlignmentBorder();

  /**
   * Returns the element alignment. Elements will be layouted aligned to this border, so that <code>mod(X,
   * horizontalAlignment) == 0</code> and <code>mod(Y, verticalAlignment) == 0</code>. Returning 0 will disable the
   * alignment.
   * <p/>
   * Q&D Hack: Save some cycles of processor time by computing that thing only once.
   *
   * @return the vertical alignment grid boundry
   */
  public long getInternalHorizontalAlignmentBorder();

  /**
   * Checks, if the layouter uses the System's native resolution to compute the size of images. This fixes problems with
   * the HTML output, which assumes that images are rendered with a resolution of 96dpi.
   *
   * @return true, if the image-resolution mapping is active, false otherwise.
   */
  public boolean isImageResolutionMappingActive();
}

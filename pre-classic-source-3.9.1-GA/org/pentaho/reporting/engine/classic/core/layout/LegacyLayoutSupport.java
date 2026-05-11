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

import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorFeature;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.style.FontDefinition;

/**
 * Creation-Date: 08.04.2007, 15:49:03
 *
 * @author Thomas Morgner
 * @deprecated Like the base class, this class cannot be used to create a valid precomputation of the report layout.
 */
public class LegacyLayoutSupport implements LayoutSupport
{
  private OutputProcessorMetaData outputProcessorMetaData;

  public LegacyLayoutSupport(final OutputProcessorMetaData outputProcessorMetaData)
  {
    if (outputProcessorMetaData == null)
    {
      throw new NullPointerException();
    }
    this.outputProcessorMetaData = outputProcessorMetaData;
  }

  /**
   * Creates a size calculator for the current state of the output target.  The calculator is used to calculate the
   * string width and line height and later maybe more...
   *
   * @param font the font.
   * @return the size calculator.
   * @throws org.pentaho.reporting.engine.classic.core.layout.SizeCalculatorException
   *          if there is a problem with the output target.
   */
  public SizeCalculator createTextSizeCalculator(final FontDefinition font) throws SizeCalculatorException
  {
    return new DefaultSizeCalculator(font,
        outputProcessorMetaData.isFeatureSupported(OutputProcessorFeature.LEGACY_LINEHEIGHT_CALC));
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
  public long getInternalVerticalAlignmentBorder()
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

  public boolean isImageResolutionMappingActive()
  {
    return outputProcessorMetaData.isFeatureSupported(OutputProcessorFeature.IMAGE_RESOLUTION_MAPPING);
  }
}

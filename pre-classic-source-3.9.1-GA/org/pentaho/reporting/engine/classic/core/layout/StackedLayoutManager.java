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

import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictDimension;

/**
 * @deprecated This layout manager is no longer used.
 */
public class StackedLayoutManager implements BandLayoutManager
{
  public StackedLayoutManager()
  {
  }

  /**
   * Performs the layout of a band.
   *
   * @param b       the band.
   * @param support the layout support used to compute sizes.
   */
  public void doLayout(final Band b,
                       final LayoutSupport support,
                       final ExpressionRuntime runtime)
  {
    throw new UnsupportedOperationException();
  }

  /**
   * Calculates the minimum layout size for a band.
   *
   * @param b             the band.
   * @param containerDims the bounds of the surrounding container.
   * @param maxUsableSize
   * @param support       the layout support used to compute sizes.
   * @return the minimum size.
   */
  public StrictDimension minimumLayoutSize(final Band b,
                                           final StrictDimension containerDims,
                                           final StrictDimension maxUsableSize,
                                           final LayoutSupport support,
                                           final ExpressionRuntime runtime)
  {
    throw new UnsupportedOperationException();
  }

  /**
   * Calculates the preferred layout size for a band.
   *
   * @param b             the band.
   * @param containerDims the bounds of the surrounding container.
   * @param maxUsableSize
   * @param support       the layout support used to compute sizes.
   * @return the preferred size.
   */
  public StrictDimension preferredLayoutSize(final Band b,
                                             final StrictDimension containerDims,
                                             final StrictDimension maxUsableSize,
                                             final LayoutSupport support,
                                             final ExpressionRuntime runtime)
  {
    throw new UnsupportedOperationException();
  }
}

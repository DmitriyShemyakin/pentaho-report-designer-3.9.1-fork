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
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictDimension;

/**
 * An implementation of the BandLayoutManager interface.
 * <p/>
 * Rule: Bands can have minimum, max and pref size defined. These values are hints for the layout container, no
 * restrictions. If min and pref are '0', they are ignored. MaxSize is never ignored.
 * <p/>
 * Elements that have the "dynamic" flag set, are checked for their content-bounds. This operation is expensive, so this
 * is only done if really needed. The dynamic flag will influence the height of an element, a valid width must be
 * already set.
 * <p/>
 * Invisible elements within the layouted band are not evaluated. This layout manager will ignore invisible child bands
 * and -elements.
 * <p/>
 * Note to everybody who tries to understand this class: This class is full of old compatibility code, this class is not
 * designed to be smart, or suitable for complex layouts. The only purpose of this class is to maintain backward
 * compatiblity with older releases of JFreeReport.
 * <p/>
 * The use of relative elements (the one's with 100% should be considered carefully, as these elements are not fully
 * predictable).
 *
 * @author Thomas Morgner
 * @deprecated This layout manager is no longer used.
 */
public class StaticLayoutManager implements BandLayoutManager
{
  /**
   * A key for the absolute position of an element.
   */
  public static final StyleKey ABSOLUTE_POS = ElementStyleKeys.ABSOLUTE_POS;

  /**
   * Creates a new layout manager.
   */
  public StaticLayoutManager()
  {
  }

  /**
   * Calculates the preferred layout size for a band. The band is limited to the given container bounds as well as to
   * the own maximum size.
   * <p/>
   * The preferred size of non-absolute elements is calculated by using the parents dimensions supplied in
   * containerDims. Elements with a width or height of 100% will consume all available space of the parent.
   *
   * @param b             the band.
   * @param containerDims the maximum size the band should use for that container.
   * @param maxUsableSize
   * @param support       the layout support used to compute sizes.
   * @return the preferred size.
   */
  public StrictDimension preferredLayoutSize
      (final Band b,
       final StrictDimension containerDims,
       final StrictDimension maxUsableSize,
       final LayoutSupport support,
       final ExpressionRuntime runtime)
  {
    throw new UnsupportedOperationException();
  }

  /**
   * Calculates the minimum layout size for a band. The width for the child elements are not calculated, as we assume
   * that the width's are defined fixed within the parent.
   *
   * @param b               the band.
   * @param containerBounds the bounds of the bands parents.
   * @param maxUsableSize
   * @param support         the layout support used to compute sizes.
   * @return the minimum size.
   */
  public StrictDimension minimumLayoutSize
      (final Band b,
       final StrictDimension containerBounds,
       final StrictDimension maxUsableSize,
       final LayoutSupport support,
       final ExpressionRuntime runtime)
  {
    throw new UnsupportedOperationException();
  }

  /**
   * Layout a single band with all elements contained within the band.
   * <p/>
   * The band has its <code>BOUNDS</code> already set and all elements are laid out within these bounds. The band's
   * properties will not be changed during the layouting.
   * <p/>
   * This layout manager requires that all direct child elements have the <code>ABSOLUTE_POS</code> and
   * <code>MINIMUM_SIZE</code> properties set to valid values.
   *
   * @param b       the band to lay out.
   * @param support the layout support used to compute sizes.
   * @throws java.lang.IllegalStateException
   *          if the bands has no bounds set.
   */
  public synchronized void doLayout(final Band b,
                                    final LayoutSupport support,
                                    final ExpressionRuntime runtime)
  {
    throw new UnsupportedOperationException();
  }
}

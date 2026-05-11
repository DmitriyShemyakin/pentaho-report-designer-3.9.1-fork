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

/**
 * The interface for an class that is able to calculate the width of a given string, and the height of a line of text.
 * The calculations rely on state information (e.g. font size, graphics device, etc) maintained by the calculator.
 * <p/>
 * Every {@link org.pentaho.reporting.engine.classic.core.layout.LayoutSupport} can create an instance of a class that
 * implements this interface, via the {@link org.pentaho.reporting.engine.classic.core.layout.LayoutSupport#createTextSizeCalculator}
 * method.
 *
 * @author Thomas Morgner
 * @deprecated The size-calculator does no longer yield correct results, as the layouting depends on more than just the
 *             element itself. Functions should not try to precompute a layout, they should rely on a correct
 *             report-definition and style instead.
 */
public interface SizeCalculator
{
  /**
   * @deprecated This config-key should not be declared here.
   */
  public static final String USE_MAX_CHAR_SIZE = "org.pentaho.reporting.engine.classic.core.layout.fontrenderer.UseMaxCharBounds";

  /**
   * @deprecated This config-key is no longer used.
   */
  public static final String CLIP_TEXT = "org.pentaho.reporting.engine.classic.core.layout.fontrenderer.ClipText";

  /**
   * Calculates the width of a <code>String<code> in the current <code>Graphics</code> context.
   *
   * @param text         the text.
   * @param lineStartPos the start position of the substring to be measured.
   * @param endPos       the position of the last character to be measured.
   * @return the width of the string in Java2D units.
   */
  public float getStringWidth(String text, int lineStartPos, int endPos);

  /**
   * Returns the line height.  This includes the font's ascent, descent and leading.
   *
   * @return the line height.
   */
  public float getLineHeight();

}

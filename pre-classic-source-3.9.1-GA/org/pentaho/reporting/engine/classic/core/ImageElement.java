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

import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;

/**
 * Used to draw images. References to the Images must be given as ImageContainer. If you use the
 * <code>ImageElementFactory</code> implementations, the necessary wrapping is done for you, if needed.
 * <p/>
 *
 * @author Thomas Morgner
 * @deprecated This class is no longer used and will be removed in the next version.
 */
public class ImageElement extends Element
{
  /**
   * Constructs a image element.
   */
  public ImageElement()
  {
  }

  /**
   * Returns true if the image should be scaled, and false otherwise.
   *
   * @return true or false.
   */
  public boolean isScale()
  {
    return getStyle().getBooleanStyleProperty(ElementStyleKeys.SCALE);
  }

  /**
   * Sets a flag that controls whether the image should be scaled to fit the element bounds.
   *
   * @param scale the flag.
   */
  public void setScale(final boolean scale)
  {
    getStyle().setBooleanStyleProperty(ElementStyleKeys.SCALE, scale);
    notifyNodePropertiesChanged();
  }

  /**
   * Returns true if the image's aspect ratio should be preserved, and false otherwise.
   *
   * @return true or false.
   */
  public boolean isKeepAspectRatio()
  {
    return getStyle().getBooleanStyleProperty(ElementStyleKeys.KEEP_ASPECT_RATIO);
  }

  /**
   * Sets a flag that controls whether the shape's aspect ratio should be preserved.
   *
   * @param kar the flag.
   */
  public void setKeepAspectRatio(final boolean kar)
  {
    getStyle().setBooleanStyleProperty(ElementStyleKeys.KEEP_ASPECT_RATIO, kar);
    notifyNodePropertiesChanged();
  }
}

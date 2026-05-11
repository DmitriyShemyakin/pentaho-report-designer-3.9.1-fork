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

package org.pentaho.reporting.engine.classic.core.layout.style;

import java.awt.geom.Point2D;

import org.pentaho.reporting.engine.classic.core.style.AbstractStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;
import org.pentaho.reporting.libraries.base.util.FloatDimension;

/**
 * A replaced content element that is contained in a 'canvas' box (which is the default for all non-inline replaced
 * content elements) must have a minimum width and height of 100% so that it fills the whole box.
 *
 * @author Thomas Morgner
 */
public class DynamicReplacedContentStyleSheet extends AbstractStyleSheet
{
  private static final Float SIZE = new Float(-100);
  private static final Float POS = new Float(0);
  private StyleSheet parent;

  public DynamicReplacedContentStyleSheet(final StyleSheet parent)
  {
    this.parent = parent;
  }

  public StyleSheet getParent()
  {
    return parent;
  }

  public InstanceID getId()
  {
    return parent.getId();
  }

  public long getChangeTracker()
  {
    return parent.getChangeTracker();
  }

  public Object getStyleProperty(final StyleKey key, final Object defaultValue)
  {
//    if (ElementStyleKeys.MAXIMUMSIZE.equals(key))
//    {
//      final Float maxHeight = (Float) parent.getStyleProperty(ElementStyleKeys.MAX_HEIGHT);
//      return new FloatDimension(SIZE.floatValue(), maxHeight.floatValue());
//    }
//    if (ElementStyleKeys.MAX_WIDTH.equals(key))
//    {
//      return SIZE;
//    }
//
    if (ElementStyleKeys.MINIMUMSIZE.equals(key))
    {
      return new FloatDimension(-100, -100);
    }
    if (ElementStyleKeys.MIN_WIDTH.equals(key))
    {
      return DynamicReplacedContentStyleSheet.SIZE;
    }
    if (ElementStyleKeys.MIN_HEIGHT.equals(key))
    {
      return DynamicReplacedContentStyleSheet.SIZE;
    }
    if (ElementStyleKeys.POS_X.equals(key))
    {
      return DynamicReplacedContentStyleSheet.POS;
    }
    if (ElementStyleKeys.POS_Y.equals(key))
    {
      return DynamicReplacedContentStyleSheet.POS;
    }
    if (ElementStyleKeys.ABSOLUTE_POS.equals(key))
    {
      return new Point2D.Float(0, 0);
    }
    return parent.getStyleProperty(key, defaultValue);
  }

  public Object[] toArray()
  {
    final Object[] objects = parent.toArray();
    objects[ElementStyleKeys.MIN_WIDTH.getIdentifier()] = DynamicReplacedContentStyleSheet.SIZE;
    objects[ElementStyleKeys.MIN_HEIGHT.getIdentifier()] = DynamicReplacedContentStyleSheet.SIZE;
    objects[ElementStyleKeys.MINIMUMSIZE.getIdentifier()] = null;
    objects[ElementStyleKeys.POS_X.getIdentifier()] = DynamicReplacedContentStyleSheet.POS;
    objects[ElementStyleKeys.POS_Y.getIdentifier()] = DynamicReplacedContentStyleSheet.POS;
    objects[ElementStyleKeys.ABSOLUTE_POS.getIdentifier()] = null;
//    objects[ElementStyleKeys.MAXIMUMSIZE.getIdentifier()] = null;
//    objects[ElementStyleKeys.MAX_WIDTH.getIdentifier()] = SIZE;
    return objects;
  }
}

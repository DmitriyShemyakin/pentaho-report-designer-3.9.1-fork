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

import java.io.Serializable;

/**
 * An anchor is a possible target for external hyperlinks.
 * <p/>
 * In HTML anchors would be produced by using &lt;a name=&quot;anchorname&quot;&gt;. This class is immutable.
 *
 * @author Thomas Morgner
 * @see AnchorElement
 * @deprecated Ancors should not be created this way. Add a Anchor-Style-Expression instead. This class will be removed
 *             in 0.8.11.
 */
public class Anchor implements Serializable
{
  /**
   * Unique identifier for long-term persistence.
   */
  private static final long serialVersionUID = 8495721791372012478L;

  /**
   * The name of the anchor. Should be unique within the report.
   */
  private String name;

  /**
   * Creates a new anchor with the given name.
   *
   * @param name the name of the anchor.
   * @throws NullPointerException if the given name is null.
   */
  public Anchor(final String name)
  {
    if (name == null)
    {
      throw new NullPointerException();
    }
    this.name = name;
  }

  /**
   * Returns the name of the anchor.
   *
   * @return the name
   */
  public String getName()
  {
    return name;
  }

  /**
   * Checks whether the given object is an anchor with the same name as this one.
   *
   * @param obj the other object.
   * @return true, if the object is equal to this one, false otherwise.
   */
  public boolean equals(final Object obj)
  {
    if (this == obj)
    {
      return true;
    }
    if (!(obj instanceof Anchor))
    {
      return false;
    }

    final Anchor anchor = (Anchor) obj;

    if (!name.equals(anchor.name))
    {
      return false;
    }

    return true;
  }

  /**
   * Computes a hashcode for this anchor.
   *
   * @return the hashcode.
   */
  public int hashCode()
  {
    return name.hashCode();
  }

  public String toString()
  {
    return name;
  }
}

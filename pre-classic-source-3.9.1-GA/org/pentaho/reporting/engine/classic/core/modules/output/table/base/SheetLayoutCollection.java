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

package org.pentaho.reporting.engine.classic.core.modules.output.table.base;

import java.util.ArrayList;

/**
 * The tablelayout info class is used to store the layout that was generated in the repagination process. This layout
 * can be shared over several pages to unify the look of the tables.
 *
 * @author Thomas Morgner
 * @deprecated no longer used. Will be removed in the next release.
 */
public class SheetLayoutCollection
{
  /**
   * A list of page layouts, one entry for every page.
   */
  private ArrayList pageLayouts;
  private SheetLayout globalLayout;
  private boolean useGlobalLayout;

  /**
   * Creates a new tablelayout info object to store the layout information.
   *
   * @param useGlobalLayout whether to use a global layout for all pages
   */
  public SheetLayoutCollection(final boolean useGlobalLayout)
  {
    this.pageLayouts = new ArrayList();
    this.useGlobalLayout = useGlobalLayout;
    this.globalLayout = null;
  }

  /**
   * Adds a layout for the next page to the layout information.
   *
   * @param bounds the layout.
   */
  public void addLayout(final SheetLayout bounds)
  {
    if (isGlobalLayout())
    {
      globalLayout = bounds;
    }
    else
    {
      pageLayouts.add(bounds);
    }
  }

  /**
   * Checks whether to define a global layout.
   *
   * @return true, if the report uses an global layout, false otherwise.
   */
  public boolean isGlobalLayout()
  {
    return useGlobalLayout;
  }

  /**
   * Returns the layout for a given page. This returns the same layout for all pages if the globallayout feature is
   * enabled.
   *
   * @param page the page for that the layout is requested.
   * @return the stored layout.
   * @throws IndexOutOfBoundsException if the page is invalid.
   */
  public SheetLayout getLayoutForPage(final int page)
  {
    //Log.debug("Query Layout [" + isGlobalLayout() + "] for page " + page);
    if (isGlobalLayout())
    {
      if (globalLayout == null)
      {
        throw new IllegalStateException("No global layout defined.");
      }
      return globalLayout;
    }
    else if (page < pageLayouts.size())
    {
      return (SheetLayout) pageLayouts.get(page);
    }
    else
    {
      return null;
    }
  }

  /**
   * Return the number of pages stored in that list. This returns 1 if the global layout is active.
   *
   * @return the number of pages.
   */
  public int getPageCount()
  {
    if (!isGlobalLayout())
    {
      return pageLayouts.size();
    }
    if (globalLayout == null)
    {
      return 0;
    }
    return 1;
  }
}

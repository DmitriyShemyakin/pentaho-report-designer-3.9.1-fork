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

package org.pentaho.reporting.engine.classic.core.layout.process;

import org.pentaho.reporting.engine.classic.core.layout.model.BlockRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.CanvasRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.InlineRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.ParagraphRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableReplacedContentBox;

/**
 * Creation-Date: 19.04.2007, 20:47:38
 *
 * @author Thomas Morgner
 */
public final class ApplyCachedValuesStep extends IterateStructuralProcessStep
{
  private RenderBox uncleanBox;

  public ApplyCachedValuesStep()
  {
  }

  public void compute(final RenderBox box)
  {
    try
    {
      startProcessing(box);
    }
    finally
    {
      uncleanBox = null;
    }
  }

  protected void processParagraphChilds(final ParagraphRenderBox box)
  {
    processBoxChilds(box);
  }

  public boolean startCanvasBox(final CanvasRenderBox box)
  {
    if (uncleanBox == null)
    {
      final int state = box.getCacheState();
      if (state == RenderNode.CACHE_CLEAN)
      {
        return false;
      }
      if (state == RenderNode.CACHE_DEEP_DIRTY)
      {
        uncleanBox = box;
      }
    }

    box.apply();
    box.setStaticBoxPropertiesAge(box.getChangeTracker());
    return true;
  }

  protected void processOtherNode(final RenderNode node)
  {
    node.apply();
  }

  protected void processRenderableContent(final RenderableReplacedContentBox box)
  {
    box.apply();
  }

  protected boolean startBlockBox(final BlockRenderBox box)
  {
    if (uncleanBox == null)
    {
      final int state = box.getCacheState();
      if (state == RenderNode.CACHE_CLEAN)
      {
        if (box.getStaticBoxPropertiesAge() == box.getChangeTracker())
        {
          return false;
        }
      }
      if (state == RenderNode.CACHE_DEEP_DIRTY)
      {
        uncleanBox = box;
      }
    }

    box.apply();
    box.setStaticBoxPropertiesAge(box.getChangeTracker());
    return true;
  }

  protected boolean startRowBox(final RenderBox box)
  {
    if (uncleanBox == null)
    {
      final int state = box.getCacheState();
      if (state == RenderNode.CACHE_CLEAN)
      {
        return false;
      }
      if (state == RenderNode.CACHE_DEEP_DIRTY)
      {
        uncleanBox = box;
      }
    }

    box.apply();
    box.setStaticBoxPropertiesAge(box.getChangeTracker());
    return true;
  }

  protected boolean startInlineBox(final InlineRenderBox box)
  {
    if (uncleanBox == null)
    {
      final int state = box.getCacheState();
      if (state == RenderNode.CACHE_CLEAN)
      {
        return false;
      }
      if (state == RenderNode.CACHE_DEEP_DIRTY)
      {
        uncleanBox = box;
      }
    }

    box.apply();
    box.setStaticBoxPropertiesAge(box.getChangeTracker());
    return true;
  }

  protected boolean startOtherBox(final RenderBox box)
  {
    if (uncleanBox == null)
    {
      final int state = box.getCacheState();
      if (state == RenderNode.CACHE_CLEAN)
      {
        return false;
      }
      if (state == RenderNode.CACHE_DEEP_DIRTY)
      {
        uncleanBox = box;
      }
    }

    box.apply();
    box.setStaticBoxPropertiesAge(box.getChangeTracker());
    return true;
  }


  public void finishCanvasBox(final CanvasRenderBox box)
  {
    if (box == uncleanBox)
    {
      uncleanBox = null;
    }
  }

  protected void finishBlockBox(final BlockRenderBox box)
  {
    if (box == uncleanBox)
    {
      uncleanBox = null;
    }
  }

  protected void finishInlineBox(final InlineRenderBox box)
  {
    if (box == uncleanBox)
    {
      uncleanBox = null;
    }
  }

  protected void finishRowBox(final RenderBox box)
  {
    if (box == uncleanBox)
    {
      uncleanBox = null;
    }
  }

  protected void finishOtherBox(final RenderBox box)
  {
    if (box == uncleanBox)
    {
      uncleanBox = null;
    }
  }
}

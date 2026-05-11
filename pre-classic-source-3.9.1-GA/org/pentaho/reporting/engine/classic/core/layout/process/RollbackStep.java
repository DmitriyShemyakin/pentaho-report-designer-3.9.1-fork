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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.layout.model.BlockRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.CanvasRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.InlineRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.ParagraphRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableReplacedContentBox;

/**
 * Creation-Date: 18.10.2007, 13:41:08
 *
 * @author Thomas Morgner
 */
public final class RollbackStep extends IterateStructuralProcessStep
{
  private static final Log logger = LogFactory.getLog(RollbackStep.class);

  private boolean deepDirty;

  public RollbackStep()
  {
  }

  public void compute(final LogicalPageBox pageBox)
  {
    deepDirty = false;

    if (pageBox.isAppliedSeen() == false)
    {
      throw new IllegalStateException("How can I not see the root of the layout-tree?");
    }
    if (processBoxRollback(pageBox))
    {
      startProcessing(pageBox);
    }

    pageBox.rollbackSaveInformation();

  }

  private boolean processBoxRollback(final RenderBox pageBox)
  {
    if (pageBox.isAppliedOpen() ||
        pageBox.getAppliedContentRefCount() > 0)
    {
      pageBox.reopenAfterRollback(deepDirty);
      return true;
    }
    return false;
  }

  protected void processParagraphChilds(final ParagraphRenderBox box)
  {
    processBoxChilds(box);
  }

  private boolean processBox(final RenderBox parent)
  {
    boolean needDeepDive = false;
    if (parent.getNodeType() == LayoutNodeTypes.TYPE_BOX_LOGICALPAGE)
    {
      final LogicalPageBox lb = (LogicalPageBox) parent;
      if (processBoxRollback(lb.getWatermarkArea()))
      {
        needDeepDive = true;
      }
      if (processBoxRollback(lb.getHeaderArea()))
      {
        needDeepDive = true;
      }
      if (processBoxRollback(lb.getFooterArea()))
      {
        needDeepDive = true;
      }
      if (processBoxRollback(lb.getRepeatFooterArea()))
      {
        needDeepDive = true;
      }
    }

    RenderNode child = parent.getFirstChild();
    while (child != null)
    {
      final int type = child.getNodeType();
      if (type == LayoutNodeTypes.TYPE_NODE_FINISHEDNODE)
      {
        child = child.getNext();
        continue;
      }

      if ((type & LayoutNodeTypes.MASK_BOX) != LayoutNodeTypes.MASK_BOX)
      {
        // this should never be an issue, as plain render-nodes are no added to boxes other than
        // canvas boxes made of bands or inline-boxes. Therefore we are guaranteed that we already
        // removed the parent of a render node.
        throw new IllegalStateException
            ("Assertation error: A rollback-process encountered a render-node it should not have encountered.");
      }

      final RenderBox box = (RenderBox) child;
      if (box.isAppliedSeen() == false)
      {
        // must be a new box. Go away, evil new box ...
        final RenderNode next = child.getNext();
        parent.removeGenerated(box);
        parent.resetCacheState(deepDirty);
        child = next;
        continue;
      }

      if (box.isAppliedOpen() || box.getAppliedContentRefCount() > 0)
      {
        box.reopenAfterRollback(deepDirty);
        needDeepDive = true;
      }
      child = child.getNext();
    }
    return needDeepDive;
  }

  protected boolean startCanvasBox(final CanvasRenderBox box)
  {
    deepDirty = true;
    return processBox(box);
  }

  protected boolean startBlockBox(final BlockRenderBox box)
  {
    return processBox(box);
  }

  protected boolean startInlineBox(final InlineRenderBox box)
  {
    deepDirty = true;
    return processBox(box);
  }

  protected void processRenderableContent(final RenderableReplacedContentBox box)
  {
    deepDirty = true;
    processBox(box);
  }

  protected boolean startRowBox(final RenderBox box)
  {
    deepDirty = true;
    return processBox(box);
  }

  protected boolean startOtherBox(final RenderBox box)
  {
    deepDirty = true;
    return processBox(box);
  }
}

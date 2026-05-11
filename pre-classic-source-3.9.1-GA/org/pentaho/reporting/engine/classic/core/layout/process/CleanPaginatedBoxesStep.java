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
import org.pentaho.reporting.engine.classic.core.layout.model.FinishedRenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.InlineRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.ParagraphRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.context.StaticBoxLayoutProperties;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;


/**
 * This step must not remove boxes that have a manual break attached.
 *
 * @author Thomas Morgner
 */
public final class CleanPaginatedBoxesStep extends IterateStructuralProcessStep
{
  private long pageOffset;
  private long shiftOffset;
  private InstanceID shiftNode;

  public CleanPaginatedBoxesStep()
  {
  }

  public long compute(final LogicalPageBox pageBox)
  {
    shiftOffset = 0;
    pageOffset = pageBox.getPageOffset();
    if (startBlockBox(pageBox))
    {
      // not processing the header and footer area: they are 'out-of-context' bands
      processBoxChilds(pageBox);
    }
    finishBlockBox(pageBox);
    //Log.debug ("ShiftOffset after clean: " + shiftOffset);
    return shiftOffset;
  }

  public InstanceID getShiftNode()
  {
    return shiftNode;
  }

  protected void processParagraphChilds(final ParagraphRenderBox box)
  {
    // we do not process the paragraph lines. This should have been done
    // in the startblock thing and they get re-added anyway as long as the
    // paragraph is active.
  }

  public boolean startCanvasBox(final CanvasRenderBox box)
  {
    return false;
  }

  protected boolean startRowBox(final RenderBox box)
  {
    return false;
  }

  protected boolean startBlockBox(final BlockRenderBox box)
  {
    final int nodeType = box.getNodeType();
    if (nodeType == LayoutNodeTypes.TYPE_BOX_PARAGRAPH)
    {
      return false;
    }

    if (nodeType == LayoutNodeTypes.TYPE_BOX_BREAKMARK)
    {

      final RenderBox parent = box.getParent();
      if (parent == null)
      {
        // No parent? Unlikely and should not happen. Throw an assert-failure
        throw new IllegalStateException("Encountered a render-node that has no parent. How can that be?");
      }

      // A breakmark box must be translated into a finished node, so that we consume space without
      // triggering yet another break. The finished node will consume all space up to the next pagebreak.
      final long width = box.getContentAreaX2() - box.getContentAreaX1();
      final RenderNode prevSilbling = box.getPrev();
      if (prevSilbling == null)
      {
        // Node is first, so the parent's y is the next edge we take care of.
        final long y = parent.getY();
        final long y2 = Math.max(pageOffset, box.getY() + box.getHeight());
        parent.replaceChild(box, new FinishedRenderNode(width, y2 - y, 0, 0, true));
      }
      else
      {
        final long y = prevSilbling.getY() + prevSilbling.getHeight();
        final long y2 = Math.max(pageOffset, box.getY() + box.getHeight());
        parent.replaceChild(box, new FinishedRenderNode(width, y2 - y, 0, 0, true));
      }
    }

    if (box.isFinishedPaginate() == false)
    {
      return true;
    }

    final RenderNode firstNode = box.getFirstChild();
    if (firstNode == null)
    {
      // The cell is empty ..
      return false;
    }

    final long nodeY = firstNode.getY();
    if (nodeY > pageOffset)
    {
      // This box will be visible or will be processed in the future.
      return false;
    }

    if (firstNode.isOpen())
    {
      return true;
    }

    if ((nodeY + firstNode.getHeight()) > pageOffset)
    {
      // this box will span to the next page and cannot be removed ...
      return true;
    }

    // Next, search the last node that is fully invisible. We collapse all
    // invisible node into one big box for efficiency reasons. They wont be
    // visible anyway and thus the result will be the same as if they were
    // still alive ..
    RenderNode last = firstNode;
    while (true)
    {
      final RenderNode next = last.getNext();
      if (next == null)
      {
        break;
      }
      if (next.isOpen())
      {
        // as long as a box is open, it can grow and therefore it cannot be
        // removed ..
        break;
      }

      if ((next.getY() + next.getHeight()) > pageOffset)
      {
        // we cant handle that. This node will be visible. So the current last
        // node is the one we can shrink ..
        break;
      }
      last = next;
    }

    // todo: Aggressive removing by commenting out this if-block does seem to trigger a bug in the code below 
    if (last == firstNode)
    {
      if (last.getNodeType() == LayoutNodeTypes.TYPE_NODE_FINISHEDNODE)
      {
        // In this case, we can skip the replace-action below ..
        return true;
      }
    }

    final StaticBoxLayoutProperties sblp = box.getStaticBoxLayoutProperties();
    final long insetsTop = sblp.getBorderTop() + box.getBoxDefinition().getPaddingTop();

    // So lets get started. We remove all nodes between (and inclusive)
    // node and last.
    final long width = box.getContentAreaX2() - box.getContentAreaX1();
    final long lastY2;
    if (last.getNext() == null)
    {
      lastY2 = last.getY() + last.getHeight();
    }
    else
    {
      // in case the next box had been shifted
      lastY2 = last.getNext().getY();
    }
    final long startOfBox = box.getY() + insetsTop;
    final long height = lastY2 - startOfBox;

    // make sure that the finished-box inherits the margins ..
    final long marginsTop = firstNode.getEffectiveMarginTop();
    final long marginsBottom = last.getEffectiveMarginBottom();
    final boolean breakAfter = isBreakAfter(last);
    final FinishedRenderNode replacement = new FinishedRenderNode(width, height, marginsTop, marginsBottom, breakAfter);

    RenderNode removeNode = firstNode;
    while (removeNode != last)
    {
      final RenderNode next = removeNode.getNext();
      if (removeNode.isOpen())
      {
        throw new IllegalStateException("A node is still open. We should not have come that far.");
      }
      box.remove(removeNode);
      removeNode = next;
    }

    if (last.isOpen())
    {
      throw new IllegalStateException("The last node is still open. We should not have come that far.");
    }
    box.replaceChild(last, replacement);
    if (replacement.getParent() != box)
    {
//      return true;
      throw new IllegalStateException("The replacement did not work.");
    }

    final long cachedY2;
    if (last.getNext() == null)
    {
      cachedY2 = last.getCachedY() + last.getCachedHeight();
    }
    else
    {
      cachedY2 = last.getNext().getCachedY();
    }

    final long newShift = lastY2 - cachedY2;
    if (newShift > shiftOffset)
    {
      shiftOffset = newShift;
      shiftNode = box.getInstanceId();
    }
    return (box.getLastChild() != replacement);
  }


  private boolean isBreakAfter(final RenderNode node)
  {
    if (node.isBreakAfter())
    {
      return true;
    }

    if ((node.getNodeType() & LayoutNodeTypes.MASK_BOX_BLOCK) == LayoutNodeTypes.MASK_BOX_BLOCK)
    {
      final RenderBox box = (RenderBox) node;
      final RenderNode lastChild = box.getLastChild();
      if (lastChild != null)
      {
        return isBreakAfter(lastChild);
      }
    }
    return false;
  }


  protected boolean startInlineBox(final InlineRenderBox box)
  {
    return false;
  }
}

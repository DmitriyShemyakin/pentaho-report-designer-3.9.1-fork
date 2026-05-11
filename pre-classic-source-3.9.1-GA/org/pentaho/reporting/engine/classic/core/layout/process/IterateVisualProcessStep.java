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

import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.ParagraphRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;


/**
 * Iterates over the tree of nodes and classifies nodes by their Display-Model. The Display-Model of nodes is either
 * 'Block' or 'Inline'. All steps dealing with element placement commonly use this strategy.
 *
 * @author Thomas Morgner
 */
public abstract class IterateVisualProcessStep
{
  protected IterateVisualProcessStep()
  {
  }

  protected final void startProcessing(final RenderNode node)
  {
    final RenderBox parent = node.getParent();
    if (parent == null)
    {
      processBlockLevelChild(node);
    }
    else
    {
      final int parentType = parent.getNodeType();
      if ((parentType & LayoutNodeTypes.MASK_BOX_BLOCK) == LayoutNodeTypes.MASK_BOX_BLOCK)
      {
        processBlockLevelChild(node);
      }
      else if ((parentType & LayoutNodeTypes.MASK_BOX_CANVAS) == LayoutNodeTypes.MASK_BOX_CANVAS)
      {
        processCanvasLevelChild(node);
      }
      else if ((parentType & LayoutNodeTypes.MASK_BOX_INLINE) == LayoutNodeTypes.MASK_BOX_INLINE)
      {
        processInlineLevelChild(node);
      }
      else if ((parentType & LayoutNodeTypes.MASK_BOX_ROW) == LayoutNodeTypes.MASK_BOX_ROW)
      {
        processRowLevelChild(node);
      }
      else
      {
        processOtherLevelChild(node);
      }
    }
  }

  protected void processOtherLevelChild(final RenderNode node)
  {
    // we do not even handle that one. Other level elements are
    // always non-visual!
  }

  protected void processInlineLevelNode(final RenderNode node)
  {
  }

  protected boolean startInlineLevelBox(final RenderBox box)
  {
    return true;
  }

  protected void finishInlineLevelBox(final RenderBox box)
  {
  }

  protected final void processInlineLevelChild(final RenderNode node)
  {
    final int type = node.getNodeType();
    if (type == LayoutNodeTypes.TYPE_BOX_PARAGRAPH)
    {
      final ParagraphRenderBox box = (ParagraphRenderBox) node;
      if (startInlineLevelBox(box))
      {
        processParagraphChilds(box);
      }
      finishInlineLevelBox(box);
    }
    else if ((type & LayoutNodeTypes.MASK_BOX) == LayoutNodeTypes.MASK_BOX)
    {
      final RenderBox box = (RenderBox) node;
      if (startInlineLevelBox(box))
      {
        processBoxChilds(box);
      }
      finishInlineLevelBox(box);
    }
    else
    {
      processInlineLevelNode(node);
    }
  }

  protected void processCanvasLevelNode(final RenderNode node)
  {
  }

  protected boolean startCanvasLevelBox(final RenderBox box)
  {
    return true;
  }

  protected void finishCanvasLevelBox(final RenderBox box)
  {
  }

  protected final void processCanvasLevelChild(final RenderNode node)
  {
    final int nodeType = node.getNodeType();
    if (nodeType == LayoutNodeTypes.TYPE_BOX_PARAGRAPH)
    {
      final ParagraphRenderBox box = (ParagraphRenderBox) node;
      if (startCanvasLevelBox(box))
      {
        processParagraphChilds(box);
      }
      finishCanvasLevelBox(box);
    }
    else if ((nodeType & LayoutNodeTypes.MASK_BOX) == LayoutNodeTypes.MASK_BOX)
    {
      final RenderBox box = (RenderBox) node;
      if (startCanvasLevelBox(box))
      {
        processBoxChilds(box);
      }
      finishCanvasLevelBox(box);
    }
    else
    {
      processCanvasLevelNode(node);
    }
  }

  protected void processBlockLevelNode(final RenderNode node)
  {
  }

  protected boolean startBlockLevelBox(final RenderBox box)
  {
    return true;
  }

  protected void finishBlockLevelBox(final RenderBox box)
  {
  }

  protected final void processBlockLevelChild(final RenderNode node)
  {
    final int nodeType = node.getNodeType();
    if (nodeType == LayoutNodeTypes.TYPE_BOX_LOGICALPAGE)
    {
      final LogicalPageBox box = (LogicalPageBox) node;
      if (startBlockLevelBox(box))
      {
        startProcessing(box.getWatermarkArea());
        startProcessing(box.getHeaderArea());
        processBoxChilds(box);
        startProcessing(box.getRepeatFooterArea());
        startProcessing(box.getFooterArea());
      }
      finishBlockLevelBox(box);
    }
    else if (nodeType == LayoutNodeTypes.TYPE_BOX_PARAGRAPH)
    {
      final ParagraphRenderBox box = (ParagraphRenderBox) node;
      if (startBlockLevelBox(box))
      {
        processParagraphChilds(box);
      }
      finishBlockLevelBox(box);
    }
    else if ((nodeType & LayoutNodeTypes.MASK_BOX) == LayoutNodeTypes.MASK_BOX)
    {
      final RenderBox box = (RenderBox) node;
      if (startBlockLevelBox(box))
      {
        processBoxChilds(box);
      }
      finishBlockLevelBox(box);
    }
    else
    {
      processBlockLevelNode(node);
    }
  }

  protected abstract void processParagraphChilds(final ParagraphRenderBox box);

  protected final void processBoxChilds(final RenderBox box)
  {
    RenderNode node = box.getFirstChild();
    while (node != null)
    {
      startProcessing(node);
      node = node.getNext();
    }
  }

  protected final void processRowLevelChild(final RenderNode node)
  {
    final int nodeType = node.getNodeType();
    if (nodeType == LayoutNodeTypes.TYPE_BOX_PARAGRAPH)
    {
      final ParagraphRenderBox box = (ParagraphRenderBox) node;
      if (startRowLevelBox(box))
      {
        processParagraphChilds(box);
      }
      finishRowLevelBox(box);
    }
    else if ((nodeType & LayoutNodeTypes.MASK_BOX) == LayoutNodeTypes.MASK_BOX)
    {
      final RenderBox box = (RenderBox) node;
      if (startRowLevelBox(box))
      {
        processBoxChilds(box);
      }
      finishRowLevelBox(box);
    }
    else
    {
      processRowLevelNode(node);
    }
  }

  protected void processRowLevelNode(final RenderNode node)
  {
  }

  protected boolean startRowLevelBox(final RenderBox box)
  {
    return true;
  }

  protected void finishRowLevelBox(final RenderBox box)
  {
  }

}

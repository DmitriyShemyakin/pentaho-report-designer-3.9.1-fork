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
 * Copyright (c) 2009 Pentaho Corporation.  All rights reserved.
 */

package org.pentaho.reporting.designer.core.editor.report;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import org.pentaho.reporting.designer.core.model.ModelUtility;
import org.pentaho.reporting.designer.core.settings.WorkspaceSettings;
import org.pentaho.reporting.engine.classic.core.layout.model.BlockRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.CanvasRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.InlineRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.ParagraphRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableReplacedContentBox;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.graphics.internal.LogicalPageDrawable;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictBounds;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

/**
 * Todo: Document me!
 *
 * @author Thomas Morgner
 */
public class DesignerPageDrawable extends LogicalPageDrawable
{
  private DesignerCollectSelectedNodesStep collectSelectedNodesStep;

  public DesignerPageDrawable(final LogicalPageBox rootBox,
                              final OutputProcessorMetaData metaData,
                              final ResourceManager resourceManager)
  {
    super(rootBox, metaData, resourceManager);
    setDrawPageBackground(false);
    collectSelectedNodesStep = new DesignerCollectSelectedNodesStep();
  }

  /**
   * Draws the object.
   *
   * @param g2   the graphics device.
   * @param area the area inside which the object should be drawn.
   */
  public void draw(final Graphics2D g2, final Rectangle2D area)
  {
    final boolean outlineMode = WorkspaceSettings.getInstance().isAlwaysDrawElementFrames();
    setOutlineMode(outlineMode);
    super.draw(g2, area);
  }

  public boolean startCanvasBox(final CanvasRenderBox box)
  {
    if (ModelUtility.isHideInLayoutGui(box))
    {
      return false;
    }

    return super.startCanvasBox(box);
  }

  protected boolean startBlockBox(final BlockRenderBox box)
  {
    if (ModelUtility.isHideInLayoutGui(box))
    {
      return false;
    }
    return super.startBlockBox(box);
  }

  protected boolean startRowBox(final RenderBox box)
  {
    if (ModelUtility.isHideInLayoutGui(box))
    {
      return false;
    }
    return super.startRowBox(box);
  }

  protected boolean startInlineBox(final InlineRenderBox box)
  {
    if (ModelUtility.isHideInLayoutGui(box))
    {
      return false;
    }
    return super.startInlineBox(box);
  }

  protected void processOtherNode(final RenderNode node)
  {
    if (ModelUtility.isHideInLayoutGui(node))
    {
      return;
    }
    super.processOtherNode(node);
  }

  protected void processRenderableContent(final RenderableReplacedContentBox box)
  {
    if (ModelUtility.isHideInLayoutGui(box))
    {
      return;
    }
    super.processRenderableContent(box);
  }

  protected void processRootBand(final StrictBounds pageBounds)
  {
    processBoxChilds(getRootBox());
  }

  protected void processParagraphChilds(final ParagraphRenderBox box)
  {
    if (box.getFirstChild() == null)
    {
      if (box.getPoolSize() > 0)
      {
        final Graphics2D graphics1 = getGraphics();
        graphics1.setFont(new Font("Serif", Font.PLAIN, 10)); // NON-NLS
        graphics1.drawString("Your box is too small ...", // NON-NLS : TODO _ Change the way we handle this case
            (int) StrictGeomUtility.toExternalValue(box.getX()),
            (int) StrictGeomUtility.toExternalValue(box.getY()) + 10);
      }
    }
    else
    {
      super.processParagraphChilds(box);
    }
  }

  /**
   * Retries the nodes under the given coordinate which have a given attribute set. If name and namespace are null, all
   * nodes are returned. The nodes returned are listed in their respective hierarchical order.
   *
   * @param x         the x coordinate
   * @param y         the y coordinate
   * @param namespace the namespace on which to filter on
   * @param name      the name on which to filter on
   * @return the ordered list of nodes.
   */
  public RenderNode[] getNodesAt(final double x, final double y, final String namespace, final String name)
  {
    return collectSelectedNodesStep.getNodesAt
        (getRootBox(), StrictGeomUtility.createBounds(x, y, 1, 1), namespace, name);
  }

  public RenderNode[] getNodesAt(final double x,
                                 final double y,
                                 final double width,
                                 final double height,
                                 final String namespace,
                                 final String name)
  {
    return collectSelectedNodesStep.getNodesAt
        (getRootBox(), StrictGeomUtility.createBounds(x, y, width, height), namespace, name);
  }

  protected boolean drawOutlineBox(final Graphics2D g2, final RenderBox box)
  {
    if (box.getNodeType() == LayoutNodeTypes.TYPE_BOX_LINEBOX)
    {
      return false;
    }
    else
    {
      g2.setPaint(Color.lightGray);
    }

    final double x = StrictGeomUtility.toExternalValue(box.getX());
    final double y = StrictGeomUtility.toExternalValue(box.getY());
    final double w = StrictGeomUtility.toExternalValue(box.getWidth());
    final double h = StrictGeomUtility.toExternalValue(box.getHeight());
    final Rectangle2D boxArea = getBoxArea();
    boxArea.setFrame(x, y, w, h);
    g2.draw(boxArea);
    return false;
  }
}

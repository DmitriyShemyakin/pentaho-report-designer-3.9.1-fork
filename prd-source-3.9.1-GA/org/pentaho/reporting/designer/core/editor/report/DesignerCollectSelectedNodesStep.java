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

import org.pentaho.reporting.designer.core.model.ModelUtility;
import org.pentaho.reporting.engine.classic.core.layout.model.BlockRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.CanvasRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.InlineRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableReplacedContentBox;
import org.pentaho.reporting.engine.classic.core.layout.output.CollectSelectedNodesStep;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class DesignerCollectSelectedNodesStep extends CollectSelectedNodesStep
{
  public DesignerCollectSelectedNodesStep()
  {
    setStrictSelection(false);
  }

  protected boolean startCanvasBox(final CanvasRenderBox box)
  {
    if (ModelUtility.isHideInLayoutGui(box) == true)
    {
      return false;
    }

    return super.startCanvasBox(box);
  }

  protected void processOtherNode(final RenderNode node)
  {
    if (ModelUtility.isHideInLayoutGui(node) == true)
    {
      return;
    }
    super.processOtherNode(node);
  }

  protected boolean startBlockBox(final BlockRenderBox box)
  {
    if (ModelUtility.isHideInLayoutGui(box) == true)
    {
      return false;
    }
    return super.startBlockBox(box);
  }

  protected boolean startInlineBox(final InlineRenderBox box)
  {
    if (ModelUtility.isHideInLayoutGui(box) == true)
    {
      return false;
    }
    return super.startInlineBox(box);
  }

  protected void processRenderableContent(final RenderableReplacedContentBox box)
  {
    if (ModelUtility.isHideInLayoutGui(box) == true)
    {
      return;
    }
    super.processRenderableContent(box);
  }

  protected boolean startOtherBox(final RenderBox box)
  {
    if (ModelUtility.isHideInLayoutGui(box) == true)
    {
      return false;
    }
    return super.startOtherBox(box);
  }

  protected boolean startRowBox(final RenderBox box)
  {
    if (ModelUtility.isHideInLayoutGui(box) == true)
    {
      return false;
    }
    return super.startRowBox(box);
  }

}

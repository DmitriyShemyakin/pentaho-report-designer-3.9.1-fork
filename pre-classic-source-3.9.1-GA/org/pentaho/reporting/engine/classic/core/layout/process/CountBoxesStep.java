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
 * Copyright (c) 2005-2011 Pentaho Corporation.  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.layout.process;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.filter.types.AutoLayoutBoxType;
import org.pentaho.reporting.engine.classic.core.layout.FileModelPrinter;
import org.pentaho.reporting.engine.classic.core.layout.model.BlockRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.CanvasRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.FinishedRenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.InlineRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.libraries.base.util.DebugLog;

public class CountBoxesStep extends IterateStructuralProcessStep
{
  private int totalCount;
  private int finishedBoxes;
  private int autoBoxes;
  private boolean enabled;

  public CountBoxesStep()
  {
    enabled = "true".equals(ClassicEngineBoot.getInstance().getGlobalConfig().getConfigProperty
        ("org.pentaho.reporting.engine.classic.core.layout.process.EnableCountBoxesStep", "false"));
  }

  public void process(final LogicalPageBox box)
  {
    if (!enabled)
    {
      return;
    }

    totalCount = 0;
    finishedBoxes = 0;
    autoBoxes = 0;
    startProcessing(box);
    DebugLog.log("CountBoxes: Total=" + totalCount + "; finished=" + finishedBoxes + "; auto=" + autoBoxes + " - " +
        "Finished-Ratio: " + (finishedBoxes / (double) totalCount * 100f) +
        " AutoRatio: " + (autoBoxes / (double) totalCount * 100f));

    if ((autoBoxes / (double) totalCount * 100f) > 90)
    {
      FileModelPrinter.print(box);
      System.exit(0);
    }
  }

  protected void count(final RenderNode node)
  {
    totalCount += 1;
    if (node instanceof FinishedRenderNode)
    {
      finishedBoxes += 1;
    }
    if (node.getElementType() instanceof AutoLayoutBoxType)
    {
      autoBoxes += 1;
    }
  }

  protected boolean startCanvasBox(final CanvasRenderBox box)
  {
    count(box);
    return true;
  }

  protected boolean startBlockBox(final BlockRenderBox box)
  {
    count(box);
    return true;
  }

  protected boolean startInlineBox(final InlineRenderBox box)
  {
    count(box);
    return true;
  }

  protected boolean startOtherBox(final RenderBox box)
  {
    count(box);
    return true;
  }

  protected boolean startRowBox(final RenderBox box)
  {
    count(box);
    return true;
  }

  protected void processOtherNode(final RenderNode node)
  {
    count(node);
  }
}

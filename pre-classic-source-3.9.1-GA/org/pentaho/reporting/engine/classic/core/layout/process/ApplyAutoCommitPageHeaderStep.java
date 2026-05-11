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
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.ParagraphRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableReplacedContentBox;

/**
 * Marks the Page-Header as commited and as seen. Page-headers are generated on the fly and are not part of the common
 * report-processing system. Therefore they must be printed as soon as there is some commitable content.
 *
 * @author Thomas Morgner
 */
public final class ApplyAutoCommitPageHeaderStep extends IterateStructuralProcessStep
{
  private ApplyAutoCommitStep autoCommitStep;
  private boolean hasCommitableContent;

  public ApplyAutoCommitPageHeaderStep()
  {
    autoCommitStep = new ApplyAutoCommitStep();
  }

  public boolean compute(final LogicalPageBox pageBox)
  {
    hasCommitableContent = false;
    processBoxChilds(pageBox);
    if (hasCommitableContent)
    {
      autoCommitStep.compute(pageBox.getWatermarkArea());
      autoCommitStep.compute(pageBox.getHeaderArea());
      return true;
    }
    else
    {
      return false;
    }
  }

  public void commitAll(final LogicalPageBox pageBox)
  {
    processBoxChilds(pageBox);
    autoCommitStep.compute(pageBox.getWatermarkArea());
    autoCommitStep.compute(pageBox.getHeaderArea());
    autoCommitStep.compute(pageBox.getFooterArea());
    autoCommitStep.compute(pageBox.getRepeatFooterArea());
  }

  protected void processParagraphChilds(final ParagraphRenderBox box)
  {
    processBoxChilds(box);
  }

  private boolean processBox(final RenderBox box)
  {
    if (hasCommitableContent == true || box.isCommited())
    {
      hasCommitableContent = true;
      return false;
    }

    return true;
  }

  protected void processRenderableContent(final RenderableReplacedContentBox box)
  {
    processBox(box);
  }

  protected boolean startCanvasBox(final CanvasRenderBox box)
  {
    return processBox(box);
  }

  protected boolean startBlockBox(final BlockRenderBox box)
  {
    return processBox(box);
  }

  protected boolean startInlineBox(final InlineRenderBox box)
  {
    return processBox(box);
  }

  protected boolean startOtherBox(final RenderBox box)
  {
    return processBox(box);
  }

  protected boolean startRowBox(final RenderBox box)
  {
    return processBox(box);
  }
}

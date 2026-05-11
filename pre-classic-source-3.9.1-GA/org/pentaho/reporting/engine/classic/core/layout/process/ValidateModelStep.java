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
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;

/**
 * This remains empty, until we start implementing the multi-column steps or some more advanced breakability checks.
 *
 * @author Thomas Morgner
 */
public class ValidateModelStep extends IterateStructuralProcessStep
{
  private static final Log logger = LogFactory.getLog(ValidateModelStep.class);
  
  private boolean result;

  public ValidateModelStep()
  {
  }

  public boolean isLayoutable(final LogicalPageBox root)
  {
    result = true;
    // do not validate the header or footer or watermark sections..
    processBoxChilds(root);
    return result;
  }

  protected boolean startCanvasBox(final CanvasRenderBox box)
  {
    if (result == false)
    {
      return false;
    }

    if (box.isCacheValid())
    {
      return false;
    }

    if (box.isOpen())
    {
      if (logger.isDebugEnabled())
      {
        logger.debug("Canvas: Box is open: " + box);
      }
      result = false;
      return false;
    }

    if (box.getAppliedContentRefCount() == 0)
    {
      return false;
    }

    return true;
  }

  protected boolean startBlockBox(final BlockRenderBox box)
  {
    if (result == false)
    {
      return false;
    }

    if (box.isCacheValid())
    {
      return false;
    }

    if (box.isOpen())
    {
      if (box.getNext() != null)
      {
        // if this box is not the last box in a sequence of boxes, then we cannot finish the layouting
        if (logger.isDebugEnabled())
        {
          logger.debug("Block: Box is open with next element pending : " + box);
        }
        result = false;
        return false;
      }
      else if (box.getStaticBoxLayoutProperties().isPlaceholderBox())
      {
        if (box.getFirstChild() == null)
        {
          if (logger.isDebugEnabled())
          {
            logger.debug("Block: Box is placeholder : " + box);
          }
          result = false;
          return false;
        }
      }
    }
    else if (box.getAppliedContentRefCount() == 0)
    {
      return false;
    }

    return true;
  }

  protected boolean startInlineBox(final InlineRenderBox box)
  {
    if (result == false)
    {
      return false;
    }

    if (box.isCacheValid())
    {
      return false;
    }

    if (box.isOpen())
    {
      if (logger.isDebugEnabled())
      {
        logger.debug("Inline: Box is open : " + box);
      }
      result = false;
      return false;
    }
    if (box.getAppliedContentRefCount() == 0)
    {
      return false;
    }


    return true;
  }

  protected boolean startRowBox(final RenderBox box)
  {
    if (result == false)
    {
      return false;
    }

    if (box.isCacheValid())
    {
      return false;
    }

    if (box.isOpen())
    {
      if (logger.isDebugEnabled())
      {
        logger.debug("Row: Box is open : " + box);
      }
      result = false;
      return false;
    }
    if (box.getAppliedContentRefCount() == 0)
    {
      return false;
    }


    return true;
  }

  protected boolean startOtherBox(final RenderBox box)
  {
    return result;
  }

  protected boolean isResult()
  {
    return result;
  }

  protected void setResult(final boolean result)
  {
    this.result = result;
  }
}

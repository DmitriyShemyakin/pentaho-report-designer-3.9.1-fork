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

package org.pentaho.reporting.engine.classic.testcases.layout;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.layout.output.AbstractOutputProcessor;
import org.pentaho.reporting.engine.classic.core.layout.output.LogicalPageKey;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.ModelPrinter;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.base.PageableRenderer;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.graphics.internal.GraphicsOutputProcessorMetaData;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Creation-Date: 05.04.2007, 17:37:03
 *
 * @author Thomas Morgner
 */
public class DebugRenderer extends PageableRenderer
{
  private static final Log logger = LogFactory.getLog(DebugRenderer.class);
  private static class DebugOutputProcessor extends AbstractOutputProcessor
  {
    private OutputProcessorMetaData metaData;

    protected DebugOutputProcessor()
    {
      metaData = new GraphicsOutputProcessorMetaData (ClassicEngineBoot.getInstance().getGlobalConfig());
    }

    public OutputProcessorMetaData getMetaData()
    {
      return metaData;
    }

    protected void processPageContent(final LogicalPageKey logicalPageKey, final LogicalPageBox logicalPage)
    {
      logger.debug("Page-Offset: " + logicalPage.getPageOffset());
    }
  }

  public DebugRenderer()
  {
    super(new DebugOutputProcessor());
  }

  protected void debugPrint(final LogicalPageBox pageBox)
  {
    //ModelPrinter.print(pageBox);
  }

  public LogicalPageBox getPageBox()
  {
    return super.getPageBox();
  }

  public void markDirty()
  {
    super.markDirty();
  }
}

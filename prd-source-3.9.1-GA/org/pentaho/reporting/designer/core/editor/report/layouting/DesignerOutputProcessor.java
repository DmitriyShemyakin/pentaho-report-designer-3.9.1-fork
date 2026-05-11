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

package org.pentaho.reporting.designer.core.editor.report.layouting;

import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.PageGrid;
import org.pentaho.reporting.engine.classic.core.layout.output.ContentProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.output.LogicalPageKey;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.layout.output.PhysicalPageKey;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.base.AbstractPageableOutputProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.base.AllPageFlowSelector;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.base.PageFlowSelector;
import org.pentaho.reporting.libraries.base.config.Configuration;

/**
 * Todo: Document me!
 *
 * @author Thomas Morgner
 */
public class DesignerOutputProcessor extends AbstractPageableOutputProcessor
{
  private PageFlowSelector flowSelector;
  private OutputProcessorMetaData metadata;
  private LogicalPageBox logicalPage;

  public DesignerOutputProcessor(final Configuration configuration)
  {
    this.flowSelector = new AllPageFlowSelector(true);
    this.metadata = new DesignerOutputProcessorMetaData(configuration);
  }

  public void reset()
  {
    super.reset();
  }

  protected PageFlowSelector getFlowSelector()
  {
    return flowSelector;
  }

  protected void processPhysicalPage(final PageGrid pageGrid,
                                     final LogicalPageBox logicalPage,
                                     final int row,
                                     final int col,
                                     final PhysicalPageKey pageKey)
      throws ContentProcessingException
  {

  }

  public boolean isNeedAlignedPage()
  {
    // this guarantees that we get a copy of the logical page. 
    return true;
  }

  protected void processLogicalPage(final LogicalPageKey key,
                                    final LogicalPageBox logicalPage) throws ContentProcessingException
  {
    this.logicalPage = logicalPage;
  }

  protected void processPaginationContent(final LogicalPageKey logicalPageKey,
                                          final LogicalPageBox logicalPage) throws ContentProcessingException
  {
    this.logicalPage = logicalPage;
  }

  public OutputProcessorMetaData getMetaData()
  {
    return metadata;
  }

  public LogicalPageBox getLogicalPage()
  {
    return logicalPage;
  }
}

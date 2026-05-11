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

import org.pentaho.reporting.designer.core.editor.ReportRenderContext;
import org.pentaho.reporting.engine.classic.core.CrosstabGroup;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.output.ContentProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class CrosstabRenderer extends AbstractElementRenderer
{
  private CrosstabBandLayouter bandLayouter;
  
  public CrosstabRenderer(final CrosstabGroup group,
                          final ReportRenderContext renderContext)
  {
    super(group, renderContext);
    bandLayouter = new CrosstabBandLayouter(renderContext.getMasterReportElement());
  }

  protected LogicalPageBox performReportLayout() throws ReportProcessingException, ContentProcessingException
  {
    return bandLayouter.doCrosstabLayout((CrosstabGroup) getElement());
  }

  protected OutputProcessorMetaData getOutputProcessorMetaData()
  {
    return bandLayouter.getMetaData();
  }

  public CrosstabGroup getCrosstabGroup()
  {
    return (CrosstabGroup) getElement();
  }
}

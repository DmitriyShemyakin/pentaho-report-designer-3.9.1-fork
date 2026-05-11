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

import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.RootLevelBand;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.layout.InlineSubreportMarker;
import org.pentaho.reporting.engine.classic.core.layout.model.BlockRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.output.ContentProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.states.datarow.StaticDataRow;
import org.pentaho.reporting.engine.classic.core.wizard.DataSchema;
import org.pentaho.reporting.engine.classic.core.wizard.DefaultDataSchema;

/**
 * A class holding the current layouter state. This class acts as a single point of caching for all re-layouting
 * activities.
 *
 * @author Thomas Morgner
 */
public class BandLayouter
{
  private MasterReport report;
  private LayoutingContext context;

  public BandLayouter(final MasterReport report,
                      final LayoutingContext context)
  {
    this.report = report;
    this.context = context;
  }

  public OutputProcessorMetaData getMetaData()
  {
    return context.getMetaData();
  }

  public LogicalPageBox doRootBandLayout(final Band band, final DataSchema dataSchema)
      throws ReportProcessingException, ContentProcessingException
  {
    synchronized (context)
    {
      final DesignerExpressionRuntime runtime = context.getRuntime();
      runtime.setContentBase(report.getContentBase());
      runtime.setDataSchema(dataSchema);
      
      final DesignerRenderer renderer = new DesignerRenderer(context.getOutputProcessor());
      renderer.startReport(report);
      final LogicalPageBox logicalPageBox = renderer.getPageBox();
      final DesignerLayoutBuilder builder = new DesignerLayoutBuilder(context.getMetaData());
      final BlockRenderBox contentArea = logicalPageBox.getContentArea();
      builder.startSection(contentArea, false);
      builder.add(contentArea, band, runtime, null);
      final InlineSubreportMarker[] subreportMarkers = builder.endSection(contentArea, contentArea);

      for (int i = 0; i < subreportMarkers.length; i++)
      {
        final InlineSubreportMarker marker = subreportMarkers[i];
        final RenderNode node =
            contentArea.findNodeById(marker.getInsertationPointId());
        if (node instanceof RenderBox)
        {
          final RenderBox box = (RenderBox) node;
          box.close();
        }
      }

      if (band instanceof RootLevelBand)
      {
        final RootLevelBand rlb = (RootLevelBand) band;
        final SubReport[] reports = rlb.getSubReports();
        if (reports.length > 0)
        {
          builder.startSection(contentArea, false);
          for (int i = 0; i < reports.length; i++)
          {
            final SubReport subReport = reports[i];
            builder.add(contentArea, subReport, runtime, null);
          }
          builder.endSection(contentArea, contentArea);
        }
      }

      renderer.endReport();
      renderer.applyAutoCommit();
      if (renderer.processPage(null, null, true) == false)
      {
        return null;
      }

      final RenderNode firstChild = context.getOutputProcessor().getLogicalPage();
      if (firstChild == null)
      {
        return null;
      }

      return logicalPageBox;
    }
  }
}

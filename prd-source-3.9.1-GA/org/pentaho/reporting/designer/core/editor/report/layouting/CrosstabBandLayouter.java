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

import javax.swing.table.DefaultTableModel;

import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.CrosstabGroup;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.EmptyReportException;
import org.pentaho.reporting.engine.classic.core.Group;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.function.OutputFunction;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.output.ContentProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.output.DefaultOutputFunction;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.StreamReportProcessor;
import org.pentaho.reporting.engine.classic.core.states.ReportState;
import org.pentaho.reporting.engine.classic.core.states.datarow.StaticDataRow;
import org.pentaho.reporting.engine.classic.core.wizard.DefaultDataSchema;
import org.pentaho.reporting.libraries.base.util.DebugLog;

/**
 * A class holding the current layouter state. This class acts as a single point of caching for all re-layouting
 * activities.
 *
 * @author Thomas Morgner
 */
public class CrosstabBandLayouter
{
  private static class DesignerReportProcessor extends StreamReportProcessor
  {
    private DesignerOutputProcessor outputProcessor;
    private ExpressionRuntime runtime;

    private DesignerReportProcessor(final MasterReport report,
                                    final DesignerOutputProcessor outputProcessor,
                                    final ExpressionRuntime runtime)
        throws ReportProcessingException
    {
      super(report, outputProcessor);
      this.outputProcessor = outputProcessor;
      this.runtime = runtime;
    }

    protected OutputFunction createLayoutManager()
    {
      final DesignerCrosstabOutputFunction outputFunction = new DesignerCrosstabOutputFunction(runtime);
      outputFunction.setRenderer(new DesignerRenderer(outputProcessor));
      return outputFunction;
    }
  }

  private static class DesignerCrosstabOutputFunction extends DefaultOutputFunction
  {
    private ExpressionRuntime runtime;

    private DesignerCrosstabOutputFunction(final ExpressionRuntime runtime)
    {
      this.runtime = runtime;
    }

    public ExpressionRuntime getRuntime()
    {
      return runtime;
    }

    protected void updateHeaderArea(final ReportState state) throws ReportProcessingException
    {
      // skip, do nothing at all.
    }

    public void updateFooterArea(final ReportEvent event) throws ReportProcessingException
    {
      // skip, do nothing at all.
    }

    protected void print(final ExpressionRuntime dataRow, final Band band) throws ReportProcessingException
    {
//      System.out.println(band);
//      super.print(dataRow, band);
      // argh, the crosstab code directly interacts with the renderer, no need to channel printing through
      // this method.
    }
  }

  private OutputProcessorMetaData metaData;
  private DesignerOutputProcessor outputProcessor;
  private MasterReport report;
  private DesignerExpressionRuntime runtime;

  public CrosstabBandLayouter(final MasterReport report)
  {
    this.report = report;
    this.outputProcessor = new DesignerOutputProcessor(report.getConfiguration());
    this.metaData = outputProcessor.getMetaData();

    final DefaultDataSchema schema = new DefaultDataSchema();
    final DataRow dataRow = new StaticDataRow();
    this.runtime = new DesignerExpressionRuntime(dataRow, schema, report);
  }

  public OutputProcessorMetaData getMetaData()
  {
    return metaData;
  }

  public LogicalPageBox doCrosstabLayout(final CrosstabGroup band)
      throws ReportProcessingException, ContentProcessingException
  {
    try
    {
      final MasterReport report = new MasterReport();
      report.setDataFactory(new TableDataFactory(report.getQuery(), new DefaultTableModel(1, 1)));
      report.setResourceManager(this.report.getResourceManager());
      report.setResourceBundleFactory(this.report.getResourceBundleFactory());
      report.setPageDefinition(this.report.getPageDefinition());
      report.setContentBase(this.report.getContentBase());
      report.setReportEnvironment(this.report.getReportEnvironment());
      final Group group = (Group) band.clone();
      report.setRootGroup(group);

      final DesignerOutputProcessor outputProcessor = new DesignerOutputProcessor(report.getConfiguration());
      final DesignerReportProcessor processor = new DesignerReportProcessor(report, outputProcessor, runtime);
      processor.processReport();
      final LogicalPageBox box = outputProcessor.getLogicalPage();
//      ModelPrinter.print(box);
      return box;
    }
    catch (CloneNotSupportedException er)
    {
      UncaughtExceptionsModel.getInstance().addException(er);
      return null;
    }
    catch (EmptyReportException er)
    {
      DebugLog.log("Empty report", er); // NON-NLS
      return null;
    }
  }
}

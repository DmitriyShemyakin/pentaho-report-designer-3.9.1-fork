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
 * Copyright (c) 2007 - 2009 Pentaho Corporation, .  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.testcases.table;

import java.awt.print.PageFormat;
import java.net.URL;
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.SimplePageDefinition;
import org.pentaho.reporting.engine.classic.core.function.ProcessingContext;
import org.pentaho.reporting.engine.classic.core.layout.Renderer;
import org.pentaho.reporting.engine.classic.core.layout.output.ContentProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.output.DefaultProcessingContext;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.helper.HtmlOutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.util.PageFormatFactory;
import org.pentaho.reporting.engine.classic.testcases.layout.DebugExpressionRuntime;
import org.pentaho.reporting.engine.classic.testcases.table.model.ResultTable;
import org.pentaho.reporting.engine.classic.testcases.table.model.SourceChunk;
import org.pentaho.reporting.engine.classic.testcases.table.model.ValidationSequence;
import org.pentaho.reporting.engine.classic.testcases.table.parser.TableTestSpecXmlResourceFactory;
import org.pentaho.reporting.libraries.base.config.HierarchicalConfiguration;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceKeyCreationException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

/**
 * Creation-Date: 17.08.2007, 17:34:13
 *
 * @author Thomas Morgner
 */
public class TestRuntime
{
  private static final Log logger = LogFactory.getLog(TestRuntime.class);
  private ValidationSequence sequence;

  public TestRuntime(final ValidationSequence sequence)
  {
    this.sequence = sequence;
  }

  public static void main(final String[] args) throws ResourceException, ContentProcessingException, ReportProcessingException
  {


    ClassicEngineBoot.getInstance().start();
//    runTest("background-color.tabletest");
//    runTest("background-splitting.tabletest");
//    runTest("border-splitting-simple.tabletest");
//    runTest("border-overlay-simple.tabletest");
//    runTest("border-splitting-overlapping.tabletest");
//    runTest("legacy-lines-border.tabletest");
//    runTest("legacy-round-rectangles.tabletest"); // Excel does not support round-rects, so this test will fail
    runTest("legacy-lines-bug.tabletest");
  }

  private static void runTest(final String filename)
      throws ResourceException, ContentProcessingException, ReportProcessingException
  {
    logger.debug("Processing " + filename);
    final ResourceManager resourceManager = new ResourceManager();
    resourceManager.registerDefaults();
    resourceManager.registerFactory(new TableTestSpecXmlResourceFactory());


    final URL url = TestRuntime.class.getResource(filename);
    final Resource resource = resourceManager.createDirectly(url, ValidationSequence.class);
    final ValidationSequence sequence = (ValidationSequence) resource.getResource();

    final TestRuntime runtime = new TestRuntime(sequence);

    final HierarchicalConfiguration config = new HierarchicalConfiguration(
        ClassicEngineBoot.getInstance().getGlobalConfig());
    config.setConfigProperty("org.pentaho.reporting.engine.classic.core.modules.output.table.base.StrictLayout",
        "false");
    final OutputProcessorMetaData metaData = new HtmlOutputProcessorMetaData(config,
        HtmlOutputProcessorMetaData.PAGINATION_NONE);
    runtime.run(metaData);
  }


  public void run(final OutputProcessorMetaData metaData) throws ResourceKeyCreationException, ContentProcessingException, ReportProcessingException
  {
    // Set up the process ..
    final PageFormatFactory fmFactory = PageFormatFactory.getInstance();
    final PageFormat pageFormat = new PageFormat();
    pageFormat.setPaper(fmFactory.createPaper((double) sequence.getPageWidth(), 1000d));

    final SimplePageDefinition pageDefinition = new SimplePageDefinition(pageFormat);
    final ProcessingContext processingContext = new DefaultProcessingContext();
    final DebugExpressionRuntime runtime = new DebugExpressionRuntime(new DefaultTableModel(), 0, processingContext);

    final TableDebugOutputProcessor outputProcessor = new TableDebugOutputProcessor(metaData);
    final TableDebugRenderer flowRenderer = new TableDebugRenderer(outputProcessor);
    final MasterReport report = new MasterReport();
    report.setPageDefinition(pageDefinition);
    flowRenderer.startReport(report);
    // execute .. (maybe it is not pretty, "... but it works")
    final ArrayList list = sequence.getContents();
    for (int i = 0; i < list.size(); i++)
    {
      final Object o = list.get(i);
      if (o instanceof SourceChunk)
      {
        final SourceChunk chunk = (SourceChunk) o;
        flowRenderer.startSection(Renderer.TYPE_NORMALFLOW);
        flowRenderer.add(chunk.getRootBand(), runtime, null);
        flowRenderer.endSection();
      }
      else if (o instanceof ResultTable)
      {
        // perform the layouting first.
        final ResultTable chunk = (ResultTable) o;
        outputProcessor.setResultTable(chunk);
        flowRenderer.processPage(null, new Object(), true);
        outputProcessor.setResultTable(null);
      }
    }

    logger.debug("All ok");
  }
}

package org.pentaho.reporting.designer.core.editor.report.layouting;

import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.states.datarow.StaticDataRow;
import org.pentaho.reporting.engine.classic.core.wizard.DefaultDataSchema;

/**
 * Todo: Document me!
 * <p/>
 * Date: 23.06.2010
 * Time: 14:34:26
 *
 * @author Thomas Morgner.
 */
public class LayoutingContext
{
  private DesignerOutputProcessor outputProcessor;
  private OutputProcessorMetaData metaData;
  private DesignerExpressionRuntime runtime;

  public LayoutingContext(final MasterReport report)
  {
    this.outputProcessor = new DesignerOutputProcessor(report.getConfiguration());
    this.metaData = outputProcessor.getMetaData();

    final DefaultDataSchema schema = new DefaultDataSchema();
    final DataRow dataRow = new StaticDataRow();
    this.runtime = new DesignerExpressionRuntime(dataRow, schema, report);
  }

  public DesignerOutputProcessor getOutputProcessor()
  {
    return outputProcessor;
  }

  public OutputProcessorMetaData getMetaData()
  {
    return metaData;
  }

  public DesignerExpressionRuntime getRuntime()
  {
    return runtime;
  }
}

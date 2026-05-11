package org.pentaho.reporting.designer.extensions.toc;

import org.pentaho.reporting.designer.core.editor.report.ReportElementDragHandler;
import org.pentaho.reporting.designer.core.editor.report.ReportElementInlineEditor;
import org.pentaho.reporting.designer.core.editor.report.elements.DefaultReportElementEditor;

/**
 * Todo: Document me!
 *
 * @author Thomas Morgner.
 */
public class IndexReportElementEditor extends DefaultReportElementEditor
{
  public IndexReportElementEditor()
  {
  }

  public ReportElementInlineEditor createInlineEditor()
  {
    return new IndexReportElementInlineEditor();
  }

  public ReportElementDragHandler createDragHandler()
  {
    return new IndexReportElementDragHandler();
  }
}
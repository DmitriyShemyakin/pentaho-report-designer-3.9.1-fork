package org.pentaho.reporting.engine.classic.core.layout.output;

import org.pentaho.reporting.engine.classic.core.filter.types.bands.SubReportType;
import org.pentaho.reporting.engine.classic.core.layout.ModelPrinter;
import org.pentaho.reporting.engine.classic.core.layout.model.BlockRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.CanvasRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.InlineRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.process.IterateStructuralProcessStep;

/**
 * Todo: Document me!
 * <p/>
 * Date: 14.10.2010
 * Time: 14:58:17
 *
 * @author Thomas Morgner.
 */
public class ValidateSafeToStoreStateStep extends IterateStructuralProcessStep
{
  private boolean safeToStore;

  public ValidateSafeToStoreStateStep()
  {
  }

  public boolean isSafeToStore(final LogicalPageBox box)
  {
    safeToStore = true;
   // ModelPrinter.print(box);
    processBoxChilds(box);
    return safeToStore;
  }

  protected boolean startCanvasBox(final CanvasRenderBox box)
  {
    return startBox(box);
  }

  private boolean startBox(final RenderBox box)
  {
    if (safeToStore == false)
    {
      return false;
    }

    if (box.getStaticBoxLayoutProperties().isPlaceholderBox())
    {
      if (box.getNodeType() == LayoutNodeTypes.TYPE_BOX_CONTENTPLACEHOLDER)
      {
        // inline subreport
        safeToStore = false;
        return false;
      }
//      if (box.getElementType() instanceof SubReportType)
//      {
//        // Banded subreport
//        safeToStore = false;
//        return false;
//      }
    }
    return true;
  }

  protected boolean startBlockBox(final BlockRenderBox box)
  {
    return startBox(box);
  }

  protected boolean startInlineBox(final InlineRenderBox box)
  {
    return startBox(box);
  }

  protected boolean startRowBox(final RenderBox box)
  {
    return startBox(box);
  }

  protected boolean startOtherBox(final RenderBox box)
  {
    return startBox(box);
  }
}

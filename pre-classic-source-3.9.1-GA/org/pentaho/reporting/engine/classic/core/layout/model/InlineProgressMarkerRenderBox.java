package org.pentaho.reporting.engine.classic.core.layout.model;

import org.pentaho.reporting.engine.classic.core.states.ReportStateKey;

/**
 * Todo: Document me!
 * <p/>
 * Date: 01.09.2009
 * Time: 20:26:47
 *
 * @author Thomas Morgner.
 */
public class InlineProgressMarkerRenderBox extends InlineRenderBox
{
  public InlineProgressMarkerRenderBox()
  {
  }

  public int getNodeType()
  {
    return LayoutNodeTypes.TYPE_BOX_INLINE_PROGRESS_MARKER;
  }

  public void setStateKey(final ReportStateKey stateKey)
  {
    super.setStateKey(stateKey);
  }
}

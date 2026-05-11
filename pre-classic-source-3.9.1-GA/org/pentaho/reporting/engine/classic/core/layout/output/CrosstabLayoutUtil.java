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
 * Copyright (c) 2009 Pentaho Corporation..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.layout.output;

import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.layout.Renderer;
import org.pentaho.reporting.engine.classic.core.layout.model.BlockRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.style.CrosstabBoxStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class CrosstabLayoutUtil
{
  private static StyleSheet boxStyle;

  private static StyleSheet getBoxStyle()
  {
    if (boxStyle == null)
    {
      boxStyle = new CrosstabBoxStyleSheet();
    }
    return boxStyle;
  }

  private CrosstabLayoutUtil()
  {
  }

  public static void handleFinishPending(final DefaultOutputFunction outputFunction,
                                         final ReportEvent event)
      throws ReportProcessingException
  {
    final PreparedCrosstabLayout preparedCrosstabLayout = outputFunction.getCurrentCrosstabLayout();
    if (preparedCrosstabLayout.isFinishPending())
    {
      final Renderer renderer = outputFunction.getRenderer();
      outputFunction.updateFooterArea(event);

      if (preparedCrosstabLayout.getRowCount() == 0)
      {
        renderer.startSection(Renderer.TYPE_NORMALFLOW);
        renderer.add(preparedCrosstabLayout.getPrintableHeaderBox());
        renderer.endSection();
        outputFunction.addSubReportMarkers(preparedCrosstabLayout.getHeaderSubReportMarker());
      }

      renderer.startSection(Renderer.TYPE_NORMALFLOW);
      renderer.add(preparedCrosstabLayout.getPrintableDataBox());
      renderer.endSection();
      outputFunction.addSubReportMarkers(preparedCrosstabLayout.getDataSubReportMarker());

      preparedCrosstabLayout.setFinishPending(false);
      preparedCrosstabLayout.setRowCount(preparedCrosstabLayout.getRowCount() + 1);
    }
  }

  public static RenderBox getSlot(final RenderBox container, final int position)
  {
    int runPosition = 0;
    RenderBox child = (RenderBox) container.getFirstChild();
    while (child != null)
    {
      if (runPosition == position)
      {
        return child;
      }
      child = (RenderBox) child.getNext();
      runPosition += 1;
    }

    for (int i = runPosition; i <= position; i++)
    {
      final BlockRenderBox slotBox = new BlockRenderBox(getBoxStyle());
      slotBox.setName("Slot-" + i);
      container.addChild(slotBox);
      if (i == position)
      {
        return slotBox;
      }
    }

    throw new IllegalArgumentException("You should never end up here");
  }

}

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

import java.util.ArrayList;

import org.pentaho.reporting.engine.classic.core.layout.InlineSubreportMarker;
import org.pentaho.reporting.engine.classic.core.layout.model.BlockRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RowRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.style.CrosstabHeaderStyleSheet;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class PreparedCrosstabLayout implements Cloneable
{
  private InstanceID columnHeaderTitleBoxId;
  private InstanceID columnHeaderBoxId;
  private InstanceID rowHeaderTitleBoxId;
  private InstanceID rowFooterTitleBoxId;

  private InstanceID rowHeaderBoxId;
  private InstanceID rowFooterBoxId;
  private InstanceID cellDataBoxId;

  private RowRenderBox headerBox;
  private RowRenderBox dataBox;

  private ArrayList headerSubReports;
  private ArrayList dataSubReports;
  private boolean finishPending;
  private int rowCount;

  public PreparedCrosstabLayout()
  {
    headerSubReports = new ArrayList();
    dataSubReports = new ArrayList();

    final CrosstabHeaderStyleSheet styleSheet = new CrosstabHeaderStyleSheet();
    final RenderBox rowFooterTitleBox = new RowRenderBox(styleSheet);
    rowFooterTitleBox.setName("rowFooterTitleBox");
    final RenderBox rowHeaderTitleBox = new RowRenderBox(styleSheet);
    rowHeaderTitleBox.setName("rowHeaderTitleBox");
    final RenderBox columnHeaderTitleBox = new RowRenderBox(styleSheet);
    columnHeaderTitleBox.setName("columnHeaderTitleBox");
    final RenderBox columnHeaderBox = new RowRenderBox(styleSheet);
    columnHeaderBox.setName("columnHeaderBox");

    rowFooterTitleBoxId = rowFooterTitleBox.getInstanceId();
    rowHeaderTitleBoxId = rowHeaderTitleBox.getInstanceId();
    columnHeaderTitleBoxId = columnHeaderTitleBox.getInstanceId();
    columnHeaderBoxId = columnHeaderBox.getInstanceId();

    final RenderBox rowHeaderBox = new RowRenderBox(styleSheet);
    rowHeaderBox.setName("rowHeaderBox");
    final RenderBox rowFooterBox = new RowRenderBox(styleSheet);
    rowFooterBox.setName("rowFooterBox");
    final RenderBox cellDataBox = new RowRenderBox(styleSheet);
    cellDataBox.setName("cellDataBox");

    rowHeaderBoxId = rowHeaderBox.getInstanceId();
    rowFooterBoxId = rowFooterBox.getInstanceId();
    cellDataBoxId = cellDataBox.getInstanceId();

    final BlockRenderBox columnHeaderArea = new BlockRenderBox(styleSheet);
    columnHeaderArea.setName("columnHeaderArea");
    columnHeaderArea.addChild(columnHeaderTitleBox);
    columnHeaderArea.addChild(columnHeaderBox);
    columnHeaderArea.close();

    headerBox = new RowRenderBox(styleSheet);
    headerBox.setName("headerBox");
    headerBox.addChild(rowHeaderTitleBox);
    headerBox.addChild(columnHeaderArea);
    headerBox.addChild(rowFooterTitleBox);
    headerBox.close();

    dataBox = new RowRenderBox(styleSheet);
    dataBox.setName("dataBox");
    dataBox.addChild(rowHeaderBox);
    dataBox.addChild(cellDataBox);
    dataBox.addChild(rowFooterBox);
    dataBox.close();
  }

  public RenderBox getColumnHeaderTitleBox()
  {
    final RenderBox renderBox = (RenderBox) headerBox.findNodeById(columnHeaderTitleBoxId);
    if (renderBox == null)
    {
      throw new IllegalStateException();
    }
    return renderBox;
  }

  public RenderBox getColumnHeaderBox()
  {
    final RenderBox renderBox = (RenderBox) headerBox.findNodeById(columnHeaderBoxId);
    if (renderBox == null)
    {
      throw new IllegalStateException();
    }
    return renderBox;
  }

  public RenderBox getRowHeaderTitleBox()
  {
    final RenderBox renderBox = (RenderBox) headerBox.findNodeById(rowHeaderTitleBoxId);
    if (renderBox == null)
    {
      throw new IllegalStateException();
    }
    return renderBox;
  }

  public RenderBox getRowFooterTitleBox()
  {
    final RenderBox renderBox = (RenderBox) headerBox.findNodeById(rowFooterTitleBoxId);
    if (renderBox == null)
    {
      throw new IllegalStateException();
    }
    return renderBox;
  }

  public RenderBox getRowHeaderBox()
  {
    final RenderBox renderBox = (RenderBox) dataBox.findNodeById(rowHeaderBoxId);
    if (renderBox == null)
    {
      throw new IllegalStateException();
    }
    return renderBox;
  }

  public RenderBox getRowFooterBox()
  {
    final RenderBox renderBox = (RenderBox) dataBox.findNodeById(rowFooterBoxId);
    if (renderBox == null)
    {
      throw new IllegalStateException();
    }
    return renderBox;
  }

  public RenderBox getCellDataBox()
  {
    final RenderBox renderBox = (RenderBox) dataBox.findNodeById(cellDataBoxId);
    if (renderBox == null)
    {
      throw new IllegalStateException();
    }
    return renderBox;
  }

  public PreparedCrosstabLayout derive() throws CloneNotSupportedException
  {
    final PreparedCrosstabLayout layout = (PreparedCrosstabLayout) super.clone();
    layout.dataBox = (RowRenderBox) dataBox.derive(true);
    layout.headerBox = (RowRenderBox) headerBox.derive(true);
    return layout;
  }

  public Object clone() throws CloneNotSupportedException
  {
    final PreparedCrosstabLayout o = (PreparedCrosstabLayout) super.clone();
    o.dataBox = (RowRenderBox) dataBox.clone();
    o.headerBox = (RowRenderBox) headerBox.clone();
    return o;
  }

  public boolean isFinishPending()
  {
    return finishPending;
  }

  public void setFinishPending(final boolean finishPending)
  {
    this.finishPending = finishPending;
  }

  public InlineSubreportMarker[] getHeaderSubReportMarker()
  {
    return (InlineSubreportMarker[]) headerSubReports.toArray(new InlineSubreportMarker[headerSubReports.size()]);
  }

  public InlineSubreportMarker[] getDataSubReportMarker()
  {
    return (InlineSubreportMarker[]) dataSubReports.toArray(new InlineSubreportMarker[dataSubReports.size()]);
  }

  public int getRowCount()
  {
    return rowCount;
  }

  public void setRowCount(final int rowCount)
  {
    this.rowCount = rowCount;
  }

  public RenderBox getPrintableDataBox()
  {
    final RowRenderBox dataBox = (RowRenderBox) this.dataBox.derive(true);
    final RenderBox cellDataBox = (RenderBox) dataBox.findNodeById(cellDataBoxId);
    final RenderBox rowFooterBox = (RenderBox) dataBox.findNodeById(rowFooterBoxId);
    final RenderBox rowHeaderBox = (RenderBox) dataBox.findNodeById(rowHeaderBoxId);

    closeBoxAndChilds(rowFooterBox, false);
    closeBoxAndChilds(rowHeaderBox, false);
    closeBoxAndChilds(cellDataBox, false);
    return dataBox;
  }

  public RenderBox getPrintableHeaderBox()
  {
    final RowRenderBox headerBox = (RowRenderBox) this.headerBox.derive(true);
    final RenderBox rowFooterTitleBox = (RenderBox) headerBox.findNodeById(rowFooterTitleBoxId);
    final RenderBox rowHeaderTitleBox = (RenderBox) headerBox.findNodeById(rowHeaderTitleBoxId);
    final RenderBox columnHeaderTitleBox = (RenderBox) headerBox.findNodeById(columnHeaderTitleBoxId);
    final RenderBox columnHeaderBox = (RenderBox) headerBox.findNodeById(columnHeaderBoxId);
    closeBoxAndChilds(rowFooterTitleBox, false);
    closeBoxAndChilds(rowHeaderTitleBox, false);
    closeBoxAndChilds(columnHeaderTitleBox, true);
    closeBoxAndChilds(columnHeaderBox, true);
    return headerBox;
  }

  private void closeBoxAndChilds(final RenderBox box, final boolean secondLevel)
  {
    box.close();
    RenderBox child = (RenderBox) box.getFirstChild();
    while (child != null)
    {
      if (secondLevel)
      {
        closeBoxAndChilds(child, false);
      }
      else
      {
        child.close();
      }
      child = (RenderBox) child.getNext();

    }
  }

}

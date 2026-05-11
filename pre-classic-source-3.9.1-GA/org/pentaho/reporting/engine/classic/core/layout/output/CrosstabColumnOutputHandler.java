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

import org.pentaho.reporting.engine.classic.core.CrosstabColumnGroup;
import org.pentaho.reporting.engine.classic.core.ItemBand;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.layout.LayoutBuilder;
import org.pentaho.reporting.engine.classic.core.layout.Renderer;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class CrosstabColumnOutputHandler implements GroupOutputHandler
{
  // is relative to the dataitem at the start of the innermost row-group
  private int beginOfGroup;
  private int slotNumber;

  public CrosstabColumnOutputHandler(final int beginOfGroup,
                                     final int slotNumber)
  {
    this.beginOfGroup = beginOfGroup;
    this.slotNumber = slotNumber;
  }

  public int getSlotNumber()
  {
    return slotNumber;
  }

  public int getBeginOfGroup()
  {
    return beginOfGroup;
  }

  public void groupStarted(final DefaultOutputFunction outputFunction,
                           final ReportEvent event) throws ReportProcessingException
  {
    final PreparedCrosstabLayout preparedCrosstabLayout = outputFunction.getCurrentCrosstabLayout();

    final int columnSlotNumber = event.getState().getCurrentDataItem() - beginOfGroup;

    final Renderer renderer = outputFunction.getRenderer();
    final LayoutBuilder builder = renderer.createBufferedLayoutBuilder();
    final int gidx = event.getState().getCurrentGroupIndex();
    final CrosstabColumnGroup g = (CrosstabColumnGroup) event.getReport().getGroup(gidx);

    final RenderBox titleBox = preparedCrosstabLayout.getColumnHeaderTitleBox();
    final RenderBox titleColumnSlot = CrosstabLayoutUtil.getSlot(titleBox, columnSlotNumber);
    final RenderBox titleSlot = CrosstabLayoutUtil.getSlot(titleColumnSlot, slotNumber);
    titleSlot.clear();

    builder.startSection(titleSlot, true);
    builder.add(titleSlot, g.getTitleHeader(), outputFunction.getRuntime(), event.getState().getProcessKey());
    outputFunction.addSubReportMarkers(builder.endSection(titleSlot, titleSlot));

    final RenderBox headerBox = preparedCrosstabLayout.getColumnHeaderBox();
    final RenderBox headerColumnSlot = CrosstabLayoutUtil.getSlot(headerBox, columnSlotNumber);
    final RenderBox headerSlot = CrosstabLayoutUtil.getSlot(headerColumnSlot, slotNumber);
    headerSlot.clear();

    builder.startSection(headerSlot, true);
    builder.add(headerSlot, g.getHeader(), outputFunction.getRuntime(), event.getState().getProcessKey());
    outputFunction.addSubReportMarkers(builder.endSection(headerSlot, headerSlot));

  }

  public void itemsStarted(final DefaultOutputFunction outputFunction,
                           final ReportEvent event) throws ReportProcessingException
  {

  }

  public void itemsAdvanced(final DefaultOutputFunction outputFunction,
                            final ReportEvent event) throws ReportProcessingException
  {
    final PreparedCrosstabLayout preparedCrosstabLayout = outputFunction.getCurrentCrosstabLayout();

    final Renderer renderer = outputFunction.getRenderer();
    final LayoutBuilder builder = renderer.createBufferedLayoutBuilder();
    final ItemBand g = event.getReport().getItemBand();
    final int columnSlotNumber = event.getState().getCurrentDataItem() - beginOfGroup;

    final RenderBox dataBox = preparedCrosstabLayout.getCellDataBox();
    final RenderBox dataSlot = CrosstabLayoutUtil.getSlot(dataBox, columnSlotNumber);
    dataSlot.clear();

    builder.startSection(dataSlot, true);
    builder.add(dataSlot, g, outputFunction.getRuntime(), event.getState().getProcessKey());
    outputFunction.addSubReportMarkers(builder.endSection(dataSlot, dataSlot));
  }

  public void itemsFinished(final DefaultOutputFunction outputFunction,
                            final ReportEvent event) throws ReportProcessingException
  {

  }

  public void groupFinished(final DefaultOutputFunction outputFunction,
                            final ReportEvent event) throws ReportProcessingException
  {
    // no support for column footer yet
  }
}
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

import org.pentaho.reporting.engine.classic.core.CrosstabRowGroup;
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
public class CrosstabRowOutputHandler implements GroupOutputHandler
{
  private int slotNumber;

  public CrosstabRowOutputHandler(final int slotNumber)
  {
    this.slotNumber = slotNumber;
  }

  public int getSlotNumber()
  {
    return slotNumber;
  }

  public void groupStarted(final DefaultOutputFunction outputFunction,
                           final ReportEvent event) throws ReportProcessingException
  {
    CrosstabLayoutUtil.handleFinishPending(outputFunction, event);

    final PreparedCrosstabLayout preparedCrosstabLayout = outputFunction.getCurrentCrosstabLayout();
    final Renderer renderer = outputFunction.getRenderer();
    final int gidx = event.getState().getCurrentGroupIndex();
    final CrosstabRowGroup g = (CrosstabRowGroup) event.getReport().getGroup(gidx);

    final LayoutBuilder builder = renderer.createBufferedLayoutBuilder();

    final RenderBox titleBox = preparedCrosstabLayout.getRowHeaderTitleBox();
    final RenderBox titleSlot = CrosstabLayoutUtil.getSlot(titleBox, slotNumber);
    titleSlot.clear();

    builder.startSection(titleSlot, true);
    builder.add(titleSlot, g.getTitleHeader(), outputFunction.getRuntime(), event.getState().getProcessKey());
    outputFunction.addSubReportMarkers(builder.endSection(titleSlot, titleSlot));

    final RenderBox headerBox = preparedCrosstabLayout.getRowHeaderBox();
    final RenderBox headerSlot = CrosstabLayoutUtil.getSlot(headerBox, slotNumber);
    headerSlot.clear();

    builder.startSection(headerSlot, true);
    builder.add(headerSlot, g.getHeader(), outputFunction.getRuntime(), event.getState().getProcessKey());
    outputFunction.addSubReportMarkers(builder.endSection(headerSlot, headerSlot));

  }

  public void groupFinished(final DefaultOutputFunction outputFunction,
                            final ReportEvent event) throws ReportProcessingException
  {
    final PreparedCrosstabLayout preparedCrosstabLayout = outputFunction.getCurrentCrosstabLayout();

    final Renderer renderer = outputFunction.getRenderer();
    final LayoutBuilder builder = renderer.createBufferedLayoutBuilder();
    final int gidx = event.getState().getCurrentGroupIndex();
    final CrosstabRowGroup g = (CrosstabRowGroup) event.getReport().getGroup(gidx);

    final RenderBox titleBox = preparedCrosstabLayout.getRowFooterTitleBox();
    final RenderBox titleSlot = CrosstabLayoutUtil.getSlot(titleBox, slotNumber);
    titleSlot.clear();

    builder.startSection(titleSlot, true);
    builder.add(titleSlot, g.getTitleFooter(), outputFunction.getRuntime(), event.getState().getProcessKey());
    outputFunction.addSubReportMarkers(builder.endSection(titleSlot, titleSlot));

    final RenderBox footerBox = preparedCrosstabLayout.getRowFooterBox();
    final RenderBox footerSlot = CrosstabLayoutUtil.getSlot(footerBox, slotNumber);
    footerSlot.clear();

    builder.startSection(footerSlot, true);
    builder.add(footerSlot, g.getFooter(), outputFunction.getRuntime(), event.getState().getProcessKey());
    outputFunction.addSubReportMarkers(builder.endSection(footerSlot, footerSlot));

    preparedCrosstabLayout.setFinishPending(true);
  }

  public void itemsStarted(final DefaultOutputFunction outputFunction,
                           final ReportEvent event) throws ReportProcessingException
  {
    throw new ReportProcessingException("A crosstab-row cannot contain a detail band. Never.");
  }

  public void itemsAdvanced(final DefaultOutputFunction outputFunction,
                            final ReportEvent event) throws ReportProcessingException
  {
    throw new ReportProcessingException("A crosstab-row cannot contain a detail band. Never.");
  }

  public void itemsFinished(final DefaultOutputFunction outputFunction,
                            final ReportEvent event) throws ReportProcessingException
  {
    throw new ReportProcessingException("A crosstab-row cannot contain a detail band. Never.");
  }
}

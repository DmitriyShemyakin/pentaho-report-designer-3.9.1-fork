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

import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.Group;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.layout.Renderer;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class RelationalGroupOutputHandler implements GroupOutputHandler
{
  public void groupStarted(final DefaultOutputFunction outputFunction,
                           final ReportEvent event) throws ReportProcessingException
  {
    outputFunction.updateFooterArea(event);
    final Renderer renderer = outputFunction.getRenderer();
    final int gidx = event.getState().getCurrentGroupIndex();
    final Group g = event.getReport().getGroup(gidx);
    final Band b = g.getHeader();
    renderer.startGroup(g);
    renderer.startSection(Renderer.TYPE_NORMALFLOW);
    outputFunction.print(outputFunction.getRuntime(), b);
    outputFunction.addSubReportMarkers(renderer.endSection());
    renderer.startGroupBody(g.getBody());
  }

  public void itemsStarted(final DefaultOutputFunction outputFunction,
                           final ReportEvent event) throws ReportProcessingException
  {
    // activating this state after the page has ended is invalid.
    final int numberOfRows = event.getState().getNumberOfRows();
    final Renderer renderer = outputFunction.getRenderer();
    outputFunction.updateFooterArea(event);

    renderer.startSection(Renderer.TYPE_NORMALFLOW);
    outputFunction.print(outputFunction.getRuntime(), event.getReport().getDetailsHeader());
    outputFunction.addSubReportMarkers(renderer.endSection());

    if (numberOfRows == 0)
    {
      // ups, we have no data. Lets signal that ...
      renderer.startSection(Renderer.TYPE_NORMALFLOW);
      outputFunction.print(outputFunction.getRuntime(), event.getReport().getNoDataBand());
      outputFunction.addSubReportMarkers(renderer.endSection());
      // there will be no item-band printed.
    }
    
  }

  public void itemsAdvanced(final DefaultOutputFunction outputFunction,
                            final ReportEvent event) throws ReportProcessingException
  {
    final Renderer renderer = outputFunction.getRenderer();
    outputFunction.updateFooterArea(event);

    renderer.startSection(Renderer.TYPE_NORMALFLOW);
    outputFunction.print(outputFunction.getRuntime(), event.getReport().getItemBand());
    outputFunction.addSubReportMarkers(renderer.endSection());
  }

  public void itemsFinished(final DefaultOutputFunction outputFunction,
                            final ReportEvent event) throws ReportProcessingException
  {
    final Renderer renderer = outputFunction.getRenderer();
    outputFunction.updateFooterArea(event);

    renderer.startSection(Renderer.TYPE_NORMALFLOW);
    outputFunction.print(outputFunction.getRuntime(), event.getReport().getDetailsFooter());
    outputFunction.addSubReportMarkers(renderer.endSection());
  }

  public void groupFinished(final DefaultOutputFunction outputFunction,
                            final ReportEvent event) throws ReportProcessingException
  {
    final Renderer renderer = outputFunction.getRenderer();
    outputFunction.updateFooterArea(event);

    final int gidx = event.getState().getCurrentGroupIndex();
    final Group g = event.getReport().getGroup(gidx);
    final Band b = g.getFooter();

    renderer.endGroupBody();
    renderer.startSection(Renderer.TYPE_NORMALFLOW);
    outputFunction.print(outputFunction.getRuntime(), b);
    outputFunction.addSubReportMarkers(renderer.endSection());
    renderer.endGroup();
  }
}

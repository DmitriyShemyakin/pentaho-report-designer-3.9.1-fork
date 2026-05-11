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
 * Copyright (c) 2001 - 2009 Object Refinery Ltd, Pentaho Corporation and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.function;

import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.Group;
import org.pentaho.reporting.engine.classic.core.GroupBody;
import org.pentaho.reporting.engine.classic.core.ReportDefinition;
import org.pentaho.reporting.engine.classic.core.SubGroupBody;
import org.pentaho.reporting.engine.classic.core.event.PageEventListener;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.states.ReportState;

/**
 * The AbstractElementFormatFunction provides a common base implementation for all functions that need to modify the
 * report definition or the style of an report element or band during the report processing.
 * <p/>
 * The Expression retrieves the next root-level band that will be printed and uses this band as parameter for the {@link
 * AbstractElementFormatFunction#processRootBand(org.pentaho.reporting.engine.classic.core.Band)} method.
 *
 * @author Thomas Morgner
 */
public abstract class AbstractElementFormatFunction extends AbstractFunction
    implements PageEventListener, LayoutProcessorFunction
{
  /**
   * The name of the element that should be formatted.
   */
  private String element;

  /**
   * Creates an unnamed function. Make sure the name of the function is set using {@link #setName} before the function
   * is added to the report's function collection.
   */
  protected AbstractElementFormatFunction()
  {
  }

  /**
   * Sets the element name. The name denotes an element or band within the root-band or the root-band itself. It is
   * possible to define multiple elements with the same name to apply the modification to all of these elements.
   *
   * @param name The element name.
   * @see org.pentaho.reporting.engine.classic.core.function.FunctionUtilities#findAllElements(org.pentaho.reporting.engine.classic.core.Band,String)
   */
  public void setElement(final String name)
  {
    this.element = name;
  }

  /**
   * Returns the element name.
   *
   * @return The element name.
   * @see #setElement(String)
   */
  public String getElement()
  {
    return element;
  }

  /**
   * Processes the No-Data-Band.
   *
   * @param event the report event.
   */
  public void itemsStarted(final ReportEvent event)
  {
    if (FunctionUtilities.isLayoutLevel(event) == false)
    {
      // dont do anything if there is no printing done ...
      return;
    }
    final ReportDefinition definition = event.getReport();
    processRootBand(definition.getNoDataBand());
    processRootBand(definition.getDetailsHeader());

    processFooterBands(event.getState());
  }

  /**
   * Processes the ItemBand.
   *
   * @param event the event.
   */
  public void itemsAdvanced(final ReportEvent event)
  {
    if (FunctionUtilities.isLayoutLevel(event) == false)
    {
      // dont do anything if there is no printing done ...
      return;
    }
    final Band b = event.getReport().getItemBand();
    processRootBand(b);

    processFooterBands(event.getState());
  }

  /**
   * Receives notification that a group of item bands has been completed. <P> The itemBand is finished, the report
   * starts to close open groups.
   *
   * @param event The event.
   */
  public void itemsFinished(final ReportEvent event)
  {
    if (FunctionUtilities.isLayoutLevel(event) == false)
    {
      // dont do anything if there is no printing done ...
      return;
    }
    final ReportDefinition definition = event.getReport();
    processRootBand(definition.getDetailsFooter());

    processFooterBands(event.getState());
  }

  /**
   * Receives notification that report generation initializes the current run. <P> The event carries a
   * ReportState.Started state.  Use this to initialize the report.
   *
   * @param event The event.
   */
  public void reportInitialized(final ReportEvent event)
  {
    if (FunctionUtilities.isLayoutLevel(event) == false)
    {
      // dont do anything if there is no printing done ...
      return;
    }

    final boolean slottedHeaderMode =
        ("slotted".equals(getRuntime().getConfiguration().getConfigProperty
            ("org.pentaho.reporting.engine.classic.core.layout.HeaderPageBandMode", "slotted")));
    if (slottedHeaderMode)
    {
      final Band b = event.getReport().getPageHeader();
      processRootBand(b);
    }
  }

  /**
   * Processes the Report-Footer.
   *
   * @param event the event.
   */
  public void reportFinished(final ReportEvent event)
  {
    if (FunctionUtilities.isLayoutLevel(event) == false)
    {
      // dont do anything if there is no printing done ...
      return;
    }
    final Band b = event.getReport().getReportFooter();
    processRootBand(b);

    processFooterBands(event.getState());
  }

  /**
   * Processes the Report-Header.
   *
   * @param event the event.
   */
  public void reportStarted(final ReportEvent event)
  {
    if (FunctionUtilities.isLayoutLevel(event) == false)
    {
      // dont do anything if there is no printing done ...
      return;
    }
    final Band b = event.getReport().getReportHeader();
    processRootBand(b);

    processFooterBands(event.getState());
  }

  protected void processGroup(final Group group)
  {
    final Band b = group.getHeader();
    processRootBand(b);
  }

  /**
   * Processes the group header of the current group.
   *
   * @param event the event.
   */
  public void groupStarted(final ReportEvent event)
  {
    if (FunctionUtilities.isLayoutLevel(event) == false)
    {
      // dont do anything if there is no printing done ...
      return;
    }
    final Group group = FunctionUtilities.getCurrentGroup(event);
    processGroup(group);

    processFooterBands(event.getState());
  }

  /**
   * Processes the group footer of the current group.
   *
   * @param event the event.
   */
  public void groupFinished(final ReportEvent event)
  {
    if (FunctionUtilities.isLayoutLevel(event) == false)
    {
      // dont do anything if there is no printing done ...
      return;
    }
    final Band b = FunctionUtilities.getCurrentGroup(event).getFooter();
    processRootBand(b);

    processFooterBands(event.getState());
  }

  /**
   * Processes the page footer.
   *
   * @param event the event.
   */
  public void pageFinished(final ReportEvent event)
  {
    if (FunctionUtilities.isLayoutLevel(event) == false)
    {
      // dont do anything if there is no printing done ...
      return;
    }
    final Band b = event.getReport().getPageFooter();
    processRootBand(b);
  }

  /**
   * Processes the page header.
   *
   * @param event the event.
   */
  public void pageStarted(final ReportEvent event)
  {
    if (FunctionUtilities.isLayoutLevel(event) == false)
    {
      // dont do anything if there is no printing done ...
      return;
    }
    final Band b = event.getReport().getPageHeader();
    processRootBand(b);

    final Band w = event.getReport().getWatermark();
    processRootBand(w);

    processFooterBands(event.getState());
  }

  protected void processFooterBands(ReportState state)
  {
    while (state != null)
    {
      final ReportDefinition reportDefinition = state.getReport();
      processRootBand(reportDefinition.getPageFooter());
      processRootBand(reportDefinition.getDetailsFooter());
      Group g = reportDefinition.getRootGroup();
      while (g != null)
      {
        processRootBand(g.getFooter());
        final GroupBody body = g.getBody();
        if (body instanceof SubGroupBody == false)
        {
          break;
        }

        final SubGroupBody sgb = (SubGroupBody) body;
        g = sgb.getGroup();
      }
      state = state.getParentSubReportState();
    }

  }

  /**
   * Processes the root band for the current event. This method must be implemented by all subclasses and contains all
   * code necessary to update the style or structure of the given band. The update must be deterministic, calls must
   * result in the same layout for all calls for a given report processing state.
   *
   * @param b the band.
   */
  protected abstract void processRootBand(Band b);

  /**
   * Format-Functions usually are not expected to return anything.
   *
   * @return null, as format functions do not compute values.
   */
  public Object getValue()
  {
    return null;
  }
}

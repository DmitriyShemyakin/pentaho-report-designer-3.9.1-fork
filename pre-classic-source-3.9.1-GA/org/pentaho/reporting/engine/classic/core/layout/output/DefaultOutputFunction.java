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

package org.pentaho.reporting.engine.classic.core.layout.output;

import java.util.ArrayList;
import javax.swing.table.TableModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.CrosstabColumnGroup;
import org.pentaho.reporting.engine.classic.core.CrosstabRowGroup;
import org.pentaho.reporting.engine.classic.core.DetailsFooter;
import org.pentaho.reporting.engine.classic.core.DetailsHeader;
import org.pentaho.reporting.engine.classic.core.Group;
import org.pentaho.reporting.engine.classic.core.GroupFooter;
import org.pentaho.reporting.engine.classic.core.GroupHeader;
import org.pentaho.reporting.engine.classic.core.PageFooter;
import org.pentaho.reporting.engine.classic.core.PageHeader;
import org.pentaho.reporting.engine.classic.core.ReportDefinition;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.Section;
import org.pentaho.reporting.engine.classic.core.Watermark;
import org.pentaho.reporting.engine.classic.core.event.PageEventListener;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.function.AbstractFunction;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.function.FunctionProcessingException;
import org.pentaho.reporting.engine.classic.core.function.OutputFunction;
import org.pentaho.reporting.engine.classic.core.function.ProcessingContext;
import org.pentaho.reporting.engine.classic.core.layout.InlineSubreportMarker;
import org.pentaho.reporting.engine.classic.core.layout.Renderer;
import org.pentaho.reporting.engine.classic.core.states.ReportState;
import org.pentaho.reporting.engine.classic.core.states.datarow.MasterDataRow;
import org.pentaho.reporting.engine.classic.core.states.datarow.ReportDataRow;
import org.pentaho.reporting.engine.classic.core.states.process.SubReportProcessType;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.libraries.base.util.FastStack;

/**
 * Creation-Date: 08.04.2007, 16:22:18
 *
 * @author Thomas Morgner
 */
public class DefaultOutputFunction extends AbstractFunction
    implements OutputFunction, PageEventListener
{
  private static final Log logger = LogFactory.getLog(DefaultOutputFunction.class);
  private static final LayouterLevel[] EMPTY_LAYOUTER_LEVEL = new LayouterLevel[0];

  private ReportEvent currentEvent;
  private Renderer renderer;
  private boolean lastPagebreak;
  private DefaultLayoutPagebreakHandler pagebreakHandler;
  private ArrayList<InlineSubreportMarker> inlineSubreports;
  private FastStack<GroupOutputHandler> outputHandlers;
  private FastStack<PreparedCrosstabLayout> crosstabLayouts;
  private int beginOfRow;

  /**
   * Creates an unnamed function. Make sure the name of the function is set using {@link #setName} before the function
   * is added to the report's function collection.
   */
  public DefaultOutputFunction()
  {
    this.pagebreakHandler = new DefaultLayoutPagebreakHandler();
    this.inlineSubreports = new ArrayList<InlineSubreportMarker>();
    this.outputHandlers = new FastStack<GroupOutputHandler>();
    this.crosstabLayouts = new FastStack<PreparedCrosstabLayout>();
  }

  /**
   * Return the current expression value. <P> The value depends (obviously) on the expression implementation.
   *
   * @return the value of the function.
   */
  public Object getValue()
  {
    return null;
  }

  public void reportInitialized(final ReportEvent event)
  {
    // there can be no pending page-start, we just have started ...
    if (event.getState().getParentSubReportState() != null)
    {
      // except if we are a subreport, of course ..
      clearPendingPageStart(event);
    }

    // activating this state after the page has ended is invalid.
    setCurrentEvent(event);
    try
    {
      // activating this state after the page has ended is invalid.
      final ReportDefinition report = event.getReport();
      if (event.getState().isSubReportEvent() == false)
      {
        renderer.startReport(report);

        final ReportState reportState = event.getState();
        final ExpressionRuntime runtime = getRuntime();
        try
        {
          reportState.firePageStartedEvent(reportState.getEventCode());
        }
        finally
        {
          // restore the current event, as the page-started event will clear it ..
          setRuntime(runtime);
          setCurrentEvent(event);
        }
      }
      else
      {
        renderer.startSubReport(report, event.getState().getCurrentSubReportMarker().getInsertationPointId());
      }
    }
    catch (FunctionProcessingException fe)
    {
      throw fe;
    }
    catch (Exception e)
    {
      throw new FunctionProcessingException("ReportInitialized failed", e);
    }
    finally
    {
      clearCurrentEvent();
    }
  }

  /**
   * Receives notification that the report has started. Also invokes the start of the first page ... <P> Layout and draw
   * the report header after the PageStartEvent was fired.
   *
   * @param event the event.
   */
  public void reportStarted(final ReportEvent event)
  {
    clearPendingPageStart(event);

    // activating this state after the page has ended is invalid.
    setCurrentEvent(event);
    try
    {
      // activating this state after the page has ended is invalid.
      updateFooterArea(event);

      renderer.startSection(Renderer.TYPE_NORMALFLOW);
      final ReportDefinition report = event.getReport();
      print(getRuntime(), report.getReportHeader());
      addSubReportMarkers(renderer.endSection());
    }
    catch (FunctionProcessingException fe)
    {
      throw fe;
    }
    catch (Exception e)
    {
      throw new FunctionProcessingException("ReportStarted failed", e);
    }
    finally
    {
      clearCurrentEvent();
    }
  }

  public void addSubReportMarkers(final InlineSubreportMarker[] markers)
  {
    for (int i = 0; i < markers.length; i++)
    {
      final InlineSubreportMarker marker = markers[i];
      inlineSubreports.add(marker);
    }
  }


  /**
   * Receives notification that a group has started. <P> Prints the GroupHeader
   *
   * @param event Information about the event.
   */
  public void groupStarted(final ReportEvent event)
  {
    final int type = event.getType();
    if ((type & ReportEvent.CROSSTABBING_TABLE) == ReportEvent.CROSSTABBING_TABLE)
    {
      final GroupOutputHandler handler = new CrosstabOutputHandler();
      outputHandlers.push(handler);
    }
    else if ((type & ReportEvent.CROSSTABBING_OTHER) == ReportEvent.CROSSTABBING_OTHER)
    {
      final CrosstabOtherOutputHandler handler = new CrosstabOtherOutputHandler();
      outputHandlers.push(handler);
    }
    else if ((type & ReportEvent.CROSSTABBING_ROW) == ReportEvent.CROSSTABBING_ROW)
    {
      final CrosstabRowOutputHandler handler = new CrosstabRowOutputHandler(computeRowPosition(event));
      outputHandlers.push(handler);
      beginOfRow = event.getState().getCurrentDataItem();
    }
    else if ((type & ReportEvent.CROSSTABBING_COL) == ReportEvent.CROSSTABBING_COL)
    {
      final CrosstabColumnOutputHandler handler =
          new CrosstabColumnOutputHandler(beginOfRow, computeColumnPosition(event));
      outputHandlers.push(handler);
    }
    else
    {
      final RelationalGroupOutputHandler handler = new RelationalGroupOutputHandler();
      outputHandlers.push(handler);
    }

    clearPendingPageStart(event);

    // activating this state after the page has ended is invalid.
    setCurrentEvent(event);
    try
    {
      final GroupOutputHandler handler = outputHandlers.peek();
      handler.groupStarted(this, event);
    }
    catch (FunctionProcessingException fe)
    {
      throw fe;
    }
    catch (Exception e)
    {
      throw new FunctionProcessingException("GroupStarted failed", e);
    }
    finally
    {
      clearCurrentEvent();
    }
  }

  private int computeRowPosition(final ReportEvent event)
  {
    final ReportState state = event.getState();
    final int groupIndex = state.getCurrentGroupIndex();
    Group group = event.getReport().getGroup(groupIndex);
    int retval = -1;
    while (group instanceof CrosstabRowGroup)
    {
      retval += 1;
      final Section body = group.getParentSection();
      if (body == null)
      {
        throw new IllegalStateException("Invalid report model: Inner Crosstab-group without parent-body");
      }
      group = (Group) body.getParentSection();
    }
    return retval;
  }

  private int computeColumnPosition(final ReportEvent event)
  {
    final ReportState state = event.getState();
    final int groupIndex = state.getCurrentGroupIndex();
    Group group = event.getReport().getGroup(groupIndex);
    int retval = -1;
    while (group instanceof CrosstabColumnGroup)
    {
      retval += 1;
      final Section body = group.getParentSection();
      if (body == null)
      {
        throw new IllegalStateException("Invalid report model: Inner Crosstab-group without parent-body");
      }
      group = (Group) body.getParentSection();
    }
    return retval;
  }


  /**
   * Receives notification that a group of item bands is about to be processed. <P> The next events will be
   * itemsAdvanced events until the itemsFinished event is raised.
   *
   * @param event The event.
   */
  public void itemsStarted(final ReportEvent event)
  {
    clearPendingPageStart(event);

    setCurrentEvent(event);

    try
    {
      final GroupOutputHandler handler = outputHandlers.peek();
      handler.itemsStarted(this, event);
    }
    catch (FunctionProcessingException fe)
    {
      throw fe;
    }
    catch (Exception e)
    {
      throw new FunctionProcessingException("ItemsStarted failed", e);
    }
    finally
    {
      clearCurrentEvent();
    }
  }

  /**
   * Receives notification that a row of data is being processed. <P> prints the ItemBand.
   *
   * @param event Information about the event.
   */
  public void itemsAdvanced(final ReportEvent event)
  {
    clearPendingPageStart(event);

    setCurrentEvent(event);
    try
    {
      final GroupOutputHandler handler = outputHandlers.peek();
      handler.itemsAdvanced(this, event);
    }
    catch (FunctionProcessingException fe)
    {
      throw fe;
    }
    catch (Exception e)
    {
      throw new FunctionProcessingException("ItemsAdvanced failed", e);
    }
    finally
    {
      clearCurrentEvent();
    }
  }

  /**
   * Receives notification that a group of item bands has been completed. <P> The itemBand is finished, the report
   * starts to close open groups.
   *
   * @param event The event.
   */
  public void itemsFinished(final ReportEvent event)
  {
    clearPendingPageStart(event);

    setCurrentEvent(event);

    try
    {
      final GroupOutputHandler handler = outputHandlers.peek();
      handler.itemsFinished(this, event);
    }
    catch (FunctionProcessingException fe)
    {
      throw fe;
    }
    catch (Exception e)
    {
      throw new FunctionProcessingException("ItemsFinished failed", e);
    }
    finally
    {
      clearCurrentEvent();
    }
  }

  /**
   * Receives notification that a group has finished. <P> Prints the GroupFooter.
   *
   * @param event Information about the event.
   */
  public void groupFinished(final ReportEvent event)
  {
    clearPendingPageStart(event);

    setCurrentEvent(event);
    try
    {
      final GroupOutputHandler handler = outputHandlers.pop();
      handler.groupFinished(this, event);
    }
    catch (FunctionProcessingException fe)
    {
      throw fe;
    }
    catch (Exception e)
    {
      throw new FunctionProcessingException("GroupFinished failed", e);
    }
    finally
    {
      clearCurrentEvent();
    }
  }


  /**
   * Receives notification that the report has finished. <P> Prints the ReportFooter and forces the last pagebreak.
   *
   * @param event Information about the event.
   */
  public void reportFinished(final ReportEvent event)
  {
    clearPendingPageStart(event);

    setCurrentEvent(event);
    try
    {
      // a deep traversing event means, we are in a subreport ..

      // force that this last pagebreak ... (This is an indicator for the
      // pagefooter's print-on-last-page) This is highly unclean and may or
      // may not work ..
      final Band b = event.getReport().getReportFooter();
      renderer.startSection(Renderer.TYPE_NORMALFLOW);
      print(getRuntime(), b);
      addSubReportMarkers(renderer.endSection());

      if (event.isDeepTraversing() == false)
      {
        lastPagebreak = true;
      }
      updateFooterArea(event);
    }
    catch (FunctionProcessingException fe)
    {
      throw fe;
    }
    catch (Exception e)
    {
      throw new FunctionProcessingException("ReportFinished failed", e);
    }
    finally
    {
      clearCurrentEvent();
    }
  }

  /**
   * Receives notification that report generation has completed, the report footer was printed, no more output is done.
   * This is a helper event to shut down the output service.
   *
   * @param event The event.
   */
  public void reportDone(final ReportEvent event)
  {
    if (event.getState().isSubReportEvent() == false)
    {
      renderer.endReport();
    }
    else
    {
      renderer.endSubReport();
    }
  }

  private static LayoutExpressionRuntime createRuntime(final MasterDataRow masterRow,
                                                       final ReportState state,
                                                       final ProcessingContext processingContext)
  {
    final ReportDataRow reportDataRow = masterRow.getReportDataRow();
    final TableModel reportDataModel = reportDataRow.getReportData();

    return new LayoutExpressionRuntime
        (masterRow.getGlobalView(), masterRow.getDataSchema(), state, reportDataModel, processingContext);
  }

  private static LayouterLevel[] collectSubReportStates(final ReportState state,
                                                        final ProcessingContext processingContext)
  {
    if (processingContext == null)
    {
      throw new NullPointerException();
    }
    ReportState parentState = state.getParentSubReportState();
    if (parentState == null)
    {
      return EMPTY_LAYOUTER_LEVEL;
    }

    MasterDataRow dataRow = state.getFlowController().getMasterRow();
    dataRow = dataRow.getParentDataRow();
    if (dataRow == null)
    {
      throw new IllegalStateException("Parent-DataRow in a subreport-state must be defined.");
    }

    final ArrayList<LayouterLevel> stack = new ArrayList<LayouterLevel>();
    while (parentState != null)
    {
      if (parentState.isInlineProcess() == false)
      {
        final LayoutExpressionRuntime runtime = createRuntime(dataRow, parentState, processingContext);
        stack.add(new LayouterLevel(parentState.getReport(),
            parentState.getPresentationGroupIndex(), runtime, parentState.isInItemGroup()));
      }
      parentState = parentState.getParentSubReportState();
      dataRow = dataRow.getParentDataRow();
      if (dataRow == null)
      {
        throw new IllegalStateException("Parent-DataRow in a subreport-state must be defined.");
      }
    }
    return stack.toArray(new LayouterLevel[stack.size()]);
  }

  private int computeCurrentPage()
  {
    return renderer.getPageCount() + 1;
  }

  private boolean isPageHeaderPrinting(final Band b)
  {
    final boolean displayOnFirstPage = b.getStyle().getBooleanStyleProperty(BandStyleKeys.DISPLAY_ON_FIRSTPAGE);
    if (computeCurrentPage() == 1 && displayOnFirstPage == false)
    {
      return false;
    }

    final boolean displayOnLastPage = b.getStyle().getBooleanStyleProperty(BandStyleKeys.DISPLAY_ON_LASTPAGE);
    if (isLastPagebreak() && (displayOnLastPage == false))
    {
      return false;
    }

    return true;
  }

  protected boolean isLastPagebreak()
  {
    return lastPagebreak;
  }


  /**
   * Receives notification that a page has started. <P> This prints the PageHeader. If this is the first page, the
   * header is not printed if the pageheader style-flag DISPLAY_ON_FIRSTPAGE is set to false. If this event is known to
   * be the last pageStarted event, the DISPLAY_ON_LASTPAGE is evaluated and the header is printed only if this flag is
   * set to TRUE.
   * <p/>
   * If there is an active repeating GroupHeader, print the last one. The GroupHeader is searched for the current group
   * and all parent groups, starting at the current group and ascending to the parents. The first goupheader that has
   * the StyleFlag REPEAT_HEADER set to TRUE is printed.
   * <p/>
   * The PageHeader and the repeating GroupHeader are spooled until the first real content is printed. This way, the
   * LogicalPage remains empty until an other band is printed.
   *
   * @param event Information about the event.
   */
  public void pageStarted(final ReportEvent event)
  {
    // activating this state after the page has ended is invalid.
    setCurrentEvent(event);
    try
    {
      final int mask = ReportEvent.REPORT_INITIALIZED | ReportEvent.NO_PARENT_PASSING_EVENT;
      if (event.getState().isSubReportEvent() && (event.getType() & mask) == mask)
      {
        // if this is the artificial subreport-page-start event that is fired from the
        // init-report event handler, then do not rebuild the header if the page is not empty.
        if (renderer.isCurrentPageEmpty() == false ||
            renderer.validatePages() == Renderer.LayoutResult.LAYOUT_UNVALIDATABLE)
        {
          return;
        }
      }
      renderer.newPageStarted();
      updateHeaderArea(event.getState());
    }
    catch (FunctionProcessingException fe)
    {
      throw fe;
    }
    catch (Exception e)
    {
      throw new FunctionProcessingException("PageStarted failed", e);
    }
    finally
    {
      clearCurrentEvent();
    }
  }

  protected void updateHeaderArea(final ReportState givenState)
      throws ReportProcessingException
  {
    ReportState state = givenState;
    while (state != null && state.isInlineProcess())
    {
      state = state.getParentSubReportState();
    }
    if (state == null)
    {
      return;
    }

    final ProcessingContext processingContext = getRuntime().getProcessingContext();
    final ReportDefinition report = state.getReport();
    LayouterLevel[] levels = null;
    ExpressionRuntime runtime = null;
    final OutputProcessorMetaData metaData = renderer.getOutputProcessor().getMetaData();
    if (metaData.isFeatureSupported(OutputProcessorFeature.WATERMARK_SECTION))
    {
      renderer.startSection(Renderer.TYPE_WATERMARK);
      // a new page has started, so reset the cursor ...
      // Check the subreport for sticky watermarks ...
      levels = DefaultOutputFunction.collectSubReportStates(state, processingContext);

      for (int i = levels.length - 1; i >= 0; i -= 1)
      {
        final LayouterLevel level = levels[i];
        final ReportDefinition def = level.getReportDefinition();
        final Watermark watermark = def.getWatermark();
        if (watermark.isSticky())
        {
          if (isPageHeaderPrinting(watermark))
          {
            print(level.getRuntime(), watermark);
          }
          else
          {
            printEmptyRootLevelBand();
          }
        }
      }

      // and finally print the watermark of the subreport itself ..
      final Band watermark = report.getWatermark();
      if (isPageHeaderPrinting(watermark))
      {
        runtime = createRuntime(state.getFlowController().getMasterRow(), state, processingContext);
        print(runtime, watermark);
      }
      addSubReportMarkers(renderer.endSection());
    }

    if (metaData.isFeatureSupported(OutputProcessorFeature.PAGE_SECTIONS))
    {
      renderer.startSection(Renderer.TYPE_HEADER);
      // after printing the watermark, we are still at the top of the page.

      if (levels == null)
      {
        levels = DefaultOutputFunction.collectSubReportStates(state, processingContext);
      }

      for (int i = levels.length - 1; i >= 0; i -= 1)
      {
        // This is propably wrong (or at least incomplete) in case a subreport uses header or footer which should
        // not be printed with the report-footer or header ..
        final LayouterLevel level = levels[i];
        final ReportDefinition def = level.getReportDefinition();
        final PageHeader header = def.getPageHeader();
        if (header.isSticky())
        {
          if (isPageHeaderPrinting(header))
          {
            print(level.getRuntime(), header);
          }
          else
          {
            printEmptyRootLevelBand();
          }
        }
      }

      // and print the ordinary page header ..
      final Band b = report.getPageHeader();
      if (isPageHeaderPrinting(b))
      {
        if (runtime == null)
        {
          runtime = createRuntime(state.getFlowController().getMasterRow(), state, processingContext);
        }
        print(runtime, b);
      }

      /**
       * Dive into the pending group to print the group header ...
       */

      for (int i = levels.length - 1; i >= 0; i -= 1)
      {
        final LayouterLevel level = levels[i];
        final ReportDefinition def = level.getReportDefinition();

        for (int gidx = 0; gidx <= level.getGroupIndex(); gidx++)
        {
          final Group g = def.getGroup(gidx);
          final GroupHeader header = g.getHeader();
          if (header.isSticky())
          {
            if (header.isRepeat())
            {
              print(level.getRuntime(), header);
            }
            else
            {
              printEmptyRootLevelBand();
            }
          }
        }

        if (level.isInItemGroup())
        {
          final DetailsHeader detailsHeader = def.getDetailsHeader();
          if (detailsHeader.isRepeat())
          {
            print(level.getRuntime(), detailsHeader);
          }
        }
      }

      final int groupsPrinted = state.getPresentationGroupIndex();
      for (int gidx = 0; gidx <= groupsPrinted; gidx++)
      {
        final Group g = report.getGroup(gidx);
        final GroupHeader header = g.getHeader();
        if (header.isRepeat())
        {
          print(runtime, header);
        }
      }

      if (state.isInItemGroup())
      {
        final DetailsHeader detailsHeader = report.getDetailsHeader();
        if (detailsHeader.isRepeat())
        {
          print(runtime, detailsHeader);
        }
      }

      addSubReportMarkers(renderer.endSection());
    }
    // mark the current position to calculate the maxBand-Height
  }

  /**
   * Receives notification that a page has ended.
   * <p/>
   * This prints the PageFooter. If this is the first page, the footer is not printed if the pagefooter style-flag
   * DISPLAY_ON_FIRSTPAGE is set to false. If this event is known to be the last pageFinished event, the
   * DISPLAY_ON_LASTPAGE is evaluated and the footer is printed only if this flag is set to TRUE.
   * <p/>
   *
   * @param event the report event.
   */
  public void pageFinished(final ReportEvent event)
  {
    setCurrentEvent(event);
    try
    {
      updateFooterArea(event);
    }
    catch (FunctionProcessingException fe)
    {
      throw fe;
    }
    catch (Exception e)
    {
      throw new FunctionProcessingException("PageFinished failed", e);
    }
    finally
    {
      clearCurrentEvent();
    }
  }

  public void updateFooterArea(final ReportEvent event)
      throws ReportProcessingException
  {
    final OutputProcessorMetaData metaData = renderer.getOutputProcessor().getMetaData();
    if (metaData.isFeatureSupported(OutputProcessorFeature.PAGE_SECTIONS) == false)
    {
      return;
    }
    if (event.getState().isInlineProcess())
    {
      return;
    }

    renderer.startSection(Renderer.TYPE_REPEATED_FOOTER);

    final ReportDefinition report = event.getReport();
    final ReportState state = event.getState();
    if (state.isInItemGroup())
    {
      final DetailsFooter detailsFooter = report.getDetailsFooter();
      if (detailsFooter.isRepeat())
      {
        print(getRuntime(), detailsFooter);
      }
    }

    /**
     * Repeating group header are only printed while ItemElements are
     * processed.
     */
    final int groupsPrinted = state.getPresentationGroupIndex();
    for (int gidx = groupsPrinted; gidx >= 0; gidx -= 1)
    {
      final Group g = report.getGroup(gidx);
      final GroupFooter footer = g.getFooter();
      if (footer.isRepeat())
      {
        print(getRuntime(), footer);
      }
    }

    final LayouterLevel[] levels = DefaultOutputFunction.collectSubReportStates(event.getState(), getRuntime().getProcessingContext());
    final int levelCount = levels.length;
    for (int i = 0; i < levelCount; i++)
    {
      final LayouterLevel level = levels[i];
      final ReportDefinition def = level.getReportDefinition();

      if (level.isInItemGroup())
      {
        final DetailsFooter subDetailsFooter = report.getDetailsFooter();
        if (subDetailsFooter.isSticky())
        {
          if (subDetailsFooter.isRepeat())
          {
            print(level.getRuntime(), subDetailsFooter);
          }
        }
      }

      for (int gidx = level.getGroupIndex(); gidx >= 0; gidx -= 1)
      {
        final Group g = def.getGroup(gidx);
        final GroupFooter footer = g.getFooter();
        if (footer.isSticky())
        {
          if (footer.isRepeat())
          {
            print(level.getRuntime(), footer);
          }
        }
      }
    }

    addSubReportMarkers(renderer.endSection());
    renderer.startSection(Renderer.TYPE_FOOTER);
    final PageFooter pageFooter = report.getPageFooter();
    final ElementStyleSheet pageFooterStyle = pageFooter.getStyle();
    if (computeCurrentPage() == 1)
    {
      if (pageFooterStyle.getBooleanStyleProperty(BandStyleKeys.DISPLAY_ON_FIRSTPAGE) == true)
      {
        print(getRuntime(), pageFooter);
      }
      else
      {
        printEmptyRootLevelBand();
      }
    }
    else if (isLastPagebreak())
    {
      if (pageFooterStyle.getBooleanStyleProperty(BandStyleKeys.DISPLAY_ON_LASTPAGE, false) == true)
      {
        print(getRuntime(), pageFooter);
      }
      else
      {
        printEmptyRootLevelBand();
      }
    }
    else
    {
      print(getRuntime(), pageFooter);
    }

    for (int i = 0; i < levelCount; i++)
    {
      final LayouterLevel level = levels[i];
      final ReportDefinition def = level.getReportDefinition();
      final PageFooter b = def.getPageFooter();
      if (b.isSticky() == false)
      {
        printEmptyRootLevelBand();
        continue;
      }

      final ElementStyleSheet style = b.getStyle();
      if (computeCurrentPage() == 1)
      {
        if (style.getBooleanStyleProperty(BandStyleKeys.DISPLAY_ON_FIRSTPAGE) == true)
        {
          print(level.getRuntime(), b);
        }
        else
        {
          printEmptyRootLevelBand();
        }
      }
      else if (isLastPagebreak())
      {
        if (style.getBooleanStyleProperty(BandStyleKeys.DISPLAY_ON_LASTPAGE) == true)
        {
          print(level.getRuntime(), b);
        }
        else
        {
          printEmptyRootLevelBand();
        }
      }
      else
      {
        print(level.getRuntime(), b);
      }
    }
    addSubReportMarkers(renderer.endSection());
  }

  /**
   * Returns the current report event.
   *
   * @return the event.
   */
  protected ReportEvent getCurrentEvent()
  {
    return currentEvent;
  }

  /**
   * Sets the current event (also updates the report reference).
   *
   * @param currentEvent event.
   */
  protected void setCurrentEvent(final ReportEvent currentEvent)
  {
    if (currentEvent == null)
    {
      throw new NullPointerException("Event must not be null.");
    }
    this.currentEvent = currentEvent;
    this.pagebreakHandler.setReportState(currentEvent.getState());
    this.renderer.setStateKey(currentEvent.getState().getProcessKey());
  }

  /**
   * Clears the current event.
   */
  protected void clearCurrentEvent()
  {
    if (currentEvent == null)
    {
      throw new IllegalStateException("ClearCurrentEvent called without Event set:");
    }
    this.currentEvent = null;
    this.pagebreakHandler.setReportState(null);
    this.renderer.setStateKey(null);
  }

  /**
   * Clones the function. <P> Be aware, this does not create a deep copy. If you have complex strucures contained in
   * objects, you have to override this function.
   *
   * @return a clone of this function.
   * @throws CloneNotSupportedException this should never happen.
   */
  public final Object clone() throws CloneNotSupportedException
  {
    final DefaultOutputFunction sl = (DefaultOutputFunction) super.clone();
    sl.currentEvent = null;
    sl.inlineSubreports = (ArrayList<InlineSubreportMarker>) inlineSubreports.clone();
    sl.outputHandlers = (FastStack) outputHandlers.clone();
    sl.crosstabLayouts = (FastStack<PreparedCrosstabLayout>) crosstabLayouts.clone();
    sl.crosstabLayouts.clear();
    final int clSize = crosstabLayouts.size();
    for (int i = 0; i < clSize; i++)
    {
      final PreparedCrosstabLayout o = crosstabLayouts.get(i);
      sl.crosstabLayouts.push((PreparedCrosstabLayout) o.clone());
    }
    return sl;
  }


  public Expression getInstance()
  {
    return deriveForStorage();
  }

  /**
   * Creates a storage-copy of the output function. A storage copy must create a deep clone of all referenced objects so
   * that it is guaranteed that changes to either the original or the clone do not affect the other instance.
   * <p/>
   * Any failure to implement this method correctly will be a great source of very subtle bugs.
   *
   * @return the deep clone.
   */
  public OutputFunction deriveForStorage()
  {
    try
    {
      final DefaultOutputFunction sl = (DefaultOutputFunction) super.clone();
      sl.renderer = renderer.deriveForStorage();
      sl.inlineSubreports = (ArrayList<InlineSubreportMarker>) inlineSubreports.clone();
      sl.currentEvent = null;
      sl.pagebreakHandler = (DefaultLayoutPagebreakHandler) pagebreakHandler.clone();
      sl.pagebreakHandler.setReportState(null);
      sl.outputHandlers = (FastStack<GroupOutputHandler>) outputHandlers.clone();
      sl.crosstabLayouts = (FastStack<PreparedCrosstabLayout>) crosstabLayouts.clone();
      sl.crosstabLayouts.clear();
      final int clSize = crosstabLayouts.size();
      for (int i = 0; i < clSize; i++)
      {
        final PreparedCrosstabLayout o = crosstabLayouts.get(i);
        sl.crosstabLayouts.push(o.derive());
      }
      return sl;
    }
    catch (final CloneNotSupportedException e)
    {
      throw new IllegalStateException();
    }
  }

  /**
   * Creates a cheaper version of the deep-copy of the output function. A pagebreak-derivate is created on every
   * possible pagebreak position and must contain all undo/rollback information to restore the state of any shared
   * object when a roll-back is requested.
   * <p/>
   * Any failure to implement this method correctly will be a great source of very subtle bugs.
   *
   * @return the deep clone.
   */
  public OutputFunction deriveForPagebreak()
  {
    try
    {
      final DefaultOutputFunction sl = (DefaultOutputFunction) super.clone();
      sl.renderer = renderer.deriveForPagebreak();
      sl.inlineSubreports = (ArrayList<InlineSubreportMarker>) inlineSubreports.clone();
      sl.currentEvent = null;
      sl.pagebreakHandler = (DefaultLayoutPagebreakHandler) pagebreakHandler.clone();
      sl.outputHandlers = (FastStack<GroupOutputHandler>) outputHandlers.clone();
      sl.crosstabLayouts = (FastStack<PreparedCrosstabLayout>) crosstabLayouts.clone();
      sl.crosstabLayouts.clear();
      final int clSize = crosstabLayouts.size();
      for (int i = 0; i < clSize; i++)
      {
        final PreparedCrosstabLayout o = crosstabLayouts.get(i);
        sl.crosstabLayouts.push(o.derive());
      }
      return sl;
    }
    catch (CloneNotSupportedException e)
    {
      throw new IllegalStateException();
    }
  }

  public void setRenderer(final Renderer renderer)
  {
    this.renderer = renderer;
  }

  public Renderer getRenderer()
  {
    return renderer;
  }

  /**
   * Prints the given band at the current cursor position.
   *
   * @param dataRow the datarow for evaluating the band's value-expressions.
   * @param band    the band to be printed.
   * @throws ReportProcessingException if an error occured during the layout computation.
   */
  protected void print(final ExpressionRuntime dataRow, final Band band) throws ReportProcessingException
  {
    renderer.add(band, dataRow, getCurrentEvent().getState().getProcessKey());
  }

  protected void printEmptyRootLevelBand() throws ReportProcessingException
  {
    renderer.addEmptyRootLevelBand(getCurrentEvent().getState().getProcessKey());
  }

  private void clearPendingPageStart(final ReportEvent event)
  {
    clearPendingPageStart(event, false);
  }

  private void clearPendingPageStart(final ReportEvent event, final boolean force)
  {
    pagebreakHandler.setReportState(event.getState());
    try
    {
      if (renderer.clearPendingPageStart(pagebreakHandler))
      {
        // page started has been fired ...
        return;
      }

      if (!force)
      {
        final boolean currentPageEmpty = renderer.isCurrentPageEmpty();
        if (currentPageEmpty == false)
        {
          return;
        }

        final boolean validateResult = renderer.validatePages() != Renderer.LayoutResult.LAYOUT_UNVALIDATABLE;
        if (validateResult == false)
        {
          return;
        }
      }

      try
      {
        setCurrentEvent(event);
        renderer.newPageStarted();
        updateHeaderArea(event.getState());
      }
      finally
      {
        clearCurrentEvent();
      }
    }
    catch (ReportProcessingException e)
    {
      throw new FunctionProcessingException("Failed to update the page-header", e);
    }
    catch (ContentProcessingException e)
    {
      throw new FunctionProcessingException("Failed to update the page-header", e);
    }
    finally
    {
      pagebreakHandler.setReportState(null);
    }
  }

  public InlineSubreportMarker[] getInlineSubreports()
  {
    return inlineSubreports.toArray(new InlineSubreportMarker[inlineSubreports.size()]);
  }

  public void clearInlineSubreports(final SubReportProcessType inlineExecution)
  {
    final InlineSubreportMarker[] subreports = getInlineSubreports();
    for (int i = subreports.length - 1; i >= 0; i--)
    {
      final InlineSubreportMarker subreport = subreports[i];
      if (inlineExecution == subreport.getProcessType())
      {
        inlineSubreports.remove(i);
      }
    }
  }

  public PreparedCrosstabLayout startCrosstabLayout()
  {
    final PreparedCrosstabLayout layout = new PreparedCrosstabLayout();
    crosstabLayouts.push(layout);
    return layout;
  }

  public PreparedCrosstabLayout getCurrentCrosstabLayout()
  {
    return crosstabLayouts.peek();
  }

  public void endCrosstabLayout()
  {
    crosstabLayouts.pop();
  }

  public void restart(final ReportState state) throws ReportProcessingException
  {
    final ReportEvent event = new ReportEvent(state, state.getEventCode());
    clearPendingPageStart(event, true);
  }

  public boolean createRollbackInformation()
  {
    final Renderer commitableRenderer = getRenderer();
    commitableRenderer.createRollbackInformation();
    return true;
  }
}

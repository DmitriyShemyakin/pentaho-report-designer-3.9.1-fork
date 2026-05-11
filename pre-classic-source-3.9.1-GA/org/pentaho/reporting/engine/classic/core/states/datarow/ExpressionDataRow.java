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

package org.pentaho.reporting.engine.classic.core.states.datarow;

import java.util.ArrayList;
import javax.swing.table.TableModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.ResourceBundleFactory;
import org.pentaho.reporting.engine.classic.core.event.PageEventListener;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.function.Function;
import org.pentaho.reporting.engine.classic.core.function.ProcessingContext;
import org.pentaho.reporting.engine.classic.core.states.DefaultGroupingState;
import org.pentaho.reporting.engine.classic.core.states.GroupingState;
import org.pentaho.reporting.engine.classic.core.util.IntList;
import org.pentaho.reporting.engine.classic.core.util.IntegerCache;
import org.pentaho.reporting.engine.classic.core.util.LevelList;
import org.pentaho.reporting.engine.classic.core.wizard.DataSchema;
import org.pentaho.reporting.libraries.base.config.Configuration;

/**
 * Creation-Date: Dec 13, 2006, 3:17:20 PM
 *
 * @author Thomas Morgner
 * @noinspection AssignmentToCollectionOrArrayFieldFromParameter,ReturnOfCollectionOrArrayField
 */
public final class ExpressionDataRow
{
  private static final Log logger = LogFactory.getLog(ExpressionDataRow.class);

  private static final Integer[] EMPTY_INTEGERARRAY = new Integer[0];
  private static final Expression[] EMPTY_EXPRESSIONS = new Expression[0];

  private static class DataRowRuntime implements ExpressionRuntime
  {
    private ExpressionDataRow expressionDataRow;
    private GroupingState state;

    protected DataRowRuntime(final ExpressionDataRow dataRow)
    {
      this.expressionDataRow = dataRow;
      this.state = DefaultGroupingState.EMPTY;
    }

    public DataSchema getDataSchema()
    {
      return expressionDataRow.getMasterRow().getDataSchema();
    }

    public DataRow getDataRow()
    {
      return expressionDataRow.getMasterRow().getGlobalView();
    }

    public Configuration getConfiguration()
    {
      return getProcessingContext().getConfiguration();
    }

    public ResourceBundleFactory getResourceBundleFactory()
    {
      return expressionDataRow.getMasterRow().getResourceBundleFactory();
    }

    public DataFactory getDataFactory()
    {
      return expressionDataRow.getMasterRow().getDataFactory();
    }

    /**
     * Access to the tablemodel was granted using report properties, now direct.
     */
    public TableModel getData()
    {
      return expressionDataRow.getMasterRow().getReportDataRow().getReportData();
    }

    /**
     * Where are we in the current processing.
     */
    public int getCurrentRow()
    {
      return expressionDataRow.getMasterRow().getReportDataRow().getCursor();
    }

    /**
     * The output descriptor is a simple string collections consisting of the following components:
     * exportclass/type/subtype
     * <p/>
     * For example, the PDF export would be: pageable/pdf The StreamHTML export would return table/html/stream
     *
     * @return the export descriptor.
     */
    public String getExportDescriptor()
    {
      return getProcessingContext().getExportDescriptor();
    }

    public ProcessingContext getProcessingContext()
    {
      return expressionDataRow.getProcessingContext();
    }

    public int getCurrentGroup()
    {
      return state.getCurrentGroup();
    }

    public int getGroupStartRow(final String groupName)
    {
      return state.getGroupStartRow(groupName);
    }

    public int getGroupStartRow(final int groupIndex)
    {
      return state.getGroupStartRow(groupIndex);
    }

    public GroupingState getState()
    {
      return state;
    }

    public void setState(final GroupingState state)
    {
      if (state == null)
      {
        throw new NullPointerException();
      }
      this.state = state;
    }
  }


  private static class LevelStorage
  {
    private int levelNumber;
    private int[] activeExpressions;
    private int[] functions;
    private int[] pageEventListeners;
    private int[] prepareEventListeners;
    private int[] expressions;

    protected LevelStorage(final int levelNumber,
                           final int[] expressions,
                           final int[] activeExpressions,
                           final int[] functions,
                           final int[] pageEventListeners,
                           final int[] prepareEventListeners)
    {
      this.levelNumber = levelNumber;
      this.activeExpressions = activeExpressions;
      this.functions = functions;
      this.pageEventListeners = pageEventListeners;
      this.prepareEventListeners = prepareEventListeners;
      this.expressions = expressions;
    }

    public int getLevelNumber()
    {
      return levelNumber;
    }

    public int[] getFunctions()
    {
      return functions;
    }


    /**
     * @return Returns the activeExpressions.
     */
    public int[] getActiveExpressions()
    {
      return activeExpressions;
    }

    /**
     * @return Returns the expressions.
     * @noinspection ReturnOfCollectionOrArrayField
     */
    public int[] getExpressions()
    {
      return expressions;
    }

    /**
     * @return Returns the pageEventListeners.
     */
    public int[] getPageEventListeners()
    {
      return pageEventListeners;
    }

    /**
     * @return Returns the prepareEventListeners.
     */
    public int[] getPrepareEventListeners()
    {
      return prepareEventListeners;
    }
  }

  //private HashMap nameCache;
  private MasterDataRow masterRow;
  private ProcessingContext processingContext;
  private int length;
  private Expression[] expressions;
  private LevelStorage[] levelData;
  private MasterDataRowChangeEvent chEvent;
  private DataRowRuntime runtime;
  private ArrayList errorList;
  private static final Exception[] EMPTY_EXCEPTIONS = new Exception[0];
  private boolean prepareEventListener;
  private boolean includeStructuralProcessing;

  public ExpressionDataRow(final MasterDataRow masterRow,
                           final ProcessingContext processingContext,
                           final boolean includeStructuralProcessing)
  {
    if (masterRow == null)
    {
      throw new NullPointerException();
    }
    if (processingContext == null)
    {
      throw new NullPointerException();
    }

    this.includeStructuralProcessing = includeStructuralProcessing;
    this.processingContext = processingContext;
    this.masterRow = masterRow;
    this.expressions = ExpressionDataRow.EMPTY_EXPRESSIONS;
    this.chEvent = new MasterDataRowChangeEvent
        (MasterDataRowChangeEvent.COLUMN_UPDATED, "", "");
    this.runtime = new DataRowRuntime(this);
    this.revalidate();
  }

  public boolean isIncludeStructuralProcessing()
  {
    return includeStructuralProcessing;
  }

  private ExpressionDataRow(final MasterDataRow masterRow,
                            final ExpressionDataRow previousRow,
                            final boolean updateGlobalView)
      throws CloneNotSupportedException
  {
    this.chEvent = new MasterDataRowChangeEvent
        (MasterDataRowChangeEvent.COLUMN_UPDATED, "", "");
    this.processingContext = previousRow.processingContext;
    this.masterRow = masterRow;
    this.expressions = new Expression[previousRow.expressions.length];
    this.length = previousRow.length;
    this.levelData = previousRow.levelData;
    this.runtime = new DataRowRuntime(this);
    this.runtime.setState(previousRow.runtime.getState());
    this.includeStructuralProcessing = previousRow.includeStructuralProcessing;

    for (int i = 0; i < length; i++)
    {
      final Expression expression = previousRow.expressions[i];
      if (expression == null)
      {
        ExpressionDataRow.logger.debug("Error: Expression is null...");
        throw new IllegalStateException();
      }

      if (expression instanceof Function)
      {
        expressions[i] = (Expression) expression.clone();
      }
      else
      {
        expressions[i] = expression;
      }

      if (updateGlobalView == false)
      {
        continue;
      }

      final String name = expression.getName();
      if (name != null)
      {
        chEvent.setColumnName(name);
      }
      Object value;

      final ExpressionRuntime oldRuntime = expression.getRuntime();
      try
      {
        expression.setRuntime(runtime);
        if (runtime.getProcessingContext().getProcessingLevel() <= expression.getDependencyLevel())
        {
          value = expression.getValue();
        }
        else
        {
          value = null;
        }
      }
      catch (Exception e)
      {
        if (ExpressionDataRow.logger.isDebugEnabled())
        {
          ExpressionDataRow.logger.warn("Failed to evaluate expression '" + name + '\'', e);
        }
        else
        {
          ExpressionDataRow.logger.warn("Failed to evaluate expression '" + name + '\'');
        }
        value = null;
      }
      finally
      {
        expression.setRuntime(oldRuntime);
      }
      if (name != null)
      {
        chEvent.setColumnValue(value);
        masterRow.dataRowChanged(chEvent);
      }
    }
  }

  /**
   * This adds the expression to the data-row and queries the expression for the first time.
   *
   * @param expressionSlot the expression that should be added.
   * @param preserveState  a flag indicating whether the expression is statefull and should preserve its internal
   *                       state.
   * @throws ReportProcessingException if the processing failed due to invalid function implementations.
   */
  private void pushExpression(final Expression expressionSlot,
                              final boolean preserveState)
      throws ReportProcessingException
  {
    if (expressionSlot == null)
    {
      throw new NullPointerException();
    }

    ensureCapacity(length + 1);

    if (preserveState == false)
    {
      this.expressions[length] = expressionSlot.getInstance();
    }
    else
    {
      try
      {
        this.expressions[length] = (Expression) expressionSlot.clone();
      }
      catch (final CloneNotSupportedException e)
      {
        throw new ReportProcessingException("Failed to clone the expression.", e);
      }
    }

    final String name = expressionSlot.getName();
    length += 1;

    // A manual advance to initialize the function.
    if (name != null)
    {
      final MasterDataRowChangeEvent chEvent = new MasterDataRowChangeEvent
          (MasterDataRowChangeEvent.COLUMN_ADDED, name, null);
      masterRow.dataRowChanged(chEvent);
    }
  }

  public void pushExpressions(final Expression[] expressionSlots,
                              final boolean preserveState)
      throws ReportProcessingException
  {
    if (expressionSlots == null)
    {
      throw new NullPointerException();
    }

    ensureCapacity(length + expressionSlots.length);
    for (int i = 0; i < expressionSlots.length; i++)
    {
      final Expression expression = expressionSlots[i];
      if (expression == null)
      {
        continue;
      }
      pushExpression(expression, preserveState);
    }

    revalidate();
  }

  public void popExpressions(final int counter)
  {
    for (int i = 0; i < counter; i++)
    {
      popExpression();
    }

    revalidate();
  }

  private void popExpression()
  {
    if (length == 0)
    {
      return;
    }
    final Expression removedExpression = this.expressions[length - 1];
    final String originalName = removedExpression.getName();
    removedExpression.setRuntime(null);

    this.expressions[length - 1] = null;
    this.length -= 1;
    if (originalName != null)
    {
      if (removedExpression.isPreserve() == false)
      {
        final MasterDataRowChangeEvent chEvent = new MasterDataRowChangeEvent
            (MasterDataRowChangeEvent.COLUMN_REMOVED, originalName, null);
        masterRow.dataRowChanged(chEvent);
      }
    }
  }

  private void ensureCapacity(final int requestedSize)
  {
    final int capacity = this.expressions.length;
    if (capacity > requestedSize)
    {
      return;
    }
    final int newSize = Math.max(capacity * 2, requestedSize + 10);

    final Expression[] newExpressions = new Expression[newSize];
    System.arraycopy(expressions, 0, newExpressions, 0, length);

    this.expressions = newExpressions;
  }

  private void revalidate()
  {
    // recompute the level storage ..
    int minLevel = Integer.MIN_VALUE;
    final LevelList levelList = new LevelList();
    for (int i = 0; i < length; i++)
    {
      final Expression expression = expressions[i];

      // The list maps the current position to the level ..
      final int dependencyLevel = expression.getDependencyLevel();
      levelList.add(IntegerCache.getInteger(i), dependencyLevel);
      if (minLevel < dependencyLevel)
      {
        minLevel = dependencyLevel;
      }
    }

    if (minLevel > Integer.MIN_VALUE)
    {
      if (isIncludeStructuralProcessing())
      {
        for (int i = 0; i < length; i++)
        {
          final Expression expression = expressions[i];

          // The list maps the current position to the level ..
          final int dependencyLevel = expression.getDependencyLevel();
          if (dependencyLevel == minLevel && (expression instanceof Function == false))
          {
            levelList.add(IntegerCache.getInteger(i), minLevel);
          }
        }
      }
    }

    final Integer[] levels = levelList.getLevelsDescendingArray();
    this.levelData = new LevelStorage[levels.length];
    final int expressionsCount = levelList.size();

    final int capacity = Math.min(20, expressionsCount);
    final IntList expressionPositions = new IntList(capacity);
    final IntList activeExpressions = new IntList(capacity);
    final IntList functions = new IntList(capacity);
    final IntList pageEventListeners = new IntList(capacity);
    final IntList prepareEventListeners = new IntList(capacity);
    final boolean prepareEventListener = false;

    for (int i = 0; i < levels.length; i++)
    {
      final int currentLevel = levels[i].intValue();
      final Integer[] data = (Integer[])
          levelList.getElementArrayForLevel(currentLevel, ExpressionDataRow.EMPTY_INTEGERARRAY);
      for (int x = 0; x < data.length; x++)
      {
        final Integer position = data[x];
        final Expression ex = this.expressions[position.intValue()];
        final int globalPosition = position.intValue();

        expressionPositions.add(globalPosition);
        activeExpressions.add(globalPosition);
        if (ex instanceof Function == false)
        {
          continue;
        }

        functions.add(globalPosition);
        if (ex instanceof PageEventListener)
        {
          pageEventListeners.add(globalPosition);
        }
      }

      levelData[i] = new LevelStorage(currentLevel,
          expressionPositions.toArray(), activeExpressions.toArray(),
          functions.toArray(), pageEventListeners.toArray(),
          prepareEventListeners.toArray());

      expressionPositions.clear();
      activeExpressions.clear();
      functions.clear();
      pageEventListeners.clear();
      prepareEventListeners.clear();

      this.prepareEventListener = prepareEventListener;
    }
  }

  public int[] getLevels()
  {
    final int[] retval = new int[levelData.length];
    for (int i = 0; i < levelData.length; i++)
    {
      final LevelStorage storage = levelData[i];
      retval[i] = storage.getLevelNumber();
    }
    return retval;
  }

  /**
   * Returns the number of columns, expressions and functions and marked ReportProperties in the report.
   *
   * @return the item count.
   */
  public int getColumnCount()
  {
    return length;
  }

  public void fireReportEvent(final ReportEvent event)
  {
    runtime.setState(event.getState().createGroupingState());

    if ((event.getType() & ReportEvent.PAGE_STARTED) ==
        ReportEvent.PAGE_STARTED)
    {
      firePageStartedEvent(event);
    }
    else if ((event.getType() & ReportEvent.PAGE_FINISHED) ==
        ReportEvent.PAGE_FINISHED)
    {
      firePageFinishedEvent(event);
    }
    else if ((event.getType() & ReportEvent.ITEMS_ADVANCED) ==
        ReportEvent.ITEMS_ADVANCED)
    {
      fireItemsAdvancedEvent(event);
    }
    else if ((event.getType() & ReportEvent.ITEMS_FINISHED) ==
        ReportEvent.ITEMS_FINISHED)
    {
      fireItemsFinishedEvent(event);
    }
    else if ((event.getType() & ReportEvent.ITEMS_STARTED) ==
        ReportEvent.ITEMS_STARTED)
    {
      fireItemsStartedEvent(event);
    }
    else if ((event.getType() & ReportEvent.GROUP_FINISHED) ==
        ReportEvent.GROUP_FINISHED)
    {
      fireGroupFinishedEvent(event);
    }
    else if ((event.getType() & ReportEvent.GROUP_STARTED) ==
        ReportEvent.GROUP_STARTED)
    {
      fireGroupStartedEvent(event);
    }
    else if ((event.getType() & ReportEvent.REPORT_INITIALIZED) ==
        ReportEvent.REPORT_INITIALIZED)
    {
      fireReportInitializedEvent(event);
    }
    else if ((event.getType() & ReportEvent.REPORT_DONE) ==
        ReportEvent.REPORT_DONE)
    {
      fireReportDoneEvent(event);
    }
    else if ((event.getType() & ReportEvent.REPORT_FINISHED) ==
        ReportEvent.REPORT_FINISHED)
    {
      fireReportFinishedEvent(event);
    }
    else if ((event.getType() & ReportEvent.REPORT_STARTED) ==
        ReportEvent.REPORT_STARTED)
    {
      fireReportStartedEvent(event);
    }
    else
    {
      throw new IllegalArgumentException();
    }

    reactivateExpressions(event.isDeepTraversing());
  }

  private void reactivateExpressions(final boolean deepTraversing)
  {
    final int activeLevel;

    final int rawLevel = processingContext.getProcessingLevel();
    if (rawLevel == Integer.MAX_VALUE)
    {
      // we are in the data-pre-processing stage. Include all common expressions, in case they
      // compute a group-break.
      if (levelData.length > 1)
      {
        activeLevel = levelData[1].getLevelNumber();
      }
      else
      {
        activeLevel = rawLevel;
      }
    }
    else
    {
      activeLevel = rawLevel;
    }

    for (int levelIdx = 0; levelIdx < levelData.length; levelIdx++)
    {
      final int level = levelData[levelIdx].getLevelNumber();
      if (level < activeLevel)
      {
        break;
      }

      final int[] listeners = levelData[levelIdx].getActiveExpressions();
      for (int l = 0; l < listeners.length; l++)
      {
        final Expression expression = expressions[listeners[l]];
        if (deepTraversing && expression.isDeepTraversing() == false)
        {
          continue;
        }

        expression.setRuntime(runtime);
        final String name = expression.getName();
        if (name != null)
        {
          chEvent.setColumnName(name);
          try
          {
            final Object value;
            if (runtime.getProcessingContext().getProcessingLevel() <= expression.getDependencyLevel())
            {
              value = expression.getValue();
            }
            else
            {
              value = null;
            }
            chEvent.setColumnValue(value);
          }
          catch (Exception e)
          {
            chEvent.setColumnValue(null);
            ExpressionDataRow.logger.info("Evaluation of expression '" + name + "'failed.", e);
          }
          masterRow.dataRowChanged(chEvent);
        }
        expression.setRuntime(null);
      }
    }
  }

  private void fireItemsAdvancedEvent(final ReportEvent event)
  {
    final boolean deepTraversing = event.isDeepTraversing();
    final int activeLevel = processingContext.getProcessingLevel();
    for (int levelIdx = 0; levelIdx < levelData.length; levelIdx++)
    {
      final int level = levelData[levelIdx].getLevelNumber();
      if (level < activeLevel)
      {
        break;
      }

      final int[] listeners = levelData[levelIdx].getFunctions();
      for (int l = 0; l < listeners.length; l++)
      {
        final Expression expression = expressions[listeners[l]];
        if (deepTraversing && expression.isDeepTraversing() == false)
        {
          continue;
        }

        final ExpressionRuntime oldRuntime = expression.getRuntime();
        expression.setRuntime(runtime);
        final Function e = (Function) expression;
        try
        {
          e.itemsAdvanced(event);
          final String name = expression.getName();
          if (name != null)
          {
            chEvent.setColumnName(name);
            chEvent.setColumnValue(expression.getValue());
            masterRow.dataRowChanged(chEvent);
          }
        }
        catch (Exception ex)
        {
          if (ExpressionDataRow.logger.isDebugEnabled())
          {
            ExpressionDataRow.logger.error("Failed to fire prepare event", ex);
          }
          else
          {
            ExpressionDataRow.logger.error("Failed to fire prepare event: " + ex);
          }
          addError(ex);
        }

        expression.setRuntime(oldRuntime);
      }
    }
  }

  private void fireItemsStartedEvent(final ReportEvent event)
  {
    final boolean deepTraversing = event.isDeepTraversing();
    final int activeLevel = processingContext.getProcessingLevel();
    for (int levelIdx = 0; levelIdx < levelData.length; levelIdx++)
    {
      final int level = levelData[levelIdx].getLevelNumber();
      if (level < activeLevel)
      {
        break;
      }

      final int[] listeners = levelData[levelIdx].getFunctions();
      for (int l = 0; l < listeners.length; l++)
      {
        final Expression expression = expressions[listeners[l]];
        if (deepTraversing && expression.isDeepTraversing() == false)
        {
          continue;
        }

        final ExpressionRuntime oldRuntime = expression.getRuntime();
        expression.setRuntime(runtime);
        final Function e = (Function) expression;
        try
        {
          e.itemsStarted(event);
          final String name = expression.getName();
          if (name != null)
          {
            chEvent.setColumnName(name);
            chEvent.setColumnValue(expression.getValue());
            masterRow.dataRowChanged(chEvent);
          }
        }
        catch (Exception ex)
        {
          if (ExpressionDataRow.logger.isDebugEnabled())
          {
            ExpressionDataRow.logger.error("Failed to fire prepare event", ex);
          }
          else
          {
            ExpressionDataRow.logger.error("Failed to fire prepare event: " + ex);
          }
          addError(ex);
        }

        expression.setRuntime(oldRuntime);
      }
    }
  }

  private void fireItemsFinishedEvent(final ReportEvent event)
  {
    final boolean deepTraversing = event.isDeepTraversing();
    final int activeLevel = processingContext.getProcessingLevel();
    for (int levelIdx = 0; levelIdx < levelData.length; levelIdx++)
    {
      final int level = levelData[levelIdx].getLevelNumber();
      if (level < activeLevel)
      {
        break;
      }

      final int[] listeners = levelData[levelIdx].getFunctions();
      for (int l = 0; l < listeners.length; l++)
      {
        final Expression expression = expressions[listeners[l]];
        if (deepTraversing && expression.isDeepTraversing() == false)
        {
          continue;
        }

        final ExpressionRuntime oldRuntime = expression.getRuntime();
        expression.setRuntime(runtime);
        final Function e = (Function) expression;
        try
        {
          e.itemsFinished(event);
          final String name = expression.getName();
          if (name != null)
          {
            chEvent.setColumnName(name);
            chEvent.setColumnValue(expression.getValue());
            masterRow.dataRowChanged(chEvent);
          }
        }
        catch (Exception ex)
        {
          if (ExpressionDataRow.logger.isDebugEnabled())
          {
            ExpressionDataRow.logger.error("Failed to fire prepare event", ex);
          }
          else
          {
            ExpressionDataRow.logger.error("Failed to fire prepare event: " + ex);
          }
          addError(ex);
        }

        expression.setRuntime(oldRuntime);
      }
    }
  }

  private void fireGroupStartedEvent(final ReportEvent event)
  {
    final boolean deepTraversing = event.isDeepTraversing();
    final int activeLevel = processingContext.getProcessingLevel();
    for (int levelIdx = 0; levelIdx < levelData.length; levelIdx++)
    {
      final int level = levelData[levelIdx].getLevelNumber();
      if (level < activeLevel)
      {
        break;
      }

      final int[] listeners = levelData[levelIdx].getFunctions();
      for (int l = 0; l < listeners.length; l++)
      {
        final Expression expression = expressions[listeners[l]];
        if (deepTraversing && expression.isDeepTraversing() == false)
        {
          continue;
        }

        final ExpressionRuntime oldRuntime = expression.getRuntime();
        expression.setRuntime(runtime);
        final Function e = (Function) expression;
        try
        {
          e.groupStarted(event);
          final String name = expression.getName();
          if (name != null)
          {
            chEvent.setColumnName(name);
            chEvent.setColumnValue(expression.getValue());
            masterRow.dataRowChanged(chEvent);
          }
        }
        catch (Exception ex)
        {
          if (ExpressionDataRow.logger.isDebugEnabled())
          {
            ExpressionDataRow.logger.error("Failed to fire group-started event", ex);
          }
          else
          {
            ExpressionDataRow.logger.error("Failed to fire group-started event: " + ex);
          }
          addError(ex);
        }

        expression.setRuntime(oldRuntime);
      }
    }
  }

  private void fireGroupFinishedEvent(final ReportEvent event)
  {
    final boolean deepTraversing = event.isDeepTraversing();
    final int activeLevel = processingContext.getProcessingLevel();
    for (int levelIdx = 0; levelIdx < levelData.length; levelIdx++)
    {
      final int level = levelData[levelIdx].getLevelNumber();
      if (level < activeLevel)
      {
        break;
      }

      final int[] listeners = levelData[levelIdx].getFunctions();
      for (int l = 0; l < listeners.length; l++)
      {
        final Expression expression = expressions[listeners[l]];
        if (deepTraversing && expression.isDeepTraversing() == false)
        {
          continue;
        }

        final ExpressionRuntime oldRuntime = expression.getRuntime();
        expression.setRuntime(runtime);
        final Function e = (Function) expression;
        try
        {
          e.groupFinished(event);
          final String name = expression.getName();
          if (name != null)
          {
            chEvent.setColumnName(name);
            chEvent.setColumnValue(expression.getValue());
            masterRow.dataRowChanged(chEvent);
          }
        }
        catch (Exception ex)
        {
          if (ExpressionDataRow.logger.isDebugEnabled())
          {
            ExpressionDataRow.logger.error("Failed to fire group-finished event", ex);
          }
          else
          {
            ExpressionDataRow.logger.error("Failed to fire group-finished event: " + ex);
          }
          addError(ex);
        }

        expression.setRuntime(oldRuntime);
      }
    }
  }

  private void fireReportStartedEvent(final ReportEvent event)
  {
    final boolean deepTraversing = event.isDeepTraversing();
    final int activeLevel = processingContext.getProcessingLevel();
    for (int levelIdx = 0; levelIdx < levelData.length; levelIdx++)
    {
      final int level = levelData[levelIdx].getLevelNumber();
      if (level < activeLevel)
      {
        break;
      }

      final int[] listeners = levelData[levelIdx].getFunctions();
      for (int l = 0; l < listeners.length; l++)
      {
        final Expression expression = expressions[listeners[l]];
        if (deepTraversing && expression.isDeepTraversing() == false)
        {
          continue;
        }

        final ExpressionRuntime oldRuntime = expression.getRuntime();
        expression.setRuntime(runtime);
        final Function e = (Function) expression;
        try
        {
          e.reportStarted(event);
          final String name = expression.getName();
          if (name != null)
          {
            chEvent.setColumnName(name);
            chEvent.setColumnValue(expression.getValue());
            masterRow.dataRowChanged(chEvent);
          }
        }
        catch (Exception ex)
        {
          if (ExpressionDataRow.logger.isDebugEnabled())
          {
            ExpressionDataRow.logger.error("Failed to fire report-started event", ex);
          }
          else
          {
            ExpressionDataRow.logger.error("Failed to fire report-started event: " + ex);
          }
          addError(ex);
        }

        expression.setRuntime(oldRuntime);
      }
    }
  }

  private void fireReportDoneEvent(final ReportEvent event)
  {
    final boolean deepTraversing = event.isDeepTraversing();
    final int activeLevel = processingContext.getProcessingLevel();
    for (int levelIdx = 0; levelIdx < levelData.length; levelIdx++)
    {
      final int level = levelData[levelIdx].getLevelNumber();
      if (level < activeLevel)
      {
        break;
      }

      final int[] listeners = levelData[levelIdx].getFunctions();
      for (int l = 0; l < listeners.length; l++)
      {
        final Expression expression = expressions[listeners[l]];
        if (deepTraversing && expression.isDeepTraversing() == false)
        {
          continue;
        }

        final ExpressionRuntime oldRuntime = expression.getRuntime();
        expression.setRuntime(runtime);
        final Function e = (Function) expression;
        try
        {
          e.reportDone(event);
          final String name = expression.getName();
          if (name != null)
          {
            chEvent.setColumnName(name);
            chEvent.setColumnValue(expression.getValue());
            masterRow.dataRowChanged(chEvent);
          }
        }
        catch (Exception ex)
        {
          if (ExpressionDataRow.logger.isDebugEnabled())
          {
            ExpressionDataRow.logger.error("Failed to fire report-done event", ex);
          }
          else
          {
            ExpressionDataRow.logger.error("Failed to fire report-done event: " + ex);
          }
          addError(ex);
        }

        expression.setRuntime(oldRuntime);
      }
    }
  }

  private void fireReportFinishedEvent(final ReportEvent event)
  {
    final boolean deepTraversing = event.isDeepTraversing();
    final int activeLevel = processingContext.getProcessingLevel();
    for (int levelIdx = 0; levelIdx < levelData.length; levelIdx++)
    {
      final int level = levelData[levelIdx].getLevelNumber();
      if (level < activeLevel)
      {
        break;
      }

      final int[] listeners = levelData[levelIdx].getFunctions();
      for (int l = 0; l < listeners.length; l++)
      {
        final Expression expression = expressions[listeners[l]];
        if (deepTraversing && expression.isDeepTraversing() == false)
        {
          continue;
        }

        final ExpressionRuntime oldRuntime = expression.getRuntime();
        expression.setRuntime(runtime);
        final Function e = (Function) expression;
        try
        {
          e.reportFinished(event);
          final String name = expression.getName();
          if (name != null)
          {
            chEvent.setColumnName(name);
            chEvent.setColumnValue(expression.getValue());
            masterRow.dataRowChanged(chEvent);
          }
        }
        catch (Exception ex)
        {
          if (ExpressionDataRow.logger.isDebugEnabled())
          {
            ExpressionDataRow.logger.error("Failed to fire report-finished event", ex);
          }
          else
          {
            ExpressionDataRow.logger.error("Failed to fire report-finished event: " + ex);
          }
          addError(ex);
        }

        expression.setRuntime(oldRuntime);
      }
    }
  }

  private void fireReportInitializedEvent(final ReportEvent event)
  {
    final boolean deepTraversing = event.isDeepTraversing();
    final int activeLevel = processingContext.getProcessingLevel();
    for (int levelIdx = 0; levelIdx < levelData.length; levelIdx++)
    {
      final int level = levelData[levelIdx].getLevelNumber();
      if (level < activeLevel)
      {
        break;
      }

      final int[] listeners = levelData[levelIdx].getFunctions();
      for (int l = 0; l < listeners.length; l++)
      {
        final Expression expression = expressions[listeners[l]];
        if (deepTraversing && expression.isDeepTraversing() == false)
        {
          continue;
        }

        final ExpressionRuntime oldRuntime = expression.getRuntime();
        expression.setRuntime(runtime);
        final Function e = (Function) expression;
        try
        {
          e.reportInitialized(event);
          final String name = expression.getName();
          if (name != null)
          {
            chEvent.setColumnName(name);
            chEvent.setColumnValue(expression.getValue());
            masterRow.dataRowChanged(chEvent);
          }

        }
        catch (Exception ex)
        {
          if (ExpressionDataRow.logger.isDebugEnabled())
          {
            ExpressionDataRow.logger.error("Failed to fire report-initialized event", ex);
          }
          else
          {
            ExpressionDataRow.logger.error("Failed to fire report-initialized event: " + ex);
          }
          addError(ex);
        }

        expression.setRuntime(oldRuntime);
      }
    }
  }

  private void firePageStartedEvent(final ReportEvent event)
  {
    final boolean deepTraversing = event.isDeepTraversing();
    final int activeLevel = processingContext.getProcessingLevel();
    for (int levelIdx = 0; levelIdx < levelData.length; levelIdx++)
    {
      final int level = levelData[levelIdx].getLevelNumber();
      if (level < activeLevel)
      {
        break;
      }

      final int[] listeners = levelData[levelIdx].getPageEventListeners();
      for (int l = 0; l < listeners.length; l++)
      {
        final Expression expression = expressions[listeners[l]];
        if (deepTraversing && expression.isDeepTraversing() == false)
        {
          continue;
        }

        final ExpressionRuntime oldRuntime = expression.getRuntime();
        expression.setRuntime(runtime);
        final PageEventListener e = (PageEventListener) expression;
        try
        {
          e.pageStarted(event);
          final String name = expression.getName();
          if (name != null)
          {
            chEvent.setColumnName(name);
            chEvent.setColumnValue(expression.getValue());
            masterRow.dataRowChanged(chEvent);
          }

        }
        catch (Exception ex)
        {
          if (ExpressionDataRow.logger.isDebugEnabled())
          {
            ExpressionDataRow.logger.error("Failed to fire page-started event", ex);
          }
          else
          {
            ExpressionDataRow.logger.error("Failed to fire page-started event: " + ex);
          }
          addError(ex);
        }

        expression.setRuntime(oldRuntime);
      }
    }
  }

  private void firePageFinishedEvent(final ReportEvent event)
  {
    final boolean deepTraversing = event.isDeepTraversing();
    final int activeLevel = processingContext.getProcessingLevel();
    for (int levelIdx = 0; levelIdx < levelData.length; levelIdx++)
    {
      final int level = levelData[levelIdx].getLevelNumber();
      if (level < activeLevel)
      {
        break;
      }

      final int[] listeners = levelData[levelIdx].getPageEventListeners();
      for (int l = 0; l < listeners.length; l++)
      {
        final Expression expression = expressions[listeners[l]];
        if (deepTraversing && expression.isDeepTraversing() == false)
        {
          continue;
        }

        final ExpressionRuntime oldRuntime = expression.getRuntime();
        expression.setRuntime(runtime);
        final PageEventListener e = (PageEventListener) expression;
        try
        {
          e.pageFinished(event);
          final String name = expression.getName();
          if (name != null)
          {
            chEvent.setColumnName(name);
            chEvent.setColumnValue(expression.getValue());
            masterRow.dataRowChanged(chEvent);
          }

        }
        catch (Exception ex)
        {
          if (ExpressionDataRow.logger.isDebugEnabled())
          {
            ExpressionDataRow.logger.error("Failed to fire page-finished event", ex);
          }
          else
          {
            ExpressionDataRow.logger.error("Failed to fire page-finished event: " + ex);
          }
          addError(ex);
        }

        expression.setRuntime(oldRuntime);
      }
    }
  }

  public ExpressionDataRow derive(final MasterDataRow masterRow,
                                  final boolean update)
  {
    try
    {
      return new ExpressionDataRow(masterRow, this, update);
    }
    catch (final CloneNotSupportedException e)
    {
      logger.error("Error on derive(..): ", e);
      throw new IllegalStateException("Cannot clone? Cannot survive!");
    }
  }

  public boolean isErrorOccured()
  {
    if (errorList == null)
    {
      return false;
    }
    return errorList.isEmpty() == false;
  }

  public void clearErrors()
  {
    if (errorList == null)
    {
      return;
    }
    this.errorList.clear();
  }

  public Exception[] getErrors()
  {
    if (errorList == null)
    {
      return ExpressionDataRow.EMPTY_EXCEPTIONS;
    }
    return (Exception[]) errorList.toArray(new Exception[errorList.size()]);
  }

  private void addError(final Exception e)
  {
    if (errorList == null)
    {
      errorList = new ArrayList();
    }
    errorList.add(e);
  }

  public boolean isValid()
  {
    return levelData != null;
  }

  public Expression[] getExpressions()
  {
    final Expression[] retval = new Expression[length];
    System.arraycopy(expressions, 0, retval, 0, length);
    return retval;
  }

  public boolean isPrepareEventListener()
  {
    return prepareEventListener;
  }


  /**
   * Returns the current master-row instance to inner-classes.
   *
   * @return a reference to the master-row (to be used in inner classes).
   * @noinspection ProtectedMemberInFinalClass
   */
  protected MasterDataRow getMasterRow()
  {
    return masterRow;
  }

  /**
   * Returns the current processing context to inner-classes.
   *
   * @return a reference to the processing context (to be used in inner classes).
   * @noinspection ProtectedMemberInFinalClass
   */
  protected ProcessingContext getProcessingContext()
  {
    return processingContext;
  }


  public void refresh()
  {
    reactivateExpressions(false);
  }
}

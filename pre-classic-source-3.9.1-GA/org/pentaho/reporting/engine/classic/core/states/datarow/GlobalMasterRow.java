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

import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.ParameterDataRow;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.ReportEnvironmentDataRow;
import org.pentaho.reporting.engine.classic.core.ResourceBundleFactory;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.function.ProcessingContext;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterDefinitionEntry;
import org.pentaho.reporting.engine.classic.core.states.CascadingDataFactory;
import org.pentaho.reporting.engine.classic.core.states.EmptyDataFactory;
import org.pentaho.reporting.engine.classic.core.states.ReportState;
import org.pentaho.reporting.engine.classic.core.states.crosstab.CrosstabSpecification;
import org.pentaho.reporting.engine.classic.core.wizard.DataSchema;
import org.pentaho.reporting.engine.classic.core.wizard.DataSchemaDefinition;
import org.pentaho.reporting.engine.classic.core.wizard.DefaultDataAttributeContext;

/**
 * Creation-Date: Dec 13, 2006, 2:52:23 PM
 *
 * @author Thomas Morgner
 */
public final class GlobalMasterRow implements MasterDataRow
{
  private ReportEnvironmentDataRow environmentDataRow;
  private DataFactory dataFactory;
  private ReportDataRow reportDataRow;
  private ExpressionDataRow expressionDataRow;
  private ParameterDataRow parameterDataRow;
  private MasterDataRow parentRow;
  private FastGlobalView globalView;
  private ImportedVariablesDataRow importedDataRow;
  private DataSchemaDefinition schemaDefinition;
  private ProcessingDataSchemaCompiler schemaCompiler;
  private DataSchema dataSchema;
  private PaddingDataRow paddingDataRow;
  private int prePaddingCount;
  private ResourceBundleFactory resourceBundleFactory;
  private OutputProcessorMetaData outputProcessorMetaData;
  private ParameterDefinitionEntry[] parameterDefinitionEntries;

  private GlobalMasterRow()
  {
  }

  /**
   * Creates a new master-row. This is called only once when the report processing starts for the very first time.
   *
   * @param reportContext
   * @param schemaDefinition
   * @param parameterDataRow
   * @return
   */
  public static GlobalMasterRow createReportRow(final ProcessingContext reportContext,
                                                final DataSchemaDefinition schemaDefinition,
                                                final ParameterDataRow parameterDataRow,
                                                final ParameterDefinitionEntry[] parameterDefinitionEntries,
                                                final boolean includeStucturalProcessing)
  {
    if (reportContext == null)
    {
      throw new NullPointerException();
    }
    if (schemaDefinition == null)
    {
      throw new NullPointerException();
    }
    if (parameterDataRow == null)
    {
      throw new NullPointerException();
    }

    final GlobalMasterRow gmr = new GlobalMasterRow();
    gmr.globalView = new FastGlobalView();
    gmr.expressionDataRow = new ExpressionDataRow(gmr, reportContext, includeStucturalProcessing);
    gmr.schemaDefinition = schemaDefinition;
    gmr.dataFactory = new EmptyDataFactory();
    gmr.resourceBundleFactory = reportContext.getResourceBundleFactory();
    gmr.outputProcessorMetaData = reportContext.getOutputProcessorMetaData();
    final DefaultDataAttributeContext dac = new DefaultDataAttributeContext
        (gmr.outputProcessorMetaData, gmr.getResourceBundleFactory().getLocale());
    gmr.schemaCompiler =
        new ProcessingDataSchemaCompiler(schemaDefinition, dac, reportContext.getResourceManager(), null);
    gmr.dataSchema = null;
    gmr.parameterDefinitionEntries = parameterDefinitionEntries;
    gmr.setEnvironmentDataRow(new ReportEnvironmentDataRow(reportContext.getEnvironment()));
    gmr.setParameterDataRow(parameterDataRow);
    return gmr;
  }

  public MasterDataRow deriveSubDataRow(final ProcessingContext reportContext,
                                        final DataFactory reportFactory,
                                        final ParameterDataRow parameterDataRow,
                                        final ParameterDefinitionEntry[] parameterDefinitionEntries,
                                        final ResourceBundleFactory resourceBundleFactory)
  {
    if (reportContext == null)
    {
      throw new NullPointerException();
    }
    if (reportFactory == null)
    {
      throw new NullPointerException();
    }
    if (resourceBundleFactory == null)
    {
      throw new NullPointerException();
    }
    if (parameterDataRow == null)
    {
      throw new NullPointerException();
    }
    final GlobalMasterRow gmr = new GlobalMasterRow();
    gmr.outputProcessorMetaData = outputProcessorMetaData;
    gmr.schemaDefinition = schemaDefinition;
    gmr.schemaCompiler = schemaCompiler;
    gmr.globalView = new FastGlobalView();
    gmr.expressionDataRow = new ExpressionDataRow
        (gmr, reportContext, expressionDataRow.isIncludeStructuralProcessing());
    gmr.parentRow = this;
    gmr.dataSchema = null;
    gmr.resourceBundleFactory = resourceBundleFactory;
    gmr.parameterDefinitionEntries = parameterDefinitionEntries;

    final CascadingDataFactory dataFactory = new CascadingDataFactory();
    dataFactory.add(reportFactory);
    dataFactory.add(this.dataFactory);
    gmr.dataFactory = dataFactory;
    gmr.setParameterDataRow(parameterDataRow);
    gmr.setEnvironmentDataRow(environmentDataRow);
    return gmr;
  }

  public MasterDataRow deriveWithQueryData(final ReportDataRow tableData)
  {
    if (tableData == null)
    {
      throw new NullPointerException();
    }

    final GlobalMasterRow derived = (GlobalMasterRow) derive();
    derived.setReportDataRow(tableData);
    return derived;
  }

  public MasterDataRow deriveWithReturnFromQuery()
  {
    final GlobalMasterRow derived = (GlobalMasterRow) derive();
    derived.setReportDataRow(null);
    derived.setParameterDataRow(null);
    return derived;
  }

  public ParameterDefinitionEntry[] getParameterDefinitionEntries()
  {
    if (parameterDefinitionEntries == null)
    {
      return null;
    }
    return parameterDefinitionEntries.clone();
  }

  public DataFactory getDataFactory()
  {
    return dataFactory;
  }

  public DataSchema getDataSchema()
  {
    if (dataSchema == null)
    {
      try
      {
        dataSchema = schemaCompiler.compile(this, environmentDataRow.getEnvironment());
      }
      catch (ReportDataFactoryException re)
      {
        throw new IllegalStateException("Failed to compile data-schema - aborting report processing", re);
      }
    }
    return dataSchema;
  }

  public ReportDataRow getReportDataRow()
  {
    return reportDataRow;
  }

  public void setReportDataRow(final ReportDataRow reportDataRow)
  {
    if (this.reportDataRow != null)
    {
      final int dataColCount = this.reportDataRow.getColumnCount();
      for (int i = dataColCount - 1; i >= 0; i--)
      {
        final String columnName = this.reportDataRow.getColumnName(i);
        if (columnName != null)
        {
          globalView.removeColumn(columnName);
        }
      }
    }

    this.reportDataRow = reportDataRow;

    if (reportDataRow != null)
    {
      final boolean readable = reportDataRow.isReadable();
      final int dataColCount = reportDataRow.getColumnCount();
      for (int i = 0; i < dataColCount; i++)
      {
        final String columnName = reportDataRow.getColumnName(i);
        if (columnName != null)
        {
          if (readable)
          {
            final Object columnValue = reportDataRow.get(i);
            globalView.putField(columnName, columnValue, false);
          }
          else
          {
            globalView.putField(columnName, null, false);
          }
        }
      }
    }

    this.dataSchema = null;
  }

  public ExpressionDataRow getExpressionDataRow()
  {
    return expressionDataRow;
  }

  public ReportEnvironmentDataRow getEnvironmentDataRow()
  {
    return environmentDataRow;
  }

  public void setEnvironmentDataRow(final ReportEnvironmentDataRow environmentDataRow)
  {
    if (this.environmentDataRow != null)
    {
      final String[] columnNames = this.environmentDataRow.getColumnNames();
      for (int i = 0; i < columnNames.length; i++)
      {
        final String columnName = columnNames[i];
        if (columnName != null)
        {
          globalView.removeColumn(columnName);
        }
      }
    }

    this.environmentDataRow = environmentDataRow;

    if (environmentDataRow != null)
    {
      final String[] columnNames = environmentDataRow.getColumnNames();
      for (int i = 0; i < columnNames.length; i++)
      {
        final String columnName = columnNames[i];
        if (columnName != null)
        {
          final Object columnValue = environmentDataRow.get(columnName);
          globalView.putField(columnName, columnValue, false);
        }
      }
    }

    this.dataSchema = null;
  }

  public ParameterDataRow getParameterDataRow()
  {
    return parameterDataRow;
  }

  public void setParameterDataRow(final ParameterDataRow parameterDataRow)
  {
    if (this.parameterDataRow != null)
    {
      final String[] columnNames = this.parameterDataRow.getColumnNames();
      for (int i = 0; i < columnNames.length; i++)
      {
        final String columnName = columnNames[i];
        if (columnName != null)
        {
          globalView.removeColumn(columnName);
        }
      }
    }

    this.parameterDataRow = parameterDataRow;

    if (parameterDataRow != null)
    {
      final String[] columnNames = parameterDataRow.getColumnNames();
      for (int i = 0; i < columnNames.length; i++)
      {
        final String columnName = columnNames[i];
        if (columnName != null)
        {
          final Object columnValue = parameterDataRow.get(columnName);
          globalView.putField(columnName, columnValue, false);
        }
      }
    }

    this.dataSchema = null;
  }

  public DataRow getGlobalView()
  {
    return globalView;
  }

  public void dataRowChanged(final MasterDataRowChangeEvent chEvent)
  {
    // rebuild the global view and tracks changes ..
    final int type = chEvent.getType();
    if (type == MasterDataRowChangeEvent.COLUMN_ADDED)
    {
      globalView.putField(chEvent.getColumnName(), chEvent.getColumnValue(), false);
    }
    else if (type == MasterDataRowChangeEvent.COLUMN_UPDATED)
    {
      globalView.putField(chEvent.getColumnName(), chEvent.getColumnValue(), true);
    }
    else if (type == MasterDataRowChangeEvent.COLUMN_REMOVED)
    {
      globalView.removeColumn(chEvent.getColumnName());
    }
  }

  /**
   * This updates the global view.
   * todo
   */
  private void updateGlobalView()
  {
    if (parameterDataRow != null)
    {
      final String[] columnNames = parameterDataRow.getColumnNames();
      for (int i = 0; i < columnNames.length; i++)
      {
        final String columnName = columnNames[i];
        if (columnName != null)
        {
          final Object columnValue = parameterDataRow.get(columnName);
          globalView.putField(columnName, columnValue, true);
        }
      }
    }

    if (reportDataRow != null)
    {
      final int dataColCount = reportDataRow.getColumnCount();
      final boolean readable = reportDataRow.isReadable();
      for (int i = 0; i < dataColCount; i++)
      {
        final String columnName = reportDataRow.getColumnName(i);
        if (columnName != null)
        {
          if (readable)
          {
            final Object columnValue = reportDataRow.get(i);
            globalView.putField(columnName, columnValue, true);
          }
          else
          {
            globalView.putField(columnName, null, true);
          }
        }
      }
    }
  }

  public boolean isAdvanceable()
  {
    if (paddingDataRow != null)
    {
      if (prePaddingCount > 0)
      {
        return true;
      }

      if (paddingDataRow.getPrePaddingRows(globalView) > 0)
      {
        return true;
      }
      if (paddingDataRow.getPostPaddingRows(globalView) > 0)
      {
        return true;
      }
    }
    if (reportDataRow != null)
    {
      if (reportDataRow.isAdvanceable())
      {
        return true;
      }

      // at the end of the report, we should be also at the end of the columns ...
      if (paddingDataRow != null)
      {
        final int colsToGo = (paddingDataRow.getCrosstabColumnCount() - paddingDataRow.getCurrentCursorPosition()) - 1;
        if (colsToGo > 0)
        {
          prePaddingCount = colsToGo;
          return true;
        }
      }
    }
    return false;
  }

  public MasterDataRow derive()
  {
    final GlobalMasterRow o = new GlobalMasterRow();
    o.environmentDataRow = environmentDataRow;
    o.outputProcessorMetaData = outputProcessorMetaData;
    o.prePaddingCount = prePaddingCount;
    o.paddingDataRow = paddingDataRow;
    o.dataFactory = dataFactory;
    o.dataSchema = dataSchema;
    o.schemaCompiler = schemaCompiler;
    o.schemaDefinition = schemaDefinition;
    o.globalView = globalView.derive();
    o.reportDataRow = reportDataRow;
    o.parameterDataRow = parameterDataRow;
    o.resourceBundleFactory = resourceBundleFactory;
    o.expressionDataRow = expressionDataRow.derive(o, false);
    if (parentRow != null)
    {
      o.parentRow = parentRow.derive();
    }
    o.importedDataRow = importedDataRow;
    return o;
  }

  public void setImportedDataRow(final ImportedVariablesDataRow dataRow)
  {
    if (importedDataRow != null)
    {
      final String[] columnNames = importedDataRow.getColumnNames();
      for (int i = 0; i < columnNames.length; i++)
      {
        final String columnName = columnNames[i];
        if (columnName != null)
        {
          globalView.removeColumn(columnName);
        }
      }
    }

    this.importedDataRow = dataRow;
    if (importedDataRow != null)
    {
      final String[] columnNames = importedDataRow.getColumnNames();
      for (int i = 0; i < columnNames.length; i++)
      {
        final String columnName = columnNames[i];
        if (columnName != null)
        {
          final Object columnValue = importedDataRow.get(columnName);
          globalView.putField(columnName, columnValue, false);
        }
      }
    }

    this.dataSchema = null;
  }

  /**
   * The datarow that contains the values as imported from the subreport. Can be null
   *
   * @return the imported datarow.
   */
  public ImportedVariablesDataRow getImportedDataRow()
  {
    return importedDataRow;
  }

  public MasterDataRow getParentDataRow()
  {
    return parentRow;
  }

  /**
   * This advances the cursor by one row and updates the flags.
   *
   * @return
   */
  public MasterDataRow advance()
  {
    return advanceRecursively(false, null);
  }

  /**
   * This method is public as a implementation sideeffect. It is only intended to be used internally and no matter what
   * you intend: If you are not calling it from a MasterDataRow implementation, then you are on the wrong track.
   *
   * @param deepTraversingOnly
   * @param subReportRow
   * @return
   */
  public MasterDataRow advanceRecursively(final boolean deepTraversingOnly,
                                          final MasterDataRow subReportRow)
  {
    final GlobalMasterRow dataRow = new GlobalMasterRow();
    dataRow.environmentDataRow = environmentDataRow;
    dataRow.outputProcessorMetaData = outputProcessorMetaData;
    if (deepTraversingOnly == false)
    {
      dataRow.globalView = globalView.advance();
    }
    else
    {
      dataRow.globalView = globalView.derive();
    }
    dataRow.dataSchema = dataSchema;
    dataRow.dataFactory = dataFactory;
    dataRow.schemaCompiler = schemaCompiler;
    dataRow.schemaDefinition = schemaDefinition;
    dataRow.parameterDataRow = parameterDataRow;
    dataRow.resourceBundleFactory = resourceBundleFactory;
    boolean needActivate = false;
    if (deepTraversingOnly == false && paddingDataRow != null)
    {
      dataRow.paddingDataRow = paddingDataRow.advance();
      dataRow.prePaddingCount = prePaddingCount;
    }

    if (deepTraversingOnly == false && reportDataRow != null)
    {
      if (prePaddingCount > 0)
      {
        dataRow.prePaddingCount -= 1;
        dataRow.reportDataRow = reportDataRow;
        needActivate = true;
      }
      else
      {
        dataRow.reportDataRow = reportDataRow.advance();
      }
    }
    else
    {
      dataRow.reportDataRow = reportDataRow;
    }
    dataRow.updateGlobalView();
    if (expressionDataRow != null)
    {
      dataRow.expressionDataRow = expressionDataRow.derive(dataRow, true);
    }
    if (parentRow != null)
    {
      // the parent row should get a grip on our data as well - just for the
      // deep traversing fun and so on ..
      dataRow.parentRow = parentRow.advanceRecursively(true, dataRow);
    }

    if (importedDataRow != null)
    {
      if (subReportRow != null)
      {
        dataRow.importedDataRow = importedDataRow.refresh(subReportRow.getGlobalView(), subReportRow.getDataSchema());
        final String[] columnNames = dataRow.importedDataRow.getColumnNames();
        for (int i = 0; i < columnNames.length; i++)
        {
          final String columnName = columnNames[i];
          if (columnName != null)
          {
            final Object columnValue = dataRow.importedDataRow.get(columnName);
            dataRow.globalView.putField(columnName, columnValue, true);
          }
        }
      }
    }

    if (deepTraversingOnly == false && paddingDataRow != null)
    {
      if (needActivate)
      {
        dataRow.paddingDataRow.activate(dataRow);
      }
      else
      {
        dataRow.prePaddingCount = dataRow.paddingDataRow.getPostPaddingRows(dataRow.getGlobalView());
        if (dataRow.prePaddingCount > 0)
        {
          dataRow.paddingDataRow.activate(dataRow);
        }
      }
    }
    return dataRow;
  }

  public void fireReportEvent(final ReportEvent event)
  {
    if (expressionDataRow != null)
    {
      expressionDataRow.fireReportEvent(event);
    }
    if ((event.getType() & ReportEvent.NO_PARENT_PASSING_EVENT) == ReportEvent.NO_PARENT_PASSING_EVENT)
    {
      return;
    }
    if (parentRow != null)
    {
      final ReportState parentState = event.getState().getParentSubReportState();
      final ReportEvent deepEvent;
      if (parentState == null)
      {
        deepEvent = event;
      }
      else
      {
        deepEvent = new ReportEvent
            (parentState, event.getState(), event.getType() | ReportEvent.DEEP_TRAVERSING_EVENT);
      }
      parentRow.fireReportEvent(deepEvent);
      parentRow.updateImportedVariables(getGlobalView(), getDataSchema());
    }
  }

  public void updateImportedVariables(final DataRow globalView, final DataSchema dataSchema)
  {
    if (importedDataRow == null)
    {
      return;
    }

    // This advance is just an refresh ...
    importedDataRow = importedDataRow.refresh(globalView, dataSchema);
    final String[] columnNames = importedDataRow.getColumnNames();
    for (int i = 0; i < columnNames.length; i++)
    {
      final String columnName = columnNames[i];
      if (columnName != null)
      {
        final Object columnValue = importedDataRow.get(columnName);
        this.globalView.putField(columnName, columnValue, true);
      }
    }
  }

  public boolean isPrepareEventListener()
  {
    if (parentRow != null)
    {
      if (parentRow.isPrepareEventListener())
      {
        return true;
      }
    }

    if (expressionDataRow == null)
    {
      return false;
    }
    return expressionDataRow.isPrepareEventListener();
  }


  public MasterDataRow startCrosstabMode(final CrosstabSpecification crosstabSpecification)
  {
    final GlobalMasterRow retval = (GlobalMasterRow) derive();
    retval.paddingDataRow = new PaddingDataRow(crosstabSpecification);
    final int prePaddingRows = retval.paddingDataRow.getPrePaddingRows(retval.getGlobalView());
    if (prePaddingRows > 0)
    {
      // todo
      // The current position of the first data-row of this crosstab does point to the first computed column.
      // this means, we have to insert one or more artificial rows now.
//      DebugLog.log("PrePadding on StartCR: " + prePaddingRows);
      retval.paddingDataRow.activate(retval);
      retval.prePaddingCount = prePaddingRows;
    }
    return retval;
  }

  public MasterDataRow endCrosstabMode()
  {
    final GlobalMasterRow retval = (GlobalMasterRow) derive();
    retval.paddingDataRow = null;
    return retval;
  }

  public MasterDataRow resetRowCursor()
  {
    final GlobalMasterRow retval = (GlobalMasterRow) derive();
    if (retval.paddingDataRow != null)
    {
      retval.paddingDataRow = retval.paddingDataRow.resetRowCursor();

      final int prePaddingRows = retval.paddingDataRow.getPrePaddingRows(retval.getGlobalView());
      if (prePaddingRows > 0)
      {
        // The current position of the first data-row of this crosstab does point to the first computed column.
        // this means, we have to insert one or more artificial rows now.
//        DebugLog.log("PrePadding on StartCR: " + prePaddingRows);
        retval.paddingDataRow.activate(retval);
        retval.prePaddingCount = prePaddingRows;
      }
    }
    return retval;
  }

  public int getPrePaddingCount()
  {
    return prePaddingCount;
  }

  public MasterDataRow clearExportedParameters()
  {
    final GlobalMasterRow derived = (GlobalMasterRow) derive();
    derived.setImportedDataRow(null);
    derived.resetDataSchema();
    return derived;
  }

  public ResourceBundleFactory getResourceBundleFactory()
  {
    return resourceBundleFactory;
  }

  public void resetDataSchema()
  {
    this.dataSchema = null;
  }

  public GlobalMasterRow rebuild()
  {
    if (globalView.getColumnNames().length == 0)
    {
      return this;
    }

    if (parentRow != null)
    {
      throw new IllegalStateException
          ("This should be at the beginning of the master-report processing. No parent allowed.");
    }
    if (reportDataRow != null)
    {
      throw new IllegalStateException
          ("This should be at the beginning of the master-report processing. No report-data allowed.");
    }

    final GlobalMasterRow gmr = (GlobalMasterRow) derive();
    gmr.dataSchema = null;
    gmr.globalView = new FastGlobalView();
    gmr.parameterDataRow = null;
    gmr.setParameterDataRow(getParameterDataRow());
    return gmr;
  }

  public MasterDataRow updateDataSchema(final DataSchemaDefinition dataSchemaDefinition)
  {
    if (dataSchemaDefinition == null)
    {
      throw new NullPointerException();
    }

    final DefaultDataAttributeContext dac = new DefaultDataAttributeContext
        (outputProcessorMetaData, resourceBundleFactory.getLocale());
    final GlobalMasterRow gmr = (GlobalMasterRow) derive();
    gmr.schemaDefinition = dataSchemaDefinition;
    gmr.schemaCompiler = new ProcessingDataSchemaCompiler
        (dataSchemaDefinition, dac, schemaCompiler.getResourceManager(), schemaCompiler.getGlobalDefaults());
    gmr.dataSchema = null;
    return gmr;
  }

  public DataSchemaDefinition getDataSchemaDefinition()
  {
    return schemaDefinition;
  }

  public void refresh()
  {
    updateGlobalView();

    if (expressionDataRow != null)
    {
      expressionDataRow.refresh();
    }
  }
}

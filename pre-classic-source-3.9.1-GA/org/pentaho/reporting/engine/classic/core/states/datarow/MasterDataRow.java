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
import org.pentaho.reporting.engine.classic.core.ResourceBundleFactory;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.function.ProcessingContext;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterDefinitionEntry;
import org.pentaho.reporting.engine.classic.core.states.crosstab.CrosstabSpecification;
import org.pentaho.reporting.engine.classic.core.wizard.DataSchema;
import org.pentaho.reporting.engine.classic.core.wizard.DataSchemaDefinition;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public interface MasterDataRow
{
  public ResourceBundleFactory getResourceBundleFactory();

  public DataFactory getDataFactory();

  public DataSchemaDefinition getDataSchemaDefinition();

  public DataSchema getDataSchema();

  public void resetDataSchema();

  public ReportDataRow getReportDataRow();

  public ExpressionDataRow getExpressionDataRow();

  public ParameterDataRow getParameterDataRow();

  public DataRow getGlobalView();

  public ImportedVariablesDataRow getImportedDataRow();

  public void setImportedDataRow(final ImportedVariablesDataRow importedDataRow);

  public MasterDataRow getParentDataRow();


  public void dataRowChanged(MasterDataRowChangeEvent chEvent);

  public boolean isAdvanceable();

  public MasterDataRow advance();

  public MasterDataRow advanceRecursively(final boolean deepTraversingOnly,
                                          final MasterDataRow subReportRow);


  public void fireReportEvent(ReportEvent event);

  public boolean isPrepareEventListener();

  public MasterDataRow startCrosstabMode(CrosstabSpecification crosstabSpecification);

  public MasterDataRow endCrosstabMode();

  public MasterDataRow clearExportedParameters();

  public MasterDataRow derive();

  public MasterDataRow deriveSubDataRow(final ProcessingContext reportContext,
                                        final DataFactory dataFactory,
                                        final ParameterDataRow parameterDataRow,
                                        final ParameterDefinitionEntry[] parameterDefinitionEntries,
                                        final ResourceBundleFactory resourceBundleFactory);

  public MasterDataRow deriveWithQueryData(final ReportDataRow tableData);

  public MasterDataRow deriveWithReturnFromQuery();

  public ParameterDefinitionEntry[] getParameterDefinitionEntries();

  public void updateImportedVariables(final DataRow globalView, final DataSchema dataSchema);

  public MasterDataRow resetRowCursor();

  public int getPrePaddingCount();

  public GlobalMasterRow rebuild();

  public MasterDataRow updateDataSchema(DataSchemaDefinition dataSchemaDefinition);

  public void refresh();
}

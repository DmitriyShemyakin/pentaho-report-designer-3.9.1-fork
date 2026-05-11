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
 * Copyright (c) 2005 - 2009 Pentaho Corporation, Object Refinery Limited and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.testcases.base.basic.function;

import javax.swing.table.TableModel;

import org.pentaho.reporting.engine.classic.core.CompoundDataFactory;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.DefaultResourceBundleFactory;
import org.pentaho.reporting.engine.classic.core.ResourceBundleFactory;
import org.pentaho.reporting.engine.classic.core.wizard.DefaultDataSchema;
import org.pentaho.reporting.engine.classic.core.wizard.DataSchema;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.function.ProcessingContext;
import org.pentaho.reporting.engine.classic.core.states.DataRowConnector;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.config.DefaultConfiguration;

/**
 * Creation-Date: 24.01.2006, 17:11:46
 *
 * @author Thomas Morgner
 */
public class TestingExpressionRuntime implements ExpressionRuntime
{
  private DataRow dataRow;
  private Configuration configuration;
  private ResourceBundleFactory resourceBundleFactory;
  private TableModel data;
  private int currentRow;
  private ProcessingContext processingContext;
  private DataSchema dataSchema;

  public TestingExpressionRuntime(final TableModel data,
                                  final int currentRow,
                                  final ProcessingContext processingContext)
  {
    this.processingContext = processingContext;
    this.data = data;
    this.currentRow = currentRow;
    dataRow = new DataRowConnector();
    configuration = new DefaultConfiguration();
    resourceBundleFactory = new DefaultResourceBundleFactory();
    dataSchema = new DefaultDataSchema();
  }

  public DataFactory getDataFactory()
  {
    return new CompoundDataFactory();
  }

  public DataSchema getDataSchema()
  {
    return dataSchema;
  }

  public DataRow getDataRow()
  {
    return dataRow;
  }

  public Configuration getConfiguration()
  {
    return configuration;
  }

  public ResourceBundleFactory getResourceBundleFactory()
  {
    return resourceBundleFactory;
  }

  /** Access to the tablemodel was granted using report properties, now direct. */
  public TableModel getData()
  {
    return data;
  }

  /** Where are we in the current processing. */
  public int getCurrentRow()
  {
    return currentRow;
  }

  /**
   * The output descriptor is a simple string collections consisting of the
   * following components: exportclass/type/subtype
   * <p/>
   * For example, the PDF export would be: pageable/pdf The StreamHTML export
   * would return table/html/stream
   *
   * @return the export descriptor.
   */
  public String getExportDescriptor()
  {
    return processingContext.getExportDescriptor();
  }

  public ProcessingContext getProcessingContext()
  {
    return processingContext;
  }

  public int getCurrentGroup()
  {
    return 0;
  }

  public int getGroupStartRow(final String groupName)
  {
    return 0;
  }

  public int getGroupStartRow(final int groupIndex)
  {
    return 0;
  }
}

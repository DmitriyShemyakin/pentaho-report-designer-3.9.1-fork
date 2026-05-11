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
 * Copyright (c) 2001 - 2009 Object Refinery Ltd, Pentaho Corporation and Contributors.  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.testcases.drpad;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.ParameterDataRow;
import org.pentaho.reporting.engine.classic.core.function.ProcessingContext;
import org.pentaho.reporting.engine.classic.core.layout.output.DefaultProcessingContext;
import org.pentaho.reporting.engine.classic.core.states.ReportStateKey;
import org.pentaho.reporting.engine.classic.core.states.crosstab.CrosstabSpecification;
import org.pentaho.reporting.engine.classic.core.states.crosstab.SortedMergeCrosstabSpecification;
import org.pentaho.reporting.engine.classic.core.states.datarow.GlobalMasterRow;
import org.pentaho.reporting.engine.classic.core.states.datarow.MasterDataRow;
import org.pentaho.reporting.engine.classic.core.states.datarow.ReportDataRow;
import org.pentaho.reporting.engine.classic.core.wizard.DefaultDataSchemaDefinition;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;


/**
 * @deprecated
 */
public class DataRowPaddingTest
{
  private static final Log logger = LogFactory.getLog(DataRowPaddingTest.class);

  public DataRowPaddingTest()
  {
  }

  private static void addRow(final DefaultTableModel model, String region, String product, String year)
  {
    model.addRow(new Object[]{region, product, year});
  }

  public static TableModel createTableModel()
  {
    final DefaultTableModel model = new DefaultTableModel(new String[]{"Region", "Product", "Time"}, 0);
//    addRow(model, "AMEA", "Planes", "2001");
    addRow(model, "AMEA", "Planes", "2002");
    addRow(model, "AMEA", "Planes", "2003");
    addRow(model, "AMEA", "Planes", "2004");
    addRow(model, "Europe", "Planes", "2001");
//    addRow(model, "Europe", "Planes", "2003");
    addRow(model, "Europe", "Planes", "2004");
    addRow(model, "Japan", "Planes", "2002");
    addRow(model, "Japan", "Planes", "2003");
    return model;
  }

  public static void main(String[] args)
  {
    final CrosstabSpecification specification = buildCS();
    if (specification.size() != 4)
    {
      throw new IllegalStateException("Expected Size of 4 but got " + specification.size());
    }

    // second run. Now with padding ..
    final ProcessingContext prc = new DefaultProcessingContext();
    final GlobalMasterRow gmr = GlobalMasterRow.createReportRow
        (prc, new DefaultDataSchemaDefinition(), new ParameterDataRow(), null, false);
    final TableModel data = createTableModel();
    MasterDataRow wdata = gmr.deriveWithQueryData(new ReportDataRow(data));
    int advanceCount = 0;
    wdata = wdata.startCrosstabMode(specification);
    logger.debug("Region:  " + wdata.getGlobalView().get("Region"));
    logger.debug("Product: " + wdata.getGlobalView().get("Product"));
    logger.debug("Year:    " + wdata.getGlobalView().get("Time"));
    Object grpVal = wdata.getGlobalView().get("Region");
    while (wdata.isAdvanceable())
    {
      logger.debug("-- Advance -- ");
      MasterDataRow nextdata = wdata.advance();
      Object nextGrpVal = nextdata.getGlobalView().get("Region");
      if (ObjectUtilities.equal(grpVal, nextGrpVal) == false)
      {
        nextdata = nextdata.resetRowCursor();
      }

      logger.debug("Do Advance Count: " + nextdata.getReportDataRow().getCursor());
      logger.debug("Region:  " + nextdata.getGlobalView().get("Region"));
      logger.debug("Product: " + nextdata.getGlobalView().get("Product"));
      logger.debug("Year:    " + nextdata.getGlobalView().get("Time"));
      advanceCount += 1;
      wdata = nextdata;
      grpVal = nextGrpVal;
    }
    if (advanceCount != (data.getRowCount() - 1))
    {
      throw new IllegalStateException("Expected " + (data.getRowCount() - 1) + " but got " + advanceCount);
    }

  }

  private static CrosstabSpecification buildCS()
  {
    final ProcessingContext prc = new DefaultProcessingContext();
    final GlobalMasterRow gmr = GlobalMasterRow.createReportRow
        (prc, new DefaultDataSchemaDefinition(), new ParameterDataRow(), null, false);
    final TableModel data = createTableModel();
    MasterDataRow wdata = gmr.deriveWithQueryData(new ReportDataRow(data));
    final CrosstabSpecification cs = new SortedMergeCrosstabSpecification
        (createDummyKey(), new String[]{"Product", "Time"});
    int advanceCount = 0;
    System.out.println(wdata.getReportDataRow().getCursor());
    cs.startRow();
    cs.add(wdata.getGlobalView());
    Object grpVal = wdata.getGlobalView().get("Region");
    while (wdata.isAdvanceable())
    {
      final MasterDataRow nextdata = wdata.advance();
      Object nextGrpVal = nextdata.getGlobalView().get("Region");
      if (ObjectUtilities.equal(grpVal, nextGrpVal) == false)
      {
        cs.endRow();
        cs.startRow();
      }

      cs.add(nextdata.getGlobalView());
      logger.debug("Prepare Advance Count: " + nextdata.getReportDataRow().getCursor());
      advanceCount += 1;
      wdata = nextdata;
      grpVal = nextGrpVal;
    }
    cs.endRow();
    if (advanceCount != (data.getRowCount() - 1))
    {
      throw new IllegalStateException("Expected 6 but got " + advanceCount);
    }
    return cs;
  }

  private static ReportStateKey createDummyKey()
  {
    return new ReportStateKey();
  }
}

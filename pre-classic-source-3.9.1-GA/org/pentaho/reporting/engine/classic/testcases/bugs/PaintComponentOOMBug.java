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
 * Copyright (c) 2000 - 2009 Pentaho Corporation, Object Refinery Limited and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.testcases.bugs;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.testcases.TestSystem;
import org.pentaho.reporting.engine.classic.core.modules.output.table.csv.CSVReportUtil;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.HtmlReportUtil;
import org.pentaho.reporting.engine.classic.core.modules.output.table.xls.ExcelReportUtil;

public class PaintComponentOOMBug
{
  private static final String URLNAME = "org/pentaho/reporting/engine/classic/extensions/junit/bugs/resource/spanned-header.xml";
//  private static final String URLNAME = "/org/pentaho/reporting/engine/classic/extensions/junit/bugs/resource/csv-not-working.xml";

  private PaintComponentOOMBug ()
  {
  }

  public static void main (final String[] args)
          throws Exception
  {
    final String[] colnames = new String []{
      "GroupID", "NetID", "Nickname", "StudentID", "MiddleInitial",
      "FirstName", "LastName", "TransmitterID", "LineNumber", "Total_Value"

    };

    final String[][] data = new String[][]{
      colnames, colnames
    };
    final TableModel model = new DefaultTableModel(data, colnames);
//    final JFreeReport report = new BandInBandStackingDemoHandler().createReport();
    System.out.println (model.getRowCount());
    final MasterReport report = TestSystem.loadReport(URLNAME, model);
//    FunctionalityTestLib.createXLS(report);
    ExcelReportUtil.createXLS(report, "/tmp/export.xls");
    HtmlReportUtil.createStreamHTML(report, "/tmp/export.html");
//    FunctionalityTestLib.createXLS(report);
    CSVReportUtil.createCSV(report, "/tmp/export.csv");
  }

}

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
 * Copyright (c) 2009 Pentaho Corporation.  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.testcases.pages;

import javax.swing.table.TableModel;
import javax.swing.table.DefaultTableModel;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.testcases.TestSystem;

/**
 * Creation-Date: 12.07.2007, 14:31:14
 *
 * @deprecated not functional at all.
 */
public class VerticalAlignment
{
  public static void main(String[] args)
      throws ReportProcessingException
  {
    ClassicEngineBoot.getInstance().start();

    final TableModel data = new DefaultTableModel(1, 1);
    final MasterReport report = TestSystem.loadReport("org/pentaho/reporting/engine/classic/extensions/junit/pages/valign-report.xml", data);
    if (report == null)
    {
      System.exit(1);
    }

    TestSystem.showPreview(report);

  }
}

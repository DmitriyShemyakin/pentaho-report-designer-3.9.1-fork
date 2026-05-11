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
 * Copyright (c) 2007 - 2009 Pentaho Corporation, .  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.testcases.base.functionality;

import javax.swing.table.DefaultTableModel;
import javax.swing.JLabel;

import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ItemBand;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.ContentFieldElementFactory;

/**
 * @deprecated
 */
public class ManyAWTComponentsBugTest  extends AbstractReportExecuteTest
{
  public ManyAWTComponentsBugTest(String string)
  {
    super(string);
  }

  public void testAWTPrinting () throws Exception
  {
    final MasterReport report = new MasterReport();
    final ItemBand itemBand = report.getItemBand();
    final ContentFieldElementFactory cfef = new ContentFieldElementFactory();
    cfef.setFieldname("field");
    cfef.setMinimumWidth(new Float(500));
    cfef.setMinimumHeight(new Float(200));
    itemBand.addElement(cfef.createElement());

    final DefaultTableModel tableModel = new DefaultTableModel(new String[]{"field"}, 2000);
    for (int row = 0; row < tableModel.getRowCount(); row++)
    {
      tableModel.setValueAt(new JLabel("Value row = " + row), row, 0);
    }

    report.setDataFactory(new TableDataFactory ("default", tableModel));
   performExportTest(report, "AWT Component processing");
  }
}

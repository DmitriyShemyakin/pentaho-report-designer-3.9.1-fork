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

package org.pentaho.reporting.engine.classic.testcases.bugs;

import java.net.URL;

import javax.swing.table.DefaultTableModel;

import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.ReportGenerator;
import org.pentaho.reporting.engine.classic.testcases.BaseTest;
import org.pentaho.reporting.engine.classic.testcases.base.functionality.FunctionalityTestLib;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

public class Pre83ReportTest extends BaseTest
{
  private static final String URLNAME = "org/pentaho/reporting/engine/classic/testcases/bugs/resource/Pre83Test.xml";
  private static final String IMAGE_URLNAME = "org/pentaho/reporting/engine/classic/testcases/bugs/resource/logo.gif";

  public Pre83ReportTest()
  {
  }

  public static void testPre83Report() throws Exception
  {
    ClassicEngineBoot.getInstance().start();
    String[] COLNAMES = {
            "REGION"
    };

    final DefaultTableModel tableModel = new DefaultTableModel(COLNAMES,6);
    tableModel.setValueAt("A", 0, 0);
    tableModel.setValueAt("AA", 1, 0);
    tableModel.setValueAt("B", 2, 0);
    tableModel.setValueAt("BB", 3, 0);
    tableModel.setValueAt("C", 4, 0);
    tableModel.setValueAt("CC", 5, 0);

    final URL image = ObjectUtilities.getResource(IMAGE_URLNAME, Pre83ReportTest.class);
    final URL in = ObjectUtilities.getResource(URLNAME, Pre83ReportTest.class);
    final MasterReport report = ReportGenerator.getInstance().parseReport(in);

    final TableDataFactory tdf = new TableDataFactory("Test Query", tableModel);
    tdf.addTable("default", tableModel);
    report.setDataFactory(tdf);
    assertTrue(FunctionalityTestLib.execGraphics2D(report));
    
  }
}

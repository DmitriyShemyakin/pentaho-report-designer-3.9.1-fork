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

import java.net.URL;
import java.util.Date;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.PdfReportUtil;
import org.pentaho.reporting.engine.classic.testcases.BaseTest;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

public class PagebreakItembandTest extends BaseTest
{
  public PagebreakItembandTest ()
  {
  }
  /**
   * Returns the URL of the XML definition for this report.
   *
   * @return the URL of the report definition.
   */
  public URL getReportDefinitionSource()
  {
    return ObjectUtilities.getResourceRelative
        ("pagebreak-before-itemband.xml", PagebreakItembandTest.class); //$NON-NLS-1$
  }
  public void testPagebreakItemband() throws Exception
  {
    // Capactiy, Cost
    // Group, Location, Type, Container
    // Date Acquired

    Object[][] data = new Object[][]{
            {new Integer (10), new Integer (11),
            "Group A1", "Location", "Type1", "Container", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            new Date()},
            {new Integer (10), new Integer (12),
            "Group A2", "Location", "Type2", "Container", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            new Date()},
            {new Integer (10), new Integer (13),
            "Group A3", "Location", "Type3", "Container", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            new Date()},
            {new Integer (10), new Integer (14),
            "Group A4", "Location", "Type4", "Container", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            new Date()},
            {new Integer (10), new Integer (15),
            "Group A5", "Location", "Type5", "Container", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            new Date()},
            {new Integer (10), new Integer (16),
            "Group A6", "Location6", "Type6", "Container", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            new Date()},

    };

    Object[] names = new Object[]{
            "Capacity", "Cost", "Group", "Location", "Type", "Container", "Date Acquired" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ 
    };

    ClassicEngineBoot.getInstance().start();
    final TableModel dataModel = new DefaultTableModel(data, names);
    MasterReport report = parseReport(getReportDefinitionSource());
    report.setDataFactory(new TableDataFactory
        ("default", dataModel)); //$NON-NLS-1$
    PdfReportUtil.createPDF(report, "PagebreakItemband.pdf"); //$NON-NLS-1$
    assertTrue(true);
  }
}

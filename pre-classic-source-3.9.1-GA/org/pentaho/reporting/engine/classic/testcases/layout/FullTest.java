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

package org.pentaho.reporting.engine.classic.testcases.layout;

import java.net.URL;

import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.demo.ancient.demo.bookstore.BookstoreTableModel;
import org.pentaho.reporting.engine.classic.demo.util.ReportDefinitionException;
import org.pentaho.reporting.engine.classic.testcases.BaseTest;
import org.pentaho.reporting.engine.classic.testcases.base.functionality.FunctionalityTestLib;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

/**
 * @author gone, does not test anything
 */
public class FullTest extends BaseTest
{
  
  public URL getReportDefinitionSource()
  {
    return ObjectUtilities.getResourceRelative
        ("simplereport.xml", FullTest.class); //$NON-NLS-1$
  }

  public void testFull()
      throws ResourceException, ReportProcessingException, ReportDefinitionException
  {
    ClassicEngineBoot.getInstance().start();
    MasterReport report = parseReport(getReportDefinitionSource());
    report.setDataFactory(new TableDataFactory
        ("default", new BookstoreTableModel())); //$NON-NLS-1$;
    assertTrue(FunctionalityTestLib.execGraphics2D(report));
  }
}

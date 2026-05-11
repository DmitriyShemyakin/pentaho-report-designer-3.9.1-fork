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

package org.pentaho.reporting.engine.classic.testcases.bugs;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.demo.ancient.demo.world.CountryReportAPIDemoHandler;
import org.pentaho.reporting.engine.classic.demo.ancient.demo.world.CountryReportXMLDemoHandler;
import org.pentaho.reporting.engine.classic.demo.ancient.demo.world.CountryReportExtXMLDemoHandler;
import org.pentaho.reporting.engine.classic.demo.ancient.demo.world.CountryReportSecurityXMLDemoHandler;
import org.pentaho.reporting.engine.classic.demo.ancient.demo.groups.GroupsDemo;
import org.pentaho.reporting.engine.classic.demo.util.ReportDefinitionException;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class Pre435Test extends TestCase
{
  public Pre435Test()
  {
  }

  public Pre435Test(final String s)
  {
    super(s);
  }

  protected void setUp() throws Exception
  {
    ClassicEngineBoot.getInstance().start();
  }

  public void testLoadWorldDemos() throws ReportDefinitionException
  {
    final MasterReport report = new CountryReportAPIDemoHandler().createReport();
    assertTrue(report.getItemBand().getElementCount() > 0);

    final MasterReport report2 = new CountryReportXMLDemoHandler().createReport();
    assertTrue(report2.getItemBand().getElementCount() > 0);
    final MasterReport report3 = new CountryReportExtXMLDemoHandler().createReport();
    assertTrue(report3.getItemBand().getElementCount() > 0);
    final MasterReport report4 = new CountryReportSecurityXMLDemoHandler().createReport();
    assertTrue(report4.getItemBand().getElementCount() > 0);
    final MasterReport report5 = new CountryReportSecurityXMLDemoHandler().createReport();
    assertTrue(report5.getItemBand().getElementCount() > 0);

  }

  public void testLoadGroupDemo() throws ReportDefinitionException
  {
    final MasterReport report5 = new GroupsDemo().createReport();
    assertTrue(report5.getItemBand().getElementCount() > 0);
  }
}

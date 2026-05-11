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
 * Copyright (c) 2000 - 2009 Pentaho Corporation, Simba Management Limited and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.testcases.base.basic;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.MasterReport;

/**
 * @deprecated moved into engine core
 */
public class JFreeReportTest extends TestCase
{
  public JFreeReportTest(final String s)
  {
    super(s);
  }

  public void testCreate() throws Exception
  {
    final MasterReport report = new MasterReport();
    // Report name is null
    report.setName("MyTestReport");
    assertNotNull(report.getPageDefinition());
    assertNotNull(report.getExpressions());
    assertNotNull(report.getRootGroup());
    assertEquals(report.getGroupCount(), 1);
    assertNotNull(report.getItemBand());
    assertNotNull(report.getName());
    assertNotNull(report.getPageFooter());
    assertNotNull(report.getPageHeader());
    assertNotNull(report.getProperties());
    assertNotNull(report.getReportConfiguration());
    assertNotNull(report.getReportFooter());
    assertNotNull(report.getReportHeader());
    assertNotNull(report.getGroup(0)); // the default group must be defined ...
    assertNotNull(report.clone());
  }

  public void testSerialize() throws Exception
  {
    final MasterReport report = new MasterReport();
    final ByteArrayOutputStream bo = new ByteArrayOutputStream();
    final ObjectOutputStream out = new ObjectOutputStream(bo);
    out.writeObject(report);

    final ObjectInputStream oin = new ObjectInputStream(new ByteArrayInputStream(bo.toByteArray()));
    final MasterReport e2 = (MasterReport) oin.readObject();
    assertNotNull(e2); // cannot assert equals, as this is not implemented.
  }

  public void testClone() throws Exception
  {
    final MasterReport report = new MasterReport();
    assertNotNull(report.clone());
  }


}

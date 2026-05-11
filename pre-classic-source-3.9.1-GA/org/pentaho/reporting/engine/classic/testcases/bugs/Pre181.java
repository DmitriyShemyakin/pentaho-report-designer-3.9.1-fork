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

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.Group;
import org.pentaho.reporting.engine.classic.demo.util.ReportDefinitionException;
import org.pentaho.reporting.engine.classic.demo.ancient.demo.form.SimplePatientFormDemo;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;
import org.pentaho.reporting.engine.classic.testcases.base.functionality.FunctionalityTestLib;

/**
 * Creation-Date: 28.11.2007, 14:21:14
 *
 * @author Thomas Morgner
 */
public class Pre181 extends TestCase
{
  public Pre181(String string)
  {
    super(string);
  }


  protected void setUp() throws Exception
  {
    ClassicEngineBoot.getInstance().start();
  }

  public void testBugPre181 () throws ReportDefinitionException
  {
    final SimplePatientFormDemo demo = new SimplePatientFormDemo();
    final MasterReport report = demo.createReport();
    final Group group = report.getGroupByName("Patient-Group");
    group.getFooter().getStyle().setStyleProperty(BandStyleKeys.FIXED_POSITION, new Float(1000));
    assertTrue(FunctionalityTestLib.execGraphics2D(report));
    
  }
}

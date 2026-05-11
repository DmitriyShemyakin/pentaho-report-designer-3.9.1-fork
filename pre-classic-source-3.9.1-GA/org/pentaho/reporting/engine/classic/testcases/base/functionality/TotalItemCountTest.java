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

package org.pentaho.reporting.engine.classic.testcases.base.functionality;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.Group;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.RelationalGroup;
import org.pentaho.reporting.engine.classic.demo.util.ReportDefinitionException;
import org.pentaho.reporting.engine.classic.demo.ancient.demo.world.CountryReportSecurityXMLDemoHandler;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.function.AbstractFunction;
import org.pentaho.reporting.engine.classic.core.function.FunctionUtilities;
import org.pentaho.reporting.engine.classic.core.function.TotalItemCountFunction;

/**
 * @deprecated
 */
public class TotalItemCountTest extends TestCase
{
  private static final int[] GROUPCOUNTS = new int[]{
    2, 3, 1, 14, 2, 1
  };

  private static class TotalItemCountVerifyFunction
      extends AbstractFunction
  {
    private int index;

    /**
     * Creates an unnamed function. Make sure the name of the function is set using
     * {@link #setName} before the function is added to the report's function collection.
     */
    public TotalItemCountVerifyFunction()
    {
      setName("verification");
    }

    /**
     * Receives notification that report generation initializes the current run.
     * <P>
     * The event carries a ReportState.Started state.  Use this to initialize the report.
     *
     * @param event The event.
     */
    public void reportInitialized(ReportEvent event)
    {
      index = 0;
    }

    /**
     * Receives notification that a group has finished.
     *
     * @param event  the event.
     */
    public void groupFinished(ReportEvent event)
    {
      if (event.getLevel() >= 0)
      {
        return;
      }
      assertCount(event);
      index += 1;
    }

    /**
     * Receives notification that a group has started.
     *
     * @param event  the event.
     */
    public void groupStarted(ReportEvent event)
    {
      if (event.getLevel() >= 0)
      {
        return;
      }
      assertCount(event);
    }

    private void assertCount(ReportEvent event)
    {
      // The itemcount function is only valid within the defined group.
      if (FunctionUtilities.getCurrentGroup(event).getName().equals("Continent Group"))
      {
        // the number of continents in the report1
        Number n = (Number) event.getDataRow().get("continent-total-gc");
        assertEquals("continent-total-gc", GROUPCOUNTS[index], n.intValue());
      }

      // the number of continents in the report1 + default group start
      Number n2 = (Number) event.getDataRow().get("total-gc");
      assertEquals("total-gc", 23, n2.intValue());
    }

    public Object getValue()
    {
      return null;
    }
  }

  public TotalItemCountTest()
  {
  }

  public TotalItemCountTest(final String s)
  {
    super(s);
  }

  protected void setUp() throws Exception
  {
    ClassicEngineBoot.getInstance().start();
  }

  public void testGroupItemCount() throws ReportDefinitionException
  {
    CountryReportSecurityXMLDemoHandler demoHandler = new CountryReportSecurityXMLDemoHandler();
    MasterReport report = demoHandler.createReport();
    report.addExpression(new TotalItemCountVerifyFunction());
    final RelationalGroup g = report.getGroupByName("default");
    if (g != null)
    {
      report.removeGroup(g);
    }

    TotalItemCountFunction f = new TotalItemCountFunction();
    f.setName("continent-total-gc");
    f.setGroup("Continent Group");
    f.setDependencyLevel(1);
    report.addExpression(f);

    TotalItemCountFunction f2 = new TotalItemCountFunction();
    f2.setName("total-gc");
    f2.setDependencyLevel(1);
    report.addExpression(f2);

    assertTrue(FunctionalityTestLib.execGraphics2D(report));


  }
}

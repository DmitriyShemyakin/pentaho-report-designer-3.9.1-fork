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
import org.pentaho.reporting.engine.classic.demo.ancient.demo.world.CountryReportXMLDemoHandler;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.function.AbstractFunction;
import org.pentaho.reporting.engine.classic.core.function.FunctionUtilities;
import org.pentaho.reporting.engine.classic.core.function.TotalGroupSumFunction;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @deprecated
 */
public class TotalGroupSumTest extends TestCase
{
  private static final Log logger = LogFactory.getLog(TotalGroupSumTest.class);

  private static final int[] SUMS = {
     69698070,
   1340100000,
     18751000,
    343344776,
    304357300,
    165715400
  };

  private static class TotalGroupCountVerifyFunction
      extends AbstractFunction
  {
    private int index;
    /**
     * Creates an unnamed function. Make sure the name of the function is set using
     * {@link #setName} before the function is added to the report's function collection.
     */
    public TotalGroupCountVerifyFunction()
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
    public void reportInitialized(final ReportEvent event)
    {
      index = 0;
    }

    /**
     * Receives notification that a group has finished.
     *
     * @param event  the event.
     */
    public void groupFinished(final ReportEvent event)
    {
      if (event.getLevel() >= 0)
      {
        return;
      }
      assertSum(event);
      index += 1;
    }

    /**
     * Receives notification that a group has started.
     *
     * @param event  the event.
     */
    public void groupStarted(final ReportEvent event)
    {
      if (event.getLevel() >= 0)
      {
        return;
      }
      assertSum(event);
    }

    private void assertSum(final ReportEvent event)
    {
      // the number of continents in the report1
      if ("Continent Group".equals(FunctionUtilities.getCurrentGroup(event).getName()))
      {
        final Number n = (Number) event.getDataRow().get("continent-total-gc");
        assertEquals("continent-total-gc", SUMS[index], n.intValue());
      }
//      // the number of continents in the report1 + default group start
//      Number n2 = (Number) event.getDataRow().get("total-gc");
//      assertEquals("total-gc", 7, n2.intValue());
    }

    public Object getValue()
    {
      return null;
    }
  }

  public TotalGroupSumTest()
  {
  }

  public TotalGroupSumTest(final String s)
  {
    super(s);
  }

  protected void setUp() throws Exception
  {
    ClassicEngineBoot.getInstance().start();
  }

  public void testGroupSumTest()
  {
    final CountryReportXMLDemoHandler demoHandler =
            new CountryReportXMLDemoHandler();
    try
    {
      final MasterReport report = demoHandler.createReport();
      report.addExpression(new TotalGroupCountVerifyFunction());
      // make sure that there is no default group ...
      final RelationalGroup g = report.getGroupByName("default");
      if (g != null)
      {
        report.removeGroup(g);
      }

      final TotalGroupSumFunction f = new TotalGroupSumFunction();
      f.setName("continent-total-gc");
      f.setGroup("Continent Group");
      f.setField("Population");
      f.setDependencyLevel(1);
      report.addExpression(f);

      final TotalGroupSumFunction f2 = new TotalGroupSumFunction();
      f2.setName("total-gc");
      f2.setField("Population");
      f2.setDependencyLevel(1);
      report.addExpression(f2);

      assertTrue(FunctionalityTestLib.execGraphics2D(report));
    }
    catch (Exception e)
    {
      logger.debug("Failed to parse " + demoHandler.getReportDefinitionSource(), e);
      fail();
    }


  }
}

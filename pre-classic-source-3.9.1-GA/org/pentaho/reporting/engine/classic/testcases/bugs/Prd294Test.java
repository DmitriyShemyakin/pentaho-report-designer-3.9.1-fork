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

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import javax.swing.table.DefaultTableModel;

import org.pentaho.reporting.engine.classic.core.ElementAlignment;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.TextFieldElementFactory;
import org.pentaho.reporting.engine.classic.core.function.RowBandingFunction;
import org.pentaho.reporting.engine.classic.core.style.FontDefinition;
import org.pentaho.reporting.engine.classic.testcases.BaseTest;
import org.pentaho.reporting.engine.classic.testcases.base.functionality.FunctionalityTestLib;

/**
 * Creation-Date: 06.10.2007, 18:18:04
 *
 * @author Thomas Morgner
 */
public class Prd294Test extends BaseTest
{
  public static void testPrd294()
  {
    MasterReport report = new MasterReport();
    report.setName("RowBandingTest");
    report.getItemBand().addElement(TextFieldElementFactory.createStringElement("tf1",
        new Rectangle2D.Double(20, 0, 100, 18),
        Color.BLACK,
        ElementAlignment.LEFT,
        ElementAlignment.MIDDLE,//change to ElementAlignment.TOP
        new FontDefinition("dialog", 12),
        "-",
        "column"));

    RowBandingFunction switchFunction = new RowBandingFunction();
    switchFunction.setInitialState(true);
    switchFunction.setNumberOfElements(1);
    report.getExpressions().add(switchFunction);

    report.getReportConfiguration().setConfigProperty("org.pentaho.reporting.engine.classic.core.layout.fontrenderer.UseMaxCharBounds", Boolean.TRUE.toString());

    DefaultTableModel tm1 = new DefaultTableModel(new Object[][]{{"1"}, {"2"}, {"3"}, {"4"}, {"5"}, {"6"}, {"7"}, {"8"}, {"9"}}, new Object[]{"column"});

    TableDataFactory dataFactory = new TableDataFactory("default", tm1);
    report.setDataFactory(dataFactory);

    ClassicEngineBoot.getInstance().start();
    assertTrue(FunctionalityTestLib.execGraphics2D(report));
  }
}

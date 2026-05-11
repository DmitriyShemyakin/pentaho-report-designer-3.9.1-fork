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

import java.awt.geom.Point2D;
import javax.swing.table.DefaultTableModel;

import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.RelationalGroup;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.LabelElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.TextFieldElementFactory;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;
import org.pentaho.reporting.engine.classic.testcases.BaseTest;
import org.pentaho.reporting.engine.classic.testcases.base.functionality.FunctionalityTestLib;
import org.pentaho.reporting.libraries.base.util.FloatDimension;

/**
 * Creation-Date: 27.10.2005, 16:55:00
 * <p/>
 * Column: 0 Name = crew; DataType = class java.lang.Object
 * Column: 1 Name = trial; DataType = class java.lang.Object
 * Column: 2 Name = run; DataType = class java.lang.Object
 * Column: 3 Name = FloatVar; DataType = class java.lang.Object
 * Column: 4 Name = IntVar; DataType = class java.lang.Object
 * Column: 5 Name = run; DataType = class java.lang.Object
 * Column: 6 Name = clock; DataType = class java.lang.Object
 *
 * @author Thomas Morgner
 */
public class InvalidColumnTest extends BaseTest
{
  public static void testInvalidColumn()
  {
    ClassicEngineBoot.getInstance().start();
    String[] COLNAMES = {
        "crew", "trial", "run", "FloatVar", "IntVar", "run", "clock"
    };

    final DefaultTableModel tableModel = new DefaultTableModel(COLNAMES, 8000);

    final MasterReport report = new MasterReport();
    report.setDataFactory(new TableDataFactory
        ("default", tableModel));

    final RelationalGroup group = new RelationalGroup();
    group.setName("Run");
    group.addField("run");

    LabelElementFactory labelFactory = new LabelElementFactory();
    labelFactory.setAbsolutePosition(new Point2D.Float(0, 0));
    labelFactory.setMinimumSize(new FloatDimension(160, 12));
    labelFactory.setText("Crew:");
    group.getHeader().addElement(labelFactory.createElement());

    TextFieldElementFactory textFieldFactory = new TextFieldElementFactory();
    textFieldFactory.setFieldname("crew");
    textFieldFactory.setAbsolutePosition(new Point2D.Float(50, 0));
    textFieldFactory.setMinimumSize(new FloatDimension(-100, 12));
    group.getHeader().addElement(textFieldFactory.createElement());

    labelFactory = new LabelElementFactory();
    labelFactory.setAbsolutePosition(new Point2D.Float(100, 0));
    labelFactory.setMinimumSize(new FloatDimension(160, 12));
    labelFactory.setText("Trial:");
    group.getHeader().addElement(labelFactory.createElement());

    textFieldFactory = new TextFieldElementFactory();
    textFieldFactory.setFieldname("trial");
    textFieldFactory.setAbsolutePosition(new Point2D.Float(150, 0));
    textFieldFactory.setMinimumSize(new FloatDimension(-100, 12));
    group.getHeader().addElement(textFieldFactory.createElement());

    labelFactory = new LabelElementFactory();
    labelFactory.setAbsolutePosition(new Point2D.Float(200, 0));
    labelFactory.setMinimumSize(new FloatDimension(160, 12));
    labelFactory.setText("Run:");
    group.getHeader().addElement(labelFactory.createElement());

    textFieldFactory = new TextFieldElementFactory();
    textFieldFactory.setFieldname("run");
    textFieldFactory.setAbsolutePosition(new Point2D.Float(250, 0));
    textFieldFactory.setMinimumSize(new FloatDimension(-100, 12));
    group.getHeader().addElement(textFieldFactory.createElement());

    group.getFooter().getStyle().setStyleProperty(ElementStyleKeys.MIN_HEIGHT, new Float(15));

    report.addGroup(group);

    final Band b = new Band();
    b.setName("variables");
    b.getStyle().setStyleProperty(TextStyleKeys.BOLD, Boolean.FALSE);
    b.getStyle().setStyleProperty(TextStyleKeys.FONT, "SansSerif");
    b.getStyle().setStyleProperty(TextStyleKeys.FONTSIZE, new Integer(10));
//    b.setLayoutCacheable(false);

    for (int i = 3, max = tableModel.getColumnCount(); i < max; ++i)
    {
      TextFieldElementFactory tFF = new TextFieldElementFactory();
      tFF.setFieldname(tableModel.getColumnName(i));
      tFF.setAbsolutePosition(new Point2D.Float(200 * (i - 3), 0));
      tFF.setMinimumSize(new FloatDimension(200, 12));
      b.addElement(tFF.createElement());
    }
    report.getItemBand().addElement(b);

    assertTrue(FunctionalityTestLib.execGraphics2D(report));
  }
}

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
 * Copyright (c) 2005 - 2009 Pentaho Corporation, Object Refinery Limited and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.testcases.bugs;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Rectangle2D;
import javax.swing.table.DefaultTableModel;

import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.ElementAlignment;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.LabelElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.RectangleElementFactory;
import org.pentaho.reporting.engine.classic.core.modules.gui.base.PreviewFrame;
import org.pentaho.reporting.engine.classic.core.style.FontDefinition;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;

/**
 * Creation-Date: 16.01.2006, 18:37:30
 *
 * @author Thomas Morgner
 */
public class FontAscentBug
{
  public static void main(String[] args)
  {
    ClassicEngineBoot.getInstance().start();

    MasterReport report = new MasterReport();
    report.setName("ReportTextLayout001");

    final RectangleElementFactory rectangleElementFactory = new RectangleElementFactory();
    rectangleElementFactory.setColor(Color.GREEN);
    rectangleElementFactory.setStroke(new BasicStroke(1));
    rectangleElementFactory.setX(new Float (0));
    rectangleElementFactory.setY(new Float (10));
    rectangleElementFactory.setMinimumWidth(new Float (-100));
    rectangleElementFactory.setMinimumHeight(new Float (104));
    rectangleElementFactory.setShouldFill(Boolean.TRUE);
    rectangleElementFactory.setShouldDraw(Boolean.TRUE);

    Element labelElement = LabelElementFactory.createLabelElement("Label1",
        new Rectangle2D.Double(0, 10, -100, 104),
        Color.BLACK,
        ElementAlignment.LEFT,
        new FontDefinition("Arial", 40),
        "ppp Title ");
    labelElement.getStyle().setStyleProperty(TextStyleKeys.UNDERLINED, Boolean.TRUE);
    labelElement.getStyle().setStyleProperty(TextStyleKeys.STRIKETHROUGH, Boolean.TRUE);


    report.getReportHeader().addElement(rectangleElementFactory.createElement());
    report.getReportHeader().addElement(labelElement);

    report.setDataFactory(new TableDataFactory
        ("default", new DefaultTableModel()));

    ClassicEngineBoot.getInstance().start();
    PreviewFrame preview = new PreviewFrame(report);
    preview.pack();
    preview.setVisible(true);

  }
}
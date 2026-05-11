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

import java.awt.Color;
import java.awt.geom.Point2D;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.ElementAlignment;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.elementfactory.TextFieldElementFactory;
import org.pentaho.reporting.libraries.base.util.FloatDimension;

/**
 * @deprecated Merged with bandtest in core
 */
public class BandRemoveTest extends TestCase
{
  public BandRemoveTest(String s)
  {
    super(s);
  }

  public void testRemoveElement()
  {
    final MasterReport report = new MasterReport();
    report.setName("A Very Simple Report");


    TextFieldElementFactory factory = new TextFieldElementFactory();
    factory.setName("T1");
    factory.setAbsolutePosition(new Point2D.Float(0, 0));
    factory.setMinimumSize(new FloatDimension(150, 20));
    factory.setColor(Color.black);
    factory.setHorizontalAlignment(ElementAlignment.LEFT);
    factory.setVerticalAlignment(ElementAlignment.MIDDLE);
    factory.setNullString("-");
    factory.setFieldname("Column1");

    final Element element1 = factory.createElement();
    report.getItemBand().addElement(element1);

    factory = new TextFieldElementFactory();
    factory.setName("T2");
    factory.setAbsolutePosition(new Point2D.Float(200, 0));
    factory.setMinimumSize(new FloatDimension(150, 20));
    factory.setColor(Color.black);
    factory.setHorizontalAlignment(ElementAlignment.LEFT);
    factory.setVerticalAlignment(ElementAlignment.MIDDLE);
    factory.setNullString("-");
    factory.setFieldname("Column2");

    final Element element2 = factory.createElement();
    report.getItemBand().addElement(element2);

    //report.getStyleSheetCollection().debug();

    report.getItemBand().removeElement(element1);
    //report.getStyleSheetCollection().debug();
    FunctionalityTestLib.execGraphics2D(report);
  }

  public void testRemoveElementComplete()
  {
    final MasterReport report = new MasterReport();
    report.setName("A Very Simple Report");


    TextFieldElementFactory factory = new TextFieldElementFactory();
    factory.setName("T1");
    factory.setAbsolutePosition(new Point2D.Float(0, 0));
    factory.setMinimumSize(new FloatDimension(150, 20));
    factory.setColor(Color.black);
    factory.setHorizontalAlignment(ElementAlignment.LEFT);
    factory.setVerticalAlignment(ElementAlignment.MIDDLE);
    factory.setNullString("-");
    factory.setFieldname("Column1");

    final Element element1 = factory.createElement();
    report.getItemBand().addElement(element1);

    factory = new TextFieldElementFactory();
    factory.setName("T2");
    factory.setAbsolutePosition(new Point2D.Float(200, 0));
    factory.setMinimumSize(new FloatDimension(150, 20));
    factory.setColor(Color.black);
    factory.setHorizontalAlignment(ElementAlignment.LEFT);
    factory.setVerticalAlignment(ElementAlignment.MIDDLE);
    factory.setNullString("-");
    factory.setFieldname("Column2");

    final Element element2 = factory.createElement();
    report.getItemBand().addElement(element2);

    //report.getStyleSheetCollection().debug();

    report.getItemBand().removeElement(element1);
    report.getItemBand().removeElement(element2);

    //report.getStyleSheetCollection().debug();
    FunctionalityTestLib.execGraphics2D(report);

  }

}

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

package org.pentaho.reporting.engine.classic.testcases.base.functionality.demohandler;

import java.net.URL;
import java.awt.geom.Rectangle2D;
import java.awt.Color;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.pentaho.reporting.engine.classic.demo.util.AbstractDemoHandler;
import org.pentaho.reporting.engine.classic.demo.util.ReportDefinitionException;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ElementAlignment;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.modules.gui.base.PreviewDialog;
import org.pentaho.reporting.engine.classic.core.elementfactory.LabelElementFactory;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.FontDefinition;

/**
 * @deprecated
 */
public class PaddingTestHandler extends AbstractDemoHandler
{
  public PaddingTestHandler()
  {
  }

  public String getDemoName()
  {
    return "Testcase: A totally empty report must not crash";
  }

  public MasterReport createReport() throws ReportDefinitionException
  {
    MasterReport report = new MasterReport();

    report.setName("BorderTest");

    Element label1 = LabelElementFactory.createLabelElement("Label1",
        new Rectangle2D.Double(0, 0, 200, 100),
        Color.RED,
        ElementAlignment.LEFT,
        new FontDefinition("Arial", 12),
        "Label1");

    report.getReportHeader().addElement(label1);

    label1.getStyle().setStyleProperty(ElementStyleKeys.BACKGROUND_COLOR, new Color(255, 127, 127, 120));

    Element label2 = LabelElementFactory.createLabelElement("Label2",
        new Rectangle2D.Double(0, 110, 200, 100),
        Color.RED,
        ElementAlignment.LEFT,
        new FontDefinition("Arial", 12),
        "Label2");

    report.getReportHeader().addElement(label2);

    label2.getStyle().setStyleProperty(ElementStyleKeys.BACKGROUND_COLOR, new Color(255, 127, 127, 120));
    label2.getStyle().setStyleProperty(ElementStyleKeys.PADDING_TOP, new Float(10));
    label2.getStyle().setStyleProperty(ElementStyleKeys.PADDING_LEFT, new Float(10));
    label2.getStyle().setStyleProperty(ElementStyleKeys.PADDING_RIGHT, new Float(10));
    label2.getStyle().setStyleProperty(ElementStyleKeys.PADDING_BOTTOM, new Float(10));


    Element label3 = LabelElementFactory.createLabelElement("Label3",
        new Rectangle2D.Double(210, 0, 200, 100),
        Color.RED,
        ElementAlignment.LEFT,
        new FontDefinition("Arial", 12),
        "Label3");

    report.getReportHeader().addElement(label3);

    label3.getStyle().setStyleProperty(ElementStyleKeys.BACKGROUND_COLOR, new Color(255, 127, 127, 120));
    label3.getStyle().setStyleProperty(ElementStyleKeys.PADDING_TOP, new Float(10));
    label3.getStyle().setStyleProperty(ElementStyleKeys.PADDING_LEFT, new Float(10));
    label3.getStyle().setStyleProperty(ElementStyleKeys.PADDING_RIGHT, new Float(10));
    label3.getStyle().setStyleProperty(ElementStyleKeys.PADDING_BOTTOM, new Float(10));
    return report;
  }

  public URL getDemoDescriptionSource()
  {
    return null;
  }

  public JComponent getPresentationComponent()
  {
    return new JPanel();
  }

  public static void main(String[] args)
      throws ReportDefinitionException
  {
    ClassicEngineBoot.getInstance().start();
    PaddingTestHandler handler = new PaddingTestHandler();
    PreviewDialog dialog = new PreviewDialog(handler.createReport());
    dialog.setSize(600, 700);
    dialog.setModal(true);
    dialog.setVisible(true);
    System.exit(0);
  }

}

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
 * Copyright (c) 2001 - 2009 Object Refinery Ltd, Pentaho Corporation and Contributors.  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.testcases.bugs;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import javax.swing.table.DefaultTableModel;

import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.ElementAlignment;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.RelationalGroup;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.HorizontalLineElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.LabelElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.MessageFieldElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.NumberFieldElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.TextElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.TextFieldElementFactory;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.HtmlReportUtil;
import org.pentaho.reporting.engine.classic.core.util.IntegerCache;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class Pre205Test
{
  private static final int FONTSIZE = 9;
  private static final String FONTNAME = "SansSerif";

  public Pre205Test()
  {
  }

  private MasterReport buildSampleReport()
  {
    MasterReport jfreeReport = new MasterReport();
    jfreeReport.setName("Sample Group Report");
    final int width = (int) jfreeReport.getPageDefinition().getWidth();

    final String[] columns = {"column0", "column1", "column2", "column3", "type"};
    final String numberFormat = "$#,##0.00";
    jfreeReport.getPageHeader().addElement(messageField("Sample Group Report",//
        true, 14, ElementAlignment.CENTER, new Rectangle(0, 0, width, 16)));
    RelationalGroup group = new RelationalGroup();
    group.setName("group");
    group.addField("type");
    // group.getHeader().setRepeat(true);
    group.getHeader().addElement(label("Column 0", true, ElementAlignment.CENTER, new Rectangle(0, 0, 120, 14)));
    group.getHeader().addElement(label("Column 1", true, ElementAlignment.CENTER, new Rectangle(130, 0, 120, 14)));
    group.getHeader().addElement(label("Column 2", true, ElementAlignment.CENTER, new Rectangle(260, 0, 120, 14)));
    group.getHeader().addElement(label("Column 3", true, ElementAlignment.CENTER, new Rectangle(390, 0, 70, 14)));
    group.getHeader().addElement(HorizontalLineElementFactory.createHorizontalLine(16));
    group.getHeader().setRepeat(false);
    group.getHeader().setPagebreakBeforePrint(true);
    group.getFooter().setPagebreakAfterPrint(true);
    jfreeReport.addGroup(group);
    // report data item elements
    jfreeReport.getReportFooter().setPagebreakBeforePrint(true);
    jfreeReport.getItemBand().addElement(textField(columns[0], false, new Rectangle(0, 0, 120, 14)));
    jfreeReport.getItemBand().addElement(textField(columns[1], false, new Rectangle(130, 0, 120, 14)));
    jfreeReport.getItemBand().addElement(textField(columns[2], false, new Rectangle(260, 0, 120, 14)));
    jfreeReport.getItemBand().addElement(numberField(columns[3], false, numberFormat, new Rectangle(390, 0, 70, 14)));
    // layout is built, so now fill in the report content to be display
    final int length = 60;
    final int breakpos = 20;
    Object[][] data = new Object[length][5];
    int type = 0;
    for (int i = 0; i < length; i++)
    {
      if (i % breakpos == 0)
      {
        type++;
      }
      data[i][0] = "ROW-" + i + "-0";
      data[i][1] = "ROW-" + i + "-1";
      data[i][2] = "ROW-" + i + "-2";
      data[i][3] = IntegerCache.getInteger(i);
      data[i][4] = IntegerCache.getInteger(type);
    }
    jfreeReport.setDataFactory(new TableDataFactory("default", new DefaultTableModel(data, columns)));
    return jfreeReport;
  }

  protected Element messageField(String label, boolean bold, int fontSize, ElementAlignment alignment,
                                 Rectangle rectangle)
  {
    MessageFieldElementFactory elementFactory = new MessageFieldElementFactory();
    elementFactory.setName(label);
    elementFactory.setNullString("");
    elementFactory.setFormatString(label);
    configureFactory(elementFactory, bold, fontSize, alignment, rectangle);
    return elementFactory.createElement();
  }

  protected Element textField(String text, boolean bold, Rectangle rectangle)
  {
    TextFieldElementFactory elementFactory = new TextFieldElementFactory();
    elementFactory.setName(text);
    elementFactory.setNullString("");
    elementFactory.setFieldname(text);
    configureFactory(elementFactory, bold, FONTSIZE, ElementAlignment.LEFT, rectangle);
    return elementFactory.createElement();
  }

  protected Element numberField(String filedName, boolean bold, String numberFormat, Rectangle rectangle)
  {
    NumberFieldElementFactory elementFactory = new NumberFieldElementFactory();
    elementFactory.setNullString("");
    elementFactory.setName(filedName);
    elementFactory.setFieldname(filedName);
    elementFactory.setFormatString(numberFormat);
    configureFactory(elementFactory, bold, FONTSIZE, ElementAlignment.RIGHT, rectangle);
    return elementFactory.createElement();
  }

  protected Element label(String text, boolean bold, ElementAlignment alignment, Rectangle rectangle)
  {
    LabelElementFactory elementFactory = new LabelElementFactory();
    elementFactory.setText(text);
    configureFactory(elementFactory, bold, FONTSIZE, alignment, rectangle);
    return elementFactory.createElement();
  }

  private void configureFactory(TextElementFactory elementFactory, boolean bold, int fontSize,
                                ElementAlignment alignment, Rectangle rectangle)
  {
    elementFactory.setBold(new Boolean(bold));
    elementFactory.setFontSize(IntegerCache.getInteger(fontSize));
    elementFactory.setFontName(FONTNAME);
    elementFactory.setWrapText(Boolean.TRUE);
    elementFactory.setDynamicHeight(Boolean.TRUE);
    elementFactory.setHorizontalAlignment(alignment);
    elementFactory.setVerticalAlignment(ElementAlignment.MIDDLE);
    elementFactory.setAbsolutePosition(new Point2D.Float(rectangle.x, rectangle.y));
    elementFactory.setMinimumSize(new Dimension(rectangle.width, rectangle.height));
  }


  public static void main(String[] args) throws Exception
  {
    ClassicEngineBoot.getInstance().start();
    Pre205Test sampleReport = new Pre205Test();
    final MasterReport report = sampleReport.buildSampleReport();
//    PreviewDialog previewDialog = new PreviewDialog(report);
//    previewDialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//    previewDialog.pack();
//    previewDialog.setSize(800, 1024);
//    previewDialog.setVisible(true);
    HtmlReportUtil.createDirectoryHTML(report, "/tmp/report.html");
  }
}
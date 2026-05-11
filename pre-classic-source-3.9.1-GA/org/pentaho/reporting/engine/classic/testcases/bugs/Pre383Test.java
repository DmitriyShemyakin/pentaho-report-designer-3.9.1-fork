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

import java.util.Vector;
import java.util.List;
import java.util.ArrayList;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.print.Paper;
import java.awt.print.PageFormat;
import javax.swing.table.TableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.JLabel;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jfree.ui.RefineryUtilities;
import org.jfree.ui.FloatDimension;
import org.pentaho.reporting.engine.classic.core.style.FontDefinition;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.engine.classic.core.modules.gui.base.DefaultReportController;
import org.pentaho.reporting.engine.classic.core.modules.gui.base.PreviewDialog;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.SimplePageDefinition;
import org.pentaho.reporting.engine.classic.core.ReportHeader;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.ReportFooter;
import org.pentaho.reporting.engine.classic.core.PageHeader;
import org.pentaho.reporting.engine.classic.core.PageFooter;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.ItemBand;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.elementfactory.ContentFieldElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.LabelElementFactory;
import org.pentaho.reporting.engine.classic.core.util.PageFormatFactory;
import org.pentaho.reporting.engine.classic.core.util.PageSize;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class Pre383Test
{
  private static final int PRINT_DIALOG_HEIGHT = 750;
  private static final int PRINT_DIALOG_WIDTH = 950;

  private static final FontDefinition HEADER_FONT =
      new FontDefinition("Arial", 12, true, false, false, false);

  /**
   * Test data generator
   */
  private static TableModel generateReportTable(int tableNum, int rows,
                                            int columns, boolean useLabels)
  {
    DefaultTableModel t = new DefaultTableModel();
    for (int i = 0; i < columns; i++)
    {
      t.addColumn(getReportTableColumnName(i));
    }

    for (int row = 0; row < rows; row++)
    {
      if (useLabels)
      {
        Vector v = new Vector();
        for (int col = 0; col < columns; col++)
        {
          v.add(new JLabel(getReportTableData(tableNum, row, col)));
        }
        t.addRow(v);
      }
      else
      {
        Vector v = new Vector();
        for (int col = 0; col < columns; col++)
        {
          v.add(getReportTableData(tableNum, row, col));
        }
        t.addRow(v);
      }
    }

    return t;
  }

  private static String getReportTableData(int table, int row, int column)
  {
    return "T" + table + ":R" + row + "-C" + column;
  }

  private static String getReportTableColumnName(int column)
  {
    return "reportheadercolumn" + column;
  }

  private static void showReport(MasterReport report, JFrame mainAppWindow)
      throws Exception
  {
    // 0.8.9.4
    PreviewDialog preview = new PreviewDialog(report, mainAppWindow, true);
    DefaultReportController demo = new DefaultReportController();
    preview.setReportController(demo);
    preview.setReportJob(report);

    // // 0.8.8-01
    // PreviewDialog preview = new PreviewDialog(report, mainAppWindow,
    // true);
    // DefaultReportControler demo = new DefaultReportControler();
    // preview.getBase().setReportControler(demo);

    // Both
    preview.pack();
    preview.setSize(new Dimension(PRINT_DIALOG_WIDTH, PRINT_DIALOG_HEIGHT));
    RefineryUtilities.centerFrameOnScreen(preview);
    preview.setVisible(true);
  }

  private static JFrame initMainAppWindow()
  {
    JFrame mainAppWindow = new JFrame();
    JPanel panel = new JPanel();
    panel.add(new JLabel("foobar"));
    mainAppWindow.add(panel);
    mainAppWindow.pack();
    mainAppWindow.setVisible(true);
    try
    {
      Thread.sleep(1000);
    }
    catch (InterruptedException e)
    {
    }
    return mainAppWindow;
  }

  private static MasterReport createDemoReport(List tables) throws ReportDataFactoryException
  {
    MasterReport report = new MasterReport();

    // Creates new TableDataFactory where all report data is to be stored.
    TableDataFactory tableDataFactory = new TableDataFactory();

    // Adds all JTables to TableDataFactory with name "table" + index. This
    // name is referred later.
    for (int a = 0; a < tables.size(); a++)
    {
      final TableModel jtable = (TableModel) tables.get(a);
      tableDataFactory.addTable("table" + a, jtable);
      tableDataFactory.queryData("table" + a, null);
    }
    report.setDataFactory(tableDataFactory);
    report.setName("Raportti");

    PageFormatFactory pfFact = PageFormatFactory.getInstance();
    // 0.8.9.4
    Paper paper = pfFact.createPaper(PageSize.A4);
    // // 0.8.8-01 (works also with 0.8.9.4 although deprecated)
    // Paper paper = pfFact.createPaper(PageFormatFactory.A4);
    pfFact.setBordersMm(paper, 25.5, 25.5, 25.5, 25.5);
    PageFormat pf = pfFact.createPageFormat(paper, PageFormat.LANDSCAPE);
    SimplePageDefinition pageDef = new SimplePageDefinition(pf);
    report.setPageDefinition(pageDef);

    ReportHeader header = new ReportHeader();
    header.setName("Report-Header");
    /**
     * Creates and adds one SubReport for every JTable. Then header
     * (including all SubReports) is added to master report.
     */
    int columnsPerPage = 8;
    for (int i = 0; i < tables.size(); i++)
    {
      final TableModel jtables = (TableModel) tables.get(i);
      header.addSubReport(createSubReport(jtables, i));
      header.getSubReport(i).setQuery("table" + i);
      header.getSubReport(i).getReportHeader().setPagebreakAfterPrint(true);
    }
    report.setReportHeader(header);

    report.setReportFooter(new ReportFooter());
    report.setPageHeader(initPageHeader("Master"));
    report.setPageFooter(new PageFooter());
    report.setDataFactory(tableDataFactory);

    return report;
  }

  protected static PageHeader initPageHeader(String label)
  {
    PageHeader pHeader = new PageHeader();
    pHeader.getStyle().setStyleProperty(ElementStyleKeys.BACKGROUND_COLOR, Color.yellow);
    pHeader.setName("Page-Header");
    pHeader.getStyle().setStyleProperty(ElementStyleSheet.MINIMUMSIZE, new FloatDimension(0, 91));
    pHeader.getStyle().setFontDefinitionProperty(HEADER_FONT);
    pHeader.setDisplayOnFirstPage(true);

    pHeader.addElement(LabelElementFactory.createLabelElement(null, new Rectangle2D.Double(0,0,400, 20),null, null, null, label));
    return pHeader;
  }

  /**
   * Creates a new SubReport.
   */
  public static SubReport createSubReport(TableModel tm, int columnsPerPage)
  {
    SubReport report = new SubReport();

    report.setReportHeader(new ReportHeader());
    report.setReportFooter(new ReportFooter());
    report.setPageHeader(initPageHeader("SubReport " + columnsPerPage));
    report.setPageFooter(new PageFooter());

    ItemBand items = report.getItemBand();
    items.getStyle().setStyleProperty(ElementStyleKeys.BACKGROUND_COLOR, Color.lightGray);
    items.setName("Items");

    ContentFieldElementFactory tfFactory = new ContentFieldElementFactory();
    items.getStyle().setFontDefinitionProperty(HEADER_FONT);
    float[] columnPos =
        new float[]{0.0f, 60.0f, 120.0f, 184, 248, 313, 377, 442};
    float[] columnWidth = new float[]{60, 60, 64, 64, 64, 64, 64, 64};

    int cellHeight = 12;
    for (int i = 0; i < tm.getColumnCount(); i++)
    {
      tfFactory.setMinimumSize(new FloatDimension(columnWidth[i] - 4,
          cellHeight));
      tfFactory.setAbsolutePosition(new Point2D.Float(columnPos[i] + 2, 0));
      tfFactory.setFieldname(tm.getColumnName(i));
      items.addElement(tfFactory.createElement());
    }
    return report;
  }

  public void testAndShowCustomReport() throws ReportDataFactoryException
  {
    // Init JFreeReport
    ClassicEngineBoot.getInstance().start();
    // Init main window
    JFrame mainAppWindow = initMainAppWindow();

    // Init test data
    int rows = 7; // With 5 the things work fine...
    List tables = new ArrayList();
    tables.add(generateReportTable(0, rows, 8, true));
    tables.add(generateReportTable(1, rows, 8, true));
    tables.add(generateReportTable(2, rows, 8, true));
    tables.add(generateReportTable(3, rows, 8, true));

    // Create JFreeReport based on test data
    MasterReport report = createDemoReport(tables);
    // Show the report
    try
    {
      showReport(report, mainAppWindow);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) throws ReportDataFactoryException
  {
    new Pre383Test().testAndShowCustomReport();
  }


}

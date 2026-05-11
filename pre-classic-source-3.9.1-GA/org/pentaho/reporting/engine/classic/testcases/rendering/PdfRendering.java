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

package org.pentaho.reporting.engine.classic.testcases.rendering;

import java.net.URL;
import java.io.IOException;
import java.util.Date;

import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.ReportGenerator;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.PdfReportUtil;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;

/**
 * Creation-Date: 13.06.2007, 18:20:38
 *
 * @author Thomas Morgner
 * @deprecated Gone with no replacement
 */
public class PdfRendering
{
  private PdfRendering()
  {
  }

  public static void main(final String[] args) throws IOException, ResourceException
  {
    ClassicEngineBoot.getInstance().start();

    final long date = System.currentTimeMillis();
    final int date2 = (int) System.currentTimeMillis();
    System.out.println(date);
    System.out.println(date2);
    System.out.println(new Date(date2));

    final URL rul = PdfRendering.class.getResource("pdf-rendering.xml");
    final MasterReport report = ReportGenerator.getInstance().parseReport(rul);
    report.getParameterValues().put("reportDate", new Long(System.currentTimeMillis()));
    PdfReportUtil.createPDF(report, "/tmp/export.pdf");
//    final PreviewDialog dialog = new PreviewDialog(report);
//    dialog.pack();
//    dialog.setModal(true);
//    dialog.setVisible(true);
//    System.exit(0);

//    PdfGraphics2D g2; // created elsewhere
//
//    final Graphics2D derivedGraphics = (Graphics2D) g2.create();
//    derivedGraphics.setPaint(Color.blue);
//    derivedGraphics.fill(new Rectangle2D.Float(0, 0, 100, 100));
//    derivedGraphics.dispose();
//    // This dispose does nothing yet. The code for creating a blue-rectangle will not
//    // be inserted into the stream until the parent gets closed.
//
//    g2.setFont(new Font ("Arial", Font.PLAIN, 15));
//    g2.drawString("My Text", 20, 20);
//
//    // this now triggers the dispose of the derived graphics. The blue rectangle will
//    // overlay the printed text and thus it becomes unreadable.
//    g2.dispose();
//

  }
}

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

package org.pentaho.reporting.engine.classic.testcases;

import java.net.URL;
import javax.swing.table.TableModel;

import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.demo.ancient.demo.groups.ColorAndLetterTableModel;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.ReportGenerator;
import org.pentaho.reporting.engine.classic.core.modules.gui.base.PreviewDialog;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.SwingUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

public final class TestSystem
{
  private static final Log logger = LogFactory.getLog(TestSystem.class);

  private TestSystem()
  {
  }

  public static MasterReport loadReport(final String urlname, final TableModel data)
  {
    final URL in = ObjectUtilities.getResource(urlname, TestSystem.class);
    if (in == null)
    {
      logger.error("xml file not found.");
      return null;
    }
    final ReportGenerator gen = ReportGenerator.getInstance();
    final MasterReport report1;
    try
    {
      report1 = gen.parseReport(in, in);
    }
    catch (Exception ioe)
    {
      logger.error("1: report definition failure.", ioe);
      return null;
    }

    if (report1 == null)
    {
      logger.error("2: the report is null.");
      return null;
    }
    report1.setDataFactory(new TableDataFactory
        ("default", data));
    return report1;
  }

  public static void showPreview(final MasterReport report1)
      throws ReportProcessingException
  {
    final PreviewDialog frame1 = new PreviewDialog(report1);
    frame1.setModal(true);
    frame1.pack();
    SwingUtil.positionFrameRandomly(frame1);
    frame1.setVisible(true);
  }

  public static void main(final String[] args)
      throws Exception
  {
    final ColorAndLetterTableModel m_dataModel = new ColorAndLetterTableModel();
    final MasterReport report = TestSystem.loadReport
      ("org/pentaho/reporting/engine/classic/extensions/junit/pagebreak.xml", m_dataModel);
    if (report == null)
    {
      System.exit(1);
    }

    TestSystem.showPreview(report);
  }

}

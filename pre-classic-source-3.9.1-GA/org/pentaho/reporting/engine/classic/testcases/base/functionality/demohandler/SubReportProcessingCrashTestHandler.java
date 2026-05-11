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

import java.awt.Color;
import java.awt.Rectangle;
import java.net.URL;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.pentaho.reporting.engine.classic.core.ElementAlignment;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ParameterMapping;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.demo.util.AbstractDemoHandler;
import org.pentaho.reporting.engine.classic.demo.util.ReportDefinitionException;
import org.pentaho.reporting.engine.classic.core.elementfactory.TextFieldElementFactory;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.StaticDataFactory;
import org.pentaho.reporting.engine.classic.core.style.FontDefinition;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @deprecated
 */
public class SubReportProcessingCrashTestHandler extends AbstractDemoHandler
{
  private static final Log logger = LogFactory.getLog(SubReportProcessingCrashTestHandler.class);

  public SubReportProcessingCrashTestHandler()
  {
  }

  public static TableModel createMainTableModel()
  {
    logger.debug("TestDataFactory.createTableModel");
    return new DefaultTableModel(new String[][]{{"1.1", "1.2"}, {"2.1", "2.2"}}, new String[]{"c1", "c2"});
  }

  public static TableModel createSubReportTableModel(final String param1)
  {
    logger.debug("TestDataFactory.createTableModel(" + param1 + ")");
    return new DefaultTableModel(new String[][]{{"1.1:" + param1, "1.2:" + param1}, {"2.1:" + param1, "2.2:" + param1}}, new String[]{"t1", "t2"});
  }

  public String getDemoName()
  {
    return "Test-Case: Reports with subreports crash under certain conditions.";
  }

  public MasterReport createReport() throws ReportDefinitionException
  {
    final MasterReport report = new MasterReport();
    final StaticDataFactory staticDataFactory = new StaticDataFactory();
    report.setDataFactory(staticDataFactory);
    report.setQuery("org.pentaho.reporting.engine.classic.testcases.base.functionality.demohandler.SubReportProcessingCrashTestHandler#createMainTableModel()");

    final Element textElement = TextFieldElementFactory.createStringElement("reportField1", new Rectangle(0, 0, 100, 20), Color.BLACK, ElementAlignment.LEFT, ElementAlignment.TOP, new FontDefinition("Arial", 12), "-", "c1");
    report.getItemBand().addElement(textElement);

    final SubReport subReport = new SubReport();
    subReport.addInputParameter("c1", "c1");

    subReport.setQuery("org.pentaho.reporting.engine.classic.testcases.base.functionality.demohandler.SubReportProcessingCrashTestHandler#createSubReportTableModel(c1)");
    final Element subReportTextElement = TextFieldElementFactory.createStringElement("subreportField1", new Rectangle(20, 0, 100, 20), Color.RED, ElementAlignment.LEFT, ElementAlignment.TOP, new FontDefinition("Arial", 12), "-", "t1");
    subReport.getItemBand().addElement(subReportTextElement);
    report.getItemBand().addSubReport(subReport);

    final Element textElementT1 = TextFieldElementFactory.createStringElement("reportFieldT1", new Rectangle(0, 20, 100, 20), Color.BLACK, ElementAlignment.LEFT, ElementAlignment.TOP, new FontDefinition("Arial", 12), "-", "t2");
    report.getItemBand().addElement(textElementT1);

    final ParameterMapping[] parameterMappings = subReport.getExportMappings();

    for (int i = 0; i < parameterMappings.length; i++)
    {
      final ParameterMapping parameterMapping = parameterMappings[i];
      logger.debug("parameterMapping.getAlias() = " + parameterMapping.getAlias());
      logger.debug("parameterMapping.getName() = " + parameterMapping.getName());
    }

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
}

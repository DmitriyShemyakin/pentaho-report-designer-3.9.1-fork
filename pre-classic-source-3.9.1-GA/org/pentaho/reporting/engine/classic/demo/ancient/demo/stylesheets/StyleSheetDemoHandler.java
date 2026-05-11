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

package org.pentaho.reporting.engine.classic.demo.ancient.demo.stylesheets;

import java.awt.Color;
import java.io.IOException;
import java.net.URL;
import javax.swing.JComponent;
import javax.swing.table.TableModel;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.SwingUtil;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.ReportGenerator;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.StyleSheetCollection;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;
import org.pentaho.reporting.engine.classic.demo.ancient.demo.world.CountryDataTableModel;
import org.pentaho.reporting.engine.classic.demo.util.AbstractXmlDemoHandler;
import org.pentaho.reporting.engine.classic.demo.util.ReportDefinitionException;
import org.pentaho.reporting.engine.classic.demo.util.SimpleDemoFrame;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;

/**
 * Creation-Date: 11.10.2005, 12:54:29
 *
 * @author Thomas Morgner
 */
public class StyleSheetDemoHandler extends AbstractXmlDemoHandler
{
  private TableModel data;

  public StyleSheetDemoHandler()
  {
    data = new CountryDataTableModel();
  }

  public String getDemoName()
  {
    return "WorldDemo using StyleSheets and Macros";
  }

  public MasterReport createReport() throws ReportDefinitionException
  {
    // create a private instance of the parser so that we can safely modify
    // the configuration..
    final ReportGenerator generator = ReportGenerator.createInstance();
    generator.setObject("outer-header-color", "red");

    final URL in = getReportDefinitionSource();
    if (in == null)
    {
      throw new ReportDefinitionException("URL is invalid");
    }
    try
    {
      MasterReport report = generator.parseReport(in);
      final StyleSheetCollection styleCollection =
          report.getStyleSheetCollection();
      final ElementStyleSheet styleSheet =
          styleCollection.createStyleSheet("my-style");
      styleSheet.setStyleProperty(TextStyleKeys.FONT, "SansSerif");
      styleSheet.setStyleProperty(ElementStyleKeys.PAINT, Color.blue);

      report.setDataFactory(new TableDataFactory("default", data));
      return report;
    }
    catch (IOException e)
    {
      throw new ReportDefinitionException("IOError", e);
    }
    catch (ResourceException e)
    {
      throw new ReportDefinitionException("ResourceError", e);
    }
  }

  public URL getDemoDescriptionSource()
  {
    return ObjectUtilities.getResourceRelative("stylesheets.html", StyleSheetDemoHandler.class);
  }

  public JComponent getPresentationComponent()
  {
    return createDefaultTable(data);
  }

  public URL getReportDefinitionSource()
  {
    return ObjectUtilities.getResourceRelative("stylesheets.xml", StyleSheetDemoHandler.class);
  }

  public static void main(String[] args)
  {
    // initialize JFreeReport
    ClassicEngineBoot.getInstance().start();

    final StyleSheetDemoHandler handler = new StyleSheetDemoHandler();
    final SimpleDemoFrame frame = new SimpleDemoFrame(handler);
    frame.init();
    frame.pack();
    SwingUtil.centerFrameOnScreen(frame);
    frame.setVisible(true);
  }
}

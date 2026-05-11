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

package org.pentaho.reporting.engine.classic.testcases.base.functionality;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;
import javax.swing.table.DefaultTableModel;

import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.demo.util.InternalDemoHandler;
import org.pentaho.reporting.engine.classic.demo.ancient.demo.opensource.OpenSourceXMLDemoHandler;
import org.pentaho.reporting.engine.classic.core.util.ReportProperties;

/**
 * Creation-Date: 27.10.2005, 15:24:14
 *
 * @author Thomas Morgner
 */
public class SerializationTest extends TestCase
{
  private static final Log logger = LogFactory.getLog(SerializationTest.class);

  public SerializationTest()
  {
  }

  public SerializationTest(String string)
  {
    super(string);
  }

  protected void setUp() throws Exception
  {
    ClassicEngineBoot.getInstance().start();
  }

  public void testSerializeReport()
  {
    InternalDemoHandler[] handlers = FunctionalityTestLib.getAllDemoHandlers();
    for (int i = 0; i < handlers.length; i++)
    {
      final InternalDemoHandler handler = handlers[i];
      try
      {
        if (handler instanceof OpenSourceXMLDemoHandler)
        {
          // contains a non-serializable image
          continue;
        }
        logger.debug("Starting to read " + handler.getDemoName());

        final MasterReport report = handler.createReport();
        final DataFactory model = report.getDataFactory();

        // we don't test whether our demo models are serializable :)
        report.setDataFactory(new TableDataFactory
            ("default", new DefaultTableModel()));
        // clear all report properties, which may cause trouble ...
        final ReportProperties p = report.getProperties();
        final Iterator keys = p.keys();
        while (keys.hasNext())
        {
          String key = (String) keys.next();
          if (p.get(key) instanceof Serializable == false)
          {
            p.put(key, null);
          }
        }

        final ByteArrayOutputStream bo = new ByteArrayOutputStream();
        try
        {
          final ObjectOutputStream oout = new ObjectOutputStream(bo);
          oout.writeObject(report);
          oout.close();
        }
        catch (Exception e)
        {
          logger.debug("Failed to write " + handler.getDemoName(), e);
          fail();
        }

        try
        {
          final ByteArrayInputStream bin = new ByteArrayInputStream(bo.toByteArray());
          final ObjectInputStream oin = new ObjectInputStream(bin);
          final MasterReport report2 = (MasterReport) oin.readObject();
          report2.setDataFactory(model);

          assertTrue(FunctionalityTestLib.execGraphics2D(report2));
        }
        catch (Exception e)
        {
          logger.debug("Failed to read " + handler.getDemoName(), e);
          fail();
        }
      }
      catch (Exception e)
      {
        logger.debug("Failed to execute " + handler.getDemoName(), e);
        fail();
      }
    }
  }

}

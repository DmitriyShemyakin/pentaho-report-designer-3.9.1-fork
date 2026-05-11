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
 * Copyright (c) 2001 - 2009 Object Refinery Ltd, Pentaho Corporation and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.testcases.layout;

import java.net.URL;
import javax.swing.table.DefaultTableModel;

import junit.framework.TestCase;

import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.function.ProcessingContext;
import org.pentaho.reporting.engine.classic.core.layout.Renderer;
import org.pentaho.reporting.engine.classic.core.layout.output.DefaultProcessingContext;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

/**
 * @author gone, does not test anything
 */
public class LineBreakTest extends TestCase
{
  public static void testLineBreak() throws Exception
  {
    ClassicEngineBoot.getInstance().start();

    final ProcessingContext processingContext = new DefaultProcessingContext();
    final DebugExpressionRuntime runtime = new DebugExpressionRuntime(new DefaultTableModel(), 0, processingContext);

    final URL target = LineBreakTest.class.getResource("linebreak-test.xml"); //$NON-NLS-1$
    final ResourceManager rm = new ResourceManager();
    rm.registerDefaults();
    final Resource directly = rm.createDirectly(target, MasterReport.class);
    final MasterReport report = (MasterReport) directly.getResource();

    final DebugRenderer debugLayoutSystem = new DebugRenderer();
    debugLayoutSystem.startReport(report);
    debugLayoutSystem.startSection(Renderer.TYPE_NORMALFLOW);

    debugLayoutSystem.add(report.getItemBand(), runtime, null);
    debugLayoutSystem.endSection();

    final boolean b = Renderer.LayoutResult.LAYOUT_PAGEBREAK == debugLayoutSystem.validatePages();
    final boolean b2 = Renderer.LayoutResult.LAYOUT_PAGEBREAK == debugLayoutSystem.validatePages();
    System.out.println("Layouting 1 result: " + b); //$NON-NLS-1$
    System.out.println("Layouting 2 result: " + b2); //$NON-NLS-1$
    
    assertTrue(true);
  }
}

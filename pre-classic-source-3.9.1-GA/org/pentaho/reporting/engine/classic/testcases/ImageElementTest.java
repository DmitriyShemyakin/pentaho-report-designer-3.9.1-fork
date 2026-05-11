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

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.net.URL;
import javax.swing.table.DefaultTableModel;

import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.libraries.base.util.WaitingImageObserver;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

public class ImageElementTest
{
  public ImageElementTest ()
  {
  }

  private static Image createImage(final Image source)
  {
    final double scale = 0.2;
    final double width = 300;
    final double height = 400;

    final WaitingImageObserver obs = new WaitingImageObserver(source);
    obs.waitImageLoaded();

    final BufferedImage bImage = new BufferedImage((int) (width * scale), (int) (height * scale), BufferedImage.TYPE_INT_ARGB);

    final Graphics2D graph = bImage.createGraphics();
    graph.setTransform(AffineTransform.getScaleInstance(scale, scale));
    graph.drawImage(source, AffineTransform.getScaleInstance(scale, scale), null);
    graph.dispose();
    return bImage;
  }

  public static void main(final String[] args)
      throws Exception
  {
    ClassicEngineBoot.getInstance().start();
    // add an image as a report property...
    final URL imageURL = ObjectUtilities.getResource
            ("org/pentaho/reporting/engine/classic/demo/opensource/gorilla.jpg", ImageElementTest.class);
    final Image image = Toolkit.getDefaultToolkit().createImage(imageURL);

    final Object[][] data = {{createImage(image), createImage(image), createImage(image)}};
    final Object[] names = {"Foto1", "Foto2", "Foto3"};
    final DefaultTableModel mod = new DefaultTableModel(data, names);

    final MasterReport report = TestSystem.loadReport("org/pentaho/reporting/engine/classic/extensions/junit/image-element.xml", mod);
    if (report == null)
    {
      System.exit(1);
    }

    report.getParameterValues().put("GraphImage", image);
    TestSystem.showPreview(report);
  }

}

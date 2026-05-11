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

package org.pentaho.reporting.engine.classic.testcases.bugs;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Rectangle2D;

import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.ElementAlignment;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.modules.gui.base.PreviewFrame;
import org.pentaho.reporting.engine.classic.core.elementfactory.LabelElementFactory;
import org.pentaho.reporting.engine.classic.core.style.FontDefinition;
import org.pentaho.reporting.libraries.xmlns.writer.CharacterEntityParser;

public final class ThaiCharacterBug
{
  private ThaiCharacterBug()
  {
  }

  public static MasterReport getReport() throws Exception
  {
    String test =
        CharacterEntityParser.createXMLEntityParser().decodeEntities
        ("Sample Thai chars: &#3648;&#3614;&#3636;&#3656;&#3617;, " +
          "&#3621;&#3641;&#3585;&#3588;&#3657;&#3634;");
    //String test = "\u3648\u3614\u3636\u3656\u3617\u3621\u3641\u3585\u3588\u3657\u3634";
    test = new String(test.getBytes("iso-8859-1"), "TIS620");

    final Element e = LabelElementFactory.createLabelElement(null,
        new Rectangle2D.Float(10, 10, 250, 50),
        null,
        ElementAlignment.CENTER,
        new FontDefinition("Serif", 14),
        test);
    final MasterReport report = new MasterReport();
    report.getReportHeader().addElement(e);
    return report;
  }

  public static void main(final String[] args) throws Exception
  {
    final PreviewFrame d = new PreviewFrame(getReport());
    d.pack();
    d.addWindowListener(new WindowAdapter()
    {
      /**
       * Invoked when a window is in the process of being closed.
       * The close operation can be overridden at this point.
       */
      public void windowClosing(final WindowEvent e)
      {
        System.exit(0);
      }
    });
    d.setVisible(true);
  }
}

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

package org.pentaho.reporting.engine.classic.testcases.bugs;

import java.awt.print.PageFormat;
import java.awt.print.Paper;

import junit.framework.TestCase;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.util.PageFormatFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Creation-Date: 02.10.2007, 18:06:33
 *
 * @author Thomas Morgner
 */
public class PageFormatTest extends TestCase
{
  private static final Log logger = LogFactory.getLog(PageFormatTest.class);

  public static void testPageFormat()
  {
    ClassicEngineBoot.getInstance().start();
    final Paper p = PageFormatFactory.getInstance().createPaper("A4"); //$NON-NLS-1$
    final PageFormat pageFormat = PageFormatFactory.getInstance().createPageFormat(p, PageFormat.LANDSCAPE);
    final double defRightMargin = 70;
    final double defTopMargin = 50;
    final double defLeftMargin = 30;
    final double defBottomMargin= 10;
    PageFormatFactory.getInstance().setBorders(p, defRightMargin, defTopMargin,
            defLeftMargin, defBottomMargin);
    pageFormat.setPaper(p);

    final float marginLeft = (float) pageFormat.getImageableX();
    final float marginRight = (float)
                (pageFormat.getWidth() - pageFormat.getImageableWidth() - pageFormat.getImageableX());
    final float marginTop = (float) (pageFormat.getImageableY());
    final float marginBottom = (float)
                (pageFormat.getHeight() - pageFormat.getImageableHeight() - pageFormat.getImageableY());

    logger.debug ("Margins: Top:    " + marginTop); //$NON-NLS-1$
    logger.debug ("Margins: Left:   " + marginLeft); //$NON-NLS-1$
    logger.debug ("Margins: Bottom: " + marginBottom); //$NON-NLS-1$
    logger.debug ("Margins: Right:  " + marginRight);  //$NON-NLS-1$
  }
}

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

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JDialog;

import org.pentaho.reporting.engine.classic.core.layout.output.LogicalPageKey;
import org.pentaho.reporting.engine.classic.core.layout.output.PhysicalPageKey;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.graphics.internal.GraphicsContentInterceptor;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.graphics.PageDrawable;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.DrawablePanel;


/**
 * Creation-Date: 10.11.2006, 20:41:29
 *
 * @author Thomas Morgner
 */
public class DisplayInterceptor implements GraphicsContentInterceptor
{
  private LogicalPageKey logicalPageKey;
  private boolean matched;

  public DisplayInterceptor(final LogicalPageKey logicalPageKey)
  {
    this.logicalPageKey = logicalPageKey;
  }

  public boolean isLogicalPageAccepted(LogicalPageKey key)
  {
    if (logicalPageKey.equals(key))
    {
      matched = true;
      return true;
    }
    return false;
  }

  public void processLogicalPage(LogicalPageKey key, PageDrawable page)
  {
    final DrawablePanel comp = new DrawablePanel();
    comp.setDrawableAsRawObject(page);

    JPanel contentPane = new JPanel();
    contentPane.setLayout(new BorderLayout());
    contentPane.add(comp, BorderLayout.CENTER);

    JDialog dialog = new JDialog();
    dialog.setModal(true);
    dialog.setContentPane(contentPane);
    dialog.setSize(800, 600);
    dialog.setVisible(true);
  }

  public boolean isPhysicalPageAccepted(PhysicalPageKey key)
  {
    return false;
  }

  public void processPhysicalPage(PhysicalPageKey key, PageDrawable page)
  {

  }

  public boolean isMoreContentNeeded()
  {
    return matched == false;
  }

  public boolean isMatched()
  {
    return matched;
  }

  public void setMatched(final boolean matched)
  {
    this.matched = matched;
  }
}

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

package org.pentaho.reporting.designer.core.xul;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.pentaho.ui.xul.XulComponent;
import org.pentaho.ui.xul.XulDomContainer;
import org.pentaho.ui.xul.components.XulMenuseparator;
import org.pentaho.ui.xul.dom.Element;
import org.pentaho.ui.xul.swing.SwingElement;

/**
 * this is a very minimal and definitely not standard-conforming popup menu.
 */
public class SwingXulPopupMenu extends SwingElement implements XulPopup
{
  private JPopupMenu menu;

  public SwingXulPopupMenu(Element self, XulComponent parent, XulDomContainer domContainer, String tagName)
  {
    super(tagName);

    menu = new JPopupMenu();
    setManagedObject(menu);

  }

  /**
   * Defeats layout calls. Useful for bulk updates.
   *
   * @param suppress
   */
  public void suppressLayout(final boolean suppress)
  {

  }

  public void layout()
  {
    for (Element comp : getChildNodes())
    {
      if (comp instanceof XulMenuseparator)
      {
        this.menu.addSeparator();
      }
      else if (comp instanceof SwingXulPopupMenu)
      {
        this.menu.add((JMenu) ((XulComponent) comp).getManagedObject());
      }
      else
      {
        this.menu.add((JMenuItem) ((XulComponent) comp).getManagedObject());
      }
    }
    initialized = true;
  }

  @Deprecated
  public void addComponent(XulComponent c)
  {
    addChild(c);
  }

  public void addComponentAt(XulComponent component, int idx)
  {
    addChildAt(component, idx);
  }

  public void removeComponent(XulComponent component)
  {
    removeChild(component);
  }

  public boolean isDisabled()
  {
    return !menu.isEnabled();
  }

  public void setDisabled(boolean disabled)
  {
    menu.setEnabled(!disabled);
  }

}

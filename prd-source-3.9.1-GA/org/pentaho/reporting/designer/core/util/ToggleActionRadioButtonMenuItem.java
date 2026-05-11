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

package org.pentaho.reporting.designer.core.util;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.Action;
import javax.swing.JRadioButtonMenuItem;

import org.pentaho.reporting.designer.core.actions.ToggleStateAction;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 * @deprecated No longer used.
 */
public class ToggleActionRadioButtonMenuItem extends JRadioButtonMenuItem
{
  private class SelectedUpdateHandler implements PropertyChangeListener
  {
    /**
     * This method gets called when a bound property is changed.
     *
     * @param evt A PropertyChangeEvent object describing the event source and the property that has changed.
     */

    public void propertyChange(final PropertyChangeEvent evt)
    {
      if (ToggleStateAction.SELECTED.equals(evt.getPropertyName()) == false)
      {
        return;
      }

      final Action action = getAction();
      if (action instanceof ToggleStateAction)
      {
        final ToggleStateAction taction = (ToggleStateAction) action;
        setSelected(taction.isSelected());
      }
    }
  }

  private SelectedUpdateHandler updateHandler;

  /**
   * Creates a menu item whose properties are taken from the Action supplied.
   *
   * @since 1.3
   */
  public ToggleActionRadioButtonMenuItem(final Action a)
  {
    updateHandler = new SelectedUpdateHandler();
    setAction(a);
  }


  /**
   * Sets the <code>Action</code> for the <code>ActionEvent</code> source. The new <code>Action</code> replaces any
   * previously set <code>Action</code> but does not affect <code>ActionListeners</code> independently added with
   * <code>addActionListener</code>. If the <code>Action</code> is already a registered <code>ActionListener</code> for
   * the button, it is not re-registered.
   * <p/>
   * A side-effect of setting the <code>Action</code> is that the <code>ActionEvent</code> source's properties  are
   * immediately set from the values in the <code>Action</code> (performed by the method
   * <code>configurePropertiesFromAction</code>) and subsequently updated as the <code>Action</code>'s properties change
   * (via a <code>PropertyChangeListener</code> created by the method <code>createActionPropertyChangeListener</code>.
   *
   * @param a the <code>Action</code> for the <code>AbstractButton</code>, or <code>null</code>
   * @beaninfo bound: true attribute: visualUpdate true description: the Action instance connected with this ActionEvent
   * source
   * @see Action
   * @see #getAction
   * @see #configurePropertiesFromAction
   * @see #createActionPropertyChangeListener
   * @since 1.3
   */
  public void setAction(final Action a)
  {
    final Action oldAction = getAction();
    if (oldAction instanceof ToggleStateAction)
    {
      oldAction.removePropertyChangeListener(updateHandler);
    }
    super.setAction(a);
    if (a instanceof ToggleStateAction)
    {
      final ToggleStateAction a2 = (ToggleStateAction) a;
      a.addPropertyChangeListener(updateHandler);
      setSelected(a2.isSelected());
    }
  }
}
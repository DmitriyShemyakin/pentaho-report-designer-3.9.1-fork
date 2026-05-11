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

package org.pentaho.reporting.designer.core.actions.global.units;

import java.awt.event.ActionEvent;
import javax.swing.Action;

import org.pentaho.reporting.designer.core.actions.AbstractDesignerContextAction;
import org.pentaho.reporting.designer.core.actions.ActionMessages;
import org.pentaho.reporting.designer.core.actions.ToggleStateAction;
import org.pentaho.reporting.designer.core.settings.SettingsListener;
import org.pentaho.reporting.designer.core.settings.WorkspaceSettings;
import org.pentaho.reporting.designer.core.util.Unit;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public abstract class SetUnitAction extends AbstractDesignerContextAction implements ToggleStateAction, SettingsListener
{
  private Unit unit;
  private Unit state;

  protected SetUnitAction(final Unit unit)
  {
    if (unit == null)
    {
      throw new NullPointerException();
    }

    this.unit = unit;
    putValue(Action.NAME, ActionMessages.getString("SetUnitAction.Text", Integer.valueOf(unit.ordinal())));
    putValue(Action.SHORT_DESCRIPTION, ActionMessages.getString("SetUnitAction.Description", Integer.valueOf(unit.ordinal())));
    putValue(Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic("SetUnitAction.Text"));
    putValue(Action.ACCELERATOR_KEY, ActionMessages.getOptionalKeyStroke("SetUnitAction.Accelerator"));

    state = WorkspaceSettings.getInstance().getUnit();
    WorkspaceSettings.getInstance().addSettingsListener(this);
  }

  public boolean isSelected()
  {
    return ObjectUtilities.equal(WorkspaceSettings.getInstance().getUnit(), unit);
  }

  public void settingsChanged()
  {
    final Unit oldState = state;
    state = WorkspaceSettings.getInstance().getUnit();
    firePropertyChange(SELECTED, oldState.equals(unit), state.equals(unit));
  }

  /**
   * Invoked when an action occurs.
   */
  public void actionPerformed(final ActionEvent e)
  {
    WorkspaceSettings.getInstance().setUnit(unit);
  }
}

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

package org.pentaho.reporting.designer.core.actions.global;

import java.awt.event.ActionEvent;
import javax.swing.Action;

import org.pentaho.reporting.designer.core.actions.AbstractDesignerContextAction;
import org.pentaho.reporting.designer.core.actions.ActionMessages;
import org.pentaho.reporting.designer.core.actions.ToggleStateAction;
import org.pentaho.reporting.designer.core.settings.SettingsListener;
import org.pentaho.reporting.designer.core.settings.WorkspaceSettings;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public final class OfflineModeAction extends AbstractDesignerContextAction
    implements ToggleStateAction, SettingsListener
{
  private boolean state;

  public OfflineModeAction()
  {
    putValue(Action.NAME, ActionMessages.getString("OfflineModeAction.Text"));
    putValue(Action.SHORT_DESCRIPTION, ActionMessages.getString("OfflineModeAction.Description"));
    putValue(Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic("OfflineModeAction.Mnemonic"));
    putValue(Action.ACCELERATOR_KEY, ActionMessages.getOptionalKeyStroke("OfflineModeAction.Accelerator"));

    state = WorkspaceSettings.getInstance().isOfflineMode();
    WorkspaceSettings.getInstance().addSettingsListener(this);
  }

  public boolean isSelected()
  {
    return state;
  }

  public void settingsChanged()
  {
    final boolean oldState = state;
    state = WorkspaceSettings.getInstance().isOfflineMode();
    firePropertyChange(SELECTED, oldState, state);
  }

  /**
   * Invoked when an action occurs.
   */
  public void actionPerformed(final ActionEvent e)
  {
    final boolean snap = WorkspaceSettings.getInstance().isOfflineMode();
    WorkspaceSettings.getInstance().setOfflineMode(!snap);
  }
}

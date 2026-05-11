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
import org.pentaho.reporting.designer.core.util.DrawSelectionType;
import org.pentaho.reporting.designer.core.util.IconLoader;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public final class DrawSelectionTypeOutlineAction extends AbstractDesignerContextAction
    implements ToggleStateAction, SettingsListener
{

  private boolean selected = false;

  public DrawSelectionTypeOutlineAction()
  {
    putValue(Action.NAME, ActionMessages.getString("DrawSelectionTypeOutlineAction.Text"));
    putValue(Action.SHORT_DESCRIPTION, ActionMessages.getString("DrawSelectionTypeOutlineAction.Description"));
    putValue(Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic("DrawSelectionTypeOutlineAction.Mnemonic"));
    putValue(Action.SMALL_ICON, IconLoader.getInstance().getDrawSelectionTypeOutlineIcon());
    putValue(Action.ACCELERATOR_KEY, ActionMessages.getOptionalKeyStroke("DrawSelectionTypeOutlineAction.Accelerator"));

    selected = isSelected();
    WorkspaceSettings.getInstance().addSettingsListener(this);
  }

  public boolean isSelected()
  {
    final DrawSelectionType state = WorkspaceSettings.getInstance().getDrawSelectionType();
    selected = ObjectUtilities.equal(DrawSelectionType.OUTLINE, state);
    return selected;
  }

  public void settingsChanged()
  {
    final boolean oldSelected = selected;
    final boolean newSelected = isSelected();
    firePropertyChange(SELECTED, oldSelected, newSelected);
  }

  /**
   * Invoked when an action occurs.
   */
  public void actionPerformed(final ActionEvent e)
  {
    WorkspaceSettings.getInstance().setDrawSelectionType(DrawSelectionType.OUTLINE);
  }
}
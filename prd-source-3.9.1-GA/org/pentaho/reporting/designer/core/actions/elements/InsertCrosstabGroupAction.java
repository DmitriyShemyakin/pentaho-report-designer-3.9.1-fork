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

package org.pentaho.reporting.designer.core.actions.elements;

import java.awt.event.ActionEvent;
import javax.swing.Action;

import org.pentaho.reporting.designer.core.actions.AbstractElementSelectionAction;
import org.pentaho.reporting.designer.core.actions.ActionMessages;
import org.pentaho.reporting.designer.core.editor.ReportRenderContext;
import org.pentaho.reporting.designer.core.settings.SettingsListener;
import org.pentaho.reporting.designer.core.settings.WorkspaceSettings;
import org.pentaho.reporting.designer.core.util.IconLoader;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.designer.core.util.undo.UndoEntry;
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.CrosstabGroup;
import org.pentaho.reporting.engine.classic.core.Group;
import org.pentaho.reporting.engine.classic.core.SubGroupBody;

/**
 * Inserts a crosstab group, only appears when inside a report element
 *
 * @author Will Gorman (wgorman@pentaho.com)
 */
public final class InsertCrosstabGroupAction extends AbstractElementSelectionAction implements SettingsListener
{
  private static final long serialVersionUID = 6766753579037904765L;

  private boolean visible;

  public InsertCrosstabGroupAction()

  {
    putValue(Action.NAME, ActionMessages.getString("InsertCrosstabGroupAction.Text"));
    putValue(Action.SHORT_DESCRIPTION, ActionMessages.getString("InsertCrosstabGroupAction.Description"));
    putValue(Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic("InsertCrosstabGroupAction.Mnemonic"));
    putValue(Action.ACCELERATOR_KEY, ActionMessages.getOptionalKeyStroke("InsertCrosstabGroupAction.Accelerator"));
    putValue(Action.SMALL_ICON, IconLoader.getInstance().getGenericSquare());

    visible = WorkspaceSettings.getInstance().isExperimentalFeaturesVisible();
    WorkspaceSettings.getInstance().addSettingsListener(this);
  }

  public void actionPerformed(final ActionEvent e)
  {
    final ReportRenderContext activeContext = getActiveContext();
    if (activeContext == null)
    {
      return;
    }

    final CrosstabGroup newGroup = new CrosstabGroup();
    try
    {
      final AbstractReportDefinition report = activeContext.getReportDefinition();
      Object selectedElement = report;
      if (getSelectionModel().getSelectionCount() > 0)
      {
        selectedElement = getSelectionModel().getSelectedElement(0);
      }

      if (selectedElement == report ||
          selectedElement == report.getRootGroup())
      {
        final Group rootGroup = report.getRootGroup();
        report.setRootGroup(newGroup);
        activeContext.getUndo().addChange(ActionMessages.getString("InsertCrosstabGroupAction.UndoName"),
            new InsertGroupOnReportUndoEntry(rootGroup, newGroup));
      }
    }
    catch (Exception ex)
    {
      UncaughtExceptionsModel.getInstance().addException(ex);
    }
  }

  protected void updateSelection()
  {
    if (visible == false)
    {
      setEnabled(false);
      return;
    }

    if (getSelectionModel() != null && getSelectionModel().getSelectionCount() == 0)
    {
      // there's nothing selected, we can safely add a new group
      // at the report level (AbstractReportDefinition)
      setEnabled(true);
      return;
    }
    if (isSingleElementSelection() == false)
    {
      // there's more than 1 element selected, disable because
      // we can't know where to insert in this case
      setEnabled(false);
      return;
    }

    final AbstractReportDefinition report = getActiveContext().getReportDefinition();
    final Object selectedElement = getSelectionModel().getSelectedElement(0);
    if (selectedElement == report ||
        selectedElement == report.getRootGroup())
    {
      setEnabled(true);
      return;
    }

    setEnabled(false);
  }

  private static class InsertGroupOnReportUndoEntry implements UndoEntry
  {
    private static final long serialVersionUID = -6048384734272767240L;
    private Group newRootGroup;
    private Group oldRootGroup;

    private InsertGroupOnReportUndoEntry(final Group oldRootGroup, final Group newRootGroup)
    {
      this.oldRootGroup = oldRootGroup;
      this.newRootGroup = newRootGroup;
    }

    public void undo(final ReportRenderContext renderContext)
    {
      final AbstractReportDefinition report = renderContext.getReportDefinition();
      report.setRootGroup(oldRootGroup);
    }

    public void redo(final ReportRenderContext renderContext)
    {
      final AbstractReportDefinition report = renderContext.getReportDefinition();
      final SubGroupBody body = new SubGroupBody();
      newRootGroup.setBody(body);
      report.setRootGroup(newRootGroup);
      body.setGroup(oldRootGroup);
    }

    public UndoEntry merge(final UndoEntry newEntry)
    {
      return null;
    }
  }


  public void setVisible(final boolean visible)
  {
    final boolean oldValue = this.visible;
    if (oldValue != visible)
    {
      this.visible = visible;
      firePropertyChange("visible", oldValue, visible);//NON-NLS
    }
  }

  public boolean isVisible()
  {
    return visible;
  }

  public void settingsChanged()
  {
    setVisible(WorkspaceSettings.getInstance().isExperimentalFeaturesVisible());
  }
}

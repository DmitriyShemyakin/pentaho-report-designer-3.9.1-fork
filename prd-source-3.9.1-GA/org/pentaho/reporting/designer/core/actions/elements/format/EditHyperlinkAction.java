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

package org.pentaho.reporting.designer.core.actions.elements.format;

import java.awt.Component;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.util.Map;
import javax.swing.Action;
import javax.swing.JDialog;
import javax.swing.JFrame;

import org.pentaho.reporting.designer.core.actions.AbstractElementSelectionAction;
import org.pentaho.reporting.designer.core.actions.ActionMessages;
import org.pentaho.reporting.designer.core.editor.drilldown.HyperlinkEditorDialog;
import org.pentaho.reporting.designer.core.editor.format.EditableStyleSheet;
import org.pentaho.reporting.designer.core.model.selection.ReportSelectionModel;
import org.pentaho.reporting.designer.core.util.IconLoader;
import org.pentaho.reporting.designer.core.util.undo.ElementFormatUndoEntry;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.SwingUtil;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class EditHyperlinkAction extends AbstractElementSelectionAction
{
  public EditHyperlinkAction()
  {
    putValue(Action.SMALL_ICON, IconLoader.getInstance().getHyperlinkIcon());
    putValue(Action.NAME, ActionMessages.getString("EditHyperlinkAction.Text"));
    putValue(Action.SHORT_DESCRIPTION, ActionMessages.getString("EditHyperlinkAction.Description"));
    putValue(Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic("EditHyperlinkAction.Mnemonic"));
    putValue(Action.ACCELERATOR_KEY, ActionMessages.getOptionalKeyStroke("EditHyperlinkAction.Accelerator"));
  }

  protected void updateSelection()
  {
    if (isSingleElementSelection() == false)
    {
      setEnabled(false);
    }
    else
    {
      setEnabled(getSelectionModel().getSelectedElement(0) instanceof Element);
    }
  }

  public void actionPerformed(final ActionEvent e)
  {
    final ReportSelectionModel selectionModel1 = getSelectionModel();
    if (selectionModel1 == null)
    {
      return;
    }

    final Element[] visualElements = selectionModel1.getSelectedVisualElements();
    if (visualElements.length == 0)
    {
      return;
    }

    final EditableStyleSheet styleSheet = createEditableStyleForSelection(visualElements);

    final Component parent = getReportDesignerContext().getParent();
    final Window window = SwingUtil.getWindowAncestor(parent);
    final HyperlinkEditorDialog dialog = createDialog(window);

    final Map styleExpressions;
    if (visualElements.length != 1)
    {
      styleExpressions = null;
    }
    else
    {
      styleExpressions = visualElements[0].getStyleExpressions();
    }

    final ElementFormatUndoEntry.EditResult result =
        dialog.performEdit(getReportDesignerContext(), styleSheet, styleExpressions);
    if (result == null)
    {
      return;
    }

    final ElementFormatUndoEntry undoEntry = result.process(visualElements);
    getActiveContext().getUndo().addChange(ActionMessages.getString("EditHyperlinkAction.UndoName"), undoEntry);
  }

  private EditableStyleSheet createEditableStyleForSelection(final Element[] visualElements)
  {
    // collect all common values ..
    final StyleKey[] keys = StyleKey.getDefinedStyleKeys();
    final Object[] values = new Object[keys.length];
    for (int i = 0; i < keys.length; i++)
    {
      final StyleKey styleKey = keys[i];
      for (int j = 0; j < visualElements.length; j++)
      {
        final Element element = visualElements[j];
        final Object o = element.getStyle().getStyleProperty(styleKey);
        if (values[i] == null)
        {
          values[i] = o;
        }
        else
        {
          if (ObjectUtilities.equal(values[i], o) == false)
          {
            values[i] = null;
            break;
          }
        }
      }
    }

    final EditableStyleSheet styleSheet = new EditableStyleSheet(null);
    for (int i = 0; i < keys.length; i++)
    {
      final StyleKey styleKey = keys[i];
      styleSheet.setStyleProperty(styleKey, values[i]);
    }
    return styleSheet;
  }

  protected HyperlinkEditorDialog createDialog(final Window window)
  {
    final HyperlinkEditorDialog dialog;
    if (window instanceof JDialog)
    {
      dialog = new HyperlinkEditorDialog((JDialog) window);
    }
    else if (window instanceof JFrame)
    {
      dialog = new HyperlinkEditorDialog((JFrame) window);
    }
    else
    {
      dialog = new HyperlinkEditorDialog();
    }
    LibSwingUtil.centerDialogInParent(dialog);
    return dialog;
  }
}

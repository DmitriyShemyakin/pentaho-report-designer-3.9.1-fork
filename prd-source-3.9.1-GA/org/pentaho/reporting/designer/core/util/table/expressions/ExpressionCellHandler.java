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

package org.pentaho.reporting.designer.core.util.table.expressions;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.editor.ReportRenderContext;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.SwingUtil;

public class ExpressionCellHandler implements TableCellRenderer, TableCellEditor
{
  private JButton editButton;
  private Component expressionEditorPanel;
  private ExpressionCellEditor expressionCellEditor;
  private ImageIcon addIcon;
  private ImageIcon editIcon;
  private JTable table;

  public ExpressionCellHandler(final ExpressionCellEditor aExpressionCellEditor)
  {
    this.expressionCellEditor = aExpressionCellEditor;

    editButton = new JButton(new EditorAction());
    editButton.setHorizontalAlignment(SwingConstants.CENTER);
    editButton.setBorderPainted(false);

    addIcon = new ImageIcon
        (ExpressionCellHandler.class.getResource("/org/pentaho/reporting/designer/core/icons/Add.png")); // NON-NLS
    editIcon = new ImageIcon
        (ExpressionCellHandler.class.getResource("/org/pentaho/reporting/designer/core/icons/Edit.png"));// NON-NLS
  }

  public Component getTableCellRendererComponent(final JTable table,
                                                 final Object value,
                                                 final boolean isSelected,
                                                 final boolean hasFocus,
                                                 final int row,
                                                 final int column)
  {
    this.table = table;

    configureEditButton(table, value, isSelected);
    return editButton;
  }

  public Component getTableCellEditorComponent(final JTable table,
                                               final Object value,
                                               final boolean isSelected,
                                               final int row,
                                               final int column)
  {
    this.table = table;

    this.expressionEditorPanel = expressionCellEditor.getTableCellEditorComponent(table, value, isSelected, row, column);
    configureEditButton(table, value, isSelected);
    return editButton;
  }

  private void configureEditButton(final JTable table, final Object aValue, final boolean isSelected)
  {
    if (aValue == null)
    {
      editButton.setIcon(addIcon);
    }
    else
    {
      editButton.setIcon(editIcon);
    }
    if (isSelected)
    {
      editButton.setBackground(table.getSelectionBackground());
    }
    else
    {
      editButton.setBackground(Color.WHITE);
    }
  }

  private class EditorAction extends AbstractAction
  {
    private EditorAction()
    {
    }

    public void actionPerformed(final ActionEvent e)
    {
      if (table == null)
      {
        return;
      }
      if (expressionEditorPanel == null)
      {
        return;
      }
      final ExpressionEditorDialog editorDialog;
      final Object o = SwingUtil.getWindowAncestor(table);
      if (o instanceof Dialog)
      {
        editorDialog = new ExpressionEditorDialog((Dialog) o, expressionEditorPanel);
      }
      else if (o instanceof Frame)
      {
        editorDialog = new ExpressionEditorDialog((Frame) o, expressionEditorPanel);
      }
      else
      {
        editorDialog = new ExpressionEditorDialog(expressionEditorPanel);
      }
      editorDialog.setVisible(true);
      stopCellEditing();
    }
  }

  public Object getCellEditorValue()
  {
    return expressionCellEditor.getCellEditorValue();
  }

  public boolean isCellEditable(final EventObject anEvent)
  {
    if (anEvent instanceof MouseEvent)
    {
      final MouseEvent mouseEvent = (MouseEvent) anEvent;
      return mouseEvent.getClickCount() >= 1 && mouseEvent.getButton() == MouseEvent.BUTTON1;
    }
    return true;
  }

  public boolean shouldSelectCell(final EventObject anEvent)
  {
    return expressionCellEditor.shouldSelectCell(anEvent);
  }

  public boolean stopCellEditing()
  {
    return expressionCellEditor.stopCellEditing();
  }

  public void removeCellEditorListener(final CellEditorListener l)
  {
    expressionCellEditor.removeCellEditorListener(l);
  }

  public void addCellEditorListener(final CellEditorListener l)
  {
    expressionCellEditor.addCellEditorListener(l);
  }

  public void cancelCellEditing()
  {
    expressionCellEditor.cancelCellEditing();
  }

  public ReportDesignerContext getReportDesignerContext()
  {
    return expressionCellEditor.getReportDesignerContext();
  }

  public void setReportDesignerContext(final ReportDesignerContext reportDesignerContext)
  {
    expressionCellEditor.setReportDesignerContext(reportDesignerContext);
  }
}

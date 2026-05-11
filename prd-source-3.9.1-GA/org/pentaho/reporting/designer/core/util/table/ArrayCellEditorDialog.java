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

package org.pentaho.reporting.designer.core.util.table;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.lang.reflect.Array;
import java.util.ArrayList;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.util.IconLoader;
import org.pentaho.reporting.designer.core.util.UtilMessages;
import org.pentaho.reporting.libraries.designtime.swing.BorderlessButton;
import org.pentaho.reporting.libraries.designtime.swing.CommonDialog;
import org.pentaho.reporting.libraries.designtime.swing.bulk.SortBulkDownAction;
import org.pentaho.reporting.libraries.designtime.swing.bulk.SortBulkUpAction;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class ArrayCellEditorDialog extends CommonDialog
{
  private static class AddEntryAction extends AbstractAction
  {
    private ArrayTableModel tableModel;

    private AddEntryAction(final ArrayTableModel tableModel)
    {
      this.tableModel = tableModel;
      putValue(Action.SMALL_ICON, IconLoader.getInstance().getAddIcon());
      putValue(Action.SHORT_DESCRIPTION,
          UtilMessages.getInstance().getString("ArrayCellEditorDialog.AddEntry.Description"));
    }

    public void actionPerformed(final ActionEvent e)
    {
      tableModel.add(null);
    }
  }

  private class RemoveEntryAction extends AbstractAction implements ListSelectionListener
  {
    private ListSelectionModel selectionModel;
    private ArrayTableModel tableModel;

    private RemoveEntryAction(final ArrayTableModel tableModel,
                              final ListSelectionModel selectionModel)
    {
      this.tableModel = tableModel;
      putValue(Action.SMALL_ICON, IconLoader.getInstance().getRemoveIcon());
      putValue(Action.SHORT_DESCRIPTION,
          UtilMessages.getInstance().getString("ArrayCellEditorDialog.RemoveEntry.Description"));


      this.selectionModel = selectionModel;
      this.selectionModel.addListSelectionListener(this);
    }

    public void actionPerformed(final ActionEvent e)
    {
      stopCellEditing();

      final int maxIdx = selectionModel.getMaxSelectionIndex();
      final ArrayList<Integer> list = new ArrayList<Integer>();
      for (int i = selectionModel.getMinSelectionIndex(); i <= maxIdx; i++)
      {
        if (selectionModel.isSelectedIndex(i))
        {
          list.add(0, i);
        }
      }

      for (int i = 0; i < list.size(); i++)
      {
        final Integer dataEntry = list.get(i);
        tableModel.remove(dataEntry);
      }
    }

    public void valueChanged(final ListSelectionEvent e)
    {
      setEnabled(selectionModel.isSelectionEmpty() == false);
    }
  }

  private ArrayTableModel tableModel;
  private ElementMetaDataTable table;

  public ArrayCellEditorDialog()
      throws HeadlessException
  {
    init();
  }

  public ArrayCellEditorDialog(final Frame owner)
      throws HeadlessException
  {
    super(owner);
    init();
  }

  public ArrayCellEditorDialog(final Dialog owner)
      throws HeadlessException
  {
    super(owner);
    init();
  }

  protected void init()
  {
    setTitle(UtilMessages.getInstance().getString("ArrayCellEditorDialog.Title"));
    super.init();
  }

  protected Component createContentPane()
  {
    tableModel = new ArrayTableModel();

    table = new ElementMetaDataTable();
    table.setModel(tableModel);

    final ListSelectionModel selectionModel = table.getSelectionModel();

    final Action addGroupAction = new AddEntryAction(tableModel);
    final Action removeGroupAction = new RemoveEntryAction(tableModel, selectionModel);

    final Action sortUpAction = new SortBulkUpAction(tableModel, selectionModel, table);
    final Action sortDownAction = new SortBulkDownAction(tableModel, selectionModel, table);

    final JPanel buttonsPanel = new JPanel();
    buttonsPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
    buttonsPanel.add(new BorderlessButton(sortUpAction));
    buttonsPanel.add(new BorderlessButton(sortDownAction));
    buttonsPanel.add(Box.createHorizontalStrut(20));
    buttonsPanel.add(new BorderlessButton(addGroupAction));
    buttonsPanel.add(new BorderlessButton(removeGroupAction));

    final JPanel panel = new JPanel();
    panel.setLayout(new BorderLayout());
    panel.add(new JScrollPane(table), BorderLayout.CENTER);
    panel.add(buttonsPanel, BorderLayout.NORTH);

    return panel;
  }

  public Object editArray(Object data,
                          final Class arrayType,
                          final String valueRole,
                          final Class propertyEditorType,
                          final String[] extraFields)
  {
    if (arrayType == null)
    {
      throw new NullPointerException();
    }
    if (arrayType.isArray() == false)
    {
      throw new IllegalArgumentException("Expect an array class, not a primitive data-type");
    }
    final Class componentType = arrayType.getComponentType();
    if (ArrayAccessUtility.isArray(data) == false)
    {
      data = Array.newInstance(componentType, 0);
    }

    tableModel.setType(componentType);
    tableModel.setValueRole(valueRole);
    tableModel.setExtraFields(extraFields);
    tableModel.setPropertyEditorType(propertyEditorType);
    tableModel.setData(ArrayAccessUtility.normalizeArray(data), componentType);

    if (performEdit() == false)
    {
      return null;
    }

    stopCellEditing();

    // process the array ..
    final Object[] objects = tableModel.toArray();
    return ArrayAccessUtility.normalizeNative(objects, componentType);
  }

  protected void stopCellEditing()
  {
    if (table.getCellEditor() != null)
    {
      table.getCellEditor().stopCellEditing();
    }
  }

  public ReportDesignerContext getReportDesignerContext()
  {
    return table.getReportDesignerContext();
  }

  public void setReportDesignerContext(final ReportDesignerContext reportDesignerContext)
  {
    if (reportDesignerContext != null)
    {
      this.table.setReportDesignerContext(reportDesignerContext);
    }
    else
    {
      this.table.setReportDesignerContext(null);
    }
  }
}

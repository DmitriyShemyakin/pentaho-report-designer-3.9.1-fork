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

import java.awt.Color;
import java.awt.Component;
import java.lang.reflect.Constructor;
import javax.swing.DefaultCellEditor;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.table.TableModel;

/**
 * This editor can handle all objects that have a single argument String constructor.
 *
 * @author Thomas Morgner
 */
public class GenericCellEditor extends DefaultCellEditor
{
  private Constructor constructor;
  private Object value;
  private Class fallbackType;

  public GenericCellEditor(final Class fallbackType)
  {
    super(new JTextField());
    this.fallbackType = fallbackType;
    getComponent().setName("Table.editor"); // NON-NLS
  }

  public boolean stopCellEditing()
  {
    final String s = (String) super.getCellEditorValue();
    if ("".equals(s))
    {
      super.stopCellEditing();
    }

    try
    {
      value = constructor.newInstance(new Object[]{s});
    }
    catch (Exception e)
    {
      final JComponent editorComponent = (JComponent) getComponent();
      editorComponent.setBorder(new LineBorder(Color.red));
      return false;
    }
    return super.stopCellEditing();
  }

  public Component getTableCellEditorComponent(JTable table, Object value,
                                               boolean isSelected,
                                               int row, int column)
  {
    this.value = null;
    final JComponent editorComponent = (JComponent) getComponent();
    editorComponent.setBorder(new LineBorder(Color.black));

    Class type;
    final TableModel tableModel = table.getModel();
    if (tableModel instanceof ElementMetaDataTableModel)
    {
      final ElementMetaDataTableModel model = (ElementMetaDataTableModel) tableModel;
      type = model.getClassForCell(row, table.convertColumnIndexToModel(column));
    }
    else
    {
      type = table.getColumnClass(column);
    }
    // Since our obligation is to produce a value which is
    // assignable for the required fallbackType it is OK to use the
    // String constructor for columns which are declared
    // to contain Objects. A String is an Object.
    if (type == Object.class)
    {
      type = this.fallbackType;
    }

    constructor = lookupConstructor(type);
    if (constructor == null)
    {
      constructor = lookupConstructor(this.fallbackType);
    }
    return super.getTableCellEditorComponent(table, value, isSelected, row, column);
  }

  private Constructor lookupConstructor(final Class type)
  {

    try
    {
      return type.getConstructor(new Class[]{String.class});
    }
    catch (Exception e)
    {
      return null;
    }
  }

  public Object getCellEditorValue()
  {
    return value;
  }
}

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
 * Copyright (c) 2008 - 2009 Pentaho Corporation, .  All rights reserved.
 */

package org.pentaho.reporting.designer.core.editor.expressions;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.ArrayList;
import javax.swing.JScrollPane;
import javax.swing.table.TableCellEditor;

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.editor.ReportRenderContext;
import org.pentaho.reporting.designer.core.model.selection.ReportSelectionModel;
import org.pentaho.reporting.designer.core.util.SidePanel;
import org.pentaho.reporting.designer.core.util.table.ElementMetaDataTable;
import org.pentaho.reporting.designer.core.util.table.SortHeaderPanel;
import org.pentaho.reporting.engine.classic.core.function.Expression;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class ExpressionPropertiesEditorPanel extends SidePanel
{
  private ExpressionPropertiesTableModel dataModel;
  private ElementMetaDataTable table;

  private static final Expression[] EMPTY_EXPRESSIONS = new Expression[0];
  private SortHeaderPanel headerPanel;

  /**
   * Creates a new <code>JPanel</code> with a double buffer and a flow layout.
   */
  public ExpressionPropertiesEditorPanel()
  {
    setLayout(new BorderLayout());

    dataModel = new ExpressionPropertiesTableModel();

    table = new ElementMetaDataTable();
    table.setModel(dataModel);

    headerPanel = new SortHeaderPanel(dataModel);

    add(headerPanel, BorderLayout.NORTH);
    add(new JScrollPane(table), BorderLayout.CENTER);
  }

  public Expression[] getData()
  {
    return dataModel.getData();
  }

  public void setData(final Expression[] elements)
  {
    stopEditing();

    dataModel.setData(elements);
  }

  public void stopEditing()
  {
    final TableCellEditor tableCellEditor = table.getCellEditor();
    if (tableCellEditor != null)
    {
      tableCellEditor.stopCellEditing();
    }
  }

  protected void updateDesignerContext(final ReportDesignerContext oldContext, final ReportDesignerContext newContext)
  {
    super.updateDesignerContext(oldContext, newContext);
    table.setReportDesignerContext(newContext);
  }

  protected void updateSelection(final ReportSelectionModel model)
  {
    if (model == null)
    {
      setData(EMPTY_EXPRESSIONS);
    }
    else
    {
      final Object[] selectedElements = model.getSelectedElements();
      final ArrayList<Expression> filter = new ArrayList();
      for (int i = 0; i < selectedElements.length; i++)
      {
        final Object element = selectedElements[i];
        if (element instanceof Expression)
        {
          filter.add((Expression) element);
        }
      }
      setData(filter.toArray(new Expression[filter.size()]));
    }
  }

  protected void updateActiveContext(final ReportRenderContext oldContext, final ReportRenderContext newContext)
  {
    stopEditing();

    super.updateActiveContext(oldContext, newContext);
    dataModel.setActiveContext(newContext);
    if (newContext == null)
    {
      setData(EMPTY_EXPRESSIONS);
    }
  }

  /**
   * Sets whether or not this component is enabled. A component that is enabled may respond to user input, while a
   * component that is not enabled cannot respond to user input.  Some components may alter their visual representation
   * when they are disabled in order to provide feedback to the user that they cannot take input. <p>Note: Disabling a
   * component does not disable it's children.
   * <p/>
   * <p>Note: Disabling a lightweight component does not prevent it from receiving MouseEvents.
   *
   * @param enabled true if this component should be enabled, false otherwise
   * @beaninfo preferred: true bound: true attribute: visualUpdate true description: The enabled state of the
   * component.
   * @see Component#isEnabled
   * @see Component#isLightweight
   */
  public void setEnabled(final boolean enabled)
  {
    super.setEnabled(enabled);
    table.setEnabled(enabled);
    headerPanel.setEnabled(enabled);
  }
}

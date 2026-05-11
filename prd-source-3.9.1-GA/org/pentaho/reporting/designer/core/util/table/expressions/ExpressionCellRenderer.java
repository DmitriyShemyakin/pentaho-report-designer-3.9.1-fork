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

package org.pentaho.reporting.designer.core.util.table.expressions;

import java.awt.Component;
import java.util.Locale;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.pentaho.reporting.engine.classic.core.function.FormulaExpression;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionRegistry;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 * @deprecated Will remove in 4.0
 */
public class ExpressionCellRenderer extends DefaultTableCellRenderer
{
  public ExpressionCellRenderer()
  {
    putClientProperty("html.disable", Boolean.TRUE); // NON-NLS
  }

  /**
   * Returns the default table cell renderer.
   *
   * @param table      the <code>JTable</code>
   * @param value      the value to assign to the cell at <code>[row, column]</code>
   * @param isSelected true if cell is selected
   * @param hasFocus   true if cell has focus
   * @param row        the row of the cell to render
   * @param column     the column of the cell to render
   * @return the default table cell renderer
   */
  public Component getTableCellRendererComponent(final JTable table,
                                                 final Object value,
                                                 final boolean isSelected,
                                                 final boolean hasFocus,
                                                 final int row,
                                                 final int column)
  {
    // just configure it
    super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    setText(" ");
    setToolTipText(null);
    if (value instanceof FormulaExpression)
    {
      final FormulaExpression fx = (FormulaExpression) value;
      setText(fx.getFormula());
    }
    else if (value != null)
    {
      final ExpressionRegistry registry = ExpressionRegistry.getInstance();
      final String expressionName = value.getClass().getName();
      if (registry.isExpressionRegistered(expressionName))
      {
        final ExpressionMetaData data =
            registry.getExpressionMetaData(expressionName);
        setText(data.getDisplayName(Locale.getDefault()));
        if (data.isDeprecated())
        {
          setToolTipText(data.getDeprecationMessage(Locale.getDefault()));
        }
      }
      else
      {
        setText(expressionName);
      }
    }
    return this;
  }
}

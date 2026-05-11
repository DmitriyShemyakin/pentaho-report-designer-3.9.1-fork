package org.pentaho.reporting.designer.core.util.table;

import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Todo: Document me!
 * <p/>
 * Date: 09.12.2009
 * Time: 16:43:41
 *
 * @author Thomas Morgner.
 */
public class GroupedNameCellRenderer extends DefaultTableCellRenderer
{
  public GroupedNameCellRenderer()
  {
  }

  /**
   * Returns the default table cell renderer.
   * <p/>
   * During a printing operation, this method will be called with
   * <code>isSelected</code> and <code>hasFocus</code> values of
   * <code>false</code> to prevent selection and focus from appearing
   * in the printed output. To do other customization based on whether
   * or not the table is being printed, check the return value from
   * {@link javax.swing.JComponent#isPaintingForPrint()}.
   *
   * @param table      the <code>JTable</code>
   * @param value      the value to assign to the cell at
   *                   <code>[row, column]</code>
   * @param isSelected true if cell is selected
   * @param hasFocus   true if cell has focus
   * @param row        the row of the cell to render
   * @param column     the column of the cell to render
   * @return the default table cell renderer
   * @see javax.swing.JComponent#isPaintingForPrint()
   */
  public Component getTableCellRendererComponent(final JTable table,
                                                 final Object value,
                                                 final boolean isSelected,
                                                 final boolean hasFocus,
                                                 final int row,
                                                 final int column)
  {
    super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    if (value instanceof GroupedName)
    {
      final GroupedName name = (GroupedName) value;
      if (table.getModel() instanceof SortableTableModel)
      {
        final SortableTableModel model = (SortableTableModel) table.getModel();
        final TableStyle style = model.getTableStyle();
        if (TableStyle.GROUPED.equals(style))
        {
          setText(name.getName());
        }
        else
        {
          setText(name.getName() + " (" + name.getGroupName() + ")");
        }
      }
      else
      {
        setText(name.getName() + " (" + name.getGroupName() + ")");
      }
    }
    return this;
  }
}

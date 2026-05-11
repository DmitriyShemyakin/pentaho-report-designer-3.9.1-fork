package org.pentaho.reporting.engine.classic.core.states;

import org.pentaho.reporting.engine.classic.core.MetaTableModel;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributes;

/**
 * Todo: Document me!
 * <p/>
 * Date: 15.12.2009
 * Time: 12:05:09
 *
 * @author Thomas Morgner.
 * @deprecated Do not use, class moved into "cache" package.
 */
public class IndexedMetaTableModel extends IndexedTableModel implements MetaTableModel
{
  private MetaTableModel backend;

  public IndexedMetaTableModel(final MetaTableModel backend)
  {
    super(backend);
    this.backend = backend;
  }

  public DataAttributes getCellDataAttributes(final int row, final int column)
  {
    return backend.getCellDataAttributes(row, indexToColumn(column));
  }

  public boolean isCellDataAttributesSupported()
  {
    return backend.isCellDataAttributesSupported();
  }

  public DataAttributes getColumnAttributes(final int column)
  {
    if (column < backend.getColumnCount())
    {
      return new ColumnIndexDataAttributes(backend.getColumnAttributes(indexToColumn(column)), Boolean.FALSE);
    }
    else
    {
      return new ColumnIndexDataAttributes(backend.getColumnAttributes(indexToColumn(column)), Boolean.TRUE);
    }
  }

  public DataAttributes getTableAttributes()
  {
    return backend.getTableAttributes();
  }
}

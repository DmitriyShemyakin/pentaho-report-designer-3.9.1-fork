package org.pentaho.reporting.engine.classic.core.states;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MetaAttributeNames;
import org.pentaho.reporting.engine.classic.core.MetaTableModel;
import org.pentaho.reporting.engine.classic.core.util.CloseableTableModel;
import org.pentaho.reporting.engine.classic.core.wizard.ConceptQueryMapper;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributeContext;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributes;
import org.pentaho.reporting.engine.classic.core.wizard.DefaultConceptQueryMapper;
import org.pentaho.reporting.engine.classic.core.wizard.DefaultDataAttributes;
import org.pentaho.reporting.engine.classic.core.wizard.EmptyDataAttributes;

/**
 * Todo: Document me!
 * <p/>
 * Date: 15.12.2009
 * Time: 12:05:09
 *
 * @author Thomas Morgner.
 * @deprecated Do not use, class moved into "cache" package.
 */
public class IndexedTableModel implements CloseableTableModel, MetaTableModel
{
  protected static class ColumnIndexDataAttributes implements DataAttributes
  {
    private DataAttributes backend;
    private Boolean indexColumn;

    public ColumnIndexDataAttributes(final DataAttributes backend, final Boolean indexColumn)
    {
      this.backend = backend;
      this.indexColumn = indexColumn;
      if (backend == null)
      {
        this.backend = EmptyDataAttributes.INSTANCE;
      }
    }

    private boolean contains(final String[] hay, final String needle)
    {
      for (int i = 0; i < hay.length; i++)
      {
        if (needle.equals(hay[i]))
        {
          return true;
        }
      }
      return false;
    }

    public String[] getMetaAttributeDomains()
    {
      final String[] domains = backend.getMetaAttributeDomains();
      if (contains(domains, MetaAttributeNames.Core.NAMESPACE))
      {
        return domains;
      }

      final String[] strings = new String[domains.length + 1];
      System.arraycopy(domains, 0, strings, 0, domains.length);
      strings[domains.length] = MetaAttributeNames.Core.NAMESPACE;
      return strings;
    }

    public String[] getMetaAttributeNames(final String domainName)
    {
      if (MetaAttributeNames.Core.NAMESPACE.equals(domainName) == false)
      {
        return backend.getMetaAttributeNames(domainName);
      }

      final String[] attributeNames = backend.getMetaAttributeNames(domainName);
      if (contains(attributeNames, MetaAttributeNames.Core.INDEXED_COLUMN))
      {
        return attributeNames;
      }

      final String[] strings = new String[attributeNames.length + 1];
      System.arraycopy(attributeNames, 0, strings, 0, attributeNames.length);
      strings[attributeNames.length] = MetaAttributeNames.Core.INDEXED_COLUMN;
      return strings;
    }

    /**
     * @param domain  never null.
     * @param name    never null.
     * @param type    can be null.
     * @param context never null.
     * @return
     */
    public Object getMetaAttribute(final String domain,
                                   final String name,
                                   final Class type,
                                   final DataAttributeContext context)
    {
      return getMetaAttribute(domain, name, type, context, null);
    }

    /**
     * @param domain       never null.
     * @param name         never null.
     * @param type         can be null.
     * @param context      never null.
     * @param defaultValue can be null
     * @return
     */
    public Object getMetaAttribute(final String domain,
                                   final String name,
                                   final Class type,
                                   final DataAttributeContext context,
                                   final Object defaultValue)
    {
      final Object retval = backend.getMetaAttribute(domain, name, type, context, defaultValue);
      if (retval != null)
      {
        return retval;
      }
      if (MetaAttributeNames.Core.NAMESPACE.equals(domain) &&
          MetaAttributeNames.Core.INDEXED_COLUMN.equals(name))
      {
        return indexColumn;
      }
      return defaultValue;
    }

    public ConceptQueryMapper getMetaAttributeMapper(final String domain, final String name)
    {
      final ConceptQueryMapper retval = backend.getMetaAttributeMapper(domain, name);
      if (retval != null)
      {
        return retval;
      }
      if (MetaAttributeNames.Core.NAMESPACE.equals(domain) &&
          MetaAttributeNames.Core.INDEXED_COLUMN.equals(name))
      {
        return DefaultConceptQueryMapper.INSTANCE;
      }
      return null;
    }

    public Object clone() throws CloneNotSupportedException
    {
      final ColumnIndexDataAttributes dataAttributes = (ColumnIndexDataAttributes) super.clone();
      dataAttributes.backend = (DataAttributes) backend.clone();
      return dataAttributes;
    }
  }

  private CloseableTableModel closeableTableModel;
  private TableModel backend;

  public IndexedTableModel(final TableModel backend)
  {
    if (backend == null)
    {
      throw new NullPointerException();
    }
    if (backend instanceof CloseableTableModel)
    {
      closeableTableModel = (CloseableTableModel) backend;
    }
    this.backend = backend;
  }

  /**
   * If this model has disposeable resources assigned, close them or dispose them.
   */
  public void close()
  {
    if (closeableTableModel != null)
    {
      closeableTableModel.close();
    }
  }

  public int getRowCount()
  {
    return backend.getRowCount();
  }

  public int getColumnCount()
  {
    return 2 * backend.getColumnCount();
  }

  protected int indexToColumn(final int col)
  {
    if (col < 0)
    {
      throw new IndexOutOfBoundsException();
    }
    final int count = backend.getColumnCount();
    if (col >= (count * 2))
    {
      throw new IndexOutOfBoundsException();
    }
    if (col < count)
    {
      return col;
    }
    return col - count;
  }

  public String getColumnName(final int columnIndex)
  {
    if (columnIndex < 0)
    {
      throw new IndexOutOfBoundsException();
    }
    if (columnIndex < backend.getColumnCount())
    {
      return backend.getColumnName(columnIndex);
    }

    return ClassicEngineBoot.INDEX_COLUMN_PREFIX + indexToColumn(columnIndex);
  }

  public Class getColumnClass(final int columnIndex)
  {
    return backend.getColumnClass(indexToColumn(columnIndex));
  }

  public boolean isCellEditable(final int rowIndex, final int columnIndex)
  {
    return backend.isCellEditable(rowIndex, indexToColumn(columnIndex));
  }

  public Object getValueAt(final int rowIndex, final int columnIndex)
  {
    return backend.getValueAt(rowIndex, indexToColumn(columnIndex));
  }

  public void setValueAt(final Object aValue, final int rowIndex, final int columnIndex)
  {
    backend.setValueAt(aValue, rowIndex, indexToColumn(columnIndex));
  }

  public void addTableModelListener(final TableModelListener l)
  {
    backend.addTableModelListener(l);
  }

  public void removeTableModelListener(final TableModelListener l)
  {
    backend.removeTableModelListener(l);
  }

  /**
   * Returns the meta-attribute as Java-Object. The object type that is expected by the caller is defined in the
   * TableMetaData property set. It is the responsibility of the implementor to map the native meta-data model into a
   * model suitable for reporting.
   * <p/>
   * Be aware that cell-level attributes do not make it into the designtime dataschema, as this dataschema only looks
   * at the structural metadata available and does not contain any data references.
   *
   * @param row    the row of the cell for which the meta-data is queried.
   * @param column the index of the column for which the meta-data is queried.
   * @return the meta-data object.
   */
  public DataAttributes getCellDataAttributes(final int row, final int column)
  {
    return EmptyDataAttributes.INSTANCE;
  }

  /**
   * Checks, whether cell-data attributes are supported by this tablemodel implementation.
   *
   * @return true, if the model supports cell-level attributes, false otherwise.
   */
  public boolean isCellDataAttributesSupported()
  {
    return false;
  }

  /**
   * Returns the column-level attributes for the given column.
   *
   * @param column the column.
   * @return data-attributes, never null.
   */
  public DataAttributes getColumnAttributes(final int column)
  {
    if (column < backend.getColumnCount())
    {
      return new ColumnIndexDataAttributes(null, Boolean.FALSE);
    }
    else
    {
      return new ColumnIndexDataAttributes(null, Boolean.TRUE);
    }
  }

  /**
   * Returns table-wide attributes. This usually contain hints about the data-source used to query the data as well as
   * hints on the sort-order of the data.
   *
   * @return the table-attributes, never null.
   */
  public DataAttributes getTableAttributes()
  {
    return EmptyDataAttributes.INSTANCE;
  }

  public String toString()
  {
    final StringBuilder sb = new StringBuilder();
    sb.append("IndexedTableModel");
    sb.append("={backend=").append(backend);
    sb.append('}');
    return sb.toString();
  }
}

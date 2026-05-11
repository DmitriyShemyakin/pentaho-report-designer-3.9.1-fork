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

package org.pentaho.reporting.designer.core.editor.styles;

import java.beans.PropertyEditor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.designer.core.Messages;
import org.pentaho.reporting.designer.core.editor.ReportRenderContext;
import org.pentaho.reporting.designer.core.util.FastPropertyEditorManager;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.designer.core.util.table.ElementMetaDataTableModel;
import org.pentaho.reporting.designer.core.util.table.GroupedName;
import org.pentaho.reporting.designer.core.util.table.GroupingHeader;
import org.pentaho.reporting.designer.core.util.table.GroupingModel;
import org.pentaho.reporting.designer.core.util.table.TableStyle;
import org.pentaho.reporting.designer.core.util.undo.CompoundUndoEntry;
import org.pentaho.reporting.designer.core.util.undo.StyleEditUndoEntry;
import org.pentaho.reporting.designer.core.util.undo.StyleExpressionEditUndoEntry;
import org.pentaho.reporting.designer.core.util.undo.UndoEntry;
import org.pentaho.reporting.designer.core.util.undo.UndoManager;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.metadata.AttributeMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.GroupedMetaDataComparator;
import org.pentaho.reporting.engine.classic.core.metadata.PlainMetaDataComparator;
import org.pentaho.reporting.engine.classic.core.metadata.StyleMetaData;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

/**
 * Todo: Document me!
 *
 * @author Thomas Morgner
 */
public class StyleTableModel
    extends AbstractTableModel implements ElementMetaDataTableModel, GroupingModel
{
  private static final Log logger = LogFactory.getLog(StyleTableModel.class);

  private static final Object NULL_INDICATOR = new Object();
  private static final Element[] EMPTY_ELEMENTS = new Element[0];
  private static final StyleMetaData[] EMPTY_METADATA = new StyleMetaData[0];
  private static final GroupingHeader[] EMPTY_GROUPINGS = new GroupingHeader[0];

  private static final Object[] EMPTY_VALUES = new Object[0];

  private static class StyleDataBackend
  {
    private StyleMetaData[] metaData;
    private GroupingHeader[] groupings;
    private Element[] elements;

    private Object[] inheritValues;
    private Object[] fullValues;
    private Object[] expressionValues;
    private Object[] propertyEditors;

    private StyleDataBackend()
    {
      this.elements = EMPTY_ELEMENTS;
      this.metaData = EMPTY_METADATA;
      this.groupings = EMPTY_GROUPINGS;

      propertyEditors = EMPTY_VALUES;
      inheritValues = EMPTY_VALUES;
      fullValues = EMPTY_VALUES;
      expressionValues = EMPTY_VALUES;
    }

    private StyleDataBackend(final StyleMetaData[] metaData,
                             final GroupingHeader[] groupings,
                             final Element[] elements)
    {
      this.metaData = metaData;
      this.groupings = groupings;
      this.elements = elements;

      propertyEditors = new Object[this.metaData.length];
      inheritValues = new Object[this.metaData.length];
      fullValues = new Object[this.metaData.length];
      expressionValues = new Object[this.metaData.length];
    }

    public int getRowCount()
    {
      return metaData.length;
    }

    protected StyleMetaData getMetaData(final int row)
    {
      //noinspection ReturnOfCollectionOrArrayField, as this is for internal use only
      return metaData[row];
    }

    protected GroupingHeader getGroupings(final int row)
    {
      //noinspection ReturnOfCollectionOrArrayField, as this is for internal use only
      return groupings[row];
    }

    protected GroupingHeader[] getGroupings()
    {
      return groupings;
    }

    public void resetCache()
    {
      Arrays.fill(expressionValues, null);
      Arrays.fill(inheritValues, null);
      Arrays.fill(fullValues, null);
    }

    public Element[] getData()
    {
      return elements;
    }

    public void clearCache(final int rowIndex)
    {
      fullValues[rowIndex] = null;
      inheritValues[rowIndex] = null;
    }

    public void clearExpressionsCache(final int rowIndex)
    {
      expressionValues[rowIndex] = null;
    }

    public Object[] getFullValues()
    {
      return fullValues;
    }

    public Object[] getInheritValues()
    {
      return inheritValues;
    }

    public Object[] getExpressionValues()
    {
      return expressionValues;
    }

    public Object[] getPropertyEditors()
    {
      return propertyEditors;
    }
  }

  private TableStyle tableStyle;
  private StyleDataBackend dataBackend, oldDataBackend;
  private ExecutorService pool;
  private ReportRenderContext reportRenderContext;
  private static final String[] EXTRA_FIELDS = new String[0];

  public StyleTableModel()
  {
    tableStyle = TableStyle.GROUPED;
    this.dataBackend = new StyleDataBackend();
    pool = Executors.newSingleThreadExecutor();
  }

  public synchronized StyleDataBackend getDataBackend()
  {
    return dataBackend;
  }

  public synchronized void setDataBackend(final StyleDataBackend dataBackend)
  {
    oldDataBackend = this.dataBackend;
    this.dataBackend = dataBackend;
  }

  public int getRowCount()
  {
    return dataBackend.getRowCount();
  }

  protected StyleMetaData getMetaData(final int row)
  {
    return getDataBackend().getMetaData(row);
  }

  protected GroupingHeader getGroupings(final int row)
  {
    return getDataBackend().getGroupings(row);
  }

  public TableStyle getTableStyle()
  {
    return tableStyle;
  }

  public void setTableStyle(final TableStyle tableStyle)
  {
    if (tableStyle == null)
    {
      throw new NullPointerException();
    }
    this.tableStyle = tableStyle;
    pool.submit(new UpdateDataTask(getData()));
  }

  private class SameElementsUpdateDataTask implements Runnable
  {
    private StyleDataBackend dataBackend;

    private SameElementsUpdateDataTask(final StyleDataBackend elements)
    {
      this.dataBackend = elements;
    }

    public void run()
    {
      dataBackend.resetCache();
      try
      {
        if (SwingUtilities.isEventDispatchThread())
        {
          setDataBackend(dataBackend);
          fireTableDataChanged();
        }
        else
        {
          SwingUtilities.invokeAndWait(new NotifyChangeTask(dataBackend));
        }
      }
      catch (Exception e)
      {
        UncaughtExceptionsModel.getInstance().addException(e);
      }
    }
  }

  private class UpdateDataTask implements Runnable
  {
    private Element[] elements;

    private UpdateDataTask(final Element[] elements)
    {
      this.elements = elements.clone();
    }

    public void run()
    {
      try
      {
        final StyleDataBackend dataBackend = updateData(elements);
        if (SwingUtilities.isEventDispatchThread())
        {
          setDataBackend(dataBackend);
          fireTableDataChanged();
        }
        else
        {
          SwingUtilities.invokeAndWait(new NotifyChangeTask(dataBackend));
        }
      }
      catch (Exception e)
      {
        UncaughtExceptionsModel.getInstance().addException(e);
      }
    }
  }

  private class NotifyChangeTask implements Runnable
  {
    private StyleDataBackend dataBackend;

    private NotifyChangeTask(final StyleDataBackend dataBackend)
    {

      this.dataBackend = dataBackend;
    }

    public void run()
    {
      setDataBackend(dataBackend);
      fireTableDataChanged();
    }
  }

  protected StyleDataBackend updateData(final Element[] elements)
  {
    final StyleMetaData[] metaData = selectCommonAttributes(elements);
    if (tableStyle == TableStyle.ASCENDING)
    {
      Arrays.sort(metaData, new PlainMetaDataComparator());
      return (new StyleDataBackend(metaData, new GroupingHeader[metaData.length], elements));
    }
    else if (tableStyle == TableStyle.DESCENDING)
    {
      Arrays.sort(metaData, Collections.reverseOrder(new PlainMetaDataComparator()));
      return (new StyleDataBackend(metaData, new GroupingHeader[metaData.length], elements));
    }
    else
    {
      Arrays.sort(metaData, new GroupedMetaDataComparator());
      final Locale locale = Locale.getDefault();
      int groupCount = 0;
      if (metaData.length > 0)
      {
        String oldValue = null;

        for (int i = 0; i < metaData.length; i++)
        {
          final StyleMetaData data = metaData[i];
          if (data.isHidden())
          {
            continue;
          }

          if (groupCount == 0)
          {
            groupCount = 1;
            final StyleMetaData firstdata = metaData[i];
            oldValue = firstdata.getGrouping(locale);
            continue;
          }

          final String grouping = data.getGrouping(locale);
          if ((ObjectUtilities.equal(oldValue, grouping)) == false)
          {
            oldValue = grouping;
            groupCount += 1;
          }
        }
      }

      final StyleMetaData[] groupedMetaData = new StyleMetaData[metaData.length + groupCount];
      int targetIdx = 0;
      GroupingHeader[] groupings = new GroupingHeader[groupedMetaData.length];
      GroupingHeader group = null;
      for (int sourceIdx = 0; sourceIdx < metaData.length; sourceIdx++)
      {
        final StyleMetaData data = metaData[sourceIdx];
        if (data.isHidden())
        {
          continue;
        }

        if (sourceIdx == 0)
        {
          group = new GroupingHeader(data.getGrouping(locale));
          groupings[targetIdx] = group;
          targetIdx += 1;
        }
        else
        {
          final String newgroup = data.getGrouping(locale);
          if ((ObjectUtilities.equal(newgroup, group.getHeaderText())) == false)
          {
            group = new GroupingHeader(newgroup);
            groupings[targetIdx] = group;
            targetIdx += 1;
          }
        }

        groupings[targetIdx] = group;
        groupedMetaData[targetIdx] = data;
        targetIdx += 1;
      }

      if (oldDataBackend != null)
      {
        groupings = reconcileState(groupings, oldDataBackend.getGroupings());
      }


      return (new StyleDataBackend(groupedMetaData, groupings, elements));
    }
  }

  /**
   * Uses the name of the old groupings to set the collapse status of the new
   * groupings so that when a user makes a selection not all of the groups
   * return to the expanded state.  In essence makes group collapses "sticky"
   * where the group heading hasn't changed.
   *
   * @param groupings
   * @param oldGroupings
   */
  private GroupingHeader[] reconcileState(final GroupingHeader[] groupings,
                                          final GroupingHeader[] oldGroupings)
  {
    for (final GroupingHeader header : groupings)
    {
      final GroupingHeader oldHeader = findFirstOccuranceOfHeaderTitle(oldGroupings, header.getHeaderText());
      if (oldHeader != null)
      {
        header.setCollapsed(oldHeader.isCollapsed());
      }
    }
    return groupings;
  }

  private GroupingHeader findFirstOccuranceOfHeaderTitle(final GroupingHeader[] headerArray,
                                                         final String headerTitle)
  {
    for (final GroupingHeader header : headerArray)
    {
      if (header == null)
      {
        continue;
      }
      if (ObjectUtilities.equal(header.getHeaderText(), headerTitle))
      {
        return header;
      }
    }
    return null;
  }

  private static boolean isSameElements(final Element[] elements, final Element[] otherArray)
  {
    if (elements == otherArray)
    {
      return true;
    }
    if (elements.length != otherArray.length)
    {
      // that is easy!
      return false;
    }

    for (int i = 0; i < elements.length; i++)
    {
      final Element element = elements[i];
      if (otherArray[i].getObjectID() != element.getObjectID())
      {
        return false;
      }
    }
    return true;
  }

  private static StyleMetaData[] selectCommonAttributes(final Element[] elements)
  {
    final HashMap<String, Boolean> attributes = new HashMap<String, Boolean>();
    final ArrayList<StyleMetaData> selectedArrays = new ArrayList<StyleMetaData>();
    for (int elementIdx = 0; elementIdx < elements.length; elementIdx++)
    {
      final Element element = elements[elementIdx];
      final StyleMetaData[] datas = element.getMetaData().getStyleDescriptions();
      for (int styleIdx = 0; styleIdx < datas.length; styleIdx++)
      {
        final StyleMetaData data = datas[styleIdx];
        final String name = data.getName();

        if (data.isHidden())
        {
          attributes.put(name, Boolean.FALSE);
          continue;
        }

        final Object attribute = attributes.get(name);
        if (Boolean.TRUE.equals(attribute))
        {
          // fine, we already have a value for it.
        }
        else if (attribute == null)
        {
          // add it ..
          if (elementIdx == 0)
          {
            selectedArrays.add(data);
            attributes.put(name, Boolean.TRUE);
          }
          else
          {
            attributes.put(name, Boolean.FALSE);
          }
        }

      }
    }

    return selectedArrays.toArray(new StyleMetaData[selectedArrays.size()]);
  }


  public void setData(final Element[] elements)
  {
    if (isSameElements(elements, getData()))
    {
      pool.submit(new SameElementsUpdateDataTask(getDataBackend()));
      return;
    }

    pool.submit(new UpdateDataTask(elements));
  }

  public Element[] getData()
  {
    return getDataBackend().getData();
  }

  public int getColumnCount()
  {
    return 4;
  }

  public String getColumnName(final int column)
  {
    switch (column)
    {
      case 0:
        return Messages.getString("StyleTableModel.NameColumn");
      case 1:
        return Messages.getString("StyleTableModel.InheritColumn");
      case 2:
        return Messages.getString("StyleTableModel.ValueColumn");
      case 3:
        return Messages.getString("StyleTableModel.FormulaColumn");
      default:
        throw new IllegalArgumentException();
    }
  }

  public Object getValueAt(final int rowIndex, final int columnIndex)
  {
    final StyleMetaData metaData = getMetaData(rowIndex);
    if (metaData == null)
    {
      return getGroupings(rowIndex);
    }
    switch (columnIndex)
    {
      case 0:
        return new GroupedName(metaData.getDisplayName(Locale.getDefault()), metaData.getGrouping(Locale.getDefault()));
      case 1:
        return computeInheritValue(metaData, rowIndex);
      case 2:
        return computeFullValue(metaData, rowIndex);
      case 3:
        return computeExpressionValue(metaData, rowIndex);
      default:
        throw new IndexOutOfBoundsException();
    }
  }

  public boolean isCellEditable(final int rowIndex, final int columnIndex)
  {
    final StyleMetaData metaData = getMetaData(rowIndex);
    if (metaData == null)
    {
      return false;
    }

    switch (columnIndex)
    {
      case 0:
        return false;
      case 1:
        return true;
      case 2:
        return true;
      case 3:
        return true;
      default:
        throw new IndexOutOfBoundsException();
    }
  }


  public void setValueAt(final Object aValue, final int rowIndex, final int columnIndex)
  {
    final StyleMetaData metaData = getMetaData(rowIndex);
    if (metaData == null)
    {
      return;
    }

    switch (columnIndex)
    {
      case 0:
        return;
      case 1:
      {
        if (Boolean.TRUE.equals(aValue))
        {
          if (defineFullValue(metaData, null))
          {
            getDataBackend().clearCache(rowIndex);
            fireTableDataChanged();
          }
        }
        break;
      }
      case 2:
      {
        if (defineFullValue(metaData, aValue))
        {
          getDataBackend().clearCache(rowIndex);
          fireTableDataChanged();
        }
        break;
      }
      case 3:
      {
        if (aValue != null && aValue instanceof Expression == false)
        {
          return;
        }
        if (defineExpressionValue(metaData, (Expression) aValue))
        {
          getDataBackend().clearExpressionsCache(rowIndex);
          fireTableDataChanged();
        }
        break;
      }
      default:
        throw new IndexOutOfBoundsException();
    }
  }


  private boolean defineFullValue(final StyleMetaData metaData,
                                  final Object value)
  {
    if (value != null && metaData.getTargetType().isInstance(value) == false)
    {
      // not the correct type
      logger.warn("Invalid type: " + value + "(" + value.getClass() + ") but expected " +  // NON-NLS
          metaData.getTargetType());
      return false;
    }

    boolean changed = false;
    final Element[] elements = getDataBackend().getData();
    for (int i = 0; i < elements.length; i++)
    {
      final Element element = elements[i];
      final ElementStyleSheet styleSheet = element.getStyle();
      final Object attribute = styleSheet.getStyleProperty(metaData.getStyleKey());
      if ((ObjectUtilities.equal(attribute, value)) == false)
      {
        changed = true;
      }
    }

    if (changed)
    {
      final ReportRenderContext reportRenderContext = getReportRenderContext();
      if (reportRenderContext == null)
      {
        throw new IllegalStateException("No report render context? Thats bad.");
      }
      final UndoManager undo = reportRenderContext.getUndo();
      final ArrayList<UndoEntry> undos = new ArrayList<UndoEntry>();

      for (int i = 0; i < elements.length; i++)
      {
        final Element element = elements[i];
        final ElementStyleSheet styleSheet = element.getStyle();
        final Object attribute = styleSheet.getStyleProperty(metaData.getStyleKey());
        undos.add(new StyleEditUndoEntry
            (element.getObjectID(), metaData.getStyleKey(), attribute, value));
        styleSheet.setStyleProperty(metaData.getStyleKey(), value);
      }
      undo.addChange(Messages.getString("StyleChange"), new CompoundUndoEntry((UndoEntry[]) undos.toArray(new UndoEntry[undos.size()])));
    }
    return changed;
  }

  private Object computeFullValue(final StyleMetaData metaData,
                                  final int row)
  {
    final StyleDataBackend dataBackend1 = getDataBackend();
    final Object[] fullValues = dataBackend1.getFullValues();
    final Object o = fullValues[row];
    if (o == NULL_INDICATOR)
    {
      return null;
    }
    if (o != null)
    {
      return o;
    }

    Object lastElement = null;
    final Element[] elements = dataBackend1.getData();
    if (elements.length > 0)
    {
      final Element element = elements[0];
      final ElementStyleSheet styleSheet = element.getStyle();
      lastElement = styleSheet.getStyleProperty(metaData.getStyleKey());
    }
    if (lastElement != null)
    {
      fullValues[row] = lastElement;
    }
    else
    {
      fullValues[row] = NULL_INDICATOR;
    }

    return lastElement;
  }

  private Object computeInheritValue(final StyleMetaData metaData,
                                     final int rowIndex)
  {
    final StyleDataBackend dataBackend1 = getDataBackend();
    final Object[] inheritValues = dataBackend1.getInheritValues();
    final Object o = inheritValues[rowIndex];
    if (o == NULL_INDICATOR)
    {
      return null;
    }
    if (o != null)
    {
      return o;
    }

    boolean allLocalKeys = true;
    boolean allInheritedKeys = true;
    final Element[] elements = dataBackend1.getData();
    if (elements.length > 0)
    {
      final Element element = elements[0];
      final ElementStyleSheet styleSheet = element.getStyle();
      final boolean localKey = styleSheet.isLocalKey(metaData.getStyleKey());
      allLocalKeys = allLocalKeys & localKey;
      allInheritedKeys = (localKey == false);
    }
    final Object retval;
    if (allLocalKeys == true && allInheritedKeys == true)
    {
      retval = null;
    }
    else if (allInheritedKeys == true)
    {
      retval = Boolean.TRUE;
    }
    else if (allLocalKeys == true)
    {
      retval = Boolean.FALSE;
    }
    else
    {
      retval = null;
    }
    if (retval == null)
    {
      inheritValues[rowIndex] = NULL_INDICATOR;
    }
    else
    {
      inheritValues[rowIndex] = retval;
    }
    return retval;
  }

  private boolean defineExpressionValue(final StyleMetaData metaData,
                                        final Expression value)
  {
    boolean changed = false;
    final Element[] elements = getDataBackend().getData();
    for (int i = 0; i < elements.length; i++)
    {
      final Element element = elements[i];
      final Expression attribute = element.getStyleExpression(metaData.getStyleKey());
      if ((ObjectUtilities.equal(attribute, value)) == false)
      {
        changed = true;
      }
    }

    if (changed)
    {
      final ReportRenderContext reportRenderContext = getReportRenderContext();
      if (reportRenderContext == null)
      {
        throw new IllegalStateException("No report render context? Thats bad.");
      }
      final UndoManager undo = reportRenderContext.getUndo();
      final ArrayList<UndoEntry> undos = new ArrayList<UndoEntry>();

      for (int i = 0; i < elements.length; i++)
      {
        final Element element = elements[i];
        final Expression attribute = element.getStyleExpression(metaData.getStyleKey());
        if (value == null)
        {
          undos.add(new StyleExpressionEditUndoEntry
              (element.getObjectID(), metaData.getStyleKey(), attribute, null));
          element.setStyleExpression(metaData.getStyleKey(), null);
          element.notifyNodePropertiesChanged();
        }
        else
        {
          final Expression expression = value.getInstance();
          undos.add(new StyleExpressionEditUndoEntry
              (element.getObjectID(), metaData.getStyleKey(), attribute, expression));
          element.setStyleExpression(metaData.getStyleKey(), expression);
          element.notifyNodePropertiesChanged();
        }
      }
      undo.addChange(Messages.getString("StyleChange"), new CompoundUndoEntry((UndoEntry[]) undos.toArray(new UndoEntry[undos.size()])));

    }
    return changed;
  }

  private Expression computeExpressionValue(final StyleMetaData metaData,
                                            final int row)
  {
    final StyleDataBackend dataBackend1 = getDataBackend();
    final Object[] expressionValues = dataBackend1.getExpressionValues();
    final Object o = expressionValues[row];
    if (o == NULL_INDICATOR)
    {
      return null;
    }
    if (o != null)
    {
      return (Expression) o;
    }


    Expression lastElement = null;
    final Element[] elements = dataBackend1.getData();
    if (elements.length > 0)
    {
      final Element element = elements[0];
      lastElement = element.getStyleExpression(metaData.getStyleKey());
    }
    if (lastElement != null)
    {
      expressionValues[row] = lastElement;
    }
    else
    {
      expressionValues[row] = NULL_INDICATOR;
    }
    return lastElement;
  }

  public Class getClassForCell(final int rowIndex, final int columnIndex)
  {
    final StyleMetaData metaData = getMetaData(rowIndex);
    if (metaData == null)
    {
      return GroupingHeader.class;
    }

    switch (columnIndex)
    {
      case 0:
        return GroupedName.class;
      case 1:
        return Boolean.class;
      case 2:
        return metaData.getTargetType();
      case 3:
        return Expression.class;
      default:
        throw new IndexOutOfBoundsException();
    }
  }

  public PropertyEditor getEditorForCell(final int rowIndex, final int columnIndex)
  {
    final StyleMetaData metaData = getMetaData(rowIndex);
    if (metaData == null)
    {
      return null;
    }

    switch (columnIndex)
    {
      case 0:
        return null;
      case 1:
        return null;
      case 2:
        return computeEditor(metaData, rowIndex);
      case 3:
        return null;
      default:
        throw new IndexOutOfBoundsException();
    }
  }

  private PropertyEditor computeEditor(final StyleMetaData metaData,
                                       final int row)
  {
    final Object[] propertyEditors = getDataBackend().getPropertyEditors();
    final Object o = propertyEditors[row];
    if (o == NULL_INDICATOR)
    {
      return null;
    }
    if (o != null)
    {
      return (PropertyEditor) o;
    }

    PropertyEditor propertyEditor = metaData.getEditor();
    if (propertyEditor == null)
    {
      propertyEditor = getDefaultEditor(metaData.getTargetType());
    }
    if (propertyEditor == null)
    {
      propertyEditors[row] = NULL_INDICATOR;
    }
    else
    {
      propertyEditors[row] = propertyEditor;
    }
    return propertyEditor;
  }

  protected PropertyEditor getDefaultEditor(final Class type)
  {
    if (String.class.equals(type))
    {
      return null;
    }
    return FastPropertyEditorManager.findEditor(type);
  }

  public String getValueRole(final int row, final int column)
  {
    return AttributeMetaData.VALUEROLE_VALUE;
  }

  public String[] getExtraFields(final int row, final int column)
  {
    return EXTRA_FIELDS;
  }

  public GroupingHeader getGroupHeader(final int index)
  {
    return getGroupings(index);
  }

  public boolean isHeaderRow(final int index)
  {
    return dataBackend.getMetaData(index) == null;
  }

  public ReportRenderContext getReportRenderContext()
  {
    return reportRenderContext;
  }

  public void setReportRenderContext(final ReportRenderContext reportRenderContext)
  {
    this.reportRenderContext = reportRenderContext;
  }
}

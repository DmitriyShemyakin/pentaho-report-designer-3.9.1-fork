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

package org.pentaho.reporting.ui.datasources.table;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeContext;
import org.pentaho.reporting.engine.classic.core.util.TypedTableModel;
import org.pentaho.reporting.libraries.base.util.FilesystemFilter;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.designtime.swing.BorderlessButton;
import org.pentaho.reporting.libraries.designtime.swing.CommonDialog;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;
import org.pentaho.reporting.libraries.designtime.swing.background.BackgroundCancellableProcessHelper;
import org.pentaho.reporting.libraries.designtime.swing.filechooser.CommonFileChooser;
import org.pentaho.reporting.libraries.designtime.swing.filechooser.FileChooserService;

/**
 * @author Ezequiel Cuellar
 */
public class TableDataSourceEditor extends CommonDialog
{
  private class ImportAction extends AbstractAction
  {
    /**
     * Defines an <code>Action</code> object with a default
     * description string and default icon.
     */
    private ImportAction()
    {
      //putValue(Action.NAME, "Import");
      setEnabled(false);
      final URL resource = TableDataSourceEditor.class.getResource
          ("/org/pentaho/reporting/ui/datasources/table/resources/Spreadsheet.png"); // NON-NLS
      if (resource != null)
      {
        putValue(Action.SMALL_ICON, new ImageIcon(resource));
      }
      putValue(Action.NAME, Messages.getString("TableDataSourceEditor.ImportSpreadsheet.Name"));
      putValue(Action.SHORT_DESCRIPTION, Messages.getString("TableDataSourceEditor.ImportSpreadsheet.Description"));
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(final ActionEvent e)
    {
      final FileFilter[] fileFilters =
          {new FilesystemFilter(new String[]{".xls", ".xlsx"}, // NON-NLS
              Messages.getString("TableDataSourceEditor.ExcelFileDescription"), true)};
      final CommonFileChooser fileChooser = FileChooserService.getInstance().getFileChooser("xls");

      fileChooser.setFilters(fileFilters);
      if (fileChooser.showDialog(TableDataSourceEditor.this, JFileChooser.OPEN_DIALOG) == false)
      {
        return;
      }

      final File file = fileChooser.getSelectedFile();
      final ImportFromFileTask importFromFileTask =
          new ImportFromFileTask(file, useFirstRowAsHeader.isSelected(), TableDataSourceEditor.this);
      final Thread workerThread = new Thread(importFromFileTask);
      workerThread.setName("PRD-import-table-data-task"); // NON-NLS
      BackgroundCancellableProcessHelper.executeProcessWithCancelDialog
          (workerThread, importFromFileTask, TableDataSourceEditor.this);
    }
  }

  private class AddRowAction extends AbstractAction
  {
    /**
     * Defines an <code>Action</code> object with a default
     * description string and default icon.
     */
    private AddRowAction()
    {
      setEnabled(false);
      final URL location = TableDataSourceEditor.class.getResource
          ("/org/pentaho/reporting/ui/datasources/table/resources/AddRow.png"); // NON-NLS
      if (location != null)
      {
        putValue(Action.SMALL_ICON, new ImageIcon(location));
      }
      else
      {
        putValue(Action.NAME, Messages.getString("TableDataSourceEditor.AddRow.Name"));
      }
      putValue(Action.SHORT_DESCRIPTION, Messages.getString("TableDataSourceEditor.AddRow.Description"));
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(final ActionEvent e)
    {
      table.addRow();
      removeRowAction.setEnabled(true);
    }
  }

  private class AddColumnAction extends AbstractAction
  {
    /**
     * Defines an <code>Action</code> object with a default
     * description string and default icon.
     */
    private AddColumnAction()
    {
      setEnabled(false);
      final URL location = TableDataSourceEditor.class.getResource
          ("/org/pentaho/reporting/ui/datasources/table/resources/AddColumn.png"); // NON-NLS
      if (location != null)
      {
        putValue(Action.SMALL_ICON, new ImageIcon(location));
      }
      else
      {
        putValue(Action.NAME, Messages.getString("TableDataSourceEditor.AddColumn.Name"));
      }
      putValue(Action.SHORT_DESCRIPTION, Messages.getString("TableDataSourceEditor.AddColumn.Description"));
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(final ActionEvent e)
    {
      table.addColumn(" ");
      updateComponents();
    }
  }

  private class RemoveColumnAction extends AbstractAction
  {
    /**
     * Defines an <code>Action</code> object with a default
     * description string and default icon.
     */
    private RemoveColumnAction()
    {
      setEnabled(false);
      final URL location = TableDataSourceEditor.class.getResource
          ("/org/pentaho/reporting/ui/datasources/table/resources/RemoveColumn.png"); // NON-NLS
      if (location != null)
      {
        putValue(Action.SMALL_ICON, new ImageIcon(location));
      }
      else
      {
        putValue(Action.NAME, Messages.getString("TableDataSourceEditor.RemoveColumn.Name"));
      }
      putValue(Action.SHORT_DESCRIPTION, Messages.getString("TableDataSourceEditor.RemoveColumn.Description"));
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(final ActionEvent e)
    {
      table.removeColumn();
      setEnabled(false);
    }
  }

  private class RemoveRowAction extends AbstractAction
  {
    /**
     * Defines an <code>Action</code> object with a default
     * description string and default icon.
     */
    private RemoveRowAction()
    {
      setEnabled(false);
      final URL location = TableDataSourceEditor.class.getResource
          ("/org/pentaho/reporting/ui/datasources/table/resources/RemoveRow.png"); // NON-NLS
      if (location != null)
      {
        putValue(Action.SMALL_ICON, new ImageIcon(location));
      }
      else
      {
        putValue(Action.NAME, Messages.getString("TableDataSourceEditor.RemoveRow.Name"));
      }
      putValue(Action.SHORT_DESCRIPTION, Messages.getString("TableDataSourceEditor.RemoveRow.Description"));
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(final ActionEvent e)
    {
      table.removeRow();
      setEnabled(false);
    }
  }

  private class QueryNameListSelectionListener implements ListSelectionListener
  {
    public void valueChanged(final ListSelectionEvent e)
    {
      if (!inQueryNameUpdate)
      {
        final DataSetQuery query = (DataSetQuery) queryNameList.getSelectedValue();
        if (query != null)
        {
          queryNameTextField.setText(query.getQueryName());
          table.setTableEditorModel(query.getQuery());
          updateComponents();
        }
        else
        {
          queryNameTextField.setText("");
          table.setTableEditorModel(null);
          updateComponents();
        }
      }
    }
  }

  private static class QueryNameListCellRenderer extends DefaultListCellRenderer
  {
    public Component getListCellRendererComponent(final JList list,
                                                  final Object value,
                                                  final int index,
                                                  final boolean isSelected,
                                                  final boolean cellHasFocus)
    {
      final JLabel listCellRendererComponent = (JLabel) super.getListCellRendererComponent(list, value, index,
          isSelected, cellHasFocus);
      if (value != null)
      {
        final String queryName = ((DataSetQuery) value).getQueryName();
        if (!StringUtils.isEmpty(queryName, false))
        {
          listCellRendererComponent.setText(queryName);
        }
        else
        {
          listCellRendererComponent.setText(" ");
        }
      }
      return listCellRendererComponent;
    }
  }

  private class TableSelectionHandler implements ListSelectionListener
  {
    private TableSelectionHandler()
    {
    }

    public void valueChanged(final ListSelectionEvent e)
    {
      if (selectingHeaderColumn)
      {
        return;
      }

      final EditableHeader editableHeader = (EditableHeader) table.getTableHeader();

      removeColumnAction.setEnabled(false);
      removeRowAction.setEnabled(table.getSelectedRowCount() > 0 && table.getRowCount() > 1);

      editableHeader.editingStopped(null);
      editableHeader.removeEditor();
    }
  }

  private class TableHeaderDataSourceMouseListener extends MouseAdapter
  {
    private TableHeaderDataSourceMouseListener()
    {
    }

    public void mousePressed(final MouseEvent event)
    {
      final Object eventSource = event.getSource();
      if (eventSource instanceof JTableHeader == false)
      {
        return;
      }
      final JTableHeader tableHeader = (JTableHeader) eventSource;

      removeColumnAction.setEnabled(table.getColumnCount() > 1);
      removeRowAction.setEnabled(false);

      final TableCellEditor theTableCellEditor = table.getCellEditor();
      if (theTableCellEditor != null)
      {
        table.getCellEditor().stopCellEditing();
      }

      selectColumn(tableHeader, event.getPoint());
    }

    private void selectColumn(final JTableHeader aTableHeader, final Point aPoint)
    {
      try
      {
        selectingHeaderColumn = true;

        final TableColumnModel columnModel = table.getColumnModel();
        final int columnIndex = aTableHeader.columnAtPoint(aPoint);
        if (columnIndex <= 0)
        {
          return;
        }

        final TableColumn tableColumn = columnModel.getColumn(columnIndex);

        table.clearSelection();
        table.setColumnSelectionInterval(columnIndex, columnIndex);
        table.setSelectedColumn(tableColumn);
        if (table.getRowCount() > 0)
        {
          table.addRowSelectionInterval(0, table.getRowCount() - 1);
        }
      }
      finally
      {
        selectingHeaderColumn = false;
      }
    }
  }

  private class TableAddEmptyRowAtEndHandler extends KeyAdapter
  {
    private TableAddEmptyRowAtEndHandler()
    {
    }

    public void keyTyped(final KeyEvent aEvt)
    {
      final int key = aEvt.getKeyCode();
      if (key == KeyEvent.VK_TAB)
      {
        if (table.getSelectedColumn() == (table.getColumnCount() - 1))
        {
          if (table.getSelectedRow() == (table.getRowCount() - 1))
          {
            table.addRow();
          }
        }
      }
    }
  }


  private class QueryAddAction extends AbstractAction
  {
    private QueryAddAction()
    {
      final URL location = TableDataSourceEditor.class.getResource
          ("/org/pentaho/reporting/ui/datasources/table/resources/Add.png"); // NON-NLS
      if (location != null)
      {
        putValue(Action.SMALL_ICON, new ImageIcon(location));
      }
      else
      {
        putValue(Action.NAME, Messages.getString("TableDataSourceEditor.AddQuery.Name"));
      }
      putValue(Action.SHORT_DESCRIPTION, Messages.getString("TableDataSourceEditor.AddQuery.Description"));
    }

    public void actionPerformed(final ActionEvent e)
    {
      // Find a unique query name
      String queryName = Messages.getString("TableDataSourceEditor.Query");
      for (int i = 1; i < 1000; ++i)
      {
        final String newQueryName = Messages.getString("TableDataSourceEditor.Query") + " " + i;
        if (!queries.containsKey(newQueryName))
        {
          queryName = newQueryName;
          break;
        }
      }

      final TypedTableModel defaultTableModel = new TypedTableModel();
      defaultTableModel.addColumn(Messages.getString("TableDataSourceEditor.IDColumn"), String.class);
      defaultTableModel.addColumn(Messages.getString("TableDataSourceEditor.ValueColumn"), String.class);
      final DataSetQuery newQuery = new DataSetQuery(queryName, defaultTableModel);
      queries.put(newQuery.getQueryName(), newQuery);

      inModifyingQueryNameList = true;
      updateQueryList();
      queryNameList.setSelectedValue(newQuery, true);
      inModifyingQueryNameList = false;
      updateComponents();
      table.addRow();
    }
  }

  private class QueryRemoveActionListener extends AbstractAction implements ListSelectionListener
  {
    private QueryRemoveActionListener()
    {
      final URL location = TableDataSourceEditor.class.getResource
          ("/org/pentaho/reporting/ui/datasources/table/resources/Remove.png"); // NON-NLS
      if (location != null)
      {
        putValue(Action.SMALL_ICON, new ImageIcon(location));
      }
      else
      {
        putValue(Action.NAME, Messages.getString("TableDataSourceEditor.RemoveQuery.Name"));
      }
      putValue(Action.SHORT_DESCRIPTION, Messages.getString("TableDataSourceEditor.RemoveQuery.Description"));
      setEnabled(false);
    }

    public void actionPerformed(final ActionEvent e)
    {
      final DataSetQuery query = (DataSetQuery) queryNameList.getSelectedValue();
      if (query != null)
      {
        queries.remove(query.getQueryName());
      }

      inModifyingQueryNameList = true;
      updateQueryList();
      queryNameList.clearSelection();
      inModifyingQueryNameList = false;
      updateComponents();
    }

    public void valueChanged(final ListSelectionEvent e)
    {
      setEnabled(queryNameList.isSelectionEmpty() == false);
    }
  }

  private class QueryNameTextFieldDocumentListener implements DocumentListener
  {
    public void insertUpdate(final DocumentEvent e)
    {
      update();
    }

    public void removeUpdate(final DocumentEvent e)
    {
      update();
    }

    public void changedUpdate(final DocumentEvent e)
    {
      update();
    }

    private void update()
    {
      if (inModifyingQueryNameList)
      {
        return;
      }

      final String queryName = queryNameTextField.getText();
      final DataSetQuery currentQuery = (DataSetQuery) queryNameList.getSelectedValue();
      if (currentQuery == null)
      {
        return;
      }

      if (queryName.equals(currentQuery.getQueryName()))
      {
        return;
      }

      if (queries.containsKey(queryName))
      {
        return;
      }

      inQueryNameUpdate = true;
      queries.remove(currentQuery.getQueryName());
      currentQuery.setQueryName(queryName);
      queries.put(currentQuery.getQueryName(), currentQuery);
      updateQueryList();
      queryNameList.setSelectedValue(currentQuery, true);
      inQueryNameUpdate = false;
    }
  }

  private class TableUpdateHandler implements TableModelListener
  {
    private TableUpdateHandler()
    {
    }

    /**
     * This fine grain notification tells listeners the exact range
     * of cells, rows, or columns that changed.
     */
    public void tableChanged(final TableModelEvent e)
    {
      if (!StringUtils.isEmpty(queryNameTextField.getText()))
      {
        queries.put(queryNameTextField.getText(),
            new DataSetQuery(queryNameTextField.getText(), table.getTableEditorModel()));
      }
    }
  }

  private JTextField queryNameTextField;
  private TableEditor table;
  private JList queryNameList;

  private Map<String, DataSetQuery> queries;
  private boolean inQueryNameUpdate;
  private boolean inModifyingQueryNameList;
  private JCheckBox useFirstRowAsHeader;
  private AddRowAction addRowAction;
  private AddColumnAction addColumnAction;
  private RemoveRowAction removeRowAction;
  private RemoveColumnAction removeColumnAction;
  private ImportAction importAction;
  private DesignTimeContext designTimeContext;

  private boolean selectingHeaderColumn;

  public TableDataSourceEditor(final Dialog aOwner)
  {
    super(aOwner);
    init();
  }

  public TableDataSourceEditor(final Frame aOwner)
  {
    super(aOwner);
    init();
  }

  /**
   * Creates a non-modal dialog without a title and without a specified
   * <code>Frame</code> owner.  A shared, hidden frame will be
   * set as the owner of the dialog.
   * <p/>
   * This constructor sets the component's locale property to the value
   * returned by <code>JComponent.getDefaultLocale</code>.
   *
   * @throws java.awt.HeadlessException if GraphicsEnvironment.isHeadless()
   *                                    returns true.
   * @see java.awt.GraphicsEnvironment#isHeadless
   * @see javax.swing.JComponent#getDefaultLocale
   */
  public TableDataSourceEditor()
  {
    init();
  }

  protected void init()
  {
    queries = new LinkedHashMap<String, DataSetQuery>();

    queryNameList = new JList();
    queryNameList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    queryNameList.setVisibleRowCount(5);
    queryNameList.addListSelectionListener(new QueryNameListSelectionListener());
    queryNameList.setCellRenderer(new QueryNameListCellRenderer());

    queryNameTextField = new JTextField(null, 0);
    queryNameTextField.setColumns(35);
    queryNameTextField.getDocument().addDocumentListener(new QueryNameTextFieldDocumentListener());
    queryNameTextField.setEnabled(false);

    table = new TableEditor();
    table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
    table.addKeyListener(new TableAddEmptyRowAtEndHandler());
    table.getSelectionModel().addListSelectionListener(new TableSelectionHandler());
    table.getTableHeader().addMouseListener(new TableHeaderDataSourceMouseListener());
    table.getModel().addTableModelListener(new TableUpdateHandler());
    table.setColumnSelectionAllowed(true);

    addRowAction = new AddRowAction();
    addColumnAction = new AddColumnAction();
    removeRowAction = new RemoveRowAction();
    removeColumnAction = new RemoveColumnAction();

    importAction = new ImportAction();

    useFirstRowAsHeader = new JCheckBox(Messages.getString("TableDataSourceEditor.UseFirstRowAsHeader"));
    useFirstRowAsHeader.setEnabled(false);
    useFirstRowAsHeader.setSelected(true);

    setTitle(Messages.getString("TableDataSourceEditor.Title"));

    super.init();

    pack();
    LibSwingUtil.centerDialogInParent(this);
  }

  protected Component createContentPane()
  {

    final JPanel namePanel = new JPanel(new BorderLayout());
    namePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 0));
    namePanel.add(BorderLayout.NORTH, new JLabel(Messages.getString("TableDataSourceEditor.QueryName")));
    namePanel.add(BorderLayout.CENTER, queryNameTextField);

    final JPanel theToolBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    theToolBar.add(new BorderlessButton(addRowAction));
    theToolBar.add(new BorderlessButton(addColumnAction));
    theToolBar.add(new BorderlessButton(removeRowAction));
    theToolBar.add(new BorderlessButton(removeColumnAction));

    final JPanel tableCarrierPane = new JPanel(new BorderLayout());
    tableCarrierPane.add(theToolBar, BorderLayout.NORTH);
    tableCarrierPane.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
    tableCarrierPane.add(new JScrollPane(table), BorderLayout.CENTER);

    final JPanel leftButtonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
    leftButtonsPanel.add(useFirstRowAsHeader);
    leftButtonsPanel.add(new JButton(importAction));

    final JPanel buttonsPanel = new JPanel(new BorderLayout());
    buttonsPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));
    buttonsPanel.add(leftButtonsPanel, BorderLayout.WEST);

    final JPanel queryConfigPane = new JPanel(new BorderLayout());
    queryConfigPane.add(createQuerySelectionPanel(), BorderLayout.NORTH);
    queryConfigPane.add(namePanel, BorderLayout.CENTER);

    final JPanel contentPane = new JPanel();
    contentPane.setLayout(new BorderLayout());
    contentPane.add(queryConfigPane, BorderLayout.NORTH);
    contentPane.add(tableCarrierPane, BorderLayout.CENTER);
    contentPane.add(buttonsPanel, BorderLayout.SOUTH);
    return contentPane;
  }

  private JPanel createQuerySelectionPanel()
  {
    final QueryRemoveActionListener removeQueryAction = new QueryRemoveActionListener();
    queryNameList.addListSelectionListener(removeQueryAction);

    final JPanel queryListButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    queryListButtonsPanel.add(new BorderlessButton(new QueryAddAction()));
    queryListButtonsPanel.add(new BorderlessButton(removeQueryAction));


    final JPanel queryListDetailsPanel = new JPanel(new BorderLayout());
    queryListDetailsPanel.add(new JLabel(Messages.getString("TableDataSourceEditor.QueryDetailsLabel")), BorderLayout.WEST);
    queryListDetailsPanel.add(queryListButtonsPanel, BorderLayout.EAST);

    // Create the query list panel
    final JPanel queryListPanel = new JPanel(new BorderLayout());
    queryListPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
    queryListPanel.add(BorderLayout.NORTH, queryListDetailsPanel);
    queryListPanel.add(BorderLayout.CENTER, new JScrollPane(queryNameList));
    return queryListPanel;
  }

  public TableDataFactory performConfiguration(final DesignTimeContext designTimeContext,
                                               final TableDataFactory dataFactory,
                                               final String selectedQuery)
  {
    if (designTimeContext == null)
    {
      throw new NullPointerException();
    }

    this.designTimeContext = designTimeContext;
    this.table.applyLocaleSettings(designTimeContext.getLocaleSettings());

    if (dataFactory != null)
    {
      final String[] queryNames = dataFactory.getQueryNames();
      for (int i = 0; i < queryNames.length; i++)
      {
        final String queryName = queryNames[i];
        final TableModel query = dataFactory.getTable(queryName);
        queries.put(queryName, new DataSetQuery(queryName, query));
      }
      updateQueryList();
    }

    setSelectedQuery(selectedQuery);
    if (performEdit() == false)
    {
      return null;
    }

    table.stopEditing();

    final TableDataFactory retval = new TableDataFactory();
    final DataSetQuery[] queries = this.queries.values().toArray(new DataSetQuery[this.queries.size()]);
    for (int i = 0; i < queries.length; i++)
    {
      final DataSetQuery query = queries[i];
      retval.addTable(query.getQueryName(), query.getQuery());
    }
    return retval;
  }

  protected void updateQueryList()
  {
    queryNameList.removeAll();
    queryNameList.setListData(queries.values().toArray(new DataSetQuery[queries.size()]));
  }

  private void setSelectedQuery(final String aQuery)
  {
    final ListModel theModel = queryNameList.getModel();
    for (int i = 0; i < theModel.getSize(); i++)
    {
      final DataSetQuery theDataSet = (DataSetQuery) theModel.getElementAt(i);
      if (theDataSet.getQueryName().equals(aQuery))
      {
        queryNameList.setSelectedValue(theDataSet, true);
        break;
      }
    }
  }

  protected void updateComponents()
  {
    final boolean querySelected = queryNameList.getSelectedIndex() != -1;
    final boolean hasQueries = queryNameList.getModel().getSize() > 0;

    queryNameTextField.setEnabled(querySelected);
    table.setEnabled(querySelected);

    getConfirmAction().setEnabled(hasQueries);
    addRowAction.setEnabled(querySelected);
    addColumnAction.setEnabled(querySelected);
    importAction.setEnabled(querySelected);
    useFirstRowAsHeader.setEnabled(querySelected);

    removeRowAction.setEnabled(table.getSelectedRow() != -1);
    removeColumnAction.setEnabled(table.getSelectedColumn() != -1);
  }

  public void importComplete(final TypedTableModel tableModel)
  {
    table.setTableEditorModel(tableModel);
  }

  public void importFailed(final Exception e)
  {
    designTimeContext.error(e);
  }
}

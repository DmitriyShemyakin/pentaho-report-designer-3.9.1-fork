package org.pentaho.reporting.ui.datasources.openerp;

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
 * Copyright (c) 2011 - 2012 De Bortoli Wines Pty Limited (Australia). All Rights Reserved.
 */

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.table.TableModel;


import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.DefaultReportEnvironment;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.ReportEnvironment;
import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeContext;
import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeUtil;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.ExceptionDialog;
import org.pentaho.reporting.engine.classic.core.util.ReportParameterValues;
import org.pentaho.reporting.engine.classic.extensions.datasources.openerp.OpenERPDataFactory;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.designtime.swing.CommonDialog;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;
import org.pentaho.reporting.libraries.designtime.swing.background.CancelEvent;
import org.pentaho.reporting.libraries.designtime.swing.background.DataPreviewDialog;
import org.pentaho.reporting.libraries.designtime.swing.background.PreviewWorker;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;

import com.debortoliwines.openerp.reporting.di.OpenERPConfiguration;
import com.debortoliwines.openerp.reporting.di.OpenERPFieldInfo;
import com.debortoliwines.openerp.reporting.ui.OpenERPPanel;

/**
 * 
 * @author Pieter van der Merwe
 *
 */
public class OpenERPDataSourceEditor extends CommonDialog
{
  private static final long serialVersionUID = 6685784298385723490L;
  
  private DesignTimeContext context;
  private OpenERPPanel mainPanel;
  
  private JTextField txtQueryName;
  
  public OpenERPDataSourceEditor(final DesignTimeContext context)
  {
    init(context);
  }

  public OpenERPDataSourceEditor(final DesignTimeContext context, final Frame owner)
      throws HeadlessException
  {
    super(owner);
    init(context);
  }

  public OpenERPDataSourceEditor(final DesignTimeContext context, final Dialog owner)
      throws HeadlessException
  {
    super(owner);
    init(context);
  }

  private void init(final DesignTimeContext context)
  {
    this.context = context;

    super.init();

    LibSwingUtil.centerDialogInParent(this);
  }

  protected Component createContentPane()
  {
    mainPanel = new OpenERPPanel();
    URL location = OpenERPDataSourceEditor.class.getResource("/org/pentaho/reporting/ui/datasources/openerp/resources/Add.png");
    if (location != null){
      mainPanel.setFilterAddButtonIcon(new ImageIcon(location));
    }
    
    location = OpenERPDataSourceEditor.class.getResource("/org/pentaho/reporting/ui/datasources/openerp/resources/Remove.png");
    if (location != null){
      mainPanel.setFilterRemoveButtonIcon(new ImageIcon(location));
    }
    
    txtQueryName = new JTextField(20);
    txtQueryName.setText("Query1");
    
    JPanel queryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    queryPanel.add(new JLabel("Query Name:"));
    queryPanel.add(txtQueryName);
    
    JPanel cpanel = new JPanel();
    cpanel.setLayout(new BorderLayout());
    cpanel.add(queryPanel, BorderLayout.NORTH);
    cpanel.add(mainPanel, BorderLayout.CENTER);
    cpanel.add(new JButton(new PreviewAction()), BorderLayout.SOUTH);
    
    return cpanel;
  }
  
  @Override
  protected boolean validateInputs(boolean onConfirm) {
    if (txtQueryName.getText().length() == 0){
      ExceptionDialog.showExceptionDialog(this, "Error", "Query Name is mandatory", null);
      return false;
    }
    
    ArrayList<OpenERPFieldInfo> selectedFields = mainPanel.getConfiguration().getSelectedFields();
    if (selectedFields != null){
      ArrayList<String> fieldNames = new ArrayList<String>();
      for (OpenERPFieldInfo fld : selectedFields){
        if (fieldNames.indexOf(fld.getRenamedFieldName()) >= 0){
          ExceptionDialog.showExceptionDialog(this, "Error", "Selected field name '" + fld.getRenamedFieldName() + "' is not unique.", null);
          return false;
        }
        fieldNames.add(fld.getRenamedFieldName());
      }
    }
    
    return true;
  }

  public DataFactory performConfiguration(final OpenERPDataFactory input)
  {
    if (input != null)
    {
        txtQueryName.setText(input.getQueryName());
        mainPanel.setConfiguration(input.getConfig());
    }

    if (performEdit() == false)
    {
      return null;
    }
    
    return produceDataFactory();
  }
  
  private OpenERPDataFactory produceDataFactory()
  {
	  
    final OpenERPDataFactory dataFactory = new OpenERPDataFactory();
    final OpenERPConfiguration config = mainPanel.getConfiguration();
    dataFactory.setQueryName(txtQueryName.getText());
    dataFactory.setConfig(config);
    return dataFactory;
  }
  
  private class PreviewAction extends AbstractAction
  {
    private static final long serialVersionUID = 4093248389910254252L;

    private PreviewAction()
    {
      putValue(Action.NAME, "Preview");
    }

    public void actionPerformed(final ActionEvent aEvt)
    {
      try
      {
        final OpenERPDataFactory dataFactory = produceDataFactory();
        final AbstractReportDefinition report = context.getReport();
        final MasterReport masterReport = DesignTimeUtil.getMasterReport(report);
        final Configuration configuration;
        final ResourceKey contentBase;
        final ReportEnvironment reportEnvironment;
        if (masterReport == null)
        {
          contentBase = null;
          configuration = ClassicEngineBoot.getInstance().getGlobalConfig();
          reportEnvironment = new DefaultReportEnvironment(configuration);
        }
        else
        {
          contentBase = masterReport.getContentBase();
          configuration = masterReport.getConfiguration();
          reportEnvironment = masterReport.getReportEnvironment();
        }
        dataFactory.initialize(configuration,
            report.getResourceManager(), contentBase, MasterReport.computeAndInitResourceBundleFactory
            (report.getResourceBundleFactory(), reportEnvironment));

        final DataPreviewDialog previewDialog = new DataPreviewDialog(OpenERPDataSourceEditor.this);

        final OpenERPPreviewWorker worker = new OpenERPPreviewWorker(dataFactory);
        previewDialog.showData(worker);

        final ReportDataFactoryException factoryException = worker.getException();
        if (factoryException != null)
        {
          ExceptionDialog.showExceptionDialog(OpenERPDataSourceEditor.this, "Error",
              "An Error Occured during preview", factoryException);
        }
      }
      catch (Exception e)
      {
        ExceptionDialog.showExceptionDialog(OpenERPDataSourceEditor.this, "Error",
            "An Error Occured during preview", e);
      }
    }
  }


  private static class OpenERPPreviewWorker implements PreviewWorker
  {
    private OpenERPDataFactory dataFactory;
    private TableModel resultTableModel;
    private ReportDataFactoryException exception;
    private String query;

    private OpenERPPreviewWorker(final OpenERPDataFactory dataFactory)
    {
      if (dataFactory == null)
      {
        throw new NullPointerException();
      }
      this.dataFactory = dataFactory;
    }

    public ReportDataFactoryException getException()
    {
      return exception;
    }

    public TableModel getResultTableModel()
    {
      return resultTableModel;
    }

    public void close()
    {
    }

    /**
     * Requests that the thread stop processing as soon as possible.
     */
    public void cancelProcessing(final CancelEvent event)
    {
      dataFactory.cancelRunningQuery();
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p/>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    public void run()
    {
      try
      {
        resultTableModel = dataFactory.queryData(query, new ReportParameterValues());
        dataFactory.close();
      }
      catch (ReportDataFactoryException e)
      {
        exception = e;
      }
    }
  }
}

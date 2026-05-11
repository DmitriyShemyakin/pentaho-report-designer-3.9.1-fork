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

package org.pentaho.reporting.ui.datasources.jdbc.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.Locale;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import nickyb.sqleonardo.querybuilder.QueryBuilder;
import nickyb.sqleonardo.querybuilder.QueryModel;
import nickyb.sqleonardo.querybuilder.syntax.SQLParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeContext;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql.ConnectionProvider;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql.SimpleSQLReportDataFactory;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.ResourceBundleSupport;
import org.pentaho.reporting.libraries.designtime.swing.background.DataPreviewDialog;
import org.pentaho.reporting.ui.datasources.jdbc.JdbcDataSourceModule;

/**
 * @author David Kincade
 */
public class JdbcQueryDesignerDialog extends JDialog
{

  private class OKButtonActionListener extends AbstractAction
  {
    private OKButtonActionListener()
    {
      putValue(Action.NAME, getBundleSupport().getString("Button.ok"));
    }

    public void actionPerformed(final ActionEvent arg0)
    {
      setConfirmed(true);
      setVisible(false);
      dispose();
    }
  }

  private class CancelButtonActionListener extends AbstractAction
  {
    private CancelButtonActionListener()
    {
      putValue(Action.NAME, getBundleSupport().getString("Button.cancel"));
    }

    public void actionPerformed(final ActionEvent arg0)
    {
      setConfirmed(false);
      setVisible(false);
      dispose();
    }
  }

  private class PreviewButtonActionListener extends AbstractAction
  {

    private PreviewButtonActionListener()
    {
      putValue(Action.NAME, getBundleSupport().getString("JdbcDataSourceDialog.Preview"));
    }

    public void actionPerformed(final ActionEvent arg0)
    {
      try
      {
        final String query = getQuery();
        final DataPreviewDialog dialog = new DataPreviewDialog(JdbcQueryDesignerDialog.this);
        dialog.showData(new JdbcPreviewWorker(new SimpleSQLReportDataFactory(getConnectionDefinition()), query, 0, 0));
      }
      catch (Exception e)
      {
        log.warn("QueryPanel.actionPerformed ", e);
        if (designTimeContext != null)
        {
          designTimeContext.userError(e);
        }
      }
    }
  }

  private static final Log log = LogFactory.getLog(JdbcQueryDesignerDialog.class);
  private QueryBuilder queryBuilder;
  private boolean confirmed;
  private ConnectionProvider connectionProvider;
  private ResourceBundleSupport bundleSupport;
  private DesignTimeContext designTimeContext;

  public JdbcQueryDesignerDialog(final JDialog owner, final QueryBuilder queryBuilder)
  {
    super(owner);
    setModal(true);
    bundleSupport = new ResourceBundleSupport(Locale.getDefault(), JdbcDataSourceModule.MESSAGES,
        ObjectUtilities.getClassLoader(JdbcDataSourceModule.class));
    setTitle(bundleSupport.getString("JdbcDataSourceDialog.SQLLeonardoTitle"));
    this.queryBuilder = queryBuilder;

    setLayout(new BorderLayout());
    add(queryBuilder, BorderLayout.CENTER);

    final JPanel buttonPanel = new JPanel();
    buttonPanel.add(new JButton(new PreviewButtonActionListener()));
    buttonPanel.add(new JButton(new OKButtonActionListener()));
    buttonPanel.add(new JButton(new CancelButtonActionListener()));
    add(buttonPanel, BorderLayout.SOUTH);
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    setSize(800, 600);
    setLocationRelativeTo(getParent());
  }

  public String designQuery(final DesignTimeContext designTimeContext,
                            final ConnectionProvider jndiSource,
                            final String schema, final String query)
  {
    this.designTimeContext = designTimeContext;
    this.confirmed = false;
    this.connectionProvider = jndiSource;

    try
    {
      final QueryModel queryModel = SQLParser.toQueryModel(query);
      queryBuilder.setQueryModel(queryModel);
    }
    catch (Exception e1)
    {
      log.warn("QueryPanel.actionPerformed ", e1);
    }

    try
    {
      if (schema != null)
      {
        final QueryModel qm = queryBuilder.getQueryModel();
        qm.setSchema(schema);
        queryBuilder.setQueryModel(qm);
      }
    }
    catch (Exception e1)
    {
      log.warn("QueryPanel.actionPerformed ", e1);
    }

    setVisible(true);

    return (confirmed ? getQuery() : null);
  }

  protected ConnectionProvider getConnectionDefinition()
  {
    return connectionProvider;
  }

  protected String getQuery()
  {
    return queryBuilder.getQueryModel().toString(true);
  }

  protected ResourceBundleSupport getBundleSupport()
  {
    return bundleSupport;
  }

  protected boolean isConfirmed()
  {
    return confirmed;
  }

  protected void setConfirmed(final boolean confirmed)
  {
    this.confirmed = confirmed;
  }
}

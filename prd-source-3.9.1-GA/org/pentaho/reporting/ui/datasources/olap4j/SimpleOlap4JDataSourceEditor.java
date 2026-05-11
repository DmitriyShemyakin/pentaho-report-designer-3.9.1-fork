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

package org.pentaho.reporting.ui.datasources.olap4j;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeContext;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.SwingUtil;
import org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.AbstractMDXDataFactory;
import org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.connections.OlapConnectionProvider;
import org.pentaho.reporting.ui.datasources.jdbc.connection.JdbcConnectionDefinition;
import org.pentaho.reporting.ui.datasources.jdbc.connection.JdbcConnectionDefinitionManager;
import org.pentaho.reporting.ui.datasources.jdbc.ui.SimpleDataSourceDialogModel;

/**
 * @author Michael D'Amour
 */
public abstract class SimpleOlap4JDataSourceEditor extends JDialog
{
  private class CancelAction extends AbstractAction
  {
    /**
     * Defines an <code>Action</code> object with a default
     * description string and default icon.
     */
    private CancelAction()
    {
      putValue(Action.NAME, Messages.getString("Olap4JDataSourceEditor.Cancel.Name"));
    }

    public void actionPerformed(final ActionEvent e)
    {
      dispose();
    }
  }

  private class ConfirmAction extends AbstractAction implements PropertyChangeListener
  {
    /**
     * Defines an <code>Action</code> object with a default
     * description string and default icon.
     */
    private ConfirmAction()
    {
      putValue(Action.NAME, Messages.getString("Olap4JDataSourceEditor.OK.Name"));
    }

    public void propertyChange(final PropertyChangeEvent evt)
    {
      final SimpleDataSourceDialogModel dialogModel = getDialogModel();
      setEnabled(dialogModel.isConnectionSelected());
    }

    public void actionPerformed(final ActionEvent e)
    {
      confirmed = true;
      dispose();
    }
  }

  private boolean confirmed;
  private SimpleDataSourceDialogModel dialogModel;
  private OlapConnectionPanel connectionComponent;

  public SimpleOlap4JDataSourceEditor(final DesignTimeContext context)
  {
    init(context);
  }

  public SimpleOlap4JDataSourceEditor(final DesignTimeContext context, final Dialog owner)
  {
    super(owner);
    init(context);
  }

  public SimpleOlap4JDataSourceEditor(final DesignTimeContext context, final Frame owner)
  {
    super(owner);
    init(context);
  }

  protected void init(final DesignTimeContext designTimeContext)
  {
    setModal(true);

    dialogModel = new SimpleDataSourceDialogModel
        (new JdbcConnectionDefinitionManager("org/pentaho/reporting/ui/datasources/olap4j/Settings"));

    connectionComponent = new OlapConnectionPanel(dialogModel, designTimeContext);
    connectionComponent.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));

    // Create the button panel
    final ConfirmAction confirmAction = new ConfirmAction();
    dialogModel.addPropertyChangeListener(confirmAction);

    // Create the content panel
    final JPanel contentPanel = new JPanel(new BorderLayout());
    contentPanel.add(BorderLayout.CENTER, connectionComponent);
    contentPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));


    final JButton okButton = new JButton(confirmAction);
    final JButton cancelButton = new JButton(new CancelAction());

    final JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
    buttonPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));
    buttonPanel.add(okButton);
    buttonPanel.add(cancelButton);

    // Create the center panel
    final JPanel centerPanel = new JPanel(new BorderLayout());
    centerPanel.add(BorderLayout.CENTER, contentPanel);
    centerPanel.add(BorderLayout.SOUTH, buttonPanel);

    // Return the center panel
    setContentPane(centerPanel);
    pack();
    SwingUtil.centerDialogInParent(this);
  }

  public DataFactory performConfiguration(final AbstractMDXDataFactory dataFactory)
  {
    // Reset the ok / cancel flag
    dialogModel.clear();
    connectionComponent.setRoleField(null);
    confirmed = false;

    // Initialize the internal storage

    // Load the current configuration
    if (dataFactory != null)
    {

      final OlapConnectionProvider currentJNDISource = dataFactory.getConnectionProvider();
      final JdbcConnectionDefinition definition = getConnectionPanel().createConnectionDefinition(currentJNDISource);
      getDialogModel().addConnection(definition);
      getDialogModel().getConnections().setSelectedItem(definition);

      getDialogModel().setJdbcUserField(dataFactory.getJdbcUserField());
      getDialogModel().setJdbcPasswordField(dataFactory.getJdbcPasswordField());
      connectionComponent.setRoleField(dataFactory.getRoleField());
    }

    // Enable the dialog
    pack();
    setLocationRelativeTo(getParent());
    setVisible(true);

    if (!isConfirmed())
    {
      return null;
    }

    final AbstractMDXDataFactory factory = createDataFactory();
    if (factory == null)
    {
      return null;
    }
    factory.setJdbcUserField(getDialogModel().getJdbcUserField());
    factory.setJdbcPasswordField(getDialogModel().getJdbcPasswordField());
    factory.setRoleField(connectionComponent.getRoleField());
    return factory;
  }

  protected abstract AbstractMDXDataFactory createDataFactory();

  protected SimpleDataSourceDialogModel getDialogModel()
  {
    return dialogModel;
  }

  protected OlapConnectionPanel getConnectionPanel()
  {
    return connectionComponent;
  }

  public boolean isConfirmed()
  {
    return confirmed;
  }
}
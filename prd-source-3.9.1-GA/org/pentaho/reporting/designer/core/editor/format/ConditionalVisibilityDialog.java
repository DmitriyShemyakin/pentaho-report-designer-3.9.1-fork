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

package org.pentaho.reporting.designer.core.editor.format;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.pentaho.reporting.designer.core.Messages;
import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.editor.ReportRenderContext;
import org.pentaho.reporting.designer.core.util.ExpressionEditorPane;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.SwingUtil;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class ConditionalVisibilityDialog extends JDialog
{
  private class OKAction extends AbstractAction
  {
    /**
     * Defines an <code>Action</code> object with a default description string and default icon.
     */
    private OKAction()
    {
      putValue(Action.NAME, "OK");
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(final ActionEvent e)
    {
      confirmed = true;
      dispose();
    }
  }

  private class CancelAction extends AbstractAction
  {
    /**
     * Defines an <code>Action</code> object with a default description string and default icon.
     */
    private CancelAction()
    {
      putValue(Action.NAME, "Cancel");
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(final ActionEvent e)
    {
      dispose();
    }
  }

  private boolean confirmed;
  private ExpressionEditorPane editorPane;

  public ConditionalVisibilityDialog()
          throws HeadlessException
  {
    init();
  }

  public ConditionalVisibilityDialog(final Frame owner)
          throws HeadlessException
  {
    super(owner);
    init();
  }

  public ConditionalVisibilityDialog(final Dialog owner)
          throws HeadlessException
  {
    super(owner);
    init();
  }

  private void init()
  {
    setTitle(Messages.getString("ConditionalVisibilityDialog.HideObject"));
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    setLayout(new BorderLayout());

    editorPane = new ExpressionEditorPane();

    final JPanel floatingPanel = new JPanel();
    floatingPanel.setLayout(new GridBagLayout());

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.insets = new Insets(5, 5, 5, 5);
    gbc.anchor = GridBagConstraints.WEST;
    floatingPanel.add(new JLabel(Messages.getString("ConditionalVisibilityDialog.Condition")), gbc);

    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.weightx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(5, 5, 5, 5);
    floatingPanel.add(editorPane, gbc);

    add(floatingPanel, BorderLayout.CENTER);
    add(createButtonsPane(), BorderLayout.SOUTH);

    pack();
    SwingUtil.centerDialogInParent(this);
  }

  public Expression performEdit(final Expression expression)
  {
    editorPane.setValue(expression);

    confirmed = false;
    setModal(true);
    setVisible(true);
    if (confirmed == false)
    {
      return null;
    }

    return editorPane.getValue();
  }


  private JPanel createButtonsPane()
  {
    final JPanel buttonsPanel = new JPanel();
    buttonsPanel.setLayout(new GridLayout(1, 2, 5, 5));

    final JButton button = new JButton(new OKAction());
    button.setDefaultCapable(true);
    buttonsPanel.add(button);
    buttonsPanel.add(new JButton(new CancelAction()));

    final JPanel buttonsCarrierPanel = new JPanel();
    buttonsCarrierPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
    buttonsCarrierPanel.add(buttonsPanel);
    return buttonsCarrierPanel;
  }

  public ReportDesignerContext getReportDesignerContext()
  {
    return editorPane.getReportDesignerContext();
  }

  public void setReportDesignerContext(final ReportDesignerContext reportDesignerContext)
  {
    editorPane.setReportDesignerContext(reportDesignerContext);
  }
}

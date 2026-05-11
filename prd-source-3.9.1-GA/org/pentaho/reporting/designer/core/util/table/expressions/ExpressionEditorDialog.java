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

package org.pentaho.reporting.designer.core.util.table.expressions;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.pentaho.reporting.designer.core.util.UtilMessages;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.SwingUtil;

public class ExpressionEditorDialog extends JDialog
{
  public ExpressionEditorDialog(final Component editorComponent)
  {
    init(editorComponent);
  }

  public ExpressionEditorDialog(final Dialog aParent, final Component editorComponent)
  {
    super(aParent);
    init(editorComponent);
  }


  public ExpressionEditorDialog(final Frame aParent, final Component editorComponent)
  {
    super(aParent);
    init(editorComponent);
  }

  private void init(final Component editorComponent)
  {
    setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    setModal(true);
    setTitle(UtilMessages.getInstance().getString("ExpressionEditorDialog.Title"));

    final JPanel carrierPanel = new JPanel();
    carrierPanel.setLayout(new BorderLayout());
    carrierPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
    carrierPanel.add(editorComponent, BorderLayout.CENTER);

    final JPanel theCenterPanel = new JPanel();
    theCenterPanel.setLayout(new BorderLayout());
    theCenterPanel.add(carrierPanel, BorderLayout.CENTER);
    theCenterPanel.add(createButtonsPanel(), BorderLayout.SOUTH);
    setContentPane(theCenterPanel);

    pack();

    SwingUtil.centerDialogInParent(this);
  }

  private JPanel createButtonsPanel()
  {

    final JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    buttonPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));

    final JButton closeButton = new JButton(new OkAction());
    closeButton.setDefaultCapable(true);
    buttonPanel.add(closeButton);
    return buttonPanel;
  }

  public class OkAction extends AbstractAction
  {

    public OkAction()
    {
      putValue(Action.NAME, UtilMessages.getInstance().getString("ExpressionEditorDialog.Close"));
    }

    public void actionPerformed(final ActionEvent aEvt)
    {
      dispose();
    }
  }
}

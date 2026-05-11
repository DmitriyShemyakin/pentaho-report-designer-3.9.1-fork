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

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.pentaho.reporting.designer.core.Messages;
import org.pentaho.reporting.engine.classic.core.function.RowBandingFunction;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.KeyedComboBoxModel;
import org.pentaho.reporting.libraries.designtime.swing.ColorComboBox;
import org.pentaho.reporting.libraries.designtime.swing.CommonDialog;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class RowBandingDialog extends CommonDialog
{
  private JTextField elementNameField;
  private ColorComboBox visibleColorBox;
  private ColorComboBox invisibleColorBox;
  private KeyedComboBoxModel pageStateModel;
  private KeyedComboBoxModel initialStateModel;

  public RowBandingDialog()
  {
    init();
  }

  public RowBandingDialog(final Frame owner)
  {
    super(owner);
    init();
  }

  public RowBandingDialog(final Dialog owner)
  {
    super(owner);
    init();
  }

  protected Component createContentPane()
  {
    setTitle(Messages.getString("RowBandingDialog.RowBanding"));

    visibleColorBox = new ColorComboBox();
    invisibleColorBox = new ColorComboBox();
    elementNameField = new JTextField();
    elementNameField.setColumns(40);

    initialStateModel = createInitialStateModel();
    pageStateModel = createNewPageStateModel();

    final JComboBox initialStateBox = new JComboBox(initialStateModel);
    final JComboBox newPageStateBox = new JComboBox(pageStateModel);

    final JPanel contentPanel = new JPanel();
    contentPanel.setLayout(new GridBagLayout());

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    contentPanel.add(new JLabel(Messages.getString("RowBandingDialog.VisibleColor")), gbc);

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    contentPanel.add(visibleColorBox, gbc);

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 1;
    contentPanel.add(new JLabel(Messages.getString("RowBandingDialog.InvisibleColor")), gbc);

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    contentPanel.add(invisibleColorBox, gbc);

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 2;
    contentPanel.add(new JLabel(Messages.getString("RowBandingDialog.Element")), gbc);

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 2;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    contentPanel.add(elementNameField, gbc);

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 3;
    contentPanel.add(new JLabel(Messages.getString("RowBandingDialog.StateOnNewGroup")), gbc);

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 3;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    contentPanel.add(initialStateBox, gbc);

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 4;
    contentPanel.add(new JLabel(Messages.getString("RowBandingDialog.StateOnNewPage")), gbc);

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 4;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    contentPanel.add(newPageStateBox, gbc);

    return contentPanel;
  }

  public boolean performEdit(final RowBandingFunction function)
  {
    if (function == null)
    {
      throw new NullPointerException();
    }

    final String element = function.getElement();
    if (element != null)
    {
      elementNameField.setText(element);
    }
    else
    {
      elementNameField.setText("");
    }

    initialStateModel.setSelectedKey(Boolean.valueOf(function.getInitialState()));
    pageStateModel.setSelectedKey(function.getNewPageState());
    invisibleColorBox.setValueFromModel(function.getInvisibleBackground());
    visibleColorBox.setValueFromModel(function.getVisibleBackground());


    if (super.performEdit() == false)
    {
      return false;
    }

    final String elementName = elementNameField.getText();
    if (elementName.length() == 0)
    {
      function.setElement(null);
    }
    else
    {
      function.setElement(elementName);
    }
    function.setInitialState(Boolean.TRUE.equals(initialStateModel.getSelectedKey()));
    function.setNewPageState((Boolean) pageStateModel.getSelectedKey());
    function.setInvisibleBackground(invisibleColorBox.getValueFromModel());
    function.setVisibleBackground(visibleColorBox.getValueFromModel());
    return true;
  }

  private KeyedComboBoxModel createNewPageStateModel()
  {
    final KeyedComboBoxModel model = new KeyedComboBoxModel();
    model.add(null, Messages.getString("RowBandingDialog.SameAsInitialState"));
    model.add(Boolean.TRUE, Messages.getString("RowBandingDialog.Visible"));
    model.add(Boolean.FALSE, Messages.getString("RowBandingDialog.Invisible"));
    return model;
  }

  private KeyedComboBoxModel createInitialStateModel()
  {
    final KeyedComboBoxModel model = new KeyedComboBoxModel();
    model.add(Boolean.TRUE, Messages.getString("RowBandingDialog.Visible"));
    model.add(Boolean.FALSE, Messages.getString("RowBandingDialog.Invisible"));
    return model;
  }
}

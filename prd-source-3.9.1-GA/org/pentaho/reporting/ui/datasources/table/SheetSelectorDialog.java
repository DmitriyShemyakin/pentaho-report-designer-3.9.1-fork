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
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.poi.ss.usermodel.Workbook;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.SwingUtil;

public class SheetSelectorDialog extends JDialog
{
  private Workbook workbook;
  private JComboBox sheetsComboBox;
  private int selectedIndex;

  public SheetSelectorDialog(final Workbook aWorkbook, final JDialog aParent)
  {
    super(aParent);
    workbook = aWorkbook;
    init();
  }

  private void init()
  {
    final Object[] theSheetsData = new Object[workbook.getNumberOfSheets()];
    for (int i = 0; i < workbook.getNumberOfSheets(); i++)
    {
      theSheetsData[i] = workbook.getSheetName(i);
    }
    sheetsComboBox = new JComboBox(theSheetsData);

    setTitle(Messages.getString("SheetSelectorDialog.Import"));
    setLayout(new BorderLayout());
    setModal(true);
    setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

    final JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    final JLabel theSheetLabel = new JLabel(Messages.getString("SheetSelectorDialog.ChooseSheet"));
    centerPanel.add(theSheetLabel);
    centerPanel.add(sheetsComboBox);


    final JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
    buttonsPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));
    buttonsPanel.add(new JButton(new OkAction()));
    buttonsPanel.add(new JButton(new CancelAction()));

    add(centerPanel, BorderLayout.CENTER);
    add(buttonsPanel, BorderLayout.SOUTH);

    pack();
    SwingUtil.centerDialogInParent(this);
    setVisible(true);
  }

  public int getSelectedIndex()
  {
    return selectedIndex;
  }

  private class CancelAction extends AbstractAction
  {
    private CancelAction()
    {
      putValue(Action.NAME, Messages.getString("SheetSelectorDialog.Cancel.Name"));
    }

    public void actionPerformed(final ActionEvent e)
    {
      dispose();
    }
  }

  private class OkAction extends AbstractAction
  {
    private OkAction()
    {
      putValue(Action.NAME, Messages.getString("SheetSelectorDialog.OK.Name"));
    }

    public void actionPerformed(final ActionEvent e)
    {
      selectedIndex = sheetsComboBox.getSelectedIndex();
      dispose();
    }
  }
}

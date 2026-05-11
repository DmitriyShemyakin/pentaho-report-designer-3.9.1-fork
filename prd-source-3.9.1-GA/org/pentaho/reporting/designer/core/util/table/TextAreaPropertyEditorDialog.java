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

package org.pentaho.reporting.designer.core.util.table;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyEditor;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.pentaho.reporting.designer.core.util.UtilMessages;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.KeyedComboBoxModel;
import org.pentaho.reporting.libraries.designtime.swing.CommonDialog;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class TextAreaPropertyEditorDialog extends CommonDialog
{
  private class DocumentUpdateHandler implements DocumentListener
  {
    /**
     * Gives notification that there was an insert into the document.  The range given by the DocumentEvent bounds the
     * freshly inserted region.
     *
     * @param e the document event
     */
    public void insertUpdate(final DocumentEvent e)
    {
      if (propertyEditor != null)
      {
        try
        {
          propertyEditor.setAsText(textArea.getText());
          getConfirmAction().setEnabled(true);
        }
        catch (Exception ex)
        {
          // ignore ..
          getConfirmAction().setEnabled(false);
        }
      }

    }

    /**
     * Gives notification that a portion of the document has been removed.  The range is given in terms of what the view
     * last saw (that is, before updating sticky positions).
     *
     * @param e the document event
     */
    public void removeUpdate(final DocumentEvent e)
    {
      insertUpdate(e);
    }

    /**
     * Gives notification that an attribute or set of attributes changed.
     *
     * @param e the document event
     */
    public void changedUpdate(final DocumentEvent e)
    {
      insertUpdate(e);
    }
  }

  private class SyntaxHighlightAction implements ActionListener
  {
    private SyntaxHighlightAction()
    {
    }

    public void actionPerformed(final ActionEvent e)
    {
      final Object o = syntaxModel.getSelectedKey();
      if (o instanceof String)
      {
        textArea.setSyntaxEditingStyle((String) o);
      }
    }
  }

  private KeyedComboBoxModel syntaxModel;

  private PropertyEditor propertyEditor;
  private RSyntaxTextArea textArea;
  private Object originalValue;

  public TextAreaPropertyEditorDialog()
      throws HeadlessException
  {
    init();
  }

  public TextAreaPropertyEditorDialog(final Frame owner)
      throws HeadlessException
  {
    super(owner);
    init();
  }

  public TextAreaPropertyEditorDialog(final Dialog owner)
      throws HeadlessException
  {
    super(owner);
    init();
  }

  protected void init()
  {
    setModal(true);

    syntaxModel = new KeyedComboBoxModel();
    syntaxModel.add((SyntaxConstants.SYNTAX_STYLE_NONE), UtilMessages.getInstance().getString("RSyntaxAreaLanguages.None"));
    syntaxModel.add((SyntaxConstants.SYNTAX_STYLE_JAVA), UtilMessages.getInstance().getString("RSyntaxAreaLanguages.Java"));
    syntaxModel.add((SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT), UtilMessages.getInstance().getString("RSyntaxAreaLanguages.JavaScript"));
    syntaxModel.add((SyntaxConstants.SYNTAX_STYLE_GROOVY), UtilMessages.getInstance().getString("RSyntaxAreaLanguages.Groovy"));
    syntaxModel.add((SyntaxConstants.SYNTAX_STYLE_HTML), UtilMessages.getInstance().getString("RSyntaxAreaLanguages.Html"));
    syntaxModel.add((SyntaxConstants.SYNTAX_STYLE_CSS), UtilMessages.getInstance().getString("RSyntaxAreaLanguages.CSS"));
    syntaxModel.add((SyntaxConstants.SYNTAX_STYLE_SQL), UtilMessages.getInstance().getString("RSyntaxAreaLanguages.SQL"));
    syntaxModel.add((SyntaxConstants.SYNTAX_STYLE_XML), UtilMessages.getInstance().getString("RSyntaxAreaLanguages.XML"));
    syntaxModel.add((SyntaxConstants.SYNTAX_STYLE_PYTHON), UtilMessages.getInstance().getString("RSyntaxAreaLanguages.Python"));
    syntaxModel.add((SyntaxConstants.SYNTAX_STYLE_TCL), UtilMessages.getInstance().getString("RSyntaxAreaLanguages.TCL"));

    textArea = new RSyntaxTextArea();
    textArea.setBracketMatchingEnabled(true);
    textArea.setSyntaxEditingStyle(RSyntaxTextArea.SYNTAX_STYLE_JAVA);
    textArea.setColumns(60);
    textArea.setRows(20);
    textArea.getDocument().addDocumentListener(new DocumentUpdateHandler());

    super.init();

    pack();
  }

  protected Component createContentPane()
  {
    final JComboBox syntaxBox = new JComboBox(syntaxModel);
    syntaxBox.addActionListener(new SyntaxHighlightAction());

    final JPanel syntaxSelectionPane = new JPanel();
    syntaxSelectionPane.setLayout(new FlowLayout());
    syntaxSelectionPane.add(syntaxBox);

    final JPanel contentPane = new JPanel();
    contentPane.setLayout(new BorderLayout());
    contentPane.add(new RTextScrollPane(500, 300, textArea, true), BorderLayout.CENTER);
    contentPane.add(syntaxBox, BorderLayout.NORTH);

    return contentPane;
  }

  public boolean performEdit(final PropertyEditor editor)
  {
    if (editor == null)
    {
      throw new NullPointerException();
    }
    this.propertyEditor = editor;
    this.originalValue = propertyEditor.getValue();
    if (originalValue == null)
    {
      this.textArea.setText("");
    }
    else
    {
      this.textArea.setText(propertyEditor.getAsText());
    }

    if (performEdit())
    {
      try
      {
        propertyEditor.setAsText(textArea.getText());
      }
      catch (Exception ex)
      {
        // ignore ..
      }
      return true;
    }
    else
    {
      try
      {
        propertyEditor.setValue(originalValue);
      }
      catch (Exception ex)
      {
        // ignore ..
      }
      return false;
    }
  }

  public String performEdit(final String originalValue)
  {
    this.originalValue = originalValue;
    if (originalValue == null)
    {
      this.textArea.setText("");
    }
    else
    {
      this.textArea.setText(originalValue);
    }
    if (performEdit())
    {
      return textArea.getText();
    }
    else
    {
      return originalValue;
    }
  }
}

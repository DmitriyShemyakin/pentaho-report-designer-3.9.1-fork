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

package org.pentaho.reporting.designer.core.welcome;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.TreeModel;

import org.pentaho.reporting.designer.core.Messages;
import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.actions.AbstractDesignerContextAction;
import org.pentaho.reporting.designer.core.actions.global.NewReportAction;
import org.pentaho.reporting.designer.core.settings.WorkspaceSettings;
import org.pentaho.reporting.designer.core.util.HyperLink;
import org.pentaho.reporting.designer.core.util.IconLoader;
import org.pentaho.reporting.designer.core.widgets.HyperlinkHandler;

public class WelcomePane extends JDialog
{
  private static final int WELCOME_WIDTH = 920;
  private static final int WELCOME_HEIGHT = 540;

  private JCheckBox showOnStartupCheckbox;
  private ReportDesignerContext reportDesignerContext;
  private NewReportAction newReportAction;
  private CloseActionListener closeActionListener;

  public WelcomePane(final JFrame frame, final ReportDesignerContext reportDesignerContext)
  {
    super(frame);
    init(reportDesignerContext);
  }

  public WelcomePane(final JDialog dialog, final ReportDesignerContext reportDesignerContext)
  {
    super(dialog);
    init(reportDesignerContext);
  }

  public WelcomePane(final ReportDesignerContext reportDesignerContext)
  {
    init(reportDesignerContext);
  }

  private void init(final ReportDesignerContext reportDesignerContext)
  {
    if (reportDesignerContext == null)
    {
      throw new NullPointerException();
    }

    setTitle(Messages.getString("WelcomePane.title"));// NON-NLS
    this.reportDesignerContext = reportDesignerContext;

    this.newReportAction = new NewReportAction();
    this.newReportAction.setReportDesignerContext(reportDesignerContext);

    this.closeActionListener = new CloseActionListener();

    showOnStartupCheckbox = new JCheckBox
        (Messages.getString("WelcomePane.showAtStartup"), WorkspaceSettings.getInstance().isShowLauncher());// NON-NLS
    showOnStartupCheckbox.setOpaque(false);
    showOnStartupCheckbox.addActionListener(new TriggerShowWelcomePaneAction());

    setResizable(true);
    setMinimumSize(new Dimension(780, 460));

    initGUI();

    pack();
    setSize(Math.max(getWidth(), WELCOME_WIDTH), Math.max(getHeight(), WELCOME_HEIGHT));
  }

  protected ReportDesignerContext getReportDesignerContext()
  {
    return reportDesignerContext;
  }

  private void initGUI()
  {
    final GradientWelcomePanel root = new GradientWelcomePanel();
    root.setLayout(new BorderLayout());
    root.setPreferredSize(new Dimension(WELCOME_WIDTH, WELCOME_HEIGHT));

    final JPanel shell = new JPanel(new BorderLayout(20, 0));
    shell.setOpaque(false);
    shell.setBorder(new EmptyBorder(20, 24, 20, 24));

    shell.add(createHeroPane(), BorderLayout.WEST);
    shell.add(createSidePane(), BorderLayout.CENTER);

    root.add(shell, BorderLayout.CENTER);
    setContentPane(root);
  }

  private JPanel createHeroPane()
  {
    final JPanel hero = new JPanel();
    hero.setOpaque(false);
    hero.setLayout(new BoxLayout(hero, BoxLayout.Y_AXIS));
    hero.setPreferredSize(new Dimension(360, 0));
    hero.setMaximumSize(new Dimension(420, Integer.MAX_VALUE));
    hero.setAlignmentX(JPanel.LEFT_ALIGNMENT);

    final JLabel title = new JLabel(Messages.getString("WelcomePane.title"));// NON-NLS
    final Font base = UIManager.getFont("Label.font");//NON-NLS
    if (base != null)
    {
      title.setFont(base.deriveFont(Font.BOLD, base.getSize2D() + 8f));
    }
    else
    {
      title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));
    }
    title.setAlignmentX(JPanel.LEFT_ALIGNMENT);
    hero.add(title);

    hero.add(Box.createVerticalStrut(8));

    final JLabel tagline = new JLabel(Messages.getString("WelcomePane.tagline"));// NON-NLS
    Color muted = UIManager.getColor("Label.disabledForeground");//NON-NLS
    if (muted == null)
    {
      muted = new Color(100, 100, 100);
    }
    tagline.setForeground(muted);
    if (base != null)
    {
      tagline.setFont(base.deriveFont(base.getSize2D() + 1f));
    }
    tagline.setAlignmentX(JPanel.LEFT_ALIGNMENT);
    hero.add(tagline);

    hero.add(Box.createVerticalStrut(28));

    final JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
    actions.setOpaque(false);
    actions.setAlignmentX(JPanel.LEFT_ALIGNMENT);

    try
    {
      final Class<?> wizardClass = Class.forName("org.pentaho.reporting.designer.extensions.wizard.NewWizardReportAction");
      final AbstractDesignerContextAction newWizardActionListener =
          (AbstractDesignerContextAction) wizardClass.getDeclaredConstructor().newInstance();
      newWizardActionListener.setReportDesignerContext(reportDesignerContext);

      final Object nameVal = newWizardActionListener.getValue("WIZARD.BUTTON.TEXT");//NON-NLS
      final String wizardText = nameVal != null ? nameVal.toString() : Messages.getString("WelcomePane.title");

      final JButton wizardBtn = createActionButton(wizardText, IconLoader.getInstance().getWizardDocumentIcon());
      wizardBtn.addActionListener(newWizardActionListener);
      wizardBtn.addActionListener(closeActionListener);
      actions.add(wizardBtn);
    }
    catch (Exception e)
    {
      // wizard extension not installed
    }

    final JButton newReportBtn = createActionButton(
        stripHtml(Messages.getString("WelcomePane.newReportLabel")),// NON-NLS
        IconLoader.getInstance().getCreateReportIcon());
    newReportBtn.addActionListener(newReportAction);
    newReportBtn.addActionListener(closeActionListener);
    actions.add(newReportBtn);

    hero.add(actions);
    hero.add(Box.createVerticalGlue());

    return hero;
  }

  private static String stripHtml(final String html)
  {
    if (html == null)
    {
      return "";
    }
    return html.replaceAll("<[^>]+>", " ").replace("&nbsp;", " ").trim().replaceAll("\\s+", " ");//NON-NLS
  }

  private JButton createActionButton(final String text, final javax.swing.Icon icon)
  {
    final JButton b = new JButton(text, icon);
    b.setVerticalTextPosition(SwingConstants.BOTTOM);
    b.setHorizontalTextPosition(SwingConstants.CENTER);
    b.setFocusPainted(false);
    b.setMargin(new Insets(12, 16, 12, 16));
    return b;
  }

  private JPanel createSidePane()
  {
    final TreeModel sampleTreeModel = SamplesTreeBuilder.getSampleTreeModel();
    final FilesTree tree = new FilesTree(sampleTreeModel, reportDesignerContext, this);
    final JScrollPane scrollPane = new JScrollPane(tree);
    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

    final Color tableBg = UIManager.getColor("Table.background");//NON-NLS
    scrollPane.getViewport().setBackground(tableBg != null ? tableBg : Color.WHITE);

    Color border = UIManager.getColor("Component.borderColor");//NON-NLS
    if (border == null)
    {
      border = new Color(0xD0D0D0);
    }
    scrollPane.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(border, 1),
        BorderFactory.createEmptyBorder(2, 2, 2, 2)));

    final JPanel sidePane = new JPanel();
    sidePane.setOpaque(false);
    sidePane.setLayout(new GridBagLayout());

    final JLabel samplesLabel = new JLabel(Messages.getString("WelcomePane.samples"));// NON-NLS
    final Font base = UIManager.getFont("Label.font");//NON-NLS
    if (base != null)
    {
      samplesLabel.setFont(base.deriveFont(Font.BOLD, base.getSize2D() + 1f));
    }

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(0, 0, 8, 0);
    sidePane.add(samplesLabel, gbc);

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.weightx = 1;
    gbc.weighty = 1;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.insets = new Insets(0, 0, 0, 0);
    sidePane.add(scrollPane, gbc);

    final JPanel onlineResourcesList = new JPanel(new GridLayout(0, 1, 0, 4));
    onlineResourcesList.setOpaque(false);
    final JLabel res = new JLabel(Messages.getString("WelcomePane.resources"));// NON-NLS
    if (base != null)
    {
      res.setFont(base.deriveFont(Font.BOLD, base.getSize2D() + 1f));
    }
    onlineResourcesList.add(res);
    onlineResourcesList.add(createLink(Messages.getString("WelcomePane.forums"), Messages.getString("WelcomePane.url.forums")));// NON-NLS

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.weightx = 1;
    gbc.insets = new Insets(20, 0, 16, 0);
    gbc.fill = GridBagConstraints.HORIZONTAL;
    sidePane.add(onlineResourcesList, gbc);

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 3;
    gbc.weightx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    sidePane.add(showOnStartupCheckbox, gbc);

    return sidePane;
  }

  /**
   * Creates a HyperLink label and attaches a URL click event.
   *
   * @param lbl  the link text presented to the user
   * @param link the URL to which the hyperlink points
   * @return the created hyperlink object.
   */
  public HyperLink createLink(final String lbl, final String link)
  {
    final HyperLink linkLbl = new HyperLink(lbl);
    linkLbl.addMouseListener(new HyperlinkHandler(link, this));
    return linkLbl;
  }

  private class TriggerShowWelcomePaneAction implements ActionListener
  {
    private TriggerShowWelcomePaneAction()
    {
    }

    public void actionPerformed(final ActionEvent evt)
    {
      WorkspaceSettings.getInstance().setShowLauncher(showOnStartupCheckbox.isSelected());
    }
  }

  /**
   * @author wseyler
   */
  public class CloseActionListener implements ActionListener
  {

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(final ActionEvent e)
    {
      dispose();
    }

  }
}

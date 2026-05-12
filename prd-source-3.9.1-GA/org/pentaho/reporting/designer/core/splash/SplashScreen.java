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
 * Copyright (c) 2006 - 2009 Pentaho Corporation..  All rights reserved.
 */

package org.pentaho.reporting.designer.core.splash;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Calendar;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JWindow;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.pentaho.reporting.designer.core.Messages;
import org.pentaho.reporting.designer.core.ReportDesignerInfo;
import org.pentaho.reporting.designer.core.util.ModernSurfaceFill;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.SwingUtil;

/**
 * Splash screen shown while the application boots.
 */
public class SplashScreen extends JWindow
{
  private static final int PREF_WIDTH = 580;
  private static final int PREF_HEIGHT = 380;
  private static final Color TEXT_PRIMARY = new Color(238, 242, 248);
  private static final Color TEXT_SECONDARY = new Color(175, 186, 202);
  private static final Color TEXT_MUTED = new Color(140, 152, 168);
  private static final Font LICENSE_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 10);

  private class HideOnClickHandler extends MouseAdapter
  {
    public void mouseClicked(final MouseEvent e)
    {
      SplashScreen.this.setVisible(false);
    }
  }

  private JLabel statusLabel;

  public SplashScreen()
  {
    addMouseListener(new HideOnClickHandler());

    statusLabel = new JLabel();
    statusLabel.setFont(LICENSE_FONT);
    statusLabel.setHorizontalAlignment(SwingConstants.LEADING);
    statusLabel.setForeground(TEXT_SECONDARY);
    statusLabel.setOpaque(false);

    final JPanel statusVersionPanel = new JPanel(new GridBagLayout());
    final GridBagConstraints c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = 0;
    c.weightx = 1;
    c.anchor = GridBagConstraints.LAST_LINE_START;
    c.insets = new Insets(4, 24, 14, 24);
    c.fill = GridBagConstraints.HORIZONTAL;
    statusVersionPanel.setOpaque(false);
    statusVersionPanel.add(statusLabel, c);

    final JPanel splashPanel = createSplashPanel();
    splashPanel.add(statusVersionPanel, BorderLayout.SOUTH);

    setContentPane(splashPanel);

    setSize(splashPanel.getPreferredSize());
    SwingUtil.centerFrameOnScreen(this);
  }

  public static JPanel createSplashPanel()
  {
    final JPanel root = new JPanel(new BorderLayout(0, 0))
    {
      protected void paintComponent(final Graphics g)
      {
        final Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        ModernSurfaceFill.paintSplashDark(g2, getWidth(), getHeight());
        g2.dispose();
        super.paintComponent(g);
      }
    };
    root.setOpaque(false);
    root.setBorder(BorderFactory.createLineBorder(new Color(48, 55, 68), 1));
    root.setPreferredSize(new Dimension(PREF_WIDTH, PREF_HEIGHT));

    final JPanel header = new JPanel(new GridBagLayout());
    header.setOpaque(false);

    final JLabel title = new JLabel(Messages.getString("SplashScreen.Title"));// NON-NLS
    title.setForeground(TEXT_PRIMARY);
    title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));

    final JLabel versionLabel = new JLabel();
    String ver = ReportDesignerInfo.getInstance().getVersion();
    if (ver == null)
    {
      versionLabel.setText(Messages.getString("SplashScreen.DevelopmentVersion"));// NON-NLS
    }
    else
    {
      versionLabel.setText(ver);
    }
    versionLabel.setText(Messages.getString("SplashScreen.Version", versionLabel.getText()));// NON-NLS
    versionLabel.setForeground(TEXT_SECONDARY);
    versionLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));

    final JLabel forkLabel = new JLabel(Messages.getString("SplashScreen.ForkAttribution"));// NON-NLS
    forkLabel.setForeground(TEXT_MUTED);
    forkLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));

    final GridBagConstraints gc = new GridBagConstraints();
    gc.gridx = 0;
    gc.gridy = 0;
    gc.anchor = GridBagConstraints.WEST;
    gc.insets = new Insets(22, 24, 4, 24);
    header.add(title, gc);

    gc.gridy = 1;
    gc.insets = new Insets(0, 24, 4, 24);
    header.add(versionLabel, gc);

    gc.gridy = 2;
    gc.insets = new Insets(0, 24, 8, 24);
    header.add(forkLabel, gc);

    final int year = Calendar.getInstance().get(Calendar.YEAR);
    final JTextArea copyrightArea = new JTextArea(
        Messages.getString("SplashScreen.Copyright", Integer.valueOf(year)));// NON-NLS
    copyrightArea.setEditable(false);
    copyrightArea.setOpaque(false);
    copyrightArea.setWrapStyleWord(true);
    copyrightArea.setLineWrap(true);
    copyrightArea.setFont(LICENSE_FONT);
    copyrightArea.setForeground(TEXT_MUTED);
    copyrightArea.setBorder(new EmptyBorder(0, 0, 8, 0));
    copyrightArea.setRows(2);

    final JTextArea licenseArea = new JTextArea(Messages.getString("SplashScreen.License"));// NON-NLS
    licenseArea.setEditable(false);
    licenseArea.setOpaque(false);
    licenseArea.setWrapStyleWord(true);
    licenseArea.setLineWrap(true);
    licenseArea.setFont(LICENSE_FONT);
    licenseArea.setForeground(TEXT_MUTED);
    licenseArea.setRows(6);

    final JScrollPane licenseScroll = new JScrollPane(licenseArea);
    licenseScroll.setOpaque(false);
    licenseScroll.getViewport().setOpaque(false);
    licenseScroll.setBorder(BorderFactory.createEmptyBorder());
    licenseScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    licenseScroll.setPreferredSize(new Dimension(PREF_WIDTH - 48, 150));

    final JPanel center = new JPanel(new BorderLayout(0, 0));
    center.setOpaque(false);
    center.setBorder(new EmptyBorder(0, 24, 0, 24));
    center.add(copyrightArea, BorderLayout.NORTH);
    center.add(licenseScroll, BorderLayout.CENTER);

    root.add(header, BorderLayout.NORTH);
    root.add(center, BorderLayout.CENTER);

    return root;
  }

  public void setStatus(final String status)
  {
    this.statusLabel.setText(status);
    this.statusLabel.repaint();
  }

}

/*
 * Copyright (c) 2008 - 2009 Pentaho Corporation. All rights reserved.
 */

package org.pentaho.reporting.designer.core.welcome;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;

import javax.swing.JPanel;
import javax.swing.UIManager;

/**
 * Root panel for the welcome dialog: soft vertical gradient derived from current L&amp;F colors.
 */
public class GradientWelcomePanel extends JPanel
{
  public GradientWelcomePanel()
  {
    setOpaque(false);
  }

  protected void paintComponent(final Graphics g)
  {
    final Graphics2D g2 = (Graphics2D) g.create();
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    final int w = getWidth();
    final int h = getHeight();
    Color top = UIManager.getColor("Panel.background");//NON-NLS
    if (top == null)
    {
      top = new Color(245, 247, 250);
    }
    Color bottom = UIManager.getColor("TextField.background");//NON-NLS
    if (bottom == null)
    {
      bottom = top.darker();
    }
    bottom = blend(bottom, top, 0.55f);

    final float[] dist = {0f, 0.55f, 1f};
    final Color[] colors = {top, blend(top, bottom, 0.35f), bottom};
    final java.awt.MultipleGradientPaint.CycleMethod cycle = java.awt.MultipleGradientPaint.CycleMethod.NO_CYCLE;
    final java.awt.LinearGradientPaint paint =
        new java.awt.LinearGradientPaint(
            new Point2D.Float(0, 0),
            new Point2D.Float(0, h),
            dist,
            colors,
            cycle);
    g2.setPaint(paint);
    g2.fillRect(0, 0, w, h);

    final Color accent = UIManager.getColor("Component.accentColor");//NON-NLS
    if (accent != null)
    {
      g2.setColor(accent);
      g2.fillRect(0, 0, w, Math.min(4, h / 40 + 2));
    }

    g2.dispose();
    super.paintComponent(g);
  }

  private static Color blend(final Color a, final Color b, final float t)
  {
    final float u = 1f - t;
    return new Color(
        clamp255((int) (a.getRed() * u + b.getRed() * t)),
        clamp255((int) (a.getGreen() * u + b.getGreen() * t)),
        clamp255((int) (a.getBlue() * u + b.getBlue() * t)),
        clamp255((int) (a.getAlpha() * u + b.getAlpha() * t)));
  }

  private static int clamp255(final int v)
  {
    return Math.max(0, Math.min(255, v));
  }
}

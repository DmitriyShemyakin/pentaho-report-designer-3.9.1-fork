/*
 * Copyright (c) 2006 - 2009 Pentaho Corporation. All rights reserved.
 */

package org.pentaho.reporting.designer.core.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.MultipleGradientPaint;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;

import javax.swing.UIManager;

/**
 * Shared gradient fills for splash screen and report canvas background.
 */
public final class ModernSurfaceFill
{
  private ModernSurfaceFill()
  {
  }

  /**
   * Dark startup / about dialog background (does not depend on L&amp;F; shown very early at boot).
   */
  public static void paintSplashDark(final Graphics2D g2, final int w, final int h)
  {
    if (w <= 0 || h <= 0)
    {
      return;
    }
    final Color top = new Color(18, 22, 30);
    final Color mid = new Color(28, 36, 48);
    final Color bottom = new Color(14, 17, 24);
    final float[] dist = {0f, 0.52f, 1f};
    final Color[] colors = {top, mid, bottom};
    g2.setPaint(new java.awt.LinearGradientPaint(
        new Point2D.Float(0, 0),
        new Point2D.Float(0, h),
        dist,
        colors,
        MultipleGradientPaint.CycleMethod.NO_CYCLE));
    g2.fillRect(0, 0, w, h);
    g2.setColor(new Color(255, 255, 255, 18));
    g2.drawLine(0, 0, w, 0);
  }

  /**
   * Neutral workspace background aligned with current Swing theme.
   */
  public static void paintCanvasWorkspace(final Graphics2D g2, final int w, final int h)
  {
    if (w <= 0 || h <= 0)
    {
      return;
    }
    g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

    Color top = UIManager.getColor("Panel.background");//NON-NLS
    if (top == null)
    {
      top = new Color(244, 246, 249);
    }
    Color mid = UIManager.getColor("TextField.background");//NON-NLS
    if (mid == null)
    {
      mid = top;
    }
    final Color bottom = blend(mid, new Color(228, 232, 238), 0.45f);

    final float[] dist = {0f, 0.5f, 1f};
    final Color[] colors = {top, blend(top, mid, 0.35f), bottom};
    g2.setPaint(new java.awt.LinearGradientPaint(
        new Point2D.Float(0, 0),
        new Point2D.Float(0, h),
        dist,
        colors,
        MultipleGradientPaint.CycleMethod.NO_CYCLE));
    g2.fillRect(0, 0, w, h);
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

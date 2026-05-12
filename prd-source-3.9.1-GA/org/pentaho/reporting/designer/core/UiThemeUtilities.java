/*
 * Copyright (c) 2026 — fork UI helpers; LGPL-2.1 applies to combined work with Pentaho sources.
 */

package org.pentaho.reporting.designer.core;

import java.lang.reflect.Method;

import javax.swing.UIManager;

import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.libraries.base.util.StringUtils;

/**
 * Central place for modern look-and-feel selection (FlatLaf via reflection when the jar is present).
 */
public final class UiThemeUtilities
{
  public static final String FLAT_LIGHT_DISPLAY_NAME = "FlatLaf Light";//NON-NLS
  public static final String FLAT_DARK_DISPLAY_NAME = "FlatLaf Dark";//NON-NLS
  public static final String FLAT_LIGHT_CLASS = "com.formdev.flatlaf.FlatLightLaf";//NON-NLS
  public static final String FLAT_DARK_CLASS = "com.formdev.flatlaf.FlatDarkLaf";//NON-NLS

  private UiThemeUtilities()
  {
  }

  public static boolean isFlatLafLightAvailable()
  {
    return isClassAvailable(FLAT_LIGHT_CLASS);
  }

  public static boolean isFlatLafDarkAvailable()
  {
    return isClassAvailable(FLAT_DARK_CLASS);
  }

  private static boolean isClassAvailable(final String className)
  {
    try
    {
      Class.forName(className);
      return true;
    }
    catch (Throwable t)
    {
      return false;
    }
  }

  /**
   * Applies the look and feel chosen in workspace settings (display name or fully qualified class name).
   */
  public static void installLookAndFeelFromWorkspaceSetting(final String storedValue)
  {
    if (StringUtils.isEmpty(storedValue))
    {
      installDefaultLookAndFeel();
      return;
    }
    if (tryInstallFlatLafByToken(storedValue))
    {
      return;
    }
    try
    {
      final UIManager.LookAndFeelInfo[] lnfs = UIManager.getInstalledLookAndFeels();
      for (final UIManager.LookAndFeelInfo lnf : lnfs)
      {
        if (lnf.getName().equals(storedValue) || lnf.getClassName().equalsIgnoreCase(storedValue))
        {
          UIManager.setLookAndFeel(lnf.getClassName());
          return;
        }
      }
      UIManager.setLookAndFeel(storedValue);
    }
    catch (Throwable t)
    {
      UncaughtExceptionsModel.getInstance().addException(t);
      installDefaultLookAndFeel();
    }
  }

  /**
   * When the user has not chosen a theme: prefer FlatLaf light if on the classpath, else native, else Nimbus.
   */
  public static void installDefaultLookAndFeel()
  {
    if (tryInstallFlatLafByClassName(FLAT_LIGHT_CLASS))
    {
      return;
    }
    try
    {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }
    catch (Throwable t1)
    {
      UncaughtExceptionsModel.getInstance().addException(t1);
      try
      {
        UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");//NON-NLS
      }
      catch (Throwable t2)
      {
        UncaughtExceptionsModel.getInstance().addException(t2);
      }
    }
  }

  private static boolean tryInstallFlatLafByToken(final String token)
  {
    if (FLAT_LIGHT_DISPLAY_NAME.equals(token) || FLAT_LIGHT_CLASS.equalsIgnoreCase(token))
    {
      return tryInstallFlatLafByClassName(FLAT_LIGHT_CLASS);
    }
    if (FLAT_DARK_DISPLAY_NAME.equals(token) || FLAT_DARK_CLASS.equalsIgnoreCase(token))
    {
      return tryInstallFlatLafByClassName(FLAT_DARK_CLASS);
    }
    return false;
  }

  private static boolean tryInstallFlatLafByClassName(final String className)
  {
    try
    {
      final Class<?> lnfClass = Class.forName(className);
      try
      {
        final Method setup = lnfClass.getMethod("setup");//NON-NLS
        setup.invoke(null);
        return true;
      }
      catch (NoSuchMethodException ignored)
      {
        final Object lnf = lnfClass.getDeclaredConstructor().newInstance();
        UIManager.setLookAndFeel((javax.swing.LookAndFeel) lnf);
        return true;
      }
    }
    catch (Throwable t)
    {
      return false;
    }
  }

  public static void tweakGlobalUiDefaults()
  {
    final javax.swing.UIDefaults uiDefaults = UIManager.getDefaults();
    uiDefaults.put("Table.gridColor", uiDefaults.get("Panel.background"));//NON-NLS
  }
}

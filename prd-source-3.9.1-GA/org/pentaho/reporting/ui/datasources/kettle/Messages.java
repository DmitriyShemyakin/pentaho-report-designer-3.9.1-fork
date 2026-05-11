package org.pentaho.reporting.ui.datasources.kettle;

import java.util.Locale;
import java.util.MissingResourceException;
import javax.swing.Icon;
import javax.swing.KeyStroke;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.ResourceBundleSupport;

/**
 * Todo: Document me!
 * <p/>
 * Date: 27.10.2009
 * Time: 15:37:30
 *
 * @author Thomas Morgner.
 */
public class Messages
{
  private static final Log logger = LogFactory.getLog(Messages.class);
  private static ResourceBundleSupport bundle =
      new ResourceBundleSupport(Locale.getDefault(), "org.pentaho.reporting.ui.datasources.kettle.messages",
          ObjectUtilities.getClassLoader(Messages.class));

  private Messages()
  {
  }

  /**
   * Gets a string for the given key from this resource bundle or one of its parents. If the key is a link, the link is
   * resolved and the referenced string is returned instead. If the given key cannot be resolved, no exception will be
   * thrown and a generic placeholder is used instead.
   *
   * @param key the key for the desired string
   * @return the string for the given key
   * @throws NullPointerException if <code>key</code> is <code>null</code>
   * @throws java.util.MissingResourceException
   *                              if no object for the given key can be found
   */
  public static String getString(final String key)
  {
    try
    {
      return bundle.getString(key);
    }
    catch (MissingResourceException e)
    {
      logger.warn("Missing localization: " + key, e);
      return '!' + key + '!';
    }
  }

  /**
   * Formats the message stored in the resource bundle (using a MessageFormat).
   *
   * @param key    the resourcebundle key
   * @param param1 the parameter for the message
   * @return the formated string
   */
  public static String getString(final String key, final String param1)
  {
    try
    {
      return bundle.formatMessage(key, param1);
    }
    catch (MissingResourceException e)
    {
      logger.warn("Missing localization: " + key, e);
      return '!' + key + '!';
    }
  }

  /**
   * Formats the message stored in the resource bundle (using a MessageFormat).
   *
   * @param key    the resourcebundle key
   * @param param1 the parameter for the message
   * @param param2 the parameter for the message
   * @return the formated string
   */
  public static String getString(final String key, final String param1, final String param2)
  {
    try
    {
      return bundle.formatMessage(key, param1, param2);
    }
    catch (MissingResourceException e)
    {
      logger.warn("Missing localization: " + key, e);
      return '!' + key + '!';
    }
  }

  /**
   * Formats the message stored in the resource bundle (using a MessageFormat).
   *
   * @param key    the resourcebundle key
   * @param param1 the parameter for the message
   * @param param2 the parameter for the message
   * @param param3 the parameter for the message
   * @return the formated string
   */
  public static String getString(final String key, final String param1, final String param2, final String param3)
  {
    try
    {
      return bundle.formatMessage(key, new Object[]{param1, param2, param3});
    }
    catch (MissingResourceException e)
    {
      logger.warn("Missing localization: " + key, e);
      return '!' + key + '!';
    }
  }

  /**
   * Formats the message stored in the resource bundle (using a MessageFormat).
   *
   * @param key    the resourcebundle key
   * @param param1 the parameter for the message
   * @param param2 the parameter for the message
   * @param param3 the parameter for the message
   * @param param4 the parameter for the message
   * @return the formated string
   */
  public static String getString(final String key,
                                 final String param1,
                                 final String param2,
                                 final String param3,
                                 final String param4)
  {
    try
    {
      return bundle.formatMessage(key, new Object[]{param1, param2, param3, param4});
    }
    catch (MissingResourceException e)
    {
      logger.warn("Missing localization: " + key, e);
      return '!' + key + '!';
    }
  }


  /**
   * Formats the message stored in the resource bundle (using a MessageFormat).
   *
   * @param key    the resourcebundle key
   * @param param1 the parameter for the message
   * @param param2 the parameter for the message
   * @param param3 the parameter for the message
   * @param param4 the parameter for the message
   * @param param5 the parameter for the message
   * @return the formated string
   */
  public static String getString(final String key,
                                 final String param1,
                                 final String param2,
                                 final String param3,
                                 final String param4,
                                 final String param5)
  {
    try
    {
      return bundle.formatMessage(key, new Object[]{param1, param2, param3, param4, param5});
    }
    catch (MissingResourceException e)
    {
      logger.warn("Missing localization: " + key, e);
      return '!' + key + '!';
    }
  }


  public static Icon getIcon(final String key, final boolean large)
  {
    return bundle.getIcon(key, large);
  }

  public static Icon getIcon(final String key)
  {
    return bundle.getIcon(key);
  }

  public static Integer getMnemonic(final String key)
  {
    return bundle.getMnemonic(key);
  }

  public static Integer getOptionalMnemonic(final String key)
  {
    return bundle.getOptionalMnemonic(key);
  }

  public static KeyStroke getKeyStroke(final String key)
  {
    return bundle.getKeyStroke(key);
  }

  public static KeyStroke getOptionalKeyStroke(final String key)
  {
    return bundle.getOptionalKeyStroke(key);
  }

  public static KeyStroke getKeyStroke(final String key, final int mask)
  {
    return bundle.getKeyStroke(key, mask);
  }

  public static KeyStroke getOptionalKeyStroke(final String key, final int mask)
  {
    return bundle.getOptionalKeyStroke(key, mask);
  }
}

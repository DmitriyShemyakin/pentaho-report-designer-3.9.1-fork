package org.pentaho.reporting.tools.configeditor;

import java.util.HashMap;
import java.util.Locale;

import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.ResourceBundleSupport;

/**
 * Todo: Document me!
 * <p/>
 * Date: 16.03.2010
 * Time: 12:32:30
 *
 * @author Thomas Morgner.
 */
public class Messages extends ResourceBundleSupport
{
  private static HashMap locales;

  public static Messages getInstance()
  {
    return getInstance(Locale.getDefault());
  }

  public static synchronized Messages getInstance(final Locale locale)
  {
    if (locales == null)
    {
      locales = new HashMap();
      final Messages retval = new Messages(locale, ConfigEditorBoot.BUNDLE_NAME);
      locales.put(locale, retval);
      return retval;
    }

    final Messages o = (Messages) locales.get(locale);
    if (o != null)
    {
      return o;
    }

    final Messages retval = new Messages(locale, ConfigEditorBoot.BUNDLE_NAME);
    locales.put(locale, retval);
    return retval;
  }

  private Messages(final Locale locale, final String s)
  {
    super(locale, s, ObjectUtilities.getClassLoader(Messages.class));
  }
}

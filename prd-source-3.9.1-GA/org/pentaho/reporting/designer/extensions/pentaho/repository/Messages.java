package org.pentaho.reporting.designer.extensions.pentaho.repository;

import java.util.Locale;

import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.ResourceBundleSupport;

/**
 * Todo: Document me!
 * <p/>
 * Date: 26.11.2009
 * Time: 16:41:15
 *
 * @author Thomas Morgner.
 */
public class Messages extends ResourceBundleSupport
{
  private static Messages instance;
  /**
   * Creates a new instance.
   */
  private Messages()
  {
    super(Locale.getDefault(), "org.pentaho.reporting.designer.extensions.pentaho.repository.messages",
        ObjectUtilities.getClassLoader(Messages.class));
  }

  public static synchronized Messages getInstance()
  {
    if (instance == null)
    {
      instance = new Messages();
    }
    return instance;
  }
}

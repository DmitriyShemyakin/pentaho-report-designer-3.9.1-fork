package org.pentaho.reporting.designer.core.util;

import java.util.Locale;

import org.pentaho.reporting.libraries.base.util.Messages;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

/**
 * Todo: Document me!
 * <p/>
 * Date: 06.09.2010
 * Time: 12:21:48
 *
 * @author Thomas Morgner.
 */
public class UtilMessages extends Messages
{
  private static UtilMessages utilMessages;

  public static Messages getInstance()
  {
    if (utilMessages == null)
    {
      utilMessages = new UtilMessages();
    }
    return utilMessages;
  }

  /**
   * Creates a new Messages-collection. The locale and baseName will be used to create the resource-bundle that backs up
   * this implementation.
   */
  private UtilMessages()
  {
    super(Locale.getDefault(), "org.pentaho.reporting.designer.core.util.messages",  // NON-NLS
        ObjectUtilities.getClassLoader(UtilMessages.class));
  }
}

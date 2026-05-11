package org.pentaho.reporting.engine.classic.extensions.drilldown;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.pentaho.reporting.engine.classic.core.metadata.AbstractMetaData;

/**
 * Todo: Document me!
 * <p/>
 * Date: 23.02.2010
 * Time: 13:31:57
 *
 * @author Thomas Morgner.
 */
public class DrillDownProfile extends AbstractMetaData
{
  private Class linkCustomizerType;
  private HashMap<String,String> attributes;

  public DrillDownProfile(final String name,
                          final String bundleLocation,
                          final String keyPrefix,
                          final boolean expert,
                          final boolean preferred,
                          final boolean hidden,
                          final boolean deprecated,
                          final Class linkCustomizerType,
                          final Map<String,String> attributes)
  {
    super(name, bundleLocation, keyPrefix, expert, preferred, hidden, deprecated);
    this.linkCustomizerType = linkCustomizerType;
    this.attributes = new HashMap<String,String>(attributes);
  }

  public DrillDownProfile(final Class linkCustomizerType)
  {
    super("", "org.pentaho.reporting.engine.classic.extensions.drilldown.drilldown-profile",
        "", false, false, false, false);
    this.linkCustomizerType = linkCustomizerType;
  }

  public Class getLinkCustomizerType()
  {
    return linkCustomizerType;
  }

  public String getAttribute(final String name)
  {
    return attributes.get(name);
  }

  public String[] getAttributes()
  {
    return attributes.keySet().toArray(new String[attributes.size()]);
  }

  public String getGroupDisplayName(final Locale locale)
  {
    return getBundle(locale).getString("drilldown-profile-group." + getAttribute("group") + ".display-name");//NON-NLS
  }
}

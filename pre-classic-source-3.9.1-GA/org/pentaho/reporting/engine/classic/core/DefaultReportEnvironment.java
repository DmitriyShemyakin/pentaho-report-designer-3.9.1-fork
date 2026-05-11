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
 * Copyright (c) 2001 - 2009 Object Refinery Ltd, Pentaho Corporation and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.pentaho.reporting.engine.classic.core.util.beans.BeanException;
import org.pentaho.reporting.engine.classic.core.util.beans.LocaleValueConverter;
import org.pentaho.reporting.engine.classic.core.util.beans.TimeZoneValueConverter;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.formula.util.URLEncoder;

/**
 * A simple implementation that provides static environmental information. The environment properties are mapped
 * against the global report configuration.
 *
 * @author Thomas Morgner
 */
public class DefaultReportEnvironment implements ReportEnvironment
{
  public static final String ENVIRONMENT_KEY = "org.pentaho.reporting.engine.classic.core.environment.";

  private Configuration configuration;
  private Locale locale;
  private Locale localeFromConfiguration;
  private TimeZone timeZone;
  private TimeZone timeZoneFromConfiguration;

  public DefaultReportEnvironment(final Configuration configuration)
  {
    if (configuration == null)
    {
      throw new NullPointerException();
    }
    update(configuration);
  }

  public String getEnvironmentProperty(final String key)
  {
    if ("engine.version".equals(key))
    {
      return ClassicEngineInfo.getInstance().getVersion();
    }
    else if ("engine.version.major".equals(key))
    {
      return ClassicEngineInfo.getInstance().getReleaseMajor();
    }
    else if ("engine.version.minor".equals(key))
    {
      return ClassicEngineInfo.getInstance().getReleaseMinor();
    }
    else if ("engine.version.patch".equals(key))
    {
      return ClassicEngineInfo.getInstance().getReleaseMilestone();
    }
    else if ("engine.version.candidate-token".equals(key))
    {
      return ClassicEngineInfo.getInstance().getReleaseCandidateToken();
    }
    else if ("engine.version.buildnumber".equals(key))
    {
      return ClassicEngineInfo.getInstance().getReleaseBuildNumber();
    }
    else if ("engine.product-id".equals(key))
    {
      return ClassicEngineInfo.getInstance().getProductId();
    }
    else if ("engine.name".equals(key))
    {
      return ClassicEngineInfo.getInstance().getName();
    }
    return configuration.getConfigProperty(ENVIRONMENT_KEY + key);
  }

  /**
   * Returns the text encoding that should be used to encode URLs.
   *
   * @return the encoding for URLs.
   */
  public String getURLEncoding()
  {
    return configuration.getConfigProperty("org.pentaho.reporting.engine.classic.core.URLEncoding");
  }

  public void update(final Configuration configuration)
  {
    if (configuration == null)
    {
      throw new NullPointerException();
    }
    this.configuration = configuration;
    final String localeFromConfig = getEnvironmentProperty("designtime.Locale");
    if (localeFromConfig == null)
    {
      this.localeFromConfiguration = Locale.getDefault();
    }
    else
    {
      try
      {
        this.localeFromConfiguration = (Locale) new LocaleValueConverter().toPropertyValue(localeFromConfig);
      }
      catch (BeanException e)
      {
        this.localeFromConfiguration = Locale.getDefault();
      }
    }

    final String timeZoneFromConfig = getEnvironmentProperty("designtime.TimeZone");
    if (timeZoneFromConfig == null)
    {
      this.timeZoneFromConfiguration = TimeZone.getDefault();
    }
    else
    {
      try
      {
        this.timeZoneFromConfiguration = (TimeZone) new TimeZoneValueConverter().toPropertyValue(timeZoneFromConfig);
      }
      catch (BeanException e)
      {
        this.timeZoneFromConfiguration = TimeZone.getDefault();
      }
    }
  }

  /**
   * Servlet support. Encodes session information (or other additional parameters) into the URL. This is required if the
   * server does not use cookies for its session management.
   *
   * @param url
   * @return
   */
  public String encodeURL(final String url) throws UnsupportedEncodingException
  {
    return URLEncoder.encode(url, getURLEncoding());
  }

  public void setLocale(final Locale locale)
  {
    if (locale == null)
    {
      throw new NullPointerException();
    }
    this.locale = locale;
  }

  public void setTimeZone(final TimeZone timeZone)
  {
    if (timeZone == null)
    {
      throw new NullPointerException();
    }
    this.timeZone = timeZone;
  }

  public Locale getLocale()
  {
    if (locale != null)
    {
      return locale;
    }
    return localeFromConfiguration;
  }

  public TimeZone getTimeZone()
  {
    if (timeZone != null)
    {
      return timeZone;
    }
    return timeZoneFromConfiguration;
  }

  public Object clone() throws CloneNotSupportedException
  {
    final DefaultReportEnvironment environment = (DefaultReportEnvironment) super.clone();
    environment.configuration = (Configuration) configuration.clone();
    return environment;
  }

  public Map<String, String[]> getUrlExtraParameter()
  {
    return Collections.emptyMap();
  }
}
